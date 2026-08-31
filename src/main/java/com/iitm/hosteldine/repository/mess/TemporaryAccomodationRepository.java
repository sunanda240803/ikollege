package com.iitm.hosteldine.repository.mess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.OtherCandidate.CandidateStayDateViewEntity;

public interface TemporaryAccomodationRepository extends JpaRepository<CandidateStayDateViewEntity, Integer> {

	@Query(value = """
			  SELECT
			    request_id, created_at, candidate_id, stay_from, stay_to, first_name, gender, email,payment_amount,payment_approval_date 
			  	FROM schooldev.temp_accomm_list(CAST(:submittedFromdate AS VARCHAR), CAST(:submittedTodate AS VARCHAR), CAST(:stayFrom AS VARCHAR),
			    CAST(:stayTo AS VARCHAR), CAST(:candidateName AS VARCHAR),
			    CAST(:requestId AS VARCHAR), CAST(:email AS VARCHAR),
				:approvalFromDate
			  )
			""", nativeQuery = true)
	Page<Object[]> getTemporaryAccomodationList(LocalDate submittedFromdate, LocalDate submittedTodate, LocalDate stayFrom, LocalDate stayTo, String candidateName, String requestId, String email,
												LocalDateTime approvalFromDate, Pageable pageable);

	@Query(value = """
				SELECT * FROM schooldev."IIT_W_CANDIDATE_PERSONAL_DETAILS" a
				WHERE a.CANDIDATE_ID = CAST(:candidateId AS BIGINT) AND a.ACTIVE_FLAG='Y' AND a.SCHOOL_ID=1
			""", nativeQuery = true)
	Object[] getCandidateDetails(Long candidateId);

	@Query(value = """
				SELECT iwcw.modified_at as cand_wf_modified,iwcar.category as a_category, *
				FROM schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST" iwcar
				left join schooldev."IIT_W_CANDIDATE_WORKFLOW" iwcw on (iwcw.application_id=iwcar.request_id and iwcw.candidate_id=iwcar.candidate_id and
				upper(authority_type) like upper(:authorityType) and iwcw.active_flag='Y')
				WHERE IWCAR.CANDIDATE_ID = CAST(:candidateId AS BIGINT) AND IWCAR.REQUEST_ID = CAST(:requestId AS BIGINT) AND IWCAR.ACTIVE_FLAG='Y' AND IWCAR.SCHOOL_ID=1
			""", nativeQuery = true)
	Object[] getAppointmentDetails(Long candidateId, Long requestId, String authorityType);

	@Query(value = """
			select
			    *
			from
			    (
			    select
			        case
			            when (app_status = :approvedStatus
			            and stay_id = '0'
			            and stay_status = :validatingStatus)
			                then :approvedStatus
			            else stay_status::text
			        end as stay_status1,
			        csdv.stay_status,
			        csdv.candidate_id,
			        csdv.request_id,
			        csdv.stay_id,
			        csdv.appointment_from,
			        csdv.appointment_to,
			        csdv.stay_from,
			        csdv.stay_to,
			        csdv.app_status
			    from
			        schooldev."CANDIDATE_STAY_DATE_LIST_VIEW" as csdv) as stay
			where
			    stay.request_id = :requestId
			    and stay.stay_status1 in :stayStatusList
			    and stay.app_status in :appStatusList
			order by
			    stay.stay_id asc
			""", nativeQuery = true)
	Object[] getStayExtensionList(Long requestId, String approvedStatus, String validatingStatus, List<String> stayStatusList, List<String> appStatusList);

	@Query(value = """
				select id,category_name,acc_stay_amnt,breakfast_coupon,lunch_coupon,dinner_coupon from schooldev."IIT_PS_TEMP_ACCOM_CATEGORY_CONFIG" WHERE active_flag='Y'
			""", nativeQuery = true)
	Object[] getAccomodationList();

	@Query(value = """
						select a.id,request_id, hostel_pay_from_date,hostel_pay_to_date,c.category_name, mess_pay_from_date, mess_pay_to_date,
			overall_amount,payment_status,'' as approval_status,card_status,payment_date_1 as payment_date,payment_approval_status
			from schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE"  as a
			left join schooldev."IIT_PS_TEMP_ACCOM_CATEGORY_CONFIG" c on (a.category_id=c.id)
			WHERE request_id = :requestId and a.active_flag='Y' order by payment_date
					""", nativeQuery = true)
	Object[] getPaymentAdviceList(Long requestId);

	@Query(value = """
				select MIN(stay_from) as requested_from,MAX(stay_to) as requested_to from schooldev."CANDIDATE_STAY_DATE_LIST_VIEW" WHERE request_id=:requestId
			""", nativeQuery = true)
	Object[] getStayDetails(Long requestId);

	@Query(value = """
							select (sum(payment_amount_1 + case when (payment_amount_2 > 0)  then
			payment_amount_2 else '0' end)- sum(overall_amount)) as balance_amount from schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE" as a
			join schooldev."IIT_W_CANDIDATE_APPOINTMENT_REQUEST" as  b
			on (b.request_id=a.request_id )	where
			candidate_id=:candidateId and
			payment_status='Completed' and a.active_flag=:active
						""", nativeQuery = true)
	Long getBalanceAmount(Long candidateId,String active);

	@Query(value = """
				select count(*) from schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE"
				WHERE  (upper( payment_reference_no_1)) = (upper(:referenceNumber)) or (upper( payment_reference_no_2)) = (upper(:referenceNumber)) and active_flag='Y' and school_id = 1
			""", nativeQuery = true)
	int checkReferenceNumber(String referenceNumber);

	@Query(value = """
				select count(*) from schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE"
				right join generate_series(CAST(:stayFromdate AS DATE),CAST(:stayTodate AS DATE), '1 day'::interval) as date
				on((date::date >= hostel_pay_from_date) and (date::date <= hostel_pay_to_date) )
				where request_id=:requestId and active_flag='Y'
			""", nativeQuery = true)
	int checkAccomodationDates(Long requestId, LocalDate stayFromdate, LocalDate stayTodate);

	@Query(value = """
				select count(*) from schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE"
				right join generate_series(CAST(:messFromdate AS DATE),CAST(:messTodate AS DATE), '1 day'::interval) as date
				on((date::date >= mess_pay_from_date) and (date::date <= mess_pay_to_date) )
				where request_id=:requestId and active_flag='Y'
			""", nativeQuery = true)
	int checkMessDates(Long requestId, LocalDate messFromdate, LocalDate messTodate);
	
	@Query(value = """
				select hri.room_id,hri.room_no,capacity as total_capacity,(capacity - count(sub_room_id)) as remainingcount, group_concat(sub_room_id) as subroom
				from schooldev."HOSTEL_ROOM_INFO" hri join schooldev."HOSTEL_FLOOR_MASTER" hfm
				on(building_id=hfm.floor_id and official_guest_status in ('0', 'PD')) left join
				schooldev."IIT_PS_TEMP_ACCOM_PAYMENT_ADVICE" acc on (request_id=:requestId and acc.id=:tempAccomPaymentAdviceId) left join
				schooldev."COMPLETE_HOSTEL_ALLOTMENT_VIEW"
				as a on (a.floor_id = hfm.floor_id and
				a.room_id = hri.room_id and not ((stay_from_date >acc.hostel_pay_to_date::date) or
				(stay_to_date is not null and acc.hostel_pay_from_date::date >stay_to_date)) and a.active_flag='Y')
				where hfm.hostel_id=:hostelId and hri.active_flag='Y' and hfm.active_flag='Y'
				and (
			          CASE
			              WHEN :isSeatSelection = true THEN hri.room_no = CAST(:roomNo AS VARCHAR)
			              ELSE true
			          END
			      )
				group by hri.room_id,hri.room_no, capacity, a.room_id order by hri.room_no
			""", nativeQuery = true)
	Object[] getRoomList(Long requestId, Long tempAccomPaymentAdviceId, Long hostelId, boolean isSeatSelection, String roomNo);

}
