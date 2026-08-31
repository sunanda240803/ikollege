package com.iitm.hosteldine.model.dean;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_W_APPOINTMENT_REQUEST_HISTORY\"", schema = "schooldev")
public class StudentAppointmentRequestHistory extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 64)
    @Column(name = "request_type", length = 64)
    private String requestType;

    @Column(name = "candidate_id")
    private Long candidateId;

    @Size(max = 16)
    @Column(name = "student_id", length = 16)
    private String studentId;

    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "original_stay_from")
    private LocalDate originalStayFrom;

    @Column(name = "original_stay_to")
    private LocalDate originalStayTo;

    @Column(name = "modified_stay_from")
    private LocalDate modifiedStayFrom;

    @Column(name = "modified_stay_to")
    private LocalDate modifiedStayTo;

    @Size(max = 256)
    @Column(name = "original_category", length = 256)
    private String originalCategory;

    @Size(max = 256)
    @Column(name = "modified_category", length = 256)
    private String modifiedCategory;

}