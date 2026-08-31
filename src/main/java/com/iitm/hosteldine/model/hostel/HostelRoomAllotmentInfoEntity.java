package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"HOSTEL_ROOM_ALLOTMENT_INFO\"", schema = ModelConstants.SCHEMA)
public class HostelRoomAllotmentInfoEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_allotment_id")
    private Long roomAllotmentId;

    @Column(name = "building_id", nullable = false)
    private Long buildingId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "student_id", nullable = false, length = 30)
    private String studentId;

    @Column(name = "vacate_date")
    private LocalDate vacateDate;

    @Column(name = "sub_room_id", length = 1)
    private String subRoomId;

    @Column(name = "stay_from_date")
    private LocalDate stayFromDate;

    @Column(name = "shifted_date")
    private LocalDate shiftedDate;

    @Column(name = "new_roomallotment_id")
    private Long newRoomAllotmentId;

    @Column(name = "status")
    private String status;

    @Column(name = "checked_in_date")
    private LocalDateTime checkedInDate;

    @Column(name = "checked_out_date")
    private LocalDateTime checkedOutDate;

    @Column(name = "is_missing")
    private Boolean isMissing;

    @Column(name = "vacation_checkout_status", length = 32)
    private String vacationCheckoutStatus;

    @Column(name = "vacation_checkout_date")
    private LocalDate vacationCheckoutDate;

    
}