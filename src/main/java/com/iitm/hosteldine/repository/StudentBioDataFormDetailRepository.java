package com.iitm.hosteldine.repository;

import com.iitm.hosteldine.model.StudentBioDataFormDetailEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentBioDataFormDetailRepository extends JpaRepository<StudentBioDataFormDetailEntity, Long> {

    List<StudentBioDataFormDetailEntity> findAllByActiveFlagAndImageBytesIsNotNull(String activeFlag, Pageable pageable);
    int countAllByActiveFlagAndImageBytesIsNotNull(String activeFlag);

    Optional<StudentBioDataFormDetailEntity> findTopByStudentIdAndActiveFlag(String studentId, String statusActive);

	List<StudentBioDataFormDetailEntity> findAllByActiveFlagAndStudentId(String statusActive, String studentId);

	@Query(value = """
			SELECT s FROM StudentBioDataFormDetailEntity s WHERE UPPER(s.applicationNumber) = UPPER(:applicationNo) AND s.activeFlag = :statusActive
			""")
	Optional<StudentBioDataFormDetailEntity> getApplicationNumber(String statusActive, String applicationNo);

	@Query(value = """
			SELECT s FROM StudentBioDataFormDetailEntity s WHERE s.studentId =:studentId AND s.activeFlag = :statusActive
			""")
	Optional<StudentBioDataFormDetailEntity> getStudentDetails(String studentId, String statusActive);

    @Query("""
    	    SELECT SBDFD, SDI, HRI, HM, HRAI
			    FROM StudentBioDataFormDetailEntity SBDFD
			LEFT JOIN StudentDetailsInfoEntity SDI
			    ON SBDFD.studentId = SDI.studentId
			   	AND SDI.activeFlag = :statusActive
    	    LEFT JOIN HostelRoomAllotmentInfoEntity HRAI 
    	        ON SDI.studentId = HRAI.studentId 
    	        AND HRAI.activeFlag = :statusActive
    	        AND HRAI.vacateDate IS NULL
    	        AND HRAI.shiftedDate IS NULL
    	    LEFT JOIN HostelRoomInfoEntity HRI 
    	        ON HRAI.roomId = HRI.id 
    	        AND HRI.activeFlag = :statusActive
    	    LEFT JOIN HostelFloorMasterEntity HFM 
    	        ON HRI.building.id = HFM.id
    	        AND HRAI.buildingId = HFM.id 
    	        AND HFM.activeFlag = :statusActive
    	    LEFT JOIN HostelMasterEntity HM 
    	        ON HFM.hostel.id = HM.id 
    	        AND HM.activeFlag = :statusActive
    	    WHERE SBDFD.studentId = :studentId 
    	    	AND SBDFD.activeFlag = :statusActive 
    	    ORDER BY HRAI.modifiedAt DESC LIMIT 1
    	""")
    	Object getStudentDetailsWithEntities(String studentId, String statusActive);

		@Modifying
		@Query("""
				UPDATE StudentBioDataFormDetailEntity x SET x.facultyName = :facultyName WHERE x.activeFlag = :activeFlag AND x.studentId = :studentId
				""")
		void updateFacultyName(String activeFlag, String studentId, String facultyName);

		@Modifying
		@Query("""
				UPDATE StudentBioDataFormDetailEntity x SET x.facultyEmail = :facultyEmail WHERE x.activeFlag = :activeFlag AND x.studentId = :studentId
				""")
		void updateFacultyEmail(String activeFlag, String studentId, String facultyEmail);

	Optional<StudentBioDataFormDetailEntity> findByStudentIdAndActiveFlag(String studentId, String statusActive);
	
	Page<StudentBioDataFormDetailEntity> findAllByActiveFlag(String activeFlag, Pageable pageable);
	
	@Query("SELECT s FROM StudentBioDataFormDetailEntity s WHERE s.activeFlag = :activeFlag AND (" +
		       "LOWER(s.applicationNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "LOWER(s.studentId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "LOWER(s.studentName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "LOWER(s.gender) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
		       "LOWER(s.messName) LIKE LOWER(CONCAT('%', :search, '%'))" +
		       ")")
	Page<StudentBioDataFormDetailEntity> searchByFields(@Param("activeFlag") String activeFlag, @Param("search") String search, Pageable pageable);
	

}