package com.iitm.hosteldine.dto.hostel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HostelCapacityDto {
    private Long hostelId;
    private String hostelName;
    private String hostelCode;
    private String hostelGenderType;
    
    // Overall Capacity Totals
    private Long totalCapacity;
    private Long totalUtilized;
    private Long totalVacant;
    
    // Single Occupancy
    private Long singleRooms;
    private Long singleCapacity;
    private Long singleUtilized;
    private Long singleVacant;
    
    // Double Occupancy
    private Long doubleRooms;
    private Long doubleCapacity;
    private Long doubleUtilized;
    private Long doubleVacant;
    
    // Triple Occupancy
    private Long tripleRooms;
    private Long tripleCapacity;
    private Long tripleUtilized;
    private Long tripleVacant;
    
    // Quadruple Occupancy
    private Long quadRooms;
    private Long quadCapacity;
    private Long quadUtilized;
    private Long quadVacant;
    
    // Dormitory Rooms
    private Long dormRooms;
    private Long dormCapacity;
    private Long dormUtilized;
    private Long dormVacant;
    
    // PD (Physically Disabled) Rooms
    private Long pdRooms;
    private Long pdCapacity;
    private Long pdUtilized;
    private Long pdVacant;
    
    // Guest Rooms
    private Long guestRooms;
    private Long guestCapacity;
    private Long guestUtilized;
    private Long guestVacant;
}
