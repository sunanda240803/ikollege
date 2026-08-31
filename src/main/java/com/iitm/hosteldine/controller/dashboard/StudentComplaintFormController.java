package com.iitm.hosteldine.controller.dashboard;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.HostelBiometricTerminalDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.service.reports.LateNightEntryRecord;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.dto.student.StudentComplaintDetailsDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.student.StudentComplaintConfigurationEntity;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.service.student.StudentComplaintConfigurationService;
import com.iitm.hosteldine.service.student.StudentComplaintFormService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.student.complaint.form}")
public class StudentComplaintFormController {

	
	private final StudentComplaintFormService studentComplaintFormService;
	private final CommonResponseUtil commonResponseUtil;
	private final MessMasterService messMasterService;
	private final StudentComplaintConfigurationService studentComplaintConfigurationService;
	private final MessageSource messageSource;
	
	
	@Value("${url.student.complaint.form}")
	private String studentComplaintList;
	
	@GetMapping
	public String getStudentComplaintList(PaginationForm form,ModelMap map, HttpServletRequest request) throws Exception {
		 String studentId = SecurityCtxUtil.userId().toUpperCase(); 
		Page<StudentComplaintDetailsDto> studentComplaintList = studentComplaintFormService.getStudentComplaintList(form,studentId);
		 //map.addAttribute("studentId", studentId);
		commonResponseUtil.updateCommonModelAttributes(map, request ,studentComplaintList , form);
		return HTMLPage.STUDENT_COMPLAINT_FORM;
	}
	
	@PostMapping
	public String saveStudentComplaintForm(@Valid @ModelAttribute StudentComplaintDetailsDto studentComplaintDetailsDto,BindingResult bindingResult,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		
		 if(bindingResult.hasErrors()){
	            commonResponseUtil.updateModalFormErrorAttributes(redirectAttrs,bindingResult,studentComplaintDetailsDto);
	            return "redirect:" + studentComplaintList;
	        }
		String saveStatus = studentComplaintFormService.saveStudentComplaintForm(studentComplaintDetailsDto, request);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus,redirectAttrs);
		return Constants.REDIRECT + studentComplaintList;
	}
	
	@GetMapping("${id}")
	public String getById(@PathVariable long id, ModelMap map, HttpServletRequest request)
			throws Exception {
		 String studentId = SecurityCtxUtil.userId().toUpperCase(); 
		 String formKey = "studentComplaintDetailsDto";
		 StudentComplaintDetailsDto studentComplaintDetailsDto = commonResponseUtil.handleModalFormError(request,map,formKey, StudentComplaintDetailsDto.class);
		map.addAttribute(formKey, studentComplaintDetailsDto);
		List<StudentComplaintConfigurationEntity> complaintConfigurations = studentComplaintConfigurationService.getActiveHostelComplaints();
		 MessMasterDto messMasterList = messMasterService.getMessList();
	      map.addAttribute("messMasterList", messMasterList);
		 map.addAttribute("complaintConfigurations", complaintConfigurations); 
		return HTMLPage.ADD_STUDENT_COMPLAINT_FORM;
	}

	@GetMapping("${url.download}/{complaintId}")
	public ResponseEntity<Resource> downloadFile(@PathVariable Long complaintId) throws Exception {
		ByteArrayResource resource = studentComplaintFormService.downloadComplaintFile(complaintId);
		String fileName = studentComplaintFormService.getFileName(complaintId);
		return Utility.prepareDownloadFile(resource, fileName);
	}

	@GetMapping(value = "${url.get.report}")
	String getReportScreen(HttpServletRequest request, ModelMap model) {
		commonResponseUtil.updateCommonModelAttributes(model, request);
		return HTMLPage.STUDENT_COMPLAINT_REPORT;
	}

	@GetMapping(value = "${url.excel.download}")
	void downloadComplaintDetails(@RequestParam(required = false) String complaintType, @RequestParam String fromDate, @RequestParam String toDate, PaginationForm form, HttpServletResponse response) {
		try {
			form.setSize(Integer.MAX_VALUE);
			List<StudentComplaintDetailsDto> studentComplaintDetailsDtoList = studentComplaintFormService.getStudentComplaintList(complaintType, LocalDate.parse(fromDate), LocalDate.parse(toDate));
			Workbook workbook = studentComplaintFormService.generateExcelReport(studentComplaintDetailsDtoList, "message.student.complaint.report");
			String fileName  = commonResponseUtil.getMessage("message.student.complaint.report.filename") + Strings.EMPTY + FileUploadConstants.XLSX_EXTENSION;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			workbook.write(bos);
			workbook.close();
			byte[] excelBytes = bos.toByteArray();
			response.setContentType(FileUploadConstants.XLSX);
			response.setHeader(FileUploadConstants.CONTENT_DISPOSITION, messageSource.getMessage("message.attachment.filename", new Object[]{fileName}, Locale.getDefault()));
			response.setContentLength(excelBytes.length);
			try (ServletOutputStream outputStream = response.getOutputStream()) {
				outputStream.write(excelBytes);
				outputStream.flush();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
	 
