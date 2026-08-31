package com.iitm.hosteldine.dto.student.wellness;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class WellnessUserManagementDto {
    private String userId;
    private String username;
    private String password;
}