package com.iitm.hosteldine.form.common;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.iitm.hosteldine.config.DynamicSecurityService;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.MenuListDto;
import com.iitm.hosteldine.util.MCrypt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

@Data
public class HeaderForm {
	private String requestURL;
	private String contextPath;
	private String currentURL;
	private String menuHeading;
	private Boolean needAddNew;
	private Boolean needUpdate;
	private Boolean needBack;
	private boolean needAdditionalButton;
	private String additionalButtonColor;
	private String additionalButtonIcon;
	private String additionalButtonLabel;
	private boolean needAdditionalButton2;
	private String additionalButtonColor2;
	private String additionalButtonIcon2;
	private String additionalButtonLabel2;
	private ArrayList<HeaderMenu> breadCrumbs;
	private List<MenuListDto> dashboardList;
	private List<MenuListDto> menuList;
	private List<MenuListDto> tabList;
	private List<MenuListDto> reportList;
	private String loginType;

	public static HeaderForm defaultForm(HttpServletRequest request, DynamicSecurityService dss) {
		String requestURL = request.getRequestURL().toString();
		String contextPath = request.getContextPath();
		String currentURL = requestURL.substring(requestURL.indexOf(contextPath)).replace(contextPath, "");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ArrayList<HeaderMenu> breadCrumbs = getBreadCrumbs(currentURL, dss, auth, new ArrayList<>());
		if (!breadCrumbs.isEmpty()) {
			Enumeration<String> enumeration = request.getParameterNames();
			if (enumeration.hasMoreElements()) {
				String urlParam = currentURL + Constants.QUERY_SEPARATOR;
				while (enumeration.hasMoreElements()) {
					String parameterName = String.valueOf(enumeration.nextElement());
					urlParam += Constants.PARAM_SEPARATOR + parameterName + Constants.ASSIGNMENT_OPERATOR
							+ request.getParameter(parameterName);
				}
				breadCrumbs.add(
						new HeaderMenu().setMenuHeading(getMenuHeading(currentURL, dss, auth)).setMenuUrl(urlParam));
			} else {
				breadCrumbs.add(
						new HeaderMenu().setMenuHeading(getMenuHeading(currentURL, dss, auth)).setMenuUrl(currentURL));
			}
		}

		return new HeaderForm().setRequestURL(requestURL).setContextPath(contextPath).setCurrentURL(currentURL)
				.setLoginType(getLoginType()).setBreadCrumbs(breadCrumbs)
				.setMenuHeading(breadCrumbs.isEmpty() ? "" : breadCrumbs.getLast().getMenuHeading())
				.setNeedAddNew(getNeedAddNew(currentURL)).setNeedUpdate(getNeedUpdate(currentURL))
				.setNeedBack(getNeedBack(currentURL));
	}

	private static ArrayList<HeaderMenu> getBreadCrumbs(String currentURL, DynamicSecurityService dss,
			Authentication auth, ArrayList<HeaderMenu> breadCrumbs) {
//        if (!currentURL.isEmpty()) {
//            if (breadCrumbs.isEmpty() && !"/index".equals(currentURL)) {
//                breadCrumbs.add(new HeaderMenu().setMenuHeading("Dashboard").setMenuUrl("/index"));
//            }
//            MenuListDto menu = dss.getMenuForRoleByUrl(auth, currentURL);
//            if (menu != null) {
//                breadCrumbs.add(new HeaderMenu().setMenuHeading(menu.getMenuHeading()).setMenuUrl(menu.getUrlPath()));
//            } else {
//                breadCrumbs = (getBreadCrumbs(currentURL.substring(0, currentURL.lastIndexOf("/")), dss, auth, breadCrumbs));
//            }
//        }
//        return breadCrumbs;
		return getBreadCrumbs(currentURL);
	}

	private static ArrayList<HeaderMenu> getBreadCrumbs(String currentURL) {
		ArrayList<HeaderMenu> breadCrumbs = new ArrayList<>();
		if (!"/index".equals(currentURL)) {
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Dashboard").setMenuUrl("/index"));
		}

		return switch (currentURL) {
		// Admin Settings
		case "/collegeInfo", "/courseMaster", "/externalLoginStudentDetails", "/simsConfig", "/roleMenuPrivilege",
				"/menuMaster", "/mailManagement":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Admin Settings").setMenuUrl("/collegeInfo"));
			yield breadCrumbs;
		// Caterer Information
		case "/vendorMaster":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Caterer").setMenuUrl("/vendorMaster"));
			yield breadCrumbs;
		// Staff Information
		case "/staffDetails", "/staffSearch":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Staff").setMenuUrl("/staffSearch"));
			yield breadCrumbs;
		// Student Information
		case "/studentBulkUpload", "/adminStudentBioData", "/studentWithRemarks", "/commonSearch",
				"/adminStudentBioData/update", "/studentWellnessArchive":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Student").setMenuUrl("/commonSearch"));
			if (currentURL.equals("/adminStudentBioData/update")) {
				breadCrumbs.add(new HeaderMenu().setMenuHeading("Student Bio-Data").setMenuUrl("/adminStudentBioData"));
			}
			yield breadCrumbs;
		// Hostel Information
		case "/hostelMaster", "/floorMaster", "/hostelRoomInfo", "/bulkRoomConfig", "/workflowMaster",
				"/assetConfiguration", "/hostelIndividualAllotment", "/bulkAllotmentStudents", "/roomAllotmentLogs", "/guestAllotmentGUI":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Hostel Information").setMenuUrl("/hostelMaster"));
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Hostel").setMenuUrl("/hostelMaster"));
			yield breadCrumbs;
		case "/feedback", "/accountHead", "/hostelEventMaster", "/showWiseDetails", "/showSeatDetails", 
				"/catererLedgerMapping", "/hostelUserMapping", "/messRegistrationMapping", "/mailTemplate",  
				"/tabPrivilege", "/messCouponUserMapping", "/studentComplaintConfig", "/hostelEnrollmentConfig":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Hostel Information").setMenuUrl("/hostelMaster"));
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Master Screens").setMenuUrl("/feedback"));
			yield breadCrumbs;
		case "/uploadReceipts", "/receiptEntry", "/receiptUpdate", "/journalVoucher", "/checkerApproval",
				"/nocVacationPayment", "/studentDemandUpload", "/studentRollNoChange", "/studentCreditDebitUpload", 
				"/messCardRequest", "/establishmentDebit" :
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Hostel Information").setMenuUrl("/hostelMaster"));
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Transaction Screens").setMenuUrl("/uploadReceipts"));
			yield breadCrumbs;
		case "/temporaryAccommodationConfig", "/tempAccomodationList", "/wardenInfo", "/guestAccommodationCharges",
				"/guestCouponConfig", "/guestCouponRequest", "/wardenAwayDetails", "/guestIssuedCoupons", 
				"/tempAccomodationList/view":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Hostel Information").setMenuUrl("/hostelMaster"));
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Warden / Accommodation").setMenuUrl("/temporaryAccommodationConfig"));
			yield breadCrumbs;
		case "/roomInventory", "/bulkRoomInventory", "/roomInventoryReplacement" :
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Hostel Information").setMenuUrl("/hostelMaster"));
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Room Inventory").setMenuUrl("/roomInventory"));
			yield breadCrumbs;
		case "/generalLogReport","/studentWiseLogReport","/studentBiodataListReport","/messLedgerReport",
			 "/facultyAccommodationRequest","/showWiseReport","/generalLedger","/studentRollNoHistory",
			 "/regularStudentCheckIn", "/currentHostelVacancyStatus", "/accountHeadSummaryReport",
			 "/studentVacating", "/lateNightEntriesReport", "/sickFoodRequestListReport":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Hostel Information").setMenuUrl("/hostelMaster"));
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Reports").setMenuUrl("/generalLogReport"));
			yield breadCrumbs;
		// Mess Information
		case "/messMaster", "/messPeriodConfig", "/messTerminal", "/messVendorAllocation", "/messSession":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Mess").setMenuUrl("/messMaster"));
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Mess Master Screens").setMenuUrl("/messMaster"));
			yield breadCrumbs;
		case "/messCapacity", "/messAllottedList", "/messChangeRequests", "/studentsMessAllocation":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Mess").setMenuUrl("/messMaster"));
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Mess Settings").setMenuUrl("/messCapacity"));
			yield breadCrumbs;
		case "/messBillSummary" , "/foodCourtPurchase" , "/messDineSummary" , "/messAllottedDine", "/notDineStudent" :
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Mess").setMenuUrl("/messMaster"));
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Mess Reports").setMenuUrl("/messBillSummary"));
			yield breadCrumbs;
		case "/foodCourtCreditDebit" :
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Mess").setMenuUrl("/messMaster"));
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Mess Transaction Screens").setMenuUrl("/foodCourtCreditDebit"));
			yield breadCrumbs;
		case "/otherCandidate":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Candidate Profile Information").setMenuUrl("/otherCandidate"));
			yield breadCrumbs;
		case "/accommodationRequest":
		case "/stayExtensionRequest":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Accommodation Request").setMenuUrl("/accommodationRequest"));
			yield breadCrumbs;
		case "/guestAccommodationRequest/type/guest-room/view-room-details":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Guest Accommodation Request").setMenuUrl("/guestAccommodationRequest/type/guest-room"));
			yield breadCrumbs;
		case "/hdcComplaint/new":
		case "/hdcComplaint/view":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("HDC Complaint List").setMenuUrl("/hdcComplaint"));
			yield breadCrumbs;
		case "/hdcComplaint/details":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("HDC Complaint View").setMenuUrl("/hdcComplaint/details"));
			yield breadCrumbs;
		case "/studentMessFeedbackReport":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Mess Information").setMenuUrl("/messMaster"));
			yield breadCrumbs;
		case "/studentMessRegistrationReport":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Mess Information").setMenuUrl("/messMaster"));
			yield breadCrumbs;
		case "/hostelAccommodationRebateRequest":
			breadCrumbs
					.add(new HeaderMenu().setMenuHeading("Hostel Accommodation/Rebate Request Report").setMenuUrl(""));
		case "/hostelNightCouponReport":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Hostel Information").setMenuUrl("/hostelMaster"));
			yield breadCrumbs;
		case "/studentVacating/adminReport":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Vacating Student Report").setMenuUrl(""));
			yield breadCrumbs;
		case "/otherCandidatePayment":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Other Candidate Payment").setMenuUrl(""));
			yield breadCrumbs;
		case "/hdcComplaint/report":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("HDC Complaint Report").setMenuUrl(""));
			yield breadCrumbs;
		case "/frDashboard", "/liveStatus", "/frPull", "/frPush", "/frClear", "/messDetails", "/frScheduler", "/history", "/syncData":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Biometric & FR").setMenuUrl("/frDashboard"));
			yield breadCrumbs;
		case "/accommodationMessConvocation":
			breadCrumbs.add(new HeaderMenu().setMenuHeading("Convocation").setMenuUrl("/accommodationMessConvocation"));
			yield breadCrumbs;
//            case "/candidateList":
//            	breadCrumbs.add(new HeaderMenu().setMenuHeading("Other Candidates").setMenuUrl("/candidateList"));
//                yield breadCrumbs;

		default:
			if (!currentURL.isEmpty()) {
				yield getBreadCrumbs(currentURL.substring(0, currentURL.lastIndexOf("/")));
			} else
				yield breadCrumbs;
		};
	}

	private static Boolean getNeedAddNew(String currentURL) {
		return switch (currentURL) {
		case "/courseMaster", "/hostelMaster", "/floorMaster", "/hostelRoomInfo", "/designationMaster",
				"/assetConfiguration", "/messMaster", "/vendorMaster", "/messSession", "/messTerminal",
				"/roomInventory", "/guestAccommodationCharges", "/hostelEventMaster", "/showWiseDetails",
				"/showSeatDetails", "/scholarsStayExtension", "/hostelPayment", "/messToCardTransfer",
				"/sickFoodRequest", "/studentComplaintForm", "/studentComplaintConfig", "/faq", "/simsConfig",
				"/feedback", "/workflowMaster", "/mailTemplate", "/accountHead", "/studentBulkUpload",
				"/messPeriodConfig", "/studentWithRemarks", "/menuMaster", "/accommodationRequest",
				"/guestCouponConfig", "/messVendorAllocation", "/guestIssuedCoupons", "/messCardRequest",
				"/studentWellnessArchive", "/guestCouponRequest", "/studentVacating/roomInventory", "/messAllottedList",
				"/foodCourtCreditDebit", "/wardenInfo", "/temporaryAccommodationConfig", "/journalVoucher":
			yield true;
		default:
			if (!currentURL.isEmpty()) {
				yield getNeedAddNew(currentURL.substring(0, currentURL.lastIndexOf("/")));
			} else
				yield false;
		};
	}

	private static String getMenuHeading(String currentURL, DynamicSecurityService dss, Authentication auth) {
		String heading = "";
		MenuListDto menu = dss.getMenuForRoleByUrl(auth, currentURL);
		if (menu != null) {
			heading = menu.getMenuHeading();
			if (heading.isEmpty()) {
				heading = getMenuHeading(currentURL);
			}
		} else {
			heading = getMenuHeading(currentURL);
		}
		return heading;
	}

	private static String getMenuHeading(String currentURL) {

		return switch (currentURL) {
		case "/studentRegistration/getDetailsById":
			yield "Bio-Data";
		case "/courseMaster":
			yield "Course Master";
		case "/studentBulkUpload":
			yield "Student Bulk Upload";
		case "/hostelMaster":
			yield "Hostel Master";
		case "/floorMaster":
			yield "Floor Master";
		case "/hostelRoomInfo":
			yield "Room Configuration";
		case "/bulkRoomConfig":
			yield "Bulk Room Configuration";
		case "/index":
			yield "Dashboard";
		case "/staffDetails":
			yield "Staff Personal Details";
		case "/checkerApproval":
			yield "Checker Approval";
		case "/designationMaster":
			yield "Designation Master";
		case "/assetConfiguration":
			yield "Asset Configuration";
		case "/messMaster":
			yield "Mess Master";
		case "/vendorMaster":
			yield "Vendor Master";
		case "/messSession":
			yield "Mess Session";
		case "/messTerminal":
			yield "Mess Terminal";
		case "/hostelUserMapping":
			yield "Hostel User Mapping";
		case "/staffSearch":
			yield "Staff Search";
		case "/bulkRoomInventory":
			yield "Bulk Room Inventory";
		case "/messCapacity":
			yield "Mess Capacity";
		case "/roomInventory":
			yield "Room Inventory";
		case "/guestAccommodationCharges":
			yield "Guest Accommodation Charges";
		case "/hostelEventMaster":
			yield "Show Event Master";
		case "/showWiseDetails":
			yield "Show Wise Details";
		case "/showSeatDetails":
			yield "Show Seat Details";
		case "/hostelEnrollmentConfig":
			yield "Hostel Enrollment Configuration";
		case "/scholarsStayExtension":
			yield "MS/PHD Scholar Stay Extension";
		case "/showAction":
			yield "Professional Shows Tickets and T-Shirts";
		case "/getMessPriorityList":
			yield "Mess Registration";
		case "/messFeedback":
			yield "Mess Feedback";
		case "/hostelPayment":
			yield "Hostel Payment";
		case "/messToCardTransfer":
			yield "Mess To Card Account Transfer";
		case "/sickFoodRequest":
			yield "Sick Food Request";
		case "/studentComplaintForm":
			yield "Student Complaint Form";
		case "/studentComplaintConfig":
			yield "Student Complaint Configuration";
		case "/priorityMess":
			yield "Mess Registration";
		case "/externalLoginStudentDetails":
			yield "External Other Candidate Login Details";
		case "/faq":
			yield "FAQ";
		case "/simsConfig":
			yield "Sims Config";
		case "/workflowMaster":
			yield "Hostel Workflow Master";
		case "/mailTemplate":
			yield "Mail Template";
		case "/feedback":
			yield "Feedback Question";
		case "/messCouponUserMapping":
			yield "Mess Coupon User Mapping";
		case "/catererLedgerMapping":
			yield "Caterer Ledger Mapping";
		case "/messRegistrationMapping":
			yield "Mess Registration Configuration";
		case "/collegeInfo":
			yield "College Information";
		case "/accountHead":
			yield "Account Head";
		case "/messPeriodConfig":
			yield "Mess Period Config";
		case "/commonSearch":
			yield "Common Search";
		case "/studentWithRemarks":
			yield "Student With Remarks";
		case "/menuMaster":
			yield "Menu Master";
		case "/mailManagement":
			yield "Mail Management";
		case "/roleMenuPrivilege":
			yield "Role Menu Privilege";
		case "/accommodationRequest":
			yield "Accommodation Request";
		case "/guestCouponConfig":
			yield "Guest Coupon Config";
		case "/guestAccommodationRequest":
		case "/guestAccommodationRequest/type":
			yield "Guest Accommodation Request";
		case "/guestAccommodationRequest/type/guest-room/view-room-details":
			yield "View Guest Accommodation Request";
		case "/messVendorAllocation":
			yield "Mess / Vendor Allocation";
		case "/tabPrivilege":
			yield "Tab Privileges";
		case "/adminStudentBioData":
			yield "Student Bio-Data";
		case "/adminStudentBioData/update":
			yield "Student Bio-Data Form";
		case "/roomInventoryReplacement":
			yield "Room Inventory Replacement";
		case "/otherCandidate":
			yield "Candidate Profile Information";
		case "/bulkAllotmentStudents":
			yield "Bulk Allotment - Students";
		case "/stayExtensionRequest":
			yield "Stay Extension Request";
		case "/guestIssuedCoupons":
			yield "Guest Coupon Issued List";
		case "/messCardRequest":
			yield "Mess To Card Request List";
		case "/studentVacating":
			yield "Student Vacating";
		case "/studentWellnessArchive":
			yield "Wellness Archive";
		case "/hostelIndividualAllotment":
			yield "Hostel Individual Allotment (GUI)";
		case "/studentWellnessArchive/students":
			yield "Add / Edit Wellness Archive";
		case "/studentWellnessArchive/others":
			yield "Add / Edit Wellness Archive";
		case "/studentWellnessArchive/wellnessDetails":
			yield "View Wellness Archive Details";
		case "/guestCouponRequest":
			yield "Guest Coupon Request List";
		case "/guestCouponRequest/view":
			yield "Guest Coupon Request View";
		case "/candidateList":
			yield "Other Candidate";
		case "/deanOtherCandidateRequests":
			yield "Other Candidate Accommodation";
		case "/messRebateList/view":
			yield "Rebate View";
		case "/studentAccommodationRequest/view":
			yield "IIT-M-Students View";
		case "/studentStayExtension/view":
			yield "IITM Stud - Stay Ext View";
		case "/scholars/view":
			yield "Scholars";
		case "/public/studentAccommodationRequest/view":
			yield "Hostel Accommodation or Scholar Stay Ext View";
		case "/messAllottedList":
			yield "Mess Allotted List";
		case "/studentsMessAllocation":
			yield "Students Mess Allocation";
		case "/foodCourtCreditDebit":
			yield "Food Court Credit/Debit";
		case "/messChangeRequests":
			yield "Mess Change Requests";
		case "/summaryPage":
			yield "Summary Page";
		case "/studentCreditDebitUpload":
			yield "Student Credit Debit Upload";
		case "/receiptEntry":
			yield "Receipt Entry";
		case "/receiptUpdate":
			yield "Receipt Update";
		case "/temporaryAccommodationConfig":
			yield "Temporary Accommodation Config";
		case "/temporaryAccommodationPaymentDetails":
			yield "Temporary Accommodation Online Payment List";
		case "/establishmentDebit":
			yield "Establishment Debits";
		case "/studentDemandUpload":
			yield "Student Demand Upload";
		case "/uploadReceipts":
			yield "Upload Receipts";
		case "/regularStudentCheckIn":
			yield "Current Hostel Allocation Status";
		case "/journalVoucher":
			yield "Journal Voucher";
		case "/currentHostelVacancyStatus":
			yield "Current Hostel Vacancy Status";
		case "/studentMessRegistrationReport":
			yield "Student Mess Registration Report";
		case "/lateNightEntriesReport":
			yield "Late Night Entries Report";
		case "/studentRollNoHistory":
			yield "Student Roll No. History";
		case "/generalLedger":
			yield "General Ledger";
		case "/studentRollNoChange":
			yield "Student Roll No Change";
		case "/messBillSummary":
			yield "Mess Bill Summary Report";
		case "/hostelAccommodationRebateRequest":
			yield "Hostel Accommodation/Rebate Request Report";
		case "/hostelNightCouponReport":
			yield "Hostel Night Coupon Report";
		case "/generalLogReport":
			yield "General Log Report";
		case "/studentVacating/adminReport":
			yield "Vacating Student Report";
		case "/otherCandidatePayment":
			yield "Other Candidate Payment";
		case "/hdcComplaint/new":
			yield "Add HDC Complaint";
		case "/hdcComplaint/view":
			yield "View HDC Complaint";
		case "/hdcComplaint/report":
			yield "HDC Complaint Report";
		case "/tempAccomodationList/view":
			yield "Temp Accom List in create/Edit and view";
		case "/accommodationMessConvocation":
			yield "Convocation";
		default:
			if (!currentURL.isEmpty()) {
				yield getMenuHeading(currentURL.substring(0, currentURL.lastIndexOf("/")));
			} else
				yield "";
		};
	}

	private static Boolean getNeedUpdate(String currentURL) {
		return switch (currentURL) {
		case "/messCapacity", "/staffDetails", "/collegeInfo", "/commonSearch", "/roleMenuPrivilege",
				"/checkerApproval", "/messBillSummary", "/studentBulkUpload", "/adminStudentBioData/update",
				"/messCardRequest", "/studentWellnessArchive", "/guestAccommodationRequest":
			yield true;
		default:
			if (!currentURL.isEmpty()) {
				yield getNeedUpdate(currentURL.substring(0, currentURL.lastIndexOf("/")));
			} else
				yield false;
		};
	}

	private static Boolean getNeedBack(String currentURL) {
		return !(currentURL.equals("/index") || currentURL.equals("/dashboard/student")
				|| currentURL.startsWith("/otherCandidate"));
	}

	private static String getLoginType() {
		try {
			return MCrypt.getInstance().encryptToText(ModelConstants.STUDENT_LOGIN_TYPE);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public HeaderForm setAdditionalButtonProperties(boolean needAdditionalButton, String additionalButtonColor,
			String additionalButtonLabel, String additionalButtonIcon) {
		this.needAdditionalButton = needAdditionalButton;
		this.additionalButtonColor = additionalButtonColor;
		this.additionalButtonLabel = additionalButtonLabel;
		this.additionalButtonIcon = additionalButtonIcon;
		return this;
	}

	public HeaderForm setAdditionalButton2Properties(boolean needAdditionalButton2, String additionalButtonColor2,
			String additionalButtonLabel2, String additionalButtonIcon2) {
		this.needAdditionalButton2 = needAdditionalButton2;
		this.additionalButtonColor2 = additionalButtonColor2;
		this.additionalButtonLabel2 = additionalButtonLabel2;
		this.additionalButtonIcon2 = additionalButtonIcon2;
		return this;
	}

	private HeaderForm setLoginType(String loginType) {
		this.loginType = loginType;
		return this;
	}

	public HeaderForm setBreadCrumbs(ArrayList<HeaderMenu> breadCrumbs) {
		this.breadCrumbs = breadCrumbs;
		return this;
	}

	public HeaderForm setNeedAddNew(Boolean needAddNew) {
		this.needAddNew = needAddNew;
		return this;
	}

	public HeaderForm setRequestURL(String requestURL) {
		this.requestURL = requestURL;
		return this;
	}

	public HeaderForm setContextPath(String contextPath) {
		this.contextPath = contextPath;
		return this;
	}

	public HeaderForm setCurrentURL(String currentURL) {
		this.currentURL = currentURL;
		return this;
	}

	public HeaderForm setMenuHeading(String menuHeading) {
		this.menuHeading = menuHeading;
		return this;
	}

	public HeaderForm setNeedUpdate(Boolean needUpdate) {
		this.needUpdate = needUpdate;
		return this;
	}

	public HeaderForm setMenuList(List<MenuListDto> menuList) {
		this.menuList = menuList;
		return this;
	}

	public HeaderForm setDashboardList(List<MenuListDto> dashboardList) {
		this.dashboardList = dashboardList;
		return this;
	}

	public HeaderForm setTabList(List<MenuListDto> tabList) {
		this.tabList = tabList;
		return this;
	}

	public HeaderForm setReportList(List<MenuListDto> reportList) {
		this.reportList = reportList;
		return this;
	}

	public HeaderForm setNeedBack(Boolean needBack) {
		this.needBack = needBack;
		return this;
	}

	public boolean isNeedAdditionalButton() {
		return needAdditionalButton;
	}

	public String getAdditionalButtonColor() {
		return additionalButtonColor;
	}

	public String getAdditionalButtonLabel() {
		return additionalButtonLabel;
	}

	public String getAdditionalButtonIcon() {
		return additionalButtonIcon;
	}
}
