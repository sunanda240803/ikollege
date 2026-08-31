package com.iitm.hosteldine.repository.studentDashboard;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.studentDashboard.GuestFilesInformationEntity;

public interface GuestFilesInformationRepository extends JpaRepository<GuestFilesInformationEntity, Long> {
   

    @Query(value = """
                select count(x) from GuestFilesInformationEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

	List<GuestFilesInformationEntity> findAllByActiveFlagAndRequestId(String statusActive, Long requestId);
}