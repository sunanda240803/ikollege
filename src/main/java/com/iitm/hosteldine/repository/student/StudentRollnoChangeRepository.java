package com.iitm.hosteldine.repository.student;

import com.iitm.hosteldine.model.student.StudentRollnoChangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StudentRollnoChangeRepository extends JpaRepository<StudentRollnoChangeEntity, Long> {
    Optional<StudentRollnoChangeEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<StudentRollnoChangeEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from StudentRollnoChangeEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

	StudentRollnoChangeEntity findByStudentidAndActiveFlag(String studentId, String statusActive);

	Optional<StudentRollnoChangeEntity> findTopByNewRollNoAndActiveFlag(String rollNo, String statusActive);

	Optional<StudentRollnoChangeEntity> findTopByStudentidAndActiveFlag(String studentId, String statusActive);
	
	Optional<StudentRollnoChangeEntity> findTopByStudentidAndActiveFlagAndStatus(String upperCase, String statusActive,
			String pending);

	@Query(value = """
			 select schooldev.student_rollno_change(:previousId, :changeId, :studentName, :requestDate)
			""", nativeQuery = true)
	List<Object[]> getStudentRollNoChange(String previousId, String changeId, String studentName, String requestDate);

	@Modifying
	@Query(value = """
	update StudentRollnoChangeEntity src set src.status = :status, src.modifiedAt = :modifiedAt, src.modifiedBy = :modifiedBy
		where src.id = :id
	""")
	int updateStatus(String status, String modifiedBy, LocalDateTime modifiedAt, Long id);
}