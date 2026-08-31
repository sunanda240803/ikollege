package com.iitm.hosteldine.dto.hostel;

import com.iitm.hosteldine.model.hostel.HostelFloorMasterEntity;
import com.iitm.hosteldine.model.hostel.HostelRoomInfoEntity;
import com.iitm.hosteldine.model.hostel.VacationHostelRoomAllotmentInfoEntity;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link VacationHostelRoomAllotmentInfoEntity}
 */
@Data
public class VacationHostelRoomAllotmentInfoDto implements Serializable {
    Long id;
    HostelFloorMasterEntity building;
    Long roomId;
    HostelRoomInfoEntity roomid;
    String requestid;
    LocalDate vacatedate;
    LocalDate stayFromDate;
    LocalDate stayToDate;
    String studentName;
    LocalDate dob;
    String email;
    String natureOfAppointment;
    String diningRequired;
    String studentId;
    String department;
    String stayType;
    String subRoomid;
    String stayId;
    String gender;
    String screenType;
    Long hostelId;
}