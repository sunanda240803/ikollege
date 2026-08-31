package com.iitm.hosteldine.controller.otherCandidate;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.OtherCandidate.CandidateAppointmentRequestDto;
import com.iitm.hosteldine.dto.OtherCandidate.CandidateStayDateViewDto;
import com.iitm.hosteldine.dto.SimsConfigDataDto;
import com.iitm.hosteldine.dto.SimsConfigDataJsonArrayDto;
import com.iitm.hosteldine.form.AccommodationRequestForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.OtherCandidate.AccommodationRequestService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${url.accommodation.request}")
@RequiredArgsConstructor
public class AccommodationRequestController {

    private final AccommodationRequestService accommodationRequestService;
    private final SimsConfigDataService simsConfigDataService;
    private final CommonResponseUtil commonResponseUtil;
    private final Utility utility;

    @Value("${url.accommodation.request}")
    private String baseUrl;

    @GetMapping
    public String getAccommodationRequestDetails(PaginationForm form, HttpServletRequest request, ModelMap model) throws Exception {
        String resendMailDate = simsConfigDataService.getSimConfigValue(SimsConfigDataService.RESEND_MAIL);
        Page<CandidateStayDateViewDto> accommodationRequestDetails = accommodationRequestService.getAccommodationRequestDetails(form);
        commonResponseUtil.updateCommonModelAttributes(model, request, accommodationRequestDetails, form);
        String requestKey = MCrypt.getInstance().encryptToText(ModelConstants.REQUEST_KEY + utility.getCurrentTimeStamp());
        List<Map<String, String>> accommodationCharges = simsConfigDataService.getSimConfigValueAsTable(SimsConfigDataService.ACCOMMODATION_CHARGES);
        model.addAttribute("resendMailDate", resendMailDate);
        model.addAttribute("workflowStatus", WorkflowStatus.class);
        model.addAttribute("requestKey", requestKey);
        model.addAttribute("accommodationCharges", accommodationCharges);
        request.getSession().setAttribute("encryptedUserId", MCrypt.getInstance().encryptToText(SecurityCtxUtil.userId()));
        return HTMLPage.ACCOMMODATION_REQUEST_LIST;
    }

    @GetMapping(value = "${id}")
    public String addAccommodationRequest(@PathVariable("id") String encryptedKey, HttpServletRequest request,
                                          RedirectAttributes redirectAttributes, ModelMap model) throws Exception {
        Boolean status = utility.checkRequestType(encryptedKey);
        if (status) {
            try {
                String[] split = MCrypt.getInstance().decryptToString(encryptedKey).split(Constants.BACKTICK);
                Long candidateId = (split[1] == null ||split[1].isEmpty()) ? SecurityCtxUtil.candidateId() : Long.parseLong(split[1]);
                Long requestId = Long.parseLong(split[2]);
                AccommodationRequestForm accommodationRequestDetails = accommodationRequestService
                        .getAccommodationRequestDetailsById(candidateId,requestId,encryptedKey,true);
                List<CandidateAppointmentRequestDto> candidateAppointmentApprovedDetails = accommodationRequestService.getApprovedCandidateDetails(candidateId);
                model.addAttribute("accommodationRequestForm", accommodationRequestDetails);
                model.addAttribute("candidateAppointmentApprovedDetails", candidateAppointmentApprovedDetails);
                request.getSession().setAttribute("requestKey", encryptedKey);
                String guideEmail = simsConfigDataService.getSimConfigValue(SimsConfigDataService.GUIDE_EMAIL);
                model.addAttribute("guideEmail",guideEmail);
                List<Map<String, String>> accommodationCharges = simsConfigDataService.getSimConfigValueAsTable(SimsConfigDataService.ACCOMMODATION_CHARGES);
                model.addAttribute("accommodationCharges", accommodationCharges);
                commonResponseUtil.updateHeaderForm(request, model, Constants.ACCOMMODATION_FORM_HEADER, false);
                return HTMLPage.ADD_ACCOMMODATION_REQUEST;
            } catch (Exception e) {
                e.printStackTrace();
                commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
            }
        } else {
            commonResponseUtil.invalidAccess(redirectAttributes);
        }

        return Constants.REDIRECT + baseUrl;
    }

    @PostMapping
    public String saveOrUpdateAccommodationRequest(@ModelAttribute AccommodationRequestForm accommodationRequestForm,
                                                   BindingResult bindingResult, RedirectAttributes redirectAttributes,
                                                   ModelMap model, HttpServletRequest request) throws Exception {
        try {
            String requestKey = request.getSession().getAttribute("requestKey").toString();
            Boolean keyStatus = utility.checkRequestType(requestKey);
            if (keyStatus) {
                accommodationRequestService.validateAccommodationRequestForm(accommodationRequestForm.getCandidateAppointmentRequestDto(), bindingResult);
                if (bindingResult.hasErrors()) {
                    String[] split = MCrypt.getInstance().decryptToString(requestKey).split(Constants.BACKTICK);
                    Long candidateId = SecurityCtxUtil.candidateId();
                    Long requestId = Long.parseLong(split[2]);
                    AccommodationRequestForm accommodationRequestDetailsById = accommodationRequestService
                            .getAccommodationRequestDetailsById(candidateId, requestId, requestKey, true);
                    accommodationRequestForm.setCandidateProfileDto(accommodationRequestDetailsById.getCandidateProfileDto());
                    accommodationRequestForm.setFiles(accommodationRequestDetailsById.getFiles());
                    accommodationRequestForm.setCandidateFilesInformationList(accommodationRequestDetailsById.getCandidateFilesInformationList());
                    accommodationRequestForm.setCandidateWorkflowList(accommodationRequestDetailsById.getCandidateWorkflowList());
                    commonResponseUtil.updateHeaderForm(request, model, Constants.ACCOMMODATION_FORM_HEADER, false);
                    return HTMLPage.ADD_ACCOMMODATION_REQUEST;
                }
                String status = accommodationRequestService.saveOrUpdateAccommodationRequest(accommodationRequestForm,
                        requestKey, request);
                String message;
                if (status.equals(Constants.SAVED)) {
                    message = "message.accommodation.request.save.success";
                } else {
                    message = "message.accommodation.request.update.success";
                }
                commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, message);
            } else {
                commonResponseUtil.invalidAccess(redirectAttributes);
            }
        }catch (Exception e){
            commonResponseUtil.exceptionMessageHandling(e,redirectAttributes);
        }
        return Constants.REDIRECT + baseUrl;
    }


    @GetMapping(value = "${url.cancel.check}")
    @ResponseBody
    public int checkConflictingAppointmentRequest(@ModelAttribute CandidateAppointmentRequestDto dto,
                                                  HttpServletRequest request) throws Exception {
        String requestKey = request.getSession().getAttribute("requestKey").toString();
        return accommodationRequestService.validateStayPeriod(dto, requestKey);
    }

    @GetMapping(value = "${url.view}" + "${url.accommodation.request}" + "${id}")
    public String viewAccommodationRequest(@PathVariable("id") String encryptedKey, HttpServletRequest request,
                                           ModelMap model,RedirectAttributes redirectAttributes) throws Exception {
        Boolean status = utility.checkRequestType(encryptedKey);
        if(status){
            String[] split = MCrypt.getInstance().decryptToString(encryptedKey).split(Constants.BACKTICK);
            Long candidateId = Long.parseLong(split[1]);
            Long requestId = Long.parseLong(split[2]);
            AccommodationRequestForm accommodationRequestForm =
                    accommodationRequestService.getAccommodationRequestDetailsById(candidateId,
                            requestId,encryptedKey,true);
            model.addAttribute("accommodationRequestDetails", accommodationRequestForm);
            return HTMLPage.VIEW_ACCOMMODATION_REQUEST;
        }
        else{
            commonResponseUtil.invalidAccess(redirectAttributes);
            return Constants.REDIRECT + baseUrl;
        }
    }

    @GetMapping(value = "${url.cancel}" + "${id}")
    public String cancelAccommodationRequest(@PathVariable("id") String encryptedKey, RedirectAttributes redirectAttributes) throws Exception {
        Boolean status = utility.checkRequestType(encryptedKey);
        if(status){
            String[] split = MCrypt.getInstance().decryptToString(encryptedKey).split(Constants.BACKTICK);
            Long candidateId = Long.parseLong(split[1]);
            Long requestId = Long.parseLong(split[2]);
            String cancelStatus = accommodationRequestService.cancelAccommodationRequest(candidateId,requestId);
            String message;
            if (cancelStatus.equals(Constants.SAVED)) {
                message = "message.cancelled.accommodation.request";
            } else {
                message = "message.failure.cancelling.accommodation.request";
            }
            commonResponseUtil.updateSaveResponseByStatus(cancelStatus, redirectAttributes, message);
        }
        else{
            commonResponseUtil.invalidAccess(redirectAttributes);
        }

        return Constants.REDIRECT + baseUrl;
    }

    @GetMapping(value = "${url.resend.mail}" + "${id}")
    @ResponseBody
    public Boolean reSendMail(@PathVariable("id") String key, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        Boolean status = utility.checkRequestType(key);
        String[] split = MCrypt.getInstance().decryptToString(key).split(Constants.BACKTICK);
        Long requestId = Long.parseLong(split[2]);
        Long stayId = Long.parseLong(split[3]);
        if (status) {
            return accommodationRequestService.resendMail(request, SecurityCtxUtil.candidateId(), requestId, stayId);
        } else {
            return false;
        }
    }

    @GetMapping("${url.pdf.download}" + "${id}")
    public ResponseEntity<Resource> downloadAccommodationRequestDetailsPDF(@PathVariable("id") String encryptedkey) throws Exception {
        String[] split = MCrypt.getInstance().decryptToString(encryptedkey).split(Constants.BACKTICK);
        Long candidateId = Long.parseLong(split[1]);
        Long requestId = Long.parseLong(split[2]);
        Long stayId = Long.parseLong(split[3]);
        Resource resource = accommodationRequestService.generateAccommodationRequestDetailsPdf(candidateId,requestId,encryptedkey,true, stayId);
        return Utility.prepareDownloadFile(resource);
    }

}
