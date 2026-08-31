package com.iitm.hosteldine.repository;

import com.iitm.hosteldine.model.StudentNetBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentNetBalanceEntityRepository extends JpaRepository<StudentNetBalanceEntity, String> {

    Optional<StudentNetBalanceEntity> findByStudentId(String studentId);

}