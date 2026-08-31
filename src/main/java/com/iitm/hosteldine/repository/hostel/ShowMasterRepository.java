package com.iitm.hosteldine.repository.hostel;

import com.iitm.hosteldine.model.hostel.ShowEventMasterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.iitm.hosteldine.model.hostel.ShowMasterEntity;

import java.util.List;
import java.util.Optional;

public interface ShowMasterRepository extends JpaRepository<ShowMasterEntity, Long> {
    Optional<ShowMasterEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<ShowMasterEntity> findByShowEventMasterAndActiveFlag(ShowEventMasterEntity showEventMaster, String activeFlag);

    List<ShowMasterEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from ShowMasterEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

    @Query(value = """
	    		select e from ShowMasterEntity e join ShowEventMasterEntity sem on (e.showEventMaster.id=sem.id AND sem.activeFlag=:statusActive AND sem.currentlyActive=:statusActive) 
	    		where e.showEventMaster.id= :eventId AND e.activeFlag=:statusActive 
    		""")
    Page<ShowMasterEntity> getShowWiseList(String statusActive, Pageable pageable, long eventId );
    
    @Query(value = """
    		select e from ShowMasterEntity e join ShowEventMasterEntity sem on (e.showEventMaster.id=sem.id AND sem.activeFlag=:statusActive AND sem.currentlyActive=:statusActive) 
    		where (sem.eventName ilike concat('%',:search,'%') OR e.showName ilike concat('%',:search,'%') OR e.showDescr ilike concat('%',:search,'%')) And e.showEventMaster.id= :eventId AND e.activeFlag=:statusActive 
		""")
    Page<ShowMasterEntity> getShowWiseListSearch(String statusActive, Pageable pageable, String search , long eventId );

    List<ShowMasterEntity> findByActiveFlagAndShowEventMasterIdAndShowNameIgnoreCase(String statusActive, long id,
			String showName);

	List<ShowMasterEntity> findByActiveFlagAndShowEventMasterIdAndShowNameIgnoreCaseAndIdNot(String statusActive,
			long eventId, String showName, long showId);

	List<ShowMasterEntity> findAllByActiveFlagAndShowEventMasterIdOrderByShowDescrAscShowNameAsc(String statusActive, long eventId);

    @Query(value = """
        select sm from ShowMasterEntity sm where sm.id in (select ssd.show.id from ShowSeatDetailsEntity ssd where ssd.id=:seatId and ssd.activeFlag=:statusActive)
                and sm.activeFlag=:statusActive
        """)
    Optional<ShowMasterEntity> getBySeatId(Long seatId,String statusActive);
    
}

