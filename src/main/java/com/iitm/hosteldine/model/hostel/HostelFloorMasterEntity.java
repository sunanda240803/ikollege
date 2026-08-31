package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"HOSTEL_FLOOR_MASTER\"", schema = ModelConstants.SCHEMA)
public class HostelFloorMasterEntity extends CommonEntity{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "floor_id", nullable = false, length = 25)
    private Long id;

    @Column(name = "floor_name", length = 32)
    private String floorName;

    @Column(name = "floor_desc", length = 128)
    private String floorDesc;

    @Column(name = "floor_size")
    private Double floorSize;

    @Column(name = "hostel_or_college", length = 1)
    private String hostelOrCollege;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hostel_id", nullable = false)
    private HostelMasterEntity hostel;
    
}