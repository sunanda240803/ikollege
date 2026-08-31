package com.iitm.hosteldine.repository.student;

import com.iitm.hosteldine.model.student.SickFoodDeliveryStatusEntity;
import com.iitm.hosteldine.model.student.SickFoodRequestEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SickFoodDeliveryStatusRepository extends JpaRepository<SickFoodDeliveryStatusEntity, Long> {

	Optional<SickFoodDeliveryStatusEntity> findBySickFoodRequest(SickFoodRequestEntity existingEntity);

	@Query("SELECT b FROM SickFoodDeliveryStatusEntity b WHERE b.activeFlag = :statusActive AND  b.sickFoodRequest.id = :requestId ORDER BY b.id")
    List<SickFoodDeliveryStatusEntity> getSickFoodDeliveryStatuses(Long requestId,String statusActive);



	Optional<SickFoodDeliveryStatusEntity> findBySickFoodRequestIdAndMessSession(Long id, String messSession);

	List<SickFoodDeliveryStatusEntity> findAllBySickFoodRequest(SickFoodRequestEntity existingEntity);

	List<SickFoodDeliveryStatusEntity> findAllBySickFoodRequestIdAndActiveFlag(long id, String statusActive);

	Optional<SickFoodDeliveryStatusEntity> findByIdAndSickFoodRequestIdAndActiveFlag(Long id, Long id2,
			String statusActive);

	Optional<SickFoodDeliveryStatusEntity> findByIdAndActiveFlag(Long id, String statusActive);
	
	@Query(value = """
			SELECT  sfd.*,ms.session_name,ms.start_time,ms.end_time
			FROM schooldev."SICK_FOOD_DELIVERY_STATUS" sfd
			LEFT JOIN schooldev."MESS_SESSIONS" ms
			    ON ms.mess_id = :messIds
			    AND ms.session_name = sfd.mess_session
			    AND ms.active_flag = 'Y'
			    AND sfd.caterer_status = 'Pending'
			    AND (to_timestamp(ms.end_time, 'HH24:MI')::time - make_interval(mins => :thresholdTime)) < to_timestamp(:currentTimeString, 'HH24:MI')::time
			WHERE sfd.active_flag = :statusActive
			  AND sfd.request_id = :requestId
			""", nativeQuery = true)
	List<Object[]> findPendingDeliveries(@Param("messIds") int messIds, @Param("thresholdTime") int thresholdTime,
			@Param("currentTimeString") String currentTimeString, @Param("requestId") Long requestId,
			@Param("statusActive") String statusActive);


	@Query(value = """
			select
				sfd.*,
				ms.session_name,ms.start_time
			from
				schooldev."SICK_FOOD_REQUEST" sfr
			join schooldev."SICK_FOOD_DELIVERY_STATUS" sfd on
				sfd.request_id = sfr.id
				and sfd.active_flag = :statusActive
			left join schooldev."MESS_SESSIONS" ms
						    on
				ms.mess_id = :messIds
				and ms.session_name = sfd.mess_session
				and ms.active_flag = :statusActive
				and sfd.caterer_status = 'Pending'
			where
				sfr.active_flag = :statusActive
				and sfr.student_id = :studentId
				and sfr.request_date = :requestDate
				and to_timestamp(:currentTimeString,
				'HH24:MI')::time between (to_timestamp(ms.end_time,
				'HH24:MI')::time - make_interval(mins => :thresholdTime))
			                         and to_timestamp(ms.end_time,
				'HH24:MI')::time;
			""", nativeQuery = true)
	List<Object[]> getPendingDeliveriesToCallVendor(@Param("messIds") int messIds, @Param("thresholdTime") int thresholdTime,
													@Param("currentTimeString") String currentTimeString,
													@Param("studentId") String studentId,
													@Param("requestDate") LocalDate requestDate,
													@Param("statusActive") String statusActive);
	
	@Query(value = "SELECT * FROM schooldev.sick_food_request_list( " +
			" cast(:userRole as varchar), " +
			" cast(:userName as varchar), " +
			" cast(:requestFromdate as varchar), " +
			" cast(:requestTodate as varchar), " +
			" cast(:studentId as varchar), " +
			" cast(:studentName as varchar), " +
			" cast(:catererStatus as varchar), " +
			" cast(:studentStatus as varchar), " +
			" cast(:messSession as varchar) " +
			");", nativeQuery = true)
	Page<Object[]> getSickFoodRequestList(
			@Param("userRole") String userRole,
			@Param("userName") String userName,
			@Param("requestFromdate") String requestFromdate,
			@Param("requestTodate") String requestTodate,
			@Param("studentId") String studentId,
			@Param("studentName") String studentName,
			@Param("catererStatus") String catererStatus,
			@Param("studentStatus") String studentStatus,
			@Param("messSession") String messSession,
			Pageable pageable);


    @Query(value = """
    select sfr.id, sfds.id, sfds.sickFoodRequest.createdAt, asdv.studentName, asdv.emailId, sfds.messSession from SickFoodDeliveryStatusEntity sfds
        join SickFoodRequestEntity sfr on (sfr.id = sfds.sickFoodRequest.id and sfr.activeFlag = :activeFlag)
            join AllStudentsDetailsViewEntity asdv on (sfr.studentId = asdv.studentId)
                where sfds.activeFlag = :activeFlag and sfds.id = :deliveryId and sfds.sickFoodRequest.id = :requestId
    """)
    List<Object[]> getSickFoodDeliveryStudentDetails(Long requestId, Long deliveryId, String activeFlag);
	
}