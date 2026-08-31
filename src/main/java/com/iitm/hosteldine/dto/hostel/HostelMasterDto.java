package com.iitm.hosteldine.dto.hostel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HostelMasterDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("hostelName")
    private String hostelName;
    @JsonProperty("extensionFlag")
    private String extensionFlag;
    @JsonProperty("hostelGenderType")
    private String hostelGenderType;
    @JsonProperty("hostelOfficeEmail")
    private String hostelOfficeEmail;
    @JsonProperty("hostelShortCode")
    private String hostelShortCode;
    private Double vegAmount;
    private Double nonVegAmount;
    private Boolean status;

	public HostelMasterDto() {
	}
    
    public HostelMasterDto(Long id, String hostelName, String hostelShortCode) {
        this.id = id;
        this.hostelName = hostelName;
        this.hostelShortCode = hostelShortCode;
    }

    public HostelMasterDto(Long hostelId) {
        this.id = hostelId;
    }
}