package com.iitm.hosteldine.repository.student.wellness;

import com.iitm.hosteldine.model.student.wellness.StudentWellnessFollowupDataEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentWellnessFollowupDataRepository extends JpaRepository<StudentWellnessFollowupDataEntity, Long> {

	static final String selectAllQuery = "SELECT s FROM StudentWellnessFollowupDataEntity s"
			+ " WHERE s.wellness.id = :wellnessId AND s.activeFlag = :activeFlag"
			+ " ORDER BY s.noOfVisit DESC";

	Optional<StudentWellnessFollowupDataEntity>	findByIdAndActiveFlag(Long id, String activeFlag);
	
	StudentWellnessFollowupDataEntity findByIdAndWellnessIdAndActiveFlag(Long id, Long wellnessId, String activeFlag);
	
	@Query(value = selectAllQuery)
	Page<StudentWellnessFollowupDataEntity> getWellnessViewList(Long wellnessId, String activeFlag, Pageable pageable);
	
	@Query(value = selectAllQuery)
	List<StudentWellnessFollowupDataEntity> getWellnessViewList(Long wellnessId, String activeFlag);

	@Query(value = "SELECT s FROM StudentWellnessFollowupDataEntity s "
			+ "WHERE s.wellness.id = :wellnessId AND s.activeFlag = :activeFlag "
			+ "AND (:search IS NULL OR " + "LOWER(CAST(s.noOfVisit AS string)) LIKE LOWER(CONCAT('%', :search, '%')) "
			+ "OR LOWER(s.interactionMode) LIKE LOWER(CONCAT('%', :encryptSearch, '%')) "
			+ "OR LOWER(s.visitStatus) LIKE LOWER(CONCAT('%', :encryptSearch, '%')))")
	Page<StudentWellnessFollowupDataEntity> getWellnessViewListBySearch(Long wellnessId, String activeFlag, String search,
			String encryptSearch, Pageable pageable);
	
	@Query(value = "SELECT count(s) + 1 FROM StudentWellnessFollowupDataEntity s"
			+ " WHERE s.wellness.id = :wellnessId AND s.activeFlag = :activeFlag")
	Integer visitCountByWellnessId(Long wellnessId, String activeFlag);
	
	@Query(value = "SELECT * FROM schooldev.wellness_all_visit_list( " +
			" cast(:studentId as varchar), " +
			" cast(:visitFromdate as varchar), " +
			" cast(:visitTodate as varchar), " +
			" cast(:followUpFromdate as varchar), " +
			" cast(:followUpTodate as varchar), " +
			" cast(:referralType as varchar), " +
			" cast(:concernType as varchar), " +
			" cast(:userName as varchar) " +
			");", nativeQuery = true)
	Page<Object[]> getStudentWelnessList(
			@Param("studentId") String studentId,
			@Param("visitFromdate") String visitFromdate,
			@Param("visitTodate") String visitTodate,
			@Param("followUpFromdate") String followUpFromdate,
			@Param("followUpTodate") String followUpTodate,			
			@Param("referralType") String referralType,
			@Param("concernType") String concernType,
			@Param("userName") String userName,
			Pageable pageable);
	
}