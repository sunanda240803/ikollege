package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"VACATION_HOSTEL_ROOM_ALLOTMENT_INFO\"", schema = "schooldev")
public class VacationHostelRoomAllotmentInfoEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_allotment_id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "building_id", nullable = false)
    private HostelFloorMasterEntity building;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private HostelRoomInfoEntity roomid;

    @Size(max = 30)
    @Column(name = "request_id", length = 30)
    private String requestid;

    @Column(name = "vacate_date")
    private LocalDate vacatedate;

    @Column(name = "stay_from_date")
    private LocalDate stayFromDate;

    @Column(name = "stay_to_date")
    private LocalDate stayToDate;

    @Size(max = 120)
    @Column(name = "student_name", length = 120)
    private String studentName;

    @Column(name = "dob")
    private LocalDate dob;

    @Size(max = 128)
    @Column(name = "email", length = 128)
    private String email;

    @Size(max = 120)
    @Column(name = "nature_of_appointment", length = 120)
    private String natureOfAppointment;

    @Size(max = 30)
    @Column(name = "dining_required", length = 30)
    private String diningRequired;

    @Size(max = 30)
    @Column(name = "student_id", length = 30)
    private String studentId;

    @Size(max = 128)
    @Column(name = "department", length = 128)
    private String department;

    @Size(max = 60)
    @Column(name = "stay_type", length = 60)
    private String stayType;

    @Size(max = 10)
    @Column(name = "sub_room_id", length = 10)
    private String subRoomid;

    @Size(max = 15)
    @Column(name = "stay_id", length = 15)
    private String stayId;

    @Size(max = 1)
    @Column(name = "gender", length = 1)
    private String gender;

}