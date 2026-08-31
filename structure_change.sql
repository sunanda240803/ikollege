--------------------------Dec 10 2025 (Sanjay)-----------------------------------
create function search_students_hostel(sstatus character varying, scategory character varying, app_from character varying, app_to character varying, sty_from character varying, sty_to character varying, sname character varying, sid character varying, svname character varying, svemail character varying, logintype character varying, submitted_from character varying, submitted_to character varying, approval_from character varying, approval_to character varying, facilityid integer, loginid character varying, tab integer, current_stay_flag character varying) returns SETOF schooldev.student_record_result_hostel
    language plpgsql
as
$$
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
        if(tab=2) then scategory='outsidecampus,insidecampus,sasthra,other,stustayextension'; END IF;
        if (tab=10) then scategory='SCHOLAR'; END IF;
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
              and (case when (logintype='Hostel Check In')
                            then (case when (hostel.hostel_id is null)
                                           then a.hostel_id::text in((select cat from regexp_split_to_table(facility_ids, ',')as cat))
                                       else hostel.hostel_id::text in ((select cat from regexp_split_to_table(facility_ids, ',')as cat)) end)
                        else 1=1 end )
              and case when lower(logintype) like '%office' then a.status = 'Approved'
                       when (logintype='Warden') then a.status in ('Allotted')
                       when (logintype = 'Hostel Check In') then a.status in ('Pending','Validating','Allotted','Approved')
                       else wrk.status in ('Pending','Default') and a.status<>'Rejected' end
              and (case when (scategory is not null and scategory<>'')
                            then lower(a.category) in (select lower(cat) from regexp_split_to_table(scategory, ',')as cat)
                        else 1=1 end )
              and a.created_at >=(now()::date-90)
              and case when lower(logintype) like '% office'
                           then (hostel.room_allotment_id is null or hostel.room_allotment_id=0) else 1=1 end
              and case when (lower(logintype)='Warden' or lower(logintype) = 'Hostel Check In')
                           then (hostel.room_allotment_id>0) else 1=1 end
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
        if(tab=2) then if(scategory is null or scategory='') then scategory='outsidecampus,insidecampus,sasthra,other,stustayextension'; end if; END IF;
        if (tab=10) then if(scategory is null or scategory='') then scategory='SCHOLAR'; end if; END IF;
--if (tab=18) then if(scategory is null or scategory='') then scategory='stustayextension'; end if; END IF;
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
                             (case when lower(logintype) like '% office' then a.status in ('Approved','Allotted', 'CheckedIn', 'CheckedOut')
                                   when (logintype='Warden') then a.status in ('Allotted', 'CheckedIn', 'CheckedOut')
                                   when (logintype='Hostel Check In') then a.status in ('Allotted', 'CheckedIn', 'CheckedOut','Validating','Pending','Approved')
                                   else a.status not in ('Deleted','Cancelled') end) end)
                or (case when (sstatus is not null and sstatus<>'') then wrk.status in (sstatus)
                         else
                             (case when lower(logintype) like '% office' then wrk.status in ('Approved')
                                   when ( logintype='Warden') then a.status in ('Allotted', 'CheckedIn', 'CheckedOut')
                                   when (logintype = 'Hostel Check In')then a.status in('Allotted', 'CheckedIn', 'CheckedOut','Validating','Pending','Approved')
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
                        when current_stay_flag='todayCheckOut' then  (hostel.room_allotment_id>0) and hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) and  stay_to_date=now()::date

                        when current_stay_flag='pendingCheckout' then   hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) and  stay_to_date>=now()::date-30
                        when current_stay_flag='vacatingLink' then   hostel.hostel_id::text in (select cat from regexp_split_to_table(facility_ids, ',')as cat) and  stay_to::date=now()::date

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
drop type schooldev.student_record_result_hostel cascade;
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

select * from schooldev.search_students_hostel(NULL,NULL,'null','null','null','null',NULL,'',NULL,NULL,'CCW DEAN','null','null','null','null','0','ccw.iitm','2',NULL) as result
select * from schooldev.search_students_hostel('null''null','null','null','null','null','null','','null',null,null,'Warden','null','null','null','null',0,'wardencauvery','2',null) as result
select * from schooldev.search_students_hostel(null ,NULL,'null','null','null','null',NULL,'',NULL,NULL,'Hostel Check In','null','null','null','null',0,'ganga.hostel',2,NULL)
*/
$$;

alter function search_students_hostel(varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar, integer, varchar, integer, varchar) owner to postgres;




--------------------------total summary count-----------------------------------------------------------------------------
create function total_summary_count(para_user_name character varying) returns SETOF count_result
    language plpgsql
as
$$
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
                       sum( case when (a.checkin_checkout_status in ('Allotted','CheckedIn')) then 1
                                 else 0 end ) as  alloted_total,

                       sum( case when (a.checkin_checkout_status='CheckedIn') then 1
                                 else 0 end ) as checked_in,

                       sum( case when (a.checkin_checkout_status='Allotted') then 1
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
$$;

alter function total_summary_count(varchar) owner to postgres;
------------------------------End-------------------------------------------------




-- DROP FUNCTION schooldev.student_vacating_hostel(varchar, varchar, varchar, varchar, varchar, int4, varchar, varchar, varchar, varchar, int4, varchar, varchar);

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
where lower(h.user_name)=lower(username) and a.school_id=1 and a.active_flag='Y' order by a.created_at desc
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
order by  a.created_at desc
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
where (lower(h.ldap_username)=lower(username) or lower(h.associate_ldap_username)=lower(username)) and a.school_id='1' and a.active_flag='Y' and wrk.status<>'AutoApproved' order by a.created_at desc
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
order by a.created_at desc
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

----------------------------------------------------------------------------------------------------------------------------------------------

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
order by a.created_at::date desc
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
					 order by a.created_at::date desc
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
order by a.created_at::date desc
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
					 order by a.created_at::date desc
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
order by a.created_at::date desc
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
				 order by a.created_at::date desc
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

-----------------------------Dec 11 2025(sanjay)----------------------------------
UPDATE schooldev."IIT_W_MAIL_TEMPLATE"
SET mail_template = e'<p></p>
<p>The following student has been submitted for the Sick Food Request</p>
<p>StudentId: <span style="font-weight:normal;">
        <<student_id>>
    </span><br>Medical Reason: <span style="font-weight:normal;">
        <<medical_reason>>
    </span><br>Mobile Number: <span style="font-weight:normal;">
        <<mobile_number>>
    </span><br>Request Date: <span style="font-weight:normal;">
        <<request_date>>
    </span><br>Mess Session: <span style="font-weight:normal;">
        <<mess_session>>
    </span></p>
<p></p>'
WHERE mail_type = 'SickFoodRequest';
------------------------------End-------------------------------------------------


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


------------------------------------------------END------------------------------------------------

-------------------------------------------------Dec 18 2025 (Sanjay)---------------------------------
INSERT INTO schooldev."IIT_W_MAIL_TEMPLATE" (mail_type, mail_subject, mail_template, description, active_flag,
                                             created_by, created_at, modified_by, modified_at, category, approval_level,
                                             authority_type)
VALUES ('CandidateReject', 'IIT-M hostel management - Accommodation or Stay Ext. request approval status',
        '<p style="background-color: rgb(255, 255, 255); margin-top: 10px;">Your
        request for hostel accommodation/stay extension dated #%submittedDate%# has been rejected by #%authorityType%#<br>
        reason : #%rejectReason%#.</p>', 'IIT-M hostel management - Accommodation or Stay Ext. request approval status', 'Y', 'triesten',
        '2018-04-17 15:35:21.665000', 'triesten', '2024-04-29 00:01:29.887000', 'Reject', 1, 'Candidate');
------------------------------------------------End------------------------------------------------------

-------------------------------------------------Dec 19 2025 (Sanjay)---------------------------------

INSERT INTO schooldev."IIT_W_MAIL_TEMPLATE" (mail_type, mail_subject, mail_template, description, active_flag,
                                             created_by, created_at, modified_by, modified_at, category, approval_level,
                                             authority_type)
VALUES ('StudentAccommodationApproveRejectButton', 'IIT-M hostel management - Accommodation request approval buttons', e'<a href="#%approveUrl%#" class="btn btn-sm btn-success button mt-1 approve" target="_blank"><i
        class="fa-solid fa-check-circle me-1"></i> Approve Request</a>
<a href="#%rejectUrl%#" class="btn btn-sm btn-danger button mt-1 reject" target="_blank"><i
        class="fa-solid fa-circle-xmark me-1"></i> Reject Request</a>',
        'IIT-M hostel management - Accommodation request approval buttons', 'Y', 'triesten',
        '2018-06-27 11:23:40.070000', 'triesten', '2018-06-28 16:06:00.132000', 'SCHOLAR', 1, 'Student');
--------------------------------------------------------------END-----------------------------------------------------------------------------------------------------------

ALTER TABLE schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST" ADD manual_update varchar NULL;

-------------------------------------------------Dec 23 2025 (Sanjay)---------------------------------
UPDATE schooldev."IIT_WD_DASHBOARD_TAB_MASTER"
SET url = 'resendMail'
WHERE id =
      (select id from schooldev."IIT_WD_DASHBOARD_TAB_MASTER"
       where tab_name = 'Re-send Mail' and parent_id = (select id from schooldev."IIT_WD_DASHBOARD_TAB_MASTER" where tab_name = 'Other Candidate'));
--------------------------------------------------------------END-----------------------------------------------------------------------------------------------------------


ALTER TABLE schooldev."SHAASTRA_SAARANG_PURCHASE_CLAIM" ALTER COLUMN school_id SET DEFAULT 1;
ALTER TABLE schooldev."SHAASTRA_SAARANG_PURCHASE_CLAIM" RENAME COLUMN active_status TO active_flag;

drop view schooldev.event_purcharse_details_view;
-- schooldev.event_purcharse_details_view source
CREATE OR REPLACE VIEW schooldev.event_purcharse_details_view
AS SELECT v_sdi_studentid,
          id,
          event_name,
          sum(discount) - sum(claim) AS purchased_amt
   FROM ( SELECT a.v_sdi_studentid,
                 a.id,
                 sum(a.discount) AS discount,
                 sum(a.claim_amt) AS claim,
                 a.event_name
          FROM ( SELECT ssds.student_id AS v_sdi_studentid,
                        sem.id,
                        sum(ssds.discount_amount) AS discount,
                        0 AS claim_amt,
                        sem.event_name
                 FROM schooldev."SHOW_EVENT_MASTER" sem
                          JOIN schooldev."SHOW_MASTER" sm ON sem.id = sm.event_id
                          JOIN schooldev."SHOW_SEAT_DETAILS" ssd ON sm.id = ssd.show_id
                          JOIN schooldev."SHOW_STUDENT_DETAILS" ssds ON ssd.id = ssds.seat_id
                 where ssds.active_flag='Y'
                 GROUP BY sem.id, ssds.student_id, sem.event_name
                 UNION
                 SELECT sspc.student_id AS v_sdi_studentid,
                        sspc.event_id,
                        0 AS discount,
                        sum(sspc.amount) AS claim,
                        s2.event_name
                 FROM schooldev."SHAASTRA_SAARANG_PURCHASE_CLAIM" sspc
                          left join schooldev."SHOW_EVENT_MASTER" s2 on (sspc.event_id=s2.id)
                 GROUP BY sspc.event_id, sspc.student_id,s2.event_name) a
          GROUP BY a.id, a.v_sdi_studentid, a.event_name) ab
   GROUP BY v_sdi_studentid, id,event_name;

ALTER TABLE schooldev."MESS_MASTER" ADD is_veg_nonveg varchar(16) NULL;

-------------------------------------Dec 30 2025 (Sanjay) --------------------------------------------
INSERT INTO schooldev."IIT_WD_DASHBOARD_TAB_MASTER"
(parent_id, tab_type, tab_name, property, icon, url, sort, mandatory, active_flag, created_by, created_at, modified_by, modified_at, action, order_by, tab_url, style)
VALUES ((select id from schooldev."IIT_WD_DASHBOARD_TAB_MASTER" where tab_name = 'Other Candidate' and active_flag = 'Y'), 'Col-Action', 'CheckBox', 'checkbox', 'fa-solid fa-trash-can', '', null, null,
        'Y', 'admin', '2018-03-19 15:43:01.630390', 'admin', '2018-03-19 15:43:01.630390', 'CheckBox',
        32, null, 'form-check-input');

INSERT INTO schooldev."IIT_WD_DASHBOARD_TAB_MASTER"
(parent_id, tab_type, tab_name, property, icon, url, sort, mandatory, active_flag, created_by, created_at, modified_by, modified_at, action, order_by, tab_url, style)
VALUES ((select id from schooldev."IIT_WD_DASHBOARD_TAB_MASTER" where tab_name = 'Interview' and active_flag = 'Y'), 'Col-Action', 'CheckBox', 'checkbox', 'fa-solid fa-trash-can', '', null, null,
        'Y', 'admin', '2018-03-19 15:43:01.630390', 'admin', '2018-03-19 15:43:01.630390', 'CheckBox',
        32, null, 'form-check-input');
-------------------------------------------------End-------------------------------------------------------

--------------------------------------Dec 31 2025 (Sanjay) --------------------------------------------
INSERT INTO schooldev."IIT_WD_DASHBOARD_TAB_MASTER"
(parent_id, tab_type, tab_name, property, icon, url, sort, mandatory, active_flag, created_by, created_at, modified_by, modified_at, action, order_by, tab_url, style)
VALUES ((select id from schooldev."IIT_WD_DASHBOARD_TAB_MASTER" where tab_name = 'IIT-M-Students' and active_flag = 'Y'), 'Col-Action', 'CheckBox', 'checkbox', 'fa-solid fa-trash-can', '', null, null,
        'Y', 'admin', '2018-03-19 15:43:01.630390', 'admin', '2018-03-19 15:43:01.630390', 'CheckBox',
        42, null, 'form-check-input');
-------------------------------------------------End-------------------------------------------------------

-- schooldev."STUDENT_SEARCH_VIEW" source

CREATE OR REPLACE VIEW schooldev."STUDENT_SEARCH_VIEW"
AS SELECT ( SELECT dost_election_department.dept_name
            FROM schooldev.dost_election_department
            WHERE dost_election_department.dept_code::text = "substring"("STUDENT_DETAILS_INFO".student_id::text, 1, 2) OR "substring"("STUDENT_DETAILS_INFO".student_id::text, 1, 2) = dost_election_department.alt_dept_code::text) AS dept_name,
    "STUDENT_DETAILS_INFO".student_id,
    COALESCE("STUDENT_BIO_DATA_FORM_DETAILS".student_name, ((("STUDENT_DETAILS_INFO".first_name::text || ' '::text) || "STUDENT_DETAILS_INFO".last_name::text))::character varying(120)) AS student_name,
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
     LEFT JOIN schooldev."HOSTEL_ROOM_ALLOTMENT_INFO" ON "STUDENT_DETAILS_INFO".student_id::text = "HOSTEL_ROOM_ALLOTMENT_INFO".student_id::text AND "HOSTEL_ROOM_ALLOTMENT_INFO".active_flag = 'Y'::bpchar AND ("HOSTEL_ROOM_ALLOTMENT_INFO".vacate_date IS NULL) AND ("HOSTEL_ROOM_ALLOTMENT_INFO".shifted_date IS NULL)
     LEFT JOIN schooldev."HOSTEL_ROOM_INFO" ON "HOSTEL_ROOM_INFO".room_id = COALESCE("HOSTEL_ROOM_ALLOTMENT_INFO".room_id, 0) AND "HOSTEL_ROOM_INFO".active_flag = 'Y'::bpchar
     LEFT JOIN schooldev."HOSTEL_FLOOR_MASTER" ON "HOSTEL_FLOOR_MASTER".floor_id = COALESCE("HOSTEL_ROOM_ALLOTMENT_INFO".building_id, 0) AND "HOSTEL_FLOOR_MASTER".active_flag::text = 'Y'::text
     LEFT JOIN schooldev."HOSTEL_MASTER" ON COALESCE("HOSTEL_FLOOR_MASTER".hostel_id, 0::bigint) = "HOSTEL_MASTER".hostel_id AND "HOSTEL_MASTER".active_flag = 'Y'::bpchar
     LEFT JOIN schooldev."COURSE_ALLOCATION_INFO" ON "COURSE_ALLOCATION_INFO".student_id::text = "STUDENT_DETAILS_INFO".student_id::text AND "COURSE_ALLOCATION_INFO".active_flag::text = 'Y'::text
     LEFT JOIN schooldev.course_master ON "COURSE_ALLOCATION_INFO".course_id = course_master.course_master_id AND course_master.active_flag = 'Y'::bpchar
     LEFT JOIN schooldev."CURRENT_MESS_DETAILS_VIEW" ON "CURRENT_MESS_DETAILS_VIEW".student_id::text = "STUDENT_DETAILS_INFO".student_id::text
     LEFT JOIN schooldev."USER_MANAGEMENT" ON "USER_MANAGEMENT".user_id::text = "STUDENT_DETAILS_INFO".student_id::text and "USER_MANAGEMENT".active_flag='Y'
  WHERE "STUDENT_DETAILS_INFO".settlement_flag = 'N'::bpchar;

----------------------------------------------------------------------------------------------------------------------------------

---------------------------------------------- Jan 02 2026 -------------------------------------------------------------
drop view schooldev."COMPLETE_CANDIDATE_VIEW";
drop view schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST_VIEW";
drop view schooldev."CANDIDATE_APPOINTMENT_VIEW";

ALTER TABLE schooldev."IIT_W_CANDIDATE_PERSONAL_DETAILS"
    ALTER COLUMN post_others TYPE varchar(256);
ALTER TABLE schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST"
    ALTER COLUMN category_others TYPE varchar(256);

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

-- schooldev."COMPLETE_CANDIDATE_VIEW" source

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


-- schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST_VIEW" source

CREATE OR REPLACE VIEW schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST_VIEW"
AS SELECT candidate_id,
          request_id,
          appointment_from,
          appointment_to,
          stay_from,
          stay_to,
          gross_pay,
          category,
          category_others,
          dining,
          occupancy,
          validating_authority,
          validating_authority_email,
          documents_uploaded,
          applicable_charges,
          created_by,
          created_at,
          modified_by,
          modified_at,
          approval_status,
          rejection_description,
          approval_date
   FROM schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST"
   WHERE (approval_status::text = ANY (ARRAY['Pending'::character varying::text, 'Validating'::character varying::text, 'Rejected'::character varying::text, 'Approved'::character varying::text])) AND active_flag = 'Y'::bpchar AND school_id = 1;

-----------------------------------------------------------------------------------------------------------------------
------------------------------------------------Jan 05 2026(Sanjay)----------------------------------------------------
INSERT INTO schooldev."IIT_WD_DASHBOARD_TAB_MASTER"
(parent_id, tab_type, tab_name, property, icon, url, sort, mandatory, active_flag, created_by, created_at, modified_by, modified_at, action, order_by, tab_url, style)
VALUES ((select id from "IIT_WD_DASHBOARD_TAB_MASTER" where tab_name = 'Rebate' and active_flag = 'Y'), 'Col-Action', 'CheckBox', 'checkbox', 'fa-solid fa-trash-can', '', null, null,
        'Y', 'admin', '2018-03-19 15:43:01.630390', 'admin', '2018-03-19 15:43:01.630390', 'CheckBox',
        15, null, 'form-check-input');
----------------------------------------------------------------------------------------------------------------------

-- DROP FUNCTION schooldev.update_student_current_balance();

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
            INSERT into schooldev.student_net_balance values (m_student_id, total_balance, total_balance_cc);
else
UPDATE schooldev.student_net_balance SET net_balance_mess = total_balance, net_balance_card = total_balance_cc WHERE student_id = m_student_id;
end if;
end if;

RETURN NEW;
END;
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
BEGIN
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
else updated_login_id='CCW' ;
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
    join schooldev."IIT_W_CANDIDATE_WORKFLOW" wrk on (wrk.application_id=a.request_id and wrk.candidate_id=a.candidate_id and (authority_type like '%'|| updated_login_id || '%' or authority_type like '%'|| 'Dean' || '%'))
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
				order by a.created_at::date desc
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
    wrk.stay_id=v.stay_id and (authority_type like '%'|| updated_login_id || '%' or authority_type like '%'|| 'Dean' || '%'))
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
				order by v.created_at::date desc
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
    and (authority_type like '%'|| updated_login_id || '%' or authority_type like '%'|| 'Dean' || '%') )
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
order by a.created_at::date desc
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
    (authority_type like '%'|| updated_login_id || '%' or authority_type like '%'|| 'Dean' || '%'))
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
order by a.stay_created_at::date desc
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


schooldev.search_candidates_hostel(cstatus character varying, ccategory character varying, app_from character varying,
 app_to character varying, sty_from character varying, sty_to character varying, cname character varying,
cid character varying, cvname character varying, cvemail character varying, stay character varying, tab integer,
logintype character varying, submitted_from character varying, submitted_to character varying,
approval_from character varying, approval_to character varying, facilityid integer, loginid character varying,
cemail character varying, current_stay_flag character varying)

select * from schooldev.search_candidates_hostel('null','null','null','null','null','null','null','',NULL,NULL,'null','1','CCW Office','null','null','null','null','0','ccw.office','null',NULL) as result
select * from schooldev.search_candidates_hostel(NULL,NULL,'null','null','null','null',NULL,'',NULL,NULL,NULL,'1','Hostel Check In','null','null','null','null','0','cauvery.hostel',NULL,NULL) as result
select * from schooldev.search_candidates_hostel('','','null','null','null','null','',NULL,NULL,NULL,NULL,'1','Warden','2019-01-30','null','null','null','11','wardensarayu','','') as result
select * from schooldev.search_candidates_hostel('CheckedIn','','null','null','null','null','Aparna M',NULL,NULL,NULL,NULL,'1','Hostel Check In','null','null','null','null','0','sarayu.hostel','','') as result
select * from schooldev.search_candidates_hostel('Approved','null','null','null','null','null','null','',NULL,NULL,'null','1','Dean','null','null','null','null','0','ccw.iitm','null',NULL) as result
select * from schooldev.search_candidates_hostel('CheckedOut','null','null','null','null','null','null','',NULL,NULL,'null','1','Hostel Check In','null','null','null','null','0','cauvery.hostel','null',NULL) as result
select * from schooldev.search_candidates_hostel('Approved',NULL,'null','null','2025-01-01','null',NULL,'',NULL,NULL,NULL,'1','CCW Office','null','null','null','null','0','ccw.office',NULL,NULL) as result
select * from schooldev.search_candidates_hostel('null',NULL,'null','null','null','null',NULL,NULL,NULL,NULL,'App','1',
'Dean','null','null','null','null','0','ccw',NULL,NULL) as result

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
         join schooldev."IIT_W_STUDENT_WORKFLOW" wrk on (wrk.request_id=a.request_id and wrk.student_id = a.student_id and (authority_type like '%'||'Dean'||'%' or authority_type like '%'||'CCW'||'%'))
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
         join schooldev."IIT_W_STUDENT_WORKFLOW" wrk on (wrk.request_id=a.request_id and wrk.student_id = a.student_id and (authority_type like '%'||'Dean'||'%' or authority_type like '%'||'CCW'||'%'))
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



UPDATE schooldev."IIT_W_MAIL_TEMPLATE"
SET mail_template = 'Your Request has been Validated by <<validatorName>> and forwarded to <<nextLevelValidator>> for approval. You will be intimated upon approval.'
WHERE mail_type = 'StudentScholarLevel1';


UPDATE schooldev."IIT_W_MAIL_TEMPLATE"
SET mail_template = 'Your Request for hostel accommodation dated <<submittedDate>> has been rejected by <<validatorName>>. The reason stated is as follows : <<rejectionReason>>.'
WHERE mail_type = 'StudentScholarReject';

update schooldev."IIT_W_WORKFLOW_MASTER" set authority_type='CCW',validator_name='CCW' where category ='CCW' and active_flag ='Y' and id=37;
update schooldev."IIT_W_WORKFLOW_MASTER" set authority_type='CCW',validator_name='CCW' where category ='SCHOLAR' and active_flag ='Y' and id=40;
update schooldev."IIT_W_WORKFLOW_MASTER" set validator_name='CCW' where category ='SCHOLAR' and active_flag ='Y' and id=41;


UPDATE schooldev."IIT_W_MAIL_TEMPLATE"
SET mail_template='<div style="background-color: rgb(255, 255, 255); margin-top: 10px;"><p style="background-color: rgb(255, 255, 255); margin-top: 10px;"><font size="2" face="comic sans ms">Your
 request for hostel accommodation dated #%submittedDate%# has been approved by the
#%prevValidators%# from #%stayFromDate%# to #%stayToDate%#.&nbsp;You are requested to contact the CCW Office for further informations.</font></p></div>'
WHERE mail_type='otherLevel2' and category='others';

UPDATE schooldev."IIT_W_MAIL_TEMPLATE"
SET mail_template='<div style="background-color: rgb(255, 255, 255); margin-top: 10px;"><p style="background-color: rgb(255, 255, 255); margin-top: 10px;"><font size="2" face="comic sans ms">Your
 request for hostel accommodation dated #%submittedDate%# has been approved by the
#%prevValidators%# from #%stayFromDate%# to #%stayToDate%#.&nbsp;You are requested to contact the CCW Office for further information.</font></p></div>'
WHERE mail_type='GianLevel2' and category='GIAN';

UPDATE schooldev."IIT_W_MAIL_TEMPLATE"
SET mail_template='<div style="background-color: rgb(255, 255, 255); margin-top: 10px;"><p style="background-color: rgb(255, 255, 255); margin-top: 10px;"><font size="2" face="comic sans ms">Your
 request for the hostel accommodation stay extension dated #%submittedDate%# has been approved by the
#%prevValidators%# from #%stayFromDate%# to #%stayToDate%#.&nbsp;You are requested to contact the CCW Office for further information.</font></p></div>'
WHERE mail_type='Stay_Extension_2' and category='STAY-EXTENSION';


UPDATE schooldev."STUDENT_WELLNESS_CATEGORICAL_DATA" SET created_by = um.user_id FROM schooldev."USER_MANAGEMENT" um
WHERE "STUDENT_WELLNESS_CATEGORICAL_DATA".created_by = um.user_name;

UPDATE schooldev."STUDENT_WELLNESS_CATEGORICAL_DATA" SET modified_by = um.user_id FROM schooldev."USER_MANAGEMENT" um
WHERE "STUDENT_WELLNESS_CATEGORICAL_DATA".modified_by = um.user_name;


INSERT INTO schooldev."IIT_WD_DASHBOARD_TAB_MASTER"
(parent_id, tab_type, tab_name, property,  active_flag, created_by, created_at, modified_by, modified_at, order_by)
VALUES ((select id from schooldev."IIT_WD_DASHBOARD_TAB_MASTER" where tab_name = 'Regular Student Check In' and active_flag = 'Y'),
        'Col', 'Stay From', 'stayFrom','Y', 'admin', now(), 'admin', now(),  7);

INSERT INTO schooldev."IIT_WD_DASHBOARD_TAB_MASTER"
(parent_id, tab_type, tab_name, property,  active_flag, created_by, created_at, modified_by, modified_at, order_by)
VALUES ((select id from schooldev."IIT_WD_DASHBOARD_TAB_MASTER" where tab_name = 'Regular Student Check In' and active_flag = 'Y'),
        'Col', 'Stay To', 'stayTo','Y', 'admin', now(), 'admin', now(),  7);



UPDATE schooldev."IIT_W_MAIL_TEMPLATE"
SET mail_template='<div style="background-color: rgb(255, 255, 255); margin-top: 10px;">
<p style="background-color: rgb(255, 255, 255); margin-top: 10px;"><font size="2" face="comic sans ms">Your
 request for hostel accommodation dated #%submittedDate%# has been approved by the
#%prevValidators%# from #%stayFromDate%# to #%stayToDate%#.&nbsp;You are requested to contact the CCW Office for further informations.</font></p></div>

<div><div style="background-color: rgb(255, 255, 255); margin-top: 10px;">
<font size="2" face="comic sans ms">
<p>On arrival to IIT Campus, you have to contact Hostel management office (OHM) during working hours of the office between 9 am and 5.30 pm.
Then you will be directed to go to the hostel where accommodation is available.</p></font>
<div style="background-color: rgb(255, 255, 255);"><div style="background-color: rgb(255, 255, 255); margin-top: 10px;">
<font size="2" face="comic sans ms">
<p>After proceeding to the hostel you have to produce the receipt given by the Hostel management office for allocation of room.
You will be provided basic amenities like cot, chair and table. You will have to make your own arrangement for locks, bedding etc.,.
Since we have only limited number of rooms in the hostels you will be provided only shared room accommodation.</p>
<p>Note that the accommodation is restricted only to students and there is no provision for accompanying parents/guests and avoid last
minute embarrassment.</p></font></div></div></div></div>'
WHERE mail_type='otherLevel2' and  approval_level=2 and authority_type='Candidate';


UPDATE schooldev."IIT_W_MAIL_TEMPLATE"
SET mail_template='<div style="background-color: rgb(255, 255, 255); margin-top: 10px;">
<p style="background-color: rgb(255, 255, 255); margin-top: 10px;"><font size="2" face="comic sans ms">Your
 request for the hostel accommodation stay extension dated #%submittedDate%# has been approved by the
#%prevValidators%# from #%stayFromDate%# to #%stayToDate%#.&nbsp;You are requested to contact the CCW Office for further information.</font></p></div>

<div><div style="background-color: rgb(255, 255, 255); margin-top: 5px;">
<font size="2" face="comic sans ms">
<p>On arrival to IIT Campus, you have to contact Hostel management office (OHM) during working hours of the office between 9 am and 5.30 pm.
Then you will be directed to go to the hostel where accommodation is available.</p></font>
<div style="background-color: rgb(255, 255, 255);"><div style="background-color: rgb(255, 255, 255); margin-top: 10px;">
<font size="2" face="comic sans ms">
<p>After proceeding to the hostel you have to produce the receipt given by the Hostel management office for allocation of room.
You will be provided basic amenities like cot, chair and table. You will have to make your own arrangement for locks, bedding etc.,.
Since we have only limited number of rooms in the hostels you will be provided only shared room accommodation.</p>
<p>Note that the accommodation is restricted only to students and there is no provision for accompanying parents/guests and avoid last
minute embarrassment.</p></font></div></div></div></div>'
WHERE mail_type='Stay_Extension_2' and  approval_level=2 and authority_type='Candidate';



UPDATE schooldev."IIT_W_MAIL_TEMPLATE"
SET mail_template='<div style="background-color: rgb(255, 255, 255); margin-top: 10px;">
<p style="background-color: rgb(255, 255, 255); margin-top: 10px;"><font size="2" face="comic sans ms">Your
 request for hostel accommodation dated #%submittedDate%# has been approved by the
#%prevValidators%# from #%stayFromDate%# to #%stayToDate%#.&nbsp;You are requested to contact the CCW Office for
further information.</font></p></div>

<div><div style="background-color: rgb(255, 255, 255); margin-top: 10px;">
<font size="2" face="comic sans ms">
<p>On arrival to IIT Campus, you have to contact Hostel management office (OHM) during working hours of the office between 9 am and 5.30 pm.
Then you will be directed to go to the hostel where accommodation is available.</p></font>
<div style="background-color: rgb(255, 255, 255);"><div style="background-color: rgb(255, 255, 255); margin-top: 10px;">
<font size="2" face="comic sans ms">
<p>After proceeding to the hostel you have to produce the receipt given by the Hostel management office for allocation of room.
You will be provided basic amenities like cot, chair and table. You will have to make your own arrangement for locks, bedding etc.,.
Since we have only limited number of rooms in the hostels you will be provided only shared room accommodation.</p>
<p>Note that the accommodation is restricted only to students and there is no provision for accompanying parents/guests and avoid last
minute embarrassment.</p></font></div></div></div></div>'
WHERE mail_type='GianLevel2' and  approval_level=2 and authority_type='Candidate';


UPDATE schooldev."IIT_W_MAIL_TEMPLATE"
SET mail_template='<div style="background-color: rgb(255, 255, 255); margin-top: 10px;">
<p style="background-color: rgb(255, 255, 255); margin-top: 10px;"><font size="2" face="comic sans ms">Your
 request for hostel accommodation dated #%submittedDate%# has been approved by the
#%prevValidators%# from #%stayFromDate%# to #%stayToDate%#.&nbsp;You are requested to contact the CCW Office for further informations.</font></p></div>

<div><div style="background-color: rgb(255, 255, 255); margin-top: 10px;">
<font size="2" face="comic sans ms">
<p>On arrival to IIT Campus, you have to contact Hostel management office (OHM) during working hours of the office between 9 am and 5.30 pm.
Then you will be directed to go to the hostel where accommodation is available.</p></font>
<div style="background-color: rgb(255, 255, 255);"><div style="background-color: rgb(255, 255, 255); margin-top: 10px;">
<font size="2" face="comic sans ms">
<p>After proceeding to the hostel you have to produce the receipt given by the Hostel management office for allocation of room.
You will be provided basic amenities like cot, chair and table. You will have to make your own arrangement for locks, bedding etc.,.
Since we have only limited number of rooms in the hostels you will be provided only shared room accommodation.</p>
<p>Note that the accommodation is restricted only to students and there is no provision for accompanying parents/guests and avoid last
minute embarrassment.</p></font></div></div></div></div>'
WHERE mail_type='InternshipLevel2' and  approval_level=2 and authority_type='Candidate';



-- DROP FUNCTION schooldev.get_student_current_balance(varchar, varchar);

CREATE OR REPLACE FUNCTION schooldev.get_student_current_balance(p_stud_id character varying, p_book_type character varying)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$
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
                  JOIN schooldev.student_previous_id_view_2 spiv ON (mla.acchead = ANY (ARRAY[spiv.new_id] || COALESCE(string_to_array(spiv.old_id, ','), ARRAY[]::text[])))
         WHERE mla.active_flag = 'Y' AND mla.cancel_status = 'N' AND mla.recon in ('', 'N') AND mla.book_type::text = p_book_type and spiv.new_id = p_stud_id
         UNION ALL
         SELECT mlb.amount, mlb.debit_or_credit, spiv.new_id as student_id
         FROM schooldev."MESS_LEDGER_B" mlb
             JOIN schooldev.student_previous_id_view_2 spiv ON (mlb.acchead = ANY (ARRAY[spiv.new_id] || COALESCE(string_to_array(spiv.old_id, ','), ARRAY[]::text[])))
         WHERE mlb.active_flag = 'Y' AND mlb.cancel_status = 'N' AND mlb.recon in ('', 'N') AND mlb.book_type = p_book_type and spiv.new_id = p_stud_id
         UNION ALL
         SELECT mob.amount, mob.debit_or_credit, spiv.new_id as student_id
         FROM schooldev."MESS_OPENING_BAL" mob
             JOIN schooldev.student_previous_id_view_2 spiv ON (mob.acchead = ANY (ARRAY[spiv.new_id] || COALESCE(string_to_array(spiv.old_id, ','), ARRAY[]::text[])))
         WHERE mob.active_flag = 'Y' AND spiv.new_id = p_stud_id) t;
raise notice 'Balance fetched for % for % in %', p_stud_id, p_book_type, (clock_timestamp() - duration);
return total_balance;
END;
/*

select * from schooldev.get_student_current_balance('CY22C052', 'MS');
select * from schooldev.get_student_current_balance('CY22C052', 'CC');

*/
$function$
;


UPDATE schooldev."IIT_W_MAIL_TEMPLATE"
SET mail_template =REPLACE(REPLACE(mail_template, '<<', '#%'),'>>', '%#')
WHERE mail_template LIKE '%<<%>>%';

UPDATE schooldev."SIMS_CONFIG_DATA"
SET config_value =REPLACE(REPLACE(config_value, '<<', '#%'),'>>', '%#')
WHERE config_value LIKE '%<<%>>%';

------------------------------------ Warden Incharge Mail Template Insertion Script (Feb 16 2026, Sanjay) ------------------------------------------------
INSERT INTO schooldev."IIT_W_MAIL_TEMPLATE" (mail_type, mail_subject, mail_template, description, active_flag, created_by, created_at, modified_by, modified_at, category, approval_level, authority_type) VALUES ('WardenInchareIntimation', 'Incharge Intimation', '
<table width="100%" cellpadding="0" cellspacing="0" style="font-family: Arial, sans-serif; font-size:14px; color:#333333; border-collapse:collapse;">
    <!-- Header Section -->
    <tr>
        <td colspan="2" style="padding:12px 0;">
            <p style="margin:0; font-size:15px;">
                It is informed that the warden of hostel(s)
                <strong>#%warden_hostel%#</strong>
                is away for a short duration.
                The warden of hostel(s)
                <strong>#%incharge_hostel%#</strong>
                will take charge.
            </p>
        </td>
    </tr>
    <!-- Spacer -->
    <tr>
        <td colspan="2" style="height:15px;"></td>
    </tr>
    <!-- Details Table -->
    <tr>
        <td style="padding:8px; background-color:#f4f4f4; width:35%; font-weight:bold; border:1px solid #dddddd;">
            Requested By
        </td>
        <td style="padding:8px; border:1px solid #dddddd;">
            #%warden_name%#
        </td>
    </tr>
    <tr>
        <td style="padding:8px; background-color:#f9f9f9; font-weight:bold; border:1px solid #dddddd;">
            Warden Hostel
        </td>
        <td style="padding:8px; border:1px solid #dddddd;">
            #%warden_hostel%#
        </td>
    </tr>
    <tr>
        <td style="padding:8px; background-color:#f4f4f4; font-weight:bold; border:1px solid #dddddd;">
            Away Dates
        </td>
        <td style="padding:8px; border:1px solid #dddddd;">
            #%away_dates%#
        </td>
    </tr>
    <tr>
        <td style="padding:8px; background-color:#f9f9f9; font-weight:bold; border:1px solid #dddddd;">
            Reason
        </td>
        <td style="padding:8px; border:1px solid #dddddd;">
            #%reason%#
        </td>
    </tr>
    <tr>
        <td style="padding:8px; background-color:#f4f4f4; font-weight:bold; border:1px solid #dddddd;">
            Incharge Warden
        </td>
        <td style="padding:8px; border:1px solid #dddddd;">
            #%incharge_name%#
        </td>
    </tr>
    <tr>
        <td style="padding:8px; background-color:#f9f9f9; font-weight:bold; border:1px solid #dddddd;">
            Incharge Hostel
        </td>
        <td style="padding:8px; border:1px solid #dddddd;">
            #%incharge_hostel%#
        </td>
    </tr>
</table>
', null, 'Y', 'triesten', '2018-04-17 15:35:21.665000', 'triesten', '2024-04-29 00:01:29.887000', 'WARDEN', 1, 'Warden');
-------------------------------------------------------------------------------- End ---------------------------------------------------------------
--- Tem Accommodation List Function Changes and Including additional Column in the Table IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE (Feb 19 2026 Sanjay) -----

ALTER TABLE schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE" ADD payment_approval_date timestamp NULL;

-- DROP FUNCTION schooldev.temp_accomm_list(varchar, varchar, varchar, varchar, varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION schooldev.temp_accomm_list(submittedfromdate character varying, submittedtodate character varying,
stayfrom character varying, stayto character varying, candidatename character varying, requestid character varying,
candidateemail character varying, approval_from_date character varying, approval_to_date character varying)
 RETURNS SETOF templist
 LANGUAGE plpgsql
AS $function$
 declare
res templist%rowtype;
BEGIN

if (submittedfromdate is null or submittedfromdate='') and (submittedtodate is null or submittedtodate='') and  (stayfrom is null or stayfrom='') and
	(stayto is null or stayto='')  and (candidatename is null or candidatename='') and (requestid = '0' or requestid is null or requestid='')
	and (candidateemail is null or candidateemail='') and (approval_from_date is null or approval_from_date='')and (approval_to_date is null or approval_to_date='') then
	raise notice 'ifloop: %',  '1';
for res in
select  b.request_id,min(b.created_at) as submitted_date,min(b.candidate_id) as candidate_id ,
        min(b.stay_from) as stay_from, max(b.stay_to) as stay_to ,min(c.first_name) ||' '|| min(c.last_name) as candidate_name,
        min(c.gender) as gender ,min(c.email) as email
from schooldev."CANDIDATE_STAY_DATE_LIST_VIEW" b
         left join schooldev."IIT_W_CANDIDATE_PERSONAL_DETAILS" c on (b.candidate_id =c.candidate_id)
where post_select in ('internship','others','projectStaff','gian') and b.created_at >=(now()::date-15)
  and app_status in ('Alloted','Approved','CheckedIn','CheckedOut'   )
group by b.request_id order by request_id
    loop
		return next res;
end loop;
else
 raise notice 'loginIDS: %',  requestid;
for res in
select  b.request_id,min(b.created_at) as submitted_date,min(b.candidate_id) as candidate_id ,
        min(b.stay_from) as stay_from, max(b.stay_to) as stay_to ,min(c.first_name) ||' '|| min(c.last_name) as candidate_name,
        min(c.gender) as gender ,min(c.email) as email
from schooldev."CANDIDATE_STAY_DATE_LIST_VIEW" b
         left join schooldev."IIT_W_CANDIDATE_PERSONAL_DETAILS" c on (b.candidate_id =c.candidate_id)
         left join schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE" d on (b.request_id = d.request_id and d.active_flag = 'Y')
where post_select in ('internship','others','projectStaff','gian')
  and app_status in ('Alloted','Approved','CheckedIn','CheckedOut')
  and( case when (submittedfromdate::text<>'null' and submittedfromdate::date is not null) then  b.created_at::date >=submittedfromdate::date else 1=1 end)
  and (case when (submittedtodate::text<>'null' and submittedtodate::date is not null) then b.created_at::date <= submittedtodate::date else 1=1 end)
  and (case when (stayfrom::text <>'null' and stayfrom::date is not null) then b.stay_from >= stayfrom::date  else 1=1 end)
  and (case when (stayto::text <>'null' and stayto::date is not null) then b.stay_to <= stayto::date else 1=1 end)
  and (case when (candidatename is not null and candidatename<>'') then lower(c.first_name||' '||c.last_name) like lower('%'||candidatename||'%') else 1=1 end)
  and (case when candidateemail is not null then lower(c.email) like lower('%'||candidateemail||'%') else 1=1 end)
  and (case when requestid is not null and requestid<> '0' then b.request_id::text = requestid else 1=1 end )
  and( case when (approval_from_date::text<>'null' and approval_from_date::date is not null) then d.payment_approval_date::date >=approval_from_date::date else 1=1 end)
  and (case when (approval_to_date::text<>'null' and approval_to_date::date is not null) then d.payment_approval_date::date <= approval_to_date::date else 1=1 end)
group by b.request_id order by request_id
    loop
return next res;
end loop;
end if;

END;

/*
drop type templist;

create type templist as (request_id integer,created_at character varying,candidate_id character varying,
stay_from character varying,stay_to character varying,first_name character varying,gender character varying,
email character varying);

select *  from  schooldev.temp_accomm_list(NULL,NULL,NULL,NULL,NULL,'0',NULL)
select *  from  schooldev.temp_accomm_list(NULL,NULL,NULL,NULL,'','4506',NULL)
select *  from  schooldev.temp_accomm_list(NULL,NULL,NULL,NULL,NULL,NULL,NULL)
select *  from  schooldev.temp_accomm_list(NULL,NULL,NULL,NULL,'gowtham','',NULL)
*/

$function$
;

------------------------------------------------------------------------ END -----------------------------------------------------------------------------

---------------------------------------------------------------Feb 23 (Sanjay) =---------------------------------------------------------------------
INSERT INTO schooldev."IIT_WD_DASHBOARD_TAB_MASTER" (parent_id, tab_type, tab_name, property, icon, url, sort, mandatory,
                                                     active_flag, created_by, created_at, modified_by, modified_at, action, order_by, tab_url, style)
VALUES ((select p.id from schooldev."IIT_WD_DASHBOARD_TAB_MASTER" p where p.tab_name = 'HDC Complaint List'), 'Col-Link', 'Delete', 'delete', 'fa-solid fa-trash-can', 'delete', null, null,
        'Y', 'admin', now(), 'admin', now(), 'Delete', 10, null, 'btn-danger');
-----------------------------------------------------------------------END-----------------------------------------------------------------------


----------------------------------------------------------------Mar 04 2026(Sanjay)--------------------------------------------------------------
INSERT INTO schooldev."SIMS_CONFIG_DATA" (config_key, config_value, created_by, created_at, modified_by, modified_at, active_flag, description, school_id) VALUES ('ACCOMMODATION_CHARGES', e'[
  {
    "category": "Library Trainee",
    "singleRoom": "-",
    "sharingRoom": "400",
    "dormitoryRoom": "375"
  },
  {
    "category": "TAs and Institute Trainee",
    "singleRoom": "-",
    "sharingRoom": "465",
    "dormitoryRoom": "440"
  },
  {
    "category": "Internship Students",
    "singleRoom": "580",
    "sharingRoom": "515",
    "dormitoryRoom": "440"
  },
  {
    "category": "Others: Conference, Workshop, GIAN Course, etc.",
    "singleRoom": "790",
    "sharingRoom": "680",
    "dormitoryRoom": "565"
  }
]', '60000100', '2026-03-04 10:39:43.396244', '60000100', '2026-03-04 10:56:08.896337', 'Y', 'ACCOMMODATION_CHARGES', 1);
-----------------------------------------------------------------------------END-------------------------------------------------------------------------------------------------------


-------------------------------------------------------------Mar 09, 2026-----------------------------------------------
-- DROP FUNCTION schooldev.temp_accomm_list(varchar, varchar, varchar, varchar, varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION schooldev.temp_accomm_list(submittedfromdate character varying, submittedtodate character varying,
                                                      stayfrom character varying, stayto character varying, candidatename character varying, requestid character varying,
                                                      candidateemail character varying, approval_from_date character varying)
    RETURNS SETOF templist
    LANGUAGE plpgsql
AS $function$
declare
    res templist%rowtype;
BEGIN

    if (submittedfromdate is null or submittedfromdate='') and (submittedtodate is null or submittedtodate='') and  (stayfrom is null or stayfrom='') and
       (stayto is null or stayto='')  and (candidatename is null or candidatename='') and (requestid = '0' or requestid is null or requestid='')
        and (candidateemail is null or candidateemail='') and (approval_from_date is null or approval_from_date='') then
        raise notice 'ifloop: %',  '1';
        for res in
            select  b.request_id,min(b.created_at) as submitted_date,min(b.candidate_id) as candidate_id ,
                    min(b.stay_from) as stay_from, max(b.stay_to) as stay_to ,min(c.first_name) ||' '|| min(c.last_name) as candidate_name,
                    min(c.gender) as gender ,min(c.email) as email
            from schooldev."CANDIDATE_STAY_DATE_LIST_VIEW" b
                     left join schooldev."IIT_W_CANDIDATE_PERSONAL_DETAILS" c on (b.candidate_id =c.candidate_id)
            where post_select in ('internship','others','projectStaff','gian') and b.created_at >=(now()::date-15)
              and app_status in ('Alloted','Approved','CheckedIn','CheckedOut'   )
            group by b.request_id order by request_id
            loop
                return next res;
            end loop;
    else
        raise notice 'loginIDS: %',  requestid;
        for res in
            select  b.request_id,min(b.created_at) as submitted_date,min(b.candidate_id) as candidate_id ,
                    min(b.stay_from) as stay_from, max(b.stay_to) as stay_to ,min(c.first_name) ||' '|| min(c.last_name) as candidate_name,
                    min(c.gender) as gender ,min(c.email) as email
            from schooldev."CANDIDATE_STAY_DATE_LIST_VIEW" b
                     left join schooldev."IIT_W_CANDIDATE_PERSONAL_DETAILS" c on (b.candidate_id =c.candidate_id)
                     left join schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE" d on (b.request_id = d.request_id and d.active_flag = 'Y')
            where post_select in ('internship','others','projectStaff','gian')
              and app_status in ('Alloted','Approved','CheckedIn','CheckedOut')
              and( case when (submittedfromdate::text<>'null' and submittedfromdate::date is not null) then  b.created_at::date >=submittedfromdate::date else 1=1 end)
              and (case when (submittedtodate::text<>'null' and submittedtodate::date is not null) then b.created_at::date <= submittedtodate::date else 1=1 end)
              and (case when (stayfrom::text <>'null' and stayfrom::date is not null) then b.stay_from >= stayfrom::date  else 1=1 end)
              and (case when (stayto::text <>'null' and stayto::date is not null) then b.stay_to <= stayto::date else 1=1 end)
              and (case when (candidatename is not null and candidatename<>'') then lower(c.first_name||' '||c.last_name) like lower('%'||candidatename||'%') else 1=1 end)
              and (case when candidateemail is not null then lower(c.email) like lower('%'||candidateemail||'%') else 1=1 end)
              and (case when requestid is not null and requestid<> '0' then b.request_id::text = requestid else 1=1 end )
              and( case when (approval_from_date::text<>'null' and approval_from_date::date is not null) then d.payment_approval_date::date = approval_from_date::date else 1=1 end)
            group by b.request_id order by request_id
            loop
                return next res;
            end loop;
    end if;

END;

/*
drop type templist;

create type templist as (request_id integer,created_at character varying,candidate_id character varying,
stay_from character varying,stay_to character varying,first_name character varying,gender character varying,
email character varying);

select *  from  schooldev.temp_accomm_list(NULL,NULL,NULL,NULL,NULL,'0',NULL)
select *  from  schooldev.temp_accomm_list(NULL,NULL,NULL,NULL,'','4506',NULL)
select *  from  schooldev.temp_accomm_list(NULL,NULL,NULL,NULL,NULL,NULL,NULL)
select *  from  schooldev.temp_accomm_list(NULL,NULL,NULL,NULL,'gowtham','',NULL)
*/

$function$
;
-------------------------------------------------------------END------------------------------------------------------------------------

-------------------------------------------------------------Mar 11, 2026-----------------------------------------------
DROP FUNCTION schooldev.temp_accomm_list(varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar);
drop type templist;
create type templist as (request_id integer,created_at character varying,candidate_id character varying,
    stay_from character varying,stay_to character varying,first_name character varying,gender character varying,
    email character varying,payment_amount integer,payment_approval_date character varying);

-- DROP FUNCTION schooldev.temp_accomm_list(varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar);
CREATE OR REPLACE FUNCTION schooldev.temp_accomm_list(submittedfromdate character varying, submittedtodate character varying, stayfrom character varying, stayto character varying, candidatename character varying, requestid character varying, candidateemail character varying, approval_from_date character varying)
 RETURNS SETOF templist
 LANGUAGE plpgsql
AS $function$
 declare
res templist%rowtype;
BEGIN

if (submittedfromdate is null or submittedfromdate='') and (submittedtodate is null or submittedtodate='') and  (stayfrom is null or stayfrom='') and
	(stayto is null or stayto='')  and (candidatename is null or candidatename='') and (requestid = '0' or requestid is null or requestid='')
	and (candidateemail is null or candidateemail='') and (approval_from_date is null or approval_from_date='') then
	raise notice 'ifloop: %',  '1';
for res in
select  b.request_id,min(b.created_at) as submitted_date,min(b.candidate_id) as candidate_id ,
        min(b.stay_from) as stay_from, max(b.stay_to) as stay_to ,min(c.first_name) ||' '|| min(c.last_name) as candidate_name,
        min(c.gender) as gender ,min(c.email) as email, 0 as  payment_amount, '' as payment_approval_date
from schooldev."CANDIDATE_STAY_DATE_LIST_VIEW" b
         left join schooldev."IIT_W_CANDIDATE_PERSONAL_DETAILS" c on (b.candidate_id =c.candidate_id)
where post_select in ('internship','others','projectStaff','gian') and b.created_at >=(now()::date-15)
  and app_status in ('Alloted','Approved','CheckedIn','CheckedOut'   )
group by b.request_id order by request_id
    loop
		return next res;
end loop;
else
 raise notice 'loginIDS: %',  requestid;
for res in
select  b.request_id,min(b.created_at) as submitted_date,min(b.candidate_id) as candidate_id ,
        min(b.stay_from) as stay_from, max(b.stay_to) as stay_to ,min(c.first_name) ||' '|| min(c.last_name) as candidate_name,
        min(c.gender) as gender ,min(c.email) as email, max(d.payment_amount_1) as payment_amount,max(payment_approval_date::date) as payment_approval_date
from schooldev."CANDIDATE_STAY_DATE_LIST_VIEW" b
         left join schooldev."IIT_W_CANDIDATE_PERSONAL_DETAILS" c on (b.candidate_id =c.candidate_id)
         left join schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE" d on (b.request_id = d.request_id
    and d.active_flag = 'Y' and d.payment_approval_status='Approved')
where post_select in ('internship','others','projectStaff','gian')
  and app_status in ('Alloted','Approved','CheckedIn','CheckedOut')
  and( case when (submittedfromdate::text<>'null' and submittedfromdate::date is not null) then  b.created_at::date >=submittedfromdate::date else 1=1 end)
  and (case when (submittedtodate::text<>'null' and submittedtodate::date is not null) then b.created_at::date <= submittedtodate::date else 1=1 end)
  and (case when (stayfrom::text <>'null' and stayfrom::date is not null) then b.stay_from >= stayfrom::date  else 1=1 end)
  and (case when (stayto::text <>'null' and stayto::date is not null) then b.stay_to <= stayto::date else 1=1 end)
  and (case when (candidatename is not null and candidatename<>'') then lower(c.first_name||' '||c.last_name) like lower('%'||candidatename||'%') else 1=1 end)
  and (case when candidateemail is not null then lower(c.email) like lower('%'||candidateemail||'%') else 1=1 end)
  and (case when requestid is not null and requestid<> '0' then b.request_id::text = requestid else 1=1 end )
  and( case when (approval_from_date::text<>'null' and approval_from_date::date is not null) then d.payment_approval_date::date =approval_from_date::date else 1=1 end)
--and (case when (approval_to_date::text<>'null' and approval_to_date::date is not null) then d.payment_approval_date::date <= approval_to_date::date else 1=1 end)
group by b.request_id order by request_id
    loop
return next res;
end loop;
end if;

END;

/*
drop type templist;

create type templist as (request_id integer,created_at character varying,candidate_id character varying,
stay_from character varying,stay_to character varying,first_name character varying,gender character varying,
email character varying,payment_amount integer,payment_approval_date character varying);

select *  from  schooldev.temp_accomm_list(NULL,NULL,NULL,NULL,NULL,'0',NULL)
select *  from  schooldev.temp_accomm_list(NULL,NULL,NULL,NULL,'','4506',NULL)
select *  from  schooldev.temp_accomm_list(NULL,NULL,NULL,NULL,NULL,NULL,NULL)
select *  from  schooldev.temp_accomm_list(NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-04')
*/

$function$
;
-------------------------------------------------------------END------------------------------------------------------------------------

-------------------------------------------------------- Mar 19, 2026 ( Sanjay )----------------------------------------------------------
-- DROP FUNCTION schooldev.food_court_list(varchar, varchar, int8, int4, varchar, varchar);

CREATE OR REPLACE FUNCTION schooldev.food_court_list(studentid character varying, userrole character varying, messperiodid bigint, foodcourtmessnameid integer, fromdate character varying, todate character varying)
    RETURNS SETOF foodcourtlist
    LANGUAGE plpgsql
AS $function$
declare res foodcourtlist%rowtype;
BEGIN
    --if(userrole='SoftwareAdmin') then
    if(studentid is null or studentid='') and (userrole is null or userrole='') and (messperiodid=0) and
      (foodcourtmessnameid=0) and (fromdate is null or fromdate='') and (todate is null or todate='')then
        raise notice 'ifloop: %',  '1';
        for res in
            select concat('Ex',fc.fc_id) as voucher_no,fc.purchase_timestamp as voucher_date,a.student_id,d.mess_name,b.dining_from_date,
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
            select concat('Ex',fc.fc_id) as voucher_no,fc.purchase_timestamp as voucher_date,a.student_id,d.mess_name,b.dining_from_date,
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


   select *  from  schooldev.food_court_list('','SoftwareAdmin','81','0','','')
   select *  from  schooldev.food_court_list('','','33','0','09-Sep-2019','10-Sep-2019')
   select *  from  schooldev.food_court_list('','','33','0','','')
   select *  from  schooldev.food_court_list('MM19B014','','26','0','','')
   */

$function$
;
---------------------------------------------------------------------------END-------------------------------------------------------------------------------
---------------------------------------------------------------------Apr 30, 2026 ---------------------------------------------------------------------------
-- DROP FUNCTION schooldev.temp_accomm_list(varchar, varchar, varchar, varchar, varchar, varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION schooldev.temp_accomm_list(submittedfromdate character varying, submittedtodate character varying, stayfrom character varying, stayto character varying, candidatename character varying, requestid character varying, candidateemail character varying, approval_from_date character varying)
    RETURNS SETOF templist
    LANGUAGE plpgsql
AS $function$
declare
    res templist%rowtype;
BEGIN

    if (submittedfromdate is null or submittedfromdate='') and (submittedtodate is null or submittedtodate='') and  (stayfrom is null or stayfrom='') and
       (stayto is null or stayto='')  and (candidatename is null or candidatename='') and (requestid = '0' or requestid is null or requestid='')
        and (candidateemail is null or candidateemail='') and (approval_from_date is null or approval_from_date='') then
        raise notice 'ifloop: %',  '1';
        for res in
            select  b.request_id,min(b.created_at) as submitted_date,min(b.candidate_id) as candidate_id ,
                    min(b.stay_from) as stay_from, max(b.stay_to) as stay_to ,min(c.first_name) ||' '|| min(c.last_name) as candidate_name,
                    min(c.gender) as gender ,min(c.email) as email, 0 as  payment_amount, NULL::DATE as payment_approval_date
            from schooldev."CANDIDATE_STAY_DATE_LIST_VIEW" b
                     left join schooldev."IIT_W_CANDIDATE_PERSONAL_DETAILS" c on (b.candidate_id =c.candidate_id)
            where post_select in ('interviews','internship','others','projectStaff','gian') and b.created_at >=(now()::date-15)
              and app_status in ('Allotted','Approved','CheckedIn','CheckedOut'   )
            group by b.request_id order by request_id
            loop
                return next res;
            end loop;
    else
        raise notice 'loginIDS: %',  requestid;
        for res in
            select  b.request_id,min(b.created_at) as submitted_date,min(b.candidate_id) as candidate_id ,
                    min(b.stay_from) as stay_from, max(b.stay_to) as stay_to ,min(c.first_name) ||' '|| min(c.last_name) as candidate_name,
                    min(c.gender) as gender ,min(c.email) as email, max(d.payment_amount_1) as payment_amount,max(payment_approval_date::date) as payment_approval_date
            from schooldev."CANDIDATE_STAY_DATE_LIST_VIEW" b
                     left join schooldev."IIT_W_CANDIDATE_PERSONAL_DETAILS" c on (b.candidate_id =c.candidate_id)
                     left join schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE" d on (b.request_id = d.request_id
                and d.active_flag = 'Y' and d.payment_approval_status='Approved')
            where post_select in ('interviews','internship','others','projectStaff','gian')
              and app_status in ('Allotted','Approved','CheckedIn','CheckedOut')
              and( case when (submittedfromdate::text<>'null' and NULLIF(submittedfromdate, '') is not null) THEN b.created_at::date >= NULLIF(submittedfromdate, '')::date  else 1=1 end)
              and (case when (submittedtodate::text<>'null' and NULLIF(submittedfromdate, '')::date is not null) then b.created_at::date <= NULLIF(submittedfromdate, '')::date else 1=1 end)
              and (case when (stayfrom::text <>'null' and stayfrom::date is not null) then b.stay_from >= stayfrom::date  else 1=1 end)
              and (case when (stayto::text <>'null' and stayto::date is not null) then b.stay_to <= stayto::date else 1=1 end)
              and (case when (candidatename is not null and candidatename<>'') then lower(c.first_name||' '||c.last_name) like lower('%'||candidatename||'%') else 1=1 end)
              and (case when candidateemail is not null then lower(c.email) like lower('%'||candidateemail||'%') else 1=1 end)
              and (case when requestid is not null and requestid<> '0' then b.request_id::text = requestid else 1=1 end )
              and( case when (approval_from_date::text<>'null' and approval_from_date::date is not null) then d.payment_approval_date::date =approval_from_date::date else 1=1 end)
--and (case when (approval_to_date::text<>'null' and approval_to_date::date is not null) then d.payment_approval_date::date <= approval_to_date::date else 1=1 end)
            group by b.request_id order by request_id
            loop
                return next res;
            end loop;
    end if;

END;

/*
drop type templist;

create type templist as (request_id integer,created_at character varying,candidate_id character varying,
stay_from character varying,stay_to character varying,first_name character varying,gender character varying,
email character varying,payment_amount integer,payment_approval_date character varying);

select *  from  schooldev.temp_accomm_list(NULL,NULL,NULL,NULL,NULL,'0',NULL)
select *  from  schooldev.temp_accomm_list(NULL,NULL,NULL,NULL,'','4506',NULL)
select *  from  schooldev.temp_accomm_list(NULL,NULL,NULL,NULL,NULL,NULL,NULL)
select *  from  schooldev.temp_accomm_list(NULL,NULL,NULL,NULL,NULL,NULL,NULL,'2025-12-04')
*/

$function$
;

---------------------------------------------------------------------------END---------------------------------------------------------------------------------------------

-------------------------------------------------------------------------May 07 2026----------------------------------------------------------------------
INSERT INTO schooldev."SIMS_CONFIG_DATA" (config_key, config_value, created_by, created_at, modified_by, modified_at, active_flag, description, school_id)
VALUES ('OTHER_LOGIN_RETRY_COUNT', '5', 'Admin', '2026-05-07 13:48:01.154380', 'Admin', '2026-05-07 13:48:01.154380', 'Y', 'OTHER_LOGIN_RETRY_COUNT', 1);
----------------------------------------------------------------------------END---------------------------------------------------------------------------

------------------------------------------------------------------------May 19,2026----------------------------------------------------------------------
-----------------------------------------------------------------------process_workflow-------------------------------------------------------------------
-- DROP FUNCTION schooldev.process_workflow(int8, varchar, varchar, varchar, varchar, varchar, int8);

CREATE OR REPLACE FUNCTION schooldev.process_workflow(given_wf_row_id bigint, given_date character varying, given_status character varying, given_reason character varying, given_approval_notes character varying, occupancy_selected character varying, accom_priority_selected bigint, given_modified_by character varying)
    RETURNS SETOF candidate_workflow_type_tosendemail
    LANGUAGE plpgsql
AS $function$

declare
    approvel_levelfld bigint;
    application_idfld bigint;
    authentication_typefld varchar;
    status_all_approved_in_same_level boolean:=false;
    count_of_pending int :=0;
    list_size int :=0;
    r candidate_workflow_type_toSendEmail%rowtype;
    update_result varchar;
    description text :='';

BEGIN
    -- Here updating the Specific status. If overriding directly updating status without the current

    --START TRANSACTION ISOLATION LEVEL READ COMMITTED;
    if given_status!='OverrideApproved' then
        UPDATE schooldev."IIT_W_CANDIDATE_WORKFLOW"    SET  modified_at=now(), status=given_status, approval_notes=given_approval_notes, modified_by=given_modified_by  WHERE id=given_WF_row_id
                                                                                                                                                                          and modified_at =given_date :: timestamp returning status into update_result;
        --and status='Pending';
    else
        UPDATE schooldev."IIT_W_CANDIDATE_WORKFLOW"    SET  modified_at=now(), status='Approved', approval_notes=given_approval_notes, modified_by=given_modified_by  WHERE id=given_WF_row_id
                                                                                                                                                                        and modified_at =given_date :: timestamp  returning status into update_result;
    end if;
-- Get the level, application id, authentication_type  of current record
    SELECT approval_level,application_id, authentication_type INTO approvel_levelfld,application_idfld, authentication_typefld FROM schooldev."IIT_W_CANDIDATE_WORKFLOW" WHERE id=given_WF_row_id;



    --if the given_status is rejected, then the student status is directly updated as 'Rejected'
    if given_status='Rejected' then
        update schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST" set modified_at=now(), approval_status = given_status, rejection_description = given_reason,status_notes='Your request has been rejected for the reason:'||given_reason, modified_by=given_modified_by where request_id=application_idfld;
        update schooldev."IIT_W_CANDIDATE_WORKFLOW" set rejection_description = given_reason, modified_by=given_modified_by where id=given_wf_row_id;
        update schooldev."IIT_W_CANDIDATE_WORKFLOW" set modified_at=now(), status=given_status, modified_by=given_modified_by where id=given_wf_row_id;

    else
        begin
            -- Decide whether all other people have approved, who are in the same level
            if authentication_typefld='a' THEN
                begin
                    select count(*) into count_of_pending FROM schooldev."IIT_W_CANDIDATE_WORKFLOW" WHERE  approval_level=approvel_levelfld and application_id=application_idfld and active_flag='Y' and (status ='Rejected' or status ='Pending' ) ;
                    IF count_of_pending=0 THEN
                        status_all_approved_in_same_level=true;
                    END If;

                end;
            end if;
-- if all have approved,  Set all level+1 records - status='Pending', return level+1 of records,


            if (authentication_typefld='o' or (authentication_typefld='a' and status_all_approved_in_same_level=true)) and (given_status='Approved' or given_status='OverrideApproved') THEN
                BEGIN
                    UPDATE schooldev."IIT_W_CANDIDATE_WORKFLOW"    SET  status='Pending', modified_by=given_modified_by  WHERE   application_id=application_idfld and
                        approval_level=approvel_levelfld+1  and status='Default';

                    --get the count of next level validatiors. if there are next level validatiors, student's status will be updated to 'Pending' (from validating)
                    -- if there is no next level approval validators and the given status is 'Approved' the student's status will be updated to 'Approved'.

                    select count(candidate_id) into list_size from schooldev."IIT_W_CANDIDATE_WORKFLOW" where
                        application_id=application_idfld and
                        approval_level=approvel_levelfld+1 and status='Pending';

                    if(list_size>0) then
                        update schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST" set modified_at=now(), approval_status = 'Pending', modified_by=given_modified_by where request_id=application_idfld;
                    end if;

                    if(list_size=0 or list_size=null) then
                        if given_approval_notes is not null then
                            description = ' Upon approval the validator has added a comment, please make a note of it:'||given_approval_notes;
                        else description ='' ;
                        end if;
                        if given_status!='OverrideApproved' then
                            update schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST" set modified_at=now(), approval_status = given_status, occupancy=occupancy_selected,accom_priority=accom_priority_selected, status_notes=' Your request has been Approved.'||description, modified_by=given_modified_by where request_id=application_idfld;
                        else
                            update schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST" set modified_at=now(), approval_status = 'Approved', occupancy=occupancy_selected,accom_priority=accom_priority_selected, status_notes=' Your request has been Approved.'||description, modified_by=given_modified_by where request_id=application_idfld;
                        end if;
                    end if;
                    -- raise EXCEPTION 'Status%',status_all_approved_in_same_level;
                    for r in select id, application_id, candidate_id, authority_type, approval_level, email, validator_name, authentication_type,
                                    created_by, created_at, modified_by, modified_at, active_flag, school_id, category, status
                             FROM schooldev."IIT_W_CANDIDATE_WORKFLOW"
                             where application_id=application_idfld and approval_level=approvel_levelfld+1 and status='Pending'
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


-------------------------------------------------------------------------process_workflow_stay_extension-------------------------------------------------------------------------------------------------------------------------------------------


-- DROP FUNCTION schooldev.process_workflow_stay_extension(int8, varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION schooldev.process_workflow_stay_extension(given_wf_row_id bigint, given_status character varying, given_reason character varying, given_approval_notes character varying, given_modified_by character varying)
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
        UPDATE schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW"    SET  modified_at=now(), approval_status=given_status, approval_notes=given_approval_notes, modified_by=given_modified_by  WHERE id=given_WF_row_id ;
        --		and modified_at =given_date :: timestamp;
        --and status='Pending';
    else
        UPDATE schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW"    SET   modified_at=now(),approval_status='Approved', approval_notes=given_approval_notes, modified_by=given_modified_by  WHERE id=given_WF_row_id ;
--			and modified_at =given_date :: timestamp;
    end if;

-- Get the level, application id, authentication_type  of current record
    SELECT approval_level,stay_id, authentication_type INTO approvel_levelfld,application_idfld, authentication_typefld FROM schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW" WHERE id=given_WF_row_id;


    --if the given_status is rejected, then the student status is directly updated as 'Rejected'
    raise notice 'application_idfld%',application_idfld;
    if given_status='Rejected' then
        update schooldev."IIT_W_CANDIDATE_STAY_REQUEST" set modified_at = now(), approval_status = given_status, rejection_description = given_reason,status_notes='Your request has been rejected for the reason:'||given_reason, modified_by=given_modified_by where stay_id=application_idfld;
        update schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW" set modified_at = now(), rejection_description = given_reason, modified_by=given_modified_by where id=given_wf_row_id;
        update schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW" set modified_at=now(), approval_status=given_status, modified_by=given_modified_by where id=given_wf_row_id;
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
                    UPDATE schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW"    SET  approval_status='Pending', modified_by=given_modified_by  WHERE   stay_id=application_idfld and
                        approval_level=approvel_levelfld+1  and approval_status='Default';

                    --get the count of next level validatiors. if there are next level validatiors, student's status will be updated to 'Pending' (from validating)
                    -- if there is no next level approval validators and the given status is 'Approved' the student's status will be updated to 'Approved'.
                    select count(candidate_id) into list_size from schooldev."IIT_W_CANDIDATE_STAY_REQUEST_WORKFLOW" where
                        stay_id=application_idfld and
                        approval_level=approvel_levelfld+1 and approval_status='Pending';

                    if(list_size>0) then
                        update schooldev."IIT_W_CANDIDATE_STAY_REQUEST" set approval_status = 'Pending', modified_by=given_modified_by where stay_id=application_idfld;
                    end if;

                    if(list_size=0 or list_size=null) then
                        if given_approval_notes is not null then
                            status_description = ' Upon approval the validator has added a comment, please make a note of it:'||given_approval_notes;
                        else status_description ='' ;
                        end if;
                        if given_status!='OverrideApproved' then
                            update schooldev."IIT_W_CANDIDATE_STAY_REQUEST" set modified_at=now(), approval_status = given_status, status_notes=' Your request has been Approved.'||status_description, modified_by=given_modified_by  where stay_id=application_idfld;
                        else
                            update schooldev."IIT_W_CANDIDATE_STAY_REQUEST" set modified_at=now(), approval_status = 'Approved', status_notes=' Your request has been Approved.'||status_description, modified_by=given_modified_by where stay_id=application_idfld;
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


-------------------------------------------------------------------------process_workflow_student-------------------------------------------------------------------------------------------------------------------------------------------

-- DROP FUNCTION schooldev.process_workflow_student(int8, varchar, varchar, varchar, varchar);

CREATE OR REPLACE FUNCTION schooldev.process_workflow_student(given_wf_row_id bigint, given_date character varying, given_status character varying, given_reason character varying, given_approval_notes character varying, given_modified_by character varying)
    RETURNS SETOF student_workflow_type_tosendemail
    LANGUAGE plpgsql
AS $function$

declare
    approvel_levelfld bigint;
    request_idfld bigInt;
    authentication_typefld varchar;
    status_all_approved_in_same_level boolean:=false;
    count_of_pending int :=0;
    list_size int :=0;
    description text :='';
    r student_workflow_type_toSendEmail%rowtype;

BEGIN
    -- Here updating the Specific status
    --START TRANSACTION ISOLATION LEVEL READ COMMITTED;
    if given_status!='OverrideApproved' then
        UPDATE schooldev."IIT_W_STUDENT_WORKFLOW"    SET modified_at=now(),  status=given_status, approval_notes=given_approval_notes,modified_by=given_modified_by  WHERE id=given_WF_row_id
                                                                                                                                                                       and modified_at =given_date :: timestamp
                                                                                                                                                                       and (status='Pending' or status='Rejected' or status='Default');
    else
        UPDATE schooldev."IIT_W_STUDENT_WORKFLOW"    SET  modified_at=now(), status='Approved', approval_notes=given_approval_notes,modified_by=given_modified_by  WHERE id=given_WF_row_id
                                                                                                                                                                     and modified_at =given_date :: timestamp;
    end if;

-- Get the level, request id, authentication_type  of current record
    SELECT approval_level,request_id, authentication_type INTO approvel_levelfld,request_idfld, authentication_typefld FROM schooldev."IIT_W_STUDENT_WORKFLOW" WHERE id=given_WF_row_id;


    --if the given_status is rejected, then the student status is directly updated as 'Rejected'
    if given_status='Rejected' then
        update schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST" set modified_at=now(), status = given_status, reject_description=given_reason, status_notes=' Your request has been rejected for the following reason:'||given_reason,modified_by=given_modified_by  where request_id=request_idfld;
        update schooldev."IIT_W_STUDENT_WORKFLOW" set modified_at=now(), reject_description = given_reason,modified_by=given_modified_by where id=given_wf_row_id;
        update schooldev."IIT_W_STUDENT_WORKFLOW" set modified_at=now(), status=given_status,modified_by=given_modified_by where id=given_wf_row_id;
    else
        begin
            -- Decide whether all other people have approved, who are in the same level
            if authentication_typefld='a' THEN
                begin
                    select count(*) into count_of_pending FROM schooldev."IIT_W_STUDENT_WORKFLOW" WHERE  approval_level=approvel_levelfld and request_id=request_idfld and (status ='Rejected' or status ='Pending' ) ;
                    IF count_of_pending=0 THEN
                        status_all_approved_in_same_level=true;
                    END If;

                end;
            end if;
            -- if all have approved,  Set all level+1 records - status='Pending', return level+1 of records,


            if (authentication_typefld='o' or (authentication_typefld='a' and status_all_approved_in_same_level=true)) and (given_status='Approved' or given_status='OverrideApproved') THEN
                BEGIN
                    UPDATE schooldev."IIT_W_STUDENT_WORKFLOW"    SET  modified_at=now(), status='Pending',modified_by=given_modified_by  WHERE   request_id=request_idfld and
                        approval_level=approvel_levelfld+1  and status='Default';

                    --get the count of next level validatiors. if there are next level validatiors, student's status will be updated to 'Pending' (from validating)
                    -- if there is no next level approval validators and the given status is 'Approved' the student's status will be updated to 'Approved'.
                    select count(student_id) into list_size from schooldev."IIT_W_STUDENT_WORKFLOW" where
                        request_id=request_idfld and
                        approval_level=approvel_levelfld+1 and status='Pending';

                    if(list_size>0) then
                        update schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST" set modified_at=now(), status = 'Pending',modified_by=given_modified_by where request_id=request_idfld;
                    end if;
                    if(list_size=0 or list_size=null) then
                        if given_approval_notes is not null then
                            description = ' Upon approval the validator has added a comment, please make a note of it:'||given_approval_notes;
                        else description ='' ;
                        end if;

                        if given_status!='OverrideApproved' then
                            update schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST" set modified_at=now(),  status = given_status, status_notes=' Your request has been Approved.'||description,modified_by=given_modified_by where request_id=request_idfld;
                        else
                            update schooldev."IIT_W_STUDENT_APPOINTMENT_REQUEST" set modified_at=now(), status = 'Approved', status_notes=' Your request has been Approved.'||description,modified_by=given_modified_by where request_id=request_idfld;
                        end if;
                    end if;
                    -- raise EXCEPTION 'Status%',status_all_approved_in_same_level;
                    for r in select id, request_id, student_id, authority_type, approval_level,
                                    validator_email, validator_name, authentication_type, category, status, created_by, created_at,
                                    modified_by, modified_at, active_flag, school_id
                             FROM schooldev."IIT_W_STUDENT_WORKFLOW" where
                                 request_id=request_idfld and
                                 approval_level=approvel_levelfld+1 and status='Pending' order by approval_level, id loop
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
-------------------------------------------------------------------END--------------------------------------------------------------------------------------------

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
            and (messid is null or messid=0)

        then
            raise notice 'ifloop: %',  'in';
for res in
select a.coupon_id, a.request_id,coupon_number,b.candidate_name,to_char(validity_from_date, dateFormat),to_char(validity_to_date, dateFormat),
       coupon_type,coupon_used_status,to_char(a.created_at, dateFormat),mess_name,b.student_id
from schooldev."IITM_GUEST_COUPON_MAPPINGS" a
         left join schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" b on (b.request_id=a.request_id and b.active_flag ='Y')
         left join schooldev."MESS_MASTER" mess on (mess.mess_master_id=b.mess_id and mess.active_flag='Y')
where a.active_flag='Y' and (b.payment_status = 'Success' or (b.category='IITM Faculty' and b.approval_status='Approved'))
  and b.created_at::date =now()::date
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
         left join schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" b on (b.request_id=a.request_id and b.active_flag ='Y')
         left join schooldev."MESS_MASTER" mess on (mess.mess_master_id=b.mess_id and mess.active_flag='Y')
where a.active_flag='Y' and (b.payment_status = 'Success' or (b.category='IITM Faculty' and b.approval_status='Approved'))
  and (case when (candidatename is not null and candidatename<>'') then (lower(candidate_name) like lower('%'||candidatename||'%')) else 1=1 end)
  and (case when (couponused is not null and couponused<>'' and couponused<>'0') then lower(coupon_used_status) = lower(couponused) else 1=1 end)
  and (case when (requestid is not null and requestid<>0) then (a.request_id = requestid) else 1=1 end )
  and (case when (diningfrom::text <>'null' and diningfrom::text is not null) then  validity_from_date::date >=diningfrom::date else 1=1 end)
  and (case when (diningto::text <>'null' and diningto::text is not null ) then  validity_to_date::date <= diningto::date else 1=1 end)
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
where a.active_flag='Y' and (b.payment_status = 'Success' or (b.category='IITM Faculty' and b.approval_status='Approved')) --and b.created_at::date >=now()::date-7
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
         left join schooldev."IITM_GUEST_COUPON_PAYMENT_ADVICE" b on (b.request_id=a.request_id and b.active_flag ='Y')
         left join schooldev."MESS_MASTER" c on (c.mess_master_id=b.mess_id and c.active_flag='Y')
where a.active_flag='Y' and (b.payment_status = 'Success' or (b.category='IITM Faculty' and b.approval_status='Approved'))
  and b.mess_id=loginmessid

  and (case when (candidatename is not null and candidatename<>'') then (lower(candidate_name) like lower('%'||candidatename||'%')) else 1=1 end)
  and (case when (couponused is not null and couponused<>'' and couponused<>'0') then lower(coupon_used_status) = lower(couponused) else 1=1 end)
  and (case when (requestid is not null and requestid<>0) then (a.request_id = requestid) else 1=1 end )
  and (case when (diningfrom::text <>'null' and diningfrom::text is not null) then  validity_from_date::date >=diningfrom::date else 1=1 end)
  and (case when (diningto::text <>'null' and diningto::text is not null ) then  validity_to_date::date <= diningto::date else 1=1 end)
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

   select *  from  schooldev.guest_coupon_issued_list(NULL,NULL,'0',NULL,NULL,NULL,NULL,'0','Caterer','fs.nv')
   select *  from  schooldev.guest_coupon_issued_list(NULL,NULL,'0',NULL,NULL,NULL,NULL,'0','SENIOR COOK','sgr.ms')
   select *  from  schooldev.guest_coupon_issued_list('','0','0','null','null','null','null','97','SOFTWAREADMIN','triesten')
   select *  from  schooldev.guest_coupon_issued_list('','0','0','null','null','null','null','0','SOFTWAREADMIN','triesten')

    */


$function$
;

---------------------------------------------------------May 21, 2026-------------------------------------------------------
-- DROP FUNCTION schooldev.student_vacating_hostel(varchar, varchar, varchar, varchar, varchar, int4, varchar, varchar, varchar, varchar, int4, varchar, varchar);

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
                where lower(h.user_name)=lower(username) and a.school_id=1 and a.active_flag='Y' order by a.created_at desc
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
                order by  a.created_at desc
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
                where (lower(h.ldap_username)=lower(username) or lower(h.associate_ldap_username)=lower(username)) and a.school_id='1' and a.active_flag='Y' and wrk.status<>'AutoApproved' order by a.created_at desc
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
                order by a.created_at desc
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
----------------------------------------------------------END------------------------------------------------------------------------
-----------------------------------------------------------Jun 05, 2026---------------------------------------------------------------
alter table schooldev."IITM_CONVOCATION_ACCOMMODATION" add column hostel_name varchar(128) default null;
---------------------------------------------------------END-------------------------------------------