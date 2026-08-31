package com.iitm.hosteldine.service.hostel;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.RoleDto;
import com.iitm.hosteldine.dto.hostel.PrincipalDashBoardDTO;
import com.iitm.hosteldine.dto.hostel.TabDTO;
import com.iitm.hosteldine.model.hostel.RoleTabSettings;
import com.iitm.hosteldine.model.hostel.UserTabSettings;
import com.iitm.hosteldine.repository.hostel.RoleTabSettingsRepository;
import com.iitm.hosteldine.repository.hostel.UserTabSettingsRepository;
import com.iitm.hosteldine.service.adminSettings.RoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TabPrivilegeService {

    private final RoleTabSettingsRepository roleTabSettingsRepository;
    private final RoleService roleService;
    private final UserTabSettingsRepository userTabSettingsRepository;

    public String saveAdminTabPrivileges(PrincipalDashBoardDTO principalDashBoardDTO) {
        String methodName = "saveAdminTabPrivileges";
        String status = null;
        boolean showHide = true;
        boolean showHideMain = true;
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        try {
            List<String> selectedItems = principalDashBoardDTO.getCheckedTabList();
            Map<String, List<String>> checkedPrivilegeMap = new LinkedHashMap<>();
            String currentL1 = null;
            checkedPrivilegeMap.put("l2", new ArrayList<>());
            for (String item : selectedItems) {
                String[] parts = item.split("-");
                String type = parts[0];
                String id = parts[1];
                if (type.equals("l2")) {
                    // Add L2 to the latest L1 found
                    checkedPrivilegeMap.get("l2").add(id);
                }
            }

            List<RoleTabSettings> existingRecords = roleTabSettingsRepository.findByRoleAndActiveFlag(principalDashBoardDTO.getRole(),ModelConstants.STATUS_ACTIVE);
            // Convert existing records into a Map with tabId as key
            Map<Long, RoleTabSettings> existingMap = existingRecords.stream()
                    .collect(Collectors.toMap(RoleTabSettings::getTabId, roleTabSettings -> roleTabSettings));

            // Convert existing tabIds to a Set for quick lookup
            Set<Long> existingTabIds = existingRecords.stream()
                    .map(RoleTabSettings::getTabId)
                    .collect(Collectors.toSet());

            // Convert received tabIds to a Set
            Set<Long> receivedTabIds = checkedPrivilegeMap.get("l2").stream()
                    .map(Long::valueOf) // Convert Integer to Long
                    .collect(Collectors.toSet());

            // List to hold new records
            List<RoleTabSettings> newRecords = receivedTabIds.stream()
                    .filter(tabId -> !existingTabIds.contains(tabId)) // If tabId is new, create a new record
                    .map(tabId -> {
                        RoleTabSettings newSetting = roleTabSettingsRepository.findByRoleAndTabId(principalDashBoardDTO.getRole(),tabId);
                        if(newSetting!=null) {
                            newSetting.setActiveFlag(ModelConstants.STATUS_ACTIVE);
                        }else {
                        	newSetting = new RoleTabSettings();
                        	newSetting.setRole(principalDashBoardDTO.getRole());
                            newSetting.setTabId(tabId);
                            newSetting.onCreate();
                        }
                        return newSetting;
                    })
                    .collect(Collectors.toList());
            existingRecords.addAll(newRecords);
            // Update showHide for existing records
            for (RoleTabSettings record : existingRecords) {
                // If tabId is in the received list, set showHide = true, else set showHide = false
                record.setShowHide(receivedTabIds.contains(record.getTabId()));
                record.setActiveFlag(receivedTabIds.contains(record.getTabId()) ? ModelConstants.STATUS_ACTIVE:
                        ModelConstants.STATUS_INACTIVE);
            }

            // Save updated existing records
            roleTabSettingsRepository.saveAllAndFlush(existingRecords);

            // Save only new records to avoid duplicate key error
            //roleTabSettingsRepository.saveAllAndFlush(newRecords);

            status = "saved";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return status;
    }

    public PrincipalDashBoardDTO getAdminTabPrivileges(String selectedRole)  {
        // Fetch the tab masters based on the role
    	List<Object[]> tabMasters = new ArrayList<Object[]>();
    	if(Objects.requireNonNull(SecurityCtxUtil.userRole()).equals("SoftwareAdmin")) {
    		tabMasters = roleTabSettingsRepository.findTabMastersByRole(selectedRole);
    	}else {
    		tabMasters = userTabSettingsRepository.findTabMastersByRoleAndUserId(Objects.requireNonNull(SecurityCtxUtil.userRole()),selectedRole);
    	}
        Map<Long, TabDTO> tabMap = new HashMap<>();
        List<TabDTO> tabList = new ArrayList<>();

        // Iterate through query results and populate the map of tabDTOs
        for (Object[] result : tabMasters) {
            Long l1Id = ((Number) result[0]).longValue();
            String l1Name = (String) result[1];
            Long l2Id = ((Number) result[2]).longValue();
            String l2Name = (String) result[3];
            String property = (String) result[4];
            String action = (String) result[5];
            Boolean show = (Boolean) result[6];

            TabDTO tabDTO = new TabDTO(l1Id, l2Id, l1Name, l2Name, property, action, show, true, false);

            // Check if the L1 Tab is already in the map
            TabDTO existingTab = tabMap.get(l1Id);
            if (existingTab == null) {
                existingTab = new TabDTO();
                existingTab.setL1Id(l1Id);
                existingTab.setL1Name(l1Name);
                existingTab.setSubList(new ArrayList<>());
                tabList.add(existingTab);
                tabMap.put(l1Id, existingTab);
            }

            // If it's a sub-item (L2 Tab), add it to the subList of the parent L1 Tab
            existingTab.getSubList().add(tabDTO);
        }

        // Create PrincipalDashBoardDTO and set the tabPrevilegeList
        PrincipalDashBoardDTO principalDashBoardDTO = new PrincipalDashBoardDTO();
        principalDashBoardDTO.setTabPrevilegeList(tabList);

        principalDashBoardDTO.setRole(selectedRole);

        return principalDashBoardDTO;
    }

    public List<String> getRoles() {
        ArrayList<RoleDto> roleList = roleService.getRoleList();
        return roleList.stream().map(RoleDto::getRoleName).collect(Collectors.toList());
    }

	public String saveUserTabPrivileges(PrincipalDashBoardDTO principalDashBoardDTO) {
		String methodName = "saveAdminTabPrivileges";
        String status = null;
        try {
            List<String> selectedItems = principalDashBoardDTO.getCheckedTabList();
            Map<String, List<String>> checkedPrivilegeMap = new LinkedHashMap<>();
            checkedPrivilegeMap.put("l2", new ArrayList<>());
            for (String item : selectedItems) {
                String[] parts = item.split("-");
                String type = parts[0];
                String id = parts[1];
                if (type.equals("l2")) {
                    // Add L2 to the latest L1 found
                    checkedPrivilegeMap.get("l2").add(id);
                }
            }
            List<UserTabSettings> existingRecords = userTabSettingsRepository.findByUserIdAndActiveFlag(Objects.requireNonNull(SecurityCtxUtil.userName()),ModelConstants.STATUS_ACTIVE);;
            // Convert existing records into a Map with tabId as key
            Map<Long, UserTabSettings> existingMap = existingRecords.stream()
                    .collect(Collectors.toMap(UserTabSettings::getTabId, roleTabSettings -> roleTabSettings));

            // Convert existing tabIds to a Set for quick lookup
            Set<Long> existingTabIds = existingRecords.stream()
                    .map(UserTabSettings::getTabId)
                    .collect(Collectors.toSet());

            // Convert received tabIds to a Set
            Set<Long> receivedTabIds = checkedPrivilegeMap.get("l2").stream()
                    .map(Long::valueOf) // Convert Integer to Long
                    .collect(Collectors.toSet());

            // List to hold new records
            List<UserTabSettings> newRecords = receivedTabIds.stream()
                    .filter(tabId -> !existingTabIds.contains(tabId)) // If tabId is new, create a new record
                    .map(tabId -> {
                    	UserTabSettings newSetting = userTabSettingsRepository.findByUserIdAndTabId(Objects.requireNonNull(SecurityCtxUtil.userName()),tabId);
                        if(newSetting!=null) {
                            newSetting.setActiveFlag(ModelConstants.STATUS_ACTIVE);
                        }else {
                        	newSetting = new UserTabSettings();
                        	newSetting.setUserId(Objects.requireNonNull(SecurityCtxUtil.userName()));
                            newSetting.setTabId(tabId);
                            newSetting.onCreate();
                        }
                        return newSetting;
                    })
                    .collect(Collectors.toList());
            existingRecords.addAll(newRecords);
            // Update showHide for existing records
            for (UserTabSettings record : existingRecords) {
                // If tabId is in the received list, set showHide = true, else set showHide = false
                record.setShowHide(receivedTabIds.contains(record.getTabId()));
                record.setActiveFlag(receivedTabIds.contains(record.getTabId()) ? ModelConstants.STATUS_ACTIVE:
                        ModelConstants.STATUS_INACTIVE);
            }

            // Save updated existing records
            userTabSettingsRepository.saveAllAndFlush(existingRecords);

            status = "saved";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return status;
	}

}
