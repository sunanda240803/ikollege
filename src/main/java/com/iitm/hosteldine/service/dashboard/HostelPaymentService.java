package com.iitm.hosteldine.service.dashboard;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.constant.dashboard.student.HostelPaymentTypeEnum;
import com.iitm.hosteldine.dto.student.StudentHostelPaymentDto;
import com.iitm.hosteldine.entity.student.StudentHostelPaymentEntity;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.mapper.student.StudentHostelPaymentMapper;
import com.iitm.hosteldine.repository.student.StudentHostelPaymentRepository;
import com.iitm.hosteldine.service.IfscCodeService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HostelPaymentService {


    private final StudentHostelPaymentRepository studentHostelPaymentRepository;
    private final MessageSource messageSource;
    private final IfscCodeService ifscCodeService;
    private final CommonResponseUtil commonResponseUtil;

    public StudentHostelPaymentDto getRecentStudentHostelPayment() {
        return studentHostelPaymentRepository.findFirstByStudentConfirmStatusAndStudentIdAndActiveFlagOrderByModifiedAtDesc
                (WorkflowStatus.PAYMENT_CONFIRMED.getStatus(), SecurityCtxUtil.userId().toUpperCase(), ModelConstants.STATUS_ACTIVE)
                .map(StudentHostelPaymentMapper.INSTANCE::toDto)
                .orElse(null);
    }

    public List<StudentHostelPaymentDto> getStudentHostelPayments() {
        return studentHostelPaymentRepository.findAllByStudentConfirmStatusAndHostelOfficeEnrollmentNotAndStudentIdAndActiveFlagOrderByModifiedAtDesc(
                        WorkflowStatus.PAYMENT_CONFIRMED.getStatus(), WorkflowStatus.REJECTED.getStatus(), SecurityCtxUtil.userId().toUpperCase(),
                        ModelConstants.STATUS_ACTIVE
                ).map(list -> list.stream().map(StudentHostelPaymentMapper.INSTANCE::toDto).toList())
                .orElse(Collections.emptyList());
    }

    public String saveHostelPayment(StudentHostelPaymentDto studentHostelPaymentDto) {
        if (studentHostelPaymentDto.getPaymentType().equals(HostelPaymentTypeEnum.ICOLLECT) && !studentHostelPaymentDto.getPaymentReferenceNo().toUpperCase().startsWith(Constants.DU)) {
            studentHostelPaymentDto.setPaymentReferenceNo(Constants.DU + studentHostelPaymentDto.getPaymentReferenceNo());
        }

        StudentHostelPaymentEntity entity = StudentHostelPaymentMapper.INSTANCE.toEntity(studentHostelPaymentDto);
        entity.setStudentId(SecurityCtxUtil.userId().toUpperCase());
        if(studentHostelPaymentDto.getPaymentType().equals(HostelPaymentTypeEnum.ALREADY_PAID)){
            entity.setPaymentDate(LocalDate.now());
        }
        entity.setStudentConfirmStatus(WorkflowStatus.PAYMENT_CONFIRMED.getStatus());
        entity.setHostelOfficeEnrollment(WorkflowStatus.VALIDATING.getStatus());
        entity.onCreate();
        studentHostelPaymentRepository.save(entity);
        return Constants.SAVED;
    }

    public boolean deleteHostelPayment(Long id) throws RecordNotExistsException {
        return studentHostelPaymentRepository.findByIdAndActiveFlagAndHostelOfficeEnrollmentEqualsIgnoreCase(id, ModelConstants.STATUS_ACTIVE,
                        WorkflowStatus.VALIDATING.getStatus())
                .map(entity -> {
                    entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
                    studentHostelPaymentRepository.save(entity);
                    return true;
                })
                .orElseThrow(() -> new RecordNotExistsException(
                        messageSource.getMessage("validation.error.id.not.found", null, Locale.getDefault()
                        )));
    }

    public boolean checkDuNumberExists(String duNumber) {
        if (!duNumber.toUpperCase().startsWith(Constants.DU)) {
            duNumber = Constants.DU + duNumber;
        }
        return studentHostelPaymentRepository.existsByDuNumberAndConditions(SecurityCtxUtil.userId().toUpperCase(), duNumber,
                WorkflowStatus.VALIDATING.getStatus(), WorkflowStatus.CHECKED_IN.getStatus(), WorkflowStatus.OVERRIDE_AND_APPROVED.getStatus());
    }


    public void validateStudentHostelPaymentDto(StudentHostelPaymentDto dto, BindingResult bindingResult) {

        switch (dto.getPaymentType()) {
            case ICOLLECT:
                validateIcollectPayment(dto, bindingResult);
                break;
            case DD:
                validateDdPayment(dto, bindingResult);
                break;
            case BANK_LOAN:
                validateMandatoryFields(dto, bindingResult);
                break;
            default:
                break;
        }
    }

    private void validateMandatoryFields(StudentHostelPaymentDto dto, BindingResult bindingResult) {
        if (dto.getPaymentAmount() == null || dto.getPaymentAmount() <= 0) {
            rejectField(bindingResult, "paymentAmount", "message.validation.payment.amount.required");
        }

        if (dto.getPaymentDate() == null) {
            rejectField(bindingResult, "paymentDate", "message.validation.payment.date.required");
        }
    }

    private void validateIcollectPayment(StudentHostelPaymentDto dto, BindingResult bindingResult) {
        validateMandatoryFields(dto, bindingResult);
        if (isNullOrEmpty(dto.getPaymentReferenceNo())) {
            rejectField(bindingResult, "paymentReferenceNo", "message.validation.ref.no.required");
        } else if (checkDuNumberExists(dto.getPaymentReferenceNo())) {
            rejectField(bindingResult, "paymentReferenceNo", "message.ref.number.exists");
        }
    }

    private void validateDdPayment(StudentHostelPaymentDto dto, BindingResult bindingResult) {
        validateMandatoryFields(dto, bindingResult);
        if (isNullOrEmpty(dto.getIfscCode())) {
            rejectField(bindingResult, "ifscCode", "message.validation.ifsc.required");
        } else {
            Map<String, Object> response = ifscCodeService.validateIfscCode(dto.getIfscCode());
            if (response.isEmpty()) {
                dto.setBankName("");
                dto.setBranchName("");
                rejectField(bindingResult, "ifscCode", "message.invalid.ifsc.code");
            }
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void rejectField(BindingResult bindingResult, String field, String messageKey) {
        bindingResult.rejectValue(field, field + ".invalid", commonResponseUtil.getMessage(messageKey));
    }

}
