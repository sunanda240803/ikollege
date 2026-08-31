package com.iitm.hosteldine.model.OtherCandidate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_W_CANDIDATE_FILES_INFORMATION\"", schema = "schooldev")
public class CandidateFilesInformationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "candidate_id", nullable = false)
    private Integer candidateId;

    @Column(name = "request_id")
    private Integer requestId;

    @Size(max = 256, message = "File name should not exceed 256 characters.")
    @Column(name = "filename", length = 256)
    private String filename;

    @Size(max = 256, message = "Description should not exceed 256 characters.")
    @Column(name = "description", length = 256)
    private String description;

    @Column(name = "active_flag", nullable = false, length = 1)
    private String activeFlag;

    @ColumnDefault("0")
    @Column(name = "stay_id")
    private Long stayId;

}