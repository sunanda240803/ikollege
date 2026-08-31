package com.iitm.hosteldine.model;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

/**
 * Mapping for DB view
 */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "\"FINAL_OCCUPIED_ROOM_ID_VIEW\"", schema = ModelConstants.SCHEMA)
public class FinalOccupiedRoomIdViewEntity {
    @Id
    @Column(name = "n_hral_roomallotmentid")
    private Integer nHralRoomallotmentid;

    @Size(max = 30)
    @Column(name = "v_harl_studentid", length = 30)
    private String vHarlStudentid;
}