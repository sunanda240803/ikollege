package com.iitm.hosteldine.service.transactions;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.MessLedgerADto;
import com.iitm.hosteldine.dto.transactions.ReceiptEntryDto;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntity;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.mess.MessLedgerAMapper;
import com.iitm.hosteldine.repository.financialYear.FinancialYearRepository;
import com.iitm.hosteldine.repository.mess.MessLedgerARepository;
import com.iitm.hosteldine.repository.mess.MessLedgerBRepository;
import com.iitm.hosteldine.service.AuditTrailService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReceiptUpdateService {

    private final MessLedgerARepository messLedgerARepository;
    private final MessLedgerBRepository messLedgerBRepository;
    private final FinancialYearRepository financialYearRepository;
    private final AuditTrailService auditTrailService;
    private final CommonResponseUtil commonResponseUtil;

    public Page<MessLedgerADto> getReceiptDetails(PaginationForm form) {
        var pageRequest = PageRequest.of(form.getPage() - 1, form.getSize(), Sort.by("voucherDate").descending());
        return Optional.ofNullable(form.getSearch())
                .filter(search -> !search.isEmpty())
                .map(s -> messLedgerARepository
                        .findAllByAccheadAndActiveFlagOrderByVoucherDateDesc(s, ModelConstants.STATUS_INACTIVE,pageRequest)
                        .map(MessLedgerAMapper.INSTANCE::toDto))
                .orElse(Page.empty());
    }

    @Transactional(rollbackFor = Exception.class)
    public String updateReceiptDetails(ReceiptEntryDto receiptEntryDto) throws Exception {
        var list = Optional.ofNullable(receiptEntryDto)
                .map(ReceiptEntryDto::getReceiptEntryDtoList)
                .stream()
                .flatMap(Collection::stream)
                .filter(dto -> Objects.nonNull(dto.getIsCredited()) && Objects.nonNull(dto.getCreditedDate()))
                .toList();

        if (list.isEmpty()) return null;

        var finYear = financialYearRepository
                .getFinYearDetails(Constants.CREDIT, ModelConstants.STATUS_ACTIVE)
                .getFinYear();

        for (var dto : list) {
            updateMessLedgerA(dto, finYear);
        }

        return Constants.SAVED;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateMessLedgerA(ReceiptEntryDto receiptEntryDto, String finYear) throws Exception {
        MessLedgerAEntity messLedgerAEntity = messLedgerARepository.findByIdVoucherNoAndIdFinYearAndActiveFlag(receiptEntryDto.getVoucherNo(),
               finYear, ModelConstants.STATUS_INACTIVE).orElse(null);
        if(Objects.nonNull(messLedgerAEntity)){
            messLedgerAEntity.setActiveFlag(ModelConstants.STATUS_ACTIVE);
            messLedgerAEntity.setVoucherDate(receiptEntryDto.getCreditedDate());
            messLedgerAEntity.setRecon(ModelConstants.STATUS_INACTIVE);
            String description = "Credited Date: "+receiptEntryDto.getCreditedDate()+"."+messLedgerAEntity.getDescription()+
                    "Credited Date: "+receiptEntryDto.getCreditedDate();
            messLedgerAEntity.setDescription(description);
            messLedgerARepository.save(messLedgerAEntity);
            boolean b = updateMessLedgerB(receiptEntryDto, finYear);
            if(b){
                auditTrailService.saveAuditTrail("Receipt Update Form",commonResponseUtil.getMessage("url.receipt.update"),
                        this.getClass().getName() + Constants.HYPHEN + "updateMessLedgerA()");
            }
        }
        else{
            throw new RuntimeException("MessLedgerA not found with id: "+receiptEntryDto.getVoucherNo());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateMessLedgerB(ReceiptEntryDto receiptEntryDto,String finYear) throws RecordNotExistsException {
        MessLedgerBEntity messLedgerBEntity = messLedgerBRepository.findByIdVoucherNoAndIdFinYearAndActiveFlag(receiptEntryDto.getVoucherNo(), finYear, ModelConstants.STATUS_INACTIVE)
                .orElse(null);
        if(Objects.nonNull(messLedgerBEntity)){
            messLedgerBEntity.setActiveFlag(ModelConstants.STATUS_ACTIVE);
            messLedgerBEntity.setVoucherDate(receiptEntryDto.getCreditedDate());
            messLedgerBEntity.setRecon(ModelConstants.STATUS_INACTIVE);
            String description = "Credited Date: "+receiptEntryDto.getCreditedDate()+"."+messLedgerBEntity.getDescription()+
                    "Credited Date: "+receiptEntryDto.getCreditedDate();
            messLedgerBEntity.setDescription(description);
            messLedgerBRepository.save(messLedgerBEntity);
            return true;
        }
        else{
            throw new RecordNotExistsException("MessLedgerB not found with voucherNo: "+receiptEntryDto.getVoucherNo());
        }
    }

    public boolean deleteReceipt(String voucherNo) throws RecordNotExistsException {
        Boolean b = messLedgerARepository.findByIdVoucherNoAndActiveFlag(voucherNo, ModelConstants.STATUS_INACTIVE)
                .map(this::deleteMessLedgerA)
                .orElse(false);

        Boolean c = messLedgerBRepository.findByIdVoucherNoAndActiveFlag(voucherNo, ModelConstants.STATUS_INACTIVE)
                .map(this::deleteMessLedgerB)
                .orElse(false);

        if(b & c){
            return true;
        }
        else{
            throw new RecordNotExistsException("Voucher No: "+voucherNo+" does not exist");
        }
    }

    public  boolean deleteMessLedgerA(MessLedgerAEntity entity){
        entity.setActiveFlag("C");
        entity.onUpdate();
        messLedgerARepository.save(entity);
        return true;
    }

    public  boolean deleteMessLedgerB(MessLedgerBEntity entity){
        entity.setActiveFlag("C");
        entity.onUpdate();
        messLedgerBRepository.save(entity);
        return true;
    }
}