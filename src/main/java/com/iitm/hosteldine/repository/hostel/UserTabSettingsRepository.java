package com.iitm.hosteldine.repository.hostel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iitm.hosteldine.model.hostel.UserTabSettings;

@Repository
public interface UserTabSettingsRepository extends JpaRepository<UserTabSettings, Long> {
	
    @Query(value = "SELECT t.l1_id, t.l1_name, t.l2_id, t.l2_name, l1_property, l2_property , " +
    		" case when uts.show_hide is null then false else uts.show_hide end as show_hide"+
            " FROM schooldev.\"tab_master\" t " +
    		" LEFT JOIN schooldev.\"IIT_WD_ROLE_TAB_SETTINGS\" rts on ( t.l2_id = rts.tab_id and rts.role = :role and rts.active_flag = 'Y' )"+
            " LEFT JOIN schooldev.\"IIT_WD_USER_TAB_SETTINGS\" uts on ( rts.tab_id = uts.tab_id and uts.user_id = :userId and uts.active_flag = 'Y' )" +
            " where rts.show_hide = true ORDER BY t.l1_Order, t.l2_Order", nativeQuery = true)
	List<Object[]> findTabMastersByRoleAndUserId(String role, String userId);
 
	List<UserTabSettings> findByUserIdAndActiveFlag(String userId, java.lang.String statusActive);
	
	UserTabSettings findByUserIdAndTabId(String role, Long tabId);
}

