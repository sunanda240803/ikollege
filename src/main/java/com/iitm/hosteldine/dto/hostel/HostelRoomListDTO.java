package com.iitm.hosteldine.dto.hostel;

import lombok.Data;

import java.util.List;
@Data
public class HostelRoomListDTO {
    HostelMasterDto hostel;
    List<HostelRoomInfoDto> rooms;
}
