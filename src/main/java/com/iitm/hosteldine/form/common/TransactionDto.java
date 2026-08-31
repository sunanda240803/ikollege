package com.iitm.hosteldine.form.common;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.hostel.StudentDebitAccheadConfigDto;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class TransactionDto {
	private String accHead;
    private double amount;
    private String description;
    private double totalTransferAmount;
    private int listSize;
    private int slNo;
    private String finYear;
    private Date voucherDate;
    private String bookType;
    private String subAccHead;
    private String fcno;
    private String recon;
    private String cancelStatus;
    private String reportrp;
    private String adv;
    private String adj;
    private String chequeNo;
    private LocalDate chequeDate;
    private String description1;
    private String user;
    private String pono;
    private String a2no;
    private String party;
    private double itAmount;
    private double itPer;
    private String itcons;
    private String itacc;
    private int link;
    private String surcharge;
    private double unmatchamt;
    private String mrno;
    private String activeFlag;
    private String debitOrCredit;
    private String type;
    private String docRefNo;
    private double tdsValue;
    private double advAmount;
    private double billAmount;
    private String docDt;
    private double unmatchAmt;
    private String iKollegeTransferType;
    
    private String screenType;
    private int studentCount;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate date;
    private String voucherNo;
    
    private List<TransactionDto> transferAmtList1;
    private List<TransactionDto> transferAmtList2;
    private List<TransactionDto> transferAmtList3;
    private List<TransactionDto> transferAmtList4;

    private List<StudentDebitAccheadConfigDto> accheadInputs = new ArrayList<>();
    private String studentId;
	private MultipartFile file;
	private String error;
	private String excelErrorMsg;
	private ArrayList<String> errorList;
    private Double totalDebit;
    private Double totalCredit;
    private int rowNumber;
    private Map<String, Integer> columnIndexMap;

    private byte[] fileBytes;
    private String logTag;
}
