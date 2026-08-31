package com.iitm.hosteldine.repository.dean;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.dean.DynamicUserTabEntity;

public interface DynamicUserTabRepository extends JpaRepository<DynamicUserTabEntity, Long> {
	
	@Query(value = "SELECT d from DynamicUserTabEntity d")
	List<DynamicUserTabEntity> getDeanParentMenuList();
	
	@Query(value = "SELECT distinct(d.l1_name), d.l1_id,d.l1_property,d.l1_url, d.l1_order from schooldev.\"DYNAMIC_USER_TABS_DISPLAYABLE_VIEW\" d "
			+ "	where d.role = :role and (d.user_id is null or d.user_id = :userId) order by d.l1_id asc", nativeQuery = true)
	List<Object[]> getDeanParentMenuList1(String role, String userId);

	@Query(value = "SELECT distinct(d.l1_name), d.l1_id, d.l1_order, d.l1_url from schooldev.\"DYNAMIC_USER_TABS_DISPLAYABLE_VIEW\" d "
			+ "	where d.role = :role order by d.l1_order asc", nativeQuery = true)
	List<Object[]> getTabList(String role);

	@Query(value = "SELECT d.l1_id, d.l1_property, d.l2_type, d.l2_name, d.l2_property,d.icon,d.style,d.url,d.action from schooldev.\"DYNAMIC_USER_TABS_DISPLAYABLE_VIEW\" d "
    		+ "	where d.role = :role and (d.user_id is null or d.user_id = :userId) and d.l1_url = :id and d.show_hide=true order by d.l1_id asc", nativeQuery = true)
	List<Object[]> getDeanSubMenuListById(String id, String role, String userId);
	
	@Query(value = "SELECT d.l1_id, d.l1_property, d.l2_type, d.l2_name, d.l2_property,d.icon,d.style,d.url,d.action from schooldev.\"DYNAMIC_USER_TABS_DISPLAYABLE_VIEW\" d "
    		+ "	where d.role = :role and (d.user_id is null or d.user_id = :userId) and d.l1_url = :url and d.show_hide=true order by d.l1_id asc", nativeQuery = true)
	List<Object[]> getDeanSubMenuListByRoleAndUserIdAndUrl(String role, String userId, String url);
	
	@Query(value = "SELECT d.l1_id, d.l1_property, d.l2_type, d.l2_name, d.l2_property,d.icon,d.style,d.url,d.action from schooldev.\"DYNAMIC_USER_TABS_DISPLAYABLE_VIEW\" d "
    		+ "	where d.role = :role and (d.user_id is null or d.user_id = :userId) and d.l1_url = :url and d.l2_type = :colType and d.show_hide=true order by d.l1_id asc", nativeQuery = true)
	List<Object[]> getDeanSubMenuListByRoleAndUserIdAndUrlAndColType(String role, String userId, String url,String colType);
}