package com.iitm.hosteldine.controller.collegeInfo;

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
import com.iitm.hosteldine.dto.collegeInfo.DepartmentDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.collegeInfo.DepartmentService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.course.master}" + "${url.department}")
public class DepartmentController {
	@Value("${url.course.master}")
	private String getCourseMasterList;
	private final DepartmentService departmentService;
	private final MessageSource messageSource;
	private final CommonResponseUtil commonResponseUtil;

	@GetMapping
	public String departmentDetails(ModelMap map, HttpServletRequest request) throws Exception {
		List<DepartmentDto> departmentList = departmentService.getDepartmentList();
		map.addAttribute("departmentList", departmentList);
		commonResponseUtil.updateCommonModelAttributes(map, request);
		return HTMLPage.COURSE_MASTER;
	}

	@GetMapping("/{id}")
	public String getDepartmentDetailsById(@PathVariable long id, ModelMap map, HttpServletRequest request)
			throws Exception {
		DepartmentDto departmentDto = departmentService.getDepartmentDetailsById(id);
		map.addAttribute("departmentDto", departmentDto);
		return HTMLPage.ADD_EDIT_COURSE_MASER;
	}

	@PostMapping
	public String saveUpdateDepartment(ModelMap map, @ModelAttribute DepartmentDto departmentDto,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		String saveStatus = departmentService.saveUpdateDepartment(departmentDto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus,redirectAttrs);
		return Constants.REDIRECT + getCourseMasterList;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteDepartment(ModelMap map, @PathVariable long id, HttpServletRequest request,
			RedirectAttributes redirectAttrs) throws Exception {
		try {
			Map<String, List<?>> hashMap = new HashMap<>();
			Boolean status = departmentService.deleteDepartmentById(id);
			if (status) {
				hashMap.put("Success", departmentService.getDepartmentList());
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
	    }
		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
		}
		

	}

	@GetMapping("${url.department.name.exist}" + "${departmentName}" + "${id}")
	public @ResponseBody boolean checkDepartmentNameExist(@PathVariable String departmentName, @PathVariable long id,
			ModelMap map, HttpServletRequest request) {
		return departmentService.checkDepartmentNameExist(departmentName, id);
	}

}
