package com.iitm.hosteldine.dto.api;

import lombok.Data;
import lombok.Builder;
import java.lang.Long;
import com.iitm.hosteldine.model.mess.MessMasterEntity;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class MessQrApplicationDto {
    private Long qrId;
    private String studentId;
    private String qrNumber;
    private String messSession;
    private MessMasterEntity messMaster;
    private String qrUsageStatus;
    private LocalDate qrUsageDate;
    private LocalDateTime qrUsageTime;
}