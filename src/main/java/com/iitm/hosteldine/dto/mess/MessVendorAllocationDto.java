package com.iitm.hosteldine.dto.mess;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MessVendorAllocationDto {
    private Long messId;
    private String vendorCode;
    private Double rate;
    private LocalDate fromDate;
    private LocalDate toDate;
    private LocalDate effectiveDate;
    private String activeFlag = "Y";
    private Integer gst;
    private String floorName;
    private String caterer;
    private String messName;
    private String description;

    public MessVendorAllocationDto(Long messId, String floorName) {
        this.messId = messId;
        this.floorName = floorName;
    }

    public MessVendorAllocationDto(String vendorCode, String caterer) {
        this.vendorCode = vendorCode;
        this.caterer = caterer;
    }
}
