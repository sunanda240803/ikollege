package com.iitm.hosteldine.repository.collegeInfo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.collegeInfo.FaqEntity;

public interface FaqRepository extends JpaRepository<FaqEntity, Long> {
    Optional<FaqEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    Page<FaqEntity> findAllByActiveFlagOrderByIdAsc(String activeFlag, Pageable pageable);

    @Query("select faq from FaqEntity faq where activeFlag = :statusActive and (question ILIKE CONCAT('%', :search, '%')" +
            "or answer ILIKE CONCAT('%', :search, '%'))")
    Page<FaqEntity> findByQuestionAndAnswerAndActive(String statusActive, Pageable pageable, String search);

}