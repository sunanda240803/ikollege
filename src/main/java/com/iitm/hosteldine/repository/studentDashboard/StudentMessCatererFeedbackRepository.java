package com.iitm.hosteldine.repository.studentDashboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.mess.StudentMessCatererFeedbackEntity;

import java.util.List;
import java.util.Optional;

public interface StudentMessCatererFeedbackRepository extends JpaRepository<StudentMessCatererFeedbackEntity, Long> {
    Optional<StudentMessCatererFeedbackEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<StudentMessCatererFeedbackEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from StudentMessCatererFeedbackEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

	List<StudentMessCatererFeedbackEntity> findAllByStudentIdAndMessMasterIdAndMessControllerId(String studentId,
			Long messId, Long messControllerId);

	List<StudentMessCatererFeedbackEntity> findAllByStudentIdAndMessControllerId(String studentId, Long messControllerId);

	
}