package com.iitm.hosteldine.repository.dashboard.student;

import com.iitm.hosteldine.model.dashboard.student.HostelVacatingAllowedStudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HostelVacatingAllowedStudentRepository extends JpaRepository<HostelVacatingAllowedStudentEntity, String> {

    Optional<HostelVacatingAllowedStudentEntity> findByStudentIdAndActiveFlag(String studentId, String statusActive);

    @Query(value = """
SELECT
    COALESCE(hb.acchead, 'N/A') AS acchead,
    COALESCE(CAST(s AS INTEGER), 0) AS studentBalance,
    COALESCE(hb.hostel_deposit_amt, 0) AS hostelDepositAmt,
    CASE
        WHEN (COALESCE(CAST(s AS INTEGER), 0) + COALESCE(hb.hostel_deposit_amt, 0)) < -100
            THEN COALESCE(CAST(s AS INTEGER), 0)
        ELSE (COALESCE(CAST(s AS INTEGER), 0) + COALESCE(hb.hostel_deposit_amt, 0))
        END AS netBal
FROM (
         SELECT schooldev.student_balance(:studentId) AS s
     ) AS student_balance_data
         LEFT JOIN schooldev.students_with_hostel_deposit_balance hb
                   ON hb.acchead = :studentId
WHERE (COALESCE(CAST(student_balance_data.s AS INTEGER), 0) + COALESCE(hb.hostel_deposit_amt, 0)) < -100;
""", nativeQuery = true)
    Optional<List<Object[]>> getStudentBalanceDetails(String studentId);


    @Query(value = """
            select warden_email, email_address, n_fm_facility_master_name, v_sdi_studentid, ldap_username as warden_name, c.user_name as hostel_office_name
            from  schooldev."COMPLETE_STUDENT_VIEW" b
            join schooldev."HOSTEL_USER_MAPPING" c on ((c.hostel_id = b.n_fm_facility_master_id) and c.active_flag = 'Y')
            join schooldev."USER_MANAGEMENT" h on ((c.user_name = h.user_name) and h.active_flag = 'Y')
            join schooldev."WARDEN_HOSTEL_MAPPING" whm on (whm.hostel_id = b.n_fm_facility_master_id and whm.active_flag = 'Y')
            join schooldev."WARDEN_INFO" wi on (whm.warden_id = wi.id and wi.active_flag = 'Y')
            left join schooldev."FACULTY_PERSONAL_DETAILS" j on ((h.user_id = j.faculty_id) and j.active_flag = 'Y')
            where v_sdi_studentid= :studentId;
            """,
            nativeQuery = true)
    List<Object[]> getStudentAndHostelDetails(String studentId);

    @Query(value = """
                select hrvr.dues_permission_required, hrvr.student_id, hrvr.hostel_or_warden_approval_status, hrvr.created_at
                from schooldev."IITMSTUDENT_HOSTEL_ROOM_VACATING_REQUEST_VIEW" hrvr
                where hrvr.student_id = :studentId and hrvr.active_flag = :activeFlag;
            """,
            nativeQuery = true)
    List<Object[]> getDueApprovalStatus(String studentId, String activeFlag);
}
