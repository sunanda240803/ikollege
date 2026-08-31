package com.iitm.hosteldine.model.hostel;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class AccountHeadIdEntity implements Serializable {
    private static final long serialVersionUID = 5159371975112451323L;
    @Column(name = "acchead", nullable = false, length = 32)
    private String acchead;

    @Column(name = "fin_year", nullable = false, length = 16)
    private String finYear;
}