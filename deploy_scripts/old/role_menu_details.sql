delete from schooldev.role_menu_privilege;
select setval('schooldev.role_menu_privilege_id_seq', 1);
delete from schooldev.dashboard_widget_privilege;
select setval('schooldev.dashboard_widget_privilege_id_seq', 1);
delete from schooldev.dashboard_widget_master;
select setval('schooldev.dashboard_widget_master_id_seq', 1);
delete from schooldev."MENU_LIST";
select setval('schooldev.menu_list_menu_id_seq', 1);
-- delete from schooldev.user_role;
delete from schooldev.roles;
select setval('schooldev.roles_role_id_seq', 1);
alter table schooldev."MENU_LIST"
    alter column menu_id set default nextval('schooldev.menu_list_menu_id_seq') ;


INSERT INTO schooldev."MENU_LIST" (menu_order, main_menu_order, sub_menu_order, sub_sub_menu_order, heading, url_path, image_name, menu_type, active_flag, icon_name, banner_name)
VALUES
    (0, 0, 0, 1, 'Dashboard', '/dashboard/module', '', 'dashboard', 'Y','menusIcon', ''),
    (0, 0, 0, 2, 'Student Dashboard', '/dashboard/student', '', 'dashboard', 'Y','studentIcon', ''),
    (0, 0, 0, 3, 'Dean Dashboard', '/dashboard/dean', '', 'dashboard', 'Y','studentIcon', ''),
    (0, 0, 0, 4, 'Other Candidate Dashboard', '/dashboard/other', '', 'dashboard', 'Y','studentIcon', ''),
    (0, 0, 0, 5, 'Academic Staff Dashboard', '/dashboard/staff', '', 'dashboard', 'Y','staffIcon', ''),
    (0, 0, 0, 6, 'ISO Staff Dashboard', '/dashboard/staff', '', 'dashboard', 'Y','staffIcon', ''),

    (0, 0, 1, 1, 'Admin Settings', '/collegeInfo', '01-AdminSettings.svg', 'module', 'Y','ph-devices', 'modules-img-01.webp'),

    (0, 1, 1, 1, 'College Information', '/collegeInfo', '', 'module', 'Y','', ''),
    (0, 1, 1, 2, 'Course Master', '/courseMaster', '', 'module', 'Y','', ''),
    (0, 1, 1, 3, 'Other Candidate Login Details', '/externalLoginStudentDetails', '', 'module', 'Y','', ''),
    (0, 1, 1, 4, 'FAQ', '/faq', '', 'module', 'Y','', ''),
    (0, 1, 1, 5, 'Sims Config', '/simsConfig', '', 'module', 'Y','', ''),
    (0, 1, 1, 6, 'Menu List', '/menuMaster', '', 'module', 'Y','', ''),
    (0, 1, 1, 7, 'Role Menu Privileges', '/roleMenuPrivilege', '', 'module', 'Y','', ''),
    (0, 1, 1, 8, 'Mail Management', '/mailManagement', '', 'module', 'Y','', ''),

    (0, 0, 1, 2, 'Caterer Info', '/catererInfo', '08-Caterer.svg', 'module', 'Y','ph-user-circle', 'modules-img-08.webp'),

    (0, 1, 2, 1, 'Vendor Master', '/vendorMaster', '', 'module', 'Y','', ''),
    (0, 1, 2, 2, 'Caterer Details', '/index', '', 'module', 'Y','', ''),
    (0, 1, 2, 3, 'Mess Employee Details', '/index', '', 'module', 'Y','', ''),
    (0, 1, 2, 4, 'Mess Employee List', '/index', '', 'module', 'Y','', ''),

    (0, 0, 1, 3, 'Staff', '/index', '02-Staff.svg', 'module', 'Y','ph-user-circle', 'modules-img-02.webp'),

    (0, 1, 3, 1, 'Staff Search', '/staffSearch', '', 'module', 'Y','', ''),
    (0, 1, 3, 2, 'New Staff', '/staffDetails/new', '', 'module', 'Y','', ''),

    (0, 0, 1, 4, 'Student', '/commonSearch', '03-Students.svg', 'module', 'Y','ph-student', 'modules-img-03.webp'),
    (0, 1, 4, 1, 'Common Search', '/commonSearch', '', 'module', 'Y','', ''),
    (0, 1, 4, 2, 'Bulk Upload', '/studentBulkUpload', '', 'module', 'Y','', ''),
    (0, 1, 4, 3, 'Student Bio-Data', '/adminStudentBioData', '', 'module', 'Y','', ''),
    (0, 1, 4, 4, 'Students with Remarks', '/studentWithRemarks', '', 'module', 'Y','', ''),
    (0, 1, 4, 5, 'Wellness Report Menu', '/index', '', 'module', 'Y','', ''),

    (0, 0, 1, 5, 'Hostel Information', '/index', '05-Hostel.svg', 'module', 'Y','ph-buildings', 'modules-img-05.webp'),

    (0, 1, 5, 1, 'Hostel', '/index', '', 'module', 'Y','', ''),
    (0, 1, 5, 2, 'Master Screens', '/index', '', 'module', 'Y','', ''),
    (0, 1, 5, 3, 'Transaction Screens', '/index', '', 'module', 'Y','', ''),
    (0, 1, 5, 4, 'Warden / Accommodation', '/index', '', 'module', 'Y','', ''),
    (0, 1, 5, 5, 'Room Inventory', '/index', '', 'module', 'Y','', ''),
    (0, 1, 5, 6, 'Reports', '/index', '', 'module', 'Y','', ''),

    (1, 5, 1, 1, 'Hostel Master', '/hostelMaster', '', 'module', 'Y','', ''),
    (1, 5, 1, 2, 'Floor Master', '/floorMaster', '', 'module', 'Y','', ''),
    (1, 5, 1, 3, 'Room Configuration', '/hostelRoomInfo', '', 'module', 'Y','', ''),
    (1, 5, 1, 4, 'Bulk Room Configuration', '/bulkRoomConfig', '', 'module', 'Y','', ''),
    (1, 5, 1, 5, 'WorkFlow Master', '/workflowMaster', '', 'module', 'Y','', ''),
    (1, 5, 1, 6, 'Asset Configuration', '/assetConfiguration', '', 'module', 'Y','', ''),
    (1, 5, 1, 6, 'Guest Allotment - GUI', '/index', '', 'module', 'Y','', ''),
    (1, 5, 1, 7, 'Individual Allotment - GUI', '/index', '', 'module', 'Y','', ''),
    (1, 5, 1, 8, 'Room Allotment', '/index', '', 'module', 'Y','', ''),
    (1, 5, 1, 9, 'Staff Room Allotment', '/index', '', 'module', 'Y','', ''),
    (1, 5, 1, 10, 'Bulk Allotment - Students', '/bulkAllotmentStudents', '', 'module', 'Y','', ''),
    (1, 5, 1, 11, 'Bulk Allotment - Candidates', '/index', '', 'module', 'Y','', ''),
    (1, 5, 1, 12, 'Room Allotment LOGS', '/index', '', 'module', 'Y','', ''),

    (1, 5, 2, 1, 'HostelWise Verification', '/index', '', 'module', 'Y','', ''),
    (1, 5, 2, 2, 'Opening Balance', '/index', '', 'module', 'Y','', ''),
    (1, 5, 2, 3, 'Item Master', '/index', '', 'module', 'Y','', ''),
    (1, 5, 2, 4, 'Feedback Questions', '/feedback', '', 'module', 'Y','', ''),
    (1, 5, 2, 5, 'Account Head', '/accountHead', '', 'module', 'Y','', ''),
    (1, 5, 2, 6, 'Season Head', '/index', '', 'module', 'Y','', ''),
    (1, 5, 2, 7, 'Show Event Master', '/hostelEventMaster', '', 'module', 'Y','', ''),
    (1, 5, 2, 8, 'Exchange Program Student List', '/index', '', 'module', 'Y','', ''),
    (1, 5, 2, 8, 'Show Wise List', '/showWiseDetails', '', 'module', 'Y','', ''),
    (1, 5, 2, 9, 'Caterer Ledger Mapping', '/catererLedgerMapping', '', 'module', 'Y','', ''),
    (1, 5, 2, 10, 'Hostel User Mapping', '/hostelUserMapping', '', 'module', 'Y','', ''),
    (1, 5, 2, 11, 'Mess Registration Mapping', '/messRegistrationMapping', '', 'module', 'Y','', ''),
    (1, 5, 2, 12, 'Mail Template', '/mailTemplate', '', 'module', 'Y','', ''),
    (1, 5, 2, 13, 'Tab Privileges', '/tabPrivilege', '', 'module', 'Y','', ''),
    (1, 5, 2, 14, 'Mess Coupon User Mapping', '/messCouponUserMapping', '', 'module', 'Y','', ''),
    (1, 5, 2, 15, 'Student Complaint Config', '/studentComplaintConfig', '', 'module', 'Y','', ''),

    (1, 5, 3, 1, 'Upload Receipts', '/index', '', 'module', 'Y','', ''),
    (1, 5, 3, 2, 'Receipt Entry', '/index', '', 'module', 'Y','', ''),
    (1, 5, 3, 3, 'Receipt Update', '/index', '', 'module', 'Y','', ''),
    (1, 5, 3, 4, 'Establishment Debits', '/index', '', 'module', 'Y','', ''),
    (1, 5, 3, 5, 'Journal Voucher', '/index', '', 'module', 'Y','', ''),
    (1, 5, 3, 6, 'Noc/Vacation/Payment', '/index', '', 'module', 'Y','', ''),
    (1, 5, 3, 7, 'Checker Approval', '/index', '', 'module', 'Y','', ''),
    (1, 5, 3, 8, 'SC/ST Claim', '/index', '', 'module', 'Y','', ''),
    (1, 5, 3, 9, 'SC/ST Claim Update', '/index', '', 'module', 'Y','', ''),
    (1, 5, 3, 10, 'Mess Rebate List', '/index', '', 'module', 'Y','', ''),
    (1, 5, 3, 11, 'Student Demands', '/index', '', 'module', 'Y','', ''),
    (1, 5, 3, 12, 'Student Credit/Debit Upload', '/index', '', 'module', 'Y','', ''),
    (1, 5, 3, 13, 'CatererWeeklySettlement', '/index', '', 'module', 'Y','', ''),
    (1, 5, 3, 14, 'Mess to Card Request List', '/messCardRequest', '', 'module', 'Y','', ''),
    (1, 5, 3, 15, 'Yearly Mess Ledger', '/index', '', 'module', 'Y','', ''),

    (1, 5, 4, 1, 'Temp Accom Online Payment List', '/index', '', 'module', 'Y','', ''),
    (1, 5, 4, 2, 'Temp Refund List', '/index', '', 'module', 'Y','', ''),
    (1, 5, 4, 3, 'Temp Accom Config', '/index', '', 'module', 'Y','', ''),
    (1, 5, 4, 4, 'Temp Accom List', '/index', '', 'module', 'Y','', ''),
    (1, 5, 4, 5, 'Warden Info', '/index', '', 'module', 'Y','', ''),
    (1, 5, 4, 6, 'Guest Accommodation Charges', '/guestAccommodationCharges', '', 'module', 'Y','', ''),
    (1, 5, 4, 7, 'Guest Coupon Configuration', '/guestCouponConfig', '', 'module', 'Y','', ''),
    (1, 5, 4, 8, 'Guest Coupon Request List', '/guestCouponRequest', '', 'module', 'Y','', ''),
    (1, 5, 4, 9, 'Warden Away Details', '/index', '', 'module', 'Y','', ''),
    (1, 5, 4, 10, 'Guest Coupon Request Upload', '/index', '', 'module', 'Y','', ''),
    (1, 5, 4, 11, 'Guest Coupon Issued List', '/guestIssuedCoupons', '', 'module', 'Y','', ''),

    (1, 5, 5, 1, 'Room Inventory', '/roomInventory', '', 'module', 'Y','', ''),
    (1, 5, 5, 2, 'Inventory Bulk Upload', '/bulkRoomInventory', '', 'module', 'Y','', ''),
    (1, 5, 5, 3, 'Inventory Replacement', '/roomInventoryReplacement', '', 'module', 'Y','', ''),

    (1, 5, 6, 1, 'General Log Report', '/index', '', 'module', 'Y','', ''),
    (1, 5, 6, 2, 'Late Night Entries Report', '/index', '', 'module', 'Y','', ''),
    (1, 5, 6, 3, 'Student Wise Log Report', '/index', '', 'module', 'Y','', ''),
    (1, 5, 6, 4, 'Student Biodata List', '/index', '', 'module', 'Y','', ''),
    (1, 5, 6, 5, 'Mess LedgerReport', '/index', '', 'module', 'Y','', ''),
    (1, 5, 6, 6, 'General Ledger', '/index', '', 'module', 'Y','', ''),
    (1, 5, 6, 7, 'Hostelwise Ledger', '/index', '', 'module', 'Y','', ''),
    (1, 5, 6, 8, 'Student Balance Ledger', '/index', '', 'module', 'Y','', ''),
    (1, 5, 6, 9, 'Mess Rebate Requests-Day Wise', '/index', '', 'module', 'Y','', ''),
    (1, 5, 6, 10, 'Student RollNo History', '/index', '', 'module', 'Y','', ''),
    (1, 5, 6, 11, 'Current Hostel Allocation Status', '/index', '', 'module', 'Y','', ''),
    (1, 5, 6, 12, 'Current Hostel Vacancy Status', '/index', '', 'module', 'Y','', ''),
    (1, 5, 6, 13, 'Account Head Summary Report', '/index', '', 'module', 'Y','', ''),
    (1, 5, 6, 14, 'Vacating Students List', '/index', '', 'module', 'Y','', ''),
    (1, 5, 6, 15, 'Hostel Vacating Allowed Student', '/index', '', 'module', 'Y','', ''),
    (1, 5, 6, 17, 'Student Roll Number Change', '/index', '', 'module', 'Y','', ''),
    (1, 5, 6, 18, 'Hostel Enrollment Late Fee List', '/index', '', 'module', 'Y','', ''),

    (0, 0, 1, 6, 'Mess', '/index', '06-Mess.svg', 'module', 'Y','ph-fork-knife', 'modules-img-06.webp'),

    (0, 1, 6, 1, 'Mess Master Screens', '/index', '', 'module', 'Y','', ''),
    (0, 1, 6, 2, 'Mess Settings', '/index', '', 'module', 'Y','', ''),
    (0, 1, 6, 3, 'Mess Transaction Screens', '/index', '', 'module', 'Y','', ''),
    (0, 1, 6, 4, 'Mess Reports', '/index', '', 'module', 'Y','', ''),

    (1, 6, 1, 1, 'Mess Period Configuration', '/messPeriodConfig', '', 'module', 'Y','', ''),
    (1, 6, 1, 2, 'Mess Terminal', '/messTerminal', '', 'module', 'Y','', ''),
    (1, 6, 1, 3, 'Mess Capacity Settings', '/messCapacity', '', 'module', 'Y','', ''),

    (1, 6, 2, 1, 'Student Mess Upload', '/index', '', 'module', 'Y','', ''),
    (1, 6, 2, 2, 'Vacation Mess Due List', '/index', '', 'module', 'Y','', ''),
    (1, 6, 2, 3, 'Mess Allotted List', '/index', '', 'module', 'Y','', ''),
    (1, 6, 2, 4, 'Mess Change Requests', '/index', '', 'module', 'Y','', ''),
    (1, 6, 2, 5, 'Mess Due List', '/index', '', 'module', 'Y','', ''),
    (1, 6, 2, 6, 'Students Deactivate', '/index', '', 'module', 'Y','', ''),
    (1, 6, 2, 7, 'Vacation Mess Allotted List', '/index', '', 'module', 'Y','', ''),
    (1, 6, 2, 8, 'Vacation Mess Change Requests', '/index', '', 'module', 'Y','', ''),
    (1, 6, 2, 9, 'Students Mess Allocation', '/index', '', 'module', 'Y','', ''),
    (1, 6, 2, 10, 'Food Court Debit', '/index', '', 'module', 'Y','', ''),
    (1, 6, 2, 11, 'Food Court Credit', '/index', '', 'module', 'Y','', ''),
    (1, 6, 2, 12, 'Student Mess Time Slot Upload', '/index', '', 'module', 'Y','', ''),

    (1, 6, 3, 1, 'Daywise Dined Report', '/index', '', 'module', 'Y','', ''),
    (1, 6, 3, 2, 'Mess Allotted Dined Report', '/index', '', 'module', 'Y','', ''),
    (1, 6, 3, 3, 'Student Mess Attendance', '/index', '', 'module', 'Y','', ''),
    (1, 6, 3, 4, 'Mess Bill Summary Report', '/index', '', 'module', 'Y','', ''),
    (1, 6, 3, 5, 'Mess Dined Summary Report', '/index', '', 'module', 'Y','', ''),
    (1, 6, 3, 6, 'Not Dine Students', '/index', '', 'module', 'Y','', ''),
    (1, 6, 3, 7, 'Partial Payment Students', '/index', '', 'module', 'Y','', ''),
    (1, 6, 3, 8, 'Food Court Purchase Report', '/index', '', 'module', 'Y','', ''),
    (1, 6, 3, 9, 'Students Mess InTime Report', '/index', '', 'module', 'Y','', ''),

    (1, 6, 4, 1, 'Mess Master', '/messMaster', '', 'module', 'Y','', ''),
    (1, 6, 4, 3, 'Mess / Vendor Allocation', '/messVendorAllocation', '', 'module', 'Y','', ''),
    (1, 6, 4, 4, 'Mess Session', '/messSession', '', 'module', 'Y','', ''),
    (1, 6, 4, 5, 'Vacation MessMaster', '/index', '', 'module', 'Y','', '')
;
INSERT INTO schooldev."MENU_LIST" (heading,menu_order,main_menu_order,sub_menu_order,sub_sub_menu_order,url_path,image_name,menu_type,active_flag,icon_name,banner_name) VALUES
    ('HDC Complaint List',0,1,7,4,'/hdcComplaint','','module','Y','','');
INSERT INTO schooldev."MENU_LIST" (menu_order,main_menu_order,sub_menu_order,sub_sub_menu_order,heading,url_path,image_name,menu_type,active_flag,icon_name,banner_name)
VALUES (0, 1, 1, 8, 'Mail Management', '/mailManagement', '', 'module', 'Y','', '');
update schooldev."MENU_LIST" set url_path='/roomAllotmentLogs' where main_menu_order=5 and sub_menu_order=1 and sub_sub_menu_order=12;

INSERT INTO schooldev.roles (role_id,role_name,active_flag,created_by,created_at,modified_by,modified_at,school_id)
VALUES
    (1,'Admin','Y','Ram Kumar','2011-03-24 15:39:43.238','Triesten Tech','2011-04-01 12:16:35.544',1),
    (2,'Student','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (3,'Vendor','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (4,'Anonymous','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (5,'Accountant','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (6,'Principal','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (7,'Developer','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (8,'Lecturer','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (9,'Other Candidate','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (11,'CCW','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (12,'DOST','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (13,'ccww','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (14,'Dean','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (15,'Leisure Tap','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (16,'Academic Staff','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (17,'ISO Staff','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (18,'Saarang','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (19,'Accounts Section','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (20,'CCW Office','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (21,'Dost Office','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (22,'Hostel Office','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (23,'Hostel Check In','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (24,'Caterer','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (25,'Warden','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (26,'Mess Accountant','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (27,'Staff','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (28,'Faculty','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (29,'AR','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (30,'Mess Staff','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (31,'HM Office','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (32,'Guest Coupon','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (33,'ccw.admin','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (34,'ICSR','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (35,'HDC','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (38,'Wellness','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (39,'Clerk','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (40,'AppAdmin','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (600001,'SoftwareAdmin','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (600002,'Data Operator','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1),
    (60000099,'RollupAdmin','Y','triesten','2024-09-02 14:18:19.878','triesten','2024-09-02 14:18:19.878',1);
INSERT INTO schooldev.roles
(role_id, role_name, active_flag, created_by, created_at, modified_by, modified_at, school_id)
VALUES(41, 'Wellness Officer', 'Y', 'triesten', now(), 'triesten', now(), 1);

INSERT INTO schooldev.roles
(role_id, role_name, active_flag, created_by, created_at, modified_by, modified_at, school_id)
VALUES(42, 'Wellness Report', 'Y', 'triesten', now(), 'triesten', now(), 1);

INSERT INTO schooldev.role_menu_privilege (role_id, menu_id, created_by, created_at, modified_by, modified_at, active_flag)
    (SELECT (SELECT role_id FROM schooldev.roles WHERE role_name = 'SoftwareAdmin'), menu_id, 'admin', now(), 'admin', now(), 'Y' FROM schooldev."MENU_LIST" where menu_type <> 'dashboard' );

INSERT INTO schooldev.role_menu_privilege (role_id, menu_id, created_by, created_at, modified_by, modified_at, active_flag)
VALUES
    ((SELECT role_id FROM schooldev.roles WHERE role_name = 'SoftwareAdmin'), (SELECT menu_id FROM schooldev."MENU_LIST" WHERE heading = 'Dashboard'), 'admin', now(), 'admin', now(), 'Y'),
    ((SELECT role_id FROM schooldev.roles WHERE role_name = 'Hostel Check In'), (SELECT menu_id FROM schooldev."MENU_LIST" WHERE heading = 'Dean Dashboard'), 'admin', now(), 'admin', now(), 'Y'),
    ((SELECT role_id FROM schooldev.roles WHERE role_name = 'Student'), (SELECT menu_id FROM schooldev."MENU_LIST" WHERE heading = 'Student Dashboard'), 'admin', now(), 'admin', now(), 'Y'),
    ((SELECT role_id FROM schooldev.roles WHERE role_name = 'Other Candidate'), (SELECT menu_id FROM schooldev."MENU_LIST" WHERE heading = 'Other Candidate Dashboard'), 'admin', now(), 'admin', now(), 'Y')
;
INSERT INTO schooldev.role_menu_privilege (role_id, menu_id, created_by, created_at, modified_by, modified_at, active_flag)
VALUES ((SELECT role_id FROM schooldev.roles WHERE role_name = 'SoftwareAdmin'), (SELECT menu_id FROM schooldev."MENU_LIST" WHERE heading = 'Mail Management'), 'admin', now(), 'admin', now(), 'Y');

-- UPDATE schooldev."USER_MANAGEMENT" SET role_id = (SELECT role_id FROM schooldev.roles WHERE role_name = 'Faculty') WHERE account_type = 'Faculty';
UPDATE schooldev."USER_MANAGEMENT" SET role_id = (SELECT role_id FROM schooldev.roles WHERE role_name = 'Student') WHERE account_type = 'Student';
UPDATE schooldev."USER_MANAGEMENT" SET role_id = (SELECT role_id FROM schooldev.roles WHERE role_name = 'Vendor') WHERE account_type = 'Vendor';
UPDATE schooldev."USER_MANAGEMENT" SET role_id = 600001 WHERE role_id = 60000103;
UPDATE schooldev."USER_MANAGEMENT" SET role_id = (SELECT role_id FROM schooldev.roles WHERE role_name = 'Admin') WHERE account_type = 'Admin';
UPDATE schooldev."USER_MANAGEMENT" SET account_type = 'Anonymous' WHERE account_type IS NULL;
UPDATE schooldev."USER_MANAGEMENT" SET role_id = (SELECT role_id FROM schooldev.roles WHERE role_name = 'Anonymous') WHERE account_type = 'Anonymous';

INSERT INTO schooldev.dashboard_widget_master(widget_name, widget_url, widget_description, widget_image_path, created_by, created_at, modified_by, modified_at, active_flag, school_id, widget_size, order_by)
VALUES('MS / PHD Scholar Stay Extension', '/scholarsStayExtension/widget', 'MS / PHD Scholar Stay Extension', NULL, 'Admin', now(), 'Admin', now(), 'Y', 1, 'small', 6);
INSERT INTO schooldev.dashboard_widget_master (widget_name, widget_description, created_by, created_at, modified_by, modified_at, active_flag, widget_url, widget_size, order_by)
VALUES ('RFID Pin Change', 'RFID Pin Change', 'Admin', now(), 'Admin', now(), 'Y', '/userRfidPinChange/widget', 'small', 16);
INSERT INTO schooldev.dashboard_widget_master (widget_name, widget_description, created_by, created_at, modified_by, modified_at, active_flag, widget_url, widget_size, order_by)
VALUES ('Roll Number Change', 'Roll Number Change', 'Admin', now(), 'Admin', now(), 'Y', '/rollNoChangeDetails/widget', 'small', 14);
INSERT INTO schooldev.dashboard_widget_master(widget_name, widget_url, widget_description, widget_image_path, created_by, created_at, modified_by, modified_at, active_flag, school_id, widget_size, order_by)
VALUES('Professional Shows Tickets and T-Shirts', '/showAction/widget', 'Professional Shows Tickets and T-Shirts', NULL, 'Admin', now(),'Admin', now(), 'Y', 1, 'small', 15);
INSERT INTO schooldev.dashboard_widget_master(widget_name, widget_url, widget_description, widget_image_path, created_by, created_at, modified_by, modified_at, active_flag, school_id, widget_size, order_by)
VALUES('Priority Mess Registration', '/messPriority/widget', 'Priority Mess Registration', NULL, 'Admin', now(), 'Admin', now(), 'Y', 1, 'small', 14);
INSERT INTO schooldev.dashboard_widget_master(widget_name, widget_url, widget_description, widget_image_path, created_by, created_at, modified_by, modified_at, active_flag, school_id, widget_size, order_by)
VALUES('Hostel Payment', '/hostelPayment/widget', 'Hostel Payment Widget', NULL, 'Admin', now(), 'Admin', now(), 'Y', 1, 'small', 16);
INSERT INTO schooldev.dashboard_widget_master (widget_name, widget_description, created_by, created_at, modified_by, modified_at, active_flag, widget_url, widget_size, order_by)
VALUES ('Mess Rebate', 'Mess Rebate', 'Admin', now(), 'Admin', now(), 'Y', '/studentMessRebate/widget', 'small', 9);
INSERT INTO schooldev.dashboard_widget_master(widget_name, widget_url, widget_description, widget_image_path, created_by, created_at, modified_by, modified_at, active_flag, school_id, widget_size, order_by)
VALUES('Ledger Report', '/ledgerReport/widget', 'Ledger Report Widget', NULL, 'Admin', now(), 'Admin', now(), 'Y', 1, 'small', 17);
INSERT INTO schooldev.dashboard_widget_master(widget_name, widget_url, widget_description, widget_image_path, created_by, created_at, modified_by, modified_at, active_flag, school_id, widget_size, order_by)
VALUES('Guest Accommodation Request', '/guestAccommodationRequest/widget', 'Guest Accommodation Request', NULL, 'Admin', now(), 'Admin', now(), 'Y', 1, 'small', 14);
INSERT INTO schooldev.dashboard_widget_master(widget_name, widget_url, widget_description, widget_image_path, created_by, created_at, modified_by, modified_at, active_flag, school_id, widget_size, order_by)
VALUES('Student Complaint Form', '/studentComplaint/widget', 'Student Complaint', NULL, 'Admin', now(), 'Admin', now(), 'Y', 1, 'small', 4);
INSERT INTO schooldev.dashboard_widget_master(widget_name, widget_url, widget_description, widget_image_path, created_by, created_at, modified_by, modified_at, active_flag, school_id, widget_size, order_by)
VALUES('Sick Food Request', '/sickFoodRequest/widget', 'Sick Food', NULL, 'Admin', now(), 'Admin', now(), 'Y', 1, 'small', 2);
INSERT INTO schooldev.dashboard_widget_master(widget_name, widget_url, widget_description, widget_image_path, created_by, created_at, modified_by, modified_at, active_flag, school_id, widget_size, order_by)
VALUES('Hostel Room Vacating Form', '/hostelRoomVacatingForm/widget', 'Vacating Form', NULL, 'Admin', now(), 'Admin', now(), 'Y', 1, 'small', 2);


delete from schooldev.dashboard_widget_privilege;
select setval('schooldev.dashboard_widget_privilege_id_seq', 1);
INSERT INTO schooldev.dashboard_widget_privilege(dashboard_menu_id, widget_id, created_by, created_at, modified_by, modified_at, active_flag, school_id)
    (SELECT (SELECT menu_id FROM schooldev."MENU_LIST" WHERE heading = 'Student Dashboard'), id, 'Admin', now(), 'Admin', now(), 'Y', 1
     FROM schooldev.dashboard_widget_master);

       
INSERT INTO schooldev.dashboard_widget_master(widget_name, widget_url, widget_description, widget_image_path, created_by, created_at, modified_by, modified_at, active_flag, school_id, widget_size, order_by)
VALUES('Online Mess Coupon Purchase', '/onlineMessCoupon/widget', 'Online Mess Coupon Purchase', NULL, 'Admin', now(), 'Admin', now(), 'Y', 1, 'small', 2);

INSERT INTO schooldev.dashboard_widget_privilege
(dashboard_menu_id, widget_id, created_by, created_at, modified_by, modified_at, active_flag, school_id)
VALUES( (SELECT menu_id FROM schooldev."MENU_LIST" WHERE heading = 'Student Dashboard'), (select id from schooldev.dashboard_widget_master where widget_name='Online Mess Coupon Purchase') , 'Admin', now(), 'Admin', now(), 'Y', 1);

INSERT INTO schooldev."MENU_LIST" (heading,menu_order,main_menu_order,sub_menu_order,sub_sub_menu_order,url_path,image_name,menu_type,active_flag,icon_name,banner_name) VALUES
     ('FR Dashboard',0,1,8,1,'/frDashboard','','module','Y','',''),
    ('Live Status',0,1,8,2,'/liveStatus','','module','Y','',''),
    ('FR Pull',0,1,8,3,'/frPull','','module','Y','',''),
    ('FR Push',0,1,8,4,'/frPush','','module','Y','',''),
    ('FR Clear',0,1,8,5,'/frClear','','module','Y','',''),
    ('Mess Details',0,1,8,6,'/messDetails','','module','Y','',''),
    ('Schedular',0,1,8,7,'/frScheduler','','module','Y','',''),
    ('History',0,1,8,8,'/history','','module','Y','','');

INSERT INTO schooldev.role_menu_privilege (role_id, menu_id, created_by, created_at, modified_by, modified_at, active_flag)
VALUES
     ((SELECT role_id FROM schooldev.roles WHERE role_name = 'SoftwareAdmin'), (SELECT menu_id FROM schooldev."MENU_LIST" WHERE heading = 'FR Dashboard'), 'admin', now(), 'admin', now(), 'Y'),
    ((SELECT role_id FROM schooldev.roles WHERE role_name = 'SoftwareAdmin'), (SELECT menu_id FROM schooldev."MENU_LIST" WHERE heading = 'Live Status'), 'admin', now(), 'admin', now(), 'Y'),
    ((SELECT role_id FROM schooldev.roles WHERE role_name = 'SoftwareAdmin'), (SELECT menu_id FROM schooldev."MENU_LIST" WHERE heading = 'FR Pull'), 'admin', now(), 'admin', now(), 'Y'),
    ((SELECT role_id FROM schooldev.roles WHERE role_name = 'SoftwareAdmin'), (SELECT menu_id FROM schooldev."MENU_LIST" WHERE heading = 'FR Push'), 'admin', now(), 'admin', now(), 'Y'),
    ((SELECT role_id FROM schooldev.roles WHERE role_name = 'SoftwareAdmin'), (SELECT menu_id FROM schooldev."MENU_LIST" WHERE heading = 'FR Clear'), 'admin', now(), 'admin', now(), 'Y'),
    ((SELECT role_id FROM schooldev.roles WHERE role_name = 'SoftwareAdmin'), (SELECT menu_id FROM schooldev."MENU_LIST" WHERE heading = 'Mess Details'), 'admin', now(), 'admin', now(), 'Y'),
    ((SELECT role_id FROM schooldev.roles WHERE role_name = 'SoftwareAdmin'), (SELECT menu_id FROM schooldev."MENU_LIST" WHERE heading = 'Schedular'), 'admin', now(), 'admin', now(), 'Y'),
    ((SELECT role_id FROM schooldev.roles WHERE role_name = 'SoftwareAdmin'), (SELECT menu_id FROM schooldev."MENU_LIST" WHERE heading = 'History'), 'admin', now(), 'admin', now(), 'Y');
