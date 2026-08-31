package com.iitm.hosteldine.dto.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.ALWAYS)
public class WorkFlowWardenAPIDto {
    @JsonProperty("warden_name")
    private String wardenName;
    @JsonProperty("warden_email")
    private String wardenEmail;
    @JsonProperty("hostel_code")
    private String hostelCode;
    @JsonProperty("hostel_name")
    private String hostelName;
    @JsonProperty("warden_ldap_name")
    private String wardenLDAPName;
    @JsonProperty("warden_phone_no")
    private String wardenPhoneNo;
}
