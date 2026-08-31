package com.iitm.hosteldine.service.dean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import com.iitm.hosteldine.service.warden.WardenInfoService;
import com.iitm.hosteldine.util.RoleEnum;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.SimsConfigDataDto;
import com.iitm.hosteldine.dto.dean.DeanHdcComplaintDto;
import com.iitm.hosteldine.dto.dean.DeanHdcComplaintInvolvedStudentsDto;
import com.iitm.hosteldine.dto.dean.PropertyDto;
import com.iitm.hosteldine.dto.mailQueue.MailTemplateDto;
import com.iitm.hosteldine.entity.mailQueue.MailQueueDetailsEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.SimsConfigDataMapper;
import com.iitm.hosteldine.mapper.mailQueue.MailTemplateMapper;
import com.iitm.hosteldine.mapper.warden.DeanHdcComplaintMapper;
import com.iitm.hosteldine.model.dean.DeanHdcComplaintEntity;
import com.iitm.hosteldine.model.hostel.HostelMasterEntity;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.repository.SimsConfigDataRepository;
import com.iitm.hosteldine.repository.dean.DeanHdcComplaintRepository;
import com.iitm.hosteldine.repository.dean.DynamicUserTabRepository;
import com.iitm.hosteldine.repository.hostel.HostelMasterRepository;
import com.iitm.hosteldine.repository.mailQueue.MailQueueDetailsRepository;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.ValidationCommon;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeanHdcComplaintService {

    private final DeanHdcComplaintRepository deanHdcComplaintRepository;
    private final DynamicUserTabRepository dynamicUserTabRepository;
    private final Utility utility;

    private final MessageSource messageSource;
    private final FileService fileService;
    private final MailTemplateRepository mailTemplateRepository;
    private final CommonResponseUtil commonResponseUtil;
    private final SimsConfigDataRepository simsConfigDataRepository;
    private final MailQueueDetailsRepository mailQueueDetailsRepository;
    private final HostelMasterRepository hostelMasterRepository;
    private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;
    private final WardenInfoService wardenInfoService;


    public List<DeanHdcComplaintDto> getHdcComplaintList(PaginationForm form, String url, boolean isExcelReport) {

        String studentName = (form.getAdditionalParam().get("studentName") != null && !form.getAdditionalParam().get("studentName").equals(""))
                ? form.getAdditionalParam().get("studentName").toString().toUpperCase()
                : null;
        String studentId = (form.getAdditionalParam().get("studentId") != null && !form.getAdditionalParam().get("studentId").equals(""))
                ? form.getAdditionalParam().get("studentId").toString().toUpperCase()
                : null;
        Integer year = (form.getAdditionalParam().get("year") != null && !form.getAdditionalParam().get("year").equals("")) ? Integer.valueOf(form.getAdditionalParam().get("year").toString()) : 0;

        String complaintFromDate = (form.getAdditionalParam().get("complaintFromDate") != null && !form.getAdditionalParam().get("complaintFromDate").equals(""))
                ? form.getAdditionalParam().get("complaintFromDate").toString().toUpperCase()
                : null;
        String complaintToDate = (form.getAdditionalParam().get("complaintToDate") != null && !form.getAdditionalParam().get("complaintToDate").equals(""))
                ? form.getAdditionalParam().get("complaintToDate").toString().toUpperCase()
                : null;

        Long hostelId = ValidationCommon.toLongOrZero(form.getAdditionalParam().get("hostelId"));

        String userRole = Objects.requireNonNull(SecurityCtxUtil.userRole());
        String userName = Objects.requireNonNull(SecurityCtxUtil.userName());
        int wardenId = 0;
        Object[] result;

        if (isExcelReport) {
            // Fetch all results for Excel report
            result = deanHdcComplaintRepository.getHdcComplaintListFromFunction(studentId, studentName, complaintFromDate, complaintToDate, userName, userRole, wardenId, hostelId, year);
            return Arrays.stream(result).map(objects -> {
                return setHdcComplaintDto((Object[]) objects, new ArrayList<PropertyDto>());
            }).toList();
        } else {
            // Fetch paginated results
            int page = form.getPage() - 1;
            Pageable pageable = PageRequest.of(page, form.getSize());

            result = deanHdcComplaintRepository.getHdcComplaintListFromFunction(studentId, studentName, complaintFromDate, complaintToDate, userName, userRole, wardenId, hostelId, year);

            List<Object[]> subMenuList = dynamicUserTabRepository.getDeanSubMenuListById(url, SecurityCtxUtil.userRole(), SecurityCtxUtil.userName());

            return Arrays.stream(result).map(data -> {
                Object[] objects = (Object[]) data;
                List<PropertyDto> actionList = new ArrayList<PropertyDto>();
                for (Object[] action : subMenuList) {
                    PropertyDto actionDto = new PropertyDto();
                    String displayName = action[8] != null ? action[8].toString().trim() : "";
                    if (action[2].toString().equals(Constants.COL_LINK) || action[2].toString().equals(Constants.COL_ACTION)) {
                        if (WorkflowStatus.VIEW.getStatus().equalsIgnoreCase(displayName)){
                            actionDto.setActionIcon(action[5] != null ? action[5].toString() : null);
                            actionDto.setActionStyle(action[6] != null ? action[6].toString() : null);
                            actionDto.setDisplayName(action[8] != null ? action[8].toString() : null);
                            String studentIdVal = objects[3] != null ? objects[3].toString() : null;
                            Long hdcId = objects[2] != null ? Long.valueOf(objects[2].toString()) : null;
                            try {
                                actionDto.setUrl(url + "/view?data=" + encryptAccommodationRequestUrl(studentIdVal, hdcId));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            actionList.add(actionDto);
                        } else if (WorkflowStatus.DELETE.getStatus().equalsIgnoreCase(displayName)) {
                            actionDto.setActionIcon(action[5] != null ? action[5].toString() : null);
                            actionDto.setActionStyle(action[6] != null ? action[6].toString() : null);
                            actionDto.setDisplayName(action[8] != null ? action[8].toString() : null);
                            String studentIdVal = objects[3] != null ? objects[3].toString() : null;
                            Long hdcId = objects[2] != null ? Long.valueOf(objects[2].toString()) : null;
                            try {
                                actionDto.setUrl(url + "/delete?data=" + encryptAccommodationRequestUrl(studentIdVal, hdcId));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            actionList.add(actionDto);
                        }
                    }
                }
                return setHdcComplaintDto(objects, actionList);
            }).toList();
        }
    }

    private DeanHdcComplaintDto setHdcComplaintDto(Object[] objects, List<PropertyDto> actionList) {
        DeanHdcComplaintDto dto = new DeanHdcComplaintDto();
        dto.setStudentId(objects[3] != null ? objects[3].toString() : null);
        dto.setStudentName(objects[4] != null ? objects[4].toString() : null);
        dto.setHostelName(objects[5] != null ? objects[5].toString() : null);
        dto.setRoomNo(objects[6] != null ? (Integer) objects[6] : 0);
        dto.setViolation(objects[7] != null ? objects[7].toString() : null);
        dto.setWardenPlea(objects[8] != null ? objects[8].toString() : null);
        dto.setWardenDecision(objects[9] != null ? objects[9].toString() : null);
        dto.setWardenRemarks(getRemarks(objects[10]));
        dto.setStatus(objects[12] != null ? objects[12].toString() : null);
        dto.setDateOfComplaint(objects[13] != null ? (utility.convertToLocalDateTime(objects[13]).format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT))) : null);
        dto.setCategory(objects[15] != null ? objects[15].toString() : null);
        dto.setDayScholarInvolve(objects[16] != null ? objects[16].toString() : null);
        dto.setActionList(actionList);
        return dto;
    }

    public DeanHdcComplaintDto getHdcComplaintDetails(String studentId,Long hdcId) {
        DeanHdcComplaintDto dto = new DeanHdcComplaintDto();
        List<Object[]> objects = deanHdcComplaintRepository.getHdcComplaintDetails(studentId,hdcId);

        if (objects.isEmpty()) {
            return null; // or throw an exception if preferred
        }

        Object[] data = objects.get(0);
        dto.setStudentId(studentId);
        dto.setStudentName(data[0] != null ? data[0].toString() : null);
        dto.setHostelName(data[1] != null ? data[1].toString() : null);
        dto.setRoomNo(data[2] != null ? (Integer) data[2] : 0);
        dto.setViolation(data[7] != null ? data[7].toString() : null);
        dto.setDateOfComplaint(data[8] != null ? (utility.convertToLocalDate(data[8]).format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT))) : null);
        dto.setWardenPlea(data[9] != null ? data[9].toString() : null);
        dto.setWardenDecision(data[10] != null ? data[10].toString() : null);
        dto.setWardenRemarks(data[11] != null ? data[11].toString() : null);
        dto.setStatus(data[12] != null ? data[12].toString() : null);
        dto.setDayScholarInvolve(data[13] != null ? data[13].toString() : null);
        dto.setPenaltyAmount(data[14] != null ? data[14].toString() : null);
        dto.setPenaltyDueDate(data[15] != null ? utility.convertToLocalDate(data[15]) : null);
        dto.setPaidAmount(data[16] != null ? Double.parseDouble(data[16].toString()) : null);
        dto.setPaymentDesc(data[17] != null ? data[17].toString() : null);
        dto.setPaymentRefNo(data[18] != null ? data[18].toString() : null);
        dto.setId(data[19] != null ? Long.parseLong(data[19].toString()) : 0L);
        dto.setId(data[19] != null ? Long.parseLong(data[19].toString()) : 0L);
        dto.setFileName(data[20] != null ? data[20].toString() : null);
        List<DeanHdcComplaintInvolvedStudentsDto> involvedStudentsDtoList = new ArrayList<>();
        for (Object[] involvedStudents : objects) {
            DeanHdcComplaintInvolvedStudentsDto involvedStudentsDto = new DeanHdcComplaintInvolvedStudentsDto();
            involvedStudentsDto.setInvolvedStudentId(involvedStudents[3] != null ? involvedStudents[3].toString() : null);
            involvedStudentsDto.setInvolvedStudentName(involvedStudents[4] != null ? involvedStudents[4].toString() : null);
            involvedStudentsDto.setInvolvedStudentHostelName(involvedStudents[5] != null ? involvedStudents[5].toString() : null);
            involvedStudentsDto.setInvolvedStudentRoomNo(involvedStudents[6] != null ? involvedStudents[6].toString() : null);
            involvedStudentsDtoList.add(involvedStudentsDto);
        }
        dto.setInvolvedStudentsDto(involvedStudentsDtoList);
        return dto;

    }

    public Workbook getHdcComplaintReport(List<DeanHdcComplaintDto> hdcComplaintDtoList) throws Exception {
        XSSFWorkbook workbook = null;
        int colCount = 0;
        ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(messageSource
                    .getMessage("message.hdc.complaint.list", null, Locale.getDefault()));

            // Create styles using ExcelUtility
            XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
            XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);

            // Create second header row
            XSSFRow rowheadFirst = sheet.createRow(1);
            excelUtility.createCell(rowheadFirst, 0, messageSource
                    .getMessage("message.hdc.complaint.list", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 12));

            // Create third header row for report date
            XSSFRow rowheadSecond = sheet.createRow(2);
            DateFormat dateFormat = new SimpleDateFormat(Constants.BACKEND_DATETIME_FORMAT_2);
            Date date = new Date();
            excelUtility.createCell(rowheadSecond, 0, messageSource
                    .getMessage("message.label.report.date", null, Locale.getDefault()) + dateFormat.format(date), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 12));

            // Create column headers
            XSSFRow rowhead = sheet.createRow(3);
            String[] headers = {
                    messageSource.getMessage("message.label.hdc.date.of.complaint", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.hdc.category", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.studentID", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.studentName", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.hostelName", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.room.number", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.hdc.violation", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.hdc.plea", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.hdc.decision", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.hdc.dayscholar.involved", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.hdc.remarks", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.status", null, Locale.getDefault())
            };

            // Add headers to the sheet
            for (String header : headers) {
                excelUtility.createCell(rowhead, colCount, header, headerStyle);
                sheet.setColumnWidth(colCount, 4000); // Set column width
                colCount++;
            }

            // Populate data rows
            int rowcount = 3;
            if (CollectionUtils.isNotEmpty(hdcComplaintDtoList)) {
                for (DeanHdcComplaintDto hdcComplaintDto : hdcComplaintDtoList) {
                    rowcount++;
                    XSSFRow row = sheet.createRow(rowcount);
                    excelUtility.createCell(row, 0, hdcComplaintDto.getDateOfComplaint(), dataStyle);
                    excelUtility.createCell(row, 1, hdcComplaintDto.getCategory(), dataStyle);
                    excelUtility.createCell(row, 2, hdcComplaintDto.getStudentId(), dataStyle);
                    excelUtility.createCell(row, 3, hdcComplaintDto.getStudentName(), dataStyle);
                    excelUtility.createCell(row, 4, hdcComplaintDto.getHostelName(), dataStyle);
                    excelUtility.createCell(row, 5, hdcComplaintDto.getRoomNo(), dataStyle);
                    excelUtility.createCell(row, 6, hdcComplaintDto.getViolation(), dataStyle);
                    excelUtility.createCell(row, 7, hdcComplaintDto.getWardenPlea(), dataStyle);
                    excelUtility.createCell(row, 8, hdcComplaintDto.getWardenDecision(), dataStyle);
                    excelUtility.createCell(row, 9, hdcComplaintDto.getDayScholarInvolve(), dataStyle);
                    excelUtility.createCell(row, 10, hdcComplaintDto.getWardenRemarks(), dataStyle);
                    excelUtility.createCell(row, 11, hdcComplaintDto.getStatus(), dataStyle);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Error generating HDC Complaint List report", exception);
        }
        return workbook;
    }

    private static String encryptAccommodationRequestUrl(String studentId,Long hdcId)
            throws Exception {
        String encryptKey = null;
        encryptKey = hdcId + Constants.BACKTICK + studentId + Constants.BACKTICK + Utility.getCurrentTimeStamp();
        return MCrypt.getInstance().encryptToText(encryptKey);
    }


    private String getRemarks(Object object) {
        if (object != null) {
            if (Constants.NIL.equalsIgnoreCase(object.toString()) || Constants.NILL.equalsIgnoreCase(object.toString())) {
                return Constants.NA;
            } else {
                return object.toString();
            }
        }
        return null;
    }

    public String updateHdcComplaint(@Valid DeanHdcComplaintDto deanHdcComplaintDto) {
        Optional<DeanHdcComplaintEntity> entity = deanHdcComplaintRepository.findByIdAndActiveFlag(deanHdcComplaintDto.getId(), ModelConstants.YES);
        if (entity.isPresent()) {
            DeanHdcComplaintEntity hdcComplaintEntity = entity.get();
            DeanHdcComplaintMapper.INSTANCE.onUpdateEntity(hdcComplaintEntity, deanHdcComplaintDto);
            hdcComplaintEntity.onUpdate();
            deanHdcComplaintRepository.saveAndFlush(hdcComplaintEntity);
            return Constants.SAVED;
        } else {
            return null;
        }
    }

    public void validateUpdateHdcComplaintForm(@Valid DeanHdcComplaintDto deanHdcComplaintDto, BindingResult bindingResult) {

        String errorMessage = "";
        if (deanHdcComplaintDto.getStatus() != null && !deanHdcComplaintDto.getStatus().isEmpty() && !deanHdcComplaintDto.getStatus().equals(Constants.PENDING)) {
            if (deanHdcComplaintDto.getStatus() == null || deanHdcComplaintDto.getStatus().isEmpty()) {
                errorMessage = messageSource.getMessage("message.validation.penalty.status", null, Locale.getDefault());
                bindingResult.rejectValue("status", "error.status", errorMessage);
            }
            if (deanHdcComplaintDto.getPaymentRefNo() == null || deanHdcComplaintDto.getPaymentRefNo().isEmpty()) {
                errorMessage = messageSource.getMessage("message.validation.reference.no", null, Locale.getDefault());
                bindingResult.rejectValue("paymentRefNo", "error.paymentRefNo", errorMessage);
            }
            if (deanHdcComplaintDto.getPaymentDesc() == null || deanHdcComplaintDto.getPaymentDesc().isEmpty()) {
                errorMessage = messageSource.getMessage("message.validation.payment.description", null, Locale.getDefault());
                bindingResult.rejectValue("paymentDesc", "error.paymentDesc", errorMessage);
            }
            if (deanHdcComplaintDto.getPaidAmount() == null || deanHdcComplaintDto.getPaidAmount().equals(0.0)) {
                errorMessage = messageSource.getMessage("message.validation.paid.amount", null, Locale.getDefault());
                bindingResult.rejectValue("paidAmount", "error.paidAmount", errorMessage);
            }
            if (deanHdcComplaintDto.getStatus() != null && !deanHdcComplaintDto.getStatus().isEmpty() && deanHdcComplaintDto.getStatus().equals(Constants.PAYMENT_STATUS_PAID)) {
                if (deanHdcComplaintDto.getPenaltyAmount() != null && deanHdcComplaintDto.getPaidAmount() != null &&
                        deanHdcComplaintDto.getPaidAmount() < Double.parseDouble(deanHdcComplaintDto.getPenaltyAmount())) {
                    errorMessage = messageSource.getMessage("message.validation.status.invalid.paid.amount", null, Locale.getDefault());
                    bindingResult.rejectValue("paidAmount", "error.paidAmount", errorMessage);
                }
            }
            if (deanHdcComplaintDto.getStatus() != null && !deanHdcComplaintDto.getStatus().isEmpty() && deanHdcComplaintDto.getStatus().equals(Constants.PAYMENT_STATUS_PAID)) {
                if (deanHdcComplaintDto.getPenaltyAmount() != null && deanHdcComplaintDto.getPaidAmount() != null &&
                        deanHdcComplaintDto.getPaidAmount() != Double.parseDouble(deanHdcComplaintDto.getPenaltyAmount())) {
                    errorMessage = messageSource.getMessage("message.validation.amount.invalid.status", null, Locale.getDefault());
                    bindingResult.rejectValue("paidAmount", "error.paidAmount", errorMessage);
                }
            }
            if (deanHdcComplaintDto.getPenaltyAmount() != null && deanHdcComplaintDto.getPaidAmount() != null &&
                    deanHdcComplaintDto.getPaidAmount() > Double.parseDouble(deanHdcComplaintDto.getPenaltyAmount())) {
                errorMessage = messageSource.getMessage("message.validation.penalty.invalid.paid.amount", null, Locale.getDefault());
                bindingResult.rejectValue("paidAmount", "error.paidAmount", errorMessage);
            }
        }
        if (!errorMessage.isEmpty()) {
            bindingResult.rejectValue(null, errorMessage);
        }


    }

    @Transactional
    public String saveHdcComplaint(@Valid DeanHdcComplaintDto deanHdcComplaintDto) {
        DeanHdcComplaintEntity deanHdcComplaintEntity;
        //File save
        if (deanHdcComplaintDto.getFile() != null && !deanHdcComplaintDto.getFile().isEmpty()) {
            try {
                byte[] fileBytes = deanHdcComplaintDto.getFile().getBytes();
                String fileName = deanHdcComplaintDto.getStudentId() + "-" + Utility.getCurrentTimeStamp() + ".pdf";
                boolean fileSaved = fileService.encodeFile(Constants.HDC_COMPLAINT_FILE_PATH, fileBytes, fileName);
                if (fileSaved) {
                    deanHdcComplaintDto.setFileName(fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //updating penalty status
        if (deanHdcComplaintDto.getPenaltyAmount().equals("0")) {
            deanHdcComplaintDto.setPenaltyStatus(Constants.PAYMENT_STATUS_PAID);
        } else {
            deanHdcComplaintDto.setPenaltyStatus(Constants.PENDING);
        }
        deanHdcComplaintEntity = DeanHdcComplaintMapper.INSTANCE.onSaveEntity(deanHdcComplaintDto);
//        WardenInfoDto wardenInfoDto = wardenInfoService.getWardenDetailsByLDAPUsername(SecurityCtxUtil.userName());
        deanHdcComplaintEntity.setWardenId(0L);
        deanHdcComplaintEntity.setHostelId(deanHdcComplaintDto.getHostelId()!=null ? Long.valueOf(deanHdcComplaintDto.getHostelId()): 0);
        deanHdcComplaintEntity.setInvolvedStudentId(deanHdcComplaintDto.getStudenIds());
        deanHdcComplaintRepository.save(deanHdcComplaintEntity);
        if (deanHdcComplaintEntity.getId() > 0) {
            boolean status;
            if (!deanHdcComplaintDto.getCategory().equals(WorkflowStatus.OTHERS.getStatus())) {
                status = hdcComplaintMail(deanHdcComplaintDto);
            } else {
                status = true;
            }
            if (status) {
                return Constants.SAVED;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public List<DeanHdcComplaintDto> getPreviousComplaints(String id) throws Exception {
        String complaints = deanHdcComplaintRepository.getComplaintList(id, id);
        List<DeanHdcComplaintDto> complaintList = new ArrayList<>();
        if (complaints != null && !complaints.isEmpty()) {
            String[] records = complaints.split(","); // Split each complaint
            for (String record : records) {
                String[] parts = record.split("~"); // Split hdcId, studentId, status
                if (parts.length > 0) {
                    DeanHdcComplaintDto dto = new DeanHdcComplaintDto();
                    dto.setId(Long.parseLong(parts[0]));
                    String studentId = parts[1];
                    dto.setStudentId("/view?data=" + encryptAccommodationRequestUrl(studentId,dto.getId()));
                    complaintList.add(dto);
                }
            }
        }
        return complaintList;
    }

    @Transactional
    public boolean hdcComplaintMail(DeanHdcComplaintDto deanHdcComplaintDto) {
        boolean success = false;
        //Getting hotsel Name
        long hostelId = Long.parseLong(deanHdcComplaintDto.getHostelId());
        Optional<HostelMasterEntity> hostelEntity = hostelMasterRepository.findByIdAndActiveFlag(hostelId, Constants.ACTIVE_FLAG);
        String officeMail = "";
        if (hostelEntity.isPresent()) {
            deanHdcComplaintDto.setHostelName(hostelEntity.get().getHostelName());
            officeMail = hostelEntity.get().getHostelOfficeEmail();
        } else {
            deanHdcComplaintDto.setHostelName(ModelConstants.EMPTY_STRING);
        }
        //Prepare mail content
        String template = mailTemplateRepository
                .findByActiveFlagAndMailType(ModelConstants.STATUS_ACTIVE, ModelConstants.HDC_COMPLAINT_MAIL).map(MailTemplateMapper.INSTANCE::toDto)
                .map(MailTemplateDto::getMailTemplate).orElse(new MailTemplateDto().getMailTemplate());
        //updating dynamic values
        template = template.replace("#%subject%#", commonResponseUtil.getMessage("message.label.mail.hdc.complaint"));
        template = template.replace("#%date%#", DateUtility.formatDateInd(new java.util.Date()));
        //template = template.replace("#%subSubject%#", "The following particulars have been applied for stay extension in one of our campus hostels.");
        template = template.replace("#%heading1%#", ModelConstants.STUDENT_DETAILS);
        template = template.replace("#%heading2%#", ModelConstants.COMPLAINT_DETAILS);
        template = template.replace("#%heading3%#", ModelConstants.PENALTY_DETAILS);
        template = template.replace("#%studentName%#", deanHdcComplaintDto.getStudentName());
        template = template.replace("#%studentId%#", deanHdcComplaintDto.getStudentId());
        template = template.replace("#%hostelName%#", deanHdcComplaintDto.getHostelName());
        template = template.replace("#%violation%#", deanHdcComplaintDto.getViolation());
        template = template.replace("#%wardenDesicion%#", deanHdcComplaintDto.getWardenDecision());
        template = template.replace("#%penaltyAmount%#", deanHdcComplaintDto.getPenaltyAmount());
        template = template.replace("#%dueDate%#", deanHdcComplaintDto.getPenaltyDueDate() != null ? utility.dateFormatter(deanHdcComplaintDto.getPenaltyDueDate()) : "");
        //Saving in mail queue table
        try {
            // Mail to office
            sendMail(template, commonResponseUtil.getMessage("message.label.hdc.complaint.subject"), officeMail);
            success = true;
            // Mail to student
            String studentMail = getStudentMail(deanHdcComplaintDto.getStudentId());
            if (studentMail != null) {
                sendMail(template, commonResponseUtil.getMessage("message.label.hdc.complaint.student"), studentMail);
            }
            // Mail to parent
            if (Boolean.TRUE.equals(deanHdcComplaintDto.getMailToParent())) {
                String parentMail = getParentMail(deanHdcComplaintDto.getStudentId());
                if (parentMail != null) {
                    sendMail(template, commonResponseUtil.getMessage("message.label.hdc.complaint.subject.parent"), parentMail);
                }
            }
        } catch (Exception e) {
            // Log the failure but don't throw — returning false indicates failure
            log.error("Error sending HDC complaint mail (StudentId: {}, Hostel Id: {}) - {}", deanHdcComplaintDto.getStudentId(), deanHdcComplaintDto.getHostelId(), e.getMessage(), e);
            success = false;
        }
        return success;
    }

    private void sendMail(String template, String messageBody, String mailTo) {
        MailQueueDetailsEntity mailQueueDetails = new MailQueueDetailsEntity();
        mailQueueDetails.setMailSubject(commonResponseUtil.getMessage("message.label.mail.hdc.complaint"));
        mailQueueDetails.setSubmittedModule(ModelConstants.HDC_COMPLAINT_MAIL + "- send mail");
        mailQueueDetails.setMailPriority(1);
        mailQueueDetails.setMailStatus(1);
        mailQueueDetails.setRetryCount(0);
        mailQueueDetails.setMailFrom(simsConfigDataRepository.findByConfigKeyIgnoreCaseAndActiveFlag(SimsConfigDataService.MAIL_FROM,
                        ModelConstants.STATUS_ACTIVE).map(SimsConfigDataMapper.INSTANCE::fromSimsConfigDataEntity)
                .map(SimsConfigDataDto::getConfigValue).orElse(new SimsConfigDataDto().getConfigValue()));
        mailQueueDetails.onCreate();
        String newTemplate = template.replace("#%subSubject%#", messageBody);
        mailQueueDetails.setMailTo(mailTo);
        mailQueueDetails.setMailContent(newTemplate);
        mailQueueDetailsRepository.save(mailQueueDetails);
    }

	public String getParentMail(String id) {
		String email = null;
		List<Object[]> studentDetails = deanHdcComplaintRepository.getParentMailByStudentId(id);

		if (studentDetails == null || studentDetails.isEmpty()) {
			return email;
		}

		String fatherMail = null;
		String motherMail = null;
		String guardianMail = null;

		for (Object[] row : studentDetails) {
			String mail = (String) row[0];
			String relationType = (String) row[1];

			if (mail == null || mail.trim().isEmpty() || relationType == null) {
				continue;
			}

			switch (relationType.trim()) {
			case Constants.FATHER:
				fatherMail = mail;
				break;
			case Constants.MOTHER:
				motherMail = mail;
				break;
			case Constants.GUARDIAN:
				guardianMail = mail;
				break;
			default:
				// ignore others like Brother, Sister, etc.
				break;
			}
		}
		if (fatherMail != null) {
			return fatherMail;
		}
		if (motherMail != null) {
			return motherMail;
		}
		if (guardianMail != null) {
			return guardianMail;
		}

		return email;
	}

    public String getStudentMail(String id) {
        String email = null;
        Optional<AllStudentsDetailsViewEntity> studentEmail = allStudentsDetailsViewRepository.findBystudentId(id);
        if (studentEmail.isPresent()) {
            email = studentEmail.get().getEmailId();
        }
        return email;
    }

    public void validateHdcComplaintForm(@Valid DeanHdcComplaintDto deanHdcComplaintDto, BindingResult bindingResult) {
        String errorMessage = "";
        if (deanHdcComplaintDto.getStudentId() == null || deanHdcComplaintDto.getStudentId().isEmpty()) {
            errorMessage = messageSource.getMessage("message.validation.student.id.required", null, Locale.getDefault());
            bindingResult.rejectValue("studentId", "error.status", errorMessage);
        }
        if (deanHdcComplaintDto.getStudentName() == null || deanHdcComplaintDto.getStudentName().isEmpty()) {
            errorMessage = messageSource.getMessage("message.validation.student.name.required", null, Locale.getDefault());
            bindingResult.rejectValue("studentName", "error.status", errorMessage);
        }
        if (deanHdcComplaintDto.getCategory() != null && !deanHdcComplaintDto.getCategory().isEmpty() && !deanHdcComplaintDto.getCategory().equals(WorkflowStatus.OTHERS.getStatus())) {
            if (deanHdcComplaintDto.getHostelId() == null || deanHdcComplaintDto.getHostelId().isEmpty()) {
                errorMessage = messageSource.getMessage("message.validation.hostel.required", null, Locale.getDefault());
                bindingResult.rejectValue("hostelId", "error.status", errorMessage);
            }
            if (deanHdcComplaintDto.getRoomNo() == null || deanHdcComplaintDto.getRoomNo() == 0) {
                errorMessage = messageSource.getMessage("message.validation.room.no.required", null, Locale.getDefault());
                bindingResult.rejectValue("roomNo", "error.status", errorMessage);
            }
        }
        if (deanHdcComplaintDto.getViolation() == null || deanHdcComplaintDto.getViolation().isEmpty()) {
            errorMessage = messageSource.getMessage("message.validation.violation.required", null, Locale.getDefault());
            bindingResult.rejectValue("violation", "error.status", errorMessage);
        }
        if (deanHdcComplaintDto.getWardenPlea() == null || deanHdcComplaintDto.getWardenPlea().isEmpty()) {
            errorMessage = messageSource.getMessage("message.validation.warden.plea.required", null, Locale.getDefault());
            bindingResult.rejectValue("wardenPlea", "error.status", errorMessage);
        }
        if (deanHdcComplaintDto.getWardenDecision() == null || deanHdcComplaintDto.getWardenDecision().isEmpty()) {
            errorMessage = messageSource.getMessage("message.validation.warden.decision.required", null, Locale.getDefault());
            bindingResult.rejectValue("wardenDecision", "error.status", errorMessage);
        }
//        if (deanHdcComplaintDto.getWardenRemarks() == null || deanHdcComplaintDto.getWardenRemarks().isEmpty()) {
//            errorMessage = messageSource.getMessage("message.validation.warden.remarks.required", null, Locale.getDefault());
//            bindingResult.rejectValue("wardenRemarks", "error.status", errorMessage);
//        }
        if (deanHdcComplaintDto.getPenaltyAmount() == null || deanHdcComplaintDto.getPenaltyAmount().isEmpty()) {
            errorMessage = messageSource.getMessage("message.validation.penalty.amount.required", null, Locale.getDefault());
            bindingResult.rejectValue("paidAmount", "error.status", errorMessage);
        } else {
            if (Double.parseDouble(deanHdcComplaintDto.getPenaltyAmount()) > 0) {
                if (deanHdcComplaintDto.getPenaltyDueDate() == null) {
                    errorMessage = messageSource.getMessage("message.validation.date.of.complaint.required", null, Locale.getDefault());
                    bindingResult.rejectValue("dueDate", "error.status", errorMessage);
                }
            }
        }
        if (deanHdcComplaintDto.getFile() != null && !deanHdcComplaintDto.getFile().isEmpty()) {
            MultipartFile file = deanHdcComplaintDto.getFile();
            String originalFilename = file.getOriginalFilename();
            long fileSizeInBytes = file.getSize();
            long maxFileSizeInBytes = 2 * 1024 * 1024; // 2 MB
            // List of allowed extensions
            List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "bmp", "png", "jpeg", "doc", "docx", "xls", "xlsx", "txt");
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
            }
            if (!allowedExtensions.contains(extension)) {
                //throw new IllegalArgumentException("Invalid file type. Allowed types: " + String.join(", ", allowedExtensions));
                errorMessage = messageSource.getMessage("message.label.student.invalid.file.format", null, Locale.getDefault());
                bindingResult.rejectValue("file", "error.status", errorMessage);
            } else if (fileSizeInBytes > maxFileSizeInBytes) {
                //throw new IllegalArgumentException("File size exceeds the 2MB limit.");
                errorMessage = messageSource.getMessage("message.validation.file.size", null, Locale.getDefault());
                bindingResult.rejectValue("file", "error.status", errorMessage);
            }
        }
        if (!errorMessage.isEmpty()) {
            bindingResult.rejectValue(null, errorMessage);
        }
    }

    @Transactional
    public boolean deleteHDCComplaintDetails(String encryptedKey) throws Exception {
        String[] split = MCrypt.getInstance().decryptToString(encryptedKey).split(Constants.BACKTICK);
        Long hdcId = Long.parseLong(split[0]);
        String studentId = split[1];
        try {
            DeanHdcComplaintEntity deanHdcComplaintEntity = deanHdcComplaintRepository.findByIdAndStudentIdAndActiveFlag(hdcId, studentId, ModelConstants.STATUS_ACTIVE).orElse(null);
            if (Objects.nonNull(deanHdcComplaintEntity)) {
                deanHdcComplaintEntity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
                deanHdcComplaintEntity.setModifiedBy(SecurityCtxUtil.userId());
                deanHdcComplaintEntity.setModifiedAt(DateUtility.getNowTimeInstant());
                deanHdcComplaintRepository.save(deanHdcComplaintEntity);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
