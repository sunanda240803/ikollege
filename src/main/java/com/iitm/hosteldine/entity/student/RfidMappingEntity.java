package com.iitm.hosteldine.entity.student;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "\"RFID_MAPPINGS\"", schema = ModelConstants.SCHEMA)
public class RfidMappingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rf_id", nullable = false, length = 32)
    private String rfId;

    @Column(name = "student_id", length = 32, unique = true)
    private String studentId;

}
