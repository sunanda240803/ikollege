package com.iitm.hosteldine.model.staff;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import java.time.LocalDateTime;
import java.time.LocalDate;

import com.iitm.hosteldine.constant.ModelConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "\"STAFF_DESIGNATION_MASTER\"", schema = ModelConstants.SCHEMA)
public class StaffDesignationMasterEntity extends CommonEntity {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "designation_id", nullable = false)
    private Long id;

    @Column(name = "designation_name", nullable = false, length = 64)
    private String designationName;

	/*
	 * @Column(name = "active_status", nullable = false, length = 1) private String
	 * activeStatus;
	 */

    @Column(name = "school_id")
    private Integer school;
 
}
