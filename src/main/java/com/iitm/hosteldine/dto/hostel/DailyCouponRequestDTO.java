package com.iitm.hosteldine.dto.hostel;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.util.CustomLocalDateDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyCouponRequestDTO {
//    @JsonFormat(pattern = Constants.FRONTEND_DATE_FORMAT)
    @JsonDeserialize(using = CustomLocalDateDeserializer.class)
    private LocalDate date;
    private Integer noOfBreakfast;
    private Integer noOfLunch;
    private Integer noOfDinner;
    private Integer noOfSnacks;
    private Boolean havingBreakfast;
    private Boolean havingLunch;
    private Boolean havingDinner;
    private Boolean havingSnacks;
    private Integer dayTotal;
}
