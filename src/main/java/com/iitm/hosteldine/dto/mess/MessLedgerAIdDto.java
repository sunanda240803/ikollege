package com.iitm.hosteldine.dto.mess;

import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.iitm.hosteldine.entity.mess.MessLedgerAEntityId}
 */
@Data
public class MessLedgerAIdDto implements Serializable {
    String voucherNo;
    String bookType;
    String finYear;
}