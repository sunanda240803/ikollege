package com.iitm.hosteldine.repository.OtherCandidate;

import com.iitm.hosteldine.model.OtherCandidate.CandidateFilesInformationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateFilesInformationRepository extends JpaRepository<CandidateFilesInformationEntity, Long> {

    Optional<List<CandidateFilesInformationEntity>> findAllByRequestIdAndCandidateIdAndActiveFlag(Long requestId,
                                                                                              Long candidateId, String activeFlag);
    
    Optional<CandidateFilesInformationEntity> findByCandidateIdAndRequestIdAndStayIdAndActiveFlag(Long candidateId,
			Long requestId, Long stayId, String statusActive);
    
    Optional<List<CandidateFilesInformationEntity>> findAllByCandidateIdAndRequestIdAndStayIdAndActiveFlag(Long candidateId,
			Long requestId, Long stayId, String statusActive);
}
