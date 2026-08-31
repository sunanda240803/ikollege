package com.iitm.hosteldine.repository;

import com.iitm.hosteldine.entity.CaptchaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface CaptchaRepository extends JpaRepository<CaptchaEntity, Long> {

	@Query("select e from #{#entityName} e where e.token =?1 ")
	CaptchaEntity findByToken(String token);
}
