package com.iitm.hosteldine.dto.mess;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.lang.Long;
import java.lang.Boolean;
import java.util.ArrayList;
import java.util.List;

@Data
public class MessRegistrationMappingDto {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("girlsOptionOne")
    private String girlsOptionOne;
    @JsonProperty("girlsOptionTwo")
    private String girlsOptionTwo;
    @JsonProperty("boysOption")
    private String boysOption;
    @JsonProperty("girlsOptionOneMinCount")
    private String girlsOptionOneMinCount;
    @JsonProperty("girlsOptionTwoMinCount")
    private String girlsOptionTwoMinCount;
    @JsonProperty("boysOptionMinCount")
    private String boysOptionMinCount;
    @JsonProperty("girlsOptionTwoEnable")
    private Boolean girlsOptionTwoEnable;
    List<MessMasterDto> girlsOneMessList =  new ArrayList<>();
    List<MessMasterDto> girlsTwoMessList =  new ArrayList<>();
    List<MessMasterDto> boysOneMessList =  new ArrayList<>();
    List<MessMasterDto> messList =  new ArrayList<>();
}