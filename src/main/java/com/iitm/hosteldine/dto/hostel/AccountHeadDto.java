package com.iitm.hosteldine.dto.hostel;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.iitm.hosteldine.model.hostel.AccountHeadEntity}
 */
@Data
public class AccountHeadDto implements Serializable {
    private AccountHeadIdDto id;
    private String accname;
    private Double opbal;
    private LocalDate opdate;
    private Double clbal;
    private LocalDate cldate;
    private String type;
    private Short blSubschedule;
    private Short blSchedule;
    private String bkt1;
    private String bkt2;
    private String bkt3;
    private String bkt4;
    private String bkt5;
    private String bkt6;
    private Short plSchedule;
    private Short plSubschedule;
    private String trn;
    private String sub;
    private Double itOpbal;
    private Integer companyid;
    private Double budget;
    private Boolean status;
    private String accHead;
    private double credit;
    private double debit;
    private String creditStr;
    private String debitStr;
}