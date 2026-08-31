package com.iitm.hosteldine.constant;

import com.iitm.hosteldine.config.SecurityCtxUtil;

import java.util.List;

public class Constants {

	//public static final String STUDENT_ID = SecurityCtxUtil.userName().toUpperCase();

	public static final String REDIRECT = "redirect:";
	public static final String FORM = "form";
	public static final String FOOTER_FORM = "footerForm";
	public static final String RESPONSE = "response";
	public static final String ANONYMOUS = "Anonymous";
	public static final String MODAL_ERROR = "modalError";
	public static final String FORM_ERROR = "formError";
	public static final String BINDING_RESULT_DATA = "org.springframework.validation.BindingResult.";
	public static final String HTTP = "http://";
	public static final String HTTPS = "https://";

	public static final String SAVED = "saved";
	public static final String UPDATED = "updated";
	public static final String ERROR = "ERROR";
	public static final String LOGOUT = "Logout";

	public static final String TIME_FORMAT = "HH:mm";
	public static final String TIME_FORMAT_SS = "HH:mm:ss";
	public static final String BACKEND_DATE_FORMAT = "yyyy-MM-dd";
	public static final String BACKEND_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
	public static final String BACKEND_DATETIME_FORMAT_2 = "yyyy-MM-dd HH:mm:ss.S";
	public static final String BACKEND_DATETIME_FORMAT_3 = "yyyy-MM-ddTHH:mm:ssZ";
	public static final String FRONTEND_DATE_FORMAT = "MMM dd, yyyy";
	public static final String FRONTEND_DATE_TIME_FORMAT = FRONTEND_DATE_FORMAT+ " "+TIME_FORMAT_SS;
	public static final String FRONTEND_DATE_TIME_WITH_IST = FRONTEND_DATE_TIME_FORMAT + " 'IST'";
	public static final String PAYMENT_DATE_FORMAT = "dd/MM/yyyy";
	public static final String PAYMENT_DATE_TIME_FORMAT = PAYMENT_DATE_FORMAT+" "+TIME_FORMAT_SS;
	public static final String FRONTEND_DATE_MON_YEAR_TIME_FORMAT = "MMM dd, yyyy hh:mm a";
	public static final String BACKEND_DATE_MON_YEAR_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String FRONTEND_DATE_TIME_FORMAT_2 = "dd-MMM-yy HH:mm";
    public static final List<String> SUPPORTED_DATE_FORMATS = List.of(
            "yyyy-MM-dd",
            "dd-MM-yyyy",
            "MM-dd-yyyy",
            "yyyy/MM/dd",
            "dd/MM/yyyy",
            "MM/dd/yyyy",
            "dd.MM.yyyy",
            "MM.dd.yyyy",
            "dd MMM yyyy",
            "MMM dd, yyyy",
            "MMMM dd, yyyy",
            "dd-MMM-yyyy",
            "dd/MMM/yyyy"
    );

	 public static final String DECIMAL_FORMAT = "#,##,##,##0.00";
	 public static final String LANG = "en";
	 public static final String COUNTRY = "IN";

	public static final String DOT = ".";
	public static final String OUT_FOR_DELIVERY = "OutForDelivery";
	public static final String SICK_FOOD = "Sick Food";
	public static final String SICK_FOOD_NOT_DELIVER = "Sick Food Not Delivered";
	public static final String DELIVERED = "Delivered";
	public static final String HOSTEL = "hostel";
	public static final String MESS = "mess";
	public static final String RECEIVED = "Received";
	public static final String NA = "NA";
	public static final String MALE = "M";
	public static final String FEMALE = "F";
	public static final String ARRAY_SEPARATOR = ",";
	public static final String ENABLE = "Enable";
	public static final String DISABLE = "Disable";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String SYMBOL_PATTERN = "^,|,$";
	public static final String QUERY_SEPARATOR = "?";
	public static final String PARAM_SEPARATOR = "&";
	public static final String ASSIGNMENT_OPERATOR = "=";
	public static final String PREFIX_TEXT_ROLE= "^ROLE_";

	public static final String DU = "DU";
	public static final String FAILURE = "Failure";

	public static final String APPROVED = "Approved";
	public static final String APPROVED_WITH_CONDITION = "Approved?";
	public static final String REJECTED = "Rejected";
	public static final String PENDING = "Pending";
	public static final String HYPHEN = "-";
	public static final String STUDENT_COMPLAINT = "Student Complaint";
	public static final String TIME = "T";

	public static final String DEBIT = "d";
	public static final String CREDIT = "c";
	public static final String DEBIT_FULL_FORM = "Debit";
	public static final String CREDIT_FULL_FORM = "Credit";
	public static final String DATA = "data";
	public static final String TOTAL_CREDIT = "totalCredit";
	public static final String TOTAL_DEBIT = "totalDebit";
	public static final String TOTAL_CLOSING_BAL = "totalClosingBalance";
	public static final String TOTAL_OPENING_CLOSING_BAL = "totalOpeningClosingBalance";
	public static final String DOLLER = "₹";
	public static final String STUDENT_MESS_PRIORITY = "Student Mess Priority Registration";
	public static final String LOGIN_ISSUE_MESS_PRIORITY = "Login Issue Mess Priority Registration";
	public static final String STAY_WITH_STUDENT = "Stay Along with Student";
	public static final String INDIVIDUAL_GUEST_ROOM = "Individual Guest Room";
	public static final String FATHER = "Father";
	public static final String MOTHER = "Mother";
	public static final String BROTHER = "Brother";
	public static final String SISTER = "Sister";
	public static final String HUSBAND = "Husband";
	public static final String SPOUSE = "Spouse";
	public static final String MALE_FULL_FORM = "Male";
	public static final String FEMALE_FULL_FORM = "Female";
	public static final String NEW = "New";
	public static final String GUEST_ACCOMMODATION_REQUEST = "Guest Accommodation Request";
	public static final String STUDENT_BULK_UPLOAD = "Student Bulk Upload";
	public static final String BACKTICK = "`";
	public static final String MULTI = "multi";
	public static final String MESS_MS = "MS";
	public static final String ACCOUNT_HEAD_TYPE_R = "R";
	public static final String CURRENT_YEAR = "c";
	public static final String NULL = "null";
	public static final String CANDIDATE_POST = "CANDIDATE_POST_JSON";
	public static final String CANDIDATE_PROFILE = "CANDIDATE_PROFILE";
	public static final String ADD = "add";
	public static final String EDIT = "edit";
	public static final String TO = " to ";
	public static final String AFTER_PAYMENTGATEWAY = "?responseType=afterPayment";
	public static final String PAYMENT_CANCEL = "?responseType=cancel";
	public static final String SOFTWARE_ADMIN = "SoftwareAdmin";
	public static final String ADMIN = "Admin";
	public static final String CANCEL = "cancel";
	public static final String AFTER_PAYMENT = "afterPayment";
	public static final String COMMONFILEEXTENSION = "commonFileExtension";
	public static final String ACCOMMODATION_FORM_HEADER = "Application for Accommodation in Hostel";
	public static final String STAY_EXTENSION_FORM_HEADER = "Stay Extension Request Form";
	public static final String ADDITIONAL_PARAM = "additionalParam.";
	public static final String PAGE = ".page";
	public static final String SIZE = ".size";

	//Regex
	public static final String ACCOMMODATION_EDIT_REGEX = "^([^`]*)`([^`]*)`([^`]*)`([^`]*)`([^`]*)$";


	public static final String GUEST_ROOM = "guest-room";
	public static final String CCW = "CCW";
	public static final String USER_ROLE_CCW_DEAN = "DEAN";
	public static final String USER_ROLE_DOST_DEAN = "DOST Dean";
	public static final String USER_ROLE_AR= "AR";
	public static final String USER_NAME_CCW = "ccw.iitm";
	public static final String USER_ROLE_FACULTY = "Faculty";
	public static final String STATUS_ACTIVE = "Active";
	public static final String PAYMENT_STATUS_PAID = "Paid";
	public static final String PAYMENT_TYPE_I_COLLECT = "icollect";
	public static final String PAYMENT_TYPE_DIGITAL = "digital";
	public static final String STATUS_REJECTED = "Rejected";
	public static final String MESS_CC_EXCHEANGE = "MS to CC Exchange";
	public static final String MESS_CC_EXCHEANGE_TYPE = "ms_to_cc_exchange";
	public static final String TRANSFER_AMOUNT = "Transfer Amount";
	public static final String STUD = "STUD";
	public static final String CREDIT_CARD = "CC";
	public static final String IKOLLEGE_TRANSACTION = "ikollege_transaction";
	public static final String JV = "JV";
	public static final String COL_LINK = "Col-Link";
	public static final String COL_ACTION = "Col-Action";
	public static final String TYPE_GUEST_ROOM = "guest-room";
	public static final String TYPE_STUDENT_ROOM = "student-room";	
	
	public static final String NIL = "NIL";
	public static final String NILL = "nILL";
	public static final String MESS_REBATE_INFORMATION = "Approval not required. Only for information.";
	public static final String ON = "on";
	public static final String ACTIVE_FLAG = "Y";
	public static final String USER_ROLE_DEAN = "DEAN";
	public static final String USER_ROLE_WARDEN = "Warden";
	public static final String INCHARGE = "Incharge";
	public static final String DUMMY_ROLE = "Dummy";
	public static final String ROLE_OFFICE = "Office";
	public static final String ACCOMODATION_LEDGER_DESC1 = "Amount Paid through";
	public static final String ACCOMODATION_LEDGER_DESC2 = "by Reference no :";
	public static final String ACCOMODATION_LEDGER_HOSTEL_AMOUNT = "Hostel Amount Paid through";
	public static final String ACCOMODATION_LEDGER_MESS_AMOUNT = "Mess Amount Paid through";
	public static final String ACCOMODATION_LEDGER_CARD_CHARGES = "CardCharges Amount Paid through";
	public static final String CANCEL_STATUS = "N";

	public static final String ACTION_EDIT = "Edit";
	public static final String ACTION_VIEW = "View";
	public static final String ACTION_DOWNLOAD = "Download";
	public static final String ACTION_APPROVE = "Approve";
	public static final String ACTION_REJECT = "Reject";
	public static final String ACTION_APPROVE_WITH_CONDITION = "Approve With Condition";
	public static final String ACTION_SEND_MESSAGE = "Send Message";
	public static final String ACTION_REJECT_REVERSAL = "Reject Reversal";

	public static final String BOYS_HOSTEL_B = "B";
	public static final String BOYS_HOSTEL_BT = "BT";

	public static final String HDC_COMPLAINT_FILE_PATH = "HDC_COMPLAINT_FILE_PATH";
	public static final String GUARDIAN = "Guardian";

	public static final String HOSTEL_NOT_FOUND = "Hostel Not Found";
	public static final String INVALID_REQUEST = "Invalid Request";

	public static final String VACATING_LINK = "vacatingLink";
	public static final String TODAY_CHECKOUT = "todayCheckOut";
	public static final String PENDING_CHECKOUT = "pendingCheckout";
	public static final String ALL = "All";
	public static final String SUCCESS = "Success";

	public static final String ESTABLISHMENT_B = "Establishment B";
	public static final String SCREEN_TYPE_STUDENT_DEBIT = "student_debit";
	public static final String SCREEN_TYPE_STUDENT_DEMAND = "student_demand";
	public static final String ESTABLISHMENT_DEBIT = "establishment_debit";
	public static final String ODEP = "ODEP";

	public static final String OTHERS = "others" ;

	public static final String PENALTY_CLAIM = "penaltyClaim" ;
	public static final String DONATE_CLAIM = "donateClaim" ;
	public static final String HOSTEL_DEPOSIT_REFUND = "hostelDepositRefund" ;
	public static final String CARD_AMOUNT_TRANSFER = "cardAmountTransfer" ;
	public static final String PURCHASE_AMOUNT_DEDUCT = "purchaseAmountDeduct" ;

	public static final String CLAIM_DONATION = "claim_donation" ;
	public static final String REFUND_HOSTEL_DEPOSIT = "refund_hostel_deposit" ;
	public static final String CLAIM_PENALTY_CHARGES = "claim_penalty_charges" ;


	public static final String CC_TO_MS_EXCHANGE = "CC to MS Transfer" ;
	public static final String IKOLLEGE_CC_TO_MS_EXCHANGE = "cc_to_ms_exchange" ;

	public static final String SCREEN_TYPE_CLAIM_PENALTY_CHARGES = "CLAIM PENALTY CHARGES" ;
	public static final String SCREEN_TYPE_CLAIM_DONATION_CHARGES = "CLAIM DONATION CHARGES" ;
	public static final String SCREEN_TYPE_REFUND_HOSTEL_DEPOSIT = "REFUND HOSTEL DEPOSIT" ;
	public static final String SCREEN_TYPE_TRANSFER_AMOUNT = "Transfer Amount" ;
	public static final String SCREEN_TYPE_PURCHASE_AMOUNT = "CLAIM STUDENT EVENT PURCHASE AMOUNT" ;


	public static final String ACC_HEAD_PENCHAR = "PENCHAR" ;
	public static final String ACC_HEAD_DONAMNT = "DONAMNT" ;
	public static final String ACC_HEAD_HOSDEP = "HOSDEP" ;
	public static final String IKOLLEGE_SETTLEMENT_REFUND = "settlement_refund" ;

	public static final String  PDF_APPLICATION_TYPE="application/pdf";
	public static final String  TEMPORARY_ACCOMMODATION = "TC";
	public static final String  GUEST_COUPON = "GC";
	public static final String OCCUPIED = "Occupied";
	public static final String VACANT = "Vacant";
	public static final String PARTIALLY_OCCUPIED = "Partially Occupied";

	public static final String APP_ADMIN = "AppAdmin";
	public static final String CATERER = "caterer";
	public static final String STUDENT = "student";

	public static final String REPORT_TYPE_ROOM_OCCUPANCY = "roomOccupancy";
	public static final String REPORT_TYPE_ROOM_VACANCY = "seatVacancy";

    public static final String VIEW="view";
    public static final String PDF="pdf";
    public static final String DELETE="delete";
    public static final String ONLINE_JOB="OnlineJob";
    public static final String LOGIN_ISSUE="loginIssue";
    public static final String MONTHLY="Monthly";

	public static final String BIOMETRIC_ADMIN_DEFAULT_USERNAME	 = "A999";
	public static final String BIOMETRIC_ADMIN_DEFAULT_PASSWORD	 = "1";

}
