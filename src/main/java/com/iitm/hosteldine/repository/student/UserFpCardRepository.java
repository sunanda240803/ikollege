package com.iitm.hosteldine.repository.student;

import com.iitm.hosteldine.entity.student.UserFpCardEntity;
import com.iitm.hosteldine.model.mess.MessSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserFpCardRepository extends JpaRepository<UserFpCardEntity, String> {
    boolean existsByUserId(String userId);

    @Query(value="SELECT nextval('schooldev.\"USER_FP_ACCESS_CARD_SEQ\"')")
    String findMaxAccessCardSerialNo();

	UserFpCardEntity findByUserId(String studentId);

    @Query(value = """
            SELECT max(access_card_serial_no) from schooldev."USER_FP_CARD" where card_active_status=:activeStatus
            """, nativeQuery = true)
    Long getMaxSequenceNumber(String activeStatus);

    @Query(value = """
    select sdi.student_name, ufc.access_card_serial_no, ufc.user_id, ufc.active, ufc.card_active_status, ufc.facial, mm.mess_name, 
           case when :withFacialImage then facial_photo else '' end
      from schooldev."USER_FP_CARD" ufc
 left join schooldev."CURRENT_MESS_DETAILS_VIEW" smd
        on smd.student_id = ufc.user_id
 left join schooldev."ALL_STUDENTS_DETAILS_VIEW" sdi
        on sdi.student_id = smd.student_id
 left join schooldev."MESS_MASTER" mm 
        on mm.mess_master_id = smd.mess_id
     where ufc.active = 'Y'
       and user_id like (case when :idPart is null then user_id else upper('%' || :idPart || '%') end)
       and access_card_serial_no like (case when :seralNo is null then access_card_serial_no else '%' || :seralNo || '%' end)
 order by (case when ufc.facial is not null then 0 else 1 end),
          (case when mm.mess_master_id is not null then 0 else 1 end)
    limit 100
""", nativeQuery = true)
    List<Object[]> fetchUserDetails(boolean withFacialImage, String idPart, String seralNo);

    Optional<UserFpCardEntity> findFirstByUserIdAndFacialIsTrue(String userId);
    Optional<UserFpCardEntity> findFirstByAccessCardSerialNo(String userId);

  /*  @Query("SELECT a FROM UserFpCardEntity a  " +
            "JOIN StudentDetailsInfoEntity b ON a.userId=b.studentId " +
            "WHERE b.activeFlag = :activeFlag and a.accessCardSerialNo = :cardsn ")
    Optional<UserFpCardEntity>  checkUserWithStudent(String cardsn,String activeFlag);

    Optional<UserFpCardEntity>  findByAccessCardSerialNoAndCardActiveStatus(String cardsn,String activeFlag);
*/
    @Query("SELECT a,b FROM UserFpCardEntity a  " +
            "LEFT JOIN AllStudentsDetailsViewEntity b ON a.userId=b.studentId " +
            "WHERE a.cardActiveStatus = :activeFlag and a.cardsn = :cardSn ")
   List<Object[]>  checkUserFpAndStudentMessDetails(String cardSn,String activeFlag);
}
