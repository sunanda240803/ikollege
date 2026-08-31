package com.iitm.hosteldine.repository.hostel;

import com.iitm.hosteldine.model.hostel.HostelBiometricTerminalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HostelBiometricTerminalRepository extends JpaRepository<HostelBiometricTerminalEntity, Long> {
    List<HostelBiometricTerminalEntity> findAllByActiveFlag(String activeFlag);

    String query1 = "select a.*, b.*, c.*, to_char(swipe_date, 'day') as swipe_day from schooldev.\"HOSTEL_BIOMETRIC_TERMINAL\" a " +
            "join schooldev.\"HOSTEL_BIOMETRIC_LOGS\" b on (a.terminal_ip = b.terminal_ip) left join schooldev.\"ALL_STUDENTS_DETAILS_VIEW\" c " +
            "on (c.student_id = b.studentid) where (swipe_time > to_timestamp(:fromTime)::time without time zone or " +
            "swipe_time < to_timestamp(:toTime)::time without time zone) ";

    String query2 = "and terminal_id = :terminalId ";

    String order = "order by id desc";

    @Query(value = query1 + order, nativeQuery = true)
    Page<Object[]> getLateEntriesListOnlyByTime(Long fromTime, Long toTime, Pageable pageable);

    @Query(value = query1 + query2 + order, nativeQuery = true)
    Page<Object[]> getLateEntriesListByTerminalIdAndTime(Long terminalId, Long fromTime, Long toTime, Pageable pageable);

    @Query(value = """
    select * from schooldev.hostel_login_biometric_list(:terminalId, :fromDate, :toDate, :genderType)
    """, nativeQuery = true)
    Page<Object[]> getGeneralLogDetails(Integer terminalId, String fromDate, String toDate, String genderType, Pageable pageable);
    
    @Query(value = """
    	    select a.*, b.*, c.student_name,
    	           (a.swipe_date || ' ' || a.swipe_time) as swipe_date_time,
    	           to_char(a.swipe_date, 'day') as swipe_day
    	    from schooldev."HOSTEL_BIOMETRIC_LOGS" a
    	    join schooldev."HOSTEL_BIOMETRIC_TERMINAL" b on (a.terminal_ip = b.terminal_ip)
    	    left join schooldev."ALL_STUDENTS_DETAILS_VIEW" c on TRIM(c.student_id) = TRIM(a.studentid)
    	    where TRIM(a.studentid) = TRIM(:studentId)
    	      and (
    	            (:fromDate is null or :fromDate = '') 
    	            or 
    	            (
    	                a.swipe_date between CAST(:fromDate AS date) and 
    	                CASE 
    	                    WHEN :toDate is null or :toDate = '' THEN CAST(NOW() AS date)
    	                    ELSE CAST(:toDate AS date)
    	                END
    	            )
    	          )
    	    order by a.id desc
    	    """, nativeQuery = true)
	Page<Object[]> getStudentWiseLogDetailsList(@Param("studentId") String studentId, @Param("fromDate") String fromDate, 
			@Param("toDate") String toDate, Pageable pageable );
	
}