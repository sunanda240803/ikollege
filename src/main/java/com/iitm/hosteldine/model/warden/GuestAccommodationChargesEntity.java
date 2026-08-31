package com.iitm.hosteldine.model.warden;

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

@Getter
@Setter
@Entity
@Table(name = "\"GUEST_ACCOMMODATION_CHARGES\"", schema = ModelConstants.SCHEMA)
public class GuestAccommodationChargesEntity extends CommonEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer amount;

	@Column(length = 64)
	private String description;

	@Column(name = "from_date")
	private LocalDate fromDate;

	@Column(name = "to_date")
	private LocalDate toDate;

	@Column(name = "individual_room_amount")
	private Integer individualRoomAmount;

	@Column(name = "individual_room_multiple_amount")
	private Integer individualRoomMultipleAmount;

}
