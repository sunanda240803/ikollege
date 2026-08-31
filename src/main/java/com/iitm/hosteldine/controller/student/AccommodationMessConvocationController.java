package com.iitm.hosteldine.controller.student;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.OtherCandidate.CandidateStayDateViewDto;
import com.iitm.hosteldine.dto.SimsConfigDataJsonArrayDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.paymentGatewayCC.PaymentGatewayCcavenueDto;
import com.iitm.hosteldine.dto.student.ConvocationAccommodationDto;
import com.iitm.hosteldine.dto.student.ConvocationAdditionalCouponsDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.student.AccommodationMessConvocationService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.student.AccommodationMessConvocationValidator;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.accommodation.mess.convocation}")
public class AccommodationMessConvocationController {

	private final HostelMasterService hostelMasterService;
	@Value("${url.accommodation.mess.convocation}")
	private String baseURL;

	@Value("${url.home}")
	private String homeURL;

	private final CommonResponseUtil commonResponseUtil;
	private final SimsConfigDataService simsConfigDataService;
	private final AccommodationMessConvocationService service;
	private final AccommodationMessConvocationValidator validator;
	
	@GetMapping
	public String getConvocationForm(ModelMap map, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
		String convocationOpenLink = simsConfigDataService.getSimConfigValue(SimsConfigDataService.CONVOCATION_OPEN_LINK);
		boolean isConvocationOpen = Boolean.parseBoolean(convocationOpenLink);
		if (!isConvocationOpen) {
			redirectAttributes.addFlashAttribute(Constants.RESPONSE, new BaseResponse(commonResponseUtil.getMessage("message.error.convocation.ended"), "Error"));
			return Constants.REDIRECT + homeURL;
		}
	    map.addAttribute("convocationAdditionalCouponsDto", new ConvocationAdditionalCouponsDto());
	    commonResponseUtil.updateCommonModelAttributes(map, request);
		return getConfigDetails(map, request);
	}
	
	private String getConfigDetails(ModelMap map, HttpServletRequest request) {
		List<HostelMasterDto> hostelList = hostelMasterService.getHostelList()
				.stream()
				.filter(dto -> !"Mandakini A".equals(dto.getHostelName()) &&
						!"Mandakini B".equals(dto.getHostelName()) &&
						!"CCW".equals(dto.getHostelName()) &&
						!"Dost".equals(dto.getHostelName()) &&
						!"Sarayu".equals(dto.getHostelName()))
				.toList();
		String convocationAccommodationRate = simsConfigDataService.getSimConfigValue(SimsConfigDataService.CONVOCATION_ACCOMMODATION_RATE);
		map.addAttribute("convocationNotes", simsConfigDataService.getSimConfigValue(SimsConfigDataService.CONVOCATION_NOTES));
		map.addAttribute("convocationAccommodationQuestion", replaceAccommodationRate(simsConfigDataService
				.getSimConfigValue(SimsConfigDataService.CONVOCATION_ACCOMMODATION_QUESTION), convocationAccommodationRate));
		map.addAttribute("convocationAccommodationNote", replaceAccommodationRate(simsConfigDataService
				.getSimConfigValue(SimsConfigDataService.CONVOCATION_ACCOMMODATION_NOTE), convocationAccommodationRate));
		map.addAttribute("accommodationPreferenceAccommodation", ModelConstants.ACCOMMODATION_PREFERENCE_ACCOMMODATION);
		map.addAttribute("accommodationPreferenceHostel", ModelConstants.ACCOMMODATION_PREFERENCE_HOSTEL);
		map.addAttribute("accommodationPreferenceHostelNotNeeded", ModelConstants.ACCOMMODATION_PREFERENCE_HOSTEL_NOT_NEEDED);
	    map.addAttribute("menuType", simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.MENU_TYPE));
	    map.addAttribute("complimentaryCouponDate", simsConfigDataService.getSimConfigValue(SimsConfigDataService.COMPLIMENTARY_COUPONS_DATE));
	    map.addAttribute("convocationAccommodationRate", convocationAccommodationRate);
	    map.addAttribute("hostelList", hostelList);

		ArrayList<String> convocationDates = simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.CONVOCATION_DATES);
		if (convocationDates != null && !convocationDates.isEmpty()) {
			String dateRange = convocationDates.getFirst().trim();
			String[] parts = dateRange.split(ModelConstants.TILDE);
			if (parts.length == 2) {
				LocalDate startDate = LocalDate.parse(parts[0].trim().replace(ModelConstants.SLASH, ModelConstants.HYPHEN));
				LocalDate endDate   = LocalDate.parse(parts[1].trim().replace(ModelConstants.SLASH, ModelConstants.HYPHEN));
				map.addAttribute("startDate", startDate);
				map.addAttribute("endDate", endDate);
			}
		}
	    ArrayList<String> convocationMessCouponRates = simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.CONVOCATION_MESS_COUPON_RATES);
		if (convocationMessCouponRates != null && convocationMessCouponRates.size() == 3) {
			map.addAttribute("bfRate", Double.parseDouble(convocationMessCouponRates.get(0)));
			map.addAttribute("lnRate", Double.parseDouble(convocationMessCouponRates.get(1)));
			map.addAttribute("dnRate", Double.parseDouble(convocationMessCouponRates.get(2)));
		}
	    return HTMLPage.CONVOCATION_FORM;
	}

	private String replaceAccommodationRate(String content, String convocationAccommodationRate) {
		if (content == null) {
			return null;
		}
		String rate = convocationAccommodationRate != null ? convocationAccommodationRate : "";
		String rateKey = commonResponseUtil.getMessage("message.label.convocation.accommodation.rate.replace");
		return content.replace(rateKey, rate);
	}

	@PostMapping
	public String saveConvocationForm(@ModelAttribute ConvocationAdditionalCouponsDto dto,
			BindingResult result, RedirectAttributes redirectAttrs, HttpServletRequest request, ModelMap map)
			throws Exception {
		if (!validator.validateConvocationForm(result)) {
			redirectAttrs.addFlashAttribute(Constants.RESPONSE, new BaseResponse(commonResponseUtil.getMessage("message.error.convocation.ended"), "Error"));
			return Constants.REDIRECT + homeURL;
		}
		validator.validate(dto, result);
		if (result.hasErrors()) {
			return getConfigDetails(map, request);
		}
		String saveStatus=Constants.ERROR;
		try {
			ConvocationAccommodationDto convDto= service.saveConvocationForm(dto, request);
			if(convDto!=null && convDto.getId()>0){
				saveStatus=Constants.SAVED;
				if((convDto.getAccommodationStatus()!=null && convDto.getAccommodationStatus().equals(true) )|| convDto.getAdditionalNoOfCoupons()>0){
					PaymentGatewayCcavenueDto pgDto = service.initiateTransaction(convDto,request);
					// Pass encrypted data and access code to the Thymeleaf template
					map.addAttribute("pgDto", pgDto);
					return HTMLPage.CCAVENUE_HTML;
				}
			}
		} catch (Exception e) {
			commonResponseUtil.exceptionMessageHandling(e, redirectAttrs);
		}
		commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs);
		return Constants.REDIRECT + baseURL;
	}

	@PostMapping("${url.payment.response}")
	public String savePaymentResponse(@RequestParam(value = "responseType", required = false) String responseType, ModelMap model, HttpServletRequest request) throws Exception {

		System.out.println("responseType------"+responseType);
		PaymentGatewayCcavenueDto pgDto = service.savePaymentResponse(responseType,request);

		if(responseType!=null & responseType.equals(Constants.CANCEL)) {
			return Constants.REDIRECT + baseURL;
		}

		model.addAttribute("paymentGatewayDto", pgDto);
		model.addAttribute("backToList", baseURL);
		return HTMLPage.CCAVENUE_RESPONSE_HTML;
	}

	@GetMapping(value = "${url.student}" + "${id}")
	public @ResponseBody String CheckStudentExists(@PathVariable String id) {
		return service.CheckStudentExists(id);
	}

	@GetMapping(value = "${url.accommodation.exists}" + "${id}")
	public @ResponseBody boolean checkAccommodationExists(@PathVariable String id) {
		return service.isStudentAlreadyAppliedAccommodation(id);
	}

	@GetMapping(value = "${url.report}")
	public String getConvocationList(HttpServletRequest request, ModelMap model) throws Exception {
		List<String> paymentStatusList = simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.CONVOCATION_PAYMENT_STATUS).stream()
				.map(SimsConfigDataJsonArrayDto::getValue)
				.toList();
		model.addAttribute("paymentStatusList", paymentStatusList);
		commonResponseUtil.updateCommonModelAttributes(model, request);
		return HTMLPage.CONVOCATION_REPORT;
	}

	@GetMapping(value = "${url.excel.download}")
	public void downloadConvocationExcelReport(
			@RequestParam(required = false) String fromDate,
			@RequestParam(required = false) String toDate,
			@RequestParam(required = false) String paymentStatus,
			HttpServletResponse response) {

		try {
			LocalDate parsedFromDate = StringUtils.hasText(fromDate) ? LocalDate.parse(fromDate) : null;
			LocalDate parsedToDate = StringUtils.hasText(toDate) ? LocalDate.parse(toDate) : null;
			Workbook workbook = service.generateConvocationExcelReport(parsedFromDate, parsedToDate, paymentStatus);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			workbook.write(bos);
			workbook.close();

			byte[] excelBytes = bos.toByteArray();
			response.setContentType(ExcelConstants.CONTENT_TYPE);
			response.setHeader(ExcelConstants.CONTENT_DISPOSITION, ExcelConstants.ATTACHMENT_FILE_NAME + "AccommodationMessConvocationReport" + ExcelConstants.EXCEL_EXTENSION);
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
