package com.iitm.hosteldine.repository.hostel;

import com.iitm.hosteldine.model.hostel.ShowEventPurchaseClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowEventPurchaseClaimRepository extends JpaRepository<ShowEventPurchaseClaim, Long> {

}