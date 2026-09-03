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
    private String academicYear;
    private String fromDate;
    private String toDate;
    private String courseCode;
    private String courseName;
    private String batchYear;
    
    // Overall Room & Capacity Totals
    private Long totalRooms;
    private Long totalCapacity;
    private Long totalUtilized;
    private Long totalVacant;
    
    // Room Count Classifications & Partial Tracking
    private Long fullyOccupiedRooms;
    private Long partiallyOccupiedRooms;
    private Long fullyVacantRooms;
    private Long partiallyOccupiedBeds;
    private Long partiallyVacantBeds;
    
    // Single Occupancy
    private Long singleRooms;
    private Long singleCapacity;
    private Long singleUtilized;
    private Long singleVacant;
    private Long singleFullVac;
    
    // Double Occupancy
    private Long doubleRooms;
    private Long doubleCapacity;
    private Long doubleUtilized;
    private Long doubleVacant;
    private Long doubleFullVac;
    private Long doublePartOccBeds;
    private Long doublePartVacBeds;
    
    // Triple Occupancy
    private Long tripleRooms;
    private Long tripleCapacity;
    private Long tripleUtilized;
    private Long tripleVacant;
    private Long tripleFullVac;
    private Long triplePartOccBeds;
    private Long triplePartVacBeds;
    
    // Quadruple Occupancy
    private Long quadRooms;
    private Long quadCapacity;
    private Long quadUtilized;
    private Long quadVacant;
    private Long quadFullVac;
    private Long quadPartOccBeds;
    private Long quadPartVacBeds;
    
    // Dormitory Rooms
    private Long dormRooms;
    private Long dormCapacity;
    private Long dormUtilized;
    private Long dormVacant;
    private Long dormFullVac;
    private Long dormPartOccBeds;
    private Long dormPartVacBeds;
    
    // PD (Physically Disabled) Rooms
    private Long pdRooms;
    private Long pdCapacity;
    private Long pdUtilized;
    private Long pdVacant;
    private Long pdFullVac;
    private Long pdPartOccBeds;
    private Long pdPartVacBeds;
    
    // Guest Rooms
    private Long guestRooms;
    private Long guestCapacity;
    private Long guestUtilized;
    private Long guestVacant;
    private Long guestFullVac;
    private Long guestPartOccBeds;
    private Long guestPartVacBeds;
}
