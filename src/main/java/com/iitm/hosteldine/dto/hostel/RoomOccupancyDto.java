package com.iitm.hosteldine.dto.hostel;

import com.iitm.hosteldine.constant.DateUtility;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
public class RoomOccupancyDto {
    private Long hostelId;
    private String hostelName;
    private Integer roomId;
    private String roomNo;
    private String subRoomId;
    private String allocationType;
    private String studentId;
    private String studentName;
    private String email;
    private String natureOfAppointment;
    private LocalDate vacateDate;

    public RoomOccupancyDto(Object[] row) {
        try {
            this.hostelId = row[0] != null ? ((Number) row[0]).longValue() : null;
            this.hostelName = row[1] != null ? String.valueOf(row[1]) : null;
            this.roomId = row[2] != null ? ((Number) row[2]).intValue() : null;
            this.roomNo = row[3] != null ? String.valueOf(row[3]) : null;

            // Handle subRoomId which might be Character or String
            this.subRoomId = row[4] != null ?
                    (row[4] instanceof Character ? String.valueOf((Character) row[4]) : String.valueOf(row[4]))
                    : null;

            this.allocationType = row[5] != null ? String.valueOf(row[5]) : null;
            this.studentId = row[6] != null ? String.valueOf(row[6]) : null;
            this.studentName = row[7] != null ? String.valueOf(row[7]) : null;
            this.email = row[8] != null ? String.valueOf(row[8]) : null;
            this.natureOfAppointment = row[9] != null ? String.valueOf(row[9]) : null;
            this.vacateDate = row[10] != null ? DateUtility.parseSqlDateToLocalDate((java.sql.Date) row[10]) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping row data to RoomOccupancyDto", e);
        }
    }

}