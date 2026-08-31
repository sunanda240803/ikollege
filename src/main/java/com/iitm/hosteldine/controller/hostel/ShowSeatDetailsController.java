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
import com.iitm.hosteldine.dto.hostel.ShowSeatDetailsDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.ShowEventMasterService;
import com.iitm.hosteldine.service.hostel.ShowMasterService;
import com.iitm.hosteldine.service.hostel.ShowSeatDetailsService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.show.seat.details}")
public class ShowSeatDetailsController {

	private final ShowSeatDetailsService showSeatDetailsService;
	private final ShowMasterService showMasterService;
	private final CommonResponseUtil commonResponseUtil;
	private final ShowEventMasterService showEventMasterService;

	@Value("${url.show.seat.details}")
	private String showSeatDetails;
	
	@GetMapping
	public String getShowSeatDetailsList(@RequestParam Map<String, String> allParams,PaginationForm form, ModelMap map, HttpServletRequest request)
			throws Exception {
		Page<ShowSeatDetailsDto> seatList = null;
		commonResponseUtil.getAdditionalParams(allParams, form);
		Long showId = (Long) map.get("showId"); // From redirect attributes
		Long eventId = (Long) map.get("eventId");
		if (showId != null) {
			form.getAdditionalParam().put("showId", String.valueOf(showId));
		}
		if (eventId != null) {
			form.getAdditionalParam().put("eventId", String.valueOf(eventId));
		}
		if (form.getAdditionalParam().get("showId") != null) {
			seatList = showSeatDetailsService.getSeatDetailsById(form);
		} else {
			form.getAdditionalParam().put("eventId", 0l);
			form.getAdditionalParam().put("showId", 0l);
		}
		List<ShowEventMasterDto> eventList = showEventMasterService.getActiveEventMasterList();
		map.addAttribute("eventList", eventList);
		if(form.getAdditionalParam().get("eventId") != null) {
		List<ShowMasterDto> showList =	showMasterService.getShowNameList(Long.parseLong(form.getAdditionalParam().get("eventId").toString()));
		map.addAttribute("showList", showList);
		}
		commonResponseUtil.updateCommonModelAttributes(map, request, seatList, form);
		return HTMLPage.SHOW_SEAT;
	}

	@GetMapping("${url.get}" + "${id}" + "${eventId}")
	public String getShowSeatDetailsById(@PathVariable long id, @PathVariable long eventId,ModelMap map, HttpServletRequest request)
			throws Exception {

		String formKey = "showSeatDetailsDto";
		ShowSeatDetailsDto showSeatDetailsDto = commonResponseUtil.handleModalFormError(request, map, formKey,ShowSeatDetailsDto.class);

		if (Objects.nonNull(id) && id > 0) {
			showSeatDetailsDto = showSeatDetailsService.getShowSeatDetailsById(id);
		}

		List<ShowEventMasterDto> eventList = showEventMasterService.getActiveEventMasterList();
		map.addAttribute("eventList", eventList);
		if (eventId > 0) {
			List<ShowMasterDto> showList = showMasterService.getShowNameList(eventId);
			map.addAttribute("showList", showList);
		}
		map.addAttribute(formKey, showSeatDetailsDto);
		return HTMLPage.ADD_EDIT_SHOW_SEAT;
	}

	@PostMapping
	public String saveUpdateShowSeat(@Valid @ModelAttribute ShowSeatDetailsDto showSeatDetailsDto,BindingResult bindingResult,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		String saveStatus = null;
		if (bindingResult.hasErrors()) {
			commonResponseUtil.updateModalFormErrorAttributes(redirectAttrs, bindingResult, showSeatDetailsDto);
			return "redirect:" + showSeatDetails;
		}
		ShowSeatDetailsDto dto = showSeatDetailsService.saveUpdateShowSeat(showSeatDetailsDto);
		if(dto != null) {
		saveStatus = (showSeatDetailsDto.getId()!=null && showSeatDetailsDto.getId() > 0) ? Constants.UPDATED : Constants.SAVED;
		commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs);
		redirectAttrs.addFlashAttribute("dto", dto);
		Long showId = dto.getShow().getId();
		Long eventId = dto.getShow().getShowEventMaster().getId();
	    redirectAttrs.addFlashAttribute("showId", showId);
	    redirectAttrs.addFlashAttribute("eventId", eventId);
	}
		return Constants.REDIRECT + showSeatDetails;
	}
	
	@GetMapping("${url.show.name.list}" + "${id}")
	public @ResponseBody List<ShowMasterDto> getShowNameList(@PathVariable long id,
			HttpServletRequest request) {
		return showMasterService.getShowNameList(id);
	}

}
