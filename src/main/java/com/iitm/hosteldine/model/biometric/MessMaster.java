package com.iitm.hosteldine.model.biometric;

import com.iitm.hosteldine.constant.ModelConstants;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "\"MESS_MASTER\"", schema = ModelConstants.SCHEMA)
public class MessMaster {
    @Id
    @Column(name = "mess_master_id")
    private Long messMasterId;
    @Column(name = "school_id")
    private Integer schoolId;
    @Column(name = "mess_name")
    private String messName;
    @Column(name = "capacity")
    private String capacity;
    @Column(name = "active_flag")
    private String activeStatus;
    @Column(name = "gender_option")
    private String genderOption;
    @Column(name = "mess_head")
    private String messHead;

    @Transient
    private String[] terminalIps;
}
