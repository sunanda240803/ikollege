package com.iitm.hosteldine.repository.mess;

import com.iitm.hosteldine.model.mess.MessCouponUserMappingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessCouponUserMappingRepository extends JpaRepository<MessCouponUserMappingEntity, Long> {

    @Query(value = """
                SELECT mcum 
                FROM MessCouponUserMappingEntity mcum 
                JOIN MessMasterEntity mme ON mme.id = mcum.messMaster.id
                WHERE mcum.activeFlag = :activeFlag 
                  AND mme.activeFlag = :activeFlag
                  AND (:search IS NULL OR :search = '' 
                       OR mcum.userName ILIKE %:search% 
                       OR mme.messName ILIKE %:search%)
            """)
    Page<MessCouponUserMappingEntity> getAllMessCouponUserMapping(@Param("activeFlag") String activeFlag,
                                                                  @Param("search") String search, Pageable pageable);

    Optional<MessCouponUserMappingEntity> findByUserNameAndMessMaster_Id(String userName, Long messId);

    Optional<MessCouponUserMappingEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    Optional<List<MessCouponUserMappingEntity>> findByUserNameIgnoreCaseAndActiveFlag(String userName, String activeFlag);
}