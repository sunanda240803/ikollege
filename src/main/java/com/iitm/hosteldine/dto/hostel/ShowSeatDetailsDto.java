package com.iitm.hosteldine.dto.hostel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Long;
import com.iitm.hosteldine.model.hostel.ShowMasterEntity;
import com.iitm.hosteldine.validator.fieldValidators.ValidStringField;

import java.lang.Double;
import java.lang.Boolean;

@Data
public class ShowSeatDetailsDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("show")
    private ShowMasterDto show;
    @ValidStringField(message = "message.validation.seat.name.required",fieldName = "message.label.seat.name",min = 1,max =64)
    private String seatName;
    @JsonProperty("discountedAmount")
    private Double discountedAmount;
    // We are not always use this amount.So amount to be 0
    @JsonProperty("amount")
    private Double amount;
    @JsonProperty("discountStatus")
    private String discountStatus;
   // @ValidStringField(message = "message.validation.currently.active.required",fieldName = "message.label.active")
    @JsonProperty("currentlyActive")
    private String currentlyActive;
    @JsonProperty("isAvailable")
    private Boolean isAvailable;

    private Boolean isSeatBooked;
    private int purchaseCount;
}