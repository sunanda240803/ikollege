create schema archive;
CREATE TYPE archive.archive_table_record AS (table_name text, column_name TEXT);

drop table if exists archive.archive_log;
create table archive.archive_log
(
    id                           bigserial               not null
        constraint archive_log_pk
            primary key,
    archive_id                   timestamp               not null,
    created_at                   timestamp default now() not null,
    segment                      varchar                 not null,
    total_records                integer,
    remaining_records            integer,
    time_taken                   bigint,
    time_remaining               bigint,
    estimated_time_of_completion timestamp,
    additional_log               varchar
);

create table archive.archive_table_master
(
    id          bigserial,
    table_name  varchar                                not null,
    column_name varchar                                not null,
    active_flag varchar default 'Y'::character varying not null,
    col_order   bigint
);
alter table archive.archive_table_master owner to postgres;
alter table archive.archive_table_master drop constraint if exists archive_table_master_unique;
alter table archive.archive_table_master add constraint archive_table_master_unique unique (table_name, column_name);

INSERT INTO archive.archive_table_master (table_name, column_name, active_flag, col_order) 
VALUES ('EVENT_ACCOMMODATION_STUDENT_DETAILS', 'student_id = %L', 'Y', 1),
       ('EXTRA_COURSE_STUDENTS_PREFERENCE', 'student_id = %L', 'Y', 2),
       ('FOOD_COURT_LEDGER', 'student_id = %L', 'Y', 3),
       ('GROUP_PRIORITY_MESS', 'group_id in (select n_gmd_group_id from schooldev."GROUP_MASTER_DETAILS" where v_gmd_created_student_id = %L)', 'Y', 4),
       ('GROUP_MASTER_DETAILS', 'v_gmd_created_student_id = %L', 'Y', 5),
       ('GUEST_ROOM_ALLOTMENT_INFO', 'studentid = %L', 'Y', 6),
       ('GUEST_FILES_INFORMATION', 'request_id in (select id from schooldev."GUEST_ACCOMMODATION_REQUEST" where student_id = %L)', 'Y', 7),
       ('GUEST_ACCOMMODATION_GUEST_DETAILS', 'request_id in (select id from schooldev."GUEST_ACCOMMODATION_REQUEST" where student_id = %L)', 'Y', 8),
       ('GUEST_ACCOMMODATION_REQUEST', 'student_id = %L', 'Y', 9),
       ('HOSTEL_BIOMETRIC_LOGS', 'studentid = %L', 'Y', 10),
       ('HOSTEL_ROOM_ALLOTMENT_INFO', 'student_id = %L', 'Y', 11),
       ('HOSTEL_VACATING_ALLOWED_STUDENT', 'student_id = %L', 'Y', 13),
       ('IITM_STUDENT_ROOM_ASSET_DETAILS', 'student_id = %L', 'Y', 14),
       ('IITM_GUEST_COUPON_PAYMENT_ADVICE', 'student_id = %L', 'Y', 15),
       ('IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST', 'student_id = %L', 'Y', 16),
       ('IIT_W_MESS_REBATE_DOC_INFORMATION', 'rebate_id in (select id from schooldev."IIT_A_MESS_REBATE" where student_id = %L)', 'Y', 17),
       ('IIT_A_MESS_REBATE_WORKFLOW', 'request_id in (select id from schooldev."IIT_A_MESS_REBATE" where student_id = %L)', 'Y', 18),
       ('IIT_A_MESS_REBATE', 'student_id = %L', 'Y', 19),
       ('IIT_HDC_COMPLAINT_FORMDETAILS', 'student_id = %L', 'Y', 20),
       ('IIT_W_HELP_DESK', 'student_id = %L', 'Y', 21),
       ('IIT_W_STUDENT_APPOINTMENT_REQUEST', 'student_id = %L', 'Y', 22),
       ('IIT_W_STUDENT_FILES_INFORMATION', 'student_id = %L', 'Y', 23),
       ('SELF_ALLOTMENT_FEE_NOT_PAID_LIST', 'student_id = %L', 'Y', 24),
       ('IIT_W_STUDENT_WORKFLOW', 'student_id = %L', 'Y', 25),
       ('IIT_W_VACATING_HOSTEL_STUDENT_WORKFLOW', 'student_id = %L', 'Y', 26),
       ('MESS_BILLING_B', 'student_id = %L', 'Y', 27),
       ('MESS_BILLING_A', 'student_id = %L', 'Y', 28),
       ('MESS_OPENING_BAL', 'acchead = %L', 'Y', 29),
       ('MESS_QR_APPLICATION', 'student_id = %L', 'Y', 30),
       ('MESS_TO_CARD_AMOUNT_TRANSFER', 'student_id = %L', 'Y', 31),
       ('RFID_MAPPINGS', 'student_id = %L', 'Y', 32),
       ('SECONDARY_ROLES', 'user_id = %L', 'Y', 33),
       ('SHAASTRA_SAARANG_PURCHASE_CLAIM', 'student_id = %L', 'Y', 34),
       ('SHOW_STUDENT_DETAILS', 'student_id = %L', 'Y', 35),
       ('SICK_FOOD_DELIVERY_STATUS', 'request_id in (select id from schooldev."SICK_FOOD_REQUEST" where student_id = %L)', 'Y', 36),
       ('SICK_FOOD_REQUEST', 'student_id = %L', 'Y', 37),
       ('STUDENT_BIO_DATA_FAMILY_INFO', 'bio_data_id in (select bio_data_id from schooldev."STUDENT_BIO_DATA_FORM_DETAILS" where student_id = %L)', 'Y', 38),
       ('STUDENT_BIODATA_LIST', 'student_id = %L', 'Y', 39),
       ('STUDENT_BIO_DATA_FORM_DETAILS', 'student_id = %L', 'Y', 40),
       ('STUDENT_BLACK_LIST_DETAILS', 'student_id = %L', 'Y', 41),
       ('STUDENT_COMPLAINT_DETAILS', 'student_id = %L', 'Y', 42),
       ('STUDENT_COMPLETE_LISTING', 'student_id = %L', 'Y', 43),
       ('STUDENT_DETAILS_FAMILY_INFO', 'student_id = %L', 'Y', 44),
       ('STUDENT_DEVICE_REGISTRATION_DETAILS', 'student_id = %L', 'Y', 45),
       ('STUDENT_EXCEESS_MESS_DETAILS', 'student_id = %L', 'Y', 46),
       ('STUDENT_EXCHANGE_PROGRAM', 'student_id = %L', 'Y', 47),
       ('STUDENT_HOSTEL_PAYMENTS', 'student_id = %L', 'Y', 48),
       ('STUDENT_HOSTEL_PAYMENT_LATE_FEE_DETAILS', 'student_id = %L', 'Y', 49),
       ('STUDENT_MESS_CATERER_FEEDBACK', 'student_id = %L', 'Y', 50),
       ('STUDENT_MESS_CHANGE_WORKFLOW', 'student_id = %L', 'Y', 51),
       ('STUDENT_MESS_GROUP_PRIORITY_REGISTRATION', 'group_id in (select group_id from schooldev."STUDENT_MESS_GROUP_DETAILS" where created_student_id = %L)', 'Y', 52),
       ('STUDENT_MESS_GROUP_DETAILS', 'created_student_id = %L', 'Y', 53),
       ('STUDENT_MESS_LOGIN_ISSUE_PRIORITY', 'studentid = %L', 'Y', 54),
       ('STUDENT_MESS_PRIORITY_REGISTRATION', 'studentid = %L', 'Y', 55),
       ('STUDENT_WELLNESS_FOLLOWUP_DATA', 'wellness_id in (select wellness_id from schooldev."STUDENT_WELLNESS_CATEGORICAL_DATA" where student_id = %L)', 'Y', 56),
       ('STUDENT_WELLNESS_CATEGORICAL_DATA', 'student_id = %L', 'Y', 57),
       ('SUGGESTIONS_COMPLAINTS_MAIL_LOGGER', 'v_scml_created_by = %L', 'Y', 58),
       ('TEMPORARY_VACATING_FORM_ASSET_DETAILS', 'vacating_form_id in (select vacating_form_id from schooldev."TEMPORARY_VACATING_FORM" where student_id = %L)', 'Y', 59),
       ('TEMPORARY_VACATING_FORM', 'student_id = %L', 'Y', 60),
       ('TEMP_HOSTEL_BIOMETRIC_LOGS', 'studentid = %L', 'Y', 61),
       ('TEMP_MESS_REGISTRATION', 'studentid = %L', 'Y', 62),
       ('TEMP_STUDENT', 'studentid = %L', 'Y', 63),
       ('VACATIONSTUDENT_DETAILS', 'v_vsd_student_id = %L', 'Y', 64),
       ('VACATIONSTUDENT_MESS_DETAILS', 'v_vsmd_student_id = %L', 'Y', 65),
       ('VACATION_MESS_ALLOWED_STUDENT', 'student_id = %L', 'Y', 66),
       ('VACATION_TEMP_MESS_REGISTRATION', 'studentid = %L', 'Y', 67),
       ('biometric_ip_queue_details', 'student_id = %L', 'Y', 68),
       ('STUDENT_MESS_DETAILS', 'student_id = %L', 'Y', 69),
       ('STUDENT_MESS_EXCHANGE', 'v_sme_requested_studentid = %L', 'Y', 70),
       ('STUDENT_MESS_EXCHANGE', 'v_sme_exchange_studentid = %L', 'Y', 70),
       ('COURSE_ALLOCATION_INFO', 'student_id = %L', 'Y', 71),
       ('dost_attendance_waiver', 'student_id = %L', 'Y', 72),
       ('STUDENT_ROLLNO_CHANGE', 'studentid = %L', 'Y', 73),
       ('STUDENT_MESS_TIME_CONFIQURATON', 'smtc_student_id = %L', 'Y', 74),
       ('STUDENT_DETAILS_INFO', 'student_id = %L', 'Y', 1000),
       ('SETTLEMENT_HISTORY', 'student_id = %L', 'Y', 1001);

drop table schooldev."STUDENT_CARD_MAPPINGS";

create function archive.create_archive_tables() returns void
    language plpgsql
as
$$
declare
    archive_query  TEXT;

    archive_table_record archive.archive_table_record;
    archive_table_records archive.archive_table_record[];

begin

    for archive_table_record in select table_name, column_name from archive.archive_table_master where active_flag = 'Y' order by col_order
        loop
            archive_table_records := archive_table_records || archive_table_record;
            archive_query := FORMAT('create table if not exists archive.%I (LIKE schooldev.%I INCLUDING ALL);',
                                    archive_table_record.table_name, archive_table_record.table_name);
            execute archive_query;
        end loop;
    create table if not exists archive."MESS_LEDGER_B" (LIKE schooldev."MESS_LEDGER_B" INCLUDING ALL);
    create table if not exists archive."IIT_W_STUDENT_BULK_APPOINTMENT" (LIKE schooldev."IIT_W_STUDENT_BULK_APPOINTMENT" INCLUDING ALL);
    create table if not exists archive."IIT_W_STUDENT_BULK_APPOINTMENT_DETAILS" (LIKE schooldev."IIT_W_STUDENT_BULK_APPOINTMENT_DETAILS" INCLUDING ALL);
    create table if not exists archive."MESS_LEDGER_A" (LIKE schooldev."MESS_LEDGER_A" INCLUDING ALL);
    create table if not exists archive."IIT_W_STUDENT_MASTER_BULK_APPOINTMENT" (LIKE schooldev."IIT_W_STUDENT_MASTER_BULK_APPOINTMENT" INCLUDING ALL);

    raise notice 'Archive Tables creation successful.';
end;
$$;

--drop function archive.insert_archive_log(m_archive_id timestamp, m_segment varchar, m_total_records integer, m_remaining_records integer, m_time_taken bigint, m_time_remaining bigint, m_estimated_time_of_completion timestamp, m_additional_log varchar);
create or replace function archive.insert_archive_log(m_archive_id timestamp without time zone, m_segment character varying, m_total_records integer, m_remaining_records integer, m_time_taken bigint, m_time_remaining bigint, m_estimated_time_of_completion timestamp without time zone, m_additional_log character varying) returns void
    language plpgsql
as
$$
begin
    CREATE EXTENSION if not exists dblink;
    perform public.dblink_exec(
                   'host=127.0.0.1 port=10691 dbname=d1 user=postgres password=W$lkl3TlPo'::text,
                   FORMAT('insert into archive.archive_log(archive_id, segment, total_records, remaining_records, time_taken, time_remaining, estimated_time_of_completion, additional_log) values (%L, %L, %L, %L, %L, %L, %L, %L);',
                          m_archive_id, m_segment, m_total_records, m_remaining_records, m_time_taken, m_time_remaining, m_estimated_time_of_completion, m_additional_log)
           );
end;
$$;

drop function if exists archive.archive_settled_students(integer);
drop function if exists archive.archive_settled_students_by_batch(integer, varchar);
drop function if exists archive.archive_settled_students_by_batch(timestamp, integer, varchar);

create or replace function archive.archive_settled_students_by_batch(m_archive_id timestamp, lmt integer, batch varchar) returns text
    language plpgsql
as
$$
declare
    running_count INTEGER := 0;
    settlement record;
    archive_id timestamp without time zone;

    r record;
    voucher_list_b TEXT ARRAY;
    ledger_b_count INTEGER;
    archived_voucher_no TEXT ARRAY;
    partial_archived_voucher_no TEXT ARRAY;
    total_amount INTEGER;
    archive_total_amount INTEGER;
    unarchived_amount INTEGER;
    ledger_a_exists TEXT;
    part text;

    bulk_appointment_ids BIGINT ARRAY;
    archived_bulk_appointment_id TEXT ARRAY;
    partial_archived_bulk_appointment_id TEXT ARRAY;
    id_part bigint;

    archive_query  TEXT;
    archive_table_record archive.archive_table_record;
    archive_table_records archive.archive_table_record[];

    start_time timestamp;
    curr_time timestamp;
    elapsed_time interval;
    etc interval;
    time_taken interval;
begin
    if m_archive_id is null then archive_id := clock_timestamp();
    else archive_id = m_archive_id;
    end if;
    start_time := clock_timestamp();
    curr_time := clock_timestamp();
    perform archive.insert_archive_log(archive_id, 'archive started'::varchar, null::integer, null::integer, null::bigint, null::bigint, null::timestamp, 'Archiving process started.'::varchar);
    raise notice 'Archive ID: %', archive_id;

    perform archive.create_archive_tables();

    perform archive.insert_archive_log(archive_id, 'archive table creation', null::integer, null::integer, null::bigint, null::bigint, null::timestamp, 'Attempting to create archive tables.');
    for archive_table_record in select table_name, column_name from archive.archive_table_master where active_flag = 'Y' order by col_order
        loop
            archive_table_records := archive_table_records || archive_table_record;
        end loop;
    perform archive.insert_archive_log(archive_id, 'archive table creation', null::integer, null::integer, null::bigint, null::bigint, null::timestamp, 'All Archive tables created successfully.');

    elapsed_time = 0;
    perform archive.insert_archive_log(archive_id, 'before loop', lmt, null::integer, null::bigint, null::bigint, null::timestamp, 'Checking Settlement History by Student Id');
    raise notice 'Checking Settlement History by Student Id with limit %', lmt;
    for settlement in select distinct(student_id) from schooldev."SETTLEMENT_HISTORY"
                      where active_flag  = 'Y' and case when batch is not null then substring(student_id from 3 for 2) = batch else true end
                      order by 1 limit lmt
    loop
        running_count = running_count +1;
        raise notice 'Student Id: %', settlement.student_id;

        /*Steps to follow for bulk data's child table starts here*/
        INSERT INTO archive."MESS_LEDGER_B" SELECT * FROM schooldev."MESS_LEDGER_B"
        WHERE acchead = settlement.student_id;

        for r in select distinct(voucher_no) from schooldev."MESS_LEDGER_B" where acchead = settlement.student_id
        loop
            if not array_position(voucher_list_b, r.voucher_no) is null then continue; end if;
            voucher_list_b := voucher_list_b || r.voucher_no;
-- 			raise notice 'r.voucher_no: %', r.voucher_no;
        end loop;

        delete from schooldev."MESS_LEDGER_B" WHERE acchead = settlement.student_id;
        /*Steps to follow for bulk data's child table ends here*/
        INSERT INTO archive."IIT_W_STUDENT_BULK_APPOINTMENT_DETAILS" SELECT * FROM schooldev."IIT_W_STUDENT_BULK_APPOINTMENT_DETAILS"
        WHERE bulk_appointment_id in (SELECT bulk_appointment_id FROM schooldev."IIT_W_STUDENT_BULK_APPOINTMENT"
                                      WHERE student_id = settlement.student_id);
        INSERT INTO archive."IIT_W_STUDENT_BULK_APPOINTMENT" SELECT * FROM schooldev."IIT_W_STUDENT_BULK_APPOINTMENT"
        WHERE student_id = settlement.student_id;

        for r in select distinct(bulk_appointment_id) from schooldev."IIT_W_STUDENT_BULK_APPOINTMENT" where student_id = settlement.student_id
        loop
            if not array_position(bulk_appointment_ids, r.bulk_appointment_id) is null then continue; end if;
            bulk_appointment_ids := bulk_appointment_ids || r.bulk_appointment_id;
--            raise notice 'r.bulk_appointment_id: %', r.bulk_appointment_id;
        end loop;

        Delete FROM schooldev."IIT_W_STUDENT_BULK_APPOINTMENT_DETAILS"
        WHERE bulk_appointment_id in (SELECT bulk_appointment_id FROM schooldev."IIT_W_STUDENT_BULK_APPOINTMENT"
                                      WHERE student_id = settlement.student_id);
        delete from schooldev."IIT_W_STUDENT_BULK_APPOINTMENT" WHERE student_id = settlement.student_id;

        foreach archive_table_record in array archive_table_records
        loop
--            raise notice 'Record: %', archive_table_record;
            archive_query := FORMAT('
                    INSERT INTO archive.%I select * from schooldev.%I where ' || archive_table_record.column_name || ';
                    DELETE FROM schooldev.%I where ' || archive_table_record.column_name || ';',
                                        archive_table_record.table_name, archive_table_record.table_name, settlement.student_id,
                                        archive_table_record.table_name, settlement.student_id);
			  raise notice 'Query: %', archive_query;
            execute archive_query;
        end loop;
        time_taken = (clock_timestamp() - curr_time);
        elapsed_time = elapsed_time + time_taken;
        etc = ((lmt - running_count) * (elapsed_time / running_count));
        perform archive.insert_archive_log(archive_id, 'common archive tables'::varchar, lmt::integer,
                                           running_count::integer, (EXTRACT(EPOCH from time_taken) * 1000)::bigint,
                                           (EXTRACT(EPOCH from etc) * 1000)::bigint, (clock_timestamp() + etc)::timestamp,
                                           ('Completed for the Student ID: ' || settlement.student_id)::varchar);
--         raise notice 'Complete for %,\t%, \tTime Taken: %, \tElapsed: %, \tETC: %, \t%', running_count, settlement.student_id,
--             time_taken, elapsed_time, etc, clock_timestamp() + etc;
        curr_time := clock_timestamp();
    end loop;
    perform archive.insert_archive_log(archive_id, 'after common archive tables', null::integer, null::integer, null::bigint, null::bigint, null::timestamp, 'Archiving complete for all sub tables. Archiving Mess Ledger A.');
    perform archive.insert_archive_log(archive_id, 'Archive Data', null::integer, null::integer, null::bigint, null::bigint, null::timestamp, 'Archiving process 1 / 3 complete. Archiving Mess Ledger A.');

    raise notice 'Archiving complete for all sub tables. Archiving Mess Ledger A.';
    lmt = cardinality(voucher_list_b);
    perform archive.insert_archive_log(archive_id, 'before messLedgerA archive', lmt, null::integer, null::bigint, null::bigint, null::timestamp, 'Archiving voucher list of ' || lmt);
--     raise notice 'voucher_list_b: %', lmt;
    running_count = 0;
    elapsed_time = 0;
    if voucher_list_b is not null then
        foreach part in array voucher_list_b
        loop
            running_count = running_count +1;
            select count(1) into ledger_b_count from schooldev."MESS_LEDGER_B"  WHERE voucher_no = part;
            select voucher_no into ledger_a_exists from archive."MESS_LEDGER_A" where voucher_no = part;
            if ledger_a_exists is null then
                insert into archive."MESS_LEDGER_A" select * from schooldev."MESS_LEDGER_A"
                where voucher_no = part;
            end if;
            if ledger_b_count = 0 then
                delete from schooldev."MESS_LEDGER_A" where voucher_no = part;
                archived_voucher_no := archived_voucher_no || part;
                update archive."MESS_LEDGER_A" set amount = (
                    select SUM(amount) from archive."MESS_LEDGER_B" where voucher_no = part
                ) where voucher_no = part;

            else
                total_amount = 0; archive_total_amount = 0; unarchived_amount = 0;
                select amount into total_amount from schooldev."MESS_LEDGER_A" where voucher_no = part;
                select amount into archive_total_amount from archive."MESS_LEDGER_A" where voucher_no = part;
                select SUM(amount) into unarchived_amount from schooldev."MESS_LEDGER_B" where voucher_no = part;
                if archive_total_amount is not null then total_amount = total_amount + archive_total_amount; end if;
                if total_amount > 0 and unarchived_amount > 0 then
                    update schooldev."MESS_LEDGER_A" set amount = unarchived_amount where voucher_no = part;
                    update archive."MESS_LEDGER_A" set amount = total_amount - unarchived_amount
                    where voucher_no = part;
                    partial_archived_voucher_no := partial_archived_voucher_no || part;
                end if;
            end if;
            time_taken = (clock_timestamp() - curr_time);
            elapsed_time = elapsed_time + time_taken;
            etc = ((lmt - running_count) * (elapsed_time / running_count));
            perform archive.insert_archive_log(archive_id, 'messLedgerA archive', lmt::integer,
                                               running_count::integer, (EXTRACT(EPOCH from time_taken) * 1000)::bigint,
                                               (EXTRACT(EPOCH from etc) * 1000)::bigint, (clock_timestamp() + etc)::timestamp,
                                               'Completed for the voucher no: ' || part);
--             raise notice 'Complete for %,%,\t Time Taken: %, \tElapsed: %, \tETC: %, \t%', running_count, part,
--                 time_taken, elapsed_time, etc, clock_timestamp() + etc;
            curr_time := clock_timestamp();
        end loop;
    end if;
--     raise notice 'voucher_list_b: %', voucher_list_b;
--     raise notice 'archived_voucher_noo: %', archived_voucher_no;
--     raise notice 'partial_archived_voucher_no: %', partial_archived_voucher_no;
    perform archive.insert_archive_log(archive_id, 'after messLedgerA archive', null::integer, null::integer, (EXTRACT(EPOCH from (clock_timestamp() - start_time)) * 1000)::bigint, null::bigint, null::timestamp, 'Archiving Mess Ledger A complete.');
    perform archive.insert_archive_log(archive_id, 'Archive Data', null::integer, null::integer, null::bigint, null::bigint, null::timestamp, 'Archiving process 2 / 3 complete. Archiving Bulk Appointment.');
    raise notice 'After Mess Ledger A update: %, Elapsed: %', (clock_timestamp() - curr_time), (clock_timestamp() - start_time);
    curr_time := clock_timestamp();
    raise notice 'Archiving complete for Mess Ledger A. Archiving Student Bulk Appointment.';

    lmt = cardinality(bulk_appointment_ids);
    perform archive.insert_archive_log(archive_id, 'before bulk appointment archive', lmt, null::integer, null, null::bigint, null::timestamp, 'Archiving Bulk Appointment list of ' || lmt);
    raise notice 'bulk_appointment_ids: %', lmt;
    if bulk_appointment_ids is not null then
        foreach id_part in array bulk_appointment_ids
        loop
            select count(1) into ledger_b_count from schooldev."IIT_W_STUDENT_BULK_APPOINTMENT"  WHERE bulk_appointment_id = id_part;
            select bulk_appointment_id::text into ledger_a_exists from archive."IIT_W_STUDENT_MASTER_BULK_APPOINTMENT" where bulk_appointment_id = id_part;
            if ledger_a_exists is null then
                insert into archive."IIT_W_STUDENT_MASTER_BULK_APPOINTMENT" select * from schooldev."IIT_W_STUDENT_MASTER_BULK_APPOINTMENT"
                where bulk_appointment_id = id_part;
            end if;
            if ledger_b_count = 0 then
                delete from schooldev."IIT_W_STUDENT_MASTER_BULK_APPOINTMENT" where bulk_appointment_id = id_part;
                archived_bulk_appointment_id := archived_bulk_appointment_id || id_part::text;
                update archive."IIT_W_STUDENT_MASTER_BULK_APPOINTMENT" set student_count = (
                    select count(1) from archive."IIT_W_STUDENT_BULK_APPOINTMENT" where bulk_appointment_id = id_part
                ) where bulk_appointment_id = id_part;

            else
                total_amount = 0; archive_total_amount = 0; unarchived_amount = 0;
                select count(1) into total_amount from schooldev."IIT_W_STUDENT_MASTER_BULK_APPOINTMENT" where bulk_appointment_id = id_part;
                select student_count into archive_total_amount from archive."IIT_W_STUDENT_MASTER_BULK_APPOINTMENT" where bulk_appointment_id = id_part;
                select SUM(1) into unarchived_amount from schooldev."IIT_W_STUDENT_BULK_APPOINTMENT" where bulk_appointment_id = id_part;
                if archive_total_amount is not null then total_amount = total_amount + archive_total_amount; end if;
                if total_amount > 0 and unarchived_amount > 0 then
                    update schooldev."IIT_W_STUDENT_MASTER_BULK_APPOINTMENT" set student_count = unarchived_amount where bulk_appointment_id = id_part;
                    update archive."IIT_W_STUDENT_MASTER_BULK_APPOINTMENT" set student_count = total_amount - unarchived_amount
                    where bulk_appointment_id = id_part;
                    partial_archived_bulk_appointment_id := partial_archived_bulk_appointment_id || id_part::text;
                end if;
            end if;
        end loop;
        perform archive.insert_archive_log(archive_id, 'after bulk appointment archive', null::integer, null::integer, null::bigint, null::bigint, null::timestamp,'Processed Appointment IDS: ' || (bulk_appointment_ids)::character varying);
        perform archive.insert_archive_log(archive_id, 'after bulk appointment archive', null::integer, null::integer, null::bigint, null::bigint, null::timestamp,'Completely Archived Appointment IDS: ' || archived_bulk_appointment_id::character varying);
        perform archive.insert_archive_log(archive_id, 'after bulk appointment archive', null::integer, null::integer, null::bigint, null::bigint, null::timestamp,'Partially Archived Appointment IDS: ' || partial_archived_bulk_appointment_id::character varying);
        perform archive.insert_archive_log(archive_id, 'after bulk appointment archive', null::integer, null::integer, null::bigint, null::bigint, null::timestamp,'Archiving Bulk Appointment complete.');
        raise notice 'bulk_appointment_ids: %', bulk_appointment_ids;
        raise notice 'archived_voucher_noo: %', archived_bulk_appointment_id;
        raise notice 'partial_archived_voucher_no: %', partial_archived_bulk_appointment_id;
    end if;
    curr_time := clock_timestamp();
    perform archive.insert_archive_log(archive_id, 'Final Log', null::integer, null::integer, null::bigint, null::bigint, null::timestamp, 'Total time taken: ' || curr_time - start_time || ' for ' || running_count);
    raise notice 'Total Time Taken: % for %', curr_time - start_time, running_count;
    return '';

/*
select archive.archive_settled_students_by_batch(now(), 400, '14);--runs for all the students.
select * from archive.archive_settled_students_by_batch(clock_timestamp(), 10, '10');--runs with static limit of 1000.
select archive.archive_settled_students_by_batch(now()::timestamp, 20, '06'::character varying);--runs for the mentioned limit.
select * from archive.archive_settled_students_by_batch(now()::timestamp, 2, '01');
select * from archive.archive_log where archive_id = '2025-04-19 09:01:52.103153';
select count(distinct(student_id)) from schooldev."SETTLEMENT_HISTORY" where active_flag = 'Y';
select substring(student_id from 3 for 2) as batch, cast(count(1) as varchar) as cnt
            from schooldev."SETTLEMENT_HISTORY"
            where active_flag  = 'Y'
            group by substring(student_id from 3 for 2)
            order by 1

CREATE TYPE archive.archive_table_record AS (table_name text, column_name TEXT);
*/
END;
$$;
