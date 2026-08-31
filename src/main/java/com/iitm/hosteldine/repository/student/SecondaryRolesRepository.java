package com.iitm.hosteldine.repository.student;

import com.iitm.hosteldine.entity.student.SecondaryRolesEntity;
import com.iitm.hosteldine.repository.CaptchaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SecondaryRolesRepository extends CrudRepository<SecondaryRolesEntity,Long> {

    boolean existsByUserIdAndActiveFlag(String userId,String activeFlag);

    Optional<SecondaryRolesEntity> findByUserIdAndRoleId(String userId, Long roleId);
    List<SecondaryRolesEntity> findAllByUserIdAndActiveFlag(String userId, String activeFlag);

    @Modifying
    @Query("""
update SecondaryRolesEntity x set x.activeFlag = :activeFlag where x.userId = :userId
""")
    void updateActiveFlagByUser(String activeFlag, String userId);
}
