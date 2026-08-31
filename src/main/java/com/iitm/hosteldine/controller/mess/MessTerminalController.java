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
import com.iitm.hosteldine.dto.mess.MessTerminalDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.service.mess.MessTerminalService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.mess.terminal}")
public class MessTerminalController {
	private final MessTerminalService messTerminalService;
	private final CommonResponseUtil commonResponseUtil;
	private final MessMasterService messMasterService;

	@Value("${url.mess.terminal}")
	private String getMessTerminal;
	
	@GetMapping
	public String getMessTerminalList(PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		//map.addAttribute("messTerminaList", messTerminalService.getMessTerminalList());
		Page<MessTerminalDto> messTerminaList = messTerminalService.getMessTerminalList(form);
		commonResponseUtil.updateCommonModelAttributes(map, request,messTerminaList,form);
		return HTMLPage.MESS_TERMINAL;
	}

	@GetMapping("${id}")
	public String getMessTerminalDetailsById(@PathVariable Long id, ModelMap map, HttpServletRequest request)
			throws Exception {
		MessTerminalDto messTerminalDto = new MessTerminalDto();
		if (id != null && id != 0) {
			messTerminalDto = messTerminalService.getMessTerminalDetailsById(id);
		}
		map.addAttribute("messTerminalDto", messTerminalDto);
		
		map.addAttribute("messMasterList", messMasterService.getMessMasterList());

		return HTMLPage.ADD_EDIT_MESS_TERMINAL;
	}

	@PostMapping
	public String saveAndUpdate(ModelMap map, @ModelAttribute MessTerminalDto dto, HttpServletRequest request,
			RedirectAttributes redirectAttrs) throws Exception {
		String saveStatus = messTerminalService.saveAndUpdate(dto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus,redirectAttrs);
		redirectAttrs.addFlashAttribute(Constants.FORM, dto);
		return Constants.REDIRECT + getMessTerminal;
	}

	@DeleteMapping("${id}")
	public @ResponseBody BaseResponse deleteMessTerminalById(@PathVariable Long id, ModelMap map,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		return CommonResponseUtil.generateDeleteResponseByStatus(messTerminalService.deleteMessTerminalById(id));
	}

	@GetMapping("${url.exist}" + "${terminalIp}" + "${id}")
	public @ResponseBody boolean checkMessNameExist(@PathVariable String terminalIp,@PathVariable Long id, ModelMap map,
			HttpServletRequest request) {
		return messTerminalService.checkTerminalIpExist(terminalIp, id);
	}
	
}
