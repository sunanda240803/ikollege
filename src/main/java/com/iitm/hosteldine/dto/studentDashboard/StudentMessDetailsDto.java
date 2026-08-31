package com.iitm.hosteldine.dto.studentDashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.dto.student.AllStudentsDetailsViewDto;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.model.mess.MessMasterEntity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

@Data
public class StudentMessDetailsDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("studentId")
    private String studentId;
    @JsonProperty("messId")
    private Long messId;
    @JsonProperty("fromDate")
    private LocalDate fromDate;
    @JsonProperty("toDate")
    private LocalDate toDate;
    @JsonProperty("userIp")
    private String userIp;
    @JsonProperty("hostelSigned")
    private String hostelSigned;
    @JsonProperty("messSigned")
    private String messSigned;
    @JsonProperty("allotedSlno")
    private Integer allotedSlno;
    @JsonProperty("changeFromDate")
    private LocalDate changeFromDate;
    @JsonProperty("changeToDate")
    private LocalDate changeToDate;
    @JsonProperty("currentActiveFlag")
    private String currentActiveFlag;
    @JsonProperty("pushRemoveStatus")
    private String pushRemoveStatus;
    @JsonProperty("exceptionStatus")
    private String exceptionStatus;
    @JsonProperty("exceptionReason")
    private String exceptionReason;
    @JsonProperty("exceptionDate")
    private LocalDate exceptionDate;
    @JsonProperty("pushStatus")
    private String pushStatus;
    @JsonProperty("pushDate")
    private LocalDate pushDate;
    @JsonProperty("mmcId")
    private Long mmcId;
    @JsonProperty("toRemoveDate")
    private LocalDate toRemoveDate;
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("mailStatus")
    private String mailStatus;
    @JsonProperty("selfAllotmentQrUsageDate")
    private LocalDateTime selfAllotmentQrUsageDate;
    @JsonProperty("selfAllotmentQrStatus")
    private String selfAllotmentQrStatus;
    @JsonProperty("selfAllotmentQrNumber")
    private String selfAllotmentQrNumber;
    @JsonProperty("comments")
    private String comments;
    @JsonProperty("toPushDate")
    private LocalDate toPushDate;
    @JsonProperty("messMaster")
    private MessMasterEntity messMaster;
    @JsonProperty("studentDetailsInfo")
    private StudentDetailsInfoEntity studentDetailsInfo;

    private AllStudentsDetailsViewDto allStudentsDetailsViewDto;
    private MessMasterDto messMasterDto;
    private String description;
}