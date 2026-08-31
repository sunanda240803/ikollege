package com.iitm.hosteldine.dto.hostel;

import com.iitm.hosteldine.validator.fieldValidators.ValidStringField;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.iitm.hosteldine.model.hostel.ShowEventMasterEntity}
 */
@Data
public class ShowEventMasterDto implements Serializable {
    private Long id;
    @ValidStringField(message = "message.validation.event.name.required",fieldName = "message.label.event.name",min = 3,max =64)
    private String eventName;
    @ValidStringField(message = "message.validation.event.description.required",fieldName = "message.label.event.description",min = 3,max =127)
    private String eventDesc;
    private Long schoolId;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventStartingDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventEndingDate;
    private Double creditLimit;
    private String logoName;
    @ValidStringField(message = "message.validation.contact.us.required",fieldName = "message.label.contact.us",min =3,max =300)
    private String contactUs;
    private String currentlyActive;
//    private byte[] logo;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate actualEventStartingDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate actualEventEndingDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventAccRegistrationStartingDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate eventAccRegistrationEndingDate;
    private Boolean salesType;
    @ValidStringField(message = "message.validation.accommodation.required",fieldName = "message.label.accommodation")
    private String eventType;
    @ValidStringField(message = "message.validation.select.account.head.required",fieldName = "message.label.account.head")
    private String acchead;
}