package com.iitm.hosteldine.dto.studentDashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Long;
import java.lang.Integer;
import java.time.LocalDate;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.model.mess.MessMasterEntity;

@Data
public class StudentExcessMessDetailsDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("mmcId")
    private Integer mmcId;
    @JsonProperty("fromDate")
    private LocalDate fromDate;
    @JsonProperty("toDate")
    private LocalDate toDate;
    @JsonProperty("studentDetailsInfo")
    private StudentDetailsInfoEntity studentDetailsInfo;
    @JsonProperty("messMaster")
    private MessMasterEntity messMaster;
}