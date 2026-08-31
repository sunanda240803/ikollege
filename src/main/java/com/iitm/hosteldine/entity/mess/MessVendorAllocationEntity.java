package com.iitm.hosteldine.entity.mess;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.controller.mess.MessAllocationId;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "\"MESS_ALLOCATION\"", schema = ModelConstants.SCHEMA)
public class MessVendorAllocationEntity extends CommonEntity {
    @EmbeddedId
    private MessAllocationId id;

    @Column(name = "rate")
    private Double rate;

    @Column(name = "mess_from_date")
    private LocalDate fromDate;

    @Column(name = "mess_to_date")
    private LocalDate toDate;

    @Column(name = "mess_effective_date")
    private LocalDate effectiveDate;

    @Column(name = "gst_percentage")
    private Integer gst;
}
