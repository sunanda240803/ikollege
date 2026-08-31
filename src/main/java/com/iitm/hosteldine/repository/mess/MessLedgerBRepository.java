package com.iitm.hosteldine.repository.mess;

import com.iitm.hosteldine.entity.mess.MessLedgerBEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntityId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessLedgerBRepository extends JpaRepository<MessLedgerBEntity, MessLedgerBEntityId> {

    List<MessLedgerBEntity> findById_FinYearAndId_VoucherNoAndActiveFlag(String finYear, String voucherNo, String activeFlag);

    Optional<MessLedgerBEntity> findByIdVoucherNoAndActiveFlag(String voucherNo, String activeFlag);

    Optional<MessLedgerBEntity> findByIdVoucherNoAndIdFinYearAndActiveFlag(String voucherNo, String finYear, String activeFlag);

    Optional<MessLedgerBEntity> findByIdVoucherNoAndAccheadAndIdSlnoAndActiveFlag(String voucherNo, String acchead, int slNo, String activeFlag);

    @Query(value = "SELECT SUM(slno) FROM schooldev.\"MESS_LEDGER_B\" " +
            "WHERE voucher_no = :voucherNo " +
            "AND active_flag = :activeFlag",
            nativeQuery = true)
    Optional<Integer> findTotalSlNo(
            @Param("voucherNo") String voucherNo,
            @Param("activeFlag") String activeFlag);


    Optional<MessLedgerBEntity> findTop1ByDocRefNoOrderByVoucherDateDesc(String docRefNo);

    Optional<MessLedgerBEntity> findByIdSlnoAndIdVoucherNoAndIdFinYearAndActiveFlag(Integer slno, String voucherNo, String finYear, String activeFlag);

    Optional<MessLedgerBEntity> findFirstByActiveFlagAndAccheadInOrderByVoucherDate(String activeFlag, List<String> acchead);

    @Transactional
    @Modifying
    @Query(value = """
			    update MessLedgerBEntity mlb 
			    set mlb.cancelStatus = :cancelStatus, mlb.modifiedAt = :modifiedAt, mlb.modifiedBy = :modifiedBy
			    where mlb.activeFlag = :activeFlag and mlb.id.finYear = :finYear and mlb.id.voucherNo = :voucherNo
			""")
    int cancelMessLedgerB(String cancelStatus, String activeFlag, String finYear, String voucherNo, LocalDateTime modifiedAt,String modifiedBy);
}