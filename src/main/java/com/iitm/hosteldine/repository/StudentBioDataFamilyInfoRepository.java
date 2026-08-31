package com.iitm.hosteldine.repository;

import com.iitm.hosteldine.model.StudentBioDataFamilyInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentBioDataFamilyInfoRepository extends JpaRepository<StudentBioDataFamilyInfoEntity, Long> {
    List<StudentBioDataFamilyInfoEntity> findAllByBioDataId(long bioDataId);
   
    @Query("""
    	    SELECT e.id,e.bioDataId,e.relationType , e.relationName, e.proofFileName,e.age
    	    FROM StudentBioDataFamilyInfoEntity e 
    	    LEFT JOIN StudentBioDataFormDetailEntity s ON s.id = e.bioDataId 
    	    WHERE s.studentId = :studentId 
    	    AND e.activeFlag = :statusActive
    	    AND s.activeFlag = :statusActive
    	    AND e.relationType <> 'Guardian'
    	""")
    Optional<List<Object[]>> getStudentFamilyDetails(String studentId, String statusActive);
    

    @Query("""
    	    SELECT e.proofFileName,e.relationName,e.relationType
    	    FROM StudentBioDataFamilyInfoEntity e 
    	    LEFT JOIN StudentBioDataFormDetailEntity s ON s.id = e.bioDataId 
    	    WHERE s.studentId = :studentId 
    	    AND e.activeFlag = :statusActive
    	    AND s.activeFlag = :statusActive
    	    AND e.id IN(:guestIds)
    	""")
	List<Object[]> getFileUploadDetails(String studentId,List<Long> guestIds, String statusActive);
	
	@Query("""
    	    SELECT e.proofFileName
    	    FROM StudentBioDataFamilyInfoEntity e 
    	    LEFT JOIN StudentBioDataFormDetailEntity s ON s.id = e.bioDataId 
    	    WHERE s.studentId = :studentId 
    	    AND e.activeFlag = :statusActive
    	    AND s.activeFlag = :statusActive
    	    AND e.id = :bioId
    	""")

	Optional<String> checkFileName(String studentId, long bioId, String statusActive);
    
	 Optional<StudentBioDataFamilyInfoEntity> findByBioDataIdAndRelationTypeAndActiveFlag(Long bioDataId, String relationType, String activeFlag);

	 @Query("""
select count(1) from StudentBioDataFamilyInfoEntity x where (x.proofFileName is null or x.proofFileName = '') and 
x.bioDataId = (select y.id from StudentBioDataFormDetailEntity y where y.studentId = :studentId) and x.relationType != :guardian
""")
    Integer hasNoFamilyProofs(String studentId, String guardian);
}