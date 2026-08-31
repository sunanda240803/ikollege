package com.iitm.hosteldine.controller.hostel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.hostel.AccountHeadDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.AccountHeadService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.hostel.account.head}")
public class AccountHeadController {
	
	private final AccountHeadService accountHeadService;
	private final CommonResponseUtil commonResponseUtil;

	@Value("${url.hostel.account.head}")
	private String accountHead;
	
	@GetMapping
	public String getAccHeadList(PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		Page<AccountHeadDto> accHeadList = accountHeadService.getAccHeadList(form);
		commonResponseUtil.updateCommonModelAttributes(map, request ,accHeadList , form);
		return HTMLPage.ACCOUNT_HEAD;
	}
	
	@PostMapping
	public String saveUpdateAccountHead(ModelMap map, @ModelAttribute AccountHeadDto accountHeadDto,
			BindingResult bindingResult,HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		
		accountHeadService.validateAccountHead(accountHeadDto, bindingResult);
		if (bindingResult.hasErrors()) {
			commonResponseUtil.updateModalFormErrorAttributes(redirectAttrs, bindingResult, accountHeadDto);
			return "redirect:" + accountHead;
		}
		String saveStatus = accountHeadService.saveUpdateAccountHead(accountHeadDto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus,redirectAttrs);
		return Constants.REDIRECT + accountHead;
	}
	
	@GetMapping("/{accHead}")
	public String getAccountHeadByAccHead(@PathVariable String accHead, ModelMap map, HttpServletRequest request)
			throws Exception {
		String formKey = "accountHeadDto";
		AccountHeadDto accountHeadDto = new AccountHeadDto();
		if(accHead.equals("0")) {
		accountHeadDto = commonResponseUtil.handleModalFormError(request, map,
				formKey, AccountHeadDto.class);
		}else {
			accountHeadDto = accountHeadService.getAccountHeadByAccHead(accHead);
		}
		map.addAttribute(formKey, accountHeadDto);
		return HTMLPage.ADD_EDIT_ACCOUNT_HEAD;
	}
	
	@DeleteMapping("/{accHead}")
	public @ResponseBody BaseResponse deleteAccounthead(@PathVariable String accHead) throws Exception {
	    try {
	        boolean deleteStatus = accountHeadService.deleteAccountHeadByAccHead(accHead);
	        return CommonResponseUtil.generateDeleteResponseByStatus(deleteStatus);
	    } catch (RecordNotExistsException e) {
	        BaseResponse errorResponse = new BaseResponse();
	        errorResponse.setMessage(e.getMessage());  
	        errorResponse.setStatus("Failure");        
	        return errorResponse;
	    }
	}
	
	@GetMapping("${url.hostel.account.head.exist}" + "/{accHead}")
			public @ResponseBody boolean checkAccHeadExist(@PathVariable String accHead,
			ModelMap map, HttpServletRequest request) {
		return accountHeadService.checkAccHeadExist(accHead);
	}

}
