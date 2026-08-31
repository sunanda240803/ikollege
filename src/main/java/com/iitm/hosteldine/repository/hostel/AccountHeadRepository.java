package com.iitm.hosteldine.repository.hostel;

import com.iitm.hosteldine.model.hostel.AccountHeadEntity;
import com.iitm.hosteldine.model.hostel.AccountHeadIdEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountHeadRepository extends JpaRepository<AccountHeadEntity, AccountHeadIdEntity> {
    List<AccountHeadEntity> findAllByActiveFlag(String activeFlag);
    
    @Query("SELECT a from AccountHeadEntity a "
    		+ "WHERE a.type = :type "
    		+ "AND a.id.finYear = :finYear "
    		+ "AND a.activeFlag = :activeFlag "
    		+ "AND (a.cldate >= :now OR a.cldate IS NULL) "
    		+ "ORDER BY a.accname ASC")
	List<AccountHeadEntity> getAccountHeadList(String type, String finYear, String activeFlag, LocalDate now);

	AccountHeadEntity findByIdAccheadAndIdFinYearAndActiveFlag(String accHead, String finYear, String activeFlag);
	
	Page<AccountHeadEntity> findAllByActiveFlag(String statusActive, Pageable pageable);

	@Query("SELECT ace FROM AccountHeadEntity ace WHERE ace.activeFlag = :statusActive AND (" +
	           "ace.accname ILIKE CONCAT('%', :search, '%') OR " +
	           "ace.id.acchead ILIKE CONCAT('%', :search, '%') OR " +
	           "ace.type ILIKE CONCAT('%', :search, '%'))")
	Page<AccountHeadEntity> findByAccountHeadSearchList(String statusActive, Pageable pageable, String search);

	boolean existsByActiveFlagAndId_Acchead(String statusActive, String acchead);

	Optional<AccountHeadEntity> findById_Acchead(String acchead);

	Optional<AccountHeadEntity> findById_AccheadAndActiveFlag(String id, String statusActive);

	@Query("SELECT a.id.acchead FROM AccountHeadEntity a WHERE upper(a.id.acchead) = upper(:accHead) " +
		       " AND a.activeFlag = :statusActive AND upper(a.id.acchead) <> (:accHead2)")
	List<String> findById_AccheadAndActiveFlagIgnoreCaseAndId_AccheadNot(String accHead,String statusActive,String accHead2);
	
	List<AccountHeadEntity> findByIdFinYearAndActiveFlag(String finYear, String activeFlag);

	@Query(value = """
	select a from AccountHeadEntity a where a.id.finYear = :finYear and a.activeFlag = :activeFlag and a.type in (:type)
	""")
	Optional<List<AccountHeadEntity>> getBankListBy(String finYear, String activeFlag, List<String> type);
	
    @Query(value = "SELECT t.l1_id, t.l1_name, t.l2_id, t.l2_name, l1_property, l2_property , " +
    		" case when uts.show_hide is null then false else uts.show_hide end as show_hide"+
            " FROM schooldev.\"tab_master\" t " +
    		" LEFT JOIN schooldev.\"IIT_WD_ROLE_TAB_SETTINGS\" rts on ( t.l2_id = rts.tab_id and rts.role = :role and rts.active_flag = 'Y' )"+
            " LEFT JOIN schooldev.\"IIT_WD_USER_TAB_SETTINGS\" uts on ( rts.tab_id = uts.tab_id and uts.user_id = :userId and uts.active_flag = 'Y' )" +
            " where rts.show_hide = true ORDER BY t.l1_Order, t.l2_Order", nativeQuery = true)
	List<Object[]> findTabMastersByRoleAndUserId(String role, String userId);
	
	static final String getMessCardDataQuery = "SELECT ACCHEAD, DEBIT_OR_CREDIT, TRANSACTION_AMOUNT, REF_DESCRIPTION, "
			+ "VOUCHER_DATE, VOUCHER_NO, REF_ACCOUNT,REF_SUB_ACCOUNT, ACCNAME, "
			+ "ss.room_number, ss.floor_name, student_name, BOOK_TYPE "
			+ "FROM schooldev.general_account_ledger_view as ga "
			+ "left join schooldev.\"ALL_STUDENTS_DETAILS_VIEW\" ss ON (ga.ref_account = ss.student_id) "
			+ "WHERE (voucher_date between :fromDate and :toDate) "
			+ "and (case when acchead ='0' then upper(sub_account_head)=upper(:accHead) "
			+ "else upper(acchead) = upper(:accHead) end) and fin_year = :finYear "
			+ "and companyid = '1' and book_type = :bookType ";
	static final String getMessCardOrderByQuery = "order by voucher_date asc, book_type, to_number(voucher_no, '99999')";
	static final String getTempAccommodationDataQuery = "select req_id, a.usage_date as voucher_date, "
			+ "concat(c.first_name, ' ', c.last_name) as candidatename, e.accesscard_serial_no, "
			+ "sum(case when (coupon_type = 'Breakfast') then 1  else 0 end)as breakfast, "
			+ "sum(case when (coupon_type = 'Lunch') then 1 else 0 end)as lunch, "
			+ "sum(case when (coupon_type = 'Dinner') then 1 else 0 end) as dinner, "
			+ "count(*) as total_count, d.mess_head "
			+ "from  schooldev.\"IIT_PS_TEMP_ACCOM_CARD_USAGE_DETAILS\" a "
			+ "join schooldev.\"IIT_W_CANDIDATE_APPOINTMENT_REQUEST\" b on (a.req_id = b.request_id) "
			+ "join schooldev.\"IIT_W_CANDIDATE_PERSONAL_DETAILS\" c on (c.candidate_id = b.candidate_id) "
			+ "join schooldev.\"MESS_MASTER\" d on (d.mess_master_id = a.mess_id) "
			+ "join schooldev.\"MESS_ALLOCATION\" f on(f.mess_master_id = a.mess_id and f.active_flag='Y') "
			+ "join schooldev.\"ACCOUNT_HEAD\" ac on(ac.acchead = f.vendor_code and ac.active_flag = 'Y') "
			+ "join schooldev.\"IIT_PS_TEMP_ACCOM_RFID_KEY_MAPPINGS\" e on (a.rfid_card_no = e.rfid_card_no) "
			+ "where a.usage_date between :fromDate and :toDate "
			+ "and ac.acchead = :accHead and coupon_type != 'Evening Tea' and a.active_flag = 'Y'"
			+ "group by req_id, a.usage_date, concat(c.first_name, ' ', c.last_name), accesscard_serial_no, d.mess_head "
			+ "order by voucher_date asc";
	static final String getGuestCouponDataQuery = "select sum(case when (coupon_type='BF') then 1  else 0 end) as breakfast, "
			+ "sum(case when (coupon_type='LC') then 1 else 0 end)as lunch, "
			+ "sum(case when (coupon_type='DR') then 1 else 0 end) as dinner, "
			+ "sum(case when (coupon_type='ET') then 1 else 0 end) as snacks, "
			+ "a.request_id, a.usage_date, c.candidate_name, b.mess_head, a.coupon_number, count(*) as total_count "
			+ "from schooldev.\"IITM_GUEST_COUPON_MAPPINGS\" a "
			+ "join schooldev.\"MESS_MASTER\" b on (b.mess_master_id = a.mess_id and b.active_flag='Y') "
			+ "join schooldev.\"IITM_GUEST_COUPON_PAYMENT_ADVICE\" c on (c.request_id = a.request_id and c.active_flag='Y') "
			+ "join schooldev.\"MESS_ALLOCATION\" f on(f.mess_master_id = a.mess_id and f.active_flag='Y') "
			+ "join schooldev.\"ACCOUNT_HEAD\" ac on(ac.acchead = f.vendor_code and ac.active_flag = 'Y') "
			+ "where a.usage_date between :fromDate and :toDate and ac.acchead = :accHead and a.active_flag = 'Y' "
			+ "group by a.usage_date, a.request_id, c.candidate_name, b.mess_head, a.coupon_number";

	@Query(value = getMessCardDataQuery + "and ga.day_scholar = 'DS' " + getMessCardOrderByQuery, nativeQuery = true)
	Page<Object[]> getMessCardDataByDS(LocalDate fromDate, LocalDate toDate, String accHead, String bookType,
			String finYear, Pageable pageable);

	@Query(value = getMessCardDataQuery + "and fc_no = :hostelId " + getMessCardOrderByQuery, nativeQuery = true)
	Page<Object[]> getMessCardDataByFC(LocalDate fromDate, LocalDate toDate, String accHead, String bookType,
			String hostelId, String finYear, Pageable pageable);

	@Query(value = getMessCardDataQuery + getMessCardOrderByQuery, nativeQuery = true)
	Page<Object[]> getMessCardData(LocalDate fromDate, LocalDate toDate, String accHead, String bookType,
			String finYear, Pageable pageable);

	@Query(value = getMessCardDataQuery + "and ga.day_scholar = 'DS' " + getMessCardOrderByQuery, nativeQuery = true)
	List<Object[]> getMessCardDataByDS(LocalDate fromDate, LocalDate toDate, String accHead, String bookType,
			String finYear);

	@Query(value = getMessCardDataQuery + "and fc_no = :hostelId " + getMessCardOrderByQuery, nativeQuery = true)
	List<Object[]> getMessCardDataByFC(LocalDate fromDate, LocalDate toDate, String accHead, String bookType,
			String hostelId, String finYear);

	@Query(value = getMessCardDataQuery + getMessCardOrderByQuery, nativeQuery = true)
	List<Object[]> getMessCardData(LocalDate fromDate, LocalDate toDate, String accHead, String bookType,
			String finYear);
	
	@Query(value = getTempAccommodationDataQuery, nativeQuery = true)
	Page<Object[]> getTempAccomData(LocalDate fromDate, LocalDate toDate, String accHead, Pageable pageable);
	
	@Query(value = getTempAccommodationDataQuery, nativeQuery = true)
	List<Object[]> getTempAccomData(LocalDate fromDate, LocalDate toDate, String accHead);
	
	@Query(value = getGuestCouponDataQuery, nativeQuery = true)
	Page<Object[]> getGuestCouponData(LocalDate fromDate, LocalDate toDate, String accHead, Pageable pageable);
	
	@Query(value = getGuestCouponDataQuery, nativeQuery = true)
	List<Object[]> getGuestCouponData(LocalDate fromDate, LocalDate toDate, String accHead);
	
	@Query(value = """
			select ah.acchead, accname, type, opbal, opdate,
			sum(case when credit is not null then credit else 0 end) as credit,
			sum(case when debit is not null then debit else 0 end) as debit,
			sum(case when debit is not null or credit is  not null then credit-debit else 0 end) as net_bal,
			min(ahv.created_at), max(ahv.created_at) from schooldev."ACCOUNT_HEAD" ah
			left join schooldev."ACCOUNT_HEAD_SUMMARY_REPORT_VIEW_FOR_CARD" ahv
			on((ahv.acchead = upper(ah.acchead)  )
				and (
			         (:fromDate is null or :toDate is null)
			         or (ahv.created_at::date between cast(:fromDate as date) and cast(:toDate as date))
			     )
			) where ah.active_flag='Y' and type ='R'
			and ah.acchead in (:accountHead)
			group by ah.acchead, accname, type, opbal, opdate order by 9, 10, ah.acchead
									   """, nativeQuery = true)
	Page<Object[]> getAccountHeadSummaryForCaterer(List<String> accountHead, String fromDate, String toDate, Pageable pageable);
	
	@Query(value = """
			select ah.acchead, accname, type, opbal, opdate,
			sum(case when credit is not null then credit else 0 end) as credit,
			sum(case when debit is not null then debit else 0 end) as debit,
			sum(case when debit is not null or credit is not null then credit-debit else 0 end) as net_bal,
			min(ahv.created_at), max(ahv.created_at) from schooldev."ACCOUNT_HEAD" ah
			left join schooldev."ACCOUNT_HEAD_SUMMARY_REPORT_VIEW" ahv on((ahv.acchead = upper(ah.acchead))
				and (
			         (:fromDate is null or :toDate is null)
			         or (ahv.created_at::date between cast(:fromDate as date) and cast(:toDate as date))
			     )
			)
			where ah.active_flag='Y' group by ah.acchead, accname, type, opbal, opdate order by 9, 10, ah.acchead
												   """, nativeQuery = true)
	Page<Object[]> getAccountHeadSummary(String fromDate, String toDate, Pageable pageable);
	
	@Query(value = """
			select accHead from schooldev."CATERER_LEDGER_MAPPING" where caterer_name=:catererName and active_flag ='Y';
												   """, nativeQuery = true)
	List<String> getAccountHeadList(String catererName);
	
}