package com.iitm.hosteldine.repository.hostel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iitm.hosteldine.model.hostel.StudentDebitAccheadConfigEntity;

public interface StudentDebitAccheadConfigRepository extends JpaRepository<StudentDebitAccheadConfigEntity, Long> {
	
	List<StudentDebitAccheadConfigEntity> findAllByActiveFlag(String activeFlag);
	
}