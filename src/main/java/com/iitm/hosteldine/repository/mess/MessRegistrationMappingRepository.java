package com.iitm.hosteldine.repository.mess;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.mess.MessRegistrationMappingEntity;

import java.util.List;
import java.util.Optional;

public interface MessRegistrationMappingRepository extends JpaRepository<MessRegistrationMappingEntity, Long> {
    Optional<MessRegistrationMappingEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<MessRegistrationMappingEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from MessRegistrationMappingEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);

	List<MessRegistrationMappingEntity> findAllByActiveFlag(String statusActive);
}