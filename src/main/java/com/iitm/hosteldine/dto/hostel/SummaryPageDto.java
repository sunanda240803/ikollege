package com.iitm.hosteldine.dto.hostel;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SummaryPageDto {
    private String hostelId;
    private String hostelName;
    private int totalRooms;
    private int general;
    private int official;
    private int guest;
    private int icsr;
    private int notFit;
    private int pd;
    private int roomsAllotted;
    private int roomsVacant;
    private int totalNoOfSeats;
    private int allottedSeats;
    private int seatsVacant;
    private String accommodationType;
    private String studentType;
    private int allottedTotal;
    private int checkedIn;
    private int pendingCheckedIn;
    private int tobCheckedOutToday;
    private int checkedOutToday;
    private int pendingCheckedOut;
}
