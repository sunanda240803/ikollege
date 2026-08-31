package com.iitm.hosteldine.service.student;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.FinancialYearMapper;
import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.controller.TransferAmountUtils;
import com.iitm.hosteldine.dto.SimsConfigDataDto;
import com.iitm.hosteldine.dto.financialYear.FinancialYearDto;
import com.iitm.hosteldine.dto.student.MessCardAmountTransferDto;
import com.iitm.hosteldine.entity.mailQueue.MailQueueDetailsEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntity;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.form.common.TransactionDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.form.common.StudentDebitForm;
import com.iitm.hosteldine.mapper.SimsConfigDataMapper;
import com.iitm.hosteldine.mapper.student.MessCardAmountTransferMapper;
import com.iitm.hosteldine.model.student.MessCardAmountTransferEntity;
import com.iitm.hosteldine.model.student.SaveTransactionFAEntity;
import com.iitm.hosteldine.repository.SimsConfigDataRepository;
import com.iitm.hosteldine.repository.financialYear.FinancialYearRepository;
import com.iitm.hosteldine.repository.mailQueue.MailQueueDetailsRepository;
import com.iitm.hosteldine.repository.mess.MessLedgerARepository;
import com.iitm.hosteldine.repository.mess.MessLedgerBRepository;
import com.iitm.hosteldine.repository.student.MessCardAmountTransferRepository;
import com.iitm.hosteldine.repository.student.SaveTransactionFARepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessToCardRequestService {

	private final MessCardAmountTransferRepository messCardAmountTransferRepository;
	private final StudentDetailsInfoRepository studentDetailsInfoRepository;
	private final CommonResponseUtil commonResponseUtil;
	private final SimsConfigDataService simsConfigDataService;
	private final SimsConfigDataRepository simsConfigDataRepository;
	private final MailQueueDetailsRepository mailQueueDetailsRepository;
	private final MessageSource messageSource;
	private final ExcelUtility excelUtility;
	private final FinancialYearRepository financialYearRepository;
	private final MessLedgerARepository messLedgerARepository;
	private final MessLedgerBRepository messLedgerBRepository;
	private final SaveTransactionFARepository saveTransactionFARepository;
	private final TransferAmountUtils transferAmountUtils;
    private final MailQueueService mailQueueService;
	
	public Page<MessCardAmountTransferDto> getMessToCardRequestList(PaginationForm form, String studentId) {
	    String requestStatus = form.getAdditionalParam().get("requestedStatus")!=null ? form.getAdditionalParam().get("requestedStatus").toString() : null;
	    LocalDate requestDate = (form.getAdditionalParam().get("requestDate")!=null && !form.getAdditionalParam().get("requestDate").equals("")) ? LocalDate.parse(form.getAdditionalParam().get("requestDate").toString()) : null;
	    LocalDate transferDate = (form.getAdditionalParam().get("transferredDate")!=null && !form.getAdditionalParam().get("transferredDate").equals("")) ? LocalDate.parse(form.getAdditionalParam().get("transferredDate").toString()) : null;
	    int page = form.getPage() - 1;
	    Pageable pageable = PageRequest.of(page, form.getSize());

	    Page<Object[]> result;

	    if (form.getSearch() == null || form.getSearch().isEmpty()) {
	        result = messCardAmountTransferRepository.getMessToCardRequestList(
	            studentId, requestStatus, requestDate, transferDate, pageable
	        );
	    } else {
	       String search = form.getSearch().trim();
	        result = messCardAmountTransferRepository.searchMessToCardRequestsList(
	            studentId, requestStatus, requestDate, transferDate, search, pageable
	        );
	    }

	    return result.map(objects -> {
	        MessCardAmountTransferDto dto = new MessCardAmountTransferDto();
	        dto.setMesstocardId((Integer) objects[0]);
	        dto.setStudentId((String) objects[1]);
	        dto.setStudentName((String) objects[2]);
	        dto.setRequestDate(
	            objects[3] != null ? LocalDate.parse((String) objects[3]) : null);
	        dto.setNetBalance((Double) objects[4]);
	        dto.setTransferAmount((Double) objects[5]);
	        dto.setRequestedStatus((String) objects[6]);
	        dto.setTransferredDate(
	            objects[7] != null ? LocalDate.parse((String) objects[7]) : null );
	        return dto;
	    });
	}

	public boolean approveMessToCardRequest(Long id, String status, String rejectReason) throws Exception {
		Optional<MessCardAmountTransferEntity> messEntity = messCardAmountTransferRepository.findByMesstocardIdAndRequestedStatusAndActiveFlag(id,WorkflowStatus.PENDING.getStatus() ,ModelConstants.STATUS_ACTIVE);
		boolean result = false;
		if(messEntity.isPresent()) {
			MessCardAmountTransferEntity entity = messEntity.get();
			if(status.equals(WorkflowStatus.APPROVED.getStatus())) {				
				entity.setRequestedStatus(WorkflowStatus.APPROVED.getStatus());
			}else {
				entity.setRequestedStatus(WorkflowStatus.REJECTED.getStatus());
			}
			MessCardAmountTransferEntity savedEntity =  messCardAmountTransferRepository.saveAndFlush(entity);
			if (savedEntity != null) {
				result = true;
				Optional<StudentDetailsInfoEntity> infoEntity = studentDetailsInfoRepository
						.findByStudentIdAndActiveFlag(savedEntity.getStudentId(), ModelConstants.STATUS_ACTIVE);
				if (infoEntity.isPresent()) {
					String studentId = infoEntity.get().getStudentId();
					String studentEmailSuffix = simsConfigDataService
							.getSimConfigValue(SimsConfigDataService.STUDENT_MAIL_SUFFIX);
					String defaultEmail = studentId + studentEmailSuffix;
					String studentMail = Objects.nonNull(infoEntity.get().getEmailId()) ? infoEntity.get().getEmailId()
							: defaultEmail;
					String lastName = "";
					if (infoEntity.get().getLastName() != null && !infoEntity.get().getLastName().isEmpty()) {
						lastName = infoEntity.get().getLastName();
					}
					String studentName = infoEntity.get().getFirstName() + (lastName.isEmpty() ? "" : " " + lastName);
					String requestDate = DateUtility.formatDateInd(savedEntity.getRequestDate());
					String subject = status.equals(WorkflowStatus.APPROVED.getStatus())
							? commonResponseUtil.getMessage("message.mail.subject.approve.mess.card.request")
							: commonResponseUtil.getMessage("message.mail.subject.reject.mess.card.request");
					String message = status.equals(WorkflowStatus.APPROVED.getStatus())
							? commonResponseUtil.getMessage("message.mail.body.approve.mess.card.request")
							: commonResponseUtil.getMessage("message.mail.body.reject.mess.card.request");
					Boolean mailStatus = sendMessToCardEmail(studentId, studentMail, studentName, requestDate, subject,
							message, rejectReason);
					result = mailStatus;
				}
			}
		}
		return result;
	}

	public boolean sendMessToCardEmail(String studentId, String studentMail, String studentName, String requestDate,
			String subject, String message, String rejectReasons) throws Exception {
		String messageTemplate = simsConfigDataService
				.getSimConfigValue(SimsConfigDataService.MESS_TO_CARD_REQUEST_MAIL_TEMPLATE);
		messageTemplate = messageTemplate.replace("#%date%#", DateUtility.formatDateInd(new java.util.Date()))
				.replace("#%subject%#", subject).replace("#%studentId%#", studentId)
				.replace("#%studentName%#", studentName).replace("#%message%#", message)
				.replace("#%request_date%#", requestDate);
		if (rejectReasons != null) {
			messageTemplate = messageTemplate.replace("#%rejectReason%#", "").replace("#%reject_reason%#",
					rejectReasons);
		} else {
			messageTemplate = messageTemplate.replace("#%rejectReason%#", "style='display:none;'");
		}
		MailQueueDetailsEntity mailQueueDetails = new MailQueueDetailsEntity();
		String module; 
		if (rejectReasons != null && !rejectReasons.equals(Constants.NULL) && !rejectReasons.isEmpty()) {
			module = ModelConstants.MESS_TO_CARD_REQUEST_REJECT;
		} else {
			module = ModelConstants.MESS_TO_CARD_REQUEST_APPROVAL;
		}
		return mailQueueService.saveMailQueue(subject, studentName, messageTemplate, studentMail, module, studentId,
                1, null, null, null, null, null);
	}

	public Workbook downloadAllMessToCardRequestReport(MessCardAmountTransferDto messCardAmountTransferDto) {
		String sheetName = messageSource.getMessage("message.label.all.mess.card.request.details", null, Locale.getDefault());
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);

		int columnCount = ExcelConstants.ALL_MESS_CARD_REQUEST_HEADER_DATA.length;
		String[] headerData = ExcelConstants.ALL_MESS_CARD_REQUEST_HEADER_DATA;
		String[] headerDataWidth = ExcelConstants.ALL_MESS_CARD_REQUEST_HEADER_DATA_WIDTH;

		XSSFCellStyle style3 = excelUtility.setDataStyle(workbook);
		XSSFCellStyle centerAlignStyle = excelUtility.setCenterAlignStyle(workbook);

		Row row0 = sheet.createRow(0);
		Cell cell0 = row0.createCell(0);
		cell0.setCellValue(messageSource.getMessage("message.label.mess.card.request.report", null, Locale.getDefault()));
		cell0.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

		Row row1 = sheet.createRow(1);
		Cell cell1 = row1.createCell(0);
		SimpleDateFormat sdf = new SimpleDateFormat(
				messageSource.getMessage("session.date.format", null, Locale.getDefault()));
		String reportDate = messageSource.getMessage("message.label.report.date.colon", null, Locale.getDefault())
				+ sdf.format(new Date());
		cell1.setCellValue(reportDate);
		cell1.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, columnCount - 1));

		Row row2 = sheet.createRow(2);
		excelUtility.createHeader(row2, 0, headerData, workbook);

		IntStream.range(0, headerData.length).forEach(i -> {
			int width = Integer.parseInt(headerDataWidth[i]);
			sheet.setColumnWidth(i, width);
		});

		String requestStatus = messCardAmountTransferDto.getRequestedStatus();
		LocalDate requestDate = messCardAmountTransferDto.getRequestDate();
	    LocalDate transferDate = messCardAmountTransferDto.getTransferredDate();
	    String studentId = SecurityCtxUtil.accountType().equals(ModelConstants.STUDENT) ? SecurityCtxUtil.userId().toString() : null;
	    Pageable pageable = PageRequest.of(0, 1000000);

	    Page<Object[]> result = null;
	    if (messCardAmountTransferDto.getSearch() == null || messCardAmountTransferDto.getSearch().isEmpty()) {
	        result = messCardAmountTransferRepository.getMessToCardRequestList(
		            	studentId, requestStatus, requestDate, transferDate, pageable
	    	        );
	    } else {
	       String search = messCardAmountTransferDto.getSearch().trim();
	        result = messCardAmountTransferRepository.searchMessToCardRequestsList(
	            studentId, requestStatus, requestDate, transferDate, search, pageable
	        );
	    }
	    
		List<MessCardAmountTransferDto> dtoList = new ArrayList<>();
		// Iterate over the result page and map each Object[] to your DTO
		for (Object[] objects : result.getContent()) {
			MessCardAmountTransferDto dto = new MessCardAmountTransferDto();
			dto.setMesstocardId((Integer) objects[0]);
	        dto.setStudentId((String) objects[1]);
	        dto.setStudentName((String) objects[2]);
	        dto.setRequestDate(
	            objects[3] != null ? LocalDate.parse((String) objects[3]) : null);
	        dto.setNetBalance((Double) objects[4]);
	        dto.setTransferAmount((Double) objects[5]);
	        dto.setRequestedStatus((String) objects[6]);
	        dto.setTransferredDate(
	            objects[7] != null ? LocalDate.parse((String) objects[7]) : null );
		    dtoList.add(dto);
		}
		AtomicInteger rowCount = new AtomicInteger(2);

		dtoList.forEach(messCard -> {
			Row row = sheet.createRow(rowCount.incrementAndGet());

			String[] values = { getNonNullValue(messCard.getMesstocardId()),getNonNullValue(DateUtility.formatDateInd(messCard.getRequestDate())),
								getNonNullValue(messCard.getStudentId()),getNonNullValue(messCard.getStudentName()),
								getNonNullValue(messCard.getNetBalance()),getNonNullValue(messCard.getTransferAmount()),
								getNonNullValue(DateUtility.formatDateInd(messCard.getTransferredDate())),getNonNullValue(messCard.getRequestedStatus())};

			for (int colIdx = 0; colIdx < values.length; colIdx++) {
				excelUtility.createAndSetColumn(sheet, row, colIdx, -1, values[colIdx], style3);
			}
		});

		for (int i = 0; i < columnCount; i++) {
			sheet.autoSizeColumn(i);
		}

		return workbook;
	}
	
	private String getNonNullValue(Object value) {
		return value == null ? "" : value.toString();
	}

	@Transactional
	public String transferMessCardAmount(MessCardAmountTransferDto messCardAmountTransferDto) throws Exception {
		String status = null;
		
		List<String> studentIds = Arrays.asList(messCardAmountTransferDto.getStudentIdData().split(","));
		List<String> messCardIds = Arrays.asList(messCardAmountTransferDto.getMessCardIdData().split(","));
        List<String> transferAmounts = Arrays.asList(messCardAmountTransferDto.getTransferAmountData().split(","));
        BigDecimal totalTransferAmount = BigDecimal.ZERO;
        if(studentIds!=null && !studentIds.isEmpty()) {
	        for (int i = 0; i < studentIds.size(); i++) {
	            BigDecimal amount = new BigDecimal(transferAmounts.get(i));
	            totalTransferAmount = totalTransferAmount.add(amount);
	        }
        }
		String needFA = simsConfigDataService.getSimConfigValue(SimsConfigDataService.IKOLLEGE_FA_INTEGRATION);
		//Getting current fin year
		FinancialYearDto finYearDto = Optional.ofNullable(financialYearRepository.getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE))
                .map(FinancialYearMapper.INSTANCE::toDto)
                .orElse(null);
		String currentFinYear = finYearDto.getFinYear();
		//Updating mess card request list status to transferred
		MessCardAmountTransferEntity savedEntity = new MessCardAmountTransferEntity();
		List<MessCardAmountTransferDto> dtoList = new ArrayList<MessCardAmountTransferDto>();
		if(studentIds!=null && !studentIds.isEmpty()) {
			for(int i = 0; i < studentIds.size(); i++) {
				Long id = Long.parseLong(messCardIds.get(i));
				String studentId = studentIds.get(i);
				Optional<MessCardAmountTransferEntity> messEntity = messCardAmountTransferRepository.findByMesstocardIdAndStudentIdAndActiveFlag(id,studentId,ModelConstants.STATUS_ACTIVE);
				if(messEntity.isPresent()) {
					MessCardAmountTransferDto cardAmountTransferDto = new MessCardAmountTransferDto();
					messEntity.get().setRequestedStatus(WorkflowStatus.TRANSFERRED.getStatus());
					messEntity.get().setTransferredDate(LocalDate.now());
					savedEntity = messCardAmountTransferRepository.save(messEntity.get());
					cardAmountTransferDto = MessCardAmountTransferMapper.INSTANCE.fromMessCardAmountTransferEntity(savedEntity);
					cardAmountTransferDto.setListSize(studentIds.size());
					dtoList.add(cardAmountTransferDto);
					//TransferAmountUtils.transferMessToCard(messCardAmountTransferDto, totalTransferAmount, currentFinYear, false, i, i);
					status = Constants.SAVED;
				}
			}
			String transferStatus = transferMessToCard(dtoList, totalTransferAmount, currentFinYear, Boolean.parseBoolean(needFA), studentIds.size(), studentIds.size(),studentIds);
		}
		
		return status;
	}
	public String transferMessToCard(List<MessCardAmountTransferDto> messCardAmountTransferList, BigDecimal totalTransferredAmount,
									 String currentFinYear, boolean needFA, int loopIndex, int slNo,List<String> studentIds) throws Exception {
        String status = null;
        int jvSeq = messLedgerARepository.getNextValMessLedger();
        List<MessLedgerAEntity> messLedgerAList = new ArrayList<MessLedgerAEntity>();
        List<MessLedgerBEntity> messLedgerBList = new ArrayList<MessLedgerBEntity>();
        TransactionDto messTranserForm = new TransactionDto();
        for(MessCardAmountTransferDto currentRecord : messCardAmountTransferList ) {
	        TransactionDto transferForm = new TransactionDto();
	        transferForm.setAccHead(currentRecord.getStudentId());
	        transferForm.setAmount(currentRecord.getTransferAmount());
	        transferForm.setDescription(commonResponseUtil.getMessage("message.transfer.amount.description"));
	        // Condition to set the values based on needFA flag
	        if (needFA) {
	            transferForm.setTotalTransferAmount(totalTransferredAmount.doubleValue());
	            transferForm.setListSize(currentRecord.getListSize());
	            transferForm.setIKollegeTransferType(Constants.MESS_CC_EXCHEANGE_TYPE);
	        } else {
	            transferForm.setSlNo(0);
	        }
	        messTranserForm = transferForm;
			String description = commonResponseUtil.getMessage("message.transfer.amount.description");

	        List<TransactionDto> transferAmtList1 = new ArrayList<>();
	        TransactionDto ledgerAMess = TransferAmountUtils.createLedgerEntries(needFA, currentFinYear, Constants.MESS_MS);
			ledgerAMess.setAccHead(needFA ? Constants.MESS_CC_EXCHEANGE : currentRecord.getStudentId());
			ledgerAMess.setDebitOrCredit(needFA ? Constants.CREDIT : Constants.DEBIT);
	        ledgerAMess.setDescription(description);
			ledgerAMess.setAmount(transferForm.getAmount());
	        transferAmtList1.add(ledgerAMess);
	        transferForm.setTransferAmtList1(transferAmtList1);

	        List<TransactionDto> transferAmtList3 = new ArrayList<>();
	        TransactionDto ledgerACard = TransferAmountUtils.createLedgerEntries(needFA, currentFinYear, Constants.CREDIT_CARD);;
	        ledgerACard.setAccHead(Constants.MESS_CC_EXCHEANGE);
			ledgerACard.setSubAccHead("0");
	        ledgerACard.setDescription(Constants.MESS_CC_EXCHEANGE);
	        ledgerACard.setDebitOrCredit(Constants.DEBIT);
			ledgerACard.setDescription(description);
			ledgerACard.setAmount(transferForm.getAmount());
	        transferAmtList3.add(ledgerACard);
	        transferForm.setTransferAmtList3(transferAmtList3);
	
	        List<TransactionDto> transferAmtList2 = new ArrayList<>();
	        TransactionDto ledgerBMess = TransferAmountUtils.createLedgerEntries(needFA, currentFinYear, Constants.MESS_MS);
	        ledgerBMess.setSlNo(slNo);
			ledgerBMess.setAccHead(Constants.MESS_CC_EXCHEANGE);
	        ledgerBMess.setSubAccHead(needFA ? Constants.STUD : "0");
			ledgerBMess.setDescription(description);
	        ledgerBMess.setDocRefNo(transferForm.getAccHead());
	        ledgerBMess.setAmount(transferForm.getAmount());
	        ledgerBMess.setDebitOrCredit(needFA ? Constants.DEBIT : Constants.CREDIT);
	        transferAmtList2.add(ledgerBMess);
	        transferForm.setTransferAmtList2(transferAmtList2);
	
	        List<TransactionDto> transferAmtList4 = new ArrayList<TransactionDto>();
			TransactionDto ledgerBCard = TransferAmountUtils.createLedgerEntries(needFA, currentFinYear, Constants.CREDIT_CARD);;
			ledgerBCard.setSlNo(slNo);
			ledgerBCard.setAccHead(currentRecord.getStudentId());
			ledgerBCard.setSubAccHead("0");
			ledgerBCard.setDescription(description);
			ledgerBCard.setDocRefNo(transferForm.getAccHead());
			ledgerBCard.setAmount(transferForm.getAmount());
	        ledgerBCard.setDebitOrCredit(Constants.CREDIT);
	
	        transferAmtList4.add(ledgerBCard);
	        transferForm.setTransferAmtList4(transferAmtList4);
	        // TransferAmountUtils.transferAmtForMess(transferForm, ledgerA1, loopIndex);
		    for(TransactionDto billing : transferForm.getTransferAmtList1()) {
		    	billing.setScreenType(Constants.MESS_CC_EXCHEANGE_TYPE);
		    	billing.setStudentCount(loopIndex);
		    	billing.setDate(LocalDate.now());
                billing.setRecon(ModelConstants.STATUS_INACTIVE);
				billing.setVoucherNo(Constants.JV + (String.format("%08d", jvSeq)));
		        MessLedgerAEntity messLedgerAEntity = TransferAmountUtils.createMessLedgerAEntity(billing, currentFinYear);
		        messLedgerAList.add(messLedgerAEntity);
		       // savedLedgerA1 = messLedgerARepository.save(messLedgerAEntity);
		    }
		    for(TransactionDto billing : transferForm.getTransferAmtList3()) {
		    	billing.setScreenType(Constants.MESS_CC_EXCHEANGE_TYPE);
		    	billing.setStudentCount(loopIndex);
		    	billing.setDate(LocalDate.now());
		    	billing.setVoucherNo(Constants.JV + (String.format("%08d", jvSeq)));
                billing.setRecon(ModelConstants.STATUS_INACTIVE);
		        MessLedgerAEntity messLedgerAEntity = TransferAmountUtils.createMessLedgerAEntity(billing, currentFinYear);
		        messLedgerAList.add(messLedgerAEntity);
		        //savedLedgerA3 = messLedgerARepository.save(messLedgerAEntity);
		    }
		    for(TransactionDto billing : transferForm.getTransferAmtList2()) {
		    	billing.setDate(LocalDate.now());
		    	billing.setVoucherNo(Constants.JV + (String.format("%08d", jvSeq)));
                billing.setRecon(ModelConstants.STATUS_INACTIVE);
		    	MessLedgerBEntity messLedgerBEntity = TransferAmountUtils.createMessLedgerBEntity(billing, currentFinYear, jvSeq,slNo);
		    	messLedgerBList.add(messLedgerBEntity);
		       // savedLedgerA2 = messLedgerBRepository.save(messLedgerBEntity);
		    }
		    for(TransactionDto billing : transferForm.getTransferAmtList4()) {
		    	billing.setDate(LocalDate.now());
		    	billing.setVoucherNo(Constants.JV + (String.format("%08d", jvSeq)));
                billing.setRecon(ModelConstants.STATUS_INACTIVE);
		        MessLedgerBEntity messLedgerBEntity = TransferAmountUtils.createMessLedgerBEntity(billing, currentFinYear, jvSeq,slNo);
		        messLedgerBList.add(messLedgerBEntity);
		        //savedLedgerA4 = messLedgerBRepository.save(messLedgerBEntity);
		    }
        }
        if((messLedgerAList!=null && !messLedgerAList.isEmpty()) && (messLedgerBList!=null && !messLedgerBList.isEmpty())) {
        	List<MessLedgerAEntity> savedAEntity = messLedgerARepository.saveAll(messLedgerAList);
        	List<MessLedgerBEntity> savedBEntity = messLedgerBRepository.saveAll(messLedgerBList);
        	if(savedAEntity!=null && savedBEntity!=null) {
        		status = Constants.SAVED;
				StudentDebitForm studentDebitForm = new StudentDebitForm();
				studentDebitForm.setReferenceNumber(Constants.JV+jvSeq);
				studentDebitForm.setAmount(totalTransferredAmount.doubleValue());
				studentDebitForm.setStudentCount(loopIndex);
				studentDebitForm.setTransferStatus(WorkflowStatus.INITIATED.getStatus());
				studentDebitForm.setIKollegeTransferType(messTranserForm.getIKollegeTransferType());
				studentDebitForm.setScreenType(Constants.IKOLLEGE_TRANSACTION);
				studentDebitForm.setHostelName("");
				studentDebitForm.setDescription(messTranserForm.getDescription());
				studentDebitForm.setStudentIds(studentIds);
				studentDebitForm.setReferenceNumber(Constants.JV+jvSeq);
				studentDebitForm.setAmount(totalTransferredAmount.doubleValue());
				studentDebitForm.setNumOfTransaction(messTranserForm.getListSize());;

				if(needFA) {
					status = transferAmountUtils.saveTransactioninFA(studentDebitForm);
				}
			}

        	//Sending mail to students
        	for(MessCardAmountTransferDto dto : messCardAmountTransferList) {
        		Optional<StudentDetailsInfoEntity> infoEntity = studentDetailsInfoRepository.findByStudentIdAndActiveFlag(dto.getStudentId(), ModelConstants.STATUS_ACTIVE);
				if(infoEntity.isPresent()) {
					String studentId = infoEntity.get().getStudentId();
					String studentEmailSuffix = simsConfigDataService.getSimConfigValue(SimsConfigDataService.STUDENT_MAIL_SUFFIX);
					String defaultEmail = studentId + studentEmailSuffix;
					String studentMail = Objects.nonNull(infoEntity.get().getEmailId()) ? infoEntity.get().getEmailId() : defaultEmail;
					String lastName = "";
					if(infoEntity.get().getLastName()!=null && !infoEntity.get().getLastName().isEmpty()) {
						lastName = infoEntity.get().getLastName();
					}
					String studentName = infoEntity.get().getFirstName() + (lastName.isEmpty() ? "" : " " + lastName);
					String requestDate = DateUtility.formatDateInd(dto.getRequestDate());
					String subject = commonResponseUtil.getMessage("message.mail.subject.approve.mess.card.request");
					String message = commonResponseUtil.getMessage("message.mail.body.approve.mess.card.request");
					Boolean mailStatus = sendMessToCardEmail(studentId,studentMail,studentName,requestDate,subject,message,null);
				}
        	}
        }
        return status;
    }
}
