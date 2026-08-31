package com.iitm.hosteldine.model.mess;

import java.time.LocalDate;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "\"MESS_MASTER_CONTROLLER\"", schema = ModelConstants.SCHEMA)
public class MessMasterControllerEntity extends CommonEntity {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "month", length = 16)
    private String month;

    @Column(name = "reg_begin_date")
    private LocalDate regBeginDate;

    @Column(name = "reg_begin_time", length = 16)
    private String regBeginTime;

    @Column(name = "reg_end_date")
    private LocalDate regEndDate;

    @Column(name = "reg_end_time", length = 16)
    private String regEndTime;

    @Column(name = "dining_from_date")
    private LocalDate diningFromDate;

    @Column(name = "dining_to_date")
    private LocalDate diningToDate;

    @Column(name = "student_edit_status")
    private Boolean studentEditStatus;

    @Column(name = "student_device_registration_status")
    private Boolean studentDeviceRegistrationStatus;

    @Column(name = "exchange_from_date")
    private LocalDate exchangeFromDate;

    @Column(name = "exchange_to_date")
    private LocalDate exchangeToDate;

    @Column(name = "feedback_status")
    private Boolean feedbackStatus;
    
    @Column(name = "current_active_flag")
    private String currentActiveFlag;

    @Column(name = "pushing_time", length = 128)
    private String pushingTime;

    @Column(name = "pushing_date")
    private LocalDate pushingDate;

    @Column(name = "semester_begin")
    private Boolean semesterBegin;

    @Column(name = "bulk_mail_subject", length = 256)
    private String bulkMailSubject;

    @Column(name = "bulk_mail_content")
    private String bulkMailContent;

    @Column(name = "mail_status", length = 16)
    private String mailStatus;

    @Column(name = "reg_extend_start_time", length = 16)
    private String regExtendStartTime;

    @Column(name = "reg_extend_end_time", length = 16)
    private String regExtendEndTime;

    @Column(name = "reg_extend_start_date")
    private LocalDate regExtendStartDate;

    @Column(name = "reg_extend_end_date")
    private LocalDate regExtendEndDate;

    @Column(name = "food_court_amount")
    private Double foodCourtAmount;

    @Column(name = "allotment_from_date")
    private LocalDate allotmentFromDate;

    @Column(name = "allotment_to_date")
    private LocalDate allotmentToDate;

    @Column(name = "sem_start_date")
    private LocalDate semStartDate;

    @Column(name = "sem_end_date")
    private LocalDate semEndDate;

}
