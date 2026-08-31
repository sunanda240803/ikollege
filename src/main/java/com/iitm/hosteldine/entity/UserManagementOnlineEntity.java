package com.iitm.hosteldine.entity;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"USER_MANAGEMENT_ONLINE\"", schema = ModelConstants.SCHEMA)
public class UserManagementOnlineEntity
        extends CommonEntity
         {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id_pk")
    private long userId;

    @Column(name = "first_name", length = 60)
    private String firstName;

    @Column(name = "last_name", length = 60)
    private String lastName;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "email", length = 60)
    private String email;

    @Column(name = "application_id", length = 30)
    private String applicationId;

    @Column(name = "user_id", length = 128)
    private String userEmailId;

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

    @Column(name = "active_status")
    private String activeStatus;

    @Column(name = "no_failed_attempts")
    private Long noOfFailedAttempts;
}