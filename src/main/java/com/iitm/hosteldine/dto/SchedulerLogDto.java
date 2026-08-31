package com.iitm.hosteldine.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SchedulerLogDto {
    private Long id;
    private LocalDateTime createdAt;
    private String scheduler;
    private String log;
}
