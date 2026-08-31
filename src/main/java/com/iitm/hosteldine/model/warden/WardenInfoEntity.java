package com.iitm.hosteldine.model.warden;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import java.time.LocalDate;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"WARDEN_INFO\"", schema = ModelConstants.SCHEMA)
public class WardenInfoEntity extends CommonEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long id;

	@Column(name = "warden_name", nullable = false, length = 128)
	private String wardenName;

	@Column(name = "office_no", length = 32)
	private String officeNo;

	@Column(name = "warden_email", length = 128)
	private String wardenEmail;

	@Column(name = "phone_number", length = 32)
	private String phoneNumber;

	@Column(name = "ldap_username", length = 32)
	private String ldapUsername;

	@Column(name = "alternate_email", length = 128)
	private String alternateEmail;

	@Column(name = "image_bytes")
	private byte[] imageBytes;

	@Column(name = "image_name")
	private String imageName;

	@Column(name = "warden_info_url", length = 250)
	private String wardenInfoUrl;

	@Column(name = "associate_warden_name", length = 128)
	private String associateWardenName;

	@Column(name = "associate_ldap_username", length = 32)
	private String associateLdapUsername;
}
