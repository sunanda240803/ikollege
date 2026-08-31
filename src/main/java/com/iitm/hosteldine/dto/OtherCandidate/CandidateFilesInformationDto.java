package com.iitm.hosteldine.dto.OtherCandidate;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CandidateFilesInformationDto {
    private Long id;
    private Integer candidateId;
    private Integer requestId;
    private String filename;
    private String description;
    private Long stayId;
}