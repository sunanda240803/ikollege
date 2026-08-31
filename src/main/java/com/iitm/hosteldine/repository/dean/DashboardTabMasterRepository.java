package com.iitm.hosteldine.repository.dean;

import org.springframework.data.jpa.repository.JpaRepository;
import com.iitm.hosteldine.model.dean.DashboardTabMasterEntity;

public interface DashboardTabMasterRepository extends JpaRepository<DashboardTabMasterEntity, Long> {
	
	DashboardTabMasterEntity findByTabTypeAndTabUrl(String tab, String tabUrl);
}