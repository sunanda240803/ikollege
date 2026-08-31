update schooldev."STUDENT_BIODATA_FORM_DETAILS"
set active_flag = 'A', modified_by = 'sashi kiran', modified_at = now()
where biodata_id in (10598, 11106, 11480, 11904, 12352, 12954, 13104, 1422, 3529, 6571, 7318, 8402, 9165, 9508, 244, 6433, 6846, 8949, 9244, 10883) insert
into schooldev.course_master (course_master_id,course_master_name,description,degree_awarded,affiliation,
created_by, modified_by, created_at,modified_at,active_status, school_id, display_count, department_id, course_master_head)
values (0, 'BE', 'test', '', '1', now(), 1, now(), 'Y', '1', 0, 0, 0, 'BE');

update  schooldev."FACULTY_PERSONAL_DETAILS" set employee_id  = nextval('schooldev."FACULTY_PERSONAL_DETAILS_n_fpd_employee_id_seq"'::regclass) where employee_id = 0;



------------------------------------------------------------------------------------------------------

WITH active_secondary_roles AS (
    SELECT
        sr.user_id,
        sr.role_id,
        um.role_id AS primary_role_id,
        um.secondary_role_id AS old_secondary,
        ROW_NUMBER() OVER (PARTITION BY sr.user_id ORDER BY sr.role_id) AS rn
    FROM
        schooldev."SECONDARY_ROLES" sr
            JOIN
        schooldev."USER_MANAGEMENT" um
        ON sr.user_id = um.user_id and um.active_flag='Y'
    WHERE
        sr.active_flag = 'Y'
),
     grouped_roles AS (
         SELECT
             user_id,
             ARRAY_AGG(role_id ORDER BY role_id) AS active_roles,
             MAX(primary_role_id) AS role_id
         FROM
             active_secondary_roles
         GROUP BY
             user_id
         HAVING
             COUNT(*) = 2
     ),
     role_to_update AS (
         SELECT
             gr.user_id,
             -- Pick the one that does NOT match primary role
             CASE
                 WHEN gr.active_roles[1] = gr.role_id THEN gr.active_roles[2]
                 ELSE gr.active_roles[1]
                 END AS new_secondary_role_id
         FROM
             grouped_roles gr
     )
UPDATE schooldev."USER_MANAGEMENT" um
SET secondary_role_id = rtu.new_secondary_role_id
    FROM role_to_update rtu
WHERE um.user_id = rtu.user_id;
--SELECT * FROM role_to_update;

-- Auto-generated SQL script #202512131159
UPDATE schooldev."IIT_W_WORKFLOW_MASTER"
	SET authentication_type='i'
	WHERE id=9;


------------------------------------------------------------------------------------------------------
ALTER TABLE schooldev."IITM_CONVOCATION_ACCOMMODATION" ALTER COLUMN overall_amount TYPE float8 USING overall_amount::float8;
ALTER TABLE schooldev."IITM_CONVOCATION_ACCOMMODATION" ADD hostel_name varchar(64) NULL;
ALTER TABLE schooldev."IITM_CONVOCATION_ACCOMMODATION" ADD retry_count int4 NULL;
ALTER TABLE schooldev."IITM_CONVOCATION_ACCOMMODATION" ALTER COLUMN payment_date TYPE timestamp USING payment_date::timestamp;



INSERT INTO schooldev."IIT_W_MAIL_TEMPLATE"
(mail_type, mail_subject, mail_template, description, active_flag, created_by, created_at, modified_by, modified_at, category, approval_level, authority_type)
VALUES('Convocation_Purchase_Confirmation', 'Convocation Purchase Confirmation', '<p style="font-size: 14px; color: #636363;">You have successfully purchased Convocation Coupons. Given below are the details.</p>

<table style="width: 100%; border: 1px solid #ddd; border-collapse: collapse; font-size: 13px; margin-top: 15px;">
    <tbody><tr style="background-color: #e9ecef;">
        <th style="padding: 8px; text-align: center; font-weight: bold;">Purchase Date</th>
        <th style="padding: 8px; text-align: center; font-weight: bold;">Student Id</th>
        <th style="padding: 8px; text-align: center; font-weight: bold;">Accommodation Status</th>
        <th style="padding: 8px; text-align: center; font-weight: bold;">Complementary</th>
        <th style="padding: 8px; text-align: center; font-weight: bold;">Additional Coupon</th>
        <th style="padding: 8px; text-align: center; font-weight: bold;">Total Amount</th>
    </tr>
    <tr>
        <td style="padding: 8px; text-align: center;">#%purchaseDate%#</td>
        <td style="padding: 8px; text-align: center;">#%studentId%#</td>
        <td style="padding: 8px; text-align: center;">#%accommStatus%#</td>
        <td style="padding: 8px; text-align: center;">#%complementary%#</td>
        <td style="padding: 8px; text-align: center;">#%additionalCoupon%#</td>
        <td style="padding: 8px; text-align: center;">#%totalAmount%#</td>
    </tr>
</tbody></table>', 'Convocation_Purchase_Confirmation', 'Y', 'ST00229', now(), 'ST00229', 'now()', '', NULL, '');

ALTER TABLE schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST" ALTER COLUMN donation_status SET DEFAULT false;


-- schooldev."STUDENT_SEARCH_VIEW" source

CREATE OR REPLACE VIEW schooldev."STUDENT_SEARCH_VIEW_WITH_SETTLEMENT"
AS SELECT ( SELECT dost_election_department.dept_name
            FROM schooldev.dost_election_department
            WHERE dost_election_department.dept_code::text = "substring"("STUDENT_DETAILS_INFO".student_id::text, 1, 2) OR "substring"("STUDENT_DETAILS_INFO".student_id::text, 1, 2) = dost_election_department.alt_dept_code::text) AS dept_name,
    "STUDENT_DETAILS_INFO".student_id,
    COALESCE("STUDENT_BIO_DATA_FORM_DETAILS".student_name, concat("STUDENT_DETAILS_INFO".first_name, ' ', "STUDENT_DETAILS_INFO".last_name)::character varying(120)) AS student_name,
        CASE
            WHEN "HOSTEL_ROOM_ALLOTMENT_INFO".student_id IS NOT NULL AND ("HOSTEL_ROOM_ALLOTMENT_INFO".vacate_date IS NULL OR "HOSTEL_ROOM_ALLOTMENT_INFO".is_missing = true) AND ("HOSTEL_ROOM_ALLOTMENT_INFO".shifted_date IS NULL OR "HOSTEL_ROOM_ALLOTMENT_INFO".is_missing = true) THEN 'active'::character varying
            ELSE NULL::character varying
END AS student_status,
        CASE
            WHEN "HOSTEL_ROOM_ALLOTMENT_INFO".is_missing = false THEN "HOSTEL_MASTER".hostel_name
            ELSE NULL::character varying
END AS hostel_name,
        CASE
            WHEN "HOSTEL_ROOM_ALLOTMENT_INFO".is_missing = false THEN "HOSTEL_MASTER".hostel_id
            ELSE NULL::bigint
END AS hostel_id,
    "HOSTEL_FLOOR_MASTER".floor_name,
    "HOSTEL_FLOOR_MASTER".floor_id,
        CASE
            WHEN "HOSTEL_ROOM_ALLOTMENT_INFO".is_missing = false THEN "HOSTEL_ROOM_INFO".room_no
            ELSE NULL::character varying
END AS room_number,
    "HOSTEL_ROOM_ALLOTMENT_INFO".sub_room_id AS seat,
    "HOSTEL_ROOM_INFO".room_id,
    "HOSTEL_ROOM_ALLOTMENT_INFO".room_allotment_id,
    "HOSTEL_ROOM_ALLOTMENT_INFO".vacate_date,
    "HOSTEL_ROOM_ALLOTMENT_INFO".shifted_date,
    "CURRENT_MESS_DETAILS_VIEW".mess_period_id,
    "CURRENT_MESS_DETAILS_VIEW".dining_from_date,
    "CURRENT_MESS_DETAILS_VIEW".dining_to_date,
    "CURRENT_MESS_DETAILS_VIEW".mess_id,
    "CURRENT_MESS_DETAILS_VIEW".mess_name,
    "STUDENT_BIO_DATA_FORM_DETAILS".mess_name AS mess_preference,
    "STUDENT_BIO_DATA_FORM_DETAILS".bio_data_id,
    "STUDENT_BIO_DATA_FORM_DETAILS".application_number,
    "STUDENT_DETAILS_INFO".gender,
    "STUDENT_BIO_DATA_FORM_DETAILS".dob,
    "STUDENT_BIO_DATA_FORM_DETAILS".blood_group,
    "STUDENT_BIO_DATA_FORM_DETAILS".category,
    "STUDENT_BIO_DATA_FORM_DETAILS".student_mobile,
    "STUDENT_BIO_DATA_FORM_DETAILS".student_personal_email,
    concat(TRIM(BOTH FROM "STUDENT_DETAILS_INFO".student_id), ( SELECT scd.config_value
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
            WHEN "STUDENT_BIO_DATA_FORM_DETAILS".pwd_percentage IS NOT NULL AND "STUDENT_BIO_DATA_FORM_DETAILS".pwd_percentage > 0 THEN true
            ELSE false
END AS pwd_status,
    "STUDENT_BIO_DATA_FORM_DETAILS".pwd,
    "STUDENT_BIO_DATA_FORM_DETAILS".pwd_percentage,
    "STUDENT_DETAILS_INFO".settlement_flag,
    "STUDENT_DETAILS_INFO".day_scholar,
    "STUDENT_DETAILS_INFO".vacation_category,
    "HOSTEL_ROOM_ALLOTMENT_INFO".is_missing,
    "HOSTEL_MASTER".hostel_office_email,
    "USER_MANAGEMENT".authentication_server AS auth,
    "STUDENT_DETAILS_INFO".active_flag
   FROM schooldev."STUDENT_DETAILS_INFO"
     LEFT JOIN schooldev."STUDENT_BIO_DATA_FORM_DETAILS" ON "STUDENT_DETAILS_INFO".student_id::text = "STUDENT_BIO_DATA_FORM_DETAILS".student_id::text AND "STUDENT_BIO_DATA_FORM_DETAILS".active_flag = 'Y'::bpchar
     LEFT JOIN schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" ON "STUDENT_DETAILS_INFO".student_id::text = "HOSTEL_ROOM_ALLOTMENT_INFO".student_id::text AND "HOSTEL_ROOM_ALLOTMENT_INFO".active_flag = 'Y'::bpchar AND "HOSTEL_ROOM_ALLOTMENT_INFO".vacate_date IS NULL AND "HOSTEL_ROOM_ALLOTMENT_INFO".shifted_date IS NULL
     LEFT JOIN schooldev."HOSTEL_ROOM_INFO" ON "HOSTEL_ROOM_INFO".room_id = COALESCE("HOSTEL_ROOM_ALLOTMENT_INFO".room_id, 0) AND "HOSTEL_ROOM_INFO".active_flag = 'Y'::bpchar
     LEFT JOIN schooldev."HOSTEL_FLOOR_MASTER" ON "HOSTEL_FLOOR_MASTER".floor_id = COALESCE("HOSTEL_ROOM_ALLOTMENT_INFO".building_id, 0) AND "HOSTEL_FLOOR_MASTER".active_flag::text = 'Y'::text
     LEFT JOIN schooldev."HOSTEL_MASTER" ON COALESCE("HOSTEL_FLOOR_MASTER".hostel_id, 0::bigint) = "HOSTEL_MASTER".hostel_id AND "HOSTEL_MASTER".active_flag = 'Y'::bpchar
     LEFT JOIN schooldev."COURSE_ALLOCATION_INFO" ON "COURSE_ALLOCATION_INFO".student_id::text = "STUDENT_DETAILS_INFO".student_id::text AND "COURSE_ALLOCATION_INFO".active_flag::text = 'Y'::text
     LEFT JOIN schooldev.course_master ON "COURSE_ALLOCATION_INFO".course_id = course_master.course_master_id AND course_master.active_flag = 'Y'::bpchar
     LEFT JOIN schooldev."CURRENT_MESS_DETAILS_VIEW" ON "CURRENT_MESS_DETAILS_VIEW".student_id::text = "STUDENT_DETAILS_INFO".student_id::text
     LEFT JOIN schooldev."USER_MANAGEMENT" ON "USER_MANAGEMENT".user_id::text = "STUDENT_DETAILS_INFO".student_id::text AND "USER_MANAGEMENT".active_flag::text = 'Y'::text;


-- schooldev."ALL_STUDENTS_DETAILS_VIEW" source

CREATE OR REPLACE VIEW schooldev."ALL_STUDENTS_DETAILS_VIEW_WITH_SETTLEMENT"
AS SELECT dept_name,
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
   FROM schooldev."STUDENT_SEARCH_VIEW_WITH_SETTLEMENT"
   WHERE active_flag = 'Y'::bpchar;