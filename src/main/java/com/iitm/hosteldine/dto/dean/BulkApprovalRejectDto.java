package com.iitm.hosteldine.dto.dean;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class BulkApprovalRejectDto {
    private List<String> encryptedIds;
    private LocalDate stayFromDate;
    private LocalDate stayToDate;
    private String approvalNote;
    private String rejectionReason;
    private String approvalStatus;
}
