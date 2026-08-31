package com.iitm.hosteldine.constant;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class ModelConstants {
    public static final String SCHEMA = "schooldev";
    public static final String SCHEMA_ARCHIVE = "archive";

    public static final int SCHOOL_ID = 1;

    public static final String YES = "Y";
    public static final String NO = "N";
    public static final String T = "T";
    public static final String UNDERSCORE = "_";


    public static final String STATUS_ACTIVE = YES;
    public static final String STATUS_INACTIVE = NO;

    public static final String IMAGE_CANDIDATE_PROFILE = "candidateProfile";
    public static final String IMAGE_BIO_DATA_PROFILE = "bioDataProfile";
    public static final String IMAGE_BIO_DATA_STUDENT_SIGN = "bioDataStudentSign";
    public static final String IMAGE_BIO_DATA_PARENT_SIGN = "bioDataParentSign";
    public static final String FILE_BIO_DATA_PARENT_PROOF = "bioDataParentProof";
    public static final String IMAGE_STUDENT_COMPLAINT_PROFILE = "studentComplaintProfile";
    public static final String FILE_STUDENT_COMPLAINT_PROFILE = "complaintProfile";
    public static final String SICK_FOOD_REQUEST_MEDICAL_FILES_PATH = "medicalFile";
    public static final String STUDENT_LOGIN_TYPE = "Student";
    public static final String OTHER_LOGIN_TYPE = "Other Candidate";
    public static final String FACULTY_LOGIN_TYPE = "Faculty";
    public static final String HM_OFFICE_LOGIN_TYPE = "Office";

    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";
    public static final String LARGE = "large";
    public static final String SMALL = "small";

    public static final String PASSWORD_MISMATCH = "Password Mismatch";

    public static final String DEFAULT_FILE_EXTENSION = "";

    public static final String RELATION_GUARDIAN = "Guardian";
    public static final String FILE_STUDENT_PROFILE = "profile";
    public static final String FILE_STUDENT_SIGNATURE = "stud_sign";
    public static final String FILE_PARENT_SIGNATURE = "parent_sign";
    public static final String FILE_PARENT_PROOF = "_proof";
    public static final String IMAGE_STAFF_PROFILE = "StaffProfile";
    public static final String FILE_STAFF_PROFILE = FILE_STUDENT_PROFILE;

    public static final String[] EXCEL_COLUMNS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK", "AL", "AM", "AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV", "AW", "AX", "AY", "AZ", "BA", "BB", "BC", "BD", "BE", "BF", "BG", "BH", "BI", "BJ", "BK", "BL", "BM", "BN", "BO", "BP", "BQ", "BR", "BS", "BT", "BU", "BV", "BW", "BX", "BY", "BZ", "CA", "CB", "CC", "CD", "CE", "CF", "CG", "CH", "CI", "CJ", "CK", "CL", "CM", "CN", "CO", "CP", "CQ", "CR", "CS", "CT", "CU", "CV", "CW", "CX", "CY", "CZ", "DA", "DB", "DC", "DD", "DE", "DF", "DG", "DH", "DI", "DJ", "DK", "DL", "DM"};
    public static final String ROW = "Row";
    public static final String ALPHA_NUMERIC_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]{6,10}$";
    public static final String BANK_REF_NO_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]{6,30}$";
    public static final String NON_ALPHA_NUMERIC = "[^a-zA-Z0-9 ]";
    public static final String[] GENDER_LIST = {"M","F"};
    public static final String[] FLAG_LIST = {"Y","N","Yes","No"};
    public static final String ALPHA_WITH_SPACE = "^[a-zA-Z ]+$";
    public static final String[] STUDENT_HEADER_DATA = {"Student ID *", "Student First Name *", "Student Last Name", "Gender(M/F) *", "Course Head *", "Previous ID"};
    public static final String[] GUEST_COUPON_ISSUED_HEADER = {"Submitted Date", "Request ID", "Mess Name", "Coupon Number", "Name", "Dining From", "Dining To", "Coupon Type", "Coupon Used Status"};
    public static final String NON_DIGIT = "\\D";

    public static final String APP_AUTH="App";
    public static final String LDAP_AUTH="LDAP";
    public static final String STAY="Stay";
    public static final int PASSWORD_LENGTH = 6;

	public static final String ASSET_CATEGORY = "Category";
	public static final String ASSET_MAINTENANCE_TYPE = "Maintenance";
	public static final String HOSTEL = "Hostel";
	public static final String GOOD = "Good";
	
	public static String numericPattern = "^[0-9]+$";
	public static String decimalNumericPattern = "[^0-9.]";
	public static String decimalNumericPatternWithHyphen = "[^0-9.\\-]";
	public static Pattern numericRegex = Pattern.compile(numericPattern);
    public static Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
    public static final String PENDING = "Pending";
    
    public static final String FILE_SEAT_LAYOUT = "seatLayout";
    public static final String IMAGE_SHOW_WISE_SEAT_IMAGE = "seatLayoutImage";
    public static final String IMAGE_SHOW_WISE_LAYOUT_IMAGE = "layoutImage";

    public static final String ID = "id";
    public static final String URL_ID = "${id}";
    public static final String SPACE = " ";
    public static final String HASH = "#";
    public static final String EMPTY_STRING = Strings.EMPTY;
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String STUDENT = "Student";
    public static final String CANDIDATE = "Candidate";
    public static final String HYPHEN = "-";
    public static final String SINGLE_QUOTE = "'";
    public static final String ASTERISK = "*";
    public static final String SLASH = "/";
    public static final String OPEN_PARENTHESIS = "(";
    public static final String CLOSE_PARENTHESIS = ")";

	public static final String AUTHENTICATION_TYPE_INFORMATION = "i";
	public static final String AUTHENTICATION_TYPE_APPROVAL = "a";

    public static final String COMMA = ",";
    public static final String DOT = ".";
    public static final String COLAN = ":";
    public static final String RIGHT_BRACKET = " (";
    public static final String LEFT_BRACKET = ") ";
    public static final String AUTH_TYPE_I = "i";
    public static final String AUTH_TYPE_A = "a";
    public static final String DEFAULT_MAIL = "defaultMail";
    public static final String Y = "Yes";
    public static final String N = "No";
    public static final String SPLIT_SPECIAL_CHARS = "(^,+|,+$)";
    public static final String HEADER_FORM = "headerForm";
    public static final String BUTTON_PINK = "btn-pink";
    public static final String BUTTON_PURPLE = "btn-purple";
    public static final String BUTTON_GREEN = "btn-success";
    public static final String FA_FA_DOWNLOAD = "fa-solid fa-download";
    public static final String FA_FILE_PDF = "fa-solid fa-file-pdf";
    public static final String FA_FILE_EXCEL = "fa-solid fa-file-excel";
    public static final String HOSTEL_ACCOMODATION = "hostelAccomodation";
    public static final String WATER_MARKED = "waterMarked";
    public static final String TWO_DECIMAL_ZERO = "0.00";
    public static final String CR = "Cr";
    public static final String TWO_DECIMAL_POINT = "%.2f";
    public static final String AMOUNT_STRING_SPLITTER = "\\.";
    public static final String NEW_LINE = "\n";
    public static final String NOT_APPLICABLE = "N/A";
    public static final String REMITTER_ACC_NO = "10007726225";
    public static final String REMITTER_ACC_NAME = "IITM CCW REFUND ACCOUNT";
    public static final String REMITTER_ADDRESS = "CHENNAI";
    public static final String EN = "en";
    public static final String IN = "IN";
    public static final String UTC = "UTC";
    public static final String BLOCKED = "Blocked";
    public static final String NOT_BLOCKED = "NOT Blocked";
	public static final String ACTIVE = "active";
	public static final String ALL = "all";
	
	public static final String INITIATED = "Initiated";
    public static final String TESTING = "testing";
    public static final String PRODUCTION = "production";
    public static final String PG_CURRENCY = "INR";
    public static final String BILL_COUNTRY = "India";
    public static final String PG_LANGUAGE = "EN";
    public static final String PDF_EXTENSION = ".pdf";
    public static final String CANDIDATE_ACCOMODATION = "candidateAccom";

	public static final String SENT = "Sent";
    //    Accommodation Request
    public static final String REQUEST_KEY =
        "New" + Constants.BACKTICK + Constants.BACKTICK + "0" + Constants.BACKTICK+Constants.BACKTICK;

    public static final String CANDIDATE_FILE = "CANDIDATE_FILE";
    public static final String DOST = "DOST";
    public static final String VALIDATOR = "Validator";
    public static final String A = "a";
    public static final String A_CAPS = "A";
    public static final String B_CAPS = "B";

	public static final String CCW_OFFICE = "CCW Office";
	public static final String REGARDS = "Regards";
	public static final String STAY_EXTENSION_REQUEST = "StayExtensionRequest";
	public static final String CANDIDATE_PROFILE = "Candidate Profile";
	public static final String CANIDADATE_STAY_REQUEST = "CandidateStayRequestAction - sendEmail";
	public static final String MESS_TO_CARD_REQUEST_APPROVAL = "MessToCardRequestApproval - sendEmail";
	public static final String MESS_TO_CARD_REQUEST_REJECT = "MessToCardRequestRejection - sendEmail";
	
	public static final String REG = "(Reg)";
	public static final String VAC = "(Vac)";
	public static final String VACATION = "V";
	public static final String NON_VACATION = "N";
    public static final String ACCOMMODATOIN_ONLY = "Accommodation Only";
    public static final String MESS_ONLY = "Mess Only";
    public static final String ACC_MESS_BOTH = "Accommodation and Mess Both";
    
    public static final String HDC_COMPLAINT_MAIL = "HdcComplaint";
	public static final String STUDENT_DETAILS = "Student Details";
	public static final String COMPLAINT_DETAILS = "Complaint Details";
	public static final String PENALTY_DETAILS = "Penalty Details";
	public static final String MESS_BULK_MAIL = "MessBulkMail";

	public static final String BUTTON_PRIMARY = "btn-primary";
	public static final String FA_ADD_NEW = "fa-solid fa-circle-plus me-1";
    public static final String DEFAULT_MAIL_TO = "support@triesten.com";
    public static final String STAY_EXTENSION = "Stay Extension";
    public static final String STAY_EXT = "Stay Ext";
    public static final String ACCOM_REQ = "Accom Req";
    public static final String CURRENT = "Current";
    public static final String NEXT = "Next";
    public static final String WARDEN_PROFILE = "wardenProfile";
    public static final String WARDEN_IMAGE = "wardenImage";
    public static final String FILE_PATH_WARDEN_IMAGE = "FILE_PATH_WARDEN_IMAGE";

    public static final String[] TEMP_ACCOM_CONFIG_ISSUED_HEADER = {"Category Name", "Description", "Hostel Rent/day", "Breakfast", "Lunch", "Dinner",
            "Effective Date","Rebate Charges"};

    public static final String Feed_Back_Raw_Report = "FRR";
    public static final String Feed_Back_Sum_Report = "FSR";
    public static final String Mess_Priority_Registration_Report = "SPL";
    public static final String Mess_Student_Group_Registration_Report = "SGL";

    public static final String[] MESS_REGISTRATION_PRIORITY_HEADER = {"S.No", "Student ID", "Student Name", "Gender","Priority","Mess Name", "Mess Option",
            "Last Modified Time","Joining Time","Registration Type"};
    public static final String[] MESS_STUDENT_GROUP_HEADER = {"S.No", "Group Name", "Leader", "Leader Name","Leader Gender","Member", "Member Name"};
    public static final String[] MESS_STUDENT_LOGIN_ISSUE_HEADER = {"S.No", "Student ID", "Student Name", "Gender","Priority","Mess Name", "Mess Option",
            "Registration Time"};
    public static final String[] LATE_NIGHT_ENTRIES_HEADER = {"S.No", "Roll No", "Name of the Student", "Hostel Name","Swipe Day","Swipe Date", "Swipe Time"};
    public static final String[] STUDENT_COMPLAINT_HEADER = {"S.No", "Complaint Date", "Roll No", "Complaint Type", "Complaint","Complaint Description"};
    public static final String HOSTEL_NIGHT_COUPON_MAIL = "Hostel_Night_Coupon";
    public static final String LEDGER = "Ledger";
    public static final String ONLINE = "Online";
    public static final String HOSTEL_NIGHT = "Hostel Night";
    public static final String[] HOSTEL_NIGHT_COUPON_HEADER = {"S.No","Student ID", "Student Name", "Hostel Name","Pay By","Total Veg Coupon", "Total Non Veg Coupon",
            "Total Amount"};
    public static final List<String> HOSTEL_NIGHT_COUPON_HEADER_2 = Arrays.asList("S.No", "Submitted Date","Student ID", "Student Name", "Hostel Name","Pay By","Total Veg Coupon", "Total Non Veg Coupon", "Veg Rate"
            , "Non-veg Rate", "Total Amount", "Paid Amount", "Order Number", "Payment Date", "Payment Type");
    public static final String HOSTEL_SWAP_MAIL = "Hostel_Swap_Mail";
    public static final String SICK_FOOD_REQUEST_UPDATE_MAIL = "Sick_Food_Request_Update";
    public static final String[] DEVICE_FR_LOG_REPORT_HEADER = {"S.No", "Roll No", "Log Time", "Mess Name", "User ID", "Device IP"};
    public static final String TILDE = "~";
    public static final String ACCOMMODATION_PREFERENCE_ACCOMMODATION = "ACCOMMODATION";
    public static final String ACCOMMODATION_PREFERENCE_HOSTEL = "HOSTEL";
    public static final String ACCOMMODATION_PREFERENCE_HOSTEL_NOT_NEEDED = "HOSTEL_NOT_NEEDED";
    public static final String Convocation_Purchase_Confirmation = "Convocation_Purchase_Confirmation";
}
