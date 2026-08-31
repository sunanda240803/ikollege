package com.iitm.hosteldine.repository;

import com.iitm.hosteldine.model.DashboardWidgetMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.iitm.hosteldine.model.DashboardWidgetPrivilegeEntity;
import java.util.List;

@Repository
public interface DashboardWidgetPrivilegeRepository extends JpaRepository<DashboardWidgetPrivilegeEntity, Long> {

	List<DashboardWidgetPrivilegeEntity> findAllByDashboardMenuMenuIdAndActiveFlag(long menuId, String statusActive);

	@Query(value = """
select d.widget from DashboardWidgetPrivilegeEntity d where d.dashboardMenu.menuId = :menuId and d.activeFlag = :statusActive and d.widget.activeFlag = :statusActive
""")
	List<DashboardWidgetMasterEntity> getWidgetListByMenuId(long menuId, String statusActive);
}