package com.iitm.hosteldine.dto.mess;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Long;
import com.iitm.hosteldine.model.mess.MessMasterEntity;

@Data
public class MessTerminalDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("messMaster")
    private MessMasterDto messMaster;
    @JsonProperty("terminalIp")
    private String terminalIp;
    @JsonProperty("terminalMacId")
    private String terminalMacId;
    @JsonProperty("terminalDescription")
    private String terminalDescription;
    @JsonProperty("issuedBy")
    private String issuedBy;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("password")
    private String password;
}