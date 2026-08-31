package com.iitm.hosteldine.dto.dashboard.student;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.model.dashboard.student.StudentBlackListDetailEntity;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for {@link StudentBlackListDetailEntity}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentBlackListDetailDto implements Serializable {
    private Long id;
    @Size(max = 32)
    private String studentId;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate fromDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate toDate;
    private String currentlyActive;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate currentlyActiveDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDateTime createdAt;
    @Size(max = 1026)
    private String remarkDescription;
    private String firstName;
    private String lastName;
    private String studentRemark;
    private MultipartFile file;
    private List<String> errorList;
    private Integer rowNumber;
}