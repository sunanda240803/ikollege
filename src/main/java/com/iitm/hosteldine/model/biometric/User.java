package com.iitm.hosteldine.model.biometric;

import com.iitm.hosteldine.constant.ModelConstants;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

/**
 * 
 * @author Dhivakar
 *
 */
@Getter
@Setter
@Entity
@Table(name = "\"USER_MANAGEMENT\"", schema = ModelConstants.SCHEMA)
public class User {
	
	@Id
	@Column(name = "v_um_user_id")
	private String userId;

	@Column(name = "v_um_username")
	private String userName;

	@Column(name = "v_um_password")
	private String password;

	@Column(name = "v_um_account_type")
	private String roleType;

	@Column(name = "v_um_authentication_server")
	private String authServer;

}