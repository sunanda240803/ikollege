package com.iitm.hosteldine.model.OtherCandidate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class CandidateStayDateIdEntity implements Serializable {
    @Column(name = "candidate_id")
    private Long candidateId;
    @Column(name = "request_id")
    private Long requestId;
    @Column(name = "stay_id")
    private Long stayId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CandidateStayDateIdEntity that = (CandidateStayDateIdEntity) o;
        return Objects.equals(candidateId, that.candidateId) && Objects.equals(requestId, that.requestId) && Objects.equals(stayId, that.stayId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(candidateId, requestId, stayId);
    }
}
