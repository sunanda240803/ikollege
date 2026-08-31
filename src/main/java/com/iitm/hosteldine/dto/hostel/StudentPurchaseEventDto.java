package com.iitm.hosteldine.dto.hostel;

import lombok.Data;

@Data
public class StudentPurchaseEventDto {
    private Long eventId;
    private String eventName;
    private Long PurchaseAmount;
}
