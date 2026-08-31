package com.iitm.hosteldine.entity;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"USER_MANAGEMENT\"", schema = ModelConstants.SCHEMA)
public class UserManagementEntity extends CommonEntity {
    @EmbeddedId
    private UserManagementId id;

//    @Column(name = "role_id")
//    private Integer roleId;

    @Column(name = "no_failed_attempts")
    private Integer noFailedAttempts;

    @Column(name = "is_lockable", length = 3)
    private String isLockable;

    @Column(name = "email", length = 60)
    private String email;

    @Column(name = "password", length = 130)
    private String password;

    @Column(name = "password_modified_time")
    private LocalDateTime passwordModifiedTime;

    @Column(name = "wrong_pwd_time")
    private LocalDateTime wrongPwdTime;

    @Column(name = "last_login_time", nullable = false)
    private LocalDateTime lastLoginTime;

    @Column(name = "account_type", length = 30)
    private String accountType;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "ip_address", length = 32)
    private String ipAddress;

    @Column(name = "authentication_server", length = 30)
    private String authenticationServer;

    @Column(name = "card_pin", length = 140)
    private String cardPin;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @ManyToOne
    @JoinColumn(name = "secondary_role_id", nullable = false)
    private RoleEntity roleSecondary;

}