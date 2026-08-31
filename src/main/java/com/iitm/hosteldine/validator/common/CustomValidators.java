package com.iitm.hosteldine.validator.common;

import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class CustomValidators {

    private final CommonResponseUtil commonResponseUtil;

    public boolean isNullOrEmpty(Object field) {
        if(field instanceof String string) {
            return string.isEmpty();
        }
        return field == null;
    }

    public void rejectField(BindingResult bindingResult, String field, String messageKey) {
        bindingResult.rejectValue(field, field + ".invalid", commonResponseUtil.getMessage(messageKey));
    }

    public  <T> void validateField(T field, String fieldName, String errorMessage, BindingResult result) {
        if (isNullOrEmpty(field)) {
            rejectField(result, fieldName, errorMessage);
        }
    }

    public void validateDateOrder(LocalDate startDate, LocalDate endDate, String fieldName, String errorMessage,
                             BindingResult result) {
        if (Objects.nonNull(startDate) && Objects.nonNull(endDate) && endDate.isBefore(startDate)) {
            rejectField(result, fieldName, errorMessage);
        }
    }

}
