package com.iitm.hosteldine.model.student;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.validator.fieldValidators.ValidStringField;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO for {@link StudentHostelPaymentLateFeeDetailEntity}
 */
@Getter
@Setter
@ToString
@Builder
public class StudentHostelPaymentLateFeeDetailDto{
    private Long id;
    @ValidStringField(message = "message.label.validation.student.id.is.required",fieldName = "message.label.studentID",min = 3,max =20)
    private String studentId;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate dueDate;
    private Integer hostelId;
    private String hostelName;
    @ValidStringField(message = "message.validation.description.required",fieldName = "message.label.description",min = 3,max =120)
    private String description;
}