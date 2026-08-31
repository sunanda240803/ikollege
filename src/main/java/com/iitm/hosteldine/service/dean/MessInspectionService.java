package com.iitm.hosteldine.service.dean;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.dean.MessInspectionReportDto;
import com.iitm.hosteldine.dto.dean.PropertyDto;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.dto.warden.WardenInfoDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.hostel.MessInspectionReportMapper;
import com.iitm.hosteldine.model.mess.MessInspectionReportEntity;
import com.iitm.hosteldine.model.mess.MessMasterEntity;
import com.iitm.hosteldine.model.warden.WardenInfoEntity;
import com.iitm.hosteldine.repository.dean.DynamicUserTabRepository;
import com.iitm.hosteldine.repository.mess.MessInspectionReportRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.repository.warden.WardenInfoRepository;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.service.warden.WardenInfoService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.RoleEnum;
import com.iitm.hosteldine.util.Utility;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MessInspectionService {

	private final DynamicUserTabRepository dynamicUserTabRepository;
	private final MessageSource messageSource;
	private final MessInspectionReportRepository messInspectionReportRepository;
	private final MessMasterRepository messMasterRepository;
	private final WardenInfoRepository WardenInfoRepository;
	private final MessMasterService messMasterService;
	private final WardenInfoService wardenInfoService;
	private final Utility utility;
	private final FileService fileService;

	public List<MessInspectionReportDto> getMessInspection(PaginationForm form, String value) {
		// Updating additional param values

		String messId = (form.getAdditionalParam().get("messName") != null && !form.getAdditionalParam().get("messName").equals("")) ? form.getAdditionalParam().get("messName").toString() : null;
		LocalDate fromDate = (form.getAdditionalParam().get("inspectionFromDate") != null && !form.getAdditionalParam().get("inspectionFromDate").equals("")) ? LocalDate.parse(form.getAdditionalParam().get("inspectionFromDate").toString()) : null;
		LocalDate toDate = (form.getAdditionalParam().get("inspectionToDate") != null && !form.getAdditionalParam().get("inspectionToDate").equals("")) ? LocalDate.parse(form.getAdditionalParam().get("inspectionToDate").toString()) : null;


		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());
		List<MessInspectionReportDto> result;

		String userRole = SecurityCtxUtil.userRole();
		String userId = SecurityCtxUtil.userId();
        Long wardenId =  getWardenId(form);

		result = messInspectionReportRepository.getMessInspectionList(
				wardenId,
				messId != null ? Long.valueOf(messId) : null,
				fromDate != null ? fromDate.toString() : null,
				toDate != null ? toDate.toString() : null,
				userRole != null ? userRole : null,
				userId != null ? userId : null);


		List<Object[]> subMenuList = dynamicUserTabRepository.getDeanSubMenuListById(value, SecurityCtxUtil.userRole(), SecurityCtxUtil.userName());
		result.stream().map(dto -> {
			List<PropertyDto> actionList = new ArrayList<PropertyDto>();
			for (Object[] action : subMenuList) {
				if (action[2].toString().equals(Constants.COL_LINK) || action[2].toString().equals(Constants.COL_ACTION)) {
					PropertyDto actionDto = new PropertyDto();
					actionDto.setActionIcon(action[5] != null ? action[5].toString() : null);
					actionDto.setActionStyle(action[6] != null ? action[6].toString() : null);
					actionDto.setDisplayName(action[8] != null ? action[8].toString() : null);
					var actionUrl = action[7] != null ? action[7].toString() : null;
					actionDto.setUrl(value + (actionUrl != null ? "/" + actionUrl : "") + "/" + dto.getId());
					actionList.add(actionDto);
				}
			}
			dto.setActionList(actionList);
			return dto;
		}).toList();
		return result;
	}

    private Long getWardenId(PaginationForm form) {
        return 	SecurityCtxUtil.userRole().equalsIgnoreCase(RoleEnum.WARDEN.getValue()) ?
                SecurityCtxUtil.wardenId() :
                (form.getAdditionalParam().get("wardenName") != null &&
                !form.getAdditionalParam().get("wardenName").equals("")) ?
                        Long.parseLong(form.getAdditionalParam().get("wardenName").toString()) : 0L;
    }

    public MessInspectionReportDto getMessInspectionById(String id) {
		Optional<MessInspectionReportEntity> entityOptional = messInspectionReportRepository.findById(Long.valueOf(id));
        return entityOptional.map(MessInspectionReportMapper.INSTANCE::toDto).orElse(null);
    }


	public Workbook getMessInspectionReport(List<MessInspectionReportDto> messInspectionDtoList) throws Exception {
		XSSFWorkbook workbook = null;
		int colCount = 0;
		ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

		try {
			workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(messageSource.getMessage("message.mess.inspection.list", null, Locale.getDefault()));

			// Create styles using ExcelUtility
			XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
			headerStyle.setWrapText(true);
			XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
			dataStyle.setWrapText(true);
			
			XSSFRow rowheadZero = sheet.createRow(1);
            excelUtility.createCell(rowheadZero, 0, messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 12));
            
			// Create second header row
			XSSFRow rowheadFirst = sheet.createRow(2);
			excelUtility.createCell(rowheadFirst, 0, messageSource.getMessage("message.mess.inspection.list", null, Locale.getDefault()), headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 12));

			// Create third header row for report date
			XSSFRow rowheadSecond = sheet.createRow(3);
			SimpleDateFormat sdf = new SimpleDateFormat(
    				messageSource.getMessage("session.date.format", null, Locale.getDefault()));
    		String reportDate = messageSource.getMessage("message.label.report.date.colon", null, Locale.getDefault())
    				+ sdf.format(new Date());
            excelUtility.createCell(rowheadSecond, 0, reportDate, headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 12));

			// Create column headers
			XSSFRow rowhead = sheet.createRow(5);
			String[] headers = { messageSource.getMessage("message.label.mess.rebate.sl.no", null, Locale.getDefault()),
					messageSource.getMessage("message.label.mess.inspection.date", null, Locale.getDefault()),
					messageSource.getMessage("message.label.mess.inspection.warden.name", null, Locale.getDefault()),
					messageSource.getMessage("message.label.mess.inspection.mess.name", null, Locale.getDefault()),
					messageSource.getMessage("message.label.mess.inspection.kitchen.cleanliness", null, Locale.getDefault()),
					messageSource.getMessage("message.label.mess.inspection.plate.cleanliness", null, Locale.getDefault()),
					messageSource.getMessage("message.label.mess.inspection.mess.hygiene", null, Locale.getDefault()),
					messageSource.getMessage("message.label.mess.inspection.queue.maintenance", null, Locale.getDefault()),
					messageSource.getMessage("message.label.mess.inspection.food.availability", null, Locale.getDefault()),
					messageSource.getMessage("message.label.mess.inspection.student.feedBack", null, Locale.getDefault()),
					messageSource.getMessage("message.label.mess.inspection.any.other.item", null, Locale.getDefault())

					 };

			// Add headers to the sheet
			for (String header : headers) {
				excelUtility.createCell(rowhead, colCount, header, headerStyle);
				sheet.setColumnWidth(colCount, 4000); // Set column width
				colCount++;
			}

			// Populate data rows
			int rowcount = 5;
			if (CollectionUtils.isNotEmpty(messInspectionDtoList)) {
				for (MessInspectionReportDto messInspectionDto : messInspectionDtoList) {
					rowcount++;
					XSSFRow row = sheet.createRow(rowcount);
					excelUtility.createCell(row, 0, messInspectionDto.getId(), dataStyle);
					excelUtility.createCell(row, 1, utility.convertDateToString(messInspectionDto.getCreatedAt(), Constants.FRONTEND_DATE_FORMAT), dataStyle);
					excelUtility.createCell(row, 2, messInspectionDto.getWardenName(), dataStyle);
					excelUtility.createCell(row, 3, messInspectionDto.getMessName(), dataStyle);
					excelUtility.createCell(row, 4, messInspectionDto.getCleanlinessKitchen(), dataStyle);
					excelUtility.createCell(row, 5, messInspectionDto.getCleanlinessPlate(), dataStyle);
					excelUtility.createCell(row, 6, messInspectionDto.getHygieneMess(), dataStyle);
					excelUtility.createCell(row, 7, messInspectionDto.getQueueMaintainance(), dataStyle);
					excelUtility.createCell(row, 8, messInspectionDto.getAvailabilityFood(), dataStyle);
					excelUtility.createCell(row, 9, messInspectionDto.getFeedbackStudent(), dataStyle);
					excelUtility.createCell(row, 10, messInspectionDto.getOtherItem(), dataStyle);
				}
			}

			// Auto-size columns with a maximum width limit
			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
				// Cap the column width to 10000 (about 100 characters) to prevent extremely wide columns
				if (sheet.getColumnWidth(i) > 10000) {
					sheet.setColumnWidth(i, 10000);
				}
			}


		} catch (Exception exception) {
			exception.printStackTrace();
			throw new Exception("Error generating Mess Inspection List report", exception);
		}
		return workbook;
	}

	public Boolean saveOrUpdate(MessInspectionReportDto dto) {

		String loggedInUserRole = SecurityCtxUtil.userRole();
		String loggedInUserName = SecurityCtxUtil.userName();
		Boolean isWardenLogin = StringUtils.equalsIgnoreCase(loggedInUserRole, Constants.USER_ROLE_WARDEN);
		Boolean isDeanLogin = StringUtils.equalsIgnoreCase(loggedInUserRole, Constants.USER_ROLE_DEAN);

		MessInspectionReportEntity newEntity = MessInspectionReportMapper.INSTANCE.toEntity(dto);
		Optional<MessMasterEntity> messMasterEntity = messMasterRepository.findById(Long.valueOf(dto.getMessName()));
		if(messMasterEntity.isEmpty()){
			return false;
		}
		newEntity.setMessMaster(messMasterEntity.get());
		if(Objects.nonNull(dto.getFile())) {
			newEntity.setFileName(dto.getFile().getOriginalFilename());
            try {
                saveFile(dto.getFile(), dto.getFile().getOriginalFilename());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
		if(isWardenLogin) {
			List<WardenInfoEntity> wardenInfoEntityList = WardenInfoRepository.findByLdapUsernameAndActiveFlag(loggedInUserName, ModelConstants.STATUS_ACTIVE);
			if(CollectionUtils.isNotEmpty(wardenInfoEntityList)){
				WardenInfoEntity wardenInfoEntity = wardenInfoEntityList.getFirst();
				newEntity.setWardenId(Math.toIntExact(wardenInfoEntity.getId()));
				newEntity.setWardenName(wardenInfoEntity.getWardenName());
			}
		} else {
			newEntity.setWardenId(Integer.valueOf(dto.getWardenName()));
			Optional<WardenInfoEntity> wardenInfoEntity = WardenInfoRepository.findById(Long.valueOf(dto.getWardenName()));
			if(wardenInfoEntity.isPresent()) {
				newEntity.setWardenName(wardenInfoEntity.get().getWardenName());
			}
		}
		messInspectionReportRepository.save(newEntity);
		return true;
	}

	public void saveFile(MultipartFile file, String fileName) throws Exception {
		fileService.encodeFile(SimsConfigDataService.MESS_INSPECTION_FILE_PATH, file.getBytes(), fileName);
	}
	
	public Map<Long, String> fetchMessListAsMap() {
		List<MessMasterDto> messList = messMasterService.getMessMasterList();
		Map<Long, String> messMap = new HashMap<>();
		for (MessMasterDto mess : messList) {
			messMap.put(mess.getId(), mess.getMessName());
		}
		return messMap;
	}
	
	public Map<Long, String> fetchWardenListAsMap() {
		List<WardenInfoDto> wardenList = wardenInfoService.getAllWardenDetailsList();
		Map<Long, String> wardenMap = new HashMap<>();
		for (WardenInfoDto wardenInfoDto : wardenList) {
			wardenMap.put(wardenInfoDto.getId(), wardenInfoDto.getWardenName());
		}
		return wardenMap;
	}

	public Page<MessInspectionReportDto> getMessInspectionList(PaginationForm form) {
		String wardenId = (form.getAdditionalParam().get("wardenId") != null && !form.getAdditionalParam().get("wardenId").equals("")) ? form.getAdditionalParam().get("wardenId").toString() : null;
		String messId = (form.getAdditionalParam().get("messId") != null && !form.getAdditionalParam().get("messId").equals("")) ? form.getAdditionalParam().get("messId").toString() : null;
		LocalDate fromDate = (form.getAdditionalParam().get("inspectionFromDate") != null && !form.getAdditionalParam().get("inspectionFromDate").equals("")) ? LocalDate.parse(form.getAdditionalParam().get("inspectionFromDate").toString()) : null;
		LocalDate toDate = (form.getAdditionalParam().get("inspectionToDate") != null && !form.getAdditionalParam().get("inspectionToDate").equals("")) ? LocalDate.parse(form.getAdditionalParam().get("inspectionToDate").toString()) : null;
		Pageable pageable = Pageable.unpaged();
		Page<MessInspectionReportDto> result = Page.empty();

		String userRole = SecurityCtxUtil.userRole();
		result = messInspectionReportRepository.getMessInspectionList(
				wardenId != null ? Long.valueOf(wardenId) : null,
				messId != null ? Long.valueOf(messId) : null,
				fromDate != null ? fromDate.toString() : null,
				toDate != null ? toDate.toString() : null,
                userRole, SecurityCtxUtil.userId(),
				pageable);
		
		return result;		
	}
}
