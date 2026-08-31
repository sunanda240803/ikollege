package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.hostel.CheckerApprovalCreditDebitDto;
import com.iitm.hosteldine.dto.hostel.CheckerApprovalDto;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.financialYear.FinancialYearEntity;
import com.iitm.hosteldine.model.hostel.HostelMasterEntity;
import com.iitm.hosteldine.model.student.SaveTransactionFAEntity;
import com.iitm.hosteldine.repository.financialYear.FinancialYearRepository;
import com.iitm.hosteldine.repository.hostel.HostelMasterRepository;
import com.iitm.hosteldine.repository.mess.MessLedgerARepository;
import com.iitm.hosteldine.repository.mess.MessLedgerBRepository;
import com.iitm.hosteldine.repository.student.SaveTransactionFARepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckerApprovalService {
	private final MessLedgerARepository messLedgerARepository;
	private final MessLedgerBRepository messLedgerBRepository;
	private final FinancialYearRepository financialYearRepository;
	private final SimsConfigDataService smsConfigDataService;
	private final HostelMasterRepository hostelMasterRepository;
	private final SaveTransactionFARepository saveTransactionFARepository;

	private final FaTransactionService faTransactionService;


	public PageImpl<CheckerApprovalCreditDebitDto> getCheckerApprovalList(PaginationForm form) {
		Page<HostelMasterEntity> result = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("voucher_date").ascending());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
		}
		FinancialYearEntity finYearDetails = financialYearRepository.getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE);
		List<Object[]> checkerApprovalList = messLedgerARepository.getAllCheckerApprovalList(finYearDetails.getFinYear());

		List<CheckerApprovalDto> content = checkerApprovalList.stream()
				.map(this::mapToCheckerApprovalDto)
				.collect(Collectors.toList());
		processVouchersSimplest(content);
		return new PageImpl<>(convert(content), pageable, checkerApprovalList.size());
	}

	private CheckerApprovalDto mapToCheckerApprovalDto(Object[] row) {
		return new CheckerApprovalDto(
				(String) row[1],       // voucherNumber
                ((LocalDate)row[2]),         // voucherDate
				(Double) row[3],      // amount
				(String) row[4],       // accountHead
				(String) row[5],       // description
				(String) row[6],    // debitOrCredit
				(String) row[7]   // screenType
				);
	}
	public static void processVouchersSimplest(List<CheckerApprovalDto> dtos) {
		Map<String, Integer> voucherSlNos = new HashMap<>();
		dtos.sort(Comparator
				.comparing(CheckerApprovalDto::getVoucherNumber)
				.thenComparing(dto -> !Constants.DEBIT.equals(dto.getDebitOrCredit())));
		// Assign slNos
		Map<String, Integer> slNos = new HashMap<>();
		int[] counter = {1};

		dtos.forEach(dto -> {
			String voucher = dto.getVoucherNumber();
			dto.setSlNo(Constants.DEBIT.equals(dto.getDebitOrCredit())
					? slNos.computeIfAbsent(voucher, k -> counter[0]++)
					: null);
		});
	}
	public List<CheckerApprovalCreditDebitDto> convert(List<CheckerApprovalDto> dtoList) {
		List<CheckerApprovalCreditDebitDto> result = new ArrayList<>();
		Map<String, List<CheckerApprovalDto>> groupedCheckerApproval = dtoList.stream().collect(Collectors.groupingBy(CheckerApprovalDto::getVoucherNumber));
		int count = 0;
		for (String key : groupedCheckerApproval.keySet()) {
			count+=1;
			CheckerApprovalCreditDebitDto dto  = new CheckerApprovalCreditDebitDto();
			List<CheckerApprovalDto> checkerApprovalDtos = groupedCheckerApproval.get(key);

			CheckerApprovalDto firstDto = checkerApprovalDtos.get(0);
			dto.setVoucherNumber(firstDto.getVoucherNumber());
			dto.setDescription(firstDto.getDescription());
			dto.setVoucherDate(firstDto.getVoucherDate());
			dto.setSlNo(count);

			List<CheckerApprovalDto> debitVouchers = checkerApprovalDtos.stream()
					.filter(d -> d.getDebitOrCredit().equalsIgnoreCase(Constants.DEBIT))
					.collect(Collectors.toList());
			debitVouchers.forEach(d ->  d.setDebitOrCredit(Constants.DEBIT.equalsIgnoreCase(
							d.getDebitOrCredit()) ? Constants.DEBIT_FULL_FORM : Constants.CREDIT_FULL_FORM));


			List<CheckerApprovalDto> creditVouchers = checkerApprovalDtos.stream()
					.filter(d -> d.getDebitOrCredit().equalsIgnoreCase(Constants.CREDIT))
					.map(d -> d).collect(Collectors.toList());
			creditVouchers.forEach(d ->  d.setDebitOrCredit(Constants.DEBIT.equalsIgnoreCase(
					d.getDebitOrCredit()) ? Constants.DEBIT_FULL_FORM : Constants.CREDIT_FULL_FORM));

			dto.setDebitRecord(debitVouchers);
			dto.setCreditRecord(creditVouchers);

			if(debitVouchers.size() >creditVouchers.size()) {
				dto.setDebitRecord(creditVouchers);
				dto.setCreditRecord(debitVouchers);
			}

			result.add(dto);
		}

		return result;
	}

	public Boolean saveCheckerApprovals(List<String> voucherNumbers) {
		String faIntegrationFlag = smsConfigDataService.getSimConfigValue(SimsConfigDataService.IKOLLEGE_FA_INTEGRATION);
		FinancialYearEntity finYearDetails = financialYearRepository.getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE);

        for (String voucherNo: voucherNumbers) {
            List<MessLedgerAEntity> messLedgerAEntities = messLedgerARepository.
					findById_FinYearAndId_VoucherNoAndActiveFlag(finYearDetails.getFinYear(),voucherNo,ModelConstants.STATUS_ACTIVE);
            if(CollectionUtils.isNotEmpty(messLedgerAEntities)){
            	for (MessLedgerAEntity entity : messLedgerAEntities) {
                    entity.setRecon(ModelConstants.STATUS_INACTIVE);
                }
            	messLedgerARepository.saveAll(messLedgerAEntities);
            }

            List<MessLedgerBEntity> messLedgerBEntities = messLedgerBRepository.
					findById_FinYearAndId_VoucherNoAndActiveFlag(finYearDetails.getFinYear(),voucherNo,ModelConstants.STATUS_ACTIVE);
            if(CollectionUtils.isNotEmpty(messLedgerBEntities)){
            	for (MessLedgerBEntity entity : messLedgerBEntities) {
                    entity.setRecon(ModelConstants.STATUS_INACTIVE);
                }
            	messLedgerBRepository.saveAll(messLedgerBEntities);
            }

			String screenType = CollectionUtils.isNotEmpty(messLedgerAEntities) ? messLedgerAEntities.get(0).getScreenType() : null;
			if(StringUtils.equalsIgnoreCase(faIntegrationFlag,"true") && StringUtils.isNotEmpty(screenType)) {
				Double amount = 0.0;
				String description = null;
				int studentCount = 0;
				String hostelName = null;
				if(StringUtils.equalsIgnoreCase(screenType,Constants.SCREEN_TYPE_STUDENT_DEMAND)){
					List<Object[]> ledgerCheckerValue = messLedgerARepository.getLedgerCheckerValue(finYearDetails.getFinYear(), voucherNo);

					if(CollectionUtils.isNotEmpty(ledgerCheckerValue)) {

						for (Object[] object: ledgerCheckerValue) {
							amount+=(Double) object[3];
							description =(String) object[5];
							studentCount =(Integer) object[8];
						}
					}
				} else {
					amount  = CollectionUtils.isNotEmpty(messLedgerAEntities) ? messLedgerAEntities.get(0).getAmount() : 0;
					description = CollectionUtils.isNotEmpty(messLedgerAEntities) ? messLedgerAEntities.get(0).getDescription() : null;
					studentCount =  CollectionUtils.isNotEmpty(messLedgerAEntities) ? messLedgerAEntities.get(0).getStudentCount() : null;
				}


				String iKollegeTransferType = null;
				if(StringUtils.equalsIgnoreCase(screenType,Constants.SCREEN_TYPE_STUDENT_DEBIT)){
					String accHead = CollectionUtils.isNotEmpty(messLedgerAEntities) ? messLedgerAEntities.get(0).getAcchead() : null;
					iKollegeTransferType = accHead;

					if(StringUtils.equalsIgnoreCase(accHead,Constants.ESTABLISHMENT_B)) {
						String hostelId = CollectionUtils.isNotEmpty(messLedgerAEntities) ? messLedgerAEntities.get(0).getFcNo() : null;
						if(StringUtils.isNotEmpty(hostelId)) {
							Optional<HostelMasterEntity> hostelMaster = hostelMasterRepository.findByIdAndActiveFlag(Long.parseLong(hostelId), ModelConstants.STATUS_ACTIVE);
							hostelName = hostelMaster.isPresent() ? hostelMaster.get().getHostelName() : null;
						}
					}
				} else {
					iKollegeTransferType = screenType;
				}

				int jvSeq = messLedgerARepository.getNextValMessLedger();
				String transactionUrl = smsConfigDataService.getSimConfigValue(SimsConfigDataService.FA_TRANSACTION_URL);

				if(!(transactionUrl!=null && !transactionUrl.isEmpty())) {
					SaveTransactionFAEntity transactionFAEntity = new SaveTransactionFAEntity();
					transactionFAEntity.setScreenType(Constants.IKOLLEGE_TRANSACTION);
					transactionFAEntity.setTransferType(iKollegeTransferType);
					transactionFAEntity.setHostelName(hostelName);
					transactionFAEntity.setDescription(description);;
					transactionFAEntity.setTotalAmount(amount);
					transactionFAEntity.setReferenceNumber(Constants.JV+jvSeq);
					transactionFAEntity.setNumOfTransaction(studentCount);
					transactionFAEntity.setTransactionStatus(WorkflowStatus.INITIATED.getStatus());
					transactionFAEntity.setTransferDate(LocalDate.now());
					SaveTransactionFAEntity savedTransactionFAEntity = saveTransactionFARepository.save(transactionFAEntity);

					if(savedTransactionFAEntity != null) {
						faTransactionService.updateFATransaction(savedTransactionFAEntity);
					}
				}
			}
        }



        return true;
    }

	public Boolean deleteCheckerApprovals(String voucherNo) {
		FinancialYearEntity finYearDetails = financialYearRepository.getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE);
		List<MessLedgerAEntity> messLedgerAEntities = messLedgerARepository.
				findById_FinYearAndId_VoucherNoAndActiveFlag(finYearDetails.getFinYear(),voucherNo,ModelConstants.STATUS_ACTIVE);
		if(CollectionUtils.isNotEmpty(messLedgerAEntities)){
			MessLedgerAEntity messLedgerAEntity = messLedgerAEntities.get(0);
			messLedgerAEntity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
			messLedgerARepository.save(messLedgerAEntity);
		}

		List<MessLedgerBEntity> messLedgerBEntities = messLedgerBRepository.
				findById_FinYearAndId_VoucherNoAndActiveFlag(finYearDetails.getFinYear(),voucherNo,ModelConstants.STATUS_ACTIVE);
		if(CollectionUtils.isNotEmpty(messLedgerBEntities)){
			MessLedgerBEntity messLedgerBEntity = messLedgerBEntities.get(0);
			messLedgerBEntity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
			messLedgerBRepository.save(messLedgerBEntity);
		}
		return true;
	}
}
