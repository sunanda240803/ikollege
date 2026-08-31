package com.iitm.hosteldine.repository.hostel;

import com.iitm.hosteldine.model.hostel.StudentHostelEnrollmentDateConfigurationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface HostelEnrollmentConfigurationRepository extends JpaRepository<StudentHostelEnrollmentDateConfigurationEntity, Long> {

	StudentHostelEnrollmentDateConfigurationEntity findByIdAndActiveFlag(long Id, String statusActive);

    StudentHostelEnrollmentDateConfigurationEntity findTopByActiveFlagOrderByModifiedAtDesc(String statusActive);

    @Modifying
    @Transactional
    @Query("""
    		UPDATE StudentHostelEnrollmentDateConfigurationEntity SET activeFlag = :statusInactive
    	""")
	void deactivateHostelEnrollmentDates(String statusInactive);

    Optional<StudentHostelEnrollmentDateConfigurationEntity> findByActiveFlag(String activeFlag);

    @Query(value = "select * from schooldev.hostel_enrollment_list(:status,:studentName,:studentId,:hostelId,:userRole,:userName);"
            , nativeQuery = true)
    Page<Object[]> getHostelEnrollmentDetails(@Param("status") String status, @Param("studentName") String studentName,
                                               @Param("studentId") String studentId, @Param("hostelId") Integer hostelId,
                                               @Param("userRole") String userRole, @Param("userName") String userName,
                                              Pageable pageable);

}


