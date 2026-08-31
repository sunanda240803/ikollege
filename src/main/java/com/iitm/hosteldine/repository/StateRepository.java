package com.iitm.hosteldine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.StateEntity;

import java.util.List;
import java.util.Optional;

public interface StateRepository extends JpaRepository<StateEntity, Integer> {
    Optional<StateEntity> findByStateIdAndActiveFlag(Long id, String activeFlag);

    List<StateEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from StateEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

	List<StateEntity> findAllByActiveFlagAndCountryIdOrderByStateName(String statusActive, Integer countryId);
}