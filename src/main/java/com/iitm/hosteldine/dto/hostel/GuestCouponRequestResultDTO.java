package com.iitm.hosteldine.dto.hostel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuestCouponRequestResultDTO {
    private Integer requestId;
    private String category;
    private String studentId;
    private String name;
    private Date diningFrom;
    private Date diningTo;
    private String paymentStatus;
    private String numOfBreakfast;
    private String numOfLunch;
    private String numOfDinner;
    private String totalAmount;
    private String approvalStatus;
    private Timestamp createdAt;
    private String mailStatus;
    private String orderNum;
    private String messName;
    private String vegOrNonveg;
    private String numOfSnacks;
    private String activeFlag;
    private String encryReqIdView;
    private String encryReqIdPdf;
    private String encryReqIdDelete;
    private boolean deleteStatus;
    private List<GuestCouponRequestResultDTO> reqList;
}
