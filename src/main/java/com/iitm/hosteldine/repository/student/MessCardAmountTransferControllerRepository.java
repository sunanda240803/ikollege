package com.iitm.hosteldine.repository.student;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.student.MessCardAmountTransferControllerEntity;

public interface MessCardAmountTransferControllerRepository extends JpaRepository<MessCardAmountTransferControllerEntity, Long> {

	 @Query("SELECT MAX(m.maxAmount) FROM MessCardAmountTransferControllerEntity m WHERE m.activeFlag = :statusActive")
	    Double getMaxAmount(String statusActive);

	 @Query("SELECT COUNT(m) > 0 " +
		       "FROM MessCardAmountTransferControllerEntity m " +
		       "WHERE m.activeFlag = :statusActive " +
		       "AND CURRENT_DATE BETWEEN m.openingDate AND m.closingDate")
		boolean isMessToCardTransferActive(String statusActive);
	 
	
	
   
}