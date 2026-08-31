package com.iitm.hosteldine.model.asset;

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
@Table(name = "\"ASSET_MAINTENANCE_TYPE\"", schema = ModelConstants.SCHEMA)
public class AssetMaintenanceTypeEntity extends CommonEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "maintenance_type_id", nullable = false)
	private Long maintenanceTypeId;

	@Column(name = "maintenance_type", nullable = false, length = 64)
	private String maintenanceType;

}
