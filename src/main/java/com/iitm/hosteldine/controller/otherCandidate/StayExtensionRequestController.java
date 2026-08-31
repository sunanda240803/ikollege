package com.iitm.hosteldine.controller.otherCandidate;

import com.iitm.hosteldine.dto.OtherCandidate.StayExtensionRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.form.AccommodationRequestForm;
import com.iitm.hosteldine.form.StayExtensionRequestForm;
import com.iitm.hosteldine.service.OtherCandidate.AccommodationRequestService;
import com.iitm.hosteldine.service.OtherCandidate.StayExtensionRequestService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Controller
@RequestMapping(value = "${url.stay.extension.request}")
@RequiredArgsConstructor
public class StayExtensionRequestController {

	private final StayExtensionRequestService stayExtensionRequestService;
	private final CommonResponseUtil commonResponseUtil;
	private final AccommodationRequestService accommodationRequestService;
	private final HostelMasterService hostelMasterService;
	
	@Value("${url.accommodation.request}")
    private String accReqUrl;
	
	@Value("${url.stay.extension.request}")
	private String stayExtReqUrl;
	
	@GetMapping(value = "${id}")
    public String applyStayExtensionRequest(@PathVariable("id") String encryptedKey,HttpServletRequest request,
                                          RedirectAttributes redirectAttributes, ModelMap model) throws Exception {
        Boolean type = checkRequestType(encryptedKey);
        try{
            //Decrypting key
            String[] split = MCrypt.getInstance().decryptToString(encryptedKey).split(Constants.BACKTICK);
            Long candidateId = SecurityCtxUtil.candidateId();
            Long requestId = Long.parseLong(split[2]);
            Long stayId = Long.parseLong(split[3]);
            StayExtensionRequestForm stayExtensionRequestForm = stayExtensionRequestService.getStayExtensionDetailsById(candidateId,requestId,stayId,encryptedKey,true);
            List<StayExtensionRequestDto> stayExtensionApprovedDetails = stayExtensionRequestService.getStayExtensionDetails(candidateId, requestId);
            model.addAttribute("stayExtensionRequestForm", stayExtensionRequestForm);
            model.addAttribute("stayExtensionApprovedDetails", stayExtensionApprovedDetails);
            commonResponseUtil.updateHeaderForm(request,model,Constants.STAY_EXTENSION_FORM_HEADER,false);
            return HTMLPage.ADD_STAY_EXTENSION_REQUEST;
        }
        catch (Exception e){
            e.printStackTrace();
            commonResponseUtil.exceptionMessageHandling(e,redirectAttributes);
        }
        return Constants.REDIRECT + accReqUrl;
    }
	
	@PostMapping
	public String saveUpdateOtherCandidate(ModelMap map, @ModelAttribute StayExtensionRequestForm stayExtensionRequestForm,
			BindingResult bindingResult,HttpServletRequest request, RedirectAttributes redirectAttrs,ModelMap model) throws Exception {
		try {
            stayExtensionRequestService.validateStayExtensionRequest(stayExtensionRequestForm, bindingResult);
            if (bindingResult.hasErrors()) {
                String encryptedKey = stayExtensionRequestForm.getEncryptedKey();
                String[] split = MCrypt.getInstance().decryptToString(encryptedKey).split(Constants.BACKTICK);
                Long candidateId = Long.parseLong(split[1]);
                Long requestId = Long.parseLong(split[2]);
                AccommodationRequestForm stayExtensionDetails = accommodationRequestService
                        .getAccommodationRequestDetailsById(candidateId, requestId, encryptedKey, true);
                stayExtensionRequestForm.setCandidateAppointmentRequestDto(stayExtensionDetails.getCandidateAppointmentRequestDto());
                //stayExtensionRequestDetails.setCandidateFilesInformationDto(stayExtensionDetails.getCandidateFilesInformationDto());
                stayExtensionRequestForm.setCandidateProfileDto(stayExtensionDetails.getCandidateProfileDto());
                stayExtensionRequestForm.setCandidateWorkflowList(stayExtensionDetails.getCandidateWorkflowList());
                stayExtensionRequestForm.getStayExtensionRequestDto().setHostelList(hostelMasterService.getHostelList());

                return HTMLPage.ADD_STAY_EXTENSION_REQUEST;
            }
            String saveStatus = stayExtensionRequestService.saveUpdateStayExtensionRequest(stayExtensionRequestForm, request);
            commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs);
        } catch (Exception e) {
            commonResponseUtil.exceptionMessageHandling(e, redirectAttrs);
        }
		return Constants.REDIRECT + accReqUrl;
	}
	
	@GetMapping(value = "${url.view}" + "${id}")
    public String viewStayExtensionRequest(@PathVariable("id") String encryptedKey,
                                          RedirectAttributes redirectAttributes, ModelMap model) throws Exception {
        Boolean type = checkRequestType(encryptedKey);
        try{
            //Decrypting key
            String[] split = MCrypt.getInstance().decryptToString(encryptedKey).split(Constants.BACKTICK);
            Long candidateId = SecurityCtxUtil.candidateId();
            Long requestId = Long.parseLong(split[2]);
            Long stayId = Long.parseLong(split[3]);
            StayExtensionRequestForm stayExtensionDetails = stayExtensionRequestService.getStayExtensionDetailsById(candidateId,requestId,stayId,encryptedKey,true);
            model.addAttribute("stayExtensionDetails", stayExtensionDetails);
            model.addAttribute("workFlowDetails", stayExtensionRequestService.getStayExtensionWorkFlowDetails(encryptedKey));
            return HTMLPage.VIEW_STAY_EXTENSION_REQUEST;
        }
        catch (Exception e){
            e.printStackTrace();
            commonResponseUtil.exceptionMessageHandling(e,redirectAttributes);
        }
        return Constants.REDIRECT + accReqUrl;
    }

	@GetMapping(value = "${url.cancel.request}" + "${id}")
    public String cancelStayRequest(@PathVariable("id") String encryptedKey,
                                          RedirectAttributes redirectAttributes, ModelMap model) throws Exception {
        Boolean type = checkRequestType(encryptedKey);
        try{
        	//Decrypting key
    		String[] split = MCrypt.getInstance().decryptToString(encryptedKey).split(Constants.BACKTICK);
            Long candidateId = SecurityCtxUtil.candidateId();
            Long requestId = Long.parseLong(split[2]);
            Long stayId = Long.parseLong(split[3]);
            accommodationRequestService.cancelStayExtension(stayId);
            return Constants.REDIRECT + accReqUrl;
        }
        catch (Exception e){
            e.printStackTrace();
            commonResponseUtil.exceptionMessageHandling(e,redirectAttributes);
        }
        return Constants.REDIRECT + accReqUrl;
    }
	
	private Boolean checkRequestType(String encryptedKey) throws Exception {
        String decryptedKey = MCrypt.getInstance().decryptToString(encryptedKey);
        if (decryptedKey.equals(ModelConstants.REQUEST_KEY)) {
            return true;
        }
        else return Utility.validateRegexPattern(Constants.ACCOMMODATION_EDIT_REGEX, decryptedKey);
    }
}
