package com.iitm.hosteldine.service.reports;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.controller.TransferAmountUtils;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntity;
import com.iitm.hosteldine.form.common.TransactionDto;
import com.iitm.hosteldine.model.financialYear.FinancialYearEntity;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.repository.financialYear.FinancialYearRepository;
import com.iitm.hosteldine.repository.mess.MessLedgerARepository;
import com.iitm.hosteldine.repository.mess.MessLedgerBRepository;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import com.iitm.hosteldine.service.AuditTrailService;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalVoucherService {

    private final MessLedgerARepository messLedgerARepository;
    private final FinancialYearRepository financialYearRepository;
    private final Utility utility;
    private final AuditTrailService auditTrailService;
    private final CommonResponseUtil commonResponseUtil;
    private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;
    private final MessLedgerBRepository messLedgerBRepository;

    public TransactionDto getByVoucherNo(String voucherNo) {
        return Optional.ofNullable(voucherNo)
                .filter(v -> !v.isEmpty() && !v.equalsIgnoreCase("0"))
                .map(this::getDetailsByVoucherNo)
                .orElseGet(this::newTransaction);
    }

    public TransactionDto newTransaction() {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setTransferAmtList1(List.of(new TransactionDto()));
        return transactionDto;
    }


    public TransactionDto getDetailsByVoucherNo(String voucherNo) {
        String finYear = Optional.ofNullable(financialYearRepository.getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE))
                .map(FinancialYearEntity::getFinYear)
                .orElse(Strings.EMPTY);
        List<TransactionDto> ledgerDetailsByVoucherNo = messLedgerARepository.getLedgerDetailsByVoucherNo(voucherNo, finYear, ModelConstants.STATUS_ACTIVE)
                .stream().map(this::mapToTransactionDto).toList();
        TransactionDto transactionDto = new TransactionDto();
        if(ledgerDetailsByVoucherNo!=null && !ledgerDetailsByVoucherNo.isEmpty()) {
            transactionDto.setTransferAmtList1(ledgerDetailsByVoucherNo);
            transactionDto.setVoucherNo(voucherNo);
            transactionDto.setCancelStatus(transactionDto.getTransferAmtList1().getFirst().getCancelStatus());
            transactionDto.setDate(transactionDto.getTransferAmtList1().getFirst().getDate());

            record DebitCredit(double debit, double credit) {
            }

            DebitCredit totals = ledgerDetailsByVoucherNo.stream()
                    .collect(Collectors.teeing(
                            Collectors.filtering(d -> d.getDebitOrCredit().equals(Constants.DEBIT),
                                    Collectors.summingDouble(TransactionDto::getAmount)),
                            Collectors.filtering(d -> d.getDebitOrCredit().equals(Constants.CREDIT),
                                    Collectors.summingDouble(TransactionDto::getAmount)),
                            DebitCredit::new
                    ));

            transactionDto.setTotalDebit(totals.debit());
            transactionDto.setTotalCredit(totals.credit());

            transactionDto.setTotalDebit(totals.debit);
            transactionDto.setTotalCredit(totals.credit);
        }

        return transactionDto;
    }

    private TransactionDto mapToTransactionDto(Object[] o) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setVoucherNo(Objects.nonNull(o[0]) ? String.valueOf(o[0]) : Strings.EMPTY);
        transactionDto.setDate(Objects.nonNull(o[1]) ? utility.convertToLocalDate(o[1]) : null);
        transactionDto.setAccHead(Objects.nonNull(o[2]) ? String.valueOf(o[2]) : Strings.EMPTY);
        transactionDto.setSubAccHead(Objects.nonNull(o[3]) ? String.valueOf(o[3]) : Strings.EMPTY);
        transactionDto.setDocRefNo(Objects.nonNull(o[4]) ? String.valueOf(o[4]) : Strings.EMPTY);
        transactionDto.setDescription(Objects.nonNull(o[5]) ? String.valueOf(o[5]) : Strings.EMPTY);
        transactionDto.setDebitOrCredit(Objects.nonNull(o[6]) ? String.valueOf(o[6]) : Strings.EMPTY);
        transactionDto.setAmount(Objects.nonNull(o[7]) ? utility.parseDouble(o[7]) : 0.0);
        transactionDto.setSlNo(Objects.nonNull(o[8]) ? utility.parseInt(o[8]) : 0);
        transactionDto.setCancelStatus(Objects.nonNull(o[9]) ? String.valueOf(o[9]) : Strings.EMPTY);
        return transactionDto;
    }

    @Transactional
    public String saveOrUpdateJournalVoucher(TransactionDto transactionDto) {
        if (Objects.nonNull(transactionDto.getTransferAmtList1()) && !transactionDto.getTransferAmtList1().isEmpty()) {
            List<TransactionDto> voucherList = transactionDto.getTransferAmtList1()
                    .stream()
                    .filter(this::filterVoucherList)
                    .toList();

            FinancialYearEntity finYearDetails = financialYearRepository.getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE);
            if (Objects.nonNull(transactionDto.getDate()) && Objects.nonNull(transactionDto.getVoucherNo()) &&
                    !transactionDto.getVoucherNo().isEmpty()) {
                boolean s1 = updateMessLedgerA(voucherList.getFirst(), finYearDetails.getFinYear());
                for (int i = 1; i < voucherList.size(); i++) {
                    updateMessLedgerB(voucherList.get(i), finYearDetails.getFinYear());
                }
                return Constants.UPDATED;
            }

            Integer nextValMessLedger = messLedgerARepository.getNextValMessLedger();
            boolean messLedgerAStatus = saveMessLedgerA(voucherList.getFirst(), nextValMessLedger, finYearDetails.getFinYear(),
                    transactionDto.getDate());

            for (int i = 1; i < voucherList.size(); i++) {
                saveMessLedgerB(voucherList.get(i), nextValMessLedger, finYearDetails.getFinYear(),
                        transactionDto.getDate(), i);
            }

            if (messLedgerAStatus) {
                boolean auditTrail = auditTrailService.saveAuditTrail("Journal Voucher Form", commonResponseUtil.getMessage("url.journal.voucher"),
                        this.getClass().getName() + Constants.HYPHEN + "saveOrUpdateJournalVoucher()");
                return Constants.SAVED;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private boolean filterVoucherList(TransactionDto transactionDto) {
        return Objects.nonNull(transactionDto.getAccHead()) && Objects.nonNull(transactionDto.getDebitOrCredit());
    }

    public boolean saveMessLedgerA(TransactionDto transactionDto, Integer voucherNo, String finYear, LocalDate voucherDate) {
        transactionDto.setFinYear(finYear);
        transactionDto.setDate(voucherDate);
        transactionDto.setBookType(Constants.MESS_MS);
        transactionDto.setVoucherNo(Constants.JV + String.format("%08d", voucherNo));
        transactionDto.setCancelStatus(ModelConstants.STATUS_INACTIVE);
        transactionDto.setRecon(ModelConstants.STATUS_ACTIVE);
        transactionDto.setScreenType("journal_voucher");
        transactionDto.setStudentCount(0);

        MessLedgerAEntity messLedgerAEntity = TransferAmountUtils.createMessLedgerAEntity(transactionDto, finYear);
        messLedgerAEntity.setActiveFlag(ModelConstants.STATUS_ACTIVE);
        messLedgerARepository.save(messLedgerAEntity);
        return true;
    }

    public boolean saveMessLedgerB(TransactionDto transactionDto, Integer voucherNo, String finYear, LocalDate voucherDate,
                                   int slNo) {
        transactionDto.setFinYear(finYear);
        transactionDto.setSlNo(slNo);
        transactionDto.setDate(voucherDate);
        transactionDto.setBookType(Constants.MESS_MS);
        transactionDto.setVoucherNo(Constants.JV + String.format("%08d", voucherNo));
        transactionDto.setCancelStatus(ModelConstants.STATUS_INACTIVE);
        transactionDto.setRecon(ModelConstants.STATUS_ACTIVE);
        transactionDto.setScreenType("journal_voucher");
        transactionDto.setStudentCount(0);

        String fcNo = null;
        if (Objects.nonNull(transactionDto.getStudentId()) && !transactionDto.getStudentId().isEmpty()) {
            AllStudentsDetailsViewEntity completeStudentDetails = allStudentsDetailsViewRepository.getCompleteStudentDetails(transactionDto.getStudentId());
            fcNo = String.valueOf(completeStudentDetails.getHostelId());
        }
        transactionDto.setFcno(fcNo);
        MessLedgerBEntity messLedgerBEntity = TransferAmountUtils.createMessLedgerBEntity(transactionDto, finYear, 0, slNo);
        messLedgerBEntity.setActiveFlag(ModelConstants.STATUS_ACTIVE);
        messLedgerBRepository.save(messLedgerBEntity);
        return true;
    }

    public boolean updateMessLedgerA(TransactionDto transactionDto, String finYear) {
        MessLedgerAEntity entity = messLedgerARepository.findByIdVoucherNoAndIdFinYearAndActiveFlag(transactionDto.getVoucherNo(), finYear,
                ModelConstants.STATUS_ACTIVE).orElse(null);
        if (Objects.nonNull(entity)) {
            entity.setDescription(transactionDto.getDescription());
            entity.setDocRefNo(transactionDto.getDocRefNo());
            entity.onUpdate();
            messLedgerARepository.save(entity);
            return true;
        }
        return false;
    }

    public boolean updateMessLedgerB(TransactionDto transactionDto, String finYear) {
        MessLedgerBEntity messLedgerBEntity = messLedgerBRepository.findByIdSlnoAndIdVoucherNoAndIdFinYearAndActiveFlag(transactionDto.getSlNo(),
                transactionDto.getVoucherNo(), finYear, ModelConstants.STATUS_ACTIVE).orElse(null);
        if (Objects.nonNull(messLedgerBEntity)) {
            messLedgerBEntity.setDescription(transactionDto.getDescription());
            messLedgerBEntity.setDocRefNo(transactionDto.getDocRefNo());
            messLedgerBEntity.onUpdate();
            messLedgerBRepository.save(messLedgerBEntity);
            return true;
        } else {
            return false;
        }
    }


    public String cancelJournalVoucher(String voucherNo) {
        FinancialYearEntity finYearDetails = financialYearRepository.getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE);
        int i = messLedgerARepository.cancelMessLedgerA(ModelConstants.STATUS_ACTIVE, ModelConstants.STATUS_ACTIVE, finYearDetails.getFinYear(),
                voucherNo, LocalDateTime.now(), SecurityCtxUtil.userId());

        int j = messLedgerBRepository.cancelMessLedgerB(ModelConstants.STATUS_ACTIVE, ModelConstants.STATUS_ACTIVE, finYearDetails.getFinYear(),
                voucherNo, LocalDateTime.now(), SecurityCtxUtil.userId());
        return i > 0 && j > 0 ? Constants.SAVED : null;
    }
}