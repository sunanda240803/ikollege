package com.iitm.hosteldine.controller.warden;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.warden.GuestAccommodationChargesDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.warden.GuestAccommodationChargesService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.guest.accomm.charges}")
public class GuestAccommodationChargesController {
	
	private final GuestAccommodationChargesService guestAccommodationService;
	private final CommonResponseUtil commonResponseUtil;
	
	@Value("${url.guest.accomm.charges}")
	private String guestAccommodationCharges;

	@GetMapping
	public String getGuestAccommodationList(PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		Page<GuestAccommodationChargesDto> guestAccommodationList = guestAccommodationService.getGuestAccommodationList(form);
		commonResponseUtil.updateCommonModelAttributes(map, request ,guestAccommodationList , form);
		return HTMLPage.GUEST_ACCOMMODATION;
	}
	
	@GetMapping("${id}")
	public String getGuestAccommodationDetailsById(@PathVariable long id, ModelMap map, HttpServletRequest request)
			throws Exception {
		GuestAccommodationChargesDto guestDto = guestAccommodationService.getGuestAccommodationDetailsById(id);
		map.addAttribute("guestAccommodationDto", guestDto);
		return HTMLPage.ADD_EDIT_GUEST_ACCOMMODATN_CHARGE;
	}
	
	@PostMapping
	public String saveUpdateGuestAccommodationCharge(ModelMap map, @ModelAttribute GuestAccommodationChargesDto guestAccommodationChargesDto,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		String saveStatus = guestAccommodationService.saveUpdateGuestAccommodationCharge(guestAccommodationChargesDto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus,redirectAttrs);
		return Constants.REDIRECT + guestAccommodationCharges;
	}

}
