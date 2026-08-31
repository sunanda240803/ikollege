package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.*;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.hostel.HostelRoomInfoMapper;
import com.iitm.hosteldine.model.hostel.HostelRoomInfoEntity;
import com.iitm.hosteldine.repository.hostel.HostelMasterRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomAllotmentRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomInfoRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.util.ExcelUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class HostelRoomInfoService {
	private final HostelRoomInfoRepository hostelRoomInfoRepo;
	private final HostelMasterRepository hostelMasterRepo;
	private final MessageSource messageSource;
	private final ExcelUtility excelUtility;
	private final HostelMasterService hostelMasterService;
	private final HostelFloorMasterService floorMasterService;
	private final SimsConfigDataService simsConfigDataService;
	private final HostelRoomAllotmentRepository hostelRoomAllotmentRepository;

	public List<HostelRoomInfoDto> getHostelRoomInfoList(HostelRoomInfoDto roomInfoDto) {
		if (roomInfoDto.getBuilding() != null && roomInfoDto.getBuilding().getId() > 0) {
			return hostelRoomInfoRepo
					.findAllByActiveFlagAndBuildingIdAndBuildingHostelIdOrderByModifiedAtDesc(
							ModelConstants.STATUS_ACTIVE, roomInfoDto.getBuilding().getId(),
							roomInfoDto.getBuilding().getHostel().getId())
					.stream().map(HostelRoomInfoMapper.INSTANCE::fromHostelRoomInfoEntity).collect(Collectors.toList());
		} else {
			return hostelRoomInfoRepo.findAllByActiveFlagOrderByModifiedAtDesc(ModelConstants.STATUS_ACTIVE).stream()
					.map(HostelRoomInfoMapper.INSTANCE::fromHostelRoomInfoEntity).collect(Collectors.toList());
		}
	}

	public HostelRoomInfoDto getHostelRoomInfoDetailsById(int id) {
		return hostelRoomInfoRepo.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
				.map(HostelRoomInfoMapper.INSTANCE::fromHostelRoomInfoEntity).orElse(new HostelRoomInfoDto());
	}

	public boolean deleteHostelRoomInfoById(Long id) throws NoSuchMessageException, RecordNotExistsException {
		if (hostelRoomAllotmentRepository.existsByActiveFlagAndRoomId(ModelConstants.STATUS_ACTIVE, id)) {
			throw new RecordNotExistsException(
					messageSource.getMessage("message.validation.error.room.config.assigned", null, Locale.getDefault()));
		}
		return hostelRoomInfoRepo.findById(id).map(entity -> {
			entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
			entity.onUpdate();
			return hostelRoomInfoRepo.save(entity).getId() != null;
		}).orElseThrow(() -> new RecordNotExistsException(
				messageSource.getMessage("message.validation.error.id.not.found", null, Locale.getDefault())));
	}

	public String saveAndUpdate(HostelRoomInfoDto dto) throws Exception {
		return Optional.ofNullable(dto.getId()).filter(id -> id > 0)
				.flatMap(id -> hostelRoomInfoRepo.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					HostelRoomInfoMapper.INSTANCE.onUpdateEntity(existingEntity, dto);
					existingEntity.onUpdate();
					hostelRoomInfoRepo.save(existingEntity);
					return Constants.UPDATED;
				}).orElseGet(() -> {
					HostelRoomInfoEntity newEntity = HostelRoomInfoMapper.INSTANCE.toHostelRoomInfoEntity(dto);
					newEntity.onCreate();
					hostelRoomInfoRepo.save(newEntity);
					return Constants.SAVED;
				});
	}

	public boolean checkHostelRoomInfoExist(long floorId, long hostelId, String roomNo, Long id) {
		return hostelRoomInfoRepo.findByActiveFlagAndBuildingIdAndBuildingHostelIdAndRoomNoAndIdNot(
				ModelConstants.STATUS_ACTIVE, floorId, hostelId, roomNo, id != null ? id : 0).isPresent();
	}

	@Transactional
	public HostelRoomInfoDto saveBulkRoomConfigUpload(MultipartFile file) throws IOException {
		List<HostelRoomInfoDto> roomInfoList = new ArrayList<>();
		DataFormatter formatter = new DataFormatter();
		HostelRoomInfoDto roomInfoDto = new HostelRoomInfoDto();
		roomInfoDto.setErrorList(new ArrayList<>());

		try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
			Sheet sheet = workbook.getSheetAt(0);
			// Assuming first row is header
			int rowNumber = 0;

			Row firstRow = sheet.getRow(0);
			ArrayList<String> cellValues = new ArrayList<>();
			for (int i = 0; i < 7; i++) {
				Cell cell = firstRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				cellValues.add(formatter.formatCellValue(cell));
			}
			// Checking labels
			boolean isHostelName = cellValues.contains("Hostel Name *");
			boolean isFloorName = cellValues.contains("Floor Name *");
			boolean isRoomNumber = cellValues.contains("Room Number *");
			boolean isCapacity = cellValues.contains("Capacity *");
			boolean isVacate = cellValues.contains("Vacation(Y/N) *");
			boolean isVacCap = cellValues.contains("Vacation Capacity");
			boolean isOfficialStatus = cellValues.contains("Room Configuration Type");

			if (isHostelName && isFloorName && isRoomNumber && isCapacity && isVacate && isVacCap && isOfficialStatus) {

				int rowCount = ExcelUtility.countNonEmptyRows(sheet);
				for (Row row : sheet) {
					if (rowCount > rowNumber) {
						if (rowNumber == 0) { // Skip header row
							rowNumber++;
							continue;
						}
						HostelRoomInfoDto dto = new HostelRoomInfoDto();
						StringBuilder errorDetails = new StringBuilder();
						boolean error = false;
						int column = 0;
						dto.setBuilding(new HostelFloorMasterDto());
						dto.getBuilding().setHostel(new HostelMasterDto());
						// Hostel Name
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							
							Cell hostelIdCell = row.getCell(column);
							switch (hostelIdCell.getCellType()) {
							case STRING:
								dto.getBuilding().getHostel().setId(getHostelId(hostelIdCell.getStringCellValue()));
								break;
							case NUMERIC:
								dto.getBuilding().getHostel().setId((long) hostelIdCell.getNumericCellValue());
								break;
							default:
								throw new IllegalStateException("Unexpected cell type: " + hostelIdCell.getCellType());
							}

						} else {
							excelUtility.cellError("Hostel Name should not be empty ", column, rowNumber, dto);
							error = true;
							errorDetails.append("-").append(dto.getExcelErrorMsg()).append("\n");
						}
						column++;
						// Floor Name
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							if(dto.getBuilding().getHostel().getId()!=null && dto.getBuilding().getHostel().getId()!=0)
							{Cell floorIdCell = row.getCell(column);
							String val = getFloorId(dto.getBuilding().getHostel().getId(),
									floorIdCell.getStringCellValue());
							if (!val.equalsIgnoreCase("Floor not found")) {
								dto.getBuilding().setId(Long.parseLong(val));
							} else {
								excelUtility.cellError("The selected floor is not associated with selected hostel",
										column, rowNumber, dto);
								error = true;
								errorDetails.append("-").append(dto.getExcelErrorMsg()).append("\n");
							}
							}
						} else {
							excelUtility.cellError("Floor Name should not be empty ", column, rowNumber, dto);
							error = true;
							errorDetails.append("-").append(dto.getExcelErrorMsg()).append("\n");
						}
						column++;
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							if ((ModelConstants.numericRegex
									.matcher(excelUtility.getCellValueAsString(row.getCell(column))).matches())) {

								dto.setRoomNo(String.format("%.0f", row.getCell(column).getNumericCellValue()));
								if (dto.getBuilding().getId() != null
										&& checkHostelRoomInfoExist(dto.getBuilding().getId(),
												dto.getBuilding().getHostel().getId(), dto.getRoomNo(), dto.getId())) {
									excelUtility.cellError("The selected hostel,floor,room is already exist", column,
											rowNumber, dto);
									error = true;
									errorDetails.append("-").append(dto.getExcelErrorMsg()).append("\n");
								}
							} else {
								excelUtility.cellError("Room Number should only numeric values", column, rowNumber,
										dto);
								error = true;
								errorDetails.append("-").append(dto.getExcelErrorMsg()).append("\n");
							}

						} else {
							excelUtility.cellError("Room Number should not be empty ", column, rowNumber, dto);
							error = true;
							errorDetails.append("-").append(dto.getExcelErrorMsg()).append("\n");
						}
						column++;
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							if ((ModelConstants.numericRegex
									.matcher(excelUtility.getCellValueAsString(row.getCell(column))).matches())) {

								int cap = (int) row.getCell(column).getNumericCellValue();
								if (cap > 0)
									dto.setCapacity((int) row.getCell(column).getNumericCellValue());
								else {
									excelUtility.cellError("Capacity must be greater than 1", column, rowNumber, dto);
									error = true;
									errorDetails.append("-").append(dto.getExcelErrorMsg()).append("\n");
								}
							} else {
								excelUtility.cellError("Capacity should only numeric values ", column, rowNumber, dto);
								error = true;
								errorDetails.append("-").append(dto.getExcelErrorMsg()).append("\n");

							}
						} else {
							excelUtility.cellError("Capacity should not be empty ", column, rowNumber, dto);
							error = true;
							errorDetails.append("-").append(dto.getExcelErrorMsg()).append("\n");
						}
						column++;
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							dto.setIsVacation(row.getCell(column).getStringCellValue().trim().equalsIgnoreCase("Y"));
						} else {
							excelUtility.cellError("Is Vacation should not be empty ", column, rowNumber, dto);
							error = true;
							errorDetails.append("-").append(dto.getExcelErrorMsg()).append("\n");
						}
						column++;
						if (dto.getIsVacation() != null && dto.getIsVacation()  && row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							if ((ModelConstants.numericRegex
									.matcher(excelUtility.getCellValueAsString(row.getCell(column))).matches())) {

								dto.setVacCapacity((int) row.getCell(column).getNumericCellValue());
							} else {
								excelUtility.cellError("Vacation Capacity should only numeric values ", column,
										rowNumber, dto);
								error = true;
								errorDetails.append("-").append(dto.getExcelErrorMsg()).append("\n");

							}
						} else {
							if (dto.getIsVacation() != null && dto.getIsVacation()) {
								excelUtility.cellError(" Vacation Capacity should not be empty ", column, rowNumber,
										dto);
								error = true;
								errorDetails.append("-").append(dto.getExcelErrorMsg()).append("\n");
							}
						}
						column++;
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							dto.setOfficialGuestStatus(row.getCell(column).getStringCellValue());

						} /*
							 * else { excelUtility.cellError("Official Guest Status should not be empty ",
							 * column, rowNumber, dto); error = true;
							 * errorDetails.append("-").append(dto.getExcelErrorMsg()).append("\n"); }
							 */
						// Add errors for the row to the HostelRoomInfoDto if any
						if (error) {
							roomInfoDto.getErrorList().add("Row " + rowNumber + ": " + errorDetails.toString());
						} else {
							roomInfoList.add(dto);
						}
						rowNumber++;
					}
				}
			} else {
				roomInfoDto.getErrorList().add("Invalid Room Configuration Excel Template");
			}
			if (!roomInfoList.isEmpty()) {
				saveBulkRoomConfigLIst(roomInfoList);
			} else if (roomInfoDto.getErrorList().isEmpty()) {
				roomInfoDto.getErrorList().add("Required mandatory fields");
			}

		}
		return roomInfoDto;
	}

	public void saveBulkRoomConfigLIst(List<HostelRoomInfoDto> roomInfoList) {
		List<HostelRoomInfoEntity> roomInfoEntities = roomInfoList.stream()
				.map(HostelRoomInfoMapper.INSTANCE::toHostelRoomInfoEntity).collect(Collectors.toList());
		roomInfoEntities.forEach(HostelRoomInfoEntity::onCreate);
		hostelRoomInfoRepo.saveAll(roomInfoEntities);
	}

	public Workbook downloadBulkRoomConfigUploadTemplate() {
		String sheetName = "Bulk Room Config";
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);
		int columnCount = 0;
		String[] headerData = ExcelConstants.BULK_ROOM_CONFIGURATION_DATA;
		String[] headerDataWidth = ExcelConstants.BULK_ROOM_CONFIGURATION_DATA_WIDTH;
		  /**
	         * Data Style
	         */
	        XSSFCellStyle style3 = excelUtility.setDataStyle(workbook);

	        Row row0 = sheet.createRow(0);
	        /**
	         * Set Header Lines
	         */
	        excelUtility.createHeader(row0, columnCount, headerData, workbook);

	        /**
	         * Set cell width
	         */
	        IntStream.range(0, headerDataWidth.length).forEach(i -> {
	            sheet.setColumnWidth(i, Integer.parseInt(headerDataWidth[i]));

	        });


		// Create a hidden sheet for dropdown data
		XSSFSheet hiddenSheet = workbook.createSheet("DropdownData");
		workbook.setSheetHidden(workbook.getSheetIndex("DropdownData"), true);

		// Dropdown data for the three columns
		List<String> hostelNames = hostelMasterService.getHostelList().stream().map(HostelMasterDto::getHostelName)
				.collect(Collectors.toList());

		List<String> floorNames = floorMasterService.getUniqueFloorName();
		ArrayList<String> categories = simsConfigDataService.getSimConfigValueArrayList("OFFICIAL_GUEST_STATUS");
		ArrayList<String> isVacation = simsConfigDataService.getSimConfigValueArrayList("IS_VACATION_STATUS");

		// Add dropdown data to the hidden sheet

		excelUtility.addDropdownDataToHiddenSheet(hiddenSheet, "HostelNames", hostelNames);
		excelUtility.addDropdownDataToHiddenSheet(hiddenSheet, "FloorNames", floorNames);
		excelUtility.addDropdownDataToHiddenSheet(hiddenSheet, "Categories", categories);
		excelUtility.addDropdownDataToHiddenSheet(hiddenSheet, "vacation", isVacation);

		// Add dropdowns to the main sheet columns
		excelUtility.addDropdownToColumn(sheet, 1, 100, 0, "DropdownData!$A$1:$A$" + hostelNames.size()); // Hostel Name
		excelUtility.addDropdownToColumn(sheet, 1, 100, 1, "DropdownData!$B$1:$B$" + floorNames.size()); // Floor Name
		excelUtility.addDropdownToColumn(sheet, 1, 100, 4, "DropdownData!$E$1:$E$" + isVacation.size()); // Vacation
		excelUtility.addDropdownToColumn(sheet, 1, 100, 6, "DropdownData!$G$1:$G$" + categories.size()); // Student /

		IntStream.range(0, headerData.length).forEach(sheet::autoSizeColumn);
		return workbook;
	}

	public long getHostelId(String value) {
		return hostelMasterService.getHostelList().stream().filter(hostel -> hostel.getHostelName().equals(value))
				.findFirst().map(HostelMasterDto::getId)
				.orElseThrow(() -> new IllegalArgumentException("Hostel not found: " + value));

	}

	public String getFloorId(long hostelId, String value) {
		return floorMasterService.getFloorListByHostelId(hostelId).stream()
				.filter(floor -> floor.getFloorName().equals(value) && floor.getHostel().getId() == hostelId)
				.findFirst().map(floor -> String.valueOf(floor.getId())).orElse("Floor not found");
	} 
	
	public List<String> getRoomNoList() {
		return hostelRoomInfoRepo.findDistinctRoomNoByActiveFlag(ModelConstants.STATUS_ACTIVE);
	}

	public List<HostelRoomInfoDto> findRoomByHostelId(Long hostelId) {
		return hostelRoomInfoRepo.findRoomByHostelId(ModelConstants.STATUS_ACTIVE, hostelId).stream()
				.map(HostelRoomInfoMapper.INSTANCE::fromHostelRoomInfoEntity).collect(Collectors.toList());
	}

	public HostelRoomInfoEntity findRoomByHostelIdAndRoomNo(Long hostelId, String roomNo) {
	    return hostelRoomInfoRepo.findRoomByHostelIdAndRoomNo(ModelConstants.STATUS_ACTIVE, hostelId, roomNo);
	}

	public HostelRoomInfoEntity findRoomByHostelIdAndRoomId(Long hostelId, Long roomId) {
	    return hostelRoomInfoRepo.findRoomByHostelIdAndRoomId(ModelConstants.STATUS_ACTIVE, hostelId, roomId);
	}

	public Page<HostelRoomInfoDto> getHostelRoomInfoList(HostelRoomInfoDto roomInfoDto, String search,
			Pageable pageable) {
		if (roomInfoDto.getBuilding() != null && roomInfoDto.getBuilding().getId() > 0) {
	        return hostelRoomInfoRepo
	                .findAllByActiveFlagAndBuildingIdAndBuildingHostelId(
	                        ModelConstants.STATUS_ACTIVE,
	                        roomInfoDto.getBuilding().getId(),
	                        roomInfoDto.getBuilding().getHostel().getId(),
	                        pageable)
	                .map(HostelRoomInfoMapper.INSTANCE::fromHostelRoomInfoEntity); 
	    } else {
	        // Fetch paginated results without building and hostel filtering
	        return hostelRoomInfoRepo
	                .findAllByActiveFlag(ModelConstants.STATUS_ACTIVE, pageable)
	                .map(HostelRoomInfoMapper.INSTANCE::fromHostelRoomInfoEntity); 
	    }
	}

	public Page<HostelRoomInfoDto> getHostelRoomInfoList(PaginationForm form) {
		Page<HostelRoomInfoEntity> result = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(),Sort.by("modifiedAt").ascending());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
		if (!form.getAdditionalParam().isEmpty()) {
	        return hostelRoomInfoRepo
	                .findAllByActiveFlagAndBuildingIdAndBuildingHostelId(
	                        ModelConstants.STATUS_ACTIVE,
	                        Long.parseLong(form.getAdditionalParam().get("floorId").toString()),  
	                        Long.parseLong(form.getAdditionalParam().get("hostelId").toString()),
	                        pageable)
	                .map(HostelRoomInfoMapper.INSTANCE::fromHostelRoomInfoEntity); 
	    } else {
	        // Fetch paginated results without building and hostel filtering
	        result = hostelRoomInfoRepo
	                .findAllByActiveFlag(ModelConstants.STATUS_ACTIVE, pageable);
	                
	    }
	}else {
		result = hostelRoomInfoRepo.findByHostelRoomInfoSearchList(ModelConstants.STATUS_ACTIVE,form.getSearch(),
                Long.parseLong(form.getAdditionalParam().get("floorId").toString()),  
                Long.parseLong(form.getAdditionalParam().get("hostelId").toString()),
                pageable);
		}
		return result.map(HostelRoomInfoMapper.INSTANCE::fromHostelRoomInfoEntity);
	}

	public List<HostelRoomListDTO> getHostelRoomList(String category) {
		// Fetch all hostels
		List<HostelMasterDto> hostels = hostelMasterService.getHostelList();

		// Create a list to store the combined data
		List<HostelRoomListDTO> hostelRoomList = new ArrayList<>();

		// Iterate through each hostel and fetch its room numbers
		for (HostelMasterDto hostel : hostels) {
			// Fetch room numbers for the current hostel
			List<HostelRoomInfoDto> rooms = hostelRoomInfoRepo.findRoomByHostelId(ModelConstants.STATUS_ACTIVE, hostel.getId()).stream()
					.map(HostelRoomInfoMapper.INSTANCE::fromHostelRoomInfoEntity).collect(Collectors.toList());

			// Create a DTO for the hostel and its room numbers
			HostelRoomListDTO dto = new HostelRoomListDTO();
			dto.setHostel(hostel);
			dto.setRooms(rooms);

			// Add the DTO to the list
			hostelRoomList.add(dto);
		}

		return hostelRoomList;
	}

	public List<HostelRoomInfoDto> getAvailableRooms(Long hostelId, Long requestId, Long stayId, String screenType) {
		Optional<List<Object[]>> rooms;

		if ("deanOtherCandidateRequests".equalsIgnoreCase(screenType)) {
			rooms = hostelRoomInfoRepo.getAvailableRoomsForStay(hostelId, requestId, stayId, null, ModelConstants.STATUS_ACTIVE);
		} else {
			rooms = hostelRoomInfoRepo.getAvailableRooms(hostelId, requestId, null, ModelConstants.STATUS_ACTIVE);
		}

		return rooms.map(objects -> objects
						.stream()
						.map(row -> mapToHostelRoomDto(row, null))
						.filter(dto -> dto.getRoomNo() != null && !dto.getRoomNo().isEmpty())
						.toList()
				)
				.orElse(Collections.emptyList());
	}

	public List<HostelRoomInfoDto> getAvailableSeats(Long hostelId, String roomNo, Long requestId, Long stayId, String screenType) {
		Optional<List<Object[]>> rooms;

		if ("deanOtherCandidateRequests".equalsIgnoreCase(screenType)) {
			rooms = hostelRoomInfoRepo.getAvailableRoomsForStay(hostelId, requestId, stayId, roomNo, ModelConstants.STATUS_ACTIVE);
		} else {
			rooms = hostelRoomInfoRepo.getAvailableRooms(hostelId, requestId, roomNo, ModelConstants.STATUS_ACTIVE);
		}

		return rooms.map(objects -> objects
						.stream()
						.map(row -> mapToHostelRoomDto(row, roomNo))
						.filter(dto -> dto.getRoomNo() != null && !dto.getRoomNo().isEmpty())
						.toList())
				.orElse(Collections.emptyList());
	}


	private HostelRoomInfoDto mapToHostelRoomDto(Object[] row, String roomNumber) {
		HostelRoomInfoDto dto = new HostelRoomInfoDto();

		String roomNo = row[0] != null ? row[0].toString() : ModelConstants.EMPTY_STRING;
		long occupied = row[1] != null ? Long.parseLong(row[1].toString()) : 0L;
		long totalCapacity = row[2] != null ? Long.parseLong(row[2].toString()) : 0L;
		long remainingCount = row[3] != null ? Long.parseLong(row[3].toString()) : 0L;

		if (remainingCount >= 1) {
			dto.setRoomNo(roomNo);
			dto.setRemainingCount(remainingCount);
			dto.setOccupied((int) occupied);
			dto.setTotalCapacity(totalCapacity);

			List<String> seatList = IntStream.range(0, (int) totalCapacity)
					.mapToObj(i -> String.valueOf((char) ('A' + i)))
					.collect(Collectors.toList());
			dto.setSeatList(seatList);

			if (row.length > 4 && roomNumber != null && row[4] != null) {
				String occupiedCsv = row[4].toString();
				List<String> occupiedSeats = Arrays.stream(occupiedCsv.split(ModelConstants.COMMA))
						.map(String::trim)
						.toList();

				List<String> vacantSeats = new ArrayList<>(seatList);
				vacantSeats.removeAll(occupiedSeats);

				dto.setVacantList(vacantSeats);
			} else {
				dto.setVacantList(new ArrayList<>(seatList));
			}
		}
		return dto;
	}


	public List<HostelRoomInfoDto> getRoomListByHostelId(Long hostelId){
		return hostelRoomInfoRepo
				.findAllByActiveFlagAndBuildingHostelIdAndOfficialGuestStatusOrderByRoomNo(
						ModelConstants.STATUS_ACTIVE,hostelId,"0")
				.stream().map(HostelRoomInfoMapper.INSTANCE::fromHostelRoomInfoEntity).
				collect(Collectors.toList());
	}

	public List<Map<String, Object>> findGuestRoomByHostelIdAndRequestId(GuestHostelAllotmentDTO request) {

		List<Object[]> results = hostelRoomInfoRepo.findAvailableGuestRoomsByHostel(
				Long.valueOf(request.getRequestId()),
				Long.valueOf(request.getHostelId()),
				request.getFromDate(),
				request.getToDate()
		);

		List<Map<String, Object>> finalList = new ArrayList<>();

		for (Object[] row : results) {
			String roomNo = (String) row[0];
			Integer roomId = ((Number) row[1]).intValue();
			Integer totalCapacity = ((Number) row[2]).intValue();
			Integer remainingCount = ((Number) row[3]).intValue();
			Integer floorId = ((Number) row[4]).intValue();

			if (remainingCount >= 1) {
				Map<String, Object> map = new HashMap<>();
				map.put("roomNo", roomNo);
				map.put("roomId", roomId);
				map.put("floorId", floorId);
				finalList.add(map);
			}
		}

		return finalList;
	}

}
