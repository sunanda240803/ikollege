package com.iitm.hosteldine.repository.student;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iitm.hosteldine.model.student.SaveTransactionFAEntity;

public interface SaveTransactionFARepository extends JpaRepository<SaveTransactionFAEntity, Long> {
}