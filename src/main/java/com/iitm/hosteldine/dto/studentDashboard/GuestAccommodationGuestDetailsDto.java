package com.iitm.hosteldine.dto.studentDashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Long;
import java.lang.Integer;

@Data
public class GuestAccommodationGuestDetailsDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("guestId")
    private Long guestId;
    @JsonProperty("requestId")
    private Integer requestId;
    @JsonProperty("guestName")
    private String guestName;
    @JsonProperty("relationOfGuest")
    private String relationOfGuest;
    @JsonProperty("idProof")
    private String idProof;
    @JsonProperty("proofDescription")
    private String proofDescription;
    @JsonProperty("guestGender")
    private String guestGender;
    private boolean selected;
    private String guestOtherRelation;
    private String allotedHostelName;
    private String allotedRoomNo;
}