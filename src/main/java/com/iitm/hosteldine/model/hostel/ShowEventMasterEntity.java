package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@Entity
@Table(name = "\"SHOW_EVENT_MASTER\"", schema = "schooldev")
public class ShowEventMasterEntity extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "event_name", length = 64)
    private String eventName;

    @Column(name = "event_desc", length = 127)
    private String eventDesc;

    @Column(name = "school_id")
    private Long schoolId;

    @Column(name = "event_starting_date")
    private LocalDate eventStartingDate;

    @Column(name = "event_ending_date")
    private LocalDate eventEndingDate;

    @Column(name = "credit_limit")
    private Double creditLimit;

    @Column(name = "logo_name", length = Integer.MAX_VALUE)
    private String logoName;

    @Column(name = "contact_us", length = Integer.MAX_VALUE)
    private String contactUs;

    @Column(name = "currently_active", length = 1)
    private String currentlyActive;

    /*@Column(name = "logo")
    private byte[] logo;*/

    @Column(name = "actual_event_starting_date")
    private LocalDate actualEventStartingDate;

    @Column(name = "actual_event_ending_date")
    private LocalDate actualEventEndingDate;

    @Column(name = "event_acc_registration_starting_date")
    private LocalDate eventAccRegistrationStartingDate;

    @Column(name = "event_acc_registration_ending_date")
    private LocalDate eventAccRegistrationEndingDate;

    @Column(name = "sales_type")
    private Boolean salesType;

    @Column(name = "event_type", length = 200)
    private String eventType;

    @Column(name = "acchead", length = 45)
    private String acchead;

}