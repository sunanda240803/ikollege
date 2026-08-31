package com.iitm.hosteldine.repository.mess;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.mess.MessSessionsMasterEntity;

import java.util.List;
import java.util.Optional;

public interface MessSessionsMasterRepository extends JpaRepository<MessSessionsMasterEntity, Long> {
   // Optional<MessSessionsMasterEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<MessSessionsMasterEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    @Query(value = """
                select count(x) from MessSessionsMasterEntity x where x.activeFlag = :activeFlag
            """)
    Long getActiveCount(String activeFlag);
}