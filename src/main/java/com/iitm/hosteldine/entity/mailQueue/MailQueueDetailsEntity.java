package com.iitm.hosteldine.entity.mailQueue;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "dost_mail_queue_details", schema = ModelConstants.SCHEMA)
public class MailQueueDetailsEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submitted_module", length = 128)
    private String submittedModule;

    @Column(name = "mail_from", nullable = false, length = 255)
    private String mailFrom;

    @Column(name = "mail_to", nullable = false, length = 255)
    private String mailTo;

    @Column(name = "mail_subject")
    private String mailSubject;

    @Column(name = "mail_content")
    private String mailContent;

    @Column(name = "mail_type", length = 32)
    private String mailType;

    @Column(name = "mail_priority")
    private Integer mailPriority;

    @Column(name = "mail_status")
    private Integer mailStatus;

    @Column(name = "mail_cc", length = 255)
    private String mailCc;

    @Column(name = "mail_bcc", length = 255)
    private String mailBcc;

    @Column(name = "attach_file")
    private String attachFile;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "error_def")
    private String errorDef;
}
