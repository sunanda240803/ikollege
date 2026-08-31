package com.iitm.hosteldine.repository.student.wellness;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.iitm.hosteldine.model.student.wellness.DostElectionDepartmentEntity;

public interface DostElectionDepartmentRepository extends JpaRepository<DostElectionDepartmentEntity, Long> {
	
	@Query(value = "SELECT d.deptCode FROM DostElectionDepartmentEntity d" +
            " WHERE d.activeFlag = :activeFlag ORDER BY d.deptCode")
	List<String> getDeptCodeList(String activeFlag);

}