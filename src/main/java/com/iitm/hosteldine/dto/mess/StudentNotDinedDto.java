package com.iitm.hosteldine.dto.mess;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class StudentNotDinedDto {
    private Integer serialNumber;
    private String rollNo;
    private String studentName;
    private String messName;
    private LocalDate diningFromDate;
    private LocalDate diningToDate;


    public StudentNotDinedDto(Integer serialNumber, String rollNo, String studentName, String messName,
                              LocalDate diningFromDate, LocalDate diningToDate) {
        this.serialNumber = serialNumber;
        this.rollNo = rollNo;
        this.studentName = studentName;
        this.messName = messName;
        this.diningFromDate = diningFromDate;
        this.diningToDate = diningToDate;
    }
}
