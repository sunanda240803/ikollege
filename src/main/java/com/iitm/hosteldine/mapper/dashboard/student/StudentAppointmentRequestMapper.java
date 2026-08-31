package com.iitm.hosteldine.mapper.dashboard.student;

import com.iitm.hosteldine.dto.dashboard.student.StudentAppointmentRequestDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentDetailsPdfDto;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.model.StudentBioDataFormDetailEntity;
import com.iitm.hosteldine.model.dashboard.student.StudentAppointmentRequestEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentAppointmentRequestMapper {

    StudentAppointmentRequestMapper INSTANCE = Mappers.getMapper(StudentAppointmentRequestMapper.class);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentAppointmentRequestEntity toEntity(StudentAppointmentRequestDto scholarsStayExtensionDto);

    @Mapping(target = ".", source = ".")
    StudentAppointmentRequestDto toDto(StudentAppointmentRequestEntity scholarsStayExtensionEntity);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "activeFlag", ignore = true)
    StudentAppointmentRequestEntity onUpdate(StudentAppointmentRequestDto scholarsStayExtensionDto, @MappingTarget StudentAppointmentRequestEntity scholarsStayExtensionEntity);
    
   

	// Map single entities to DTO
    @Mapping(target = "studentId", source = "studentId")
    @Mapping(target = "appointmentFrom", source = "appointmentFrom")
    @Mapping(target = "appointmentTo", source = "appointmentTo")
    @Mapping(target = "stayFrom", source = "stayFrom")
    @Mapping(target = "stayTo", source = "stayTo")
    @Mapping(target = "grossPay", source = "grossPay")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "dining", source = "dining")
    @Mapping(target = "validatingAuthority", source = "validatingAuthority")
    @Mapping(target = "validatingAuthorityEmail", source = "validatingAuthorityEmail")
    @Mapping(target = "occupancy", source = "occupancy")
    @Mapping(target = "approvalNotes", source = "approvalNotes")
    @Mapping(target = "purpose", source = "purpose")
    StudentDetailsPdfDto toDtos(StudentAppointmentRequestDto studentAppointmentRequestDto);
    

}