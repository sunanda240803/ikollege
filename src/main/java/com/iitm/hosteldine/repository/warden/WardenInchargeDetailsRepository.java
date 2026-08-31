package com.iitm.hosteldine.repository.warden;

import java.util.Optional;

import com.iitm.hosteldine.service.warden.WardenAwayRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.warden.WardenInchargeDetailsEntity;

public interface WardenInchargeDetailsRepository extends JpaRepository<WardenInchargeDetailsEntity, Long> {
    
	Optional<WardenInchargeDetailsEntity> findByIdAndActiveFlag(Long id, String activeFlag);
    
	@Query(value = """
			         select a.id,a.warden_id,group_concat(c.hostel_id::text) as hostel_id,group_concat(hostel_name) as hostel_name,away_from,away_to,
			away_description,incharger_id,b.office_no,b.warden_name as wardenName,b.phone_number,b.warden_email,e.warden_name as alt_wardenName,
			e.phone_number as alt_phone_number,e.warden_email as alt_warden_email
			from schooldev."WARDEN_INCHARGE_DETAILS" a
			join schooldev."WARDEN_INFO" b on(a.warden_id = b.id) join schooldev."WARDEN_HOSTEL_MAPPING" c
			on(c.warden_id=a.warden_id) join  schooldev."HOSTEL_MASTER" d on d.hostel_id = c.hostel_id
			join schooldev."WARDEN_INFO" e on (e.id=a.incharger_id)
			where a.active_flag='Y' and b.active_flag='Y' and c.active_flag='Y'
			and now()::date between away_from and  away_to
			group by a.id,
			a.warden_id,away_from,away_to, away_description,incharger_id,b.office_no,b.warden_name,b.phone_number,b.warden_email,e.warden_name,
			e.phone_number,e.warden_email
			     """, nativeQuery = true)
	Page<Object[]> getWardenAwayRequestList(Pageable pageable);

	@Query(value = """
			           select hm.hostel_name as hostelname,wi.id as wardenid,wi.warden_name as wardenname,
			wi.warden_email as warden_email,wi.alternate_email as alternateemail,wi.office_no,wi.phone_number,
			wih.id as inchargeid,wih.warden_name as inchargename,wih.warden_email as inchargeemail,wih.alternate_email as inchargealternateemail,
			wih.phone_number as incharger_phone,wid.away_from1 ,wid.away_to1,warden1,wid.incharge1,wid.away_from2 , wid.away_to2,warden2,wid.incharge2,
			wid.away_from3 , wid.away_to3,warden3,wid.incharge3,wid.away_from4,wid.away_to4,warden4,wid.incharge4,
			wid.away_from5,wid.away_to5,warden5,wid.incharge5,
			case when (now()::date between wid.away_from1 and wid.away_to1) then
			(case when(now()::date between wid.away_from2 and wid.away_to2) then
			case when (now()::date between wid.away_from3 and wid.away_to3) then
			case when (now()::date between wid.away_from4 and wid.away_to4) then
			case when (now()::date between wid.away_from5 and wid.away_to5) then  incharge5
			else incharge4 end else incharge3 end else incharge2 end else incharge1 end )
			else incharge1 end as incharge,
			case when (now()::date between wid.away_from1 and wid.away_to1) then
			(case when(now()::date between wid.away_from2 and wid.away_to2) then
			case when (now()::date between wid.away_from3 and wid.away_to3) then
			case when (now()::date between wid.away_from4 and wid.away_to4)  then
			case when (now()::date between wid.away_from5 and wid.away_to5) then  5
			else 5 end else 4  end else 3  end else 2  end ) else 1 end as count
			from schooldev."HOSTEL_MASTER" hm
			join schooldev."WARDEN_HOSTEL_MAPPING" wum on(hm.hostel_id =wum.hostel_id)
			left join schooldev.warden_incharge_details_baseview wid on(wum.warden_id=wid.warden1 and now()::date between wid.away_from1 and wid.away_to1 )
			left join schooldev."WARDEN_INFO" wi on(wi.id = wum.warden_id)
			left join schooldev."WARDEN_INFO" wih on(wih.id = case when (now()::date between wid.away_from1 and wid.away_to1) then
			(case when(now()::date between wid.away_from2 and wid.away_to2) then
			case when (now()::date between wid.away_from3 and wid.away_to3) then
			case when (now()::date between wid.away_from4 and wid.away_to4)  then
			case when (now()::date between wid.away_from5 and wid.away_to5) then  incharge5
			else incharge4 end else incharge3  end else incharge2  end else incharge1  end ) else incharge1 end)
			where wum.active_flag='Y'
			       """, nativeQuery = true)
	Page<Object[]> getHostelWardenDetails(Pageable pageable);

	@Query(value = """
        SELECT
            wid.id,

            wi.id AS wardenid,
            wi.warden_name AS wardenname,
            wi.warden_email AS wardenemail,

            win.id AS inchargerid,
            win.warden_name AS inchargename,
            win.warden_email AS inchargeremail,

            wid.away_from,
            wid.away_to,
            wid.away_description,

            (
                SELECT STRING_AGG(DISTINCT hm.hostel_name, ',')
                FROM schooldev."WARDEN_HOSTEL_MAPPING" whm
                JOIN schooldev."HOSTEL_MASTER" hm
                    ON hm.hostel_id = whm.hostel_id
                WHERE whm.warden_id = wid.warden_id
            ) AS wardenHostels,

            (
                SELECT STRING_AGG(DISTINCT hm.hostel_office_email, ',')
                FROM schooldev."WARDEN_HOSTEL_MAPPING" whm
                JOIN schooldev."HOSTEL_MASTER" hm
                    ON hm.hostel_id = whm.hostel_id
                WHERE whm.warden_id = wid.warden_id
            ) AS hostelOfficeEmails,

            (
                SELECT STRING_AGG(DISTINCT hm.hostel_name::text, ',')
                FROM schooldev."WARDEN_HOSTEL_MAPPING" whm
                JOIN schooldev."HOSTEL_MASTER" hm
                    ON hm.hostel_id = whm.hostel_id
                WHERE whm.warden_id = wid.incharger_id
            ) AS inchargeHostelIds

        FROM schooldev."WARDEN_INCHARGE_DETAILS" wid
        JOIN schooldev."WARDEN_INFO" wi
            ON wid.warden_id = wi.id
        JOIN schooldev."WARDEN_INFO" win
            ON wid.incharger_id = win.id
        WHERE wid.id = :id and wid.active_flag = :activeFlag
        """, nativeQuery = true)
	Optional<WardenAwayRequestRecord> getWardenAwayRequestDetailsById(Long id, String activeFlag);
}
