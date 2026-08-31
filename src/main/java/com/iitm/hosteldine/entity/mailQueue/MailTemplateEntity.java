package com.iitm.hosteldine.entity.mailQueue;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


@Entity
@Table(name = "\"IIT_W_MAIL_TEMPLATE\"", schema = ModelConstants.SCHEMA)
@Setter
@Getter
public class MailTemplateEntity extends CommonEntity {

    @Id
    @Column(name = "mail_type", length = 64, nullable = false)
    private String mailType;

    @Column(name = "mail_subject", length = 256)
    private String mailSubject;

    @Column(name = "mail_template", columnDefinition = "text")
    private String mailTemplate;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "category", length = 1024)
    private String category;

    @Column(name = "approval_level")
    private Long approvalLevel;

    @Column(name = "authority_type", length = 256)
    private String authorityType;

    //Mess Rebate
	public static final String MESS_REBATE_MAIL = "MessRebate";
	public static final String MESS_REBATE_APPROVAL_MAIL = "MessRebateRequestApproval";
	public static final String MESS_REBATE_STUDENT_APPROVAL = "MessRebateStudentApprovalStatus";
	public static final String APPROVE_REBATE_BUTTON = "ApproveRebateButton";
	public static final String REJECT_REBATE_BUTTON = "RejectRebateButton";
	public static final String VIEW_DETAILS_REBATE_BUTTON = "ViewDetailsRebateButton";
	public static final String APPROVE_REBATE_LINK = "ApproveRebateLink";
	public static final String REJECT_REBATE_LINK = "RejectRebateLink";
	public static final String VIEW_REBATE_LINK = "ViewDetailsRebateLink";
	public static final String REBATE_REJECT_REASON = "RebateRejectReason";
	
	public static final String SICK_FOOD_REQUEST = "SickFoodRequest";
	public static final String STUDENT_COMPLAINT_FORM = "StudentComplaintForm";
	public static final String PRIORITY_MESS_REGISTRATION = "PrioityMessRegistration";
	public static final String LOGIN_ISSUE_PRIORITY_MESS = "LoginIssuePrioityMess";
	public static final String GUEST_ACCOMMODATION_REQUEST = "GuestAccommodationRequest";
	public static final String GUEST_ACCOMMODATION_CANCEL_REQUEST = "GuestAccommodationCancelRequest";
	public static final String GUEST_ACCOMMODATION_STAY_REQUEST = "GuestAccommodationExtensionRequest";
	public static final String SICK_FOOD_NOT_DELIVER = "FoodNotDeliverStatus";
	public static final String GUEST_ACCOMMODATION_REQUEST_APPROVED = "GuestAccommodationRequestApproved";
	public static final String GUEST_ACCOMMODATION_REQUEST_REJECTED = "GuestAccommodationRequestRejected";
	public static final String GUEST_ACCOMMODATION_REQUEST_PAID = "GuestAccommodationRequestPaid";


	public static final String STUDENT_BULK_UPLOAD_SEND_MESSAGE = "StudentBulkUploadSendMessage";
	public static final String STUDENT_BULK_ACCOMMODATION_FACULTY_MAIL = "StudentBulkAccommodationUploadedFacultyMail";

	//Hostel Accommodation
	public static final String HOSTEL_ACCOMMODATION_MAIL = "HostelAccommodation";
	public static final String APPROVE_ACCOMMODATION_BUTTON = "ApproveAccommodationButton";
	public static final String REJECT_ACCOMMODATION_BUTTON = "RejectAccommodationButton";
	public static final String VIEW_ACCOMMODATION_BUTTON = "ViewAccommodationButton";
	public static final String APPROVE_ACCOMMODATION_LINK = "ApproveAccommodationLink";
	public static final String REJECT_ACCOMMODATION_LINK = "RejectAccommodationLink";
	public static final String VIEW_ACCOMMODATION_LINK = "ViewAccommodationLink";
	public static final String STUDENT_SCHOLAR_REJECT = "StudentScholarReject";
	public static final String STUDENT_SCHOLAR_LEVEL_1 = "StudentScholarLevel1";
	public static final String STUDENT_SCHOLAR_LEVEL_2 = "StudentScholarLevel2";
	// Guest Coupon Request
	public static final String GUEST_COUPON_REQUEST_MAIL = "GuestCouponRequest";
	public static final String STUDENT_BULK_ACCOM_UPLOADED = "StudentBulkAccommodationUploaded";
	
	//Vacating Students
	public static final String STUDENT_VACATING_APPROVAL_MAIL = "StudentHostelVacatingFormTemplate";
	public static final String VIEW_DETAILS_VACATING_STUDENT_BUTTON = "StudentHostelVacatingFormButtonsTemplate";
	//Mess Bill Summary Report
	public static final String MESS_BILL_SUMMARY_MAIL = "messBillSummaryReportTemplate";
    //Other candidate password reset template
	public static final String OTHER_CANDIDATE_PASSWORD_RESET_CONTENT = "OtherCandidatePasswordResetContent";
	public static final String NEW_STAFF_CREDENTIALS = "NewStaffCredentials";
	public static final String WARDEN_INCHARGE_INTIMATION = "WardenInchareIntimation";

}
