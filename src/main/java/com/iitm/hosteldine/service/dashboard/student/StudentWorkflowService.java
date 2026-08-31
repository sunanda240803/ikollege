package com.iitm.hosteldine.service.dashboard.student;

import java.util.List;
import java.util.Locale;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.util.RoleEnum;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.dashboard.student.StudentAppointmentRequestDto;
import com.iitm.hosteldine.dto.dashboard.student.StudentWorkflowDto;
import com.iitm.hosteldine.mapper.dashboard.student.StudentAppointmentRequestMapper;
import com.iitm.hosteldine.mapper.dashboard.student.StudentWorkflowMapper;
import com.iitm.hosteldine.model.dashboard.student.StudentWorkflowEntity;
import com.iitm.hosteldine.repository.dashboard.student.StudentAppointmentRequestRepository;
import com.iitm.hosteldine.repository.dashboard.student.StudentWorkflowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentWorkflowService {
    private final MessageSource messageSource;
    private final StudentWorkflowRepository studentWorkflowRepository;
    private final StudentAppointmentRequestRepository scholarsStayExtensionRepository;

    public StudentAppointmentRequestDto getValidatorList(String studentId, Long id, String category) {
        StudentWorkflowDto studentWorkflowDto = new StudentWorkflowDto();
        new StudentAppointmentRequestDto();
        String resolvedCategory = resolveCategory(category);
        studentWorkflowDto.setCategory(resolvedCategory);
        List<StudentWorkflowEntity> workflowEntities = studentWorkflowRepository.getStudentWorkflowByApprovalStatus(
                studentId, id, WorkflowStatus.PENDING.getStatus(), studentWorkflowDto.getCategory(), 1, ModelConstants.STATUS_ACTIVE
        );
        List<StudentWorkflowDto> workflowDtos = workflowEntities.stream()
                .map(StudentWorkflowMapper.INSTANCE::toDto)
                .toList();
        StudentAppointmentRequestDto scholarsStayExtensionDto = getInfoMail(id);
        scholarsStayExtensionDto.setStudentWorkflowDto(workflowDtos);
        return scholarsStayExtensionDto;
    }

    private String resolveCategory(String inputCategory) {
        if (inputCategory.equalsIgnoreCase(
                messageSource.getMessage(StudentConstants.CATEGORY_SCHOLAR.getStudentConstant(), null, Locale.getDefault()))) {
            return messageSource.getMessage(StudentConstants.CATEGORY_SCHOLAR.getStudentConstant(), null, Locale.getDefault());
        } else if (StudentConstants.CATEGORY_CCW.getStudentConstant().equalsIgnoreCase(inputCategory)) {
            return StudentConstants.CATEGORY_CCW.getStudentConstant();
        }else if (inputCategory.equalsIgnoreCase(
                messageSource.getMessage(StudentConstants.CATEGORY_INSIDE_CAMPUS.getStudentConstant(), null, Locale.getDefault()))) {
            return messageSource.getMessage(StudentConstants.CATEGORY_INTERN_INSIDE_CAMPUS.getStudentConstant(), null, Locale.getDefault());
        }else if (inputCategory.equalsIgnoreCase(
                messageSource.getMessage(StudentConstants.CATEGORY_OUTSIDE_CAMPUS.getStudentConstant(), null, Locale.getDefault()))) {
            return messageSource.getMessage(StudentConstants.CATEGORY_INTERN_OUTSIDE_CAMPUS.getStudentConstant(), null, Locale.getDefault());
        }
        return inputCategory;
    }

    private StudentAppointmentRequestDto getInfoMail(Long requestId) {
        return scholarsStayExtensionRepository.getInformationForMail(requestId, ModelConstants.STATUS_ACTIVE)
                .map(StudentAppointmentRequestMapper.INSTANCE::toDto)
                .orElse(new StudentAppointmentRequestDto());
    }

    public StudentWorkflowEntity getStudentWorkflowDetails(Long requestId, String studentId) {
        return studentWorkflowRepository
                .getStudentWorkflowDetails(requestId, studentId, Constants.CCW,RoleEnum.DEAN.getValue(), ModelConstants.STATUS_ACTIVE)
                .orElse(new StudentWorkflowEntity());
    }

//    public StudentWorkflowDto getStudentWorkflowAuthorityTypeDean(Long requestId) {
//        return studentWorkflowRepository
//                .findByRequestIdAndActiveFlagAndAuthorityTypeContaining(requestId, ModelConstants.STATUS_ACTIVE, Constants.CCW)
//                .map(StudentWorkflowMapper.INSTANCE::toDto)
//                .orElse(new StudentWorkflowDto());
//    }

    public StudentWorkflowDto getStudentWorkflowAuthorityTypeDean(Long requestId) {
        return studentWorkflowRepository
                .findByAuthorityTypeCCWOrDean(requestId,ModelConstants.STATUS_ACTIVE, Constants.CCW, RoleEnum.DEAN.getValue())
                .map(StudentWorkflowMapper.INSTANCE::toDto)
                .orElse(new StudentWorkflowDto());
    }

    public StudentWorkflowDto getWorkflowById(Long id) {
        return studentWorkflowRepository
                .findById(id)
                .map(StudentWorkflowMapper.INSTANCE::toDto)
                .orElse(new StudentWorkflowDto());
    }

}
