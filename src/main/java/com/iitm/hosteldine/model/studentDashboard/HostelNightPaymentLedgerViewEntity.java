package com.iitm.hosteldine.model.studentDashboard;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Mapping for DB view
 */
@Getter
@Setter
@Entity
@Table(name = "\"HOSTEL_NIGHT_PAYMENT_LEDGER_VIEW\"", schema = ModelConstants.SCHEMA)
public class HostelNightPaymentLedgerViewEntity {
    @Id
    @Column(name = "student_id", length = 16)
    private String studentId;

    @Column(name = "student_name", length = 120)
    private String studentName;

    @Column(name = "hostel_name", length = 64)
    private String hostelName;

    @Column(name = "pay_by", length = 32)
    private String payBy;

    @Column(name = "total_veg_coupons")
    private Long totalVegCoupons;

    @Column(name = "total_nonveg_coupons")
    private Long totalNonvegCoupons;

    @Column(name = "total_amount")
    private Double totalAmount;

}