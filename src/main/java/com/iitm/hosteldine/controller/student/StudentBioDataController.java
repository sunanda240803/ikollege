package com.iitm.hosteldine.controller.student;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.iitm.hosteldine.util.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.StudentBioDataFamilyInfoDto;
import com.iitm.hosteldine.dto.student.AllStudentsDetailsViewDto;
import com.iitm.hosteldine.dto.student.StudentDetailsDto;
import com.iitm.hosteldine.dto.student.wellness.StudentWellnessCategoricalDataDto;
import com.iitm.hosteldine.service.AllStudentsDetailsViewService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.service.student.wellness.StudentWellnessArchiveService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("${url.admin.student.bio.data}")
public class StudentBioDataController {

    private final CommonResponseUtil commonResponseUtil;
    private final StudentBioDataService studentBioDataService;
	private final AllStudentsDetailsViewService allStudentsDetailsViewService;
	private final SimsConfigDataService simsConfigDataService;
	private final MessageSource messageSource;
	private final StudentWellnessArchiveService studentWellnessArchiveService;

    @Value("${url.admin.student.bio.data}")
    private String studentBioDataUrl;

    @GetMapping
	public String getStudentDetails(ModelMap model, HttpServletRequest request) throws Exception {
		String mappedDto = "studentDto";
		String studentIdKey = "studentId";
		String searchStringKey = "searchString";
		String categoryKey = "category";
		String wellnessDataKey = messageSource.getMessage("message.label.data", null, Locale.getDefault());
		StudentDetailsDto dto = new StudentDetailsDto();
		Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
		String studentId = flashInputMap != null && flashInputMap.get(studentIdKey) != null
				? (String) flashInputMap.get(studentIdKey)
				: null;
		String searchString = flashInputMap != null && flashInputMap.get(searchStringKey) != null
				? (String) flashInputMap.get(searchStringKey)
				: null;
		String category = flashInputMap != null && flashInputMap.get(categoryKey) != null
				? (String) flashInputMap.get(categoryKey)
				: null;
		String wellnessData = flashInputMap != null && flashInputMap.get(wellnessDataKey) != null
				? (String) flashInputMap.get(wellnessDataKey)
				: null;

		setRole(SecurityCtxUtil.userRole(), model);
	    String studentCategory = messageSource.getMessage("message.label.category.students", null, Locale.getDefault());

	    if (studentId != null && (category == null || studentCategory.equals(category))) {
	        dto = studentBioDataService.getFullStudentDetails(studentId);
	        if (dto.getImageBytes() != null) {
	            model.addAttribute("studentProfile", Base64.getEncoder().encodeToString(dto.getImageBytes()));
	        }
	    }

	    dto.setCategory(category);
	    dto.setStudentId(searchString!=null ? searchString : studentId);
	    if (dto.getFamilyDetails() == null) {
	        dto.setFamilyDetails(new ArrayList<>());
	        dto.getFamilyDetails().add(new StudentBioDataFamilyInfoDto());
	    }
	    model.addAttribute(mappedDto, dto);
	    model.addAttribute(categoryKey, category);
	    model.addAttribute(studentIdKey, searchString);
	    model.addAttribute(messageSource.getMessage("message.label.data", null, Locale.getDefault()), wellnessData);
	    model.addAttribute("roleId",Objects.requireNonNull(SecurityCtxUtil.userRole()));
	    if(Objects.requireNonNull(SecurityCtxUtil.userRole()).equals("SoftwareAdmin")){
	    	model.addAttribute("edit",true);
	    }else {
	    	model.addAttribute("edit",false);
	    }
        String guideEmail = simsConfigDataService.getSimConfigValue(SimsConfigDataService.GUIDE_EMAIL);
        model.addAttribute("guideEmail",guideEmail);
		LocalDate dobMax = LocalDate.of(LocalDate.now().getYear() - 16, 12, 31);
        model.addAttribute("dobMax", java.sql.Date.valueOf(dobMax));
	    model.addAttribute("proofTypeList", simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.PROOF_TYPES));
	    commonResponseUtil.updateCommonModelAttributes(model, request);
	    return HTMLPage.VIEW_STUDENT_BIO_DATA;
	}

//	@PostMapping(value = { "${studentId}", "${category}" + "${studentId}" })
	@PostMapping({"/{studentId}", "/{category}/{studentId}"})
	public String handleStudentBioDataSubmission(@PathVariable String studentId,
			@PathVariable(required = false) String category, ModelMap model, RedirectAttributes redirectAttrs)
			throws Exception {
		AllStudentsDetailsViewDto dto = null;
		String studentIdKey = "studentId";
		String student = studentId.toUpperCase();
		if (student != null) {
			dto = allStudentsDetailsViewService.getCompleteStudentDetails(student);
		}

		if (dto != null && dto.getBioDataId() != null) {
			if (category == null) {
				redirectAttrs.addFlashAttribute(studentIdKey, student);
				commonResponseUtil.dynamicResponseByStatus(ModelConstants.SUCCESS, redirectAttrs, "response.data.available");
			} else if (messageSource.getMessage("message.label.category.students", null, Locale.getDefault()).equals(category)) {
				redirectAttrs.addFlashAttribute(studentIdKey, student);
				redirectAttrs.addFlashAttribute("category", category);
				commonResponseUtil.dynamicResponseByStatus(ModelConstants.SUCCESS, redirectAttrs, "response.data.available");

				StudentWellnessCategoricalDataDto studentWellnessDto = null;
				studentWellnessDto = studentWellnessArchiveService.getWellnessDetailsByStudentId(student);
				if (studentWellnessDto != null) {
					redirectAttrs.addFlashAttribute(
							messageSource.getMessage("message.label.data", null, Locale.getDefault()),
							messageSource.getMessage("message.label.exist", null, Locale.getDefault()));
				} else {
					redirectAttrs.addFlashAttribute(
							messageSource.getMessage("message.label.data", null, Locale.getDefault()),
							messageSource.getMessage("message.label.new", null, Locale.getDefault()));
				}
			} else if (messageSource.getMessage("message.label.category.others", null, Locale.getDefault()).equals(category)) {
				redirectAttrs.addFlashAttribute("category", category);
				commonResponseUtil.dynamicResponseByStatus(ModelConstants.FAILURE, redirectAttrs,
						"response.student.id.already.exist.in.the.system");
			}
		} else if (messageSource.getMessage("message.label.category.others", null, Locale.getDefault()).equals(category)) {
			StudentWellnessCategoricalDataDto studentWellnessDto = null;
			studentWellnessDto = studentWellnessArchiveService.getWellnessDetailsByStudentId(student);
			redirectAttrs.addFlashAttribute("category", category);
			if (studentWellnessDto != null) {
				redirectAttrs.addFlashAttribute(
						messageSource.getMessage("message.label.data", null, Locale.getDefault()),
						messageSource.getMessage("message.label.exist", null, Locale.getDefault()));
				commonResponseUtil.dynamicResponseByStatus(ModelConstants.SUCCESS, redirectAttrs,
						"response.student.already.has.wellness.data");
			} else {
				redirectAttrs.addFlashAttribute(
						messageSource.getMessage("message.label.data", null, Locale.getDefault()),
						messageSource.getMessage("message.label.new", null, Locale.getDefault()));
				commonResponseUtil.dynamicResponseByStatus(ModelConstants.FAILURE, redirectAttrs,
						"response.wellness.data.not.found");
			}
		} else {
			redirectAttrs.addFlashAttribute("category", category);
			commonResponseUtil.dynamicResponseByStatus(ModelConstants.FAILURE, redirectAttrs,
					"response.student.id.does.not.exist");
		}
		redirectAttrs.addFlashAttribute("searchString", student);
		return Constants.REDIRECT + studentBioDataUrl;
	}

	@GetMapping("${url.pdf.download}" + "${studentId}" + "${id}")
	public ResponseEntity<Resource> downloadStudentBioDataPDF(@PathVariable String studentId,
															  @PathVariable("id") Boolean needDeclaration) throws Exception {
		Resource resource = studentBioDataService.getStudentBioDataPDF(studentId.toUpperCase(),needDeclaration);
		return Utility.prepareDownloadFile(resource);
	}

	private void setRole(String userRole, ModelMap model) {
		String wellnessRoles = simsConfigDataService.getSimConfigValue(SimsConfigDataService.WELLNESS_ROLES);
		if (userRole != null && !userRole.isEmpty()) {
			String[] roleParts = userRole.split(ModelConstants.UNDERSCORE);
			String role = roleParts[roleParts.length - 1];

			List<String> wellnessRoleList = Arrays.asList(wellnessRoles.split(ModelConstants.COMMA));
			if (wellnessRoleList.contains(role)) {
				model.addAttribute("role", ModelConstants.STATUS_ACTIVE);
			}
		}
	}
	
	@PostMapping("${url.update}")
    public String updateStudentBioData(@Valid @ModelAttribute StudentDetailsDto studentDetailsDto,
                              BindingResult bindingResult, RedirectAttributes redirectAttributes,ModelMap map, HttpServletRequest request) throws Exception {
		studentDetailsDto.getAllStudentsDetailsViewDto().setBloodGroup(studentDetailsDto.getBloodGroup());
        String status = studentBioDataService.updateStudentBioData(studentDetailsDto);
        String message = Objects.nonNull(status) && Constants.SAVED.equals(status) ? "message.student.biodata.update" : "message.student.biodata.failure";
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, message);
        redirectAttributes.addAttribute("studentId", studentDetailsDto.getStudentId());
        redirectAttributes.addFlashAttribute("studentId", studentDetailsDto.getStudentId());
        return Constants.REDIRECT + studentBioDataUrl;
    }
}
