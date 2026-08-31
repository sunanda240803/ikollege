package com.iitm.hosteldine.repository.mess;

import com.iitm.hosteldine.controller.mess.MessAllocationId;
import com.iitm.hosteldine.dto.mess.MessVendorAllocationDto;
import com.iitm.hosteldine.entity.mess.MessVendorAllocationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessAllocationEntityRepository extends JpaRepository<MessVendorAllocationEntity, MessAllocationId> {
  @Query("""
          SELECT new com.iitm.hosteldine.dto.mess.MessVendorAllocationDto(allo.id.messId, ven.vendorCode,
          allo.rate, allo.fromDate, allo.toDate, allo.fromDate, allo.activeFlag, allo.gst, 
          mess.messFloorName, ven.vendorName, mess.messName, mess.description) 
          from MessVendorAllocationEntity as allo
          JOIN MessMasterEntity AS mess ON (mess.id = allo.id.messId) AND mess.activeFlag = 'Y'
          INNER JOIN MessVendorMasterEntity AS ven ON (ven.vendorCode = allo.id.vendorCode) AND ven.activeFlag = 'Y'
          WHERE allo.activeFlag = :isActive
          """)
  Page<MessVendorAllocationDto> getVendorAllocationsByActiveFlag(@Param("isActive") String isActive, Pageable pageable);

  @Query("""
          SELECT new com.iitm.hosteldine.dto.mess.MessVendorAllocationDto(allo.id.messId, ven.vendorCode,
          allo.rate, allo.fromDate, allo.toDate, allo.fromDate, allo.activeFlag, allo.gst, 
          mess.messFloorName, ven.vendorName,mess.messName, mess.description)
          FROM MessVendorAllocationEntity as allo
          JOIN MessMasterEntity AS mess ON (mess.id = allo.id.messId) AND mess.activeFlag = 'Y'
          INNER JOIN MessVendorMasterEntity AS ven ON (ven.vendorCode = allo.id.vendorCode) AND ven.activeFlag = 'Y'
          WHERE allo.activeFlag = :isActive
          AND ven.vendorName ILIKE CONCAT('%', :search, '%') OR mess.messFloorName ILIKE CONCAT('%', :search, '%') 
                    OR mess.messName ILIKE CONCAT('%', :search, '%') OR mess.description ILIKE CONCAT('%', :search, '%')
          """)
  Page<MessVendorAllocationDto> getVendorAllocationsByActiveAndSearch(String isActive, Pageable pageable, String search);

  @Query("""
          SELECT allocation FROM MessVendorAllocationEntity AS allocation
          WHERE allocation.id.messId = :messId AND allocation.id.vendorCode = :vendorCode AND
          allocation.activeFlag = :isActive
          """)
  Optional<MessVendorAllocationEntity> getByMessIdAndVendorCode(Long messId, String vendorCode, String isActive);

    @Query(value = """
    SELECT vendor_code 
    FROM schooldev."MESS_ALLOCATION" 
    WHERE mess_master_id = :messMasterId 
      AND mess_effective_date = (
           SELECT MAX(mess_effective_date) 
           FROM schooldev."MESS_ALLOCATION" 
           WHERE mess_master_id = :messMasterId AND active_flag = :activeFlag
      ) 
      AND active_flag = :activeFlag
    """, nativeQuery = true)
    String findRateAndVendorCodeByMessMasterId(Long messMasterId, String activeFlag);

}