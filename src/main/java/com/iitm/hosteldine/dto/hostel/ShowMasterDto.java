package com.iitm.hosteldine.dto.hostel;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iitm.hosteldine.validator.fieldValidators.ValidStringField;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ShowMasterDto {
    @JsonProperty("id")
    private Long id;
    @ValidStringField(message = "message.validation.show.name.required",fieldName = "message.label.show.name",min = 3,max =64)
    private String showName;
    @ValidStringField(message = "message.validation.show.desc.required",fieldName = "message.label.show.desc")
    private String showDescr;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate regStartingDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate regEndingDate;
    @JsonProperty("imageLocation")
    private String imageLocation;
    @JsonProperty("currentlyActive")
    private String currentlyActive;
    //@ValidStringField(message = "message.validation.max.seat.count.required",fieldName = "message.label.max.seat.count")
    @JsonProperty("maxSeatCount")
    private Integer maxSeatCount;
    @JsonProperty("seatImageLocation")
    private String seatImageLocation;
    @JsonProperty("isNameType")
    private Boolean isNameType;
    private MultipartFile seatLayout;
    private MultipartFile layout;
    private ShowEventMasterDto showEventMaster;
    List<ShowSeatDetailsDto> showSeatDetails;

    private Boolean isShowBooked;
    private Long totalSeatsBooked;

    //for seat count = 1(radio button)
    private ShowSeatDetailsDto showSeatDetailsDto;
    private String printName;
}