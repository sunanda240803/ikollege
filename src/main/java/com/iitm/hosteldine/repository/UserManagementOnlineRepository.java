package com.iitm.hosteldine.repository;

import java.util.List;
import java.util.Optional;

import com.iitm.hosteldine.model.hostel.ShowEventMasterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iitm.hosteldine.entity.UserManagementId;
import com.iitm.hosteldine.entity.UserManagementOnlineEntity;

@Repository
public interface UserManagementOnlineRepository extends JpaRepository<UserManagementOnlineEntity, UserManagementId> {
	Optional<UserManagementOnlineEntity> findByUserEmailIdAndActiveFlag(String email, String activeFlag);

	Optional<UserManagementOnlineEntity> findByUserEmailIdAndPasswordAndActiveFlag(String emailId, String password,
			String activeFlag);
	boolean existsByEmailIgnoreCaseAndActiveFlag(String emailId, String activeFlag);

	@Query(value = """
		select ume from UserManagementOnlineEntity ume where ume.activeFlag = :activeFlag and ume.activeStatus = :activeStatus
				AND ((:search is NULL OR :search = '')
            			OR (ume.userEmailId ILIKE %:search% OR ume.firstName ILIKE %:search% or ume.lastName ILIKE %:search%)
        			) 
		        order by ume.noOfFailedAttempts desc
		""")
	Page<UserManagementOnlineEntity> getAllOrByUserIdOrFirstNameOrLastName(String activeStatus, String activeFlag,
																		   String search,Pageable pageable);

	Optional<UserManagementOnlineEntity> findByUserIdAndActiveFlag(Long userId, String statusActive);
}
