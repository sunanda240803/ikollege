package com.iitm.hosteldine.controller.dashboard;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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
import com.iitm.hosteldine.dto.student.MessCardAmountTransferDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.student.MessToCardTransferControllerService;
import com.iitm.hosteldine.service.student.MessToCardTransferService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.mess.to.card.transfer}")
public class MessToCardTransferController {

	
	private final MessToCardTransferService messToCardTransferService;
	private final MessToCardTransferControllerService messToCardTransferControllerService;
	private final CommonResponseUtil commonResponseUtil;
	
	
	@Value("${url.mess.to.card.transfer}")
	private String studentMessToCardList;
	
	@GetMapping
	public String getMessToCardList(PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		 String studentId = SecurityCtxUtil.userId().toUpperCase(); 
		Page<MessCardAmountTransferDto> messToCardList = messToCardTransferService.getMessToCardList(form,studentId);
		commonResponseUtil.updateCommonModelAttributes(map, request ,messToCardList , form);
		return HTMLPage.MESS_TO_CARD_TRANSFER;
	}
	
	@GetMapping("${id}")
	public String getMessToCardById(@PathVariable long id, ModelMap map, HttpServletRequest request) throws Exception {
		String studentId = SecurityCtxUtil.userId().toUpperCase();
		String formKey = "messCardAmountTransferDto";
		MessCardAmountTransferDto messCardAmountTransferDto = commonResponseUtil.handleModalFormError(request, map,
				formKey, MessCardAmountTransferDto.class);
		map.addAttribute(formKey, messCardAmountTransferDto);
		Double maxAmount = messToCardTransferControllerService.getMaxTransferAmount();
		map.addAttribute("maxAmount", maxAmount);
		String ledgerBalance = messToCardTransferService.getStudentLedgerBalance(studentId);
		map.addAttribute("ledgerBalance", ledgerBalance);
		Double totalfinalAmount = messToCardTransferService.getTotalTransferAmount();
		map.addAttribute("totalfinalAmount", totalfinalAmount);
		return HTMLPage.ADD_MESS_CARD_TRANSFER;
	}
	
	

	@PostMapping
	public String saveMessToCardAmount(@ModelAttribute MessCardAmountTransferDto messCardAmountTransferDto,
			BindingResult bindingResult, HttpServletRequest request, RedirectAttributes redirectAttrs)
			throws Exception {
		messToCardTransferService.validateMessToCard(messCardAmountTransferDto, bindingResult);
		if (bindingResult.hasErrors()) {
			commonResponseUtil.updateModalFormErrorAttributes(redirectAttrs, bindingResult, messCardAmountTransferDto);
			return "redirect:" + studentMessToCardList;
		}

		String saveStatus = messToCardTransferService.saveMessToCardAmount(messCardAmountTransferDto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs);
		return Constants.REDIRECT + studentMessToCardList;
	}


}
	 
