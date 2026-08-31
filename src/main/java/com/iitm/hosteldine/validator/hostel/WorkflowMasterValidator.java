package com.iitm.hosteldine.validator.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.dashboard.student.WorkflowMasterDto;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WorkflowMasterValidator {

    private final CommonResponseUtil commonResponseUtil;

    public void validate(WorkflowMasterDto workflowMasterDto, BindingResult bindingResult) {
        validateCategory(workflowMasterDto.getCategory(), bindingResult);
        validateApprovalLevel(workflowMasterDto.getAuthorityType(), bindingResult);
        validateAuthorityType(workflowMasterDto.getAuthorityType(), bindingResult);
        validateEmail(workflowMasterDto.getEmail(), bindingResult);
    }

    private void validateCategory(String category, BindingResult result) {
        String categoryField = "category";
        String invalidCategoryCode = "category.invalid";
        if (!Objects.nonNull(category) || category.isEmpty()) {
            result.rejectValue(categoryField, invalidCategoryCode, commonResponseUtil.getMessage("message.validation.category.required"));
        }
    }

    private void validateApprovalLevel(String approvalLevel, BindingResult result) {
        String approvalLevelField = "approvalLevel";
        String invalidApprovalLevelCode = "approvalLevel.invalid";
        if (!Objects.nonNull(approvalLevel) || approvalLevel.isEmpty()) {
            result.rejectValue(approvalLevelField, invalidApprovalLevelCode, commonResponseUtil.getMessage("message.validation.level.required"));
        }
    }

    private void validateAuthorityType(String authorityType, BindingResult result) {
        String authorityTypeField = "authorityType";
        String invalidAuthorityTypeCode = "authorityType.invalid";
        if (!Objects.nonNull(authorityType) || authorityType.isEmpty()) {
            result.rejectValue(authorityTypeField, invalidAuthorityTypeCode, commonResponseUtil.getMessage("message.validation.authority.type.required"));
        }
    }

    private void validateEmail(String email, BindingResult result) {
        String emailField = "email";
        String invalidEmailCode = "email.invalid";
        if (email.isEmpty()){
            return;
        } else if (isValidEmail(email)) {
            result.rejectValue(emailField, invalidEmailCode, commonResponseUtil.getMessage("message.validation.local.guardian.details.email"));
        }
    }

    private boolean isValidEmail(String email) {
        return !ModelConstants.EMAIL_PATTERN.matcher(email).matches();
    }
}
