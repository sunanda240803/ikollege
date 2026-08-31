package com.iitm.hosteldine.model.collegeInfo;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@Entity
@Table(name = "\"SCHOOL_GEOGRAPHY_INFO\"", schema = ModelConstants.SCHEMA)
public class SchoolGeographyInfoEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_id", nullable = false)
    private Integer schoolId;

    @Column(name = "school_name", nullable = false, length = 128)
    private String schoolName;

    @Column(name = "date_of_estd")
    @Temporal(TemporalType.DATE)
    private Date dateOfEstd;

    @Column(name = "location", length = 32)
    private String location;

    @Column(name = "address1", length = 256)
    private String address1;

    @Column(name = "district", length = 32)
    private String district;

    @Column(name = "pincode")
    private Integer pincode;

    @Column(name = "landline_contact_no1")
    private Long landlineContactNo1;

    @Column(name = "landline_contact_no1_ext")
    private Integer landlineContactNo1Ext;

    @Column(name = "landline_contact_no2")
    private Long landlineContactNo2;

    @Column(name = "landline_contact_no2_ext")
    private Integer landlineContactNo2Ext;

    @Column(name = "email", length = 64)
    private String email;

    @Column(name = "website", length = 64)
    private String website;
}
