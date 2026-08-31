package com.iitm.hosteldine.dto.hostel;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class HostelFloorMasterDto {
    private Long id;
    private String floorName;
    private String floorDesc;
    private Double floorSize;
    private String hostelOrCollege;
    private HostelMasterDto hostel;

    public HostelFloorMasterDto(Long floorId) {
        this.id = floorId;
    }
}