package com.iitm.hosteldine.dto.mess;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;

import com.iitm.hosteldine.dto.OtherCandidate.TempAccomPaymentAdviceDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemporaryAccomodationDto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long requestId;
	private String createdAt;
	private String candidateName;
	private String stayFrom;
	private String stayTo;
	private String gender;
	private String email;
	private String url;
	private String firstName;
	private String lastName;
	private String candidateCategory;
	private String dob;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private int pincode;
	private String phoneNumber;
	private String mobileNumber;
	private String appointmentFrom;
	private String appointmentTo;
	private String stayRequestFrom;
	private String stayRequestTo;
	private double grossPay;
	private String natureOfAppointment;
	private String validatingAuthorityName;
	private String validatingAuthorityEmail;
	private String messOption;
	private String purpose;
	private String approvalStatus;
	private String rejectionDescription;
	private String approvalStatusDetails;
	private List<StayExtensionDto> stayExtensionList;
	private List<AccomodationDto> accomodationList;
	private Map<Long, String> messMap;
	private List<TempAccomPaymentAdviceDto> paymentAdviceList;
	private String imageId;
	private Long candidateId;
	
	//Payment Details List
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate submittedDate;
	private String adviceId;
	private String orderNo;
	private String referenceNo;
	private String paymentMethod;
	private Double netAmount;
	private String paymentStatus;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate submittedFrom;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate submittedTo;
	private String approvalDate;
}
