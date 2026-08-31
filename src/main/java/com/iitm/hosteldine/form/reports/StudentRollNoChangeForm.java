package com.iitm.hosteldine.form.reports;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.form.common.PaginationForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class StudentRollNoChangeForm extends PaginationForm {
    private String previousId;
    private String changeStudentId;
    private String studentName;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate requestDate;
}