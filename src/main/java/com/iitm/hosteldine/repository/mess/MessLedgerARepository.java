package com.iitm.hosteldine.repository.mess;

import com.iitm.hosteldine.entity.mess.MessLedgerAEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntityId;
import com.iitm.hosteldine.service.hostel.UploadReceiptsRecord;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessLedgerARepository extends JpaRepository<MessLedgerAEntity, MessLedgerAEntityId> {
    String UPLOAD_RECEIPTS = """
        from MessLedgerAEntity a
        join MessLedgerBEntity b on (
            a.id.finYear = b.id.finYear and a.id.bookType = b.id.bookType and
            a.id.voucherNo = b.id.voucherNo and a.voucherDate=b.voucherDate)
        left join StudentBioDataFormDetailEntity c on (
            c.studentId = b.acchead and c.activeFlag = :activeFlag)
        where a.activeFlag = :activeFlag and b.activeFlag = :activeFlag
          and a.cancelStatus = :statusInactive and b.cancelStatus = :statusInactive
          and a.id.bookType = :bookType and a.screenType like 'ur_%'
    """;

    @Query(value = """
        SELECT 'A' tbl, a.voucherDate dt, a.description, a.docRefNo, a.amount, a.debitOrCredit, a.id.voucherNo, a.createdAt as c_date, 0 as slno
        FROM MessLedgerAEntity a
        WHERE a.activeFlag = 'Y'
          AND a.cancelStatus = 'N'
          AND (a.recon = '' OR a.recon = 'N') 
          AND a.id.bookType = :bookType 
          AND a.acchead IN :accHeads
        UNION
        SELECT 'B' tbl, b.voucherDate dt, b.description, b.docRefNo, b.amount, b.debitOrCredit, b.id.voucherNo, b.createdAt as c_date, b.id.slno
        FROM MessLedgerBEntity b
        WHERE b.activeFlag = 'Y' 
          AND b.cancelStatus = 'N' 
          AND (b.recon = '' OR b.recon = 'N') 
          AND b.id.bookType = :bookType 
          AND b.acchead IN :accHeads
        """)
    List<Object[]> getLedgerReport(@Param("bookType") String bookType, @Param("accHeads") List<String> accHeads);



    @Query(value = """
    SELECT * FROM schooldev.food_court_list(:studentId, null, :messPeriodId, null,null,null)
    """, nativeQuery = true)
    Optional<List<Object[]>> getFilteredFoodCourtReport(String studentId, Long messPeriodId);
    
    @Query(value = "SELECT NEXTVAL('schooldev.\"MESS_LEDGER_seq\"')", nativeQuery = true)
	int getNextValMessLedger();

    @Query(value = "SELECT NEXTVAL('schooldev.\"MESS_BILLING_seq\"')", nativeQuery = true)
    int getNextValMessBillingSeq();


    @Query(value = "select count(voucher_no)+1 from schooldev.\"MESS_LEDGER_A\" where voucher_no like 'JV%' ", nativeQuery = true)
    int getNextVoucherNoMessLedger();

    @Query(value = """
            SELECT a.voucher_no FROM 
            schooldev."MESS_LEDGER_A" a where a.active_flag='Y' and fin_year=? and UPPER(a.acchead) LIKE UPPER(?) and
            a.match_date :: date is not null order by a.voucher_no desc limit 1 
            """ , nativeQuery = true)
    String getVoucherNoByStudentId(String finYear, String accHead);

//    @Query(value = """
//            select coalesce(max(to_number(voucher_no,'99999999')),0) from schooldev."MESS_LEDGER_A" where voucher_no not like 'JV%'
//            """ , nativeQuery = true)
//    String getVoucherNoMax();


    @Query(value = """
        SELECT 'A' tbl, b.id.voucherNo, b.voucherDate dt, b.amount, b.acchead, b.description, b.debitOrCredit, a.screenType, b.id.slno
        FROM MessLedgerBEntity b
        INNER JOIN MessLedgerAEntity a ON (a.id.finYear = b.id.finYear
            AND a.id.bookType = b.id.bookType
            AND a.id.voucherNo = b.id.voucherNo
            AND a.voucherDate = b.voucherDate
            AND a.recon = b.recon
            AND a.activeFlag = b.activeFlag)
        WHERE a.recon = 'Y'
        AND a.id.finYear = :finYear
        AND a.activeFlag = 'Y'
        
        UNION
        
        SELECT 'B' tbl, a.id.voucherNo, a.voucherDate dt, a.amount, a.subAccountHead, a.description, a.debitOrCredit, a.screenType, 0 as slno
        FROM MessLedgerAEntity a
        WHERE a.recon = 'Y'
        AND a.id.finYear = :finYear
        AND a.activeFlag = 'Y'
        AND a.id.voucherNo LIKE 'JV%'
        """)
    List<Object[]> getCheckerApprovalList(@Param("finYear") String finYear);

    List<MessLedgerAEntity> findById_FinYearAndId_VoucherNoAndActiveFlag(String finYear, String voucherNo, String activeFlag);

    @Query(value = """
        SELECT 'A' tbl, a.id.voucherNo, a.voucherDate dt, a.amount, a.acchead, a.description, a.debitOrCredit, a.fcNo, a.studentCount, 0 as slno
        FROM MessLedgerAEntity a
        WHERE a.activeFlag = 'Y'
          AND a.debitOrCredit = 'c'
          AND  a.id.voucherNo = :voucherNo
          AND a.id.finYear = :finYear
        UNION
        SELECT 'B' tbl, b.id.voucherNo, b.voucherDate dt, b.amount,  b.acchead, b.description,  b.debitOrCredit, b.fcNo,0 as studentCount, b.id.slno
        FROM MessLedgerBEntity b
        WHERE b.activeFlag = 'Y'
          AND b.debitOrCredit = 'c'
          AND b.id.voucherNo = :voucherNo
          AND b.id.finYear = :finYear
        """)
    List<Object[]> getLedgerCheckerValue(@Param("finYear") String finYear,@Param("voucherNo") String voucherNo);

    @Query(value = """
        SELECT 'A' tbl, b.id.voucherNo, b.voucherDate dt, b.amount, b.acchead, b.description, b.debitOrCredit,'' as screen_type, b.fcNo, 'B' as ledgertable, b.id.slno
        FROM MessLedgerBEntity b
        INNER JOIN MessLedgerAEntity a ON (a.id.finYear = b.id.finYear
            AND a.id.bookType = b.id.bookType
            AND a.id.voucherNo = b.id.voucherNo
            AND a.voucherDate = b.voucherDate
            AND a.recon = b.recon
            AND a.activeFlag = b.activeFlag)
        WHERE a.recon = 'Y'
        AND a.id.finYear = :finYear
        AND a.activeFlag = 'Y'
        
        UNION
        
        SELECT 'B' tbl, a.id.voucherNo, a.voucherDate dt, a.amount,
         CASE
            WHEN  a.screenType IS NOT NULL THEN (a.acchead)  ELSE (a.subAccountHead)
         END as subAccountHead,
        a.description, a.debitOrCredit, a.screenType , a.fcNo, 'A' as ledgertable, 0 as slno
        FROM MessLedgerAEntity a
        WHERE a.recon = 'Y'
        AND a.id.finYear = :finYear
        AND a.activeFlag = 'Y'
        AND (a.id.voucherNo LIKE 'JV%' OR a.screenType IS NOT NULL)
        """)
    List<Object[]> getAllCheckerApprovalList(@Param("finYear") String finYear);

    Page<MessLedgerAEntity> findAllByAccheadAndActiveFlagOrderByVoucherDateDesc(String accHead, String activeFlag, Pageable pageable);

    Optional<MessLedgerAEntity> findByIdVoucherNoAndActiveFlag(String voucherNo, String activeFlag);

    Optional<MessLedgerAEntity> findByIdVoucherNoAndIdFinYearAndActiveFlag(String voucherNo, String finYear, String activeFlag);

    Optional<MessLedgerAEntity> findByIdVoucherNoAndAccheadAndActiveFlag(String voucherNo,String acchead, String activeFlag);

    @Query(value = """
        SELECT SUM(  CASE WHEN aa.debit_or_credit = 'c' THEN aa.amount ELSE -aa.amount END ) AS amt
        FROM (
          SELECT 'A'::text AS tbl, a.acchead, a.voucher_date AS dt, a.description, a.doc_ref_no, a.amount, a.debit_or_credit, a.voucher_no, a.recon, 0 as slno
           FROM
               schooldev."MESS_LEDGER_A" a
           JOIN
               schooldev."MESS_LEDGER_B" bb
               ON bb.cancel_status = a.cancel_status AND bb.voucher_no = a.voucher_no AND bb.book_type = a.book_type AND a.fin_year = bb.fin_year
           WHERE
               a.active_flag = 'Y'::bpchar  AND a.cancel_status::text = 'N' AND (a.recon::text = '' OR a.recon::text = 'N') AND a.companyid = 1
               AND a.book_type = 'CC' AND (  UPPER(a.acchead) = UPPER(:studentId)  OR UPPER(a.acchead) = UPPER(:studentId) )

           UNION

           SELECT 'B'::text AS tbl, b.acchead, b.voucher_date AS dt, b.description, b.doc_ref_no, b.amount, b.debit_or_credit, b.voucher_no, b.recon, b.slno
           FROM
               schooldev."MESS_LEDGER_B" b
           JOIN
               schooldev."MESS_LEDGER_A" a  ON b.cancel_status = a.cancel_status AND b.voucher_no = a.voucher_no  AND b.book_type = a.book_type  AND a.fin_year = b.fin_year
           WHERE
               b.active_flag = 'Y'::bpchar
               AND b.cancel_status::text = 'N'  AND (b.recon::text = '' OR b.recon::text = 'N') AND b.companyid = 1  AND b.book_type = 'CC'
               AND ( UPPER(b.acchead) = UPPER(:studentId)  OR UPPER(b.acchead) LIKE UPPER(:studentId)  )
           ) AS aa  JOIN schooldev."STUDENT_DETAILS_INFO" sdi ON UPPER(sdi.student_id) LIKE UPPER(aa.acchead)
           WHERE  UPPER(aa.acchead) = UPPER(:studentId)  OR UPPER(aa.acchead) LIKE UPPER(:previousId)
           
        """, nativeQuery = true)
    List<Object[]> getStudentCardAmount(String studentId, String previousId);

    @Query(value = """
            select sum(case when aa.debit_or_credit = 'd' then aa.amount else -(aa.amount) end ) as amt from (SELECT 'A'::text AS tbl,
            	a.acchead, a.voucher_date AS dt, a.description,a.doc_ref_no,a.amount,a.debit_or_credit,a.voucher_no,a.recon, 0 as slno FROM
            	schooldev.
            	"MESS_LEDGER_A" a join
            	schooldev.
            	"MESS_LEDGER_B" bb on(bb.cancel_status=a.cancel_status and bb.voucher_no=a.voucher_no and bb.book_type=a.book_type and a.fin_year=bb.fin_year)
            	WHERE a.active_flag = 'Y'::bpchar AND a.cancel_status::text = 'N'::text AND (a.recon::text = ''::text OR a.recon::text = 'N'::text)
            	 AND a.companyid = 1 AND a.book_type::text = 'MS'::text and (upper(a.acchead)=upper(:studentId) or
            	 upper(a.acchead)=upper(:previousId))and upper(bb.acchead)='HOSDEP'
            	 UNION
            	SELECT 'B'::text AS tbl, b.acchead, b.voucher_date AS dt, b.description, b.doc_ref_no, b.amount, b.debit_or_credit, b.voucher_no, b.recon, b.slno FROM
            	schooldev.
            	"MESS_LEDGER_B" b join
            	schooldev.
            	"MESS_LEDGER_A" a on (b.cancel_status=a.cancel_status and b.voucher_no=a.voucher_no and b.book_type=a.book_type  and a.fin_year=b.fin_year)
            	 WHERE b.active_flag = 'Y'::bpchar AND b.cancel_status::text = 'N'::text AND (b.recon::text = ''::text OR b.recon::text = 'N'::text)
            	AND b.companyid = 1 AND b.book_type::text = 'MS'::text and (upper(b.acchead)=upper(:studentId)
            	or upper(b.acchead) like upper(:previousId))and upper(a.acchead)='HOSDEP') as aa join
            	schooldev.
            	"STUDENT_DETAILS_INFO" sdi on (upper(sdi.student_id) like upper(aa.acchead))
            	where upper(aa.acchead)=upper(:studentId) or upper(aa.acchead) like upper(:previousId)
        
        """, nativeQuery = true)
    List<Object[]> getStudentLeadAmount(String studentId, String previousId);


    @Query("select new com.iitm.hosteldine.service.hostel.UploadReceiptsRecord(" +
            "a.docRefNo, a.voucherDate, a.description, b.acchead, b.amount, c.studentName) " +
            UPLOAD_RECEIPTS + " and a.voucherDate between :fromDate and :toDate " +
            " and (coalesce(:receiptType, '') = '' or a.screenType like concat('%', :receiptType, '%'))" +
            "order by a.voucherDate desc")
    Page<UploadReceiptsRecord> getUploadReceiptsRecordsByDateRange(
            Pageable pageable,
            String activeFlag,
            String statusInactive,
            LocalDate fromDate,
            LocalDate toDate,
            String bookType,
            String receiptType);

    @Query("select new com.iitm.hosteldine.service.hostel.UploadReceiptsRecord(" +
            "a.docRefNo, a.voucherDate, a.description, b.acchead, b.amount, c.studentName) " +
            UPLOAD_RECEIPTS + " and a.voucherDate >= :defaultVoucherDate " +
            " and (coalesce(:receiptType, '') = '' or a.screenType like concat('%', :receiptType, '%'))" +
            "order by a.voucherDate desc")
    Page<UploadReceiptsRecord> getUploadReceiptsRecordsByDefaultDate(
            Pageable pageable,
            String activeFlag,
            String statusInactive,
            LocalDate defaultVoucherDate,
            String bookType,
            String receiptType);

    @Query("""
         select sum(a.amount) as totalAmount from MessLedgerAEntity a where
         FUNCTION('DATE', a.createdAt) = CURRENT_DATE and a.description like concat(:description, '%') and a.activeFlag = :activeFlag
             and a.cancelStatus = :statusInactive
    """)
    Optional<Double> getCurrentDateTotalAmount(String activeFlag, String description,String statusInactive);

    @Query(value = """
    (select mla.id.voucherNo,mla.voucherDate,mla.acchead,mla.subAccountHead,mla.docRefNo,mla.description,mla.debitOrCredit,
        mla.amount,0 as slno,mla.cancelStatus
         from MessLedgerAEntity mla where mla.activeFlag = :activeFlag and mla.id.finYear = :finYear
        and mla.id.voucherNo = :voucherNo order by slno)
            union all
    (select mlb.id.voucherNo,mlb.voucherDate,mlb.acchead,mlb.subAccountHead,mlb.docRefNo,mlb.description,mlb.debitOrCredit,
         mlb.amount,mlb.id.slno,mlb.cancelStatus
         from MessLedgerBEntity mlb where mlb.activeFlag = :activeFlag and mlb.id.finYear = :finYear
        and mlb.id.voucherNo = :voucherNo order by mlb.id.slno asc)
    """)
    List<Object[]> getLedgerDetailsByVoucherNo(String voucherNo, String finYear, String activeFlag);


    @Transactional
    @Modifying
    @Query(value = """
			    update MessLedgerAEntity mla
			    set mla.cancelStatus = :cancelStatus,mla.modifiedAt = :modifiedAt, mla.modifiedBy = :modifiedBy
			    where mla.activeFlag = :activeFlag and mla.id.finYear = :finYear and mla.id.voucherNo = :voucherNo
			""")
    int cancelMessLedgerA(String cancelStatus, String activeFlag, String finYear, String voucherNo, LocalDateTime modifiedAt, String modifiedBy);

    @Query(value = """
        SELECT 'A' tbl, a.voucherDate dt, a.description, a.docRefNo, a.amount, a.debitOrCredit, a.id.voucherNo, a.createdAt as c_date, 0 as slno
        FROM MessLedgerAEntity a
        WHERE a.activeFlag = :activeFlag
          AND a.cancelStatus = :cancelStatus
          AND (a.recon = :emptyString OR a.recon = :cancelStatus)
          AND a.id.bookType = :bookType
          AND a.acchead IN :accHeads
          AND a.voucherDate >= COALESCE(:fromDateParam, a.voucherDate)
          AND a.voucherDate <= COALESCE(:toDateParam, a.voucherDate)
        
        UNION
        SELECT 'B' tbl, b.voucherDate dt, b.description, b.docRefNo, b.amount, b.debitOrCredit, b.id.voucherNo, b.createdAt as c_date, b.id.slno as slno
        FROM MessLedgerBEntity b
        WHERE b.activeFlag = :activeFlag
          AND b.cancelStatus = :cancelStatus
          AND (b.recon = :emptyString OR b.recon = :cancelStatus) 
          AND b.id.bookType = :bookType 
          AND b.acchead IN :accHeads
          AND b.voucherDate >= COALESCE(:fromDateParam, b.voucherDate)
          AND b.voucherDate <= COALESCE(:toDateParam, b.voucherDate)
        """)
    List<Object[]> getLedgerReport(@Param("bookType") String bookType,
                                   @Param("accHeads") List<String> accHeads,
                                   @Param("fromDateParam") LocalDate fromDate,
                                   @Param("toDateParam") LocalDate toDate,
                                   @Param("activeFlag") String activeFlag,
                                   @Param("cancelStatus") String cancelStatus,
                                   @Param("emptyString") String emptyString
    );
}