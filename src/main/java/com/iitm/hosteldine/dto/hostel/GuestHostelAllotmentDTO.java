package com.iitm.hosteldine.dto.hostel;

import lombok.Data;

@Data
public class GuestHostelAllotmentDTO {
    private String requestId;
    private String buildingId;
    private String hostelId;
    private String roomId;
    private String fromDate;
    private String toDate;
    private int totalCapacity;
    private int remainingCapacity;
    private String guestId;
}
