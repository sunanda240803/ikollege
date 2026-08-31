package com.iitm.hosteldine.repository.collegeInfo;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.RoleEntity;
import com.iitm.hosteldine.model.MenuListEntity;
import com.iitm.hosteldine.model.collegeInfo.RoleMenuPrivilegeEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoleMenuPrivilegeRepository extends JpaRepository<RoleMenuPrivilegeEntity, Long> {

    @Transactional
    @Modifying
    @Query("UPDATE RoleMenuPrivilegeEntity SET activeFlag =:inActive " +
            "where role.roleId = :roleId  and activeFlag =:active ")
    Integer updateAllByRoleRoleId(long roleId,String active,String inActive);

    Optional<RoleMenuPrivilegeEntity> findFirstByMenuMenuIdAndRoleRoleId(long menuId, long roleId);

    @Query(value = """
            select ml
            from MenuListEntity  ml
            join RoleMenuPrivilegeEntity rp on rp.menu.menuId = ml.menuId
            join RoleEntity rm on rm.roleId = rp.role.roleId
            join UserManagementEntity um on rm.roleId = um.role.roleId
            where um.id.userId = :userId and ml.urlPath = :urlEntry and rp.activeFlag = '""" + ModelConstants.STATUS_ACTIVE + "'")
    Optional<List<MenuListEntity>> getMenuByHeading(String userId, String urlEntry);

    @Query(value = """
select rmp.role.roleName, rmp.menu  from RoleMenuPrivilegeEntity rmp
where rmp.activeFlag = 'Y' and rmp.role.activeFlag = 'Y' and rmp.menu.activeFlag = 'Y'
order by rmp.role.roleId, rmp.menu.menuOrder, rmp.menu.mainMenuOrder, rmp.menu.subMenuOrder, rmp.menu.subSubMenuOrder
""")
    List<Object[]> getAllRoleMenuMapping();

    @Query(value = """
select ml, rmp from MenuListEntity ml
left join RoleMenuPrivilegeEntity rmp on (ml.menuId = rmp.menu.menuId and rmp.role.roleId = :roleId and rmp.activeFlag = 'Y')
where ml.activeFlag = 'Y'
order by ml.menuOrder, ml.mainMenuOrder, ml.subMenuOrder, ml.subSubMenuOrder
""")
    List<Object[]> getAllMenuWithPrivilegeByRole(Long roleId);
}