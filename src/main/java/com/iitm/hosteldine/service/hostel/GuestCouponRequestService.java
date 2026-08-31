package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.config.MyAuthenticationProvider;
import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.MyUserDetails;
import com.iitm.hosteldine.dto.hostel.*;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.exception.GlobalExceptionHandler;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.hostel.PaymentAdviceMapper;
import com.iitm.hosteldine.model.hostel.GuestCouponMappingsEntity;
import com.iitm.hosteldine.model.hostel.GuestCouponOnlinePaymentEntity;
import com.iitm.hosteldine.model.hostel.GuestCouponPaymentAdviceEntity;
import com.iitm.hosteldine.model.hostel.HostelMasterEntity;
import com.iitm.hosteldine.model.mess.MessMasterEntity;
import com.iitm.hosteldine.repository.hostel.*;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.repository.studentDashboard.StudentMessDetailsRepository;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.MyUserDetailsService;
import com.iitm.hosteldine.service.PdfActionService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.util.CommonEnum;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.MD5Encryption;
import com.iitm.hosteldine.validator.common.ValidationCommon;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class GuestCouponRequestService {
    private final GuestCouponRequestRepository guestCouponRepository;
    private final GuestCouponPaymentAdviceRepository paymentAdviceRepository;
	private final GuestCouponMappingsService guestCouponMappingsService;
	private final PdfActionService pdfActionService;
	private final MessageSource messageSource;
	private final PdfActionService pdfActiveService;
    private final SimsConfigDataService simsConfigDataService;
	private final FileService fileService;
	private final MailTemplateRepository mailTemplateRepository;
	private final MailQueueService mailQueueService;
	private final HostelMasterRepository hostelMasterRepository;
	private final MessMasterRepository messMasterRepository;
	private final MyUserDetailsService myUserDetailsService;
	private final MyAuthenticationProvider myAuthenticationProvider;
    private final GuestCouponConfigRepository guestCouponConfigRepository;
	private final StudentMessDetailsRepository studentMessDetailsRepository;
    private final GuestCouponOnlinePaymentRepository guestCouponOnlinePaymentRepository;

    @Value("${url.error}")
    private String errorPage;
	
	public static final String GUEST_COUPON_REQUEST = "GUEST_COUPON_REQUEST";
	public static final String MAIL = "mail";

    @Transactional
    public GuestCouponRequestResultDTO getRequestedCoupons(PaginationForm form,String loginType) {
        GuestCouponRequestResultDTO returnDto=new GuestCouponRequestResultDTO();
        boolean deleteStatus=false;
        String studentId = ValidationCommon.toStringOrNull(form.getAdditionalParam().get("studentId"));
        String category = ValidationCommon.toStringOrNull(form.getAdditionalParam().get("category"));

        if(messageSource.getMessage("message.online.coupon", null, Locale.getDefault()).equalsIgnoreCase(category)
			&& (loginType != null && loginType.equals(ModelConstants.STUDENT))) {
            int expireDuration = Integer.valueOf(simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_PENDING_EXPIRED_DURATION));
            List<Long> expiredRequestIds = paymentAdviceRepository.findExpiredRequestIds(messageSource.getMessage("message.online.coupon", null, Locale.getDefault()),
                    studentId, Constants.PENDING, ModelConstants.STATUS_ACTIVE, expireDuration);
            for (Long requestId : expiredRequestIds) {
                deleteStatus |= deleteGuestCouponByRequestId(requestId,"Admin");
            }
        }
        returnDto.setDeleteStatus(deleteStatus);

        int page = form.getPage() - 1;
        Pageable pageable = PageRequest.of(page, form.getSize());
        
		String name = ValidationCommon.toStringOrNull(form.getAdditionalParam().get("name"));
//		String studentId = ValidationCommon.toStringOrNull(form.getAdditionalParam().get("studentId"));
		LocalDate diningFrom = ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("diningFrom"));
		LocalDate diningTo = ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("diningTo"));
		LocalDate submittedFrom = ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("submittedFrom"));
		LocalDate submittedTo = ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("submittedTo"));
//		String category = ValidationCommon.toStringOrNull(form.getAdditionalParam().get("category"));
		String paymentStatus = ValidationCommon.toStringOrNull(form.getAdditionalParam().get("paymentStatus"));

        Object[] rawResult = guestCouponRepository.getRequestCoupons(ValidationCommon.isValid(name) ? name.trim() : null,
                ValidationCommon.isValid(studentId) ? studentId.trim() : null,
                ValidationCommon.isValid(category) ? category.trim() : null, diningFrom, diningTo,
                ValidationCommon.isValid(paymentStatus) ? paymentStatus.trim() : null, submittedFrom, submittedTo);
        returnDto.setReqList(Arrays.stream(rawResult).map(data -> mapToDTO((Object[]) data)).toList());

        return returnDto;
    }

    public GuestCouponRequestResultDTO mapToDTO(Object[] record) {
        GuestCouponRequestResultDTO dto = new GuestCouponRequestResultDTO();

        dto.setRequestId(record[0] != null ? ((Number) record[0]).intValue() : 0);
        dto.setCategory(record[1] != null ? record[1].toString() : null);
        dto.setStudentId(record[2] != null ? record[2].toString().toUpperCase() : null);
        dto.setName(record[3] != null ? record[3].toString() : null);
        dto.setDiningFrom(record[4] != null ? (Date) record[4] : null);
        dto.setDiningTo(record[5] != null ? (Date) record[5] : null);
        dto.setPaymentStatus(record[6] != null ? record[6].toString() : null);
        dto.setNumOfBreakfast(record[7] != null ? record[7].toString() : null);
        dto.setNumOfLunch(record[8] != null ? record[8].toString() : null);
        dto.setNumOfDinner(record[9] != null ? record[9].toString() : null);
        dto.setTotalAmount(record[10] != null ? record[10].toString() : null);
        dto.setApprovalStatus(record[11] != null ? record[11].toString() : null);
        dto.setCreatedAt(record[12] != null ? (Timestamp) record[12] : null);
        dto.setMailStatus(record[13] != null ? record[13].toString() : null);
        dto.setOrderNum(record[14] != null ? record[14].toString() : null);
        dto.setMessName(record[15] != null ? record[15].toString() : null);
        dto.setVegOrNonveg(record[16] != null ? record[16].toString() : null);
        dto.setNumOfSnacks(record[17] != null ? record[17].toString() : null);
        dto.setActiveFlag(record[18] != null ? record[18].toString() : null);

        // Encrypt requestId and set encrypted version
        if (dto.getRequestId() != null && dto.getRequestId() != 0) {
            try {
				Long time = System.currentTimeMillis();
                String user= SecurityCtxUtil.userId().toLowerCase();
                dto.setEncryReqIdView(MCrypt.getInstance().encryptToText(String.valueOf(dto.getRequestId())+Constants.BACKTICK+Constants.VIEW+Constants.BACKTICK+time));
                dto.setEncryReqIdPdf(MCrypt.getInstance().encryptToText(String.valueOf(dto.getRequestId()+"~"+time+"~"+user)+Constants.BACKTICK+Constants.PDF));
                dto.setEncryReqIdDelete(MCrypt.getInstance().encryptToText(String.valueOf(dto.getRequestId())+Constants.BACKTICK+Constants.DELETE+Constants.BACKTICK+time));
            } catch (Exception e) {
                dto.setEncryReqIdView(null);
            }
        } else {
            dto.setEncryReqIdView(null);
        }

        return dto;
    }


    @Transactional
    public String saveCouponRequest(GuestCouponRequestDTO dto) {
        
    	GuestCouponPaymentAdviceEntity entity = PaymentAdviceMapper.INSTANCE.toEntity(dto, getSessionPredicates());
       
        if(dto.getCategory()!=null && dto.getCategory().equalsIgnoreCase(messageSource.getMessage("message.online.coupon", null, Locale.getDefault()))) {
        	 entity.setApprovalStatus(WorkflowStatus.APPROVED.getStatus());
        }else {
        	 entity.setApprovalStatus(WorkflowStatus.PENDING.getStatus());
        }
		String category = dto.getCategory();
		if (dto.getCategory().equals(messageSource.getMessage("message.label.hostel.residents", null, Locale.getDefault()))) {
			category = category + ModelConstants.HYPHEN + dto.getVegOrNonVeg();
		}
        else if(dto.getCategory().equals(messageSource.getMessage("message.online.coupon", null, Locale.getDefault()))){
            category = messageSource.getMessage("message.label.hostel.residents", null, Locale.getDefault())+ ModelConstants.HYPHEN + dto.getVegOrNonVeg();
        }
        else if(dto.getCategory().equals(messageSource.getMessage("message.coupon.category.projectStaff", null, Locale.getDefault()))){
            category = messageSource.getMessage("message.coupon.category.projectStaff", null, Locale.getDefault());
        }
        else{
            category = messageSource.getMessage("message.coupon.category.general", null, Locale.getDefault());
        }

		GuestCouponConfigDto ratesPerUnit = guestCouponConfigRepository.getCouponRates(dto.getDiningFrom(),
				dto.getDiningTo(), category);
		
		GuestCouponPaymentAdviceEntity updatedEntity = calculateTotalAmount(entity, ratesPerUnit,dto);
        GuestCouponPaymentAdviceEntity savedEntity = paymentAdviceRepository.save(updatedEntity);
        
        //save in mapping table
        guestCouponMappingsService.saveCoupons(dto, savedEntity);

        return Constants.SAVED;
    }
    
    public GuestCouponPaymentAdviceEntity calculateTotalAmount(GuestCouponPaymentAdviceEntity entity, GuestCouponConfigDto ratesPerUnit,
                                                               GuestCouponRequestDTO dto) {
        Long totalAmount = 0L;
        int totalDiscountAmount = 0;
        Integer breakfastRate = ratesPerUnit.getBreakfastAmount()!=null ? ratesPerUnit.getBreakfastAmount() : 0;
        Integer lunchRate = ratesPerUnit.getLunchAmount()!=null ? ratesPerUnit.getLunchAmount() : 0;
        Integer dinnerRate = ratesPerUnit.getDinnerAmount()!=null ? ratesPerUnit.getDinnerAmount() : 0;
        Integer snacksRate = ratesPerUnit.getSnacksAmount()!=null ? ratesPerUnit.getSnacksAmount() : 0;

        // discounted rates (veg/non-veg)
        int discountedVegAmount = Integer.valueOf(simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_COUPON_VEG_DISCOUNTED_AMOUNT));
        int discountedNonVegAmount = Integer.valueOf(simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_COUPON_NONVEG_DISCOUNTED_AMOUNT));

        boolean isDiscountCategory =messageSource.getMessage("message.online.coupon", null, Locale.getDefault()).equalsIgnoreCase(entity.getCategory())
                || messageSource.getMessage("message.label.hostel.residents", null, Locale.getDefault()).equalsIgnoreCase(entity.getCategory());

        // calculate full day cost normally
        int normalDayCost = (breakfastRate +lunchRate+dinnerRate+snacksRate);

        // pick discount rate based on meal type
        int discountedDayCost = messageSource.getMessage("message.nonveg", null, Locale.getDefault()).equalsIgnoreCase(entity.getVegOrNonVeg())
                ? discountedNonVegAmount : discountedVegAmount;

        if (isDiscountCategory) {
            for (DailyCouponRequestDTO day : dto.getFoodFrequency()) {
                boolean havingBreakfast = day.getHavingBreakfast();
                boolean havingLunch = day.getHavingLunch();
                boolean havingDinner = day.getHavingDinner();
                boolean havingSnacks = day.getHavingSnacks();
                boolean allSessionsSelected = havingBreakfast && havingLunch && havingDinner && havingSnacks;

                // case 1: discount applies
                if (allSessionsSelected) {
                    totalAmount += discountedDayCost;
                    totalDiscountAmount += (normalDayCost - discountedDayCost);
                } else {
                    // case 2: no discount, calculate per-session
                    if (havingBreakfast) totalAmount += breakfastRate;
                    if (havingLunch)     totalAmount += lunchRate;
                    if (havingDinner)    totalAmount += dinnerRate;
                    if (havingSnacks)    totalAmount += snacksRate;
                }
            }
        }else{
            totalAmount += entity.getNoOfBreakfastCoupons() * breakfastRate;
            totalAmount += entity.getNoOfLunchCoupons() * lunchRate;
            totalAmount += entity.getNoOfDinnerCoupons() * dinnerRate;
            totalAmount += entity.getNoOfSnacksCoupons() * snacksRate;
        }

        entity.setBreakfastCouponRate(breakfastRate);
        entity.setLunchCouponRate(lunchRate);
        entity.setDinnerCouponRate(dinnerRate);
        entity.setSnacksCouponRate(snacksRate);
        entity.setOverallAmount(totalAmount);
        entity.setTotalDiscountedAmount(totalDiscountAmount);
        entity.setConfigDiscountedAmount(discountedDayCost);

        return entity;
    }
    
    private static Map<String, Predicate<DailyCouponRequestDTO>> getSessionPredicates() {
        Predicate<DailyCouponRequestDTO> bfPredicate = DailyCouponRequestDTO::getHavingBreakfast;
        Predicate<DailyCouponRequestDTO> lunchPredicate = DailyCouponRequestDTO::getHavingLunch;
        Predicate<DailyCouponRequestDTO> dinnerPredicate = DailyCouponRequestDTO::getHavingDinner;
        Predicate<DailyCouponRequestDTO> snacksPredicate = DailyCouponRequestDTO::getHavingSnacks;
        Map<String, Predicate<DailyCouponRequestDTO>> predicates = new HashMap<>();
        predicates.put("bfPredicate", bfPredicate);
        predicates.put("lunchPredicate", lunchPredicate);
        predicates.put("dinnerPredicate", dinnerPredicate);
        predicates.put("snacksPredicate", snacksPredicate);

        return predicates;
    }
    
    public GuestCouponsDto populateGuestCouponsDto(Long requestId) {
        List<Object[]> objList = guestCouponRepository.getGuestCouponRequestListByRequestId(requestId,
                    ModelConstants.STATUS_ACTIVE);
        
        GuestCouponsDto dto = new GuestCouponsDto();
        List<GuestCouponPaymentAdviceEntity> couponRequest = new ArrayList<>();
        List<GuestCouponMappingsEntity> couponMappings = new ArrayList<>();
        List<MessMasterEntity> messMaster = new ArrayList<>();

        for (Object[] obj : objList) {
            if (obj != null) {
                GuestCouponPaymentAdviceEntity reqEntity = (GuestCouponPaymentAdviceEntity) obj[0];
                GuestCouponMappingsEntity mappingEntity = (GuestCouponMappingsEntity) obj[1];
                MessMasterEntity messEntity = (MessMasterEntity) obj[2];

                couponRequest.add(reqEntity);
                couponMappings.add(mappingEntity);
                messMaster.add(messEntity);
            }
        }
        
        dto.setCouponRequest(couponRequest);
        dto.setCouponMapping(couponMappings);
        dto.setMessMaster(messMaster);
        
        return dto;
    }

	public String getDownloadGuestCouponPrintPDF(Long requestId, String type, HttpServletResponse response) throws NoSuchMessageException, Exception {
		GuestCouponsDto dto = populateGuestCouponsDto(requestId);

		if (dto == null || dto.getCouponMapping() == null || dto.getCouponMapping().isEmpty()) {
			throw new Exception(messageSource.getMessage("message.label.no.coupons.found", null, Locale.getDefault()));
		}

		String fileName = messageSource.getMessage("message.label.guest.coupons.filename", null, Locale.getDefault())
				+ ModelConstants.UNDERSCORE + System.currentTimeMillis() + PdfActionService.PDF_EXTENSION;

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try {
			PdfWriter writer;
			if (MAIL.equals(type)) {
				writer = new PdfWriter(byteArrayOutputStream);
			} else {
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
				writer = new PdfWriter(response.getOutputStream());
			}

			try (PdfDocument pdfDocument = new PdfDocument(writer); Document document = new Document(pdfDocument)) {

				pdfDocument.setDefaultPageSize(PageSize.A6);
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);

				if (dto.getCouponRequest().getFirst().getPaymentStatus() != null
						&& dto.getCouponRequest().getFirst().getPaymentStatus().equals(Constants.SUCCESS)) {
					for (int i = 0; i < dto.getCouponMapping().size(); i++) {
						GuestCouponPaymentAdviceEntity reqEntity = dto.getCouponRequest().get(i);
						GuestCouponMappingsEntity mappingEntity = dto.getCouponMapping().get(i);
						MessMasterEntity messEntity = dto.getMessMaster().get(i);

						String studentId = (reqEntity.getStudentId() != null && !reqEntity.getStudentId().isEmpty())
								? reqEntity.getStudentId()
								: reqEntity.getCandidateName();

						String messSession = mappingEntity.getCouponType().equals(CommonEnum.BF.toString())
								        ? messageSource.getMessage("message.label.breakfast", null, Locale.getDefault()).toUpperCase()
								: mappingEntity.getCouponType().equals(CommonEnum.LC.toString())
										? messageSource.getMessage("message.label.lunch", null, Locale.getDefault()).toUpperCase()
                                : mappingEntity.getCouponType().equals(CommonEnum.DR.toString())
                                        ? messageSource.getMessage("message.label.dinner", null, Locale.getDefault()).toUpperCase()
                                :messageSource.getMessage("message.label.snacks", null, Locale.getDefault()).toUpperCase();

						String messName = (messEntity != null && messEntity.getMessName() != null)
								? messEntity.getMessName()
								: "";

						String eligible = messageSource.getMessage("message.label.not.eligible.for.return.refund", null,
								Locale.getDefault());

						pdfActiveService.addDocumentGuestCouponHeader(document,
								messageSource.getMessage("message.label.heading", null, Locale.getDefault()),
								messageSource.getMessage("message.label.guest.coupon.header", null,
										Locale.getDefault()),
								mappingEntity.getToDate().format(dateFormatter));

						document.add(new LineSeparator(new SolidLine()).setMarginTop(5F).setMarginBottom(10F));

						Table table = new Table(2).setWidth(PdfActionService.VALUE_100_P);

						Cell leftCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
						leftCell.add(new Paragraph(studentId).setBold().setFontSize(14));
						leftCell.add(new Paragraph(messSession).setBold().setFontSize(16).setMarginTop(7F)
								.setMultipliedLeading(1f));
						leftCell.add(new Paragraph(eligible).setFontSize(8));
						if (!messName.isEmpty()) {
							leftCell.add(new Paragraph(messName).setBold().setFontSize(14));
						}
						table.addCell(leftCell);

						byte[] qrCodeImage = pdfActiveService.generateQRCode(
								mappingEntity.getCouponNumber(),
                                String.valueOf(mappingEntity.getCouponId()),
                                mappingEntity.getToDate().format(dateFormatter));

						Cell rightCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER)
								.setVerticalAlignment(VerticalAlignment.MIDDLE);
						if (qrCodeImage != null && qrCodeImage.length > 0) {
							Image qrCode = new Image(ImageDataFactory.create(qrCodeImage)).setWidth(90).setHeight(90);
							rightCell.add(qrCode);
						} else {
							rightCell.add(new Paragraph(
									messageSource.getMessage("message.label.no.qr.code", null, Locale.getDefault()))
									.setBold().setFontSize(8));
						}
						table.addCell(rightCell);

						document.add(table);
						document.add(new LineSeparator(new SolidLine()).setMarginTop(10F).setMarginBottom(5F));

						Table mainTable = new Table(2).setWidth(UnitValue.createPercentValue(100)).setFixedLayout();

						String serialNo = messageSource.getMessage("message.label.sl.no", null, Locale.getDefault())
								+ ModelConstants.COLAN + ModelConstants.SPACE + mappingEntity.getCouponNumber() + mappingEntity.getCouponId();

						Cell serialNoCell = new Cell().add(new Paragraph(serialNo).setBold().setFontSize(12))
								.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT);
						mainTable.addCell(serialNoCell);

						Table signatureTable = new Table(1).setWidth(UnitValue.createPercentValue(100)).setFixedLayout()
								.addCell(new Cell()
										.add(new Paragraph(messageSource.getMessage("message.label.authority.sign.pdf",
												null, Locale.getDefault())).setFontSize(12).setBold())
										.setBorder(Border.NO_BORDER).setPaddingBottom(3F)
										.setTextAlignment(TextAlignment.RIGHT))
								.addCell(new Cell().setBorder(Border.NO_BORDER).setPaddingTop(-5)
										.setTextAlignment(TextAlignment.RIGHT)
										.add(pdfActionService.getAuthoritySignature()));

						mainTable.addCell(new Cell().add(signatureTable).setBorder(Border.NO_BORDER)
								.setTextAlignment(TextAlignment.RIGHT));

						document.add(mainTable);
						if (i < dto.getCouponMapping().size() - 1) {
							document.add(new AreaBreak());
						}
					}
				}
			}

			if (MAIL.equals(type)) {
				byte[] pdfBytes = byteArrayOutputStream.toByteArray();
				fileService.encodeFile(SimsConfigDataService.COUPON_MAIL_ATTACHEMENTS, pdfBytes, fileName);
			}

			return fileName;

		} catch (IOException e) {
			throw new Exception(messageSource.getMessage("message.label.error.generate.pdf", null, Locale.getDefault()),
					e);
		}
	}

	public String downloadGuestCouponPrintPDF(Long requestId, String type, HttpServletResponse response)
			throws NoSuchMessageException, Exception {
		return getDownloadGuestCouponPrintPDF(requestId, type, response);
	}

	public String downloadGuestCouponPrintPDF(Long requestId, Long requestTime, String user, String type,
			HttpServletResponse response) throws NoSuchMessageException, Exception {
		long currentTime = System.currentTimeMillis();

		long diffInMillis = currentTime - requestTime;
		long fiveMinutesInMillis = 5 * 60 * 1000; // 5 minutes in milliseconds

		if (diffInMillis < fiveMinutesInMillis) {
			if (user != null
					&& ((user.equals(SecurityCtxUtil.userId().toLowerCase())) || (SecurityCtxUtil.userRole() != null
							&& (SecurityCtxUtil.userRole().equalsIgnoreCase("SoftwareAdmin")
									|| SecurityCtxUtil.userRole().equalsIgnoreCase("Guest Coupon"))))) {
				return getDownloadGuestCouponPrintPDF(requestId, type, response);
			}
		}
		return Constants.REDIRECT + errorPage + GlobalExceptionHandler.PAGE_NOT_ALLOWED_PARAM;
	}

	@Transactional
	public String approveRequest(Long requestId) throws Exception{
		guestCouponRepository.updateApprovalStatus(WorkflowStatus.APPROVED.getStatus(), SecurityCtxUtil.userId(),
				DateUtility.getNowTimeInstant(), requestId, ModelConstants.STATUS_ACTIVE);
		return Constants.SAVED;
	}

	@Transactional (rollbackFor = Exception.class)
	public boolean deleteGuestCouponByRequestId(long requestId,String userId) {
		String statusInactive = ModelConstants.STATUS_INACTIVE;
//		String userId = SecurityCtxUtil.userId();
		LocalDateTime now = DateUtility.getNowTimeInstant();
		try {
			int updateCount1 = guestCouponRepository.deleteGuestCoupon(requestId, statusInactive, userId, now);
			if (updateCount1 > 0) {
				int updateCount2 = guestCouponRepository.deleteGuestCouponList(requestId, statusInactive, userId, now);
				if (updateCount2 == 0) {
					throw new RuntimeException();
				}
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}

	@Transactional
	public String sendMail(Long requestId, HttpServletResponse response) throws NoSuchMessageException, Exception {
		GuestCouponPaymentAdviceEntity entity = guestCouponRepository.getEntityByRequestId(requestId,
				ModelConstants.STATUS_ACTIVE);
		if (entity != null) {
			String mailId;
			if (entity.getCategory()
					.equals(messageSource.getMessage("message.label.iitm.students", null, Locale.getDefault()))
					|| entity.getCategory().equals(
							messageSource.getMessage("message.label.hostel.residents", null, Locale.getDefault()))) {
				mailId = entity.getStudentId().toUpperCase()
						+ simsConfigDataService.getSimConfigValue(SimsConfigDataService.STUDENT_MAIL_ID);
			} else
				mailId = entity.getMailId();

			if (mailId != null && !mailId.isEmpty()) {
				String fileName = downloadGuestCouponPrintPDF(requestId, MAIL, response);
				String fileLocation = simsConfigDataService
						.getSimConfigValue(SimsConfigDataService.COUPON_MAIL_ATTACHEMENTS);
				String fullFilePath = fileLocation + fileName;

				Optional<MailTemplateEntity> mailTemplate = mailTemplateRepository
						.findByMailType(MailTemplateEntity.GUEST_COUPON_REQUEST_MAIL);
				if (mailTemplate.isPresent()) {
					MailTemplateEntity template = mailTemplate.get();
					String subject = template.getMailSubject();
					String content = template.getMailTemplate();
					content = content.replaceAll("#%student_name%#", entity.getCandidateName().toUpperCase());
					content = content.replaceAll("#%created_at%#", DateUtility.formatDate(entity.getCreatedAt()));

					mailQueueService.saveMailQueue(subject, entity.getCandidateName().toUpperCase(), content, mailId,
							GUEST_COUPON_REQUEST, SecurityCtxUtil.userId(), 1, null, fullFilePath,
							MailQueueService.BEST_REGARDS, MailQueueService.CCW_OFFICE);
					String sent = "sent";
					int count = guestCouponRepository.updateMailGuestCoupon(requestId, sent, SecurityCtxUtil.userId(),
							DateUtility.getNowTimeInstant());
					if (count > 0) {
						return Constants.SAVED;
					}
				}
			}
		}
		return null;
	}

	public GuestCouponRequestDTO viewGuestCouponDetails(Long requestId) {
		GuestCouponRequestDTO returnDto = new GuestCouponRequestDTO();
		GuestCouponPaymentAdviceEntity entity = guestCouponRepository.getByRequestIdAlone(requestId);

		PaymentAdviceMapper.INSTANCE.toGuestCouponDtoFromEntity(returnDto, entity);
        returnDto.setStudentId(returnDto.getStudentId()!=null ? returnDto.getStudentId().toUpperCase():null);

		String loginType= SecurityCtxUtil.accountType();
		if(loginType!=null && !loginType.equals(ModelConstants.STUDENT)) {
			if (entity.getHostelId() != null && entity.getHostelId() != 0) {
				Optional<HostelMasterEntity> hostelEntity = hostelMasterRepository
						.findByIdAndActiveFlag(entity.getHostelId(), ModelConstants.STATUS_ACTIVE);
				if (hostelEntity != null && !hostelEntity.isEmpty()) {
					HostelMasterEntity hostel = hostelEntity.get();
					returnDto.setHostelName(hostel.getHostelName());
				}
			}
		}

		if (entity.getMessId() != null && entity.getMessId() != 0) {
			Optional<MessMasterEntity> messEntity = messMasterRepository.findByIdAndActiveFlag(entity.getMessId(),
					ModelConstants.STATUS_ACTIVE);
			if (messEntity != null && !messEntity.isEmpty()) {
				MessMasterEntity mess = messEntity.get();
				returnDto.setMessName(mess.getMessName());
			}
		}

		returnDto.setVegOrNonVeg(entity.getVegOrNonVeg() != null && entity.getVegOrNonVeg().equals("Veg") ? "Veg" : "Non Veg");
		returnDto.setSubBfTotal((entity.getNoOfBreakfastCoupons()!=null ? entity.getNoOfBreakfastCoupons() : 0) * entity.getBreakfastCouponRate());
		returnDto.setSubLnTotal((entity.getNoOfLunchCoupons()!=null ? entity.getNoOfLunchCoupons() : 0) * entity.getLunchCouponRate());
		returnDto.setSubDnTotal((entity.getNoOfDinnerCoupons()!=null ? entity.getNoOfDinnerCoupons(): 0) * entity.getDinnerCouponRate());
		returnDto.setSubSnacksTotal((entity.getNoOfSnacksCoupons()!=null ? entity.getNoOfSnacksCoupons() : 0) * entity.getSnacksCouponRate());

		List<Object[]> couponList = guestCouponRepository.getGuestCouponsList(requestId, ModelConstants.STATUS_ACTIVE);
		List<DailyCouponRequestDTO> guestCouponList = new ArrayList<>();
        boolean isDiscountCategory =messageSource.getMessage("message.online.coupon", null, Locale.getDefault()).equalsIgnoreCase(entity.getCategory())
                || messageSource.getMessage("message.label.hostel.residents", null, Locale.getDefault()).equalsIgnoreCase(entity.getCategory());

		for (Object[] obj : couponList) {
			if (obj == null)
				continue;

			DailyCouponRequestDTO list = new DailyCouponRequestDTO();
			list.setDate((LocalDate) obj[1]);

			if (Boolean.TRUE.equals(entity.isBulkCoupon())) {
				list.setNoOfBreakfast(((Number) obj[2]).intValue());
				list.setNoOfLunch(((Number) obj[3]).intValue());
				list.setNoOfDinner(((Number) obj[4]).intValue());
				list.setNoOfSnacks(((Number) obj[5]).intValue());
			} else {
				list.setHavingBreakfast((obj[2] != null) && (((Number) obj[2]).intValue() != 0));
				list.setHavingLunch((obj[3] != null) && (((Number) obj[3]).intValue() != 0));
				list.setHavingDinner((obj[4] != null) && (((Number) obj[4]).intValue() != 0));
				list.setHavingSnacks((obj[5] != null) && (((Number) obj[5]).intValue() != 0));

                int dayTotal = getDayTotal(list, isDiscountCategory, entity);
                list.setDayTotal(dayTotal);
			}
			guestCouponList.add(list);
		}
		returnDto.setFoodFrequency(guestCouponList);
        
        //Get Online COupon Payment Details
        if(messageSource.getMessage("message.online.coupon", null, Locale.getDefault()).equalsIgnoreCase(entity.getCategory())) {
            getOnlinePaymentDetails(requestId, returnDto);
        }

        returnDto.setStudCategory(isDiscountCategory || messageSource.getMessage("message.label.iitm.students", null, Locale.getDefault()).equalsIgnoreCase(entity.getCategory()));

        return returnDto;
	}

    private void getOnlinePaymentDetails(Long requestId, GuestCouponRequestDTO returnDto) {
            Optional<GuestCouponOnlinePaymentEntity> onlinePaymentOpt =
                    guestCouponOnlinePaymentRepository.findByPaymentAdviceRequestIdAndActiveFlagAndPaymentStatusIgnoreCase(
                            requestId, ModelConstants.STATUS_ACTIVE, Constants.SUCCESS);
            if (onlinePaymentOpt.isPresent()) {
                GuestCouponOnlinePaymentEntity onlinePayment = onlinePaymentOpt.get();
                returnDto.setOrderNo(onlinePayment.getOrderNo() != null ? onlinePayment.getOrderNo() : "-");
                returnDto.setPaymentMode(
                        onlinePayment.getPaymentMethod() != null
                                ? onlinePayment.getPaymentMethod() + "-" + onlinePayment.getPaymentGateway()
                                : "-");
                returnDto.setPaidAmount(onlinePayment.getNetPayable() != null ? onlinePayment.getNetPayable() : 0d);
                returnDto.setOnlinePaymentStatus(
                        onlinePayment.getPaymentStatus() != null ? onlinePayment.getPaymentStatus() : "-");
                returnDto.setTransactionRefNo(
                        onlinePayment.getTransactionRefNumber() != null ? onlinePayment.getTransactionRefNumber() : "-");
                returnDto.setCcAvRefNo(
                        onlinePayment.getCcavReferenceNo() != null ? onlinePayment.getCcavReferenceNo() : "-");
                returnDto.setTransactionDate(
                        onlinePayment.getTransactionDate() != null ? onlinePayment.getTransactionDate() : null);
            }
    }

    private static int getDayTotal(DailyCouponRequestDTO list, boolean isDiscountCategory, GuestCouponPaymentAdviceEntity entity) {
        boolean hasBreakfast = list.getHavingBreakfast();
        boolean hasLunch     = list.getHavingLunch();
        boolean hasDinner    = list.getHavingDinner();
        boolean hasSnacks    = list.getHavingSnacks();

        int dayTotal = 0;
        if (isDiscountCategory && hasBreakfast && hasLunch && hasDinner && hasSnacks) {
            dayTotal = entity.getConfigDiscountedAmount();
        } else {
            if (hasBreakfast) {
                dayTotal += entity.getBreakfastCouponRate();
            }
            if (hasLunch) {
                dayTotal += entity.getLunchCouponRate();
            }
            if (hasDinner) {
                dayTotal += entity.getDinnerCouponRate();
            }
            if (hasSnacks) {
                dayTotal += entity.getSnacksCouponRate();
            }
        }
        return dayTotal;
    }

    @Transactional
	public String saveCouponPayment(GuestCouponRequestDTO dto) {
		int count = 0;
		if (dto.getCategory()
				.equals(messageSource.getMessage("message.label.hostel.residents", null, Locale.getDefault()))) {
			count = guestCouponRepository.updateHostelResidentsGuestCouponPayment(dto.getPaymentType(),
					dto.getPaymentDate(), dto.getPaymentReferenceNo(), dto.getPaymentAmount(), dto.getPaymentStatus(),
					SecurityCtxUtil.userId(), DateUtility.getNowTimeInstant(), dto.getRequestId(),
					ModelConstants.STATUS_ACTIVE, WorkflowStatus.APPROVED.getStatus());
		} else {
			count = guestCouponRepository.updateGuestCouponPayment(dto.getPaymentType(), dto.getPaymentDate(),
					dto.getPaymentReferenceNo(), dto.getPaymentAmount(), dto.getPaymentStatus(),
					SecurityCtxUtil.userId(), DateUtility.getNowTimeInstant(), dto.getRequestId(),
					ModelConstants.STATUS_ACTIVE);
		}
		if (count > 0) {
			return Constants.UPDATED;
		}
		return null;
	}
	

	public boolean checkSameDateAndSession(String studentId, LocalDate date, String session) {
        return paymentAdviceRepository.checkSameDateAndSession(studentId, date, session,ModelConstants.STATUS_ACTIVE);
    }
	
	public int checkMessAvailability(Long messId, LocalDate date, String session) {
        return paymentAdviceRepository.checkMessAvailability(messId, date, session,ModelConstants.STATUS_ACTIVE);
    }

    public GuestCouponIssuedDTO checkSameSessionAndMessAvailWithinDates(
            String studentId, Long messId, LocalDate diningFrom, LocalDate diningTo) {

        GuestCouponIssuedDTO returnDto = new GuestCouponIssuedDTO();

        // 1. First check: same sessions within dates
        List<GuestCouponIssuedDTO> errorList = checkSameSessions(studentId, diningFrom, diningTo);
        if (!errorList.isEmpty()) {
            returnDto.setErrorList(errorList);
            returnDto.setStatus("sameDateRequest");
            return returnDto;
        }

        // 2. Second check: mess availability
        errorList = checkMessAvailability(messId, diningFrom, diningTo);
        if (!errorList.isEmpty()) {
            returnDto.setErrorList(errorList);
            returnDto.setStatus("messAvailCheck");
        }
        return returnDto;
    }

    /**
     * Helper method to check same sessions for a student within given dates
     */
    private List<GuestCouponIssuedDTO> checkSameSessions(
            String studentId, LocalDate diningFrom, LocalDate diningTo) {
        List<Object[]> result = paymentAdviceRepository.checkSameSessionsWithinDates(
                studentId.toUpperCase(), ModelConstants.STATUS_ACTIVE, diningFrom, diningTo);
        return mapToGuestCouponList(result);
    }

    /**
     * Helper method to check mess availability within given dates
     */
    public List<GuestCouponIssuedDTO> checkMessAvailability(
            Long messId, LocalDate diningFrom, LocalDate diningTo) {
        List<Object[]> messResult = paymentAdviceRepository.checkMessAvailabilityWithinDates(
                messId, ModelConstants.STATUS_ACTIVE, diningFrom, diningTo);
        return mapToGuestCouponList(messResult);
    }

    /**
     * Utility method to map Object[] rows into GuestCouponIssuedDTO list.
     */
    private List<GuestCouponIssuedDTO> mapToGuestCouponList(List<Object[]> rows) {
        List<GuestCouponIssuedDTO> list = new ArrayList<>();
        if (rows != null && !rows.isEmpty()) {
            for (Object[] row : rows) {
                if (row != null && row.length > 1) {
                    GuestCouponIssuedDTO dto = new GuestCouponIssuedDTO();
                    dto.setDiningTo(row[0] != null ? row[0].toString() : null);
                    dto.setCouponType(getStringValue(String.valueOf(row[1])));
                    list.add(dto);
                }
            }
        }
        return list;
    }



    public GuestCouponRequestDTO checkStudentAllottedMessPeriod(String studentId,LocalDate diningFrom, LocalDate diningTo) {
        Object[] result = studentMessDetailsRepository.checkStudentAllottedMessPeriod(studentId.toUpperCase(),
                ModelConstants.STATUS_ACTIVE,diningFrom,diningTo);
        GuestCouponRequestDTO returnDto = new GuestCouponRequestDTO();
        if (result != null && result.length > 0) {
            Object[] obj = (Object[]) result[0];
            returnDto.setDiningFrom(obj[0] != null ? LocalDate.parse(obj[0].toString()) : null);
            returnDto.setDiningTo(obj[1] != null ? LocalDate.parse(obj[1].toString()) : null);
            returnDto.setStudentId(getStringValue(String.valueOf(obj[2])));
        }else{
            return null;
        }
        return returnDto;
    }
	
	public GuestCouponPaymentAdviceEntity getCouponPaymentEntityForStudent(String studentId, String category) {
        return guestCouponRepository.getCouponPaymentEntityByStudentIdAndCategory(studentId, category, ModelConstants.STATUS_ACTIVE);
    }
	
	public Boolean authenticateUserPassword(String password) {
		MyUserDetails user = myUserDetailsService.loadUserByUsername(SecurityCtxUtil.userName().toLowerCase().trim());
		if (user != null && user.getAccountType().equals(ModelConstants.STUDENT_LOGIN_TYPE)) {
			String encPassword = MD5Encryption.md5Encrypt(password);
			return myAuthenticationProvider.isPasswordValid(user, password, encPassword);
		}
		return null;
	}



	private String getStringValue(Object obj) {
		return obj != null && !String.valueOf(obj).trim().isEmpty() ? String.valueOf(obj) : Constants.HYPHEN;
	}


	@Transactional
	public boolean resetGuestCouponByRequestId(long requestId,String userId) {
		try {
			List<GuestCouponOnlinePaymentEntity> guestCouponOnlinePaymentEntityList = guestCouponOnlinePaymentRepository.findByPaymentAdviceRequestIdAndActiveFlag(requestId, ModelConstants.STATUS_ACTIVE);
			for (GuestCouponOnlinePaymentEntity guestCouponOnlinePaymentEntity : guestCouponOnlinePaymentEntityList) {
				guestCouponOnlinePaymentEntity.setRetryCount(0);
				guestCouponOnlinePaymentEntity.setModifiedBy(userId);
				guestCouponOnlinePaymentEntity.setModifiedAt(DateUtility.getNowTimeInstant());
			}
			guestCouponOnlinePaymentRepository.saveAll(guestCouponOnlinePaymentEntityList);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Transactional (rollbackFor = Exception.class)
	public boolean enableGuestCouponByRequestId(long requestId,String userId){
		String statusActive = ModelConstants.STATUS_ACTIVE;
		LocalDateTime now = DateUtility.getNowTimeInstant();
		try {
			int updateCount1 = guestCouponRepository.enableGuestCoupon(requestId, statusActive, userId, now);
			if (updateCount1 > 0) {
				int updateCount2 = guestCouponRepository.enableGuestCouponList(requestId, statusActive, userId, now);
				if (updateCount2 == 0) {
					throw new RuntimeException();
				}
				return true;
			}
		} catch (Exception e) {
			throw e;
		}
		return false;
	}
}
