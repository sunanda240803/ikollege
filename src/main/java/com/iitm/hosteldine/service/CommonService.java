package com.iitm.hosteldine.service;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.RoleDto;
import com.iitm.hosteldine.dto.SimsConfigDataJsonArrayDto;
import com.iitm.hosteldine.dto.UserManagementDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.entity.RoleEntity;
import com.iitm.hosteldine.entity.UserManagementEntity;
import com.iitm.hosteldine.mapper.RoleMapper;
import com.iitm.hosteldine.mapper.UserManagementMapper;
import com.iitm.hosteldine.repository.RoleRepository;
import com.iitm.hosteldine.repository.UserManagementRepository;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.hostel.HostelUserMappingService;
import com.iitm.hosteldine.service.warden.WardenInfoService;
import com.iitm.hosteldine.util.RoleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommonService {
    private final UserManagementRepository userManagementRepository;
    private final RoleRepository roleRepository;
	private final SimsConfigDataService simsConfigDataService;
	private final WardenInfoService wardenInfoService;
	private final HostelMasterService hostelMasterService;
	private final HostelUserMappingService hostelUserMappingService;

	public static String capitalizeEachWord(String name) {
        return Arrays.stream(name.split("\\s+"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

	public List<RoleDto> getRoleslist() {
		Optional<List<RoleEntity>> roleMasterList = roleRepository.findAllByActiveFlagOrderByRoleName(ModelConstants.STATUS_ACTIVE);
		ArrayList<RoleDto> resultList = null;
		if (roleMasterList.isPresent()) {
			resultList = new ArrayList<>();
			for (RoleEntity roleMaster : roleMasterList.get()) {
				RoleDto roleMas = RoleMapper.INSTANCE.toRoleDTO(roleMaster);
				resultList.add(roleMas);
			}
		}
		return resultList;
	}

	public String getUserName(String firstName, String lastName, String accountType, String userId) {
		String finalizedUserName = "";
		if (ModelConstants.HYPHEN.equals(lastName)) {
			lastName = null;
		}
		String userName = "";
		// concatenating first name and last name
		if (firstName != null) {
			userName = removeSpaces(firstName);
			if (lastName != null) {
				userName += removeSpaces("." + lastName);
			}
		}
		/*
		 * Checking For User Id Exist or not
		 */
		if (userId != null && (userId.equalsIgnoreCase("0") || userId.equalsIgnoreCase("new"))) {
			// checking user name is exist or not
			finalizedUserName = getNewUserName(userName);
		} else {
			Optional<UserManagementEntity> existUser = userManagementRepository.findByAccountTypeAndIdUserId(accountType, userId);
			UserManagementDto existUserDto = existUser.map(UserManagementMapper.INSTANCE::fromUserManagementEntity)
					.orElse(null);
			if (existUserDto != null) {
				finalizedUserName = existUserDto.getUserName();
			} else {
				// checking user name is exist or not
				finalizedUserName = getNewUserName(userName);
			}
		}
		return finalizedUserName;
	}

	private String getNewUserName(String userName) {
		int count = 0;
		String finalizedUserName = userName;
		boolean existUserName = this.checkUserName(userName);
		if (!existUserName) {
			while (!existUserName) {
				finalizedUserName = userName + (++count);
				existUserName = this.checkUserName(finalizedUserName);
			}
		}
		return finalizedUserName;
	}

    private boolean checkUserName(String userName) {
        Optional<UserManagementEntity> existUser = userManagementRepository.findByIdUsername(userName);
        UserManagementDto existUserDto = existUser.map(UserManagementMapper.INSTANCE::fromUserManagementEntity).orElse(null);
        boolean resultStatus = existUserDto == null;
        return resultStatus;
    }

    public String removeSpaces(String s) {
        StringTokenizer st = new StringTokenizer(s, " ", false);
        String t = "";
        while (st.hasMoreElements())
            t += st.nextElement();

        return t.toLowerCase();
    }

    public static String formatAmountToDecimalPlaces(double amount) {
        return String.format("%.2f", amount);
    }

    //To calculate max date in date input field
    public Date getMaxDate(Integer years) {
    	LocalDate currentDate = LocalDate.now();
		LocalDate minDate = currentDate.minusYears(years);
		return Date.valueOf(minDate);
    }

	public List<HostelMasterDto> getHostelListForUser() {
		List<HostelMasterDto> hostelList;
		String userRole = SecurityCtxUtil.userRole();
		String userName = SecurityCtxUtil.userName();

		List<String> allowedRoles = simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.ALL_HOSTEL_VIEW_ROLES)
				.stream()
				.map(SimsConfigDataJsonArrayDto::getValue)
				.toList();
		if (HostelConstants.WARDEN.getConstants().equals(userRole)) {
			hostelList = wardenInfoService.getWardenDetailsList(userName);
		} else if (allowedRoles.contains(userRole) || RoleEnum.DEAN.getValue().equals(userRole)) {
			hostelList = hostelMasterService.getHostelList();
		} else {
			hostelList = hostelUserMappingService.getHostelDetailsList(userName);
		}
		return hostelList;
	}
}
