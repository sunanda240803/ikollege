package com.iitm.hosteldine.validator.hostel;

import com.iitm.hosteldine.dto.hostel.GuestCouponConfigDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class GuestCouponConfigValidator {

    private final MessageSource messageSource;

    public void validate(GuestCouponConfigDto guestCouponConfigDto, BindingResult bindingResult) {
        validateRequiredFields(guestCouponConfigDto, bindingResult);
        validateDateFields(guestCouponConfigDto, bindingResult);
    }

    private void validateRequiredFields(GuestCouponConfigDto guestCouponConfigDto, BindingResult bindingResult) {
        validateField(guestCouponConfigDto.getCategory(), "category", "error.category", "message.validation.coupon.category.required", bindingResult);
        validateField(guestCouponConfigDto.getEffectiveDate(), "effectiveDate", "error.effectiveDate", "message.guest.coupon.error.effectiveDate", bindingResult);
        validateField(guestCouponConfigDto.getValidToDate(), "validToDate", "error.validToDate", "message.guest.coupon.error.validToDate", bindingResult);
        validateField(guestCouponConfigDto.getBreakfastAmount(), "breakfastAmount", "error.breakfastAmount", "message.guest.coupon.error.breakfastAmount", bindingResult);
        validateField(guestCouponConfigDto.getLunchAmount(), "lunchAmount", "error.lunchAmount", "message.guest.coupon.error.lunchAmount", bindingResult);
        validateField(guestCouponConfigDto.getDinnerAmount(), "dinnerAmount", "error.dinnerAmount", "message.guest.coupon.error.dinnerAmount", bindingResult);
        validateField(guestCouponConfigDto.getSnacksAmount(), "snacksAmount", "error.snacksAmount", "message.guest.coupon.error.snacksAmount", bindingResult);
    }

    private void validateDateFields(GuestCouponConfigDto guestCouponConfigDto, BindingResult bindingResult) {
        if (guestCouponConfigDto.getEffectiveDate() != null && guestCouponConfigDto.getValidToDate() != null) {
            if (guestCouponConfigDto.getEffectiveDate().isAfter(guestCouponConfigDto.getValidToDate())) {
                bindingResult.rejectValue("validToDate", "error.validToDate", messageSource.getMessage("message.guest.coupon.error.date.validToDate", null, Locale.getDefault()));
            }
        }
    }

    private void validateField(Object fieldValue, String fieldName, String errorCode, String messageKey, BindingResult bindingResult) {
        if (fieldValue == null || (fieldValue instanceof String && ((String) fieldValue).isEmpty())) {
            bindingResult.rejectValue(fieldName, errorCode, messageSource.getMessage(messageKey, null, Locale.getDefault()));
        }
    }
}

