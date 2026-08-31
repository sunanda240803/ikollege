-- noinspection SqlResolveForFile
/*
create or replace function _group_concat(text, text) returns text
    immutable
    language sql
as
$$
SELECT CASE
           WHEN $2 IS NULL THEN $1
           WHEN $1 IS NULL THEN $2
           ELSE $1 operator(pg_catalog.||) ',' operator(pg_catalog.||) $2
           END
$$;

alter function _group_concat(text, text) owner to postgres;

create or replace aggregate group_concat(text) (
    sfunc = _group_concat,
    stype = text
    );

alter aggregate group_concat(text) owner to postgres;
*/
--Drop elements
set search_path to public;
DROP FUNCTION IF EXISTS schooldev.mess_to_card_req_list(character varying, character varying, character varying, character varying);
DROP FUNCTION IF EXISTS schooldev.mess_to_card_req_list(character varying,character varying,date,date);
DROP FUNCTION IF EXISTS schooldev.guest_coupon_req_list(varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar);
DROP FUNCTION IF EXISTS schooldev.room_occupancy_status(varchar, varchar, varchar, varchar, varchar);
DROP FUNCTION IF EXISTS schooldev.room_occupancy_status(date, date, bigint, bigint, character varying);
DROP FUNCTION IF EXISTS schooldev.student_balance(varchar);
DROP FUNCTION IF EXISTS schooldev.hostel_enrollment_list(varchar, varchar, varchar, int4, varchar, varchar);
DROP FUNCTION IF EXISTS schooldev.get_filtered_bulk_appointments(varchar, varchar, date, date, date, varchar, varchar);
DROP FUNCTION IF EXISTS schooldev.get_filtered_bulk_appointments(varchar, varchar, date, date, date, varchar);
DROP FUNCTION IF EXISTS schooldev.search_students_hostel(character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,integer,character varying,integer);
DROP FUNCTION IF EXISTS schooldev.search_students_hostel(varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, int4, varchar, int4, varchar);
DROP FUNCTION IF EXISTS schooldev.mess_inspection_list(int8, int8, varchar, varchar, varchar);
DROP FUNCTION IF EXISTS schooldev.mess_inspection_list(int8, int8, varchar, varchar, varchar);
DROP FUNCTION IF EXISTS schooldev.mess_allotted_list(varchar, varchar, int8, varchar, varchar);
DROP FUNCTION IF EXISTS schooldev.mess_allotted_list(varchar, varchar, bigint, bigint, varchar);
DROP FUNCTION IF EXISTS schooldev.mess_allotted_list(character varying,character varying,bigint,bigint);
DROP FUNCTION IF EXISTS schooldev.hdc_complaint_list(character varying,character varying,character varying,character varying,character varying,character varying,integer,bigint,integer);
DROP FUNCTION IF EXISTS schooldev.wellness_data_list(character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying,character varying);
DROP FUNCTION IF EXISTS schooldev.mess_billing_report(int4, varchar, varchar);
DROP FUNCTION IF EXISTS schooldev.get_student_details_api(character varying,character varying);
DROP FUNCTION IF EXISTS schooldev.search_candidates_hostel(varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, int4, varchar, varchar, varchar, varchar, varchar, int4, varchar, varchar);

DROP VIEW IF EXISTS schooldev.employee_communication_details_view;
DROP TABLE IF EXISTS schooldev."COMPLETE_STUDENT_APPLICATION_VIEW" ;
DROP VIEW IF EXISTS schooldev."DYNAMIC_USER_TABS_DISPLAYABLE_VIEW";
drop view if exists schooldev.iit_wd_user_tabs_displayable_view;
DROP VIEW IF EXISTS schooldev.tab_master;
DROP VIEW IF EXISTS schooldev.employee_communication_detailss_view;
DROP TABLE IF EXISTS schooldev."ROOM_OCCUPANCY_STATUS_VIEW_ALL_TYPE_ROOMS";
DROP TABLE IF EXISTS schooldev."ROOM_OCCUPANCY_STATUS_VIEW";
DROP TABLE IF EXISTS schooldev."HOSTEL_DETAILS_INFO";
DROP VIEW IF EXISTS schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW";
DROP VIEW IF EXISTS schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW_06_04_2018_backup";
DROP TABLE IF EXISTS schooldev."COMPLETE_STUDENT_APPLICATION_VIEW_BACKUP_JUN19";
DROP TABLE IF EXISTS schooldev."COMPLETE_STUDENT_APPLICATION_VIEW_BACKUP_FEB2019";
DROP VIEW IF EXISTS schooldev."CANDIDATE_STAY_DATE_LIST_VIEW_backup_19-02-18";
DROP VIEW IF EXISTS schooldev."students_final_balance_backup";
DROP VIEW IF EXISTS schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW";
DROP VIEW IF EXISTS schooldev."ALL_STUDENTS_DETAILS_VIEW";
DROP VIEW IF EXISTS schooldev."CURRENT_MESS_DETAILS_VIEW";
drop view IF EXISTS schooldev."HOSTEL_NIGHT_PAYMENT_LEDGER_VIEW";
drop view IF EXISTS schooldev."HOSTEL_NIGHT_PAYMENT_ONLINE_VIEW";
drop table if exists schooldev."GUEST_ROOM_ALLOTMENT_INFO_VIEW";
drop view if exists schooldev."COMPLETE_CANDIDATE_VIEW" ;
drop table if exists schooldev."CANDIDATE_APPOINTMENT_VIEW";

DROP TYPE IF EXISTS messtocardrequestlist;
DROP TYPE IF EXISTS schooldev.mess_to_card_request_list;
DROP TYPE IF EXISTS student_record_result_hostel;
DROP TYPE IF EXISTS guestcouponlist;
DROP TYPE IF EXISTS room_occupancy_status_result;
DROP TYPE IF EXISTS hdccomplaintlisttype;
DROP TYPE IF EXISTS schooldev.mess_list;
DROP TYPE IF EXISTS details_result1;

DROP INDEX IF EXISTS schooldev."IDX_FACULTY_PERSONAL_DETAILS_EMAIL";

DROP TABLE IF EXISTS schooldev.student_net_balance ;
DROP TABLE IF EXISTS schooldev."ROOM_OCCUPANCY_STATUS_VIEW";

ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" DROP CONSTRAINT IF EXISTS facultylevel;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" DROP CONSTRAINT IF EXISTS staff_lib_card_id_uniq;
ALTER TABLE schooldev."FACULTY_DEPARTMENT_ALLOCATION_INFO" DROP CONSTRAINT IF EXISTS "Faculty_Personal_Foreign_Key";
ALTER TABLE schooldev."FEEDBACK_QUALIFIER" DROP CONSTRAINT IF EXISTS feed_qual_pkey;
ALTER TABLE schooldev."FEEDBACK_QUESTION" DROP CONSTRAINT IF EXISTS feed_ques_pkey;
ALTER TABLE schooldev."GUEST_FILES_INFORMATION" DROP CONSTRAINT IF EXISTS "GUEST_FILES_INFORMATION_PK";
ALTER TABLE schooldev."IIT_W_STUDENT_FILES_INFORMATION" DROP CONSTRAINT IF EXISTS "stud_file_info_PK";
ALTER TABLE schooldev."TRANSPORT_ROUTE_DETAILS" DROP CONSTRAINT IF EXISTS "FOREIGN_KEY_VEHICLE_ID";

--Create New tables and types
CREATE TABLE IF NOT EXISTS schooldev."CAPTCHA_TOKEN"
(
    id           bigserial,
    token        character varying,
    captcha_code character varying,
    created_at   timestamp without time zone,
    primary key (id)
);
CREATE TABLE IF NOT EXISTS schooldev."ASSET_MAINTENANCE_TYPE" (
    maintenance_type_id bigserial NOT NULL,
    maintenance_type varchar(64) NOT NULL,
    created_by varchar NOT NULL,
    created_at timestamp NOT NULL,
    modified_by varchar NOT NULL,
    modified_at timestamp NOT NULL,
    active_flag varchar NOT NULL,
    school_id int8 NOT NULL,
    CONSTRAINT asset_maintenance_type_pkey PRIMARY KEY (maintenance_type_id)
);
CREATE TABLE IF NOT EXISTS schooldev."STUDENT_ROLLNO_CHANGE" (
    id bigserial NOT NULL,
    school_id int4 NOT NULL DEFAULT 1,
    studentid varchar(30) NOT NULL,
    new_roll_no varchar(30) NOT NULL,
    file_upload varchar(300) NULL,
    created_by varchar(32) NULL,
    created_at timestamp NULL,
    modified_by varchar(32) NULL,
    modified_at timestamp NULL,
    active_flag bpchar(1) NULL,
    status varchar(150) NULL,
    CONSTRAINT student_rollno_changekey PRIMARY KEY (id),
    CONSTRAINT school_id FOREIGN KEY (studentid, school_id) REFERENCES schooldev."STUDENT_DETAILS_INFO"(student_id, school_id)
);
CREATE TABLE IF NOT EXISTS schooldev.dashboard_widget_master (
    id bigserial NOT NULL,
    widget_name varchar NOT NULL,
    widget_url varchar NOT NULL,
    widget_description varchar NULL,
    widget_image_path varchar NULL,
    created_by varchar NOT NULL,
    created_at timestamp NOT NULL,
    modified_by varchar NOT NULL,
    modified_at timestamp NOT NULL,
    active_flag varchar NOT NULL,
    school_id int8 NOT NULL DEFAULT 1,
    widget_size varchar(5) NOT NULL,
    order_by int4 NOT NULL,
    CONSTRAINT dashboard_widget_master_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS schooldev.dashboard_widget_privilege (
    id bigserial NOT NULL,
    dashboard_menu_id int8 NOT NULL,
    widget_id int8 NULL,
    created_by varchar NOT NULL,
    created_at timestamp NOT NULL,
    modified_by varchar NOT NULL,
    modified_at timestamp NOT NULL,
    active_flag varchar NOT NULL,
    school_id int8 NOT null DEFAULT 1,
    CONSTRAINT dashboard_widget_privilege_pk PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS schooldev.role_menu_privilege
(
    id          bigserial primary key,
    role_id     bigint not null constraint fk_user_role_role_id references schooldev.roles,
    menu_id          bigint not null constraint fk_user_role_menu_id references schooldev."MENU_LIST",
    created_by       character varying       not null,
    created_at       timestamp         not null,
    modified_by       character varying       not null,
    modified_at       timestamp         not null,
    active_flag      varchar(1)        not null
);
CREATE TABLE schooldev."PAYMENT_GATEWAY_CONFIGURATION_CCAV" (
    id bigserial NOT NULL,
    url varchar(128) NOT NULL,
    access_code varchar(255) NOT NULL,
    working_key varchar(255) NOT NULL,
    instance varchar(32) NOT NULL,
    action_url varchar NOT NULL,
    CONSTRAINT payment_gateway_configuration_ccav_unique UNIQUE (url)
);

CREATE TABLE IF NOT EXISTS schooldev.student_net_balance (
    student_id varchar NOT NULL,
    net_balance_mess float8 NULL,
    net_balance_card float8 NULL,
    CONSTRAINT student_net_balance_pk PRIMARY KEY (student_id)
);
CREATE TABLE IF NOT EXISTS schooldev."USER_AUDIT_TRAIL" (
    id bigserial NOT NULL,
    date timestamp NOT NULL,
    user_id varchar(32) NULL,
    user_name varchar(32) NULL,
    ip_address varchar(64) NULL,
    form_name varchar(64) NULL,
    action_commit_type varchar(16) NULL,
    action_path varchar(130) NULL,
    class_method_name varchar(130) NULL,
    school_id int4 default 1,
    CONSTRAINT "USER_AUDIT_TRAIL_pkey" PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS schooldev."HOSTEL_NIGHT_PAYMENT_TRANSACTION" (
                                                                            id bigserial NOT NULL,
                                                                            student_id varchar(16) NOT NULL,
    hostel_id int8 NOT NULL,
    no_of_veg_coupon int4 NULL,
    no_of_nonveg_coupon int4 NULL,
    veg_rate float8 NOT NULL,
    nonveg_rate float8 NOT NULL,
    total_amount float8 NOT NULL,
    pay_by varchar(32) NOT NULL,
    order_no varchar(50) NULL,
    payment_type varchar(64) NULL,
    payment_date date NULL,
    paid_amount float8 NULL,
    transaction_ref_number varchar(50) NULL,
    ccav_reference_no varchar(50) NULL,
    payment_status varchar(32) NULL,
    trans_fee float8 NULL,
    service_tax float8 NULL,
    status_message text NULL,
    retry_count int4 DEFAULT 0 NULL,
    mail_status varchar(16) NULL,
    active_flag bpchar(1) NOT NULL,
    school_id int4 DEFAULT 1 NOT NULL,
    created_by varchar(64) NOT NULL,
    created_at timestamp NOT NULL,
    modified_by varchar(64) NOT NULL,
    modified_at timestamp NOT NULL,
    CONSTRAINT "HOSTEL_NIGHT_PAYMENT_TRANSACTION_PK" PRIMARY KEY (id)
);
create type schooldev.mess_to_card_request_list as (mess_to_card_id integer, student_id character varying, student_name character varying,
                                                    request_date character varying, net_bal double precision, transfer_amount double precision,
                                                    requested_status character varying, transferred_date character varying, checkbox_transfer character varying,
                                                    email_id character varying);

create type schooldev.student_record_result_hostel as (app_status character varying, wrk_status character varying,
                                                       request_id bigint, dining character varying, dining_others character varying, student_id character varying,
                                                       created_at date,student_name character varying, gender character, dob character varying, student_email character varying,
                                                       appointment_from date, appointment_to date, stay_from date, stay_to date, gross_pay double precision,
                                                       validating_authority character varying, validating_authority_email character varying, approval_notes character varying,
                                                       rejection_description character varying, category character varying, approval_date date,
                                                       allotted_hostel_name character varying,allotted_room_no int ,allotted_seat character varying,workflow_id integer,
                                                       modified_at timestamp without time zone,thesis_submitted_date date,admission_date date,
                                                       current_hostel_name character varying,current_room_no character varying,current_sub_room character varying,
                                                       check_in_allowed_status character varying,vacating_status character varying,city character varying,
                                                       state character varying,student_mobile bigint,occupancy character varying,purpose character varying,
                                                       hod_name character varying,hod_email character varying,category_others character varying,cancel_description character varying,
                                                       authority_type character varying,approval_level integer);



create type public.room_occupancy_status_result as (allotment_id bigint, hostel_id bigint, hostel_name character varying, floor_id bigint, floor_name character varying,
    room_id bigint, room_no character varying, sub_room_id character varying, student_type character varying, request_id character varying, student_name character varying,
    student_id character varying, stay_from_date date, stay_to_date date, vacate_date date, shifted_date date, dob date, email character varying, nature_of_appointment character varying,
    dining_required character varying, allocation_type character varying, created_by character varying, created_at timestamp, modified_by character varying, modified_at timestamp,
    active_flag character, vacation_category character varying);

create type public.hdccomplaintlisttype as (hostel_id bigint,warden_id character varying,hdc_id integer,student_id character varying,
    student_name character varying,hostel_name character varying,room_no integer,violation character varying,
    wardern_plea character varying,warden_decision character varying,follow_up_report character varying,
    warden_remarks character varying,penality_status character varying,
    created_at character varying,created_by character varying,category character varying, day_scholar_involve character varying);

create type schooldev.mess_list as (smd_id bigint, mmc_id bigint, student_id character varying, student_name character varying, mess_name character varying,
    mess_head character varying, from_date date, to_date date, change_from_date date, change_to_date date,
    approval_status character varying, push_remove_status character varying, push_status character varying,
    smd_current_active_flag character varying, smd_remarks character varying, smd_description character varying);

create type schooldev.details_result1 as(student_id character varying, student_name character varying,
                                         hostel_name character varying, room_no int,seat_name character varying, date_of_last_residence date,
                                         hostel_student_status character varying, v_sdi_previous_id character varying,
                                         vacate_date character varying,workflow_flag character varying,workflow_date character varying);

--Create Missing Sequences
CREATE SEQUENCE IF NOT EXISTS schooldev.floor_id_seq;
CREATE SEQUENCE IF NOT EXISTS schooldev.course_master_id_seq;
CREATE SEQUENCE IF NOT EXISTS schooldev.department_id_seq;
CREATE SEQUENCE IF NOT EXISTS schooldev.asset_category_id_seq;
CREATE SEQUENCE IF NOT EXISTS schooldev.room_id_seq;
CREATE SEQUENCE IF NOT EXISTS schooldev.asset_id_seq;
CREATE SEQUENCE IF NOT EXISTS schooldev."USER_FP_ACCESS_CARD_SEQ";
CREATE SEQUENCE IF NOT EXISTS schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST_request_id_seq";
CREATE SEQUENCE IF NOT EXISTS schooldev.inventory_id_seq;
CREATE SEQUENCE IF NOT EXISTS schooldev.mess_rebate_id_seq;
CREATE SEQUENCE IF NOT EXISTS schooldev.mess_rebate_workflow_id_seq;
CREATE SEQUENCE IF NOT EXISTS schooldev.file_id_seq;
create sequence IF NOT EXISTS schooldev.menu_list_menu_id_seq;
CREATE SEQUENCE IF NOT EXISTS schooldev."IIT_W_CANDIDATE_FILES_INFORMATION_file_id_seq";
CREATE SEQUENCE IF NOT EXISTS schooldev."IIT_WD_ROLE_TAB_SETTINGS_id_seq";
CREATE SEQUENCE IF NOT EXISTS schooldev."IIT_WD_DASHBOARD_TAB_MASTER_id_seq";
CREATE SEQUENCE IF NOT EXISTS schooldev."IIT_WD_USER_TAB_SETTINGS_id_seq";
CREATE SEQUENCE IF NOT EXISTS schooldev.roomallotment_id_seq;
CREATE SEQUENCE IF NOT EXISTS schooldev.IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE_SEQ;
CREATE SEQUENCE IF NOT EXISTS schooldev.IIT_PS_TEMP_ACCOM_LEDGER_A_SEQ;
CREATE SEQUENCE IF NOT EXISTS schooldev.roles_role_id_seq;
CREATE SEQUENCE IF NOT EXISTS schooldev."SEQ_MAST_SECONDARY_ROLE_ID";
CREATE SEQUENCE IF NOT EXISTS schooldev."SEQ_MAST_COURSE_ALLOCATION_ID";
CREATE SEQUENCE IF NOT EXISTS schooldev."SEQ_MAST_RFID_MAPPING";
CREATE SEQUENCE IF NOT EXISTS schooldev."SEQ_MAST_HOSTEL_ROOM_ALLOTMENT_ID";
CREATE SEQUENCE IF NOT EXISTS schooldev."SEQ_SIMS_CONFIG_DATA_CONFIG_ID";
CREATE SEQUENCE IF NOT EXISTS schooldev.WARDEN_INFO_SEQ;
CREATE SEQUENCE IF NOT EXISTS schooldev."HOSTEL_NIGHT_ONLINE_PAYMENT_ORDERID";
--Alter Table and Column Names
alter table schooldev."USER_MANAGEMENT" rename column v_um_user_id to user_id;
alter table schooldev."USER_MANAGEMENT" rename column v_um_username to user_name;
alter table schooldev."USER_MANAGEMENT" rename column n_um_roleid to role_id;
alter table schooldev."USER_MANAGEMENT" rename column v_um_active_status to active_status;
alter table schooldev."USER_MANAGEMENT" rename column v_um_no_failed_attempts to no_failed_attempts;
alter table schooldev."USER_MANAGEMENT" rename column v_um_islockable to is_lockable;
alter table schooldev."USER_MANAGEMENT" rename column v_um_email to email;
alter table schooldev."USER_MANAGEMENT" rename column v_um_password to password;
alter table schooldev."USER_MANAGEMENT" rename column t_um_password_modified_time to password_modified_time;
alter table schooldev."USER_MANAGEMENT" rename column t_um_wrong_pwd_time to wrong_pwd_time;
alter table schooldev."USER_MANAGEMENT" rename column v_um_created_by to created_by;
alter table schooldev."USER_MANAGEMENT" rename column t_um_created_at to created_at;
alter table schooldev."USER_MANAGEMENT" rename column v_um_updated_by to modified_by;
alter table schooldev."USER_MANAGEMENT" rename column t_um_updated_at to modified_at;
alter table schooldev."USER_MANAGEMENT" rename column t_um_last_login_time to last_login_time;
alter table schooldev."USER_MANAGEMENT" rename column v_um_account_type to account_type;
alter table schooldev."USER_MANAGEMENT" rename column v_um_active_flag to active_flag;
alter table schooldev."USER_MANAGEMENT" rename column n_um_employee_id to employee_id;
alter table schooldev."USER_MANAGEMENT" rename column v_um_ip_address to ip_address;
alter table schooldev."USER_MANAGEMENT" rename column v_um_authentication_server to authentication_server;
alter table schooldev."USER_MANAGEMENT" rename column v_um_cardpin to card_pin;
ALTER TABLE schooldev."USER_MANAGEMENT" ADD secondary_role_id int4 NULL;

alter table schooldev."SIMS_CONFIG_DATAS" rename to "SIMS_CONFIG_DATA";
alter table schooldev."SIMS_CONFIG_DATA" rename column v_scd_config_key to config_key;
alter table schooldev."SIMS_CONFIG_DATA" rename column v_scd_config_value to config_value;
alter table schooldev."SIMS_CONFIG_DATA" rename column v_scd_config_created_by to created_by;
alter table schooldev."SIMS_CONFIG_DATA" rename column t_scd_config_created_at to created_at;
alter table schooldev."SIMS_CONFIG_DATA" rename column v_scd_config_updated_by to modified_by;
alter table schooldev."SIMS_CONFIG_DATA" rename column t_scd_config_updated_at to modified_at;
alter table schooldev."SIMS_CONFIG_DATA" rename column v_scd_config_active_flag to active_flag;
alter table schooldev."SIMS_CONFIG_DATA" rename column v_scd_config_description to description;
alter table schooldev."SIMS_CONFIG_DATA" rename column n_scd_school_id to school_id;
alter table schooldev."SIMS_CONFIG_DATA" rename column v_scd_config_id to config_id;
ALTER TABLE schooldev."SIMS_CONFIG_DATA" ALTER COLUMN school_id set default(1);
ALTER TABLE schooldev."SIMS_CONFIG_DATA" ALTER COLUMN config_id SET DEFAULT nextval('schooldev."SEQ_SIMS_CONFIG_DATA_CONFIG_ID"');
SELECT setval('schooldev."SEQ_SIMS_CONFIG_DATA_CONFIG_ID"', (SELECT max(config_id) + 1 FROM schooldev."SIMS_CONFIG_DATA")::bigint);
ALTER SEQUENCE schooldev."SEQ_SIMS_CONFIG_DATA_CONFIG_ID" OWNED BY schooldev."SIMS_CONFIG_DATA".config_id;

alter table schooldev."STUDENT_BIODATA_FORM_DETAILS" rename to "STUDENT_BIO_DATA_FORM_DETAILS";
alter table schooldev."STUDENT_BIO_DATA_FORM_DETAILS" rename column biodata_id to bio_data_id;
alter table schooldev."STUDENT_BIO_DATA_FORM_DETAILS" rename column application_num to application_number;
alter table schooldev."STUDENT_BIO_DATA_FORM_DETAILS" rename column student_mobile_num to student_mobile;
alter table schooldev."STUDENT_BIO_DATA_FORM_DETAILS" rename column room_num to room_number;
alter table schooldev."STUDENT_BIO_DATA_FORM_DETAILS" rename column aadhar_num to aadhaar_number;
alter table schooldev."STUDENT_BIO_DATA_FORM_DETAILS" rename column pan_num to pan_number;
alter table schooldev."STUDENT_BIO_DATA_FORM_DETAILS" alter column student_name type varchar(120) using student_name::varchar(120);
alter table schooldev."STUDENT_BIO_DATA_FORM_DETAILS" alter column signed_parent_name type varchar(120) using signed_parent_name::varchar(120);
ALTER TABLE schooldev."STUDENT_BIO_DATA_FORM_DETAILS" ADD COLUMN faculty_email VARCHAR(50) NULL;
ALTER TABLE schooldev."STUDENT_BIO_DATA_FORM_DETAILS" ADD student_personal_email varchar(128) NULL;
ALTER TABLE schooldev."STUDENT_BIO_DATA_FORM_DETAILS" ADD city varchar(128) NULL;
ALTER TABLE schooldev."STUDENT_BIO_DATA_FORM_DETAILS" ADD state varchar(128) NULL;
ALTER TABLE schooldev."STUDENT_BIO_DATA_FORM_DETAILS" ADD country varchar(120) NULL;
ALTER TABLE schooldev."STUDENT_BIO_DATA_FORM_DETAILS" ADD pin_code int4 NULL;
alter table schooldev."STUDENT_BIO_DATA_FORM_DETAILS" add column pwd text;
ALTER TABLE schooldev."STUDENT_BIO_DATA_FORM_DETAILS" ADD COLUMN pwd_percentage int4;
ALTER TABLE schooldev."STUDENT_BIO_DATA_FORM_DETAILS" ADD pwd_description text NULL;

alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_user_id to user_id;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_firstname to first_name;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_lastname to last_name;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column n_umo_roleid to role_id;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_password to password;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_passport_name to passport_name;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_passport_number to passport_number;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_email to email;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_dob to dob;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_country to country;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_applicatonid to application_id;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_no_failed_attempts to no_failed_attempts;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_islockable to is_lockable;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column t_umo_password_modified_time to password_modified_time;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column t_umo_wrong_pwd_time to wrong_pwd_time;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_created_by to created_by;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column t_umo_created_at to created_at;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_updated_by to modified_by;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column t_umo_updated_at to modified_at;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column t_umo_last_login_time to last_login_time;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_account_type to account_type;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_active_flag to active_flag;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_admission_flag to admission_flag;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_active_status to active_status;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_account_registered_for to account_registered_for;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column v_umo_contact_number to contact_number;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column n_umo_user_id_pk to user_id_pk;
alter table schooldev."USER_MANAGEMENT_ONLINE" rename column n_umo_school_id to school_id;
ALTER TABLE schooldev."USER_MANAGEMENT_ONLINE" ALTER COLUMN admission_flag SET DEFAULT 0;

alter table schooldev."STUDENT_BIODATA_FAMILY_INFO" rename to "STUDENT_BIO_DATA_FAMILY_INFO";
alter table schooldev."STUDENT_BIO_DATA_FAMILY_INFO" rename column biodata_id to bio_data_id;
alter table schooldev."STUDENT_BIO_DATA_FAMILY_INFO" rename column application_num to application_number;

ALTER TABLE schooldev."FACILITY_MASTER" RENAME TO "HOSTEL_MASTER";
ALTER TABLE schooldev."HOSTEL_MASTER" RENAME COLUMN n_fm_facility_master_id TO hostel_id;
ALTER TABLE schooldev."HOSTEL_MASTER" RENAME COLUMN n_fm_facility_master_name TO hostel_name;
ALTER TABLE schooldev."HOSTEL_MASTER" RENAME COLUMN v_fm_created_by TO created_by;
ALTER TABLE schooldev."HOSTEL_MASTER" RENAME COLUMN t_fm_created_at TO created_at;
ALTER TABLE schooldev."HOSTEL_MASTER" RENAME COLUMN v_fm_modified_by TO modified_by;
ALTER TABLE schooldev."HOSTEL_MASTER" RENAME COLUMN t_fm_modified_at TO modified_at;
ALTER TABLE schooldev."HOSTEL_MASTER" RENAME COLUMN v_fm_active_flag TO active_flag;
ALTER TABLE schooldev."HOSTEL_MASTER" RENAME COLUMN n_fm_schoolid TO school_id;
ALTER TABLE schooldev."HOSTEL_MASTER" RENAME COLUMN v_fm_hostel_gender_type TO hostel_gender_type;
ALTER TABLE schooldev."HOSTEL_MASTER" RENAME COLUMN v_fm_hostel_office_email TO hostel_office_email;
ALTER TABLE schooldev."HOSTEL_MASTER" RENAME COLUMN v_fm_facility_master_shortcode TO hostel_short_code;
ALTER TABLE schooldev."HOSTEL_MASTER" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."HOSTEL_MASTER" RENAME COLUMN extention_flag TO extension_flag;
-- ALTER TABLE schooldev."HOSTEL_MASTER" ADD CONSTRAINT "HOSTEL_MASTER_pkey" PRIMARY KEY (hostel_id);
-- ALTER TABLE schooldev."HOSTEL_MASTER" ADD hn_veg_amount double precision NULL;
-- ALTER TABLE schooldev."HOSTEL_MASTER" ADD hn_nonveg_amount double precision NULL;

ALTER TABLE schooldev."SCHOOL_FACILITIES" RENAME TO "HOSTEL_FLOOR_MASTER";
ALTER TABLE schooldev."HOSTEL_FLOOR_MASTER" RENAME COLUMN n_sf_facility_id TO floor_id;
ALTER TABLE schooldev."HOSTEL_FLOOR_MASTER" RENAME COLUMN n_sf_school_id TO school_id;
ALTER TABLE schooldev."HOSTEL_FLOOR_MASTER" RENAME COLUMN v_sf_facility_name TO floor_name;
ALTER TABLE schooldev."HOSTEL_FLOOR_MASTER" RENAME COLUMN v_sf_facility_desc TO floor_desc;
ALTER TABLE schooldev."HOSTEL_FLOOR_MASTER" RENAME COLUMN l_sf_facility_size TO floor_size;
ALTER TABLE schooldev."HOSTEL_FLOOR_MASTER" RENAME COLUMN v_sf_created_by TO created_by;
ALTER TABLE schooldev."HOSTEL_FLOOR_MASTER" RENAME COLUMN t_sf_created_at TO created_at;
ALTER TABLE schooldev."HOSTEL_FLOOR_MASTER" RENAME COLUMN v_sf_updated_by TO modified_by;
ALTER TABLE schooldev."HOSTEL_FLOOR_MASTER" RENAME COLUMN t_sf_updated_at TO modified_at;
ALTER TABLE schooldev."HOSTEL_FLOOR_MASTER" RENAME COLUMN v_sf_active_flag TO active_flag;
ALTER TABLE schooldev."HOSTEL_FLOOR_MASTER" RENAME COLUMN v_sf_school_or_hostel_in_campus TO hostel_or_college;
ALTER TABLE schooldev."HOSTEL_FLOOR_MASTER" RENAME COLUMN n_sf_facility_master_id TO hostel_id;
ALTER TABLE schooldev."HOSTEL_FLOOR_MASTER" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."HOSTEL_FLOOR_MASTER" ALTER COLUMN floor_id SET DEFAULT nextval('schooldev.floor_id_seq');

ALTER TABLE schooldev.course_master RENAME COLUMN n_cm_course_master_id TO course_master_id;
ALTER TABLE schooldev.course_master RENAME COLUMN v_cm_course_master_name TO course_master_name;
ALTER TABLE schooldev.course_master RENAME COLUMN v_cm_description TO description;
ALTER TABLE schooldev.course_master RENAME COLUMN v_cm_degree_awarded TO degree_awarded;
ALTER TABLE schooldev.course_master RENAME COLUMN v_cm_affiliation TO affiliation;
ALTER TABLE schooldev.course_master RENAME COLUMN v_cm_created_by TO created_by;
ALTER TABLE schooldev.course_master RENAME COLUMN v_cm_modified_by TO modified_by;
ALTER TABLE schooldev.course_master RENAME COLUMN t_cm_created_at TO created_at;
ALTER TABLE schooldev.course_master RENAME COLUMN t_cm_modified_at TO modified_at;
ALTER TABLE schooldev.course_master RENAME COLUMN v_cm_active_status TO active_status;
ALTER TABLE schooldev.course_master RENAME COLUMN n_cm_school_id TO school_id;
ALTER TABLE schooldev.course_master RENAME COLUMN n_cm_display_count TO display_count;
ALTER TABLE schooldev.course_master RENAME COLUMN n_cm_department_id TO department_id;
ALTER TABLE schooldev.course_master RENAME COLUMN v_cm_course_master_head TO course_master_head;
ALTER TABLE schooldev.course_master ALTER COLUMN created_at TYPE timestamp USING created_at::timestamp;
ALTER TABLE schooldev.course_master ALTER COLUMN modified_at TYPE timestamp USING modified_at::timestamp;
ALTER TABLE schooldev.course_master RENAME COLUMN active_status TO active_flag;

ALTER TABLE schooldev.departments_details RENAME COLUMN n_d_department_id TO department_id;
ALTER TABLE schooldev.departments_details RENAME COLUMN v_d_department_name TO department_name;
ALTER TABLE schooldev.departments_details RENAME COLUMN v_d_department_description TO department_description;
ALTER TABLE schooldev.departments_details RENAME COLUMN v_d_department_head TO department_head;
ALTER TABLE schooldev.departments_details RENAME COLUMN v_d_department_assistant_head TO department_assistant_head;
ALTER TABLE schooldev.departments_details RENAME COLUMN v_d_created_by TO created_by;
ALTER TABLE schooldev.departments_details RENAME COLUMN t_d_created_at TO created_at;
ALTER TABLE schooldev.departments_details RENAME COLUMN v_d_modified_by TO modified_by;
ALTER TABLE schooldev.departments_details RENAME COLUMN t_d_modified_at TO modified_at;
ALTER TABLE schooldev.departments_details RENAME COLUMN v_d_active_status TO active_flag;
ALTER TABLE schooldev.departments_details RENAME COLUMN n_d_school_id TO school_id;

ALTER TABLE schooldev.course_master ALTER COLUMN course_master_id SET NOT NULL;
ALTER TABLE schooldev.course_master ALTER COLUMN course_master_id SET DEFAULT nextval('schooldev.course_master_id_seq');

alter table schooldev."STUDENT_DETAILS_INFO" rename column v_sdi_studentid to student_id;
alter table schooldev."STUDENT_DETAILS_INFO" rename column v_sdi_firstname to first_name;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_lastname to last_name;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_dob to dob;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_gender to gender;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_birthplace to birth_place;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_religion to religion;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_caste to caste ;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_category to category;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_student_nationality to nationality;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_student_mothertongue to mother_tongue;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_fathersname to fathers_name;
alter table schooldev."STUDENT_DETAILS_INFO" rename column    v_sdi_mothersname to mothers_name;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	n_sdi_contactnumber1 to contact_number;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_fathersqualification to fathers_qualification;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_mothersqualification to mothers_qualification;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_fathersoccupation to fathers_occupation;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_mothersoccupation to mothers_occupation;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_guardianname to guardian_name;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_city to city;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_state to state;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_taluk to taluk;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_district to district;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_country to country;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	n_sdi_pincode to pin_code;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_studentaddress to student_address;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_hostel_status to hostel_status;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_transportaion_status to transportation_status;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_transfercertificate_submition to transfer_certificate_submission;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_leavingcertificate_submition to leaving_certificate_submission;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_dateoftc_produced to date_of_tc_produced;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_numberoftc_produced to number_of_tc_produced;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_created_by to created_by;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_created_at to created_at;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_modified_by to modified_by;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_modified_at to modified_at;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_active_flag to active_flag;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	n_sdi_admission_number to admission_number;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_dateoflc_produced to date_of_lc_produced;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_numberoflc_produced to number_of_lc_produced;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_gaurdian_occupation to guardian_occupation;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_certificatesubmitionstatus to certificate_submission_status;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_parentorgaurdian_checked to parent_or_gaurdian_checked;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_housing to housing;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_stopping_name to stopping_name;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_parent_emailid to parent_email_id;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	n_sdi_schoolid to school_id;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	n_sdi_pickup_routeid to pick_up_route_id;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	n_sdi_drop_routeid to drop_route_id;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	n_sdi_alternate_contact_number to alternate_contact_number;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_father_income to father_income;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_mother_income to moter_income;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	b_sdi_scholarship_status to scholarship_status;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_scholarship_number to scholarship_number;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_transfer_certificate_issue to transfer_certificate_issue;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_library_card_id to library_card_id;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_field1 to field1;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_field2 to field2;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_field3 to field3;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_field4 to field4;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_field5 to field5;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_field6 to field6;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_settlement_flag to settlement_flag;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_previous_id to previous_id;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_day_scholar to day_scholar;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_vacation_category to vacation_category;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_student_personal_email to student_personal_email;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_blood_group to blood_group;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_landline_number to landline_number;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_student_height to student_height;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_last_update_details to last_update_details;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_last_device_registration_date to last_device_registration_date;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_aadhar_number to aadhar_number;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	v_sdi_pan_number to pan_number;
alter table schooldev."STUDENT_DETAILS_INFO" rename column 	registrationdate to registration_date;

alter table schooldev."SECONDARY_ROLES" rename column v_sr_user_id to user_id;
alter table schooldev."SECONDARY_ROLES" rename column l_sr_role_id to role_id;
alter table schooldev."SECONDARY_ROLES" rename column v_sr_created_by to created_by;
alter table schooldev."SECONDARY_ROLES" rename column t_sr_created_at to created_at;
alter table schooldev."SECONDARY_ROLES" rename column v_sr_updated_by to updated_by;
alter table schooldev."SECONDARY_ROLES" rename column t_sr_updated_at to updated_at;
alter table schooldev."SECONDARY_ROLES" rename column v_sr_active_flag to active_flag;
alter table schooldev."SECONDARY_ROLES" rename column l_sr_secondary_reference_role_id to secondary_reference_role_id;
alter table schooldev."SECONDARY_ROLES" rename column n_sr_schoolid to school_id;
alter table schooldev."SECONDARY_ROLES" ALTER COLUMN school_id set default(1);
alter table schooldev."SECONDARY_ROLES" rename column updated_by to modified_by;
alter table schooldev."SECONDARY_ROLES" rename column updated_at to modified_at;

ALTER TABLE schooldev."SECONDARY_ROLES" ALTER COLUMN secondary_reference_role_id SET NOT NULL;
ALTER TABLE schooldev."SECONDARY_ROLES" ALTER COLUMN secondary_reference_role_id SET DEFAULT nextval('schooldev."SEQ_MAST_SECONDARY_ROLE_ID"');
alter table schooldev."COURSE_ALLOCATION_INFO" rename column n_cai_course_allocation_id to course_allocation_id;
alter table schooldev."COURSE_ALLOCATION_INFO" rename column v_cai_student_id to student_id;
alter table schooldev."COURSE_ALLOCATION_INFO" rename column n_cai_course_id to course_id;
alter table schooldev."COURSE_ALLOCATION_INFO" rename column n_cai_course_period_id to course_period_id;
alter table schooldev."COURSE_ALLOCATION_INFO" rename column n_cai_section_id to section_id;
alter table schooldev."COURSE_ALLOCATION_INFO" rename column n_cai_batch_id to batch_id;
alter table schooldev."COURSE_ALLOCATION_INFO" rename column v_cai_created_by to created_by;
alter table schooldev."COURSE_ALLOCATION_INFO" rename column t_cai_created_at to created_at;
alter table schooldev."COURSE_ALLOCATION_INFO" rename column v_cai_modified_by to modified_by;
alter table schooldev."COURSE_ALLOCATION_INFO" rename column t_cai_modified_at to modified_at;
alter table schooldev."COURSE_ALLOCATION_INFO" rename column v_cai_active_flag to active_flag;
alter table schooldev."COURSE_ALLOCATION_INFO" rename column n_cai_school_id to school_id;
alter table schooldev."COURSE_ALLOCATION_INFO" rename column d_cai_joiningdate to joining_date;
alter table schooldev."COURSE_ALLOCATION_INFO" rename column n_cai_joining_period_id to joining_period_id;
alter table schooldev."COURSE_ALLOCATION_INFO" rename column v_cai_student_registration_number to student_registration_number;
ALTER TABLE schooldev."COURSE_ALLOCATION_INFO" ALTER COLUMN course_allocation_id SET NOT NULL;
ALTER TABLE schooldev."COURSE_ALLOCATION_INFO" ALTER COLUMN course_allocation_id SET DEFAULT nextval('schooldev."SEQ_MAST_COURSE_ALLOCATION_ID"');
ALTER TABLE schooldev."COURSE_ALLOCATION_INFO" ALTER COLUMN course_period_id DROP NOT NULL;
ALTER TABLE schooldev."COURSE_ALLOCATION_INFO" ALTER COLUMN joining_date DROP NOT NULL;
ALTER TABLE schooldev."COURSE_ALLOCATION_INFO" ALTER COLUMN joining_period_id DROP NOT NULL;
alter table schooldev."STUDENT_DETAILS_INFO" ALTER COLUMN school_id set default(1);

alter table schooldev."RFID_MAPPINGS" ALTER COLUMN school_id set default(1);
ALTER TABLE schooldev."RFID_MAPPINGS" ALTER COLUMN rf_id SET DEFAULT nextval('schooldev."SEQ_MAST_RFID_MAPPING"');

alter table schooldev."USER_FP_CARD" rename column cardsn to card_sn;
alter table schooldev."USER_FP_CARD" rename column accesscard_serial_no to access_card_serial_no;
alter table schooldev."USER_FP_CARD" rename column automatch to auto_match;
alter table schooldev."USER_FP_CARD" rename column smartcard to smart_card;
alter table schooldev."USER_FP_CARD" rename column fingerprint to finger_print;
alter table schooldev."USER_FP_CARD" rename column fingerprint_image to finger_print_image;
alter table schooldev."USER_FP_CARD" ALTER COLUMN school_id set default(1);

alter table schooldev.roles rename column company_id to school_id;
alter table schooldev.roles rename column active_status to active_flag;
alter table schooldev.roles ALTER COLUMN school_id set default(1);

ALTER TABLE schooldev.departments_details ALTER COLUMN department_id SET NOT NULL;
ALTER TABLE schooldev.departments_details ALTER COLUMN department_id SET DEFAULT nextval('schooldev.department_id_seq');

ALTER TABLE schooldev."HOSTEL_ROOM_INFO" RENAME COLUMN n_hri_roomid TO room_id;
ALTER TABLE schooldev."HOSTEL_ROOM_INFO" RENAME COLUMN v_hri_roomno TO room_no;
ALTER TABLE schooldev."HOSTEL_ROOM_INFO" RENAME COLUMN n_hri_buildingid TO building_id;
ALTER TABLE schooldev."HOSTEL_ROOM_INFO" RENAME COLUMN n_hri_capacity TO capacity;
ALTER TABLE schooldev."HOSTEL_ROOM_INFO" RENAME COLUMN n_hri_occupied TO occupied;
ALTER TABLE schooldev."HOSTEL_ROOM_INFO" RENAME COLUMN b_hri_isvacation TO is_vacation;
ALTER TABLE schooldev."HOSTEL_ROOM_INFO" RENAME COLUMN n_hri_room_size TO room_size;
ALTER TABLE schooldev."HOSTEL_ROOM_INFO" RENAME COLUMN n_hri_vac_capacity TO vac_capacity;
alter table schooldev."HOSTEL_ROOM_INFO" rename column v_hri_created_by to created_by;
alter table schooldev."HOSTEL_ROOM_INFO" rename column t_hri_created_at to created_at;
alter table schooldev."HOSTEL_ROOM_INFO" rename column v_hri_modified_by to modified_by;
alter table schooldev."HOSTEL_ROOM_INFO" rename column t_hri_modified_at to modified_at;
alter table schooldev."HOSTEL_ROOM_INFO" rename column v_hri_active_flag to active_flag;
alter table schooldev."HOSTEL_ROOM_INFO" rename column n_hri_schoolid to school_id;
alter table schooldev."HOSTEL_ROOM_INFO" rename column v_hri_official_guest_status to official_guest_status;
alter table schooldev."HOSTEL_ROOM_INFO" ALTER COLUMN school_id set default(1);

ALTER TABLE schooldev."HOSTEL_ROOM_INFO" ALTER COLUMN room_id SET NOT NULL;
ALTER TABLE schooldev."HOSTEL_ROOM_INFO" ALTER COLUMN room_id SET DEFAULT nextval('schooldev.room_id_seq');

alter table schooldev."MESS_VENDOR_MASTER" rename column active_status to active_flag;
alter table schooldev."MESS_VENDOR_MASTER" ALTER COLUMN school_id set default(1);
ALTER TABLE schooldev."MESS_VENDOR_MASTER" ALTER COLUMN user_name DROP NOT NULL;
ALTER TABLE schooldev."MESS_VENDOR_MASTER" ALTER COLUMN "password" DROP NOT NULL;

ALTER TABLE schooldev."MESS_MASTER" ALTER COLUMN school_id set default(1);
ALTER TABLE schooldev."MESS_MASTER" RENAME COLUMN active_status to active_flag;
ALTER TABLE schooldev."MESS_MASTER" RENAME COLUMN v_mm_fromdate TO from_date;
ALTER TABLE schooldev."MESS_MASTER" RENAME COLUMN v_mm_todate TO to_date;
ALTER TABLE schooldev."MESS_MASTER" RENAME COLUMN v_mm_types TO mess_type;
ALTER TABLE schooldev."MESS_MASTER" RENAME COLUMN v_mm_floor_name TO mess_floor_name;
ALTER TABLE schooldev."MESS_MASTER" ALTER COLUMN is_jainfood TYPE boolean USING is_jainfood::boolean;
ALTER TABLE schooldev."MESS_MASTER" ALTER COLUMN is_food_court TYPE boolean USING is_food_court::boolean;
ALTER TABLE schooldev."MESS_MASTER" ALTER COLUMN description SET NOT NULL;
UPDATE schooldev."MESS_MASTER" set mess_head = '' where mess_head IS NULL;
ALTER TABLE schooldev."MESS_MASTER" ALTER COLUMN mess_head SET NOT NULL;
UPDATE schooldev."MESS_MASTER" set mess_floor_name = '' where mess_floor_name IS NULL;
ALTER TABLE schooldev."MESS_MASTER" ALTER COLUMN mess_floor_name SET NOT NULL;
UPDATE schooldev."MESS_MASTER" set mess_type = '' where mess_type IS NULL;
ALTER TABLE schooldev."MESS_MASTER" ALTER COLUMN mess_type SET NOT NULL;
UPDATE schooldev."MESS_MASTER" set gender_option = '' where gender_option IS NULL;
ALTER TABLE schooldev."MESS_MASTER" ALTER COLUMN gender_option SET NOT NULL;
ALTER TABLE schooldev."MESS_MASTER" RENAME COLUMN is_jainfood TO is_jain_food;
ALTER TABLE schooldev."MESS_MASTER" RENAME COLUMN mess_allottedcapacity TO mess_allotted_capacity;
ALTER TABLE schooldev."MESS_MASTER" ADD CONSTRAINT "MESS_MASTER_pkey" UNIQUE (mess_master_id);
-- ALTER TABLE schooldev."MESS_MASTER" ADD COLUMN online_coupon boolean;
ALTER TABLE schooldev."MESS_MASTER" ADD food_court_amount float8 NULL;

alter table schooldev."MESS_TERMINAL" ALTER COLUMN school_id set default(1);
alter table schooldev."MESS_TERMINAL" rename column active_status to active_flag;

ALTER TABLE schooldev."STAFF_DESIGNATION_MASTER" RENAME COLUMN n_sdm_designation_id TO designation_id;
ALTER TABLE schooldev."STAFF_DESIGNATION_MASTER" RENAME COLUMN v_sdm_designation_name TO designation_name;
ALTER TABLE schooldev."STAFF_DESIGNATION_MASTER" RENAME COLUMN v_sdm_active_status TO active_status;
ALTER TABLE schooldev."STAFF_DESIGNATION_MASTER" RENAME COLUMN v_sdm_created_by TO created_by;
ALTER TABLE schooldev."STAFF_DESIGNATION_MASTER" RENAME COLUMN t_sdm_created_at TO created_at;
ALTER TABLE schooldev."STAFF_DESIGNATION_MASTER" RENAME COLUMN v_sdm_modified_by TO modified_by;
ALTER TABLE schooldev."STAFF_DESIGNATION_MASTER" RENAME COLUMN t_sdm_modified_at TO modified_at;
ALTER TABLE schooldev."STAFF_DESIGNATION_MASTER" RENAME COLUMN n_sdm_school_id TO school_id;
ALTER TABLE schooldev."STAFF_DESIGNATION_MASTER" RENAME COLUMN active_status TO active_flag;

ALTER TABLE schooldev."MESS_SESSIONS" RENAME COLUMN n_ms_mess_id TO mess_id;
ALTER TABLE schooldev."MESS_SESSIONS" RENAME COLUMN v_ms_session_name TO session_name;
ALTER TABLE schooldev."MESS_SESSIONS" RENAME COLUMN t_ms_start_time TO start_time;
ALTER TABLE schooldev."MESS_SESSIONS" RENAME COLUMN t_ms_end_time TO end_time;
ALTER TABLE schooldev."MESS_SESSIONS" RENAME COLUMN v_ms_created_by TO created_by;
ALTER TABLE schooldev."MESS_SESSIONS" RENAME COLUMN t_ms_created_at TO created_at;
ALTER TABLE schooldev."MESS_SESSIONS" RENAME COLUMN v_ms_modified_by TO modified_by;
ALTER TABLE schooldev."MESS_SESSIONS" RENAME COLUMN t_ms_modified_at TO modified_at;
ALTER TABLE schooldev."MESS_SESSIONS" RENAME COLUMN v_ms_active_flag TO active_flag;
ALTER TABLE schooldev."MESS_SESSIONS" RENAME COLUMN n_ms_school_id TO school_id;
ALTER TABLE schooldev."MESS_SESSIONS" ALTER COLUMN school_id set default(1);

ALTER TABLE schooldev."MESS_SESSIONS_MASTER" RENAME COLUMN v_msm_session_code TO session_code;
ALTER TABLE schooldev."MESS_SESSIONS_MASTER" RENAME COLUMN v_msm_session_name TO session_name;
ALTER TABLE schooldev."MESS_SESSIONS_MASTER" RENAME COLUMN v_msm_created_by TO created_by;
ALTER TABLE schooldev."MESS_SESSIONS_MASTER" RENAME COLUMN t_msm_created_at TO created_at;
ALTER TABLE schooldev."MESS_SESSIONS_MASTER" RENAME COLUMN v_msm_modified_by TO modified_by;
ALTER TABLE schooldev."MESS_SESSIONS_MASTER" RENAME COLUMN t_msm_modified_at TO modified_at;
ALTER TABLE schooldev."MESS_SESSIONS_MASTER" RENAME COLUMN v_msm_active_flag TO active_flag;
ALTER TABLE schooldev."MESS_SESSIONS_MASTER" RENAME COLUMN n_msm_school_id TO school_id;
ALTER TABLE schooldev."MESS_SESSIONS_MASTER" ALTER COLUMN school_id set default(1);

ALTER TABLE schooldev.course_master ALTER COLUMN school_id set default(1);
ALTER TABLE schooldev.departments_details ALTER COLUMN school_id set default(1);

ALTER TABLE schooldev."ASSET_CATEGORY_INFO" RENAME COLUMN n_aci_assetcategoryid TO asset_category_id;
ALTER TABLE schooldev."ASSET_CATEGORY_INFO" RENAME COLUMN v_aci_assetcategory TO asset_category;
ALTER TABLE schooldev."ASSET_CATEGORY_INFO" RENAME COLUMN v_aci_created_by TO created_by;
ALTER TABLE schooldev."ASSET_CATEGORY_INFO" RENAME COLUMN t_aci_created_at TO created_at;
ALTER TABLE schooldev."ASSET_CATEGORY_INFO" RENAME COLUMN v_aci_modified_by TO modified_by;
ALTER TABLE schooldev."ASSET_CATEGORY_INFO" RENAME COLUMN t_aci_modified_at TO modified_at;
ALTER TABLE schooldev."ASSET_CATEGORY_INFO" RENAME COLUMN v_aci_active_flag TO active_flag;
ALTER TABLE schooldev."ASSET_CATEGORY_INFO" RENAME COLUMN n_aci_school_id TO school_id;
ALTER TABLE schooldev."ASSET_CATEGORY_INFO" RENAME COLUMN n_aci_minor_repair_cost TO minor_repair_cost;
ALTER TABLE schooldev."ASSET_CATEGORY_INFO" RENAME COLUMN n_aci_major_repair_cost TO major_repair_cost;
ALTER TABLE schooldev."ASSET_CATEGORY_INFO" RENAME COLUMN n_aci_replacement_cost TO replacement_cost;
ALTER TABLE schooldev."ASSET_CATEGORY_INFO" RENAME COLUMN v_aci_assetcategory_description TO asset_category_description;
ALTER TABLE schooldev."ASSET_CATEGORY_INFO" RENAME COLUMN v_aci_assetcategory_shortcode TO asset_category_shortcode;
ALTER TABLE schooldev."ASSET_CATEGORY_INFO" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."ASSET_CATEGORY_INFO" ALTER COLUMN asset_category_id SET DEFAULT nextval('schooldev.asset_category_id_seq');

ALTER TABLE schooldev."ASSET_MAINTENANCE_TYPE" ALTER COLUMN school_id SET DEFAULT 1;

ALTER TABLE schooldev."ASSET_INVENTROTY_INFO" RENAME TO "ASSET_INVENTORY_INFO";
-- ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetid TO asset_id;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN n_aii_assetcategoryid TO asset_category_id;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetname TO asset_name;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetownershipstatus TO asset_ownership_status;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetvehiclenumber TO asset_vehicle_number;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetvehiclercnumber TO asset_vehicle_rc_number;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetvehicleregnumber TO asset_vehicle_reg_number;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN n_aii_seatingcapacity TO seating_capacity;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN n_aii_assetquantity TO asset_quantity;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN d_aii_assetprice TO asset_price;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN d_aii_assetdiscount TO asset_discount;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN d_aii_assettax TO asset_tax;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN d_aii_assetnetamount TO asset_net_amount;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetlocation TO asset_location;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetinusestatusflag TO asset_in_use_status_flag;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN n_aii_assetbillno TO asset_bill_no;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN t_aii_assetpurchasedate TO asset_purchase_date;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN t_aii_assetwarantydate TO asset_warranty_date;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetdescription TO asset_description;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetsuppliername TO asset_supplier_name;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetsupplieraddress TO asset_supplier_address;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN n_aii_assetsupplierlandno TO asset_supplier_land_no;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN n_aii_assetsuppliermobileno TO asset_supplier_mobile_no;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetconditionstatus TO asset_condition_status;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetconditionremarks TO asset_condition_remarks;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_created_by TO created_by;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN t_aii_created_at TO created_at;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_modified_by TO modified_by;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN t_aii_modified_at TO modified_at;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_active_flag TO active_flag;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assettransportstatus TO asset_transport_status;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN n_aii_school_id TO school_id;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetlender_id TO asset_lender_id;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetlender_type TO asset_lender_type;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetvehiclechasisnumber TO asset_vehicle_chassis_number;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetvehiclemake TO asset_vehicle_make;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetvehiclemodel TO asset_vehicle_model;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetvehiclepermit TO asset_vehicle_permit;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN t_aii_assetfc TO asset_fc;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN t_aii_assetroadtax TO asset_road_tax;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN t_aii_assetinsurance TO asset_insurance;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN t_aii_assetpollution TO asset_pollution;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN t_aii_assetservice TO asset_service;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN d_aii_assetcondition_date TO asset_condition_date;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_asset_condition TO asset_condition;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN v_aii_assetcode TO asset_code;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" ALTER COLUMN school_id SET DEFAULT(1);
-- ALTER TABLE schooldev."ASSET_INVENTORY_INFO" RENAME COLUMN asset_id TO v_aii_assetid;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" ADD COLUMN asset_id bigint;
UPDATE schooldev."ASSET_INVENTORY_INFO" set asset_id = cast(v_aii_assetid as bigint);
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" DROP COLUMN v_aii_assetid;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" ADD CONSTRAINT "ASSET_INVENTORY_INFO_pkey" PRIMARY KEY (asset_id);
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" ALTER COLUMN asset_id DROP DEFAULT;
ALTER TABLE schooldev."ASSET_INVENTORY_INFO" ALTER COLUMN asset_id SET DEFAULT nextval('schooldev.asset_id_seq');

ALTER TABLE schooldev."HOSTEL_ROOM_INVENTORY" RENAME COLUMN n_hriv_inventoryid TO inventory_id;
ALTER TABLE schooldev."HOSTEL_ROOM_INVENTORY" RENAME COLUMN n_hriv_roomid TO room_id;
ALTER TABLE schooldev."HOSTEL_ROOM_INVENTORY" RENAME COLUMN n_hriv_itemid TO item_id;
ALTER TABLE schooldev."HOSTEL_ROOM_INVENTORY" RENAME COLUMN n_hriv_categegoryid TO category_id;
ALTER TABLE schooldev."HOSTEL_ROOM_INVENTORY" RENAME COLUMN n_hriv_quantity TO quantity;
ALTER TABLE schooldev."HOSTEL_ROOM_INVENTORY" RENAME COLUMN v_hriv_created_by TO created_by;
ALTER TABLE schooldev."HOSTEL_ROOM_INVENTORY" RENAME COLUMN t_hriv_created_at TO created_at;
ALTER TABLE schooldev."HOSTEL_ROOM_INVENTORY" RENAME COLUMN v_hriv_modified_by TO modified_by;
ALTER TABLE schooldev."HOSTEL_ROOM_INVENTORY" RENAME COLUMN t_hriv_modified_at TO modified_at;
ALTER TABLE schooldev."HOSTEL_ROOM_INVENTORY" RENAME COLUMN v_hriv_active_flag TO active_flag;
ALTER TABLE schooldev."HOSTEL_ROOM_INVENTORY" RENAME COLUMN n_hriv_schoolid TO school_id;
ALTER TABLE schooldev."HOSTEL_ROOM_INVENTORY" RENAME COLUMN v_hriv_asset_condition TO asset_condition;
ALTER TABLE schooldev."HOSTEL_ROOM_INVENTORY" RENAME COLUMN d_hriv_assetcondition_date TO asset_condition_date;
ALTER TABLE schooldev."HOSTEL_ROOM_INVENTORY" RENAME COLUMN n_hriv_asset_code TO asset_code;
ALTER TABLE schooldev."HOSTEL_ROOM_INVENTORY" ALTER COLUMN school_id SET DEFAULT(1);
ALTER TABLE schooldev."HOSTEL_ROOM_INVENTORY" ALTER COLUMN inventory_id SET DEFAULT nextval('schooldev.inventory_id_seq');

ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_faculty_id TO faculty_id;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" ADD CONSTRAINT "FACULTY_PERSONAL_DETAILS_pkey" UNIQUE (faculty_id);
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_first_name TO first_name;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_middle_name TO middle_name;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_last_name TO last_name;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN d_fpd_date_of_birth TO date_of_birth;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_gender TO gender;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_birth_place TO birth_place;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_religion TO religion;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_caste TO caste;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_marital_status TO marital_status;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN n_fpd_contact_number TO contact_number;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN n_fpd_mobile_number TO mobile_number;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_email_address TO email_address;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_address_one TO address_one;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_address_two TO address_two;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_address_three TO address_three;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_city TO city;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_state TO state;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_taluk TO taluk;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_district TO district;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_country TO country;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_created_by TO created_by;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN t_fpd_created_at TO created_at;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_modified_by TO modified_by;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN t_fpd_modified_at TO modified_at;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_active_flag TO active_flag;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN n_fpd_stdcode TO std_code;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN n_fpd_pincode TO pin_code;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_teaching_or_nonteaching TO teaching_or_nonteaching;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN d_fpd_date_of_joining TO date_of_joining;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_accomodation TO accommodation;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_mode_of_transport TO mode_of_transport;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_driver_license_no TO driver_license_no;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN n_fpd_pickup_route_id TO pickup_route_id;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_driver_license_validupto TO driver_license_valid_upto;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN n_fpd_drop_route_id TO drop_route_id;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_stopping_name TO stopping_name;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN fpd_n_faculty_level_id TO faculty_level_id;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN n_fpd_schoolid TO school_id;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_employment_type TO employment_type;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN d_fpd_from_date TO from_date;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN d_fpd_to_date TO to_date;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN n_fpd_employee_id TO employee_id;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN n_fpd_city TO city_id;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN n_fpd_state TO state_id;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN n_fpd_country TO country_id;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN n_fpd_designation TO designation_id;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_file_no TO file_no;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN d_fpd_dateof_death TO date_of_death;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_emp_left TO emp_left;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_leaving_reason TO leaving_reason;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN d_fpd_dateof_leaving TO date_of_leaving;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN d_fpd_dateof_confirmation TO date_of_confirmation;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN n_fpd_continuous_period TO continuous_period;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN n_fpd_max_period TO max_period;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_library_card_id TO library_card_id;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_blood_group TO blood_group;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" RENAME COLUMN v_fpd_pan_number TO pan_number;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" ALTER COLUMN faculty_level_id set default 1;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" ALTER COLUMN school_id set default 1;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" ALTER COLUMN photo_status set default false;
ALTER TABLE schooldev."FACULTY_PERSONAL_DETAILS" ALTER COLUMN address_one TYPE VARCHAR(300);

ALTER TABLE schooldev."FACULTY_DEPARTMENT_ALLOCATION_INFO" RENAME COLUMN fdai_n_staff_dept_allc_info_id TO staff_dept_allc_info_id;
ALTER TABLE schooldev."FACULTY_DEPARTMENT_ALLOCATION_INFO" RENAME COLUMN fdai_v_faculty_id TO faculty_id;
ALTER TABLE schooldev."FACULTY_DEPARTMENT_ALLOCATION_INFO" RENAME COLUMN fdai_n_department_id TO department_id;
ALTER TABLE schooldev."FACULTY_DEPARTMENT_ALLOCATION_INFO" RENAME COLUMN fdai_n_school_id TO school_id;
ALTER TABLE schooldev."FACULTY_DEPARTMENT_ALLOCATION_INFO" RENAME COLUMN fdai_v_created_by TO created_by;
ALTER TABLE schooldev."FACULTY_DEPARTMENT_ALLOCATION_INFO" RENAME COLUMN fdai_t_created_at TO created_at;
ALTER TABLE schooldev."FACULTY_DEPARTMENT_ALLOCATION_INFO" RENAME COLUMN fdai_v_modified_by TO modified_by;
ALTER TABLE schooldev."FACULTY_DEPARTMENT_ALLOCATION_INFO" RENAME COLUMN fdai_t_modified_at TO modified_at;
ALTER TABLE schooldev."FACULTY_DEPARTMENT_ALLOCATION_INFO" RENAME COLUMN fdai_v_active_flag TO active_flag;
ALTER TABLE schooldev."FACULTY_DEPARTMENT_ALLOCATION_INFO" RENAME COLUMN fdai_n_employee_id TO employee_id;

ALTER TABLE schooldev.state RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev.country RENAME COLUMN active_status TO active_flag;

ALTER TABLE schooldev."SHOW_EVENT_MASTER" RENAME COLUMN active_status TO active_flag;

ALTER TABLE schooldev."ACCOUNT_HEAD" RENAME COLUMN active_status TO active_flag;

ALTER TABLE schooldev."GUEST_ACCOMMODATION_CHARGES" ALTER COLUMN school_id set default(1);

ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN n_hral_roomallotmentid TO room_allotment_id;
ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN n_hral_building_id TO building_id;
ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN n_hral_roomid TO room_id;
ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN v_harl_studentid TO student_id;
ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN v_hral_created_by TO created_by;
ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN t_hral_created_at TO created_at;
ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN v_hral_modified_by TO modified_by;
ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN t_hral_modified_at TO modified_at;
ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN v_hral_active_flag TO active_flag;
ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN n_hral_schoolid TO school_id;
ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" ALTER COLUMN school_id set default(1);
ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN d_hral_vacatedate TO vacate_date;
ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN v_hral_sub_roomid TO sub_room_id;
ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN v_hral_stay_from_date TO stay_from_date;
ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN d_hral_shifted_date TO shifted_date;
ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN n_hral_new_roomallotmentid TO new_roomallotment_id;
ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" ALTER COLUMN room_allotment_id SET DEFAULT nextval('schooldev."SEQ_MAST_HOSTEL_ROOM_ALLOTMENT_ID"');
-- ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" ADD workflow_flag bool DEFAULT FALSE;
-- ALTER TABLE schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" ADD workflow_date date NULL;

alter table schooldev."STUDENT_ROLLNO_CHANGE" rename column active_status to active_flag;
ALTER TABLE schooldev."STUDENT_ROLLNO_CHANGE" ALTER COLUMN school_id SET DEFAULT 1;

ALTER TABLE schooldev."SHOW_MASTER" RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."SHOW_MASTER" ALTER COLUMN school_id set default(1);

ALTER TABLE schooldev."SHOW_SEAT_DETAILS" ALTER COLUMN school_id set default(1);
ALTER TABLE schooldev."SHOW_SEAT_DETAILS" RENAME COLUMN active_status TO active_flag;

ALTER TABLE schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST" ALTER COLUMN occupancy TYPE character varying(64);
ALTER TABLE schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST" ALTER COLUMN gross_pay SET DEFAULT 0.0;
ALTER TABLE schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST" ALTER COLUMN school_id SET DEFAULT 1;

ALTER TABLE schooldev."IIT_W_STUDENT_WORKFLOW" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."IIT_W_WORKFLOW_MASTER" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."IIT_W_SCHOLARS_HOD_DETAILS" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."STUDENT_BLACK_LIST_DETAILS" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."IIT_W_STUDENT_FILES_INFORMATION" ALTER COLUMN school_id SET DEFAULT 1;

ALTER TABLE schooldev."HOSTEL_VACATING_ALLOWED_STUDENT" RENAME COLUMN v_hvas_student_id TO student_id;
ALTER TABLE schooldev."HOSTEL_VACATING_ALLOWED_STUDENT" RENAME COLUMN v_hvas_created_by TO created_by;
ALTER TABLE schooldev."HOSTEL_VACATING_ALLOWED_STUDENT" RENAME COLUMN t_hvas_created_at TO created_at;
ALTER TABLE schooldev."HOSTEL_VACATING_ALLOWED_STUDENT" RENAME COLUMN v_hvas_modified_by TO modified_by;
ALTER TABLE schooldev."HOSTEL_VACATING_ALLOWED_STUDENT" RENAME COLUMN t_hvas_modified_at TO modified_at;
ALTER TABLE schooldev."HOSTEL_VACATING_ALLOWED_STUDENT" RENAME COLUMN v_hvas_active_flag TO active_flag;
ALTER TABLE schooldev."HOSTEL_VACATING_ALLOWED_STUDENT" RENAME COLUMN n_hvas_school_id TO school_id;
ALTER TABLE schooldev."HOSTEL_VACATING_ALLOWED_STUDENT" ALTER COLUMN school_id SET DEFAULT 1;

ALTER TABLE schooldev."IIT_W_VACATING_HOSTEL_STUDENT_WORKFLOW" ALTER COLUMN school_id SET DEFAULT 1;

ALTER TABLE schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST" ALTER COLUMN donation_amount SET DEFAULT 0;
ALTER TABLE schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST" ADD COLUMN bicycle_declaration boolean;

ALTER TABLE schooldev."STUDENT_HOSTEL_ENROLLMENT_DATE_CONFIGURATION" ALTER COLUMN school_id SET DEFAULT 1;

ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN v_ml_menu_menuheading to menu_menuheading;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN n_ml_menu_menuorder to menu_menuorder;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN n_ml_menu_mainmenuorder to menu_mainmenuorder;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN n_ml_menu_submenuorder to menu_submenuorder;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN n_ml_menu_subsubmenuorder to menu_subsubmenuorder;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN v_ml_menu_urlpath to menu_urlpath;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN n_ml_menu_menuid to menu_menuid;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN v_ml_menu_imagename to menu_imagename;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN v_ml_menu_menutype to menu_menutype;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN v_ml_menu_activflag to active_flag;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN menu_menuheading to menu_heading;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN menu_menuorder to menu_order;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN menu_menuid to menu_id;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN menu_menutype to menu_type;
ALTER TABLE schooldev."MENU_LIST" ADD icon_name varchar DEFAULT ''::varchar;
ALTER TABLE schooldev."MENU_LIST" ADD banner_name varchar DEFAULT ''::varchar;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN menu_imagename to image_name;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN menu_mainmenuorder to main_menu_order;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN menu_submenuorder to sub_menu_order;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN menu_subsubmenuorder to sub_sub_menu_order;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN menu_urlpath to url_path;
ALTER TABLE schooldev."MENU_LIST" RENAME COLUMN menu_heading to heading;

ALTER TABLE schooldev."USER_ROLES" RENAME COLUMN n_ur_role_id to role_id;
ALTER TABLE schooldev."USER_ROLES" RENAME COLUMN v_ur_role_description to role_description;
ALTER TABLE schooldev."USER_ROLES" RENAME COLUMN v_ur_created_by to created_by;
ALTER TABLE schooldev."USER_ROLES" RENAME COLUMN t_ur_created_at to created_at;
ALTER TABLE schooldev."USER_ROLES" RENAME COLUMN v_ur_updated_by to updated_by;
ALTER TABLE schooldev."USER_ROLES" RENAME COLUMN t_ur_updated_at to updated_at;
ALTER TABLE schooldev."USER_ROLES" RENAME COLUMN v_ur_active_flag to active_flag;
ALTER TABLE schooldev."USER_ROLES" RENAME COLUMN n_ur_menu_id to menu_id;
ALTER TABLE schooldev."USER_ROLES" RENAME COLUMN n_ur_schoolid to school_id;
ALTER TABLE schooldev."USER_ROLES" ADD COLUMN id BIGSERIAL;
ALTER TABLE schooldev."USER_ROLES" ADD CONSTRAINT user_roles_pkey PRIMARY KEY (id);

ALTER TABLE schooldev."STUDENT_COMPLAINT_DETAILS" ALTER COLUMN school_id set default(1);

ALTER TABLE schooldev."STUDENT_COMPLAINT_CONFIGURATION" ALTER COLUMN school_id SET DEFAULT 1;

ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN n_sgi_school_id TO school_id;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_school_name TO school_name;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN d_sgi_date_of_estd TO date_of_estd;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_medium TO medium;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_level TO "level";
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN n_sgi_student_strength TO student_strength;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN n_sgi_staff_strength TO staff_strength;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_hostel_available TO hostel_available;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN n_sgi_student_capacity_school TO student_capacity_school;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN n_sgi_student_capacity TO student_capacity;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN n_sgi_total_area_in_acre TO total_area_in_acre;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_address1 TO address1;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_city TO city;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_state TO state;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_taluk TO taluk;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_district TO district;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_country TO country;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN n_sgi_pincode TO pincode;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_aided_type TO aided_type;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN n_sgi_landline_contact_no1 TO landline_contact_no1;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN n_sgi_landline_contact_no1_ext TO landline_contact_no1_ext;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN n_sgi_landline_contact_no2 TO landline_contact_no2;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN n_sgi_landline_contact_no2_ext TO landline_contact_no2_ext;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_email TO email;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_website TO website;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_created_by TO created_by;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN t_sgi_created_at TO created_at;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_updated_by TO updated_by;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN t_sgi_updated_at TO updated_at;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_active_flag TO active_flag;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN n_sgi_std_code TO std_code;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN n_sgi_country_code TO country_code;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_location TO "location";
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_email_config TO email_config;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_trustname TO trust_name;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_registration_information TO registration_information;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_address TO address;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_description TO description;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_pan_number TO pan_number;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN v_sgi_tan_number TO tan_number;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN updated_at TO modified_at;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN updated_by TO modified_by;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN n_sgi_state TO state_id;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN n_sgi_city TO city_id;
ALTER TABLE schooldev."SCHOOL_GEOGRAPHY_INFO" RENAME COLUMN n_sgi_country TO country_id;

--- Show Student Table
ALTER TABLE schooldev."SHOW_STUDENT_DETAILS" RENAME COLUMN active_status TO active_flag;

------------------Mess_master_controller-------------------------------------------------------------
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_n_id TO id;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_v_month TO "month";
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_d_reg_begin_date TO reg_begin_date;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_v_reg_begin_time TO reg_begin_time;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_d_reg_end_date TO reg_end_date;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_v_reg_end_time TO reg_end_time;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_v_created_by TO created_by;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_t_created_at TO created_at;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_v_modified_by TO modified_by;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_t_modified_at TO modified_at;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_v_active_flag TO active_flag;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_n_school_id TO school_id;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_d_dining_fromdate TO dining_from_date;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_d_dining_todate TO dining_to_date;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_b_student_edit_status TO student_edit_status;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_b_student_device_registration_status TO student_device_registration_status;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_d_exchange_fromdate TO exchange_from_date;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_d_exchange_todate TO exchange_to_date;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_b_feedback_status TO feedback_status;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_v_current_active_flag TO current_active_flag;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_v_pushing_time TO pushing_time;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_v_pushing_date TO pushing_date;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_b_semester_begin TO semester_begin;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_d_allotment_fromdate TO allotment_from_date;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" RENAME COLUMN mmc_d_allotment_todate TO allotment_to_date;
ALTER TABLE schooldev."MESS_MASTER_CONTROLLER" ALTER COLUMN school_id set default(1);

-----------------------------Student_mess_details-----------------------------------------------------------------------------------------------
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN n_smd_id TO id;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN v_smd_student_id TO student_id;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN n_smd_mess_id TO mess_id;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN d_smd_from_date TO from_date;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN d_smd_to_date TO to_date;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN v_smd_user_ip TO user_ip;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN v_smd_created_by TO created_by;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN t_smd_created_at TO created_at;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN v_smd_modified_by TO modified_by;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN t_smd_modified_at TO modified_at;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN v_smd_active_flag TO active_flag;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN n_smd_school_id TO school_id;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN v_smd_hostel_signed TO hostel_signed;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN v_smd_mess_signed TO mess_signed;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN n_smd_alloted_slno TO allotted_sl_no;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN d_smd_change_fromdate TO change_from_date;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN d_smd_change_todate TO change_to_date;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN v_smd_current_active_flag TO current_active_flag;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN v_smd_pushremove_status TO push_remove_status;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN v_smd_exception_status TO exception_status;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN v_smd_exception_reason TO exception_reason;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN d_smd_exception_date TO exception_date;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN v_smd_pushstatus TO push_status;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN d_smd_pushdate TO push_date;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN n_smd_mmc_id TO mmc_id;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN d_smd_to_removedate TO to_remove_date;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN v_smd_remarks TO remarks;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" RENAME COLUMN d_smd_to_pushdate TO to_push_date;
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" ALTER COLUMN school_id set default(1);
ALTER TABLE schooldev."STUDENT_MESS_DETAILS" ADD COLUMN IF NOT EXISTS description TEXT;

-----------------------------STUDENT_MESS_CATERER_FEEDBACK----------------------------------------------
ALTER TABLE schooldev."STUDENT_MESS_CATERER_FEEDBACK" RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."STUDENT_MESS_CATERER_FEEDBACK" ALTER COLUMN school_id set default(1);

---------------------Feedback qualifier--------------------
ALTER TABLE schooldev."FEEDBACK_QUALIFIER" RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."FEEDBACK_QUALIFIER" ALTER COLUMN school_id set default(1);

------------------Feedbcak question------------------
ALTER TABLE schooldev."FEEDBACK_QUESTION" RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."FEEDBACK_QUESTION" ALTER COLUMN school_id set default(1);

ALTER TABLE schooldev."MESS_REGISTRATION_MAPPING" ALTER COLUMN school_id set default(1);

-------------------------------------Student mess priority registration--------------------
ALTER TABLE schooldev."STUDENT_MESS_PRIORITY_REGISTRATION" RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."STUDENT_MESS_PRIORITY_REGISTRATION" ALTER COLUMN school_id set default(1);

ALTER TABLE schooldev."STUDENT_MESS_LOGIN_ISSUE_PRIORITY" RENAME COLUMN mmc_n_id TO mmc_id;
ALTER TABLE schooldev."STUDENT_MESS_LOGIN_ISSUE_PRIORITY" RENAME COLUMN studentid TO student_id;
ALTER TABLE schooldev."STUDENT_MESS_LOGIN_ISSUE_PRIORITY" RENAME COLUMN priority_messid TO priority_mess_id;
ALTER TABLE schooldev."STUDENT_MESS_LOGIN_ISSUE_PRIORITY" RENAME COLUMN studentname TO student_name;
ALTER TABLE schooldev."STUDENT_MESS_LOGIN_ISSUE_PRIORITY" RENAME COLUMN hostelid TO hostel_id;
ALTER TABLE schooldev."STUDENT_MESS_LOGIN_ISSUE_PRIORITY" RENAME COLUMN roomno TO room_no;
ALTER TABLE schooldev."STUDENT_MESS_LOGIN_ISSUE_PRIORITY" RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."STUDENT_MESS_LOGIN_ISSUE_PRIORITY" ALTER COLUMN school_id set default(1);

-------------------------------------------------------------------------------------------------------
ALTER TABLE schooldev."STUDENT_EXCEESS_MESS_DETAILS" RENAME COLUMN n_semd_id TO id;
ALTER TABLE schooldev."STUDENT_EXCEESS_MESS_DETAILS" RENAME COLUMN v_semd_student_id TO student_id;
ALTER TABLE schooldev."STUDENT_EXCEESS_MESS_DETAILS" RENAME COLUMN n_semd_mess_id TO mess_id;
ALTER TABLE schooldev."STUDENT_EXCEESS_MESS_DETAILS" RENAME COLUMN n_semd_mmc_id TO mmc_id;
ALTER TABLE schooldev."STUDENT_EXCEESS_MESS_DETAILS" RENAME COLUMN d_semd_from_date TO from_date;
ALTER TABLE schooldev."STUDENT_EXCEESS_MESS_DETAILS" RENAME COLUMN d_semd_to_date TO to_date;
ALTER TABLE schooldev."STUDENT_EXCEESS_MESS_DETAILS" RENAME COLUMN v_semd_created_by TO created_by;
ALTER TABLE schooldev."STUDENT_EXCEESS_MESS_DETAILS" RENAME COLUMN t_semd_created_at TO created_at;
ALTER TABLE schooldev."STUDENT_EXCEESS_MESS_DETAILS" RENAME COLUMN v_semd_modified_by TO modified_by;
ALTER TABLE schooldev."STUDENT_EXCEESS_MESS_DETAILS" RENAME COLUMN t_semd_modified_at TO modified_at;
ALTER TABLE schooldev."STUDENT_EXCEESS_MESS_DETAILS" RENAME COLUMN v_semd_active_flag TO active_flag;
ALTER TABLE schooldev."STUDENT_EXCEESS_MESS_DETAILS" RENAME COLUMN n_semd_school_id TO school_id;
ALTER TABLE schooldev."STUDENT_EXCEESS_MESS_DETAILS" ALTER COLUMN school_id set default(1);

----------------------------------------------------------- Mess Rebate Widget -------------------------------------------------------
ALTER TABLE schooldev."IIT_A_MESS_REBATE" RENAME COLUMN hod_name TO validator_name;
ALTER TABLE schooldev."IIT_A_MESS_REBATE" RENAME COLUMN hod_email TO validator_email;
ALTER TABLE schooldev."IIT_A_MESS_REBATE" ALTER COLUMN id SET DEFAULT nextval('schooldev.mess_rebate_id_seq');
ALTER TABLE schooldev."IIT_A_MESS_REBATE" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."IIT_A_MESS_REBATE_WORKFLOW" ALTER COLUMN id SET DEFAULT nextval('schooldev.mess_rebate_workflow_id_seq');
ALTER TABLE schooldev."IIT_A_MESS_REBATE_WORKFLOW" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."IIT_A_MESS_REBATE" ADD COLUMN file_upload VARCHAR(300) NULL;

---- Mess To Card Transfer-----
ALTER TABLE schooldev."MESS_TO_CARD_AMOUNT_TRANSFER" ALTER COLUMN school_id SET DEFAULT 1;

---Ledger Report
ALTER TABLE schooldev."MESS_LEDGER_A" RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."MESS_LEDGER_B" RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."MESS_OPENING_BAL" RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."STUDENT_HOSTEL_PAYMENTS" ALTER COLUMN school_id set default(1);

ALTER TABLE schooldev."GUEST_FILES_INFORMATION" ALTER COLUMN school_id set default(1);
ALTER TABLE schooldev."GUEST_ACCOMMODATION_REQUEST" ALTER COLUMN school_id set default(1);
ALTER TABLE schooldev."GUEST_ACCOMMODATION_REQUEST" ADD secondary_amount int4 NULL;
ALTER TABLE schooldev."GUEST_ACCOMMODATION_GUEST_DETAILS" ALTER COLUMN school_id set default(1);
ALTER TABLE schooldev."GUEST_FILES_INFORMATION" ADD CONSTRAINT "GUEST_FILES_INFORMATION_PK" PRIMARY KEY (file_id);
ALTER TABLE schooldev."IIT_W_STUDENT_FILES_INFORMATION" ADD CONSTRAINT iit_w_student_files_information_pk PRIMARY KEY (file_id);
ALTER TABLE schooldev."IIT_W_STUDENT_FILES_INFORMATION" ALTER COLUMN file_id SET DEFAULT nextval('schooldev.file_id_seq');
ALTER TABLE schooldev."STUDENT_BIO_DATA_FAMILY_INFO" ADD proof_file_name varchar(256) NULL;

ALTER TABLE schooldev."SICK_FOOD_REQUEST" ALTER COLUMN school_id SET DEFAULT '1';
ALTER TABLE schooldev."SICK_FOOD_REQUEST" ADD delivery_option varchar(64) NULL;
ALTER TABLE schooldev."SICK_FOOD_REQUEST" ALTER COLUMN mess_type DROP NOT NULL;
ALTER TABLE schooldev."SICK_FOOD_DELIVERY_STATUS" ALTER COLUMN school_id SET DEFAULT '1';
ALTER TABLE schooldev."SICK_FOOD_DELIVERY_STATUS" ADD COLUMN food_delivery_status BOOLEAN;
ALTER TABLE schooldev."FINANCIAL_YEAR" RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."MESS_LEDGER_A" ALTER COLUMN companyid set default(1);
ALTER TABLE schooldev."MESS_LEDGER_B" ALTER COLUMN companyid set default(1);
ALTER TABLE schooldev."STUDENT_COMPLAINT_CONFIGURATION" rename COLUMN configuration_id to id;
ALTER TABLE schooldev."MESS_COUPON_USER_MAPPING" ALTER COLUMN school_id set default(1);
ALTER TABLE schooldev."CATERER_LEDGER_MAPPING" RENAME COLUMN acc_head TO acchead;
ALTER TABLE schooldev."CATERER_LEDGER_MAPPING" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."USER_MANAGEMENT_ONLINE" ADD COLUMN image_path VARCHAR(256) NULL;
ALTER TABLE schooldev."IIT_W_CANDIDATE_FILES_INFORMATION" ALTER COLUMN school_id set default(1);
ALTER TABLE schooldev."IIT_W_CANDIDATE_WORKFLOW" ALTER COLUMN school_id set default(1);
ALTER TABLE schooldev."IITM_GUEST_COUPON_CONFIG" ALTER COLUMN school_id SET DEFAULT 1;
-- ALTER TABLE schooldev."IITM_GUEST_COUPON_CONFIG" ADD PRIMARY KEY (config_id);

ALTER TABLE schooldev."MESS_ALLOCATION" RENAME active_status TO active_flag;
ALTER TABLE schooldev."MESS_ALLOCATION" alter column school_id set default(1);
ALTER TABLE schooldev."IIT_W_CANDIDATE_PERSONAL_DETAILS" ADD COLUMN image_name VARCHAR(256) NULL;

-- ALTER TABLE schooldev."IIT_WD_ROLE_TAB_SETTINGS" ADD PRIMARY KEY (id);
ALTER TABLE schooldev."IIT_WD_ROLE_TAB_SETTINGS" ADD COLUMN role_id bigint;

ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_TRANSACTION_DETAILS" ALTER COLUMN school_id SET DEFAULT 1;
-- ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_TRANSACTION_DETAILS" ADD status_message varchar NULL;
-- ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_TRANSACTION_DETAILS" ADD retry_count int4 NULL;

ALTER TABLE schooldev."IIT_W_CANDIDATE_STAY_REQUEST" ALTER COLUMN school_id set default(1);
ALTER TABLE schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW" ALTER COLUMN school_id set default(1);
ALTER TABLE schooldev.roles ALTER COLUMN role_id SET DEFAULT nextval('schooldev.roles_role_id_seq');

ALTER TABLE schooldev."STUDENT_DETAILS_INFO" ALTER COLUMN school_id SET DEFAULT(1);
ALTER TABLE schooldev."STUDENT_WELLNESS_CATEGORICAL_DATA" RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."STUDENT_WELLNESS_FOLLOWUP_DATA" RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."WELLNESS_USER_MANAGEMENT" RENAME COLUMN v_um_user_id TO user_id;
ALTER TABLE schooldev."WELLNESS_USER_MANAGEMENT" RENAME COLUMN v_um_username TO username;
ALTER TABLE schooldev."WELLNESS_USER_MANAGEMENT" RENAME COLUMN v_um_password TO password;
ALTER TABLE schooldev."WELLNESS_USER_MANAGEMENT" RENAME COLUMN v_um_created_by TO created_by;
ALTER TABLE schooldev."WELLNESS_USER_MANAGEMENT" RENAME COLUMN t_um_created_at TO created_at;
ALTER TABLE schooldev."WELLNESS_USER_MANAGEMENT" RENAME COLUMN v_um_modified_by TO modified_by;
ALTER TABLE schooldev."WELLNESS_USER_MANAGEMENT" RENAME COLUMN t_um_modified_at TO modified_at;
ALTER TABLE schooldev."WELLNESS_USER_MANAGEMENT" RENAME COLUMN v_um_active_flag TO active_flag;
ALTER TABLE schooldev."WELLNESS_USER_MANAGEMENT" RENAME COLUMN n_um_schoolid TO school_id;
ALTER TABLE schooldev."WELLNESS_USER_MANAGEMENT" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."MESS_LEDGER_B" ALTER COLUMN companyid set default(1);
ALTER TABLE schooldev."MESS_LEDGER_A" ALTER COLUMN companyid set default(1);
ALTER TABLE schooldev."FA_TRANSACTION_DETAILS" ALTER COLUMN school_id set default(1);
ALTER TABLE schooldev."FA_TRANSACTION_DETAILS" RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."IITM_GUEST_COUPON_MAPPINGS" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."IIT_WD_DASHBOARD_TAB_MASTER" ADD tab_url varchar NULL;
ALTER TABLE schooldev."IIT_WD_DASHBOARD_TAB_MASTER" ADD style varchar NULL;
ALTER TABLE schooldev."IIT_WD_DASHBOARD_TAB_MASTER" ALTER COLUMN id SET DEFAULT nextval('schooldev."IIT_WD_DASHBOARD_TAB_MASTER_id_seq"');
ALTER TABLE schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO" ALTER COLUMN schoolid SET DEFAULT 1;
ALTER TABLE schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN sub_roomid TO sub_room_id;
ALTER TABLE schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN roomallotmentid TO room_allotment_id;
ALTER TABLE schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN roomid TO room_id;
ALTER TABLE schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN requestid TO request_id;
ALTER TABLE schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO" RENAME COLUMN vacatedate TO vacate_date;

ALTER TABLE schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" ALTER COLUMN bulk_coupon SET DEFAULT false;
ALTER TABLE schooldev."IIT_HDC_COMPLAINT_FORMDETAILS" ALTER COLUMN school_id SET DEFAULT 1;
alter table schooldev."STUDENT_HOSTEL_PAYMENT_LATE_FEE_DETAILS" add column description text;
ALTER TABLE schooldev."IIT_W_STUDENT_MASTER_BULK_APPOINTMENT" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."IIT_W_STUDENT_BULK_APPOINTMENT" ALTER COLUMN school_id SET DEFAULT 1;
alter table schooldev."IIT_W_STUDENT_BULK_APPOINTMENT" add column gender varchar(10);
ALTER TABLE schooldev."IIT_W_STUDENT_BULK_APPOINTMENT_DETAILS" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."mess_inspection_report" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev.mess_inspection_report RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."STUDENT_BIO_DATA_FAMILY_INFO" ADD COLUMN proof_type varchar(64);
alter table schooldev."COMPLETE_MESS_REBATE_VIEW" owner to postgres;
ALTER TABLE schooldev."VACATION_MESS_ALLOWED_STUDENT" RENAME COLUMN n_vmas_id TO id;
ALTER TABLE schooldev."VACATION_MESS_ALLOWED_STUDENT" RENAME COLUMN v_vmas_student_id TO student_id;
ALTER TABLE schooldev."VACATION_MESS_ALLOWED_STUDENT" RENAME COLUMN v_vmas_created_by TO created_by;
ALTER TABLE schooldev."VACATION_MESS_ALLOWED_STUDENT" RENAME COLUMN t_vmas_created_at TO created_at;
ALTER TABLE schooldev."VACATION_MESS_ALLOWED_STUDENT" RENAME COLUMN v_vmas_modified_by TO modified_by;
ALTER TABLE schooldev."VACATION_MESS_ALLOWED_STUDENT" RENAME COLUMN t_vmas_modified_at TO modified_at;
ALTER TABLE schooldev."VACATION_MESS_ALLOWED_STUDENT" RENAME COLUMN v_vmas_active_flag TO active_flag;
ALTER TABLE schooldev."VACATION_MESS_ALLOWED_STUDENT" RENAME COLUMN n_vmas_school_id TO school_id;

ALTER TABLE schooldev."VACATION_MESS_CONTROLLER" RENAME COLUMN vmc_n_id TO id;
ALTER TABLE schooldev."VACATION_MESS_CONTROLLER" RENAME COLUMN vmc_v_month TO month;
ALTER TABLE schooldev."VACATION_MESS_CONTROLLER" RENAME COLUMN vmc_d_reg_begin_date TO reg_begin_date;
ALTER TABLE schooldev."VACATION_MESS_CONTROLLER" RENAME COLUMN vmc_v_reg_begin_time TO reg_begin_time;
ALTER TABLE schooldev."VACATION_MESS_CONTROLLER" RENAME COLUMN vmc_d_reg_end_date TO reg_end_date;
ALTER TABLE schooldev."VACATION_MESS_CONTROLLER" RENAME COLUMN vmc_v_reg_end_time TO reg_end_time;
ALTER TABLE schooldev."VACATION_MESS_CONTROLLER" RENAME COLUMN vmc_v_created_by TO created_by;
ALTER TABLE schooldev."VACATION_MESS_CONTROLLER" RENAME COLUMN vmc_t_created_at TO created_at;
ALTER TABLE schooldev."VACATION_MESS_CONTROLLER" RENAME COLUMN vmc_v_modified_by TO modified_by;
ALTER TABLE schooldev."VACATION_MESS_CONTROLLER" RENAME COLUMN vmc_t_modified_at TO modified_at;
ALTER TABLE schooldev."VACATION_MESS_CONTROLLER" RENAME COLUMN vmc_v_active_flag TO active_flag;
ALTER TABLE schooldev."VACATION_MESS_CONTROLLER" RENAME COLUMN vmc_n_school_id TO school_id;
ALTER TABLE schooldev."VACATION_MESS_CONTROLLER" RENAME COLUMN d_vmc_prd_fromdate TO prd_fromdate;
ALTER TABLE schooldev."VACATION_MESS_CONTROLLER" RENAME COLUMN d_vmc_prd_todate TO prd_todate;
ALTER TABLE schooldev."VACATION_MESS_CONTROLLER" RENAME COLUMN vmc_d_dining_from TO dining_from;
ALTER TABLE schooldev."VACATION_MESS_CONTROLLER" RENAME COLUMN vmc_d_dining_to TO dining_to;
ALTER TABLE schooldev."VACATION_MESS_CONTROLLER" RENAME COLUMN vmc_v_mess_type_to TO mess_type_to;
ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE" ALTER COLUMN id SET DEFAULT nextval('schooldev.IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE_SEQ');
ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_LEDGER_A" ALTER COLUMN voucher_no SET DEFAULT nextval('schooldev.IIT_PS_TEMP_ACCOM_LEDGER_A_SEQ');
ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_LEDGER_A" RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_LEDGER_B" RENAME COLUMN active_status TO active_flag;

ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_LEDGER_A" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_LEDGER_B" ALTER COLUMN companyid SET DEFAULT 1;

ALTER TABLE schooldev."STUDENT_MESS_CHANGE_WORKFLOW" RENAME COLUMN originally_mess_fromdate TO originally_mess_from_date;
ALTER TABLE schooldev."STUDENT_MESS_CHANGE_WORKFLOW" RENAME COLUMN originally_mess_todate TO originally_mess_to_date;
ALTER TABLE schooldev."STUDENT_MESS_CHANGE_WORKFLOW" RENAME COLUMN originally_messid TO originally_mess_id;
ALTER TABLE schooldev."STUDENT_MESS_CHANGE_WORKFLOW" RENAME COLUMN requested_mess_fromdate TO requested_mess_from_date;
ALTER TABLE schooldev."STUDENT_MESS_CHANGE_WORKFLOW" RENAME COLUMN requested_mess_todate TO requested_mess_to_date;
ALTER TABLE schooldev."STUDENT_MESS_CHANGE_WORKFLOW" RENAME COLUMN requested_messid TO requested_mess_id;
ALTER TABLE schooldev."STUDENT_MESS_CHANGE_WORKFLOW" RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."STUDENT_MESS_CHANGE_WORKFLOW" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."STUDENT_MESS_CHANGE_WORKFLOW" ADD COLUMN mmc_id bigint;

ALTER TABLE schooldev."SHAASTRA_SAARANG_PURCHASE_CLAIM" RENAME COLUMN v_sdi_studentid TO student_id;
ALTER TABLE schooldev."SHOW_STUDENT_DETAILS" RENAME COLUMN v_sdi_studentid TO student_id;

ALTER TABLE schooldev."WARDEN_INFO" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."WARDEN_INFO" ALTER COLUMN id SET DEFAULT nextval('schooldev.WARDEN_INFO_SEQ');

ALTER TABLE schooldev."WARDEN_HOSTEL_MAPPING" ALTER COLUMN school_id SET DEFAULT 1;

ALTER TABLE schooldev."STUDENT_EXCHANGE_PROGRAM" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."STUDENT_EXCHANGE_PROGRAM" RENAME COLUMN active_status TO active_flag;

ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_CATEGORY_CONFIG" RENAME COLUMN createdat TO created_at;
ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_CATEGORY_CONFIG" RENAME COLUMN createdby TO created_by;
ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_CATEGORY_CONFIG" RENAME COLUMN schoolid TO school_id;
ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_CATEGORY_CONFIG" RENAME COLUMN modifiedby TO modified_by;
ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_CATEGORY_CONFIG" RENAME COLUMN modifiedat TO modified_at;
ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_CATEGORY_CONFIG" RENAME COLUMN rebate_fromdate TO effective_date;
ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_CATEGORY_CONFIG" ALTER COLUMN school_id SET DEFAULT 1;

ALTER TABLE schooldev."IIT_HDC_COMPLAINT_FORMDETAILS" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."WARDEN_INCHARGE_DETAILS" ALTER COLUMN school_id SET DEFAULT 1;

ALTER TABLE schooldev."STUDENT_DEBIT_ACCHEAD_CONFIG" RENAME COLUMN active_status TO active_flag;

ALTER TABLE schooldev."HOSTEL_BIOMETRIC_TERMINAL" RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."HOSTEL_BIOMETRIC_TERMINAL" ALTER COLUMN school_id SET DEFAULT 1;

ALTER TABLE schooldev."IITM_GUEST_COUPON_ACCESS_TOKEN" RENAME COLUMN active_status TO active_flag;
ALTER TABLE schooldev."IITM_GUEST_COUPON_ACCESS_TOKEN" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."MESS_QR_APPLICATION" ALTER COLUMN school_id SET DEFAULT 1;

alter table schooldev."MESS_LEDGER_A" ALTER COLUMN screen_type TYPE varchar(128);

alter table schooldev."SETTLEMENT_HISTORY" add column modified_at date;
alter table schooldev."SETTLEMENT_HISTORY" add column modified_by varchar(64);

alter table schooldev."USER_AUDIT_TRAIL" rename column v_at_action_committype to action_commit_type;
ALTER TABLE schooldev."USER_AUDIT_TRAIL" RENAME COLUMN at_id TO id;
ALTER TABLE schooldev."USER_AUDIT_TRAIL" RENAME COLUMN t_at_date TO date;
ALTER TABLE schooldev."USER_AUDIT_TRAIL" RENAME COLUMN v_at_user_id TO user_id;
ALTER TABLE schooldev."USER_AUDIT_TRAIL" RENAME COLUMN v_at_user_name TO user_name;
ALTER TABLE schooldev."USER_AUDIT_TRAIL" RENAME COLUMN v_at_ip_address TO ip_address;
ALTER TABLE schooldev."USER_AUDIT_TRAIL" RENAME COLUMN v_at_formname TO form_name;
ALTER TABLE schooldev."USER_AUDIT_TRAIL" RENAME COLUMN v_at_action_path TO action_path;
ALTER TABLE schooldev."USER_AUDIT_TRAIL" RENAME COLUMN v_at_classmethodname TO class_method_name;
ALTER TABLE schooldev."USER_AUDIT_TRAIL" RENAME COLUMN n_at_school_id TO school_id;

ALTER TABLE schooldev."HOSTEL_USER_MAPPING" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST" ALTER COLUMN school_id SET DEFAULT 1;


ALTER TABLE schooldev."IITM_STUDENT_ROOM_ASSET_DETAILS" ALTER COLUMN school_id SET DEFAULT 1;

ALTER TABLE schooldev."SHOW_STUDENT_DETAILS" ADD contact_no int8 NULL;

ALTER TABLE schooldev."IIT_W_APPOINTMENT_REQUEST_HISTORY" ALTER COLUMN school_id SET DEFAULT 1;


create or replace view schooldev.current_mess_period as
select * from schooldev."MESS_MASTER_CONTROLLER"
where CURRENT_DATE between dining_from_date and dining_to_date and active_flag = 'Y';

create or replace view schooldev.next_mess_period as
select nmp.* from schooldev."MESS_MASTER_CONTROLLER" nmp
                      left join schooldev.current_mess_period cmp on (cmp.active_flag = 'Y')
where nmp.dining_from_date > cmp.dining_to_date and nmp.active_flag = 'Y';

create or replace view schooldev."CURRENT_MESS_DETAILS_VIEW" as
SELECT c.student_id,
       c.mess_id,
       b.id AS mess_period_id,
       b.dining_from_date,
       b.dining_to_date,
       b.month,
       d.mess_name,
       d.description,
       d.is_food_court,
       c.push_remove_status,
       c.to_push_date,
       c.to_remove_date,
       c.push_status,
       c.push_date
FROM schooldev.current_mess_period b
         JOIN schooldev."STUDENT_MESS_DETAILS" c
              ON c.mmc_id = b.id AND c.current_active_flag = 'Y'::bpchar AND c.active_flag = 'Y'::bpchar
         JOIN schooldev."MESS_MASTER" d ON c.mess_id = d.mess_master_id AND d.active_flag = 'Y'::bpchar;

CREATE OR REPLACE VIEW schooldev."STUDENT_SEARCH_VIEW" AS
SELECT (SELECT dost_election_department.dept_name
        FROM schooldev.dost_election_department
        WHERE dost_election_department.dept_code::text = "substring"("STUDENT_DETAILS_INFO".student_id::text, 1, 2)
    OR "substring"("STUDENT_DETAILS_INFO".student_id::text, 1, 2) =
       dost_election_department.alt_dept_code::text)                                                              AS dept_name,
       "STUDENT_DETAILS_INFO".student_id,
       COALESCE("STUDENT_BIO_DATA_FORM_DETAILS".student_name,
                ((("STUDENT_DETAILS_INFO".first_name::text || ' '::text) ||
                  "STUDENT_DETAILS_INFO".last_name::text))::character varying(120))                                      AS student_name,
       CASE
           WHEN "HOSTEL_ROOM_ALLOTMENT_INFO".student_id IS NOT NULL AND
                ("HOSTEL_ROOM_ALLOTMENT_INFO".vacate_date IS NULL OR "HOSTEL_ROOM_ALLOTMENT_INFO".is_missing = true) AND
                ("HOSTEL_ROOM_ALLOTMENT_INFO".shifted_date IS NULL OR "HOSTEL_ROOM_ALLOTMENT_INFO".is_missing = true)
               THEN 'active'::character varying
           ELSE NULL::character varying
END                                                                                                           AS student_status,
       CASE
           WHEN "HOSTEL_ROOM_ALLOTMENT_INFO".is_missing = false THEN "HOSTEL_MASTER".hostel_name
           ELSE NULL::character varying
END                                                                                                           AS hostel_name,
       CASE
           WHEN "HOSTEL_ROOM_ALLOTMENT_INFO".is_missing = false THEN "HOSTEL_MASTER".hostel_id
           ELSE NULL::bigint
END                                                                                                           AS hostel_id,
       "HOSTEL_FLOOR_MASTER".floor_name,
       "HOSTEL_FLOOR_MASTER".floor_id,
       CASE
           WHEN "HOSTEL_ROOM_ALLOTMENT_INFO".is_missing = false THEN "HOSTEL_ROOM_INFO".room_no
           ELSE NULL::character varying
END                                                                                                           AS room_number,
       "HOSTEL_ROOM_ALLOTMENT_INFO".sub_room_id                                                                          AS seat,
       "HOSTEL_ROOM_INFO".room_id,
       "HOSTEL_ROOM_ALLOTMENT_INFO".room_allotment_id,
       "HOSTEL_ROOM_ALLOTMENT_INFO".vacate_date,
       "HOSTEL_ROOM_ALLOTMENT_INFO".shifted_date,
       "CURRENT_MESS_DETAILS_VIEW".mess_period_id,
       "CURRENT_MESS_DETAILS_VIEW".dining_from_date,
       "CURRENT_MESS_DETAILS_VIEW".dining_to_date,
       "CURRENT_MESS_DETAILS_VIEW".mess_id,
       "CURRENT_MESS_DETAILS_VIEW".mess_name,
       "STUDENT_BIO_DATA_FORM_DETAILS".mess_name                                                                         AS mess_preference,
       "STUDENT_BIO_DATA_FORM_DETAILS".bio_data_id,
       "STUDENT_BIO_DATA_FORM_DETAILS".application_number,
       "STUDENT_DETAILS_INFO".gender,
       "STUDENT_BIO_DATA_FORM_DETAILS".dob,
       "STUDENT_BIO_DATA_FORM_DETAILS".blood_group,
       "STUDENT_BIO_DATA_FORM_DETAILS".category,
       "STUDENT_BIO_DATA_FORM_DETAILS".student_mobile,
       "STUDENT_BIO_DATA_FORM_DETAILS".student_personal_email,
       concat(TRIM(BOTH FROM "STUDENT_DETAILS_INFO".student_id), (SELECT scd.config_value
                                                                  FROM schooldev."SIMS_CONFIG_DATA" scd
                                                                  WHERE scd.config_key::text = 'STUDENT_MAIL_ID'::text)) AS student_iitm_smail,
       "STUDENT_BIO_DATA_FORM_DETAILS".city,
       "STUDENT_BIO_DATA_FORM_DETAILS".state,
       "STUDENT_BIO_DATA_FORM_DETAILS".country,
       "STUDENT_BIO_DATA_FORM_DETAILS".pin_code,
       "STUDENT_BIO_DATA_FORM_DETAILS".student_address,
       "STUDENT_DETAILS_INFO".previous_id,
       "STUDENT_BIO_DATA_FORM_DETAILS".aadhaar_number,
       "STUDENT_BIO_DATA_FORM_DETAILS".pan_number,
       "STUDENT_BIO_DATA_FORM_DETAILS".guardian_status,
       "STUDENT_BIO_DATA_FORM_DETAILS".signed_parent_name,
       "STUDENT_BIO_DATA_FORM_DETAILS".faculty_name,
       "STUDENT_BIO_DATA_FORM_DETAILS".faculty_contact_no,
       "STUDENT_BIO_DATA_FORM_DETAILS".faculty_email,
       CASE
           WHEN "STUDENT_BIO_DATA_FORM_DETAILS".pwd_percentage IS NOT NULL AND
                "STUDENT_BIO_DATA_FORM_DETAILS".pwd_percentage > 0 THEN true
           ELSE false
END                                                                                                           AS pwd_status,
       "STUDENT_BIO_DATA_FORM_DETAILS".pwd,
       "STUDENT_BIO_DATA_FORM_DETAILS".pwd_percentage,
       "STUDENT_DETAILS_INFO".settlement_flag,
       "STUDENT_DETAILS_INFO".day_scholar,
       "STUDENT_DETAILS_INFO".vacation_category,
       "HOSTEL_ROOM_ALLOTMENT_INFO".is_missing,
       "HOSTEL_MASTER".hostel_office_email,
       "USER_MANAGEMENT".authentication_server                                                                           AS auth,
       "STUDENT_DETAILS_INFO".active_flag
FROM schooldev."STUDENT_DETAILS_INFO"
         LEFT JOIN schooldev."STUDENT_BIO_DATA_FORM_DETAILS"
                   ON "STUDENT_DETAILS_INFO".student_id::text = "STUDENT_BIO_DATA_FORM_DETAILS".student_id::text AND
                      "STUDENT_BIO_DATA_FORM_DETAILS".active_flag = 'Y'::bpchar
         LEFT JOIN schooldev."HOSTEL_ROOM_ALLOTMENT_INFO"
                   ON "STUDENT_DETAILS_INFO".student_id::text = "HOSTEL_ROOM_ALLOTMENT_INFO".student_id::text AND
                      "HOSTEL_ROOM_ALLOTMENT_INFO".active_flag = 'Y'::bpchar AND
                      ("HOSTEL_ROOM_ALLOTMENT_INFO".vacate_date IS NULL OR
                       "HOSTEL_ROOM_ALLOTMENT_INFO".is_missing = true) AND
                      ("HOSTEL_ROOM_ALLOTMENT_INFO".shifted_date IS NULL OR
                       "HOSTEL_ROOM_ALLOTMENT_INFO".is_missing = true)
         LEFT JOIN schooldev."HOSTEL_ROOM_INFO"
                   ON "HOSTEL_ROOM_INFO".room_id = COALESCE("HOSTEL_ROOM_ALLOTMENT_INFO".room_id, 0) AND
                      "HOSTEL_ROOM_INFO".active_flag = 'Y'::bpchar
         LEFT JOIN schooldev."HOSTEL_FLOOR_MASTER"
                   ON "HOSTEL_FLOOR_MASTER".floor_id = COALESCE("HOSTEL_ROOM_ALLOTMENT_INFO".building_id, 0) AND
                      "HOSTEL_FLOOR_MASTER".active_flag::text = 'Y'::text
         LEFT JOIN schooldev."HOSTEL_MASTER"
                   ON COALESCE("HOSTEL_FLOOR_MASTER".hostel_id, 0::bigint) = "HOSTEL_MASTER".hostel_id AND
                      "HOSTEL_MASTER".active_flag = 'Y'::bpchar
         LEFT JOIN schooldev."COURSE_ALLOCATION_INFO"
                   ON "COURSE_ALLOCATION_INFO".student_id::text = "STUDENT_DETAILS_INFO".student_id::text AND
                      "COURSE_ALLOCATION_INFO".active_flag::text = 'Y'::text
         LEFT JOIN schooldev.course_master ON "COURSE_ALLOCATION_INFO".course_id = course_master.course_master_id AND
                                              course_master.active_flag = 'Y'::bpchar
         LEFT JOIN schooldev."CURRENT_MESS_DETAILS_VIEW"
                   ON "CURRENT_MESS_DETAILS_VIEW".student_id::text = "STUDENT_DETAILS_INFO".student_id::text
         LEFT JOIN schooldev."USER_MANAGEMENT" ON "USER_MANAGEMENT".user_id::text = "STUDENT_DETAILS_INFO".student_id::text
WHERE "STUDENT_DETAILS_INFO".settlement_flag = 'N'::bpchar;

CREATE OR REPLACE VIEW schooldev."ALL_STUDENTS_DETAILS_VIEW"
AS
SELECT dept_name,
       student_id,
       student_name,
       student_status,
       hostel_name,
       hostel_id,
       floor_name,
       floor_id,
       room_number,
       seat,
       room_id,
       room_allotment_id,
       vacate_date,
       shifted_date,
       mess_period_id,
       dining_from_date,
       dining_to_date,
       mess_id,
       mess_name,
       mess_preference,
       bio_data_id,
       application_number,
       gender,
       dob,
       blood_group,
       category,
       student_mobile,
       student_personal_email,
       student_iitm_smail,
       city,
       state,
       country,
       pin_code,
       student_address,
       previous_id,
       aadhaar_number,
       pan_number,
       guardian_status,
       signed_parent_name,
       faculty_name,
       faculty_contact_no,
       faculty_email,
       pwd_status,
       pwd,
       pwd_percentage,
       settlement_flag,
       day_scholar,
       vacation_category,
       is_missing,
       hostel_office_email,
       auth,
       active_flag
FROM schooldev."STUDENT_SEARCH_VIEW"
WHERE active_flag = 'Y'::bpchar;

CREATE OR REPLACE VIEW schooldev."COMPLETE_STUDENT_APPLICATION_VIEW"
AS SELECT per.student_id,
          app.request_id,
          per.student_name,
          per.dob,
          per.gender,
          per.student_mobile,
          per.city,
          per.state,
          per.country,
          per.pin_code,
          per.student_address,
          per.student_iitm_smail,
          app.appointment_from,
          app.appointment_to,
          app.stay_from,
          app.stay_to,
          app.gross_pay,
          app.category,
          app.category_others,
          app.dining,
          app.dining_others,
          app.occupancy,
          app.validating_authority,
          app.validating_authority_email,
          app.status,
          app.reject_description,
          app.approval_date,
          app.created_at,
          app.modified_at,
          app.status_notes AS approval_notes,
          app.active_flag,
          app.school_id,
          app.status_notes,
          group_concat(wrk.approval_notes::text || ' '::text) AS wrk_approval_notes,
          group_concat(wrk.reject_description::text || ' '::text) AS wrk_rejection_description,
          group_concat(((((fil.file_id || '~'::text) || fil.filename::text) || '~'::text) || fil.description::text) || '`'::text) AS group_concat,
          app.thesis_submitted_date,
          app.admission_date,
          per.room_number AS room_no,
          per.hostel_name,
          per.floor_name,
          per.hostel_id,
          per.seat,
          CASE
              WHEN per.vacate_date IS NULL AND per.shifted_date IS NULL THEN 'NV'::text
              WHEN per.vacate_date IS NOT NULL AND per.shifted_date IS NULL THEN 'V'::text
              ELSE NULL::text
                     END AS vacating_status,
          app.purpose,
          app.hod_name,
          app.hod_email,
          app.cancel_description
   FROM schooldev."ALL_STUDENTS_DETAILS_VIEW" per
            JOIN schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST" app ON per.student_id::text = app.student_id::text
            LEFT JOIN schooldev."IIT_W_STUDENT_FILES_INFORMATION" fil ON fil.student_id::text = app.student_id::text AND app.request_id = fil.request_id
              LEFT JOIN schooldev."IIT_W_STUDENT_WORKFLOW" wrk ON wrk.request_id = app.request_id AND wrk.student_id::text = per.student_id::text AND wrk.active_flag = 'Y'::bpchar
   GROUP BY per.student_id, app.request_id, per.student_name, per.dob, per.gender, per.student_mobile, per.city, per.state,
              per.country, per.pin_code, per.student_address, per.student_iitm_smail, app.appointment_from, app.appointment_to,
              app.stay_from, app.stay_to, app.gross_pay, app.category, app.category_others, app.dining, app.dining_others,
              app.occupancy, app.validating_authority, app.validating_authority_email, app.status, app.reject_description,
              app.approval_date, app.created_at, app.modified_at, app.status_notes, app.active_flag, app.school_id, per.room_number,
              per.hostel_name, per.floor_name, per.hostel_id, per.seat, per.vacate_date, app.thesis_submitted_date, app.admission_date,
              app.purpose, app.hod_name, app.hod_email, app.cancel_description, per.shifted_date
   ORDER BY per.student_id, app.request_id;

CREATE OR REPLACE VIEW schooldev.tab_master
AS SELECT

              tab.id AS l1_id,
              tab.tab_type AS l1_type,
              tab.tab_name AS l1_name,
              tab.order_by AS l1_order,
              tab.property AS l1_property,
              tab.tab_url as l1_url,
              col.id AS l2_id,
              col.tab_type AS l2_type,
              col.tab_name AS l2_name,
              col.order_by AS l2_order,
              col.property AS l2_property,
              col.icon,
              col.style,
              col.url,
              col.sort,
              col.mandatory,
              col.action
   FROM schooldev."IIT_WD_DASHBOARD_TAB_MASTER" tab
            JOIN schooldev."IIT_WD_DASHBOARD_TAB_MASTER" col ON col.parent_id = tab.id AND col.active_flag = 'Y'::bpchar
   WHERE tab.parent_id = 0 AND tab.active_flag = 'Y'::bpchar
   ORDER BY tab.order_by, col.order_by;

-- schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW" source

CREATE OR REPLACE VIEW schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW"
AS SELECT "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".room_allotment_id,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".building_id,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".room_id,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".request_id,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".created_by,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".created_at,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".modified_by,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".modified_at,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".active_flag,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".vacate_date,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".stay_from_date,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".stay_to_date,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".student_name,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".dob,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".email,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".nature_of_appointment,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".dining_required,
          'Direct'::character varying                                         AS student_type,
       "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".student_id::character varying AS student_id,
       'V'::character varying                                              AS allocation_type,
       "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".sub_room_id,
       NULL::date                                                          AS shifted_date,
       'N'::character varying                                              AS vacation_category,
       NULL::integer                                                       AS new_allotment_id,
       "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".stay_id,
       NULL::text                                                          AS allocation_status,
       NULL::integer                                                       AS candidate_id,
       NULL::text                                                          AS checkin_checkout_status,
       NULL::date                                                          AS vacation_checkout_date,
       NULL::character varying                                             AS vacation_checkout_status,
       NULL::character varying                                             AS checked_in_date,
       false                                                               AS pwd_status
   FROM schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO"
   WHERE "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO".request_id::text = '0'::text
   UNION
SELECT vhra.room_allotment_id,
       vhra.building_id,
       vhra.room_id,
       vhra.request_id,
       vhra.created_by,
       vhra.created_at,
       vhra.modified_by,
       vhra.modified_at,
       vhra.active_flag,
       vhra.vacate_date,
       vhra.stay_from_date,
       vhra.stay_to_date,
       (cpd.first_name::text || ' '::text) || cpd.last_name::text AS student_name,
    cpd.date_of_birth                                          AS dob,
       cpd.email,
       car.category                                               AS nature_of_appointment,
       ''::character varying                                      AS dining_required,
       'Candidate'::character varying                             AS student_type,
       NULL::character varying                                    AS student_id,
       'V'::character varying                                     AS allocation_type,
       vhra.sub_room_id,
       NULL::date                                                 AS shifted_date,
       'N'::character varying                                     AS vacation_category,
       NULL::integer                                              AS new_allotment_id,
       vhra.stay_id,
       NULL::text                                                 AS allocation_status,
       csr.candidate_id,
       CASE
           WHEN vhra.stay_id::integer <> 0 THEN csr.stay_status
           ELSE csr.app_status
END                                                    AS checkin_checkout_status,
       NULL::date                                                 AS vacation_checkout_date,
       NULL::character varying                                    AS vacation_checkout_status,
       NULL::character varying                                    AS checked_in_date,
       false                                                      AS pwd_status
FROM schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST" car
         LEFT JOIN schooldev."IIT_W_CANDIDATE_PERSONAL_DETAILS" cpd ON cpd.candidate_id = car.candidate_id
         LEFT JOIN schooldev."CANDIDATE_STAY_DATE_LIST_VIEW" csr ON csr.candidate_id = car.candidate_id
         JOIN schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO" vhra
              ON vhra.request_id::bigint = car.request_id AND vhra.stay_id::bigint = csr.stay_id AND
                 vhra.stay_from_date >= csr.stay_from AND vhra.stay_to_date <= csr.stay_to
UNION
SELECT vhrai.room_allotment_id,
       vhrai.building_id,
       vhrai.room_id,
       vhrai.request_id,
       vhrai.created_by,
       vhrai.created_at,
       vhrai.modified_by,
       vhrai.modified_at,
       vhrai.active_flag,
       vhrai.vacate_date,
       sar.stay_from           AS stay_from_date,
       sar.stay_to             AS stay_to_date,
       sdi.student_name,
       sdi.dob,
       sdi.student_iitm_smail  AS email,
       sar.category            AS nature_of_appointment,
       ''::character varying   AS dining_required,
       CASE
           WHEN sar.category::text = 'SCHOLAR'::text THEN 'StudentScholar'::character varying
           ELSE 'StudentApp'::character varying
END                 AS student_type,
       sar.student_id,
       'V'::character varying  AS allocation_type,
       vhrai.sub_room_id,
       NULL::date              AS shifted_date,
       'N'::character varying  AS vacation_category,
       NULL::integer           AS new_allotment_id,
       '0'::character varying  AS stay_id,
       NULL::text              AS allocation_status,
       NULL::integer           AS candidate_id,
       sar.status              AS checkin_checkout_status,
       NULL::date              AS vacation_checkout_date,
       NULL::character varying AS vacation_checkout_status,
       NULL::character varying AS checked_in_date,
       sdi.pwd_status
FROM schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST" sar
         LEFT JOIN schooldev."ALL_STUDENTS_DETAILS_VIEW" sdi ON sar.student_id::text = sdi.student_id::text
         JOIN schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO" vhrai ON vhrai.request_id::bigint = sar.request_id
UNION
SELECT sar.room_allotment_id::bigint          AS room_allotment_id,
    sar.building_id,
       sar.room_id,
       ' '::character varying(30)             AS request_id,
       sar.created_by,
       sar.created_at,
       sar.modified_by,
       sar.modified_at,
       sar.active_flag,
       sar.vacate_date,
       sar.stay_from_date,
       sar.vacate_date                        AS stay_to_date,
       sdi.student_name,
       sdi.dob,
       sdi.student_iitm_smail                 AS email,
       ''::character varying                  AS nature_of_appointment,
       ''::character varying                  AS dining_required,
       'Student'::character varying           AS student_type,
       sar.student_id,
       'N'::character varying                 AS allocation_type,
       sar.sub_room_id,
       sar.shifted_date,
       sdi.vacation_category,
       sar.new_roomallotment_id               AS new_allotment_id,
       NULL::character varying                AS stay_id,
       sar.status                             AS allocation_status,
       NULL::integer                          AS candidate_id,
       NULL::text                             AS checkin_checkout_status,
       sar.vacation_checkout_date,
       sar.vacation_checkout_status,
       sar.checked_in_date::character varying AS checked_in_date,
       sdi.pwd_status
FROM schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" sar
    LEFT JOIN schooldev."ALL_STUDENTS_DETAILS_VIEW" sdi ON sar.student_id::text = sdi.student_id::text
ORDER BY 3, 22;

CREATE OR REPLACE VIEW schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW"
AS SELECT "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".room_allotment_id AS allotment_id,
          "HOSTEL_MASTER".hostel_id,
          "HOSTEL_MASTER".hostel_name,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".building_id AS floor_id,
          "HOSTEL_FLOOR_MASTER".floor_name,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".room_id,
          "HOSTEL_ROOM_INFO".room_no,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".sub_room_id,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".student_type,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".request_id,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".student_name,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".student_id,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".stay_from_date,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".stay_to_date,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".vacate_date,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".shifted_date,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".new_allotment_id,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".dob,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".email,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".nature_of_appointment,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".dining_required,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".allocation_status,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".allocation_type,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".vacation_category,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".created_by,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".created_at,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".modified_by,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".modified_at,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".active_flag,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".vacation_checkout_date,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".vacation_checkout_status,
          "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".checked_in_date
   FROM schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW"
            JOIN schooldev."HOSTEL_ROOM_INFO" ON "HOSTEL_ROOM_INFO".room_id = "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".room_id
            JOIN schooldev."HOSTEL_FLOOR_MASTER" ON "VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW".building_id = "HOSTEL_FLOOR_MASTER".floor_id
            JOIN schooldev."HOSTEL_MASTER" ON "HOSTEL_FLOOR_MASTER".hostel_id = "HOSTEL_MASTER".hostel_id;

CREATE OR REPLACE VIEW schooldev."HOSTEL_DETAILS_INFO" AS
SELECT aa.hostel_id,
       aa.hostel_name,
       aa.warden_id,
       aa.warden_name,
       aa.office_no,
       aa.phone_number,
       aa.warden_email,
       aa.hostel_gender_type,
       aa.no_of_floors,
       count(DISTINCT aa.room_no) AS total_rooms,
       sum(aa.total) AS total_no_of_seats,
       sum(aa.occupied) AS occupied,
       sum(aa.vacancy) AS vacancy,
       sum(
               CASE
                   WHEN (aa.vacancy < 0) THEN (0)::bigint
                   ELSE aa.vacancy
                   END) AS exact_vacancy,
       aa.incharger_id,
       aa.incharger_name,
       aa.incharger_officeno,
       aa.incharger_phone,
       aa.incharger_email,
       ((aa.config_value || '/'::text) || (aa.image_name)::text) AS image_location,
       aa.warden_info_url
FROM ( SELECT fm.hostel_id AS hostel_id,
    fm.hostel_name AS hostel_name,
    hri.room_no,
    max(hri.capacity) AS total,
    sum(
    CASE
    WHEN (chv.sub_room_id IS NULL) THEN 0
    ELSE 1
    END) AS occupied,
    (max(hri.capacity) - sum(
    CASE
    WHEN (chv.sub_room_id IS NULL) THEN 0
    ELSE 1
    END)) AS vacancy,
    b.warden_name,
    b.id AS warden_id,
    b.office_no,
    b.warden_email,
    b.phone_number,
    det.incharger_id,
    info.warden_name AS incharger_name,
    info.office_no AS incharger_officeno,
    info.warden_email AS incharger_email,
    info.phone_number AS incharger_phone,
    floors.no_of_floors,
    fm.hostel_gender_type,
    b.image_name,
    scd.config_value,
    b.warden_info_url
    FROM ((((((((((schooldev."HOSTEL_ROOM_INFO" hri
    JOIN schooldev."HOSTEL_FLOOR_MASTER" sf ON (((sf.floor_id = hri.building_id) AND ((sf.active_flag)::text = 'Y'::text))))
    JOIN schooldev."HOSTEL_MASTER" fm ON (((sf.hostel_id = fm.hostel_id) AND (fm.active_flag = 'Y'::bpchar))))
    JOIN ( SELECT sf1.hostel_id,
    (count(sf1.hostel_id) - 1) AS no_of_floors
    FROM schooldev."HOSTEL_FLOOR_MASTER" sf1
    WHERE ((sf1.active_flag)::text = 'Y'::text)
    GROUP BY sf1.hostel_id
    ORDER BY sf1.hostel_id) floors ON (((floors.hostel_id = fm.hostel_id) AND (fm.active_flag = 'Y'::bpchar))))
    LEFT JOIN schooldev."IIT_W_SEASON_MASTER" sm ON ((
    CASE
    WHEN (((now())::date >= sm.start_date) AND ((now())::date <= sm.end_date)) THEN true
    ELSE false
    END AND (((sm.season)::text ~~ '2%'::text) OR (((sm.season)::text ~~ '4%'::text) AND sm.is_current)))))
    LEFT JOIN schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW" chv ON ((((((chv.room_id = hri.room_id) AND (chv.stay_from_date <= (now())::date)) AND ((COALESCE(chv.stay_to_date, chv.shifted_date) >= (now())::date) OR ((chv.stay_to_date IS NULL) AND (chv.shifted_date IS NULL)))) AND
    CASE
    WHEN (sm.season IS NULL) THEN (1 = 1)
    ELSE ((chv.vacation_category)::text = 'N'::text)
    END) AND (chv.active_flag = 'Y'::bpchar))))
    LEFT JOIN schooldev."WARDEN_HOSTEL_MAPPING" a ON (((fm.hostel_id = a.hostel_id) AND (a.active_flag = 'Y'::bpchar))))
    LEFT JOIN schooldev."WARDEN_INFO" b ON (((a.warden_id = b.id) AND (b.active_flag = 'Y'::bpchar))))
    LEFT JOIN schooldev."WARDEN_INCHARGE_DETAILS" det ON (((((((det.warden_id = b.id) AND (det.active_flag = 'Y'::bpchar)) AND (b.active_flag = 'Y'::bpchar)) AND (det.active_flag = 'Y'::bpchar)) AND ((now())::date >= det.away_from)) AND ((now())::date <= det.away_to))))
    LEFT JOIN schooldev."WARDEN_INFO" info ON ((info.id = det.incharger_id)))
    LEFT JOIN schooldev."SIMS_CONFIG_DATA" scd ON ((((scd.config_key)::text = 'FILE_PATH_WARDEN_IMAGE'::text) AND ((scd.active_flag)::text = 'Y'::text))))
    WHERE ((((hri.active_flag = 'Y'::bpchar) AND (fm.active_flag = 'Y'::bpchar)) AND ((hri.official_guest_status)::text = '0'::text)) AND (b.id IS NOT NULL))
    GROUP BY fm.hostel_id, fm.hostel_name, fm.hostel_gender_type,b.image_name,
    scd.config_value,
    b.warden_info_url, hri.room_no, b.warden_name, b.id, b.office_no, b.warden_email, b.phone_number, det.incharger_id, info.warden_name, info.office_no, info.warden_email, info.phone_number, floors.no_of_floors, scd.config_value, b.warden_info_url
    ORDER BY fm.hostel_id, hri.room_no) aa
WHERE
    CASE
    WHEN ((0)::bigint = 0) THEN (aa.hostel_id <> 0)
    ELSE (aa.hostel_id = (0)::bigint)
END
GROUP BY aa.hostel_id, aa.hostel_name, aa.warden_id, aa.warden_name, aa.office_no, aa.phone_number, aa.warden_email, aa.incharger_id, aa.incharger_name, aa.incharger_officeno, aa.incharger_phone, aa.incharger_email, aa.no_of_floors, aa.hostel_gender_type, aa.config_value, aa.image_name, aa.warden_info_url
ORDER BY aa.hostel_name;

CREATE OR REPLACE VIEW schooldev."ROOM_OCCUPANCY_STATUS_VIEW"
AS SELECT hm.hostel_id,
          hm.hostel_name,
          hri.room_no,
          max(hri.capacity) AS total,
          sum(
                         CASE
                             WHEN chav.sub_room_id IS NULL THEN 0
                             ELSE 1
                             END) AS occupied,
          max(hri.capacity) - sum(
                  CASE
                      WHEN chav.sub_room_id IS NULL THEN 0
                      ELSE 1
                      END) AS vacancy
   FROM schooldev."HOSTEL_ROOM_INFO" hri
            JOIN schooldev."HOSTEL_FLOOR_MASTER" hfm ON hri.building_id = hfm.floor_id AND hfm.active_flag::text = 'Y'::text
            JOIN schooldev."HOSTEL_MASTER" hm ON hm.hostel_id = hfm.hostel_id AND hm.active_flag = 'Y'::bpchar
              LEFT JOIN schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW" chav ON chav.room_id = hri.room_id AND chav.active_flag = 'Y'::bpchar AND chav.stay_from_date <= CURRENT_DATE AND (COALESCE(chav.stay_to_date, chav.shifted_date) >= now()::date OR chav.stay_to_date IS NULL AND chav.shifted_date IS NULL) AND chav.vacation_category::text = 'N'::text
   WHERE hri.active_flag = 'Y'::bpchar AND (hri.official_guest_status::text = ANY (ARRAY['0'::character varying, 'Student'::character varying]::text[]))
   GROUP BY hm.hostel_id, hri.room_id
   ORDER BY hm.hostel_id, hri.room_id;

-- schooldev."ROOM_OCCUPANCY_STATUS_VIEW_ALL_TYPE_ROOMS" source

CREATE OR REPLACE VIEW schooldev."ROOM_OCCUPANCY_STATUS_VIEW_ALL_TYPE_ROOMS"
AS SELECT fm.hostel_id,
          fm.hostel_name,
          "HOSTEL_ROOM_INFO".room_no,
          max("HOSTEL_ROOM_INFO".capacity) AS total,
          sum(
                         CASE
                             WHEN "COMPLETE_HOSTEL_ALLOTMENT_VIEW".sub_room_id IS NULL THEN 0
                             ELSE 1
                             END) AS occupied,
          max("HOSTEL_ROOM_INFO".capacity) - sum(
                  CASE
                      WHEN "COMPLETE_HOSTEL_ALLOTMENT_VIEW".sub_room_id IS NULL THEN 0
                      ELSE 1
                      END) AS vacancy,
          "HOSTEL_ROOM_INFO".official_guest_status
   FROM schooldev."HOSTEL_ROOM_INFO"
            JOIN schooldev."HOSTEL_FLOOR_MASTER" ON "HOSTEL_FLOOR_MASTER".floor_id = "HOSTEL_ROOM_INFO".building_id AND "HOSTEL_FLOOR_MASTER".active_flag::text = 'Y'::text
     JOIN schooldev."HOSTEL_MASTER" fm ON "HOSTEL_FLOOR_MASTER".hostel_id = fm.hostel_id AND fm.active_flag = 'Y'::bpchar
              LEFT JOIN schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW" ON "COMPLETE_HOSTEL_ALLOTMENT_VIEW".room_id = "HOSTEL_ROOM_INFO".room_id AND "COMPLETE_HOSTEL_ALLOTMENT_VIEW".stay_from_date <= now()::date AND (COALESCE("COMPLETE_HOSTEL_ALLOTMENT_VIEW".stay_to_date, "COMPLETE_HOSTEL_ALLOTMENT_VIEW".shifted_date) >= now()::date OR "COMPLETE_HOSTEL_ALLOTMENT_VIEW".stay_to_date IS NULL AND "COMPLETE_HOSTEL_ALLOTMENT_VIEW".shifted_date IS NULL) AND "COMPLETE_HOSTEL_ALLOTMENT_VIEW".vacation_category::text = 'N'::text AND "COMPLETE_HOSTEL_ALLOTMENT_VIEW".active_flag = 'Y'::bpchar
   WHERE "HOSTEL_ROOM_INFO".active_flag = 'Y'::bpchar
   GROUP BY fm.hostel_id, "HOSTEL_ROOM_INFO".room_no, "HOSTEL_ROOM_INFO".official_guest_status
   ORDER BY fm.hostel_id, "HOSTEL_ROOM_INFO".room_no;

CREATE OR REPLACE VIEW schooldev.employee_communication_details_view
AS SELECT employee_id AS communication_id,
          active_flag AS active_flag,
          email_address AS alternate_email_id,
          mobile_number AS alternate_mobile_no,
          email_address AS email_id,
          mobile_number,
          contact_number AS landline_number,
          address_one AS comm_address,
          pin_code AS comm_zipcode,
          address_one AS perm_address,
          pin_code AS perm_zipcode,
          created_by,
          created_at,
          modified_by,
          modified_at,
          state_id AS perm_state,
          city_id AS comm_city,
          state_id AS comm_state,
          country_id AS comm_country,
          country_id AS perm_country,
          employee_id,
          city_id AS perm_city
   FROM schooldev."FACULTY_PERSONAL_DETAILS";

create or replace view schooldev.student_previous_id_view_2(new_id, old_id) as
SELECT student_id AS new_id,
       unnest(COALESCE(string_to_array(previous_id::text, ','::text), ARRAY [NULL::text])) AS old_id
FROM schooldev."STUDENT_DETAILS_INFO" s1
WHERE active_flag = 'Y'::bpchar;



create or replace view schooldev.hostel_details as
select hm.hostel_id, hm.hostel_name, hm.extension_flag, hm.hostel_gender_type, hm.hostel_office_email, hm.hostel_short_code,
       hfm.floor_id, hfm.floor_name, hfm.floor_desc, hfm.floor_size, hfm.hostel_or_college,
       hri.room_id, hri.room_no, hri.capacity, hri.occupied, hri.is_vacation, hri.room_size, hri.vac_capacity, hri.official_guest_status
from schooldev."HOSTEL_MASTER" hm
         left join schooldev."HOSTEL_FLOOR_MASTER" hfm on (hfm.hostel_id = hm.hostel_id and hfm.active_flag = 'Y')
         left join schooldev."HOSTEL_ROOM_INFO" hri on (hri.building_id = hfm.floor_id and hri.active_flag = 'Y')
where hm.active_flag = 'Y';

CREATE OR REPLACE VIEW schooldev."HOSTEL_NIGHT_PAYMENT_LEDGER_VIEW"
AS SELECT a.student_id,
          c.student_name,
          b.hostel_name,
          a.pay_by,
          sum(COALESCE(a.no_of_veg_coupon, 0)) AS total_veg_coupons,
          sum(COALESCE(a.no_of_nonveg_coupon, 0)) AS total_nonveg_coupons,
          sum(a.total_amount) AS total_amount
   FROM schooldev."HOSTEL_NIGHT_PAYMENT_TRANSACTION" a
            LEFT JOIN schooldev."HOSTEL_MASTER" b ON a.hostel_id = b.hostel_id AND b.active_flag = 'Y'::bpchar
            LEFT JOIN schooldev."ALL_STUDENTS_DETAILS_VIEW" c ON (a.student_id::text = c.student_id::text)
   WHERE upper(a.pay_by::text) = upper('Ledger'::text) AND a.active_flag = 'Y'::bpchar AND a.payment_status::text = 'Success'::text
   GROUP BY a.student_id, c.student_name, b.hostel_name, a.pay_by
   ORDER BY a.pay_by, a.student_id;

CREATE OR REPLACE VIEW schooldev."HOSTEL_NIGHT_PAYMENT_ONLINE_VIEW"
AS SELECT a.student_id,
          c.student_name,
          b.hostel_name,
          a.pay_by,
          sum(COALESCE(a.no_of_veg_coupon, 0)) AS total_veg_coupons,
          sum(COALESCE(a.no_of_nonveg_coupon, 0)) AS total_nonveg_coupons,
          sum(a.total_amount) AS total_amount
   FROM schooldev."HOSTEL_NIGHT_PAYMENT_TRANSACTION" a
            LEFT JOIN schooldev."HOSTEL_MASTER" b ON a.hostel_id = b.hostel_id AND b.active_flag = 'Y'::bpchar
            LEFT JOIN schooldev."ALL_STUDENTS_DETAILS_VIEW" c ON a.student_id::text = c.student_id::text
   WHERE upper(a.pay_by::text) = upper('Online'::text) AND a.active_flag = 'Y'::bpchar AND a.payment_status::text = 'Success'::text
   GROUP BY a.student_id, c.student_name, b.hostel_name, a.pay_by
   ORDER BY a.pay_by, a.student_id;

-- schooldev."DYNAMIC_USER_TABS_DISPLAYABLE_VIEW" source

CREATE OR REPLACE VIEW schooldev."DYNAMIC_USER_TABS_DISPLAYABLE_VIEW"
AS SELECT row_number() OVER () AS id,
              rts.role,
          COALESCE(uts.show_hide, rts.show_hide, false) AS show_hide,
          tm.id AS l1_id,
          tm.tab_type AS l1_type,
          tm.tab_name AS l1_name,
          tm.property AS l1_property,
          tm.order_by AS l1_order,
          tm.tab_url AS l1_url,
          tm2.id AS l2_id,
          tm2.tab_type AS l2_type,
          tm2.tab_name AS l2_name,
          tm2.property AS l2_property,
          tm2.order_by AS l2_order,
          tm2.icon,
          tm2.style,
          tm2.url,
          tm2.sort,
          tm2.mandatory,
          tm2.action,
          um.user_name AS user_id
   FROM schooldev."IIT_WD_DASHBOARD_TAB_MASTER" tm
            LEFT JOIN schooldev."IIT_WD_DASHBOARD_TAB_MASTER" tm2 ON tm2.parent_id = tm.id
            LEFT JOIN schooldev."IIT_WD_ROLE_TAB_SETTINGS" rts ON (tm.id = rts.tab_id OR tm2.id = rts.tab_id) AND rts.active_flag = 'Y'::bpchar
     LEFT JOIN schooldev."IIT_WD_USER_TAB_SETTINGS" uts ON (tm.id = uts.tab_id OR tm2.id = uts.tab_id) AND uts.active_flag = 'Y'::bpchar
              LEFT JOIN schooldev."USER_MANAGEMENT" um ON um.user_id::text = uts.user_id::text
   WHERE (tm.parent_id = 0 OR tm.parent_id IS NULL) AND tm.active_flag = 'Y'::bpchar
   GROUP BY rts.role, (COALESCE(uts.show_hide, rts.show_hide, false)), tm.id, tm.tab_type, tm.tab_name, tm.property, tm.order_by, tm.tab_url, tm2.id, tm2.tab_type, tm2.tab_name, tm2.property, tm2.order_by, tm.icon, tm.style, tm.url, tm.sort, tm.mandatory, tm.action, um.user_name, rts.show_hide
   ORDER BY tm.order_by, tm2.order_by;

-- schooldev."GUEST_ROOM_ALLOTMENT_INFO_VIEW" source

CREATE OR REPLACE VIEW schooldev."GUEST_ROOM_ALLOTMENT_INFO_VIEW"
AS SELECT fm.hostel_name AS facility_master_name,
          fm.hostel_id AS facility_master_id,
          hri.building_id,
          hri.room_id,
          hri.room_no,
          COALESCE(grai.occupied_status, 0) AS sr_occupied,
          hri.capacity,
          gar.id,
          gagd.request_id,
          gar.from_date,
          gar.to_date,
          gar.student_id,
          sdi.student_name::text AS student_name,
              sdi.student_personal_email AS parent_email_id,
          COALESCE(grai.roomallotmentid, 0::bigint) AS room_allotment_id,
          gar.no_of_days,
          gar.no_of_persons,
          group_concat(((gagd.guest_name::text || '-'::text) || gagd.relation_of_guest::text) || ''::text) AS guest_name,
          group_concat(gagd.relation_of_guest::text) AS guest_relationship,
          group_concat(gagd.guest_gender::text) AS guest_gender
   FROM schooldev."HOSTEL_ROOM_INFO" hri
            LEFT JOIN schooldev."HOSTEL_FLOOR_MASTER" hfm ON hri.building_id = hfm.floor_id
            JOIN schooldev."HOSTEL_MASTER" fm ON hfm.hostel_id = fm.hostel_id
            LEFT JOIN schooldev."GUEST_ROOM_ALLOTMENT_INFO" grai ON hri.room_id = grai.roomid AND hri.building_id = grai.building_id AND grai.active_flag = 'Y'::bpchar
     LEFT JOIN schooldev."GUEST_ACCOMMODATION_REQUEST" gar ON gar.id = grai.requestid::integer
              LEFT JOIN schooldev."GUEST_ACCOMMODATION_GUEST_DETAILS" gagd ON gar.id = gagd.request_id AND (gagd.guest_id::character varying::text IN ( SELECT regexp_split_to_table(grai.guest_id::text, ','::text) AS guestids
              FROM schooldev."GUEST_ROOM_ALLOTMENT_INFO"))
              LEFT JOIN schooldev."ALL_STUDENTS_DETAILS_VIEW" sdi ON sdi.student_id::text = grai.studentid::text
   WHERE hri.official_guest_status::text = 'guest'::text AND hri.active_flag::text = 'Y'::text
   GROUP BY fm.hostel_name, hri.room_id, hri.room_no, grai.occupied_status, hri.capacity, gar.id, sdi.student_name, grai.roomallotmentid, sdi.student_personal_email, gagd.request_id, fm.hostel_id
   ORDER BY fm.hostel_name, hri.room_id, hri.room_no;


-- schooldev."CANDIDATE_APPOINTMENT_VIEW" source

CREATE OR REPLACE VIEW schooldev."CANDIDATE_APPOINTMENT_VIEW"
AS SELECT app.candidate_id,
          app.request_id,
          btrim(group_concat(wrk.approval_notes::text || ''::text), ','::text) AS wrk_approval_notes,
          group_concat(wrk.rejection_description::text || ''::text) AS wrk_rejection_description,
          app.appointment_from,
          app.appointment_to,
          app.stay_from AS app_stayfrom,
          app.stay_to AS app_stayto,
          app.gross_pay,
          app.category,
          app.category_others,
          app.dining AS app_dining,
          app.occupancy,
          app.validating_authority,
          app.validating_authority_email,
          app.approval_status AS app_status,
          app.rejection_description AS app_reject_desc,
          app.approval_date AS app_approval_date,
          app.created_at,
          app.active_flag,
          app.application_no,
          app.purpose,
          app.school_id,
          app.mess_option,
          app.program_dept
   FROM schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST" app
            JOIN schooldev."IIT_W_CANDIDATE_WORKFLOW" wrk ON wrk.application_id = app.request_id AND wrk.candidate_id = app.candidate_id AND wrk.active_flag = 'Y'::bpchar
   GROUP BY app.request_id
   ORDER BY app.request_id;


CREATE OR REPLACE VIEW schooldev."COMPLETE_CANDIDATE_VIEW"
AS SELECT per.candidate_id,
          app.request_id,
          stay.stay_id,
          per.first_name,
          per.last_name,
          per.date_of_birth,
          per.gender,
          per.address1,
          per.address2,
          per.city,
          per.state,
          per.pin,
          per.phone_number,
          per.mobile_number,
          per.email,
          per.post_select,
          per.post_others,
          per.employee_id,
          per.designation,
          app.appointment_from,
          app.appointment_to,
          app.app_stayfrom,
          app.app_stayto,
          app.gross_pay,
          app.category,
          app.category_others,
          app.app_dining,
          app.occupancy,
          app.validating_authority,
          app.validating_authority_email,
          app.app_status,
          app.app_reject_desc,
          app.app_approval_date,
          app.created_at,
          app.active_flag,
          app.school_id,
          app.application_no,
          app.purpose,
          stay.stay_created_at,
          stay.stay_from,
          stay.stay_to,
          stay.dining,
          stay.description,
          stay.approval_status,
          stay.rejection_description,
          stay.stay_created,
          app.wrk_approval_notes,
          app.wrk_rejection_description,
          stay.staywrk_approval_notes,
          stay.staywrk_rejection_description,
          app.mess_option,
          stay.mess_option AS stay_mess_option,
          app.program_dept,
          stay.approval_date AS stay_approval_date
   FROM schooldev."IIT_W_CANDIDATE_PERSONAL_DETAILS" per
            LEFT JOIN schooldev."CANDIDATE_APPOINTMENT_VIEW" app ON per.candidate_id = app.candidate_id
            LEFT JOIN schooldev."CANDIDATE_STAY_VIEW" stay ON app.candidate_id = stay.candidate_id AND app.request_id = stay.appointment_id AND stay.stay_id = (( SELECT max(stay1.stay_id) AS max
   FROM schooldev."IIT_W_CANDIDATE_STAY_REQUEST" stay1
   WHERE stay1.candidate_id = app.candidate_id AND app.request_id = stay1.appointment_id))
   ORDER BY app.request_id, stay.stay_id;


-- schooldev.student_parent_mail source

CREATE OR REPLACE VIEW schooldev.student_parent_mail
AS SELECT a.student_id,
          a.parent_email_id AS student_mailid,
          a.fathers_name AS parent_name,
          a.student_personal_email,
          c.email,
          a.field5 AS v_sdi_field5,
          (
                     CASE
                         WHEN c.email IS NOT NULL THEN c.email::text
                         ELSE a.field5::text
                         END
                     ) AS parent_mail, c.relation_type
   FROM schooldev."STUDENT_DETAILS_INFO" a
            LEFT JOIN schooldev."STUDENT_BIO_DATA_FORM_DETAILS" b ON a.student_id::text = b.student_id::text
     LEFT JOIN schooldev."STUDENT_BIO_DATA_FAMILY_INFO" c ON c.bio_data_id = b.bio_data_id
   WHERE a.active_flag = 'Y'::bpchar;


--Function UpdateHOSTEL_ROOM_ALLOTMENT_INFO

CREATE OR REPLACE FUNCTION schooldev.student_balance_upto_definedate(par_acchead character varying)
    RETURNS double precision
    LANGUAGE plpgsql
AS $function$

declare
result double precision;
BEGIN
SELECT sum(
               CASE
                   WHEN t.debit_or_credit::text = 'c'::text THEN t.amount
                   ELSE 0.0::double precision
               END) - sum(
               CASE
                   WHEN t.debit_or_credit::text = 'd'::text THEN t.amount
                   ELSE 0.0::double precision
               END) into result
FROM (			SELECT 'A'::text AS tbl, a_1.acchead, a_1.voucher_date AS dt, a_1.description, a_1.doc_ref_no, 				      a_1.amount, a_1.debit_or_credit, a_1.voucher_no,newid
                  FROM schooldev."MESS_LEDGER_A" a_1  JOIN schooldev.student_previous_id_view a ON (upper(a.oldid)=upper(a_1.acchead))
                  WHERE a_1.active_flag = 'Y'::bpchar AND a_1.cancel_status::text = 'N'::text AND (a_1.recon::text = ''::text OR a_1.recon::text = 'N'::text) AND a_1.companyid = 1 AND a_1.book_type::text = 'MS'::text and upper(a.newid)=upper(par_acchead)
                  UNION
                  SELECT 'B'::text AS tbl, b.acchead, b.voucher_date AS dt, b.description, b.doc_ref_no, b.amount, b.debit_or_credit, b.voucher_no,newid
                  FROM schooldev."MESS_LEDGER_B" b JOIN schooldev.student_previous_id_view a ON (upper(a.oldid) = upper(b.acchead))
                  WHERE b.active_flag = 'Y'::bpchar AND b.cancel_status::text = 'N'::text AND (b.recon::text = ''::text OR b.recon::text = 'N'::text) AND b.companyid = 1 AND b.book_type::text = 'MS'::text  and upper(a.newid)=upper(par_acchead)-- and b.voucher_date<='2018-06-30'
                  UNION
                  SELECT 'Open Bal'::text AS tbl, "MESS_OPENING_BAL".acchead, "MESS_OPENING_BAL".opn_date AS dt, 'OPENINGBAL'::character varying AS "varchar", NULL::character varying AS unknown, "MESS_OPENING_BAL".amount, "MESS_OPENING_BAL".debit_or_credit, ''::character varying AS "varchar",newid
                  FROM schooldev."MESS_OPENING_BAL"JOIN schooldev.student_previous_id_view a ON (upper(a.oldid) = upper("MESS_OPENING_BAL".acchead))
                  WHERE "MESS_OPENING_BAL".active_status = 'Y'::bpchar AND "MESS_OPENING_BAL".companyid = 1 and upper(a.newid)=upper(par_acchead)) t

GROUP BY upper(newid::text);

RETURN result;

END;
$function$
;

create or replace function schooldev.food_court_list(studentid character varying, userrole character varying, messperiodid bigint, foodcourtmessnameid integer, fromdate character varying, todate character varying) returns SETOF foodcourtlist
    language plpgsql
as
$$
declare res foodcourtlist%rowtype;
BEGIN
    --if(userrole='SoftwareAdmin') then
    if(studentid is null or studentid='') and (userrole is null or userrole='') and (messperiodid=0) and
      (foodcourtmessnameid=0) and (fromdate is null or fromdate='') and (todate is null or todate='')then
        raise notice 'ifloop: %',  '1';
for res in
select concat('Ex',fc.fc_id) as voucher_no,fc.purchase_timestamp::date as voucher_date,a.student_id,d.mess_name,b.dining_from_date,
       b.dining_to_date,fc.amount,fc.debit_or_credit,a.total_purchase_amount,a.total_credit_amount,a.balance_amount
from schooldev."FOOD_COURT_LEDGER" fc join schooldev."FOOD_COURT_LEDGER_VIEW" a
                                           on (fc.student_id=a.student_id and fc.fc_mess_id=a.fc_mess_id and fc.mess_period_id=a.mess_period_id)
                                      join schooldev."MESS_MASTER_CONTROLLER" b on (a.mess_period_id=b.id)
                                      join schooldev."MESS_MASTER" d on(a.fc_mess_id=d.mess_master_id) where fc.active_flag='Y' order by voucher_date asc

    loop
                return next res;
end loop;
else
        raise notice 'else: %',  '1';
for res in
select concat('Ex',fc.fc_id) as voucher_no,fc.purchase_timestamp::date as voucher_date,a.student_id,d.mess_name,b.dining_from_date,
       b.dining_to_date,fc.amount,fc.debit_or_credit,a.total_purchase_amount,a.total_credit_amount,a.balance_amount
from    schooldev."FOOD_COURT_LEDGER" fc join  schooldev."FOOD_COURT_LEDGER_VIEW" a on (fc.student_id=a.student_id and fc.fc_mess_id=a.fc_mess_id and fc.mess_period_id=a.mess_period_id)  join
        schooldev."STUDENT_MESS_DETAILS"  c on (a.fc_mess_id=c.mess_id and a.student_id =c.student_id and c.active_flag='Y' )
                                         join schooldev."MESS_MASTER_CONTROLLER" b on (a.mess_period_id=b.id and b.dining_from_date=c.from_date and b.dining_to_date=c.to_date )
                                         join schooldev."MESS_MASTER" d on(a.fc_mess_id=d.mess_master_id)
where fc.active_flag='Y'
  --and a.mess_period_id=messperiodid
  and (case when (studentid is not null and studentid<>'') then (upper(fc.student_id) = upper(studentid)) else 1=1 end )
  and(case when messperiodid<>'0' then fc.mess_period_id=messperiodid else 1=1 end)
  and(case when foodcourtmessnameid<>'0' then fc.fc_mess_id=(foodcourtmessnameid) else 1=1 end)
  and(case when (fromdate::text<>'' and fromdate::text<>'null' and fromdate::date is not null) then  fc.purchase_timestamp::date >=fromdate::date else 1=1 end)
  and(case when (todate::text<>'' and todate::text<>'null' and todate::date is not null) then fc.purchase_timestamp::date <= todate::date else 1=1 end)
order by voucher_date asc

    loop return next res;
end loop;
end if;
    --end if;

/*if(userrole='Student') then

  for res in
      select a.student_id,d.mess_name,b.mmc_d_dining_from_date,b.mmc_d_dining_to_date,fc.amount,fc.debit_or_credit,a.total_purchase_amount,
        a.total_credit_amount,a.balance_amount from schooldev."FOOD_COURT_LEDGER" fc
        join schooldev."FOOD_COURT_LEDGER_VIEW" a on (fc.student_id=a.student_id and fc.fc_mess_id=a.fc_mess_id and fc.mess_period_id=a.mmc_n_id)
        join schooldev."MESS_MASTER_CONTROLLER" b on (a.mmc_n_id=b.mmc_n_id and (now()::date >=mmc_d_dining_from_date and now()::date <=mmc_d_dining_to_date) and mmc_v_active_flag='Y')
        join schooldev."STUDENT_MESS_DETAILS" c on (a.fc_mess_id=c.n_smd_mess_id and d_smd_from_date::date<=now()::date and d_smd_to_date::date >= now()::date and v_smd_current_active_flag='Y')
      join schooldev."MESS_MASTER" d on(a.fc_mess_id=d.mess_master_id and d.active_status='Y')

       where UPPER(a.student_id)=UPPER(studentid)
       --and a.mess_period_id=messperiodid
  loop

  return next res;
  end loop;

 end if;  */
END;

    /* drop type foodcourtlist cascade;
    create type foodcourtlist as (voucher_no character varying,voucher_date character varying,student_id character varying,
    mess_name character varying,mmc_d_dining_from_date character varying,
    mmc_d_dining_to_date character varying, amount double precision, debit_or_credit character varying, total_purchase_amount double precision,total_credit_amount double precision, balance_amount double precision);


   select *  from  schooldev.food_court_list('','','26','0','','')
   select *  from  schooldev.food_court_list('','','33','0','09-Sep-2019','10-Sep-2019')
   select *  from  schooldev.food_court_list('','','33','0','','')
   select *  from  schooldev.food_court_list('MM19B014','','26','0','','')
   */

$$;

create or replace function schooldev.mess_to_card_req_list(p_student_id character varying, p_request_status character varying,
                                                           p_request_date date, p_transfer_date date)
    returns SETOF schooldev.mess_to_card_request_list
    language plpgsql
as
$$
DECLARE
res schooldev.mess_to_card_request_list%rowtype;
    duration timestamp;
    res_count numeric;
BEGIN
    res_count = 0;
    duration = clock_timestamp();
    p_student_id = upper(p_student_id);
    IF (p_student_id IS NULL OR p_student_id = '') AND (p_request_status IS NULL OR p_request_status = '')
        AND p_request_date IS NULL AND p_transfer_date IS NULL
    THEN
        RAISE NOTICE 'ifloop: %', '1';
FOR res IN
SELECT a.messtocard_id, a.student_id, a.student_name, a.request_date, schooldev.student_balance(a.student_id),
       a.transfer_amount, requested_status, transferred_date, checkbox_transfer, b.parent_email_id
FROM schooldev."MESS_TO_CARD_AMOUNT_TRANSFER" AS a
         JOIN schooldev."STUDENT_DETAILS_INFO" AS b ON (b.student_id = a.student_id)
WHERE a.active_flag = 'Y' and (request_date between (now() - interval '1 month') and now() or transferred_date between (now() - interval '1 month') and now())
ORDER BY messtocard_id
    LOOP
                res_count = res_count + 1;
RETURN NEXT res;
END LOOP;
ELSE
        RAISE NOTICE 'p_request_status: %', p_request_status;
FOR res IN
SELECT a.messtocard_id, a.student_id, a.student_name, a.request_date, schooldev.student_balance(a.student_id),
       a.transfer_amount, requested_status, transferred_date, checkbox_transfer, b.parent_email_id
FROM schooldev."MESS_TO_CARD_AMOUNT_TRANSFER" AS a
         JOIN schooldev."STUDENT_DETAILS_INFO" AS b ON (b.student_id = a.student_id)
WHERE a.active_flag = 'Y'
  AND (CASE WHEN (p_student_id IS NOT NULL AND p_student_id <> '')
                THEN a.student_id LIKE ('%' || p_student_id || '%') ELSE 1 = 1	END)
  AND (CASE WHEN (p_request_status IS NOT NULL AND p_request_status not in ('', '0'))
                THEN requested_status = p_request_status ELSE 1 = 1 END)
  AND (CASE WHEN (p_request_date IS NOT NULL)
                THEN request_date = p_request_date ELSE 1 = 1 END)
  AND (CASE WHEN (p_transfer_date IS NOT NULL)
                THEN transferred_date = p_transfer_date ELSE 1 = 1 END)
ORDER BY messtocard_id
    LOOP
                res_count = res_count + 1;
RETURN NEXT res;
END LOOP;
END IF;
    raise notice 'Duration: % to fetch % records', (clock_timestamp() - duration), res_count;
END;

/* drop type mess_to_card_request_list;
 create type mess_to_card_request_list as (messtocard_id integer, student_id character varying, student_name character varying, request_date character varying,
net_bal double precision, transfer_amount double precision, requested_status character varying, transferred_date character varying, checkbox_transfer character varying,
email_id character varying);

  select *  from  schooldev.mess_to_card_req_list(NULL,NULL,NULL,NULL)
  select *  from  schooldev.mess_to_card_req_list('CE18B112',NULL,NULL,NULL)
  select *  from  schooldev.mess_to_card_req_list('CE18B112','Pending',NULL,NULL)
  select *  from  schooldev.mess_to_card_req_list(NULL,NULL,'2019-01-17',NULL)
  select *  from  schooldev.mess_to_card_req_list('EP18B013','Transfered',NULL,NULL)
  select *  from  schooldev.mess_to_card_req_list('', 'Approved',NULL,NULL)
  select *  from  schooldev.mess_to_card_req_list('', 'Transfered',NULL,NULL)
  select *  from  schooldev.mess_to_card_req_list('', 'Pending',NULL,NULL)
  select *  from  schooldev.mess_to_card_req_list('', 'Rejected',NULL,NULL)
  select student_id, count(1) from schooldev."MESS_TO_CARD_AMOUNT_TRANSFER" where active_flag = 'Y' group by student_id order by 2 desc;
*/
$$;

CREATE OR REPLACE FUNCTION schooldev.room_occupancy_status(p_from_date date, p_to_date date, p_hostel_id bigint, p_room_id bigint, p_sub_room character varying)
    RETURNS SETOF room_occupancy_status_result
    LANGUAGE plpgsql
AS $function$
declare
res room_occupancy_status_result%rowtype;
BEGIN
for res in
SELECT allotment_id, hostel_id, hostel_name, floor_id, floor_name, room_id,room_no, sub_room_id, student_type,
       request_id, student_name, student_id, stay_from_date, stay_to_date, vacate_date, shifted_date, dob, email,
       nature_of_appointment, dining_required, allocation_type, created_by, created_at, modified_by, modified_at, active_flag
from schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW" c
where hostel_id = p_hostel_id and room_id = p_room_id and sub_room_id = p_sub_room and active_flag='Y'
  and (
    (
        (stay_from_date <= p_to_date) and (p_from_date <= COALESCE(stay_to_date, shifted_date))
        )
        or (
        (stay_from_date <= p_from_date or stay_from_date <= p_to_date)
            and stay_to_date is null and vacation_category='N' and shifted_date is null and vacate_date is null
        )
        or (
        (stay_from_date <= p_from_date or stay_from_date <= p_to_date)
            and stay_to_date is null and vacation_category='V' and shifted_date is null and vacate_date is null
        )
    )
    loop
            return next res;
end loop;
    return;

END;
/*
create type room_occupancy_status_result as (allotment_id bigint, hostel_id bigint, hostel_name character varying, floor_id bigint, floor_name character varying,
room_id bigint, room_no character varying, sub_room_id character varying, student_type character varying, request_id character varying, student_name character varying,
student_id character varying, stay_from_date date, stay_to_date date, vacate_date date, shifted_date date, dob date, email character varying, nature_of_appointment character varying,
dining_required character varying, allocation_type character varying, created_by character varying, created_at timestamp, modified_by character varying, modified_at timestamp,
active_flag character, vacation_category character varying);

SELECT * from schooldev.room_occupancy_status('2025-01-12'::date,'2016-01-16'::date, 16, 10617, 'A'::character varying);

*/
$function$;


drop type guestcouponissuedlist cascade;
create type guestcouponissuedlist as (coupon_id integer, request_id integer, coupon_number character varying,
    candidate_name character varying,validity_from_date character varying, validity_to_date character varying,
    coupon_type character  varying,coupon_used_status character varying, Submitted_Date character varying,
    mess_name character varying,student_id character varying);

-- DROP FUNCTION schooldev.guest_coupon_issued_list(varchar, varchar, int8, varchar, varchar, varchar, varchar, int4, varchar, varchar);

CREATE OR REPLACE FUNCTION schooldev.guest_coupon_issued_list(candidatename character varying, couponused character varying, requestid bigint, diningfrom character varying, diningto character varying, submittedfromdate character varying, submittedtodate character varying, messid integer, userrole character varying, userlogin character varying)
 RETURNS SETOF guestcouponissuedlist
 LANGUAGE plpgsql
AS $function$
declare    res guestcouponissuedlist%rowtype;
           loginmessid integer;
           dateFormat varchar = 'Mon dd, yyyy';

BEGIN
    if(userrole!='Caterer') then
        if (candidatename is null or candidatename='') and (couponused is null or couponused='' or couponused='0')
            and (requestid is null or requestid='0') and (diningfrom is null or diningfrom='' or diningfrom ='null') and (diningto is null or diningto='' or diningto ='null')
            and (submittedfromdate is null or submittedfromdate='' or submittedfromdate='null') and (submittedtodate is null or submittedtodate='' or submittedtodate='null')
            and (messid is null or messid='0')

        then
            raise notice 'ifloop: %',  'in';
for res in
select a.coupon_id, a.request_id,coupon_number,b.candidate_name,to_char(validity_from_date, dateFormat),to_char(validity_to_date, dateFormat),
       coupon_type,coupon_used_status,to_char(a.created_at, dateFormat),mess_name,b.student_id
from schooldev."IITM_GUEST_COUPON_MAPPINGS" a
         left join schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" b on (b.request_id=a.request_id)
         left join schooldev."MESS_MASTER" mess on (mess.mess_master_id=b.mess_id and mess.active_flag='Y')
where a.active_flag='Y' and approval_status='Approved' and b.created_at::date >=now()::date-2
order by a.request_id
    loop
    return next res;
end loop;
else
            raise notice 'elseloop: %',  'in';
for res in
select a.coupon_id, a.request_id,coupon_number,b.candidate_name,to_char(validity_from_date, dateFormat),to_char(validity_to_date, dateFormat),
       coupon_type,coupon_used_status,to_char(a.created_at, dateFormat) as Submitted_Date,mess_name,b.student_id
from schooldev."IITM_GUEST_COUPON_MAPPINGS" a
         left join schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" b on (b.request_id=a.request_id)
         left join schooldev."MESS_MASTER" mess on (mess.mess_master_id=b.mess_id and mess.active_flag='Y')
where a.active_flag='Y' and approval_status='Approved'
  and (case when (candidatename is not null and candidatename<>'') then (lower(candidate_name) like lower('%'||candidatename||'%')) else 1=1 end)
  and (case when (couponused is not null and couponused<>'' and couponused<>'0') then lower(coupon_used_status) = lower(couponused) else 1=1 end)
  and (case when (requestid is not null and requestid<>0) then (a.request_id = requestid) else 1=1 end )
  and (case when (diningfrom::text <>'null' and diningfrom::text is not null) then  dining_from_date::date >=diningfrom::date else 1=1 end)
  and (case when (diningto::text <>'null' and diningto::text is not null ) then  dining_to_date::date <= diningto::date else 1=1 end)
  and (case when (submittedfromdate::text <> 'null' ) then  a.created_at::date >=submittedfromdate::date else 1=1 end)
  and (case when (submittedtodate::text <> 'null' ) then a.created_at::date <= submittedtodate::date else 1=1 end)
  and (case when (messid <> '0' ) then b.mess_id=messid  else 1=1 end)

    loop return next res;
end loop;
end if;
else
        raise notice 'caterer login: %',  'senior cook';
select d.mess_master_id into loginmessid
from schooldev."CATERER_LEDGER_MAPPING" a
         join schooldev."MESS_ALLOCATION" c  on (c.vendor_code=acchead and c.active_flag='Y')
         join schooldev."MESS_MASTER" d on (d.mess_master_id=c.mess_master_id and d.active_flag='Y')
WHERE lower(caterer_name)=lower(userlogin) and a.active_flag='Y';
raise notice 'loginmessid: %',  loginmessid;

        if (candidatename is null or candidatename='') and (couponused is null or couponused='')
            and (requestid is null or requestid='0') and (diningfrom is null or diningfrom='' or diningfrom ='null') and (diningto is null or diningto='' or diningto ='null')
            and (submittedfromdate is null or submittedfromdate='' or submittedfromdate='null') and (submittedtodate is null or submittedtodate='' or submittedtodate='null')
            and (messid is null or messid='0')

        then
            raise notice 'caterer login: %',  'If loop';
for res in
select a.coupon_id, a.request_id,coupon_number,b.candidate_name,to_char(validity_from_date, dateFormat),to_char(validity_to_date, dateFormat),
       coupon_type,coupon_used_status,to_char(a.created_at, dateFormat) as Submitted_Date,mess_name,b.student_id
from schooldev."IITM_GUEST_COUPON_MAPPINGS" a
         left join schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" b on (b.request_id=a.request_id and b.active_flag ='Y')
         left join schooldev."MESS_MASTER" c on (c.mess_master_id=b.mess_id and c.active_flag='Y')
where a.active_flag='Y' and approval_status='Approved' --and b.created_at::date >=now()::date-7
  and b.mess_id=loginmessid --and now()::date between dining_from_date and dining_to_date
  and now()::date = validity_from_date
order by coupon_type
    loop
    return next res;
end loop;
else
            raise notice 'caterer login: %',  'else loop';
for res in
select a.coupon_id, a.request_id,coupon_number,b.candidate_name,to_char(validity_from_date, dateFormat),to_char(validity_to_date, dateFormat),
       coupon_type,coupon_used_status,to_char(a.created_at, dateFormat) as Submitted_Date,mess_name,b.student_id
from schooldev."IITM_GUEST_COUPON_MAPPINGS" a
         left join schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" b on (b.request_id=a.request_id)
         left join schooldev."MESS_MASTER" c on (c.mess_master_id=b.mess_id and c.active_flag='Y')
where a.active_flag='Y' and approval_status='Approved' and b.mess_id=loginmessid

  and (case when (candidatename is not null and candidatename<>'') then (lower(candidate_name) like lower('%'||candidatename||'%')) else 1=1 end)
  and (case when (couponused is not null and couponused<>'' and couponused<>'0') then lower(coupon_used_status) = lower(couponused) else 1=1 end)
  and (case when (requestid is not null and requestid<>0) then (a.request_id = requestid) else 1=1 end )
  and (case when (diningfrom::text <>'null' and diningfrom::text is not null) then  dining_from_date::date >=diningfrom::date else 1=1 end)
  and (case when (diningto::text <>'null' and diningto::text is not null ) then  dining_to_date::date <= diningto::date else 1=1 end)
  and (case when (submittedfromdate::text <> 'null' ) then  a.created_at::date >=submittedfromdate::date else 1=1 end)
  and (case when (submittedtodate::text <> 'null' ) then a.created_at::date <= submittedtodate::date else 1=1 end)
  and (case when (messid <> '0' ) then b.mess_id=messid  else 1=1 end)

    loop return next res;
end loop;
end if;
end if;
END;

    /* drop type guestcouponissuedlist cascade;
    create type guestcouponissuedlist as (coupon_id integer, request_id integer, coupon_number character varying,
    candidate_name character varying,validity_from_date character varying, validity_to_date character varying,
    coupon_type character  varying,coupon_used_status character varying, Submitted_Date character varying,
	mess_name character varying,student_id character varying);

   select *  from  schooldev.guest_coupon_issued_list(NULL,NULL,'0',NULL,NULL,NULL,NULL,'0','SENIOR COOK','sgr.ms')
   select *  from  schooldev.guest_coupon_issued_list(NULL,NULL,'0',NULL,NULL,NULL,NULL,'0','SENIOR COOK','sgr.ms')
   select *  from  schooldev.guest_coupon_issued_list('','0','0','null','null','null','null','97','SOFTWAREADMIN','triesten')
   select *  from  schooldev.guest_coupon_issued_list('','0','0','null','null','null','null','0','SOFTWAREADMIN','triesten')

    */


$function$
;


CREATE OR REPLACE FUNCTION schooldev.student_vacating_hostel(submittedfromdate character varying, submittedtodate character varying, vacatingreason character varying, vacatingfromdate character varying, vacatingtodate character varying, hostelid integer, studentname character varying, studentid character varying, wardenapprovalstatus character varying, userrole character varying, approvallevel integer, approvalemail character varying, username character varying)
    RETURNS SETOF vacating_list_res
    LANGUAGE plpgsql
AS $function$
declare
res vacating_list_res%rowtype;
BEGIN
    if (wardenapprovalstatus is null or wardenapprovalstatus='') then wardenapprovalstatus='levelOneComplete,Pending,Approved'; end if;
    raise notice 'wardenapprovalstatus%',wardenapprovalstatus;
    if(userrole='Hostel Check In') then
        if (submittedfromdate is null or submittedfromdate='null') and (submittedtodate is null or submittedtodate='null') and
           (vacatingreason is null or vacatingreason='') and (vacatingfromdate is null or vacatingfromdate='null') and (vacatingtodate is null or vacatingtodate='null') and
           (hostelid=0) and (studentname is null or studentname='') and
           (studentid is null or studentid='0') and (wardenapprovalstatus is null or wardenapprovalstatus='') and (username<>'null' and username is not null) and (userrole<>'null' and userrole='Hostel Check In') then
            for res in
select wrk.status,a.hostel_or_warden_approval_status as warden_status,wrk.id,authority_type,a.student_id,
       approval_email,email_id,a.id as request_id,b.student_name as student_name,b.hostel_name as hostel_name,
       b.room_number as room_no,vacating_date,vacating_reason,penality_amount,donation_amount
from schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST_VIEW" a
         join schooldev."IIT_W_VACATING_HOSTEL_STUDENT_WORKFLOW" wrk on(a.id=wrk.request_id and approval_level=approvallevel and wrk.active_flag='Y')
         join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (b.student_id=a.student_id)
         join schooldev."HOSTEL_USER_MAPPING" c on ((c.hostel_id=b.hostel_id) and c.active_flag='Y')
         join schooldev."USER_MANAGEMENT" h on ((c.user_name=h.user_name) and h.active_flag='Y')
where lower(h.user_name)=lower(username) and a.school_id=1 and a.active_flag='Y' order by a.created_at
    loop
                    return next res;
end loop;
else
            raise notice 'loginIDS: %', 'dsfsfa';
for res in
select wrk.status,a.hostel_or_warden_approval_status as warden_status,wrk.id,authority_type,a.student_id,
       approval_email,email_id,a.id as request_id,b.student_name as student_name,b.hostel_name as hostel_name,
       b.room_number as room_no,vacating_date,vacating_reason,penality_amount,donation_amount
from schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST_VIEW" a
         join schooldev."IIT_W_VACATING_HOSTEL_STUDENT_WORKFLOW" wrk on(a.id=wrk.request_id and approval_level=approvallevel and wrk.active_flag='Y')
         join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (b.student_id=a.student_id)
         join schooldev."HOSTEL_USER_MAPPING" c on ((c.hostel_id=b.hostel_id) and c.active_flag='Y')
         join schooldev."USER_MANAGEMENT" h on ((c.user_name=h.user_name) and h.active_flag='Y')
where lower(h.user_name)=lower(username) and a.school_id='1' and a.active_flag='Y'
/*and
(hostel_or_warden_approval_status = case when (wardenapprovalstatus <>'null' and wardenapprovalstatus<>'') then wardenapprovalstatus else 'Pending'  end or hostel_or_warden_approval_status =case when (wardenapprovalstatus <>'null' and   wardenapprovalstatus<>'') then wardenapprovalstatus else 'Approved'  end)*/
  and case when (wardenapprovalstatus <>'null' and wardenapprovalstatus<>'') then hostel_or_warden_approval_status in (select cat from regexp_split_to_table(wardenapprovalstatus, ',')as cat) else hostel_or_warden_approval_status in (wardenapprovalstatus) end
  and( case when (submittedfromdate::text<>'null' and submittedfromdate::date is not null) then a.created_at::date >=submittedfromdate::date
                            else 1=1 end)
  and (case when (submittedtodate::text<>'null' and submittedtodate::date is not null) then a.created_at::date <= submittedtodate::date
                            else 1=1 end)
  and (case when (vacatingreason is not null and vacatingreason<>'') then
                a.vacating_reason = vacatingreason
            else 1=1 end )
  and( case when (vacatingfromdate::text<>'null' and vacatingfromdate::date is not null) then vacating_date::date >=vacatingfromdate::date
                            else 1=1 end)
  and (case when (vacatingtodate::text<>'null' and vacatingtodate::date is not null) then vacating_date::date <= vacatingtodate::date
                            else 1=1 end)
  and (case when (studentid<>'NULL' and studentid is not null and studentid<>'0') then upper(a.student_id) like upper(studentid||'%') else 1=1 end)
  and (case when (studentname<>'null' and studentname is not null) then upper(acount_name) like upper( studentname||'%') else 1=1 end)
  and(case when hostelid<>'0'then hostel_name=(select hostel_name from schooldev."HOSTEL_MASTER" where hostel_id::text=hostelid::text)::text else 1=1 end)
                loop
                    return next res;
end loop;
end if;
end if;
    if(userrole='Warden') then
        raise notice 'login: %', 'warden';
        if (submittedfromdate is null or submittedfromdate='null') and (submittedtodate is null or submittedtodate='null') and
           (vacatingreason is null or vacatingreason='') and (vacatingfromdate is null or vacatingfromdate='null') and (vacatingtodate is null or vacatingtodate='null') and
           (hostelid=0) and (studentname is null or studentname='') and
           (studentid is null or studentid='0') and (wardenapprovalstatus is null or wardenapprovalstatus='') and (username<>'null' and username is not null) and (userrole<>'null' and userrole='Warden') then
            for res in
select wrk.status,a.hostel_or_warden_approval_status as warden_status,wrk.id,authority_type,a.student_id,
       approval_email,email_id,a.id as request_id,b.student_name as student_name,b.hostel_name as hostel_name,
       b.room_number as room_no,vacating_date,vacating_reason,penality_amount,donation_amount
from schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST_VIEW" a
         join schooldev."IIT_W_VACATING_HOSTEL_STUDENT_WORKFLOW" wrk on(a.id=wrk.request_id and approval_level=approvallevel and wrk.active_flag='Y')
         join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (b.student_id=a.student_id)
         join schooldev."WARDEN_HOSTEL_MAPPING" c on ((c.hostel_id=b.hostel_id) and c.active_flag='Y')
         join schooldev."WARDEN_INFO" h on ((c.warden_id=h.id) and h.active_flag='Y')
where (lower(h.ldap_username)=lower(username) or lower(h.associate_ldap_username)=lower(username)) and a.school_id='1' and a.active_flag='Y' and wrk.status<>'AutoApproved' order by a.created_at
    loop
                    return next res;
end loop;
else
            for res in
select wrk.status,a.hostel_or_warden_approval_status as warden_status,wrk.id,authority_type,a.student_id,
       approval_email,email_id,a.id as request_id,b.student_name as student_name,b.hostel_name as hostel_name,
       b.room_number as room_no,vacating_date,vacating_reason,penality_amount,donation_amount
from schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST_VIEW" a
         join schooldev."IIT_W_VACATING_HOSTEL_STUDENT_WORKFLOW" wrk on(a.id=wrk.request_id and approval_level=approvallevel and wrk.active_flag='Y')
         join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (b.student_id=a.student_id)
         join schooldev."WARDEN_HOSTEL_MAPPING" c on ((c.hostel_id=b.hostel_id) and c.active_flag='Y')
         join schooldev."WARDEN_INFO" h on ((c.warden_id=h.id) and h.active_flag='Y')
where (lower(h.ldap_username)=lower(username) or lower(h.associate_ldap_username)=lower(username))  and a.school_id='1' and a.active_flag='Y' and   wrk.status<>'AutoApproved' /*and
(hostel_or_warden_approval_status = case when (wardenapprovalstatus <>'null' and wardenapprovalstatus<>'') then wardenapprovalstatus else 'Pending'  end or hostel_or_warden_approval_status =case when (wardenapprovalstatus <>'null' and   wardenapprovalstatus<>'') then wardenapprovalstatus else 'Approved'  end)*/
  and case when (wardenapprovalstatus <>'null' and wardenapprovalstatus<>'') then hostel_or_warden_approval_status in (select cat from regexp_split_to_table(wardenapprovalstatus, ',')as cat) else hostel_or_warden_approval_status in (wardenapprovalstatus) end
  and( case when (submittedfromdate::text<>'null' and submittedfromdate::date is not null) then a.created_at::date >=submittedfromdate::date
                            else 1=1 end)
  and (case when (submittedtodate::text<>'null' and submittedtodate::date is not null) then a.created_at::date <= submittedtodate::date
                            else 1=1 end)
  and (case when (vacatingreason is not null and vacatingreason<>'') then
                a.vacating_reason = vacatingreason
            else 1=1 end )
  and( case when (vacatingfromdate::text<>'null' and vacatingfromdate::date is not null) then vacating_date::date >=vacatingfromdate::date
                            else 1=1 end)
  and (case when (vacatingtodate::text<>'null' and vacatingtodate::date is not null) then vacating_date::date <= vacatingtodate::date
                            else 1=1 end)
  and (case when (studentid<>'NULL' and studentid is not null and studentid<>'0') then upper(a.student_id) like upper(studentid||'%') else 1=1 end)
  and (case when (studentname<>'null' and studentname is not null) then upper(acount_name) like upper( studentname||'%') else 1=1 end)
  and(case when hostelid<>'0'then hostel_name=(select hostel_name from schooldev."HOSTEL_MASTER" where hostel_id::text=hostelid::text)::text else 1=1 end)
                loop
                    return next res;
end loop;
end if;
end if;
    if(userrole='SoftwareAdmin')or(userrole='CCW DEAN') then
        raise notice 'dfdfds%',userrole;
        if (submittedfromdate is null or submittedfromdate='null') and (submittedtodate is null or submittedtodate='null') and (vacatingreason is null or vacatingreason='') and (vacatingfromdate is null or vacatingfromdate='null') and (vacatingtodate is null or vacatingtodate='null') and
           (hostelid=0) and (studentname is null or studentname='') and
           (studentid is null or studentid='0') and (wardenapprovalstatus is null or wardenapprovalstatus='') and (username<>'null' and username is not null) and (userrole<>'null' and ((userrole='SoftwareAdmin')or(userrole='CCW DEAN')))
        then
            for res in
select wrk.status,a.hostel_or_warden_approval_status as warden_status,wrk.id,authority_type,a.student_id,
       approval_email,email_id,a.id as request_id,b.student_name as student_name,b.hostel_name as hostel_name,
       b.room_number as room_no,vacating_date,vacating_reason,penality_amount,donation_amount
from schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST_VIEW" a
         left join schooldev."IIT_W_VACATING_HOSTEL_STUDENT_WORKFLOW" wrk on(a.id=wrk.request_id and approval_level=approvallevel and wrk.active_flag='Y')
         join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (b.student_id=a.student_id)
where (case when (userrole like 'SoftwareAdmin') then a.hostel_or_warden_approval_status='Approved' else a.hostel_or_warden_approval_status in('Pending','Approved','levelOneComplete') end) and a.school_id='1' and a.active_flag='Y' order by
    case when hostel_or_warden_approval_status='levelOneComplete' then 1 when hostel_or_warden_approval_status='Pending' then 2 when hostel_or_warden_approval_status='ApproveComplete' then 3 end
    loop
                    return next res;
end loop;
else
            for res in
select wrk.status,a.hostel_or_warden_approval_status as warden_status,wrk.id,authority_type,a.student_id,
       approval_email,email_id,a.id as request_id,b.student_name as student_name,b.hostel_name as hostel_name,
       b.room_number as room_no,vacating_date,vacating_reason,penality_amount,donation_amount
from schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST_VIEW" a
         left join schooldev."IIT_W_VACATING_HOSTEL_STUDENT_WORKFLOW" wrk on(a.id=wrk.request_id and approval_level=approvallevel and wrk.active_flag='Y')
         join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (b.student_id=a.student_id)
where a.school_id='1' and a.active_flag='Y' /*and
(hostel_or_warden_approval_status = case when (wardenapprovalstatus <>'null' and wardenapprovalstatus<>'') then wardenapprovalstatus else 'Pending'  end or hostel_or_warden_approval_status =case when (wardenapprovalstatus <>'null' and   wardenapprovalstatus<>'') then wardenapprovalstatus else 'Approved'  end)*/
  and case when (wardenapprovalstatus <>'null' and wardenapprovalstatus<>'') then hostel_or_warden_approval_status in (select cat from regexp_split_to_table(wardenapprovalstatus, ',')as cat) else hostel_or_warden_approval_status in (wardenapprovalstatus) end
  and( case when (submittedfromdate::text<>'null' and submittedfromdate::date is not null) then a.created_at::date >=submittedfromdate::date
                            else 1=1 end)
  and (case when (submittedtodate::text<>'null' and submittedtodate::date is not null) then a.created_at::date <= submittedtodate::date
                            else 1=1 end)
  and (case when (vacatingreason is not null and vacatingreason<>'') then
                a.vacating_reason = vacatingreason
            else 1=1 end )
  and( case when (vacatingfromdate::text<>'null' and vacatingfromdate::date is not null) then vacating_date::date >=vacatingfromdate::date
                            else 1=1 end)
  and (case when (vacatingtodate::text<>'null' and vacatingtodate::date is not null) then vacating_date::date <= vacatingtodate::date
                            else 1=1 end)
  and (case when (studentid<>'NULL' and studentid is not null and studentid<>'0') then upper(a.student_id) like upper(studentid||'%') else 1=1 end)
  and (case when (studentname<>'null' and studentname is not null) then upper(acount_name) like upper( studentname||'%') else 1=1 end)
  and(case when hostelid<>'0'then hostel_name=(select hostel_name from schooldev."HOSTEL_MASTER" where hostel_id::text=hostelid::text)::text else 1=1 end)
                loop
                    return next res;
end loop;
end if;
end if;
    if(userrole='HM Office') then
        raise notice 'login: %', 'HM Office';
        if (submittedfromdate is null or submittedfromdate='null') and (submittedtodate is null or submittedtodate='null') and
           (vacatingreason is null or vacatingreason='') and (vacatingfromdate is null or vacatingfromdate='null') and (vacatingtodate is null or vacatingtodate='null') and
           (hostelid=0) and (studentname is null or studentname='') and
           (studentid is null or studentid='0') and (wardenapprovalstatus is null or wardenapprovalstatus='') and (username<>'null' and username is not null) and (userrole<>'null' and userrole='HM Office') then
            for res in
select wrk.status,a.hostel_or_warden_approval_status as warden_status,wrk.id,authority_type,a.student_id,
       approval_email,email_id,a.id as request_id,b.student_name as student_name,b.hostel_name as hostel_name,
       b.room_number as room_no,vacating_date,vacating_reason,penality_amount,donation_amount
from schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST_VIEW" a
         join schooldev."IIT_W_VACATING_HOSTEL_STUDENT_WORKFLOW" wrk on(a.id=wrk.request_id and approval_level=approvallevel and wrk.active_flag='Y' and authority_type=userrole)
         join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (b.student_id=a.student_id)
where a.school_id='1' and a.active_flag='Y'
    loop
                    return next res;
end loop;
else
            for res in
select wrk.status,a.hostel_or_warden_approval_status as warden_status,wrk.id,authority_type,a.student_id,
       approval_email,email_id,a.id as request_id,b.student_name as student_name,b.hostel_name as hostel_name,
       b.room_number as room_no,vacating_date,vacating_reason,penality_amount,donation_amount
from schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST_VIEW" a
         join schooldev."IIT_W_VACATING_HOSTEL_STUDENT_WORKFLOW" wrk on(a.id=wrk.request_id and approval_level=approvallevel and wrk.active_flag='Y' and approval_email like approvalemail||'%' and approval_name=userrole)
         join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (b.student_id=a.student_id)
where a.school_id='1' and a.active_flag='Y' /*and
(hostel_or_warden_approval_status = case when (wardenapprovalstatus <>'null' and wardenapprovalstatus<>'') then wardenapprovalstatus else 'Pending'  end or hostel_or_warden_approval_status =case when (wardenapprovalstatus <>'null' and   wardenapprovalstatus<>'') then wardenapprovalstatus else 'Approved'  end)*/
  and case when (wardenapprovalstatus <>'null' and wardenapprovalstatus<>'') then hostel_or_warden_approval_status in (select cat from regexp_split_to_table(wardenapprovalstatus, ',')as cat) else hostel_or_warden_approval_status in (wardenapprovalstatus) end
  and( case when (submittedfromdate::text<>'null' and submittedfromdate::date is not null) then a.created_at::date >=submittedfromdate::date
                            else 1=1 end)
  and (case when (submittedtodate::text<>'null' and submittedtodate::date is not null) then a.created_at::date <= submittedtodate::date
                            else 1=1 end)
  and (case when (vacatingreason is not null and vacatingreason<>'') then
                a.vacating_reason = vacatingreason
            else 1=1 end )
  and( case when (vacatingfromdate::text<>'null' and vacatingfromdate::date is not null) then vacating_date::date >=vacatingfromdate::date
                            else 1=1 end)
  and (case when (vacatingtodate::text<>'null' and vacatingtodate::date is not null) then vacating_date::date <= vacatingtodate::date
                            else 1=1 end)
  and (case when (studentid<>'NULL' and studentid is not null and studentid<>'0') then upper(a.student_id) like upper(studentid||'%') else 1=1 end)
  and (case when (studentname<>'null' and studentname is not null) then upper(acount_name) like upper( studentname||'%') else 1=1 end)
  and(case when hostelid<>'0'then hostel_name=(select hostel_name from schooldev."HOSTEL_MASTER" where hostel_id::text=hostelid::text)::text else 1=1 end)
                loop
                    return next res;
end loop;
end if;
end if;
END;
/*
drop type vacating_list_res;
create type vacating_list_res as (status character varying,warden_status character varying,id integer,authority_type character varying,student_id character varying,approval_email character varying,email_id character varying,request_id integer, student_name character varying,hostel_name character varying,room_no integer,vacating_date date,vacating_reason character varying,penality_amount character varying,donation_amount character varying);*/
$function$
;

CREATE OR REPLACE FUNCTION schooldev.wellness_data_list(studentname character varying, studentid character varying, submittedfromdate character varying, submittedtodate character varying, refferalfrom character varying, refferalto character varying, referraltype character varying, concerntype character varying, numofvisit character varying, coordinatorname character varying, getdata character varying, visitfromdate character varying, visittodate character varying, userid character varying, roledesignation character varying, department character varying)
    RETURNS SETOF wellnessdatalist
    LANGUAGE plpgsql
AS $function$
declare res wellnessdatalist%rowtype;
BEGIN
    if(getdata is null or getdata!='downloadExcel') then
        if (studentname is null or studentname='') and (studentid is null or studentid='')
            and (submittedfromdate is null or submittedfromdate='' or submittedfromdate='null')
            and (submittedtodate is null or submittedtodate='' or submittedtodate='null')
            and (refferalfrom is null or refferalfrom='' or refferalfrom='null') and (refferalto is null or refferalto='' or refferalto='null')
            and (referraltype is null or referraltype='' or referraltype='0') and (concerntype is null or concerntype='' or concerntype='0')
            and (numofvisit is null or numofvisit='') and (coordinatorname is null or coordinatorname is null or coordinatorname='')
            and (visitfromdate is null or visitfromdate='' or visitfromdate='null') and (visittodate is null or visittodate='' or visittodate='null')
            and (department is null or department='' or department='0')
        then
            raise notice 'get: %', 'ifloop';
for res in
SELECT Distinct on( a.student_id ) a.student_id, a.wellness_id,
    case when (a.category='Others') then other_stud_name else b.student_name end as student_name,referral_date,referral_type,
                                                   concern_type,coordinated_name,a.created_at ::date, no_of_visit as num_of_visit,visit_date,follow_up_date,
                                                   hostel_name,room_number,student_mobile,student_iitm_smail,interaction_mode,
                                                   session_start_time,duration,concerns_discussed,future_action_plan,visit_status,a.category,other_stud_phone,other_stud_email
                FROM schooldev."STUDENT_WELLNESS_CATEGORICAL_DATA" a
                         left join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (a.student_id=b.student_id)
                         left join schooldev."STUDENT_WELLNESS_FOLLOWUP_DATA" d on (a.wellness_id=d.wellness_id)-- and c.num_of_visit=d.no_of_visit)
                where a.active_flag='Y'
                  and (a.created_at >=(now()::date-30))
-- and (lower(a.created_by) = lower(userid) or lower(a.modified_by) = lower(userid))
                  and (case when (roleDesignation::text is not null and lower(roleDesignation::text) = lower('WELLNESS OFFICER'::text))
                                then (lower(a.created_by) = lower(userid) or lower(a.modified_by) = lower(userid)) else 1=1 end)
                order by a.student_id asc, no_of_visit desc
                loop
                    return next res;
end loop;
else
            raise notice 'get: %', 'elseloop';
for res in
SELECT Distinct on (a.student_id) a.student_id, a.wellness_id,
    case when (a.category='Others') then other_stud_name else b.student_name end as student_name,referral_date,referral_type,
                                                  concern_type,coordinated_name,a.created_at ::date, no_of_visit as num_of_visit,visit_date,follow_up_date,
                                                  hostel_name,room_number,student_mobile,student_iitm_smail,interaction_mode,
                                                  session_start_time,duration,concerns_discussed,future_action_plan,visit_status,a.category,other_stud_phone,other_stud_email
                FROM schooldev."STUDENT_WELLNESS_CATEGORICAL_DATA" a
                         left join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (a.student_id=b.student_id)
                         left join schooldev."STUDENT_WELLNESS_FOLLOWUP_DATA" d on (a.wellness_id=d.wellness_id)
                where a.active_flag='Y'
                  and (case when (roleDesignation::text is not null and lower(roleDesignation::text) = lower('WELLNESS OFFICER'))
                                then (lower(a.created_by) = lower(userid) or lower(a.modified_by) = lower(userid)) else 1=1 end)
--and (lower(a.created_by) = lower(userid) or lower(a.modified_by) = lower(userid))
                  and (case when (studentname is not null and studentname<>'') then (lower(case when (a.category='Others') then other_stud_name else b.student_name end) like lower('%'||studentname||'%')) else 1=1 end)
                  and (case when (studentid is not null and studentid<>'') then upper(a.student_id) like upper('%'||studentid||'%') else 1=1 end)
                  and (case when (submittedfromdate::text<>'null' and submittedfromdate::date is not null and submittedfromdate::text<>'') then a.created_at::date>=submittedfromdate::date else 1=1 end)
                  and (case when (submittedtodate::text<>'null' and submittedtodate::date is not null and submittedtodate::text<>'') then a.created_at::date <= submittedtodate::date else 1=1 end)
                  and (case when (refferalfrom::text<>'null' and refferalfrom::date is not null and refferalfrom::text<>'') then referral_date::date >=refferalfrom::date else 1=1 end)
                  and (case when (refferalto::text<>'null' and refferalto::date is not null and refferalto::text<>'') then referral_date::date <= refferalto::date else 1=1 end)
                  and (case when (referraltype is not null and referraltype<>'' and referraltype<>'0') then lower(referral_type) = lower(referraltype) else 1=1 end)
                  and (case when (concerntype is not null and concerntype<>'0' and concerntype<>'') then lower(concern_type) = lower(concerntype) else 1=1 end)
                  and (case when (numofvisit<>'0' and numofvisit<>'') then (no_of_visit) = (numofvisit)::int when (numofvisit='0') then (no_of_visit) is null else 1=1 end)
                  and (case when (coordinatorname is not null and coordinatorname<>'') then (lower(coordinated_name) like lower('%'||coordinatorname||'%')) else 1=1 end)
                  and (case when (visitfromdate::text<>'null' and visitfromdate::date is not null and visitfromdate::text<>'')
                                then d.visit_date::date >= visitfromdate::date else 1=1 end)
                  and (case when (visittodate::text<>'null' and visittodate::date is not null and visittodate::text<>'')
                                then d.visit_date::date <= visittodate::date else 1=1 end)
                  and (case when (department::text is not null and department::text!='' and department::text!='0')
                                then (a.category='Students' and upper(LEFT(a.student_id, 2)) = upper(department)) else 1=1 end)
                order by a.student_id asc ,no_of_visit desc
                loop return next res;
end loop;
end if;
end if;
    if(getdata='downloadExcel') then
        if (studentname is null or studentname='') and (studentid is null or studentid='')
            and (submittedfromdate is null or submittedfromdate='' or submittedfromdate='null')
            and (submittedtodate is null or submittedtodate='' or submittedtodate='null')
            and (refferalfrom is null or refferalfrom='' or refferalfrom='null')
            and (refferalto is null or refferalto='' or refferalto='null') and (referraltype is null or referraltype='' or referraltype='0')
            and (concerntype is null or concerntype='' or concerntype='0') and (numofvisit is null or numofvisit='')
            and (coordinatorname is null or coordinatorname is null or coordinatorname='') and (visitfromdate is null or visitfromdate='' or visitfromdate='null')
            and (visittodate is null or visittodate='' or visittodate='null')
            and (department is null or department='' or department='0')
        then
            raise notice 'downloadExcel: %', 'ifloop';
for res in
SELECT a.student_id, a.wellness_id,
       case when (a.category='Others') then other_stud_name else b.student_name end as student_name,referral_date,referral_type,concern_type,coordinated_name,
       a.created_at ::date,no_of_visit as num_of_visit,visit_date,follow_up_date,
       hostel_name,room_number,student_mobile,student_iitm_smail,interaction_mode,session_start_time,duration,
       concerns_discussed,future_action_plan,visit_status,a.category,other_stud_phone,other_stud_email
FROM schooldev."STUDENT_WELLNESS_CATEGORICAL_DATA" a
         left join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (a.student_id=b.student_id)
         left join schooldev."STUDENT_WELLNESS_FOLLOWUP_DATA" c on (c.wellness_id=a.wellness_id)
where a.active_flag='Y'
--and (lower(a.created_by) = lower(userid) or lower(a.modified_by) = lower(userid))
  and (case when (roleDesignation::text is not null and lower(roleDesignation::text) = lower('WELLNESS OFFICER'))
                then (lower(a.created_by) = lower(userid) or lower(a.modified_by) = lower(userid)) else 1=1 end)
order by a.student_id asc,no_of_visit asc
    loop
                    return next res;
end loop;
else
            raise notice 'downloadExcel: %', 'elseloop';
for res in
SELECT a.student_id,a.wellness_id,
       case when (a.category='Others') then other_stud_name else b.student_name end as student_name,referral_date,referral_type,
       concern_type,coordinated_name,a.created_at ::date,no_of_visit as num_of_visit,visit_date,follow_up_date,
       hostel_name,room_number,student_mobile,student_iitm_smail,interaction_mode,
       session_start_time,duration,concerns_discussed,future_action_plan,visit_status,a.category,other_stud_phone,other_stud_email
FROM schooldev."STUDENT_WELLNESS_CATEGORICAL_DATA" a
         left join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (a.student_id=b.student_id)
         left join schooldev."STUDENT_WELLNESS_FOLLOWUP_DATA" c on (c.wellness_id=a.wellness_id)
where a.active_flag='Y'
--and (lower(a.created_by) = lower(userid) or lower(a.modified_by) = lower(userid))
  and (case when (roleDesignation::text is not null and lower(roleDesignation::text) = lower('WELLNESS OFFICER'))
                then (lower(a.created_by) = lower(userid) or lower(a.modified_by) = lower(userid)) else 1=1 end)
  and (case when (studentname is not null and studentname<>'') then (lower(case when (a.category='Others') then other_stud_name else b.student_name end ) like lower('%'||studentname||'%')) else 1=1 end)
  and (case when (studentid is not null and studentid<>'') then upper(a.student_id) like upper('%'||studentid||'%') else 1=1 end)
  and (case when (submittedfromdate::text<>'null' and submittedfromdate::date is not null and submittedfromdate::text<>'') then a.created_at::date>=submittedfromdate::date else 1=1 end)
  and (case when (submittedtodate::text<>'null' and submittedtodate::date is not null and submittedtodate::text<>'') then a.created_at::date <= submittedtodate::date else 1=1 end)
  and (case when (refferalfrom::text<>'null' and refferalfrom::date is not null and refferalfrom::text<>'') then referral_date::date >=refferalfrom::date else 1=1 end)
  and (case when (refferalto::text<>'null' and refferalto::date is not null and refferalto::text<>'') then referral_date::date <= refferalto::date else 1=1 end)
  and (case when (referraltype is not null and referraltype<>'' and referraltype<>'0') then lower(referral_type) = lower(referraltype) else 1=1 end)
  and (case when (concerntype is not null and concerntype<>'0' and concerntype<>'') then lower(concern_type) = lower(concerntype) else 1=1 end)
  and (case when (numofvisit<>'0' and numofvisit<>'') then (no_of_visit) = (numofvisit)::int when (numofvisit='0') then (no_of_visit) is null else 1=1 end)
  and (case when (coordinatorname is not null and coordinatorname<>'') then (lower(coordinated_name) like lower('%'||coordinatorname||'%')) else 1=1 end)
  and (case when (visitfromdate::text<>'null' and visitfromdate::date is not null and visitfromdate::text<>'')
                then c.visit_date::date >= visitfromdate::date else 1=1 end)
  and (case when (visittodate::text<>'null' and visittodate::date is not null and visittodate::text<>'')
                then c.visit_date::date <= visittodate::date else 1=1 end)
  and (case when (department::text is not null and department::text!='' and department::text!='0')
                then (category='Students' and upper(LEFT(a.student_id, 2)) = upper(department)) else 1=1 end)
order by a.student_id asc,no_of_visit asc
    loop return next res;
end loop;
end if;
end if;
END;
/* drop type wellnessdatalist cascade;
create type wellnessdatalist as (student_id character varying,wellness_id integer, student_name character varying,
referral_date character varying,referral_type character varying, concern_type character varying,
coordinated_name character  varying,created_at character  varying,num_of_visit integer,last_visit_date date,
next_follow_up_date date,hostel_name character varying,room_no character varying,student_mobile character varying,
student_iitm_smail character varying,interaction_mode character varying,session_start_time time,duration integer,
concerns_discussed character varying,future_action_plan character varying,visit_status character varying,
category character varying,other_stud_phone character varying,other_stud_email character varying);
select *  from  schooldev.wellness_data_list(NULL,NULL,'null','null','null','null',NULL,NULL,'','','downloadExcel',null,null)
select *  from  schooldev.wellness_data_list('','',NULL,NULL,'null','null','0','0',NULL,'','downloadExcel')
select *  from  schooldev.wellness_data_list(NULL,NULL,NULL,NULL,'null','null',NULL,NULL,NULL,NULL,NULL,'null','null','wellness.ohm','Wellness Officer',null)
select *  from  schooldev.wellness_data_list('ee',NULL,NULL,NULL,'null','null',NULL,NULL,NULL,NULL,NULL,'null','null','wellness.six','Wellness Report',null)
*/
$function$
;
create type schooldev.guestcouponlist as (request_id integer, category_wise character varying,student_id character varying,
    candidate_name character varying,dining_from_date date, dining_to_date date,
    Payment_Status character  varying, BreakfastCount  character varying,LunchCount  character varying,
    DinnerCount  character varying, Total_Amount character varying, approval_status character varying,
    created_at timestamp,mail_status character varying,order_no character varying,mess_name character varying,
    veg_or_nonveg character varying,snacksCount character varying);

CREATE OR REPLACE FUNCTION schooldev.guest_coupon_req_list(candidatename character varying, studentid character varying, category_wise character varying, diningfrom character varying, diningto character varying, paymentstatus character varying, submittedfromdate character varying, submittedtodate character varying)
 RETURNS SETOF schooldev.guestcouponlist
 LANGUAGE plpgsql
AS $function$
declare    res schooldev.guestcouponlist%rowtype;
BEGIN
    if (candidatename is null or candidatename='')
        and (studentid is null or studentid='')
        and (diningfrom is null or diningfrom='') and (diningto is null or diningto='')
        and (paymentstatus is null or paymentstatus='' or paymentstatus='0')
        and (submittedfromdate is null or submittedfromdate='null')
        and (submittedtodate is null or submittedtodate='null')
    then
        raise notice 'ifloop: %',  '1';
        if((category_wise is null or category_wise='' or category_wise='0' or category_wise='null'))
        then
            raise notice 'category_wise: %',  'null';
for res in
select  a.request_id,category,student_id, candidate_name, dining_from_date , dining_to_date,
        a.payment_status as Payment_Status, no_of_breakfast_coupons as BreakfastCount, no_of_lunch_coupons as LunchCount,
        no_of_dinner_coupons as DinnerCount,a.overall_amount as Total_Amount ,approval_status,a.created_at,mail_status,
        b.order_no, c.mess_name,veg_or_nonveg,no_of_snacks_coupons as snacksCount
from schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" a
         left join schooldev."IITM_GUEST_COUPON_ONLINE_PAYMENT_TRANSACTIONS" b on (a.request_id=b.request_id and b.active_flag='Y' and b.payment_status='Success')
         left join  schooldev."MESS_MASTER" c on (a.mess_id=c.mess_master_id and c.active_flag='Y')
where a.active_flag='Y' and a.created_at::date >= now()::date- '7 Day'::interval
                  and lower(category) != lower('Online Coupon')
order by  a.created_at desc
    loop
    return next res;
end loop;
else --if (category_wise='Online Coupon')
            raise notice 'category_wise: %',  category_wise;
for res in
select  a.request_id,category,student_id, candidate_name, dining_from_date , dining_to_date,
        case when (b.payment_status!=null and b.payment_status='Success') then b.payment_status else a.payment_status end as Payment_Status,
        no_of_breakfast_coupons as BreakfastCount, no_of_lunch_coupons as LunchCount,
        no_of_dinner_coupons as DinnerCount,a.overall_amount as Total_Amount ,approval_status,a.created_at,mail_status,
        b.order_no, c.mess_name,veg_or_nonveg,no_of_snacks_coupons as snacksCount
from schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" a
         left join schooldev."IITM_GUEST_COUPON_ONLINE_PAYMENT_TRANSACTIONS" b on (a.request_id=b.request_id and b.active_flag='Y' and b.payment_status='Success')
         left join  schooldev."MESS_MASTER" c on (a.mess_id=c.mess_master_id and c.active_flag='Y')
where a.active_flag='Y' --and a.created_at::date >= now()::date- '2 Day'::interval
  and lower(category) = lower(category_wise)
  and (case when (category_wise='Online Coupon') then a.created_at::date >= now()::date- '2 Day'::interval else 1=1 end)

order by  a.created_at desc
    loop
                    return next res;
end loop;
end if;

else
        raise notice 'else: %',  studentid;
for res in
select a.request_id, category, student_id, candidate_name, dining_from_date, dining_to_date,
       case when (b.payment_status is not null and b.payment_status='Success') then b.payment_status else a.payment_status end as payment_status,
       no_of_breakfast_coupons as BreakfastCount, no_of_lunch_coupons as LunchCount,
       no_of_dinner_coupons as DinnerCount,a.overall_amount as Total_Amount, approval_status,a.created_at,mail_status,
       b.order_no, c.mess_name,veg_or_nonveg,no_of_snacks_coupons as snacksCount
from schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" a
         left join schooldev."IITM_GUEST_COUPON_ONLINE_PAYMENT_TRANSACTIONS" b on (a.request_id=b.request_id and b.active_flag='Y' and b.payment_status='Success')
         left join  schooldev."MESS_MASTER" c on (a.mess_id=c.mess_master_id and c.active_flag='Y')
where a.active_flag='Y'
  and (case when (candidatename is not null and candidatename<>'') then (lower(candidate_name) like lower('%'||candidatename||'%')) else 1=1 end)
  and (case when (studentid is not null and studentid<>'') then (upper(student_id) = upper(studentid)) else 1=1 end )
  and (case when (category_wise is not null and category_wise<>'null' and category_wise<>'0'  and category_wise<>'') then lower(category) = lower(category_wise) else lower(category) != lower('Online Coupon') end)
  and( case when (diningfrom::text<>'null' and diningfrom::date is not null and diningfrom::text<>'') then  dining_from_date::date >=diningfrom::date else 1=1 end)
  and( case when (diningto::text<>'null' and diningto::date is not null and diningto::text<>'') then  dining_to_date::date <= diningto::date else 1=1 end)
  and (case when (paymentstatus is not null and paymentstatus<>'' and paymentstatus<>'0') then lower(a.payment_status) = lower(paymentstatus) else 1=1 end)
  and(case when (submittedfromdate::text<>'null' and submittedfromdate::date is not null) then  a.created_at::date>=submittedfromdate::date else 1=1 end)
  and (case when (submittedtodate::text<>'null' and submittedtodate::date is not null) then a.created_at::date <= submittedtodate::date else 1=1 end)
order by  a.created_at desc
    loop return next res;
end loop;
end if;

END;

/* drop type schooldev.guestcouponlist cascade;
 create type schooldev.guestcouponlist as (request_id integer, category_wise character varying,student_id character varying,
	candidate_name character varying,dining_from_date date, dining_to_date date,
	Payment_Status character  varying, BreakfastCount  character varying,LunchCount  character varying,
	DinnerCount  character varying, Total_Amount character varying, approval_status character varying,
	created_at timestamp,mail_status character varying,order_no character varying,mess_name character varying,
	veg_or_nonveg character varying,snacksCount character varying);

select *  from  schooldev.guest_coupon_req_list('','CY18D032','0','','','0','null','null')
select *  from  schooldev.guest_coupon_req_list('','','','','','0','null','null')
select *  from  schooldev.guest_coupon_req_list('','','null','','','0','2023-07-28','null')
select *  from  schooldev.guest_coupon_req_list(NULL,NULL,'IITM Students',NULL,NULL,NULL,'null','null')
select *  from  schooldev.guest_coupon_req_list(NULL,NULL,'Online Coupon',NULL,NULL,NULL,'null','null')
select *  from  schooldev.guest_coupon_req_list('','','Online Coupon','','','Pending','null','null')
select *  from  schooldev.guest_coupon_req_list('','AE22B028','Online Coupon','','','','null','null')
*/
$function$
;


CREATE OR REPLACE FUNCTION schooldev.get_student_current_balance(p_stud_id varchar, p_book_type varchar)
    RETURNS NUMERIC AS $$
DECLARE
total_balance NUMERIC;
    duration timestamp;
BEGIN
    duration = clock_timestamp();
    -- Calculate the total balance based on past transactions
SELECT
    sum(CASE WHEN t.debit_or_credit = 'c' THEN t.amount ELSE 0.0 END) -
    sum(CASE WHEN t.debit_or_credit = 'd' THEN t.amount ELSE 0.0 END) into total_balance
FROM (
         SELECT mla.amount, mla.debit_or_credit, spiv.new_id as student_id
         FROM schooldev."MESS_LEDGER_A" mla
                  JOIN schooldev.student_previous_id_view_2 spiv ON (mla.acchead in (spiv.old_id, spiv.new_id))
         WHERE mla.active_flag = 'Y' AND mla.cancel_status = 'N' AND mla.recon in ('', 'N') AND mla.book_type::text = p_book_type and spiv.new_id = p_stud_id
         UNION ALL
         SELECT mlb.amount, mlb.debit_or_credit, spiv.new_id as student_id
         FROM schooldev."MESS_LEDGER_B" mlb
             JOIN schooldev.student_previous_id_view_2 spiv ON (mlb.acchead in (spiv.old_id, spiv.new_id))
         WHERE mlb.active_flag = 'Y' AND mlb.cancel_status = 'N' AND mlb.recon in ('', 'N') AND mlb.book_type = p_book_type and spiv.new_id = p_stud_id
         UNION ALL
         SELECT mob.amount, mob.debit_or_credit, spiv.new_id as student_id
         FROM schooldev."MESS_OPENING_BAL" mob
             JOIN schooldev.student_previous_id_view_2 spiv ON (mob.acchead in (spiv.old_id, spiv.new_id))
         WHERE mob.active_flag = 'Y' AND spiv.new_id = p_stud_id) t;
raise notice 'Balance fetched for % for % in %', p_stud_id, p_book_type, (clock_timestamp() - duration);
return total_balance;
END;
/*

select * from schooldev.get_student_current_balance('CY22C052', 'MS');
select * from schooldev.get_student_current_balance('CY22C052', 'CC');

*/
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION schooldev.update_student_current_balance()
 RETURNS trigger
 LANGUAGE plpgsql
AS $function$
DECLARE
total_balance NUMERIC;
    total_balance_cc NUMERIC;
    existing      varchar;
    m_student_id  varchar;
BEGIN
    m_student_id = null;
select student_id into m_student_id from schooldev."STUDENT_DETAILS_INFO"
where student_id = NEW.acchead and active_flag = 'Y';

if (m_student_id is not null)
    then

select get_student_current_balance into total_balance from schooldev.get_student_current_balance(m_student_id, 'MS');
select get_student_current_balance into total_balance_cc from schooldev.get_student_current_balance(m_student_id, 'CC');
existing = null;
select student_id into existing from schooldev.student_net_balance where student_id = m_student_id;
if (existing is null)
        then
            INSERT into schooldev.student_net_balance values (student_id, total_balance, total_balance_cc);
else
UPDATE schooldev.student_net_balance SET net_balance_mess = total_balance, net_balance_card = total_balance_cc WHERE student_id = m_student_id;
end if;
end if;

RETURN NEW;
END;
$function$
;

CREATE OR REPLACE TRIGGER mess_ledger_balance_trigger
    AFTER INSERT OR UPDATE
                               ON schooldev."MESS_LEDGER_B"
                               FOR EACH ROW
                               EXECUTE FUNCTION schooldev.update_student_current_balance()
;

CREATE OR REPLACE FUNCTION schooldev.student_balance(p_student_id varchar)
    RETURNS double precision
    LANGUAGE plpgsql
AS $function$
declare
result double precision;
BEGIN
SELECT student_balance into result from schooldev.student_balance(p_student_id, 'MS');
RETURN result;
END;
$function$
;
CREATE OR REPLACE FUNCTION schooldev.student_balance(p_student_id varchar, book_type varchar)
    RETURNS double precision
    LANGUAGE plpgsql
AS $function$
declare
result double precision;
BEGIN
SELECT case when book_type = 'MS' then net_balance_mess when book_type = 'CC' then net_balance_card else 0 end into result
FROM schooldev.student_net_balance where student_id = p_student_id;
RETURN result;
END;
/*
select * from schooldev.student_balance('AE10D010', 'MS');
select * from schooldev.student_balance('AE10D010', 'CC');
*/
$function$
;

-- DROP FUNCTION schooldev.hostel_enrollment_list(varchar, varchar, varchar, int4, varchar, varchar);

CREATE OR REPLACE FUNCTION schooldev.hostel_enrollment_list(p_status character varying, p_stud_name character varying, p_stud_id character varying, p_hostel_id integer, p_user_role character varying, p_username character varying)
 RETURNS SETOF enrollment_list
 LANGUAGE plpgsql
AS $function$
declare
res enrollment_list%rowtype;
    default_status text := 'CheckedIn,Rejected,Validating';
BEGIN
    if p_hostel_id is null then p_hostel_id = 0; end if;
    if(p_user_role='Hostel Check In') then
        for res in
select schooldev.student_balance(a.student_id), a.id, a.student_id,student_name as stud_name, hostel_name, room_number, seat, mm.mess_head,
       (case when payment_type='bankloan' then loan_acc_no::character varying else payment_reference_no::character varying  end) as payment_reference_no,
       payment_date, payment_amount, hosteloffice_enrollment, push_status, override_and_approve as override_approve, approved_or_rejected_by as approved_by,
       approval_date as approvaldate, last_payment_date, total_payment_amount, se.student_id
from schooldev.current_mess_period mmc
         left join schooldev."STUDENT_MESS_DETAILS" smd on (smd.current_active_flag = 'Y' and smd.mmc_id = mmc.id)
         join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (b.student_id = smd.student_id)
         left join schooldev."STUDENT_HOSTEL_PAYMENTS" a on (b.student_id = a.student_id and a.active_flag = 'Y')
         join schooldev.student_enrollement_paid_amount_view se on (se.student_id = a.student_id)
         left join schooldev."MESS_MASTER" mm ON (smd.mess_id = mm.mess_master_id and mm.active_flag = 'Y')
         left join schooldev."HOSTEL_USER_MAPPING" c on (c.hostel_id = b.hostel_id and c.active_flag='Y')
         left join schooldev."USER_MANAGEMENT" um on (c.user_name = um.user_name and um.active_flag='Y')
where student_confirm_status = 'Payment Confirmed' and um.user_name = p_username
  and case when p_status is not null then hosteloffice_enrollment in (select cat from regexp_split_to_table(p_status, ',') as cat)
           else hosteloffice_enrollment in (select cat from regexp_split_to_table(default_status, ',') as cat) end
  and case when p_stud_id is not null then a.student_id like '%' || upper(p_stud_id) || '%' else 1 = 1 end
  and case when p_stud_name is not null then upper(student_name) like upper('%' || p_stud_name || '%') else 1 = 1 end
  and case when p_hostel_id <> 0 then hostel_name in (select hostel_name from schooldev."HOSTEL_MASTER" where hostel_id = p_hostel_id) else 1 = 1 end
ORDER BY a.student_id asc
    loop
                return next res;
end loop;
    ELSIF (upper(p_user_role) = 'CCW DEAN' or upper(p_user_role) = 'CCW' or upper(p_user_role) = 'DEAN' or upper(p_user_role) = 'CCW OFFICE' ) then
        raise notice 'loginIDS: %',  'ccw dean';
for res in
select schooldev.student_balance(a.student_id), a.id, a.student_id,student_name as stud_name, hostel_name, room_number, seat, mess_head,
       (case when payment_type='bankloan' then loan_acc_no::character varying else payment_reference_no::character varying  end) as payment_reference_no,
       payment_date, payment_amount, hosteloffice_enrollment, push_status, override_and_approve as override_approve, approved_or_rejected_by as approved_by,
       approval_date as approvaldate, last_payment_date, total_payment_amount, se.student_id
from schooldev.current_mess_period mmc
         left join schooldev."STUDENT_MESS_DETAILS" smd on (smd.current_active_flag = 'Y' and smd.mmc_id = mmc.id)
         join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (b.student_id = smd.student_id)
         left join schooldev."STUDENT_HOSTEL_PAYMENTS" a on (b.student_id = a.student_id and a.active_flag = 'Y')
         join schooldev.student_enrollement_paid_amount_view se on (se.student_id = a.student_id)
         left join schooldev."MESS_MASTER" mm ON (smd.mess_id = mm.mess_master_id and mm.active_flag = 'Y')
where student_confirm_status = 'Payment Confirmed'
  and case when p_status is not null then hosteloffice_enrollment in (select cat from regexp_split_to_table(p_status, ',') as cat)
           else hosteloffice_enrollment in (select cat from regexp_split_to_table(default_status, ',') as cat) end
  and case when p_stud_id is not null then a.student_id like '%' || upper(p_stud_id) || '%' else 1 = 1 end
  and case when p_stud_name is not null then upper(student_name) like upper('%' || p_stud_name || '%') else 1 = 1 end
  and case when p_hostel_id <> 0 then hostel_name in (select hostel_name from schooldev."HOSTEL_MASTER" where hostel_id = p_hostel_id) else 1 = 1 end
ORDER BY a.student_id asc
    loop
                return next res;
end loop;
end if;
END;

/*
drop type enrollment_list;
create type enrollment_list as (balance double precision, id bigint, student_id character varying, stud_name character varying, hostel_name character varying,
room_number character varying, seat character varying, mess_head character varying, payment_reference_no character varying, payment_date character varying,
payment_amount integer, hosteloffice_enrollment character varying, push_status character varying, override_approve character varying, approved_by character varying,
approvaldate character varying, last_payment_date date, total_payment_amount double precision, viewstudent_id  character varying);

select *  from schooldev.hostel_enrollment_list('Validating',null,null,'0','Hostel Check In','sindhu.hostel')
select *  from schooldev.hostel_enrollment_list('CheckedIn',NULL,NULL,null,'Dean','ccw.iitm')
select *  from schooldev.hostel_enrollment_list(NULL,NULL,NULL,'0','CCW DEAN','ccw.iitm')
select *  from schooldev.hostel_enrollment_list('','','','0','CCW DEAN','ccw.iitm')
select * from schooldev.hostel_enrollment_list(null,NULL,NULL,null,'Hostel Check In','bhadra.hostel');
select * from schooldev.hostel_enrollment_list('Validating',NULL,NULL,null,'Hostel Check In','bhadra.hostel');
select * from schooldev."ALL_STUDENTS_DETAILS_VIEW"
select * from schooldev.student_balance('AE10D010');
*/

$function$
;


/*CREATE OR REPLACE FUNCTION schooldev.hostel_enrollment_list(
    p_status character varying, p_stud_name character varying, p_stud_id character varying, p_hostel_id integer, p_user_role character varying, p_username character varying)
    RETURNS SETOF enrollment_list LANGUAGE plpgsql AS $function$
declare
res enrollment_list%rowtype;
BEGIN
    if p_status is null then  p_status='CheckedIn,Rejected,Validating'; end if;

    if(p_user_role='Hostel Check In') then
        if (p_status is null or p_status='') and (p_stud_name is null or p_stud_name='') and (p_stud_id is null or p_stud_id='0') and
           (p_hostel_id=0) and (p_username<>'null' and p_username is not null)  then
            for res in
select schooldev.student_balance(a.student_id), a.id, a.student_id,student_name as stud_name, hostel_name, room_number, seat, mess_head,
       (case when payment_type='bankloan' then loan_acc_no::character varying else payment_reference_no::character varying  end) as payment_reference_no,
       payment_date, payment_amount, hosteloffice_enrollment, pushstatus, override_and_approve as override_approve, approved_or_rejected_by as approved_by,
       approval_date as approvaldate, last_payment_date, total_payment_amount, se.student_id
from schooldev."STUDENT_HOSTEL_PAYMENTS" a
         join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (b.student_id = a.student_id and a.active_flag='Y')
         join schooldev.student_enrollement_paid_amount_view se on (se.student_id = a.student_id)
         left join schooldev."STUDENT_MESS_DETAILS" smd on(b.student_id = smd.student_id and current_active_flag='Y' and now()::date between smd.from_date and smd.to_date)
         left join schooldev."HOSTEL_USER_MAPPING" c on (c.hostel_id = b.hostel_id and c.active_flag='Y')
         left join schooldev."USER_MANAGEMENT" um on (c.user_name = um.user_name and um.active_flag='Y')
where student_confirm_status='Payment Confirmed' and a.created_at::date = now()::date
                  and um.user_name = p_username
ORDER BY a.student_id asc
    loop
    return next res;
end loop;
else
            raise notice 'loginIDS: %',  'dsfsfa';
for res in
select schooldev.student_balance(a.student_id), a.id, a.student_id,student_name as stud_name, hostel_name, room_number, seat, mess_head,
       (case when payment_type='bankloan' then loan_acc_no::character varying else payment_reference_no::character varying  end) as payment_reference_no,
       payment_date, payment_amount, hosteloffice_enrollment, pushstatus, override_and_approve as override_approve, approved_or_rejected_by as approved_by,
       approval_date as approvaldate, last_payment_date, total_payment_amount, se.student_id
from schooldev."STUDENT_HOSTEL_PAYMENTS" a
         join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (b.student_id = a.student_id and a.active_flag='Y')
         join schooldev.student_enrollement_paid_amount_view se on (se.student_id = a.student_id)
         left join schooldev."STUDENT_MESS_DETAILS" smd on(b.student_id = smd.student_id and current_active_flag='Y' and now()::date between smd.from_date and smd.to_date)
         left JOIN schooldev."MESS_MASTER_CONTROLLER" mmc on (mmc.dining_fromdate = smd.from_date and mmc.dining_todate = smd.to_date)
         left join schooldev."MESS_MASTER" mm ON (smd.mess_id = mm.mess_master_id and mm.active_flag='Y')
         left join schooldev."HOSTEL_USER_MAPPING" c on (c.hostel_id = b.hostel_id and c.active_flag='Y')
         left join schooldev."USER_MANAGEMENT" um on (c.user_name = um.user_name and um.active_flag='Y')
where student_confirm_status = 'Payment Confirmed' and (um.user_name)=lower(p_username)
  and case when p_status <> 'null' and p_status <> '' then hosteloffice_enrollment in (select cat from regexp_split_to_table(p_status, ',') as cat) else hosteloffice_enrollment in (p_status) end
  and case when p_stud_id is not null and p_stud_id not in ('null', 'NULL', '0', '') then a.student_id like '%' || p_stud_id || '%' else 1 = 1 end
  and case when p_stud_name is not null and p_stud_name not in ('null', 'NULL', '') then upper(student_name) like upper('%' || p_stud_name || '%') else 1 = 1 end
  and case when p_hostel_id <> 0 then hostel_name in (select hostel_name from schooldev."HOSTEL_MASTER" where hostel_id = p_hostel_id) else 1 = 1 end
ORDER BY a.student_id asc
    loop
                    return next res;
end loop;
end if;
    ELSIF (p_user_role = 'CCW DEAN') then
        raise notice 'loginIDS: %',  'ccw dean';
        raise notice '1%',p_status;
        raise notice '2%',p_stud_name;
        raise notice '3%',p_stud_id;
        raise notice '4%',p_hostel_id;
        raise notice '5%',p_username;

        if (p_status is null or p_status='') and (p_stud_name is null or p_stud_name='') and (p_stud_id is null or p_stud_id='0') and
           (p_hostel_id=0) and (p_username<>'null' and p_username is not null) then
            for res in
select schooldev.student_balance(a.student_id), a.id, a.student_id,student_name as stud_name, hostel_name, room_number, seat, mess_head,
       (case when payment_type='bankloan' then loan_acc_no::character varying else payment_reference_no::character varying  end) as payment_reference_no,
       payment_date, payment_amount, hosteloffice_enrollment, pushstatus, override_and_approve as override_approve, approved_or_rejected_by as approved_by,
       approval_date as approvaldate, last_payment_date, total_payment_amount, se.student_id
from schooldev."STUDENT_HOSTEL_PAYMENTS" a
         join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (b.student_id = a.student_id and a.active_flag='Y')
         join schooldev.student_enrollement_paid_amount_view se on (se.student_id = a.student_id)
         left join schooldev."STUDENT_MESS_DETAILS" smd on(b.student_id = smd.student_id and current_active_flag='Y' and now()::date between smd.from_date and smd.to_date)
         left join schooldev."HOSTEL_USER_MAPPING" c on (c.hostel_id = b.hostel_id and c.active_flag='Y')
         left join schooldev."USER_MANAGEMENT" um on (c.user_name = um.user_name and um.active_flag='Y')
where student_confirm_status='Payment Confirmed' and a.created_at::date = now()::date
ORDER BY a.student_id asc
    loop
    return next res;
end loop;
else
            raise notice 'loginIDS: %',  '1';
for res in
select schooldev.student_balance(a.student_id), a.id, a.student_id,student_name as stud_name, hostel_name, room_number, seat, mess_head,
       (case when payment_type='bankloan' then loan_acc_no::character varying else payment_reference_no::character varying  end) as payment_reference_no,
       payment_date, payment_amount, hosteloffice_enrollment, pushstatus, override_and_approve as override_approve, approved_or_rejected_by as approved_by,
       approval_date as approvaldate, last_payment_date, total_payment_amount, se.student_id
from schooldev."STUDENT_HOSTEL_PAYMENTS" a
         join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (b.student_id = a.student_id and a.active_flag='Y')
         join schooldev.student_enrollement_paid_amount_view se on (se.student_id = a.student_id)
         left join schooldev."STUDENT_MESS_DETAILS" smd on(b.student_id = smd.student_id and current_active_flag='Y' and now()::date between smd.from_date and smd.to_date)
         left JOIN schooldev."MESS_MASTER_CONTROLLER" mmc on (mmc.dining_fromdate = smd.from_date and mmc.dining_todate = smd.to_date)
         left join schooldev."MESS_MASTER" mm ON (smd.mess_id = mm.mess_master_id and mm.active_flag='Y')
         left join schooldev."HOSTEL_USER_MAPPING" c on (c.hostel_id = b.hostel_id and c.active_flag='Y')
         left join schooldev."USER_MANAGEMENT" um on (c.user_name = um.user_name and um.active_flag='Y')
where student_confirm_status = 'Payment Confirmed'
  and case when p_status <> 'null' and p_status <> '' then hosteloffice_enrollment in (select cat from regexp_split_to_table(p_status, ',') as cat) else hosteloffice_enrollment in (p_status) end
  and case when p_stud_id is not null and p_stud_id not in ('null', 'NULL', '0', '') then a.student_id like '%' || p_stud_id || '%' else 1 = 1 end
  and case when p_stud_name is not null and p_stud_name not in ('null', 'NULL', '') then upper(student_name) like upper('%' || p_stud_name || '%') else 1 = 1 end
  and case when p_hostel_id <> 0 then hostel_name in (select hostel_name from schooldev."HOSTEL_MASTER" where hostel_id = p_hostel_id) else 1 = 1 end
ORDER BY a.student_id asc
    loop
                    return next res;
end loop;
end if;
    elsif (p_user_role='CCW Office') then
        raise notice 'loginIDS: %',  'ccw Office';
        raise notice '1%',p_status;
        raise notice '2%',p_stud_name;
        raise notice '3%',p_stud_id;
        raise notice '4%',p_hostel_id;
        raise notice '5%',p_username;

        if (p_status is null or p_status='') and (p_stud_name is null or p_stud_name='') and (p_stud_id is null or p_stud_id='0') and
           (p_hostel_id=0) and (p_username<>'null' and p_username is not null) then
            for res in
select schooldev.student_balance(a.student_id), a.id, a.student_id,student_name as stud_name, hostel_name, room_number, seat, mess_head,
       (case when payment_type='bankloan' then loan_acc_no::character varying else payment_reference_no::character varying  end) as payment_reference_no,
       payment_date, payment_amount, hosteloffice_enrollment, pushstatus, override_and_approve as override_approve, approved_or_rejected_by as approved_by,
       approval_date as approvaldate, last_payment_date, total_payment_amount, se.student_id
from schooldev."STUDENT_HOSTEL_PAYMENTS" a
         join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (b.student_id = a.student_id and a.active_flag='Y')
         join schooldev.student_enrollement_paid_amount_view se on (se.student_id = a.student_id)
         left join schooldev."STUDENT_MESS_DETAILS" smd on(b.student_id = smd.student_id and current_active_flag='Y' and now()::date between smd.from_date and smd.to_date)
         left JOIN schooldev."MESS_MASTER_CONTROLLER" mmc on (mmc.dining_fromdate = smd.from_date and mmc.dining_todate = smd.to_date)
         left join schooldev."MESS_MASTER" mm ON (smd.mess_id = mm.mess_master_id and mm.active_flag='Y')
         left join schooldev."HOSTEL_USER_MAPPING" c on (c.hostel_id = b.hostel_id and c.active_flag='Y')
         left join schooldev."USER_MANAGEMENT" um on (c.user_name = um.user_name and um.active_flag='Y')
where student_confirm_status = 'Payment Confirmed' and a.created_at::date = now()::date
order by (case when hosteloffice_enrollment='CheckedIn' then 1 when hosteloffice_enrollment='Rejected' then 2 when hosteloffice_enrollment is null then 3 end),
    a.student_id asc
    loop
    return next res;
end loop;
else
            raise notice 'loginIDS: %',  '1';
for res in
select schooldev.student_balance(a.student_id), a.id, a.student_id,student_name as stud_name, hostel_name, room_number, seat, mess_head,
       (case when payment_type='bankloan' then loan_acc_no::character varying else payment_reference_no::character varying  end) as payment_reference_no,
       payment_date, payment_amount, hosteloffice_enrollment, pushstatus, override_and_approve as override_approve, approved_or_rejected_by as approved_by,
       approval_date as approvaldate, last_payment_date, total_payment_amount, se.student_id
from schooldev."STUDENT_HOSTEL_PAYMENTS" a
         join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (b.student_id = a.student_id and a.active_flag='Y')
         join schooldev.student_enrollement_paid_amount_view se on (se.student_id = a.student_id)
         left join schooldev."STUDENT_MESS_DETAILS" smd on(b.student_id = smd.student_id and current_active_flag='Y' and now()::date between smd.from_date and smd.to_date)
         left JOIN schooldev."MESS_MASTER_CONTROLLER" mmc on (mmc.dining_fromdate = smd.from_date and mmc.dining_todate = smd.to_date)
         left join schooldev."MESS_MASTER" mm ON (smd.mess_id = mm.mess_master_id and mm.active_flag='Y')
         left join schooldev."HOSTEL_USER_MAPPING" c on (c.hostel_id = b.hostel_id and c.active_flag='Y')
         left join schooldev."USER_MANAGEMENT" um on (c.user_name = um.user_name and um.active_flag='Y')
where student_confirm_status='Payment Confirmed'
  and case when p_status <> 'null' and p_status <> '' then hosteloffice_enrollment in (select cat from regexp_split_to_table(p_status, ',') as cat) else hosteloffice_enrollment in (p_status) end
  and case when p_stud_id is not null and p_stud_id not in ('null', 'NULL', '0', '') then a.student_id like '%' || p_stud_id || '%' else 1 = 1 end
  and case when p_stud_name is not null and p_stud_name not in ('null', 'NULL', '') then upper(student_name) like upper('%' || p_stud_name || '%') else 1 = 1 end
  and case when p_hostel_id <> 0 then hostel_name in (select hostel_name from schooldev."HOSTEL_MASTER" where hostel_id = p_hostel_id) else 1 = 1 end
ORDER BY a.student_id asc
    loop
                    return next res;
end loop;
end if;
end if;
END;

/*
drop type enrollment_list;
create type enrollment_list as (balance double precision, id bigint, student_id character varying, stud_name character varying, hostel_name character varying,
room_number character varying, seat character varying, mess_head character varying, payment_reference_no character varying, payment_date character varying,
payment_amount integer, hosteloffice_enrollment character varying, pushstatus character varying, override_approve character varying, approved_by character varying,
approvaldate character varying, last_payment_date date, total_payment_amount double precision, viewstudent_id  character varying);

select *  from schooldev.hostel_enrollment_list('Validating',null,null,'0','Hostel Check In','sindhu.hostel')
select *  from schooldev.hostel_enrollment_list('Validating',NULL,NULL,'0','CCW DEAN','ccw.iitm')
select *  from schooldev.hostel_enrollment_list(NULL,NULL,NULL,'0','CCW DEAN','ccw.iitm')
select *  from schooldev.hostel_enrollment_list('','','','0','CCW DEAN','ccw.iitm')
select * from schooldev.hostel_enrollment_list(NULL,NULL,NULL,'0','Hostel Check In','bhadra.hostel') ,
select * from schooldev."ALL_STUDENTS_DETAILS_VIEW"
select * from schooldev.student_balance('AE10D010');
*/

$function$
;*/

-- DROP FUNCTION schooldev.get_filtered_bulk_appointments(varchar, varchar, date, date, date, varchar, varchar);
CREATE OR REPLACE FUNCTION schooldev.get_filtered_bulk_appointments(p_approval_status character varying DEFAULT NULL::character varying, p_event_name character varying DEFAULT NULL::character varying, p_to_date date DEFAULT NULL::date, p_from_date date DEFAULT NULL::date, p_created_at date DEFAULT NULL::date, p_loginid character varying DEFAULT NULL::character varying, p_loginrole character varying DEFAULT NULL::character varying)
 RETURNS TABLE(slno bigint, bulk_appointment_id integer, createdat date, uploadedby character varying, event_name character varying, from_date date, to_date date, approval_status character varying, approval_notes character varying, file_name character varying, count bigint)
 LANGUAGE plpgsql
AS $function$
DECLARE
r RECORD;
    updated_login_id VARCHAR := p_loginid; -- Initialize with the provided loginid
BEGIN
    RAISE NOTICE 'Filtering with parameters: approval_status=%, event_name=%, to_date=%, from_date=%, created_at=%, loginid=%, loginrole=%',
        p_approval_status, p_event_name, p_to_date, p_from_date, p_created_at, p_loginid,p_loginrole;

    -- Update login_id logic (similar to the sample function)
    IF (p_loginid IS NOT NULL AND p_loginid <> '') THEN
        IF (LOWER(p_loginid) LIKE '% office' OR LOWER(p_loginid) = 'ccw dean') THEN
            updated_login_id := 'Dean';
END IF;
END IF;

    RAISE NOTICE 'Updated login_id: %', updated_login_id;
 RAISE NOTICE 'login role: %', p_loginrole;

    -- Dynamic filtering logic
FOR r IN
        WITH distinct_appointments AS (
            SELECT DISTINCT ON (a.bulk_appointment_id)
                a.bulk_appointment_id,
                a.created_at::DATE AS createdat,
                a.created_by AS createdby, -- Assuming created_by is the column for uploadedby
                a.event_name,
                a.from_date,
                a.to_date,
                a.approval_status,
                a.approval_notes,
                a.file_name,
                a.student_count
            FROM
                schooldev."IIT_W_STUDENT_MASTER_BULK_APPOINTMENT" a
                    LEFT JOIN
                schooldev."IIT_W_STUDENT_BULK_APPOINTMENT_DETAILS" d
                ON
                    a.bulk_appointment_id = d.bulk_appointment_id and d.active_flag = 'Y'
            WHERE a.active_flag = 'Y'
              AND (p_approval_status IS NULL OR p_approval_status = '' OR a.approval_status = p_approval_status)
              AND (p_event_name IS NULL OR p_event_name = '' OR a.event_name = p_event_name)
              AND (p_to_date IS NULL OR a.to_date::DATE = p_to_date)
              AND (p_from_date IS NULL OR a.from_date::DATE = p_from_date)
              AND (p_created_at IS NULL OR a.created_at::DATE = p_created_at)
               ---AND (updated_login_id = 'Dean' OR updated_login_id IS NULL) -- Additional logic based on updated_login_id
             AND (case when p_loginrole = 'Faculty' then a.created_by = p_loginid
					when lower(p_loginrole) LIKE '% office' then a.approval_status='Approved'
					else  p_loginrole = 'Dean' end)
			--AND (p_loginrole = 'Dean' OR lower(p_loginrole) LIKE '% office' OR a.created_by = p_loginid)

            ORDER BY
                a.bulk_appointment_id, a.created_at DESC
        )
SELECT
    ROW_NUMBER() OVER (ORDER BY da.bulk_appointment_id DESC) AS slNo,
    da.bulk_appointment_id,
    da.createdat,
    da.createdby,
    da.event_name,
    da.from_date,
    da.to_date,
    da.approval_status,
    da.approval_notes,
    da.file_name,
    da.student_count
FROM distinct_appointments da
ORDER BY da.createdat DESC

    LOOP
            -- Assign values to the OUT parameters in the specified order
            slNo := r.slNo;
bulk_appointment_id := r.bulk_appointment_id;
            createdat := r.createdat;
            uploadedby := r.createdby; -- Assign createdby to uploadedby
            event_name := r.event_name;
            from_date := r.from_date;
            to_date := r.to_date;
            approval_status := r.approval_status;
            approval_notes := r.approval_notes;
            file_name := r.file_name;
count := r.student_count; -- Assign count

            -- Return the row
            RETURN NEXT;
END LOOP;

    RETURN;
END;

/*

select * from schooldev.get_filtered_bulk_appointments(null,null,null,null,null,'','CCW Office');
select * from schooldev.get_filtered_bulk_appointments(null,null,null,null,null,'','Dean');
select * from schooldev.get_filtered_bulk_appointments(null,null,null,null,null,'FT231','Faculty');

*/
$function$
;

-- DROP FUNCTION schooldev.search_students_hostel(varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, int4, varchar, int4, varchar);

CREATE OR REPLACE FUNCTION schooldev.search_students_hostel(sstatus character varying, scategory character varying, app_from character varying, app_to character varying, sty_from character varying, sty_to character varying, sname character varying, sid character varying, svname character varying, svemail character varying, logintype character varying, submitted_from character varying, submitted_to character varying, approval_from character varying, approval_to character varying, facilityid integer, loginid character varying, tab integer, current_stay_flag character varying)
 RETURNS SETOF schooldev.student_record_result_hostel
 LANGUAGE plpgsql
AS $function$
declare
r schooldev.student_record_result_hostel%rowtype;
    updated_login_id character varying :=lower(logintype);
    facility_ids character varying;
BEGIN
    if(logintype='Warden') then
select group_concat(hm.hostel_id::text) into facility_ids
from schooldev."USER_MANAGEMENT" um
         join schooldev."WARDEN_INFO" w on (w.ldap_username = um.user_name) or (w.associate_ldap_username = um.user_name)
         join schooldev."WARDEN_HOSTEL_MAPPING" hm on (hm.warden_id=w.id)
where w.active_flag='Y' and um.active_flag='Y' and um.user_name=loginid group by um.user_name;
raise notice 'IDS: %', facility_ids;
--raise notice 'Login:%',logintype;
else
select group_concat(hostel_id::text) into facility_ids from schooldev."HOSTEL_USER_MAPPING" where user_name=loginid and active_flag = 'Y' group by 		user_name;
raise notice 'IDS: %', facility_ids;
end if;
    if (app_from is null or app_from='null') and (app_to is null or app_to='null') and
       (sty_from is null or sty_from='null') and (sty_to is null or sty_to='null') and
       (submitted_from is null or submitted_from='null') and (submitted_to is null or submitted_to='null') and
       (approval_from is null or approval_from='null') and (approval_to is null or approval_to='null') and
       (sstatus is null or sstatus='') and (scategory is null or scategory='') and
       (sid is null or sid='') and (sname is null or sname='') and
       (svemail is null or svemail='') and (facilityid=0) then
        if(tab=2) then scategory='outsidecampus,insidecampus,sasthra,other,stustayextension,SCHOLAR'; END IF;
        --if (tab=10) then scategory='SCHOLAR'; END IF;
        --if (tab=18) then scategory='stustayextension'; END IF;
        raise notice 'if:';
--if(lower(logintype) like '% office') then raise notice '1:';
for r in
SELECT a.status, wrk.status, a.request_id, dining, dining_others, a.student_id, a.created_at::date, a.student_name as student_name,
       a.gender as gender, a.dob as dob, a.student_iitm_smail as student_email, appointment_from, appointment_to, stay_from,
       stay_to, gross_pay, validating_authority, validating_authority_email, wrk_approval_notes as approval_notes,
       wrk_rejection_description as rejection_description, a.category, a.approval_date,hostel.hostel_name,
       hostel.room_no,hostel.sub_room_id,wrk.id as workflow_id,wrk.modified_at,a.thesis_submitted_date,a.admission_date,
       a.hostel_name,a.room_no,a.seat,
       (case when (logintype='Hostel Check In') then (case when (hostel.hostel_id is null) then false
                                                           when (hostel.hostel_id is not null and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat)) then true else false end) else 1=1 end ) as check_in_allowed_status,
       a.vacating_status,a.city,a.state,a.student_mobile,a.occupancy,a.purpose,hod_name,hod_email,
       a.category_others,a.cancel_description,wrk.authority_type,wrk.approval_level
FROM schooldev."COMPLETE_STUDENT_APPLICATION_VIEW" a
         join schooldev."IIT_W_STUDENT_WORKFLOW" wrk on (wrk.request_id=a.request_id and wrk.student_id = a.student_id and authority_type like '%'||'Dean'||'%')
         join schooldev."FACULTY_PERSONAL_DETAILS" on (wrk.validator_email like '%'||email_address||'%')
         left join (SELECT h2.hostel_name,h3.room_no,sub_room_id, student_type, v1.request_id,
                           h2.hostel_id, h3.room_id, v1.room_allotment_id
                    FROM schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW" v1
                             join schooldev."HOSTEL_FLOOR_MASTER" h1 on (h1.floor_id=v1.building_id)
                             join schooldev."HOSTEL_MASTER" h2 on(h2.hostel_id=h1.hostel_id)
                             join schooldev."HOSTEL_ROOM_INFO" h3 on(v1.room_id = h3.room_id)
                    where (CASE WHEN student_type = 'StudentApp' THEN (case when (logintype='Warden' or logintype = 'Hostel Check In') then (v1.room_allotment_id>0) and h2.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) else 1=1 end)
                                WHEN student_type = 'SCHOLARS' THEN (case when (logintype='Warden' or logintype = 'Hostel Check In') then (v1.room_allotment_id>0) and h2.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) else 1=1 end )
                                else 1=1 end)) as hostel on(a.request_id::text=hostel.request_id)
where a.active_flag = 'Y' and wrk.active_flag = 'Y' and a.school_id='1'
  and (case when (logintype='Hostel Check In') then (case when (hostel.hostel_id is null) then hostel.hostel_id::text in((select cat from regexp_split_to_table(facility_ids, ',')as cat)) else hostel.hostel_id::text in ((select cat from regexp_split_to_table(facility_ids, ',')as cat)) end) else 1=1 end )
  and case when lower(logintype) like '%office' then a.status = 'Approved' when (logintype='Warden') then a.status in ('Alloted') when (logintype = 'Hostel Check In') then a.status in ('Pending','Validating','Alloted','Approved') else wrk.status in ('Pending','Default') and a.status<>'Rejected' end
  and (case when (scategory is not null and scategory<>'') then
                lower(a.category) in (select lower(cat) from regexp_split_to_table(scategory, ',')as cat)
            else 1=1 end )
  and a.created_at >=(now()::date-90)
  and case when lower(logintype) like '% office' then (hostel.room_allotment_id is null or hostel.room_allotment_id=0) else 1=1 end
  and case when (lower(logintype)='Warden' or lower(logintype) = 'Hostel Check In') then (hostel.room_allotment_id>0) else 1=1 end
group by a.status, wrk.status, a.request_id, dining, dining_others, a.student_id, a.created_at::date,
                     a.student_name, a.gender, a.dob, a.student_iitm_smail, appointment_from,
                     appointment_to, stay_from, stay_to, gross_pay, validating_authority, validating_authority_email,
                     wrk_approval_notes, wrk_rejection_description, a.category, a.approval_date,hostel.hostel_name,
                     hostel.room_no,hostel.sub_room_id,wrk.id,wrk.modified_at,a.thesis_submitted_date ,a.admission_date,a.hostel_name,a.room_no,
                     a.seat,hostel.hostel_id,vacating_status,a.city,a.state,
                     a.student_mobile,occupancy,purpose,hod_name,hod_email,a.category_others,a.cancel_description,
                     wrk.authority_type,wrk.approval_level
order by a.created_at::date desc
    loop
    return next r;
end loop;
---------------------------------------------------------
else
        if(tab=2) then if(scategory is null or scategory='') then scategory='outsidecampus,insidecampus,sasthra,other,stustayextension,SCHOLAR'; end if; END IF;
        --if (tab=10) then if(scategory is null or scategory='') then scategory='SCHOLAR'; end if; END IF;
       -- if (tab=18) then if(scategory is null or scategory='') then scategory='stustayextension'; end if; END IF;
        raise notice 'else:';
for r in
SELECT a.status, wrk.status, a.request_id, dining, dining_others, a.student_id, a.created_at::date, a.student_name as student_name,
       a.gender as gender, a.dob as dob, a.student_iitm_smail as student_email, appointment_from, appointment_to, stay_from,
       stay_to, gross_pay, validating_authority, validating_authority_email, wrk_approval_notes as approval_notes,
       wrk_rejection_description as rejection_description, a.category, a.approval_date,hostel.hostel_name,
       hostel.room_no,sub_room_id,wrk.id as workflow_id,wrk.modified_at,a.thesis_submitted_date,a.admission_date,
       a.hostel_name,a.room_no,a.seat,
       (case when (logintype='Hostel Check In' or logintype='Warden' ) then (case when (hostel.hostel_id is null) then false
                                                                                  when (hostel.hostel_id is not null and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat)) then true else false end) else 1=1 end ) as check_in_allowed_status,
       a.vacating_status,a.city,a.state,a.student_mobile,a.occupancy,a.purpose,hod_name,hod_email,
       a.category_others,a.cancel_description,wrk.authority_type,wrk.approval_level
FROM schooldev."COMPLETE_STUDENT_APPLICATION_VIEW" a
         join schooldev."IIT_W_STUDENT_WORKFLOW" wrk on (wrk.request_id=a.request_id and wrk.student_id = a.student_id and authority_type like '%'||'Dean'||'%')
         join schooldev."FACULTY_PERSONAL_DETAILS" on (wrk.validator_email like '%'||email_address||'%')
         left join (SELECT h2.hostel_name,h3.room_no,sub_room_id, student_type, v1.request_id,
                           h2.hostel_id, h3.room_id, v1.room_allotment_id,stay_from_date, stay_to_date
                    FROM schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW" v1
                             join schooldev."HOSTEL_FLOOR_MASTER" h1 on (h1.floor_id=v1.building_id)
                             join schooldev."HOSTEL_MASTER" h2 on(h2.hostel_id=h1.hostel_id)
                             join schooldev."HOSTEL_ROOM_INFO" h3 on(v1.room_id = h3.room_id)
                    where (CASE WHEN student_type = 'StudentApp' THEN (case when (logintype='Warden' or logintype = 'Hostel Check In') then (v1.room_allotment_id>0) and h2.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) else 1=1 end)
                                WHEN student_type = 'SCHOLARS' THEN (case when (logintype='Warden' or logintype = 'Hostel Check In') then (v1.room_allotment_id>0) and h2.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) else 1=1 end )
                                else 1=1 end)) as hostel on(a.request_id::text=hostel.request_id)
where a.active_flag = 'Y' and wrk.active_flag = 'Y' and a.school_id='1'
  and ((case when (sstatus is not null and sstatus<>'') then
                 a.status in (select cat from regexp_split_to_table(sstatus, ',')as cat)
             else
                 (case when lower(logintype) like '% office' then a.status in ('Approved','Alloted', 'CheckedIn', 'CheckedOut')
                       when (logintype='Warden') then a.status in ('Alloted', 'CheckedIn', 'CheckedOut')
                       when (logintype='Hostel Check In') then a.status in ('Alloted', 'CheckedIn', 'CheckedOut','Validating','Pending','Approved')
                       else a.status not in ('Deleted','Cancelled') end) end)
    or (case when (sstatus is not null and sstatus<>'') then wrk.status in (sstatus)
             else
                 (case when lower(logintype) like '% office' then wrk.status in ('Approved')
                       when ( logintype='Warden') then a.status in ('Alloted', 'CheckedIn', 'CheckedOut')
                       when (logintype = 'Hostel Check In')then a.status in('Alloted', 'CheckedIn', 'CheckedOut','Validating','Pending','Approved')
                       else 1=1 and wrk.status not in ('Deleted','Cancelled') end) end))
  and (case when (scategory is not null and scategory<>'') then
                lower(a.category) in (select lower(cat) from regexp_split_to_table(scategory, ',')as cat)
            else 1=1 end )
  and case when (sid is not null and sid<>'') then a.student_id ilike '%'||sid||'%'
           else 1=1 end
  and case when (sname is not null and sname<>'') then a.student_name ilike '%'||sname||'%'
           else 1=1 end
  and case when (svname is not null and svname<>'') then a.validating_authority ilike'%'||svname||'%'
           else 1=1 end
  and case when svemail is not null then a.validating_authority_email ilike '%'||svemail||'%'
           else 1=1 end
  and case when (app_from::text<>'null' and app_from::date is not null) then appointment_from >=app_from::date
                       else 1=1 end
              and case when (app_to::text<>'null' and app_to::date is not null) then appointment_to <= app_to::date
                       else 1=1 end
              and case when (sty_from::text <>'null' and sty_from::date is not null) then stay_from >= sty_from::date
                       else 1=1 end
              and case when (sty_to::text <>'null' and sty_to::date is not null) then stay_to <= sty_to::date
                       else 1=1 end
/**For Summary page link starts **/
              and (case when current_stay_flag='All' then hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) and stay_from_date<=now()::date and stay_to_date>=now()::date
                        when current_stay_flag='todayCheckOut' then (hostel.room_allotment_id>0) and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) and stay_to_date=now()::date

                        when current_stay_flag='pendingCheckout' then   hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) and stay_to_date>=now()::date-30
                        when current_stay_flag='vacatingLink' then   hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) and stay_to::date=now()::date

                        else 1=1 end)
/**For Summary page link ends **/
              and case when (submitted_from::text <>'null' and submitted_from::date is not null) then a.created_at::date >= submitted_from::date
                       else 1=1 end
              and case when (submitted_to::text <>'null' and submitted_to::date is not null) then a.created_at::date <= submitted_to::date
                       else 1=1 end
              and case when (approval_from::text <>'null' and approval_from::date is not null) then (coalesce(a.approval_date,'2000-01-01'::date)) >= approval_from::date
                       else 1=1 end
              and case when (approval_to::text <>'null' and approval_to::date is not null) then (coalesce(a.approval_date,'2025-01-01'::date)) <= approval_to::date
                       else 1=1 end
              and case when (facilityid<>0) then (coalesce(hostel.hostel_id::int,0)) = facilityid
                       else 1=1 end
            group by a.status, wrk.status, a.request_id, dining, dining_others, a.student_id, a.created_at::date,
                     a.student_name, a.gender, a.dob, student_iitm_smail, appointment_from,
                     appointment_to, stay_from, stay_to, gross_pay, validating_authority, validating_authority_email,
                     wrk_approval_notes, wrk_rejection_description, a.category, a.approval_date,hostel.hostel_name,
                     hostel.room_no,hostel.sub_room_id,wrk.id,wrk.modified_at,a.thesis_submitted_date,a.admission_date,a.hostel_name,
                     hostel.hostel_id,a.room_no,a.seat,vacating_status,a.city,a.state,a.student_mobile,occupancy,
                     purpose,hod_name,hod_email,a.category_others,a.cancel_description,wrk.authority_type,wrk.approval_level
			order by a.created_at::date desc
            loop
                return next r;
end loop;
end if;
END;
/*
drop type schooldev.student_record_result_hostel;
CREATE TYPE schooldev.student_record_result_hostel as (app_status character varying, wrk_status character varying,
request_id bigint, dining character varying, dining_others character varying, student_id character varying,
created_at date,student_name character varying, gender character, dob character varying, student_email character varying,
appointment_from date, appointment_to date, stay_from date, stay_to date, gross_pay double precision,
validating_authority character varying, validating_authority_email character varying, approval_notes character varying,
rejection_description character varying, category character varying, approval_date date,
hostel_name character varying,room_no int ,seat character varying,workflow_id integer,
modified_at timestamp without time zone,thesis_submitted_date date,admission_date date,
current_hostel_name character varying,current_room_no character varying,current_sub_room character varying,
check_in_allowed_status character varying,vacating_status character varying,city character varying,
state character varying,student_mobile bigint,occupancy character varying,purpose character varying,
hod_name character varying,hod_email character varying,category_others character varying,cancel_description character varying,
authority_type character varying,approval_level integer);
select * from schooldev.search_students_hostel(NULL,NULL,'null','null','null','null',NULL,'',NULL,NULL,'CCW DEAN','null','null','null','null','0','ccw.iitm','2',NULL) as result
	select * from schooldev.search_students_hostel(NULL,NULL,'null','null','null','null','gad','hs',NULL,NULL,'CCW DEAN','null','null','null','null','0','ccw.iitm','2',NULL) as result
*/
$function$
;



-- DROP FUNCTION schooldev.mess_inspection_list(int8, int8, varchar, varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION schooldev.mess_inspection_list(wardenid bigint, messid bigint, fromdate character varying, todate character varying, userrole character varying, username character varying)
 RETURNS SETOF messinspectionlist
 LANGUAGE plpgsql
AS $function$
declare res messinspectionlist%rowtype;
BEGIN

    if(userrole='Dean' or userrole='SoftwareAdmin') then
        if (wardenid is null) and (messid is null) and (fromdate is null or fromdate='null') and (todate is null or todate='null')
            and (userrole is null or userrole='')
        then
            raise notice 'ifloop: %',  '1';
for res in

Select c.id,a.ldap_username,c.created_at::Date, c.warden_name, d.mess_name, cleanliness_kitchen,
       cleanliness_plate, queue_maintainance, hygiene_mess, availability_food, feedback_student, other_item
FROM schooldev."WARDEN_INFO" a
         join schooldev."mess_inspection_report" c on (a.id = c.warden_id)
         left join schooldev."MESS_MASTER" d on (d.mess_master_id=c.mess_master_id)
where a.active_flag = 'Y' and c.active_flag = 'Y'
  and c.created_at >=(now()::date-30) order by  created_at desc

    loop
                    return next res;
end loop;
else
            raise notice 'else: %', wardenid;
for res in


Select c.id,a.ldap_username,c.created_at::Date, c.warden_name, d.mess_name, cleanliness_kitchen,
       cleanliness_plate, queue_maintainance, hygiene_mess, availability_food, feedback_student, other_item, c.file_name
FROM schooldev."WARDEN_INFO" a
         join schooldev."mess_inspection_report" c on (a.id = c.warden_id)
         left join schooldev."MESS_MASTER" d on (d.mess_master_id=c.mess_master_id)
where a.active_flag = 'Y' and c.active_flag = 'Y'

  and (case when ( wardenid is not null and wardenid<>0) then ((a.id) = (wardenid)) else 1=1 end)
  and (case when (messid<>'0' and messid is not null) then ((c.mess_master_id) = (messid)) else 1=1 end)
  and(case when (fromdate::text<>'null' and fromdate::date is not null) then  c.created_at::date>=fromdate::date else 1=1 end)
  and (case when (todate::text<>'null' and todate::date is not null) then c.created_at::date <= todate::date else 1=1 end)

order by  created_at desc

    loop return next res;
end loop;
end if;
end if;

    if(userrole='Warden') then
        if (wardenid is null) and (messid is null) and (fromdate is null or fromdate='null') and (todate is null or todate='null')
            and (userrole is null or userrole='')
        then
            raise notice 'ifloop: %',  '3';
for res in

Select c.id,a.ldap_username,c.created_at::Date, c.warden_name, d.mess_name, cleanliness_kitchen,
       cleanliness_plate, queue_maintainance, hygiene_mess, availability_food, feedback_student, other_item, c.file_name
FROM schooldev."WARDEN_INFO" a
         join schooldev."mess_inspection_report" c on (a.id = c.warden_id)
         left join schooldev."MESS_MASTER" d on (d.mess_master_id=c.mess_master_id)
where a.active_flag = 'Y' and c.active_flag = 'Y' and
    a.id=wardenid  and c.created_at >=(now()::date-30) order by  created_atdesc


    loop
                    return next res;
end loop;
else
            raise notice 'loginIDS: %',  4;
for res in


Select c.id,a.ldap_username,c.created_at::Date, c.warden_name, d.mess_name, cleanliness_kitchen,
       cleanliness_plate, queue_maintainance, hygiene_mess, availability_food, feedback_student, other_item, c.file_name
FROM schooldev."WARDEN_INFO" a
         join schooldev."mess_inspection_report" c on (a.id = c.warden_id)
         left join schooldev."MESS_MASTER" d on (d.mess_master_id=c.mess_master_id)
where a.active_flag = 'Y' and c.active_flag = 'Y'

  and (case when (wardenid<>'0' and wardenid is not null) then ((c.warden_id) = (wardenid)) else 1=1 end)
  and (case when (messid<>'0' and messid is not null) then ((c.mess_master_id) = (messid)) else 1=1 end)
  and(case when (fromdate::text<>'null' and fromdate::date is not null) then  c.created_at::date>=fromdate::date else 1=1 end)
  and (case when (todate::text<>'null' and todate::date is not null) then c.created_at::date <= todate::date else 1=1 end)
  and (case when (userrole = 'Warden') then c.created_by = username else 1=1 end)

order by  created_at desc
    loop return next res;
end loop;
end if;
end if;

END;


/* drop type messinspectionlist cascade;
 create type  messinspectionlist as ( id integer,ldap_username character varying,created_at date,warden_name character varying, mess_name character varying, cleanliness_kitchen text, cleanliness_plate text, queue_maintainance text, hygiene_mess text, availability_food text, feedback_student text, other_item text, file_name character varying);


select *  from  schooldev.mess_inspection_list('32','0','null','null','Warden','ST325')
select *  from  schooldev.mess_inspection_list(null,'0','null','null','SoftwareAdmin','')

 */

$function$
;


-- DROP FUNCTION schooldev.search_candidates_hostel(varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, int4, varchar, varchar, varchar, varchar, varchar, int4, varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION schooldev.search_candidates_hostel(cstatus character varying, ccategory character varying, app_from character varying, app_to character varying, sty_from character varying, sty_to character varying, cname character varying, cid character varying, cvname character varying, cvemail character varying, stay character varying, tab integer, logintype character varying, submitted_from character varying, submitted_to character varying, approval_from character varying, approval_to character varying, facilityid integer, loginid character varying, cemail character varying, current_stay_flag character varying)
 RETURNS SETOF candidate_record_result_hostel
 LANGUAGE plpgsql
AS $function$
declare
count int :=0;
    updated_login_id character varying :=logintype;
    r candidate_record_result_hostel%rowtype;
    facility_ids character varying;
    max_limit int2 := 500;
BEGIN
    select config_value into max_limit from schooldev."SIMS_CONFIG_DATA" where config_key = 'FUNCTION_RECORD_LIMIT';
    raise notice 'Max Limit: %', max_limit;
    if(logintype='Warden') then
select string_agg(hostel_id::text, ',') into facility_ids
from schooldev."USER_MANAGEMENT" um
         join schooldev."WARDEN_INFO" w on (w.ldap_username = um.user_name) or (w.associate_ldap_username = um.user_name)
         join schooldev."WARDEN_HOSTEL_MAPPING" hm on (hm.warden_id=w.id)
where w.active_flag='Y' and um.active_flag='Y' and um.user_name=loginid group by um.user_name;
raise notice 'IDS: %', facility_ids;
else
select string_agg(hostel_id::text, ',') into facility_ids
from schooldev."HOSTEL_USER_MAPPING" where user_name=loginid and active_flag = 'Y' group by user_name;
raise notice 'IDS: %', facility_ids;

end if;
    if(lower(logintype)='icsr dean') then updated_login_id='ICSR Dean' ;
else updated_login_id='Dean(Students)' ;
end if;
    raise notice 'updated_login_id:%',updated_login_id;
    if (app_from is null or app_from='null') and (app_to is null or app_to='null') and (sty_from is null or sty_from='null') and
       (sty_to is null or sty_to='null') and  (submitted_from is null or submitted_from='null') and
       (submitted_to is null or submitted_to='null') and (approval_from is null or approval_from='null') and
       (approval_to is null or approval_to='null') and  (cstatus is null or cstatus='' or cstatus= 'null') and (ccategory is null or ccategory='null') and
       (cid is null or cid='') and (cname is null or cname='' or cname='null') and
       (cvname is null or cvname='' or cvname='null') and (cvemail is null or cvemail='' or cvemail='null') and
       (cemail is null or cemail='' or cemail='null') and
       (facilityid=0 or facilityid is null) then
        if(tab=0 or tab=1) then ccategory='icsr,others,internship,gian,convocation'; END IF;
        if (tab=7) then ccategory='INTERVIEWS,SASTHRA'; END IF;
        raise notice 'category:%',ccategory;

        if(stay is null or stay!='stay') then
            raise notice 'if,first if -updated_login_id:%',updated_login_id;
for r in
SELECT 'App' as ac_type, hostel.room_allotment_id, a.app_status as app_status, wrk.status as wrk_status,null as stay_status,
       a.request_id,app_dining, a.candidate_id, a.created_at::date, a.first_name, a.last_name,a.gender,a.date_of_birth,
       a.email, appointment_from, appointment_to,app_stayfrom, app_stayto, gross_pay,
       validating_authority,validating_authority_email, wrk_approval_notes as approval_notes, wrk_rejection_description as rejection_description,
       a.category, app_approval_date, post_select, a.employee_id, a.designation, 0 as stayid,
       hostel.hostel_name,hostel.room_no,hostel.sub_room_id,a.purpose,a.application_no,wrk.id,wrk.modified_at,
       (case when (logintype='Hostel Check In' or logintype = 'Warden') then (case when (hostel.hostel_id is null) then 'false'::character varying
                                                                                                   when (hostel.hostel_id is not null and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat)) then 'true'::character varying
                                                                                                   else 'false'::character varying end) else 'true'::character varying  end )::character varying as check_in_allowed_status,
                       a.mess_option,a.city,a.state,a.mobile_number,a.category_others,a.occupancy,a.program_dept,a.address1,a.address2,
                       a.pin,a.post_others,a.description
FROM  schooldev."COMPLETE_CANDIDATE_VIEW" a
    join schooldev."IIT_W_CANDIDATE_WORKFLOW" wrk on (wrk.application_id=a.request_id and wrk.candidate_id=a.candidate_id and authority_type like '%'|| updated_login_id || '%')
    join schooldev."FACULTY_PERSONAL_DETAILS"  on (wrk.email like '%'||email_address||'%')
    left join
    (SELECT stay_id,h2.hostel_name,h3.room_no,v1.sub_room_id, student_type,v1.request_id,
    h2.hostel_id, h3.room_id, v1.room_allotment_id
    FROM schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW" v1
    join schooldev."HOSTEL_FLOOR_MASTER" h1 on (h1.floor_id=v1.building_id and stay_id is not NULL and  stay_id='0')
    join schooldev."HOSTEL_MASTER" h2 on (h2.hostel_id=h1.hostel_id)
    join schooldev."HOSTEL_ROOM_INFO" h3 on(v1.room_id = h3.room_id)
    where student_type = 'Candidate'
    and case when (logintype = 'Hostel Check In' or logintype = 'Warden') then (v1.room_allotment_id>0) and
    h2.hostel_id::text in (select cat from
    regexp_split_to_table(facility_ids, ',')as cat) else 1=1 end) as hostel on(a.request_id=hostel.request_id::bigint)
where a.active_flag = 'Y' and  wrk.active_flag = 'Y' and a.school_id='1'
  and case when lower(logintype) like '% office' then a.app_status = 'Approved'
    when (logintype = 'Hostel Check In' or logintype = 'Warden') then a.app_status = 'Allotted'
    else a.app_status in ('Pending','Validating') end
                  and (case when (ccategory is not null and ccategory<>'')
                                then lower(a.category) in (select lower(cat) from regexp_split_to_table(ccategory, ',')as cat)
                            else 1=1 end )
                  and case when lower(logintype) like '% office' then (a.created_at >=(now()::date-10) or a.app_stayfrom>=now()::date)
                           else (a.created_at >=(now()::date-30) or a.app_stayfrom>=now()::date) end
                  and case when lower(logintype) like '% office' then (hostel.room_allotment_id is null or hostel.room_allotment_id=0) else 1=1 end
                  and case when  (logintype = 'Hostel Check In' or logintype = 'Warden') then (hostel.room_allotment_id>0) else 1=1 end
                group by ac_type, hostel.room_allotment_id, a.app_status, wrk.status,stay_status, a.request_id, app_dining,
                         a.candidate_id,a.created_at::date, a.first_name, a.last_name, a.gender, a.date_of_birth,
                         a.email, appointment_from, appointment_to, app_stayfrom, app_stayto,
                         gross_pay, validating_authority, validating_authority_email,
                         wrk_approval_notes, wrk_rejection_description, a.category,
                         app_approval_date, post_select, a.employee_id, a.designation, stayid,
                         status,hostel.hostel_name,hostel.room_no,hostel.sub_room_id,a.purpose,a.application_no,wrk.id,
                         wrk.modified_at,check_in_allowed_status,a.mess_option,a.city,a.state,a.mobile_number,a.category_others,
                         a.occupancy,a.program_dept,a.address1,a.address2,a.pin,a.post_others,a.description
    limit max_limit
                loop
                    return next r;	count=count+1;
end loop;
            raise notice 'count1:%', count;
end if;
        if(stay is null or stay!='app') then
            raise notice 'if,second if- logintype:%',logintype;
for r in
SELECT 'Stay' as ac_type, hostel.room_allotment_id,a.app_status,wrk.approval_status, v.stay_status as stay_status,
       a.request_id, app_dining, a.candidate_id, v.created_at::date,a.first_name, a.last_name,a.gender,a.date_of_birth,
       a.email,a.appointment_from, a.appointment_to, v.stay_from, v.stay_to, gross_pay,
       validating_authority,validating_authority_email, staywrk_approval_notes as approval_notes, staywrk_rejection_description as rejection_description,
       a.category, app_approval_date, post_select,a.employee_id, a.designation, v.stay_id as stayid,
       hostel.hostel_name,hostel.room_no,hostel.sub_room_id,a.purpose,a.application_no,wrk.id,wrk.modified_at,
       (case when (logintype='Hostel Check In' or logintype = 'Warden') then (case when (hostel.hostel_id is null) then 'false'::character varying
                                                                                                   when (hostel.hostel_id is not null and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat)) then 'true'::character varying
                                                                                                   else 'false'::character varying end) else 'true'::character varying  end )::character varying as check_in_allowed_status,
                       a.stay_mess_option,a.city,a.state,a.mobile_number,a.category_others,a.occupancy,a.program_dept,a.address1,a.address2,
                       a.pin,a.post_others,a.description
FROM  schooldev."CANDIDATE_STAY_DATE_LIST_VIEW" v
    join schooldev."COMPLETE_CANDIDATE_VIEW" a on (v.candidate_id=a.candidate_id and v.request_id=a.request_id and v.stay_id<>0)
    join schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW" wrk on (wrk.appointment_id=v.request_id and wrk.candidate_id=v.candidate_id and
    wrk.stay_id=v.stay_id and authority_type like '%'|| updated_login_id || '%')
    join schooldev."FACULTY_PERSONAL_DETAILS"  on (wrk.validator_email like '%'||email_address||'%')
    left join
    (SELECT stay_id,h2.hostel_name,h3.room_no,v1.sub_room_id, student_type,v1.request_id,
    h2.hostel_id, h3.room_id, v1.room_allotment_id
    FROM schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW" v1
    join schooldev."HOSTEL_FLOOR_MASTER" h1 on (h1.floor_id=v1.building_id and stay_id is not NULL and  stay_id<>'0')
    join schooldev."HOSTEL_MASTER" h2 on(h2.hostel_id=h1.hostel_id)
    join schooldev."HOSTEL_ROOM_INFO" h3 on(v1.room_id = h3.room_id)
    where student_type = 'Candidate'
    and case when (logintype = 'Hostel Check In' or logintype='Warden') then (v1.room_allotment_id>0)
    and h2.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) else 1=1 end)
    as hostel on(a.request_id=hostel.request_id::bigint and v.stay_id=hostel.stay_id::bigint)
where a.school_id='1' and  wrk.active_flag = 'Y'
  and case when lower(logintype) like '% office' then v.stay_status = 'Approved'
    when (logintype = 'Hostel Check In' or logintype='Warden') then v.stay_status = 'Allotted'
    else v.stay_status in ('Pending','Validating') end
                  and (case when (ccategory is not null and ccategory<>'')
                                then lower(a.category) in (select lower(cat) from regexp_split_to_table(ccategory, ',')as cat)
                            else a.category = a.category end )
                  and case when lower(logintype) like '% office' then (v.created_at >=(now()::date-10)  or v.stay_from>=now()::date)
                           else (v.created_at >=(now()::date-30)  or v.stay_from>=now()::date) end
                  and case when lower(logintype) like '% office' then (hostel.room_allotment_id is null or hostel.room_allotment_id=0) else 1=1 end
                  and case when  (logintype = 'Hostel Check In' or logintype = 'Warden') then (hostel.room_allotment_id>0) else 1=1 end
                group by ac_type, hostel.room_allotment_id,a.app_status,wrk.approval_status, v.stay_status, a.request_id,app_dining,
                         a.candidate_id, v.created_at::date, a.first_name, a.last_name,a.gender,a.date_of_birth,
                         a.email, a.appointment_from, a.appointment_to,v.stay_from, v.stay_to, gross_pay,
                         validating_authority,validating_authority_email,
                         staywrk_approval_notes,staywrk_rejection_description, a.category,
                         app_approval_date, post_select,a.employee_id, a.designation, v.stay_id,
                         a.approval_status,hostel.hostel_name,hostel.room_no,hostel.sub_room_id,a.purpose,a.application_no,wrk.id,
                         wrk.modified_at,check_in_allowed_status,a.stay_mess_option,a.city,a.state,a.mobile_number,a.category_others,
                         a.occupancy,a.program_dept,a.address1,a.address2,a.pin,a.post_others,a.description
limit max_limit
                loop
                    return next r;	count=count+1;
end loop;	raise notice 'count2:%', count;
end if;
----------------------------------------------------------------------------------------------------------
else
        --if(lower(logintype)='ccw office') then cstatus='Approved';end if;
        raise notice 'count4:%', cstatus;
        if (tab=0 or tab=1) then
            if(ccategory is null or ccategory='') then ccategory='icsr,others,internship,gian,convocation';
END IF;
END IF;
        if (tab=7) then
            if(ccategory is null or ccategory='') then ccategory='INTERVIEWS,SASTHRA';
END IF;
END IF;
        if(stay is null or stay!='stay') then
            raise notice 'else,first if:%', cstatus;
for r in
SELECT 'App' as ac_type, hostel.room_allotment_id, a.app_status as app_status, wrk.status as wrk_status,null as stay_status,
       a.request_id, app_dining, a.candidate_id, a.created_at::date, a.first_name, a.last_name,a.gender,a.date_of_birth,
       a.email, appointment_from, appointment_to,app_stayfrom, app_stayto, gross_pay,
       validating_authority,validating_authority_email, wrk_approval_notes as approval_notes, wrk_rejection_description as rejection_description,
       a.category, app_approval_date, post_select, a.employee_id, a.designation, 0 as stayid,
       hostel.hostel_name,hostel.room_no,hostel.sub_room_id,a.purpose,a.application_no,wrk.id,wrk.modified_at,
       (case when (logintype='Hostel Check In' or logintype = 'Warden') then (case when (hostel.hostel_id is null) then 'false'::character varying
                                                                                                   when (hostel.hostel_id is not null and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat)) then 'true'::character varying
                                                                                                   else 'false'::character varying end) else 'true'::character varying  end )::character varying as check_in_allowed_status,
                       a.mess_option,a.city,a.state,a.mobile_number,a.category_others,a.occupancy,a.program_dept,a.address1,a.address2,
                       a.pin,a.post_others,a.description
FROM  schooldev."COMPLETE_CANDIDATE_VIEW" a
    join schooldev."IIT_W_CANDIDATE_WORKFLOW" wrk on (wrk.application_id=a.request_id and wrk.candidate_id=a.candidate_id
    and (authority_type) like ('%'|| updated_login_id || '%') )
    join schooldev."FACULTY_PERSONAL_DETAILS"  on (wrk.email like '%'||email_address||'%')
    left join
    (SELECT stay_id,h2.hostel_name,h3.room_no,v1.sub_room_id, student_type,v1.request_id,
    h2.hostel_id, h3.room_id, v1.room_allotment_id, stay_from_date,stay_to_date
    FROM schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW" v1
    join schooldev."HOSTEL_FLOOR_MASTER" h1 on (h1.floor_id=v1.building_id and stay_id is not NULL and  stay_id='0')
    join schooldev."HOSTEL_MASTER" h2 on(h2.hostel_id=h1.hostel_id)
    join schooldev."HOSTEL_ROOM_INFO" h3 on(v1.room_id = h3.room_id)
    where student_type = 'Candidate') as hostel on(a.request_id=hostel.request_id::bigint)
where a.active_flag = 'Y' and  wrk.active_flag = 'Y' and a.school_id='1'
  and ((case when (cstatus is not null and cstatus<>'') then a.app_status in (select cat from regexp_split_to_table(cstatus, ',')as cat)
    else (case when lower(logintype) like '% office' then a.app_status in ('Approved','Allotted', 'CheckedIn', 'CheckedOut')
    when (logintype = 'Hostel Check In' or logintype = 'Warden') then a.app_status in ('Allotted', 'CheckedIn', 'CheckedOut','Validating','Pending','Approved')
    else a.app_status not in ('Deleted','Cancelled') end) end)
   or (case when (cstatus is not null and cstatus<>'') then wrk.status in (cstatus)
    else (case when lower(logintype) like '% office' then wrk.status in ('Approved')
    when (logintype = 'Hostel Check In' or logintype ='Warden') then a.app_status in ('Allotted', 'CheckedIn', 'CheckedOut','Validating','Pending','Approved')
    else  1=1 and wrk.status not in ('Deleted','Cancelled') end) end))
  and (case when (ccategory is not null and ccategory<>'') then lower(a.category) in (select lower(cat) from regexp_split_to_table(ccategory, ',')as cat) else 1=1 end)
  and (case when cid is not null and cid<>'' then a.candidate_id::text = cid else 1=1 end )
  and (case when (cname is not null and cname<>'') then lower(a.first_name||' '||a.last_name) like lower('%'||cname||'%') else 1=1 end)
  and (case when (cvname is not null and cvname<>'') then lower(a.validating_authority) like lower('%'||cvname||'%') else 1=1 end)
  and (case when cvemail is not null then lower(a.validating_authority_email) like lower('%'||cvemail||'%') else 1=1 end)
  and (case when cemail is not null then lower(a.email) like lower('%'||cemail||'%') else 1=1 end)
  and (case when (app_from::text<>'null' and app_from::date is not null) then appointment_from >= app_from::date else 1=1 end)
  and (case when (app_to::text<>'null' and app_to::date is not null) then appointment_to <= app_to::date else 1=1 end)
  and (case when (sty_from::text <>'null' and sty_from::date is not null) then app_stayfrom >= sty_from::date else 1=1 end)
  and (case when (sty_to::text <>'null' and sty_to::date is not null) then app_stayto <= sty_to::date else 1=1 end)
  and (case when current_stay_flag='All' then  (hostel.room_allotment_id>0) and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat)
  and stay_from_date<=now()::date and stay_to_date>=now()::date
    when current_stay_flag='todayCheckOut' then  (hostel.room_allotment_id>0) and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat)
  and stay_to_date=now()::date
    when current_stay_flag='pendingCheckout' then  (hostel.room_allotment_id>0) and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat)
  and stay_to_date>=now()::date - 30
    when current_stay_flag='vacatingLink' then  (hostel.room_allotment_id>0) and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat)
  and (app_stayto::date=now()::date) else 1=1 end)
  and (case when (submitted_from::text <>'null' and submitted_from::date is not null) then a.created_at::date >= submitted_from::date else 1=1 end)
  and (case when (submitted_to::text <>'null' and submitted_to::date is not null) then a.created_at::date <= submitted_to::date else 1=1 end)
  and (case when (approval_from::text <>'null' and approval_from::date is not null) then coalesce(app_approval_date,'2000-01-01'::date) >= approval_from::date else 1=1 end )
  and (case when (approval_to::text <>'null' and approval_to::date is not null) then coalesce(app_approval_date,'2025-01-01'::date) <= approval_to::date else 1=1 end )
  and (case when (facilityid<>0) then coalesce(hostel.hostel_id::int,0) = facilityid else 1=1 end)
--  else  case when (logintype = 'Hostel Check In' or logintype = 'Warden') then (hostel.room_allotment_id>0) and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) else 1=1 end end)
group by ac_type, hostel.room_allotment_id, a.app_status, wrk.status,stay_status, a.request_id, app_dining,
    a.candidate_id,a.created_at::date, a.first_name, a.last_name,a.gender,a.date_of_birth,
    a.email, appointment_from, appointment_to, app_stayfrom, app_stayto,gross_pay,
    validating_authority, validating_authority_email,
    wrk_approval_notes, wrk_rejection_description, a.category,
    app_approval_date, post_select, a.employee_id, a.designation, stayid,
    status,hostel.hostel_name,hostel.room_no,hostel.sub_room_id,a.purpose,a.application_no,wrk.id,
    wrk.modified_at,check_in_allowed_status,a.mess_option,a.city,a.state,a.mobile_number,a.category_others,
    a.occupancy,a.program_dept,a.address1,a.address2,a.pin,a.post_others,a.description
limit max_limit
    loop
    return next r;	count=count+1;
end loop;	raise notice 'count3:%', count;
end if;

        if(stay is null or stay!='app') then
            raise notice 'else,second if:%',updated_login_id;
for r in
SELECT 'Stay' as ac_type, hostel.room_allotment_id,a.app_status,wrk.approval_status, v.stay_status as stay_status,
       a.request_id, app_dining, a.candidate_id, a.stay_created_at::date, a.first_name, a.last_name,a.gender,a.date_of_birth,
       a.email, a.appointment_from, a.appointment_to, v.stay_from, v.stay_to, gross_pay,
       validating_authority,validating_authority_email,
       staywrk_approval_notes as approval_notes, staywrk_rejection_description as rejection_description,
       a.category, app_approval_date, post_select,a.employee_id, a.designation, v.stay_id as stayid,
       hostel.hostel_name,hostel.room_no,hostel.sub_room_id,a.purpose,a.application_no,wrk.id,wrk.modified_at,
       (case when (logintype='Hostel Check In' or logintype = 'Warden') then (case when (hostel.hostel_id is null) then 'false'::character varying
                                                                                                   when (hostel.hostel_id is not null and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat)) then 'true'::character varying
                                                                                                   else 'false'::character varying end) else 'true'::character varying  end )::character varying as check_in_allowed_status,
                       a.stay_mess_option,a.city,a.state,a.mobile_number,a.category_others,a.occupancy,a.program_dept,a.address1,a.address2,
                       a.pin,a.post_others,a.description
FROM  schooldev."CANDIDATE_STAY_DATE_LIST_VIEW" v
    join schooldev."COMPLETE_CANDIDATE_VIEW" a on (v.candidate_id=a.candidate_id and v.request_id=a.request_id and v.stay_id<>0)
    join schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW" wrk on (wrk.appointment_id=a.request_id and wrk.candidate_id=a.candidate_id and wrk.stay_id=v.stay_id and
    (authority_type) like ('%'|| updated_login_id || '%') )
    join schooldev."FACULTY_PERSONAL_DETAILS"  on (wrk.validator_email like '%'||email_address||'%')
    left join
    (SELECT stay_id,h2.hostel_name,h3.room_no,v1.sub_room_id, student_type,v1.request_id,
    h2.hostel_id, h3.room_id, v1.room_allotment_id, stay_from_date, stay_to_date
    FROM schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW" v1
    join schooldev."HOSTEL_FLOOR_MASTER" h1 on (h1.floor_id=v1.building_id and stay_id is not NULL and  stay_id<>'0')
    join schooldev."HOSTEL_MASTER" h2 on(h2.hostel_id=h1.hostel_id)
    join schooldev."HOSTEL_ROOM_INFO" h3 on(v1.room_id = h3.room_id)
    where student_type = 'Candidate') as hostel on(a.request_id=hostel.request_id::bigint and v.stay_id=hostel.stay_id::bigint)
where a.active_flag = 'Y' and a.school_id='1' and wrk.active_flag = 'Y'
  and ((case when (cstatus is not null and cstatus<>'') then v.stay_status  in (select cat from regexp_split_to_table(cstatus, ',')as cat)
    else case when lower(logintype) like '% office' then v.stay_status in ('Approved','Allotted', 'CheckedIn', 'CheckedOut')
    when  (logintype = 'Hostel Check In' or logintype = 'Warden') then v.stay_status in ('Allotted', 'CheckedIn', 'CheckedOut','Validating','Pending','Approved')
    else v.stay_status not in ('Deleted','Cancelled') end end )
   or (case when (cstatus is not null and cstatus<>'') then wrk.approval_status  in (cstatus)
    else case when lower(logintype) like '% office' then v.stay_status in ('Approved','Allotted', 'CheckedIn', 'CheckedOut')
    when  (logintype = 'Hostel Check In' or logintype ='Warden') then v.stay_status in ('Allotted', 'CheckedIn', 'CheckedOut','Validating','Pending','Approved')
    else wrk.approval_status not in ('Deleted','Cancelled')end end))
  and (case when (ccategory is not null and ccategory<>'') then lower(a.category) in (select lower(cat) from regexp_split_to_table(ccategory, ',')as cat)
    else 1=1 end )
  and (case when cid is not null and cid<>'' then a.candidate_id::text = cid else 1=1 end )
  and (case when (cname is not null and cname<>'') then lower(a.first_name||' '||a.last_name) like lower('%'||cname||'%') else 1=1 end)
  and (case when (cvname is not null and cvname<>'') then lower(a.validating_authority) like lower('%'||cvname||'%') else 1=1 end)
  and (case when cvemail is not null then lower(a.validating_authority_email) like lower('%'||cvemail||'%') else 1=1 end)
  and (case when cemail is not null then lower(a.email) like lower('%'||cemail||'%') else 1=1 end)
  and (case when (app_from::text<>'null' and app_from::date is not null) then a.appointment_from >= app_from::date else 1=1 end)
  and (case when (app_to::text<>'null' and app_to::date is not null) then a.appointment_to <= app_to::date else 1=1 end)
  and (case when (sty_from::text <>'null' and sty_from::date is not null) then v.stay_from >= sty_from::date else 1=1 end)
  and (case when (sty_to::text <>'null' and sty_to::date is not null) then v.stay_to <= sty_to::date else 1=1 end)
  and (case when current_stay_flag='All' then  (hostel.room_allotment_id>0) and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) and stay_from_date<=now()::date and stay_to_date>=now()::date
    when current_stay_flag='todayCheckOut' then  (hostel.room_allotment_id>0) and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) and  stay_to_date=now()::date
    when current_stay_flag='pendingCheckout' then  (hostel.room_allotment_id>0) and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) and  stay_to_date>=now()::date - 30
    when current_stay_flag='vacatingLink' then  (hostel.room_allotment_id>0) and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) and  (v.stay_to::date=now()::date) else 1=1 end)
  and (case when (submitted_from::text <>'null' and submitted_from::date is not null) then v.created_at::date >=  submitted_from::date else 1=1 end)
  and (case when (submitted_to::text <>'null' and submitted_to::date is not null) then v.created_at::date <= submitted_to::date else 1=1 end)
  and (case when (approval_from::text <>'null' and approval_from::date is not null) then coalesce(app_approval_date,'2000-01-01'::date) >= approval_from::date else 1=1 end )
  and (case when (approval_to::text <>'null' and approval_to::date is not null) then coalesce(app_approval_date,'2025-01-01'::date) <= approval_to::date else 1=1 end )
  and (case when (facilityid<>0) then coalesce(hostel.hostel_id::int,0) = facilityid else 1=1 end)
--else case when (logintype = 'Hostel Check In' or logintype = 'Warden') then (hostel.room_allotment_id>0) and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) else 1=1 end end)
group by ac_type, hostel.room_allotment_id,a.app_status,wrk.approval_status, v.stay_status, a.request_id,app_dining,
    a.candidate_id, a.stay_created_at::date, a.first_name, a.last_name,a.gender,a.date_of_birth,
    a.email, a.appointment_from,a.appointment_to, v.stay_from, v.stay_to, gross_pay,
    validating_authority, validating_authority_email,
    staywrk_approval_notes,staywrk_rejection_description, a.category,
    app_approval_date, post_select,a.employee_id, a.designation, v.stay_id,
    a.approval_status,hostel.hostel_name,hostel.room_no,hostel.sub_room_id,a.purpose,a.application_no,wrk.id,
    wrk.modified_at,check_in_allowed_status,a.stay_mess_option,a.city,a.state,a.mobile_number,a.category_others,
    a.occupancy,a.program_dept,a.address1,a.address2,a.pin,a.post_others,a.description
limit max_limit
    loop
    return next r;	count=count+1;
end loop;	raise notice 'count4:%', count;
end if;
end if;
END;
/*
DROP FUNCTION schooldev.search_candidates_hostel(character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, integer, character varying, character varying, character varying, character varying, character varying, integer, character varying, character varying, character varying);
drop type candidate_record_result_hostel cascade;
create type candidate_record_result_hostel as (app_type character
varying,room_allotment_id int, app_status character varying,wrk_status
character varying, approval_status character varying, request_id bigint,
dining character varying, candidate_id int,created_at date,first_name
character varying, last_name character varying, gender character,
date_of_birth character varying, email character varying,
appointment_from date, appointment_to date, stay_from date, stay_to
date, gross_pay double precision, validating_authority character
varying, validating_authority_email character varying, approval_notes
character varying, rejection_description character varying, category
character varying, approval_date date, post_select character varying,
employee_id character varying, designation character varying, stayid
int,hostel_name character varying,room_no int
,sub_room_id character varying,purpose character varying,application_no character varying,workflow_id integer,modified_at timestamp with time zone,check_in_allowed_status character varying,mess_option character varying,city character varying,state character varying,mobile_number character varying,category_others character varying,occupancy character varying,program_dept character varying,address1 text,address2 text,pin integer,post_others character varying,stayext_description character varying);

select * from schooldev.search_candidates_hostel('null','null','null','null','null','null','null','',NULL,NULL,'null','1','CCW Office','null','null','null','null','0','ccw.office','null',NULL) as result
select * from schooldev.search_candidates_hostel(NULL,NULL,'null','null','null','null',NULL,'',NULL,NULL,NULL,'1','Hostel Check In','null','null','null','null','0','cauvery.hostel',NULL,NULL) as result
select * from schooldev.search_candidates_hostel('','','null','null','null','null','',NULL,NULL,NULL,NULL,'1','Warden','2019-01-30','null','null','null','11','wardensarayu','','') as result
select * from schooldev.search_candidates_hostel('CheckedIn','','null','null','null','null','Aparna M',NULL,NULL,NULL,NULL,'1','Hostel Check In','null','null','null','null','0','sarayu.hostel','','') as result
select * from schooldev.search_candidates_hostel('null','interviews','null','null','null','null','null','',NULL,NULL,'null','7','Dean','null','null','null','null','0','ccw.iitm','null',NULL) as result
select * from schooldev.search_candidates_hostel('CheckedOut','null','null','null','null','null','null','',NULL,NULL,'null','1','Hostel Check In','null','null','null','null','0','cauvery.hostel','null',NULL) as result
select * from schooldev.search_candidates_hostel('Approved',NULL,'null','null','2025-01-01','null',NULL,'',NULL,NULL,NULL,'1','CCW Office','null','null','null','null','0','ccw.office',NULL,NULL) as result
select * from schooldev.search_candidates_hostel('null',NULL,'null','null','null','null',NULL,'',NULL,NULL,NULL,'1','CCW Office','null','null','null','null','0','ccw.office',NULL,NULL) as result

*/
$function$
;


CREATE OR REPLACE FUNCTION schooldev.search_room_allotment_logs(facilityid integer, fromdate character varying, todate character varying, createduser character varying, alloc_type character varying, stud_id character varying, stud_email character varying)
    RETURNS SETOF room_allotment_log_record_result
    LANGUAGE plpgsql
AS $function$
declare
r room_allotment_log_record_result%rowtype;
BEGIN
    --raise notice 'sub_from:%',submitted_from;
    if (fromdate is null or fromdate='null') and (todate is null or todate='null') and
       (createdUser is null or createdUser='') and (alloc_type is null or alloc_type='') and
       (stud_id is null or stud_id='') and (stud_email is null or stud_email='')
        and (facilityid=0) then

        for r in
select 0,0,new.student_type,new.allocation_type,new.student_id,new.student_name,new.email,old.hostel_name as prev_hostel,new.hostel_name as current_hostel,old.room_no as prev_room, new.room_no as current_room,new.created_by,new.created_at,new.shifted_date,new.vacate_date,new.stay_from_date, new.stay_to_date from schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW" as new left join schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW" as old on(new.allotment_id=old.new_allotment_id)
where new.created_at >=(now()::date-2)
order by new.created_at	desc
    loop
                return next r;
end loop;
else
        for r in
select new.hostel_id::text, old.hostel_id::text, new.student_type,new.allocation_type,new.student_id,new.student_name,new.email,old.hostel_name as prev_hostel,new.hostel_name as current_hostel,old.room_no as prev_room, new.room_no as current_room,new.created_by,new.created_at,new.shifted_date,new.vacate_date,new.stay_from_date, new.stay_to_date from schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW" as new left join schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW" as old on(new.allotment_id=old.new_allotment_id)
where (case when (facilityid<>0) then facilityid = coalesce(new.hostel_id::int,0) or facilityid = coalesce(old.hostel_id::int,0) else (old.hostel_id::text is not null or new.hostel_id::text is not null) end)
  and lower(new.email) like case when stud_email is not null then lower('%'||stud_email||'%') else lower(new.email) end
  and new.created_at::date >= case when (fromdate::text<>'null' and fromdate::date is not null) then fromdate::date else new.created_at::date end
              and new.created_at::date <= case when (todate::text<>'null' and todate::date is not null) then todate::date else new.created_at::date end
              and new.allocation_type = case when (alloc_type is not null and alloc_type<>'') then alloc_type else new.allocation_type end
              and case when (stud_id is null or stud_id='') then 1=1 else upper(new.student_id) like upper('%'||stud_id||'%') end
              and lower(new.created_by) like case when createdUser is not null then lower('%'||createdUser||'%') else lower(new.created_by) end
            order by new.created_at desc
            loop
                return next r;
end loop;
end if;
END;
    --drop type room_allotment_log_record_result
--create type room_allotment_log_record_result as (n_hostel_id text, old_hostel_id text, student_type character varying, allocation_type character varying, student_id character varying, student_name character varying, email character varying, prev_hostel character varying, current_hostel character varying, prev_room character varying, current_room character varying, created_by character varying, created_at timestamp, shifted_date character varying, vacatedate date,stay_from_date date, stay_to_date date);

--SELECT * from schooldev.search_room_allotment_logs(0,'null','null','60000100','V',Null,null)
$function$
;


CREATE OR REPLACE FUNCTION schooldev.mess_allotted_list(p_student_name character varying, p_student_id character varying, p_mess_period_id bigint, p_mess_id bigint)
 RETURNS SETOF schooldev.mess_list
 LANGUAGE plpgsql
AS $function$
DECLARE
res schooldev.mess_list%rowtype;
BEGIN

    if (p_student_name is null) and
       (p_student_id is null) and
       (p_mess_period_id = 0) and
       (p_mess_id = 0) then
        raise notice 'ifloop: %',  '1';
for res in
select smd.id as smd_id, mmc.id as mmc_id, smd.student_id, (first_name || ' ' || last_name) as student_name,
       mess_name, mess_head, smd.from_date, smd.to_date, smd.change_from_date, smd.change_to_date, smcw.approval_status, smd.push_remove_status, smd.push_status,
       smd.current_active_flag, smd.remarks, smd.description
from schooldev."MESS_MASTER_CONTROLLER" mmc
         join schooldev."STUDENT_MESS_DETAILS" smd on (mmc.id = smd.mmc_id)
         join schooldev."STUDENT_DETAILS_INFO" sdi on (sdi.student_id = smd.student_id)
         join schooldev."MESS_MASTER" mm on (mm.mess_master_id = smd.mess_id and mm.active_flag = 'Y')
         left join schooldev."STUDENT_MESS_CHANGE_WORKFLOW" smcw on (smcw.student_id = smd.student_id AND mmc.id = smcw.mmc_id
    and (smcw.approval_status is NULL or smcw.approval_status = 'Pending'))
WHERE smd.active_flag = 'Y' and (smd.exception_status is NULL or smd.exception_status <> 'Mess Payment Due')
  and current_date between mmc.dining_from_date::date and mmc.dining_to_date::date
            limit 50
            loop
                return next res;
end loop;
else
        raise notice 'loginIDS: ';
for res in
select smd.id as smd_id, mmc.id as mmc_id, smd.student_id, (first_name || ' ' || last_name) as student_name,
       mess_name, mess_head, smd.from_date, smd.to_date, smd.change_from_date, smd.change_to_date, smcw.approval_status, smd.push_remove_status, smd.push_status,
       smd.current_active_flag, smd.remarks, smd.description
from schooldev."MESS_MASTER_CONTROLLER" mmc
         join schooldev."STUDENT_MESS_DETAILS" smd on (mmc.id = smd.mmc_id)
         join schooldev."STUDENT_DETAILS_INFO" sdi on (sdi.student_id = smd.student_id)
         join schooldev."MESS_MASTER" mm on (mm.mess_master_id = smd.mess_id and mm.active_flag = 'Y')
         left join schooldev."STUDENT_MESS_CHANGE_WORKFLOW" smcw on (smcw.student_id = smd.student_id AND mmc.id = smcw.mmc_id
    and (smcw.approval_status is NULL or smcw.approval_status = 'Pending'))
WHERE smd.active_flag = 'Y' and (smd.exception_status is NULL or smd.exception_status <> 'Mess Payment Due')
  and (case when p_student_id is not null then upper(smd.student_id) like upper(p_student_id || '%') else 1=1 end)
  and (case when p_student_name is not null then upper(sdi.first_name) like upper('%' || p_student_name || '%') else 1=1 end)
  and (case when p_mess_period_id <> 0 then mmc.id = p_mess_period_id else 1=1 end)
  and (case when p_mess_id <> 0 then mm.mess_master_id = p_mess_id else 1=1 end)
    loop
                return next res;
end loop;
end if;
END;
/*
DROP FUNCTION schooldev.mess_allotted_list(varchar, varchar, int8, int8);
drop type if exists schooldev.mess_list;
create type schooldev.mess_list as (smd_id bigint, mmc_id bigint, student_id character varying, student_name character varying, mess_name character varying,
                                    mess_head character varying, from_date date, to_date date, change_from_date date, change_to_date date,
                                    approval_status character varying, push_remove_status character varying, push_status character varying,
                                    smd_current_active_flag character varying, smd_remarks character varying, smd_description character varying);
select *  from schooldev.mess_allotted_list(null, null, 0, 0)
select *  from schooldev.mess_allotted_list(null, null, 46, 0)
select *  from schooldev.mess_allotted_list(NULL, NULL, 0, 34)

*/
$function$
;

CREATE OR REPLACE FUNCTION schooldev.get_student_details_api(fromdate character varying, todate character varying)
    RETURNS SETOF schooldev.details_result1
    LANGUAGE plpgsql
AS $function$
declare
res schooldev.details_result1%rowtype;
BEGIN
    if((fromDate is not null and fromDate!='' and fromDate!='null') and (toDate is not null and toDate!='' and toDate!='null'))
    then
        RAISE NOTICE 'fromDate: %', fromDate;
for res in
            (
                WITH ranked_allotments AS (
                    SELECT *,
                           ROW_NUMBER() OVER (
                               PARTITION BY student_id
                               ORDER BY
                                   COALESCE(created_at) DESC NULLS LAST
                               ) AS rn
                    FROM schooldev."HOSTEL_ROOM_ALLOTMENT_INFO"
                    WHERE active_flag = 'Y'
                )
                select distinct sdi.student_id AS Roll_Number,first_name AS Name_of_the_student,
                                hm.hostel_name,hri.room_no,ra.sub_room_id as seat_name,
                                stay_from_date::date -1 AS Date_of_the_last_residence_change,
                                case when ra.vacate_date::date>= fromDate::date and ra.vacate_date::date<=toDate::date then 'N' else 'Y' end as hostel_student_status,
                                previous_id, ra.vacate_date,workflow_flag,workflow_date,1 as sno
                FROM ranked_allotments ra
                         JOIN schooldev."STUDENT_DETAILS_INFO" sdi ON (sdi.student_id = ra.student_id)
                         join schooldev."HOSTEL_ROOM_INFO" hri on (ra.room_id=  hri.room_id and hri.active_flag='Y' )
                         join schooldev."HOSTEL_FLOOR_MASTER" fm on (hri.building_id=fm.floor_id and fm.active_flag='Y')
                         join schooldev."HOSTEL_MASTER" hm on (fm.hostel_id=hm.hostel_id and hm.active_flag='Y')
                where ra.rn = 1 --AND d_hral_shifted_date is null and d_hral_vacatedate is null and
                  and (
                    --(::date>=fromDate::date and d_hral_vacatedate::date<=toDate::date) or
                    (ra.created_at::date>=fromDate::date and ra.created_at::date<=toDate::date) or
                    (ra.modified_at::date>=fromDate::date and ra.modified_at::date<=toDate::date) or
                    (sdi.created_at::date>=fromDate::date and sdi.created_at::date<=toDate::date) or
                    (sdi.modified_at::date>=fromDate::date and sdi.modified_at::date<=toDate::date)) )
            loop
                return next res;
end loop;
else
        RAISE NOTICE 'Else case executed';
for res in
            (
                WITH ranked_allotments AS (
                    SELECT *,
                           ROW_NUMBER() OVER (
                               PARTITION BY student_id
                               ORDER BY
                                   COALESCE(created_at) DESC NULLS LAST
                               ) AS rn
                    FROM schooldev."HOSTEL_ROOM_ALLOTMENT_INFO"
                    WHERE active_flag = 'Y'
                )

                select distinct sdi.student_id AS Roll_Number,first_name AS Name_of_the_student,
                                hm.hostel_name,hri.room_no,ra.sub_room_id as seat_name,
                                ra.stay_from_date::date -1 AS date_of_the_last_residence_change,
                                case when ra.vacate_date::date=now()::date then 'N' else 'Y' end as hostel_student_status,
                                previous_id, ra.vacate_date,workflow_flag,workflow_date,1 as sno
                FROM ranked_allotments ra
                         JOIN schooldev."STUDENT_DETAILS_INFO" sdi ON (sdi.student_id = ra.student_id)
                         join schooldev."HOSTEL_ROOM_INFO" hri on (ra.room_id=  hri.room_id and hri.active_flag='Y' )
                         join schooldev."HOSTEL_FLOOR_MASTER" fm on (hri.building_id=fm.floor_id and fm.active_flag='Y')
                         join schooldev."HOSTEL_MASTER" hm on (fm.hostel_id=hm.hostel_id and hm.active_flag='Y')
                where ra.rn = 1 --AND d_hral_shifted_date is null and d_hral_vacatedate is null
                  --and (case when d_hral_vacatedate::date=now()::date then d_hral_vacatedate is not null else d_hral_vacatedate is null end)
                  and(ra.created_at::date = now()::date or ra.modified_at::date = now()::date
                    or sdi.created_at::date=now()::date or sdi.modified_at::date=now()::date) )
            loop
                return next res;
end loop;
end if;
    RETURN;
END;
/*
DROP FUNCTION schooldev.get_student_details_api(varchar, varchar);
drop type schooldev.details_result1 cascade;
create type schooldev.details_result1 as(student_id character varying, student_name character varying,
hostel_name character varying, room_no int,seat_name character varying, date_of_last_residence date,
hostel_student_status character varying, v_sdi_previous_id character varying,
vacate_date character varying,workflow_flag character varying,workflow_date character varying);

select *  from schooldev.get_student_details_api('2025-03-01','2025-06-20') order by student_id asc;
select *  from schooldev.get_student_details_API(null,null) order by student_id;
*/
$function$
;

-- DROP FUNCTION schooldev.student_rollno_change(varchar, varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION schooldev.student_rollno_change(previousid character varying, changeid character varying, studentname character varying, requestdate character varying)
 RETURNS SETOF studentrollnochange
 LANGUAGE plpgsql
AS $function$
declare res studentrollnochange%rowtype;
BEGIN
    if (previousid is null or previousid='') and (changeid is null or changeid='') and (studentname is null or studentname='') and (requestdate is null or requestdate='')
    then
        raise notice 'ifloop: %',  '1';
for res in

SELECT  a.created_at, a.studentid, b.student_name as stud_name, new_roll_no, file_upload, status, id, school_id
FROM schooldev."STUDENT_ROLLNO_CHANGE" as a
         left join schooldev."ALL_STUDENTS_DETAILS_VIEW" as b on (b.student_id=a.studentid)
WHERE a.active_flag='Y' and a.created_at between now() - interval '1 week' and now() order by
    case when status='Pending' then 1 when status='Approved' then 2 end,created_at desc

            loop
                return next res;
end loop;
else
        raise notice 'else: %',  1;
for res in


SELECT a.created_at, a.studentid, b.student_name as stud_name, new_roll_no, file_upload, status, id, school_id
FROM schooldev."STUDENT_ROLLNO_CHANGE" as a
         left join schooldev."ALL_STUDENTS_DETAILS_VIEW" as b on (b.student_id=a.studentid)
WHERE a.active_flag='Y'

  and (case when (previousid<>'' and previousid is not null) then ((studentid) = (previousid)) else 1=1 end)
  and (case when (changeid<>'' and changeid is not null) then ((new_roll_no) = (changeid)) else 1=1 end)
  and (case when (studentname<>'' and studentname is not null) then upper(b.student_name) like upper('%'||b.student_name||'%')  else 1=1 end)
  and (case when (requestdate::text<>'null' and requestdate::date is not null) then created_at::date = requestdate::date else 1=1 end)

order by case when status='Pending' then 1 when status='Approved' then 2 end,created_at desc

    loop return next res;
end loop;
end if;
END;

/* drop type studentrollnochange cascade;
   create type  studentrollnochange as (created_at date, studentid character varying,  stud_name character varying, new_roll_no character varying, file_upload character varying, status character varying,id integer, school_id integer);

     select *  from  schooldev.student_rollno_change('AE08B007',NULL,NULL,NULl)
     select *  from  schooldev.student_rollno_change(NULL,NULL,NULL,'null');
     select *  from  schooldev.student_rollno_change()
    */

$function$
;


CREATE OR REPLACE FUNCTION schooldev.mess_billing_report(messid integer, messPeriodId integer)
    RETURNS TABLE(period_from_date date, period_to_date date, mess_name character varying, vendor_code character varying, roll_no character varying,
                  student_name text, student_mail text, from_date date, to_date date, dining_period_days integer, rebate_from_date date, rebate_to_date date,
                  rebate_days integer, mess_change_date date, mess_change_days integer, vacation_date date, vacation_before_days integer, total_dined_days integer,
                  per_day_rate double precision, dined_days_amount double precision, gst_percentage bigint, gst_amount double precision,
                  total_payable_amount double precision)
    LANGUAGE plpgsql
AS $function$
declare
rec record;
    per_day_amount double precision;
    gst_percentage double precision;
    rebate_from_date date;
    rebate_to_date date;
    rebate_total_days integer;
BEGIN
    raise notice 'messid:%',messid;
    raise notice 'messPeriodId :%',messPeriodId ;
RETURN QUERY
select
    a.dining_from_date as period_from_date ,
    a.dining_to_date as period_to_date,
    c.mess_name,g.vendor_code,b.student_id roll_no, d.student_name::text as student_name,
    d.student_iitm_smail::text as student_mail,
    b.change_from_date as from_date,
    b.change_to_date as to_date,
    (b.change_to_date::date - b.change_from_date::date + 1) as dining_period_days,
    e.rebate_from as rebate_from_date,
    case when  rebate_to>= a.dining_from_date and rebate_to <=a.dining_to_date then e.rebate_to else b.change_to_date end as rebate_to_date,
    (case when  rebate_to>= b.change_from_date and rebate_to <=b.change_to_date then rebate_to else b.change_to_date end -rebate_from)+1 as rebate_days,
    case when remarks = 'Change' then b.to_remove_date else null end as mess_change_date ,0 mess_change_days , vacating_date as vacation_date , vacating_date - b.change_from_date vacation_before_days ,
    ((b.change_to_date - b.change_from_date)  - ((case when rebate_to is not null and rebate_from is not null then (case when  rebate_to>= b.change_from_date and rebate_to <b.change_to_date then rebate_to else b.change_to_date end -rebate_from ) else 0 end ) + (
        case when vacating_date >= b.change_from_date
            and vacating_date <= b.change_to_date then vacating_date - b.change_from_date else 0 end ))) total_dined_days ,
    rate,(((b.change_to_date - b.change_from_date)  - ((case when rebate_to is not null and rebate_from is not null then (case when  rebate_to>= b.change_from_date and rebate_to <b.change_to_date then rebate_to else b.change_to_date end -rebate_from ) else 0 end )+ (case when vacating_date>= b.change_from_date
    and vacating_date <=b.change_to_date then vacating_date - b.change_from_date else 0 end ))) * rate) dined_days_amount,
    g.gst_percentage,(((b.change_to_date - b.change_from_date)  - ((case when rebate_to is not null and rebate_from is not null then (case when  rebate_to>= b.change_from_date and rebate_to <b.change_to_date then rebate_to else b.change_to_date end -rebate_from ) else 0 end )+ (case when vacating_date>= b.change_from_date
    and vacating_date <=b.change_to_date then vacating_date - b.change_from_date else 0 end ))) * g.gst_percentage)::double precision gst_amount ,
            (((b.change_to_date - b.change_from_date)  - ((case when rebate_to is not null and rebate_from is not null then (case when  rebate_to>= b.change_from_date and rebate_to <b.change_to_date then rebate_to else b.change_to_date end -rebate_from ) else 0 end )+ (case when vacating_date>= b.change_from_date
                and vacating_date <=b.change_to_date then vacating_date - b.change_from_date else 0 end ))) * rate) +
            (((b.change_to_date - b.change_from_date)  - ((case when rebate_to is not null and rebate_from is not null then (case when  rebate_to>= b.change_from_date and rebate_to <b.change_to_date then rebate_to else b.change_to_date end -rebate_from ) else 0 end )+ (case when vacating_date>= b.change_from_date
                and vacating_date <=b.change_to_date then vacating_date - b.change_from_date else 0 end ))) * g.gst_percentage) total_payable_amount from
    schooldev."MESS_MASTER_CONTROLLER" a
    join schooldev."STUDENT_MESS_DETAILS" b on (a.id = b.mmc_id and b.active_flag='Y' and b.current_active_flag='Y')
    join schooldev."MESS_MASTER" c on (b.mess_id =c.mess_master_id and c.active_flag='Y')
    join schooldev."MESS_ALLOCATION" g on (g.mess_master_id = b.mess_id and g.active_flag='Y')
    join schooldev."ALL_STUDENTS_DETAILS_VIEW" d on (b.student_id = d.student_id)
    left join schooldev."IIT_A_MESS_REBATE" e on (b.student_id = e.student_id and rebate_from>= b.change_from_date
    and rebate_from <=b.change_to_date and approval_status='Approved')
    left join schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST" f on (b.student_id = f.student_id and vacating_date>= b.change_from_date
    and vacating_date <=b.change_to_date and hostel_or_warden_approval_status='Approved')
where a.id=messPeriodId and b.mess_id=messid;

END;
$function$
;

DROP FUNCTION schooldev.search_students_rebate(varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar);

-- DROP FUNCTION schooldev.search_students_rebate(varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION schooldev.search_students_rebate(sstatus character varying, approval_from character varying, approval_to character varying, submitted_from character varying, submitted_to character varying, reb_from character varying, reb_to character varying, sname character varying, sid character varying, svname character varying, svemail character varying, loginid character varying, slno_from character varying, slno_to character varying, userrole character varying, userlogin character varying)
 RETURNS SETOF search_students_rebate_result
 LANGUAGE plpgsql
AS $function$
declare
r search_students_rebate_result%rowtype;
BEGIN
    if(userrole!='Caterer') then
        if(loginid='Dean') then
/*For mess.ohm login*/
            if (sstatus is not null or sstatus='') and (approval_from is null or approval_from='null') and (approval_to is null or approval_to='null') and (submitted_from is null or submitted_from='null') and (submitted_to is null or submitted_to='null') and (reb_from is null or reb_from='null') and (reb_to is null or reb_to='null') and (sname is null or sname='') and (sid is null or sid='') and (svemail is null or svemail='') and (svname is null or svname='') and (slno_from is null or slno_from='') and (slno_to is null or slno_to='')then
                raise notice 'IfLoop1: %',  'test1';
for r in
SELECT sd.student_id, a.id, a.leave_from, a.leave_to, rebate_from, rebate_to, a.no_of_days, a.rebate_reason,
       a.cancel_status, a.approval_status, a.created_at::date, wrk.approval_status, wrk1.guide_name as guide_auth_name,
       wrk1.approval_status as guide_approval_status, wrk1.modified_at::date as guide_approval_date, wrk2.guide_name as hod_auth_name,
       wrk2.approval_status as hod_approval_status, wrk2.modified_at::date as hod_approval_date, wrk2.guide_name as ccw_auth_name,
       wrk2.approval_status as ccw_approval_status, wrk2.modified_at::date as ccw_approval_date,wrk.id as workflow_id, wrk.modified_at as modified_at
FROM  schooldev."IIT_A_MESS_REBATE" a join schooldev."STUDENT_DETAILS_INFO" sd on (a.student_id=sd.student_id)
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" wrk on (wrk.request_id=a.id and wrk.student_id = sd.student_id and authority_type like '%'|| loginid )
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk1 on (wrk1.request_id=a.id and wrk1.student_id = sd.student_id and wrk1.approval_level=1 )
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk2 on (wrk2.request_id=a.id and wrk2.student_id = sd.student_id and wrk2.approval_level=2 )
                                      left join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk3 on (wrk3.request_id=a.id and wrk3.student_id = sd.student_id and wrk3.approval_level=3 )
                                      join schooldev."FACULTY_PERSONAL_DETAILS" on (wrk.guide_email like '%'||email_address||'%')
where a.active_flag = 'Y' and  wrk.active_flag = 'Y'  and a.school_id='1'
  and wrk.approval_status in ('Pending','Default')
  and a.approval_status in ('Pending','Validating')
/*and a.created_at >= (now()::date-90)*/
  and a.created_at >= (now()::date-30)
group by  sd.student_id, a.id, a.rebate_dining_from, a.rebate_dining_to, rebate_from, rebate_to, a.no_of_days, a.rebate_reason, a.cancel_status, a.approval_status, a.created_at::date, wrk.approval_status, wrk1.guide_name, wrk1.approval_status, wrk1.modified_at::date, wrk2.guide_name, wrk2.approval_status, wrk2.modified_at::date, wrk3.guide_name, wrk3.approval_status, wrk3.modified_at::date,wrk.id,wrk.modified_at
                    loop
                        return next r;
end loop;
else
                raise notice 'else1: %',  'test1';
for r in
SELECT sd.student_id, a.id, a.leave_from, a.leave_to, rebate_from, rebate_to, a.no_of_days, a.rebate_reason, a.cancel_status, a.approval_status, a.created_at::date, wrk.approval_status, wrk1.guide_name as guide_auth_name, wrk1.approval_status as guide_approval_status, wrk1.modified_at::date as guide_approval_date, wrk2.guide_name as hod_auth_name, wrk2.approval_status as hod_approval_status, wrk2.modified_at::date as hod_approval_date, wrk2.guide_name as ccw_auth_name, wrk2.approval_status as ccw_approval_status, wrk2.modified_at::date as ccw_approval_date ,wrk.id as workflow_id, wrk.modified_at as modified_at
FROM  schooldev."IIT_A_MESS_REBATE" a join schooldev."STUDENT_DETAILS_INFO" sd on (a.student_id=sd.student_id)
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" wrk on (wrk.request_id=a.id and wrk.student_id = sd.student_id and authority_type like '%'|| loginid )
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk1 on (wrk1.request_id=a.id and wrk1.student_id = sd.student_id and wrk1.approval_level=1 )
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk2 on (wrk2.request_id=a.id and wrk2.student_id = sd.student_id and wrk2.approval_level=2 )
                                      left join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk3 on (wrk3.request_id=a.id and wrk3.student_id = sd.student_id and wrk3.approval_level=3 )
                                      join schooldev."FACULTY_PERSONAL_DETAILS" on (wrk.guide_email like '%'||email_address||'%')   --left join (SELECT n_fm_facility_master_name,v_hri_roomno,sub_roomid, student_type, requestid,n_fm_facility_master_id, n_hri_roomid, roomallotmentid  FROM schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW" join schooldev."SCHOOL_FACILITIES" on (n_sf_facility_id=building_id) join schooldev."FACILITY_MASTER" on(n_fm_facility_master_id=n_sf_facility_master_id) join schooldev."HOSTEL_ROOM_INFO" on(roomid = n_hri_roomid)) as hostel on(case when student_type = 'Candidate' or student_type = 'StudentApp' then a.id=requestid::bigint end)
where a.active_flag = 'Y' and  wrk.active_flag = 'Y' and a.school_id='1'
  and (case when (sstatus is not null and sstatus<>'') then lower(a.approval_status) in (select lower(cat) from regexp_split_to_table(sstatus, ',')as cat) else a.approval_status = a.approval_status and a.approval_status not in ('Deleted','Cancelled')  end)
  and (case when (sstatus is not null and sstatus<>'') then lower(wrk.approval_status) in (select lower(cat) from regexp_split_to_table(sstatus, ',')as cat) or wrk.approval_status='Default' else wrk.approval_status = wrk.approval_status and wrk.approval_status not in ('Deleted','Cancelled')  end)
  and (coalesce(a.approval_date,'2000-01-01'::date) >= case when (approval_from::text <>'null' and approval_from::date is not null) then approval_from::date else coalesce(a.approval_date,'2000-01-01'::date) end )
  and (coalesce(a.approval_date,'2025-01-01'::date) <= case when (approval_to::text <>'null' and approval_to::date is not null) then approval_to::date else coalesce(a.approval_date,'2025-01-01'::date) end )
  and a.created_at::date >= case when (submitted_from::text <>'null' and submitted_from::date is not null) then submitted_from::date else a.created_at::date end
                      and a.created_at::date <= case when (submitted_to::text <>'null' and submitted_to::date is not null) then submitted_to::date else a.created_at::date end
                      and rebate_from >= case when (reb_from::text <>'null' and reb_from::date is not null) then reb_from::date else rebate_from end
                      and rebate_to <= case when (reb_to::text <>'null' and reb_to::date is not null) then reb_to::date else rebate_to end
                      and sd.first_name||' '||sd.last_name like case when (sname is not null and sname<>'') then '%'||sname||'%' else sd.first_name||' '||sd.last_name end
                      and sd.student_id like case when (sid is not null and sid<>'') then '%'||sid||'%' else sd.student_id end
                      and a.guide_name like case when (svname is not null and svname<>'') then '%'||svname||'%' else a.guide_name end
                      and a.guide_email like case when svemail is not null then '%'||svemail||'%' else a.guide_email end
                      and a.id >= case when (slno_from::text <>'' and slno_from::text is not null) then slno_from::bigint else a.id end
                      and a.id <= case when (slno_to::text <>'' and slno_to::text is not null) then slno_to::bigint else a.id end
                    group by  sd.student_id, a.id, a.rebate_dining_from, a.rebate_dining_to, rebate_from, rebate_to, a.no_of_days, a.rebate_reason, a.cancel_status, a.approval_status, a.created_at::date, wrk.approval_status, wrk1.guide_name, wrk1.approval_status, wrk1.modified_at::date, wrk2.guide_name, wrk2.approval_status, wrk2.modified_at::date, wrk3.guide_name, wrk3.approval_status, wrk3.modified_at::date,wrk.id,wrk.modified_at
                    loop
                        return next r;
end loop;
end if;
end if;
        if(loginid!='Dean') then
            if (sstatus is null or sstatus='') and (approval_from is null or approval_from='null') and (approval_to is null or approval_to='null') and (submitted_from is null or submitted_from='null') and (submitted_to is null or submitted_to='null') and (reb_from is null or reb_from='null') and (reb_to is null or reb_to='null') and (sname is null or sname='') and (sid is null or sid='') and (svemail is null or svemail='') and (svname is null or svname='') and (slno_from is null or slno_from='') and (slno_to is null or slno_to='')then
                raise notice 'IfLoop2: %',  'test2';
for r in
SELECT sd.student_id, a.id, a.leave_from, a.leave_to, rebate_from, rebate_to, a.no_of_days, a.rebate_reason, a.cancel_status, a.approval_status, a.created_at::date, wrk.approval_status, wrk1.guide_name as guide_auth_name, wrk1.approval_status as guide_approval_status, wrk1.modified_at::date as guide_approval_date, wrk2.guide_name as hod_auth_name, wrk2.approval_status as hod_approval_status, wrk2.modified_at::date as hod_approval_date, wrk2.guide_name as ccw_auth_name, wrk2.approval_status as ccw_approval_status, wrk2.modified_at::date as ccw_approval_date,wrk.id as workflow_id, wrk.modified_at as modified_at
FROM  schooldev."IIT_A_MESS_REBATE" a join schooldev."STUDENT_DETAILS_INFO" sd on (a.student_id=sd.student_id)
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" wrk on (wrk.request_id=a.id and wrk.student_id = sd.student_id and authority_type like '%'|| loginid )
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk1 on (wrk1.request_id=a.id and wrk1.student_id = sd.student_id and wrk1.approval_level=1 )
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk2 on (wrk2.request_id=a.id and wrk2.student_id = sd.student_id and wrk2.approval_level=2 )
                                      left join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk3 on (wrk3.request_id=a.id and wrk3.student_id = sd.student_id and wrk3.approval_level=3 )
                                      join schooldev."FACULTY_PERSONAL_DETAILS" on (wrk.guide_email like '%'||email_address||'%')
where a.active_flag = 'Y' and  wrk.active_flag = 'Y'  and a.school_id='1'
  and wrk.approval_status in ('Pending','Default')
  and a.approval_status in ('Pending','Validating')
/*and a.created_at >= (now()::date-90)*/
  and a.created_at >= (now()::date-30)
group by  sd.student_id, a.id, a.rebate_dining_from, a.rebate_dining_to, rebate_from, rebate_to, a.no_of_days, a.rebate_reason, a.cancel_status, a.approval_status, a.created_at::date, wrk.approval_status, wrk1.guide_name, wrk1.approval_status, wrk1.modified_at::date, wrk2.guide_name, wrk2.approval_status, wrk2.modified_at::date, wrk3.guide_name, wrk3.approval_status, wrk3.modified_at::date,wrk.id,wrk.modified_at
                    loop
                        return next r;
end loop;
else
                raise notice 'else2: %',  'test2';
for r in
SELECT sd.student_id, a.id, a.leave_from, a.leave_to, rebate_from, rebate_to, a.no_of_days, a.rebate_reason, a.cancel_status, a.approval_status, a.created_at::date, wrk.approval_status, wrk1.guide_name as guide_auth_name, wrk1.approval_status as guide_approval_status, wrk1.modified_at::date as guide_approval_date, wrk2.guide_name as hod_auth_name, wrk2.approval_status as hod_approval_status, wrk2.modified_at::date as hod_approval_date, wrk2.guide_name as ccw_auth_name, wrk2.approval_status as ccw_approval_status,wrk.modified_at::date as ccw_approval_date, wrk.id as workflow_id, wrk.modified_at as modified_at
FROM  schooldev."IIT_A_MESS_REBATE" a join schooldev."STUDENT_DETAILS_INFO" sd on (a.student_id=sd.student_id)
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" wrk on (wrk.request_id=a.id and wrk.student_id = sd.student_id and authority_type like '%'|| loginid )
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk1 on (wrk1.request_id=a.id and wrk1.student_id = sd.student_id and wrk1.approval_level=1 )
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk2 on (wrk2.request_id=a.id and wrk2.student_id = sd.student_id and wrk2.approval_level=2 )
                                      left join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk3 on (wrk3.request_id=a.id and wrk3.student_id = sd.student_id and wrk3.approval_level=3 )
                                      join schooldev."FACULTY_PERSONAL_DETAILS" on (wrk.guide_email like '%'||email_address||'%')   --left join (SELECT n_fm_facility_master_name,v_hri_roomno,sub_roomid, student_type, requestid,n_fm_facility_master_id, n_hri_roomid, roomallotmentid  FROM schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW" join schooldev."SCHOOL_FACILITIES" on (n_sf_facility_id=building_id) join schooldev."FACILITY_MASTER" on(n_fm_facility_master_id=n_sf_facility_master_id) join schooldev."HOSTEL_ROOM_INFO" on(roomid = n_hri_roomid)) as hostel on(case when student_type = 'Candidate' or student_type = 'StudentApp' then a.id=requestid::bigint end)
where a.active_flag = 'Y' and  wrk.active_flag = 'Y' and a.school_id='1'
  and (case when (sstatus is not null and sstatus<>'') then lower(a.approval_status) in (select lower(cat) from regexp_split_to_table(sstatus, ',')as cat) else a.approval_status = a.approval_status and a.approval_status not in ('Deleted','Cancelled')  end)
  and (case when (sstatus is not null and sstatus<>'') then lower(wrk.approval_status) in (select lower(cat) from regexp_split_to_table(sstatus, ',')as cat) or wrk.approval_status='Default' else wrk.approval_status = wrk.approval_status and wrk.approval_status not in ('Deleted','Cancelled')  end)
  and (coalesce(a.approval_date,'2000-01-01'::date) >= case when (approval_from::text <>'null' and approval_from::date is not null) then approval_from::date else coalesce(a.approval_date,'2000-01-01'::date) end )
  and (coalesce(a.approval_date,'2025-01-01'::date) <= case when (approval_to::text <>'null' and approval_to::date is not null) then approval_to::date else coalesce(a.approval_date,'2025-01-01'::date) end )
  and a.created_at::date >= case when (submitted_from::text <>'null' and submitted_from::date is not null) then submitted_from::date else a.created_at::date end
                      and a.created_at::date <= case when (submitted_to::text <>'null' and submitted_to::date is not null) then submitted_to::date else a.created_at::date end
                      and rebate_from >= case when (reb_from::text <>'null' and reb_from::date is not null) then reb_from::date else rebate_from end
                      and rebate_to <= case when (reb_to::text <>'null' and reb_to::date is not null) then reb_to::date else rebate_to end
                      and sd.first_name||' '||sd.last_name like case when (sname is not null and sname<>'') then '%'||sname||'%' else sd.first_name||' '||sd.last_name end
                      and sd.student_id like case when (sid is not null and sid<>'') then '%'||sid||'%' else sd.student_id end
                      and a.guide_name like case when (svname is not null and svname<>'') then '%'||svname||'%' else a.guide_name end
                      and a.guide_email like case when svemail is not null then '%'||svemail||'%' else a.guide_email end
                      and a.id >= case when (slno_from::text <>'' and slno_from::text is not null) then slno_from::bigint else a.id end
                      and a.id <= case when (slno_to::text <>'' and slno_to::text is not null) then slno_to::bigint else a.id end
                    group by  sd.student_id, a.id, a.rebate_dining_from, a.rebate_dining_to, rebate_from, rebate_to, a.no_of_days, a.rebate_reason, a.cancel_status, a.approval_status, a.created_at::date, wrk.approval_status, wrk1.guide_name, wrk1.approval_status, wrk1.modified_at::date, wrk2.guide_name, wrk2.approval_status, wrk2.modified_at::date, wrk3.guide_name, wrk3.approval_status, wrk3.modified_at::date,wrk.id,wrk2.modified_at
                    loop
                        return next r;
end loop;
end if;
end if;
else
        raise notice 'caterer else: %',  'ca';
        if (sstatus is null or sstatus='') and (approval_from is null or approval_from='null') and (approval_to is null or approval_to='null') and (submitted_from is null or submitted_from='null') and (submitted_to is null or submitted_to='null') and (reb_from is null or reb_from='null') and (reb_to is null or reb_to='null') and (sname is null or sname='') and (sid is null or sid='') and (svemail is null or svemail='') and (svname is null or svname='') and (slno_from is null or slno_from='') and (slno_to is null or slno_to='')then
            raise notice 'catererIfLoop: %',  'test1';
for r in
SELECT sd.student_id, a.id, a.leave_from, a.leave_to, rebate_from, rebate_to, a.no_of_days, a.rebate_reason, a.cancel_status, a.approval_status, a.created_at::date, wrk.approval_status, wrk1.guide_name as guide_auth_name, wrk1.approval_status as guide_approval_status, wrk1.modified_at::date as guide_approval_date, wrk2.guide_name as hod_auth_name, wrk2.approval_status as hod_approval_status, wrk2.modified_at::date as hod_approval_date, wrk2.guide_name as ccw_auth_name, wrk2.approval_status as ccw_approval_status, wrk2.modified_at::date as ccw_approval_date,wrk.id as workflow_id, wrk.modified_at as modified_at
FROM  schooldev."IIT_A_MESS_REBATE" a join schooldev."STUDENT_DETAILS_INFO" sd on (a.student_id=sd.student_id)
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" wrk on (wrk.request_id=a.id and wrk.student_id = sd.student_id and authority_type like '%'|| loginid )
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk1 on (wrk1.request_id=a.id and wrk1.student_id = sd.student_id and wrk1.approval_level=1 )
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk2 on (wrk2.request_id=a.id and wrk2.student_id = sd.student_id and wrk2.approval_level=2 )
                                      left join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk3 on (wrk3.request_id=a.id and wrk3.student_id = sd.student_id and wrk3.approval_level=3 )
                                      join schooldev."FACULTY_PERSONAL_DETAILS" on (wrk.guide_email like '%'||email_address||'%')
                                      join schooldev."STUDENT_MESS_DETAILS" smd on (a.student_id=smd.student_id and smd.active_flag='Y'
    and (a.rebate_from::date >= smd.from_date::date and a.rebate_to::date <= smd.to_date::date))
                                      join schooldev."MESS_ALLOCATION" ma on (smd.mess_id=ma.mess_master_id and ma.active_status='Y')
                                      join schooldev."CATERER_LEDGER_MAPPING" cl on (ma.vendor_code=cl.acc_head and cl.active_flag='Y')
where a.active_flag = 'Y' and  wrk.active_flag = 'Y'  and a.school_id='1'
  and wrk.approval_status in ('Pending','Default','Approved')
  and a.approval_status in ('Pending','Validating','Approved')
  and a.created_at >= (now()::date-30)
--and a.created_at::date >= '2023-08-01'::date
  AND 'now()'::date between rebate_from and rebate_to
                  and cl.caterer_name=userlogin
group by  sd.student_id, a.id, a.rebate_dining_from, a.rebate_dining_to, rebate_from, rebate_to, a.no_of_days, a.rebate_reason, a.cancel_status, a.approval_status, a.created_at::date, wrk.approval_status, wrk1.guide_name, wrk1.approval_status, wrk1.modified_at::date, wrk2.guide_name, wrk2.approval_status, wrk2.modified_at::date, wrk3.guide_name, wrk3.approval_status, wrk3.modified_at::date,wrk.id,wrk.modified_at
    loop
    return next r;
end loop;
else
            raise notice 'catererelse: %',  'test1';
for r in
SELECT sd.student_id, a.id, a.leave_from, a.leave_to, rebate_from, rebate_to, a.no_of_days, a.rebate_reason, a.cancel_status, a.approval_status, a.created_at::date, wrk.approval_status, wrk1.guide_name as guide_auth_name, wrk1.approval_status as guide_approval_status, wrk1.modified_at::date as guide_approval_date, wrk2.guide_name as hod_auth_name, wrk2.approval_status as hod_approval_status, wrk2.modified_at::date as hod_approval_date, wrk2.guide_name as ccw_auth_name, wrk2.approval_status as ccw_approval_status, wrk2.modified_at::date as ccw_approval_date ,wrk.id as workflow_id, wrk.modified_at as modified_at
FROM  schooldev."IIT_A_MESS_REBATE" a join schooldev."STUDENT_DETAILS_INFO" sd on (a.student_id=sd.student_id)
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" wrk on (wrk.request_id=a.id and wrk.student_id = sd.student_id and authority_type like '%'|| loginid )
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk1 on (wrk1.request_id=a.id and wrk1.student_id = sd.student_id and wrk1.approval_level=1 )
                                      join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk2 on (wrk2.request_id=a.id and wrk2.student_id = sd.student_id and wrk2.approval_level=2 )
                                      left join schooldev."IIT_A_MESS_REBATE_WORKFLOW" as wrk3 on (wrk3.request_id=a.id and wrk3.student_id = sd.student_id and wrk3.approval_level=3 )
                                      join schooldev."FACULTY_PERSONAL_DETAILS" on (wrk.guide_email like '%'||email_address||'%')   --left join (SELECT n_fm_facility_master_name,v_hri_roomno,sub_roomid, student_type, requestid,n_fm_facility_master_id, n_hri_roomid, roomallotmentid  FROM schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW" join schooldev."SCHOOL_FACILITIES" on (n_sf_facility_id=building_id) join schooldev."FACILITY_MASTER" on(n_fm_facility_master_id=n_sf_facility_master_id) join schooldev."HOSTEL_ROOM_INFO" on(roomid = n_hri_roomid)) as hostel on(case when student_type = 'Candidate' or student_type = 'StudentApp' then a.id=requestid::bigint end)
                                      join schooldev."STUDENT_MESS_DETAILS" smd on (a.student_id=smd.student_id and smd.active_flag='Y'
    and (a.rebate_from::date >= smd.from_date::date and a.rebate_to::date <= smd.to_date::date))
                                      join schooldev."MESS_ALLOCATION" ma on (smd.mess_id=ma.mess_master_id and ma.active_status='Y')
                                      join schooldev."CATERER_LEDGER_MAPPING" cl on (ma.vendor_code=cl.acc_head and cl.active_flag='Y')
where a.active_flag = 'Y' and  wrk.active_flag = 'Y' and a.school_id='1' and cl.caterer_name=userlogin
  and (case when (sstatus is not null and sstatus<>'') then lower(a.approval_status) in (select lower(cat) from regexp_split_to_table(sstatus, ',')as cat) else a.approval_status = a.approval_status and a.approval_status not in ('Deleted','Cancelled')  end)
  and (case when (sstatus is not null and sstatus<>'') then lower(wrk.approval_status) in (select lower(cat) from regexp_split_to_table(sstatus, ',')as cat) or wrk.approval_status='Default' else wrk.approval_status = wrk.approval_status and wrk.approval_status not in ('Deleted','Cancelled')  end)
  and (coalesce(a.approval_date,'2000-01-01'::date) >= case when (approval_from::text <>'null' and approval_from::date is not null) then approval_from::date else coalesce(a.approval_date,'2000-01-01'::date) end )
  and (coalesce(a.approval_date,'2025-01-01'::date) <= case when (approval_to::text <>'null' and approval_to::date is not null) then approval_to::date else coalesce(a.approval_date,'2025-01-01'::date) end )
  and a.created_at::date >= case when (submitted_from::text <>'null' and submitted_from::date is not null) then submitted_from::date else a.created_at::date end
                  and a.created_at::date <= case when (submitted_to::text <>'null' and submitted_to::date is not null) then submitted_to::date else a.created_at::date end
                  and rebate_from >= case when (reb_from::text <>'null' and reb_from::date is not null) then reb_from::date else rebate_from end
                  and rebate_to <= case when (reb_to::text <>'null' and reb_to::date is not null) then reb_to::date else rebate_to end
                  and sd.first_name||' '||sd.last_name like case when (sname is not null and sname<>'') then '%'||sname||'%' else sd.first_name||' '||sd.last_name end
                  and sd.student_id like case when (sid is not null and sid<>'') then '%'||sid||'%' else sd.student_id end
                  and a.guide_name like case when (svname is not null and svname<>'') then '%'||svname||'%' else a.guide_name end
                  and a.guide_email like case when svemail is not null then '%'||svemail||'%' else a.guide_email end
                  and a.id >= case when (slno_from::text <>'' and slno_from::text is not null) then slno_from::bigint else a.id end
                  and a.id <= case when (slno_to::text <>'' and slno_to::text is not null) then slno_to::bigint else a.id end
                group by  sd.student_id, a.id, a.rebate_dining_from, a.rebate_dining_to, rebate_from, rebate_to, a.no_of_days, a.rebate_reason, a.cancel_status, a.approval_status, a.created_at::date, wrk.approval_status, wrk1.guide_name, wrk1.approval_status, wrk1.modified_at::date, wrk2.guide_name, wrk2.approval_status, wrk2.modified_at::date, wrk3.guide_name, wrk3.approval_status, wrk3.modified_at::date,wrk.id,wrk.modified_at
                loop
                    return next r;
end loop;
end if;
end if;
END;
/**
DROP FUNCTION schooldev.search_students_rebate(character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying, character varying);
drop type search_students_rebate_result cascade;
create type search_students_rebate_result as (student_id character varying, id bigint, leave_from date, leave_to date, rebate_from date, rebate_to date, no_of_days integer, rebate_reason character varying, cancel_status character varying, studStatus character varying, created_at timestamp without time zone, workflowStatus character varying, guide_auth_name character varying, guide_approval_status character varying,  guide_approval_date date, hod_auth_name character varying, hod_approval_status character varying, hod_approval_date date, ccw_auth_name character varying, ccw_approval_status character varying, ccw_approval_date date,workflow_id int,modified_at timestamp);
sstatus character varying, approval_from character varying, approval_to character varying, submitted_from character varying, submitted_to character varying, reb_from character varying, reb_to character varying, sname character varying, sid character varying, svname character varying, svemail character varying, loginid character varying,
select * from schooldev.search_students_rebate(NULL, 'null', 'null', 'null', 'null', 'null', 'null', NULL, NULL, NULL, NULL, 'CCW Dean', NULL, NULL,'Caterer','sakthi.ms')
*/
$function$
;


CREATE OR REPLACE FUNCTION schooldev.self_mess_allotted_list(fromdate character varying, todate character varying, messperiodid bigint)
    RETURNS SETOF selfmesslist
    LANGUAGE plpgsql
AS $function$
declare
res selfmesslist%rowtype;
BEGIN
    if (fromdate is null or fromdate='') and (todate is null or todate='') and (messperiodid=0) then
        raise notice 'ifloop: %',  '1';
for res in
select smd.student_id,smd.from_date,smd.to_date,(first_name ||''|| last_name) as student_name,mess_name,mess_head,change_fromdate,change_todate,
       self_allotment_qr_status,smd.created_at
from schooldev."MESS_MASTER_CONTROLLER" mmc
         join schooldev."STUDENT_MESS_DETAILS" smd on (mmc.dining_from_date=smd.from_date and mmc.dining_to_date = smd.to_date)
         join schooldev."STUDENT_DETAILS_INFO" sdi on (sdi.student_id=smd.student_id)
         join schooldev."MESS_MASTER" mm on (mm.mess_master_id=smd.mess_id and mm.active_flag='Y')  WHERE
    smd.active_flag='Y' and self_allotment_qr_status is not null
                                                                                                      --and mmc_v_current_active_flag='Y'
    loop
                return next res;
end loop;
else
        raise notice 'loginIDS: %',  2;
for res in
select smd.student_id,smd.from_date,smd.to_date,(first_name ||''|| last_name) as student_name,mess_name,mess_head,change_fromdate,change_todate,
       self_allotment_qr_status,smd.created_at
from schooldev."MESS_MASTER_CONTROLLER" mmc
         join schooldev."STUDENT_MESS_DETAILS" smd on (mmc.dining_from_date=smd.from_date and mmc.dining_to_date = smd.to_date)
         join schooldev."STUDENT_DETAILS_INFO" sdi on (sdi.student_id=smd.student_id)
         join schooldev."MESS_MASTER" mm on (mm.mess_master_id=smd.mess_id and mm.active_flag='Y')  WHERE
    smd.active_flag='Y' and self_allotment_qr_status is not null
                                                                                                      --and mmc_v_current_active_flag='Y'
                                                                                                      and (case when (fromdate::text<>'' and fromdate::text<>'NULL' and fromdate::date is not null) then smd.created_at::date>=fromdate::date else 1=1 end)
                                                                                                      and (case when (todate::text<>'' and todate::text<>'null' and todate::date is not null ) then smd.created_at::date<=todate::date  else 1=1 end)
                                                                                                      and(case when messperiodid<>'0' then mmc.id=(select id from schooldev."MESS_MASTER_CONTROLLER" where id::integer=messperiodid::integer)::integer else 1=1 end)


            loop
                return next res;
end loop;
end if;
END;
/*
drop type selfmesslist;

create type selfmesslist as (v_smd_student_id character varying,d_smd_from_date character varying,d_smd_to_date character varying,student_name character varying,mess_name character varying,mess_head character varying,d_smd_change_fromdate character varying,
d_smd_change_todate character varying, self_allotment_qr_status character varying, t_smd_created_at character varying);

select *  from schooldev.self_mess_allotted_list('','','0')
select *  from schooldev.self_mess_allotted_list('','','38')
select *  from schooldev.self_mess_allotted_list('09-Oct-2019','16-Oct-2019','38')
select *  from schooldev.self_mess_allotted_list('09-Oct-2019','11-Oct-2019','0')
*/

$function$
;

CREATE OR REPLACE FUNCTION schooldev.hostel_login_biometric_list(hostelid integer, fromdate character varying, todate character varying, gendertype character varying)
    RETURNS SETOF log_list
    LANGUAGE plpgsql
AS $function$
declare
res log_list%rowtype;
BEGIN
    if 	(hostelid<>0) and (fromdate is null or fromdate='null' or fromdate='') and (todate is null or todate='null' or todate='') and (genderType is null or genderType='') then
        raise notice 'if: %', hostelid;
for res in
select id,studentid,c.student_name,terminal_location,rf_id,
       swipe_date,swipe_time,to_char(swipe_date, 'day') as swipe_day,(swipe_date ||' '|| swipe_time) as swipe_date_time
from schooldev."HOSTEL_BIOMETRIC_TERMINAL" a
         join schooldev."HOSTEL_BIOMETRIC_LOGS" b on (a.terminal_ip=b.terminal_ip)
         left join schooldev."ALL_STUDENTS_DETAILS_VIEW" c on (c.student_id = upper(btrim(b.studentid)))
where a.active_flag='Y'

    loop
                return next res;
end loop;
else
        raise notice 'else: %', genderType;
for res in
select id,studentid,c.student_name,terminal_location,rf_id,
       swipe_date,swipe_time,to_char(swipe_date, 'day') as swipe_day,(swipe_date ||' '|| swipe_time) as swipe_date_time
from schooldev."HOSTEL_BIOMETRIC_TERMINAL" a
         join schooldev."HOSTEL_BIOMETRIC_LOGS" b on (a.terminal_ip=b.terminal_ip)
         left join schooldev."ALL_STUDENTS_DETAILS_VIEW" c on (student_id =upper(btrim(b.studentid)))
where a.active_flag='Y'
  and (case when hostelid<>0 and hostelid<>-1 then terminal_id=(hostelid) else 1=1 end)
  and( case when (fromdate::text<>'null' and fromdate::date is not null) then  swipe_date::date >=fromdate::date
                        else 1=1 end)
  and (case when (todate::text<>'null' and todate::date is not null) then swipe_date::date <= todate::date
                        else 1=1 end)
  and (case when (genderType<>'null' and genderType is not null AND genderType<>'') then (c.gender) = (genderType) else 1=1 end)
    loop
                return next res;
end loop;
end if;
END;
/*
drop type log_list cascade;
create type log_list as (id integer,studentid character varying,student_name character varying,terminal_location character varying,rf_id integer,swipe_date character varying,swipe_time character varying,swipe_day character varying);
select *  from schooldev.hostel_login_biometric_list('0','null','null','F');
select * from schooldev.hostel_login_biometric_list('0','null','null',NULL)
select * from schooldev.hostel_login_biometric_list('2','null','null',NULL)
select * from schooldev.hostel_login_biometric_list('14','2019-01-01','2019-10-07','F')
*/
$function$
;

-- DROP FUNCTION schooldev.sick_food_request_list(varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION schooldev.sick_food_request_list(userrole character varying, userlogin character varying, request_from character varying, request_to character varying, studentid character varying, student_name character varying, catererstatus character varying, student_delivery character varying, messsession character varying)
 RETURNS SETOF sick_food_req_list
 LANGUAGE plpgsql
AS $function$

declare res sick_food_req_list%rowtype;
        messtype character varying;
        sickMessId integer;
        available_mess_session character varying;
        mess_end_time character varying;
BEGIN
    if(lower(userrole) = 'caterer') then
select mm.mess_master_id into sickMessId
from schooldev."CATERER_LEDGER_MAPPING" cl
         join schooldev."MESS_ALLOCATION" ma on (ma.vendor_code=cl.acchead and ma.active_flag='Y')
         join schooldev."MESS_MASTER" mm on (mm.mess_master_id=ma.mess_master_id and mm.active_flag='Y')
where lower(cl.caterer_name)=lower(userlogin) and cl.active_flag='Y' and sick_food_avail=true;
raise notice 'sickMessId: %',  sickMessId;

       -- if(messtype is not null and (messtype='south' or messtype='north')) then
		if(sickMessId is not null) then
			/** check mess threshold time **/
select GROUP_CONCAT(session_name) into available_mess_session,mess_end_time
from schooldev."MESS_SESSIONS" where mess_id=sickMessId and
    (to_timestamp(end_time, 'HH24:MI')::time) >=
    to_timestamp(current_time::text, 'HH24:MI')::time;

raise notice 'after available_mess_session: %',  available_mess_session;
            if (request_from is null or request_from='null' or request_from='') and
               (request_to is null or request_to='null' or request_to='') and (studentid is null or studentid=null or studentid='')
                and (catererstatus is null or catererstatus=null or catererstatus='') and (student_name is null or student_name=NULL or student_name='')
                and (student_delivery is null or student_delivery=null or student_delivery='')
                and (messSession is null or messSession=null or messSession='') then
                raise notice 'ifloop: %',  '1';
for res in
select a.id as request_id,b.id as delivey_id,request_date,c.student_id,(c.first_name||' '||c.last_name) as student_name,
       mobile_num,delivery_address,medical_reason,mess_session,a.mess_type,caterer_status,
       stud_delivery_status,stud_feedback,medical_proof_doc,d.mess_name,
       CASE WHEN mess_session = 'BF' THEN 1
            WHEN mess_session = 'LC' THEN 2 ELSE 3 END as session_order,
       CASE WHEN request_date < CURRENT_DATE THEN FALSE
            WHEN request_date = CURRENT_DATE THEN
                (CASE WHEN available_mess_session like CONCAT('%',mess_session, '%') THEN TRUE ELSE FALSE END)
            ELSE true END as messtime_threshold,stud_feedback,feed_back_rating
from schooldev."SICK_FOOD_REQUEST" a
         left join schooldev."SICK_FOOD_DELIVERY_STATUS" b on (a.id=b.request_id and b.active_flag='Y')
         left join schooldev."STUDENT_DETAILS_INFO" c on (c.student_id=a.student_id)
         left join schooldev."MESS_MASTER" d ON (a.mess_id=d.mess_master_id)
where a.active_flag='Y'  --and a.mess_type=messtype
  and a.request_date::date=now()::date
order by session_order , b.created_at
    loop
    return next res;
end loop;
else
                raise notice 'else: %', '1';
for res in
select a.id as request_id,b.id as delivery_id,request_date,c.student_id,(c.first_name||' '||c.last_name) as student_name,
       mobile_num,delivery_address,medical_reason,mess_session,a.mess_type,caterer_status,
       stud_delivery_status,stud_feedback,medical_proof_doc,d.mess_name,
       CASE WHEN mess_session = 'BF' THEN 1
            WHEN mess_session = 'LC' THEN 2 ELSE 3 END as session_order,
       CASE WHEN request_date < CURRENT_DATE THEN FALSE
            WHEN request_date = CURRENT_DATE THEN
                (CASE WHEN available_mess_session like CONCAT('%',mess_session, '%') THEN TRUE ELSE FALSE END)
            ELSE true END as messtime_threshold,stud_feedback,feed_back_rating
from schooldev."SICK_FOOD_REQUEST" a
         join schooldev."SICK_FOOD_DELIVERY_STATUS" b on (a.id=b.request_id and b.active_flag='Y')
         join schooldev."STUDENT_DETAILS_INFO" c on (c.student_id=a.student_id)
         left join schooldev."MESS_MASTER" d ON (a.mess_id=d.mess_master_id)
where a.active_flag='Y'  --and a.mess_type=messtype
  and request_date >= case when (request_from::text <>'null' and request_from::text is not null and request_from::text <>'') then request_from::date else request_date end
                      and request_date <= case when (request_to::text <>'null' and request_to::text is not null and request_to::text <>'') then request_to::date else request_date end
                      and upper(a.student_id) like case when (studentid is not null and studentid<>'') then '%'||upper(studentid)||'%' else upper(a.student_id) end
                      and upper (c.first_name||' '||c.last_name) like case when (student_name is not null and student_name<>'') then '%'||upper(student_name)||'%' else upper(c.first_name||' '||c.last_name) end
                      and lower(caterer_status)= case when (catererstatus is not null and catererstatus<>'') then lower(catererstatus) else lower(caterer_status) end
                      and case when (student_delivery is not null and student_delivery<>'') then stud_delivery_status= student_delivery else 1=1 end
                      and mess_session = case when (messSession is not null and messSession<>'') then messSession else mess_session end
                    order by session_order , b.created_at
                    loop
                        return next res;
end loop;
end if;
end if;
end if;

    if(lower(userrole) = 'softwareadmin' or lower(userrole) = 'accountant') then
        raise notice 'SOFTWAREADMIN : %',  'true';
        if (request_from is null or request_from='null' or request_from='') and
           (request_to is null or request_to='null' or request_to='') and (studentid is null or studentid=null or studentid='')
            and (catererstatus is null or catererstatus=null or catererstatus='') and (student_name is null or student_name=null or student_name='')
            and (student_delivery is null or student_delivery=null or student_delivery='')
            and (messSession is null or messSession=null or messSession='') then
            raise notice ' SOFTWAREADMIN ifloop: %',  '1';
for res in
select a.id as request_id,b.id as delivey_id,request_date,c.student_id,(c.first_name||' '||c.last_name) as student_name,
       mobile_num,delivery_address,medical_reason,mess_session,a.mess_type,caterer_status,
       stud_delivery_status,stud_feedback,medical_proof_doc,d.mess_name,
       CASE WHEN mess_session = 'BF' THEN 1
            WHEN mess_session = 'LC' THEN 2 ELSE 3 END as session_order,
       CASE WHEN request_date < CURRENT_DATE THEN FALSE
            WHEN request_date = CURRENT_DATE THEN
                (CASE WHEN available_mess_session like CONCAT('%',mess_session, '%') THEN TRUE ELSE FALSE END)
            ELSE true END as messtime_threshold,stud_feedback,feed_back_rating
from schooldev."SICK_FOOD_REQUEST" a
         join schooldev."SICK_FOOD_DELIVERY_STATUS" b on (a.id=b.request_id and b.active_flag='Y')
         join schooldev."STUDENT_DETAILS_INFO" c on (c.student_id=a.student_id)
         left join schooldev."MESS_MASTER" d ON (a.mess_id=d.mess_master_id)
where a.active_flag='Y' and a.created_at >= (now()::date-30)
order by a.request_date desc,a.id desc,
         CASE WHEN mess_session = 'BF' THEN 1
              WHEN mess_session = 'LC' THEN 2 ELSE 3 END
    loop
                    return next res;
end loop;
else
            raise notice 'SOFTWAREADMIN else: %', '1';
for res in
select a.id as request_id,b.id as delivery_id,request_date,c.student_id,(c.first_name||' '||c.last_name) as student_name,
       mobile_num,delivery_address,medical_reason,mess_session,a.mess_type,caterer_status,
       stud_delivery_status,stud_feedback,medical_proof_doc,d.mess_name,
       CASE WHEN mess_session = 'BF' THEN 1
            WHEN mess_session = 'LC' THEN 2 ELSE 3 END as session_order,
       CASE WHEN request_date < CURRENT_DATE THEN FALSE
            WHEN request_date = CURRENT_DATE THEN
                (CASE WHEN available_mess_session like CONCAT('%',mess_session, '%') THEN TRUE ELSE FALSE END)
            ELSE true END as messtime_threshold,stud_feedback,feed_back_rating
from schooldev."SICK_FOOD_REQUEST" a
         join schooldev."SICK_FOOD_DELIVERY_STATUS" b on (a.id=b.request_id and b.active_flag='Y')
         join schooldev."STUDENT_DETAILS_INFO" c on (c.student_id=a.student_id)
         left join schooldev."MESS_MASTER" d ON (a.mess_id=d.mess_master_id)
where a.active_flag='Y'
  and request_date >= case when (request_from::text <>'null' and request_from::text is not null and request_from::text <>'') then request_from::date else request_date end
                  and request_date <= case when (request_to::text <>'null' and request_to::text is not null and request_to::text <>'') then request_to::date else request_date end
                  and upper(a.student_id) like case when (studentid is not null and studentid<>'') then '%'||upper(studentid)||'%' else upper(a.student_id) end
                  and upper (c.first_name||' '||c.last_name) like case when (student_name is not null and student_name<>'') then '%'||upper(student_name)||'%' else upper(c.first_name||' '||c.last_name) end
                  and lower(caterer_status)= case when (catererstatus is not null and catererstatus<>'') then lower(catererstatus) else lower(caterer_status) end
                  and case when (student_delivery is not null and student_delivery<>'') then stud_delivery_status= student_delivery else 1=1 end
                  and mess_session = case when (messSession is not null and messSession<>'') then messSession else mess_session end
                order by a.request_date desc,a.id desc,
                         CASE WHEN mess_session = 'BF' THEN 1
                              WHEN mess_session = 'LC' THEN 2 ELSE 3 END
                loop
                    return next res;
end loop;
end if;
end if;
END;

/* drop type sick_food_req_list cascade;
 create type sick_food_req_list as (request_id integer,delivery_id integer,request_date character varying,student_id character varying,
 student_name character varying,mobile_num character varying,delivery_address character varying,medical_reason character varying,
 mess_session character varying,mess_type character varying,caterer_status character varying,
 stud_delivery_status character varying,
 stud_feedback character varying,medical_proof_doc character varying,
 mess_name character varying,session_order integer,messtime_threshold boolean,feedback character varying,feedback_rating character varying);

select *  from  schooldev.sick_food_request_list('SENIOR COOK','neelkesh.ms',null,null,null,null,null,null,'');
select * from schooldev.sick_food_request_list('SENIOR COOK','sgr.ms','null','null','',NULL,'','','')
select * from schooldev.sick_food_request_list('SENIOR COOK','sgr.ms','','','','','','','DR')
select * from schooldev.sick_food_request_list('SOFTWAREADMIN','triesten','null','null','','','','','DR')
select * from schooldev.sick_food_request_list('Caterer','crcl.mess','2025-01-01','','','','','','')
*/
$function$
;


CREATE OR REPLACE FUNCTION schooldev.wellness_all_visit_list(studentid character varying, visitfromdate character varying, visittodate character varying, followupfrom character varying, followupto character varying, referraltype character varying, concerntype character varying, username character varying)
    RETURNS SETOF wellnessvisitlist
    LANGUAGE plpgsql
AS $function$
declare res wellnessvisitlist%rowtype;
BEGIN
    if 	(studentid is null or studentid='') and (visitfromdate is null or visitfromdate='')
        and (visittodate is null or visittodate='') and (followupfrom is null or followupfrom='')  and (followupto is null or followupto='')
        and (referraltype is null or referraltype='' or referraltype='0') and (concerntype is null or concerntype='' or concerntype='0')
    then
        raise notice 'ifloop: %',  '1';
for res in
SELECT a.wellness_id,a.student_id,case when (a.category='Others') then other_stud_name else b.student_name end,
       hostel_name,room_number,student_mobile,student_iitm_smail,
       referral_date,referral_type,concern_type,coordinated_name,a.created_at ::date,
    no_of_visit,visit_date,interaction_mode,session_start_time,duration,concerns_discussed,future_action_plan,follow_up_date,visit_status
FROM schooldev."STUDENT_WELLNESS_CATEGORICAL_DATA" a
         left join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (a.student_id=b.student_id)
         left join schooldev."STUDENT_WELLNESS_FOLLOWUP_DATA" c on (c.wellness_id=a.wellness_id)
where a.active_flag='Y' and lower(a.created_by) = lower(username) order by a.student_id asc,no_of_visit asc

    loop
                return next res;
end loop;
else
        raise notice 'elseloop: %',  '2';
for res in
SELECT a.wellness_id,a.student_id,case when (a.category='Others') then other_stud_name else b.student_name end,
       hostel_name,room_number,student_mobile,student_iitm_smail,referral_date,referral_type,concern_type,coordinated_name,a.created_at ::date,
    no_of_visit,visit_date,interaction_mode,session_start_time,duration,concerns_discussed,future_action_plan,follow_up_date,visit_status
FROM schooldev."STUDENT_WELLNESS_CATEGORICAL_DATA" a
         left join schooldev."ALL_STUDENTS_DETAILS_VIEW" b on (a.student_id=b.student_id)
         left join schooldev."STUDENT_WELLNESS_FOLLOWUP_DATA" c on (c.wellness_id=a.wellness_id)
where a.active_flag='Y' and lower(a.created_by) = lower(username)

  and (case when (studentid is not null and studentid<>'') then upper(a.student_id) like upper('%'||studentid||'%') else 1=1 end)
  and (case when (visitfromdate::text<>'null' and visitfromdate::date is not null and visitfromdate::text<>'') then  visit_date::date>=visitfromdate::date else 1=1 end)
  and (case when (visittodate::text<>'null' and visittodate::date is not null and visittodate::text<>'') then visit_date::date <= visittodate::date else 1=1 end)
  and (case when (followupfrom::text<>'null' and followupfrom::date is not null and followupfrom::text<>'') then  follow_up_date::date >=followupfrom::date else 1=1 end)
  and (case when (followupto::text<>'null' and followupto::date is not null and followupto::text<>'') then  follow_up_date::date <= followupto::date else 1=1 end)
  and (case when (referraltype is not null and referraltype<>'' and referraltype<>'0') then lower(referral_type) = lower(referraltype) else 1=1 end)
  and (case when (concerntype is not null and concerntype<>'0'  and concerntype<>'') then lower(concern_type) = lower(concerntype) else 1=1 end)
order by a.student_id asc,no_of_visit asc

    loop return next res;
end loop;
end if;
END;

    /* drop type wellnessvisitlist cascade;
       create type wellnessvisitlist as (wellness_id integer, student_id character varying,student_name character varying,
       hostel_name character varying,room_number character varying,student_mobile character varying,student_iitm_smail character varying,
       referral_date character varying,referral_type character varying, concern_type character varying, coordinated_name character  varying,
       created_at character  varying,num_of_visit integer,visit_date date,interaction_mode character varying,session_start_time time,duration integer,
       concerns_discussed character varying,future_action_plan character varying,follow_up_date date,visit_status character varying);

      select *  from  schooldev.wellness_all_visit_list('','','null',null,'null','','','arwo.wellness')

    */


$function$
;

CREATE OR REPLACE FUNCTION schooldev.update_all_student_current_balance()
    RETURNS VOID AS $$
DECLARE
total_balance NUMERIC;
    total_balance_cc NUMERIC;
    existing varchar;
    m_student_id varchar;
    r record;
BEGIN
    m_student_id = null;
for r in
select * from schooldev."STUDENT_DETAILS_INFO" where active_flag = 'Y'
    loop
            m_student_id= r.student_id;
if (m_student_id is not null)
            then

select get_student_current_balance into total_balance from schooldev.get_student_current_balance(m_student_id, 'MS');
select get_student_current_balance into total_balance_cc from schooldev.get_student_current_balance(m_student_id, 'CC');
existing = null;
select snb.student_id into existing from schooldev.student_net_balance snb where snb.student_id = m_student_id;
if (existing is null)
                then
                    INSERT into schooldev.student_net_balance values(m_student_id, total_balance, total_balance_cc);
else
UPDATE schooldev.student_net_balance snb SET net_balance_mess = total_balance, net_balance_card = total_balance_cc WHERE snb.student_id = m_student_id;
end if;
end if;
end loop;

END;
    /*
    select * from schooldev.update_all_student_current_balance();
    */
$$ LANGUAGE plpgsql;
--select * from schooldev.update_all_student_current_balance();

DROP FUNCTION schooldev.guest_accomodation_request(varchar, varchar, varchar, varchar, int4, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar);
--drop  type schooldev.guest_list;
create type schooldev.guest_list as (submitted_date character varying, student_id character varying, student_name character varying, gender character varying,
    warden_approval_status character varying, payment_status character varying, from_date character varying, to_date character varying,
    payment_date timestamp, id integer, no_of_persons integer, no_days integer,
    n_fm_facility_master_name character varying, room_no character varying, v_hral_sub_roomid character varying, approval_date character varying,
    created_at character varying, parent_request_id integer, list_order character varying, submitted_date1 character varying,
    allotted_hostel_name character varying, allotted_room_no character varying, roomallotmentid bigint, guest_gender character varying,
    guest_id character varying, relation_of_guest character varying, accommodation_type character varying,amount int,secondary_amount int);

-- DROP FUNCTION schooldev.guest_accommodation_request(varchar, varchar, varchar, varchar, int4, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar);
CREATE OR REPLACE FUNCTION schooldev.guest_accommodation_request(approvalfromdate character varying, approvaltodate character varying, submittedfromdate character varying, submittedtodate character varying, hostelid integer, paymentfromdate character varying, paymenttodate character varying, studentname character varying, rollid character varying, wardenname character varying, wardenemail character varying, wardenapprovalstatus character varying, paymentstatus character varying, staytype character varying, userrole character varying, username character varying)
    RETURNS SETOF schooldev.guest_list
    LANGUAGE plpgsql
AS $function$
declare
res  schooldev.guest_list%rowtype;
BEGIN
    raise notice 'Login:%',wardenapprovalstatus;
    raise notice 'userrole:%',userrole;
    if(userrole='Guest Allotment') then
        raise notice 'narayana.ohm:%','';
        if ((approvalfromdate is null or approvalfromdate='null') and (approvaltodate is null or approvaltodate='null') and
            (submittedfromdate is null or submittedfromdate='null') and (submittedtodate is null or submittedtodate='null') and
            (hostelid=0) and (paymentfromdate is null or paymentfromdate='null') and
            (paymenttodate is null or paymenttodate='null') and (studentname is null or studentname='null' or studentname='') and
            (rollid is null or rollid='' ) and (wardenname is null or wardenname='') and
            (wardenemail is null or wardenemail='') and (wardenapprovalstatus is  null or wardenapprovalstatus='')
            and (paymentstatus is null or paymentstatus='') and (staytype is null or staytype='') and (userrole <> 'null') and (username <> 'null'))
        then raise notice 'narayana.ohm-if loop';

for res in
select gar.created_at::date as submitted_date,gar.student_id,student_name,gender,
       case when warden_approval_status='OverrideAndApproved' then 'Approved' else warden_approval_status end as warden_approval_status,
       payment_status,gar.from_date,gar.to_date,payment_date,gar.id,no_of_persons,no_of_days,csv.hostel_name,csv.room_number,
       csv.room_id,approval_date,gar.created_at,parent_request_id,
       case when ((warden_approval_status='WardenApproveComplete' or warden_approval_status='Approved'
           or warden_approval_status='OverrideAndApproved') and payment_status='Paid') then '1'
            when ((warden_approval_status='WardenApproveComplete' or warden_approval_status='Approved'
                or warden_approval_status='OverrideAndApproved') and payment_status='Pending') then '2'
            when (warden_approval_status='WardenApproveComplete' and payment_status='Pending')  then '3'
            when (warden_approval_status='Validating' and payment_status='Pending')  then '4' end  as list_order,
       gar.created_at::date as submitted_date1,fm.hostel_name as allotted_hostel_name,hri.room_no as allotted_room_no,
       roomallotmentid,guest_gender,group_concat(DISTINCT guest.guest_id::text) as guest_id,
       group_concat(DISTINCT relation_of_guest::text) as relation_of_guest,
       gar.accommodation_type,gar.amount,gar.secondary_amount ,gar.secondary_amount
from
    schooldev."GUEST_ACCOMMODATION_REQUEST" gar  join
    schooldev."GUEST_ACCOMMODATION_GUEST_DETAILS" guest on((guest.request_id=gar.id) and guest.active_flag='Y') left join
    schooldev."ALL_STUDENTS_DETAILS_VIEW" csv ON (gar.student_id=csv.student_id)  left join
    schooldev."GUEST_ROOM_ALLOTMENT_INFO" guestroom on (gar.id=requestid::integer and (guest.guest_id::character varying in (select regexp_split_to_table(guestroom.guest_id, ',') as guestids from schooldev."GUEST_ROOM_ALLOTMENT_INFO")) and guestroom.active_flag='Y') left join
    schooldev."HOSTEL_FLOOR_MASTER" sf on (sf.floor_id=building_id) left join
    schooldev."HOSTEL_MASTER" fm on(fm.hostel_id=sf.hostel_id) left join
    schooldev."HOSTEL_ROOM_INFO" hri on(roomid = hri.room_id) left join
    schooldev."HOSTEL_USER_MAPPING" c on ((c.hostel_id=fm.hostel_id) and  c.active_flag='Y') left join
    schooldev."USER_MANAGEMENT" h on ((c.user_name=h.user_name) and h.active_flag='Y')
where gar.active_flag='Y'
  and case when userrole='Hostel Check In' then (warden_approval_status in ('OverrideAndApproved','Approved','WardenApproveComplete') and payment_status in('Paid'))
           else (warden_approval_status in ('OverrideAndApproved','Approved','Validating','WardenApproveComplete') and payment_status in('Pending','Paid')) end
  and case when  userrole='Hostel Check In'  then h.user_name=username else 1=1  end
  and case when  userrole='Hostel Check In'  then (gar.to_date::date >= now()::date and gar.from_date::date <= now()::date + 30)
           else (gar.created_at::date >=(now()::date - 30) or gar.from_date::date >=(now()::date)) end
group by gar.id,student_name,gender,csv.hostel_name,csv.room_number,fm.hostel_name,hri.room_no,roomallotmentid,csv.room_id,guest_gender
order by  list_order asc,id

    loop
                return next res;
end loop;
else raise notice 'narayana.ohm - else loop:%',hostelid;
for res in

select gar.created_at::date as submitted_date,gar.student_id,student_name,gender,
       case when warden_approval_status='OverrideAndApproved' then 'Approved' else warden_approval_status end as warden_approval_status,
       payment_status,gar.from_date,gar.to_date,payment_date,gar.id,no_of_persons,no_of_days,csv.hostel_name,csv.room_number,
       csv.room_id,approval_date,gar.created_at,parent_request_id,
       case when ((warden_approval_status='WardenApproveComplete' or warden_approval_status='Approved'
           or warden_approval_status='OverrideAndApproved') and payment_status='Paid') then '1'
            when ((warden_approval_status='WardenApproveComplete' or warden_approval_status='Approved'
                or warden_approval_status='OverrideAndApproved') and payment_status='Pending') then '2'
            when (warden_approval_status='WardenApproveComplete' and payment_status='Pending')  then '3'
            when (warden_approval_status='Validating' and payment_status='Pending')  then '4' end  as list_order,
       gar.created_at::date as submitted_date1,fm.hostel_name as allotted_hostel_name,hri.room_no as allotted_room_no,roomallotmentid,
       guest_gender,group_concat(DISTINCT guest.guest_id::text) as guest_id,
       group_concat(DISTINCT relation_of_guest::text) as relation_of_guest,
       gar.accommodation_type,gar.amount,gar.secondary_amount
from
    schooldev."GUEST_ACCOMMODATION_REQUEST" gar join
    schooldev."GUEST_ACCOMMODATION_GUEST_DETAILS" guest on((guest.request_id=gar.id) and guest.active_flag='Y') left join
    schooldev."ALL_STUDENTS_DETAILS_VIEW" csv ON (gar.student_id=csv.student_id)  left join
    schooldev."GUEST_ROOM_ALLOTMENT_INFO" guestroom on( (gar.id=requestid::integer)
        and (guest.guest_id::character varying in (select regexp_split_to_table(guestroom.guest_id, ',') as guestids
                                                               from schooldev."GUEST_ROOM_ALLOTMENT_INFO")) and guestroom.active_flag='Y') left join
    schooldev."HOSTEL_FLOOR_MASTER" sf on (sf.floor_id=building_id) left join
    schooldev."HOSTEL_MASTER" fm on(fm.hostel_id=sf.hostel_id) left join
    schooldev."HOSTEL_ROOM_INFO" hri on(roomid = hri.room_id) left join
    schooldev."HOSTEL_USER_MAPPING" c on ((c.hostel_id=fm.hostel_id) and  c.active_flag='Y') left join
    schooldev."USER_MANAGEMENT" h on ((c.user_name=h.user_name) and h.active_flag='Y')
where gar.active_flag='Y' and
    case when  userrole='Hostel Check In'  then h.user_name=username else 1=1  end
/* and(warden_approval_status = case when (wardenapprovalstatus <>'null' and wardenapprovalstatus<>'')
     then wardenapprovalstatus else 'Validating'  end or warden_approval_status =case when (wardenapprovalstatus <>'null'
     and   wardenapprovalstatus<>'') then wardenapprovalstatus else 'Approved'  end
     or warden_approval_status =case when (wardenapprovalstatus <>'null' and   wardenapprovalstatus<>'')
     then wardenapprovalstatus else 'Rejected'  end or warden_approval_status =case when (wardenapprovalstatus <>'null'
     and   wardenapprovalstatus<>'') then wardenapprovalstatus else 'WardenApproveComplete'  end or warden_approval_status =case
     when (wardenapprovalstatus <>'null' and   wardenapprovalstatus<>'') then wardenapprovalstatus else 'Cancelled'  end) */

  and (case when (wardenapprovalstatus='Approved') then  (warden_approval_status) in ('Approved','OverrideAndApproved','WardenApproveComplete')
            when (wardenapprovalstatus='Allotted') then (fm.hostel_name is not null)
            when (wardenapprovalstatus<>'null' and wardenapprovalstatus<>'') then upper(warden_approval_status) = upper(wardenapprovalstatus)
            else (warden_approval_status in('Approved','OverrideAndApproved','WardenApproveComplete','Validating')) end )
  and (payment_status = case when (paymentstatus::text <>'null' and paymentstatus::text <>'') then paymentstatus else 'Pending'  end   or
       payment_status = case when (paymentstatus::text <>'null' and paymentstatus::text <>'') then paymentstatus else 'Paid' end)

  and (case when (paymentfromdate::text<>'null' and paymentfromdate::date is not null) then  payment_date >=paymentfromdate::date
                        else 1=1 end)
  and (case when (paymenttodate::text<>'null' and paymenttodate::date is not null) then payment_date <= paymenttodate::date
                        else 1=1 end)

  and (case when (approvalfromdate::text<>'null' and approvalfromdate::date is not null) then  approval_date >=approvalfromdate::date
                        else 1=1 end)

  and (case when (approvaltodate::text<>'null' and approvaltodate::date is not null) then approval_date <= approvaltodate::date
                        else 1=1 end)

  and( case when (submittedfromdate::text<>'null' and submittedfromdate::date is not null) then  gar.created_at::date >=submittedfromdate::date
                        else 1=1 end)
  and (case when (submittedtodate::text<>'null' and submittedtodate::date is not null) then gar.created_at::date <= submittedtodate::date
                        else 1=1 end)


  and (case when (rollid<>'NULL' and rollid is not null) then upper(gar.student_id) like upper(rollid||'%') else 1=1 end)

  and (case when (studentname<>'null' and studentname is not null AND studentname!='') then upper(student_name) like upper( studentname||'%') else 1=1 end)
  and(case when hostelid<>'0'then csv.hostel_name=(select hostel_name from schooldev."HOSTEL_MASTER" hm_in where hm_in.hostel_id=hostelid)::text else 1=1 end)
  and(case when staytype='app' then parent_request_id=0
           when staytype='stay' then parent_request_id!=0
           when staytype='both' then (parent_request_id!=0 or parent_request_id=0)
           else 1=1 end)
group by gar.id,student_name,gender,csv.hostel_name,csv.room_number,fm.hostel_name,hri.room_no,roomallotmentid,csv.room_id,guest_gender
order by list_order asc,id
    loop
                return next res;
end loop;
end if;

else
        raise notice 'other users:%','';

        if ((approvalfromdate is null or approvalfromdate='null') and (approvaltodate is null or approvaltodate='null') and
            (submittedfromdate is null or submittedfromdate='null') and (submittedtodate is null or submittedtodate='null') and
            (hostelid=0) and (paymentfromdate is null or paymentfromdate='null') and
            (paymenttodate is null or paymenttodate='null') and (studentname is null or studentname='null' or studentname='') and
            (rollid is null or rollid='' ) and (wardenname is null or wardenname='') and
            (wardenemail is null or wardenemail='') and (wardenapprovalstatus is  null or wardenapprovalstatus='')
            and (paymentstatus is null or paymentstatus='') and (staytype is null or staytype='') and (userrole <> 'null') and (username <> 'null'))
        then raise notice 'other users-if loop';

for res in
select gar.created_at::date as submitted_date,gar.student_id,student_name,gender,
       case when warden_approval_status='OverrideAndApproved' then 'Approved' else warden_approval_status end as warden_approval_status,
       payment_status,gar.from_date,gar.to_date,payment_date,gar.id,no_of_persons,no_of_days,csv.hostel_name,csv.room_number,
       csv.room_id,approval_date,gar.created_at,parent_request_id,
       case when ((warden_approval_status='WardenApproveComplete' or warden_approval_status='Approved'
           or warden_approval_status='OverrideAndApproved') and payment_status='Paid') then '1'
            when ((warden_approval_status='WardenApproveComplete' or warden_approval_status='Approved'
                or warden_approval_status='OverrideAndApproved') and payment_status='Pending') then '2'
            when (warden_approval_status='WardenApproveComplete' and payment_status='Pending')  then '3'
            when (warden_approval_status='Validating' and payment_status='Pending')  then '4' end  as list_order,
       gar.created_at::date as submitted_date1,fm.hostel_name as allotted_hostel_name,hri.room_no as allotted_room_no,roomallotmentid,
       '' as guest_gender,'' as guest_id,'' as relation_of_guest,
       gar.accommodation_type,gar.amount,gar.secondary_amount    from
    schooldev."GUEST_ACCOMMODATION_REQUEST" gar left join
    schooldev."ALL_STUDENTS_DETAILS_VIEW" csv ON (gar.student_id=csv.student_id)  left join
    schooldev."GUEST_ROOM_ALLOTMENT_INFO" guestroom on( (gar.id=requestid::integer) and guestroom.active_flag='Y') left join
    schooldev."HOSTEL_FLOOR_MASTER" sf on (sf.floor_id=building_id) left join
    schooldev."HOSTEL_MASTER" fm on(fm.hostel_id=sf.hostel_id) left join
    schooldev."HOSTEL_ROOM_INFO" hri on(roomid = hri.room_id) left join
    schooldev."HOSTEL_USER_MAPPING" c on ((c.hostel_id=fm.hostel_id) and  c.active_flag='Y') left join
    schooldev."USER_MANAGEMENT" h on ((c.user_name=h.user_name) and h.active_flag='Y')
where gar.active_flag='Y'
  and case when userrole='Hostel Check In' then (warden_approval_status in ('OverrideAndApproved','Approved','WardenApproveComplete') and payment_status in('Paid'))
           else (warden_approval_status in ('OverrideAndApproved','Approved','Validating','WardenApproveComplete') and payment_status in('Pending','Paid')) end
  and case when  userrole='Hostel Check In'  then h.user_name=username else 1=1  end
  and case when  userrole='Hostel Check In'  then (gar.to_date::date >= now()::date and gar.from_date::date <= now()::date + 30) else
    (gar.created_at::date >=(now()::date - 30) or gar.from_date::date >=(now()::date)) end
order by  list_order asc,id

    loop
                return next res;
end loop;
else raise notice 'other users - else loop:%',hostelid;
        raise notice 'params: %, %, %, %, %, %, %, %, %, %, %, %, %, %, %', '', userrole, username, wardenapprovalstatus, paymentstatus, paymentfromdate, paymenttodate,
            approvalfromdate, approvaltodate, submittedfromdate, submittedtodate, rollid, studentname, hostelid, staytype;
for res in

select gar.created_at::date as submitted_date,gar.student_id,student_name,gender,
       case when warden_approval_status='OverrideAndApproved' then 'Approved' else warden_approval_status end as warden_approval_status,
       payment_status,gar.from_date,gar.to_date,payment_date,gar.id,no_of_persons,no_of_days,csv.hostel_name,csv.room_number,
       csv.room_id,approval_date,gar.created_at,parent_request_id,
       case when ((warden_approval_status='WardenApproveComplete' or warden_approval_status='Approved'
           or warden_approval_status='OverrideAndApproved') and payment_status='Paid') then '1'
            when ((warden_approval_status='WardenApproveComplete' or warden_approval_status='Approved'
                or warden_approval_status='OverrideAndApproved') and payment_status='Pending') then '2'
            when (warden_approval_status='WardenApproveComplete' and payment_status='Pending')  then '3'
            when (warden_approval_status='Validating' and payment_status='Pending')  then '4' end  as list_order,
       gar.created_at::date as submitted_date1,fm.hostel_name as allotted_hostel_name,hri.room_no as allotted_room_no,roomallotmentid,
       '' as guest_gender,'' as guest_id,'' as relation_of_guest,
       gar.accommodation_type,gar.amount,gar.secondary_amount    from
    schooldev."GUEST_ACCOMMODATION_REQUEST" gar left join
    schooldev."ALL_STUDENTS_DETAILS_VIEW" csv ON (gar.student_id=csv.student_id)  left join
    schooldev."GUEST_ROOM_ALLOTMENT_INFO" guestroom on( (gar.id=requestid::integer) and guestroom.active_flag='Y') left join
    schooldev."HOSTEL_FLOOR_MASTER" sf on (sf.floor_id=building_id) left join
    schooldev."HOSTEL_MASTER" fm on(fm.hostel_id=sf.hostel_id) left join
    schooldev."HOSTEL_ROOM_INFO" hri on(roomid = hri.room_id) left join
    schooldev."HOSTEL_USER_MAPPING" c on ((c.hostel_id=fm.hostel_id) and  c.active_flag='Y') left join
    schooldev."USER_MANAGEMENT" h on ((c.user_name=h.user_name) and h.active_flag='Y')
where gar.active_flag='Y' and case when  userrole='Hostel Check In'  then h.user_name=username else 1=1  end
/* and(warden_approval_status = case when (wardenapprovalstatus <>'null' and wardenapprovalstatus<>'')
     then wardenapprovalstatus else 'Validating'  end or warden_approval_status =case when (wardenapprovalstatus <>'null'
     and   wardenapprovalstatus<>'') then wardenapprovalstatus else 'Approved'  end
     or warden_approval_status =case when (wardenapprovalstatus <>'null' and   wardenapprovalstatus<>'')
     then wardenapprovalstatus else 'Rejected'  end or warden_approval_status =case when (wardenapprovalstatus <>'null'
     and   wardenapprovalstatus<>'') then wardenapprovalstatus else 'WardenApproveComplete'  end or warden_approval_status =case
     when (wardenapprovalstatus <>'null' and   wardenapprovalstatus<>'') then wardenapprovalstatus else 'Cancelled'  end) */

  and (case when (wardenapprovalstatus='Approved') then  (warden_approval_status) in ('Approved','OverrideAndApproved','WardenApproveComplete')
            when (wardenapprovalstatus='Allotted') then (fm.hostel_name is not null)
            when (wardenapprovalstatus<>'null' and wardenapprovalstatus<>'') then upper(warden_approval_status) = upper(wardenapprovalstatus)
            else (warden_approval_status in('Approved','OverrideAndApproved','WardenApproveComplete','Validating')) end )
  and (payment_status = case when (paymentstatus::text <>'null' and paymentstatus::text <>'') then paymentstatus else 'Pending'  end   or
       payment_status = case when (paymentstatus::text <>'null' and paymentstatus::text <>'') then paymentstatus else 'Paid' end)

  and (case when (paymentfromdate::text<>'null' and paymentfromdate::date is not null) then  payment_date >=paymentfromdate::date
                        else 1=1 end)
  and (case when (paymenttodate::text<>'null' and paymenttodate::date is not null) then payment_date <= paymenttodate::date
                        else 1=1 end)

  and (case when (approvalfromdate::text<>'null' and approvalfromdate::date is not null) then  approval_date >=approvalfromdate::date
                        else 1=1 end)

  and (case when (approvaltodate::text<>'null' and approvaltodate::date is not null) then approval_date <= approvaltodate::date
                        else 1=1 end)

  and( case when (submittedfromdate::text<>'null' and submittedfromdate::date is not null) then  gar.created_at::date >=submittedfromdate::date
                        else 1=1 end)
  and (case when (submittedtodate::text<>'null' and submittedtodate::date is not null) then gar.created_at::date <= submittedtodate::date
                        else 1=1 end)


  and (case when (rollid<>'NULL' and rollid is not null) then upper(gar.student_id) like upper(rollid||'%') else 1=1 end)

  and (case when (studentname<>'null' and studentname is not null AND studentname!='') then upper(student_name) like upper( studentname||'%') else 1=1 end)
  and(case when hostelid<>'0'then csv.hostel_name=(select hostel_name from schooldev."HOSTEL_MASTER" hm_in where hm_in.hostel_id=hostelid)::text else 1=1 end)
  and(case when staytype='app' then parent_request_id=0
           when staytype='stay' then parent_request_id!=0
           when staytype='both' then (parent_request_id!=0 or parent_request_id=0)
           else 1=1 end)

order by list_order asc,id
    loop
                return next res;
end loop;
end if;

end if;
END;
/*
drop type public.guest_list;
drop function if exists schooldev.guest_accomodation_request(varchar, varchar, varchar, varchar, integer, varchar, varchar,
                                                   varchar, varchar, varchar, varchar, varchar, varchar, varchar,
                                                   varchar, varchar);
drop function if exists schooldev.guest_accomodation_request(varchar, varchar, varchar, varchar, integer, varchar, varchar,
                                                   varchar, varchar, varchar, varchar, varchar, varchar, varchar,
                                                   varchar, varchar, varchar);
drop  type schooldev.guest_list;
create type schooldev.guest_list as (submitted_date character varying, student_id character varying, student_name character varying, gender character varying,
 warden_approval_status character varying, payment_status character varying, from_date character varying, to_date character varying,
 payment_date timestamp, id integer, no_of_persons integer, no_days integer,
 n_fm_facility_master_name character varying, room_no character varying, v_hral_sub_roomid character varying, approval_date character varying,
 created_at character varying, parent_request_id integer, list_order character varying, submitted_date1 character varying,
 allotted_hostel_name character varying, allotted_room_no character varying, roomallotmentid bigint, guest_gender character varying,
 guest_id character varying, relation_of_guest character varying, accommodation_type character varying,amount int,secondary_amount int);



SELECT * from schooldev.guest_accommodation_request('null','null','null','null','0','null','null',NULL,NULL,NULL,NULL,NULL,NULL,'Individual Guest Room','Staff','narayana.ohm');
SELECT * from schooldev.guest_accommodation_request('null','null','null','null','0','null','null',NULL,NULL,NULL,NULL,NULL,NULL,'Individual Guest Room','Hostel Check In','cauvery.hostel');
SELECT * from schooldev.guest_accommodation_request('null','null','null','null','0','null','null','','',NULL,NULL,'','','Individual Guest Room','CCW DEAN','ccw.iitm');
SELECT * from schooldev.guest_accommodation_request(null,null,null,null,'0',null,null,'','OE17S007',NULL,NULL,'','','Stay Along with Student','CCW DEAN','ccw.iitm');
SELECT * from schooldev.guest_accommodation_request('null','null','2025-11-01','null','0','null','null','','',NULL,NULL,'','',NULL,'Guest Allotment','narayana.ohm');
SELECT * from schooldev.guest_accommodation_request('null','null','null','null','0','null','null',NULL,NULL,NULL,NULL,NULL,NULL,'Stay Along with Student','Hostel Check In','cauvery.hostel');

*/

$function$
;



-- DROP FUNCTION schooldev.hdc_complaint_list(varchar, varchar, varchar, varchar, varchar, varchar, int4, int8, int4);

CREATE OR REPLACE FUNCTION schooldev.hdc_complaint_list(studentid character varying, studentname character varying, submittedfromdate character varying, submittedtodate character varying, loginid character varying, userrole character varying, wardenid integer, hostelid bigint, year integer)
 RETURNS SETOF hdccomplaintlisttype
 LANGUAGE plpgsql
AS $function$
declare    res hdccomplaintlisttype%rowtype;
           facility_ids character varying;
BEGIN
select group_concat(hostel_id::text) into facility_ids from schooldev."HOSTEL_USER_MAPPING"
where user_name=loginid and active_flag='Y' group by user_name ;
if(userrole='Hostel Check In') then
        if (studentid=null or studentid is null or studentid='') and (studentname=null  or studentname is null or studentname='')
            and  (submittedfromdate='null' or submittedfromdate is null or submittedfromdate='')
            and (submittedtodate='null' or submittedtodate is null or submittedtodate='')
            and (wardenid=0) and (hostelid=0) and (year=0)
        then
            raise notice 'ifloop: %',  'Hostel Check In';
for res in
select hdc.hostel_id,warden_id,hdc_id,student_id, student_name,hm.hostel_name,room_no,violation,wardern_plea,warden_decision,
       follow_up_report, warden_remarks,penality_status,hdc.created_at,hdc.created_by,hdc.category,hdc.day_scholar_involve
from schooldev."IIT_HDC_COMPLAINT_FORMDETAILS" hdc
         join  schooldev."HOSTEL_MASTER" hm on(hdc.hostel_id=hm.hostel_id)
         left join schooldev."USER_MANAGEMENT" um on (hdc.created_by=um.user_id and um.active_flag='Y')
         left join schooldev.roles r on (um.role_id=r.role_id and r.active_flag='Y')
where
    hdc.active_flag='Y'
  and hdc.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat)
                  and	hdc.created_at >=(now()::date-30) --and lower(a.created_by) = lower(loginid)
                  and (penality_status is null or penality_status='Pending' or penality_status='PartialPaid')
                  and upper(r.role_name)!=upper('Dean')
order by  created_at desc
    loop
    return next res;
end loop;
else
            raise notice 'else: %',  'Hostel Check In';
for res in
select  hdc.hostel_id,warden_id,hdc_id,student_id, student_name,hm.hostel_name,room_no,violation,wardern_plea,warden_decision,
        follow_up_report, warden_remarks,penality_status,hdc.created_at,hdc.created_by,hdc.category,hdc.day_scholar_involve
from schooldev."IIT_HDC_COMPLAINT_FORMDETAILS" hdc
         join  schooldev."HOSTEL_MASTER" hm on(hdc.hostel_id=hm.hostel_id)
         left join schooldev."USER_MANAGEMENT" um on (hdc.created_by=um.user_id and um.active_flag='Y')
         left join schooldev.roles r on (um.role_id=r.role_id and r.active_flag='Y')
where
    hdc.active_flag='Y'
  and upper(r.role_name)!=upper('Dean')
                  and hdc.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat)
                  and (case when (studentid is not null and studentid<>'') then upper(student_id) like upper('%'||studentid||'%') else 1=1 end)
                  and (case when (studentname is not null and studentname<>'') then (lower(student_name) like lower('%'||studentname||'%')) else 1=1 end)
                  and (case when (submittedfromdate::text<>'null' and submittedfromdate::date is not null) then  hdc.created_at::date>=submittedfromdate::date else 1=1 end)
                  and (case when (submittedtodate::text<>'null' and submittedtodate::date is not null) then hdc.created_at::date <= submittedtodate::date else 1=1 end)
                  and (case when (year is not null and year <> 0) then EXTRACT(YEAR from hdc.created_At) = year else 1=1 end)

                loop return next res;
end loop;
end if;
end if;
    if(userrole='Warden') then
        if (studentid=null or studentid is null or studentid='') and (studentname=null  or studentname is null or studentname='')
            and  (submittedfromdate='null' or submittedfromdate is null or submittedfromdate='')
            and (submittedtodate='null' or submittedtodate is null or submittedtodate='')
            and (hostelid=0) and (year=0)
        then
            raise notice 'ifloop: %',  'Warden';
for res in
select  hdc.hostel_id,warden_id,hdc_id,student_id, student_name,hm.hostel_name,room_no,violation,wardern_plea,warden_decision,
        follow_up_report, warden_remarks,penality_status,hdc.created_at,hdc.created_by,hdc.category,hdc.day_scholar_involve
from schooldev."IIT_HDC_COMPLAINT_FORMDETAILS" hdc
         join  schooldev."HOSTEL_MASTER" hm on(hdc.hostel_id=hm.hostel_id)
         left join schooldev."USER_MANAGEMENT" um on (hdc.created_by=um.user_id and um.active_flag='Y')
         left join schooldev.roles r on (um.role_id=r.role_id and r.active_flag='Y')
where
    hdc.active_flag='Y' and hdc.created_at >=(now()::date-30) and lower(hdc.created_by) = lower(loginid)
  and (penality_status is null or penality_status='Pending' or penality_status='PartialPaid')
  and upper(r.role_name)!=upper('Dean')
order by  hdc.created_at desc
    loop
    return next res;
end loop;
else
            raise notice 'else: %',  'Warden';
for res in
select  hdc.hostel_id,warden_id,hdc_id,student_id, student_name,hm.hostel_name,room_no,violation,wardern_plea,warden_decision,
        follow_up_report, warden_remarks,penality_status,hdc.created_at,hdc.created_by,hdc.category,hdc.day_scholar_involve
from schooldev."IIT_HDC_COMPLAINT_FORMDETAILS" hdc
         join  schooldev."HOSTEL_MASTER" hm on(hdc.hostel_id=hm.hostel_id)
         left join schooldev."USER_MANAGEMENT" um on (hdc.created_by=um.user_id and um.active_flag='Y')
         left join schooldev.roles r on (um.role_id=r.role_id and r.active_flag='Y')
where
    hdc.active_flag='Y'
  and lower(hdc.created_by) = lower(loginid)
  and upper(r.role_name)!=upper('Dean')
                  and (case when (studentid is not null and studentid<>'') then (upper(student_id) like upper('%'||studentid||'%')) else 1=1 end )
                  and (case when (studentname is not null and studentname<>'') then (lower(student_name) like lower('%'||studentname||'%')) else 1=1 end)
                  and (case when (submittedfromdate::text<>'null' and submittedfromdate::date is not null) then  hdc.created_at::date>=submittedfromdate::date else 1=1 end)
                  and (case when (submittedtodate::text<>'null' and submittedtodate::date is not null) then hdc.created_at::date <= submittedtodate::date else 1=1 end)
                  and (case when (year is not null and year <> 0) then EXTRACT(YEAR from hdc.created_At) = year else 1=1 end)

                loop return next res;
end loop;
end if;
end if;
    if(userrole='HDC' or userrole='Dean' or userrole='SoftwareAdmin') then
        if (studentid=null or studentid is null or studentid='') and (studentname=null  or studentname is null or studentname='')
            and  (submittedfromdate='null' or submittedfromdate is null or submittedfromdate='')
            and (submittedtodate='null' or submittedtodate is null or submittedtodate='')
            and (wardenid=0) and (hostelid=0) and (year=0)
        then
            raise notice 'ifloop: %',  'Dean';
for res in
select  hdc.hostel_id,warden_id,hdc_id,student_id, student_name,hm.hostel_name,room_no,violation,wardern_plea,warden_decision,
        follow_up_report, warden_remarks,penality_status,hdc.created_at,hdc.created_by,hdc.category,hdc.day_scholar_involve
from schooldev."IIT_HDC_COMPLAINT_FORMDETAILS" hdc
         left join  schooldev."HOSTEL_MASTER" hm on(hdc.hostel_id=hm.hostel_id)
where
    hdc.active_flag='Y' and hdc.created_at >=(now()::date-30) --and a.hostel_id=hostelid
  and (penality_status is null or penality_status='Pending' or penality_status='PartialPaid')
order by  hdc.created_at desc
    loop
                    return next res;
end loop;
else
            raise notice 'else: %',  'Dean';
for res in
select  hdc.hostel_id,warden_id,hdc_id,student_id, student_name,hm.hostel_name,room_no,violation,wardern_plea,warden_decision,
        follow_up_report, warden_remarks,penality_status,hdc.created_at,hdc.created_by,hdc.category,hdc.day_scholar_involve
from schooldev."IIT_HDC_COMPLAINT_FORMDETAILS" hdc
         left join  schooldev."HOSTEL_MASTER" hm on(hdc.hostel_id=hm.hostel_id)
where
    hdc.active_flag='Y'
  and (case when (studentid is not null and studentid<>'') then (upper(student_id) like upper('%'||studentid||'%')) else 1=1 end )
  and (case when (studentname is not null and studentname<>'') then (lower(student_name) like lower('%'||studentname||'%')) else 1=1 end)
  and (case when (submittedfromdate::text<>'null' and submittedfromdate::date is not null) then  hdc.created_at::date>=submittedfromdate::date else 1=1 end)
  and (case when (submittedtodate::text<>'null' and submittedtodate::date is not null) then hdc.created_at::date <= submittedtodate::date else 1=1 end)
  and (case when (year is not null and year <> 0) then EXTRACT(YEAR from hdc.created_At) = year else 1=1 end)
  and (case when (hostelid is not null and hostelid<> 0) then hdc.hostel_id = hostelid else 1=1 end)
    loop return next res;
end loop;
end if;
end if;
END;
/* drop type hdccomplaintlisttype cascade;
create type hdccomplaintlisttype as (hostel_id bigint,warden_id character varying,hdc_id integer,student_id character varying,student_name character varying,hostel_name character varying,room_no integer,violation character varying,wardern_plea character varying,warden_decision character varying,follow_up_report character varying, warden_remarks character varying,penality_status character varying,
created_at character varying,created_by character varying,category character varying, day_scholar_involve character varying);
select *  from  schooldev.hdc_complaint_list('',NULL,'null','null','ccw.iitm','DEAN','0','0')
select *  from  schooldev.hdc_complaint_list('','ROHIT','2024-03-14','null','ganga.hostel','Hostel Check In','0','0')
select *  from  schooldev.hdc_complaint_list('',NULL,'null','null','wardensabar','Warden','21','0')
*/
$function$
;


-- DROP FUNCTION schooldev.total_summary_count(varchar);

CREATE OR REPLACE FUNCTION schooldev.total_summary_count(para_user_name character varying)
 RETURNS SETOF count_result
 LANGUAGE plpgsql
AS $function$
declare
res count_result%rowtype;
BEGIN
for res in
select type, accommodation_type ,student_type,accom_type, alloted_total, checked_in, pending_checkin,  tobe_check_out_today, checked_out
FROM
    schooldev."IIT_W_CATEGORY_CONFIGURATION" left join
    (
        select student_type,
               ( case  when (student_type='StudentApp' and nature_of_appointment='stustayextension') then 'StuStayExtension'
                       when (student_type='StudentScholar' and nature_of_appointment='SCHOLAR') then 'StuScholar'
                       when (student_type='Candidate' and nature_of_appointment='INTERVIEWS') then 'CandInterview'
                       when (student_type='Candidate' and nature_of_appointment='ICSR') then 'CandIcsr'
                       when (student_type='StudentApp' and
                             nature_of_appointment  IN('insidecampus','outsidecampus','other','sasthra')) then
                           'vacationStudent'
                       else 'OtherAccomm' end
                   ) as accom_type,
               sum( case when (a.checkin_checkout_status in ('Alloted','CheckedIn')) then 1
                         else 0 end ) as  alloted_total,

               sum( case when (a.checkin_checkout_status='CheckedIn') then 1
                         else 0 end ) as checked_in,

               sum( case when (a.checkin_checkout_status='Alloted') then 1
                         else 0 end ) as pending_checkin,

               sum( case when (a.checkin_checkout_status in ('CheckedIn','CheckedOut') and stay_to_date::date=now()::date) then 1
                         else 0 end ) as tobe_check_out_today ,

               sum( case when (a.checkin_checkout_status='CheckedOut' and stay_to_date::date=now()::date) then 1
                         else 0 end) as checked_out
        from
            schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW" a JOIN
            schooldev."HOSTEL_FLOOR_MASTER" s ON (a.building_id = s.floor_id and s.active_flag='Y') JOIN
            schooldev."HOSTEL_MASTER" f ON (s.hostel_id = f.hostel_id and f.active_flag='Y') join
            schooldev."HOSTEL_USER_MAPPING" c on ((c.hostel_id=f.hostel_id) and  c.active_flag='Y')
        where
            a.active_flag='Y' and (c.user_name)=(para_user_name)  and (stay_from_date::date<=now()::date
                    and stay_to_date::date>=now()::date)
        group by
            student_type,
            (
                case when (student_type='StudentApp' and nature_of_appointment='stustayextension') then 'StuStayExtension'
                     when (student_type='StudentScholar' and nature_of_appointment='SCHOLAR') then 'StuScholar'
                     when (student_type='Candidate' and nature_of_appointment='INTERVIEWS') then 'CandInterview'
                     when (student_type='Candidate' and nature_of_appointment='ICSR') then 'CandIcsr'
                     when (student_type='StudentApp' and nature_of_appointment
                         IN('insidecampus','outsidecampus','other','sasthra')) then 'vacationStudent'					           else 'OtherAccomm'
                    end
                )
        order by student_type
    ) B on (B.accom_type=accommodation_type)
order by student_type
    loop
            return next res;
end loop;
end;
/*
drop type count_result cascade;
create type count_result as (student_type character varying, accomm_type character varying,student_type1 character varying, accomm_type1 character varying,alloted_total bigint, checked_in bigint, pending_checkin bigint, tobe_check_out_today bigint, checked_out  bigint);
select *  from schooldev.total_summary_count('bhadra.hostel')
*/
$function$
;


-- DROP FUNCTION schooldev.pending_checkout_count(varchar);

CREATE OR REPLACE FUNCTION schooldev.pending_checkout_count(para_user_name character varying)
 RETURNS SETOF checkout_count_result
 LANGUAGE plpgsql
AS $function$
declare
res checkout_count_result%rowtype;
BEGIN

for res in
select  student_type,
        (
            case when (student_type='StudentApp' and nature_of_appointment='stustayextension') then 'StuStayExtension'
                 when (student_type='StudentScholar' and nature_of_appointment='SCHOLAR') then 'StuScholar'
                 when (student_type='Candidate' and nature_of_appointment='INTERVIEWS') then 'CandInterview'
                 when (student_type='Candidate' and nature_of_appointment='ICSR') then 'CandIcsr'
                 when (student_type='StudentApp' and
                       nature_of_appointment  IN('insidecampus','outsidecampus','other','sasthra')) then 'vacationStudent'
                 else 'OtherAccomm' end
            ) as accomm_type,
        sum(
                case when (checkin_checkout_status='CheckedIn') then 1
                     else 0 end
        ) as pending_checkout

from schooldev."VACATION_HOSTEL_ROOM_ALLOTMENT_INFO_VIEW" a JOIN
     schooldev."HOSTEL_FLOOR_MASTER" s ON (a.building_id = s.floor_id and s.active_flag='Y') JOIN
     schooldev."HOSTEL_MASTER" f ON (s.hostel_id = f.hostel_id and f.active_flag='Y') join
     schooldev."HOSTEL_USER_MAPPING" c on ((c.hostel_id=f.hostel_id) and  c.active_flag='Y')

where a.active_flag='Y' and lower(c.user_name)=lower(para_user_name) and student_type in ('Candidate','StudentScholar','StudentApp')
  and stay_to_date::date>=now()::date-30 and checkin_checkout_status='CheckedIn'
group by  student_type,
    (
    case when (student_type='StudentApp' and nature_of_appointment='stustayextension') then 'StuStayExtension'
    when (student_type='StudentScholar' and nature_of_appointment='SCHOLAR') then 'StuScholar'
    when (student_type='Candidate' and nature_of_appointment='INTERVIEWS') then 'CandInterview'
    when (student_type='Candidate' and nature_of_appointment='ICSR') then 'CandIcsr'
    when (student_type='StudentApp' and
    nature_of_appointment  IN('insidecampus','outsidecampus','other','sasthra')) then
    'vacationStudent'
    else 'OtherAccomm' end
    )
order by student_type
    loop
    return next res;
end loop;
end;
/*
drop type checkout_count_result cascade;
create type checkout_count_result as (student_type character varying, accomm_type character varying,pending_checkout bigint);
select *  from schooldev.pending_checkout_count('bhadra.hostel')
*/
$function$
;

-- DROP FUNCTION schooldev.process_workflow_stay_extension(int8, varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION schooldev.process_workflow_stay_extension(given_wf_row_id bigint, given_status character varying, given_reason character varying, given_approval_notes character varying)
 RETURNS SETOF candidate_stay_workflow_type_tosendemail
 LANGUAGE plpgsql
AS $function$

declare
approvel_levelfld bigint;
application_idfld bigint;
authentication_typefld varchar;
status_all_approved_in_same_level boolean:=false;
count_of_pending int :=0;
list_size int :=0;
r candidate_stay_workflow_type_toSendEmail%rowtype;
status_description text :='';

BEGIN
-- Here updating the Specific status. If overriding directly updating status without the current status
 --START TRANSACTION ISOLATION LEVEL READ COMMITTED;
	if given_status!='OverrideApproved' then
UPDATE schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW"    SET  modified_at=now(), approval_status=given_status, approval_notes=given_approval_notes  WHERE id=given_WF_row_id ;
--		and modified_at =given_date :: timestamp;
--and status='Pending';
else
UPDATE schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW"    SET   modified_at=now(),approval_status='Approved', approval_notes=given_approval_notes  WHERE id=given_WF_row_id ;
--			and modified_at =given_date :: timestamp;
end if;

-- Get the level, application id, authentication_type  of current record
SELECT approval_level,stay_id, authentication_type INTO approvel_levelfld,application_idfld, authentication_typefld FROM schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW" WHERE id=given_WF_row_id;


--if the given_status is rejected, then the student status is directly updated as 'Rejected'
raise notice 'application_idfld%',application_idfld;
	if given_status='Rejected' then
update schooldev."IIT_W_CANDIDATE_STAY_REQUEST" set modified_at = now(), approval_status = given_status, rejection_description = given_reason,status_notes='Your request has been rejected for the reason:'||given_reason where stay_id=application_idfld;
update schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW" set modified_at = now(), rejection_description = given_reason where id=given_wf_row_id;
update schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW" set modified_at=now(), approval_status=given_status where id=given_wf_row_id;
else
begin
-- Decide whether all other people have approved, who are in the same level
			if authentication_typefld='a' THEN
begin
select count(*) into count_of_pending FROM schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW" WHERE  approval_level=approvel_levelfld and stay_id=application_idfld and active_flag='Y' and (approval_status ='Rejected' or approval_status ='Pending' ) ;
IF count_of_pending=0 THEN
						status_all_approved_in_same_level=true;
END If;

end;
end if;
-- if all have approved,  Set all level+1 records - status='Pending', return level+1 of records,


		if (authentication_typefld='o' or (authentication_typefld='a' and status_all_approved_in_same_level=true)) and (given_status='Approved' or given_status='OverrideApproved') THEN
BEGIN
UPDATE schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW"    SET  approval_status='Pending'  WHERE   stay_id=application_idfld and
    approval_level=approvel_levelfld+1  and approval_status='Default';

--get the count of next level validatiors. if there are next level validatiors, student's status will be updated to 'Pending' (from validating)
-- if there is no next level approval validators and the given status is 'Approved' the student's status will be updated to 'Approved'.
select count(candidate_id) into list_size from schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW" where
    stay_id=application_idfld and
    approval_level=approvel_levelfld+1 and approval_status='Pending';

if(list_size>0) then
update schooldev."IIT_W_CANDIDATE_STAY_REQUEST" set approval_status = 'Pending' where stay_id=application_idfld;
end if;

			if(list_size=0 or list_size=null) then
				if given_approval_notes is not null then
					status_description = ' Upon approval the validator has added a comment, please make a note of it:'||given_approval_notes;
else status_description ='' ;
end if;
				if given_status!='OverrideApproved' then
update schooldev."IIT_W_CANDIDATE_STAY_REQUEST" set modified_at=now(), approval_status = given_status, status_notes=' Your request has been Approved.'||status_description  where stay_id=application_idfld;
else
update schooldev."IIT_W_CANDIDATE_STAY_REQUEST" set modified_at=now(), approval_status = 'Approved', status_notes=' Your request has been Approved.'||status_description where stay_id=application_idfld;
end if;
end if;
					-- raise EXCEPTION 'Status%',status_all_approved_in_same_level;
for r in select id, stay_id, appointment_id, candidate_id, authority_type, approval_level, validator_email, validator_name, authentication_type,
                created_by, created_at, modified_by, modified_at, active_flag, school_id, category, approval_status
         FROM schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW"
         where stay_id=application_idfld and approval_level=approvel_levelfld+1 and approval_status='Pending'
         order by approval_level, id loop
						return next r;
end loop;
END;
END If;
end;
end if;
	--commit;

	return;

END;

$function$
;
--------------------------------------------------------------------------------------------------------------------------------------------------
select setval('schooldev.floor_id_seq', (select MAX(floor_id) + 1 from schooldev."HOSTEL_FLOOR_MASTER"));
ALTER SEQUENCE schooldev.floor_id_seq OWNED BY schooldev."HOSTEL_FLOOR_MASTER".floor_id;
ALTER SEQUENCE schooldev.course_master_id_seq OWNED BY schooldev.course_master.course_master_id ;
select setval('schooldev.course_master_id_seq', (select MAX(course_master_id) + 1 from schooldev.course_master cm));
ALTER SEQUENCE schooldev.department_id_seq OWNED BY schooldev.departments_details.department_id ;
select setval('schooldev.department_id_seq', (select MAX(department_id) + 1 from schooldev.departments_details dd));
ALTER SEQUENCE schooldev.room_id_seq OWNED BY schooldev."HOSTEL_ROOM_INFO".room_id ;
ALTER SEQUENCE schooldev.asset_category_id_seq OWNED BY schooldev."ASSET_CATEGORY_INFO".asset_category_id;
SELECT setval('schooldev.asset_category_id_seq', (SELECT MAX(asset_category_id) + 1 FROM schooldev."ASSET_CATEGORY_INFO"));
SELECT setval('schooldev.asset_id_seq', (SELECT MAX(asset_id) + 1 FROM schooldev."ASSET_INVENTORY_INFO"));
ALTER SEQUENCE schooldev.asset_id_seq OWNED BY schooldev."ASSET_INVENTORY_INFO".asset_id;
SELECT setval('schooldev."USER_FP_ACCESS_CARD_SEQ"', (SELECT MAX(access_card_serial_no::BIGINT) + 1 FROM schooldev."USER_FP_CARD"));
ALTER SEQUENCE schooldev."USER_FP_ACCESS_CARD_SEQ" OWNED BY schooldev."USER_FP_CARD".access_card_serial_no;
SELECT setval('schooldev.inventory_id_seq', (SELECT MAX(inventory_id) + 1 FROM schooldev."HOSTEL_ROOM_INVENTORY"));
ALTER SEQUENCE schooldev.inventory_id_seq OWNED BY schooldev."HOSTEL_ROOM_INVENTORY".inventory_id;
SELECT setval('schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST_request_id_seq"', (SELECT MAX(request_id) + 1 FROM schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST"));
ALTER SEQUENCE schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST_request_id_seq" OWNED BY schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST".request_id;
SELECT setval('schooldev.mess_rebate_id_seq', (SELECT MAX(id) + 1 FROM schooldev."IIT_A_MESS_REBATE"));
ALTER SEQUENCE schooldev.mess_rebate_id_seq OWNED BY schooldev."IIT_A_MESS_REBATE".id;
SELECT setval('schooldev.mess_rebate_workflow_id_seq', (SELECT MAX(id) + 1 FROM schooldev."IIT_A_MESS_REBATE_WORKFLOW"));
ALTER SEQUENCE schooldev.mess_rebate_workflow_id_seq OWNED BY schooldev."IIT_A_MESS_REBATE_WORKFLOW".id;
SELECT setval('schooldev.file_id_seq', (SELECT MAX(file_id) + 1 FROM schooldev."IIT_W_STUDENT_FILES_INFORMATION"));
ALTER SEQUENCE schooldev.file_id_seq OWNED BY schooldev."IIT_W_STUDENT_FILES_INFORMATION".file_id;
select setval('schooldev.menu_list_menu_id_seq', (select MAX(menu_id) + 1 from schooldev."MENU_LIST"));
ALTER SEQUENCE schooldev.menu_list_menu_id_seq OWNED BY schooldev."MENU_LIST".menu_id;
SELECT setval('schooldev."IIT_WD_ROLE_TAB_SETTINGS_id_seq"', (SELECT MAX(id) FROM schooldev."IIT_WD_ROLE_TAB_SETTINGS"));
SELECT setval('schooldev."IIT_W_CANDIDATE_FILES_INFORMATION_file_id_seq"', (SELECT MAX(file_id) FROM schooldev."IIT_W_CANDIDATE_FILES_INFORMATION"));
ALTER TABLE schooldev."IIT_W_CANDIDATE_FILES_INFORMATION" ALTER COLUMN file_id SET DEFAULT nextval('schooldev."IIT_W_CANDIDATE_FILES_INFORMATION_file_id_seq"');
select setval('schooldev.roomallotment_id_seq', (select MAX(room_allotment_id) + 1 from schooldev."HOSTEL_ROOM_ALLOTMENT_INFO"));
ALTER SEQUENCE schooldev.roomallotment_id_seq OWNED BY schooldev."HOSTEL_ROOM_ALLOTMENT_INFO".room_allotment_id;
select setval('schooldev.roomallotment_id_seq', (select MAX(room_allotment_id) + 1 from schooldev."HOSTEL_ROOM_ALLOTMENT_INFO"));
ALTER SEQUENCE schooldev.roomallotment_id_seq OWNED BY schooldev."HOSTEL_ROOM_ALLOTMENT_INFO".room_allotment_id;
SELECT setval('schooldev.IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE_SEQ', (SELECT MAX(id) + 1 FROM schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE"));
ALTER SEQUENCE schooldev.IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE_SEQ OWNED BY schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE".id;
SELECT setval('schooldev.IIT_PS_TEMP_ACCOM_LEDGER_A_SEQ', (SELECT coalesce(max(to_number(voucher_no, '99999999')), 1) FROM schooldev."IIT_PS_TEMP_ACCOM_LEDGER_A")::bigint);
ALTER SEQUENCE schooldev.IIT_PS_TEMP_ACCOM_LEDGER_A_SEQ OWNED BY schooldev."IIT_PS_TEMP_ACCOM_LEDGER_A".voucher_no;

SELECT setval('schooldev.WARDEN_INFO_SEQ', (SELECT MAX(id) + 1 FROM schooldev."WARDEN_INFO"));
ALTER SEQUENCE schooldev.WARDEN_INFO_SEQ OWNED BY schooldev."WARDEN_INFO".id;

ALTER TABLE schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST" ALTER COLUMN request_id SET DEFAULT nextval('schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST_request_id_seq"');

--Alter, Add Constraints
ALTER TABLE schooldev."HOSTEL_FLOOR_MASTER" ADD CONSTRAINT "FOREIGN_KEY_HOSTEL_ID" FOREIGN KEY (hostel_id) REFERENCES schooldev."HOSTEL_MASTER"(hostel_id);
ALTER TABLE schooldev."MESS_TERMINAL" ADD CONSTRAINT "FOREIGN_KEY_MESS_ID" FOREIGN KEY (mess_master_id) REFERENCES schooldev."MESS_MASTER"(mess_master_id);
CREATE INDEX "IDX_FACULTY_PERSONAL_DETAILS_EMAIL" ON schooldev."FACULTY_PERSONAL_DETAILS" USING btree (email_address);
ALTER TABLE schooldev."FACULTY_DEPARTMENT_ALLOCATION_INFO" ADD CONSTRAINT "Faculty_Personal_Foreign_Key" FOREIGN KEY (faculty_id) REFERENCES schooldev."FACULTY_PERSONAL_DETAILS"(faculty_id);
ALTER TABLE schooldev.dashboard_widget_privilege ADD CONSTRAINT dashboard_widget_privilege_dashboard_id_fk FOREIGN KEY (dashboard_menu_id) REFERENCES schooldev."MENU_LIST"(menu_id);
ALTER TABLE schooldev.dashboard_widget_privilege ADD CONSTRAINT dashboard_widget_privilege_widget_id_fk FOREIGN KEY (widget_id) REFERENCES schooldev.dashboard_widget_master(id);

UPDATE schooldev."USER_MANAGEMENT" SET role_id = null WHERE role_id = 0;
update schooldev."STUDENT_BIO_DATA_FORM_DETAILS" set pan_number = null where pan_number = '';
update schooldev."STUDENT_BIO_DATA_FORM_DETAILS" set student_personal_email = null where student_personal_email = '';
update schooldev."STUDENT_BIO_DATA_FORM_DETAILS" set faculty_email = null where faculty_email = '';
UPDATE schooldev."STUDENT_BIO_DATA_FORM_DETAILS" SET faculty_name = '0' WHERE faculty_name IS NULL;
UPDATE schooldev."STUDENT_BIO_DATA_FORM_DETAILS" SET category = 'OBC' WHERE category = 'obc';
UPDATE schooldev."STUDENT_BIO_DATA_FORM_DETAILS" SET category = 'General' WHERE category = 'general';
UPDATE schooldev."STUDENT_BIO_DATA_FORM_DETAILS" SET category = 'SC/ST' WHERE category IN ('SC / ST', 'scOrSt');

UPDATE schooldev."STUDENT_BIO_DATA_FAMILY_INFO" SET relation_type = 'Spouse' WHERE relation_type = 'Husband' OR relation_type = 'Wife';

UPDATE schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST" SET donation_amount = 0 WHERE donation_amount IS NULL;

update schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" set bulk_coupon = false where bulk_coupon is null;

UPDATE schooldev."IIT_HDC_COMPLAINT_FORMDETAILS" SET created_by = um.user_id FROM schooldev."USER_MANAGEMENT" um WHERE "IIT_HDC_COMPLAINT_FORMDETAILS".created_by = um.user_name;

UPDATE schooldev."IIT_HDC_COMPLAINT_FORMDETAILS" SET modified_by = um.user_id FROM schooldev."USER_MANAGEMENT" um WHERE "IIT_HDC_COMPLAINT_FORMDETAILS".modified_by = um.user_name;

UPDATE schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_TRANSACTION_DETAILS" SET transaction_ref_number = NULL WHERE transaction_ref_number = 'null';
UPDATE schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_TRANSACTION_DETAILS" SET payment_method = NULL WHERE payment_method = 'null';
UPDATE schooldev."USER_MANAGEMENT" SET role_id = 2 WHERE account_type = 'Student';

UPDATE schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" set no_of_snacks_coupons=0 where no_of_snacks_coupons is null;
UPDATE schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" set snacks_coupon_rate=0 where snacks_coupon_rate is null;
UPDATE schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" set config_discounted_amount=0 where config_discounted_amount is null;
UPDATE schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" set total_discounted_amount=0 where total_discounted_amount is null;

update schooldev."STUDENT_MESS_DETAILS" smd set mmc_id = (
    select mmc.id from schooldev."MESS_MASTER_CONTROLLER" mmc
    where smd.from_date between mmc.dining_from_date and mmc.dining_to_date) where smd.mmc_id is null;

UPDATE schooldev."GUEST_ACCOMMODATION_REQUEST" SET allocation_status='Allotted' WHERE allocation_status='Alloted';

UPDATE schooldev."mess_inspection_report" SET created_by = um.user_id
    FROM schooldev."USER_MANAGEMENT" um
WHERE "mess_inspection_report".created_by = um.user_name;

UPDATE schooldev."mess_inspection_report" SET modified_by = um.user_id
    FROM schooldev."USER_MANAGEMENT" um
WHERE "mess_inspection_report".modified_by = um.user_name;

INSERT INTO schooldev."PAYMENT_GATEWAY_CONFIGURATION_CCAV"
(url, access_code, working_key,instance,action_url)
VALUES('https://ikollege.com/hosteldine', 'ATPT05LI96CE40TPEC', '17927792515A2FF0C8ABB7CC30317335','testing','https://test.ccavenue.com/transaction/transaction.do?command=initiateTransaction');
INSERT INTO schooldev."PAYMENT_GATEWAY_CONFIGURATION_CCAV"
(url, access_code, working_key,instance,action_url)
VALUES('http://ccw.triesten.com/hosteldine', 'ATUA06MA14CL09AULC', '353A9F97570F97A486ED36478EC858CF','testing','https://test.ccavenue.com/transaction/transaction.do?command=initiateTransaction');
INSERT INTO schooldev."PAYMENT_GATEWAY_CONFIGURATION_CCAV"
(url, access_code, working_key,instance,action_url)
VALUES('https://ikollege.iitm.ac.in/iitmhostel', 'AVEF77FD50BJ62FEJB', 'A2B649FFB542A42A92311FB007F2D66B','production','https://secure.ccavenue.com/transaction/transaction.do?command=initiateTransaction');
INSERT INTO schooldev."PAYMENT_GATEWAY_CONFIGURATION_CCAV"
(url, access_code, working_key,instance,action_url)
VALUES('https://dost.iitm.ac.in/iitmhostel', 'AVEF77FD50BJ63FEJB', '84855490822AD8C6C05690BD22CE193B','production','https://secure.ccavenue.com/transaction/transaction.do?command=initiateTransaction');
INSERT INTO schooldev."PAYMENT_GATEWAY_CONFIGURATION_CCAV"
(url, access_code, working_key,instance,action_url)
VALUES('https://hosteldine.iitm.ac.in/iitmhostel', 'AVEF77FD50BJ63FEJB', '5A23848EC2F17CDD67E1D1A6EB59445F','production','https://secure.ccavenue.com/transaction/transaction.do?command=initiateTransaction');

--Primary key constraints for existing tables
ALTER TABLE schooldev."FEEDBACK_QUALIFIER" ADD CONSTRAINT feed_qual_pkey PRIMARY KEY (feedback_id);
ALTER TABLE schooldev."FEEDBACK_QUESTION" ADD CONSTRAINT feed_ques_pkey PRIMARY KEY (feedback_quesid);
ALTER TABLE schooldev.roles ADD CONSTRAINT roles_ukey UNIQUE (role_id);
ALTER TABLE schooldev."MENU_LIST" ADD CONSTRAINT "MENU_LIST_ukey" UNIQUE (menu_id);

update schooldev."IIT_W_WORKFLOW_MASTER" set category ='CCW' where category ='DOST';
update schooldev."IIT_W_WORKFLOW_MASTER" set validator_name  ='CCW' where id=37;

UPDATE schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST"
SET approval_status = 'Allotted'
WHERE approval_status IN ('Alloted', 'Alotted');

UPDATE schooldev."IIT_W_CANDIDATE_STAY_REQUEST"
SET approval_status = 'Allotted'
WHERE approval_status IN ('Alloted', 'Alotted');

ALTER TABLE schooldev."IITM_GUEST_COUPON_ONLINE_PAYMENT_TRANSACTIONS" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."FOOD_COURT_LEDGER" ALTER COLUMN school_id set default(1);
UPDATE schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST" SET category='OTHERS' where category='OTHER';
--select * from schooldev.update_all_student_current_balance();
UPDATE schooldev."IIT_W_MAIL_TEMPLATE"
SET mail_template = '<h6><<mailBodyHeader>> <<studentName>></h6>
    <div class="container mt-4">
        <div class="card shadow">
            <div class="card-header bg-secondary text-white">
                <h5 class="mb-0 text-center">Student Accommodation Details</h5>
            </div>
            <div class="card-body">
                <div class="row mb-3">
                    <div class="col-lg-6"> <strong>Student Name: </strong> <span>
                            <<studentName>>
                        </span> </div>
                    <div class="col-lg-6"> <strong>Student ID: </strong> <span>
                            <<studentId>>
                        </span> </div>
                </div>
                <div class="row mb-3">
                    <div class="col-lg-12"> <strong>Email ID: </strong> <span>
                            <<emailID>>
                        </span> </div>
                </div>
                <div class="row mb-3">
                    <div class="col-lg-6"> <strong>Address: </strong> <span>
                            <<address>>
                        </span> </div>
                    <div class="col-lg-6"> <strong>Contact Number: </strong> <span>
                            <<contactNumber>>
                        </span> </div>
                </div>
                <div class="row mb-3">
                    <div class="col-lg-6"> <strong>Appointment / Stay Extn From Date: </strong> <span>
                            <<fromDate>>
                        </span> </div>
                    <div class="col-lg-6"> <strong>Appointment / Stay Extn To Date: </strong> <span>
                            <<toDate>>
                        </span> </div>
                </div>
                <div class="row mb-3">
                    <div class="col-lg-6"> <strong>Category: </strong> <span>
                            <<category>>
                        </span> </div>
                    <div class="col-lg-6"> <strong>Purpose: </strong> <span>
                            <<purpose>>
                        </span> </div>
                </div>
            </div>
        </div>
    </div>
    <p class="mt-4" id="emailTemplateButtons">
        <<approveRejectButton>>
        <a href="#%viewUrl%#" class="btn btn-sm btn-primary button mt-1 view" target="_blank"><i class="fa-solid fa-eye"></i> View Details</a>
    </p>'
WHERE mail_type = 'ScholarOthersBodyTemplate';
