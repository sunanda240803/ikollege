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
@Table(name ="\"MESS_TO_CARD_AMOUNT_TRANSFER_CONTROLLER\"", schema = "schooldev")
public class MessCardAmountTransferControllerEntity extends CommonEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id")
    private Long configId;

    @Column(name = "opening_date")
    private LocalDate openingDate;

    @Column(name = "closing_date")
    private LocalDate closingDate;

    @Column(name = "opening_time", length = 16)
    private String openingTime;

    @Column(name = "closing_time", length = 16)
    private String closingTime;

    @Column(name = "max_amount")
    private Double maxAmount;

   

  


    
		 


}
