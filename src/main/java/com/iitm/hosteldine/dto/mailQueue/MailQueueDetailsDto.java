package com.iitm.hosteldine.dto.mailQueue;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class MailQueueDetailsDto {
    private Long id;
    private String submittedModule;
    private String mailFrom;
    private String mailTo;
    private String mailSubject;
    private String mailContent;
    private String mailType;
    private Integer mailPriority;
    private Integer mailStatus;
    private String mailCc;
    private String mailBcc;
    private String attachFile;
    private String retryCount;
    private String errorDef;
    private LocalDateTime createdAt;
}
