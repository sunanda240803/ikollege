package com.iitm.hosteldine.dto.studentDashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.model.mess.StudentMessLoginIssuePriorityId;

import lombok.Data;

import java.lang.Integer;

@Data
public class StudentMessLoginIssuePriorityDto {
    @JsonProperty("id")
    private StudentMessLoginIssuePriorityId id;
    @JsonProperty("physicallyChallenged")
    private String physicallyChallenged;
    @JsonProperty("studentName")
    private String studentName;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("description")
    private String description;
    @JsonProperty("hostelId")
    private Integer hostelId;
    @JsonProperty("roomNo")
    private String roomNo;
    @JsonProperty("semMon")
    private String semMon;
}