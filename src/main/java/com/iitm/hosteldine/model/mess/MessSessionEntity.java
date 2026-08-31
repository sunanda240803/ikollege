package com.iitm.hosteldine.model.mess;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"MESS_SESSIONS\"",  schema = ModelConstants.SCHEMA)
@EqualsAndHashCode
public class MessSessionEntity extends CommonEntity {
	@EmbeddedId
	private MessSessionEntityId id;
	/*
	 * @MapsId("sessionName")
	 * 
	 * @ManyToOne(fetch = FetchType.LAZY, optional = false)
	 * 
	 * @JoinColumn(name = "session_name", nullable = false) private
	 * MessSessionsMasterEntity session;
	 * 
	 * @MapsId("messId")
	 * 
	 * @ManyToOne(fetch = FetchType.LAZY, optional = false)
	 * 
	 * @JoinColumn(name = "mess_id", nullable = false) private MessMasterEntity
	 * messMaster;
	 */

	@Column(name = "start_time", length = 32)
	private String startTime;

	@Column(name = "end_time", length = 32)
	private String endTime;

}
