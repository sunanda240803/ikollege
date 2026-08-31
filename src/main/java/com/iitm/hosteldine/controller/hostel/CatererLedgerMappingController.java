package com.iitm.hosteldine.controller.hostel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.hostel.CatererLedgerMappingDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.UserManagementService;
import com.iitm.hosteldine.service.hostel.AccountHeadService;
import com.iitm.hosteldine.service.hostel.CatererLedgerMappingService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.hostel.CatererLedgerMappingValidator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.caterer.ledger.mapping}")
public class CatererLedgerMappingController {

	private final CatererLedgerMappingValidator catererLedgerMappingValidator;
	private final CatererLedgerMappingService catererLedgerMappingService;
	private final UserManagementService userManagementService;
	private final AccountHeadService accountHeadService;
	private final CommonResponseUtil commonResponseUtil;

	@Value("${url.caterer.ledger.mapping}")
	private String getCatererLedgerMapping;

	@GetMapping
	public String getCatererLedgerMappingList(PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
	    populateModelAttributes(form, map, request, new CatererLedgerMappingDto());
	    return HTMLPage.CATERER_LEDGER_MAPPING;
	}

	@PostMapping
	public String saveAndUpdate(@ModelAttribute CatererLedgerMappingDto dto, BindingResult bindingResult,
	                            @ModelAttribute PaginationForm form, ModelMap map, HttpServletRequest request,
	                            RedirectAttributes redirectAttrs) throws Exception {
	    catererLedgerMappingValidator.validate(dto, bindingResult);

	    if (bindingResult.hasErrors()) {
	        populateModelAttributes(form, map, request, dto);
	        return HTMLPage.CATERER_LEDGER_MAPPING;
	    }

	    String saveStatus = catererLedgerMappingService.saveAndUpdate(dto);
	    commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs);
	    return Constants.REDIRECT + getCatererLedgerMapping;
	}

	private void populateModelAttributes(PaginationForm form, ModelMap map, HttpServletRequest request, CatererLedgerMappingDto dto) throws Exception {
	    Page<CatererLedgerMappingDto> catererLedgerMappingList = catererLedgerMappingService.getCatererLedgerMappingList(form);
	    map.addAttribute("userList", userManagementService.getUserList());
	    dto.setCatererAccountHeadList(accountHeadService.getCatererAccountHeadList());
	    map.addAttribute("catererLedgerMappingDto", dto);
	    commonResponseUtil.updateCommonModelAttributes(map, request, catererLedgerMappingList, form);
	}

	@DeleteMapping
	public @ResponseBody BaseResponse deleteHostelRoomInfoById(@RequestBody CatererLedgerMappingDto dto, ModelMap map,
			HttpServletRequest request) throws Exception {
		return CommonResponseUtil.generateDeleteResponseByStatus(catererLedgerMappingService.deleteMappingById(dto));
	}

}
