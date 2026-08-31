package com.iitm.hosteldine.repository.mess;

import com.iitm.hosteldine.dto.dean.MessInspectionReportDto;
import com.iitm.hosteldine.model.mess.MessInspectionReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessInspectionReportRepository extends JpaRepository<MessInspectionReportEntity, Long> {

	@Query(value = "SELECT * FROM schooldev.mess_inspection_list( " +
			" :wardenid, " +
			" :messid , " +
			" cast(:fromdate as varchar), " +
			" cast(:todate as varchar), " +
			" cast(:userrole as varchar), " +
			" cast(:username as varchar) " +
			");", nativeQuery = true)
	List<MessInspectionReportDto> getMessInspectionList(
			@Param("wardenid") Long wardenid,
			@Param("messid") Long messid,
			@Param("fromdate") String fromDate,
			@Param("todate") String todate,
			@Param("userrole") String userrole,
			@Param("username") String username);

	@Query(value = "SELECT * FROM schooldev.mess_inspection_list( " +
			" :wardenid, " +
			" :messid , " +
			" cast(:fromdate as varchar), " +
			" cast(:todate as varchar), " +
			" cast(:userrole as varchar), " +
			" cast(:username as varchar) " +
			");", nativeQuery = true)
	Page<MessInspectionReportDto> getMessInspectionList(
			@Param("wardenid") Long wardenid,
			@Param("messid") Long messid,
			@Param("fromdate") String fromDate,
			@Param("todate") String todate,
			@Param("userrole") String userrole,
			@Param("username") String username,
			Pageable pageable);

}