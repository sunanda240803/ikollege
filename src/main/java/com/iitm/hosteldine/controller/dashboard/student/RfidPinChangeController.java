package com.iitm.hosteldine.controller.dashboard.student;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.student.UserFpCardDto;
import com.iitm.hosteldine.service.UserFpCardService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.dashboard.RfidPinChangeValidator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("${url.user.rfid.pin.change.widget}")
public class RfidPinChangeController {

	private final CommonResponseUtil commonResponseUtil;
	private final UserFpCardService userFpCardService;
	private final RfidPinChangeValidator rfidPinChangeValidator;
	
	@GetMapping
	public String getUserRfidPinDetails(ModelMap map, HttpServletRequest request) throws Exception {
		String lastUpdatedRfidPinDate = userFpCardService.getRfidPinUpdatedDate(SecurityCtxUtil.userId().toUpperCase());
		map.addAttribute("lastUpdatedRfidPinDate", lastUpdatedRfidPinDate);
		return HTMLPage.RFID_PIN_CHANGE;
	}
	
	@GetMapping("${url.user.rfid.pin.details.by.id}")
	public String getUserRfidPinDetailsById(ModelMap map, HttpServletRequest request) throws Exception {
		UserFpCardDto userFpCardDto = userFpCardService.getUserRfidPinDetails(SecurityCtxUtil.userId().toUpperCase());
		map.addAttribute("userFpCardDto", userFpCardDto);
		commonResponseUtil.updateCommonModelAttributes(map, request, null, null);
		return HTMLPage.RFID_PIN_CHANGE_MODAL;
	}

	@PostMapping("${url.update.rfid.pin}")
	public ResponseEntity<Object> saveAndUpdate(@ModelAttribute UserFpCardDto userFpCardDto,
			HttpServletRequest request, RedirectAttributes redirectAttrs, BindingResult result) throws Exception {

		rfidPinChangeValidator.validate(userFpCardDto, result);
		if (result.hasErrors()) {
			return commonResponseUtil.handleValidationErrors(result);
		}
		
		String saveStatus = userFpCardService.saveAndUpdate(userFpCardDto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs);
		BaseResponse baseResponse = (BaseResponse) redirectAttrs.getFlashAttributes().get(Constants.RESPONSE);
		return ResponseEntity.ok(baseResponse);
	}
	
	@GetMapping("${url.current.rfid.pin.no}")
    public @ResponseBody String getCurrentRfidPinNumber() throws Exception {
        return userFpCardService.getCurrentRfidPinNumber();
    }
}
