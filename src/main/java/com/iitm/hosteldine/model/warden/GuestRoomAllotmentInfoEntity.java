package com.iitm.hosteldine.model.warden;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"GUEST_ROOM_ALLOTMENT_INFO\"", schema = ModelConstants.SCHEMA)
public class GuestRoomAllotmentInfoEntity  extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roomallotmentid")
    private Long id;

    @Column(name = "building_id")
    private Long buildingId;

    @Column(name = "roomid")
    private Long roomId;

    @Column(name = "requestid")
    private String requestId;

    @Column(name = "studentid")
    private String studentId;

    @Column(name = "stay_from_date")
    private LocalDate fromDate;

    @Column(name = "stay_to_date")
    private LocalDate toDate;

    @Column(name = "occupied_status")
    private Integer occupiedStatus;

    @Column(name = "category_type")
    private String categoryType;

    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "sub_roomid")
    private String subRoomId;

    @Column(name = "accom_request_id")
    private Long accomRequestId;

    @Column(name = "stay_request_id")
    private Long stayRequestId;

    @Column(name = "guest_id")
    private String guestId;

}
