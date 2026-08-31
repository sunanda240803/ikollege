package com.iitm.hosteldine.repository.student;

import com.iitm.hosteldine.model.student.StudentHostelEnrollmentConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentHostelEnrollmentConfigurationRepository extends JpaRepository<StudentHostelEnrollmentConfigurationEntity, Long> {
    Optional<StudentHostelEnrollmentConfigurationEntity> findFirstByActiveFlag(String activeFlag);
}
