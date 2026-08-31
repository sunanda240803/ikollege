package com.iitm.hosteldine.service.student;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.student.MessCardAmountTransferDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.student.MessCardAmountTransferMapper;
import com.iitm.hosteldine.model.student.MessCardAmountTransferEntity;
import com.iitm.hosteldine.repository.student.MessCardAmountTransferRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessToCardTransferService {
	private final MessCardAmountTransferRepository messCardAmountTransferRepository;
	private final MessageSource messageSource;
	private final MessToCardTransferControllerService messToCardTransferControllerService;
	
	public Page<MessCardAmountTransferDto> getMessToCardList(PaginationForm form, String studentId) {
	    String requestStatus = null;
	    LocalDate requestDate = null;
	    LocalDate transferDate = null;
	    int page = form.getPage() - 1;
	    Pageable pageable = PageRequest.of(page, form.getSize());

	    Page<Object[]> result;

	    if (form.getSearch() == null || form.getSearch().isEmpty()) {
	        result = messCardAmountTransferRepository.getMessToCardRequestList(
	            studentId, requestStatus, requestDate, transferDate, pageable
	        );
	    } else {
	        String search = form.getSearch().trim();
	        result = messCardAmountTransferRepository.searchMessToCardRequests(
	            studentId,  search, pageable
	        );
	    }

	    return result.map(objects -> {
	        MessCardAmountTransferDto dto = new MessCardAmountTransferDto();
	        dto.setMesstocardId((Integer) objects[0]);
	        dto.setStudentId((String) objects[1]);
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

	public String getStudentLedgerBalance(String studentId) {
	    String ledgerBalance = "0.00"; 
	    List<Object[]> balances = messCardAmountTransferRepository.getStudentBalance(studentId);
	    if (balances != null && !balances.isEmpty() && balances.get(0) != null && balances.get(0).length > 0) {
	        ledgerBalance = balances.get(0)[0].toString(); 
	    }
	    return ledgerBalance; 
	}



	 public String saveMessToCardAmount(MessCardAmountTransferDto dto) throws Exception {  
		 
		 MessCardAmountTransferEntity newEntity = MessCardAmountTransferMapper.INSTANCE.onSaveEntity(dto);
		 String studentId = SecurityCtxUtil.userId().toUpperCase();
		 newEntity.setStudentId(studentId);
		 newEntity.setRequestedStatus(WorkflowStatus.PENDING.getStatus());
		 newEntity.setRequestDate(LocalDate.now());
		 //newEntity.setTransferredDate(LocalDate.now());
		 messCardAmountTransferRepository.save(newEntity); 
		 return Constants.SAVED;
	 }
	
	 public void validateMessToCard(MessCardAmountTransferDto messCardAmountTransferDto, BindingResult result) {
		    // Define status and error message
		    String transferAmountField = "transferAmount";
		    String invalidTransferAmountCode = "transferAmount.invalid";
		    String errorMessage = "";

		    // Get the max transfer amount
		    Double maxAmount = messToCardTransferControllerService.getMaxTransferAmount();

		    // Check if transfer amount is empty or null
		    if (messCardAmountTransferDto.getTransferAmount() == null || messCardAmountTransferDto.getTransferAmount() <= 0) {
		        errorMessage = messageSource.getMessage("message.label.amount.required", null, Locale.getDefault());
		    } 
		    // Check if transfer amount exceeds the maximum allowed amount
		    else if (messCardAmountTransferDto.getTransferAmount() > maxAmount) {
		        errorMessage = messageSource.getMessage("message.label.amount.less.than", null, Locale.getDefault()) + maxAmount;
		    } 
		    // Check if transfer amount exceeds the available net balance
		    else if (messCardAmountTransferDto.getNetBalance() != null && 
		             messCardAmountTransferDto.getTransferAmount() > messCardAmountTransferDto.getNetBalance()) {
		        errorMessage = messageSource.getMessage("message.label.insufficient.balance", null, Locale.getDefault());
		    } 
		    // Check if total of transfer amount and previous requests exceeds the max limit
		    else if (messCardAmountTransferDto.getTotalFinalAmount() != null && 
		             messCardAmountTransferDto.getTransferAmount() + messCardAmountTransferDto.getTotalFinalAmount() >maxAmount) {
		        errorMessage = messageSource.getMessage("message.label.amount.check.previous.transfer", null, Locale.getDefault());
		    }

		    if (!errorMessage.isEmpty()) {
		        result.rejectValue(transferAmountField, invalidTransferAmountCode, errorMessage);
		    }
		}



	 public Double getTotalTransferAmount() {
		 String studentId = SecurityCtxUtil.userId().toUpperCase();
		    Double totalTransferAmount = messCardAmountTransferRepository.getTotalTransferAmount(ModelConstants.STATUS_ACTIVE, WorkflowStatus.REJECTED.getStatus(),studentId);
		    return totalTransferAmount != null ? totalTransferAmount : 0.0;
		}
}
	
	
	

