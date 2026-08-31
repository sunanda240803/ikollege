package com.iitm.hosteldine.form.hostel;

import java.time.LocalDate;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import com.iitm.hosteldine.constant.ModelConstants;

import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomInventoryForm {
	private Long assetCategoryId;
	private String assetCategory;
	private String assetCategoryShortcode;
	private String assetName;
	private String assetCode;
	private Long categoryId;
	
	private Long hostelId;
	private String hostelName;
	private String hostelShortCode;
	
	private Long roomId;
	private String roomNo;

	private String assetId;
	private String assetCondition;
	private String assetLocation = ModelConstants.HOSTEL;
	private String assetInUseStatusFlag = ModelConstants.STATUS_ACTIVE;
	private String assetConditionStatus = ModelConstants.GOOD;
	private LocalDate assetConditionDate = LocalDate.now();

	private MultipartFile file;
	private String error;
	private String excelErrorMsg;
	private ArrayList<String> errorList;
	private Long inventoryID;
	private Long quantity;
}

