package com.iitm.hosteldine.repository.warden;

import com.iitm.hosteldine.model.hostel.HostelMasterEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iitm.hosteldine.model.warden.WardenInfoEntity;

import java.util.List;
import java.util.Optional;

public interface WardenInfoRepository extends JpaRepository<WardenInfoEntity, Long> {
    Optional<WardenInfoEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<WardenInfoEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from WardenInfoEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

    @Query("""
    	SELECT w FROM WardenInfoEntity w WHERE w.id IN :wardenIds  AND w.activeFlag = :statusActive
    		""")
	List<WardenInfoEntity> getWardenDetails(List<Long> wardenIds,String statusActive);

    @Query("""
        SELECT h FROM HostelMasterEntity h
        JOIN WardenHostelMappingEntity whm ON h.id = whm.id.hostelId AND whm.activeFlag = :statusActive
        JOIN WardenInfoEntity w ON w.id = whm.id.wardenId AND w.activeFlag = :statusActive
        WHERE LOWER(w.ldapUsername) = LOWER(:userId) OR LOWER(w.associateLdapUsername) = LOWER(:userId)
    """)
	List<HostelMasterEntity> getWardenDetails(String userId, String statusActive);

    List<WardenInfoEntity> findByWardenEmailAndActiveFlag(String wardenEmail, String activeFlag);

    List<WardenInfoEntity> findByLdapUsernameAndActiveFlag(String ldapUsername, String activeFlag);
    Optional<WardenInfoEntity> findFirstByLdapUsernameAndActiveFlag(String ldapUsername, String activeFlag);

    @Query(value = """
            select wid.id, wi.warden_name, wi.warden_email ,wi.office_no ,
			ii.id, ii.warden_name, ii.warden_email, 
			ii.phone_number, ii.office_no,whm.hostel_id ,hm.hostel_name,wid.away_from, wid.away_to,wid.away_description
			from schooldev."WARDEN_INFO" wi 
			join schooldev."WARDEN_INCHARGE_DETAILS" wid on (wi.id = wid.warden_id and wid.active_flag  = 'Y')
			join schooldev."WARDEN_INFO" ii on (ii.id = wid.incharger_id)
			join schooldev."WARDEN_HOSTEL_MAPPING" whm on (whm.warden_id  = wi.id and whm.active_flag  = 'Y')
			join schooldev."HOSTEL_MASTER" hm on (hm.hostel_id = whm.hostel_id and hm.active_flag ='Y')
			where wi.active_flag = 'Y' and wi.ldap_username = :userName order by wid.modified_at desc
    		""", nativeQuery = true)
    Page<Object[]> getWardenInOutDetails(@Param("userName")String userName, Pageable pageable);

    @Query(value = """
            select wi.id, wi.warden_name, wi.warden_email , wi.phone_number ,wi.office_no ,
			whm.hostel_id ,hm.hostel_name
			from schooldev."WARDEN_INFO" wi 
			join schooldev."WARDEN_HOSTEL_MAPPING" whm on (whm.warden_id  = wi.id and whm.active_flag  = :statusActive)
			join schooldev."HOSTEL_MASTER" hm on (hm.hostel_id = whm.hostel_id and hm.active_flag = :statusActive)
			where wi.active_flag = :statusActive and wi.ldap_username = :userName
    		""", nativeQuery = true)
	List<Object[]> getWardenDetailsByLdap(@Param("userName")String userName, String statusActive);

	@Query(value = """
            select wi.id,wi.warden_name,wi.warden_email,wi.phone_number,wi.office_no
			from schooldev."WARDEN_INFO" wi 
			join schooldev."WARDEN_HOSTEL_MAPPING" whm on (whm.warden_id  = wi.id and whm.active_flag  = :statusActive)
			where wi.active_flag = :statusActive and whm.hostel_id = :hosetlId
    		""", nativeQuery = true)
	List<Object[]> getInchargeDetailsByHostelId(String statusActive,long hosetlId);

	@Query(value = """
            select wid.id, wi.warden_name, wi.warden_email ,wi.office_no ,
			wi.id, wi.warden_name, wi.warden_email, 
			wi.phone_number, wi.office_no,whm.hostel_id ,hm.hostel_name,wid.away_from, wid.away_to,wid.away_description,wid.incharger_id
			from schooldev."WARDEN_INFO" wi 
			join schooldev."WARDEN_INCHARGE_DETAILS" wid on (wi.id = wid.incharger_id and wid.active_flag  = :statusActive)
			join schooldev."WARDEN_HOSTEL_MAPPING" whm on (whm.warden_id  = wi.id and whm.active_flag  = :statusActive)
			join schooldev."HOSTEL_MASTER" hm on (hm.hostel_id = whm.hostel_id and hm.active_flag = :statusActive)
			where wi.active_flag = :statusActive and wid.id = :inchargeId
    		""", nativeQuery = true)
	List<Object[]> getWardenDetailsByInchargeId(String statusActive, long inchargeId);
	
	Page<WardenInfoEntity> findAllByActiveFlagOrderByLdapUsername(String activeFlag, Pageable pageable);

	@Query("""
       SELECT w FROM WardenInfoEntity w WHERE w.activeFlag = :activeFlag
         AND (
              LOWER(w.wardenName) LIKE LOWER(CONCAT('%', :search, '%')) OR
              LOWER(w.officeNo) LIKE LOWER(CONCAT('%', :search, '%')) OR
              LOWER(w.wardenEmail) LIKE LOWER(CONCAT('%', :search, '%')) OR
              LOWER(w.alternateEmail) LIKE LOWER(CONCAT('%', :search, '%')) OR
              LOWER(w.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR
              LOWER(w.ldapUsername) LIKE LOWER(CONCAT('%', :search, '%'))
         )
       ORDER BY w.ldapUsername
       """)
	Page<WardenInfoEntity> searchByFields(@Param("activeFlag") String activeFlag, @Param("search") String search, Pageable pageable);


}