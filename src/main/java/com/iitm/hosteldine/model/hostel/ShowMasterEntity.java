package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import java.time.LocalDate;

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
@Table(name = "\"SHOW_MASTER\"", schema = ModelConstants.SCHEMA) 
public class ShowMasterEntity extends CommonEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "show_name", length = 64)
	private String showName;

	@Column(name = "show_desc", length = 127)
	private String showDescr;

	@Column(name = "reg_starting_date")
	private LocalDate regStartingDate;

	@Column(name = "reg_ending_date")
	private LocalDate regEndingDate;

	@Column(name = "image_location")
	private String imageLocation;

	@Column(name = "currently_active", length = 1)
	private String currentlyActive;

	@Column(name = "max_seat_count")
	private Integer maxSeatCount;

	@Column(name = "seat_image_location")
	private String seatImageLocation;

	@Column(name = "is_name_type")
	private Boolean isNameType;

	/*@Column(name = "event_id")
	private Long eventId;*/
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private ShowEventMasterEntity showEventMaster;
}
