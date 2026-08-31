package com.iitm.hosteldine.dto.hostel;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatererLedgerMappingDto {
    private String accHead;
    private String catererName;
    private String finYear;
    private String accname;
    private String userName;
    private List<AccountHeadDto> catererAccountHeadList;
}