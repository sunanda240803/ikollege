package com.iitm.hosteldine.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentInfoAPIDto {
    private String finalBalance;
    private Long bioDataId;
    private String studentId;
    private String studentName;
    private String studentMobile;
    private String gender;
    private String category;
    private String familyMobileNo;
    private String hostelName;
    private String roomNumber;
    private String seat;


    // ✅ Constructor matching the query column order
    public StudentInfoAPIDto(String finalBalance, Long bioDataId, String studentId, String studentName,
                             String studentMobile, String gender, String category, String familyMobileNo,
                             String hostelName, String roomNumber, String seat) {
        this.finalBalance = finalBalance;
        this.bioDataId = bioDataId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentMobile = studentMobile;
        this.gender = gender;
        this.category = category;
        this.familyMobileNo = familyMobileNo;
        this.hostelName = hostelName;
        this.roomNumber = roomNumber;
        this.seat = seat;
    }
}

