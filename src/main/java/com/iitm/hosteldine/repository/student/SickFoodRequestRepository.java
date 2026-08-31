package com.iitm.hosteldine.repository.student;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.staff.StaffDesignationMasterEntity;
import com.iitm.hosteldine.model.student.SickFoodRequestEntity;

public interface SickFoodRequestRepository extends JpaRepository<SickFoodRequestEntity, Long> {

	@Query("SELECT a FROM SickFoodRequestEntity a " + "WHERE a.activeFlag = :statusActive "
			+ "AND a.studentId = :studentId " + "AND (a.medicalReason ILIKE CONCAT('%', :search, '%') "
			+ "OR a.messType ILIKE CONCAT('%', :search, '%'))")
	Page<SickFoodRequestEntity> getMedicalReasonOrMessTypeAndActiveFlag(String statusActive, String studentId,
			String search, Pageable pageable);


	 Optional<SickFoodRequestEntity> findByIdAndActiveFlag(Long id, String statusActive);


	Optional<SickFoodRequestEntity> findByActiveFlagAndStudentIdIgnoreCaseAndRequestDate(String statusActive, String studentId,
			LocalDate requestDate);

	@Query("SELECT a FROM SickFoodRequestEntity a WHERE a.activeFlag = :statusActive AND a.studentId = :studentId")
	Page<SickFoodRequestEntity> getActiveFlagAndStudentId(String statusActive, String studentId, Pageable pageable);



	Optional<SickFoodRequestEntity> findTopByActiveFlagAndStudentIdIgnoreCaseAndRequestDate(String statusActive, String studentId,
			LocalDate requestDate);


	@Query("""
		    SELECT sfd.messSession AS mess_session, 
		           sfd.catererStatus AS caterer_status, 
		           sfd.studentDeliveryStatus AS stud_delivery_status
		    FROM SickFoodRequestEntity sf
		    JOIN SickFoodDeliveryStatusEntity sfd 
		      ON sf.id = sfd.sickFoodRequest.id
		    WHERE sf.studentId = :studentId
		      AND sf.activeFlag = 'Y'
		      AND sf.requestDate = CURRENT_DATE
		""")
		List<Map<String, Object>> getTodayRequests(String studentId);


    @Query(value = """
    select * from schooldev.sick_food_request_list(:userRole, :userLogin, :requestFrom, :requestTo, :studentId, :studentName, 
        :catererStatus, :studentDelivery, :messSession)
    """, nativeQuery = true)
    Object[] getSickFoodRequestList(String userRole, String userLogin, String requestFrom, String requestTo,
                                          String studentId, String studentName, String catererStatus, String studentDelivery,
                                          String messSession);
   
}