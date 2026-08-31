package com.iitm.hosteldine.service.OtherCandidate;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.model.OtherCandidate.PaymentGatewayConfigCcavEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.OtherCandidate.TempAccomPaymentAdviceDto;
import com.iitm.hosteldine.dto.mess.TemporaryAccomodationDto;
import com.iitm.hosteldine.dto.paymentGatewayCC.PaymentGatewayCcavenueDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.OtherCandidate.TempAccomPaymentTransactionMapper;
import com.iitm.hosteldine.mapper.paymentGatewayCC.PaymentGatewayCcavenueMapper;
import com.iitm.hosteldine.model.OtherCandidate.CandidateProfileEntity;
import com.iitm.hosteldine.model.OtherCandidate.TempAccomPaymentAdviceEntity;
import com.iitm.hosteldine.model.OtherCandidate.TempAccomPaymentTransactionEntity;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateProfileRepository;
import com.iitm.hosteldine.repository.OtherCandidate.TempAccomPaymentAdviceRepository;
import com.iitm.hosteldine.repository.OtherCandidate.TempAccomPaymentTransactionRepository;
import com.iitm.hosteldine.service.PdfActionService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.paymentGatewayCC.PaymentGatewayCcavenueService;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.validator.common.ValidationCommon;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtherCandidatePaymentService {
	
	@Value("${url.other.candidate.payment}")
	private String otherCandidatePayment;
	
	@Value("${url.payment.response}")
	private String paymentResponse;
	
	private final TempAccomPaymentAdviceRepository tempAccomPaymentAdviceRepository;
	private final TempAccomPaymentTransactionRepository tempPaymentTransactionRepository;
	private final PaymentGatewayCcavenueService paymentGatewayCcavenueService;
	private final CandidateProfileRepository candidateProfileRepository;
	private final PdfActionService pdfActionService;
	private final MessageSource messageSource;
    private final Utility utility;
    private final SimsConfigDataService simsConfigDataService;
	
    public Page<TempAccomPaymentAdviceDto> getPaymentList(PaginationForm form){
		Page<Object[]> result = null;
		int page = form.getPage() - 1;
		long candiadteId=SecurityCtxUtil.candidateId();
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("b.id").ascending());
		result = tempAccomPaymentAdviceRepository.getPaymentListForCandidate(candiadteId,ModelConstants.STATUS_ACTIVE, form.getSearch(), pageable,ModelConstants.SUCCESS);
		
		return result.map(obj -> {
			TempAccomPaymentAdviceDto dto = new TempAccomPaymentAdviceDto();
			dto.setId(obj[0] != null ? (Long) obj[0] : null);
			dto.setHostelPayFromDate(obj[1] != null ? ((java.sql.Date) obj[1]).toLocalDate() : null);
			dto.setHostelPayToDate(obj[2] != null ? ((java.sql.Date) obj[2]).toLocalDate() : null);
			dto.setMessPayFromDate(obj[3] != null ? ((java.sql.Date) obj[3]).toLocalDate() : null);
			dto.setMessPayToDate(obj[4] != null ? ((java.sql.Date) obj[4]).toLocalDate() : null);
			dto.setOverallAmount(obj[5] != null ? (Long) obj[5] : null);
			dto.setOrderNo(obj[6] != null ? (String) obj[6] : null);
			dto.setPaymentStatus(obj[7] != null ? (String) obj[7] : null);
			dto.setOnlinePaymentStatus(obj[8] != null ? (String) obj[8] : null);
	        try {
				dto.setEncrypId(MCrypt.getInstance().encryptToText(String.valueOf(obj[0] != null ? (Long) obj[0] : null)));
			} catch (Exception e) {
				e.printStackTrace();
			}
	        return dto;
	    }); 
	}
    
    public PaymentGatewayCcavenueDto initiateTransaction(String encrypPaymentAdviceId, HttpServletRequest request) throws Exception {
    	Long paymentAdviceId = MCrypt.getInstance().decryptToLong(encrypPaymentAdviceId);
    	PaymentGatewayCcavenueDto returnDto=new PaymentGatewayCcavenueDto();
    	
    	Object[] result=tempAccomPaymentAdviceRepository.checkPaymentStatus(paymentAdviceId, ModelConstants.STATUS_ACTIVE, ModelConstants.SUCCESS);
    	if (result != null && result instanceof Object[]) {
    	    Object[] row = (Object[]) result[0];
			Long stayRequestId = (row[0] != null) ? Long.valueOf(row[0].toString()) : null;
			Long totalAmount = (row[1] != null) ? new Long(row[1].toString()) : null;
			String onlinePaymentStatus = (row[2] != null) ? row[2].toString() : null;

			
			if(onlinePaymentStatus!=null && onlinePaymentStatus.equalsIgnoreCase(ModelConstants.SUCCESS)) {
				//set error "PaymentAlreadyCompleted" and return;
		
			}
			
			/** Get the New Order Number **/
			String 	orderId="";
            PaymentGatewayConfigCcavEntity urlEntity = paymentGatewayCcavenueService.getCredentialsForUrl(request);
            if(urlEntity.getInstance()!=null && urlEntity.getInstance().equals(ModelConstants.PRODUCTION)) {
				orderId="TEMP"+ tempPaymentTransactionRepository.getNextOrderId();
			}else {
				orderId="TEMPTEST"+ tempPaymentTransactionRepository.getNextOrderId();
			}
			
			/***
			 * Save the Initiated Transaction Details into 'IIT_PS_TEMP_ACCOM_PAYMENT_TRANSACTION_DETAILS'
			 */
			TempAccomPaymentTransactionEntity transactionEntity = new TempAccomPaymentTransactionEntity();
			TempAccomPaymentAdviceEntity paymentAdvice = new TempAccomPaymentAdviceEntity();
			paymentAdvice.setId(paymentAdviceId);
			transactionEntity.setTempAccomPaymentAdvice(paymentAdvice);
			transactionEntity.setRequestId(stayRequestId);
			transactionEntity.setOverallAmount(totalAmount);
			transactionEntity.setOrderNo(orderId);
			transactionEntity.setPaymentStatus(ModelConstants.INITIATED);
			
			tempPaymentTransactionRepository.save(transactionEntity);
			
			/**
			 * Get Candidate personal details and amount details to show it in Payment gateway page
			 */
			long candidateId=SecurityCtxUtil.candidateId();	//33345;
			Optional<CandidateProfileEntity> profileEntity =  candidateProfileRepository
					.findByIdAndActiveFlag(candidateId, ModelConstants.STATUS_ACTIVE);
			PaymentGatewayCcavenueDto pgDto=PaymentGatewayCcavenueMapper.INSTANCE.toPGDto(profileEntity.get());
			pgDto.setOrderId(orderId);
			pgDto.setTotalAmount(Double.valueOf(totalAmount));
			pgDto.setRedirectUrl(paymentGatewayCcavenueService.getBaseUrl(request)+otherCandidatePayment+paymentResponse+Constants.AFTER_PAYMENTGATEWAY);
			pgDto.setCancelUrl(paymentGatewayCcavenueService.getBaseUrl(request)+otherCandidatePayment+paymentResponse+Constants.PAYMENT_CANCEL);
			
			/**
			 * Redirect to Payment gateway page
			 */
			returnDto=paymentGatewayCcavenueService.redirectPaymentGateway(pgDto, request);
			
			
    	}
    	return returnDto;
    }

    public PaymentGatewayCcavenueDto savePaymentResponse(String responseType, HttpServletRequest request) throws Exception {
    	PaymentGatewayCcavenueDto returnDto=new PaymentGatewayCcavenueDto();
    	
    	System.out.println("encResp---"+request.getParameter("encResp")+"\n");
    	
		String decResp = paymentGatewayCcavenueService.decryptResponse(request);
		System.out.println("decryptedResp---"+decResp+"\n");
		
		// Parse the decrypted response into a map
        Map<String, Object> resMap = paymentGatewayCcavenueService.parseDecryptedResponse(decResp);
        System.out.println("MapResponse---"+resMap+"\n");
    	
        //Update the response in "IIT_PS_TEMP_ACCOM_PAYMENT_TRANSACTION_DETAILS" table
        String orderNo=resMap.get("order_id").toString();
        System.out.println("order_id---"+orderNo+"\n");
        
        TempAccomPaymentTransactionEntity transaction = tempPaymentTransactionRepository.findByOrderNoAndActiveFlag(orderNo,ModelConstants.STATUS_ACTIVE);
        if(transaction!=null) {
        	System.out.println("trans entity order_id---"+transaction.getOrderNo()+"\n");
        	transaction=TempAccomPaymentTransactionMapper.mapToTransactionEntity(transaction,resMap);
        	TempAccomPaymentTransactionEntity updatedTrans=tempPaymentTransactionRepository.save(transaction);
			
        	// update the status in payment advice details table
        	try {
				int count = tempAccomPaymentAdviceRepository.updatePaymentGatewayResponse(updatedTrans,SecurityCtxUtil.userId(),ModelConstants.STATUS_ACTIVE);
			} catch (Exception e) {
				e.printStackTrace();
			}
        	
        	if(responseType!=null && responseType.equalsIgnoreCase(Constants.AFTER_PAYMENT)) {
        		//set the response to dto
            	returnDto=PaymentGatewayCcavenueMapper.changeToPGDto(resMap);
        	}
        	
        }
		
    	return returnDto;
    }
    
    public String PaymentReceiptPdfGenerator(long paymentAdviceId,String orderNo,HttpServletResponse response) throws Exception {
    	//Get the transaction details
    	Object[] result = null;
    	result = tempPaymentTransactionRepository.getCandidateTransactionDetails(paymentAdviceId,orderNo,ModelConstants.STATUS_ACTIVE);
    	if (result != null && result.length > 0 && result instanceof Object[]) {
    	    Object[] obj = (Object[]) result[0];
    	    TempAccomPaymentTransactionEntity transactionEntity = (TempAccomPaymentTransactionEntity) obj[0];
    	    CandidateProfileEntity profile = (CandidateProfileEntity) obj[1];
    	    
	        String fileName = paymentAdviceId + ModelConstants.UNDERSCORE + ModelConstants.CANDIDATE_ACCOMODATION + ModelConstants.UNDERSCORE + System.currentTimeMillis() + ModelConstants.PDF_EXTENSION;
	        response.setContentType("application/pdf");
		  	response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		  	
	        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
	       
	        try (PdfWriter writer = new PdfWriter(response.getOutputStream());
	                PdfDocument pdfDocument = new PdfDocument(writer);
	                Document document = new Document(pdfDocument)) {
	        	
	           	pdfActionService.addDocumentHeader(document, messageSource.getMessage("message.label.heading", null, Locale.getDefault()));
	
	           	document.add(new Paragraph(messageSource.getMessage("message.label.heading.receipt", null, Locale.getDefault()))
	                    .setTextAlignment(TextAlignment.CENTER)
	                    .setBold()
	                    .setFontSize(14));
	           	
	            document.add(new LineSeparator(new SolidLine(1)).setMarginTop(5).setMarginBottom(10));
		        
		     // Add report date
	            LocalDateTime now = LocalDateTime.now();
	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_TIME_WITH_IST);
	            String formattedDate = now.format(formatter);

		        document.add(new Paragraph(messageSource.getMessage("message.label.report.date", null, Locale.getDefault()) + " "+formattedDate)
		                .setTextAlignment(TextAlignment.RIGHT)
		                .setFontSize(10));
		
		     /// Add table for receipt details
		        float[] columnWidths = {200F, 100F,300F};
		        Table table = new Table(columnWidths).useAllAvailableWidth();
		
		        // Add table rows
		        table.addCell(pdfActionService.getCell(messageSource.getMessage("message.label.payment.status", null, Locale.getDefault()), true));
		        table.addCell(pdfActionService.getCell(Constants.HYPHEN, false));
		        table.addCell(pdfActionService.getCell(transactionEntity.getPaymentStatus()!=null ? transactionEntity.getPaymentStatus() :"", false));
		
		        table.addCell(pdfActionService.getCell(messageSource.getMessage("message.label.transaction.id", null, Locale.getDefault()), true));
		        table.addCell(pdfActionService.getCell(Constants.HYPHEN, false));
		        table.addCell(pdfActionService.getCell(transactionEntity.getTransactionRefNumber()!=null ? transactionEntity.getCcavReferenceNo() :"", false));
		
		        table.addCell(pdfActionService.getCell(messageSource.getMessage("message.label.bank.reference.no", null, Locale.getDefault()), true));
		        table.addCell(pdfActionService.getCell(Constants.HYPHEN, false));
		        table.addCell(pdfActionService.getCell(transactionEntity.getCcavReferenceNo()!=null ? transactionEntity.getTransactionRefNumber() : "", false));
		
		        table.addCell(pdfActionService.getCell(messageSource.getMessage("message.label.category.course", null, Locale.getDefault()), true));
		        table.addCell(pdfActionService.getCell(Constants.HYPHEN, false));
		        table.addCell(pdfActionService.getCell(transactionEntity.getTempAccomPaymentAdvice()!=null ? transactionEntity.getTempAccomPaymentAdvice().getCandidateRequest().getCategory() : "", false));
		
		        table.addCell(pdfActionService.getCell(messageSource.getMessage("message.label.application.no", null, Locale.getDefault()), true));
		        table.addCell(pdfActionService.getCell(Constants.HYPHEN, false));
		        table.addCell(pdfActionService.getCell(profile.getId()!=null ? profile.getId().toString() :"", false));
		
		        table.addCell(pdfActionService.getCell(messageSource.getMessage("message.label.candidate.name", null, Locale.getDefault()), true));
		        table.addCell(pdfActionService.getCell(Constants.HYPHEN, false));	
		        table.addCell(pdfActionService.getCell(profile.getCandiateFullName()!=null ? profile.getCandiateFullName():"", false));
		
		        table.addCell(pdfActionService.getCell(messageSource.getMessage("message.label.candidate.contact.no", null, Locale.getDefault()), true));
		        table.addCell(pdfActionService.getCell(Constants.HYPHEN, false));
		        table.addCell(pdfActionService.getCell(profile.getMobileNumber()!=null ? profile.getMobileNumber() : "", false));
		
		        table.addCell(pdfActionService.getCell(messageSource.getMessage("message.label.payment.advice.id", null, Locale.getDefault()), true));
		        table.addCell(pdfActionService.getCell(Constants.HYPHEN, false));
		        table.addCell(pdfActionService.getCell(transactionEntity.getTempAccomPaymentAdvice().getId()!=null ? transactionEntity.getTempAccomPaymentAdvice().getId().toString() : "", false));
		
		        table.addCell(pdfActionService.getCell(messageSource.getMessage("message.label.period.of.stay", null, Locale.getDefault()), true));
		        table.addCell(pdfActionService.getCell(Constants.HYPHEN, false));
		        table.addCell(pdfActionService.getCell(transactionEntity.getTempAccomPaymentAdvice().getHostelPayFromDate().format(dateFormatter) + Constants.TO +transactionEntity.getTempAccomPaymentAdvice().getHostelPayToDate().format(dateFormatter) , false));
		
		        table.addCell(pdfActionService.getCell(messageSource.getMessage("message.label.total.amount", null, Locale.getDefault()), true));
		        table.addCell(pdfActionService.getCell(Constants.HYPHEN, false));
		        table.addCell(pdfActionService.getCell(PdfActionService.RUPEES + ModelConstants.SPACE + utility.formatCommaSeperatedCurrency(transactionEntity.getOverallAmount()!=null ? transactionEntity.getOverallAmount().doubleValue():0), false));
		
		        table.addCell(pdfActionService.getCell(messageSource.getMessage("message.label.transaction.fee", null, Locale.getDefault()), true));
		        table.addCell(pdfActionService.getCell(Constants.HYPHEN, false));
		        table.addCell(pdfActionService.getCell(PdfActionService.RUPEES + ModelConstants.SPACE + utility.formatCommaSeperatedCurrency(transactionEntity.getTransFee()!=null ? transactionEntity.getTransFee().doubleValue():0), false));
		
		        table.addCell(pdfActionService.getCell(messageSource.getMessage("message.label.service.fee", null, Locale.getDefault()), true));
		        table.addCell(pdfActionService.getCell(Constants.HYPHEN, false));
		        table.addCell(pdfActionService.getCell(PdfActionService.RUPEES + ModelConstants.SPACE + utility.formatCommaSeperatedCurrency(transactionEntity.getServiceTax() !=null ? transactionEntity.getServiceTax().doubleValue():0), false));
		
		        // Add table to the document
		        document.add(table);
	
		        // Add footer note
		        document.add(new Paragraph(messageSource.getMessage("message.label.candidate.receipt.note", null, Locale.getDefault()))
		                .setTextAlignment(TextAlignment.LEFT)
		                .setPaddingTop(20F)
		                .setFontSize(10));
		
		        // Close document
		        document.close();
		        
		        return fileName;
	
//		        String watermarkedFileName = ModelConstants.WATER_MARKED + ModelConstants.UNDERSCORE + fileName;
//		        return pdfActionService.addWatermarkAndGetResource(outputFilePath, watermarkedFileName);
	        } catch (IOException e) {
	            throw new Exception(messageSource.getMessage("message.label.error.generate.pdf", null, Locale.getDefault()), e);
	        }
    	}else {
    		return null;
    	}
    }
    
    public Page<TemporaryAccomodationDto> getTemporaryOnlinePaymentDetails(PaginationForm form) {
		String paymentStatus = form.getAdditionalParam().get("paymentStatus")!=null ? form.getAdditionalParam().get("paymentStatus").toString() : null;
	    String submittedFrom = (form.getAdditionalParam().get("submittedFrom")!=null && !form.getAdditionalParam().get("submittedFrom").equals("")) ? form.getAdditionalParam().get("submittedFrom").toString() : null;
	    String submittedTo = (form.getAdditionalParam().get("submittedTo")!=null && !form.getAdditionalParam().get("submittedTo").equals("")) ? form.getAdditionalParam().get("submittedTo").toString() : null;
	    int page = form.getPage() - 1;
	    Pageable pageable = PageRequest.of(page, form.getSize());

		Page<Object[]> result;
		result = tempPaymentTransactionRepository.getTemporaryOnlinePaymentDetails(submittedFrom, submittedTo,
				paymentStatus, pageable);

	    return result.map(objects -> {
	    	TemporaryAccomodationDto dto = new TemporaryAccomodationDto();
	    	dto.setSubmittedDate(objects[0] != null ? (DateUtility.toLocalDateTime(objects[0]).toLocalDate()) : null);
	    	dto.setAdviceId(ValidationCommon.getStringValueOrHyphen(objects[1] !=null ? (String) objects[1] : null));
	    	dto.setOrderNo(ValidationCommon.getStringValueOrHyphen(objects[2] !=null ? (String) objects[2] : null));
	    	dto.setReferenceNo(ValidationCommon.getStringValueOrHyphen(objects[3] !=null ? (String) objects[3] : null));
	    	dto.setPaymentMethod(ValidationCommon.getStringValueOrHyphen(objects[4] !=null ? (String) objects[4] : null));
	    	dto.setNetAmount(objects[5] !=null ? (Double) objects[5] : null);
	    	dto.setPaymentStatus(ValidationCommon.getStringValueOrHyphen(objects[6] !=null ? (String) objects[6] : null));
	        return dto;
	    });
	}
}
