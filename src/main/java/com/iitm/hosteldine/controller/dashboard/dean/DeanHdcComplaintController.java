package com.iitm.hosteldine.controller.dashboard.dean;

import java.io.ByteArrayOutputStream;
import java.util.*;

import com.iitm.hosteldine.dto.student.StudentRoomInfoDTO;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.util.RoleEnum;
import com.iitm.hosteldine.util.response.BaseResponse;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.dean.DeanApprovalDto;
import com.iitm.hosteldine.dto.dean.DeanHdcComplaintDto;
import com.iitm.hosteldine.dto.hostel.GeneralLedgerDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.mess.MessBillSummaryDto;
import com.iitm.hosteldine.form.common.HeaderForm;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.student.StudentHostelPaymentLateFeeDetailDto;
import com.iitm.hosteldine.service.dean.DeanDashboardService;
import com.iitm.hosteldine.service.dean.DeanHdcComplaintService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping(value = "${url.dean.approval.hdc.complaint}")
@RequiredArgsConstructor
@Slf4j
public class DeanHdcComplaintController {

	@Value("${url.dean.approval.hdc.complaint}")
	private String baseUrl;
	@Value("${url.new}")
	private String addNew;

	private final CommonResponseUtil commonResponseUtil;
	private final DeanDashboardService deanDashboardService;
	private final DeanHdcComplaintService deanHdcComplaintService;
	private final MessageSource messageSource;
	private final HostelMasterService hostelMasterService;
	private final StudentDetailsInfoService studentService;

	@GetMapping
	public String getHdcComplaintList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
		
		HttpSession session = request.getSession();
		// Getting dynamic tabs
		DeanApprovalDto deanApprovalDto = getHdcComplaintMenuList(session);	
		DeanApprovalDto columnList = getHdcComplaintColumnList(session);
		map.addAttribute("deanApprovalDto", deanApprovalDto);
		map.addAttribute("columnDto", columnList);
		String splitBaseUrl = baseUrl.replace("/", "");
		
		// Setting dynamic filter values
		if (!form.isSearchFilter()) {
			List<String> filterList = List.of("complaintFromDate", "complaintToDate", "studentName", "studentId", "year");
			filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
		}

		commonResponseUtil.getAdditionalParams(allParams, form);
		
		List<DeanHdcComplaintDto> hdcComplaintDto = deanHdcComplaintService.getHdcComplaintList(form, splitBaseUrl, false);
		commonResponseUtil.updateCommonModelAttributes2(map, request, hdcComplaintDto, form);
		setExportButton(map, splitBaseUrl);
		setAddNewButton(map, splitBaseUrl);
		return HTMLPage.DEAN_DASHBOARD_MENU_LIST;
	}

	@GetMapping(value = "${url.view}")
	public String getHdcComplaintDetails(@RequestParam String data, ModelMap map, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
		Boolean status = Utility.checkRequestType(data, 3, 2);
		if (status) {
			try {
				String[] split = MCrypt.getInstance().decryptToString(data).split(Constants.BACKTICK);
				Long hdcId = (split[0] != null) ? Long.valueOf(split[0]) : null;
				String studentId = (split[1] != null) ? split[1] : Strings.EMPTY;
				DeanHdcComplaintDto hdcComplaintDto = Optional.ofNullable(deanHdcComplaintService.getHdcComplaintDetails(studentId,hdcId)).orElse(new DeanHdcComplaintDto());
				hdcComplaintDto.setData(data);
				map.addAttribute("deanHdcComplaintDto", hdcComplaintDto);
				String userRole = Objects.requireNonNull(SecurityCtxUtil.userRole());
				map.addAttribute("userRole", userRole);
				commonResponseUtil.updateCommonModelAttributes(map, request);
				return HTMLPage.DEAN_DASHBOARD_HDC_COMPLAINT_DETAILS;
			} catch (Exception e) {
				e.printStackTrace();
				commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
			}
		} else {
			commonResponseUtil.invalidAccess(redirectAttributes);
		}
		return null;		
	}
	
	@GetMapping(value = "${url.new}")
	public String addNewHdcComplaint(ModelMap map, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
		try {
			DeanHdcComplaintDto hdcComplaintDto = new DeanHdcComplaintDto();
			map.addAttribute("deanHdcComplaintDto", hdcComplaintDto);
			String userRole = Objects.requireNonNull(SecurityCtxUtil.userRole());
			map.addAttribute("userRole", userRole);
			List<HostelMasterDto> hostelList = hostelMasterService.getHostelList();
			map.addAttribute("hostelList", hostelList);
			commonResponseUtil.updateCommonModelAttributes(map, request);
			return HTMLPage.DEAN_DASHBOARD_HDC_COMPLAINT_FORM;
		} catch (Exception e) {
			e.printStackTrace();
			commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
		}
		return null;		
	}

	@GetMapping("${url.student}/{id}")
	public @ResponseBody StudentRoomInfoDTO getStudentRoomDetails(@PathVariable String id) {
		return studentService.getStudentRoomDetails(id);
	}
	
	@GetMapping(value = "${url.previous.complaints}/{id}")
	public @ResponseBody List<DeanHdcComplaintDto> getStudentPreviousComplaints(@PathVariable String id) throws Exception{
		List<DeanHdcComplaintDto> previousComplaint = deanHdcComplaintService.getPreviousComplaints(id);
		return previousComplaint;
	}
	
	@PostMapping
    public String updateComplaint(@ModelAttribute DeanHdcComplaintDto deanHdcComplaintDto,
                              BindingResult bindingResult, RedirectAttributes redirectAttributes,ModelMap map, HttpServletRequest request) {
		try {
			deanHdcComplaintService.validateUpdateHdcComplaintForm(deanHdcComplaintDto, bindingResult);
			if (bindingResult.hasErrors()) {
				commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes, bindingResult, deanHdcComplaintDto);
				//return getHdcComplaintDetails(deanHdcComplaintDto.getData(), map, request, redirectAttributes);
				return Constants.REDIRECT + baseUrl;
			}
			String status = deanHdcComplaintService.updateHdcComplaint(deanHdcComplaintDto);
			String message = Objects.nonNull(status) && Constants.SAVED.equals(status) ? "message.hdc.complaint.update" : "message.hdc.complaint.failure";
			commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, message);
		} catch (Exception e) {
			commonResponseUtil.exceptionMessageHandling(e,redirectAttributes);
		}
        return Constants.REDIRECT + baseUrl;
    }
	
	@PostMapping("${url.save}")
    public String saveHdcComplaint(@Valid @ModelAttribute DeanHdcComplaintDto deanHdcComplaintDto,
                              BindingResult bindingResult, RedirectAttributes redirectAttributes,ModelMap map, HttpServletRequest request) throws Exception {
		try {
			deanHdcComplaintService.validateHdcComplaintForm(deanHdcComplaintDto, bindingResult);
			if (bindingResult.hasErrors()) {
				commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes, bindingResult, deanHdcComplaintDto);
				return Constants.REDIRECT + baseUrl + addNew;
			}
			String status = deanHdcComplaintService.saveHdcComplaint(deanHdcComplaintDto);
			String message = Objects.nonNull(status) && Constants.SAVED.equals(status) ? "message.hdc.complaint.save" : "message.hdc.complaint.failure";
			commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, message);
		} catch (Exception e) {
			commonResponseUtil.exceptionMessageHandling(e,redirectAttributes);
		}
        return Constants.REDIRECT + baseUrl;
    }
	
	@GetMapping("${url.dean.approval.excel.report.download}")
	public void downloadRequestReport(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map,
	                                  HttpServletRequest request, HttpServletResponse response) {
	    commonResponseUtil.getAdditionalParams(allParams, form);
	    List<DeanHdcComplaintDto> hdcComplaintDto = deanHdcComplaintService.getHdcComplaintList(form, baseUrl.replace("/", ""), true);
	    writeExcelResponse(hdcComplaintDto, response);
	}

	private DeanApprovalDto getHdcComplaintColumnList(HttpSession session) {
		DeanApprovalDto columnList;
		if(session.getAttribute("hdcComplaintColumnList") == null) {
			columnList = deanDashboardService.getDeanMenuListById(baseUrl.replace("/", ""));
			session.setAttribute("hdcComplaintColumnList", columnList);
		} else {
			columnList = (DeanApprovalDto) session.getAttribute("hdcComplaintColumnList");
		}
		return columnList;
	}
	
	private DeanApprovalDto getHdcComplaintMenuList(HttpSession session) {
		DeanApprovalDto deanApprovalDto;
		if(session.getAttribute("hdcComplaintMenuList") == null) {
			deanApprovalDto = deanDashboardService.getDeanMenuList();
			session.setAttribute("hdcComplaintMenuList", deanApprovalDto);
		} else {
			deanApprovalDto = (DeanApprovalDto) session.getAttribute("hdcComplaintMenuList");
		}
		return deanApprovalDto;
	}
	
	private void setExportButton(ModelMap map , String splitBaseUrl) {
		HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
		if (headerForm != null) {
			headerForm.setAdditionalButtonProperties(true, ModelConstants.BUTTON_PINK,
					messageSource.getMessage("message.label.hdc.complaint.report", null, Locale.getDefault()),
					ModelConstants.FA_FILE_EXCEL);
			map.addAttribute("excelUrl", splitBaseUrl
					+ messageSource.getMessage("url.dean.approval.excel.report.download", null, Locale.getDefault()));
		}
	}
	private void setAddNewButton(ModelMap map , String splitBaseUrl) {
		HeaderForm headerForm = (HeaderForm) map.getAttribute(ModelConstants.HEADER_FORM);
		if (headerForm != null && !RoleEnum.SOFTWARE_ADMIN.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole())) {
			headerForm.setAdditionalButton2Properties(true, ModelConstants.BUTTON_PRIMARY,
					messageSource.getMessage("message.button.add.new", null, Locale.getDefault()),
					ModelConstants.FA_ADD_NEW);
			map.addAttribute("addNewUrl", splitBaseUrl
					+ messageSource.getMessage("url.new", null, Locale.getDefault()));
		}
	}

	@GetMapping("${url.report}")
	public String getHDCComplaintReport(@RequestParam Map<String, String> allParams, PaginationForm form,
	                                    ModelMap map, HttpServletRequest request) throws Exception {
	    commonResponseUtil.getAdditionalParams(allParams, form);

	    List<String> keys = Arrays.asList("complaintFromDate", "complaintToDate", "hostelId");
	    keys.forEach(key -> form.getAdditionalParam().put(key, ""));

	    map.addAttribute("hostelList", hostelMasterService.getHostelList());
	    commonResponseUtil.updateCommonModelAttributes(map, request, null, form);
	    return HTMLPage.HDC_COMPLAINT_REPORT;
	}

	@GetMapping("${url.report}" + "${url.excel.download}")
	public void downloadHDCComplaintReportAdmin(@RequestParam Map<String, String> allParams, PaginationForm form,
	                                       ModelMap map, HttpServletRequest request, HttpServletResponse response) {
	    commonResponseUtil.getAdditionalParams(allParams, form);
	    List<DeanHdcComplaintDto> hdcComplaintDto = deanHdcComplaintService.getHdcComplaintList(form, null, true);
	    writeExcelResponse(hdcComplaintDto, response);
	}

	@GetMapping("${url.excel.download}")
	public void downloadHDCComplaintReport(@RequestParam Map<String, String> allParams, PaginationForm form,
	                                       ModelMap map, HttpServletRequest request, HttpServletResponse response) {
	    commonResponseUtil.getAdditionalParams(allParams, form);
	    List<DeanHdcComplaintDto> hdcComplaintDto = deanHdcComplaintService.getHdcComplaintList(form, null, true);
	    writeExcelResponse(hdcComplaintDto, response);
	}

	private void writeExcelResponse(List<DeanHdcComplaintDto> complaintList, HttpServletResponse response) {
	    try {
	        Workbook workbook = deanHdcComplaintService.getHdcComplaintReport(complaintList);
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        workbook.write(bos);
	        workbook.close();

	        byte[] excelBytes = bos.toByteArray();
	        response.setContentType(FileUploadConstants.XLSX);
	        String fileName = messageSource.getMessage("message.hdc.complaint.list.filename", null, Locale.getDefault())
	                + FileUploadConstants.XLSX_EXTENSION;
	        response.setHeader(FileUploadConstants.CONTENT_DISPOSITION,
	                messageSource.getMessage("message.attachment.filename", new Object[]{fileName}, Locale.getDefault()));
	        response.setContentLength(excelBytes.length);

	        try (ServletOutputStream outputStream = response.getOutputStream()) {
	            outputStream.write(excelBytes);
	            outputStream.flush();
	        }
	    } catch (Exception e) {
	        throw new RuntimeException("Error while generating Excel report", e);
	    }
	}

	@DeleteMapping(value = "${url.delete}")
	public @ResponseBody BaseResponse deleteHdcComplaint(@RequestParam String data, ModelMap map, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
		try {
			return CommonResponseUtil.generateDeleteResponseByStatus(deanHdcComplaintService.deleteHDCComplaintDetails(data));
		} catch (Exception e) {
			e.printStackTrace();
			commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
		}
		return null;
	}
}
