package com.iitm.hosteldine.dto.dean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessInspectionReportDto {

    private Integer id;
    private String ldapUsername;
    private Date createdAt;
    private String wardenName;
    private String messName;
    private String cleanlinessKitchen;
    private String cleanlinessPlate;
    private String queueMaintainance;
    private String hygieneMess;
    private String availabilityFood;
    private String feedbackStudent;
    private String otherItem;
    private String fileName;
    private List<PropertyDto> actionList;
    private MultipartFile file;

    public MessInspectionReportDto(Integer id, String ldapUsername, Date createdAt, String wardenName,
                                   String messName, String cleanlinessKitchen, String cleanlinessPlate,
                                   String queueMaintainance, String hygieneMess, String availabilityFood,
                                   String feedbackStudent, String otherItem, String fileName) {
        this.ldapUsername = ldapUsername;
        this.id = id;
        this.createdAt = createdAt;
        this.wardenName = wardenName;
        this.messName = messName;
        this.cleanlinessKitchen = cleanlinessKitchen;
        this.cleanlinessPlate = cleanlinessPlate;
        this.queueMaintainance = queueMaintainance;
        this.hygieneMess = hygieneMess;
        this.availabilityFood = availabilityFood;
        this.feedbackStudent = feedbackStudent;
        this.otherItem = otherItem;
        this.fileName = fileName;
    }
}
