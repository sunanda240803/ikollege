package com.iitm.hosteldine.model.dashboard.student;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_W_SEASON_MASTER\"", schema = "schooldev")
public class SeasonMasterEntity extends CommonEntity {
    @EmbeddedId
    private SeasonMasterEntityId id;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "application_openingdate")
    private LocalDate applicationOpeningDate;

    @Column(name = "next_application_openingdate")
    private LocalDate nextApplicationOpeningDate;

    @Column(name = "is_current")
    private Boolean isCurrent;

    @Column(name = "sc_st_claimrate")
    private Integer scStClaimRate;

    @Column(name = "mess_start_date")
    private LocalDate messStartDate;

    @Column(name = "mess_end_date")
    private LocalDate messEndDate;

}