package com.iitm.hosteldine.dto.warden;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.iitm.hosteldine.model.warden.WardenHostelMappingEntity}
 */
@Data
public class WardenHostelMappingDto implements Serializable {
    Integer HostelId;
    Integer WardenId;
    LocalDate withEffectiveDate;
}