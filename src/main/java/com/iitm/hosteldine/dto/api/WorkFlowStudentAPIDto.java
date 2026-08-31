package com.iitm.hosteldine.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.ALWAYS)
public class WorkFlowStudentAPIDto {
    @JsonProperty("roll_number")
    private String studentId;
    @JsonProperty("name_of_the_student")
    private String studentName;
    @JsonProperty("hostel_name")
    private String hostelName;
    @JsonProperty("hostel_room_number")
    private int roomNo;
    @JsonProperty("bed_number")
    private String seatName;
    @JsonProperty("date_of_the_last_residence_change")
    private Date lastResidenceDate;
    @JsonProperty("hostel_student_status")
    private String hostelStudentStatus;
    @JsonProperty("previous_roll_number")
    private String previousId;
    @JsonProperty("vacate_date")
    private String vacateDate;
    @JsonProperty("workflow_flag")
    private String workFlowFlag;
    @JsonProperty("workflow_date")
    private String workFlowDate;
    @JsonProperty("input_date_parameter")
    private String inputDateParameter;
}
