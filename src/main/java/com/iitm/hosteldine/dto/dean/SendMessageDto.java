package com.iitm.hosteldine.dto.dean;

import lombok.Data;

@Data
public class SendMessageDto {
    private String recipient;
    private String subject;
    private String message;
}
