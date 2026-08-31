package com.iitm.hosteldine.model.mess;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import java.sql.Timestamp;

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
@Table(name = "\"MESS_REGISTRATION_MAPPING\"", schema = ModelConstants.SCHEMA) 
public class MessRegistrationMappingEntity extends CommonEntity{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "girls_option_one", length = 64)
    private String girlsOptionOne;

    @Column(name = "girls_option_two", length = 64)
    private String girlsOptionTwo;

    @Column(name = "boys_option", length = 64)
    private String boysOption;

    @Column(name = "girls_option_one_min_count", nullable = false, length = 5)
    private String girlsOptionOneMinCount;

    @Column(name = "girls_option_two_min_count", nullable = false, length = 5)
    private String girlsOptionTwoMinCount;

    @Column(name = "boys_option_min_count", nullable = false, length = 5)
    private String boysOptionMinCount;

    @Column(name = "girls_option_two_enable")
    private Boolean girlsOptionTwoEnable;

}
