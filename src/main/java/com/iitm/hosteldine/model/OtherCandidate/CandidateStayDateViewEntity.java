package com.iitm.hosteldine.model.OtherCandidate;

import com.iitm.hosteldine.constant.ModelConstants;
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
@Table(name = "\"CANDIDATE_STAY_DATE_LIST_VIEW\"", schema = ModelConstants.SCHEMA)
public class CandidateStayDateViewEntity {

    @EmbeddedId
    private CandidateStayDateIdEntity id;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "appointment_from")
    private LocalDate appointmentFrom;

    @Column(name = "appointment_to")
    private LocalDate appointmentTo;

    @Column(name = "stay_from")
    private LocalDate stayFrom;

    @Column(name = "stay_to")
    private LocalDate stayTo;

    @Column(name = "app_status")
    private String appStatus;

    @Column(name = "stay_status")
    private String stayStatus;

    @Column(name = "status_notes")
    private String statusNotes;

    @Column(name = "rejection_description")
    private String rejectionDescription;

    @Column(name = "resend_date")
    private LocalDate resendDate;

    @Column(name = "case")
    private Integer caseStatus;
}
