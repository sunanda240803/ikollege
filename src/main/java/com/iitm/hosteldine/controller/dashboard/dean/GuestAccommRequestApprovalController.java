package com.iitm.hosteldine.controller.dashboard.dean;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.GuestHostelAllotmentDTO;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.hostel.HostelRoomInfoDto;
import com.iitm.hosteldine.dto.student.OverrideApproveGuestAccomDTO;
import com.iitm.hosteldine.dto.student.StudentGuestAccomDTO;
import com.iitm.hosteldine.dto.student.StudentRoomDTO;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.form.student.StudentRoomRequestForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.hostel.HostelRoomInfoService;
import com.iitm.hosteldine.service.studentDashboard.GuestAccommodationRequestService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.RoleEnum;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("${url.guest.accommodation.student}")
public class GuestAccommRequestApprovalController {
    private final HostelMasterService hostelMasterService;
    private final CommonResponseUtil commonResponseUtil;
    private final GuestAccommodationRequestService guestAccommodationRequestService;
    private final SimsConfigDataService simsConfigDataService;
    private final MessageSource messageSource;
    private final HostelRoomInfoService hostelRoomInfoService;

    @Value("${url.guest.accommodation.student}")
    private String guestAccommodationStudentRequest;


    @GetMapping()
    public String getStudentRoomRequest(PaginationForm form, StudentRoomRequestForm studentForm, ModelMap map, HttpServletRequest request) {
        List<HostelMasterDto> hostelList = hostelMasterService.getHostelList();
        map.addAttribute("hostelList", hostelList);
        map.addAttribute("hasFormData", false);
        map.addAttribute("showAllotmentOption", RoleEnum.GUEST_ALLOTMENT.getValue().equals(SecurityCtxUtil.userRole()));
        map.addAttribute("type","");
        List<StudentRoomDTO> studentRoomDTOS = new ArrayList<>();
        int page = form.getPage() - 1;
        Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("a.created_at").descending());
        commonResponseUtil.updateCommonModelAttributes(map, request ,new PageImpl<>(studentRoomDTOS, pageable, studentRoomDTOS.size()) , form);
        excelDownloadButton(map);
        return HTMLPage.GUEST_ACCOMMODATION_REQUEST_STUDENT_ROOM; // Thymeleaf template name
    }

    @PostMapping()
    public String filterGuestRequests(@Valid @ModelAttribute StudentRoomRequestForm studentForm, @ModelAttribute PaginationForm pageForm, ModelMap map, HttpServletRequest request) {
//        if(StringUtils.equalsIgnoreCase(type, Constants.TYPE_GUEST_ROOM) && StringUtils.equalsIgnoreCase(SecurityCtxUtil.userName(),"narayana.ohm")){
//            showAllotmentOption = true;
//        }
//        showAllotmentOption = StringUtils.equalsIgnoreCase(studentForm.getOhmlogin(),"yes"); // will be removed once ohm setup is done
//        showAllotmentOption = showAllotmentOption && StringUtils.equalsIgnoreCase(type,Constants.TYPE_GUEST_ROOM); // will be removed once ohm setup is done

        List<HostelMasterDto> hostelList = hostelMasterService.getHostelList();
        map.addAttribute("hostelList", hostelList);

        map.addAttribute("hasFormData", true);
        map.addAttribute("studentForm", studentForm);
        List<StudentRoomDTO> studentRoomDTOS = guestAccommodationRequestService.getGuestRequestList(pageForm, studentForm,hostelList);

        map.addAttribute("type","");
        map.addAttribute("showAllotmentOption", RoleEnum.GUEST_ALLOTMENT.getValue().equals(SecurityCtxUtil.userRole()));
        map.addAttribute("GUEST_MAILVIEW_MAX_TODATE",simsConfigDataService.getSimConfigValue(SimsConfigDataService.GUEST_MAILVIEW_MAX_TODATE));
        map.addAttribute("studentRoomRequestList", studentRoomDTOS);
        commonResponseUtil.updateCommonModelAttributes2(map, request , studentRoomDTOS , pageForm);
        excelDownloadButton(map);
        return HTMLPage.GUEST_ACCOMMODATION_REQUEST_STUDENT_ROOM; // Thymeleaf template name
    }


    @GetMapping( "${url.guest.student.view}" + "${id}")
    public String getRoomDetails(@PathVariable String id, PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
        StudentRoomDTO studentRoomDTO = new StudentRoomDTO();
        studentRoomDTO.setStudentDetailString(id);
        StudentGuestAccomDTO studentGuestAccomDTO = new StudentGuestAccomDTO();
//        try {
            String paramString = new MCrypt().decryptToString(id);
            if(StringUtils.isNotEmpty(paramString)) {
                String[] split = paramString.split(Constants.BACKTICK);
                studentRoomDTO.setStudentId(split[0]);
                studentRoomDTO.setRequestId(Integer.parseInt(split[1]));
                studentRoomDTO.setParentRequestId(Integer.parseInt(split[3]));
                studentRoomDTO.setPaymentStatus(split[4]);
                studentRoomDTO.setWardenApprovalStatus(split[5]);
//                studentRoomDTO.setStayType(split[6]);
                studentGuestAccomDTO = guestAccommodationRequestService.getGuestAccomForm(studentRoomDTO);
            }
//        } catch (Exception e) {
//
//        }
        studentGuestAccomDTO.setStudentDetailString(id);

        map.addAttribute("GUEST_MAILVIEW_MAX_TODATE",simsConfigDataService.getSimConfigValue(SimsConfigDataService.GUEST_MAILVIEW_MAX_TODATE));
        map.addAttribute("studentGuestAccomDTO",studentGuestAccomDTO);
        map.addAttribute("studentRoomDTO",studentRoomDTO);
        map.addAttribute("wardenNotes",guestAccommodationRequestService.getWardenNote(studentGuestAccomDTO));
        map.addAttribute("userRole",SecurityCtxUtil.userRole());

        commonResponseUtil.updateCommonModelAttributes(map, request);
        return HTMLPage.GUEST_STUDENT_ROOM_VIEW_DETAILS;
    }

    @GetMapping( "${url.guest.student.edit}" + "${id}")
    public String editRoomDetails(@PathVariable String id, PaginationForm form, ModelMap map, HttpServletRequest request) {
        StudentRoomDTO studentRoomDTO = new StudentRoomDTO();
        StudentGuestAccomDTO studentGuestAccomDTO = new StudentGuestAccomDTO();
        try {
            String paramString = new MCrypt().decryptToString(id);
            if(StringUtils.isNotEmpty(paramString)) {
                String[] split = paramString.split(Constants.BACKTICK);
                studentRoomDTO.setStudentId(split[0]);
                studentRoomDTO.setRequestId(Integer.parseInt(split[1]));
                studentRoomDTO.setParentRequestId(Integer.parseInt(split[3]));
                studentRoomDTO.setPaymentStatus(split[4]);
                studentRoomDTO.setWardenApprovalStatus(split[5]);
//                studentRoomDTO.setStayType(split[6]);
                studentGuestAccomDTO = guestAccommodationRequestService.getGuestAccomForm(studentRoomDTO);
                studentGuestAccomDTO.setPaymentType(Constants.PAYMENT_TYPE_DIGITAL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        map.addAttribute("GUEST_MAILVIEW_MAX_TODATE",simsConfigDataService.getSimConfigValue(SimsConfigDataService.GUEST_MAILVIEW_MAX_TODATE));
        map.addAttribute("studentGuestAccomDTO",studentGuestAccomDTO);
        map.addAttribute("studentRoomDTO",studentRoomDTO);
        map.addAttribute("wardenNotes",guestAccommodationRequestService.getWardenNote(studentGuestAccomDTO));
        commonResponseUtil.updateCommonModelAttributes(map, request);
        return HTMLPage.GUEST_STUDENT_ROOM_EDIT_DETAILS;
    }

    @PostMapping("${url.guest.student.request.action}")
    public @ResponseBody BaseResponse saveActionOnRequest(@ModelAttribute OverrideApproveGuestAccomDTO overrideApproveGuestAccomDTO, HttpServletRequest request,
                                                          HttpServletResponse response, RedirectAttributes redirectAttrs) throws IOException {
        String message = null, status = null;
        BaseResponse baseResponse = new BaseResponse();
        boolean isUpdated = false;
        try {
            isUpdated = guestAccommodationRequestService.updateWardenStatusOnRequest(overrideApproveGuestAccomDTO, request);
        } catch (Exception e) {
            e.printStackTrace();
            message = e.getMessage();
            status = messageSource.getMessage("response.status.error", null, Locale.getDefault());
            redirectAttrs.addFlashAttribute("response", new BaseResponse(message, status));
            redirectAttrs.addFlashAttribute(Constants.MODAL_ERROR, true);
            return baseResponse;
        }

        if(isUpdated) {
            message = messageSource.getMessage("response.student.room.update.success", null, Locale.getDefault());
            status = messageSource.getMessage("response.status.success", null, Locale.getDefault());
        } else {
            message = messageSource.getMessage("response.student.room.update.failed", null, Locale.getDefault());
            status = messageSource.getMessage("response.status.error", null, Locale.getDefault());
            redirectAttrs.addFlashAttribute(Constants.MODAL_ERROR, true);
        }

        baseResponse.setMessage(message);
        baseResponse.setStatus(status);
        redirectAttrs.addFlashAttribute(Constants.RESPONSE, baseResponse);
        return baseResponse;
    }

    @PostMapping("${url.guest.student.save}")
    public @ResponseBody BaseResponse saveStudentRequest(@ModelAttribute StudentGuestAccomDTO studentGuestAccomDTO, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs) throws IOException {
        String message = null, status = null;
        BaseResponse baseResponse = new BaseResponse();
        boolean isUpdated = false;
        String s = Constants.REDIRECT + guestAccommodationStudentRequest;
        try {
            if (studentGuestAccomDTO.getPaymentDate() == null || studentGuestAccomDTO.getPaymentReferenceNumber() == null ||
                    studentGuestAccomDTO.getPaymentAmount() == null || studentGuestAccomDTO.getPaymentType() == null) {
                message =  messageSource.getMessage("response.student.room.update.error.invalid", null, Locale.getDefault());
            } else {
                isUpdated = guestAccommodationRequestService.updateStudentRoomRequest(studentGuestAccomDTO);
                message =  messageSource.getMessage("response.student.room.update.success", null, Locale.getDefault());
            }
        } catch (Exception e) {
            message = e.getMessage();
        }

        if (isUpdated) {
            status = messageSource.getMessage("response.status.success", null, Locale.getDefault());
            message =  messageSource.getMessage("response.student.room.update.success", null, Locale.getDefault());
        } else {
            status = messageSource.getMessage("response.status.error", null, Locale.getDefault());
            message = message != null ? message : messageSource.getMessage("response.student.room.update.failed", null, Locale.getDefault());
            redirectAttrs.addFlashAttribute(Constants.MODAL_ERROR, true);
        }
        baseResponse.setMessage(message);
        baseResponse.setStatus(status);
        redirectAttrs.addFlashAttribute(Constants.RESPONSE, baseResponse);
        return baseResponse;

    }

    @PostMapping("${url.guest.student.excel.report.download.api}")
    public void downloadRequestReport(@RequestBody StudentRoomRequestForm studentForm, @ModelAttribute PaginationForm pageForm,
                                      HttpServletResponse response, HttpServletRequest request) {
        HSSFWorkbook hSSFWorkbook = new HSSFWorkbook();
        pageForm.setPage(1);
        pageForm.setSize(500);
        List<HostelMasterDto> hostelList=new ArrayList<>(); //dummy variable
        List<StudentRoomDTO> studentRoomDTOS = guestAccommodationRequestService.getGuestRequestList(pageForm, studentForm, hostelList);
        try {
            Workbook workbook = guestAccommodationRequestService.guestAllotmentReport(studentRoomDTOS);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            workbook.close();
            byte[] excelBytes = bos.toByteArray();
            response.setContentType(FileUploadConstants.XLSX);
            String fileName = messageSource.getMessage("message.guest.accommodation.request.filename", null, Locale.getDefault()) +"" + FileUploadConstants.XLSX_EXTENSION;
            response.setHeader(FileUploadConstants.CONTENT_DISPOSITION, messageSource.getMessage(
                    "message.attachment.filename", new Object[]{fileName}, Locale.getDefault()));
            response.setContentLength(excelBytes.length);

            try (ServletOutputStream outputStream = response.getOutputStream()) {
                outputStream.write(excelBytes);
                outputStream.flush();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("${url.guest.student.room.allot}")
    public @ResponseBody BaseResponse saveAllotment(@RequestBody GuestHostelAllotmentDTO guestHostelAllotmentDTO, HttpServletRequest request,
                                                    HttpServletResponse response, RedirectAttributes redirectAttrs) throws IOException {
        String message = null, status = null;
        BaseResponse baseResponse = new BaseResponse();
        boolean isUpdated = false;
        try {
            isUpdated = guestAccommodationRequestService.processGuestAllotment(guestHostelAllotmentDTO, request);
        } catch (Exception e) {
            e.printStackTrace();
            baseResponse.setMessage(e.getMessage());
            baseResponse.setStatus( messageSource.getMessage("response.status.error", null, Locale.getDefault()));
            redirectAttrs.addFlashAttribute(Constants.RESPONSE, baseResponse);
            return baseResponse;
        }

        if(isUpdated) {
            message = messageSource.getMessage("response.student.room.allot.success", null, Locale.getDefault());
            status = messageSource.getMessage("response.status.success", null, Locale.getDefault());
        } else {
            message = messageSource.getMessage("response.student.room.allot.failed", null, Locale.getDefault());
            status = messageSource.getMessage("response.status.error", null, Locale.getDefault());
            redirectAttrs.addFlashAttribute(Constants.MODAL_ERROR, true);
        }

        baseResponse.setMessage(message);
        baseResponse.setStatus(status);
        redirectAttrs.addFlashAttribute(Constants.RESPONSE, baseResponse);
        return baseResponse;
    }

    @GetMapping("${url.guest.student.pdf.download.api}" + "${id}")
    public ResponseEntity<Resource> downloadStudentBioDataPDF(@PathVariable Long id) throws Exception {
        Resource resource = guestAccommodationRequestService.generatePdf(id);
        return Utility.prepareDownloadFile(resource);
    }

    @PostMapping("${url.guest.student.room.retain}")
    public @ResponseBody BaseResponse retainAllotment(@RequestBody GuestHostelAllotmentDTO guestHostelAllotmentDTO, HttpServletRequest request,
                                                      HttpServletResponse response, RedirectAttributes redirectAttrs) throws IOException {
        String message = null, status = null;
        BaseResponse baseResponse = new BaseResponse();
        boolean isUpdated = false;
        try {
            isUpdated = guestAccommodationRequestService.processGuestAllotmentRetain(guestHostelAllotmentDTO, request);
        } catch (Exception e) {
            e.printStackTrace();
            baseResponse.setMessage(e.getMessage());
            baseResponse.setStatus( messageSource.getMessage("response.status.error", null, Locale.getDefault()));
            redirectAttrs.addFlashAttribute(Constants.RESPONSE, baseResponse);
            return baseResponse;
        }

        if(isUpdated) {
            message = messageSource.getMessage("response.student.room.allot.retain.success", null, Locale.getDefault());
            status = messageSource.getMessage("response.status.success", null, Locale.getDefault());
        } else {
            message = messageSource.getMessage("response.student.room.allot.retain.failed", null, Locale.getDefault());
            status = messageSource.getMessage("response.status.error", null, Locale.getDefault());
            redirectAttrs.addFlashAttribute(Constants.MODAL_ERROR, true);
        }

        baseResponse.setMessage(message);
        baseResponse.setStatus(status);
        redirectAttrs.addFlashAttribute(Constants.RESPONSE, baseResponse);
        return baseResponse;
    }

    @GetMapping("${url.room.list}" + "${id}")
    public @ResponseBody List<HostelRoomInfoDto> getHostelRoomListByHostelId(@PathVariable long id) {
        return hostelRoomInfoService.findRoomByHostelId(id);
    }

    @PostMapping("${url.guest.room.list}")
    public @ResponseBody List<Map<String, Object>> getGuestRoomListByHostel(@RequestBody GuestHostelAllotmentDTO request) {
        return hostelRoomInfoService.findGuestRoomByHostelIdAndRequestId(request);
    }

    void excelDownloadButton(ModelMap map){
        HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
        if (headerForm != null) {
            headerForm.setAdditionalButtonProperties(true,
                    ModelConstants.BUTTON_PINK,
                    commonResponseUtil.getMessage("message.label.guest.room.report"),
                    ModelConstants.FA_FILE_EXCEL
            );
        }
    }
}
