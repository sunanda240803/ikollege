package com.iitm.hosteldine.model;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;
import java.time.LocalDate;

/**
 * Mapping for DB view
 */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "\"COMPLETE_HOSTEL_ALLOTMENT_VIEW\"", schema = ModelConstants.SCHEMA)
public class CompleteHostelAllotmentViewEntity {
    @Id
    @Column(name = "allotment_id")
    private Long allotmentId;

    @Column(name = "hostel_id")
    private Long hostelId;

    @Size(max = 64)
    @Column(name = "hostel_name", length = 64)
    private String hostelName;

    @Column(name = "floor_id")
    private Integer floorId;

    @Size(max = 32)
    @Column(name = "floor_name", length = 32)
    private String floorName;

    @Column(name = "room_id")
    private Integer roomId;

    @Size(max = 32)
    @Column(name = "room_no", length = 32)
    private String roomNo;

    @Size(max = 1)
    @Column(name = "sub_room_id", length = 1)
    private String subRoomId;

    @Column(name = "student_type", length = Integer.MAX_VALUE)
    private String studentType;

    @Size(max = 30)
    @Column(name = "request_id", length = 30)
    private String requestId;

    @Column(name = "student_name", length = Integer.MAX_VALUE)
    private String studentName;

    @Column(name = "student_id", length = Integer.MAX_VALUE)
    private String studentId;

    @Column(name = "stay_from_date")
    private LocalDate stayFromDate;

    @Column(name = "stay_to_date")
    private LocalDate stayToDate;

    @Column(name = "vacate_date")
    private LocalDate vacateDate;

    @Column(name = "shifted_date")
    private LocalDate shiftedDate;

    @Column(name = "new_allotment_id")
    private Integer newAllotmentId;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "email", length = Integer.MAX_VALUE)
    private String email;

    @Column(name = "nature_of_appointment", length = Integer.MAX_VALUE)
    private String natureOfAppointment;

    @Column(name = "dining_required", length = Integer.MAX_VALUE)
    private String diningRequired;

    @Column(name = "allocation_status", length = Integer.MAX_VALUE)
    private String allocationStatus;

    @Column(name = "allocation_type", length = Integer.MAX_VALUE)
    private String allocationType;

    @Column(name = "vacation_category", length = Integer.MAX_VALUE)
    private String vacationCategory;

    @Size(max = 50)
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Size(max = 50)
    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "active_flag", length = Integer.MAX_VALUE)
    private String activeFlag;

    @Column(name = "vacation_checkout_date")
    private LocalDate vacationCheckoutDate;

    @Column(name = "vacation_checkout_status", length = Integer.MAX_VALUE)
    private String vacationCheckoutStatus;

    @Column(name = "checked_in_date", length = Integer.MAX_VALUE)
    private String checkedInDate;

}