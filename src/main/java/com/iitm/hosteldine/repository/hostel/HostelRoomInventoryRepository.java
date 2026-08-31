package com.iitm.hosteldine.repository.hostel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.iitm.hosteldine.model.hostel.HostelRoomInventoryEntity;

public interface HostelRoomInventoryRepository extends JpaRepository<HostelRoomInventoryEntity, Long> {
    Optional<HostelRoomInventoryEntity> findByInventoryIdAndActiveFlag(Long id, String activeFlag);

    List<HostelRoomInventoryEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from HostelRoomInventoryEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

	HostelRoomInventoryEntity findByItemIdAndActiveFlag(String id, String statusActive);
	
}