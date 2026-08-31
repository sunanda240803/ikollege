package com.iitm.hosteldine.dto.studentDashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.model.mess.StudentMessPriorityRegistrationId;

import lombok.Data;

import java.lang.Long;
import java.util.ArrayList;
import java.util.List;

@Data
public class StudentMessPriorityRegistrationDto {
    @JsonProperty("id")
    private StudentMessPriorityRegistrationId id;
    @JsonProperty("messPreference")
    private String messPreference;
    @JsonProperty("groupId")
    private Long groupId;
    private String messName;
    private Long messId;
    private String description;
    private Integer capacity; 
    List<MessMasterDto> messList =  new ArrayList<>(); // Initialized to avoid null
    private String studentName;
    private String parentEmailId;
    public Integer hostelId;
    private Integer roomNo;
    private String mobileNo;
    private String gender;

}