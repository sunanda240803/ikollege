package com.iitm.hosteldine.service.student;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.ccavenue.security.AesCryptUtil;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.ConvocationReportRow;
import com.iitm.hosteldine.dto.paymentGatewayCC.PaymentGatewayCcavenueDto;
import com.iitm.hosteldine.dto.student.ConvocationAccommodationDto;
import com.iitm.hosteldine.dto.student.StudentDetailsInfoDto;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.mapper.paymentGatewayCC.PaymentGatewayCcavenueMapper;
import com.iitm.hosteldine.model.OtherCandidate.PaymentGatewayConfigCcavEntity;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewWithSettlementEntity;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.service.paymentGatewayCC.PaymentGatewayCcavenueService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.student.AllStudentsDetailsViewDto;
import com.iitm.hosteldine.dto.student.ConvocationAdditionalCouponsDto;
import com.iitm.hosteldine.mapper.student.ConvocationAccommodationMapper;
import com.iitm.hosteldine.mapper.student.ConvocationAdditionalCouponsMapper;
import com.iitm.hosteldine.model.student.ConvocationAccommodationEntity;
import com.iitm.hosteldine.model.student.ConvocationAdditionalCouponsEntity;
import com.iitm.hosteldine.repository.student.ConvocationAccommodationRepository;
import com.iitm.hosteldine.repository.student.ConvocationAdditionalCouponsRepository;
import com.iitm.hosteldine.service.AllStudentsDetailsViewService;
import com.iitm.hosteldine.service.SimsConfigDataService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccommodationMessConvocationService {
	
	private final SimsConfigDataService simsConfigDataService;
	private final ConvocationAccommodationRepository convocationRepo;
	private final ConvocationAdditionalCouponsRepository additionalCouponsRepo;
	private final AllStudentsDetailsViewService allStudentsDetailsViewService;
	private final CommonResponseUtil commonResponseUtil;
	private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;
	private final PaymentGatewayCcavenueService paymentGatewayCcavenueService;
	private final HostelMasterService hostelMasterService;
	private final MailTemplateRepository mailTemplateRepository;
	private final Utility utility;
	private final MailQueueService mailQueueService;
	private final StudentDetailsInfoService studentDetailsInfoService;
	private final StudentDetailsInfoRepository studentDetailsInfoRepository;

	@Value("${url.accommodation.mess.convocation}")
	private String baseUrl;

	@Value("${url.payment.response}")
	private String paymentResponse;

	private static final Map<String, String> COUPON_MAP = Map.of(
			"B", "BR",
			"L", "LC",
			"D", "DR"
	);

	public ConvocationAccommodationDto saveConvocationForm(ConvocationAdditionalCouponsDto dto, HttpServletRequest request)
			throws Exception {

		ConvocationAccommodationEntity savedEntity = null;
		List<ConvocationAdditionalCouponsEntity> additionalCouponsList = new ArrayList<>();
		try {
			ConvocationAccommodationEntity convocationEntity = ConvocationAccommodationMapper.INSTANCE
					.toConvocationAccommodationEntity(dto.getConvocation());
			convocationEntity.setAccommodationStatus(isAccommodationNeeded(dto));
			if (!isHostelRequired(dto)) {
				convocationEntity.setHostelName(null);
			}
			ArrayList<String> convocationDates = simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.CONVOCATION_DATES);
			if (convocationDates != null && !convocationDates.isEmpty()) {
				String dateRange = convocationDates.getFirst().trim();
				String[] parts = dateRange.split(ModelConstants.TILDE);
				if (parts.length == 2) {
					LocalDate startDate = LocalDate.parse(parts[0].trim().replace(ModelConstants.SLASH, ModelConstants.HYPHEN));
					LocalDate endDate   = LocalDate.parse(parts[1].trim().replace(ModelConstants.SLASH, ModelConstants.HYPHEN));
					convocationEntity.setDiningFromDate(startDate);
					convocationEntity.setDiningToDate(endDate);
				}
			}
			String complimentaryCoupons = Stream.of(dto.getBf(), dto.getLn(), dto.getDn()).filter(Objects::nonNull)
					.collect(Collectors.joining());
			convocationEntity.setComplimentaryCoupons(complimentaryCoupons != null ? complimentaryCoupons : Strings.EMPTY);

			int additionalCouponsCount = 0;
			boolean isAdditionalCoupon=dto.getAdditionalCoupons()!=null && dto.getAdditionalCoupons().size()>0;
			if(isAdditionalCoupon) {
				for (ConvocationAdditionalCouponsDto coupons : dto.getAdditionalCoupons()) {
					ConvocationAdditionalCouponsEntity additionalCouponsEntity =
							ConvocationAdditionalCouponsMapper.INSTANCE.toConvocationAdditionalCouponsEntity(coupons);

					int bf = Optional.ofNullable(additionalCouponsEntity.getNoOfBreakfastCoupon()).orElse(0);
					int ln = Optional.ofNullable(additionalCouponsEntity.getNoOfLunchCoupon()).orElse(0);
					int dn = Optional.ofNullable(additionalCouponsEntity.getNoOfDinnerCoupon()).orElse(0);

					additionalCouponsCount += bf + ln + dn;

					additionalCouponsEntity.setNoOfBreakfastCoupon(bf);
					additionalCouponsEntity.setNoOfLunchCoupon(ln);
					additionalCouponsEntity.setNoOfDinnerCoupon(dn);

					if (bf + ln + dn != 0)additionalCouponsList.add(additionalCouponsEntity);
				}
			}
			convocationEntity.setAdditionalNoOfCoupons(additionalCouponsCount);

			boolean onlinePayment=((dto.getConvocation().getAccommodationStatus()!=null && dto.getConvocation().getAccommodationStatus().equals(true) )
					|| (additionalCouponsCount>0));
			//Get Order number for online payment
			if(onlinePayment) {
				Integer orderId = convocationRepo.getNextVal();
				String orderNo=null;
				PaymentGatewayConfigCcavEntity urlEntity = paymentGatewayCcavenueService.getCredentialsForUrl(request);
				if (urlEntity.getInstance() != null && urlEntity.getInstance().equals(ModelConstants.PRODUCTION)) {
					orderNo = "CONVCOUPON" + orderId;
				} else {
					orderNo = "CONVCOUPONTEST" + orderId;
				}
				convocationEntity.setOrderNo(orderNo);
				convocationEntity.setRetryCount(0);
				convocationEntity.setPaymentStatus("Initiated");
			}

			savedEntity = convocationRepo.save(convocationEntity);

			if (savedEntity != null) {
				//Mail
				if(!onlinePayment) {
					triggerMail(savedEntity);
				}

				if(additionalCouponsCount>0) {
					for (ConvocationAdditionalCouponsEntity coupon : additionalCouponsList) {
						coupon.setConvocation(savedEntity);
					}
					List<ConvocationAdditionalCouponsEntity> savedCoupons = additionalCouponsRepo.saveAll(additionalCouponsList);
				}

				return ConvocationAccommodationMapper.INSTANCE.fromConvocationAccommodationEntity(savedEntity);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(commonResponseUtil.getMessage("message.exception.invalid.operation"));
		}


		return null;
	}

	public String CheckStudentExists(String id) {
		StudentDetailsInfoDto dto = studentDetailsInfoService.getStudentInfoDetails(id);
		return dto != null ? dto.getStudentName() : null;
	}

	public boolean isStudentAlreadyAppliedAccommodation(String studentId) {
		boolean isConvocationAppliedValidationNeeded = Boolean.parseBoolean(simsConfigDataService.getSimConfigValue(SimsConfigDataService.CONVOCATION_APPLIED_VALIDATION_NEEDED));
		if (!isConvocationAppliedValidationNeeded) {
			return false;
		}
		return convocationRepo.existsBlockingConvocationAccommodation(studentId, ModelConstants.STATUS_ACTIVE, Constants.SUCCESS);
	}

	private boolean isAccommodationNeeded(ConvocationAdditionalCouponsDto dto) {
		String preference = dto.getConvocation().getAccommodationPreference();
		if (ModelConstants.ACCOMMODATION_PREFERENCE_ACCOMMODATION.equals(preference)) {
			return true;
		}
		if (ModelConstants.ACCOMMODATION_PREFERENCE_HOSTEL.equals(preference)
				|| ModelConstants.ACCOMMODATION_PREFERENCE_HOSTEL_NOT_NEEDED.equals(preference)) {
			return false;
		}
		return Boolean.TRUE.equals(dto.getConvocation().getAccommodationStatus());
	}

	private boolean isHostelRequired(ConvocationAdditionalCouponsDto dto) {
		return ModelConstants.ACCOMMODATION_PREFERENCE_HOSTEL
				.equals(dto.getConvocation().getAccommodationPreference());
	}

	public PaymentGatewayCcavenueDto initiateTransaction(ConvocationAccommodationDto dto, HttpServletRequest request) throws Exception {
		String studentId = dto.getStudentId().toUpperCase();
		AllStudentsDetailsViewWithSettlementEntity studentEntity = allStudentsDetailsViewRepository.getCompleteStudentDetailsWithSettlement(studentId)
				.orElse(null);
		PaymentGatewayCcavenueDto pgDto = PaymentGatewayCcavenueMapper.INSTANCE.toPGDtoFromStudent_2(studentEntity);
		pgDto.setOrderId(dto.getOrderNo());
		pgDto.setTotalAmount(dto.getOverallAmount());
		pgDto.setRedirectUrl(paymentGatewayCcavenueService.getBaseUrl(request) + baseUrl + paymentResponse + Constants.AFTER_PAYMENTGATEWAY);
		pgDto.setCancelUrl(paymentGatewayCcavenueService.getBaseUrl(request) + baseUrl + paymentResponse + Constants.PAYMENT_CANCEL);
		return paymentGatewayCcavenueService.redirectPaymentGateway(pgDto, request);
	}

	public PaymentGatewayCcavenueDto savePaymentResponse(String responseType, HttpServletRequest request) throws Exception {
		PaymentGatewayCcavenueDto returnDto = new PaymentGatewayCcavenueDto();

		System.out.println("encResp---" + request.getParameter("encResp") + "\n");

		String decResp = paymentGatewayCcavenueService.decryptResponse(request);
		System.out.println("decryptedResp---" + decResp + "\n");

		// Parse the decrypted response into a map
		Map<String, Object> resMap = paymentGatewayCcavenueService.parseDecryptedResponse(decResp);
		System.out.println("MapResponse---" + resMap + "\n");

		//Update the response in "IIT_PS_TEMP_ACCOM_PAYMENT_TRANSACTION_DETAILS" table
		String orderNo = resMap.get("order_id").toString();
		System.out.println("order_id---" + orderNo + "\n");

		ConvocationAccommodationEntity transaction = convocationRepo.findByOrderNoAndActiveFlag(orderNo, ModelConstants.STATUS_ACTIVE).orElse(null);
		if (transaction != null) {
			System.out.println("trans entity order_id---" + transaction.getOrderNo() + "\n");
			ConvocationAccommodationEntity updatedTransaction = PaymentGatewayCcavenueMapper.mapToConvocationTransactionEntity(transaction, resMap);
			ConvocationAccommodationEntity updatedTrans = convocationRepo.save(updatedTransaction);

			if (responseType != null && responseType.equalsIgnoreCase(Constants.AFTER_PAYMENT)) {
				//set the response to dto
				returnDto = PaymentGatewayCcavenueMapper.changeToPGDto(resMap);
				//Send mail to student after payment success
				if(updatedTrans.getPaymentStatus()!=null && updatedTrans.getPaymentStatus().equalsIgnoreCase(Constants.SUCCESS)) {
					triggerMail(updatedTrans);
				}
			}
		}
		return returnDto;
	}

	private boolean triggerMail(ConvocationAccommodationEntity convEntity) {
		MailTemplateEntity mailTemplateEntity = mailTemplateRepository.findByActiveFlagAndMailType(ModelConstants.STATUS_ACTIVE, ModelConstants.Convocation_Purchase_Confirmation)
				.orElse(null);
		if (Objects.nonNull(mailTemplateEntity)) {

			String formattedCoupons = Optional.ofNullable(convEntity.getComplimentaryCoupons())
					.filter(s -> !s.isBlank())
					.map(s -> s.chars()
							.mapToObj(ch -> String.valueOf((char) ch))
							.map(code -> COUPON_MAP.getOrDefault(code, code))
							.collect(Collectors.joining(", ")))
					.orElse("");

//			AllStudentsDetailsViewEntity studentInfoDetails = allStudentsDetailsViewRepository.findBystudentId(convEntity.getStudentId().toUpperCase())
//					.orElse(null);

			String mailBcc = simsConfigDataService.getSimConfigValue(SimsConfigDataService.CONVOCATION_MAIL_BCC);

			//String greetingMessage = studentInfoDetails!=null ? studentInfoDetails.getStudentName() + "(" + convEntity.getStudentId().toUpperCase() + ")" : "";
			String greetingMessage = convEntity!=null ? convEntity.getStudentName() + " (" + convEntity.getStudentId().toUpperCase() + ")" : "";
			String messageTemplate = mailTemplateEntity.getMailTemplate()
					.replace("#%purchaseDate%#", utility.dateFormatter(LocalDate.now()))
					.replace("#%studentId%#", convEntity.getStudentId())
					.replace("#%accommStatus%#",  Boolean.TRUE.equals(convEntity.getAccommodationStatus()) ? "Yes" : "No")
					.replace("#%complementary%#", formattedCoupons)
					.replace("#%additionalCoupon%#", String.valueOf(convEntity.getAdditionalNoOfCoupons()))
					.replace("#%totalAmount%#", utility.formatCommaSeperatedCurrency(convEntity.getOverallAmount()));

			try {
				mailQueueService.saveMailQueue(mailTemplateEntity.getMailSubject(), greetingMessage, messageTemplate,
						convEntity.getMailId(), "Convocation Purchase Email",
						null, null, null, null, ModelConstants.REGARDS, ModelConstants.CCW_OFFICE, mailBcc);
				return true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return true;
	}

	public int updateConvOnlineCouponAllPendingRequests(HttpServletRequest request) throws Exception {
		int count = 0;
		try {
			System.out.println("----into---------updateConvOnlineCouponAllPendingRequests---------");
			/*
			 * Fetch pending requests from 'IITM_CONVOCATION_ACCOMMODATION'
			 */

			int retry_count = Integer.valueOf(simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_MESSS_COUPON_RETRY_COUNT));
			String pending_status = simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_MESSS_COUPON_PENDING_STATUS);
			//List.of("Initiated", "Awaited", "Invalid", "Timeout");
			List<String> statuses = Arrays.asList(pending_status.split(ModelConstants.COMMA));

			List<String> orderNoList = convocationRepo.getConvOnlinePaymentPendingOrderNo(ModelConstants.STATUS_ACTIVE, Constants.SUCCESS, retry_count, statuses);
			if (!orderNoList.isEmpty()) {
				PaymentGatewayConfigCcavEntity ccAvenEntity = paymentGatewayCcavenueService.getCredentialsForUrl(request);
				final String accessCode = Objects.requireNonNull(ccAvenEntity.getAccessCode(), "accessCode is null");
				final String workingKey = Objects.requireNonNull(ccAvenEntity.getWorkingKey(), "workingKey is null");

				AesCryptUtil aes = new AesCryptUtil(workingKey);
				System.out.println("accessCode---" + accessCode + "\n");
				System.out.println("workingKey---" + workingKey + "\n");

				for (String orderNo : orderNoList) {
					System.out.println("orderNo---" + orderNo + "\n");
					if (orderNo == null || orderNo.isBlank()) continue;

					JSONObject gatewayJson =
							paymentGatewayCcavenueService.getPaymentStatusFromAPI(orderNo,accessCode,workingKey,aes);

					if (gatewayJson != null) {
						count += updateOnlinePaymentDetailsCron(gatewayJson);
					}
				}
			}

		} catch (Exception e) {
			throw new Exception(e);
		}
		return count;
	}

	@Transactional
	public int updateOnlinePaymentDetailsCron(JSONObject jsonResponse) throws Exception {
		int count = 0;
		String orderNo = jsonResponse.getString("order_no");
		try {
			ConvocationAccommodationEntity paymentEntity = convocationRepo.findByOrderNoAndActiveFlag(orderNo, ModelConstants.STATUS_ACTIVE).orElse(null);
			if(paymentEntity!=null) {
				// 2. Handle retry count and payment status update
				int retryCount = paymentEntity.getRetryCount();
				String orderStatus = paymentGatewayCcavenueService.getOrderStatus(jsonResponse);
				System.out.println("retryCount---" + retryCount + "\n");
				if (jsonResponse.has("status") && jsonResponse.optString("status").equals("0")) {
					// 3. Update the payment entity with details from the response
					paymentEntity = updatePaymentEntity(paymentEntity, jsonResponse, orderStatus, retryCount);
					if (paymentEntity != null) {
						count++;
					}
				} else if (jsonResponse.has("status") && jsonResponse.optString("status").equals("1") &&
						jsonResponse.has("error_code")) {
					paymentEntity.setRetryCount(retryCount + 1);
					paymentEntity.setStatusMessage(jsonResponse.optString("error_desc"));
					paymentEntity.setModifiedBy(Constants.ONLINE_JOB);
					convocationRepo.save(paymentEntity);
				}
			}

		} catch (Exception ex) {
			System.err.println("updateOnlinePaymentDetailsCron failed for order " + orderNo + ": " + ex.getMessage());
			throw new Exception(ex);
		}
		// 5. Return the number of records updated
		return count;
	}

	private ConvocationAccommodationEntity updatePaymentEntity(ConvocationAccommodationEntity paymentEntity, JSONObject jsonResponse,
															   String orderStatus, int retryCount) {
		// Update the fields of the payment entity from the JSON response
		paymentEntity.setPaymentAmount(jsonResponse.optDouble("order_gross_amt"));
		paymentEntity.setPaymentReferenceNo(jsonResponse.optString("order_bank_ref_no"));
		paymentEntity.setCcavReferenceNo(jsonResponse.optString("reference_no"));
		paymentEntity.setPaymentType(jsonResponse.optString("order_option_type")+" - "+jsonResponse.optString("order_card_name"));
		paymentEntity.setPaymentStatus(orderStatus);
		paymentEntity.setTransFee(jsonResponse.optDouble("order_fee_perc_value", 0.0));
		paymentEntity.setServiceTax(jsonResponse.optDouble("order_tax"));
		paymentEntity.setPaymentDate(DateUtility.parseToLocalDateTime2(jsonResponse.getString("order_date_time")));
		paymentEntity.setStatusMessage(jsonResponse.optString("order_bank_response", ""));
		paymentEntity.setRetryCount(retryCount + 1);

		paymentEntity = convocationRepo.save(paymentEntity);
		return paymentEntity;
	}

	public List<ConvocationAdditionalCouponsDto> getConvocationList(LocalDate fromDate, LocalDate toDate, String paymentStatus) {
		if (fromDate != null && toDate == null) {
			toDate = LocalDate.now();
		}
		boolean includeNullPaymentStatus = paymentStatus == null || paymentStatus.trim().isEmpty();
		String effectivePaymentStatus = includeNullPaymentStatus ? Constants.SUCCESS : paymentStatus.trim();
		LocalDateTime fromDateTime = fromDate != null
				? fromDate.atStartOfDay()
				: LocalDate.of(1900, 1, 1).atStartOfDay();

		LocalDateTime toDateTime = toDate != null
				? toDate.plusDays(1).atStartOfDay()
				: LocalDate.of(9999, 12, 31).atStartOfDay();

		return additionalCouponsRepo.getConvocationAdditionalCoupons(ModelConstants.STATUS_ACTIVE, fromDateTime, toDateTime, effectivePaymentStatus, includeNullPaymentStatus)
				.stream()
				.map(this::toConvocationReportDto)
				.toList();
	}

	private ConvocationAdditionalCouponsDto toConvocationReportDto(ConvocationReportRow row) {
		ConvocationAccommodationDto convocationDto = new ConvocationAccommodationDto();
		convocationDto.setId(row.convocationId());
		convocationDto.setCreatedAt(row.createdAt());
		convocationDto.setStudentId(row.studentId());
		convocationDto.setStudentName(row.studentName());
		convocationDto.setGender(Constants.MALE.equalsIgnoreCase(row.gender()) ? Constants.MALE_FULL_FORM : Constants.FEMALE_FULL_FORM);
		convocationDto.setMailId(row.mailId());
		convocationDto.setMenuType(row.menuType());
		convocationDto.setAccommodationStatus(row.accommodationStatus());
		convocationDto.setComplimentaryCoupons(row.complimentaryCoupons());
		convocationDto.setAdditionalNoOfCoupons(row.additionalNoOfCoupons());
		convocationDto.setOverallAmount(row.overallAmount());
		convocationDto.setPaymentStatus(row.paymentStatus());
		convocationDto.setHostelName(row.hostelName());

		ConvocationAdditionalCouponsDto dto = new ConvocationAdditionalCouponsDto();
		dto.setId(row.couponId());
		dto.setDiningDate(row.diningDate());
		dto.setNoOfBreakfastCoupon(row.noOfBreakfastCoupon());
		dto.setNoOfLunchCoupon(row.noOfLunchCoupon());
		dto.setNoOfDinnerCoupon(row.noOfDinnerCoupon());
		dto.setConvocation(convocationDto);

		return dto;
	}

	public Workbook generateConvocationExcelReport(LocalDate parsedFromDate, LocalDate parsedToDate, String paymentStatus) throws Exception {
		String reportTitle = "Accommodation Mess Convocation Report";

		try {
			List<ConvocationAdditionalCouponsDto> reportList = getConvocationList(parsedFromDate, parsedToDate, paymentStatus);
			List<LocalDate> convocationDates = getReportConvocationDates(reportList);

			Map<Long, List<ConvocationAdditionalCouponsDto>> accommodationCouponMap =
					groupCouponsByAccommodation(reportList);

			int fixedColumns = 12; // SL No to Complimentary DR
			int dynamicDateColumns = convocationDates.size() * 3;
			int additionalTotalColumn = fixedColumns + dynamicDateColumns;
			int overallAmountColumn = additionalTotalColumn + 1;
			int paymentStatusColumn = overallAmountColumn + 1;
			int totalColumns = paymentStatusColumn + 1;

			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(reportTitle);
			ExcelUtility excelUtility = new ExcelUtility();

			XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
			headerStyle.setWrapText(true);

			XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
			dataStyle.setWrapText(true);

			XSSFCellStyle centerDataStyle = workbook.createCellStyle();
			centerDataStyle.cloneStyleFrom(dataStyle);
			centerDataStyle.setAlignment(HorizontalAlignment.CENTER);
			centerDataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			XSSFCellStyle columnHeaderStyle = excelUtility.setHeaderStyle(workbook);
			columnHeaderStyle.setWrapText(true);
			columnHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
			columnHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			XSSFRow rowheadFirst = sheet.createRow(1);
			excelUtility.createCell(rowheadFirst, 0, commonResponseUtil.getMessage("message.iitm.report.header"), headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, totalColumns - 1));

			XSSFRow rowheadSecond = sheet.createRow(2);
			excelUtility.createCell(rowheadSecond, 0, reportTitle, headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, totalColumns - 1));

			XSSFRow rowheadThird = sheet.createRow(3);
			SimpleDateFormat sdf = new SimpleDateFormat(commonResponseUtil.getMessage("session.date.format"));
			String reportDate = commonResponseUtil.getMessage("message.label.report.date.colon") + sdf.format(new Date());
			excelUtility.createCell(rowheadThird, 0, reportDate, headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, totalColumns - 1));

			XSSFRow mainHeader = sheet.createRow(4);
			XSSFRow subHeader = sheet.createRow(5);

			createRowSpanHeader(sheet, excelUtility, mainHeader, 0, "SL No", columnHeaderStyle);
			createRowSpanHeader(sheet, excelUtility, mainHeader, 1, "Requested Date", columnHeaderStyle);
			createRowSpanHeader(sheet, excelUtility, mainHeader, 2, "Student ID", columnHeaderStyle);
			createRowSpanHeader(sheet, excelUtility, mainHeader, 3, "Student Name", columnHeaderStyle);
			createRowSpanHeader(sheet, excelUtility, mainHeader, 4, "Gender", columnHeaderStyle);
			createRowSpanHeader(sheet, excelUtility, mainHeader, 5, "Mail ID", columnHeaderStyle);
			createRowSpanHeader(sheet, excelUtility, mainHeader, 6, "Menu Type", columnHeaderStyle);
			createRowSpanHeader(sheet, excelUtility, mainHeader, 7, "Accommodation Status", columnHeaderStyle);
			createRowSpanHeader(sheet, excelUtility, mainHeader, 8, "Hostel Name", columnHeaderStyle);

			excelUtility.createCell(mainHeader, 9, "Complimentary Coupons", columnHeaderStyle);
			excelUtility.createCell(mainHeader, 10, ModelConstants.EMPTY_STRING, columnHeaderStyle);
			excelUtility.createCell(mainHeader, 11, ModelConstants.EMPTY_STRING, columnHeaderStyle);
			sheet.addMergedRegion(new CellRangeAddress(4, 4, 9, 10));

			excelUtility.createCell(subHeader, 9, "BF", columnHeaderStyle);
			excelUtility.createCell(subHeader, 10, "LC", columnHeaderStyle);
			excelUtility.createCell(subHeader, 11, "DR", columnHeaderStyle);

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);

			int column = fixedColumns;
			for (LocalDate convocationDate : convocationDates) {
				excelUtility.createCell(mainHeader, column, convocationDate.format(dateFormatter), columnHeaderStyle);
				excelUtility.createCell(mainHeader, column + 1, ModelConstants.EMPTY_STRING, columnHeaderStyle);
				excelUtility.createCell(mainHeader, column + 2, ModelConstants.EMPTY_STRING, columnHeaderStyle);
				sheet.addMergedRegion(new CellRangeAddress(4, 4, column, column + 2));

				excelUtility.createCell(subHeader, column, "BF", columnHeaderStyle);
				excelUtility.createCell(subHeader, column + 1, "LC", columnHeaderStyle);
				excelUtility.createCell(subHeader, column + 2, "DR", columnHeaderStyle);

				column += 3;
			}

			createRowSpanHeader(sheet, excelUtility, mainHeader, additionalTotalColumn, "Additional No. of Coupons", columnHeaderStyle);
			createRowSpanHeader(sheet, excelUtility, mainHeader, overallAmountColumn, "Overall Amount", columnHeaderStyle);
			createRowSpanHeader(sheet, excelUtility, mainHeader, paymentStatusColumn, "Payment Status", columnHeaderStyle);

			int rowCount = 5;
			int sNo = 0;

			for (List<ConvocationAdditionalCouponsDto> coupons : accommodationCouponMap.values()) {
				ConvocationAccommodationDto convocation = firstAccommodation(coupons);
				if (convocation == null) {
					continue;
				}

				Map<LocalDate, int[]> couponCountsByDate = new HashMap<>();
				for (ConvocationAdditionalCouponsDto coupon : coupons) {
					if (coupon.getDiningDate() == null) {
						continue;
					}

					int[] counts = couponCountsByDate.computeIfAbsent(coupon.getDiningDate(), key -> new int[3]);
					counts[0] += defaultInt(coupon.getNoOfBreakfastCoupon());
					counts[1] += defaultInt(coupon.getNoOfLunchCoupon());
					counts[2] += defaultInt(coupon.getNoOfDinnerCoupon());
				}

				XSSFRow row = sheet.createRow(++rowCount);
				String complimentaryCoupons = safe(convocation.getComplimentaryCoupons());
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_TIME_FORMAT);
				excelUtility.createCell(row, 0, ++sNo, centerDataStyle);
				excelUtility.createCell(
						row,
						1,
						convocation.getCreatedAt() != null
								? convocation.getCreatedAt().format(formatter)
								: ModelConstants.EMPTY_STRING,
						centerDataStyle
				);
				excelUtility.createCell(row, 2, safe(convocation.getStudentId()), dataStyle);
				excelUtility.createCell(row, 3, safe(convocation.getStudentName()), dataStyle);
				excelUtility.createCell(row, 4, safe(convocation.getGender()), dataStyle);
				excelUtility.createCell(row, 5, safe(convocation.getMailId()), dataStyle);
				excelUtility.createCell(row, 6, safe(convocation.getMenuType()), dataStyle);
				excelUtility.createCell(row, 7, accommodationStatus(convocation), centerDataStyle);
				excelUtility.createCell(row, 8, safe(convocation.getHostelName()), dataStyle);

				excelUtility.createCell(row, 9, hasCoupon(complimentaryCoupons, "B") ? "Yes" : "No", centerDataStyle);
				excelUtility.createCell(row, 10, hasCoupon(complimentaryCoupons, "L") ? "Yes" : "No", centerDataStyle);
				excelUtility.createCell(row, 11, hasCoupon(complimentaryCoupons, "D") ? "Yes" : "No", centerDataStyle);

				int additionalTotal = 0;
				column = fixedColumns;

				for (LocalDate convocationDate : convocationDates) {
					int[] counts = couponCountsByDate.getOrDefault(convocationDate, new int[3]);

					additionalTotal += counts[0] + counts[1] + counts[2];

					excelUtility.createCell(row, column, counts[0], centerDataStyle);
					excelUtility.createCell(row, column + 1, counts[1], centerDataStyle);
					excelUtility.createCell(row, column + 2, counts[2], centerDataStyle);

					column += 3;
				}

				excelUtility.createCell(row, additionalTotalColumn, additionalTotal, centerDataStyle);
				excelUtility.createCell(row, overallAmountColumn, formatAmount(convocation.getOverallAmount()), centerDataStyle);
				excelUtility.createCell(row, paymentStatusColumn, safe(convocation.getPaymentStatus()), centerDataStyle);
			}

			for (int i = 0; i < totalColumns; i++) {
				sheet.setColumnWidth(i, (i == 1 || i == 3 || i == 5 || i == 7) ? 7000 : 4000);
			}

			return workbook;
		} catch (Exception exception) {
			throw new Exception("Error in generating convocation report", exception);
		}
	}

	private List<LocalDate> getConfiguredConvocationDates() {
		ArrayList<String> convocationDateRanges = simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.CONVOCATION_DATES);
		if (convocationDateRanges == null || convocationDateRanges.isEmpty()) {
			return List.of();
		}
		Set<LocalDate> dates = new LinkedHashSet<>();
		for (String dateRange : convocationDateRanges) {
			if (dateRange == null || dateRange.trim().isEmpty()) {
				continue;
			}
			String[] parts = dateRange.trim().split(ModelConstants.TILDE);
			if (parts.length != 2) {
				continue;
			}
			LocalDate startDate = LocalDate.parse(parts[0].trim().replace(ModelConstants.SLASH, ModelConstants.HYPHEN));
			LocalDate endDate = LocalDate.parse(parts[1].trim().replace(ModelConstants.SLASH, ModelConstants.HYPHEN));
			for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
				dates.add(date);
			}
		}
		return new ArrayList<>(dates);
	}

	private Map<Long, List<ConvocationAdditionalCouponsDto>> groupCouponsByAccommodation(List<ConvocationAdditionalCouponsDto> reportList) {
		return reportList.stream()
				.filter(dto -> dto.getConvocation() != null && dto.getConvocation().getId() != null)
				.collect(Collectors.groupingBy(dto -> dto.getConvocation().getId(), LinkedHashMap::new, Collectors.toList()));
	}

	private ConvocationAccommodationDto firstAccommodation(List<ConvocationAdditionalCouponsDto> coupons) {
		return coupons.stream()
				.map(ConvocationAdditionalCouponsDto::getConvocation)
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);
	}

	private List<LocalDate> getReportConvocationDates(List<ConvocationAdditionalCouponsDto> reportList) {
		List<LocalDate> configuredDates = getConfiguredConvocationDates();
		Set<LocalDate> couponDates = reportList.stream()
				.map(ConvocationAdditionalCouponsDto::getDiningDate)
				.filter(Objects::nonNull)
				.collect(Collectors.toCollection(TreeSet::new));
		boolean hasMatchingCouponDate = configuredDates.stream().anyMatch(couponDates::contains);
		if (!configuredDates.isEmpty() && hasMatchingCouponDate) {
			return configuredDates;
		}
		return new ArrayList<>(couponDates);
	}

	private void createRowSpanHeader(XSSFSheet sheet, ExcelUtility excelUtility, XSSFRow row,
									 int column, String value, XSSFCellStyle style) {
		excelUtility.createCell(row, column, value, style);
		sheet.addMergedRegion(new CellRangeAddress(4, 5, column, column));
	}

	private int defaultInt(Integer value) {
		return value != null ? value : 0;
	}

	private boolean hasCoupon(String coupons, String couponCode) {
		return coupons != null && coupons.toUpperCase(Locale.ROOT).contains(couponCode);
	}

	private String accommodationStatus(ConvocationAccommodationDto convocation) {
		return convocation != null && Boolean.TRUE.equals(convocation.getAccommodationStatus()) ? "Yes" : "No";
	}

	private String safe(String value) {
		return value != null ? value : ModelConstants.EMPTY_STRING;
	}

	private String formatAmount(Double amount) {
		return amount != null ? String.format(Locale.US, "%.2f", amount) : "0.00";
	}
}
