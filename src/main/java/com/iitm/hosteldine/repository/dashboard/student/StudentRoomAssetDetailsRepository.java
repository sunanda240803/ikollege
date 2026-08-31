package com.iitm.hosteldine.repository.dashboard.student;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.iitm.hosteldine.model.dashboard.student.StudentRoomAssetDetailsEntity;

public interface StudentRoomAssetDetailsRepository extends JpaRepository<StudentRoomAssetDetailsEntity, Long> {

    List<StudentRoomAssetDetailsEntity> findByStudentIdAndVacatingRequestIdAndActiveFlag(String studentId, Long vacatingRequestId, String activeFlag);
}
