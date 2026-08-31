package com.iitm.hosteldine.model.warden;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"WARDEN_HOSTEL_MAPPING\"", schema = ModelConstants.SCHEMA)
public class WardenHostelMappingEntity extends CommonEntity {

    @EmbeddedId
    private WardenHostelMappingEntityId id;

    @Column(name = "with_effective_date")
    private LocalDate withEffectiveDate;

}