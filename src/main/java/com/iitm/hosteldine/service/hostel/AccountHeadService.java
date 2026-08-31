package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.FinancialYearMapper;
import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.financialYear.FinancialYearDto;
import com.iitm.hosteldine.dto.hostel.AccountHeadDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.hostel.AccountHeadMapper;
import com.iitm.hosteldine.model.financialYear.FinancialYearEntity;
import com.iitm.hosteldine.model.hostel.AccountHeadEntity;
import com.iitm.hosteldine.repository.financialYear.FinancialYearRepository;
import com.iitm.hosteldine.repository.hostel.AccountHeadRepository;
import com.iitm.hosteldine.util.RoleEnum;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.validator.common.ValidationCommon;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountHeadService {
    private final AccountHeadRepository accountHeadRepository;
    private final FinancialYearRepository financialYearRepository;
    private final MessageSource messageSource;
    private final Utility utility;

    public List<AccountHeadDto> getAccoutHeadList(){
        return accountHeadRepository.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE)
                .stream()
                .map(AccountHeadMapper.INSTANCE::toDto)
                .sorted(Comparator.comparing(AccountHeadDto::getAccname))
                .toList();
    }

    public List<AccountHeadDto> getCatererAccountHeadList(){
    	FinancialYearDto finYearDto = getFinYearDto();
    	
        return accountHeadRepository.getAccountHeadList(Constants.ACCOUNT_HEAD_TYPE_R, finYearDto.getFinYear(), ModelConstants.STATUS_ACTIVE, DateUtility.getNowDate())
                .stream()
				
                .map(AccountHeadMapper.INSTANCE::toDto)
                .sorted(Comparator.comparing(AccountHeadDto::getAccname))
                .toList();
    }
    
	public FinancialYearDto getFinYearDto() {
		return Optional.ofNullable(financialYearRepository.getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE))
				.map(FinancialYearMapper.INSTANCE::toDto).orElse(null);
	}
	
	public String getFinYear() {
		return Optional.ofNullable(financialYearRepository.getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE))
				.map(FinancialYearMapper.INSTANCE::toDto).map(FinancialYearDto::getFinYear).orElse(null);
	}
	
	public List<AccountHeadDto> getAccountHeadListByFinYear() {
		FinancialYearDto finYearDto = getFinYearDto();
		return accountHeadRepository.findByIdFinYearAndActiveFlag(finYearDto.getFinYear(), ModelConstants.STATUS_ACTIVE)
				.stream().map(AccountHeadMapper.INSTANCE::toDto)
				.sorted(Comparator.comparing(AccountHeadDto::getAccname)).toList();
	}
    
    public Page<AccountHeadDto> getAccHeadList(PaginationForm form) {
		Page<AccountHeadEntity> result = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("id.acchead").ascending());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			result = accountHeadRepository.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE, pageable);
		} else {
			result = accountHeadRepository.findByAccountHeadSearchList(ModelConstants.STATUS_ACTIVE, pageable,form.getSearch());
		}
		return result.map(AccountHeadMapper.INSTANCE::toDto);
	}

	public boolean deleteAccountHeadByAccHead(String acchead) throws NoSuchMessageException, RecordNotExistsException {
		/*if (accountHeadRepository.existsByActiveFlagAndId_Acchead(ModelConstants.STATUS_ACTIVE, acchead)) {
			throw new RecordNotExistsException(
					messageSource.getMessage("message.validation.error.hostel.assigned", null, Locale.getDefault()));
		}*/
		return accountHeadRepository.findById_Acchead(acchead).map(entity -> {
			entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
			AccountHeadEntity savedEntity = accountHeadRepository.save(entity);
	        if (savedEntity != null) {
	            return true;
	        }
	        return false;
		}).orElseThrow(() -> new RecordNotExistsException(
				messageSource.getMessage("̥message.validation.error.account.head.not.found", null, Locale.getDefault())));
	}

	public String saveUpdateAccountHead(AccountHeadDto dto) {
		FinancialYearEntity fnYear= financialYearRepository.getFinYearDetails(Constants.CURRENT_YEAR,ModelConstants.STATUS_ACTIVE);
		String result = null;
		if(fnYear!=null) {
			
			result = Optional.ofNullable(dto.getId().getAcchead()).filter(accHead -> (accHead != null && !accHead.isEmpty()))
				.flatMap(accHead -> accountHeadRepository.findById_AccheadAndActiveFlag(accHead, ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					AccountHeadMapper.INSTANCE.onUpdateEntity(existingEntity, dto);
					accountHeadRepository.save(existingEntity); 
					return Constants.UPDATED;
				}).orElseGet(() -> {
					//Getting finyear and company ID
					dto.getId().setFinYear(fnYear.getFinYear());
					dto.setCompanyid(fnYear.getCompanyid());
					
					AccountHeadEntity newEntity = AccountHeadMapper.INSTANCE.onSaveEntity(dto);
					accountHeadRepository.save(newEntity); 
					return Constants.SAVED;
				});
		}
		return result;
	}

	public AccountHeadDto getAccountHeadByAccHead(String accHead) {
		 return accountHeadRepository
					.findById_AccheadAndActiveFlag(accHead, ModelConstants.STATUS_ACTIVE)
					.map(AccountHeadMapper.INSTANCE::toDto).orElse(new AccountHeadDto());
	}

	public boolean checkAccHeadExist(String accHead) {
		return accountHeadRepository
				.findById_AccheadAndActiveFlagIgnoreCaseAndId_AccheadNot(
						accHead, ModelConstants.STATUS_ACTIVE, accHead).size()>0;
	}

	public void validateAccountHead(AccountHeadDto accountHeadDto, BindingResult bindingResult) {
		
		String errorMessage = "";
		
			if(accountHeadDto.getId()==null || accountHeadDto.getId().getAcchead()==null || accountHeadDto.getId().getAcchead().isEmpty()) {
				errorMessage = messageSource.getMessage("message.validation.account.head.required", null, Locale.getDefault());
				 bindingResult.rejectValue("id.acchead", "error.id.acchead", errorMessage);
			}
			if(accountHeadDto.getAccname()==null || accountHeadDto.getAccname().isEmpty()) {
				errorMessage = messageSource.getMessage("message.validation.account.name.required", null, Locale.getDefault());
				bindingResult.rejectValue("accname", "error.accname", errorMessage);
			}
			if(accountHeadDto.getType()==null || accountHeadDto.getType().isEmpty()) {
				errorMessage = messageSource.getMessage("message.validation.account.type.required", null, Locale.getDefault());
				bindingResult.rejectValue("type", "error.type", errorMessage);
			}
			if(accountHeadDto.getOpbal()==null || accountHeadDto.getOpbal().toString().isEmpty()) {
				errorMessage = messageSource.getMessage("message.validation.opening.balance.required", null, Locale.getDefault());
				bindingResult.rejectValue("opbal", "error.opbal", errorMessage);
			}
			if(accountHeadDto.getOpdate()==null || accountHeadDto.getOpdate().toString().isEmpty()) {
				errorMessage = messageSource.getMessage("message.validation.opening.balance.date.required", null, Locale.getDefault());
				bindingResult.rejectValue("opdate", "error.opdate", errorMessage);
			}
			if(accountHeadDto.getOpdate()!=null && accountHeadDto.getCldate()!=null) {
				if(accountHeadDto.getOpdate().isAfter(accountHeadDto.getCldate()) || accountHeadDto.getCldate().isBefore(accountHeadDto.getOpdate())) {
					errorMessage = messageSource.getMessage("message.label.closing.date.greater.opening.date", null, Locale.getDefault());
					bindingResult.rejectValue("cldate", "error.cldate", errorMessage);
				}
			}
		
		if (!errorMessage.isEmpty()) {
			bindingResult.rejectValue(null,errorMessage);
	    }
		
	}

	public List<AccountHeadDto> getBankList(List<String> type) {
		FinancialYearEntity finYearDetails = financialYearRepository.getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE);
		return accountHeadRepository.getBankListBy(finYearDetails.getFinYear(),ModelConstants.STATUS_ACTIVE,type)
				.map(l->l.stream().map(AccountHeadMapper.INSTANCE::toDto).toList())
				.orElse(Collections.emptyList());
	}
	
	public Page<AccountHeadDto> getAccountHeadSummaryList(PaginationForm form){
		String fromDate = ValidationCommon.toStringOrNull(form.getAdditionalParam().get("fromDate"));
		String toDate = ValidationCommon.toStringOrNull(form.getAdditionalParam().get("toDate"));
		
		int page = form.getPage() - 1;
		Pageable pageable = Pageable.unpaged();
		Page<Object[]> result = Page.empty();
		pageable = PageRequest.of(page, form.getSize());
		
		String userRole = Objects.requireNonNull(SecurityCtxUtil.userRole());
		String userName = Objects.requireNonNull(SecurityCtxUtil.userName());
		
		if(RoleEnum.CATERER.getValue().equalsIgnoreCase(userRole)) {
			List<String> accountHeadList = accountHeadRepository.getAccountHeadList(userName);
			result = accountHeadRepository.getAccountHeadSummaryForCaterer(accountHeadList, fromDate, toDate, pageable);
		} else {
			result = accountHeadRepository.getAccountHeadSummary(fromDate, toDate, pageable);
		}
		
		return result.map(objects -> {
			return setAccountHeadDtoList(objects);
		});
	}

	private AccountHeadDto setAccountHeadDtoList(Object[] objects) {
	    AccountHeadDto dto = new AccountHeadDto();
	    dto.setAccHead(utility.parseString(objects[0]));
	    Double credit = utility.parseDouble(objects[5]);
	    Double debit = utility.parseDouble(objects[6]);
	    dto.setCreditStr(credit != null ? String.format("%.2f", credit) : null);
	    dto.setDebitStr(debit != null ? String.format("%.2f", debit) : null);
	    dto.setCredit(credit);
	    dto.setDebit(debit);
	    return dto;
	}
}
