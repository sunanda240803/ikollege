package com.iitm.hosteldine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.student.MessCardAmountTransferDto;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntityId;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntityId;
import com.iitm.hosteldine.form.common.StudentDebitForm;
import com.iitm.hosteldine.form.common.TransactionDto;
import com.iitm.hosteldine.model.student.SaveTransactionFAEntity;
import com.iitm.hosteldine.repository.student.SaveTransactionFARepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class TransferAmountUtils {
	
	private final SaveTransactionFARepository saveTransactionFARepository;
	private final SimsConfigDataService simsConfigDataService;
	private final MessageSource messageSource;
	private final WebClient webClient;

    public static void transferMessToCard(
            MessCardAmountTransferDto currentRecord, BigDecimal totalTransferredAmount, String currentFinYear,
            boolean needFA, int loopIndex, int slNo) {

    }

    public static void transferAmtForMess(TransactionDto transactionDto, TransactionDto ledger, int loopindex) {

    }

    public static TransactionDto createLedgerEntries(boolean needFA, String currentFinYear, String bookType) {
        TransactionDto ledger = new TransactionDto();
        ledger.setFinYear(currentFinYear);
        ledger.setVoucherDate(new Date()); // Set current date
        ledger.setBookType(bookType);
        ledger.setSubAccHead(needFA ? "0" : Constants.STUD);
        ledger.setRecon(ModelConstants.NO);
        ledger.setCancelStatus(ModelConstants.NO);
        ledger.setActiveFlag(ModelConstants.YES);
        ledger.setType(Constants.TRANSFER_AMOUNT);
        return ledger;
    }

    public static MessLedgerAEntity createMessLedgerAEntity(TransactionDto transactionDto, String currentFinYear) {
        MessLedgerAEntity messLedgerAEntity = new MessLedgerAEntity();
        MessLedgerAEntityId messLedgerAEntityId = new MessLedgerAEntityId();
        messLedgerAEntityId.setBookType(transactionDto.getBookType());
        messLedgerAEntityId.setFinYear(currentFinYear);
        messLedgerAEntityId.setVoucherNo(transactionDto.getVoucherNo());
        messLedgerAEntity.setId(messLedgerAEntityId);
        messLedgerAEntity.setVoucherDate(transactionDto.getDate());
        messLedgerAEntity.setAcchead(transactionDto.getAccHead());
        messLedgerAEntity.setSubAccountHead(transactionDto.getSubAccHead());
        messLedgerAEntity.setDescription(transactionDto.getDescription());
        messLedgerAEntity.setDescription1(transactionDto.getDescription1());
        messLedgerAEntity.setChequeNo(transactionDto.getChequeNo());
        messLedgerAEntity.setChequeDate(transactionDto.getChequeDate());
        messLedgerAEntity.setAmount(transactionDto.getAmount());
        messLedgerAEntity.setRp(null);
        messLedgerAEntity.setAdv(transactionDto.getAdv());
        messLedgerAEntity.setAdj(transactionDto.getAdj());
        messLedgerAEntity.setRecon(transactionDto.getRecon());
        messLedgerAEntity.setCancelStatus(transactionDto.getCancelStatus());
        messLedgerAEntity.setUsr(transactionDto.getUser());
        messLedgerAEntity.setFcNo(transactionDto.getFcno());
        messLedgerAEntity.setDocRefNo(transactionDto.getDocRefNo());
        messLedgerAEntity.setA2no(transactionDto.getA2no());
        messLedgerAEntity.setParty(transactionDto.getParty());
        messLedgerAEntity.setDebitOrCredit(transactionDto.getDebitOrCredit());
        messLedgerAEntity.setItAmount(0.0);
        messLedgerAEntity.setItPer(0.0);
        messLedgerAEntity.setItCons(transactionDto.getItcons());
        messLedgerAEntity.setItAcc(transactionDto.getItacc());
        messLedgerAEntity.setLink(0);
        messLedgerAEntity.setMatchDate(null);
        messLedgerAEntity.setSurcharge(0.0);
        messLedgerAEntity.setUnmatchAmnt(0.0);
        messLedgerAEntity.setMrNo(transactionDto.getMrno());
        messLedgerAEntity.setMrDate(null);
        messLedgerAEntity.setScreenType(transactionDto.getScreenType());
        messLedgerAEntity.setStudentCount(transactionDto.getStudentCount());
        messLedgerAEntity.onCreate();
        return messLedgerAEntity;
    }

    public static MessLedgerBEntity createMessLedgerBEntity(TransactionDto transactionDto, String currentFinYear, int jvSeq, int slNo) {
        MessLedgerBEntity messLedgerBEntity = new MessLedgerBEntity();
        MessLedgerBEntityId messLedgerBEntityId = new MessLedgerBEntityId();
        messLedgerBEntityId.setBookType(transactionDto.getBookType());
        messLedgerBEntityId.setFinYear(currentFinYear);
        messLedgerBEntityId.setSlno(slNo);
        messLedgerBEntityId.setVoucherNo(transactionDto.getVoucherNo());
        messLedgerBEntity.setId(messLedgerBEntityId);
        messLedgerBEntity.setVoucherDate(transactionDto.getDate());
        messLedgerBEntity.setAcchead(transactionDto.getAccHead());
        messLedgerBEntity.setSubAccountHead(transactionDto.getSubAccHead());
        messLedgerBEntity.setDescription(transactionDto.getDescription());
        messLedgerBEntity.setDescription1(transactionDto.getDescription1());
        messLedgerBEntity.setChequeNo(transactionDto.getChequeNo());
        messLedgerBEntity.setChequeDate(null);
        messLedgerBEntity.setAmount(transactionDto.getAmount());
        messLedgerBEntity.setRp(null);
        messLedgerBEntity.setAdv(transactionDto.getAdv());
        messLedgerBEntity.setAdj(transactionDto.getAdj());
        messLedgerBEntity.setRecon(transactionDto.getRecon());
        messLedgerBEntity.setCancelStatus(transactionDto.getCancelStatus());
        messLedgerBEntity.setFcNo(transactionDto.getFcno());
        messLedgerBEntity.setDocRefNo(transactionDto.getDocRefNo());
        messLedgerBEntity.setA2no(transactionDto.getA2no());
        messLedgerBEntity.setTdsValue(transactionDto.getTdsValue());
        messLedgerBEntity.setDebitOrCredit(transactionDto.getDebitOrCredit());
        messLedgerBEntity.setBillAmt(transactionDto.getBillAmount());
        messLedgerBEntity.setDocDt(null);
        messLedgerBEntity.setLink(0);
        messLedgerBEntity.setMatchDate(null);
        messLedgerBEntity.setSurcharge(0.0);
        messLedgerBEntity.setUnmatchAmnt(0.0);
        messLedgerBEntity.onCreate();
        return messLedgerBEntity;
    }
    
    public String saveTransactioninFA(StudentDebitForm studentDebitForm) throws Exception {
    	SaveTransactionFAEntity transactionFAEntity = new SaveTransactionFAEntity();
		transactionFAEntity.setScreenType(Constants.IKOLLEGE_TRANSACTION);
		transactionFAEntity.setTransferType(studentDebitForm.getIKollegeTransferType());
		transactionFAEntity.setReferenceNumber(studentDebitForm.getReferenceNumber());
		transactionFAEntity.setDescription(studentDebitForm.getDescription());
		transactionFAEntity.setTotalAmount(studentDebitForm.getAmount());
		transactionFAEntity.setNumOfTransaction(studentDebitForm.getNumOfTransaction());
		transactionFAEntity.setTransactionStatus(WorkflowStatus.INITIATED.getStatus());
		transactionFAEntity.setTransferDate(LocalDate.now());
    	SaveTransactionFAEntity savedEntity = saveTransactionFARepository.save(transactionFAEntity);
    	if(savedEntity!=null) {
    		return updateFATransactionAPI(studentDebitForm);
    	}else {
    		return null;
    	}
    }
    
    public String updateFATransactionAPI(StudentDebitForm studentDebitForm) throws Exception {
    	
    	String status = null;
    	String faCreds = simsConfigDataService.getSimConfigValue(SimsConfigDataService.FA_OHM_CREDENTIALS);
        String faEndPoint = simsConfigDataService.getSimConfigValue(SimsConfigDataService.FA_TRANSACTION_URL);

        if (faEndPoint == null || faEndPoint.isEmpty()) {
        	status = messageSource.getMessage("message.fa.api.url.not.found", null, Locale.getDefault());
            return status;
        }
        String[] creds = null;
        if(faCreds!=null && !faCreds.isEmpty()) {
        	creds = faCreds.split("~");
        }
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        if(creds!=null) {
        	ObjectMapper objectMapper = new ObjectMapper();
            String jsonData = objectMapper.writeValueAsString(studentDebitForm);
            String encodedJsonData = URLEncoder.encode(jsonData, StandardCharsets.UTF_8);
            
            formData.add("userName", creds[0]);
            formData.add("passWord", creds[1]);
            formData.add("companyId", creds[2]);
            formData.add("jsonData", encodedJsonData);
        }
        
        String response = webClient.post()
                .uri(faEndPoint)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        if (response.equalsIgnoreCase(WorkflowStatus.SUCCESS.getStatus())) {
            status = "No of students count: " + studentDebitForm.getStudentCount()
            	+ " and Total amount: " + studentDebitForm.getAmount();
            return status;
        } else {
        	status = messageSource.getMessage("message.fa.transaction.failure", null, Locale.getDefault());
            return status;
        }
    }
}
