package com.iitm.hosteldine.repository.hostel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.iitm.hosteldine.model.hostel.RoleTabSettings;

@Repository
public interface RoleTabSettingsRepository extends JpaRepository<RoleTabSettings, Long> {
    RoleTabSettings findByRoleAndTabId(String role, Long tabId);
    List<RoleTabSettings> findByRole(String role);
    @Query(value = "SELECT t.l1_id, t.l1_name, t.l2_id, t.l2_name, l1_property, l2_property ,rts.show_hide " +
            " FROM schooldev.\"tab_master\" t " +
            " LEFT JOIN schooldev.\"IIT_WD_ROLE_TAB_SETTINGS\" rts on ( t.l2_id = rts.tab_id and rts.role = :role and rts.active_flag = 'Y' )" +
            " ORDER BY t.l1_Order, t.l2_Order", nativeQuery = true)
    List<Object[]> findTabMastersByRole(@Param("role") String role);
	
    List<RoleTabSettings> findByRoleAndActiveFlag(String role, String statusActive);
    
}

