package com.iitm.hosteldine.repository.dean;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iitm.hosteldine.model.dean.DeanHdcComplaintEntity;

public interface DeanHdcComplaintRepository extends JpaRepository<DeanHdcComplaintEntity, Long> {

	Optional<DeanHdcComplaintEntity> findByIdAndStudentIdAndActiveFlag(Long id, String studentId, String activeFlag);

	@Query(value = 
			"SELECT * FROM schooldev.hdc_complaint_list(:studentid, :studentname, :submittedfromdate, :submittedtodate, :loginid, :userrole, :wardenid, :hostelid, :year)", 
			nativeQuery = true)
    Object[] getHdcComplaintListFromFunction(
            String studentid,
            String studentname,
            String submittedfromdate,
            String submittedtodate,
            String loginid,
            String userrole,
            Integer wardenid,
            Long hostelid,
            Integer year
    );
	
	@Query(value = """
			select
			    hdc.student_name,
			    hm.hostel_name,
			    hdc.room_no,
			    hsv.student_id as involved_student_id,
			    hsv.student_name as involved_student_name,
			    hsv.hostel_name as involved_hostel_name,
			    hsv.room_num as involved_student_room_no,
			    hdc.violation,
			    hdc.created_at,
			    hdc.wardern_plea,
			    hdc.warden_decision,
			    hdc.warden_remarks,
			    hdc.penality_status ,
			    hdc.day_scholar_involve,
			    hdc.penality_amount,
			    hdc.penality_due_date,
			    hdc.paid_amount,
			    hdc.payment_description,
			    hdc.payment_reference_no,
			    hdc.hdc_id,
			    hdc.file_name
			from
			    schooldev."IIT_HDC_COMPLAINT_FORMDETAILS" hdc
			left join
			    schooldev."HOSTEL_MASTER" hm
			    on hm.hostel_id = hdc.hostel_id
			left join
			    lateral unnest(string_to_array(hdc.involved_stud_ids, ',')) as involved_id on true
			left join
			    schooldev."HOSTEL_STUDENTS_VIEW" hsv
			    on hsv.student_id = involved_id
			where
				hdc.hdc_id = :hdcId AND
			    hdc.active_flag = 'Y' AND
			    UPPER(TRIM(hdc.student_id)) = UPPER(TRIM(:studentId))
						""", nativeQuery = true)
	List<Object[]> getHdcComplaintDetails(String studentId,Long hdcId);

	Optional<DeanHdcComplaintEntity> findByIdAndActiveFlag(Long id, String yes);

	@Query(value = 
		    "SELECT GROUP_CONCAT(hdc_id::text || '~' || student_id || '~' || " +
		    "CASE " +
		    "  WHEN penality_status = 'Paid' THEN 'cd' " +
		    "  WHEN penality_status = 'PartialPaid' THEN 'cd' " +
		    "  WHEN penality_status = 'Pending' THEN 'pg' " +
		    "  ELSE 'pg' " +
		    "END) AS hdc_details " +
		    "FROM schooldev.\"IIT_HDC_COMPLAINT_FORMDETAILS\" " +
		    "WHERE (UPPER(student_id) = UPPER(?) " +
		    "   OR UPPER(?) = ANY (string_to_array(UPPER(involved_stud_ids), ','))) " +
		    "AND active_flag = 'Y'", 
		    nativeQuery = true)
		String getComplaintList(String id1,String id2);

	@Query(value = 
			"SELECT parent_mail,relation_type FROM schooldev.student_parent_mail where (parent_mail is not null and parent_mail <> '') and upper(student_id)= upper(:student_id);",nativeQuery = true)
	List<Object[]> getParentMailByStudentId(@Param("student_id")String student_id);

	@Query(value =
			"SELECT hdc_id,student_id,penality_amount FROM schooldev.\"IIT_HDC_COMPLAINT_FORMDETAILS\" " +
					"where (penality_status!= 'Paid') and upper(student_id)= upper(:student_id) and active_flag='Y' ;",nativeQuery = true)
	List<Object[]> getPendingAmountsByStudentId(@Param("student_id")String student_id);


}
