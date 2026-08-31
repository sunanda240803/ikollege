package com.iitm.hosteldine.dto.hostel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimDTO {
    private Long claimAmount;
    private String studentId;
    private Long eventId;
}
