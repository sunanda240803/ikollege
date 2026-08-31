package com.iitm.hosteldine.model.dashboard.student;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"HOSTEL_VACATING_ALLOWED_STUDENT\"", schema = "schooldev")
public class HostelVacatingAllowedStudentEntity extends CommonEntity {
    @Id
    @Size(max = 32)
    @Column(name = "student_id", nullable = false, length = 32)
    private String studentId;

    @NotNull
    @Column(name = "id", nullable = false)
    private Long id;
}