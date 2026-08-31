package com.iitm.hosteldine.mapper.dean;

import com.iitm.hosteldine.util.Utility;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {Utility.class})
public interface HostelEnrollmentMapper {
    HostelEnrollmentMapper INSTANCE = Mappers.getMapper(HostelEnrollmentMapper.class);

    /*@Mapping(target = "balance", expression = "java(Utility.parseDouble(o[0]))")
    @Mapping(target = "id", expression = "java(Utility.parseLong(o[1]))")
    @Mapping(target = "studentId", expression = "java(Utility.parseString(o[2]))")
    @Mapping(target = "studentName", expression = "java(Utility.parseString(o[3]))")
    @Mapping(target = "facilityMasterName", expression = "java(Utility.parseString(o[4]))")
    @Mapping(target = "roomNo", expression = "java(Utility.parseLong(o[5]))")
    @Mapping(target = "subRoomId", expression = "java(Utility.parseString(o[6]))")
    @Mapping(target = "messHead", expression = "java(Utility.parseString(o[7]))")
    @Mapping(target = "paymentReferenceNo", expression = "java(Utility.parseString(o[8]))")
    @Mapping(target = "paymentDate", expression = "java(Utility.convertToLocalDate(o[9]))")
    @Mapping(target = "paymentAmount", expression = "java(Utility.parseDouble(o[10]))")
    @Mapping(target = "hostelOfficeEnrollment", expression = "java(Utility.parseString(o[11]))")
    @Mapping(target = "pushStatus", expression = "java(Utility.parseString(o[12]))")
    @Mapping(target = "overrideAndApprove", expression = "java(Utility.parseString(o[13]))")
    @Mapping(target = "approvedBy", expression = "java(Utility.parseString(o[14]))")
    @Mapping(target = "approvalDate", expression = "java(Utility.convertToLocalDate(o[15]))")
    @Mapping(target = "lastPaymentDate", expression = "java(Utility.convertToLocalDate(o[16]))")
    @Mapping(target = "totalPaymentAmount", expression = "java(Utility.parseDouble(o[17]))")
    @Mapping(target = "viewStudentId", expression = "java(Utility.parseString(o[18]))")
    HostelEnrollmentDto toDto(Object[] o);*/
}