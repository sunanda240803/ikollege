package com.iitm.hosteldine.repository;

import com.iitm.hosteldine.entity.UserManagementEntity;
import com.iitm.hosteldine.entity.UserManagementId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserManagementRepository extends JpaRepository<UserManagementEntity, UserManagementId> {
    Optional<UserManagementEntity> findByIdUsernameIgnoreCaseAndActiveFlag(String userName, String activeFlag);

    boolean existsByIdUsernameAndActiveFlag(String username, String activeFlag);

    boolean existsByIdUserIdIgnoreCase(String userId);
    int countByIdUsernameIgnoreCaseContaining(String userName);

    @Modifying
    @Transactional
    @Query("UPDATE UserManagementEntity u SET u.activeFlag = 'N' WHERE (u.id.userId) IN (:userIds)")
    void updateUserActiveFlagByIds(List<String> userIds);
    @Query("select e from UserManagementEntity e join StaffDetailsEntity s on lower(e.id.userId) = lower(s.facultyId) where e.activeFlag=:statusActive and s.activeFlag=:statusActive order by e.id.username")
	List<UserManagementEntity> findAllByActiveFlagOrderByModifiedAtDesc(String statusActive);

    @Query("select e from UserManagementEntity e join StaffDetailsEntity s on (e.id.userId=s.facultyId) where upper(e.id.username)=upper(:userName) and e.activeFlag=:statusActive and s.activeFlag=:statusActive")
    List<UserManagementEntity> findByActiveFlagAndUserName(String statusActive,String userName);
    
    @Query(value="select  count(*)  from "
    		+ "schooldev.\"USER_MANAGEMENT\" ume1_0  join schooldev.\"FACULTY_PERSONAL_DETAILS\" sde1_0      on (ume1_0.user_id=sde1_0.faculty_id)  where ume1_0.user_name=:userName  and ume1_0.active_flag=:statusActive and sde1_0.active_flag=:statusActive",nativeQuery = true)
    Long countByActiveFlag(String statusActive,String userName);
    
    @Query("select count(e) from UserManagementEntity e join StaffDetailsEntity s on (e.id.userId=s.facultyId) where upper(e.id.username)=upper(:userName) and e.activeFlag=:statusActive and s.activeFlag=:statusActive")
    Long countByActiveFlag1(String statusActive,String userName);

    Optional<UserManagementEntity> findByIdUserIdIgnoreCase(String userId);
    

    Optional<UserManagementEntity> findByIdUserIdAndPasswordAndActiveFlagAndAuthenticationServerIgnoreCase(String userId, String password, String activeStatus, String authenticationServer);

    Optional<UserManagementEntity> findByIdUsername(String userName);
    Optional<UserManagementEntity> findByAccountTypeAndIdUserId(String accountType, String userId);

	boolean existsByActiveFlagAndIdUserId(String statusActive, String facultyId);
    
    @Query(value = "select (first_name||' '||last_name) as username,* from schooldev." +
            "\"USER_MANAGEMENT\" a join schooldev." +
            "\"FACULTY_PERSONAL_DETAILS\" b on(a.user_id=b.faculty_id) WHERE a.user_name=:userName", nativeQuery = true)
    List<Object[]> getUserEmailAndUserFullNameByUserName(String userName);

    Optional<UserManagementEntity> findByIdUserIdAndActiveFlag(String useId, String modelConstants);

    @Modifying
    @Query(value = """
	update schooldev."USER_MANAGEMENT" set active_flag = :activeFlag where user_id in (SELECT regexp_split_to_table(:userId, ','));
	""",nativeQuery = true)
    int updateUserManagementEntity(String userId, String activeFlag);


    @Modifying
    @Query("""
update UserManagementEntity u set u.id.username = :userName, u.id.userId = :userName where u.id = :id
""")
    void updateUserName(UserManagementId id, String userName);
}
