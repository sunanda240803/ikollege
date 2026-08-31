package com.iitm.hosteldine.dto.mailQueue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailTemplateDto {

    private String mailType;
    private String mailSubject;
    private String mailTemplate;
    private String description;
    private String category;
    private Long approvalLevel;
    private String authorityType;
    private String activeFlag;
}
