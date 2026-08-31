package com.iitm.hosteldine.dto.student;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
public class StudentBulkInfoDto {
    private MultipartFile file;
    private String studentId;
    private String firstName;
    private String lastName;
    private String gender;
    private String course;
    private String previousId;
    private String emailId;
    private String ipAddress;
    private String excelErrorMsg;
    private String error;
    private List<String> errorList;
    private String newPreviousId;
    List<String> studentIds;
    private String messPeriod;
    private byte[] fileBytes;
    private String logTag;
}
