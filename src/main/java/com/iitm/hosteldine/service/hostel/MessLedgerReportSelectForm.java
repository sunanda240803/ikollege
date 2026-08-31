package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.form.common.SelectForm;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class MessLedgerReportSelectForm extends SelectForm {
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate fromDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate toDate;
}
