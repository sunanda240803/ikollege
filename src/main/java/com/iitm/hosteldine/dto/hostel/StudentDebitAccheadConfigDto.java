package com.iitm.hosteldine.dto.hostel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentDebitAccheadConfigDto {
    private Long id;
    private String accheadName;
    private String acchead;
    private String creditOrDebit;
    private Double value;
}