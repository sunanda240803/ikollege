package com.iitm.hosteldine.repository.hostel;

import com.iitm.hosteldine.model.hostel.ShowMasterEntity;
import com.iitm.hosteldine.model.hostel.ShowSeatDetailsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShowSeatDetailsRepository extends JpaRepository<ShowSeatDetailsEntity, Long> {
    Optional<ShowSeatDetailsEntity> findByIdAndActiveFlag(Long id, String activeFlag);

	List<ShowSeatDetailsEntity> findByShowAndActiveFlag(ShowMasterEntity show, String activeFlag);

    List<ShowSeatDetailsEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from ShowSeatDetailsEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

	@Query(value = """
				select e from ShowSeatDetailsEntity e join ShowMasterEntity sme on (e.show.id=sme.id AND sme.activeFlag=:statusActive )
				where e.show.id=:id AND e.activeFlag=:statusActive 
			""")
	Page<ShowSeatDetailsEntity> getSeatListByShowId(String statusActive, Pageable pageable, long id);

	@Query(value = """
			select e from ShowSeatDetailsEntity e join ShowMasterEntity sme on (e.show.id=sme.id AND sme.activeFlag=:statusActive )
			where e.show.id=:id AND (e.seatName ilike concat('%',:search,'%')) AND e.activeFlag=:statusActive 
		""")
	Page<ShowSeatDetailsEntity> getSeatListSearchByShowId(String statusActive, Pageable pageable, String search, long id);

//	List<ShowSeatDetailsEntity> findAllByActiveFlagAndShow_IdOrderBySeatName(String activeFlag, Long showId);

	@Query("""
    SELECT s FROM ShowSeatDetailsEntity s WHERE s.activeFlag = :activeFlag AND s.show.id = :showId
    ORDER BY CASE s.seatName
        WHEN 'XS' THEN 1
        WHEN 'S'  THEN 2
        WHEN 'M'  THEN 3
        WHEN 'L'  THEN 4
        WHEN 'XL' THEN 5
        WHEN 'XXL' THEN 6
        WHEN 'XXXL' THEN 7
        ELSE 8
      END
""")
	List<ShowSeatDetailsEntity> findAllByActiveFlagAndShowIdOrderBySize(
			@Param("activeFlag") String activeFlag,
			@Param("showId") Long showId
	);

	Optional<ShowSeatDetailsEntity> findByActiveFlagAndIdAndShow_IdAndIsAvailable(String activeFlag,Long seatId,Long showId,Boolean available);
	boolean existsByActiveFlagAndIdAndIsAvailable(String activeFlag,Long seatId,Boolean available);
}


