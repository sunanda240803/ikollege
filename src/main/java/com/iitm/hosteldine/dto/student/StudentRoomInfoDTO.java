package com.iitm.hosteldine.dto.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentRoomInfoDTO {
    private String studentId;
    private String studentName;
    private String hostelName;
    private Long hostelId;
    private String roomNumber;
    private Long studentMobile;
    private Character gender;
}
