package com.iitm.hosteldine.mapper.OtherCandidate;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.OtherCandidate.CandidateStayDateViewDto;
import com.iitm.hosteldine.model.OtherCandidate.CandidateStayDateIdEntity;
import com.iitm.hosteldine.model.OtherCandidate.CandidateStayDateViewEntity;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.Utility;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CandidateStayDateMapper {
    CandidateStayDateMapper INSTANCE = Mappers.getMapper(CandidateStayDateMapper.class);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "encryptedKey", expression = "java(encryptIds(entity))")
    CandidateStayDateViewDto toDto(CandidateStayDateViewEntity entity);

    default String encryptIds(CandidateStayDateViewEntity candidateStayDateEntity){
        try {
            CandidateStayDateIdEntity id = candidateStayDateEntity.getId();
            String status = id.getStayId() > 0 ? candidateStayDateEntity.getStayStatus() :
                    candidateStayDateEntity.getAppStatus();
            String encryptKey = Utility.accommodationRequestKey(status,id.getRequestId(),id.getCandidateId(),id.getStayId());
            return encryptKey;
        } catch (Exception e) {
            throw new IllegalStateException("Error while encrypting IDs", e);
        }
    }
    
}