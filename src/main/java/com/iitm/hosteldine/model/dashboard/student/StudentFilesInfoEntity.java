package com.iitm.hosteldine.model.dashboard.student;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_W_STUDENT_FILES_INFORMATION\"", schema = "schooldev")
public class StudentFilesInfoEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id", nullable = false)
    private Long fileId;

    @Size(max = 32)
    @NotNull
    @Column(name = "student_id", nullable = false, length = 32)
    private String studentId;

    @NotNull
    @Column(name = "request_id", nullable = false)
    private Integer requestId;

    @Size(max = 256)
    @Column(name = "filename", length = 256)
    private String filename;

    @Size(max = 256)
    @Column(name = "description", length = 256)
    private String description;

    @Column(name = "active_flag", length = 1)
    private String activeFlag;

}