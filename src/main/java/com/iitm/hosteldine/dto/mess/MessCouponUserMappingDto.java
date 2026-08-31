package com.iitm.hosteldine.dto.mess;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link com.iitm.hosteldine.model.mess.MessCouponUserMappingEntity}
 */
@Data
public class MessCouponUserMappingDto implements Serializable {
    private Long id;
    private MessMasterDto messMaster;
    private String userName;
    private List<Long> messId;
}