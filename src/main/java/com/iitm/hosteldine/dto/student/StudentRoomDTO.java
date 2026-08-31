package com.iitm.hosteldine.dto.student;

import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRoomDTO {
    private LocalDate submittedDate;
    private String studentId;
    private String studentName;
    private String gender;
    private String wardenApprovalStatus;
    private String paymentStatus;
    private LocalDate fromDate;
    private LocalDate toDate;
    private LocalDateTime paymentDate;
    @Column(name = "id")
    private Integer requestId;
    private Integer noOfPersons;
    @Column(name = "noDays")
    private Integer noOfDays;
    @Column(name = "nFmFacilityMasterName")
    private String hostelName;
    private Integer roomNo;
    private Integer vHralSubRoomId;
    private LocalDate approvalDate;
    private LocalDateTime createdAt;
    private Integer parentRequestId;
    private Integer listOrder;
    private LocalDate submittedDate1;
    private String allottedHostelName;
    private String allottedRoomNo;
    private Integer roomAllotmentId;
    private String guestGender;
    private String guestId;
    private String relationOfGuest;
    private String accommodationType;
    private String stayType;
    private Integer amount;
    private Integer secondaryAmount;
    private Integer payAmount;
    private Long wardenId;


    // Extra field not returned by the query
    private String studentDetailString;
    private List<HostelMasterDto> hostelListGenderBased;


    public StudentRoomDTO(Object[] result) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter4 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

        this.submittedDate = result[0] != null ? LocalDate.parse((String) result[0], formatter2) : null;
        this.studentId = (String) result[1];
        this.studentName = (String) result[2];
        this.gender = (String) result[3];
        this.wardenApprovalStatus = (String) result[4];
        this.paymentStatus = (String) result[5];
        this.fromDate = result[6] != null ? LocalDate.parse((String) result[6], formatter2) : null;
        this.toDate = result[7] != null ? LocalDate.parse((String) result[7], formatter2) : null;
        this.paymentDate = result[8] != null ? LocalDateTime.parse((String) result[8], formatter3) : null;
        this.requestId = result[9] != null ? Integer.valueOf(result[9].toString()) : null;
        this.noOfPersons = result[10] != null ? Integer.valueOf(result[10].toString()) : null;
        this.noOfDays = result[11] != null ? Integer.valueOf(result[11].toString()) : null;
        this.hostelName = (String) result[12];
        this.roomNo = result[13] != null ? Integer.valueOf(result[13].toString()) : null;
        this.vHralSubRoomId = result[14] != null ? Integer.valueOf(result[14].toString()) : null;
        this.approvalDate = result[15] != null ? LocalDate.parse((String) result[15], formatter2) : null;
       // this.createdAt = result[16] != null ? LocalDateTime.parse((String) result[16], formatter4) : null;
        this.parentRequestId = result[17] != null ? Integer.valueOf(result[17].toString()) : null;
        this.listOrder = result[18] != null ? Integer.valueOf(result[18].toString()) : null;
        this.submittedDate1 = result[19] != null ? LocalDate.parse((String) result[19], formatter2) : null;
        this.allottedHostelName = (String) result[20];
        this.allottedRoomNo = (String) result[21];
        this.roomAllotmentId = result[22] != null ? Integer.valueOf(result[22].toString()) : null;
        this.guestGender = (String) result[23];
        this.guestId = (String) result[24];
        this.relationOfGuest = (String) result[25];
        this.accommodationType = (String) result[26];
        this.amount=result[27] != null ? Integer.valueOf(result[27].toString()) : null;
        this.secondaryAmount=result[28] != null ? Integer.valueOf(result[28].toString()) : null;
    }
}