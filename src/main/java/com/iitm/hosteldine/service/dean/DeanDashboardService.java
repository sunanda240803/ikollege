package com.iitm.hosteldine.service.dean;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.dean.DeanApprovalDto;
import com.iitm.hosteldine.dto.dean.PropertyDto;
import com.iitm.hosteldine.repository.dean.DynamicUserTabRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeanDashboardService {

    private final DynamicUserTabRepository dynamicUserTabRepository;
    private final SimsConfigDataService simsConfigDataService;

    public DeanApprovalDto getDeanMenuList() {
        return null;
    }

    public DeanApprovalDto getDeanTabs() {
        DeanApprovalDto deanApprovalDto = new DeanApprovalDto();
        List<PropertyDto> properList = new ArrayList<>();
        List<PropertyDto> actionList = new ArrayList<>();
        List<Object[]> menuList = dynamicUserTabRepository.getDeanParentMenuList1(SecurityCtxUtil.userRole(), SecurityCtxUtil.userName());
        menuList.addAll(dynamicUserTabRepository.getDeanParentMenuList1(SecurityCtxUtil.userSecondaryRole(), SecurityCtxUtil.userName()));
        for (Object[] menu : menuList) {
            PropertyDto propertyDtoList = new PropertyDto();
            propertyDtoList.setDisplayName(menu[0].toString());
            propertyDtoList.setId(Integer.parseInt(menu[1].toString()));
            propertyDtoList.setUrl(menu[3] != null ? menu[3].toString() : "");
            propertyDtoList.setOrder(menu[4] != null ? Long.parseLong(menu[4].toString()) : 0L);
            properList.add(propertyDtoList);
        }
        deanApprovalDto.setPropertyList(properList.stream().sorted(Comparator.comparing(PropertyDto::getOrder)).toList());
        deanApprovalDto.setActionList(actionList);
        return deanApprovalDto;
    }

    public DeanApprovalDto getDeanMenuListById(String value) {
        return getDeanMenuListByRoleAndUserIdAndValue(null, null, value);
    }

    public DeanApprovalDto getDeanMenuListByRoleAndUserIdAndValue(String role, String userId, String value) {
        DeanApprovalDto deanApprovalDto = new DeanApprovalDto();
        PropertyDto properList = new PropertyDto();
        List<PropertyDto> actionList = new ArrayList<>();
        List<Object[]> subMenuList = null;
		if (role != null && userId != null) {
			subMenuList = dynamicUserTabRepository.getDeanSubMenuListByRoleAndUserIdAndUrl(SecurityCtxUtil.userRole(), SecurityCtxUtil.userName(), value);
            if (Objects.nonNull(SecurityCtxUtil.userSecondaryRole()) && !SecurityCtxUtil.userSecondaryRole().equals(SecurityCtxUtil.userRole())) {
                Set<String> stringSet = subMenuList.stream()
                        .map(obj -> obj[3].toString())
                        .collect(Collectors.toSet());
                subMenuList.addAll(
                        dynamicUserTabRepository.getDeanSubMenuListByRoleAndUserIdAndUrl(SecurityCtxUtil.userSecondaryRole(), SecurityCtxUtil.userName(), value)
                                .stream()
                                .filter(list -> !stringSet.contains(list[3].toString()))
                                .toList()
                );
            }
        } else {
			subMenuList = dynamicUserTabRepository.getDeanSubMenuListById(value, SecurityCtxUtil.userRole(), SecurityCtxUtil.userName());
            if (Objects.nonNull(SecurityCtxUtil.userSecondaryRole()) && !SecurityCtxUtil.userSecondaryRole().equals(SecurityCtxUtil.userRole())) {
                Set<String> stringSet = subMenuList.stream()
                        .map(obj -> obj[3].toString())
                        .collect(Collectors.toSet());
                subMenuList.addAll(
                        dynamicUserTabRepository.getDeanSubMenuListById(value, SecurityCtxUtil.userSecondaryRole(), SecurityCtxUtil.userName())
                                .stream()
                                .filter(list -> !stringSet.contains(list[3].toString()))
                                .toList()
                );
            }
		}
        List<PropertyDto> actionUrl = new ArrayList<>();
        for (Object[] subMenu : subMenuList) {
            PropertyDto actionDtoList = new PropertyDto();
            if (subMenu[2].toString().equals(Constants.COL_LINK) || subMenu[2].toString().equals(Constants.COL_ACTION)) {
                actionDtoList.setAction(true);
                actionDtoList.setDisplayName(subMenu[3].toString());
                actionDtoList.setActionIcon(subMenu[5].toString());
                actionDtoList.setActionStyle(subMenu[6].toString());
                actionDtoList.setPropertyValue(subMenu[8].toString());
                actionDtoList.setUrl(Objects.toString(subMenu[7], ModelConstants.EMPTY_STRING));
                actionUrl.add(actionDtoList);
            } else {
                actionDtoList.setDisplayName(subMenu[3].toString());
                actionDtoList.setProperty(subMenu[4].toString());
                actionList.add(actionDtoList);
            }
        }

        deanApprovalDto.setProperty(properList);
        deanApprovalDto.setActionList(actionList);
        deanApprovalDto.setActionUrlList(actionUrl);
        return deanApprovalDto;
    }

    public String[] getValidationStatusList(String validationStatus) {
        String status = simsConfigDataService.getSimConfigValue(validationStatus);
        return status.split(",");
    }

}
