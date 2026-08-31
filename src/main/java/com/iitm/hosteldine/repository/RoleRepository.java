package com.iitm.hosteldine.repository;

import com.iitm.hosteldine.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    @Query(value = "select  e.roleId from RoleEntity  e where e.roleName=:roleName and e.activeFlag=:activeFlag")
    Long getRoleIdByName(String roleName, String activeFlag);

    Optional<List<RoleEntity>> findAllByActiveFlagOrderByRoleName(String statusActive);

    Optional<List<RoleEntity>> findAllByActiveFlag(String activeFlag);

    Optional<RoleEntity> findByRoleIdAndActiveFlag(long roleId, String activeFlag);

    Optional<RoleEntity> findByRoleName(String roleName);

    Optional<RoleEntity> findByRoleNameAndActiveFlag(String roleName, String activeFlag);
}
