package com.iitm.hosteldine.repository.hostel;

import com.iitm.hosteldine.model.hostel.TemporaryAccommodationConfigEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemporaryAccommodationConfigRepository extends JpaRepository<TemporaryAccommodationConfigEntity, Long> {

    Optional<TemporaryAccommodationConfigEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    @Query("""
            SELECT tcf
            FROM TemporaryAccommodationConfigEntity tcf
            WHERE tcf.activeFlag = :activeFlag
            AND (
                :searchString IS NULL OR :searchString = ''
                OR LOWER(tcf.categoryName) LIKE LOWER(CONCAT('%', :searchString, '%'))
                OR LOWER(tcf.description) LIKE LOWER(CONCAT('%', :searchString, '%'))
                OR LOWER(tcf.description) LIKE LOWER(CONCAT('%', :searchString, '%'))
            )
            """)
    Page<TemporaryAccommodationConfigEntity> getAllByFilter(String activeFlag, String searchString, Pageable pageable);

}
