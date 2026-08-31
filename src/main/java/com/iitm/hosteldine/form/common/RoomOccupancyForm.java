package com.iitm.hosteldine.form.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomOccupancyForm {
    private Long hostelId;
    private String reportType;
}
