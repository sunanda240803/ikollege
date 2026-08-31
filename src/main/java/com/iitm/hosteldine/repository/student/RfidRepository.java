package com.iitm.hosteldine.repository.student;

import com.iitm.hosteldine.entity.student.RfidMappingEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RfidRepository extends CrudRepository<RfidMappingEntity, String> {
    boolean existsByStudentId(String studentId);

    Optional<RfidMappingEntity> findByStudentId(String studentId);
}
