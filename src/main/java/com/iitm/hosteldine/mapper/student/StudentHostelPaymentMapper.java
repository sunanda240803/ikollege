package com.iitm.hosteldine.mapper.student;

import com.iitm.hosteldine.dto.student.StudentHostelPaymentDto;
import com.iitm.hosteldine.entity.student.StudentHostelPaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentHostelPaymentMapper {
    StudentHostelPaymentMapper INSTANCE = Mappers.getMapper(StudentHostelPaymentMapper.class);

    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "paymentType", expression = "java(studentHostelPaymentDto.getPaymentType() != null ? studentHostelPaymentDto.getPaymentType().getValue() : null)")
    @Mapping(target = "studentConfirmStatus", expression = "java(com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus.PAYMENT_CONFIRMED.getStatus())")
    @Mapping(target = "hostelOfficeEnrollment", expression = "java(com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus.VALIDATING.getStatus())")
    StudentHostelPaymentEntity toEntity(StudentHostelPaymentDto studentHostelPaymentDto);

    @Mapping(target = ".", source = ".")
    @Mapping(target = "paymentType", expression = "java(com.iitm.hosteldine.constant.dashboard.student.HostelPaymentTypeEnum.fromValue(studentHostelPaymentEntity.getPaymentType()))")
    StudentHostelPaymentDto toDto(StudentHostelPaymentEntity studentHostelPaymentEntity);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    @Mapping(target = "id", ignore = true)
    void onUpdateEntity(@MappingTarget StudentHostelPaymentEntity existingEntity, StudentHostelPaymentDto dto);

}