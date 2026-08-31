package com.iitm.hosteldine.repository.mess;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.mess.MessTerminalEntity;

public interface MessTerminalRepository extends JpaRepository<MessTerminalEntity, Long> {
    Optional<MessTerminalEntity> findByIdAndActiveFlag(Long id, String activeFlag);

    List<MessTerminalEntity> findAllByActiveFlagOrderByModifiedAtDesc(String activeFlag);

    List<MessTerminalEntity> findAllByActiveFlagAndMessMasterActiveFlagOrderByModifiedAtDesc(String statusActive, String statusActive2);

	List<MessTerminalEntity> findAllByActiveFlagAndTerminalIpIgnoreCaseAndIdNot(String statusActive, String terminalIp, long id);

    Optional<MessTerminalEntity> findByActiveFlagAndTerminalIp(String statusActive, String terminalIp);

	@Query("""
from MessTerminalEntity mte where mte.activeFlag = :statusActive and mte.terminalMacId = :terminalIp and mte.terminalDescription like '%-POS-%'
""")
    Optional<MessTerminalEntity> getTerminalDetails(String statusActive, String terminalIp);

    boolean existsByActiveFlagAndMessMasterId(String statusActive, long messId);

	Page<MessTerminalEntity> findAllByActiveFlagAndMessMasterActiveFlag(String statusActive, String statusActive2,
			Pageable pageable);

	@Query("SELECT e FROM MessTerminalEntity e join MessMasterEntity mm on (e.messMaster.id=mm.id and mm.activeFlag = :statusActive) "
			+ "WHERE e.activeFlag = :statusActive AND ("
			+ "mm.messName ILIKE CONCAT('%', :search, '%') OR "
			+ "e.terminalIp ILIKE CONCAT('%', :search, '%') OR "
			+ "e.terminalDescription ILIKE CONCAT('%', :search, '%'))")
	Page<MessTerminalEntity> findByMessTerminalSearchList(String statusActive, Pageable pageable, String search);

}