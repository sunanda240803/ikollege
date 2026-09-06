package com.iitm.hosteldine.dto.hostel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostelGuestTariffDto {
    private Long hostelId;
    private String hostelName;
    private String hostelCode;
    
    private Long totalGuestRooms;
    private Long totalGuestCapacity;
    
    private Long singleGuestRooms;
    private Long singleGuestVacant;
    private Long sharedGuestRooms;
    private Long sharedGuestVacant;
    
    private Long utilizedGuestRoomsToday;
    private Long vacantGuestRoomsToday;
    
    private Long vacantRoomsCost;
    private Integer bookingDurationDays;
    
    private Integer singleRoomTariff;
    private Integer sharedRoomTariff;
    private Integer lodgingBaseCharge;
    private String tariffDescription;

    private java.util.List<GuestRoomDetail> roomDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GuestRoomDetail {
        private String roomNo;
        private String floorName;
        private Integer capacity;
        private String currentStatus; // Occupied or Vacant
        private String occupantType; // Guest / Student / Official
        private Integer tariffPerDay;
    }
}
