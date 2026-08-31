package com.iitm.hosteldine.controller.hostel;

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
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.hostel.master}")
public class HostelMasterController {

	private final HostelMasterService hostelMasterService;
	private final CommonResponseUtil commonResponseUtil;
	
	@Value("${url.hostel.master}")
	private String hostelMaster;

	@GetMapping
	public String getHostelList(PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		//map.addAttribute("hostelList", hostelMasterService.getHostelList());
		Page<HostelMasterDto> hostelList = hostelMasterService.getHostelList(form);
		commonResponseUtil.updateCommonModelAttributes(map, request ,hostelList , form);
		return HTMLPage.HOSTEL_MASTER;
	}

	@GetMapping("${id}")
	public String getHostelDetailsById(@PathVariable long id, ModelMap map, HttpServletRequest request)
			throws Exception {
		HostelMasterDto hostelDto = hostelMasterService.getHostelDetailsById(id);
		map.addAttribute("hostelMasterDto", hostelDto);
		return HTMLPage.ADD_EDIT_HOSTEL_MASTER;
	}

	@PostMapping
	public String saveUpdateHostel(ModelMap map, @ModelAttribute HostelMasterDto hostelMasterDto,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		String saveStatus = hostelMasterService.saveUpdateHostel(hostelMasterDto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus,redirectAttrs);
		return Constants.REDIRECT + hostelMaster;
	}

	
	@DeleteMapping("/{id}")
	public @ResponseBody BaseResponse deleteHostel(@PathVariable long id) throws Exception {
	    try {
	        boolean deleteStatus = hostelMasterService.deleteHostelById(id);
	        return CommonResponseUtil.generateDeleteResponseByStatus(deleteStatus);
	    } catch (RecordNotExistsException e) {
	        // Create a custom error response in case of an exception
	        BaseResponse errorResponse = new BaseResponse();
	        errorResponse.setMessage(e.getMessage());  
	        errorResponse.setStatus("Failure");        
	        return errorResponse;
	    }
	}
	
	@GetMapping("${url.hostel.name.exist}" + "${hostelName}" + "${id}")
	public @ResponseBody boolean checkHostelNameExist(@PathVariable String hostelName, @PathVariable long id,
			ModelMap map, HttpServletRequest request) {
		return hostelMasterService.checkHostelNameExist(hostelName, id);
	}

	@GetMapping("${url.hostel.code.exist}" + "${hostelCode}" + "${id}")
	public @ResponseBody boolean checkHostelCodeExist(@PathVariable String hostelCode, @PathVariable long id,
			ModelMap map, HttpServletRequest request) {
		return hostelMasterService.checkHostelCodeExist(hostelCode, id);
	}

}
