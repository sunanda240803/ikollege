package com.iitm.hosteldine.repository.mess;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iitm.hosteldine.model.mess.MessSessionEntity;
import com.iitm.hosteldine.model.student.SickFoodDeliveryStatusEntity;

import java.util.List;
import java.util.Optional;

public interface MessSessionRepository extends JpaRepository<MessSessionEntity, Long> {
	
	final String BASE_QUERY = "FROM schooldev.\"MESS_SESSIONS\" ms "
			+ "JOIN schooldev.\"MESS_MASTER\" mm ON ms.mess_id = mm.mess_master_id AND mm.active_flag = :activeFlag "
			+ "JOIN schooldev.\"MESS_SESSIONS_MASTER\" msm ON ms.session_name = msm.session_code "
			+ "WHERE ms.active_flag = :activeFlag";
	final String SEARCH_QUERY = "FROM schooldev.\"MESS_SESSIONS\" ms "
			+ "JOIN schooldev.\"MESS_MASTER\" mm ON ms.mess_id = mm.mess_master_id AND mm.active_flag = :activeFlag "
			+ "JOIN schooldev.\"MESS_SESSIONS_MASTER\" msm ON ms.session_name = msm.session_code "
			+ "WHERE (mm.mess_name ilike concat('%',:search,'%') OR msm.session_name ilike concat('%',:search,'%')) AND ms.active_flag = :activeFlag";

	@Query(value = "SELECT ms.mess_id,ms.session_name,ms.start_time,ms.end_time, mm.mess_name ,msm.session_name " +
            "FROM schooldev.\"MESS_SESSIONS\" ms " +
            "JOIN schooldev.\"MESS_MASTER\" mm ON ms.mess_id = mm.mess_master_id and mm.active_flag = :activeFlag " +
            "JOIN schooldev.\"MESS_SESSIONS_MASTER\" msm ON ms.session_name = msm.session_code " +
            "WHERE ms.active_flag = :activeFlag  " +
            "ORDER BY ms.modified_at DESC", 
    nativeQuery = true)
	List<Object[]> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

	@Query(value = """
			    select count(x) from MessSessionEntity x where x.activeFlag = :activeFlag
			""")
	Long getActiveCount(String activeFlag);

	Optional<MessSessionEntity> findById_MessIdAndId_SessionNameAndActiveFlag(Long messId, String sessionName,
			String statusActive);

	Optional<MessSessionEntity> findById_MessIdAndActiveFlag(long id, String statusActive);

	List<MessSessionEntity> findByActiveFlagAndId_MessIdAndId_SessionNameIgnoreCase(
			String statusActive, long id, String sessionName);

	boolean existsByActiveFlagAndIdMessId(String statusActive, long id);

	@Query(value = "SELECT ms.mess_id, ms.session_name, ms.start_time, ms.end_time, mm.mess_name, msm.session_name "
			+ BASE_QUERY,
	countQuery = "SELECT count(*) " + BASE_QUERY, nativeQuery = true)
	Page<Object[]> findAllByActiveFlag(String activeFlag, Pageable pageable);

	@Query(value = "SELECT ms.mess_id, ms.session_name, ms.start_time, ms.end_time, mm.mess_name, msm.session_name "
			+ SEARCH_QUERY, 
	countQuery = "SELECT count(*)" + SEARCH_QUERY, nativeQuery = true)
	Page<Object[]> findByMessNameContainingAndActiveFlag(String search, String activeFlag, Pageable pageable);				

	
	

@Query(value = """
SELECT *
FROM schooldev."MESS_SESSIONS"
WHERE mess_id = :messIds
  AND active_flag = 'Y'
  AND (to_timestamp(start_time, 'HH24:MI')::time + make_interval(mins => :thresholdTime)) >= (to_timestamp(:currentTimeString, 'HH24:MI')::time)
""", nativeQuery = true)
List<MessSessionEntity> getAvailableSessionsWithThreshold(
    @Param("messIds") int messIds,
    @Param("thresholdTime") int thresholdTime,
    @Param("currentTimeString") String currentTimeString);

	@Query("SELECT b FROM MessSessionEntity b  " +
			"WHERE b.id.messId = :messId AND b.id.sessionName = :sessionName AND b.activeFlag = :activeFlag " +
			"AND :currentTime >= b.startTime AND :currentTime <= b.endTime")
	Optional<MessSessionEntity>  checkMessSession(Long messId, String sessionName,String activeFlag, String currentTime);
}