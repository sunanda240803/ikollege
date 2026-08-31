package com.iitm.hosteldine.repository.OtherCandidate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.OtherCandidate.CandidateProfileEntity;

public interface CandidateProfileRepository extends JpaRepository<CandidateProfileEntity, Long> {
	
    Optional<CandidateProfileEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<CandidateProfileEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from CandidateProfileEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);
}