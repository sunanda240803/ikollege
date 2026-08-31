package com.iitm.hosteldine.repository.hostel;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.hostel.CatererLedgerMappingEntity;
import com.iitm.hosteldine.model.hostel.CatererLedgerMappingId;

public interface CatererLedgerMappingRepository extends JpaRepository<CatererLedgerMappingEntity, CatererLedgerMappingId> {
    
	 static final String BASE_CATERER_LIST_QUERY = "SELECT c.id.catererName, c.id.acchead, a.accname "
	            + "FROM CatererLedgerMappingEntity c "
	            + "JOIN AccountHeadEntity a "
	            + "ON (c.id.acchead = a.id.acchead) "
	            + "WHERE c.activeFlag = :activeFlag "
	            + "AND a.activeFlag = :activeFlag ";
	
    @Query(BASE_CATERER_LIST_QUERY)
    Page<Object[]> getCatererList(String activeFlag, Pageable pageable);
    
	@Query(BASE_CATERER_LIST_QUERY + "AND (c.id.catererName ILIKE CONCAT('%', :search, '%') OR a.accname ILIKE CONCAT('%', :search, '%'))")
	Page<Object[]> getCatererListBySearch(String activeFlag, Pageable pageable, String search);
    
	Optional<CatererLedgerMappingEntity> findByIdAccheadAndIdCatererName(String accHead, String catererName);
    
    Boolean existsByIdAccheadAndIdCatererNameAndFinYearAndActiveFlag(String accHead, String catererName, String finYear, String activeFlag);
}