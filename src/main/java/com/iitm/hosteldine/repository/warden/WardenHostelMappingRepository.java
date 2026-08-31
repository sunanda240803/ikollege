package com.iitm.hosteldine.repository.warden;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.warden.WardenHostelMappingEntity;
import com.iitm.hosteldine.model.warden.WardenHostelMappingEntityId;

public interface WardenHostelMappingRepository extends JpaRepository<WardenHostelMappingEntity, WardenHostelMappingEntityId> {
	
	@Query(value = """
            select hostel_id from schooldev."WARDEN_HOSTEL_MAPPING" where active_Flag = :activeFlag and warden_id = :wardenId
        """, nativeQuery = true)
	Long getHostelId(String activeFlag, Long wardenId);

}
