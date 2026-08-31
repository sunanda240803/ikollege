package com.iitm.hosteldine.repository.dashboard.student;

import com.iitm.hosteldine.dto.dashboard.student.StudentBlackListDetailDto;
import com.iitm.hosteldine.model.dashboard.student.StudentBlackListDetailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface StudentBlackListDetailRepository extends JpaRepository<StudentBlackListDetailEntity, Long> {

    @Query("""
    SELECT new com.iitm.hosteldine.dto.dashboard.student.StudentBlackListDetailDto(
        s.id, s.studentId, s.fromDate, s.toDate, 
        s.currentlyActive, s.currentlyActiveDate, s.createdAt, s.remarkDescription, 
        sd.firstName, sd.lastName, null, null, null, null
    )
    FROM StudentBlackListDetailEntity s 
    JOIN StudentDetailsInfoEntity sd ON s.studentId = sd.studentId 
    WHERE s.activeFlag = :statusActive 
    AND (
        UPPER(sd.studentId) LIKE CONCAT('%', UPPER(:search), '%') 
        OR CONCAT(UPPER(sd.firstName), ' ', UPPER(sd.lastName)) LIKE CONCAT('%', UPPER(:search), '%')
        OR UPPER(s.remarkDescription) LIKE CONCAT('%', UPPER(:search), '%')
        OR UPPER(s.currentlyActive) LIKE CONCAT('%', UPPER(:search), '%')
    ) 
    ORDER BY s.studentId, sd.studentId DESC
""")
    Page<StudentBlackListDetailDto> findByActiveFlagAndSearch(String statusActive, Pageable pageable, String search);

    @Query("""
    SELECT new com.iitm.hosteldine.dto.dashboard.student.StudentBlackListDetailDto(
        s.id, s.studentId, s.fromDate, s.toDate, 
        s.currentlyActive, s.currentlyActiveDate, s.createdAt, s.remarkDescription, 
        sd.firstName, sd.lastName, null, null, null, null
    )
    FROM StudentBlackListDetailEntity s 
    JOIN StudentDetailsInfoEntity sd ON s.studentId = sd.studentId 
    WHERE s.activeFlag = :statusActive 
    ORDER BY s.studentId, sd.studentId DESC
""")
    Page<StudentBlackListDetailDto> findAllByActiveFlag(String statusActive, Pageable pageable);


    @Query("SELECT s FROM StudentBlackListDetailEntity s " +
            "WHERE s.fromDate = :fromDate AND s.toDate = :toDate " +
            "AND s.studentId = :studentId " +
            "AND s.activeFlag = :activeFlag " +
            "AND s.currentlyActive = :currentlyActive")
    Optional<StudentBlackListDetailEntity> getStudentDetailsInfo(LocalDate fromDate, LocalDate toDate, String studentId, String activeFlag, String currentlyActive);

    @Query("SELECT s FROM StudentBlackListDetailEntity s " +
            "WHERE :currentDate BETWEEN s.fromDate AND s.toDate " +
            "AND s.studentId = :studentId " +
            "AND s.activeFlag = :activeFlag " +
            "AND s.currentlyActive = :currentlyActive ")
    Optional<StudentBlackListDetailEntity> getStudentBlackListDetails(LocalDate currentDate, String studentId, String activeFlag, String currentlyActive);

    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM StudentBlackListDetailEntity s " +
            "WHERE s.studentId = :studentId " +
            "AND s.activeFlag = :activeFlag " +
            "AND s.currentlyActive = :currentlyActive) THEN true ELSE false END")
    boolean blacklistExistsByStudentId(
            @Param("studentId") String studentId,
            @Param("activeFlag") String activeFlag,
            @Param("currentlyActive") String currentlyActive);

}
