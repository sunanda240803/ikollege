package com.iitm.hosteldine.service.dean;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.CategoryEnum;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.dean.DeanApprovalDto;
import com.iitm.hosteldine.dto.dean.OtherCandidateRequestDto;
import com.iitm.hosteldine.dto.dean.PropertyDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.dean.DashboardTabMasterEntity;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateAppointmentRequestRepository;
import com.iitm.hosteldine.repository.dean.DashboardTabMasterRepository;
import com.iitm.hosteldine.repository.dean.DynamicUserTabRepository;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.util.*;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeanOtherCandidateRequestService {

    private final Utility utility;
    private final DynamicUserTabRepository dynamicUserTabRepository;
    private final CandidateAppointmentRequestRepository candidateAppointmentRequestRepository;
    private final DashboardTabMasterRepository dashboardTabMasterRepository;
    private final MessageSource messageSource;
    private final DeanDashboardService deanDashboardService;
    private final PropertyAccessorUtil propertyAccessorUtil;
    private final CommonResponseUtil commonResponseUtil;
    private final HostelMasterService hostelMasterService;
    private final StudentAccommodationRequestService studentAccommodationRequestService;

    public List<OtherCandidateRequestDto> getOtherCandidateRequestList(PaginationForm form, String tabUrl, boolean needSize,
                                                                           List<PropertyDto> buttonList, String interviews, HttpServletRequest request) {

        String validationStatus = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.VALIDATION_STATUS.getValue()))
                .map(String::valueOf).orElse(null);
        String category = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.CATEGORY.getValue()))
                .map(String::valueOf).orElse(null);
        String approvalFromDate = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.APPROVAL_FROM_DATE.getValue()))
                .map(utility::convertToLocalDate)
                .map(String::valueOf)
                .orElse("null");
        String approvalToDate = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.APPROVAL_TO_DATE.getValue()))
                .map(utility::convertToLocalDate)
                .map(String::valueOf)
                .orElse("null");
        String submittedFromDate = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.SUBMITTED_FROM_DATE.getValue()))
                .map(utility::convertToLocalDate)
                .map(String::valueOf)
                .orElse("null");
        String submittedToDate = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.SUBMITTED_TO_DATE.getValue()))
                .map(utility::convertToLocalDate)
                .map(String::valueOf)
                .orElse("null");
        String appointmentFromDate = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.APPOINTMENT_FROM_DATE.getValue()))
                .map(utility::convertToLocalDate)
                .map(String::valueOf)
                .orElse("null");
        String appointmentToDate = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.APPOINTMENT_TO_DATE.getValue()))
                .map(utility::convertToLocalDate)
                .map(String::valueOf)
                .orElse("null");
        String stayFromDate = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.STAY_FROM_DATE.getValue()))
                .map(utility::convertToLocalDate)
                .map(String::valueOf)
                .orElse("null");
        String stayToDate = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.STAY_TO_DATE.getValue()))
                .map(utility::convertToLocalDate)
                .map(String::valueOf)
                .orElse("null");
        String candidateName = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.CANDIDATE_NAME.getValue()))
                .map(String::valueOf).orElse(null);
        String candidateEmail = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.CANDIDATE_EMAIL.getValue()))
                .map(String::valueOf).orElse(null);
        Integer hostelId = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.HOSTEL_NAME.getValue()))
                .map(it -> Long.parseLong(it.toString()))
                .map(Long::intValue)
                .orElse(null);
        String stayType = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.STAY_TYPE.getValue()))
                .map(String::valueOf).orElse(null);

        String currentDayStayFlag = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.CURRENT_STAY_FLAG.getValue()))
                .map(String::valueOf).orElse(null);

        DashboardTabMasterEntity tabEntity = dashboardTabMasterRepository.findByTabTypeAndTabUrl(
                messageSource.getMessage("message.label.tab", null, Locale.getDefault()), tabUrl);
        Integer tabNo = Integer.valueOf(tabEntity.getProperty());

        int page = form.getPage() - 1;
        int size = needSize ? form.getSize() : Integer.MAX_VALUE;

        Pageable pageable = PageRequest.of(page, size);

        List<HostelMasterDto> maleHostelList;
        List<HostelMasterDto> femaleHostelList;
        if(RoleEnum.CCW_OFFICE.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole())){
            List<HostelMasterDto> hostelList = hostelMasterService.getHostelList();
            maleHostelList = hostelList.stream()
                    .filter(hostel -> hostel.getHostelGenderType() != null &&
                            (hostel.getHostelGenderType().equalsIgnoreCase(Constants.MALE) ||
                                    hostel.getHostelGenderType().equalsIgnoreCase(Constants.MALE_FULL_FORM)))
                    .toList();
            femaleHostelList = hostelList.stream()
                    .filter(hostel -> hostel.getHostelGenderType() != null &&
                            (hostel.getHostelGenderType().equalsIgnoreCase(Constants.FEMALE) ||
                                    hostel.getHostelGenderType().equalsIgnoreCase(Constants.FEMALE_FULL_FORM)))
                    .toList();
        }
        else {
            maleHostelList = Collections.emptyList();
            femaleHostelList = Collections.emptyList();
        }

        return Arrays.stream(candidateAppointmentRequestRepository.getOtherCandidateRequests(
                        validationStatus, category, appointmentFromDate, appointmentToDate,
                        stayFromDate, stayToDate, candidateName, Strings.EMPTY, null, null,
                        stayType, tabNo, SecurityCtxUtil.userRole(), submittedFromDate, submittedToDate, approvalFromDate, approvalToDate,
                        hostelId, SecurityCtxUtil.userName(), candidateEmail, currentDayStayFlag))
                .map(o -> mapToDto((Object[]) o, buttonList, tabUrl, maleHostelList, femaleHostelList, request)).toList();
    }

    public OtherCandidateRequestDto mapToDto(Object[] o, List<PropertyDto> buttonList, String url,
                                             List<HostelMasterDto> maleHostelList, List<HostelMasterDto> femaleHostelList, HttpServletRequest request) {
        long candidateId = utility.parseLong(o[7]);
        long workflowId = utility.parseLong(o[34]);
        long requestId = utility.parseLong(o[5]);
        long stayId = utility.parseLong(o[28]);
        String workStatus = String.valueOf(o[3]);
        String appStatus = String.valueOf(o[2]);
        String modifiedAt = String.valueOf(o[35]);
        String requestType = stayId > 0 ? ModelConstants.STAY_EXT : ModelConstants.ACCOM_REQ;

        return OtherCandidateRequestDto.builder()
                .slNo(requestId + (stayId > 0 ? ModelConstants.SPACE + ModelConstants.HYPHEN + ModelConstants.SPACE + stayId : ModelConstants.EMPTY_STRING))
                .appStatus(appStatus)
                .workStatus(workStatus)
                .approvalStatus(Objects.nonNull(o[4]) ? String.valueOf(o[4]) : appStatus)
                .requestType(requestType)
                .dining(utility.parseBoolean(o[6]))
                .candidateId(candidateId)
                .createdAt(utility.dateFormatter(utility.convertToLocalDate(o[8])))
                .candidateName(o[9] + " " + o[10])
                .gender(String.valueOf(o[11]).equalsIgnoreCase(Constants.MALE) ? "Male" : "Female")
                .dob(utility.dateFormatter(utility.convertToLocalDate(o[12])))
                .email(String.valueOf(o[13]))
                .appointmentFrom(utility.dateFormatter(utility.convertToLocalDate(o[14])))
                .appointmentTo(utility.dateFormatter(utility.convertToLocalDate(o[15])))
                .stayFrom(utility.dateFormatter(utility.convertToLocalDate(o[16])))
                .stayTo(utility.dateFormatter(utility.convertToLocalDate(o[17])))
                .grossPay(utility.parseDouble(o[18]))
                .validatingAuthority(String.valueOf(o[19]))
                .validatingAuthorityEmail(String.valueOf(o[20]))
                .approvalNotes(String.valueOf(o[21]).equalsIgnoreCase("null") ? Constants.NA : String.valueOf(o[21]))
                .rejectionReason(String.valueOf(o[22]).equalsIgnoreCase("null") ? Constants.NA : String.valueOf(o[22]))
                .category(String.valueOf(o[23]))
                .approvalDate(utility.dateFormatter(utility.convertToLocalDate(o[24])))
                .postSelect(String.valueOf(o[25]))
                .empId(String.valueOf(o[26]))
                .designation(String.valueOf(o[27]))
                .stayId(stayId)
                .hostelName(studentAccommodationRequestService.getStringValue(o[29]))
                .allottedHostelName(studentAccommodationRequestService.getStringValue(o[29]))
                .roomNo(studentAccommodationRequestService.getStringValue(o[30]))
                .subRoom(studentAccommodationRequestService.getStringValue(o[31]))
                .seat(studentAccommodationRequestService.getStringValue(o[31]))
                .purpose(String.valueOf(o[32]))
                .applicationNo(String.valueOf(o[33]))
                .workflowId(workflowId)
                .modifiedAt(modifiedAt)
                .checkInStatus(utility.parseBoolean(o[36]))
                .messOption(String.valueOf(o[37]).equalsIgnoreCase("mess") ? "Mess only" :
                        String.valueOf(o[37]).equalsIgnoreCase("accommodation") ? "Accommodation Only" :
                                "Accommodation and Mess Both")
                .city(String.valueOf(o[38]))
                .state(String.valueOf(o[39]))
                .phoneNumber(String.valueOf(o[40]))
                .categoryOthers(String.valueOf(o[41]))
                .occupancy(String.valueOf(o[42]).equalsIgnoreCase("null") ? Constants.NA : String.valueOf(o[42]))
                .programOrDept(String.valueOf(o[43]).equalsIgnoreCase("null") ? Constants.NA : String.valueOf(o[43]))
                .address1(String.valueOf(o[44]))
                .address2(String.valueOf(o[45]))
                .pin(utility.parseLong(o[46]))
                .postOthers(String.valueOf(o[47]))
                .description(String.valueOf(o[48]).equalsIgnoreCase("null") ? Constants.NA : String.valueOf(o[48]))
                .actionList(generateButtonByRoles(buttonList, candidateId, requestId, stayId, workStatus,
                        appStatus, workflowId, url, modifiedAt, request))
                .hostelListGenderBased(getHostelListByGender(maleHostelList, femaleHostelList, String.valueOf(o[11])))
                .build();
    }

    private List<HostelMasterDto> getHostelListByGender(List<HostelMasterDto> maleHostelList, List<HostelMasterDto> femaleHostelList,String gender){
        if(RoleEnum.CCW_OFFICE.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole())){
            if(Constants.MALE.equalsIgnoreCase(gender) || Constants.MALE_FULL_FORM.equalsIgnoreCase(gender)){
                return maleHostelList;
            }
            else{
                return femaleHostelList;
            }
        }
        else return Collections.emptyList();
    }

    private List<PropertyDto> generateButtonByRoles(List<PropertyDto> buttonList, Long candidateId, Long requestId, Long stayId,
                                                    String workflowStatus, String appStatus, Long workflowId, String baseUrl,
                                                    String modifiedAt, HttpServletRequest request) {
        if(RoleEnum.DEAN.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole())){
            return buttonsForDeanRole(buttonList, candidateId, requestId, stayId, workflowStatus, appStatus, workflowId, baseUrl, modifiedAt, request);
        }
        else if(RoleEnum.CCW_OFFICE.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole())){
            return buttonsForCcwOffice(buttonList, candidateId, requestId, stayId, workflowStatus, appStatus, workflowId, baseUrl, modifiedAt, request);
        }
        else if(RoleEnum.WARDEN.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole()) ||
                RoleEnum.HOSTEL_CHECK_IN.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole())){
            return buttonsForHostelAndWarden(buttonList, candidateId, requestId, stayId, workflowStatus, appStatus, workflowId, baseUrl, modifiedAt, request);
        }
        else return Collections.emptyList();
    }

    public List<PropertyDto> buttonsForDeanRole(List<PropertyDto> buttonList, Long candidateId, Long requestId, Long stayId,
                                                String workflowStatus, String appStatus, Long workflowId, String baseUrl,
                                                String modifiedAt, HttpServletRequest request) {
        if (Objects.nonNull(workflowStatus) && !workflowStatus.isEmpty()) {
            if (WorkflowStatus.DEFAULT.getStatus().equalsIgnoreCase(workflowStatus) &&
                    !WorkflowStatus.REJECTED.getStatus().equalsIgnoreCase(appStatus)) {
                return processButton(buttonList, candidateId, requestId, stayId, workflowId, baseUrl, "v",
                        List.of("View", "Delete", "Re-send Mail", "CheckBox"), modifiedAt, request);
            } else if (WorkflowStatus.PENDING.getStatus().equalsIgnoreCase(workflowStatus)) {
                return processButton(buttonList, candidateId, requestId, stayId, workflowId, baseUrl, "p",
                        List.of("View", "Delete", "CheckBox"), modifiedAt, request);
            } else if (WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(workflowStatus)) {
                return processButton(buttonList, candidateId, requestId, stayId, workflowId, baseUrl, "c",
                        List.of("View", "PDF"), modifiedAt, request);
            } else if (WorkflowStatus.REJECTED.getStatus().equalsIgnoreCase(workflowStatus) || WorkflowStatus.REJECTED.getStatus()
                    .equalsIgnoreCase(appStatus)) {
                return processButton(buttonList, candidateId, requestId, stayId, workflowId, baseUrl, "r",
                        List.of("View"), modifiedAt, request);
            } else if (WorkflowStatus.CANCELLED.getStatus().equalsIgnoreCase(workflowStatus)) {
                return processButton(buttonList, candidateId, requestId, stayId, workflowId, baseUrl, "n",
                        List.of("View"), modifiedAt, request);
            } else if (WorkflowStatus.DELETED.getStatus().equalsIgnoreCase(workflowStatus)) {
                return processButton(buttonList, candidateId, requestId, stayId, workflowId, baseUrl, "d",
                        List.of("View"), modifiedAt, request);
            } else {
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    public List<PropertyDto> buttonsForCcwOffice(List<PropertyDto> buttonList, Long candidateId, Long requestId, Long stayId,
                                                 String workflowStatus, String appStatus, Long workflowId, String baseUrl,
                                                 String modifiedAt, HttpServletRequest request) {
        List<String> requiredButtons = new ArrayList<>(List.of("View", "Change", "Allocate", "PDF"));

        List<PropertyDto> propertyDtoList = new ArrayList<>();
        String url;
        for (PropertyDto o : buttonList){
            if (requiredButtons.contains(o.getDisplayName())) {
                if(WorkflowStatus.APPROVED.getStatus().equalsIgnoreCase(appStatus) &&
                        o.getDisplayName().equalsIgnoreCase(WorkflowStatus.ALLOCATE.getStatus())){
                    o.setActionStyle(o.getActionStyle().replace("d-none", Strings.EMPTY));
                    url = baseUrl + commonResponseUtil.getMessage("url.allocate");
                    o.setUrl(generateUrlForAllocation(candidateId, requestId, stayId, workflowId, url, Constants.HYPHEN, modifiedAt));
                    try{
                        propertyDtoList.add(o.clone());
                    }
                    catch (Exception e){
                        log.error(e.getMessage());
                    }
                    continue;
                }

                if(WorkflowStatus.ALLOTTED.getStatus().equalsIgnoreCase(appStatus) &&
                        o.getDisplayName().equalsIgnoreCase(WorkflowStatus.CHANGE.getStatus())){
                    o.setActionStyle(o.getActionStyle().replace("d-none", Strings.EMPTY));
                }

                if (o.getDisplayName().equalsIgnoreCase(WorkflowStatus.ALLOCATE.getStatus())
                        || o.getDisplayName().equalsIgnoreCase(WorkflowStatus.CHANGE.getStatus())) {
                    url = baseUrl + commonResponseUtil.getMessage("url.allocate");
                    o.setUrl(generateUrlForAllocation(candidateId, requestId, stayId, workflowId, url, Constants.HYPHEN, modifiedAt));
                    try{
                        propertyDtoList.add(o.clone());
                    }
                    catch (Exception e){
                        log.error(e.getMessage());
                    }
                    continue;
                } else if (WorkflowStatus.VIEW.getStatus().equals(o.getDisplayName())) {
                    if (!baseUrl.startsWith("/")) {
                        url = request.getContextPath() + "/" + baseUrl + "/view";
                    } else {
                        url = baseUrl + "/view";
                    }
                } else if (WorkflowStatus.PDF.getStatus().equals(o.getDisplayName())) {
                    url = baseUrl + commonResponseUtil.getMessage("url.pdf.download");
                } else{
                    url = baseUrl;
                }
                o.setUrl(generateUrl(candidateId, requestId, stayId, workflowId, url, Constants.HYPHEN, modifiedAt));
                try{
                    propertyDtoList.add(o.clone());
                }
                catch (Exception e){
                    log.error(e.getMessage());
                }
            }
        }
        return propertyDtoList;
    }

    public List<PropertyDto> buttonsForHostelAndWarden(List<PropertyDto> buttonList, Long candidateId, Long requestId, Long stayId,
                                                 String workflowStatus, String appStatus, Long workflowId, String baseUrl,
                                                 String modifiedAt, HttpServletRequest request) {
        List<String> requiredButtons = new ArrayList<>(List.of("View"));

        if(WorkflowStatus.ALLOTTED.getStatus().equalsIgnoreCase(appStatus)){
            requiredButtons.add("Check In");
        }
        else if(WorkflowStatus.CHECKED_IN.getStatus().equalsIgnoreCase(appStatus)){
            requiredButtons.add("Check Out");
        }

        List<PropertyDto> propertyDtoList = new ArrayList<>();
        String url;
        for (PropertyDto o : buttonList){
            if (requiredButtons.contains(o.getDisplayName())) {
                if(o.getDisplayName().equalsIgnoreCase(WorkflowStatus.CHECK_IN.getStatus()) ||
                        o.getDisplayName().equalsIgnoreCase(WorkflowStatus.CHECK_OUT.getStatus())){
                    url = baseUrl + commonResponseUtil.getMessage("url.checkIn.checkOut") +
                            ModelConstants.SLASH + o.getDisplayName();
                    url = generateUrlForCheckProcess(candidateId, requestId, stayId, workflowId, url, Constants.HYPHEN, modifiedAt);
                    o.setUrl(url);
                    try{
                        propertyDtoList.add(o.clone());
                    }
                    catch (Exception e){
                        log.error(e.getMessage());
                    }
                    continue;
                } else if (WorkflowStatus.VIEW.getStatus().equals(o.getDisplayName())) {
                    if (!baseUrl.startsWith("/")) {
                        url = request.getContextPath() + "/" + baseUrl + "/view";
                    } else {
                        url = baseUrl + "/view";
                    }
                } else{
                    url = baseUrl;
                }
                url = generateUrl(candidateId, requestId, stayId, workflowId, url, Constants.HYPHEN, modifiedAt);
                o.setUrl(url);
                try{
                    propertyDtoList.add(o.clone());
                }
                catch (Exception e){
                    log.error(e.getMessage());
                }
            }
        }
        return propertyDtoList;
    }

    public List<PropertyDto> processButton(List<PropertyDto> buttonList, Long candidateId, Long requestId, Long stayId, Long workflowId,
                                           String baseUrl, String status, List<String> requiredButtons, String modifiedAt, HttpServletRequest request) {

        if(Objects.isNull(buttonList) || buttonList.isEmpty()){
            return Collections.emptyList();
        }

        List<PropertyDto> propertyDtoList = new ArrayList<>();
        String url;
        for (PropertyDto o : buttonList){
            if (requiredButtons.contains(o.getDisplayName())) {
                if (o.getDisplayName().equalsIgnoreCase("PDF")) {
                    url = baseUrl + commonResponseUtil.getMessage("url.pdf.download");
                } else if (WorkflowStatus.VIEW.getStatus().equals(o.getDisplayName())) {
                    if (!baseUrl.startsWith("/")) {
                        url = request.getContextPath() + "/" + baseUrl + "/view";
                    } else {
                        url = baseUrl + "/view";
                    }
                } else if (WorkflowStatus.RE_SEND_MAIL.getStatus().equals(o.getDisplayName())) {
                    url = baseUrl + commonResponseUtil.getMessage("url.resend.mail");
                } else if (WorkflowStatus.CHECKBOX.getStatus().equals(o.getDisplayName())){
                    url = baseUrl + commonResponseUtil.getMessage("url.bulk.approve.reject");
                } else{
                    url = baseUrl;
                }
                o.setUrl(generateUrl(candidateId, requestId, stayId, workflowId, url, status, modifiedAt));
                try{
                    propertyDtoList.add(o.clone());
                }
                catch (Exception e){
                    log.error(e.getMessage());
                }
            }
        }
        return propertyDtoList;
    }

    public String generateUrlForAllocation(Long candidateId, Long requestId, Long stayId, Long workflowId, String url, String status, String modifiedAt){
        String ids = candidateId + Constants.BACKTICK + requestId + Constants.BACKTICK + workflowId +
                    Constants.BACKTICK + status + Constants.BACKTICK + modifiedAt + Constants.BACKTICK + stayId;
        try {
            String key = MCrypt.getInstance().encryptToText(ids);
            return url + ModelConstants.SLASH + key;
        } catch (Exception e) {
            return Constants.NA;
        }
    }

    public String generateUrlForCheckProcess(Long candidateId, Long requestId, Long stayId, Long workflowId, String url, String status, String modifiedAt){
        String ids = candidateId + Constants.BACKTICK + requestId + Constants.BACKTICK + workflowId +
                Constants.BACKTICK + status + Constants.BACKTICK + modifiedAt + Constants.BACKTICK + stayId;
        try {
            String key = MCrypt.getInstance().encryptToText(ids);
            return url + ModelConstants.SLASH + key;
        } catch (Exception e) {
            return Constants.NA;
        }
    }

    public String generateUrl(Long candidateId, Long requestId, Long stayId, Long workflowId, String url, String status, String modifiedAt) {
        String ids;
        if (stayId > 0) {
            ids = candidateId + Constants.BACKTICK + requestId + Constants.BACKTICK + workflowId +
                    Constants.BACKTICK + status + Constants.BACKTICK + modifiedAt + Constants.BACKTICK + stayId;
        }
        else{
           ids = candidateId + Constants.BACKTICK + requestId + Constants.BACKTICK + workflowId +
                    Constants.BACKTICK + status + Constants.BACKTICK + modifiedAt;
        }

        try {
            String key = MCrypt.getInstance().encryptToText(ids);
            if (stayId > 0) {
                return url + commonResponseUtil.getMessage("url.stay.extension") + ModelConstants.SLASH + key;
            } else {
                return url + ModelConstants.SLASH + key;
            }
        } catch (Exception e) {
            return Constants.NA;
        }
    }

    public Workbook generateOtherCandidateListExcelReport(List<OtherCandidateRequestDto> otherCandidateRequestList, String baseUrl) throws Exception {
        XSSFWorkbook workbook;
        int colCount = 0;
        ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

        DeanApprovalDto columnList = deanDashboardService.getDeanMenuListById(baseUrl.replace("/", ""));
        List<PropertyDto> headerList = columnList.getActionList();

        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(messageSource.getMessage("message.other.candidate.list", null, Locale.getDefault()));

            // Create styles using ExcelUtility
            XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
            headerStyle.setWrapText(true);
            XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
            dataStyle.setWrapText(true);

            // Create second header row
            XSSFRow rowheadFirst = sheet.createRow(1);
            excelUtility.createCell(rowheadFirst, 0, messageSource.getMessage("message.other.candidate.list", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 12));

            // Create third header row for report date
            XSSFRow rowheadSecond = sheet.createRow(2);
            SimpleDateFormat sdf = new SimpleDateFormat(commonResponseUtil.getMessage("session.date.format"));
            String reportDate = commonResponseUtil.getMessage("message.label.report.date.colon") + sdf.format(new Date());
            excelUtility.createCell(rowheadSecond, 0, reportDate, headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 12));

            // Create column headers
            XSSFRow rowhead = sheet.createRow(3);

            // Add headers to the sheet
            for (PropertyDto p : headerList) {
                excelUtility.createCell(rowhead, colCount, p.getDisplayName(), headerStyle);
                sheet.setColumnWidth(colCount, 4000); // Set column width
                colCount++;
            }

            // Populate data rows
            int rowCount = 3;
            if (CollectionUtils.isNotEmpty(otherCandidateRequestList)) {
                for (OtherCandidateRequestDto dto : otherCandidateRequestList) {
                    XSSFRow row = sheet.createRow(++rowCount);

                    for (int colIndex = 0; colIndex < headerList.size(); colIndex++) {
                        PropertyDto header = headerList.get(colIndex);
                        var value = propertyAccessorUtil.getPropertyValue(dto, header.getProperty());
                        excelUtility.createCell(row, colIndex, value, dataStyle);
                    }
                }
            }

            // Auto-size columns with a maximum width limit
            for (int i = 0; i < headerList.size(); i++) {
                sheet.autoSizeColumn(i);
                // Cap the column width to 10000 (about 100 characters) to prevent extremely wide columns
                if (sheet.getColumnWidth(i) > 10000) {
                    sheet.setColumnWidth(i, 10000);
                }
            }


        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Error generating Other Candidate List report", exception);
        }
        return workbook;
    }
}