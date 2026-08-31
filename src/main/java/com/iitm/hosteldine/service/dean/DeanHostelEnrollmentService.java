package com.iitm.hosteldine.service.dean;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.CategoryEnum;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.HostelPaymentTypeEnum;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.dean.HostelEnrollmentDto;
import com.iitm.hosteldine.dto.dean.PropertyDto;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.dto.student.StudentBulkInfoDto;
import com.iitm.hosteldine.dto.student.StudentDetailsInfoDto;
import com.iitm.hosteldine.dto.student.StudentHostelPaymentDto;
import com.iitm.hosteldine.entity.student.StudentHostelPaymentEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.student.StudentHostelPaymentLateFeeDetailMapper;
import com.iitm.hosteldine.mapper.student.StudentHostelPaymentMapper;
import com.iitm.hosteldine.model.hostel.HostelRoomAllotmentInfoEntity;
import com.iitm.hosteldine.model.mess.StudentMessDetailsEntity;
import com.iitm.hosteldine.model.student.StudentHostelEnrollmentConfigurationEntity;
import com.iitm.hosteldine.model.student.StudentHostelPaymentLateFeeDetailDto;
import com.iitm.hosteldine.model.student.StudentHostelPaymentLateFeeDetailEntity;
import com.iitm.hosteldine.repository.collegeInfo.CourseMasterRepository;
import com.iitm.hosteldine.repository.dean.DynamicUserTabRepository;
import com.iitm.hosteldine.repository.hostel.HostelEnrollmentConfigurationRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomAllotmentRepository;
import com.iitm.hosteldine.repository.student.*;
import com.iitm.hosteldine.repository.studentDashboard.StudentMessDetailsRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.RoleEnum;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.CustomValidators;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DeanHostelEnrollmentService {

    private final Utility utility;
    private final HostelEnrollmentConfigurationRepository hostelEnrollmentConfigurationRepository;
    private final DynamicUserTabRepository dynamicUserTabRepository;
    private final ExcelUtility excelUtility;
    private final StudentHostelPaymentLateFeeDetailRepository studentHostelPaymentLateFeeDetailRepository;
    private final CustomValidators customValidators;
    private final CommonResponseUtil commonResponseUtil;
    private final StudentHostelPaymentRepository studentHostelPaymentRepository;
    private final StudentMessDetailsRepository studentMessDetailsRepository;
    private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;
    private final HostelRoomAllotmentRepository hostelRoomAllotmentRepository;

    private final StudentDetailsInfoRepository studentDetailsInfoRepository;
    private final SimsConfigDataService simsConfigDataService;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final CourseMasterRepository courseMasterRepository;
    private final MailQueueService mailQueueService;
    private final StudentHostelEnrollmentConfigurationRepository studentHostelEnrollmentConfigurationRepository;
    private final MessMasterCommonService messMasterCommonService;

    @Value("${url.dean.hostel.enrollment}")
    private String baseUrl;

    public Page<HostelEnrollmentDto> getHostelEnrollmentList(PaginationForm form, String url) {

        String validationStatus = Optional.ofNullable(utility.getFormAdditionalParam(form, "validationStatus"))
                .map(String::valueOf).orElse(null);
        String studentName = Optional.ofNullable(utility.getFormAdditionalParam(form, "studentName"))
                .map(String::valueOf).orElse(null);
        String studentId = Optional.ofNullable(utility.getFormAdditionalParam(form, "studentId"))
                .map(String::valueOf).orElse(null);
        Integer hostelId = Optional.ofNullable(utility.getFormAdditionalParam(form, "hostelName"))
                .map(it -> Long.parseLong(it.toString()))
                .map(Long::intValue)
                .orElse(null);


        int page = form.getPage() - 1;
        Pageable pageable = PageRequest.of(page, form.getSize());
        List<Object[]> subMenuList = dynamicUserTabRepository.getDeanSubMenuListById(url, SecurityCtxUtil.userRole(), SecurityCtxUtil.userName());
        List<Object[]> buttonList = subMenuList.subList(subMenuList.size() - 3, subMenuList.size());

        return hostelEnrollmentConfigurationRepository.getHostelEnrollmentDetails(validationStatus, studentName, studentId, hostelId,
                        SecurityCtxUtil.userRole(), SecurityCtxUtil.userName(), pageable)
                .map(d -> mapToHostelEnrollmentDto(d, buttonList));
    }

    public HostelEnrollmentDto mapToHostelEnrollmentDto(Object[] o, List<Object[]> buttonList) {
        Double bal = utility.parseDouble(o[0]);
        String studentId = utility.parseString(o[2]);
        String pushStatus = utility.parseString(o[12]);
        String hostelOfficeEnrollment = utility.parseString(o[11]);
        String overrideAndApprove = utility.parseString(o[13]);
        LocalDate lastPaymentDate = utility.convertToLocalDate(o[16]);
        String studentStatus = getStudentStatus(lastPaymentDate, studentId, bal);

        Map.Entry<String, List<PropertyDto>> map = toggleButton(hostelOfficeEnrollment, overrideAndApprove, bal, buttonList, studentId, studentStatus)
                .entrySet().iterator().next();

        return HostelEnrollmentDto.builder()
                .balance(bal)
                .id(utility.parseLong(o[1]))
                .studentId(studentId)
                .studentName(utility.parseString(o[3]))
                .facilityMasterName(utility.parseString(o[4]))
                .roomNo(utility.parseLong(o[5]))
                .subRoomId(utility.parseString(o[6]))
                .messHead(utility.parseString(o[7]))
                .paymentReferenceNo(utility.parseString(o[8]))
                .paymentDate(utility.dateFormatter(utility.convertToLocalDate(o[9])))
                .paymentAmount(utility.parseDouble(o[10]))
                .hostelOfficeEnrollment(map.getKey())
                .pushStatus(pushStatus)
                .overrideAndApprove(overrideAndApprove)
                .approvedBy(utility.parseString(o[14]))
                .approvalDate(utility.convertToLocalDate(o[15]))
                .lastPaymentDate(lastPaymentDate)
                .totalPaymentAmount(utility.parseDouble(o[17]))
                .viewStudentId(utility.parseString(o[18]))
                .bioMetricStatus(getBioMetricStatus(pushStatus, hostelOfficeEnrollment))
                .actionList(map.getValue())
                .build();
    }

    private String getBioMetricStatus(String pushStatus, String hostelOfficeEnrollmentStatus) {
        if (WorkflowStatus.PUSHED.getStatus().equalsIgnoreCase(pushStatus)) {
            return WorkflowStatus.ENABLED.getStatus();
        } else if (Objects.nonNull(pushStatus) && pushStatus.isEmpty() &&
                WorkflowStatus.CHECKED_IN.getStatus().equalsIgnoreCase(hostelOfficeEnrollmentStatus)) {
            return WorkflowStatus.PROCESSING.getStatus();
        } else {
            return Strings.EMPTY;
        }
    }

    private String getStudentStatus(LocalDate lastPaymentDate, String studentId, Double balanceAmount) {
        StudentHostelEnrollmentConfigurationEntity byActiveFlag = studentHostelEnrollmentConfigurationRepository.
                findFirstByActiveFlag(ModelConstants.STATUS_ACTIVE).orElse(null);
        if (Objects.nonNull(lastPaymentDate) && Objects.nonNull(studentId)) {
            if (!lastPaymentDate.isEqual(LocalDate.now()) && Objects.nonNull(byActiveFlag)) {
                String fifthChar = studentId.substring(4, 5);
                if (fifthChar.equalsIgnoreCase("S") || fifthChar.equalsIgnoreCase("D")) {
                    if (balanceAmount < byActiveFlag.getMsPhsAmount()) {
                        return CategoryEnum.MS_PHD.getValue();
                    } else {
                        return WorkflowStatus.NO_DUES.getStatus();
                    }
                } else {
                    if (balanceAmount < byActiveFlag.getMsPhsAmount()) {
                        return CategoryEnum.OTHERS.getValue().toLowerCase();
                    } else {
                        return WorkflowStatus.NO_DUES.getStatus();
                    }
                }
            } else {
                return WorkflowStatus.NO_DUES.getStatus();
            }
        }
        return WorkflowStatus.NO_DUES.getStatus();
    }

    public Map<String, List<PropertyDto>> toggleButton(String hostelOfficeEnrollment, String overrideAndApprove, Double bal,
                                                       List<Object[]> actionButtons, String studentId, String studentStatus) {
        List<PropertyDto> buttonList = new ArrayList<>();
        String status;
        if (SecurityCtxUtil.userRole().equalsIgnoreCase(RoleEnum.HOSTEL_CHECK_IN.getValue())) {
            if (hostelCheckInActions(hostelOfficeEnrollment, studentStatus, overrideAndApprove, buttonList, actionButtons, studentId)) {
                if (WorkflowStatus.NO_DUES.getStatus().equalsIgnoreCase(studentStatus)) {
                    status = hostelOfficeEnrollment;
                } else {
                    status = WorkflowStatus.DUES.getStatus();
                }
            } else {
                status = determineStatus(hostelOfficeEnrollment, studentStatus);
            }
        } else if (SecurityCtxUtil.userRole().equalsIgnoreCase(RoleEnum.CCW_DEAN.getValue())) {
            if (ccwDeanActions(hostelOfficeEnrollment, studentStatus, overrideAndApprove, buttonList, actionButtons, studentId)) {
                status = hostelOfficeEnrollment;
            } else {
                status = determineStatus(hostelOfficeEnrollment, studentStatus);
            }
        } else if (SecurityCtxUtil.userRole().equalsIgnoreCase(RoleEnum.CCW_OFFICE.getValue())) {
            if (Objects.nonNull(hostelOfficeEnrollment) && WorkflowStatus.VALIDATING.getStatus().equalsIgnoreCase(hostelOfficeEnrollment)
                    && (Objects.isNull(overrideAndApprove) || overrideAndApprove.isEmpty())) {
                if (WorkflowStatus.NO_DUES.getStatus().equalsIgnoreCase(studentStatus)) {
                    status = WorkflowStatus.VALIDATING.getStatus();
                } else {
                    status = WorkflowStatus.DUES.getStatus();
                }
            } else {
                status = determineStatus(hostelOfficeEnrollment, studentStatus);
            }
        } else {
            status = hostelOfficeEnrollment;
        }
        return Map.of(status, buttonList);
    }

    private boolean hostelCheckInActions(String hostelOfficeEnrollment, String studentStatus, String overrideAndApprove,
                                         List<PropertyDto> buttonList, List<Object[]> actionButtons, String studentId) {
        if (Objects.nonNull(hostelOfficeEnrollment) && WorkflowStatus.VALIDATING.getStatus().equalsIgnoreCase(hostelOfficeEnrollment)
                && (Objects.isNull(overrideAndApprove) || overrideAndApprove.isEmpty())) {
            if (WorkflowStatus.NO_DUES.getStatus().equalsIgnoreCase(studentStatus)) {
                buttonList.add(createActionButton(actionButtons.getFirst(), studentId, WorkflowStatus.APPROVED));
                buttonList.add(createActionButton(actionButtons.get(1), studentId, WorkflowStatus.REJECTED));
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean ccwDeanActions(String hostelOfficeEnrollment, String studentStatus, String overrideAndApprove,
                                   List<PropertyDto> buttonList, List<Object[]> actionButtons, String studentId) {
        if ((Objects.isNull(overrideAndApprove) || overrideAndApprove.isEmpty()) &&
                (!WorkflowStatus.CHECKED_IN.getStatus().equalsIgnoreCase(hostelOfficeEnrollment) &&
                        !WorkflowStatus.REJECTED.getStatus().equalsIgnoreCase(hostelOfficeEnrollment))) {
            if (CategoryEnum.MS_PHD.getValue().equalsIgnoreCase(studentStatus) || CategoryEnum.OTHERS.getValue().equalsIgnoreCase(studentStatus)) {
                buttonList.add(createActionButton(actionButtons.get(2), studentId, WorkflowStatus.APPROVED));
            } else {
                buttonList.add(createActionButton(actionButtons.getFirst(), studentId, WorkflowStatus.APPROVED));
            }
            buttonList.add(createActionButton(actionButtons.get(1), studentId, WorkflowStatus.REJECTED));
            return true;
        } else {
            return false;
        }
    }

    private String determineStatus(String hostelOfficeEnrollment, String studentStatus) {
        if (WorkflowStatus.VALIDATING.getStatus().equalsIgnoreCase(hostelOfficeEnrollment) ||
                WorkflowStatus.REJECTED.getStatus().equalsIgnoreCase(hostelOfficeEnrollment) &&
                        WorkflowStatus.REJECTED.getStatus().equalsIgnoreCase(studentStatus)) {
            return WorkflowStatus.REJECTED.getStatus();
        } else if (WorkflowStatus.CHECKED_IN.getStatus().equalsIgnoreCase(hostelOfficeEnrollment)) {
            return WorkflowStatus.APPROVED.getStatus();
        } else {
            return WorkflowStatus.DUES.getStatus();
        }
    }


    public PropertyDto createActionButton(Object[] o, String studentId, WorkflowStatus type) {
        PropertyDto actionDto = new PropertyDto();
        actionDto.setActionIcon(utility.parseString(o[5]));
        actionDto.setActionStyle(utility.parseString(o[6]));
        actionDto.setDisplayName(utility.parseString(o[8]));
        actionDto.setUrl(generateButtonUrl(type, studentId));
        return actionDto;
    }

    public String generateButtonUrl(WorkflowStatus type, String studentId) {
        String key;
        try {
            key = StudentAccommodationRequestService.encryptAccommodationRequestUrl(studentId, null, null);
        } catch (Exception e) {
            return null;
        }
        return switch (type) {
            case WorkflowStatus.APPROVED ->
                    baseUrl + commonResponseUtil.getMessage("url.dean.approve") + ModelConstants.SLASH + key;
            case WorkflowStatus.REJECTED ->
                    baseUrl + commonResponseUtil.getMessage("url.dean.reject") + ModelConstants.SLASH + key;
            default -> null;
        };
    }

    public Workbook downloadStudentBulkUploadTemplate() {
        String sheetName = "StudentUpload";
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        int columnCount = 0;
        String[] headerData = {ExcelConstants.STUDENT_HEADER_DATA[0]};
        String[] headerDataWidth = {ExcelConstants.STUDENT_HEADER_DATA_WIDTH[0]};
        Row row0 = sheet.createRow(0);
        excelUtility.createHeader(row0, columnCount, headerData, workbook);
        IntStream.range(0, headerDataWidth.length).forEach(i -> sheet.setColumnWidth(i, Integer.parseInt(headerDataWidth[i])));
        return workbook;
    }

    public Page<StudentHostelPaymentLateFeeDetailDto> getLateFeeEnrollmentList(PaginationForm form) {
        var pageRequest = PageRequest.of(form.getPage() - 1, form.getSize(), Sort.by("modifiedAt").descending());
        return studentHostelPaymentLateFeeDetailRepository.getStudentLateFeeDetails(ModelConstants.STATUS_ACTIVE, pageRequest)
                .map(o -> StudentHostelPaymentLateFeeDetailMapper.INSTANCE.toDto((StudentHostelPaymentLateFeeDetailEntity) o[0], utility.parseString(o[1])));
    }

    public void validateLateFeeEnrollment(StudentHostelPaymentLateFeeDetailDto dto, BindingResult bindingResult) {
        customValidators.validateField(dto.getDueDate(), "dueDate", "message.label.validation.date.required", bindingResult);
        customValidators.validateField(dto.getHostelId(), "hostelId", "message.validation.hostel.name.required", bindingResult);
    }

    public String saveLateFeeEnrollment(StudentHostelPaymentLateFeeDetailDto dto) {
        StudentHostelPaymentLateFeeDetailEntity entity = StudentHostelPaymentLateFeeDetailMapper.INSTANCE.toEntity(dto);
        studentHostelPaymentLateFeeDetailRepository.save(entity);
        return Constants.SAVED;
    }

    public String approveOrRejectHostelEnrollment(List<String> studentIds, String status, String reason,
                                                  String messPeriod) {
        List<StudentHostelPaymentDto> studentHostelPaymentList = new ArrayList<>();
        studentIds.forEach(studentId ->
                studentHostelPaymentRepository.findByStudentIdEqualsIgnoreCaseAndStudentConfirmStatusAndHostelOfficeEnrollmentAndOverrideApproveIsNull(
                                studentId, HostelPaymentTypeEnum.PAYMENT_CONFIRMED.getValue(), WorkflowStatus.VALIDATING.getStatus())
                        .filter(list -> !list.isEmpty())
                        .ifPresent(list -> list.forEach(
                                s -> processApproveOrRejectHostelEnrollment(s, status, reason,
                                        studentHostelPaymentList,messPeriod))));

        if (!studentHostelPaymentList.isEmpty()) {
            sendMail(studentHostelPaymentList, status, reason);
        }
        return Constants.SAVED;
    }

    private void processApproveOrRejectHostelEnrollment(StudentHostelPaymentEntity studentHostelPayment, String status,
                                                        String reason, List<StudentHostelPaymentDto> studentHostelPaymentList,
                                                        String messPeriod) {
        if (status.equalsIgnoreCase(WorkflowStatus.APPROVED.getStatus())) {
            approveStudentHostelPayment(studentHostelPayment,messPeriod);
            if (SecurityCtxUtil.userRole().equalsIgnoreCase(RoleEnum.CCW_DEAN.getValue())) {
                studentHostelPayment.setOverrideApprove(WorkflowStatus.OVERRIDE_AND_APPROVED.getStatus());
            }
        } else {
            rejectStudentHostelPayment(studentHostelPayment, reason);
        }
        studentHostelPayment.setApprovedOrRejectedBy(SecurityCtxUtil.userId());
        Optional.of(studentHostelPaymentRepository.save(studentHostelPayment))
                .map(StudentHostelPaymentMapper.INSTANCE::toDto)
                .ifPresent(studentHostelPaymentList::add);
    }

    private void approveStudentHostelPayment(StudentHostelPaymentEntity studentHostelPayment,String messPeriod) {
        studentHostelPayment.setHostelOfficeEnrollment(WorkflowStatus.CHECKED_IN.getStatus());
        studentHostelPayment.setApprovalDate(LocalDate.now());

        MessMasterControllerDto dto;
        Long mmcId = 0L;
        LocalDate messPeriodDate;
        if(Objects.nonNull(messPeriod) && !messPeriod.isEmpty()){
            if(ModelConstants.CURRENT.equalsIgnoreCase(messPeriod)){
                 dto = messMasterCommonService.getCurrentMessPeriod();
                 mmcId = dto.getId();
                 messPeriodDate = LocalDate.now().plusDays(1);
            }
            else{
                 dto = messMasterCommonService.getNextMessPeriod();
                 mmcId = dto.getId();
                 messPeriodDate = dto.getDiningFromDate();
            }
        } else {
            messPeriodDate = null;
        }

        studentMessDetailsRepository.getStudentMessDetailsByStudentIdAndFromDateAndToDate(studentHostelPayment.getStudentId(),
                        ModelConstants.STATUS_ACTIVE,mmcId)
                .ifPresent(e->updateStudentMessDetails(e,messPeriodDate));

        allStudentsDetailsViewRepository.findBystudentId(studentHostelPayment.getStudentId())
                .filter(e -> Objects.nonNull(e.getRoomAllotmentId()))
                .ifPresent(e -> updateHostelRoomAllotmentInfo(e.getRoomAllotmentId()));
    }

    private void rejectStudentHostelPayment(StudentHostelPaymentEntity studentHostelPayment, String reason) {
        studentHostelPayment.setOverrideApprove(WorkflowStatus.REJECTED.getStatus());
        studentHostelPayment.setHostelOfficeEnrollment(WorkflowStatus.REJECTED.getStatus());
        studentHostelPayment.setHostelOfficeRejectionReason(reason);
    }

    private void updateStudentMessDetails(StudentMessDetailsEntity studentMessDetailsEntity,LocalDate messPeriod) {
        studentMessDetailsEntity.setPushRemoveStatus(WorkflowStatus.TO_BE_PUSHED.getStatus());
        studentMessDetailsEntity.setPushDate(messPeriod);
        studentMessDetailsRepository.save(studentMessDetailsEntity);
    }

    private void updateHostelRoomAllotmentInfo(Long allotmentId) {
        HostelRoomAllotmentInfoEntity hostelRoomAllotmentInfoEntity = hostelRoomAllotmentRepository.findByRoomAllotmentId(allotmentId).orElse(null);
        if (Objects.nonNull(hostelRoomAllotmentInfoEntity)) {
            hostelRoomAllotmentInfoEntity.setStatus(WorkflowStatus.CHECKED_IN.getStatus());
            hostelRoomAllotmentRepository.save(hostelRoomAllotmentInfoEntity);
        }
    }

    public StudentBulkInfoDto approveStudentBulkUploads(MultipartFile file) throws IOException {
        List<StudentBulkInfoDto> students = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();
        StudentBulkInfoDto studentBulkInfoDto = new StudentBulkInfoDto();
        studentBulkInfoDto.setErrorList(new ArrayList<>());
        String idRepeatCheck = "";
        ArrayList prevIdList = new ArrayList();
        List<String> studentIds = new ArrayList<>();
        int rowNumber = 0;
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(rowNumber);
            List<String> headerList = Collections.singletonList(ExcelConstants.STUDENT_HEADER_DATA[0]);
            List<String> actualHeaders = new ArrayList<>();
            for (Cell cell : headerRow) {
                actualHeaders.add(formatter.formatCellValue(cell));
            }

            if (!headerList.containsAll(actualHeaders)) {
                studentBulkInfoDto.getErrorList().add("Invalid Excel Template");
                return studentBulkInfoDto;
            }
            rowNumber++;

            // Check for empty file
            if (sheet.getPhysicalNumberOfRows() <= 1) {
                studentBulkInfoDto.getErrorList().add("Empty file. Please upload with data");
                return studentBulkInfoDto;
            }
            int rowCount = ExcelUtility.countNonEmptyRows(sheet);

            for (Row row : sheet) {
                if (rowCount > rowNumber) {
                    if (row.getRowNum() == 0) { // Skip header row
                        continue;
                    }
                    StudentBulkInfoDto student = new StudentBulkInfoDto();
                    StringBuilder errorDetails = new StringBuilder();
                    boolean error = false;
                    int column = 0;
                    // Student Id
                    if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
                        boolean isValidStudentId = formatter.formatCellValue(row.getCell(column)).matches(ModelConstants.ALPHA_NUMERIC_REGEX);
                        if (isValidStudentId) {
                            student.setStudentId(formatter.formatCellValue(row.getCell(column)).trim().toUpperCase());
                            if (prevIdList.toString().contains(student.getStudentId())) {
                                cellError("Previous ID " + student.getStudentId() + " should not be Student ID ", column, rowNumber, student);
                                error = true;
                                errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                            }
                        } else {
                            cellError("Accept alphanumeric characters with a minimum of 6 and a maximum of 10 characters.", column, rowNumber, student);
                            error = true;
                            errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                        }

                        if (studentDetailsInfoRepository.existsByActiveFlagAndStudentIdAndSettlementFlag(ModelConstants.STATUS_ACTIVE, student.getStudentId(), ModelConstants.YES)) {
                            cellError("Student ID " + student.getStudentId() + ":Settlement Completed", column, rowNumber, student);
                            error = true;
                            errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                        }
                        if (!studentDetailsInfoRepository.checkStudentIdExistInPreviousId(student.getStudentId()).isEmpty()) {
                            cellError("Student ID " + student.getStudentId() + " already assigned to another roll no", column, rowNumber, student);
                            error = true;
                            errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                        }

                    } else {
                        cellError("Student ID should not be empty ", column, rowNumber, student);
                        error = true;
                        errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                    }

                    //Previous Id
                    if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
                        String previousStudId = formatter.formatCellValue(row.getCell(column)).trim().toUpperCase();
                        String[] previousArray = previousStudId.split(",");
                        // Initialize and populate the prevId array
                        String[] prevId = new String[previousArray.length];
                        for (int i = 0; i < previousArray.length; i++) {
                            prevId[i] = previousArray[i].trim();
                        }
                        // Set the previous IDs without brackets
                        student.setPreviousId(String.join(",", prevId));


                        // Check if the previous ID is assigned to another roll number
                        int checkStudentExist = studentDetailsInfoService.checkPreviousIdExist(student.getStudentId(), previousStudId);
                        if (checkStudentExist > 0) {
                            cellError("Previous ID " + previousStudId + " Already Assigned to Another Student ID", column, rowNumber, student);
                            error = true;
                            errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                        }

                        // Check if the previous student ID exists
                        String prevIdCheck = studentDetailsInfoService.checkDuplicateNoWithSeparate(student.getPreviousId());
                        if (!Objects.equals(prevIdCheck, "")) {
                            cellError("Previous Student ID " + prevIdCheck + " does not Exist: ", column, rowNumber, student);
                            error = true;
                            errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                        }

                        // Check if any IDs are repeated
                        String[] tempIds = previousStudId.split(",");
                        for (String id : tempIds) {
                            if (idRepeatCheck.contains(id)) {
                                cellError("Previous Student ID " + id + " Should not be Repeated ", column, rowNumber, student);
                                error = true;
                                errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                            }
                        }

                        // Update idRepeatCheck with the current ID for future checks
                        prevIdList.add(previousStudId);
                        idRepeatCheck = String.join(",", prevIdList);
                    }


                    // Add errors for the row to the studentBulkInfoDto if any
                    if (error) {
                        studentBulkInfoDto.getErrorList().add(ModelConstants.ROW + rowNumber + ": " + errorDetails);
                    } else {
                        student.setEmailId(student.getStudentId().toUpperCase() + simsConfigDataService.getSimConfigValue("STUDENT_MAIL_ID"));
                        students.add(student);
                        studentIds.add(student.getStudentId());
                    }
                    rowNumber++;
                }
            }
        }
        if (!studentIds.isEmpty()) studentBulkInfoDto.setStudentIds(studentIds);
        return studentBulkInfoDto;
    }

    private void cellError(String msg, int column, int rowCount, StudentBulkInfoDto studentBulkInfoDto) {
        String[] columns = ModelConstants.EXCEL_COLUMNS;
        if (column == 0 && rowCount == 0) studentBulkInfoDto.setExcelErrorMsg(msg);
        else studentBulkInfoDto.setExcelErrorMsg(" Cell " + columns[column] + (rowCount + 1) + " : " + msg);
        System.out.println(studentBulkInfoDto.getExcelErrorMsg());
        studentBulkInfoDto.setError("error");
    }

    private void sendMail(List<StudentHostelPaymentDto> studentHostelPaymentList, String status, String reason) {
        studentHostelPaymentList.forEach(student ->
                Optional.ofNullable(studentDetailsInfoService.getStudentInfoDetails(student.getStudentId()))
                        .map(StudentDetailsInfoDto::getEmailId)
                        .ifPresent(emailId -> triggerMail(student, emailId, status, reason)));
    }

    private void triggerMail(StudentHostelPaymentDto studentHostelPaymentDto, String emailId, String status, String reason) {
        String subject = commonResponseUtil.getMessage("message.mail.subject").replace("#%status%#", status);
        String msg;
        String reasonElement;
        if (WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(status)) {
            msg = commonResponseUtil.getMessage("message.mail.approved.msg");
            reasonElement = Strings.EMPTY;
        } else {
            msg = commonResponseUtil.getMessage("message.mail.rejected.msg");
            reasonElement = commonResponseUtil.getMessage("message.mail.reject.reason")
                    .replace("#%reason%#", reason);
        }

        String messageTemplate = simsConfigDataService.getSimConfigValue(SimsConfigDataService.DEAN_HOSTEL_ENROLLMENT_MAIL_TEMPLATE)
                .replace("#%message%#", msg)
                .replace("#%reasonElement%#", reasonElement)
                .replace("#%row%#", createTableRow(studentHostelPaymentDto));

        try {
            mailQueueService.saveMailQueue(subject, commonResponseUtil.getMessage("message.mail.greetings.for"),
                    messageTemplate, emailId, commonResponseUtil.getMessage("message.mail.module"),
                    null, null, null, null, ModelConstants.REGARDS, ModelConstants.CCW_OFFICE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String createTableRow(StudentHostelPaymentDto studentHostelPaymentDto) {
        return commonResponseUtil.getMessage("message.mail.table.row")
                .replace("#%refNo%#", Optional.ofNullable(studentHostelPaymentDto.getPaymentReferenceNo()).orElse(Strings.EMPTY))
                .replace("#%paymentDate%#", utility.dateFormatter(studentHostelPaymentDto.getPaymentDate()))
                .replace("#%paymentAmount%#", utility.formatCommaSeperatedCurrency(Double.valueOf(studentHostelPaymentDto.getPaymentAmount())));
    }
}