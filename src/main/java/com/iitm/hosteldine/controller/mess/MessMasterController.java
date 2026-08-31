package com.iitm.hosteldine.controller.mess;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.mess.master}")
public class MessMasterController {
	private final MessMasterService messMasterService;
	private final CommonResponseUtil commonResponseUtil;

	@Value("${url.mess.master}")
	private String getMessMaster;
	
	@GetMapping
	public String getMessMasterList(PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		//map.addAttribute("messMasterList", messMasterService.getMessMasterList());
		Page<MessMasterDto> messMasterList = messMasterService.getMessMasterList(form);
		commonResponseUtil.updateCommonModelAttributes(map, request,messMasterList,form);
		return HTMLPage.MESS_MASTER;
	}

	@GetMapping("${id}")
	public String getMessMasterDetailsById(@PathVariable Long id, ModelMap map, HttpServletRequest request)
			throws Exception {
		MessMasterDto messMasterDto = new MessMasterDto();
		if (id != null && id != 0) {
			messMasterDto = messMasterService.getMessMasterDetailsById(id);
		}
		map.addAttribute("messMasterDto", messMasterDto);

		return HTMLPage.ADD_EDIT_MESS_MASTER;
	}

	@PostMapping
	public String saveAndUpdate(ModelMap map, @ModelAttribute MessMasterDto dto, HttpServletRequest request,
			RedirectAttributes redirectAttrs) throws Exception {
		String saveStatus = messMasterService.saveAndUpdate(dto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus,redirectAttrs);
		redirectAttrs.addFlashAttribute(Constants.FORM, dto);
		return Constants.REDIRECT + getMessMaster;
	}

	@DeleteMapping("${id}")
	public @ResponseBody BaseResponse deleteMessMasterById(@PathVariable Long id, ModelMap map,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		try {
			return CommonResponseUtil.generateDeleteResponseByStatus(messMasterService.deleteMessMasterById(id));
	    } catch (RecordNotExistsException e) {
	        // Create a custom error response in case of an exception
	        BaseResponse errorResponse = new BaseResponse();
	        errorResponse.setMessage(e.getMessage());  
	        errorResponse.setStatus("Failure");        
	        return errorResponse;
	    }
	}

	@GetMapping("${url.exist}" + "${messName}" + "${id}")
	public @ResponseBody boolean checkMessNameExist(@PathVariable String messName,@PathVariable Long id, ModelMap map,
			HttpServletRequest request) {
		return messMasterService.checkMessNameExist(messName,id);
	}
	
	@GetMapping("${url.mess.head.exist}" + "${messHead}" + "${id}")
	public @ResponseBody boolean checkMessHeadExist(@PathVariable String messHead,@PathVariable Long id, ModelMap map,
			HttpServletRequest request) {
		return messMasterService.checkMessHeadExist(messHead,id);
	}
	
}
