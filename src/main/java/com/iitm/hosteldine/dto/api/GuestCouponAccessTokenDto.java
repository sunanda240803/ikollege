package com.iitm.hosteldine.dto.api;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GuestCouponAccessTokenDto {
    private Long tokenId;
    private String username;
    private String token;
    private String macId;
    private LocalDateTime generatedAt;
    private Boolean activeFlag;
    private Long timeLimit;
    private String source;
    private LocalDateTime lastUsed;
    private String userType;
}