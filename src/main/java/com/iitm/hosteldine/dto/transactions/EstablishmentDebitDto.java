package com.iitm.hosteldine.dto.transactions;

import com.iitm.hosteldine.form.common.TransactionDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EstablishmentDebitDto extends TransactionDto {
    private String hostelName;
    private Long hostelId;
    private String selectedOption;
    private double totalAmount;
    private String roomNo;
    private String floorName;
    private String studentId;
    private String studentName;
}
