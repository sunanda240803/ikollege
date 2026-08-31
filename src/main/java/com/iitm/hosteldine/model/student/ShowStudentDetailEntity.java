package com.iitm.hosteldine.model.student;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"SHOW_STUDENT_DETAILS\"", schema = "schooldev")
public class ShowStudentDetailEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 32)
    @Column(name = "student_id", length = 32)
    private String studentId;

    @Column(name = "seat_id")
    private Long seatId;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "discount_amount")
    private Double discountAmount;

    @ColumnDefault("1")
    @Column(name = "purchased_count")
    private Integer purchasedCount;

    @Size(max = 12)
    @Column(name = "student_name", length = 12)
    private String studentName;

    @Size(max = 10)
    @Column(name = "delivery_type", length = 10)
    private String deliveryType;

    @Column(name = "delivery_address", length = Integer.MAX_VALUE)
    private String deliveryAddress;

    @Column(name ="contact_no",length = 10)
    private Long contactNo;

}