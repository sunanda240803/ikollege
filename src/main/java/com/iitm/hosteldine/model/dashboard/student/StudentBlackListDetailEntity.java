package com.iitm.hosteldine.model.dashboard.student;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"STUDENT_BLACK_LIST_DETAILS\"", schema = "schooldev")
public class StudentBlackListDetailEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 32)
    @Column(name = "student_id", length = 32)
    private String studentId;

    @Column(name = "from_date")
    private LocalDate fromDate;

    @Column(name = "to_date")
    private LocalDate toDate;

    @Column(name = "currently_active", length = Integer.MAX_VALUE)
    private String currentlyActive;

    @Column(name = "currently_active_date")
    private LocalDate currentlyActiveDate;

    @Size(max = 1026)
    @Column(name = "remark_description", length = 1026)
    private String remarkDescription;

}