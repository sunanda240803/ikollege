package com.iitm.hosteldine.dto.caterer;

import com.iitm.hosteldine.dto.dean.PropertyDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class CatererSickFoodDeliveryDto {
    private LocalDate requestedDate;
    private String studentId;
    private String deliveryAddress;
    private String studentName;
    private String messType;
    private String messSession;
    private String catererStatus;
    private String catererDeliveryStatus;
    private String studentDeliveryStatus;
    private List<PropertyDto> actionList;
}
