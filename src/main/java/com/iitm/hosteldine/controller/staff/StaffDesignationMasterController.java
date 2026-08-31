package com.iitm.hosteldine.controller.staff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.iitm.hosteldine.dto.staff.StaffDesignationMasterDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.service.staff.DesignationMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.designation.master}")
public class StaffDesignationMasterController {
	
	@Value("${url.designation.master}")
	private String designationMaster;
	@Value("${url.list}")
	private String getList;;
	private final DesignationMasterService designationMasterService;
	private final CommonResponseUtil commonResponseUtil;
	private final MessageSource messageSource;

	@GetMapping
	public @ResponseBody List<StaffDesignationMasterDto> designationMasterDetails(ModelMap map, HttpServletRequest request) throws Exception {
		List<StaffDesignationMasterDto> designationList = designationMasterService.getDesignationList();
		return designationList;
	}

	@GetMapping("${id}")
	public String getDesignationMasterById(@PathVariable long id, ModelMap map, HttpServletRequest request)
			throws Exception {
		StaffDesignationMasterDto staffDesignationMasterDto;
		if (id > 0) {
			staffDesignationMasterDto = designationMasterService.getDesignationMasterById(id);
		} else {
			staffDesignationMasterDto = new StaffDesignationMasterDto();
		}
		map.addAttribute("staffDesignationMasterDto", staffDesignationMasterDto);
		return HTMLPage.ADD_EDIT_DESIGNATION_MASER;
	}
	
	
	
	@PostMapping
	public String saveUpdateDesignation(ModelMap map, @ModelAttribute StaffDesignationMasterDto staffDesignationMasterDto,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		String saveStatus = designationMasterService.saveUpdateDesignation(staffDesignationMasterDto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus,redirectAttrs);
		return Constants.REDIRECT + designationMaster;
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteDesignationMaster(@PathVariable long id) throws Exception {
		try {
			Map<String, List<?>> hashMap = new HashMap<>();
			Boolean status = designationMasterService.deleteDesignationMasterById(id);
			if (status) {
				hashMap.put("Success", designationMasterService.getDesignationList());
				return ResponseEntity.status(HttpStatus.OK).body(hashMap);
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete Department");
			}
		} catch (RecordNotExistsException e) {
			// Create a custom error response in case of an exception
			BaseResponse errorResponse = new BaseResponse();
			errorResponse.setMessage(e.getMessage());
			errorResponse.setStatus("Failure");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
		}
	}


	@GetMapping("${url.designation.name.exist}" + "${designationName}" + "/{id}")
	public @ResponseBody boolean checkDesignationNameExist(@PathVariable String designationName, @PathVariable long id,
			ModelMap map, HttpServletRequest request) {
		return designationMasterService.checkdesignationNameExist(designationName, id);
	}

}
