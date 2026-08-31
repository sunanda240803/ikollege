package com.iitm.hosteldine.repository.api;

import com.iitm.hosteldine.generated.model.GuestCouponAccessTokenEntity;
import com.iitm.hosteldine.model.mess.MessCouponUserMappingEntity;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GuestCouponAccessTokenRepository extends JpaRepository<GuestCouponAccessTokenEntity, Long> {

    Optional<GuestCouponAccessTokenEntity> findByTokenAndActiveFlagTrue(String userName);

    @Query("SELECT a,b FROM UserManagementEntity a " +
            "JOIN MessCouponUserMappingEntity b ON (a.id.username=b.userName and b.activeFlag= :activeFlag)" +
            "WHERE lower(a.id.username) = lower(:loginId) AND a.activeFlag = :activeFlag ")
    Object[] getCatererLoginDetails(String loginId, String activeFlag);

    @Query("SELECT a,b FROM UserManagementEntity a " +
            "JOIN StudentDetailsInfoEntity b ON (lower(a.id.username)=lower(b.studentId) " +
            "and b.activeFlag= :activeFlag and b.settlementFlag= :settlementFlag)" +
            "WHERE lower(a.id.username) = lower(:loginId) AND a.activeFlag = :activeFlag ")
    Object[] getStudentLoginDetails(String loginId, String activeFlag, String settlementFlag);

    List<GuestCouponAccessTokenEntity> findAllByUsernameIgnoreCaseAndActiveFlagTrue(String userName);

    @Query("SELECT g FROM GuestCouponAccessTokenEntity g " +
            "WHERE g.token = :token " +
            "AND g.activeFlag = true " +
            "AND (g.userType IS NULL OR g.userType = :userType)")
    Optional<GuestCouponAccessTokenEntity> findActiveTokenForCaterer(String token, String userType);

    @Query("SELECT b FROM GuestCouponAccessTokenEntity g " +
            "JOIN MessCouponUserMappingEntity b ON (g.username=b.userName and b.activeFlag= :activeFlag and b.messMaster.activeFlag = :activeFlag)" +
            "WHERE g.token = :token AND g.activeFlag = true ")
    Optional<MessCouponUserMappingEntity> getMessFromUserToken(String token, String activeFlag);

    @Query("SELECT s,b,c FROM StudentDetailsInfoEntity s " +
            "LEFT JOIN StudentMessDetailsEntity b ON s.studentId=b.studentDetailsInfo.studentId " +
            "AND b.activeFlag = :activeFlag AND b.currentActiveFlag = :activeFlag " +
            "AND CURRENT_DATE BETWEEN b.fromDate AND b.toDate " +
            "LEFT JOIN MessSessionEntity c ON (b.messMaster.id=c.id.messId AND c.activeFlag = :activeFlag " +
            "AND :formattedTime BETWEEN c.startTime AND c.endTime) " +
            "WHERE UPPER(s.studentId) = UPPER(:studentId) AND s.activeFlag = :activeFlag ")
    List<Object[]> findStudentDetailsWithMessInfoForApi(String studentId, String formattedTime, String activeFlag);


    @Query(value = "SELECT balance_amount from schooldev.food_court_ledger_student_amount(:studentId,:messId) ", nativeQuery = true)
    Double getFoodCourtBalance(@Param("studentId") String studentId, @Param("messId") Long messId);


}