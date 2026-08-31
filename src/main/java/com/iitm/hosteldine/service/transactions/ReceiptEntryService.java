package com.iitm.hosteldine.service.transactions;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.controller.TransferAmountUtils;
import com.iitm.hosteldine.dto.student.StudentDetailsInfoDto;
import com.iitm.hosteldine.dto.transactions.ReceiptEntryDto;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntity;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.model.financialYear.FinancialYearEntity;
import com.iitm.hosteldine.repository.financialYear.FinancialYearRepository;
import com.iitm.hosteldine.repository.mess.MessLedgerARepository;
import com.iitm.hosteldine.repository.mess.MessLedgerBRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.service.AuditTrailService;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.CustomValidators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ReceiptEntryService {

    private final CustomValidators customValidators;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final StudentDetailsInfoRepository studentDetailsInfoRepository;
    private final FinancialYearRepository financialYearRepository;
    private final MessLedgerARepository messLedgerARepository;
    private final CommonResponseUtil commonResponseUtil;
    private final MessLedgerBRepository messLedgerBRepository;
    private final AuditTrailService auditTrailService;

    public String validateStudentId(String studentId) {
        StudentDetailsInfoDto studentInfoDetails = studentDetailsInfoService.getStudentInfoDetails(studentId.toUpperCase());
        if (studentInfoDetails.getStudentId()!=null) {
            if (Objects.nonNull(studentInfoDetails.getSettlementFlag()) && (studentInfoDetails.getSettlementFlag()
                    .equalsIgnoreCase(ModelConstants.STATUS_ACTIVE) ||
                    studentInfoDetails.getSettlementFlag().startsWith(ModelConstants.STATUS_ACTIVE))) {
                return "message.student.error.student.settlement.completed";
            } else {
                return null;
            }
        } else {
            List<StudentDetailsInfoEntity> previousIds = studentDetailsInfoRepository
                    .checkStudentIdExistInPreviousId(studentId.toUpperCase());
            if (Objects.nonNull(previousIds) && !previousIds.isEmpty()) {
                return "message.roll.no.changed";
            } else {
                return "message.error.student.not.exists";
            }
        }
    }

    public void validate(ReceiptEntryDto receiptEntryDto, BindingResult bindingResult) {
        if (customValidators.isNullOrEmpty(receiptEntryDto.getBookType())) {
            customValidators.rejectField(bindingResult, "bookType", "message.validation.account.type.required");
        }

        if (customValidators.isNullOrEmpty(receiptEntryDto.getAccHead())) {
            customValidators.rejectField(bindingResult, "accHead", "message.validation.select.bank");
        }

        if (customValidators.isNullOrEmpty(receiptEntryDto.getStudentId())) {
            customValidators.rejectField(bindingResult, "studentId", "message.validation.iitm.student.id.required");
        } else {
            String status = validateStudentId(receiptEntryDto.getStudentId());
            if (Objects.nonNull(status)) {
                customValidators.rejectField(bindingResult, "studentId", status);
            }
        }

        if (customValidators.isNullOrEmpty(receiptEntryDto.getAmount()) || receiptEntryDto.getAmount() <= 0) {
            customValidators.rejectField(bindingResult, "amount", "message.validation.amount.required");
        }

        if (!"CASH".equalsIgnoreCase(receiptEntryDto.getAccHead())) {
            if (customValidators.isNullOrEmpty(receiptEntryDto.getChequeNo())) {
                customValidators.rejectField(bindingResult, "chequeNo", "message.validation.cheque.no.required");
            }

            if (customValidators.isNullOrEmpty(receiptEntryDto.getChequeDate())) {
                customValidators.rejectField(bindingResult, "chequeDate", "message.validation.cheque.date.required");
            }

            if (customValidators.isNullOrEmpty(receiptEntryDto.getBankName())) {
                customValidators.rejectField(bindingResult, "bankName", "message.validation.bank.name.required");
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public String saveReceiptEntry(ReceiptEntryDto receiptEntryDto) throws Exception{
        receiptEntryDto.setDate(LocalDate.now());
        FinancialYearEntity finYearDetails = financialYearRepository.getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE);
        Integer nextValMessLedger = messLedgerARepository.getNextValMessLedger();

        receiptEntryDto.setVoucherNo(String.valueOf(nextValMessLedger));
        receiptEntryDto.setDescription(generateDescription(receiptEntryDto));
        receiptEntryDto.setDocRefNo(receiptEntryDto.getStudentId().toUpperCase() + Constants.HYPHEN + receiptEntryDto.getAmount());
        receiptEntryDto.setCancelStatus(ModelConstants.STATUS_INACTIVE);
        receiptEntryDto.setRecon(ModelConstants.STATUS_ACTIVE);
        receiptEntryDto.setScreenType(receiptEntryDto.getAccHead().equalsIgnoreCase("CASH") ? "cash_receipt" : "cheque_receipt");
        receiptEntryDto.setStudentCount(1);

        MessLedgerAEntity messLedgerAEntity = saveMessLedgerAEntity(receiptEntryDto, finYearDetails.getFinYear());
        MessLedgerBEntity messLedgerBEntity = saveMessLedgerBEntity(receiptEntryDto, finYearDetails.getFinYear());

        if(messLedgerAEntity!=null && messLedgerBEntity!=null) {
            //update the active flag in ledger A
            messLedgerAEntity.setActiveFlag(receiptEntryDto.getAccHead().equalsIgnoreCase("CASH") ? ModelConstants.STATUS_ACTIVE :
                    ModelConstants.STATUS_INACTIVE);
            messLedgerARepository.save(messLedgerAEntity);

            //update the active flag in ledger B
            messLedgerBEntity.setActiveFlag(receiptEntryDto.getAccHead().equalsIgnoreCase("CASH") ? ModelConstants.STATUS_ACTIVE :
                    ModelConstants.STATUS_INACTIVE);
            messLedgerBRepository.save(messLedgerBEntity);

            boolean auditTrail = auditTrailService.saveAuditTrail("ReceiptEntryForm",commonResponseUtil.getMessage("url.receipt.entry"),
                    this.getClass().getName() + Constants.HYPHEN + "saveReceiptEntry()");
            if(auditTrail) {
                return Constants.SAVED;
            }
            else{
                return null;
            }
        }
        else{
            return null;
        }
    }

    public MessLedgerAEntity saveMessLedgerAEntity(ReceiptEntryDto receiptEntryDto, String finYear) {
        receiptEntryDto.setDebitOrCredit(Constants.DEBIT);
        MessLedgerAEntity messLedgerAEntity = TransferAmountUtils.createMessLedgerAEntity(receiptEntryDto, finYear);
        messLedgerAEntity.setActiveFlag(receiptEntryDto.getAccHead().equalsIgnoreCase("CASH") ? ModelConstants.STATUS_ACTIVE :
                ModelConstants.STATUS_INACTIVE);
        return messLedgerARepository.save(messLedgerAEntity);
    }

    public MessLedgerBEntity saveMessLedgerBEntity(ReceiptEntryDto receiptEntryDto, String finYear) {
        receiptEntryDto.setDebitOrCredit(Constants.CREDIT);
        MessLedgerBEntity messLedgerBEntity = TransferAmountUtils.createMessLedgerBEntity(receiptEntryDto, finYear, 0, 1);
        messLedgerBEntity.setAcchead(receiptEntryDto.getStudentId());
        messLedgerBEntity.setActiveFlag(receiptEntryDto.getAccHead().equalsIgnoreCase("CASH") ? ModelConstants.STATUS_ACTIVE :
                ModelConstants.STATUS_INACTIVE);
        return messLedgerBRepository.save(messLedgerBEntity);
    }

    public String generateDescription(ReceiptEntryDto receiptEntryDto) {
        if (receiptEntryDto.getAccHead().equalsIgnoreCase("CASH")) {
            return commonResponseUtil.getMessage("message.cash.entry.description");
        } else {
            return commonResponseUtil.getMessage("message.other.entry.description")
                    .replace("<DD/Cheque No>", receiptEntryDto.getChequeNo())
                    .replace("<DD/Cheque Date>", receiptEntryDto.getChequeDate().toString());
        }
    }
}