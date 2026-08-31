package com.iitm.hosteldine.dto.student;

import com.iitm.hosteldine.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentSearchViewDto {
    private String studentId;
    private String deptName;
    private String studentName;
    private String studentStatus;
    private String hostelName;
    private Long hostelId;
    private String roomNumber;
    private String seat;
    private Long roomId;
    private Long roomAllotmentId;
    private String messName;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String gender;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate dob;
    private String bloodGroup;
    private String category;
    private Long studentMobile;
    private String studentAddress;
    private String previousId;
    private Long aadhaarNumber;
    private String panNumber;
    private String guardianStatus;
    private String signedParentName;
    private String facultyName;
    private Long facultyContactNo;
    private String facultyEmail;
    private String settlementFlag;
    private String dayScholar;
    private String vacationCategory;
    private Boolean isMissing;
    private String hostelOfficeEmail;
    private String auth;
    private String studentPersonalEmail;
    private Long bioDataId;
    private String applicationNumber;
    private String emailId;
    private String searchCriteria;
    private String searchString;
    private String field;
    private Boolean dayScholarStatus;
    private Boolean vacationCategoryStatus;
    private List<AllStudentsDetailsViewDto> allStudentsDetailsViewList;
    private String pwd;
    private Integer pwdPercentage;
    private String pwdDescription;
    private String otherInfo;
    private String messPreference;
    private String activeFlag;
}
