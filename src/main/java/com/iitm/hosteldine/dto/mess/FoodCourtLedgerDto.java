package com.iitm.hosteldine.dto.mess;

import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.model.mess.MessMasterEntity;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class FoodCourtLedgerDto {
    private Long id;
    private StudentDetailsInfoEntity student;
    private MessMasterEntity messMaster;
    private Double amount;
    private LocalDateTime purchaseTimestamp;
    private String debitOrCredit;
    private Long messPeriodId;
    private String description;
    private String item;
    private String messBillingNo;
    private String terminalIp;
    private String messName;
    private String studentId;
    private String mmcId;
    private String messId;
    private String studentName;
    private String messHead;
    private MultipartFile file;
    private List<String> errorList;
    private LocalDate date;
    private LocalDate changeFromDate;
    private LocalDate changeToDate;
    private Double foodCourtAmount;
    private Integer rowNumber;
}
