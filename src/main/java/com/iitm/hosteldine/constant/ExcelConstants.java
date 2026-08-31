package com.iitm.hosteldine.constant;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExcelConstants {
	
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	public static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public static final String ATTACHMENT_FILE_NAME = "attachment; filename=";
	public static final String EXCEL_EXTENSION = ".xlsx";
	public static final String INVALID_EXCEL_TEMPLATE = "Invalid Excel Template";
	public static final String EMPTY_FILE = "Empty file. Please upload with data";
	public static final String ALPHANUMERIC_ISSUE = "Accept alphanumeric characters with a minimum of 6 and a maximum of 10 characters.";
	public static final String ENTER_SPECIFIC_FORMAT = "Please Enter Date in the specified format";
	public static final String ENTER_ONLY_ALPHABET = "Enter only Alphabets";

	public static final String STUDENT_BULK_UPLOAD_SHEET_NAME = "StudentBulkUpload";


	public static final String HOSTEL_ROOM_ALLOTMENT_UPLOAD = "Hostel Room Allotment Upload";
	public static final String HOSTEL_ROOM_ALLOTMENT_UPLOAD_FILENAME = "attachment; filename=Bulk Hostel Room Allotment Template.xlsx";
	public static final String MESS_TO_CARD_REQUEST_LIST = "attachment; filename=Mess To Card Transfer Request List.xlsx";
	public static final String[] HOSTEL_ROOM_ALLOTMENT_HEADER_DATA = { "Student ID *", "Hostel Name *", "Room Number *", "Seat" };
	public static final String[] HOSTEL_ROOM_ALLOTMENT_HEADER_DATA_WIDTH = { "8000", "8000", "8000", "8000" };
	
	public static final String[] STUDENT_WITH_REMARK_DATA = { "Student Id *", "From Date *", "To Date *", "Description *" };
	public static final String[] STUDENT_WITH_REMARK_DATA_WIDTH = { "6500", "4500", "4500", "7500" };
	
	public static final String[] ALL_STUDENT_DETAILS_HEADER_DATA = { "SI.No", "Roll No", "Student Name", "Hostel Name",
				"Room No", "Sub Room No", "Gender", "Course", "Semester", "Previous ID", "DOB", "Category",
				// "Nationality",
				"Contact Number 1", "Contact Number 2", "City", "State", "Country", "Pincode", "Address", "Email",
				"Server(LDAP / App)" };
	public static final String[] ALL_STUDENT_DETAILS_HEADER_DATA_WIDTH = { "1500", "4000", "6500", "7500", "5500",
			"7500", "5000", "5000", "7000", "4000", "1500", "5500", "7500", "7500", "3500", "3500", "4500", "4500",
			"9000", "3500", "7500" };
	
	public static final String[] STUDENT_HEADER_DATA = { "Student ID *", "Student First Name *", "Student Last Name",
			"Gender(M/F) *", "Course Head *", "Previous ID" };
	public static final String[] STUDENT_HEADER_DATA_WIDTH = { "6500", "7500", "7500", "4500", "4500", "7500" };
	
	public static final String[] BULK_ROOM_CONFIGURATION_DATA = { "Hostel Name *", "Floor Name *", "Room Number *",
			"Capacity *", "Vacation(Y/N) *", "Vacation Capacity", "Room Configuration Type" };
	public static final String[] BULK_ROOM_CONFIGURATION_DATA_WIDTH = { "6500", "8000", "4500", "4500", "4500", "4500", "4500", "7500" };
	
	public static final String[] ROOM_INVENTORY_HEADER_DATA_WIDTH = { "8000", "7500", "8000", "8000" };
	public static final String[] ROOM_INVENTORY_HEADER_DATA = { "Asset Category *", "Asset Name *", "Hostel Name *", "Room Number *" };

	public static final String[] ALL_MESS_CARD_REQUEST_HEADER_DATA = { "Ref#", "Request Date", "Student ID", "Student Name",
			"Current Balance", "Request Amount", "Transferred Date", "Status"};
	public static final String[] ALL_MESS_CARD_REQUEST_HEADER_DATA_WIDTH = { "2500", "5000", "6500", "8000", "5500",
			"5500", "5000", "6500"};
	
	public static final String[] WELLNESS_REPORT_HEADER_DATA = { "Category", "Student ID", "Student Name", "Hostel Name",
			"Room Number", "Contact Number", "Student Email", "Visit Date", "Number Of Visit", "Interaction Mode",
			"Referral Type", "Concern Type", "Coordinator Name", "Concerns Discussed", "Follow Up Date",
			"Future Action Plan", "Status" };
	public static final String[] WELLNESS_REPORT_HEADER_DATA_WIDTH = { "7000", "8000", "8000", "6000", "3000", "9000", "8000",
			"2500", "1500", "5000", "5000", "5000", "8000", "5000", "3000", "5000", "5000" };
	public static  final String FONT_ARIAL = "Arial";

	public static final String[] ACCOMMODATION_REQUEST_REPORT_HEADER_DATA = { "Sl.No", "Submitted Date", "Student Name",
			"Student ID", "Gender", "DOB", "Student Email", "Appointment From", "Appointment To", "Stay Request From",
			"Stay Request To", "Nature of Appointment", "Mess Option", "Stipend / Fellowship / Pay",
			"Validating Authority", "Validator Email", "Occupancy", "Status", "Approval Notes", "Hostel Name",
			"Room No", "Seat" };
	public static final String[] ACCOMMODATION_REQUEST_REPORT_HEADER_DATA_WIDTH = { "1500", "4000", "7500", "6500",
			"2500", "4000", "7500", "4000", "4000", "4000", "4000", "7500", "4000", "5000", "7500", "7500", "4000",
			"4000", "7000", "6000", "3000", "3000" };
	public static final String[] STUDENT_BULK_REQUEST_HEADER_DATA = { "Student ID", "Student/Participant Name","Stay From\n" +
			"("+Constants.BACKEND_DATE_FORMAT+")","Stay To\n" +
			"("+Constants.BACKEND_DATE_FORMAT+")", "Dining(Y/N)\n","Validating authority name",
			"Validating authority email", "Designation of the student", "Role played during stay", "Gender(M/F)" };
	public static final String[] STUDENT_BULK_REQUEST_HEADER_DATA_FACULTY = { "Student ID", "Student/Participant Name","Appointment From\n" +
			"("+Constants.BACKEND_DATE_FORMAT+")",
			"Appointment To\n" + "("+Constants.BACKEND_DATE_FORMAT+")","Stay From\n" +
			"("+Constants.BACKEND_DATE_FORMAT+")","Stay To\n" +
			"("+Constants.BACKEND_DATE_FORMAT+")", "Dining(Y/N)\n", "Gender(M/F)"};

	public static final String[] STUDENT_BULK_REQUEST_HEADER_DATA_WIDTH = { "6500", "7500", "7500", "7500", "3500", "7500","7500","7500","7500","7500" };

	public static final String MESS_ALLOTTED_LIST_FILENAME = "attachment; filename=Mess Allotted List Report.xlsx";
	public static final String[] MESS_ALLOTTED_LIST_DATA = {"Student ID", "Student Name", "From Date", "To Date", "Mess Name" };
	public static final String[] MESS_ALLOTTED_LIST_DATA_WIDTH = { "7000", "8000", "4000", "4000", "8000" };


	public static final String FOOD_COURT_DEBIT_FILENAME = "attachment; filename=FoodCourtDebitBulkUpload.xlsx";
	public static final String[] FOOD_COURT_DEBIT_DATA = { "Student Id *", "Mess Head *", "Amount *", "Purchase Date *" };
	public static final String[] FOOD_COURT_DEBIT_DATA_WIDTH = { "6500", "4500", "4500", "7500" };

	public static final String MESS_ALLOCATION_FILENAME = "attachment; filename=Mess Allocation Template.xlsx";
	public static final String MESS_CHANGE_UPLOAD_FILENAME = "attachment; filename=Mess Change Template.xlsx";
	public static final String MESS_REMOVE_UPLOAD_FILENAME = "attachment; filename=Mess Remove Template.xlsx";
	public static final String MESS_ALLOCATION = "Mess Allocation";
	public static final String MESS_CHANGE = "Mess Change";
	public static final String MESS_REMOVAL = "Mess Removal";
	public static final String[] MESS_ALLOCATION_HEADER_DATA = { "Student ID *", "Mess Head *", "From Date <YYYY-MM-DD> *", "To Date <YYYY-MM-DD> *" };
	public static final String[] MESS_CHANGE_HEADER_DATA = { "Student ID *", "New Mess Head *", "Effective From Date <YYYY-MM-DD> *", "Effective To Date <YYYY-MM-DD> *" };
	public static final String[] MESS_REMOVE_HEADER_DATA = { "Student ID *", "Effective Till Date <YYYY-MM-DD> *", "Description" };
	public static final String[] MESS_ALLOCATION_HEADER_DATA_WIDTH = { "7000", "8000", "5000", "5000" };
	public static final String[] MESS_REMOVE_HEADER_DATA_WIDTH = { "7000", "5000", "7000" };

	public static final String STUDENT_CREDIT_DEBIT_FILENAME = "attachment; filename=Student Credit Debit Template.xlsx";
	public static final String STUDENT_CREDIT_DEBIT = "Student Credit Debit Details";
	public static final String[] STUDENT_CREDIT_DEBIT_HEADER_DATA = { "Student ID *", "Amount *", "Description" };
	public static final String[] STUDENT_CREDIT_DEBIT_HEADER_DATA_WIDTH = { "7000", "7000", "7000" };

	public static final String STUDENT_DEMAND_FILENAME = "attachment; filename=Student Demand Template.xlsx";
	public static final String STUDENT_DEMANDS = "Student Demands";
	public static final String[] STUDENT_DEMAND_HEADER_DATA = { "Student ID *", "Hostel Name" };
	public static final String[] STUDENT_DEMAND_HEADER_DATA_WIDTH = { "7000", "7000" };

	// Establishment Debit
	public static final String ESTABLISHMENT_DEBIT_FILENAME = "attachment; filename=EstablishmentBulkUpload.xlsx";
	public static final String[] ESTABLISHMENT_DEBIT_DATA = { "Student Id *", "Amount *" };
	public static final String[] ESTABLISHMENT_DEBIT_DATA_WIDTH = { "6500", "4500" };

	/* Upload Receipts */
	/* IFPP_RECEIPT */
	public static final String IFPP_RECEIPT = "attachment; filename=IFPPReceipt.xlsx";
	public static final String[] IFPP_RECEIPT_DATA = { "Bank Reference No *", "Transaction Date *", "Amount *", "Roll Number *" };
	public static final String[] IFPP_RECEIPT_DATA_WIDTH = { "6500", "6500", "4500", "4500" };

	/* LOAN_RECEIPT */
	public static final String LOAN_RECEIPT = "attachment; filename=LoanReceipt.xlsx";
	public static final String[] LOAN_RECEIPT_DATA = { "Sl No. *", "Name *", "Roll No. *", "Room No. *", "Hostel *", "Account No. *", "CCW Fee *" };
	public static final String[] LOAN_RECEIPT_DATA_WIDTH = { "4500", "4500", "4500", "4500", "4500", "4500", "4500" };

	/* SUBSIDY_RECEIPT */
	public static final String SUBSIDY_RECEIPT = "attachment; filename=SubsidyReceipt.xlsx";
	public static final String[] SUBSIDY_RECEIPT_DATA = { "Sl No. *", "Name *", "Roll No. *", "Sanction & Claim Period *", "Amount (Rs.) *" };
	public static final String[] SUBSIDY_RECEIPT_DATA_WIDTH = { "4500", "4500", "4500", "7500", "4500" };

	/* MESS_BILLING */
	public static final String MESS_BILLING = "attachment; filename=MessBilling.xlsx";
	public static final String[] MESS_BILLING_DATA = { "Sl No. *", "Name *", "Roll No. *", "Basic *" };
	public static final String[] MESS_BILLING_DATA_WIDTH = { "4500", "4500", "4500", "4500", "4500" };

	/* SCHOLARSHIP */
	public static final String SCHOLARSHIP = "attachment; filename=Scholarship.xlsx";
	public static final String[] SCHOLARSHIP_DATA = { "Sl No. *", "Roll No. *", "Name *", "Rebate Days *", "Rebate Amt. *" };
	public static final String[] SCHOLARSHIP_DATA_WIDTH = { "6500", "4500", "6500", "6500", "4500" };

	/* MESS_REBATE */
	public static final String MESS_REBATE = "attachment; filename=MessRebate.xlsx";
	public static final String[] MESS_REBATE_DATA = { "Sl No. *", "Roll No. *", "Name *", "Rebate Days *", "Rebate Amt. *" };
	public static final String[] MESS_REBATE_DATA_WIDTH = { "6500", "4500", "6500", "6500", "4500" };

	/* DAYS_SCHOLAR */
	public static final String DAYS_SCHOLAR = "attachment; filename=DaysScholar.xlsx";
	public static final String[] DAYS_SCHOLAR_DATA = { "Bank Reference No *", "Transaction Date *", "Amount *", "Roll No. *", "Fee for Semester *", "Year *" };
	public static final String[] DAYS_SCHOLAR_DATA_WIDTH = { "6500", "6500", "4500", "4500", "7500", "4500" };

	// Mess Ledger report
	public static final String MESS_LEDGER_FILE_NAME = "attachment; filename=Mess Ledger Report.xlsx";
	public static final String MESS_LEDGER_REPORT = "Mess Ledger Report";
	public static final String[] MESS_LEDGER_REPORT_DATA = {"Voucher Number", "Voucher Date", "Description", "Ref No", "Credit Amount(Cr) ₹", "Debit Amount(Dr) ₹", "Closing Balance ₹"};
	public static final List<String> MESS_LEDGER_REPORT_EXCEL_HEADER = List.of("Office of the Hostel Management", "IIT MADRAS CAMPUS", "Student Ledger");
	public static final int[] MESS_LEDGER_REPORT_DATA_WIDTH = {5000, 4000, 12000, 4000, 6000, 6000, 6000};
	/* STUDENT_BALANCE_REPORT */
	public static final String STUDENT_BALANCE_REPORT_SHEET_NAME = "Student Balance Report";
	public static final String STUDENT_BALANCE_REPORT = "hostel/student/balance/report";
	public static final String[] STUDENT_BALANCE_REPORT_HEADER_DATA = { "Student ID", "Student Name", "Hostel Name", "Room Number", "Net Balance", "Cr/Dr" };
	public static final String[] STUDENT_BALANCE_REPORT_HEADER_DATA_WIDTH = { "7000", "8000", "6000", "3000", "5000" , "3000" };
	/* General Ledger */
	public static final String GENERAL_LEDGER_FILENAME = "attachment; filename=General Ledger Report.xlsx";
	public static final String[] MESS_CARD_HEADER_DATA = { "Voucher Date", "Voucher No", "Description", "Hostel Name",
			"Room No", "Ref. Account", "Student Name", "Ref. SubAccount", "Debit", "Credit", "Closing Balance" };
	public static final String[] MESS_CARD_HEADER_DATA_WIDTH = { "5000", "5000", "7000", "6000", "3000", "5000", "7000",
			"5000", "5000", "5000", "5000" };
	public static final String[] TEMP_ACCOMMODATION_HEADER_DATA = { "Voucher Date", "Candidate Name", "Mess Name",
			"Card #", "Breakfast", "Lunch", "Dinner", "Payable Count" };
	public static final String[] TEMP_ACCOMMODATION_HEADER_DATA_WIDTH = { "5000", "7000", "7000", "5000", "2000",
			"2000", "2000", "2000" };
	public static final String[] GUEST_COUPON_HEADER_DATA = { "Voucher Date", "Candidate Name", "Mess Name", "Coupon #",
			"Breakfast", "Lunch", "Dinner", "Snacks" };
	public static final String[] GUEST_COUPON_HEADER_DATA_WIDTH = { "5000", "7000", "7000", "5000", "2000", "2000",
			"2000", "2000" };
	/* Mess Bill Summary Reports */
	public static final String MESS_BILL_SUMMARY_FILENAME = "attachment; filename=Mess Bill Summary Report.xlsx";
	public static final String[] MESS_BILL_SUMMARY_HEADER_DATA = { "Sl No.", "Student Id", "Student Name",
			"Dining From Date", "Dining To Date", "Dining period Total days",
			"Rebate (Total No of days for the messing cycle)", "Mess Change", "Date of Vacating",
			"Total No of days dinned", "Rate Per Day", "Amount", "GST", "Total Payable" };
	public static final String[] MESS_BILL_SUMMARY_HEADER_DATA_WIDTH = { "3000", "7000", "7000", "7000", "7000", "7000",
			"9000", "7000", "5000", "5000", "5000", "6000", "6000", "7000" };

	/* Hostel Accommodation/Rebate Request */
	public static final String LIST_OF_STUDENTS = "attachment; filename=List of Students.xlsx";
	public static final String OTHER_CANDIDATE = "attachment; filename=Other Candidate.xlsx";
	public static final String MESS_REBATE_LIST = "attachment; filename=MessRebateList.xlsx";

	/* Vacating Students Report */
	public static final String VACATING_STUDENTS_REPORT = "Vacating Students Report";
	public static final String TYPE_VACATING_STUDENTS_REPORT = "vacatingStudentReport";
	public static final String TYPE_PENALTY_REPORT = "penaltyReport";
	public static final String TYPE_DONATION_REPORT = "donationReport";
	public static final String TYPE_VACATING_STUDENTS_SUMMARY_REPORT = "vacatingStudentSummaryReport";
	public static final List<String> VACATING_STUDENTS_REPORT_EXCEL_HEADER = List.of("Office of the Hostel Management", "IIT MADRAS CAMPUS", "Vacating Students");

	public static final List<String> VACATING_STUDENTS_REPORT_HEADER = List.of("S.NO", "Name of the Student", "Roll No", "Total Collection", "Mess Dues", "Balance", "Bank Name", "A/C No", "Account Name", "IFSC Code", "Branch", "Vacating Date", "Vacating Reason", "Exchange Period From Date", "Exchange Period To Date", "Place of visit", "Donationstatus", "Donator Type", "Donation Amount");
	public static final List<Integer> VACATING_STUDENTS_REPORT_DATA_WIDTH = List.of(3000, 7000, 6000, 5000, 5000, 5000, 7000, 6000, 7000, 6000, 6000, 5000, 7000, 7000, 7000, 7000, 5000, 6000, 5000);

	public static final List<String> PENALTY_REPORT_HEADER = List.of("Student Id", "Penalty Amount", "Penalty Reason", "Hostel Name");
	public static final List<Integer> PENALTY_REPORT_DATA_WIDTH = List.of(5000, 4000, 12000, 4000);

	public static final List<String> DONATION_REPORT_HEADER = List.of("Student Id", "Donation Amount", "Donation To", "Hostel Name");
	public static final List<Integer> DONATION_REPORT_DATA_WIDTH = List.of(5000, 4000, 12000, 4000);

	public static final List<String> VACATING_STUDENTS_SUMMARY_REPORT_HEADER = List.of(
			"Transaction Reference (16 Characters - Unique for each file)", "Student Id", "Total Collection", "Mess Dues",
			"Remitter Account Number (35 Characters)", "Remitter Account Name (50 Characters - As per CBS)",
			"Remitter Address 1 (35 Characters)", "Remitter Address 2 (35 Characters)",
			"Remitter Address 3 (35 Characters)", "Remitter Address 4 (35 Characters)",
			"Sender Account Type (2 Characters)", "Transaction Amount (Without Comma Separator)",
			"Charges", "Beneficiary Name (50 Characters)", "Beneficiary Account (35 Characters)",
			"Beneficiary Account Type (2 Characters)", "Beneficiary Roll No line 1 (35 Characters)",
			"Beneficiary address line 2 (35 Characters)", "Beneficiary IFSC (11 Characters) ( Always in Capital letter)",
			"Details of payment (35 Characters)", "Sender to receiver code (5 Characters)",
			"Customer Mobile (10 Numeric)(or) Email Id (Must contain @ and . )",
			"Sender Receiver Info 1 (35 Characters)", "Sender Receiver Info 2 (35 Characters)",
			"Sender Receiver Info 3 (35 Characters)", "Sender Receiver Info 4 (35 Characters)",
			"Sender Receiver Info 5 (35 Characters)", "Sender Receiver Info 6 (35 Characters)"
	);
	public static final List<Integer> VACATING_STUDENTS_SUMMARY_REPORT_DATA_WIDTH = List.of(
			8000, 5000, 5000, 7000, 10000,
			8000, 8000, 8000, 8000, 4000, 6000, 4000,
			9000, 8000, 4000, 7000, 7000, 9000, 7000,
			6000, 10000, 7000, 7000, 7000, 7000, 7000, 7000
	);
}
