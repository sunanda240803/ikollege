package com.iitm.hosteldine.service.mess;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.iitm.hosteldine.entity.mess.MessLedgerBEntity;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.constant.mess.MessAllotmentConstants;
import com.iitm.hosteldine.dto.dean.FilterCriteriaDto;
import com.iitm.hosteldine.dto.mess.MessAllottedListDTO;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.dto.student.AllStudentsDetailsViewDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.mess.MessMasterControllerMapper;
import com.iitm.hosteldine.mapper.mess.StudentMessChangeWorkflowMapper;
import com.iitm.hosteldine.mapper.studentDashboard.StudentMessDetailsMapper;
import com.iitm.hosteldine.model.mess.StudentMessChangeWorkflowEntity;
import com.iitm.hosteldine.model.mess.StudentMessDetailsEntity;
import com.iitm.hosteldine.repository.mess.MessMasterControllerRepository;
import com.iitm.hosteldine.repository.mess.StudentMessChangeWorkflowRepository;
import com.iitm.hosteldine.repository.studentDashboard.StudentMessDetailsRepository;
import com.iitm.hosteldine.service.AllStudentsDetailsViewService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.validator.common.ValidationCommon;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessAllottedListService {

	private final MessMasterControllerRepository messMasterControllerRepository;
	private final StudentMessDetailsRepository studentMessDetailsRepository;
	private final StudentMessChangeWorkflowRepository studentMessChangeWorkflowRepository;
	private final MessageSource messageSource;
	private final AllStudentsDetailsViewService allStudentsDetailsViewService;
	private final ExcelUtility excelUtility;
	private final MessMasterCommonService messMasterCommonService;
	private final MessMasterService messMasterService;
	private final InMemoryLogService logService;
	private final CommonResponseUtil commonResponseUtil;

	public List<MessMasterControllerDto> getMessPeriodList() {
		return messMasterControllerRepository.getMessMasterList(ModelConstants.STATUS_ACTIVE)
				.map(MessMasterControllerMapper.INSTANCE::toDto).orElse(Collections.emptyList());
	}

	public Page<MessAllottedListDTO> getMessAllottedList(PaginationForm form) throws Exception {
		Page<Object[]> entityList = fetchMessAllottedList(form);
		MessMasterControllerDto mmcDto = messMasterCommonService.getCurrentMessPeriod();
		return entityList.map(record -> {
			try {
				MessAllottedListDTO dto = new MessAllottedListDTO();

                dto.setId(record[0] != null ? Long.parseLong(record[0].toString()) : null);
				Long mmcId = record[1] != null ? Long.parseLong(record[1].toString()) : null;
                dto.setStudentId(getStringValue(record[2]));
                dto.setStudentName(getStringValue(record[3]));
                dto.setMessName(getStringValue(record[4]));
                dto.setMessHead(getStringValue(record[5]));
                dto.setFromDate(record[6] != null ? LocalDate.parse(record[6].toString()) : null);
                dto.setToDate(record[7] != null ? LocalDate.parse(record[7].toString()) : null);
                dto.setChangeFromDate(record[8] != null ? LocalDate.parse(record[8].toString()) : null);
                dto.setChangeToDate(record[9] != null ? LocalDate.parse(record[9].toString()) : null);
				String approvalStatus = record[10] != null ? record[10].toString() : null;
				String pushRemoveStatus = record[11] != null ? record[11].toString() : null;
				String pushStatus = record[12] != null ? record[12].toString() : null;
				String currentActiveFlag = record[13] != null ? record[13].toString() : null;
				String remarks = record[14] != null ? record[14].toString() : null;
				dto.setDescription(getStringValue(record[15]));

				// Status Checks
				boolean isInactive = ModelConstants.STATUS_INACTIVE.equals(currentActiveFlag);
				boolean isToBePushed = WorkflowStatus.TO_BE_PUSHED.getStatus().equals(pushRemoveStatus);
				boolean isPushed = WorkflowStatus.PUSHED.getStatus().equals(pushStatus);
				boolean isNotPending = !WorkflowStatus.PENDING.getStatus().equals(approvalStatus);
				boolean isToBeRemoved = WorkflowStatus.TO_BE_REMOVED.getStatus().equals(pushRemoveStatus);
				boolean isRemoved = WorkflowStatus.REMOVED.getStatus().equals(pushStatus);

				if (mmcDto != null && mmcDto.getId() != null && mmcId != null && mmcDto.getId() == mmcId
						&& (isToBePushed || isPushed || isNotPending) && dto.getChangeToDate().isAfter(LocalDate.now())
						&& !isInactive) {
					dto.setToChange(WorkflowStatus.SHOW.getStatus());
				}

				if (mmcDto != null && mmcDto.getId() != null
						&& !isToBeRemoved && !isRemoved && dto.getChangeToDate().isAfter(LocalDate.now())) {
					dto.setToRemove(WorkflowStatus.SHOW.getStatus());
				}

				if (!isInactive) {
					if (remarks != null) {
						dto.setMessStatus(remarks);
					} else {
						dto.setMessStatus(WorkflowStatus.ALLOTTED.getStatus());
					}
				} else if (remarks != null) {
					if (WorkflowStatus.CHANGE.getStatus().equals(remarks)) {
						dto.setMessStatus(WorkflowStatus.MESS_CHANGED.getStatus());
					} else if (WorkflowStatus.REMOVE.getStatus().equals(remarks)) {
						dto.setMessStatus(WorkflowStatus.REMOVED.getStatus());
					} else if (WorkflowStatus.VACATE.getStatus().equals(remarks)) {
						dto.setMessStatus(WorkflowStatus.VACATED.getStatus());
					} else if (WorkflowStatus.REBATE.getStatus().equals(remarks)) {
						dto.setMessStatus(WorkflowStatus.REBATE_TAKEN.getStatus());
					} else {
						dto.setMessStatus(remarks);
					}
				} else {
					dto.setMessStatus(WorkflowStatus.IN_ACTIVE.getStatus());
				}
				return dto;

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}

	private Page<Object[]> fetchMessAllottedList(PaginationForm form) {
		FilterCriteriaDto filter = getFilterData(form);
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());
		return messMasterControllerRepository.getMessAllottedList(
				ValidationCommon.isValid(filter.getStudentName()) ? filter.getStudentName().trim() : null,
				ValidationCommon.isValid(filter.getStudentId()) ? filter.getStudentId().trim() : null,
				filter.getMessPeriod(), filter.getMessName(), pageable);
	}

	private List<Object[]> fetchMessAllottedListReport(PaginationForm form) {
		FilterCriteriaDto filter = getFilterData(form);
		return messMasterControllerRepository.getMessAllottedList(
				ValidationCommon.isValid(filter.getStudentName()) ? filter.getStudentName().trim() : null,
				ValidationCommon.isValid(filter.getStudentId()) ? filter.getStudentId().trim() : null,
				filter.getMessPeriod(), filter.getMessName());
	}

	public FilterCriteriaDto getFilterData(PaginationForm form) {
		FilterCriteriaDto filter = new FilterCriteriaDto();
		filter.setMessPeriod(ValidationCommon.toLongOrZero(form.getAdditionalParam().get("messPeriod")));
		filter.setMessName(ValidationCommon.toLongOrZero(form.getAdditionalParam().get("messName")));
		filter.setStudentName(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("studentName")));
		filter.setStudentId(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("studentId")));
		return filter;
	}

	public MessAllottedListDTO getMessChangeDetails(Long id) {
		Object[] result = studentMessDetailsRepository.getStudentMessDetailsByIdAndActiveFlag(id,
				ModelConstants.STATUS_ACTIVE);
		if (result != null && result.length > 0) {
			MessAllottedListDTO dto = new MessAllottedListDTO();
			Object[] obj = (Object[]) result[0];
			dto.setId(obj[0] != null ? Long.parseLong(obj[0].toString()) : null);
			dto.setStudentId(getStringValue(String.valueOf(obj[1])));
			dto.setFromDate(obj[2] != null ? LocalDate.parse(obj[2].toString()) : null);
			dto.setToDate(obj[3] != null ? LocalDate.parse(obj[3].toString()) : null);
			dto.setStudentName(getStringValue(String.valueOf(obj[4])));
			dto.setGender(getStringValue(String.valueOf(obj[5])));
			dto.setMessName(getStringValue(String.valueOf(obj[6])));
			dto.setMessHead(getStringValue(String.valueOf(obj[7])));
			dto.setMessId(obj[8] != null ? Long.parseLong(obj[8].toString()) : null);
			dto.setChangeFromDate(obj[9] != null ? LocalDate.parse(obj[9].toString()) : null);
			dto.setChangeToDate(obj[10] != null ? LocalDate.parse(obj[10].toString()) : null);
			return dto;
		}
		return null;
	}

	@Transactional
	public String saveOrUpdateMessAllotmentDetails(MessAllottedListDTO dto) {
	    if (WorkflowStatus.CHANGE.getStatus().equalsIgnoreCase(dto.getType())) {
	        if (dto.getEffectiveTill() == null && dto.getEffectiveFromDate() != null) {
	            dto.setEffectiveTill(dto.getEffectiveFromDate().minusDays(1));
		        dto.setFromDate(dto.getEffectiveFromDate());
		        dto.setMessId(dto.getChangeMessId());
	        }
	        List<MessAllottedListDTO> messAllottedChangeRemoveList = List.of(dto);
	        saveChangeMessDetails(messAllottedChangeRemoveList,"changeMess");
	        return "message.mess.change.succuess";

	    } else if (WorkflowStatus.REMOVE.getStatus().equalsIgnoreCase(dto.getType())) {
	        List<MessAllottedListDTO> messAllottedDtoList = List.of(dto);
	        saveRemoveMessDetails(messAllottedDtoList,"removeMess");
	        return "message.mess.remove.succuess";

	    } else if (messageSource.getMessage("message.button.add", null, Locale.getDefault())
	            .equalsIgnoreCase(dto.getType())) {
	        List<MessAllottedListDTO> messAllottedDtoList = List.of(dto);
	        saveNewMessDetails(messAllottedDtoList, fetchCurrentMessPeriodId(),"AddMess");
	        return "message.mess.allotted.succuess";
	    }
	    return null;
	}

	public void saveChangeMessDetails(List<MessAllottedListDTO> messAllottedDtoList,String tag) {
	    List<StudentMessChangeWorkflowEntity> workflowEntities = new ArrayList<>();
	    List<StudentMessDetailsEntity> detailsRemoveList = new ArrayList<>();
	    List<StudentMessDetailsEntity> detailsNewList = new ArrayList<>();

	    for (MessAllottedListDTO dto : messAllottedDtoList) {
	        // Workflow entity
	        StudentMessChangeWorkflowEntity workflowEntity = new StudentMessChangeWorkflowEntity();
	        MessAllottedListDTO existingDto = getMessChangeDetails(dto.getId());
	        StudentMessChangeWorkflowMapper.INSTANCE.toEntity(workflowEntity, dto, existingDto);
	        workflowEntity.setRequestedDate(String.valueOf(DateUtility.getNowTimeInstant()));
	        workflowEntity.setMmcId(fetchCurrentMessPeriodId());
	        workflowEntity.onCreate();
	        workflowEntities.add(workflowEntity);

	        // Remove old entity
	        Optional<StudentMessDetailsEntity> existingEntity =
	                studentMessDetailsRepository.findByIdAndActiveFlag(dto.getId(), ModelConstants.STATUS_ACTIVE);
	        existingEntity.ifPresent(oldEntity -> {
				StudentMessDetailsEntity updatedEntity = createUpdatedStudentMessDetailsEntity(oldEntity, dto,
						WorkflowStatus.CHANGE.getStatus());
	            detailsRemoveList.add(updatedEntity);
	        });

	        // Add new entity
	        StudentMessDetailsEntity newEntity = createNewStudentMessDetailsEntity(dto, fetchCurrentMessPeriodId());
			addLog(tag, 0, commonResponseUtil.getMessage("upload.inserting.student")+dto.getStudentId().toUpperCase());
	        detailsNewList.add(newEntity);
	    }

	    studentMessChangeWorkflowRepository.saveAll(workflowEntities);
	    studentMessDetailsRepository.saveAll(detailsRemoveList);
		List<StudentMessDetailsEntity> saved = studentMessDetailsRepository.saveAll(detailsNewList);
		addLog(tag, 0, commonResponseUtil.getMessage("upload.total.save.records")+saved.size());
	}

	public void saveRemoveMessDetails(List<MessAllottedListDTO> messAllottedDtoList,String tag) {
		List<StudentMessDetailsEntity> detailsRemoveList = new ArrayList<>();
		for (MessAllottedListDTO dto : messAllottedDtoList) {
			Optional<StudentMessDetailsEntity> existingEntity = studentMessDetailsRepository
					.findByIdAndActiveFlag(dto.getId(), ModelConstants.STATUS_ACTIVE);
			existingEntity.ifPresent(oldEntity -> {
				StudentMessDetailsEntity updatedEntity = createUpdatedStudentMessDetailsEntity(oldEntity, dto,
						WorkflowStatus.REMOVE.getStatus());
				addLog(tag, 0, commonResponseUtil.getMessage("upload.inserting.student")+dto.getStudentId().toUpperCase());
				detailsRemoveList.add(updatedEntity);
			});
		}
		List<StudentMessDetailsEntity> saved = studentMessDetailsRepository.saveAll(detailsRemoveList);
		addLog(tag, 0, commonResponseUtil.getMessage("upload.total.save.records")+saved.size());
	}

	public void saveNewMessDetails(List<MessAllottedListDTO> messAllottedDtoList, Long mmcId,String tag) {
	    List<StudentMessDetailsEntity> detailsList = new ArrayList<>();
	    for (MessAllottedListDTO dtoEntry : messAllottedDtoList) {
	        StudentMessDetailsEntity entity = createNewStudentMessDetailsEntity(dtoEntry, mmcId);
			addLog(tag, 0, commonResponseUtil.getMessage("upload.inserting.student")+dtoEntry.getStudentId().toUpperCase());
			detailsList.add(entity);
	    }
		List<StudentMessDetailsEntity> saved = studentMessDetailsRepository.saveAll(detailsList);
		addLog(tag, 0, commonResponseUtil.getMessage("upload.total.save.records")+saved.size());
	}

	private StudentMessDetailsEntity createNewStudentMessDetailsEntity(MessAllottedListDTO dto, Long mmcId) {
	    StudentMessDetailsEntity entity = new StudentMessDetailsEntity();
	    StudentMessDetailsMapper.INSTANCE.toEntity(entity, dto);
	    entity.setMmcId(mmcId);
	    entity.onCreate();
	    return entity;
	}

	private StudentMessDetailsEntity createUpdatedStudentMessDetailsEntity(StudentMessDetailsEntity entity,
			MessAllottedListDTO dto, String remarks) {
	    entity.setRemarks(remarks);
	    if (dto.getEffectiveTill() != null) {
	        entity.setToRemoveDate(dto.getEffectiveTill().plusDays(1));
	        entity.setChangeToDate(dto.getEffectiveTill());
	    }
	    entity.setCurrentActiveFlag(ModelConstants.STATUS_INACTIVE);
	    entity.setPushRemoveStatus(WorkflowStatus.TO_BE_REMOVED.getStatus());
        entity.setDescription(dto.getDescription());
	    entity.onUpdate();
	    return entity;
	}

	public Long fetchCurrentMessPeriodId() {
		MessMasterControllerDto mmcDto = messMasterCommonService.getCurrentMessPeriod();
		return (mmcDto != null && mmcDto.getId() != null) ? mmcDto.getId() : null;
	}

	public Long fetchNextMessPeriodId() {
		MessMasterControllerDto mmcDto = messMasterCommonService.getNextMessPeriod();
		return (mmcDto != null && mmcDto.getId() != null) ? mmcDto.getId() : null;
	}

	public Workbook downloadMessAllottedListReport(PaginationForm form) throws Exception {
		String messAllottedReport = messageSource.getMessage("message.label.mess.allotted.list", null,
				Locale.getDefault());
		String sheetName = messAllottedReport;
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);

		int columnCount = ExcelConstants.MESS_ALLOTTED_LIST_DATA.length;
		String[] headerData = ExcelConstants.MESS_ALLOTTED_LIST_DATA;
		String[] headerDataWidth = ExcelConstants.MESS_ALLOTTED_LIST_DATA_WIDTH;

		XSSFCellStyle style3 = excelUtility.setDataStyle(workbook);
		XSSFCellStyle centerAlignStyle = excelUtility.setCenterAlignStyle(workbook);

		// Add new row with title "Office of the Hostel Management - IITMADRAS CAMPUS"
		Row row0 = sheet.createRow(0);
		Cell cell0 = row0.createCell(0);
		cell0.setCellValue(messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null,
				Locale.getDefault()));
		cell0.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

		// Row 1 with wellness report title
		Row row1 = sheet.createRow(1);
		Cell cell1 = row1.createCell(0);
		cell1.setCellValue(messAllottedReport);
		cell1.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, columnCount - 1));

		// Row 2 with report date
		Row row2 = sheet.createRow(2);
		SimpleDateFormat sdf = new SimpleDateFormat(
				messageSource.getMessage("session.date.format", null, Locale.getDefault()));
		String reportDate = messageSource.getMessage("message.label.report.date.colon", null, Locale.getDefault())
				+ sdf.format(new Date());
		cell1 = row2.createCell(0);
		cell1.setCellValue(reportDate);
		cell1.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, columnCount - 1));

		// Row 3 for header data
		Row row3 = sheet.createRow(3);
		excelUtility.createHeader(row3, 0, headerData, workbook);

		// Set column widths
		IntStream.range(0, headerData.length).forEach(i -> {
			int width = Integer.parseInt(headerDataWidth[i]);
			sheet.setColumnWidth(i, width);
		});

		// Fetch and populate wellness data
		List<Object[]> entityList = fetchMessAllottedListReport(form);
		AtomicInteger rowCount = new AtomicInteger(3); // Start from row 4 (index 3)

		entityList.forEach(allotted -> {
			Row row = sheet.createRow(rowCount.incrementAndGet());
			try {
				String[] values = { getNonNullValue(allotted[2]), getNonNullValue(allotted[3]),
						getNonNullValue(DateUtility.formatDate(allotted[8])),
						getNonNullValue(DateUtility.formatDate(allotted[9])), getNonNullValue(allotted[4]) };
				for (int colIdx = 0; colIdx < values.length; colIdx++) {
					excelUtility.createAndSetColumn(sheet, row, colIdx, -1, values[colIdx], style3);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		// Auto-size columns
		for (int i = 0; i < columnCount; i++) {
			sheet.autoSizeColumn(i);
		}
		return workbook;
	}

	public String CheckStudentExists(String id) {
		AllStudentsDetailsViewDto dto = allStudentsDetailsViewService.getCompleteStudentDetails(id);
		return dto != null ? dto.getStudentName() : null;
	}

	public Boolean checkStudentInCurrentMessPeriod(String studentId) {
		return studentMessDetailsRepository.existsByStudentDetailsInfoStudentIdAndCurrentActiveFlagAndMmcId(studentId,
				ModelConstants.STATUS_ACTIVE, fetchCurrentMessPeriodId());
	}

	public Boolean checkStudentInNextMessPeriod(String studentId) {
		return studentMessDetailsRepository.existsByStudentDetailsInfoStudentIdAndCurrentActiveFlagAndMmcId(studentId,
				ModelConstants.STATUS_ACTIVE, fetchNextMessPeriodId());
	}

	public Object fetchMessListByStudentGender(String studentId) {
		AllStudentsDetailsViewDto dto = allStudentsDetailsViewService.getCompleteStudentDetails(studentId);
		List<MessMasterDto> messList = Collections.emptyList();
		if (Objects.nonNull(dto)) {
			messList = messMasterService.getMessListForAllotment(null, dto.getGender());
		}
		return messList.isEmpty() ? MessAllotmentConstants.NO_MESS_AVAILABLE.getString() : messList;
	}

	private String getStringValue(Object obj) {
		return obj != null && !String.valueOf(obj).trim().isEmpty() ? String.valueOf(obj) : Constants.HYPHEN;
	}

	private String getNonNullValue(Object obj) {
		return (obj != null) ? String.valueOf(obj) : Strings.EMPTY;
	}

	private void addLog(String tag, long sessionId, String msg) {
		logService.addLog(tag, " <-" + sessionId + "-> " + msg);
	}
}
