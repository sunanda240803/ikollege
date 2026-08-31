package com.iitm.hosteldine.form.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessDineSummaryForm {
    private Long messPeriodId;
    private Long messId;
}
