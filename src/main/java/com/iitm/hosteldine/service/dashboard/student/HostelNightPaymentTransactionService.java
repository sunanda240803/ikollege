package com.iitm.hosteldine.service.dashboard.student;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.dashboard.student.HostelNightPaymentTransactionDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.paymentGatewayCC.PaymentGatewayCcavenueDto;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntityId;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntityId;
import com.iitm.hosteldine.form.hostel.HostelNightCouponForm;
import com.iitm.hosteldine.mapper.dashboard.student.HostelNightPaymentTransactionMapper;
import com.iitm.hosteldine.mapper.paymentGatewayCC.PaymentGatewayCcavenueMapper;
import com.iitm.hosteldine.model.OtherCandidate.PaymentGatewayConfigCcavEntity;
import com.iitm.hosteldine.model.financialYear.FinancialYearEntity;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.model.studentDashboard.HostelNightPaymentLedgerViewEntity;
import com.iitm.hosteldine.model.studentDashboard.HostelNightPaymentOnlineViewEntity;
import com.iitm.hosteldine.model.studentDashboard.HostelNightPaymentTransactionEntity;
import com.iitm.hosteldine.repository.dashboard.student.HostelNightPaymentTransactionRepository;
import com.iitm.hosteldine.repository.financialYear.FinancialYearRepository;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.repository.mess.MessLedgerARepository;
import com.iitm.hosteldine.repository.mess.MessLedgerBRepository;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import com.iitm.hosteldine.repository.student.ShowStudentDetailRepository;
import com.iitm.hosteldine.repository.studentDashboard.HostelNightPaymentLedgerRepository;
import com.iitm.hosteldine.repository.studentDashboard.HostelNightPaymentOnlineViewRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.service.paymentGatewayCC.PaymentGatewayCcavenueService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.CustomValidators;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class HostelNightPaymentTransactionService {

    private final HostelNightPaymentTransactionRepository hostelNightPaymentTransactionRepository;
    private final HostelMasterService hostelMasterService;
    private final FinancialYearRepository financialYearRepository;
    private final MessLedgerARepository messLedgerARepository;
    private final MessLedgerBRepository messLedgerBRepository;
    private final PaymentGatewayCcavenueService paymentGatewayCcavenueService;
    private final MailTemplateRepository mailTemplateRepository;
    private final Utility utility;
    private final MailQueueService mailQueueService;
    private final SimsConfigDataService simsConfigDataService;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final CommonResponseUtil commonResponseUtil;
    private final CustomValidators customValidators;
    private final ShowStudentDetailRepository showStudentDetailRepository;
    private final HostelNightPaymentLedgerRepository hostelNightPaymentLedgerRepository;
    private final HostelNightPaymentOnlineViewRepository hostelNightPaymentOnlineViewRepository;
    private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;

    @Value("${url.hostel.night.coupon}")
    private String baseUrl;

    @Value("${url.payment.response}")
    private String paymentResponse;

    public List<HostelNightPaymentTransactionDto> getHostelNightPaymentTransactions() {
        return hostelNightPaymentTransactionRepository
                .findAllByStudentIdAndActiveFlagAndPaymentStatusOrderByModifiedAtDesc(SecurityCtxUtil.userId(),
                        ModelConstants.STATUS_ACTIVE, WorkflowStatus.SUCCESS.getStatus())
                .stream()
                .map(HostelNightPaymentTransactionMapper.INSTANCE::toDto)
                .toList();
    }

    public void validateNightCouponForm(HostelNightCouponForm hostelNightCouponForm, BindingResult bindingResult) {
        Double ledgerBalance = showStudentDetailRepository.checkStudentBalance(SecurityCtxUtil.userId());
        HostelMasterDto hostelDetailsById = hostelMasterService.getHostelDetailsById(SecurityCtxUtil.hostelId());
        Double totalAmount = (hostelNightCouponForm.getVegCount() * hostelDetailsById.getVegAmount()) + (hostelNightCouponForm.getNonVegCount() * hostelDetailsById.getNonVegAmount());
        String maxCoupon = simsConfigDataService.getSimConfigValue(SimsConfigDataService.HOSTEL_NIGHT_MAX_COUPON);
        Integer previousTotalNumberOfCoupons = hostelNightPaymentTransactionRepository.getTotalNumberOfCoupons(SecurityCtxUtil.userId(),
                        WorkflowStatus.SUCCESS.getStatus(), ModelConstants.STATUS_ACTIVE)
                .orElse(0);
        Integer totalNoOfCoupon = hostelNightCouponForm.getVegCount() + hostelNightCouponForm.getNonVegCount() + previousTotalNumberOfCoupons;
        if (totalAmount <= 0) {
            customValidators.rejectField(bindingResult, "error", "message.validation.total.amount.greater");
        } else if (totalNoOfCoupon > Integer.parseInt(maxCoupon)) {
            customValidators.rejectField(bindingResult, "error", "message.validation.coupon.limit.exceeded");
        }
        if (hostelDetailsById.getVegAmount() <= 0 || hostelDetailsById.getNonVegAmount() <= 0) {
            bindingResult.rejectValue("error", "error.invalid",
                    commonResponseUtil.getMessage("message.validation.coupon.amount.not.configured") + " " + hostelDetailsById.getHostelName());
        } else if (Objects.nonNull(hostelNightCouponForm.getIsLedger()) && hostelNightCouponForm.getIsLedger() && ledgerBalance < totalAmount) {
            customValidators.rejectField(bindingResult, "error", "message.label.insuffiecient.ledger.balance");
        }
    }

    @Transactional
    public HostelNightPaymentTransactionDto processHostelNightCoupon(HostelNightCouponForm hostelNightCouponForm, HttpServletRequest request) {
        Integer orderId = hostelNightPaymentTransactionRepository.getNextVal();
        boolean mailStatus=false;
        //TODO modify next val
        String orderNo;
        PaymentGatewayConfigCcavEntity urlEntity = paymentGatewayCcavenueService.getCredentialsForUrl(request);
        if(urlEntity.getInstance()!=null && urlEntity.getInstance().equals(ModelConstants.PRODUCTION)) {
            orderNo = "HNCOUPON" + orderId;
        } else {
            orderNo = "HNCOUPONTEST" + orderId;
        }

        HostelMasterDto hostelDetailsById = hostelMasterService.getHostelDetailsById(SecurityCtxUtil.hostelId());
        Double totalAmount = (hostelNightCouponForm.getVegCount() * hostelDetailsById.getVegAmount()) + (hostelNightCouponForm.getNonVegCount() * hostelDetailsById.getNonVegAmount());
        hostelNightCouponForm.setPayBy(Objects.nonNull(hostelNightCouponForm.getIsLedger()) && hostelNightCouponForm.getIsLedger() ? ModelConstants.LEDGER : ModelConstants.ONLINE);

        HostelNightPaymentTransactionDto dto = saveHostelNightCoupon(hostelNightCouponForm, orderNo, hostelDetailsById, totalAmount);
        if (Objects.nonNull(dto) && Objects.nonNull(hostelNightCouponForm.getIsLedger()) && hostelNightCouponForm.getIsLedger()) {
            FinancialYearEntity finYearDetails = financialYearRepository.getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE);
            Integer nextValMessLedger = messLedgerARepository.getNextValMessLedger();
            boolean b1 = saveMessLedgerAEntity(finYearDetails.getFinYear(), nextValMessLedger, totalAmount);
            boolean b2 = saveMessLedgerBEntity(finYearDetails.getFinYear(), nextValMessLedger, totalAmount);

            if(b1 && b2) {
                triggerMail(hostelNightCouponForm, hostelDetailsById, totalAmount);
            }

        }

        return Objects.nonNull(dto) ? dto : null;
    }

    private HostelNightPaymentTransactionDto saveHostelNightCoupon(HostelNightCouponForm form, String orderNo, HostelMasterDto hostelDetailsById, Double totalAmount) {
        HostelNightPaymentTransactionEntity entity = new HostelNightPaymentTransactionEntity();
        entity.setStudentId(SecurityCtxUtil.userId());
        entity.setHostelId(SecurityCtxUtil.hostelId());
        entity.setNoOfVegCoupon(form.getVegCount());
        entity.setNoOfNonvegCoupon(form.getNonVegCount());
        entity.setVegRate(hostelDetailsById.getVegAmount());
        entity.setNonvegRate(hostelDetailsById.getNonVegAmount());
        entity.setTotalAmount(totalAmount);
        entity.setPayBy(form.getPayBy());
        entity.setOrderNo(orderNo);
        entity.setPaymentStatus(Objects.nonNull(form.getIsLedger()) && form.getIsLedger() ? WorkflowStatus.SUCCESS.getStatus() : WorkflowStatus.INITIATED.getStatus());
        HostelNightPaymentTransactionEntity saved = hostelNightPaymentTransactionRepository.save(entity);
        return HostelNightPaymentTransactionMapper.INSTANCE.toDto(saved);
    }

    private boolean saveMessLedgerAEntity(String finYear, Integer nextVal, Double amount) {
        MessLedgerAEntity messLedgerAEntity = new MessLedgerAEntity();
        MessLedgerAEntityId id = new MessLedgerAEntityId();
        id.setFinYear(finYear);
        id.setBookType(Constants.MESS_MS);
        id.setVoucherNo(String.valueOf(nextVal));
        messLedgerAEntity.setId(id);

        messLedgerAEntity.setVoucherDate(LocalDate.now());
        messLedgerAEntity.setAcchead(ModelConstants.HOSTEL_NIGHT);
        messLedgerAEntity.setSubAccountHead(Strings.EMPTY);
        messLedgerAEntity.setDescription("Hostel night coupon amount from student");
        messLedgerAEntity.setAmount(amount);
        messLedgerAEntity.setCancelStatus(ModelConstants.STATUS_INACTIVE);
        messLedgerAEntity.setDocRefNo(SecurityCtxUtil.userId());
        messLedgerAEntity.setDebitOrCredit(Constants.CREDIT);
        messLedgerAEntity.setScreenType("hostel_night_student");
        messLedgerAEntity.setRecon("N");
        messLedgerAEntity.onCreate();
        MessLedgerAEntity savedEntity = messLedgerARepository.save(messLedgerAEntity);
        if(savedEntity.getVoucherDate()==messLedgerAEntity.getVoucherDate()){
            return true;
        }
        return false;
    }

    private boolean saveMessLedgerBEntity(String finYear, Integer nextVal, Double amount) {
        MessLedgerBEntity messLedgerBEntity = new MessLedgerBEntity();
        MessLedgerBEntityId id = new MessLedgerBEntityId();
        id.setFinYear(finYear);
        id.setBookType(Constants.MESS_MS);
        id.setVoucherNo(String.valueOf(nextVal));
        id.setSlno(1);
        messLedgerBEntity.setId(id);

        messLedgerBEntity.setVoucherDate(LocalDate.now());
        messLedgerBEntity.setAcchead(SecurityCtxUtil.userId().toUpperCase());
        messLedgerBEntity.setSubAccountHead(Strings.EMPTY);
        messLedgerBEntity.setDescription("Hostel night coupon amount from student");
        messLedgerBEntity.setAmount(amount);
        messLedgerBEntity.setCancelStatus(ModelConstants.STATUS_INACTIVE);
        messLedgerBEntity.setDocRefNo(SecurityCtxUtil.userId());
        messLedgerBEntity.setDebitOrCredit(Constants.DEBIT);
        messLedgerBEntity.setFcNo(String.valueOf(SecurityCtxUtil.hostelId()));
        messLedgerBEntity.setRecon("N");
        messLedgerBEntity.onCreate();
        MessLedgerBEntity savedEntity = messLedgerBRepository.save(messLedgerBEntity);
        if(savedEntity.getVoucherDate()==messLedgerBEntity.getVoucherDate()){
            return true;
        }
        return false;
    }

    private boolean triggerMail(HostelNightCouponForm hostelNightCouponForm, HostelMasterDto hostelMasterDto, Double totalAmount) {
        MailTemplateEntity mailTemplateEntity = mailTemplateRepository.findByActiveFlagAndMailType(ModelConstants.STATUS_ACTIVE, ModelConstants.HOSTEL_NIGHT_COUPON_MAIL)
                .orElse(null);
        if (Objects.nonNull(mailTemplateEntity)) {
            String cc = simsConfigDataService.getSimConfigValue(SimsConfigDataService.HOSTEL_NIGHT_MAIL_CC);
//            StudentDetailsInfoDto studentInfoDetails = studentDetailsInfoService.getStudentInfoDetails(SecurityCtxUtil.userId());
            AllStudentsDetailsViewEntity studentInfoDetails = allStudentsDetailsViewRepository.findBystudentId(SecurityCtxUtil.userId())
                    .orElse(null);

            String greetingMessage = studentInfoDetails.getStudentName() + "(" + SecurityCtxUtil.userId().toUpperCase() + ")";
            String messageTemplate = mailTemplateEntity.getMailTemplate()
                    .replace("#%subMessage%#", commonResponseUtil.getMessage("message.night.coupon.mail.message"))
                    .replace("#%purchaseDate%#", utility.dateFormatter(LocalDate.now()))
                    .replace("#%hostelName%#", hostelMasterDto.getHostelName())
                    .replace("#%paymentMode%#", hostelNightCouponForm.getPayBy())
                    .replace("#%vegCoupon%#", String.valueOf(hostelNightCouponForm.getVegCount()))
                    .replace("#%nonVegCoupon%#", String.valueOf(hostelNightCouponForm.getNonVegCount()))
                    .replace("#%totalAmount%#", utility.formatCommaSeperatedCurrency(totalAmount));

            try {
                mailQueueService.saveMailQueue(mailTemplateEntity.getMailSubject(), greetingMessage, messageTemplate,
                        studentInfoDetails.getEmailId(), "Hostel Night Coupon",
                        null, null, null, null, ModelConstants.REGARDS, ModelConstants.CCW_OFFICE, cc);
                return true;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    public Workbook generateExcelReport(String type) throws Exception {
        XSSFWorkbook workbook;
        int colCount = 0;
        ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

        String[] headerList = ModelConstants.HOSTEL_NIGHT_COUPON_HEADER;
        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(commonResponseUtil.getMessage("message.night.coupon.report"));

            // Create styles using ExcelUtility
            XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
            headerStyle.setWrapText(true);
            XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
            dataStyle.setWrapText(true);

            XSSFCellStyle headerStyle2 = excelUtility.setHeaderStyle(workbook);
            headerStyle2.setWrapText(true);
            headerStyle2.setAlignment(HorizontalAlignment.LEFT);
            headerStyle2.setVerticalAlignment(VerticalAlignment.BOTTOM);

            // Create second header row
            XSSFRow rowheadFirst = sheet.createRow(1);
            excelUtility.createCell(rowheadFirst, 0, commonResponseUtil.getMessage("message.iitm.report.header"), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 7));

            // Create second header row
            XSSFRow rowheadSecond = sheet.createRow(2);
            excelUtility.createCell(rowheadSecond, 0, commonResponseUtil.getMessage("message.night.coupon.report"), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 7));

            // Create third header row for report date
            XSSFRow rowheadthird = sheet.createRow(3);
            DateFormat dateFormat = new SimpleDateFormat(Constants.BACKEND_DATETIME_FORMAT_2);
            Date date = new Date();
            excelUtility.createCell(rowheadthird, 0, commonResponseUtil.getMessage("message.label.report.date") + dateFormat.format(date), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 7));

            // Create column headers
            XSSFRow rowhead = sheet.createRow(4);

            // Add headers to the sheet
            for (String p : headerList) {
                excelUtility.createCell(rowhead, colCount, p, headerStyle2);
                sheet.setColumnWidth(colCount, 4000); // Set column width
                colCount++;
            }

            // Populate data rows
            int rowCount = 4;
            int sNo = 0;
            if (ModelConstants.LEDGER.equalsIgnoreCase(type)) {
                for (HostelNightPaymentLedgerViewEntity ledger : hostelNightPaymentLedgerRepository.findAll()) {
                    XSSFRow row = sheet.createRow(++rowCount);
                    excelUtility.createCell(row, 0, ++sNo, dataStyle);
                    excelUtility.createCell(row, 1, ledger.getStudentId(), dataStyle);
                    excelUtility.createCell(row, 2, ledger.getStudentName(), dataStyle);
                    excelUtility.createCell(row, 3, ledger.getHostelName(), dataStyle);
                    excelUtility.createCell(row, 4, ledger.getPayBy(), dataStyle);
                    excelUtility.createCell(row, 5, ledger.getTotalVegCoupons(), dataStyle);
                    excelUtility.createCell(row, 6, ledger.getTotalNonvegCoupons(), dataStyle);
                    excelUtility.createCell(row, 7, utility.formatCommaSeperatedCurrency(ledger.getTotalAmount()), dataStyle);
                }
            } else {
                for (HostelNightPaymentOnlineViewEntity online : hostelNightPaymentOnlineViewRepository.findAll()) {
                    XSSFRow row = sheet.createRow(++rowCount);
                    excelUtility.createCell(row, 0, ++sNo, dataStyle);
                    excelUtility.createCell(row, 1, online.getStudentId(), dataStyle);
                    excelUtility.createCell(row, 2, online.getStudentName(), dataStyle);
                    excelUtility.createCell(row, 3, online.getHostelName(), dataStyle);
                    excelUtility.createCell(row, 4, online.getPayBy(), dataStyle);
                    excelUtility.createCell(row, 5, online.getTotalVegCoupons(), dataStyle);
                    excelUtility.createCell(row, 6, online.getTotalNonvegCoupons(), dataStyle);
                    excelUtility.createCell(row, 7, utility.formatCommaSeperatedCurrency(online.getTotalAmount()), dataStyle);
                }
            }

            // Auto-size columns with a maximum width limit
            for (int i = 0; i < headerList.length; i++) {
                sheet.autoSizeColumn(i);
                // Cap the column width to 10000 (about 100 characters) to prevent extremely wide columns
                if (sheet.getColumnWidth(i) > 10000) {
                    sheet.setColumnWidth(i, 10000);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Error in generating report", exception);
        }
        return workbook;
    }

    public HostelNightPaymentTransactionDto getByDateBetween() {
        return hostelNightPaymentTransactionRepository
                .findFirstByStudentIdAndActiveFlagAndModifiedAtAfterOrderByModifiedAtDesc(SecurityCtxUtil.userId(), ModelConstants.STATUS_ACTIVE,
                        LocalDateTime.now().minus(2, ChronoUnit.DAYS))
                .map(HostelNightPaymentTransactionMapper.INSTANCE::toDto)
                .orElse(null);
    }

    public PaymentGatewayCcavenueDto initiateTransaction(HostelNightPaymentTransactionDto dto, HttpServletRequest request) throws Exception {
        String studentId = SecurityCtxUtil.userId().toUpperCase();
        AllStudentsDetailsViewEntity studentEntity = allStudentsDetailsViewRepository.findBystudentId(studentId)
                .orElse(null);
        PaymentGatewayCcavenueDto pgDto = PaymentGatewayCcavenueMapper.INSTANCE.toPGDtoFromStudent(studentEntity);
        pgDto.setOrderId(dto.getOrderNo());
        pgDto.setTotalAmount(dto.getTotalAmount());
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

        HostelNightPaymentTransactionEntity transaction = hostelNightPaymentTransactionRepository.findByOrderNoAndActiveFlag(orderNo, ModelConstants.STATUS_ACTIVE).orElse(null);
        if (transaction != null) {
            System.out.println("trans entity order_id---" + transaction.getOrderNo() + "\n");
            HostelNightPaymentTransactionEntity updatedTransaction = PaymentGatewayCcavenueMapper.mapToHostelNightTransactionEntity(transaction, resMap);
            HostelNightPaymentTransactionEntity updatedTrans = hostelNightPaymentTransactionRepository.save(updatedTransaction);

            if (responseType != null && responseType.equalsIgnoreCase(Constants.AFTER_PAYMENT)) {
                //set the response to dto
                returnDto = PaymentGatewayCcavenueMapper.changeToPGDto(resMap);

                //Send mail to student after payment success
                if(updatedTrans.getPaymentStatus()!=null && updatedTrans.getPaymentStatus().equalsIgnoreCase(Constants.SUCCESS)) {
                    HostelMasterDto hostelDetailsById = hostelMasterService.getHostelDetailsById(updatedTrans.getHostelId());

                    HostelNightCouponForm hostelNightCouponForm = HostelNightCouponForm.builder().build();
                    hostelNightCouponForm.setPayBy(updatedTrans.getPayBy());
                    hostelNightCouponForm.setVegCount(updatedTrans.getNoOfVegCoupon());
                    hostelNightCouponForm.setNonVegCount(updatedTrans.getNoOfNonvegCoupon());
                    boolean mailStatus = triggerMail(hostelNightCouponForm, hostelDetailsById, updatedTrans.getTotalAmount());
                }
            }
        }
        return returnDto;
    }

    public Workbook generateExcelReport(String type, String submittedFromDate, String submittedToDate, String hostelId) throws Exception {
        XSSFWorkbook workbook;
        int colCount = 0;
        ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

        List<String> headerList = new ArrayList<>(ModelConstants.HOSTEL_NIGHT_COUPON_HEADER_2);
        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(commonResponseUtil.getMessage("message.night.coupon.report"));
            boolean isLedger = "Ledger".equalsIgnoreCase(type);

            // Create styles using ExcelUtility
            XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
            headerStyle.setWrapText(true);
            XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
            dataStyle.setWrapText(true);

            XSSFCellStyle headerStyle2 = excelUtility.setHeaderStyle(workbook);
            headerStyle2.setWrapText(true);
            headerStyle2.setAlignment(HorizontalAlignment.LEFT);
            headerStyle2.setVerticalAlignment(VerticalAlignment.BOTTOM);

            // Create second header row
            XSSFRow rowheadFirst = sheet.createRow(1);
            excelUtility.createCell(rowheadFirst, 0, commonResponseUtil.getMessage("message.iitm.report.header"), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, isLedger ? 10 : 14));

            // Create second header row
            XSSFRow rowheadSecond = sheet.createRow(2);
            excelUtility.createCell(rowheadSecond, 0, commonResponseUtil.getMessage("message.night.coupon.report"), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0,  isLedger ? 10 : 14));

            // Create third header row for report date
            XSSFRow rowheadthird = sheet.createRow(3);
            DateFormat dateFormat = new SimpleDateFormat(Constants.BACKEND_DATETIME_FORMAT_2);
            Date date = new Date();
            excelUtility.createCell(rowheadthird, 0, commonResponseUtil.getMessage("message.label.report.date") + dateFormat.format(date), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0,  isLedger ? 10 : 14));

            // Create column headers
            XSSFRow rowhead = sheet.createRow(4);

            if (isLedger) {
                headerList.remove("Paid Amount");
                headerList.remove("Order Number");
                headerList.remove("Payment Date");
                headerList.remove("Payment Type");
            }

            // Add headers to the sheet
            for (String p : headerList) {
                excelUtility.createCell(rowhead, colCount, p, headerStyle2);
                sheet.setColumnWidth(colCount, 4000); // Set column width
                colCount++;
            }

            // Populate data rows
            populateDataRows(excelUtility, type, submittedFromDate, submittedToDate, hostelId, isLedger, dataStyle, sheet);
            // Auto-size columns with a maximum width limit
            for (int i = 0; i < headerList.size(); i++) {
                sheet.autoSizeColumn(i);
                // Cap the column width to 10000 (about 100 characters) to prevent extremely wide columns
                if (sheet.getColumnWidth(i) > 10000) {
                    sheet.setColumnWidth(i, 10000);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Error in generating report", exception);
        }
        return workbook;
    }

    private void populateDataRows(ExcelUtility excelUtility, String type, String submittedFromDate, String submittedToDate, String hostelId, boolean isLedger, XSSFCellStyle dataStyle, XSSFSheet sheet) {
        int rowCount = 4;
        int sNo = 0;
        LocalDateTime from = !submittedFromDate.isEmpty() ? LocalDate.parse(submittedFromDate).atStartOfDay() : LocalDate.of(1970, 1,1).atStartOfDay();
        LocalDateTime to = !submittedToDate.isEmpty() ? LocalDate.parse(submittedToDate).atTime(23, 59, 59) : LocalDateTime.now();
        Long hId = !hostelId.isEmpty() ? Long.valueOf(hostelId) : null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_TIME_FORMAT);
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
        List<HostelNightCouponReport> hostelNightCouponReportList = hostelNightPaymentOnlineViewRepository.getHostelNightCouponList(type, from, to, hId, ModelConstants.STATUS_ACTIVE, "Success");
        if (isLedger) {
            for (HostelNightCouponReport online : hostelNightCouponReportList) {
                XSSFRow row = sheet.createRow(++rowCount);
                excelUtility.createCell(row, 0, ++sNo, dataStyle);
                excelUtility.createCell(row, 1, online.submittedDate().format(formatter), dataStyle);
                excelUtility.createCell(row, 2, online.studentId(), dataStyle);
                excelUtility.createCell(row, 3, online.studentName(), dataStyle);
                excelUtility.createCell(row, 4, online.hostelName(), dataStyle);
                excelUtility.createCell(row, 5, online.payBy(), dataStyle);
                excelUtility.createCell(row, 6, online.noOfVegCoupon(), dataStyle);
                excelUtility.createCell(row, 7, online.noOfNonVegCoupon(), dataStyle);
                excelUtility.createCell(row, 8, utility.formatCommaSeperatedCurrency(Objects.nonNull(online.vegRate()) ? online.vegRate(): 0), dataStyle);
                excelUtility.createCell(row, 9, utility.formatCommaSeperatedCurrency(Objects.nonNull(online.nonVegRate()) ? online.nonVegRate() : 0), dataStyle);
                excelUtility.createCell(row, 10, utility.formatCommaSeperatedCurrency(Objects.nonNull(online.totalAmount()) ? online.totalAmount() : 0), dataStyle);
            }
        } else {
            for (HostelNightCouponReport online : hostelNightCouponReportList) {
                XSSFRow row = sheet.createRow(++rowCount);
                excelUtility.createCell(row, 0, ++sNo, dataStyle);
                excelUtility.createCell(row, 1, online.submittedDate().format(formatter), dataStyle);
                excelUtility.createCell(row, 2, online.studentId(), dataStyle);
                excelUtility.createCell(row, 3, online.studentName(), dataStyle);
                excelUtility.createCell(row, 4, online.hostelName(), dataStyle);
                excelUtility.createCell(row, 5, online.payBy(), dataStyle);
                excelUtility.createCell(row, 6, online.noOfVegCoupon(), dataStyle);
                excelUtility.createCell(row, 7, online.noOfNonVegCoupon(), dataStyle);
                excelUtility.createCell(row, 8, utility.formatCommaSeperatedCurrency(Objects.nonNull(online.vegRate()) ? online.vegRate(): 0), dataStyle);
                excelUtility.createCell(row, 9, utility.formatCommaSeperatedCurrency(Objects.nonNull(online.nonVegRate()) ? online.nonVegRate() : 0), dataStyle);
                excelUtility.createCell(row, 10, utility.formatCommaSeperatedCurrency(Objects.nonNull(online.totalAmount()) ? online.totalAmount() : 0), dataStyle);
                excelUtility.createCell(row, 11, utility.formatCommaSeperatedCurrency(Objects.nonNull(online.paidAmount()) ? online.paidAmount() : 0), dataStyle);
                excelUtility.createCell(row, 12, Objects.nonNull(online.orderNo()) && !online.orderNo().isEmpty() ? online.orderNo() : ModelConstants.NOT_APPLICABLE, dataStyle);
                excelUtility.createCell(row, 13, Objects.nonNull(online.paymentDate()) ? online.paymentDate().format(formatter2) : ModelConstants.NOT_APPLICABLE, dataStyle);
                excelUtility.createCell(row, 14, online.paymentType(), dataStyle);
            }
        }
    }
}