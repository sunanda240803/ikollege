package com.iitm.hosteldine.repository.dashboard.student;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iitm.hosteldine.model.dashboard.student.StudentFilesInfoEntity;

public interface StudentFilesInfoRepository extends JpaRepository<StudentFilesInfoEntity, Long> {

    @Query("SELECT s FROM StudentFilesInfoEntity s " +
            "WHERE s.studentId = :studentId " +
            "AND s.requestId = :requestId " +
            "AND s.activeFlag = :activeFlag ")
    List<StudentFilesInfoEntity> getStudentFilesInfo(String studentId, Integer requestId, String activeFlag);
}
