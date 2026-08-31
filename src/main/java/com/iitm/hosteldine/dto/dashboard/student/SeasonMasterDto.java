package com.iitm.hosteldine.dto.dashboard.student;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.model.dashboard.student.SeasonMasterEntity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link SeasonMasterEntity}
 */
@Data
public class SeasonMasterDto implements Serializable {
    private String idAcademicYear;
    private String idSeason;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate startDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate endDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate applicationOpeningDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate nextApplicationOpeningDate;
    private Boolean isCurrent;
    private Integer scStClaimRate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate messStartDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate messEndDate;
}