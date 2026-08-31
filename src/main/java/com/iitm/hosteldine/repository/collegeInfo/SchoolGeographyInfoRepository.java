package com.iitm.hosteldine.repository.collegeInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.collegeInfo.SchoolGeographyInfoEntity;

import java.util.List;
import java.util.Optional;

public interface SchoolGeographyInfoRepository extends JpaRepository<SchoolGeographyInfoEntity, Integer> {


	Optional<SchoolGeographyInfoEntity> findByActiveFlag(String statusActive);

	Optional<SchoolGeographyInfoEntity> findBySchoolId(Integer schoolId);

}