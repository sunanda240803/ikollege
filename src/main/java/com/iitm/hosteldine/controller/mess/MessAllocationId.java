package com.iitm.hosteldine.controller.mess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class MessAllocationId implements Serializable {
    @NotNull
    @Column(name = "mess_master_id", nullable = false)
    private Long messId;

    @NotNull
    @Column(name = "vendor_code", nullable = false)
    private String vendorCode;
}
