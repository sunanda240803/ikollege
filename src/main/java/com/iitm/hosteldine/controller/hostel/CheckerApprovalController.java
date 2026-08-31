package com.iitm.hosteldine.controller.hostel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.hostel.CheckerApprovalCreditDebitDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.CheckerApprovalService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.transaction.checker.approval}")
public class CheckerApprovalController {

	private final CheckerApprovalService checkerApprovalService;
	private final CommonResponseUtil commonResponseUtil;
	private final MessageSource messageSource;
	
	@Value("${url.transaction.checker.approval}")
	private String checkerApprovalUrl;

	@GetMapping
	public String getCheckerApprovalList(PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {

		PageImpl<CheckerApprovalCreditDebitDto> checkerApprovalList = checkerApprovalService.getCheckerApprovalList(form);

		commonResponseUtil.updateCommonModelAttributes(map, request ,checkerApprovalList , form);
		return HTMLPage.TRANSACTION_CHECKER_APPROVAL;
	}

	@PostMapping("${url.save}")
	public String saveCheckerApproval(@RequestParam("voucherNumbers") String voucherNumbersJson,ModelMap map,
									  HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		List<String> voucherNumbers = new ObjectMapper().readValue(voucherNumbersJson, new TypeReference<List<String>>() {});

		String loggedInUserRole = SecurityCtxUtil.userRole();
		String message = null, status = null;
		BaseResponse baseResponse = new BaseResponse();
		String s = "redirect:" + checkerApprovalUrl;

		/*if (!StringUtils.equalsIgnoreCase(loggedInUserRole, Constants.SOFTWARE_ADMIN)) {
			redirectAttrs.addFlashAttribute("Error",
					messageSource.getMessage("message.action.not.allowed", null, Locale.getDefault()));
			return s;
		}*/

		try {
			Boolean saved = checkerApprovalService.saveCheckerApprovals(voucherNumbers);
			if (!saved) {
				redirectAttrs.addFlashAttribute("Error", messageSource.getMessage("response.update.error", null, Locale.getDefault()));
				return s ;
			} else {
				message = messageSource.getMessage("response.update.success", null, Locale.getDefault());
				status = messageSource.getMessage("response.status.success", null, Locale.getDefault());
				redirectAttrs.addFlashAttribute("response", new BaseResponse(message, status));
				return s ;
			}
		} catch (Exception e) {
			redirectAttrs.addFlashAttribute("Error", messageSource.getMessage( "response.update.error", null, Locale.getDefault()));
			return s ;
		}

	}

	@DeleteMapping("/{voucherNumber}")
	public @ResponseBody BaseResponse deleteCheckerApproval(@PathVariable String voucherNumber) throws Exception {
		String loggedInUserRole = SecurityCtxUtil.userRole();
		String s = "redirect:" + checkerApprovalUrl;

		if (!StringUtils.equalsIgnoreCase(loggedInUserRole, Constants.SOFTWARE_ADMIN)) {
			BaseResponse errorResponse = new BaseResponse();
			errorResponse.setErrors(messageSource.getMessage("message.action.not.allowed", null, Locale.getDefault()));
			errorResponse.setStatus("Error");
			return errorResponse;
		}

		try {
			Boolean deleted = checkerApprovalService.deleteCheckerApprovals(voucherNumber);
			return CommonResponseUtil.generateDeleteResponseByStatus(deleted);
		} catch (Exception e) {
			// Create a custom error response in case of an exception
			BaseResponse errorResponse = new BaseResponse();
			errorResponse.setMessage(e.getMessage());
			errorResponse.setStatus("Error");
			return errorResponse;
		}
	}


}
