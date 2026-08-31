package com.iitm.hosteldine.constant.dashboard.student;

import lombok.Getter;

@Getter
public enum HostelPaymentTypeEnum {
    ICOLLECT("icollect"),
    DD("dd"),
    BANK_LOAN("bankLoan"),
    ALREADY_PAID("alreadyPaid"),
    PAYMENT_CONFIRMED("Payment Confirmed");
    private final String value;

    HostelPaymentTypeEnum(String value) {
        this.value = value;
    }

    public static HostelPaymentTypeEnum fromValue(String value) {
        for (HostelPaymentTypeEnum type : HostelPaymentTypeEnum.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid value for HostelPaymentTypeEnum: " + value);
    }
}
