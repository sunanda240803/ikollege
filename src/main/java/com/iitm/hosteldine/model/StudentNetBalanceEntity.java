package com.iitm.hosteldine.model;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"student_net_balance\"", schema = ModelConstants.SCHEMA)
public class StudentNetBalanceEntity {

    @Id
    @NotNull
    @Column(name = "student_id")
    private String studentId;

    @Column(name = "net_balance_mess")
    private Double netBalanceMess;

    @Column(name = "net_balance_card")
    private Double netBalanceCard;

}