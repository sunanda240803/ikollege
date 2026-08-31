package com.iitm.hosteldine.dto;

import com.iitm.hosteldine.dto.collegeInfo.RolePrivilegeDto;
import com.iitm.hosteldine.form.collegeInfo.RoleMenuPrivilegeForm;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuListDto implements Cloneable {
    private Long menuId;
    private String menuHeading;
    private Integer menuOrder;
    private Integer mainMenuOrder;
    private Integer subMenuOrder;
    private Integer subSubMenuOrder;
    private String urlPath;
    private String imageName;
    private String iconName;
    private String bannerName;
    private String menuType;
    private String activeFlag;
    private boolean checked;

    private RolePrivilegeDto rolePrivilegeDto;

    private List<MenuListDto> subMenu;
    private List<MenuListDto> subTab;
    private List<MenuListDto> subReport;
    private List<MenuListDto> subLink;
    private ArrayList<DashboardWidgetMasterDto> widgetList = new ArrayList<>();

    @Override
    public String toString() {
        return "Menu(" + menuId + "|" + menuHeading +
                "|" + menuOrder + "-" + mainMenuOrder + "-" + subMenuOrder + "-" + subSubMenuOrder +
                "| Sub: " + (subMenu != null ? subMenu.size() : 0) +
                "| Wid: " + (widgetList != null ? widgetList.size() : 0) +
                "| Tab: " + (subTab != null ? subTab.size() : 0) +
                "| Rep: " + (subReport != null ? subReport.size() : 0) +
                "| Lnk: " + (subLink != null ? subLink.size() : 0) +
                ')';
    }

    @Override
    public MenuListDto clone() {
        try {
            return (MenuListDto) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}