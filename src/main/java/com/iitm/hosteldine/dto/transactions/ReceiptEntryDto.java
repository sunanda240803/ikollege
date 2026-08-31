package com.iitm.hosteldine.dto.transactions;

import com.iitm.hosteldine.form.common.TransactionDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class ReceiptEntryDto extends TransactionDto {
    private LocalDate date;
    private String studentId;
    private String bankName;
    private LocalDate creditedDate;
    private Boolean isCredited;
    private List<ReceiptEntryDto> receiptEntryDtoList;
}