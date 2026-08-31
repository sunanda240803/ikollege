package com.iitm.hosteldine.dto.hostel;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.model.hostel.HostelUserMappingId;

import lombok.Data;

@Data
public class HostelUserMappingDto {
    @JsonProperty("id")
    private HostelUserMappingIdDto id;
    private String userName;
    private long hostelId;
    private List<HostelMasterDto> hostelMasterList;
}