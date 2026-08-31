package com.iitm.hosteldine.mapper.student;

import com.iitm.hosteldine.model.student.StudentHostelPaymentLateFeeDetailDto;
import com.iitm.hosteldine.model.student.StudentHostelPaymentLateFeeDetailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentHostelPaymentLateFeeDetailMapper {
    StudentHostelPaymentLateFeeDetailMapper INSTANCE = Mappers.getMapper(StudentHostelPaymentLateFeeDetailMapper.class);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "studentId", source = "entity.studentId")
    @Mapping(target = "dueDate", source = "entity.dueDate")
    @Mapping(target = "hostelId", source = "entity.hostelId")
    @Mapping(target = "description", source = "entity.description")
    @Mapping(target = "hostelName", source = "hostelName")
    StudentHostelPaymentLateFeeDetailDto toDto(StudentHostelPaymentLateFeeDetailEntity entity,String hostelName);

    @Mapping(target = ".", source = ".")
    StudentHostelPaymentLateFeeDetailEntity toEntity(StudentHostelPaymentLateFeeDetailDto dto);
}