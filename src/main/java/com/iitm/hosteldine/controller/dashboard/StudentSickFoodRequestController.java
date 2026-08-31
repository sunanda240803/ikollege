package com.iitm.hosteldine.controller.dashboard;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.student.SickFoodDeliveryStatusDto;
import com.iitm.hosteldine.dto.student.SickFoodRequestDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.student.SickFoodDeliveryStatusService;
import com.iitm.hosteldine.service.student.SickFoodService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.sick.food.request}")
public class StudentSickFoodRequestController {

	
	private final SickFoodService sickFoodService;
	private final SickFoodDeliveryStatusService sickFoodDeliveryStatusService;
	private final CommonResponseUtil commonResponseUtil;
	private final SimsConfigDataService simsConfigDataService;
	
	
	
	@Value("${url.sick.food.request}")
	private String sickFoodRequest;
	
	@GetMapping
	public String getSickFoodList(PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		 String studentId = SecurityCtxUtil.userId().toUpperCase(); 
		Page<SickFoodRequestDto> sickFoodList = sickFoodService.getSickFoodList(form,studentId);
		Long vendorContact = sickFoodService.callVendor(sickFoodList.getContent());
		commonResponseUtil.updateCommonModelAttributes(map, request ,sickFoodList , form);
		map.addAttribute("vendorContact", vendorContact);
		return HTMLPage.STUDENT_SICK_FOOD_REQUEST;
	}
	
	
	@PostMapping
	public String saveSickFoodForm(@Valid @ModelAttribute SickFoodRequestDto sickFoodRequestDto,BindingResult bindingResult,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		
		try {
		String saveStatus = sickFoodService.saveOrUpdateSickFood(sickFoodRequestDto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus,redirectAttrs);
		}
		catch(Exception e) {
			commonResponseUtil.exceptionMessageHandling(e, redirectAttrs);
		}
		
		return Constants.REDIRECT + sickFoodRequest;
		
	}
	
	
	
	 @GetMapping("${url.request.date.exist}"  + "${requestDate}")
	  public  ResponseEntity<?> checkSickFoodRequestExists(@PathVariable LocalDate requestDate) {
		 String studentId = SecurityCtxUtil.userId().toUpperCase(); 
	 boolean sickFood=sickFoodService.checkSickFoodRequestExists(studentId, requestDate); 
	 return ResponseEntity.ok().body(sickFood) ;
	  }

    @PostMapping("${url.delivery.status}")
    @ResponseBody
    public BaseResponse updateStatus(
            @ModelAttribute SickFoodDeliveryStatusDto requestDto) throws Exception {

        boolean updatedDto = sickFoodDeliveryStatusService.updateDeliveryStatus(requestDto);
        BaseResponse baseResponse = new BaseResponse();
        String status;
        String message;
        if (updatedDto) {
            status = Constants.SUCCESS;
            message = commonResponseUtil.getMessage("message.sick.food.delivered.successfully");
        } else {
            status = Constants.FAILURE;
            message = commonResponseUtil.getMessage("message.sick.food.not.delivered");
        }
        baseResponse.setStatus(status);
        baseResponse.setMessage(message);
        return baseResponse;

    }
	
	
	@GetMapping("${id}")
	public String getSickFoodById(@PathVariable long id, ModelMap map, HttpServletRequest request)
			throws Exception {
		SickFoodRequestDto sickFoodRequestDto = sickFoodService.getSickFoodById(id);

		map.addAttribute("sickFoodRequestDto", sickFoodRequestDto);
		map.addAttribute("contactInfo", simsConfigDataService.getSimConfigValue(SimsConfigDataService.SICK_FOOD_REQ_MESS_CONTACTS));
		return HTMLPage.ADD_EDIT_SICK_FOOD_REQUEST;
	}
	
	
	@GetMapping("${url.download}/{id}")
	public ResponseEntity<Resource> downloadFile(@PathVariable long id) throws Exception {
		ByteArrayResource resource = sickFoodService.downloadFile(id);
		String fileName = sickFoodService.getFileName(id);
		return Utility.prepareDownloadFile(resource, fileName);
	}
	
	@ResponseBody
	@PostMapping("${url.not.food.deliver}")
    public BaseResponse foodNotDeliverStatus(@ModelAttribute SickFoodDeliveryStatusDto requestDto) throws Exception {

        boolean updatedDto = sickFoodDeliveryStatusService.foodNotDeliverStatus(requestDto);
        BaseResponse baseResponse = new BaseResponse();
        String status;
        String message;
        if (updatedDto) {
            status = Constants.SUCCESS;
            message = commonResponseUtil.getMessage("message.sick.food.not.deliver.mail");
        } else {
            status = Constants.FAILURE;
            message = commonResponseUtil.getMessage("message.sick.food.not.deliver.mail.failure");
        }
        baseResponse.setStatus(status);
        baseResponse.setMessage(message);
        return baseResponse;

    }




}
	 
