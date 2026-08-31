package com.iitm.hosteldine.dto.mess;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccomodationDto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String categoryName;
	private int paymentPerDay;
	private int breakfastCoupon;
	private int lunchCoupon;
	private int dinnerCoupon;
}
