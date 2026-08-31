package com.iitm.hosteldine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.iitm.hosteldine.model.DashboardWidgetMasterEntity;

@Repository
public interface DashboardWidgetMasterRepository extends JpaRepository<DashboardWidgetMasterEntity, Long> {
	
}