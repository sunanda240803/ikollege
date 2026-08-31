package com.iitm.hosteldine.form.common;

import com.iitm.hosteldine.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacultyAccommodatiomRequstForm {
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate eventFromDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate eventToDate;
    private String approvalStatus;

}
