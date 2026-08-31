package com.iitm.hosteldine.repository.hostel;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.iitm.hosteldine.model.hostel.CompleteStudentApplicationView;

public interface CompleteStudentApplicationViewRepository extends JpaRepository<CompleteStudentApplicationView, Long> {

	@Query("SELECT s FROM CompleteStudentApplicationView s"
			+ " WHERE s.studentId = :studentId " + "AND ("
			+ " (:appointmentFrom BETWEEN s.stayFrom AND s.stayTo)"
			+ " OR (:appointmentTo BETWEEN s.stayFrom AND s.stayTo)"
			+ " OR (s.stayFrom >= :appointmentFrom AND s.stayTo <= :appointmentTo)" + ")"
			+ " AND s.requestId <> :requestId"
			+ " AND s.status NOT IN ('Deleted', 'Cancelled', 'Rejected','CancelledAfterApproval') order by s.studentId desc limit 1")
	Optional<CompleteStudentApplicationView> getRecentStatus(String studentId, LocalDate appointmentFrom,
															 LocalDate appointmentTo, Long requestId);

}