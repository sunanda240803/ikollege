package com.iitm.hosteldine.model;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "\"STUDENT_BIO_DATA_FAMILY_INFO\"", schema = ModelConstants.SCHEMA)
public class StudentBioDataFamilyInfoEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bio_family_id", nullable = false)
    private Long id;

    @Column(name = "bio_data_id")
    private Long bioDataId;

    @Column(name = "application_number", length = 30)
    private String applicationNumber;

    @Column(name = "relation_name", length = 120)
    private String relationName;

    @Column(name = "relation_type", length = 120)
    private String relationType;

    @Column(name = "email", nullable = false, length = 256)
    private String email;

    @Column(name = "occupation", length = 64)
    private String occupation;

    @Column(name = "mobile_no")
    private Long mobileNo;

    @ColumnDefault("0")
    @Column(name = "income")
    private Double income;

    @Column(name = "address", length = 300)
    private String address;

    @Column(name = "age")
    private Integer age;
    
    @Column(name = "proof_file_name")
    private String proofFileName;
    
    @Column(name = "proof_type")
    private String proofType;

}