package com.iitm.hosteldine.repository.studentDashboard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.studentDashboard.GuestAccommodationGuestDetailsEntity;

import java.util.List;
import java.util.Optional;

public interface GuestAccommodationGuestDetailsRepository extends JpaRepository<GuestAccommodationGuestDetailsEntity, Long> {
    Optional<GuestAccommodationGuestDetailsEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<GuestAccommodationGuestDetailsEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from GuestAccommodationGuestDetailsEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

    List<GuestAccommodationGuestDetailsEntity> findAllByRequestId(Long requestId);
}