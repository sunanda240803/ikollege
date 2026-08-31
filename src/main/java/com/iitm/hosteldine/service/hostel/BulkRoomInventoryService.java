package com.iitm.hosteldine.service.hostel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.hostel.HostelRoomInfoDto;
import com.iitm.hosteldine.form.asset.AssetCategoryForm;
import com.iitm.hosteldine.form.hostel.RoomInventoryForm;
import com.iitm.hosteldine.mapper.asset.AssetInventoryInfoMapper;
import com.iitm.hosteldine.mapper.hostel.HostelRoomInventoryMapper;
import com.iitm.hosteldine.model.asset.AssetInventoryInfoEntity;
import com.iitm.hosteldine.model.hostel.HostelRoomInfoEntity;
import com.iitm.hosteldine.model.hostel.HostelRoomInventoryEntity;
import com.iitm.hosteldine.repository.asset.AssetInventoryInfoRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomInventoryRepository;
import com.iitm.hosteldine.service.asset.AssetConfigurationService;
import com.iitm.hosteldine.util.ExcelUtility;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BulkRoomInventoryService {

	private final HostelMasterService hostelMasterService;
	private final HostelRoomInfoService hostelRoomInfoService;
	private final AssetConfigurationService assetConfigurationService;
	private final AssetInventoryInfoRepository assetInventoryInfoRepo;
	private final HostelRoomInventoryRepository hostelRoomInventoryRepo;
	private final ExcelUtility excelUtility;

	public List<AssetCategoryForm> getStudentRoomInventoryDetails() {
		return null;
	}

	public Workbook downloadRoomInventoryBulkUploadTemplate() {
		String sheetName = "Bulk Room Allotment By Hostel";
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);
		int columnCount = 0;
		String[] headerData = ExcelConstants.ROOM_INVENTORY_HEADER_DATA;
		String[] headerDataWidth = ExcelConstants.ROOM_INVENTORY_HEADER_DATA_WIDTH;
		
		/**
		 * Data Style
		 */
		excelUtility.setDataStyle(workbook);
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

		// Dropdown data for the first columns
		List<String> assetCategory = assetConfigurationService.getCategoryList().stream()
				.map(AssetCategoryForm::getCategoryName).collect(Collectors.toList());

		// Dropdown data for the three columns
		List<String> hostelNames = hostelMasterService.getHostelList().stream().map(HostelMasterDto::getHostelName)
				.collect(Collectors.toList());

		// Dropdown data for the fourth columns
		List<String> roomNumbers = hostelRoomInfoService.getRoomNoList().stream().collect(Collectors.toList());

		// Add dropdown data to the hidden sheet
		excelUtility.addDropdownDataToHiddenSheet(hiddenSheet, 0, assetCategory);
		excelUtility.addDropdownDataToHiddenSheet(hiddenSheet, 2, hostelNames);
		excelUtility.addDropdownDataToHiddenSheet(hiddenSheet, 3, roomNumbers);

		// Add dropdowns to the main sheet columns
		ExcelUtility.addDropdownToColumn(sheet, 1, 100, 0, "DropdownData!$A$1:$A$" + assetCategory.size()); // Asset Category
		ExcelUtility.addDropdownToColumn(sheet, 1, 100, 2, "DropdownData!$C$1:$C$" + hostelNames.size()); // Hostel Name
		ExcelUtility.addDropdownToColumn(sheet, 1, 100, 3, "DropdownData!$D$1:$D$" + roomNumbers.size()); // Room No

		// Auto-size the columns
		IntStream.range(0, headerData.length).forEach(sheet::autoSizeColumn);
		return workbook;
	}

	@Transactional
	public RoomInventoryForm saveRoomInventoryBulkUpload(MultipartFile file) throws Exception {
		List<RoomInventoryForm> roomInfoList = new ArrayList<>();
		List<HostelRoomInventoryEntity> hostelRoomInventoryList = new ArrayList<>();
		RoomInventoryForm roomInfoDto = new RoomInventoryForm();
		DataFormatter formatter = new DataFormatter();
		roomInfoDto.setErrorList(new ArrayList<>());

		try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
			Sheet sheet = workbook.getSheetAt(0);
			// Assuming first row is header
			int rowNumber = 0;

			Row firstRow = sheet.getRow(0);
			ArrayList<String> cellValues = new ArrayList<>();
			for (int i = 0; i < 4; i++) {
				Cell cell = firstRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				cellValues.add(formatter.formatCellValue(cell));
			}
			// Checking labels
			boolean isAssetCategory = cellValues.contains("Asset Category *");
			boolean isAssetName = cellValues.contains("Asset Name *");
			boolean isHostel = cellValues.contains("Hostel Name *");
			boolean isRoomNo = cellValues.contains("Room Number *");

			if (isAssetCategory && isAssetName && isHostel && isRoomNo) {
				int rowCount = ExcelUtility.countNonEmptyRows(sheet);
				boolean hasValidData = false;
				for (Row row : sheet) {
					if (rowCount > rowNumber) {
						if (rowNumber == 0) { // Skip header row
							rowNumber++;
							continue;
						}

						Integer column = 0;
						boolean error = false;
						RoomInventoryForm form = new RoomInventoryForm();
						StringBuilder errorDetails = new StringBuilder();

						// Asset Category
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							Cell assetCategoryCell = row.getCell(column);
							String cellValue = assetCategoryCell.getStringCellValue().trim();
							try {
								// Check if the Asset Category contains only alphabetic characters and spaces
								if (StringUtils.isAlphaSpace(cellValue)) {
									// Get asset category information
									AssetCategoryForm assetCategoryInfo = getAssetCategoryInfo(cellValue);

									// If valid data is found, set it in the form
									form.setAssetCategoryId(assetCategoryInfo.getId());
									form.setAssetCategory(assetCategoryInfo.getCategoryName());
									form.setAssetCategoryShortcode(assetCategoryInfo.getAssetCategoryShortcode());
								} else {
									// If the Asset Category is invalid, throw an error
									excelUtility.cellError(
											"Asset Category can accept alphabetic characters and spaces only", column,
											rowNumber, form);
									error = true;
									errorDetails.append("-").append(form.getExcelErrorMsg()).append("\n");
								}
							} catch (IllegalArgumentException ex) {
								// If an exception occurs, it means the category was not found
								excelUtility.cellError("Asset Category is not found: " + cellValue, column,
										rowNumber, form);
								error = true;
								errorDetails.append("-").append(form.getExcelErrorMsg()).append("\n");
							}
						} else {
							// If the cell is empty, throw an error
							excelUtility.cellError("Asset Category should not be empty", column, rowNumber, form);
							error = true;
							errorDetails.append("-").append(form.getExcelErrorMsg()).append("\n");
						}
						column++;

						// Asset Name
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							Cell assetNameCell = row.getCell(column);
							String assetName = assetNameCell.toString().trim();

							// Check if the asset name contains only alphabetic characters and spaces
							if (StringUtils.isAlphaSpace(assetName)) {
								form.setAssetName(assetName);
							} else {
								// If the asset name is invalid, throw an error
								excelUtility.cellError("Asset Name can accept alphabetic characters and spaces only", column,
										rowNumber, form);
								error = true;
								errorDetails.append("-").append(form.getExcelErrorMsg()).append("\n");
							}
						} else {
							// If the asset name is empty, throw an error
							excelUtility.cellError("Asset Name should not be empty", column, rowNumber, form);
							error = true;
							errorDetails.append("-").append(form.getExcelErrorMsg()).append("\n");
						}
						column++;

						// Hostel Name
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							Cell hostelNameCell = row.getCell(column);
							String cellValue = hostelNameCell.getStringCellValue().trim();
							try {
								// Check if the Hostel name contains only alphabetic characters and spaces
								if (StringUtils.isAlphaSpace(cellValue)) {
									// Check if the Hostel exists
									HostelMasterDto hostelInfo = hostelMasterService.checkHostelExist(cellValue);
									// If a valid Hostel Name is found, set it in the form
									form.setHostelName(hostelInfo.getHostelName());
									form.setHostelId(hostelInfo.getId());
									form.setHostelShortCode(hostelInfo.getHostelShortCode());
								} else {
									// If the Hostel name is invalid, throw an error
									excelUtility.cellError(
											"Hostel Name can accept alphabetic characters and spaces only", column,
											rowNumber, form);
									error = true;
									errorDetails.append("-").append(form.getExcelErrorMsg()).append("\n");
								}
							} catch (IllegalArgumentException ex) {
								// If an exception occurs, it means the Hostel was not found
								excelUtility.cellError("Hostel is not found: " + cellValue, column, rowNumber,
										form);
								error = true;
								errorDetails.append("-").append(form.getExcelErrorMsg()).append("\n");
							}
						} else {
							// If the cell is empty, throw an error
							excelUtility.cellError("Hostel Name should not be empty", column, rowNumber, form);
							error = true;
							errorDetails.append("-").append(form.getExcelErrorMsg()).append("\n");
						}
						column++; 
						
						// Room Number
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
						    Cell roomNoCell = row.getCell(column);
						    String cellValue = "";
						    try {
						        // Check if the Room Number Exists
						        switch (roomNoCell.getCellType()) {
						            case STRING:
						                cellValue = roomNoCell.getStringCellValue();
						                break;
						            case NUMERIC:
						                cellValue = String.valueOf((int) roomNoCell.getNumericCellValue());
						                break;
						            default:
						                throw new IllegalStateException("Unexpected cell type: " + roomNoCell.getCellType());
						        }

						        // Get room details (roomNo and roomId)
						        HostelRoomInfoDto roomInfo = checkRoomExist(form.getHostelId(), cellValue);
						        form.setRoomNo(roomInfo.getRoomNo());
						        form.setRoomId(roomInfo.getId());

							} catch (IllegalArgumentException ex) {
								// If an exception occurs, it means the Room was not found
								excelUtility.cellError("Room " + cellValue + " wasn't found in the hostel "
										+ form.getHostelName(), column, rowNumber, form);
								error = true;
								errorDetails.append("-").append(form.getExcelErrorMsg()).append("\n");
							}
						} else {
						    // If the cell is empty, throw an error
						    excelUtility.cellError("Room Number should not be empty", column, rowNumber, form);
						    error = true;
						    errorDetails.append("-").append(form.getExcelErrorMsg()).append("\n");
						}
						column++;

						// Add errors for the row to the RoomInventoryForm if any
						if (error) {
							roomInfoDto.getErrorList().add("Row " + (rowNumber + 1) + ": " + errorDetails.toString());
							hasValidData = true;
						} else {
							roomInfoList.add(form);
							hasValidData = true;
						}
						rowNumber++;
					}
				}
				// If no valid data was processed, show an error message
				if (!hasValidData) {
					roomInfoDto.getErrorList().add("Empty data in the Excel file");
				}
			} else {
				roomInfoDto.getErrorList().add("Invalid Room Inventory Excel Template");
			}
			// Sorting Room Inventory List
			List<RoomInventoryForm> sortedRoomInfoList = roomInfoList.stream()
					.sorted(Comparator.comparing(RoomInventoryForm::getAssetCategoryId)
							.thenComparing(RoomInventoryForm::getHostelName)
							.thenComparing(RoomInventoryForm::getAssetName))
					.collect(Collectors.toList());

			if (!roomInfoDto.getErrorList().isEmpty()) {
				return roomInfoDto;
			}

			// Asset Code Generation Logic and Collecting Data for Bulk Save
			for (RoomInventoryForm form : sortedRoomInfoList) {
				String baseAssetCode = form.getHostelShortCode() + form.getRoomNo() + form.getAssetCategoryShortcode();
				String assetCode = generateUniqueAssetCode(baseAssetCode);

				// Set the generated asset code in the form
				form.setAssetCode(assetCode);

				// Create AssetInventoryInfoEntity and save it to get the ID
				AssetInventoryInfoEntity assetInventoryInfo = createAssetInventoryInfoEntity(form);
				assetInventoryInfo = assetInventoryInfoRepo.save(assetInventoryInfo); // Save individually to get the ID

				// Create HostelRoomInventoryEntity and set the asset ID in it
				form.setAssetId(String.valueOf(assetInventoryInfo.getAssetId()));
				HostelRoomInventoryEntity hostelRoomInventory = createHostelRoomInventoryEntity(form);

				// Add the hostelRoomInventory to the list for bulk save later
				hostelRoomInventoryList.add(hostelRoomInventory);
			}

			// Bulk Save All HostelRoomInventoryEntities
			hostelRoomInventoryRepo.saveAll(hostelRoomInventoryList); // Saves all hostel room inventory in one go
		}
		return roomInfoDto;
	}

	/**
	 * This method is for mapping the values from the RoomInventoryForm value to the AssetInventoryInfoEntity
	 */
	public AssetInventoryInfoEntity createAssetInventoryInfoEntity(RoomInventoryForm form) {
		AssetInventoryInfoEntity assetCategoryInfoEntity = AssetInventoryInfoMapper.INSTANCE
				.toAssetInventoryInfoEntity(form);
		assetCategoryInfoEntity.onCreate();
		return assetCategoryInfoEntity;
	}

	/**
	 * This method is for mapping the values from the RoomInventoryForm value to the HostelRoomInventoryEntity
	 */
	public HostelRoomInventoryEntity createHostelRoomInventoryEntity(RoomInventoryForm form) {
		HostelRoomInventoryEntity hostelRoomInventoryEntity = HostelRoomInventoryMapper.INSTANCE
				.toHostelRoomInventoryEntity(form);
		hostelRoomInventoryEntity.onCreate();
		return hostelRoomInventoryEntity;
	}

	/**
	 * Retrieves the values of id, category name and asset shortcode from the ASSET_CATEGORY_INFO table 
	 */
	public AssetCategoryForm getAssetCategoryInfo(String value) {
		return assetConfigurationService.getCategoryList().stream()
				.filter(assetCategory -> assetCategory.getCategoryName().equalsIgnoreCase(value)).findFirst()
				.map(assetCategory -> new AssetCategoryForm(assetCategory.getId(), assetCategory.getCategoryName(),
						assetCategory.getAssetCategoryShortcode()))
				.orElseThrow(() -> new IllegalArgumentException("Asset Category is not found: " + value));
	}

	public HostelRoomInfoDto checkRoomExist(Long hostelId, String roomNo) {
		// Find the room by hostel ID and room number
		HostelRoomInfoEntity roomEntity = hostelRoomInfoService.findRoomByHostelIdAndRoomNo(hostelId, roomNo);

		if (roomEntity != null) {
			return new HostelRoomInfoDto(roomEntity.getId(), roomEntity.getRoomNo());
		} else {
			throw new IllegalArgumentException("Room number is not found: " + roomNo);
		}
	}

	/**
	 * This method generates a unique asset code by checking the existing codes in the database.
	 */
	public String generateUniqueAssetCode(String baseAssetCode) {
		int suffix = 1; // Start with suffix 1
		String newAssetCode = baseAssetCode + suffix;

		// Check if this asset code exists in the database
		while (checkAssetCodeExist(newAssetCode)) {
			suffix++; // Increment the suffix
			newAssetCode = baseAssetCode + suffix; // Generate new asset code
		}

		return newAssetCode;
	}

	/**
	 * This method checks if the asset code exists in the asset inventory table.
	 */
	public Boolean checkAssetCodeExist(String assetCode) {
		boolean result = false;
		Optional<AssetInventoryInfoEntity> category = Optional.empty();
		category = assetInventoryInfoRepo.findByActiveFlagAndAssetCode(ModelConstants.STATUS_ACTIVE, assetCode);
		if (Objects.nonNull(category) && category.isPresent()) {
			result = true;
		}
		return result;
	}
}
