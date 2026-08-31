package com.iitm.hosteldine.repository.bulkappointment;

import com.iitm.hosteldine.dto.dean.StudentAccomBulkRequestDto;
import com.iitm.hosteldine.model.bulkappointment.StudentMasterBulkAppointmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StudentMasterBulkAppointmentRepository extends JpaRepository<StudentMasterBulkAppointmentEntity, Long> {

	Page<StudentMasterBulkAppointmentEntity> findAll(Pageable pageable);


	@Query(value = "SELECT * FROM schooldev.get_filtered_bulk_appointments( " +
			"    cast(:approval_status as varchar), " +
			"    cast(:event_name as varchar), " +
			"    cast(:to_date as date), " +
			"    cast(:from_date as date), " +
			"    cast(:created_at as date), " +
			"    cast(:loginid as varchar)," +
			"	 cast(:loginrole as varchar) " +
			");", nativeQuery = true)
	List<StudentAccomBulkRequestDto> getFilteredBulkAppointments(
			@Param("approval_status") String approvalStatus,
			@Param("event_name") String eventName,
			@Param("to_date") LocalDate toDate,
			@Param("from_date") LocalDate fromDate,
			@Param("created_at") LocalDate createdAt,
			@Param("loginid") String loginId,
			@Param("loginrole") String loginrole);

	@Query(value = """
    SELECT 
        ism.student_count as studentCount,
        ism.event_name as eventName,
		ism.from_date::DATE  as fromDate,
		ism.to_date::DATE as toDate,
        isb.stay_from as stayFrom,
        isb.stay_to as stayTo,
        isb.no_of_maleparticipants as noOfMaleParticipants,
        isb.no_of_femaleparticipants as noOfFemaleParticipants,
        ism.approval_status as approvalStatus,
        isb.session_period as sessionPeriod,
        CAST(ism.created_at AS date) AS createdAt,
        isb.dining,
        isb.breakfast_count as breakfastCount,
        isb.lunch_count as lunchCount,
        isb.created_by as createdBy,
        ism.bulk_appointment_id as bulkAppointmentId,
        isb.dinner_count as dinnerCount,
        (f.first_name || ' ' || f.last_name) as uploadedFacultyName
    FROM 
        schooldev."IIT_W_STUDENT_MASTER_BULK_APPOINTMENT" ism 
    JOIN 
        schooldev."IIT_W_STUDENT_BULK_APPOINTMENT_DETAILS" isb 
        ON (ism.bulk_appointment_id = isb.bulk_appointment_id AND isb.active_flag = 'Y') 
    LEFT JOIN 
        schooldev."FACULTY_PERSONAL_DETAILS" f
        ON (ism.created_by = f.faculty_id) 
    WHERE 
        ism.active_flag = 'Y' 
        AND (CAST(:eventFromDate AS date) IS NULL OR ism.from_date::date >= CAST(:eventFromDate AS date))
		AND (CAST(:eventToDate AS date) IS NULL OR ism.to_date::date <= CAST(:eventToDate AS date))
		AND (:approvalStatus IS NULL OR ism.approval_status = :approvalStatus)
    ORDER BY 
        uploadedFacultyName, ism.created_at, ism.from_date, ism.to_date
    """, nativeQuery = true)
	Page<Object[]> getFacultyAccommodationRequests(
			@Param("eventFromDate") LocalDate eventFromDate,
			@Param("eventToDate") LocalDate eventToDate,
			@Param("approvalStatus") String approvalStatus,
			Pageable pageable);

}