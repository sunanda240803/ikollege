package com.iitm.hosteldine.controller.hostel;

import java.util.List;

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
import com.iitm.hosteldine.dto.hostel.HostelFloorMasterDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.HostelFloorMasterService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.floor.master}")
public class HostelFloorMasterController {

	private final HostelMasterService hostelMasterService;
	private final HostelFloorMasterService hostelFloorMasterService;
	private final CommonResponseUtil commonResponseUtil;

	@Value("${url.floor.master}")
	private String floorMaster;

	@GetMapping
	public String getFloorList(PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		//map.addAttribute("floorList", hostelFloorMasterService.getAllFloorList());
		Page<HostelFloorMasterDto> floorList = hostelFloorMasterService.getAllFloorList(form);
		commonResponseUtil.updateCommonModelAttributes(map, request,floorList,form);
		return HTMLPage.FLOOR_MASTER;
	}

	@GetMapping("${id}")
	public String getFloorDetailsById(@PathVariable long id, ModelMap map, HttpServletRequest request)
			throws Exception {
		HostelFloorMasterDto floorDto = hostelFloorMasterService.getFloorDetailsById(id);
		map.addAttribute("hostelFloorMasterDto", floorDto);

		// Hostel List for dropdown
		List<HostelMasterDto> hostelList = hostelMasterService.getHostelList();
		map.addAttribute("hostelList", hostelList);

		return HTMLPage.ADD_EDIT_FLOOR_MASTER;
	}

	@PostMapping
	public String saveUpdateFloor(ModelMap map, @ModelAttribute HostelFloorMasterDto hostelFloorMasterDto,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		String saveStatus = hostelFloorMasterService.saveUpdateFloor(hostelFloorMasterDto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus,redirectAttrs);
		return Constants.REDIRECT + floorMaster;
	}

	@DeleteMapping("${id}")
	public @ResponseBody BaseResponse deleteFloor(@PathVariable long id, ModelMap map, HttpServletRequest request,
			RedirectAttributes redirectAttrs) throws Exception {
		 try {
		        boolean deleteStatus = hostelFloorMasterService.deleteFloorById(id);
		        return CommonResponseUtil.generateDeleteResponseByStatus(deleteStatus);
		    } catch (RecordNotExistsException e) {
		        // Create a custom error response in case of an exception
		        BaseResponse errorResponse = new BaseResponse();
		        errorResponse.setMessage(e.getMessage());  
		        errorResponse.setStatus("Failure");        
		        return errorResponse;
		    }
	}

	@GetMapping("${url.floor.name.exist}" + "${floorName}" + "${hostelId}" + "${id}")
	public @ResponseBody boolean checkHostelNameExist(@PathVariable String floorName, @PathVariable long hostelId,
			@PathVariable long id, ModelMap map, HttpServletRequest request) {
		return hostelFloorMasterService.checkFloorNameExist(floorName, hostelId, id);
	}

}
