package com.iitm.hosteldine.dto.student;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
public class StudentDetailsWithHostelDTO {

    private String studentId;
    private String firstName;
    private String lastName;
    private String dob;
    private String gender;
    private String address;
    private String city;
    private String state;
    private String country;
    private Integer pincode;
    private String parentEmailId;
    private String hostelName;
    private String roomNo;
    private String alternateContactNumber;

    public StudentDetailsWithHostelDTO(Object[] result) {
        this.studentId = result.length > 0 && result[0] != null ? result[0].toString() : null;
        this.firstName = result.length > 1 && result[1] != null ? result[1].toString() : null;
        this.lastName = result.length > 2 && result[2] != null ? result[2].toString() : null;
        this.dob = result.length > 3 && result[3] != null ? result[3].toString() : "";
        this.gender = result.length > 4 && result[4] != null ? result[4].toString() : null;
        this.address = result.length > 5 && result[5] != null ? result[5].toString() : null;
        this.city = result.length > 6 && result[6] != null ? result[6].toString() : null;
        this.state = result.length > 7 && result[7] != null ? result[7].toString() : null;
        this.country = result.length > 8 && result[8] != null ? result[8].toString() : null;
        this.pincode = result.length > 9 && result[9] instanceof Integer ? (Integer) result[9] : 0;
        this.parentEmailId = result.length > 10 && result[10] != null ? result[10].toString() : null;
        this.hostelName = result.length > 11 && result[11] != null && StringUtils.isNotBlank(result[11].toString())  ? result[11].toString() : null;
        this.roomNo = result.length > 12 && result[12] != null && StringUtils.isNotBlank(result[12].toString())  ? result[12].toString() : null;
        this.alternateContactNumber = result.length > 13 && result[13] != null ? result[13].toString() : null;


    }
}
