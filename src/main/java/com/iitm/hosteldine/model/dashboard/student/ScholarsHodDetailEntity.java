package com.iitm.hosteldine.model.dashboard.student;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_W_SCHOLARS_HOD_DETAILS\"", schema = "schooldev")
public class ScholarsHodDetailEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 1024)
    @Column(name = "department_code", length = 1024)
    private String departmentCode;

    @Size(max = 1024)
    @Column(name = "department_name", length = 1024)
    private String departmentName;

    @Size(max = 128)
    @Column(name = "hod_name", length = 128)
    private String hodName;

    @Size(max = 128)
    @Column(name = "hod_email", length = 128)
    private String hodEmail;

}