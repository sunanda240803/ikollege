package com.iitm.hosteldine.model.student;

import java.time.LocalDate;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"MESS_TO_CARD_AMOUNT_TRANSFER\"", schema = "schooldev")
public class MessCardAmountTransferEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "messtocard_id")
    private Long messtocardId;

    @Column(name = "student_id", length = 16)
    private String studentId;

    @Column(name = "transfer_amount")
    private Double transferAmount;

    @Column(name = "request_date")
    private LocalDate requestDate;

    @Column(name = "requested_status", length = 16)
    private String requestedStatus;

    @Column(name = "transferred_date")
    private LocalDate transferredDate;

    @Column(name = "checkbox_transfer", length = 16)
    private String checkboxTransfer;

    @Column(name = "student_name", length = 128)
    private String studentName;

    
		 


}
