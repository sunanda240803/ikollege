package com.iitm.hosteldine.repository.staff;

import java.util.List;
import java.util.Optional;

import com.iitm.hosteldine.dto.staff.StaffDetailsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.iitm.hosteldine.model.staff.StaffDetailsEntity;

public interface StaffDetailsRepository extends JpaRepository<StaffDetailsEntity, String> {
	
	final String BASE_QUERY = "FROM schooldev.\"FACULTY_PERSONAL_DETAILS\" fpd "
			+ "LEFT JOIN schooldev.\"STAFF_DESIGNATION_MASTER\" sdm "
			+ "ON sdm.designation_id = fpd.designation_id AND sdm.active_flag = :statusActive "
			+ "LEFT JOIN schooldev.\"USER_MANAGEMENT\" um "
			+ "ON lower(um.user_id) = lower(fpd.faculty_id) AND um.active_flag = :statusActive "
            + "LEFT JOIN schooldev.roles rm "
            + "ON rm.role_id = um.role_id AND rm.active_flag = :statusActive "
            + "LEFT JOIN schooldev.roles srm "
            + "ON srm.role_id = um.secondary_role_id AND srm.active_flag = :statusActive "
			+ "WHERE fpd.active_flag = :statusActive ";

	final String SEARCH_QUERY = "FROM schooldev.\"FACULTY_PERSONAL_DETAILS\" fpd "
			+ "LEFT JOIN schooldev.\"STAFF_DESIGNATION_MASTER\" sdm "
			+ "ON sdm.designation_id = fpd.designation_id AND sdm.active_flag = :statusActive "
			+ "LEFT JOIN schooldev.\"USER_MANAGEMENT\" um "
			+ "ON lower(um.user_id) = lower(fpd.faculty_id) AND um.active_flag = :statusActive "
            + "LEFT JOIN schooldev.roles rm "
            + "ON rm.role_id = um.role_id AND rm.active_flag = :statusActive "
			+ "LEFT JOIN schooldev.roles srm "
			+ "ON srm.role_id = um.secondary_role_id AND srm.active_flag = :statusActive "
			+ "WHERE (fpd.first_name ilike concat('%',:search,'%') OR fpd.email_address ilike concat('%',:search,'%') "
			+ "OR fpd.faculty_id ilike concat('%',:search,'%') OR sdm.designation_name ilike concat('%',:search,'%') "
            + "OR um.user_name ilike concat('%',:search,'%')) "
			+ "AND fpd.active_flag = :statusActive ";

	
	Long countByDesignationIdAndActiveFlag(long id, String statusActive);

	
	@Query(value = "SELECT fpd.faculty_id, " +
            "(fpd.first_name || ' ' || " +
            "CASE WHEN fpd.middle_name IS NOT NULL AND fpd.middle_name <> '' THEN fpd.middle_name || ' ' ELSE '' END || fpd.last_name) AS full_name, " +
            "sdm.designation_name, fpd.contact_number, fpd.email_address, um.user_name " +
            "FROM schooldev.\"FACULTY_PERSONAL_DETAILS\" fpd " +
            "LEFT JOIN schooldev.\"STAFF_DESIGNATION_MASTER\" sdm " +
            "ON sdm.designation_id = fpd.designation_id AND sdm.active_flag = fpd.active_flag " +
            "LEFT JOIN schooldev.\"USER_MANAGEMENT\" um " +
            "ON um.user_id = fpd.faculty_id AND um.active_flag = fpd.active_flag " +
            "WHERE fpd.active_flag = :statusActive " +
            "ORDER BY fpd.faculty_id", 
    nativeQuery = true)
List<Object[]> findStaffDetailsByActiveFlag(String statusActive);



//@Query("SELECT s FROM StaffDetailsEntity s WHERE s.facultyId = :facultyId")
Optional<StaffDetailsEntity> findByFacultyId(@Param("facultyId") String facultyId);

Optional<StaffDetailsEntity> findByFacultyIdAndActiveFlag(@Param("facultyId") String facultyId, String statusActive);

@Query(value = "SELECT nextval('schooldev.\"FACULTY_PERSONAL_DETAILS_n_fpd_employee_id_seq\"')", nativeQuery = true)
Long getNextEmployeeId();

@Query(value = "SELECT fpd.faculty_id, " + "(fpd.first_name || ' ' || "
		+ "CASE WHEN fpd.middle_name IS NOT NULL AND fpd.middle_name <> '' THEN fpd.middle_name || ' ' ELSE '' END || fpd.last_name) AS full_name, "
		+ "sdm.designation_name, fpd.mobile_number, fpd.email_address, um.user_name, rm.role_name, srm.role_name "
		+ BASE_QUERY, 
		countQuery = "SELECT COUNT(fpd.faculty_id) " + BASE_QUERY, nativeQuery = true)
Page<Object[]> findStaffDetailsByActiveFlag(String statusActive, Pageable pageable);

@Query(value = "SELECT fpd.faculty_id, " + "(fpd.first_name || ' ' || "
		+ "CASE WHEN fpd.middle_name IS NOT NULL AND fpd.middle_name <> '' THEN fpd.middle_name || ' ' ELSE '' END || fpd.last_name) AS full_name, "
		+ "sdm.designation_name, fpd.mobile_number, fpd.email_address, um.user_name, rm.role_name, srm.role_name "
		+ SEARCH_QUERY, 
		countQuery = "SELECT COUNT(fpd.faculty_id) " + SEARCH_QUERY, nativeQuery = true)
Page<Object[]> findByStaffDetailsSearchContainingAndActiveFlag(String search, String statusActive, Pageable pageable);

	@Query(value = """
SELECT faculty_id AS facultyId, first_name AS firstName, last_name AS lastName, email_address AS emailAddress
FROM schooldev."FACULTY_PERSONAL_DETAILS" WHERE upper(concat(first_name, ' ', last_name)) LIKE upper( :compareName)
""", nativeQuery = true)
	Optional<StaffDetailsDto> getFacultyEmailAndName(String compareName);

	@Modifying
	@Query("""
update StaffDetailsEntity s set s.facultyId = :name where s.facultyId = :userName
""")
	void updateFacultyId(String userName, String name);
}
