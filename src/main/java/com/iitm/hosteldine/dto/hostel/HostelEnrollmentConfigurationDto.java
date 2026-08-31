package com.iitm.hosteldine.dto.hostel;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.iitm.hosteldine.constant.Constants;

import lombok.Data;

@Data
public class HostelEnrollmentConfigurationDto {

    private Long id;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate fromDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate toDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate startDate;
    
}