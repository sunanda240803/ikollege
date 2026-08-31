package com.iitm.hosteldine.dto.staff;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Long;
import java.time.LocalDateTime;
import java.lang.Integer;

@Data
public class StaffDesignationMasterDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("designationName")
    private String designationName;
   
}