package com.iitm.hosteldine.dto.hostel;

import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.iitm.hosteldine.model.hostel.AccountHeadIdEntity}
 */
@Data
public class AccountHeadIdDto implements Serializable {
    private String acchead;
    private String finYear;
}