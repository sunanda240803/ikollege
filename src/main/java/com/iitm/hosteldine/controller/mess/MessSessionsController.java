package com.iitm.hosteldine.controller.mess;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
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
import com.iitm.hosteldine.dto.mess.MessSessionDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.service.mess.MessSessionsService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.mess.session}")
public class MessSessionsController {
	@Value("${url.mess.session}")
	private String getMessSessionList;
	private final MessSessionsService messSessionsService;
	private final MessMasterService messMasterService;
	private final MessageSource messageSource;
	private final CommonResponseUtil commonResponseUtil;

	@GetMapping
	public String messSessionInfo(PaginationForm form, ModelMap map, HttpServletRequest request)
			throws Exception {
		/*List<MessSessionDto> messSessionList = messSessionsService.getMessionSessionList();
	    map.addAttribute("messSessionList", messSessionList);*/
		Page<MessSessionDto> messSessionPage = messSessionsService.getMessSessionList(form);
		commonResponseUtil.updateCommonModelAttributes(map, request, messSessionPage, form);
		return HTMLPage.MESS_SESSION;
	}
	
	@GetMapping( "${id}" + "${sessionName}")
	public String getMessSessionDetailsById(@PathVariable long id,@PathVariable String sessionName, ModelMap map, HttpServletRequest request)
			throws Exception {
		MessSessionDto messSessionDto = messSessionsService.getMessSessionDetailsById(id,sessionName);
		if (messSessionDto != null) {
			map.addAttribute("messSessionDto", messSessionDto);
			map.addAttribute("editMode", true); // Or false for create mode
		} else {
			map.addAttribute("messSessionDto", new MessSessionDto());
		}
		map.addAttribute("messMasterList", messMasterService.getMessMasterList());
		System.out.println(messMasterService.getMessMasterList());
		return HTMLPage.MESS_SESSION_MODAL;
	}
	
	@PostMapping
	public String saveUpdateMessSession(ModelMap map, @ModelAttribute MessSessionDto messSessionDto,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		String saveStatus = messSessionsService.saveUpdateMessSession(messSessionDto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus,redirectAttrs);
		return Constants.REDIRECT + getMessSessionList;
	}
	
	@DeleteMapping("${id}" + "${sessionName}")
	public @ResponseBody BaseResponse deleteMessSession(@PathVariable long id,@PathVariable String sessionName,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		return CommonResponseUtil.generateDeleteResponseByStatus(messSessionsService.deleteMessSession(id,sessionName));
	}
	
	@GetMapping("${url.mess.session.exist}" + "${id}" + "${sessionName}")
	public @ResponseBody boolean checkDesignationNameExist( @PathVariable long id,@PathVariable String sessionName,
			 HttpServletRequest request) {
		return messSessionsService.checkSessionNameExist(id,sessionName);
	}
	
}
