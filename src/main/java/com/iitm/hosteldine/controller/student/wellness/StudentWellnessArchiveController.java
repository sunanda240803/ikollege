package com.iitm.hosteldine.controller.student.wellness;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.student.AllStudentsDetailsViewDto;
import com.iitm.hosteldine.dto.student.wellness.StudentWellnessCategoricalDataDto;
import com.iitm.hosteldine.dto.student.wellness.StudentWellnessDto;
import com.iitm.hosteldine.dto.student.wellness.StudentWellnessFollowupDataDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.AllStudentsDetailsViewService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.student.wellness.StudentWellnessArchiveService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.ValidationConstants;
import com.iitm.hosteldine.validator.wellness.WellnessArchiveValidator;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("${student.wellness.archive}")
public class StudentWellnessArchiveController {

	private final MessageSource messageSource;
    private final CommonResponseUtil commonResponseUtil;
	private final SimsConfigDataService simsConfigDataService;
    private final StudentWellnessArchiveService studentWellnessArchiveService;
    private final AllStudentsDetailsViewService allStudentsDetailsViewService;
    private final WellnessArchiveValidator wellnessArchiveValidator;

	@Value("${student.wellness.archive}")
	private String baseUrl;
	@Value("${wellness.details}")
	private String wellnessDetailsUrl;	

    @GetMapping
	public String getStudentWellnessList(@RequestParam Map<String, String> allParams, PaginationForm form,
			ModelMap model, HttpServletRequest request) throws Exception {

        String[] keys = StudentWellnessDto.PARAM_KEYS;

//        Page<StudentWellnessCategoricalDataDto> studentWellnessCategoricalDataDto = null;
        List<StudentWellnessCategoricalDataDto> studentWellnessCategoricalDataDto = null;
        commonResponseUtil.getAdditionalParams(allParams, form);

        Map<String, Object> additionalParams = form.getAdditionalParam();
        Map<String, Object> extractedParams = new HashMap<>();

        for (String key : keys) {
            extractedParams.put(key, additionalParams.get(key));
        }

        StudentWellnessDto dto = new StudentWellnessDto();

        if (extractedParams.values().stream().anyMatch(this::isValid)) {
            studentWellnessCategoricalDataDto = studentWellnessArchiveService.getStudentWellnessList(form);
            dto = studentWellnessArchiveService.getDecryptFilterData(form);
        } else {
            Arrays.stream(keys).forEach(key -> additionalParams.put(key, ""));
            studentWellnessCategoricalDataDto = studentWellnessArchiveService.getStudentWellnessList(form);
        }

//	    commonResponseUtil.updateCommonModelAttributes(model, request, studentWellnessCategoricalDataDto, form);
	    commonResponseUtil.updateCommonModelAttributes2(model, request, studentWellnessCategoricalDataDto, form);
		HeaderForm headerForm = (HeaderForm) model.getAttribute(ModelConstants.HEADER_FORM);
		if (headerForm != null) {
			headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_PINK,
					messageSource.getMessage("message.label.wellness.report", null, Locale.getDefault()),
					ModelConstants.FA_FILE_EXCEL);
		}
		
	    model.addAttribute("referralTypeList", simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.REFERRAL_TYPE));
	    model.addAttribute("concernTypeList", simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.TYPE_OF_CONCERN));
	    model.addAttribute("departmentList", studentWellnessArchiveService.getDepartmentCodeList());
	    model.addAttribute("studentWellnessDto", dto);
		model.addAttribute("saveButton", Constants.DISABLE);
		model.addAttribute("addNew", Constants.DISABLE);
		model.addAttribute("USE_DATATABLES", true);
	    return HTMLPage.WELLNESS_ARCHIVE;
    }

	@GetMapping("${url.download.student.wellness.report}")
	public void downloadAllStudentsDetailsViewReport(@RequestParam Map<String, String> allParams, PaginationForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		commonResponseUtil.getAdditionalParams(allParams, form);
		Workbook workbook = studentWellnessArchiveService.downloadStudentWellnessReport(form);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		workbook.close();
		byte[] excelBytes = bos.toByteArray();
		byte[] encryptedBytes = encryptExcel(excelBytes, studentWellnessArchiveService.retrieveExcelPassword());

		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=Wellness Report.xlsx");
		response.setContentLength(encryptedBytes.length);
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			outputStream.write(encryptedBytes);
			outputStream.flush();
		}
	}

	private byte[] encryptExcel(byte[] data, String password) throws Exception {

		POIFSFileSystem fs = new POIFSFileSystem();
		EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);
		Encryptor encryptor = info.getEncryptor();
		encryptor.confirmPassword(password);

		try (OPCPackage opc = OPCPackage.open(new ByteArrayInputStream(data));
			 OutputStream os = encryptor.getDataStream(fs)) {
			opc.save(os);
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		fs.writeFilesystem(bos);

		return bos.toByteArray();
	}

	@GetMapping("${category}")
	public String getWellnessDetails(@PathVariable String category, @RequestParam String studentId, ModelMap model,
			HttpServletRequest request) throws Exception {
		category = category.equals("students")
				? messageSource.getMessage("message.label.category.students", null, Locale.getDefault())
				: messageSource.getMessage("message.label.category.others", null, Locale.getDefault());
		commonResponseUtil.updateCommonModelAttributes(model, request);
		HeaderForm headerForm = (HeaderForm) model.getAttribute(ModelConstants.HEADER_FORM);
		StudentWellnessCategoricalDataDto studentWellnessCategoricalDataDto = studentWellnessArchiveService
				.getWellnessDetailsByStudentId(studentId);
		AllStudentsDetailsViewDto studentDetails = allStudentsDetailsViewService.getCompleteStudentDetails(studentId);

		if (studentWellnessCategoricalDataDto == null) {
			studentWellnessCategoricalDataDto = new StudentWellnessCategoricalDataDto();
		}

		boolean isStudentCategory = category
				.equals(messageSource.getMessage("message.label.category.students", null, Locale.getDefault()));
		if (isStudentCategory) {
			studentWellnessCategoricalDataDto.setStudentId(studentDetails.getStudentId());
			studentWellnessCategoricalDataDto.setStudentName(studentDetails.getStudentName());
			studentWellnessCategoricalDataDto.setHostelName(studentDetails.getHostelName());
		} else
			studentWellnessCategoricalDataDto.setStudentId(studentId);

		studentWellnessCategoricalDataDto
				.setDayScholar(isStudentCategory ? getDayScholarText(studentDetails) : getCategoryText(category));

		if (headerForm != null && studentWellnessCategoricalDataDto.getId() != null) {
			setHeaderButtonProperties(headerForm);
		}

		studentWellnessCategoricalDataDto.setCategory(category);
		setModelAttributes(model, studentWellnessCategoricalDataDto);

		return HTMLPage.ADD_EDIT_WELLNESS_DETAILS;
	}

	@PostMapping
	public String saveOrUpdateWellnessDetails(
			@ModelAttribute StudentWellnessCategoricalDataDto studentWellnessCategoricalDataDto,
			BindingResult bindingResult, RedirectAttributes redirectAttrs, ModelMap model, HttpServletRequest request)
			throws Exception {
		wellnessArchiveValidator.validate(studentWellnessCategoricalDataDto, bindingResult);
		if (bindingResult.hasErrors()) {
			commonResponseUtil.updateHeaderForm(request, model,
					messageSource.getMessage("message.label.wellness.archive", null, Locale.getDefault()),
					false);
			StudentWellnessCategoricalDataDto dto = studentWellnessArchiveService
					.getWellnessDetailsByStudentId(studentWellnessCategoricalDataDto.getStudentId());
			AllStudentsDetailsViewDto studentDetails = allStudentsDetailsViewService
					.getCompleteStudentDetails(studentWellnessCategoricalDataDto.getStudentId());
			HeaderForm headerForm = (HeaderForm) model.getAttribute(ModelConstants.HEADER_FORM);

			boolean isStudentCategory = studentWellnessCategoricalDataDto.getCategory()
					.equals(messageSource.getMessage("message.label.category.students", null, Locale.getDefault()));
			if (isStudentCategory) {
				studentWellnessCategoricalDataDto.setStudentId(studentDetails.getStudentId());
				studentWellnessCategoricalDataDto.setStudentName(studentDetails.getStudentName());
				studentWellnessCategoricalDataDto.setHostelName(studentDetails.getHostelName());
			} else { studentWellnessCategoricalDataDto.setStudentId(studentWellnessCategoricalDataDto.getStudentId());
				if (dto != null) {
					studentWellnessCategoricalDataDto.setOtherStudName(dto.getOtherStudName());
					studentWellnessCategoricalDataDto.setOtherStudEmail(dto.getOtherStudEmail());
					studentWellnessCategoricalDataDto.setOtherStudPhone(dto.getOtherStudPhone());
				}
			}

			studentWellnessCategoricalDataDto.setDayScholar(isStudentCategory ? getDayScholarText(studentDetails)
					: getCategoryText(studentWellnessCategoricalDataDto.getCategory()));

			if (headerForm != null && studentWellnessCategoricalDataDto.getId() != null) {
				setHeaderButtonProperties(headerForm);
			}

			setModelAttributes(model, studentWellnessCategoricalDataDto);
			return HTMLPage.ADD_EDIT_WELLNESS_DETAILS;
		}
	    
	    String saveStatus = studentWellnessArchiveService.saveOrUpdateWellnessDetails(studentWellnessCategoricalDataDto);
	    commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs);
	    return Constants.REDIRECT + baseUrl;
	}

	private String getDayScholarText(AllStudentsDetailsViewDto studentDetails) {
	    return studentDetails.getDayScholar().equals(ModelConstants.YES) ? 
	        ModelConstants.OPEN_PARENTHESIS + ModelConstants.SPACE +
	        messageSource.getMessage("message.label.iitm.student", null, Locale.getDefault()) + ModelConstants.SPACE +
	        ModelConstants.HYPHEN + ModelConstants.SPACE +
	        messageSource.getMessage("message.label.day.scholar", null, Locale.getDefault()) +
	        ModelConstants.SPACE + ModelConstants.CLOSE_PARENTHESIS :
	        ModelConstants.OPEN_PARENTHESIS + ModelConstants.SPACE +
	        messageSource.getMessage("message.label.iitm.student", null, Locale.getDefault()) +
	        ModelConstants.SPACE + ModelConstants.CLOSE_PARENTHESIS;
	}

	private String getCategoryText(String category) {
	    return ModelConstants.OPEN_PARENTHESIS + ModelConstants.SPACE + category + ModelConstants.SPACE + ModelConstants.CLOSE_PARENTHESIS;
	}

	private void setHeaderButtonProperties(HeaderForm headerForm) {
	    headerForm.setAdditionalButtonProperties(
	        true, ModelConstants.BUTTON_PINK, 
	        messageSource.getMessage("message.label.print.pdf.wellness.report", null, Locale.getDefault()),
	        ModelConstants.FA_FILE_PDF
	    );
	}

	private void setModelAttributes(ModelMap model, StudentWellnessCategoricalDataDto studentWellnessCategoricalDataDto) {
	    model.addAttribute("studentWellnessCategoricalDataDto", studentWellnessCategoricalDataDto);
	    model.addAttribute("addNew", Constants.DISABLE);
	    model.addAttribute("saveButton", (studentWellnessCategoricalDataDto.getId() != null) ? Constants.UPDATED : Constants.SAVED);
	    model.addAttribute("referralTypeList", simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.REFERRAL_TYPE));
	    model.addAttribute("concernTypeList", simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.TYPE_OF_CONCERN));
	    model.addAttribute("selfHarmType", simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.SELF_HARM_TYPE));
	}

	@DeleteMapping("${id}")
	public @ResponseBody BaseResponse deleteHostelRoomInfoById(@PathVariable String id, ModelMap map,
			HttpServletRequest request) throws Exception {
		return CommonResponseUtil.generateDeleteResponseByStatus(studentWellnessArchiveService.deleteWellnessDetails(id));
	}

	@GetMapping("${wellness.details}")
	public String getViewWellnessDetailsList(@RequestParam String wellnessId, PaginationForm form, ModelMap model,
			HttpServletRequest request) throws Exception {
		List<StudentWellnessFollowupDataDto> wellnessViewList = studentWellnessArchiveService.getStudentWellnessViewList(wellnessId, form);
		model.addAttribute("wellnessDetailsDto", studentWellnessArchiveService.getWellnessDetailsByWellnessId(wellnessId));
		model.addAttribute("saveButton", Constants.DISABLE);
		model.addAttribute("USE_DATATABLES", true);
//		commonResponseUtil.updateCommonModelAttributes(model, request, wellnessViewList, form);
		commonResponseUtil.updateCommonModelAttributes2(model, request, wellnessViewList, form);
		return HTMLPage.VIEW_WELLNESS_DETAILS;
	}

	@GetMapping("${wellness.visit.details}" + "${wellnessId}" + "${id}")
	public String getWellnessVisitDetails(@PathVariable Long wellnessId, @PathVariable Long id, ModelMap model,
			HttpServletRequest request) throws Exception {
		String formKey = "studentWellnessFollowupDataDto";
		StudentWellnessFollowupDataDto studentWellnessFollowupDataDto = commonResponseUtil.handleModalFormError(request,
				model, formKey, StudentWellnessFollowupDataDto.class);

		if (Objects.nonNull(id) && id > 0) {
			studentWellnessFollowupDataDto = studentWellnessArchiveService.getWellnessVisitDetailsById(id, wellnessId);
		}
		
		if (studentWellnessFollowupDataDto.getId() == null) {
			studentWellnessFollowupDataDto.setNoOfVisit(studentWellnessArchiveService.getWellnessVisitCountByWellnessId(wellnessId));
			StudentWellnessCategoricalDataDto wellnessDto = new StudentWellnessCategoricalDataDto(); 
			wellnessDto.setId(wellnessId);
			studentWellnessFollowupDataDto.setWellness(wellnessDto);;
		}

		model.addAttribute("interactionModeList",
				simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.INTERACTION_MODE));
		model.addAttribute("visitStatusList",
				simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.VISIT_STATUS));
		model.addAttribute(formKey, studentWellnessFollowupDataDto);
		return HTMLPage.WELLNESS_VISIT_DETAILS_MODAL;
	}

	@PostMapping("${wellness.visit.details}")
	public String saveOrUpdateWellnessVisitDetails(@ModelAttribute StudentWellnessFollowupDataDto studentWellnessFollowupDataDto,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) throws Exception {

		String wellnessPath = "?wellnessId=";
		String wellnessId = MCrypt.getInstance().encryptToText(String.valueOf(studentWellnessFollowupDataDto.getWellness().getId()));
		wellnessArchiveValidator.validate(studentWellnessFollowupDataDto, bindingResult);
        if(bindingResult.hasErrors()){
            commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes, bindingResult, studentWellnessFollowupDataDto);
			return Constants.REDIRECT + baseUrl + wellnessDetailsUrl + wellnessPath + wellnessId;
        }

        String status = studentWellnessArchiveService.saveOrUpdateWellnessVisitDetails(studentWellnessFollowupDataDto);
		String message = status.equalsIgnoreCase(Constants.SAVED) ? "message.wellness.visit.save" : "message.wellness.visit.update";
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, message);
		return Constants.REDIRECT + baseUrl + wellnessDetailsUrl + wellnessPath + wellnessId;
    }

	@GetMapping("${url.pdf.download}"  + "${wellnessId}")
	public ResponseEntity<Resource> downloadWellnessDetailsPDF(@PathVariable String wellnessId) throws Exception {
		String encryptedWellnessId = wellnessId.matches(ValidationConstants.NUMERIC_PATTERN) 
		        ? MCrypt.getInstance().encryptToText(wellnessId) 
		        : wellnessId;
		Resource resource = studentWellnessArchiveService.downloadWellnessDetailsPDF(encryptedWellnessId);
		return Utility.prepareDownloadFile(resource);
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
