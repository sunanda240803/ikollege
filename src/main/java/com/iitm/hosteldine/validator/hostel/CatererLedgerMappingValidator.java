package com.iitm.hosteldine.validator.hostel;

import java.util.Locale;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.iitm.hosteldine.FinancialYearMapper;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.financialYear.FinancialYearDto;
import com.iitm.hosteldine.dto.hostel.AccountHeadDto;
import com.iitm.hosteldine.dto.hostel.CatererLedgerMappingDto;
import com.iitm.hosteldine.model.hostel.AccountHeadEntity;
import com.iitm.hosteldine.repository.UserManagementRepository;
import com.iitm.hosteldine.repository.financialYear.FinancialYearRepository;
import com.iitm.hosteldine.repository.hostel.AccountHeadRepository;
import com.iitm.hosteldine.repository.hostel.CatererLedgerMappingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CatererLedgerMappingValidator {

	private final MessageSource messageSource;
	private final UserManagementRepository userManagementRepository;
	private final CatererLedgerMappingRepository catererLedgerMappingRepository;
	private final AccountHeadRepository accountHeadRepository;
	private final FinancialYearRepository financialYearRepository;

	public void validate(CatererLedgerMappingDto dto, BindingResult result) {
		validateUserName(dto, result);
		validateCatererAccountHeadList(dto, result);
	}

	private void validateUserName(CatererLedgerMappingDto dto, BindingResult result) {
		final String userNameField = "userName";
		final String invalidUserNameCode = "userName.invalid";

		if (dto.getUserName() == null) {
			reject(result, userNameField, invalidUserNameCode, "message.validation.enter.usernme");
			return;
		}

		boolean userExists = userManagementRepository.existsByIdUsernameAndActiveFlag(dto.getUserName().toLowerCase(),
				ModelConstants.STATUS_ACTIVE);

		if (!userExists) {
			reject(result, userNameField, invalidUserNameCode, "message.validation.error.user.name.not.valid");
		}
	}

	private void validateCatererAccountHeadList(CatererLedgerMappingDto dto, BindingResult result) {
		final String accountHeadField = "accHead";
		final String invalidAccountHeadCode = "accHead.invalid";

		if (dto.getCatererAccountHeadList() == null || dto.getCatererAccountHeadList().isEmpty()) {
			reject(result, accountHeadField, invalidAccountHeadCode, "message.validation.caterer.account.head.name");
			return;
		}

		boolean hasActiveMapping = false;
		for (AccountHeadDto accountHeadDto : dto.getCatererAccountHeadList()) {
			if (accountHeadDto.getStatus()) {
				FinancialYearDto finYearDto = Optional.ofNullable(
						financialYearRepository.getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE))
						.map(FinancialYearMapper.INSTANCE::toDto).orElse(null);
				boolean mappingExists = catererLedgerMappingRepository
						.existsByIdAccheadAndIdCatererNameAndFinYearAndActiveFlag(accountHeadDto.getId().getAcchead(),
								dto.getUserName(), finYearDto.getFinYear(), ModelConstants.STATUS_ACTIVE);

				if (mappingExists) {
					AccountHeadEntity accHeadEntity = accountHeadRepository.findByIdAccheadAndIdFinYearAndActiveFlag(
							accountHeadDto.getId().getAcchead(), finYearDto.getFinYear(), ModelConstants.STATUS_ACTIVE);
					String exists = messageSource.getMessage(
							"message.validation.caterer.mapping.already.exist.to.this.user", null, Locale.getDefault());
					String accHead = "'" + accHeadEntity.getAccname() + "' ";
					result.rejectValue(accountHeadField, invalidAccountHeadCode, accHead + exists);
					return;
				}
				hasActiveMapping = true;
			}
		}

		if (!hasActiveMapping) {
			reject(result, accountHeadField, invalidAccountHeadCode, "message.validation.caterer.account.head.name");
		}
	}

	private void reject(BindingResult result, String field, String errorCode, String messageKey) {
		result.rejectValue(field, errorCode, messageSource.getMessage(messageKey, null, Locale.getDefault()));
	}
}
