package com.iitm.hosteldine.dto.hostel;

import com.iitm.hosteldine.dto.UserManagementDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HostelUserMappingIdDto {

	private HostelMasterDto hostel;

	private String user;
}
