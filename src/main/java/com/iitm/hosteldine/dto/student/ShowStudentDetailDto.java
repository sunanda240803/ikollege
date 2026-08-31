package com.iitm.hosteldine.dto.student;

import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.iitm.hosteldine.model.student.ShowStudentDetailEntity}
 */
@Data
public class ShowStudentDetailDto implements Serializable {
    private Long id;
    private String studentId;
    private Long seatId;
    private Double amount;
    private Double discountAmount;
    private Long schoolId;
    private String activeStatus;
    private Integer purchasedCount;
    private String studentName;
    private String deliveryType;
    private String deliveryAddress;
}