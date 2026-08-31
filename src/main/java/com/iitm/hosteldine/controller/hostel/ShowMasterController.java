package com.iitm.hosteldine.controller.hostel;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.hostel.ShowEventMasterDto;
import com.iitm.hosteldine.dto.hostel.ShowMasterDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.ShowEventMasterService;
import com.iitm.hosteldine.service.hostel.ShowMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.show.wise.details}")
public class ShowMasterController {
	
	private final ShowMasterService showMasterService;
	private final ShowEventMasterService showEventMasterService;
	private final CommonResponseUtil commonResponseUtil;
	
	@Value("${url.show.wise.details}")
	private String showWiseDetails;
	
	@GetMapping
	public String getShowWiseDetailsList(@RequestParam Map<String, String> allParams,PaginationForm form, ModelMap map, HttpServletRequest request)
			throws Exception {
		Page<ShowMasterDto> showWiseList = null;
		commonResponseUtil.getAdditionalParams(allParams, form);
		Long eventId = (Long) map.get("eventId"); // From redirect attributes
		if (eventId != null) {
			form.getAdditionalParam().put("eventId", String.valueOf(eventId));
		}
		if (form.getAdditionalParam().get("eventId") != null) {
			showWiseList = showMasterService.getShowWiseDetailsList(form);
		} else {
			form.getAdditionalParam().put("eventId", 0l);
		}
		List<ShowEventMasterDto> eventList = showEventMasterService.getActiveEventMasterList();

		map.addAttribute("eventList", eventList);
		commonResponseUtil.updateCommonModelAttributes(map, request, showWiseList, form);
		return HTMLPage.SHOW_WISE;
	}
	
	@GetMapping("${id}")
	public String getShowWiseDetailsById(@PathVariable long id, ModelMap map, HttpServletRequest request)
			throws Exception {
		String formKey = "showMasterDto";
		ShowMasterDto showMasterDto = commonResponseUtil.handleModalFormError(request, map, formKey,ShowMasterDto.class);

		if (Objects.nonNull(id) && id > 0) {
			showMasterDto = showMasterService.getShowWiseDetailsById(id);
		}
		List<ShowEventMasterDto> eventList = showEventMasterService.getActiveEventMasterList();
		map.addAttribute("eventList", eventList);
		map.addAttribute(formKey, showMasterDto);
		return HTMLPage.ADD_EDIT_SHOW_WISE;
	}
	
	@PostMapping
	public String saveUpdateShowWise(@Valid @ModelAttribute ShowMasterDto showMasterDto, BindingResult bindingResult,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		String saveStatus = null;
		if (bindingResult.hasErrors()) {
			commonResponseUtil.updateModalFormErrorAttributes(redirectAttrs, bindingResult, showMasterDto);
			return "redirect:" + showWiseDetails;
		}
		if (showMasterDto.getId() == null) {
		    showMasterDto.setId(0L);
		}
		ShowMasterDto dto = showMasterService.saveUpdateShowWise(showMasterDto);
		if(dto != null) {
			saveStatus = (showMasterDto.getId()!=null && showMasterDto.getId() > 0) ? Constants.UPDATED : Constants.SAVED;
			commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs);
			redirectAttrs.addFlashAttribute("dto", dto);
			Long eventId = dto.getShowEventMaster().getId();
		    redirectAttrs.addFlashAttribute("eventId", eventId);
		}
		
		return Constants.REDIRECT + showWiseDetails;
	}
	
	@GetMapping("${url.show.name.exist}" + "${eventId}" + "${showName}" + "${id}")
	public @ResponseBody boolean checkShowNameExist( @PathVariable long eventId,@PathVariable String showName,@PathVariable long id,
			 HttpServletRequest request) {
		return showMasterService.checkShowNameExist(eventId,showName,id);
	}

}
