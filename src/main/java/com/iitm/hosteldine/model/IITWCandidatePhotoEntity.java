package com.iitm.hosteldine.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"IIT_W_CANDIDATE_PHOTOS\"", schema = "schooldev")
public class IITWCandidatePhotoEntity {
    @Id
    @Column(name = "candidate_id", nullable = false)
    private Integer id;

    @Column(name = "image")
    private byte[] image;

    @Column(name = "created_by", length = 30)
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "modified_by", length = 30)
    private String modifiedBy;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "active_flag", nullable = false, length = 1)
    private String activeFlag;

    @ColumnDefault("1")
    @Column(name = "school_id", nullable = false)
    private Integer schoolId;

}