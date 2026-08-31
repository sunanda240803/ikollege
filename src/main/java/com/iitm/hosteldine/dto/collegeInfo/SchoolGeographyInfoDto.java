package com.iitm.hosteldine.dto.collegeInfo;

import com.iitm.hosteldine.constant.Constants;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class SchoolGeographyInfoDto {
    private Integer schoolId;
    private String schoolName;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private Date dateOfEstd;
    private String address1;
    private Long landlineContactNo1;
    private Integer landlineContactNo1Ext;
    private Long landlineContactNo2;
    private Integer landlineContactNo2Ext;
    private String email;
    private String website;
    private String location;
    private String district;
    private String pincode;
}