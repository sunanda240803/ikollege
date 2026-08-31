package com.iitm.hosteldine.model.hostel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"HOSTEL_ROOM_INFO\"", schema = ModelConstants.SCHEMA)
public class HostelRoomInfoEntity extends CommonEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "room_id", nullable = false)
	private Long id;

	@Column(name = "room_no", length = 32)
	private String roomNo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "building_id")
	private HostelFloorMasterEntity building;

	@Column(name = "capacity")
	private Integer capacity;

	@Column(name = "occupied")
	private Integer occupied;

	@ColumnDefault("false")
	@Column(name = "is_vacation")
	private Boolean isVacation;

	@Column(name = "room_size", length = Integer.MAX_VALUE)
	private String roomSize;

	@ColumnDefault("0")
	@Column(name = "vac_capacity")
	private Integer vacCapacity;

	@ColumnDefault("0")
	@Column(name = "official_guest_status", length = Integer.MAX_VALUE)
	private String officialGuestStatus;

}