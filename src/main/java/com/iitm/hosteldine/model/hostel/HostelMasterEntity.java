package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"HOSTEL_MASTER\"", schema = ModelConstants.SCHEMA)
public class HostelMasterEntity extends CommonEntity{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "hostel_id", nullable = false, length = 25)
    private Long id;

    @Column(name = "hostel_name", length = 64)
    private String hostelName;

    @Column(name = "extension_flag", length = 2)
    private String extensionFlag;

    @Column(name = "hostel_gender_type", length = 1)
    private String hostelGenderType;

    @Column(name = "hostel_office_email", length = 128)
    private String hostelOfficeEmail;

    @Column(name = "hostel_short_code", length = 4)
    private String hostelShortCode;

    @Column(name = "hn_veg_amount")
    private Double vegAmount;

    @Column(name = "hn_nonveg_amount")
    private Double nonVegAmount;

}