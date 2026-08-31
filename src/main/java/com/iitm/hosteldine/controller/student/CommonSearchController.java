package com.iitm.hosteldine.controller.student;

import java.util.Map;
import java.util.Objects;

import com.iitm.hosteldine.dto.student.StudentSearchViewDto;
import com.iitm.hosteldine.util.response.BaseResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.student.AllStudentsDetailsViewDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.student.CommonSearchService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("${url.common.search}")
public class CommonSearchController {

	private final CommonResponseUtil commonResponseUtil;
	private final SimsConfigDataService simsConfigDataService;
	private final CommonSearchService commonSearchService;
	private final StudentDetailsInfoService studentDetailsInfoService;

	@Value("${url.common.search}")
	private String commonSearchUrl;

	@GetMapping
	public String getStudentDetails(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap model,
	        HttpServletRequest request) throws Exception {
	    String studentSearchViewDtoKey = "studentDetailsViewDto";
	    String searchCriteriaKey = "searchCriteria";
	    String fieldKey = "field";
	    String searchStringKey = "searchString";

	    Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
	    StudentSearchViewDto studentsSearchViewDto = flashInputMap != null
	            && flashInputMap.get(studentSearchViewDtoKey) != null
	            ? (StudentSearchViewDto) flashInputMap.get(studentSearchViewDtoKey)
	            : new StudentSearchViewDto();

	    Page<StudentSearchViewDto> studentSearchViewDtos = null;
	    commonResponseUtil.getAdditionalParams(allParams, form);
	    Object searchCriteriaObj = form.getAdditionalParam().get(searchCriteriaKey);
	    Object fieldObj = form.getAdditionalParam().get(fieldKey);

	    if (isValid(searchCriteriaObj) && isValid(fieldObj)) {
	        studentSearchViewDtos = commonSearchService.getStudentDetailsList(form);
	        studentsSearchViewDto.setSearchCriteria(searchCriteriaObj.toString());
	        studentsSearchViewDto.setField(fieldObj.toString());
	        studentsSearchViewDto.setSearchString(form.getAdditionalParam().get(searchStringKey).toString());
	    } else if (studentsSearchViewDto.getSearchCriteria() != null
	            && !studentsSearchViewDto.getSearchCriteria().isEmpty()
	            && studentsSearchViewDto.getField() != null && !studentsSearchViewDto.getField().isEmpty()) {

	        form.getAdditionalParam().put(searchCriteriaKey, studentsSearchViewDto.getSearchCriteria());
	        form.getAdditionalParam().put(fieldKey, studentsSearchViewDto.getField());
	        form.getAdditionalParam().put(searchStringKey, studentsSearchViewDto.getSearchString());

	        studentSearchViewDtos = commonSearchService.getStudentDetailsList(form);
	    } else {
	        form.getAdditionalParam().put(searchCriteriaKey, "");
	        form.getAdditionalParam().put(fieldKey, "");
	        form.getAdditionalParam().put(searchStringKey, "");
	    }

	    model.addAttribute("fieldList", simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.COMMON_SEARCH_FIELD));
	    model.addAttribute(studentSearchViewDtoKey, studentsSearchViewDto);
	    model.addAttribute("roleId",Objects.requireNonNull(SecurityCtxUtil.userRole()));
	    commonResponseUtil.updateCommonModelAttributes(model, request, studentSearchViewDtos, form);
	    return HTMLPage.COMMON_SEARCH;
	}

	@PostMapping("${url.save}")
	public String saveOrUpdate(@ModelAttribute AllStudentsDetailsViewDto allStudentsDetailsViewDto,
			RedirectAttributes redirectAttrs, HttpServletRequest request) throws Exception {
		String saveStatus = studentDetailsInfoService.saveVacatingDayScholarDetails(allStudentsDetailsViewDto);
		redirectAttrs.addFlashAttribute("allStudentsDetailsViewDto", allStudentsDetailsViewDto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs);
		return Constants.REDIRECT + commonSearchUrl;
	}

    @PostMapping("${url.update}" + "${studentId}" + "${status}")
    public @ResponseBody BaseResponse updateStatus(@PathVariable String studentId, @PathVariable String status) {
        BaseResponse baseResponse = new BaseResponse();
        String result = commonSearchService.updateStudentStatus(studentId, status);
        baseResponse.setStatus(Constants.UPDATED.equals(result) ? Constants.SUCCESS : Constants.ERROR);
        baseResponse.setMessage(Constants.UPDATED.equals(result) ? commonResponseUtil.getMessage("message.student.status.update.success") : commonResponseUtil.getMessage("message.student.status.update.failure"));
        return baseResponse;
    }

	private boolean isValid(Object value) {
		if (value == null) {
			return false;
		}
		if (value instanceof String) {
			String strValue = (String) value;
			return !strValue.trim().isEmpty();
		}
		return false;
	}

}
