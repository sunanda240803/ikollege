package com.iitm.hosteldine.model.mess;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "\"MESS_VENDOR_MASTER\"", schema = ModelConstants.SCHEMA)
public class MessVendorMasterEntity extends CommonEntity {
	@Id
	@Column(name = "vendor_code", nullable = false, length = 8)
	private String vendorCode;

	@Column(name = "vendor_name", nullable = false, length = 64)
	private String vendorName;

	@Column(name = "address1", length = 128)
	private String address1;

	@Column(name = "address2", length = 128)
	private String address2;

	@Column(name = "pincode")
	private Integer pincode;

	@Column(name = "email", length = 64)
	private String email;

	@Column(name = "user_name",  length = 64)
	private String userName;

	@Column(name = "password", length = 64)
	private String password;

	@ColumnDefault("0")
	@Column(name = "mobile_no")
	private Long mobileNo;

	@Column(name = "contact_person", length = 32)
	private String contactPerson;

	@Column(name = "license_no", length = 64)
	private String licenseNo;

	@Column(name = "license_valid_upto")
	private LocalDate licenseValidUpto;

	@Column(name = "pan_no", length = 32)
	private String panNo;

	@Column(name = "is_caterer", length = 5)
	private String isCaterer;

	@Column(name = "managing_director", length = 64)
	private String managingDirector;

	@Column(name = "branch_manager", length = 64)
	private String branchManager;

	@Column(name = "branch", length = 64)
	private String branch;

	@Column(name = "place", length = 64)
	private String place;

	@Column(name = "total_no_of_workers")
	private Integer totalNoOfWorkers;

}
