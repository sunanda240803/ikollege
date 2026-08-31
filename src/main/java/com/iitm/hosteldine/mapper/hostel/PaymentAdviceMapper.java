package com.iitm.hosteldine.mapper.hostel;

import java.util.Map;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.hostel.DailyCouponRequestDTO;
import com.iitm.hosteldine.dto.hostel.GuestCouponRequestDTO;
import com.iitm.hosteldine.model.hostel.GuestCouponPaymentAdviceEntity;
import com.iitm.hosteldine.util.CommonEnum;

@Mapper(imports = {WorkflowStatus.class, CommonEnum.class})
public interface PaymentAdviceMapper {
    PaymentAdviceMapper INSTANCE = Mappers.getMapper(PaymentAdviceMapper.class);

    @Mapping(target = "studentId", source = "studentId")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "diningFromDate", source = "diningFrom", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "diningToDate", source = "diningTo", dateFormat = "yyyy-MM-dd")
    @Mapping(target = "hostelId", source = "hostelId")
    @Mapping(target = "roomNumber", source = "roomNo")
    @Mapping(target = "mobileNumber", source = "mobileNo")
    @Mapping(target = "messId", source = "messId")
    @Mapping(target = "noOfBreakfastCoupons", expression = "java(getCount(dto, predicates.get(\"bfPredicate\"), CommonEnum.BF.toString()))")
    @Mapping(target = "noOfDinnerCoupons", expression = "java(getCount(dto, predicates.get(\"dinnerPredicate\"), CommonEnum.DR.toString()))")
    @Mapping(target = "noOfLunchCoupons", expression = "java(getCount(dto, predicates.get(\"lunchPredicate\"), CommonEnum.LC.toString()))")
    @Mapping(target = "noOfSnacksCoupons", expression = "java(getCount(dto, predicates.get(\"snacksPredicate\"), CommonEnum.ET.toString()))")
    @Mapping(target = "breakfastCouponRate", source = "bfRatePerUnit")
    @Mapping(target = "dinnerCouponRate", source = "dinnerRatePerUnit")
    @Mapping(target = "lunchCouponRate", source = "lunchRatePerUnit")
    @Mapping(target = "snacksCouponRate", source = "snacksRatePerUnit")
    @Mapping(target = "facDepartment", source = "faculty.deptName")
    @Mapping(target = "facProgram", source = "faculty.program")
    @Mapping(target = "purpose", source = "others.purpose")
    @Mapping(target = "candidateName", expression = "java(getNameByCategory(dto))")
    @Mapping(target = "mailId", expression = "java(getMailIdByCategory(dto))")
    @Mapping(target = "bulkCoupon", source = "bulkCoupon")
    @Mapping(target = "vegOrNonVeg", source = "vegOrNonVeg")
    @Mapping(target = "configDiscountedAmount", source = "configDiscountedAmount")
    @Mapping(target = "totalDiscountedAmount", source = "totalDiscountedAmount")
    @Mapping(target = "paymentStatus", expression = "java(WorkflowStatus.PENDING.getStatus())")
    @Mapping(target = "printStatus", expression = "java(WorkflowStatus.PENDING.getStatus())")
    GuestCouponPaymentAdviceEntity toEntity(GuestCouponRequestDTO dto, @Context Map<String, Predicate<DailyCouponRequestDTO>> predicates);

    default String getNameByCategory(GuestCouponRequestDTO dto) {
        // TODO: remove hardcoding
        if (StringUtils.equals(dto.getCategory(), "IITM Faculty")) {
            return dto.getFaculty().getName();
        } else if (StringUtils.equals(dto.getCategory(), "Project Staff")) {
            return dto.getStaff().getName();
        } else if (StringUtils.equals(dto.getCategory(), "Others")) {
            return dto.getOthers().getName();
        } else {
            return dto.getName();
        }
    }

    default String getMailIdByCategory(GuestCouponRequestDTO dto) {
        if (StringUtils.equals(dto.getCategory(), "IITM Faculty")) {
            return dto.getFaculty().getEmail();
        } else if (StringUtils.equals(dto.getCategory(), "Project Staff")) {
            return dto.getStaff().getApplicationId();
        } else if (StringUtils.equals(dto.getCategory(), "Others")) {
            return dto.getOthers().getEmail();
        } else {
            return StringUtils.EMPTY;
        }
    }

    default Integer getCount(GuestCouponRequestDTO dto, Predicate<DailyCouponRequestDTO> sessionTest, String session) {
        int count = 0;

        if (!dto.isBulkCoupon()) {
            count = (int) dto.getFoodFrequency().stream().filter(sessionTest).count();
        } else {
            if (StringUtils.equals(session, CommonEnum.BF.toString())) {
                count = dto.getFoodFrequency().stream().mapToInt(DailyCouponRequestDTO::getNoOfBreakfast).sum();
            }

            if (StringUtils.equals(session, CommonEnum.LC.toString())) {
                count = dto.getFoodFrequency().stream().mapToInt(DailyCouponRequestDTO::getNoOfLunch).sum();
            }

            if (StringUtils.equals(session, CommonEnum.DR.toString())) {
                count = dto.getFoodFrequency().stream().mapToInt(DailyCouponRequestDTO::getNoOfDinner).sum();
            }

            if (StringUtils.equals(session, CommonEnum.ET.toString())) {
                count = dto.getFoodFrequency().stream().mapToInt(DailyCouponRequestDTO::getNoOfSnacks).sum();
            }
        }

        return count;
    }

    @Mapping(target = "requestId", source = "requestId")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "studentId", source = "studentId")
    @Mapping(target = "name", source = "candidateName")
    @Mapping(target = "faculty.name", source = "candidateName")
    @Mapping(target = "staff.name", source = "candidateName")
    @Mapping(target = "others.name", source = "candidateName")
    @Mapping(target = "hostelId", source = "hostelId")
    @Mapping(target = "roomNo", source = "roomNumber")
    @Mapping(target = "faculty.deptName", source = "facDepartment")
    @Mapping(target = "faculty.program", source = "facProgram")
    @Mapping(target = "email", source = "mailId")
    @Mapping(target = "faculty.email", source = "mailId")
    @Mapping(target = "staff.applicationId", source = "mailId")
    @Mapping(target = "others.email", source = "mailId")
    @Mapping(target = "others.purpose", source = "purpose")
    @Mapping(target = "diningFrom", source = "diningFromDate")
    @Mapping(target = "diningTo", source = "diningToDate")
    @Mapping(target = "messId", source = "messId")
    @Mapping(target = "noOfBreakfastCoupons", source = "noOfBreakfastCoupons")
    @Mapping(target = "noOfLunchCoupons", source = "noOfLunchCoupons")
    @Mapping(target = "noOfDinnerCoupons", source = "noOfDinnerCoupons")
    @Mapping(target = "noOfSnacksCoupons", source = "noOfSnacksCoupons")
    @Mapping(target = "bfRatePerUnit", source = "breakfastCouponRate")
    @Mapping(target = "lunchRatePerUnit", source = "lunchCouponRate")
    @Mapping(target = "dinnerRatePerUnit", source = "dinnerCouponRate")
    @Mapping(target = "snacksRatePerUnit", source = "snacksCouponRate")
    @Mapping(target = "overallAmount", source = "overallAmount")
    @Mapping(target = "paymentType", source = "paymentType")
    @Mapping(target = "paymentDate", source = "paymentDate")
    @Mapping(target = "paymentReferenceNo", source = "paymentReferenceNo")
    @Mapping(target = "paymentStatus", source = "paymentStatus")
    @Mapping(target = "vegOrNonVeg", source = "vegOrNonVeg")
    @Mapping(target = "bulkCoupon", source = "bulkCoupon")
    @Mapping(target = "configDiscountedAmount", source = "configDiscountedAmount")
    @Mapping(target = "totalDiscountedAmount", source = "totalDiscountedAmount")
	void toGuestCouponDtoFromEntity(@MappingTarget GuestCouponRequestDTO returnDto, GuestCouponPaymentAdviceEntity entity);
    
}
