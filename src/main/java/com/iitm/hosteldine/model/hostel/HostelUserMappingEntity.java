package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

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
@Table(name = "\"HOSTEL_USER_MAPPING\"", schema = ModelConstants.SCHEMA)
public class HostelUserMappingEntity extends CommonEntity {
	@EmbeddedId
	private HostelUserMappingId id;
}
