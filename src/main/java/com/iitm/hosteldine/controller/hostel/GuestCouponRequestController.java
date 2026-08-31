package com.iitm.hosteldine.controller.hostel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.hostel.*;
import com.iitm.hosteldine.dto.paymentGatewayCC.PaymentGatewayCcavenueDto;
import com.iitm.hosteldine.dto.student.StudentRoomInfoDTO;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.hostel.GuestCouponConfigService;
import com.iitm.hosteldine.service.hostel.GuestCouponOnlinePaymentService;
import com.iitm.hosteldine.service.hostel.GuestCouponRequestService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.ValidationCommon;
import com.iitm.hosteldine.validator.hostel.GuestCouponRequestValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.hostel.guest.coupon.request}")
public class GuestCouponRequestController {
	@Value("${url.hostel.add.guest.coupon.request}")
    private String addCouponRequestPath;

    @Value("${url.hostel.guest.coupon.request}")
    private String guestCouponRequestPath;
    
    

    private final GuestCouponRequestService service;
    private final SimsConfigDataService simsConfigDataService;
    private final CommonResponseUtil commonResponseUtil;
    private final StudentDetailsInfoService studentService;
    private final HostelMasterService hostelService;
    private final MessMasterService messService;
    private final GuestCouponConfigService couponService;
    private final GuestCouponOnlinePaymentService couponOnlinePaymentService;
    private final GuestCouponRequestValidator guestCouponRequestValidator;
    private final MessageSource messageSource;
	private final MessMasterCommonService messMasterCommonService;

	@GetMapping
	public String getCouponRequests(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map,
			HttpServletRequest request) {

		String returnPage = HTMLPage.GUEST_COUPON_REQUEST;
		
		String loginType = SecurityCtxUtil.accountType();
		if (loginType != null && loginType.equals(ModelConstants.STUDENT)) {
			form.getAdditionalParam().put("studentId", SecurityCtxUtil.userId().toUpperCase());
			form.getAdditionalParam().put("category", messageSource.getMessage("message.online.coupon", null, Locale.getDefault()));
		}
		
		String[] keys = GuestCouponRequestDTO.PARAM_KEYS;
        GuestCouponRequestResultDTO returnDto=new GuestCouponRequestResultDTO();
		List<GuestCouponRequestResultDTO> couponRequests = null;

		commonResponseUtil.getAdditionalParams(allParams, form);

		Map<String, Object> additionalParams = form.getAdditionalParam();
		Map<String, Object> extractedParams = new HashMap<>();

		for (String key : keys) {
			extractedParams.put(key, additionalParams.get(key));
		}
		GuestCouponRequestDTO dto = new GuestCouponRequestDTO();
		
		if (extractedParams.values().stream().anyMatch(this::isValid)) {
            returnDto = service.getRequestedCoupons(form,loginType);

			dto.setName(ValidationCommon.toStringOrNull(extractedParams.get("name")));
			dto.setStudentId(ValidationCommon.toStringOrNull(extractedParams.get("studentId")));
			dto.setDiningFrom(ValidationCommon.toLocalDateOrNull(extractedParams.get("diningFrom")));
			dto.setDiningTo(ValidationCommon.toLocalDateOrNull(extractedParams.get("diningTo")));
			dto.setSubmittedFrom(ValidationCommon.toLocalDateOrNull(extractedParams.get("submittedFrom")));
			dto.setSubmittedTo(ValidationCommon.toLocalDateOrNull(extractedParams.get("submittedTo")));
			dto.setCategory(ValidationCommon.toStringOrNull(extractedParams.get("category")));
			dto.setPaymentStatus(ValidationCommon.toStringOrNull(extractedParams.get("paymentStatus")));
		} else {
			Arrays.stream(keys).forEach(key -> additionalParams.put(key, ""));
            returnDto = service.getRequestedCoupons(form,loginType);
		}
        //Set the list
        couponRequests=returnDto.getReqList();

		if (loginType != null && !loginType.equals(ModelConstants.STUDENT)) {
			map.addAttribute(HostelConstants.COUPON_CATEGORIES.getConstants(),
					simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.COUPON_CATEGORY));
			map.addAttribute(HostelConstants.PAYMENT_STATUS.getConstants(),
					simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.COUPON_PAYMENT_STATUS));
			map.addAttribute(HostelConstants.SEARCHED_COUPONS.getConstants(), couponRequests);

			String role = SecurityCtxUtil.userRole()
					.equals(simsConfigDataService.getSimConfigValue(SimsConfigDataService.SOFTWARE_ADMIN))
							? ModelConstants.STATUS_ACTIVE
							: ModelConstants.STATUS_INACTIVE;
			map.addAttribute("role", role);
		}else {
			returnPage = HTMLPage.ONLINE_MESSCOUPON_LIST_STUDENT;
		}
		map.addAttribute("coupon", dto);
        map.addAttribute("deleteStatus", returnDto.isDeleteStatus());
		commonResponseUtil.updateCommonModelAttributes2(map, request, couponRequests, form);
		// ✅ Enable DataTables for this page only
		map.addAttribute("USE_DATATABLES", true);
		return returnPage;
	}

    @GetMapping("${url.add}")
    public String addGuestCouponRequest(ModelMap model, HttpServletRequest request) throws Exception {
    	String loginType= SecurityCtxUtil.accountType();
    	String returnPage=null;
		if(loginType!=null){
			returnPage=HTMLPage.ADD_GUEST_COUPON_REQUEST;
		}

        model.addAttribute("guestCouponRequestDTO", GuestCouponRequestDTO.builder().build());
        
        if(loginType!=null && loginType.equals(ModelConstants.STUDENT)) {
//        	ObjectMapper objectMapper = new ObjectMapper();
//        	String messesJson = objectMapper.writeValueAsString(messService.getOnlineCouponMessMasterList());
        	setOnlineCouponModelAttributes(model);
			setlModelAttributes(model);
        	returnPage=HTMLPage.ADD_ONLINE_MESS_COUPON;
        }else {
			model.addAttribute("hostels", hostelService.getHostelList());
        	setlModelAttributes(model);
        }

        commonResponseUtil.updateCommonModelAttributes(model, request);
        return returnPage;
    }
    
    @GetMapping("${url.student}/{id}")
    public @ResponseBody StudentRoomInfoDTO getStudentRoomDetails(@PathVariable String id) {
        return studentService.getStudentRoomDetails(id);
    }
    
    @PostMapping
    public String saveCouponRequest(@ModelAttribute GuestCouponRequestDTO guestCouponRequestDTO,  BindingResult bindingResult, 
    		RedirectAttributes redirectAttributes, HttpServletRequest request, ModelMap model) throws JsonProcessingException {
		try {
			if (guestCouponRequestDTO.getFrequencyJson() != null && !guestCouponRequestDTO.getFrequencyJson().isEmpty()) {
				ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
				List<DailyCouponRequestDTO> frequencies = objectMapper.readValue(guestCouponRequestDTO.getFrequencyJson(), new TypeReference<>() {
				});
				guestCouponRequestDTO.setFoodFrequency(frequencies);
			}
			String loginType = SecurityCtxUtil.accountType();
			if (loginType != null && loginType.equals(ModelConstants.STUDENT)
					&& guestCouponRequestDTO.getCategory().equalsIgnoreCase(messageSource.getMessage("message.online.coupon", null, Locale.getDefault()))) {
				String studentId = SecurityCtxUtil.userId().toUpperCase();
				StudentRoomInfoDTO stuDto = studentService.getStudentRoomDetails(studentId);
				guestCouponRequestDTO.setStudentId(studentId);
				guestCouponRequestDTO.setName(stuDto.getStudentName());
				guestCouponRequestDTO.setHostelId(stuDto.getHostelId());
				guestCouponRequestDTO.setRoomNo(stuDto.getRoomNumber() != null && !stuDto.getRoomNumber().isEmpty() ? Integer.valueOf(stuDto.getRoomNumber()) : 0);
				guestCouponRequestDTO.setMobileNo(stuDto.getStudentMobile() != null ? stuDto.getStudentMobile().toString() : "");
			} else {
				guestCouponRequestDTO.setStudentId(guestCouponRequestDTO.getStudentId() != null ? guestCouponRequestDTO.getStudentId().toUpperCase() : null);
			}

			// field validation
			guestCouponRequestValidator.validate(guestCouponRequestDTO, bindingResult);

			// If there are validation errors, return to the form with errors
			if (bindingResult.hasErrors()) {
				commonResponseUtil.updateHeaderForm(request, model,
						messageSource.getMessage("message.label.guest.coupon.request.list", null, Locale.getDefault()),
						true);
				if (loginType != null && loginType.equals(ModelConstants.STUDENT)) {
					setOnlineCouponModelAttributes(model);
					setlModelAttributes(model);
					return HTMLPage.ADD_ONLINE_MESS_COUPON;
				} else {
					model.addAttribute("hostels", hostelService.getHostelList());
					setlModelAttributes(model);
					return HTMLPage.ADD_GUEST_COUPON_REQUEST;
				}
//            return Constants.REDIRECT +  addCouponRequestPath; // Return to the form with error messages
			}

			String status = service.saveCouponRequest(guestCouponRequestDTO);

			commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes);
		} catch (Exception e) {
			commonResponseUtil.exceptionMessageHandling(e,redirectAttributes);
		}
        return Constants.REDIRECT + guestCouponRequestPath;
    }
    
    private void setOnlineCouponModelAttributes(ModelMap model) {

		String rules =  simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_COUPON_RULES);
		rules = rules.replace("#%maxdays%#", simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_COUPON_VEG_MAX_DAYS))
				.replace("#%cutofftime%#", simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_COUPON_CUTOFF_TIME))
				.replace("#%nonVegDiscountedAmt%#", simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_COUPON_NONVEG_DISCOUNTED_AMOUNT));
		model.addAttribute("rules", rules);
	}
    
    private void setlModelAttributes(ModelMap model) {
    	model.addAttribute("messes", messService.getMessMasterList());
		model.addAttribute("vegDiscountedAmt", simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_COUPON_VEG_DISCOUNTED_AMOUNT));
		model.addAttribute("nonVegDiscountedAmt", simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_COUPON_NONVEG_DISCOUNTED_AMOUNT));
		model.addAttribute("vegMaxDays", simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_COUPON_VEG_MAX_DAYS));
		model.addAttribute("nonVegMaxDays", simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_COUPON_NONVEG_MAX_DAYS));
		model.addAttribute("cutoffTime", simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_COUPON_CUTOFF_TIME));
		model.addAttribute("messPeriodDto", messMasterCommonService.getCurrentMessPeriod());
		model.addAttribute("vegMessIds", messService.getOnlineCouponMessIdList("v"));
		model.addAttribute("nonVegMessIds", messService.getOnlineCouponMessIdList("nv"));
	}
    
    
    
	@GetMapping("${url.view}")
	public String viewGuestCouponRequest(ModelMap map, @RequestParam String encryptedId, HttpServletRequest request)
			throws Exception {
		String returnPage=HTMLPage.ADD_GUEST_COUPON_REQUEST;
		commonResponseUtil.updateCommonModelAttributes(map, request);
		
		String loginType= SecurityCtxUtil.accountType();
        String[] split = MCrypt.getInstance().decryptToString(encryptedId).split(Constants.BACKTICK);
		Long id=Long.valueOf(split[0]);
		map.addAttribute("guestCouponRequestDTO", service.viewGuestCouponDetails(id));
		
		if(loginType!=null && loginType.equals(ModelConstants.STUDENT)) {
        	map.addAttribute("rules", simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_COUPON_RULES));
			return HTMLPage.ADD_ONLINE_MESS_COUPON;
		}else {
			map.addAttribute("paymentTypeList",
					simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.PAYMENT_TYPE));
			map.addAttribute("paymentStatusList",
					simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.COUPON_PAYMENT_STATUS));
			map.addAttribute("messes", messService.getMessMasterList());
			return returnPage;
		}
	}

	@PostMapping("${url.view}")
	public String saveCouponPayment(@ModelAttribute GuestCouponRequestDTO dto, RedirectAttributes redirectAttributes)
			throws JsonProcessingException {
		String status = service.saveCouponPayment(dto);
		commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes);
		return Constants.REDIRECT + guestCouponRequestPath;
	}

	@GetMapping("${url.coupon.config.rate}")
    public @ResponseBody GuestCouponConfigDto getCouponRate(@RequestParam LocalDate fromDate, @RequestParam LocalDate toDate,
                                                            @RequestParam String category) {
		Set<String> categoriesSet = Set.of(
			messageSource.getMessage("message.coupon.category.projectStaff", null, Locale.getDefault()), 
		    messageSource.getMessage("message.coupon.category.hostelResidentsVeg", null, Locale.getDefault()), 
		    messageSource.getMessage("message.coupon.category.hostelResidentsNonVeg", null, Locale.getDefault())
		);
		if (!categoriesSet.contains(category.trim())) {
		    category = messageSource.getMessage("message.coupon.category.general", null, Locale.getDefault());
		}
        return couponService.getGuestCouponRates(fromDate, toDate, category);
    }

//	@PostMapping("${url.ldap.authentication}" + "${password}")
//	public @ResponseBody BaseResponse authenticateUserPassword(@PathVariable String password, ModelMap map,
//			HttpServletRequest request) throws Exception {
//		return CommonResponseUtil.updateResponseByStatus(service.authenticateUserPassword(password), "response.status.success", "response.invalid.ldap.credential");
//	}

	@PostMapping("${url.ldap.authentication}")
	public @ResponseBody BaseResponse authenticateUserPassword(@RequestBody Map<String, String> payload) throws Exception {
		String password = payload.get("password");
		return CommonResponseUtil.updateResponseByStatus(
				service.authenticateUserPassword(password),
				"response.status.success",
				"response.invalid.ldap.credential"
		);
	}


	@GetMapping("${url.pdf.download}" + "${encryptedId}")
	public String downloadGuestCouponPrintPDF(@PathVariable String encryptedId, HttpServletResponse response)
			throws Exception {
		String decrypted = MCrypt.getInstance().decryptToString(encryptedId);
		String[] splitByBacktick = decrypted.split(Constants.BACKTICK);
		String[] parts = splitByBacktick[0].split("~");

		Long id = Long.parseLong(parts[0]);
		Long time = Long.parseLong(parts[1]);
		String user = parts[2];

		return service.downloadGuestCouponPrintPDF(id, time, user, null, response);
	}

	@GetMapping("${url.approval}" + "${id}")
	public String getResendMail(ModelMap map, @PathVariable String id, HttpServletRequest request)
			throws Exception {
		map.addAttribute("requestId", id);
		return HTMLPage.APPROVE_GUEST_COUPON_REQUEST_MODAL;
	}

	@PostMapping("${url.approval}")
	public String approveRequest(@ModelAttribute GuestCouponRequestDTO guestCouponRequestDTO,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		try {
			String saveStatus = service.approveRequest(guestCouponRequestDTO.getRequestId());
			commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs, "response.approved.success");
		} catch (Exception e) {
			commonResponseUtil.exceptionMessageHandling(e,redirectAttrs);
		}
		return Constants.REDIRECT + guestCouponRequestPath;
	}

	@GetMapping("${url.resend.mail}" + "${id}")
	public String getSendMail(ModelMap map, @PathVariable String id, HttpServletRequest request) throws Exception {
		map.addAttribute("requestId", id);
		return HTMLPage.SEND_MAIL_GUEST_COUPON_REQUEST_MODAL;
	}

	@PostMapping("${url.resend.mail}")
	public String saveSendMail(@ModelAttribute GuestCouponRequestDTO guestCouponRequestDTO, HttpServletRequest request,
			HttpServletResponse response,  RedirectAttributes redirectAttrs) throws Exception {
		try {
			String saveStatus = service.sendMail(guestCouponRequestDTO.getRequestId(), response);
			commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs, "response.mail.success");
		} catch (Exception e) {
			commonResponseUtil.exceptionMessageHandling(e,redirectAttrs);
		}
		return Constants.REDIRECT + guestCouponRequestPath;
	}

	@DeleteMapping("${id}")
	public @ResponseBody BaseResponse deleteGuestCouponByRequestId(@PathVariable long id, ModelMap map,
			HttpServletRequest request) throws Exception {
        String userId = SecurityCtxUtil.userId();
		return CommonResponseUtil.generateDeleteResponseByStatus(service.deleteGuestCouponByRequestId(id,userId));
	}
	
	@GetMapping("${url.check.same.session}")
    public @ResponseBody boolean checkSameDateAndSession(@RequestParam String studentId, @RequestParam String date,@RequestParam String session) {
		LocalDate formattedDate= DateUtility.stringToLocalDate(date);
        return service.checkSameDateAndSession(studentId, formattedDate, session);
    }

	@GetMapping("${url.check.mess.availability}")
	public @ResponseBody int checkMessAvailability(@RequestParam Long messId, @RequestParam String date,@RequestParam String session) {
		LocalDate formattedDate= DateUtility.stringToLocalDate(date);
		return service.checkMessAvailability(messId, formattedDate, session);
	}

    @GetMapping("${url.check.samesession.messavail.within.dates}")
    public ResponseEntity<GuestCouponIssuedDTO> checkSameSessionAndMessAvailWithinDates(@RequestParam String studentId, @RequestParam Long messId,
                                                                                         @RequestParam LocalDate fromDate,@RequestParam LocalDate toDate) {
        GuestCouponIssuedDTO dto = service.checkSameSessionAndMessAvailWithinDates(studentId,messId, fromDate, toDate);
        if (dto != null) {
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

	@GetMapping("${url.check.student.allotted.mess}")
	public ResponseEntity<GuestCouponRequestDTO> checkStudentAllottedMessPeriod(
			@RequestParam String studentId,@RequestParam LocalDate fromDate,@RequestParam LocalDate toDate) {

		GuestCouponRequestDTO dto = service.checkStudentAllottedMessPeriod(studentId, fromDate, toDate);

		if (dto != null) {
			return ResponseEntity.ok(dto);
		} else {
			return ResponseEntity.noContent().build();
		}
	}


	@GetMapping("${url.initiate}"+"${id}")
    public String initiateTransaction(@PathVariable String id,ModelMap model,HttpServletRequest request,RedirectAttributes redirectAttributes) throws Exception {

		PaymentGatewayCcavenueDto pgDto = couponOnlinePaymentService.initiateTransaction(id,request);
        boolean hasErrors =  (pgDto.getErrorStatus() != null)
                || (pgDto.getErrorList() != null && !pgDto.getErrorList().isEmpty());
        if(hasErrors){
            redirectAttributes.addFlashAttribute("errorStatus", pgDto.getErrorStatus());
            redirectAttributes.addFlashAttribute("errorList", pgDto.getErrorList());
            return Constants.REDIRECT + guestCouponRequestPath;
        }else{
            // Pass encrypted data and access code to the Thymeleaf template
            model.addAttribute("pgDto", pgDto);
            return HTMLPage.CCAVENUE_HTML;
        }
    }
	
	@PostMapping("${url.payment.response}")
    public String savePaymentResponse(@RequestParam(value = "responseType", required = false) String responseType,ModelMap model,HttpServletRequest request) throws Exception {

		System.out.println("responseType------"+responseType);
		PaymentGatewayCcavenueDto pgDto = couponOnlinePaymentService.savePaymentResponse(responseType,request);

		if(responseType!=null & responseType.equals(Constants.CANCEL)) {
        	return Constants.REDIRECT + guestCouponRequestPath;
        }

		model.addAttribute("paymentGatewayDto", pgDto);
		model.addAttribute("backToList", guestCouponRequestPath);
        return HTMLPage.CCAVENUE_RESPONSE_HTML;
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

	@PostMapping("${url.reset}" + "${id}")
	public @ResponseBody String resetGuestCouponByRequestId(@PathVariable long id, ModelMap map, HttpServletRequest request) {
		return service.resetGuestCouponByRequestId(id,SecurityCtxUtil.userId()) ? Constants.UPDATED : Constants.ERROR;
	}

	@PostMapping("${url.enable.coupon}" + "${id}")
	public @ResponseBody BaseResponse enableGuestCouponByRequestId(@PathVariable long id) throws Exception {
		String userId = SecurityCtxUtil.userId();
		return CommonResponseUtil.generateDeleteResponseByStatus(service.enableGuestCouponByRequestId(id,userId));
	}
}
