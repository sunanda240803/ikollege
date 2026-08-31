package com.iitm.hosteldine.repository.studentDashboard;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.model.warden.GuestRoomAllotmentInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface GuestRoomAllotmentInfoEntityRepository extends JpaRepository<GuestRoomAllotmentInfoEntity, Long> {

    @Query(value = " SELECT *  FROM "+ ModelConstants.SCHEMA+".\"GUEST_ROOM_ALLOTMENT_INFO\" " +
            " where (stay_from_date::date - INTERVAL '1 day', stay_to_date::date + INTERVAL '1 day') OVERLAPS (:fromDate, :toDate) and " +
            " building_id =:buildingId and roomid = :roomId  and active_flag='Y'", nativeQuery = true)
    List<Object[]> checkRoomOccupied(@Param("buildingId") Integer buildingId, @Param("roomId") Integer roomId,
                                     @Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate );

    List<GuestRoomAllotmentInfoEntity> findByRequestIdAndGuestId(String requestId,String GuestId);
}
