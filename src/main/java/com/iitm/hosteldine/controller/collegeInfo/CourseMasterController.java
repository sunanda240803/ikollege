package com.iitm.hosteldine.controller.collegeInfo;

import java.util.List;
import java.util.Map;

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
import org.springframework.web.servlet.support.RequestContextUtils;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.collegeInfo.CourseMasterDto;
import com.iitm.hosteldine.dto.collegeInfo.DepartmentDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.collegeInfo.CourseMasterService;
import com.iitm.hosteldine.service.collegeInfo.DepartmentService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.course.master}")
public class CourseMasterController {
	@Value("${url.course.master}")
	private String getCourseMasterList;
	private final CourseMasterService courseMasterService;
	private final DepartmentService departmentService;
	private final MessageSource messageSource;
	private final CommonResponseUtil commonResponseUtil;

	@GetMapping
	public String courseMasterDetails(PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
		//List<CourseMasterDto> courseMasterList = courseMasterService.getCourseMasterList(new CourseMasterDto());
		//map.addAttribute("courseMasterList", courseMasterList);
		Page<CourseMasterDto> courseMasterList = courseMasterService.getCourseMasterList(form);
		List<DepartmentDto> departmentList = departmentService.getDepartmentNameList();
		map.addAttribute("departmentNameList", departmentList);
		commonResponseUtil.updateCommonModelAttributes(map, request,courseMasterList,form);
		return HTMLPage.COURSE_MASTER;
	}

	@GetMapping("${id}")
	public String getHostelDetailsById(@PathVariable long id, ModelMap map, HttpServletRequest request)
			throws Exception {
		CourseMasterDto courseMasterDto = courseMasterService.getCourseMasterDetailsById(id);
		if (courseMasterDto != null) {
			map.addAttribute("courseMasterDto", courseMasterDto);
		} else {
			map.addAttribute("courseMasterDto", new CourseMasterDto());
		}
		List<DepartmentDto> departmentList = departmentService.getDepartmentNameList();
		map.addAttribute("departmentNameList", departmentList);
		return HTMLPage.ADD_EDIT_COURSE_MASER;
	}

	@PostMapping
	public String saveUpdateCourseMaster(ModelMap map, @ModelAttribute CourseMasterDto courseMasterDto,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		BaseResponse baseResponse = new BaseResponse();
		if (courseMasterDto.getCourseMasterId() == null) {
			courseMasterDto.setCourseMasterId(0l);
		}
		String saveStatus = courseMasterService.saveUpdateCourseMaster(courseMasterDto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus,redirectAttrs);
		return Constants.REDIRECT + getCourseMasterList;
		
	}

	@DeleteMapping("${id}")
	public @ResponseBody BaseResponse deleteCourseMaster(ModelMap map, @PathVariable long id, HttpServletRequest request,
			RedirectAttributes redirectAttrs) throws Exception {
		try {
			return CommonResponseUtil.generateDeleteResponseByStatus(courseMasterService.deleteCourseMasterById(id));
		} catch (RecordNotExistsException e) {
			// Create a custom error response in case of an exception
			BaseResponse errorResponse = new BaseResponse();
			errorResponse.setMessage(e.getMessage());
			errorResponse.setStatus("Failure");
			return errorResponse;
		}
	}

	@GetMapping("${url.course.name.exist}" + "${courseName}" + "${id}")
	public @ResponseBody boolean checkCourseMasterNameExist(@PathVariable String courseName,
			@PathVariable long id, ModelMap map, HttpServletRequest request) {
		return courseMasterService.checkCourseMasterNameExist(courseName, id);
	}
	
	@GetMapping("${url.course.code.exist}" +  "${courseCode}" +"${id}")
	public @ResponseBody boolean checkCourseCodeExist(@PathVariable String courseCode,@PathVariable long id, ModelMap map, HttpServletRequest request) {
		return courseMasterService.checkCourseCodeExist(courseCode,id);
	}

}
