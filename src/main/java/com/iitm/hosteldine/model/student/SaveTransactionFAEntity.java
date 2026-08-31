package com.iitm.hosteldine.model.student;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import java.time.LocalDate;

import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"FA_TRANSACTION_DETAILS\"", schema = ModelConstants.SCHEMA)
public class SaveTransactionFAEntity extends CommonEntity{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fa_transaction_id", nullable = false)
    private Long faTransactionId;
	
	@Column(name = "screen_type")
	private String screenType;
	
	@Column(name = "transfer_type")
	private String transferType;
	
	@Column(name = "hostel_name")
	private String hostelName;
	
	@Column(name = "reference_number")
	private String referenceNumber;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "total_amount")
	private double totalAmount;
	
	@Column(name = "num_of_transaction")
	private int numOfTransaction;
	
	@Column(name = "transaction_status")
	private String transactionStatus;
	
	@Column(name = "transfer_date")
	private LocalDate transferDate;
	
	@Column(name = "debit_or_credit")
	private String debitOrCredit;
	
}
