package com.iitm.hosteldine.repository.OtherCandidate;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iitm.hosteldine.model.OtherCandidate.PaymentGatewayConfigCcavEntity;

public interface PaymentGatewayConfigCcavRepository extends JpaRepository<PaymentGatewayConfigCcavEntity, Long> {
   
	Optional<PaymentGatewayConfigCcavEntity> findByUrl(String url);

}