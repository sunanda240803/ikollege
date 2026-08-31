package com.iitm.hosteldine.controller.mess;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.dean.FilterCriteriaDto;
import com.iitm.hosteldine.dto.mess.MessAllottedListDTO;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.mess.MessAllottedListService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import com.iitm.hosteldine.validator.mess.MessAllotmentListValidator;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.mess.allotted.list}")
public class MessAllottedListController {

	@Value("${url.mess.allotted.list}")
	private String baseURL;

	private final MessAllottedListService service;
	private final MessageSource messageSource;
	private final MessMasterService messMasterService;
	private final CommonResponseUtil commonResponseUtil;
	private final MessMasterCommonService messMasterCommonService;
	private final MessAllotmentListValidator messAllotmentListValidator;

	@GetMapping
	public String getMessAllottedList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map,
			HttpServletRequest request) throws Exception {

		String[] keys = { "messPeriod", "messName", "studentName", "studentId" };

		Page<MessAllottedListDTO> messAllottedList = null;
		commonResponseUtil.getAdditionalParams(allParams, form);

		Map<String, Object> additionalParams = form.getAdditionalParam();
		Map<String, Object> extractedParams = new HashMap<>();

		for (String key : keys) {
			extractedParams.put(key, additionalParams.get(key));
		}

		FilterCriteriaDto filter = new FilterCriteriaDto();

		if (extractedParams.values().stream().anyMatch(this::isValid)) {
			messAllottedList = service.getMessAllottedList(form);
			filter = service.getFilterData(form);
		} else {
			Arrays.stream(keys).forEach(key -> additionalParams.put(key, ""));
			messAllottedList = service.getMessAllottedList(form);
		}
		
		Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
		Long id = flashInputMap != null && flashInputMap.get("id") != null
				? (Long) flashInputMap.get("id")
				: null;

		String type = flashInputMap != null && flashInputMap.get("type") != null
				? (String) flashInputMap.get("type")
				: null;
		map.addAttribute("returnId", id);
		map.addAttribute("returnType", type);
		map.addAttribute("messAllottedList", filter);
		map.addAttribute("messPeriodList", service.getMessPeriodList());
		map.addAttribute("messMasterList", messMasterService.getMessMasterList());
		commonResponseUtil.updateCommonModelAttributes(map, request, messAllottedList, form);
		HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
		if (headerForm != null) {
			headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_PINK,
					messageSource.getMessage("message.label.mess.allotted.list.report", null, Locale.getDefault()),
					ModelConstants.FA_FILE_EXCEL);
		}
		return HTMLPage.MESS_ALLOTTED_LIST;
	}

	@GetMapping(value = "${url.mess.allotment.configuration}" + "${type}" + "${id}")
	public String getMessAllotmentDetails(@PathVariable("type") String type, @PathVariable("id") Long id,
			ModelMap model, HttpServletRequest request) throws Exception {
		String formKey = "messAllottedListDTO";
		MessAllottedListDTO messAllottedListDTO = commonResponseUtil.handleModalFormError(request, model, formKey,
				MessAllottedListDTO.class);

		if ((Objects.nonNull(id) && id != 0)
				|| (Objects.nonNull(messAllottedListDTO.getId()) && messAllottedListDTO.getId() != 0)) {
			MessAllottedListDTO messAllotted = service
					.getMessChangeDetails((Objects.nonNull(id) && id != 0) ? id : messAllottedListDTO.getId());
			model.addAttribute("studentMessDetails", messAllotted);
			if (type.equals(WorkflowStatus.CHANGE.getStatus().toLowerCase())) {
				model.addAttribute("messMasterList",
						messMasterService.getMessListForAllotment(messAllotted.getMessId(), messAllotted.getGender()));
			}
			if(messAllottedListDTO == null || messAllottedListDTO.getId() == null) messAllottedListDTO = messAllotted;
		}
		model.addAttribute("currentMessPeriod", messMasterCommonService.getCurrentMessPeriod());
		model.addAttribute(formKey, messAllottedListDTO);
		return HTMLPage.MESS_CONFIGURATION_MODAL;
	}

	@PostMapping(value = "${url.mess.allotment.configuration}")
	public String saveOrUpdateMessAllotmentDetails(@ModelAttribute MessAllottedListDTO messAllottedListDTO,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, ModelMap model) throws Exception {

		messAllotmentListValidator.validate(messAllottedListDTO, bindingResult);
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("id", messAllottedListDTO.getId());
			redirectAttributes.addFlashAttribute("type", messAllottedListDTO.getType());
			commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes, bindingResult, messAllottedListDTO);
			return Constants.REDIRECT + baseURL;
		}

		String status = service.saveOrUpdateMessAllotmentDetails(messAllottedListDTO);
		commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, status);
		return Constants.REDIRECT + baseURL;
	}

	@GetMapping("${url.download.mess.allotted.list.report}")
	public void downloadMessAllottedListReport(@RequestParam Map<String, String> allParams, PaginationForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		commonResponseUtil.getAdditionalParams(allParams, form);
		Workbook workbook = service.downloadMessAllottedListReport(form);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		workbook.close();
		byte[] excelBytes = bos.toByteArray();
		response.setContentType(FileUploadConstants.XLSX);
		response.setHeader(FileUploadConstants.CONTENT_DISPOSITION, ExcelConstants.MESS_ALLOTTED_LIST_FILENAME);
		response.setContentLength(excelBytes.length);
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			outputStream.write(excelBytes);
			outputStream.flush();
		}
	}

	@GetMapping(value = "${url.student}" + "${id}")
	public @ResponseBody String CheckStudentExists(@PathVariable("id") String id) {
		return service.CheckStudentExists(id);
	}

	@GetMapping(value = "${url.student.in.mess.period}" + "${id}")
	public @ResponseBody Boolean CheckStudentInMessPeriod(@PathVariable("id") String id) {
		return service.checkStudentInCurrentMessPeriod(id);
	}

	@GetMapping(value = "${url.student.in.mess.list}" + "${id}")
	public @ResponseBody Object getMessListBasedOnStudentGender(@PathVariable("id") String id) {
		return service.fetchMessListByStudentGender(id);
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
