package com.iitm.hosteldine.repository.feedback;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.feedback.FeedbackQualifierEntity;

import java.util.List;
import java.util.Optional;

public interface FeedbackQualifierRepository extends JpaRepository<FeedbackQualifierEntity, Long> {
    Optional<FeedbackQualifierEntity> findByFeedbackIdAndActiveFlag(Long id, String activeFlag);

    List<FeedbackQualifierEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from FeedbackQualifierEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

	List<FeedbackQualifierEntity> findAllByActiveFlag(String statusActive);
}