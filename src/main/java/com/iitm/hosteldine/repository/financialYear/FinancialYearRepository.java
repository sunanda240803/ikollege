package com.iitm.hosteldine.repository.financialYear;

import com.iitm.hosteldine.model.financialYear.FinancialYearEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinancialYearRepository extends JpaRepository<FinancialYearEntity, String> {
  Optional<FinancialYearEntity> findByActiveFlag(String activeFlag);
  
	@Query("SELECT f FROM FinancialYearEntity f "
			+ "WHERE f.currentFinyear = :currentFinyear AND f.activeFlag = :activeFlag "
			+ "ORDER BY f.modifiedAt DESC LIMIT 1")
	FinancialYearEntity getFinYearDetails(String currentFinyear, String activeFlag);
}