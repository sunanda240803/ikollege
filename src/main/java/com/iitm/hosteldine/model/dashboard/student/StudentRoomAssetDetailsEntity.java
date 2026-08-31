package com.iitm.hosteldine.model.dashboard.student;

import java.time.LocalDate;

import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"IITM_STUDENT_ROOM_ASSET_DETAILS\"", schema = "schooldev")
public class StudentRoomAssetDetailsEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Size(max = 32)
    @Column(name = "student_id", length = 32)
    private String studentId;
    
    @Column(name = "vacating_request_id")
    private Long vacatingRequestId;
    
    @Column(name = "asset_id")
	private Long assetId;
    
    @Column(name = "asset_category", length = 64)
	private String assetCategory;
    
    @Column(name = "aseet_name", length = 64)
	private String assetName;
    
    @Column(name = "asset_code", length = 32)
	private String assetCode;
    
    @Column(name = "asset_condition", length = 64)
	private String assetCondition;
    
    @Column(name = "penalty_amount")
    private Long penaltyAmount;
    
    @Column(name = "penalty_reason", length = 64)
    private String penaltyReason;
    
    @Size(max = 128)
    @Column(name = "verified_by", length = 128)
    private String verifiedBy;
    
    @Column(name = "verified_date")
    private LocalDate verifiedDate;
    
}
