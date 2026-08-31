package com.iitm.hosteldine.dto.OtherCandidate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CandidateStayDateIdDto {
    private Long candidateId;
    private Long requestId;
    private Long stayId;
}
