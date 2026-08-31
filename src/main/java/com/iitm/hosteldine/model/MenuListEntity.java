package com.iitm.hosteldine.model;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.model.collegeInfo.RoleMenuPrivilegeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"MENU_LIST\"", schema = ModelConstants.SCHEMA)
public class MenuListEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menu_id", nullable = false)
	private long menuId;

	@Basic
	@Column(name = "heading", nullable = false, length = 75)
	private String menuHeading;

	@Column(name = "menu_order", nullable = false)
	private int menuOrder;

	@Column(name = "main_menu_order", nullable = false)
	private int mainMenuOrder;

	@Column(name = "sub_menu_order", nullable = false)
	private int subMenuOrder;

	@Column(name = "sub_sub_menu_order", nullable = false)
	private int subSubMenuOrder;

	@Column(name = "url_path", nullable = false, length = 250)
	private String urlPath;

	@Column(name = "image_name", length = -1)
	private String imageName;

	@Column(name = "icon_name", length = -1)
	private String iconName;

	@Column(name = "banner_name", length = -1)
	private String bannerName;

	@Column(name = "menu_type", nullable = false, length = 75)
	private String menuType;
	
    @Column(name = "active_flag", nullable = false, length = 1)
    private String activeFlag;

	@Override
	public String toString() {
		return "Menu(" + menuId + "|" + menuHeading +
				"|" + menuOrder + "-" + mainMenuOrder + "-" + subMenuOrder + "-" + subSubMenuOrder +
				')';
	}
}
