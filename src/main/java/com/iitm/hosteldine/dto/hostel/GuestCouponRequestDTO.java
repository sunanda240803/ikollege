package com.iitm.hosteldine.dto.hostel;

import com.iitm.hosteldine.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuestCouponRequestDTO {
	// TODO: check StudentDTO
    private String name;
    private String studentId;
    private String category;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate diningFrom;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate diningTo;
    private String paymentStatus;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate submittedFrom;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate submittedTo;
    private String hostelName;
    public Long hostelId;
    private Integer roomNo;
    private String mobileNo;
    private String messName;
    private Long messId;
    private Integer bfRatePerUnit;
    private Integer lunchRatePerUnit;
    private Integer dinnerRatePerUnit;
    private Integer snacksRatePerUnit;
    // TODO: check if we need decimals
    private Long overallAmount;
    private List<DailyCouponRequestDTO> foodFrequency;
    private FacultyDTO faculty;
    private OthersDTO others;
    private ProjectStaffDTO staff;
    private String frequencyJson;
    private Long requestId;
    private String vegOrNonVeg;
    private String email;
    private Integer noOfBreakfastCoupons;
    private Integer noOfDinnerCoupons;
    private Integer noOfLunchCoupons;
    private Integer noOfSnacksCoupons;
    private String paymentType;
    private LocalDate paymentDate;
    private String paymentReferenceNo;
    private Integer paymentAmount;
    private boolean bulkCoupon;
    private Integer subBfTotal;
    private Integer subLnTotal;
    private Integer subDnTotal;
    private Integer subSnacksTotal;
    private Integer configDiscountedAmount;
    private Integer totalDiscountedAmount;
    private String orderNo;
    private String paymentMode;
    private Double paidAmount;
    private String onlinePaymentStatus;
    private String transactionRefNo;
    private String ccAvRefNo;
    private LocalDateTime transactionDate;
    private boolean studCategory;


    public GuestCouponRequestDTO(String name, String studentId, String category, LocalDate diningFrom, LocalDate diningTo,
                                 String paymentStatus, LocalDate submittedFrom, LocalDate submittedTo) {
        this.name = name;
        this.studentId = studentId;
        this.category = category;
        this.diningFrom = diningFrom;
        this.diningTo = diningTo;
        this.paymentStatus = paymentStatus;
        this.submittedFrom = submittedFrom;
        this.submittedTo = submittedTo;
    }
    
	// Static constant for parameter keys
	public static final String[] PARAM_KEYS = { "name", "studentId", "diningFrom", "diningTo", "submittedFrom",
			"submittedTo", "category", "paymentStatus" };
}
