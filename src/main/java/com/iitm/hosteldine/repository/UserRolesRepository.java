package com.iitm.hosteldine.repository;

import com.iitm.hosteldine.entity.UserRolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRolesRepository extends JpaRepository<UserRolesEntity, Long> {

	@Query(value = """
			      select 'a'
			      from UserRolesEntity ure
			      join UserManagementEntity ume on (ure.role.roleId = ume.role.roleId and ure.activeFlag = ume.activeFlag)
			      where ure.menu.menuId = :menuId and ume.id.userId = :userId and ume.activeFlag = :active
			""")
	Optional<Object[]> checkMenuByUser(String userId, long menuId, String active);

}