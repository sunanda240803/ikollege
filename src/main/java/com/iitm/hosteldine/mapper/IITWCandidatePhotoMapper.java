package com.iitm.hosteldine.mapper;

import com.iitm.hosteldine.dto.IITWCandidatePhotoDto;
import com.iitm.hosteldine.model.IITWCandidatePhotoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface IITWCandidatePhotoMapper {
    IITWCandidatePhotoMapper INSTANCE = Mappers.getMapper(IITWCandidatePhotoMapper.class);

    @Mapping(target = ".", source = ".")
    IITWCandidatePhotoDto fromIITWCandidatePhotoEntity(IITWCandidatePhotoEntity model);

    @Mapping(target = ".", source = ".")
    IITWCandidatePhotoEntity toIITWCandidatePhotoEntity(IITWCandidatePhotoDto modelDto);
}