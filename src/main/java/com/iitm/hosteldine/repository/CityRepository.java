package com.iitm.hosteldine.repository;

import com.iitm.hosteldine.model.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<CityEntity, Integer> {
    Optional<CityEntity> findByCityIdAndActiveFlag(Long id, String activeFlag);

    List<CityEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from CityEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

	List<CityEntity> findAllByActiveFlagAndStateIdOrderByCityName(String statusActive, Integer stateId);
}