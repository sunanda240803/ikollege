package com.iitm.hosteldine.entity.mess;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"MESS_OPENING_BAL\"", schema = "schooldev")
public class MessOpeningBalEntity extends CommonEntity {
    @EmbeddedId
    private MessOpeningBalEntityId id;

    @Column(name = "opn_date")
    private LocalDate opnDate;

    @Column(name = "amount")
    private Double amount;

    @Size(max = 1)
    @Column(name = "debit_or_credit", length = 1)
    private String debitOrCredit;

    @ColumnDefault("0")
    @Column(name = "cardamount")
    private Double cardamount;

}