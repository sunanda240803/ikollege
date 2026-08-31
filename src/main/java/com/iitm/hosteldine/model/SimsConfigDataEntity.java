package com.iitm.hosteldine.model;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"SIMS_CONFIG_DATA\"", schema = ModelConstants.SCHEMA)
public class SimsConfigDataEntity extends CommonEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id", nullable = false)
    private Long id;

    @Column(name = "config_key", nullable = false, length = Integer.MAX_VALUE)
    private String configKey;

    @Column(name = "config_value", nullable = false, length = Integer.MAX_VALUE)
    private String configValue;

    @Column(name = "description", length = 600)
    private String description;

}