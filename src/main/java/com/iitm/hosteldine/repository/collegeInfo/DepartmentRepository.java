package com.iitm.hosteldine.repository.collegeInfo;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.collegeInfo.DepartmentEntity;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Long> {

	List<DepartmentEntity> findAllByActiveFlagOrderByDepartmentName(String statusActive);

	Optional<DepartmentEntity> findByDepartmentIdAndActiveFlag(long id, String statusActive);

	Optional<DepartmentEntity> findByActiveFlagAndDepartmentNameIgnoreCaseAndDepartmentIdNot(String statusActive,
			String departmentName, long id);

	@Query(value = "select  distinct(department_name) ,department_id from schooldev.departments_details dd where dd.active_flag = 'Y'", nativeQuery = true)
	List<Object[]> getDepartmentName();

	Optional<DepartmentEntity> findByDepartmentId(long id);
}