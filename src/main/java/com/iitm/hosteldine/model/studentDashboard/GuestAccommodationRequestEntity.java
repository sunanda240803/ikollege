package com.iitm.hosteldine.model.studentDashboard;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"GUEST_ACCOMMODATION_REQUEST\"", schema = ModelConstants.SCHEMA)
public class    GuestAccommodationRequestEntity extends CommonEntity{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_request_id")
    private Long parentRequestId;

    @Column(name = "from_date")
    private LocalDate fromDate;

    @Column(name = "to_date")
    private LocalDate toDate;

    @Column(name = "no_of_days")
    private Integer noOfDays;

    @Column(name = "no_of_persons")
    private Integer noOfPersons;

    @Column(name = "purpose_of_visit")
    private String purposeOfVisit;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "warden_name")
    private String wardenName;

    @Column(name = "warden_email")
    private String wardenEmail;

    @Column(name = "warden_approval_status")
    private String wardenApprovalStatus;

    @Column(name = "approval_date")
    private LocalDate approvalDate;

    @Column(name = "rejection_description")
    private String rejectionDescription;

    @Column(name = "cancel_status", nullable = false)
    private String cancelStatus;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "payment_reference_no")
    private String paymentReferenceNo;

    @Column(name = "payment_amount")
    private Integer paymentAmount;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "applicable_charges")
    private Boolean applicableCharges;

    @Column(name = "documents_uploaded")
    private Boolean documentsUploaded;

    @Column(name = "approval_notes")
    private String approvalNotes;

    @Column(name = "accommodation_type")
    private String accommodationType;

    @Column(name = "ar_name")
    private String arName;

    @Column(name = "mail_sent_to")
    private String mailSentTo;

    @Column(name = "allocation_status")
    private String allocationStatus;

    @Column(name = "blood_relation_status")
    private Boolean bloodRelationStatus;

    @Column(name = "checkin_time")
    private String checkinTime;

    @Column(name = "checkout_time")
    private String checkoutTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", updatable = false)
    private StudentDetailsInfoEntity studentDetailsInfo;

    @Column(name = "secondary_amount")
    private Integer secondaryAmount;

}
