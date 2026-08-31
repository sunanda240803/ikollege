package com.iitm.hosteldine.repository.hostel;

import com.iitm.hosteldine.model.hostel.ShowEventMasterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowEventMasterRepository extends JpaRepository<ShowEventMasterEntity, Long> {
    Page<ShowEventMasterEntity> findAllByActiveFlag(String statusActive, Pageable pageable);

    @Query("select sem from ShowEventMasterEntity sem where sem.activeFlag = :statusActive and (sem.eventName ILIKE CONCAT('%', :search, '%')" +
            "or sem.eventDesc ILIKE CONCAT('%', :search, '%'))")
    Page<ShowEventMasterEntity> findByEventNameAndEventDescAndActive(String statusActive, Pageable pageable, String search);

	List<ShowEventMasterEntity> findAllByActiveFlagAndCurrentlyActiveOrderByCreatedAtDesc(String statusActive, String statusActive2);

    List<ShowEventMasterEntity> findAllByActiveFlagAndCurrentlyActiveAndEventStartingDateIsLessThanEqualAndEventEndingDateIsGreaterThanEqual(
                                                                                                                    String statusActive,
                                                                                                                    String currentlyActive,
                                                                                                                    LocalDate eventStartingDate,
                                                                                                                    LocalDate eventEndingDate);

    List<ShowEventMasterEntity> findAllByActiveFlag(String statusActive);

    Optional<ShowEventMasterEntity> findById(Long aLong);
}