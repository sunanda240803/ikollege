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
    private Long singleVacantRooms;
    private Long singleUtilizedRooms;
    private Long singlePartVacRooms;
    private Long singlePartVacBeds;
    private Long singleOverRooms;
    private Long singleOverSeats;
    
    // Double Occupancy
    private Long doubleRooms;
    private Long doubleCapacity;
    private Long doubleUtilized;
    private Long doubleVacant;
    private Long doubleVacantRooms;
    private Long doubleUtilizedRooms;
    private Long doublePartVacRooms;
    private Long doublePartVacBeds;
    private Long doubleOverRooms;
    private Long doubleOverSeats;
    
    // Triple Occupancy
    private Long tripleRooms;
    private Long tripleCapacity;
    private Long tripleUtilized;
    private Long tripleVacant;
    private Long tripleVacantRooms;
    private Long tripleUtilizedRooms;
    private Long triplePartVacRooms;
    private Long triplePartVacBeds;
    private Long tripleOverRooms;
    private Long tripleOverSeats;
    
    // Quadruple Occupancy
    private Long quadRooms;
    private Long quadCapacity;
    private Long quadUtilized;
    private Long quadVacant;
    private Long quadVacantRooms;
    private Long quadUtilizedRooms;
    private Long quadPartVacRooms;
    private Long quadPartVacBeds;
    private Long quadOverRooms;
    private Long quadOverSeats;
    
    // Dormitory Rooms
    private Long dormRooms;
    private Long dormCapacity;
    private Long dormUtilized;
    private Long dormVacant;
    private Long dormVacantRooms;
    private Long dormUtilizedRooms;
    private Long dormPartVacRooms;
    private Long dormPartVacBeds;
    private Long dormOverRooms;
    private Long dormOverSeats;
    
    // PD (Physically Disabled) Rooms
    private Long pdRooms;
    private Long pdCapacity;
    private Long pdUtilized;
    private Long pdVacant;
    private Long pdVacantRooms;
    private Long pdUtilizedRooms;
    private Long pdPartVacRooms;
    private Long pdPartVacBeds;
    private Long pdOverRooms;
    private Long pdOverSeats;
    
    // Guest Rooms
    private Long guestRooms;
    private Long guestCapacity;
    private Long guestUtilized;
    private Long guestVacant;
    private Long guestVacantRooms;
    private Long guestUtilizedRooms;
    private Long guestPartVacRooms;
    private Long guestPartVacBeds;
    private Long guestOverRooms;
    private Long guestOverSeats;

    // ICSR Rooms
    private Long icsrRooms;
    private Long icsrCapacity;
    private Long icsrUtilized;
    private Long icsrVacant;
    private Long icsrVacantRooms;
    private Long icsrUtilizedRooms;
    private Long icsrPartVacRooms;
    private Long icsrPartVacBeds;
    private Long icsrOverRooms;
    private Long icsrOverSeats;

    // Official Rooms
    private Long officialRooms;
    private Long officialCapacity;
    private Long officialUtilized;
    private Long officialVacant;
    private Long officialVacantRooms;
    private Long officialUtilizedRooms;
    private Long officialPartVacRooms;
    private Long officialPartVacBeds;
    private Long officialOverRooms;
    private Long officialOverSeats;

    // Partial Vacancy / Occupancy Metrics
    private Long partiallyVacantRooms;
    private Long partiallyVacantBeds;
    private Long partiallyUtilizedRooms;

    // Overload Metrics (matching Summary Page)
    private Long overloadedRooms;
    private Long overloadedSeats;
}
