package com.iitm.hosteldine.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.iitm.hosteldine.model.MenuListEntity;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuListRepository extends JpaRepository<MenuListEntity, Long> {

    @Query(value = "SELECT DISTINCT ml FROM UserManagementEntity um" +
            " LEFT JOIN RoleEntity rm ON rm.roleId = um.role.roleId" +
            " LEFT JOIN RoleMenuPrivilegeEntity rmp ON rmp.role.roleId = rm.roleId" +
            " LEFT JOIN MenuListEntity ml ON rmp.menu.menuId = ml.menuId" +
            " WHERE um.id.userId = :userId AND ml.menuType = :menuType AND rmp.activeFlag = :activeFlag" +
            " ORDER BY ml.menuOrder, ml.mainMenuOrder, ml.subMenuOrder, ml.subSubMenuOrder")
    List<MenuListEntity> getDashboardList(String userId, String menuType, String activeFlag);

	@Query("""
			SELECT ml FROM MenuListEntity ml WHERE ml.menuOrder = :menuOrderId AND ml.menuType = 'subDashboard' 
			ORDER BY ml.menuOrder, ml.mainMenuOrder, ml.subMenuOrder, ml.subSubMenuOrder
			""")
	List<MenuListEntity> getSubDashboardById(Long menuOrderId);
	
    Optional<MenuListEntity> findByMenuOrderAndMainMenuOrder(int menuOrder, int mainMenuOrder);

    Optional<MenuListEntity> findByMenuTypeAndUrlPath(String menuType, String urlPath);

	@Query("""
			SELECT DISTINCT ml.menuType FROM MenuListEntity ml WHERE ml.menuType is not null
			""")
	List<String> getMenuTypes();

	Optional<MenuListEntity> findByMenuIdAndActiveFlag(long id, String isActive);

	@Query("""
			SELECT ml FROM MenuListEntity ml where ml.activeFlag = 'Y' ORDER BY ml.menuOrder, ml.mainMenuOrder, ml.subMenuOrder, ml.subSubMenuOrder
			""")
	List<MenuListEntity> getAllMenusInSortedOrder();

	@Query("SELECT ml FROM MenuListEntity ml WHERE ml.menuHeading ILIKE CONCAT('%', :search, '%') " +
			"OR ml.urlPath ILIKE CONCAT('%', :search, '%')")
	Page<MenuListEntity> getMenusBySearchKey(@Param("search") String search, Pageable pageable);

	List<MenuListEntity> findByMainMenuOrderOrderByMenuIdAscSubSubMenuOrderAsc(int menuOrder);

	@Query("""
        select ml from MenuListEntity  ml
        where ml.menuOrder = :menuOrder and ml.mainMenuOrder = :mainMenuOrder and ml.menuType in ('module', 'report', 'dashboard')
        order by ml.menuOrder, ml.mainMenuOrder, ml.subMenuOrder, ml.subSubMenuOrder
""")
	List<MenuListEntity> getMenuListByMenuOrder(int menuOrder, int mainMenuOrder);

	@Query("""
        select ml from MenuListEntity  ml
        where ml.menuOrder = :menuOrder and ml.mainMenuOrder = :mainMenuOrder and ml.subMenuOrder = :subMenuOrder
        and ml.menuType in ('module', 'report', 'dashboard')
        order by ml.menuOrder, ml.mainMenuOrder, ml.subMenuOrder, ml.subSubMenuOrder
""")
	List<MenuListEntity> getMenuListLvl2ByMenuOrder(int menuOrder, int mainMenuOrder, int subMenuOrder);

	Optional<MenuListEntity> findByMenuId(long id);
}