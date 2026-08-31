package com.iitm.hosteldine.dto.student.wellness;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.student.AllStudentsDetailsViewDto;

import lombok.Data;

@Data
public class StudentWellnessCategoricalDataDto {
    private Long id;
    private String studentId;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate referralDate;
    private String referralType;
    private String referralBy;
    private String referralEmail;
    private String referralPhone;
    private String referralOthersDescription;
    private String coordinatedName;
    private String coordinatedEmail;
    private String concernType;
    private String concernOthersDescription;
    private Boolean selfHarm;
    private String selfHarmType;
    private Boolean psychiatricConsultation;
    private String psychiatricName;
    private String additionalDetails;
    private String referralLandlineNum;
    private String otherStudName;
    private String otherStudEmail;
    private String otherStudPhone;
    private String category;
    
    private String studentName;
    private String wellnessId;
    private LocalDate submittedDate;
    private String noOfVisit;
    private String hostelName;
    private String dayScholar;
    private AllStudentsDetailsViewDto studentDetails;
}