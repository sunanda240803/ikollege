package com.iitm.hosteldine.model;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"USER_AUDIT_TRAIL\"", schema = ModelConstants.SCHEMA)
public class UserAuditTrailEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id",nullable = false)
  private Long id;

  @Column(name = "date", nullable = false)
  private LocalDateTime date;

  @Column(name = "user_id", length = 32)
  private String userId;

  @Column(name = "user_name", length = 32)
  private String userName;

  @Column(name = "ip_address", length = 64)
  private String ipAddress;

  @Column(name = "form_name", length = 64)
  private String formName;

  @Column(name = "action_commit_type", length = 16)
  private String actionCommitType;

  @Column(name = "action_path", length = 130)
  private String actionPath;

  @Column(name = "class_method_name", length = 130)
  private String classMethodName;
}