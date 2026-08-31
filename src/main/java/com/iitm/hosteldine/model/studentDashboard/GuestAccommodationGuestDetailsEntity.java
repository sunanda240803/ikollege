package com.iitm.hosteldine.model.studentDashboard;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

@Getter
@Setter
@Entity
@Table(name = "\"GUEST_ACCOMMODATION_GUEST_DETAILS\"", schema = ModelConstants.SCHEMA)
public class GuestAccommodationGuestDetailsEntity extends CommonEntity{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "guest_id", nullable = false)
    private Long guestId;

    @Column(name = "request_id")
    private Integer requestId;

    @Column(name = "guest_name", length = 256)
    private String guestName;

    @Column(name = "relation_of_guest", length = 256)
    private String relationOfGuest;

    @Column(name = "id_proof", length = 256)
    private String idProof;

    @Column(name = "proof_description", length = 256)
    private String proofDescription;

    @Column(name = "guest_gender", length = 16)
    private String guestGender;
}
