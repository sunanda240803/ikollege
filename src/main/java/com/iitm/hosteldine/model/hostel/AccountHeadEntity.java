package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Entity
@Table(name = "\"ACCOUNT_HEAD\"", schema = "schooldev")
public class AccountHeadEntity extends CommonEntity {
    @EmbeddedId
    private AccountHeadIdEntity id;

    @Column(name = "accname", nullable = false, length = 45)
    private String accname;

    @Column(name = "opbal")
    private Double opbal;

    @Column(name = "opdate")
    private LocalDate opdate;

    @Column(name = "clbal")
    private Double clbal;

    @Column(name = "cldate")
    private LocalDate cldate;

    @Column(name = "type", length = 2)
    private String type;

    @Column(name = "bl_subschedule")
    private Short blSubschedule;

    @Column(name = "bl_schedule")
    private Short blSchedule;

    @Column(name = "bkt1", length = 2)
    private String bkt1;

    @Column(name = "bkt2", length = 2)
    private String bkt2;

    @Column(name = "bkt3", length = 2)
    private String bkt3;

    @Column(name = "bkt4", length = 2)
    private String bkt4;

    @Column(name = "bkt5", length = 2)
    private String bkt5;

    @Column(name = "bkt6", length = 2)
    private String bkt6;

    @Column(name = "pl_schedule")
    private Short plSchedule;

    @Column(name = "pl_subschedule")
    private Short plSubschedule;

    @Column(name = "trn", length = 1)
    private String trn;

    @Column(name = "sub", length = 1)
    private String sub;

    @Column(name = "it_opbal")
    private Double itOpbal;

    @Column(name = "companyid", nullable = false)
    private Integer companyid;

    @Column(name = "budget")
    private Double budget;

}