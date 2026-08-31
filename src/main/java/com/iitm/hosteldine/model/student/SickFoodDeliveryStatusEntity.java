package com.iitm.hosteldine.model.student;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

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
@Table(name = "\"SICK_FOOD_DELIVERY_STATUS\"",  schema = ModelConstants.SCHEMA)
public class SickFoodDeliveryStatusEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", referencedColumnName = "id")
    private SickFoodRequestEntity sickFoodRequest;

    @Column(name = "mess_session", nullable = false, length = 8)
    private String messSession;

    @Column(name = "caterer_status", length = 32)
    private String catererStatus;

    @Column(name = "stud_delivery_status", length = 32)
    private String studentDeliveryStatus;

    @Column(name = "stud_feedback")
    private String studentFeedback;

    @Column(name = "feed_back_rating", length = 8)
    private String feedbackRating;
    
    @Column(name = "food_delivery_status")
    private Boolean foodDeliveryStatus;



}
