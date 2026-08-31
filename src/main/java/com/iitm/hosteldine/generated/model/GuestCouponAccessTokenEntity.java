package com.iitm.hosteldine.generated.model;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "\"IITM_GUEST_COUPON_ACCESS_TOKEN\"", schema = ModelConstants.SCHEMA)
public class GuestCouponAccessTokenEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "user_name")
    private String username;

    @Column(name = "token", length = 32)
    private String token;

    @Column(name = "mac_id", length = 64)
    private String macId;

    @Column(name = "active_flag")
    private Boolean activeFlag;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "time_limit")
    private Long timeLimit;

    @Column(name = "source", length = 32)
    private String source;

    @Column(name = "last_used")
    private LocalDateTime lastUsed;

    @Column(name = "user_type", length = 32)
    private String userType;

}
