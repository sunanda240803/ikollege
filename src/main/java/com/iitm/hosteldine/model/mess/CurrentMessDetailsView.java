package com.iitm.hosteldine.model.mess;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * Mapping for DB view
 */
@Data
@Entity
@Table(name = "\"CURRENT_MESS_DETAILS_VIEW\"", schema = ModelConstants.SCHEMA)
public class CurrentMessDetailsView{

    @Id
    @Column(name = "student_id", length = 32)
    private String studentId;

    @Column(name = "mess_id")
    private Integer messId;

    @Column(name = "mess_period_id")
    private Long messPeriodId;

    @Column(name = "dining_from_date")
    private LocalDate diningFromDate;

    @Column(name = "dining_to_date")
    private LocalDate diningToDate;

    @Size(max = 16)
    @Column(name = "month", length = 16)
    private String month;

    @Size(max = 128)
    @Column(name = "mess_name", length = 128)
    private String messName;

    @Size(max = 256)
    @Column(name = "description", length = 256)
    private String description;

    @Column(name = "is_food_court")
    private Boolean isFoodCourt;

}