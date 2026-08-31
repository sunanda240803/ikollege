package com.iitm.hosteldine.dto.student;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Long;
import java.time.LocalDate;
import java.lang.Double;

@Data
public class MessCardAmountTransferControllerDto {
    @JsonProperty("configId")
    private Long configId;
    @JsonProperty("openingDate")
    private LocalDate openingDate;
    @JsonProperty("closingDate")
    private LocalDate closingDate;
    @JsonProperty("openingTime")
    private String openingTime;
    @JsonProperty("closingTime")
    private String closingTime;
    @JsonProperty("maxAmount")
    private Double maxAmount;
}