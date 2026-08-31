package com.iitm.hosteldine.validator.hostel;

import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.dto.hostel.DailyCouponRequestDTO;
import com.iitm.hosteldine.dto.hostel.GuestCouponRequestDTO;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.GuestCouponRequestService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.util.CommonEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GuestCouponRequestValidator {

    private final MessageSource messageSource;
    private final MessMasterService messMasterService;
    private final GuestCouponRequestService requestService;
    private final SimsConfigDataService simsConfigDataService;
    private final MessMasterCommonService messMasterCommonService;

    public void validate(GuestCouponRequestDTO dto, BindingResult bindingResult) {
        validateRequiredFields(dto, bindingResult);
        validateDateFields(dto, bindingResult);
        validateMess(dto, bindingResult);
        validateSessionList(dto, bindingResult);
    }

    private void validateRequiredFields(GuestCouponRequestDTO dto, BindingResult bindingResult) {
    	String category = dto.getCategory();
        validateField(category, "category", "error.category", "message.validation.coupon.category.required", bindingResult);
		boolean isOnlineCoupon= category!=null && (category.equalsIgnoreCase(messageSource.getMessage("message.online.coupon", null, Locale.getDefault()))
				|| category.equalsIgnoreCase(messageSource.getMessage("message.label.hostel.residents", null, Locale.getDefault())));

		if (isOnlineCoupon) {
       	 validateField(dto.getVegOrNonVeg(), "vegOrNonVeg", "error.vegOrNonVeg", "message.validation.meal.type.required", bindingResult);
        }
        
        if (isOnlineCoupon) {
        	 validateField(dto.getStudentId(), "studentId", "error.studentId", "message.label.validation.student.id.is.required", bindingResult);
        	 validateField(dto.getName(), "name", "error.name", "message.validation.student.details.name", bindingResult);
        	 validateField(dto.getHostelId(), "hostelId", "error.hostelId", "message.validation.hostel.name.required", bindingResult);
        	 validateField(dto.getRoomNo(), "roomNo", "error.roomNo", "message.validation.room.no.required", bindingResult);
        }else if (category!=null && category
				.equalsIgnoreCase(messageSource.getMessage("message.label.iitmfaculty", null, Locale.getDefault()))) {
        	validateField(dto.getFaculty().getName(), "faculty.name", "error.faculty.name", "message.label.validation.faculty.name.required", bindingResult);
        	validateField(dto.getFaculty().getDeptName(), "faculty.deptName", "error.faculty.deptName", "message.validation.department.required", bindingResult);
        	validateField(dto.getFaculty().getEmail(), "faculty.email", "error.faculty.email", "message.validation.email.id.required", bindingResult);
        	validateField(dto.getFaculty().getProgram(), "faculty.program", "error.faculty.program", "message.validation.program.required", bindingResult);
        }else if (category!=null && category
				.equalsIgnoreCase(messageSource.getMessage("message.coupon.category.projectStaff", null, Locale.getDefault()))) {
        	validateField(dto.getStaff().getName(), "staff.name", "error.staff.name", "message.validation.staff.name.required", bindingResult);
        	validateField(dto.getStaff().getApplicationId(), "staff.applicationId", "error.staff.applicationId", "message.validation.email.application.no.required", bindingResult);
        }else if (category!=null && category
				.equalsIgnoreCase(messageSource.getMessage("message.label.iitmfaculty", null, Locale.getDefault()))) {
        	validateField(dto.getOthers().getName(), "others.name", "error.others.name", "message.validation.name.required", bindingResult);
        	validateField(dto.getOthers().getEmail(), "others.email", "error.others.email", "message.validation.email.id.required", bindingResult);
        	validateField(dto.getOthers().getPurpose(), "others.purpose", "error.others.Purpose", "message.validation.stay.request.purpose.required", bindingResult);
        }
     
        validateField(dto.getMessId(), "messId", "error.messId", "message.validation.mess.name.required", bindingResult);
        validateField(dto.getDiningFrom(), "diningFrom", "error.diningFrom", "message.validation.dining.from.date.required", bindingResult);
        validateField(dto.getDiningTo(), "diningTo", "error.diningTo", "message.validation.dining.to.date.required", bindingResult);

    }
    
    private void validateField(Object fieldValue, String fieldName, String errorCode, String messageKey, BindingResult bindingResult) {
        if (fieldValue == null || (fieldValue instanceof String && ((String) fieldValue).isEmpty())) {
            bindingResult.rejectValue(fieldName, errorCode, messageSource.getMessage(messageKey, null, Locale.getDefault()));
        }
    }

    private void validateDateFields(GuestCouponRequestDTO dto, BindingResult bindingResult) {
        LocalDate today = LocalDate.now();
        LocalDate diningFrom = dto.getDiningFrom();
        LocalDate diningTo = dto.getDiningTo();
        String category = dto.getCategory();
        String vegOrNonVeg = dto.getVegOrNonVeg();
		int vegMaxDays = Integer.valueOf(simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_COUPON_VEG_MAX_DAYS));
		int nonVegMaxDays = Integer.valueOf(simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_COUPON_NONVEG_MAX_DAYS));
		String cutOffTimeStr = simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_COUPON_CUTOFF_TIME);
		boolean isOnlineCoupon= category!=null && (category.equalsIgnoreCase(messageSource.getMessage("message.online.coupon", null, Locale.getDefault()))
				|| category.equalsIgnoreCase(messageSource.getMessage("message.label.hostel.residents", null, Locale.getDefault())));


		// 1. Validate Dining From date
        if (diningFrom != null) {
            if (isOnlineCoupon) {
                if (!diningFrom.isAfter(today)) {
                    bindingResult.rejectValue("diningFrom", "error.diningFrom", "Dining From must be a future date for Online coupon.");
                }
            } else {
                if (diningFrom.isBefore(today)) {
                    bindingResult.rejectValue("diningFrom", "error.diningFrom", messageSource.getMessage("message.guest.coupon.fromDate.error", null, Locale.getDefault()));
                }
            }
        }

		// Should not allow applying for next day, after 8:00 PM of current day
		if (DateUtility.isTomorrowCutoffViolated(diningFrom, cutOffTimeStr)) {
			LocalTime cutoff = DateUtility.parseCutoffTime(cutOffTimeStr);
			String cutoffDisplay = DateUtility.formatCutoffTime(cutoff);
			bindingResult.rejectValue("diningFrom", "error.diningFrom", String.format("Cannot apply for tomorrow's dining after %s today", cutoffDisplay));
		}

        // 2. Validate Dining To date
        if (diningTo != null && diningFrom != null) {
            if (diningTo.isBefore(diningFrom)) {
                bindingResult.rejectValue("diningTo", "error.diningTo",  messageSource.getMessage("message.validation.dining.to.date", null, Locale.getDefault()));
            }

            // 3. If category is 'Online Coupon/Hostel resident', Dining To must be within configurable days from Dining From
			if (isOnlineCoupon) {
				if(vegOrNonVeg!=null && vegOrNonVeg.equalsIgnoreCase(messageSource.getMessage("message.nonveg", null, Locale.getDefault()))){
					if(diningTo.isBefore(diningFrom.plusDays(nonVegMaxDays-1))){
						bindingResult.rejectValue("diningTo", "error.diningTo", "Dining To must be at least "+ nonVegMaxDays +" days from Dining From for NonVeg.");
					}
				}else{ //Veg
					if(diningTo.isAfter(diningFrom.plusDays(vegMaxDays-1))){
						bindingResult.rejectValue("diningTo", "error.diningTo", "Dining To must not be more than "+ vegMaxDays +" days from Dining From.");
					}
				}

				MessMasterControllerDto messPeriodDto = messMasterCommonService.getCurrentMessPeriod();
				LocalDate currentMessPeriodStart = messPeriodDto.getDiningFromDate();
				LocalDate currentMessPeriodEnd = messPeriodDto.getDiningToDate();
				// Validate the dining dates
				if (!isValidDiningPeriod(currentMessPeriodStart, currentMessPeriodEnd, diningFrom, diningTo)) {
					bindingResult.rejectValue("diningFrom", "error.diningFrom", "Dining period exceeds the current mess period");
				}
			}

        }
    }

	public static boolean isValidDiningPeriod(LocalDate messStart, LocalDate messEnd, LocalDate diningFrom, LocalDate diningTo) {
		LocalDate nextDayAfterMessPeriod = messEnd.plusDays(1);
		LocalDate today = LocalDate.now();

		// Check if today's date is Oct 10, and if next dining dates are starts from Oct 11
		if (today.equals(messEnd) && diningFrom.equals(nextDayAfterMessPeriod) && diningTo.equals(diningFrom)) {
			return true;  // Allow Oct 11 dining if today is Oct 10
		}

		// Check if diningFrom and diningTo are within the allowed mess period
		if (!diningFrom.isBefore(messStart) && !diningTo.isAfter(messEnd)) {
			return true;
		}

		return false;
	}

	private void validateMess(GuestCouponRequestDTO dto, BindingResult bindingResult) {
		Long messId = dto.getMessId();
		String category = dto.getCategory();
		boolean isOnlineCoupon= category!=null && (category.equalsIgnoreCase(messageSource.getMessage("message.online.coupon", null, Locale.getDefault()))
				|| category.equalsIgnoreCase(messageSource.getMessage("message.label.hostel.residents", null, Locale.getDefault())));

		//check valid mess id
		if (messId!=null) {
			if (!messMasterService.checkMessIdValidOrNot(messId)) {
				bindingResult.rejectValue("messId", "error.messId", messageSource.getMessage("message.validation.mess.id", null, Locale.getDefault()));
			}
		}

		//Validate if already allotted for mess
		if (isOnlineCoupon) {
			GuestCouponRequestDTO returnDto = requestService.checkStudentAllottedMessPeriod(dto.getStudentId(),dto.getDiningFrom(),dto.getDiningTo()); // Service call to check mess allotment
			if (returnDto != null) {
				bindingResult.rejectValue("diningFrom", "error.diningFrom", "Already allotted for Mess during this period ("+returnDto.getDiningFrom()+" - "+returnDto.getDiningTo()+")");
			}
		}
	}

    private void validateSessionList(GuestCouponRequestDTO dto, BindingResult bindingResult) {
        LocalDate today = LocalDate.now();
        LocalDate diningFrom = dto.getDiningFrom();
        LocalDate diningTo = dto.getDiningTo();
        String category = dto.getCategory();
        String vegOrNonVeg = dto.getVegOrNonVeg();
        String studentId=dto.getStudentId();
        Long messId=dto.getMessId();
        List<DailyCouponRequestDTO> foodFrequency = dto.getFoodFrequency();
		boolean isOnlineCoupon= category!=null && (category.equalsIgnoreCase(messageSource.getMessage("message.online.coupon", null, Locale.getDefault()))
				|| category.equalsIgnoreCase(messageSource.getMessage("message.label.hostel.residents", null, Locale.getDefault())));

        if (foodFrequency == null || foodFrequency.isEmpty()) {
            bindingResult.rejectValue("messId", "error.foodFrequency", "Food frequency list cannot be empty.");
        }

        Set<LocalDate> uniqueDates = new HashSet<>();
        boolean atLeastOneMealSelected = false;
        if(foodFrequency!=null && foodFrequency.size()>0) {
	        for (DailyCouponRequestDTO request : foodFrequency) {
	            LocalDate date = request.getDate();
				boolean havingBreakfast = request.getHavingBreakfast() != null && request.getHavingBreakfast();
				boolean havingLunch = request.getHavingLunch() != null && request.getHavingLunch();
				boolean havingDinner = request.getHavingDinner() != null && request.getHavingDinner();
				boolean havingSnacks = request.getHavingSnacks() != null && request.getHavingSnacks();

				boolean allSessionsSelected = havingBreakfast && havingLunch && havingDinner && havingSnacks;
                boolean hasAnyMeal =  havingBreakfast || havingLunch || havingDinner || havingSnacks;

	            // 1. Validate each date in foodFrequency list
	            if (category!=null && category.equalsIgnoreCase(messageSource.getMessage("message.online.coupon", null, Locale.getDefault()))) {
	                if (!date.isAfter(today)) {
	                    bindingResult.rejectValue("diningFrom", "error.foodFrequency", "Each date in foodFrequency must be a future date for Online coupon.");
	                }
	            } else {
	                if (date.isBefore(today)) {
	                    bindingResult.rejectValue("diningFrom", "error.foodFrequency", "Each date in foodFrequency must not be a past date.");
	                }
	            }
	
	            // 2. Validate date range
	            if (date.isBefore(diningFrom) || date.isAfter(diningTo)) {
	                bindingResult.rejectValue("diningFrom", "error.foodFrequency", "Each date must be within the Dining From and Dining To range.");
	            }
	
	            // 3. Ensure dates are not repeated
	            if (!uniqueDates.add(date)) {
	                bindingResult.rejectValue("diningFrom", "error.foodFrequency", "Duplicate dates found in foodFrequency list.");
	            }
	
	            // 4. Meal selection validation
	            if (category!=null && category.equalsIgnoreCase(messageSource.getMessage("message.online.coupon", null, Locale.getDefault()))
	    				&& vegOrNonVeg.equalsIgnoreCase(messageSource.getMessage("message.nonveg", null, Locale.getDefault()))) {
	                if (!allSessionsSelected) {
	                    bindingResult.rejectValue("messId", "error.foodFrequency", "All sessions must be selected for each date for NonVeg Online coupon.");
	                }
	            } else {
	                atLeastOneMealSelected |= hasAnyMeal;
	            }
	
	            if (isOnlineCoupon) {
		            // 5. Check if request is already made for the same date and session
		            if (request.getHavingBreakfast() && requestService.checkSameDateAndSession(studentId,date, CommonEnum.BF.toString())) {
		                bindingResult.rejectValue("diningTo", "error.foodFrequency", "Request already made for breakfast on " + date);
		            }
		            if (request.getHavingLunch() && requestService.checkSameDateAndSession(studentId,date, CommonEnum.LC.toString())) {
		                bindingResult.rejectValue("diningTo", "error.foodFrequency", "Request already made for lunch on " + date);
		            }
		            if (request.getHavingDinner() && requestService.checkSameDateAndSession(studentId,date, CommonEnum.DR.toString())) {
		                bindingResult.rejectValue("diningTo", "error.foodFrequency", "Request already made for dinner on " + date);
		            }
	            }
	            
	            // 6. Check available mess capacity for each session
	            if(!dto.isBulkCoupon()) {
		            if (request.getHavingBreakfast() && requestService.checkMessAvailability(messId,date,CommonEnum.BF.toString()) <= 0) {
		                bindingResult.rejectValue("messId", "error.foodFrequency", "No available capacity for breakfast on " + date);
		            }
		            if (request.getHavingLunch() && requestService.checkMessAvailability(messId,date,CommonEnum.LC.toString()) <=0) {
		                bindingResult.rejectValue("messId", "error.foodFrequency", "No available capacity for lunch on " + date);
		            }
		            if (request.getHavingDinner() && requestService.checkMessAvailability(messId,date,CommonEnum.DR.toString()) <= 0) {
		                bindingResult.rejectValue("messId", "error.foodFrequency", "No available capacity for dinner on " + date);
		            }
	            }else {
	            	if (request.getNoOfBreakfast()!=null && request.getNoOfBreakfast()>0) {
	            		int availCount=requestService.checkMessAvailability(messId,date,CommonEnum.BF.toString());
	            		if(availCount<=0) {
	            			bindingResult.rejectValue("messId", "error.foodFrequency", "No available capacity for breakfast on " + date);
	            		}else if(request.getNoOfBreakfast() > availCount) {
	            			bindingResult.rejectValue("messId", "error.foodFrequency", "Available capacity for the selected Mess and Session " +CommonEnum.BF.toString()+" is "+availCount+" only");
	            		}
		            }
		            if (request.getNoOfLunch()!=null && request.getNoOfLunch()>0 && requestService.checkMessAvailability(messId,date,CommonEnum.LC.toString()) <=0) {
		            	int availCount=requestService.checkMessAvailability(messId,date,CommonEnum.LC.toString());
	            		if(availCount<=0) {
	            			bindingResult.rejectValue("messId", "error.foodFrequency", "No available capacity for lunch on " + date);
	            		}else if(request.getNoOfBreakfast() > availCount) {
	            			bindingResult.rejectValue("messId", "error.foodFrequency", "Available capacity for the selected Mess and Session " +CommonEnum.LC.toString()+" is "+availCount+" only");
	            		}
		            }
		            if (request.getNoOfDinner()!=null && request.getNoOfDinner()>0 && requestService.checkMessAvailability(messId,date,CommonEnum.DR.toString()) <= 0) {
		            	int availCount=requestService.checkMessAvailability(messId,date,CommonEnum.DR.toString());
	            		if(availCount<=0) {
	            			bindingResult.rejectValue("messId", "error.foodFrequency", "No available capacity for dinner on " + date);
	            		}else if(request.getNoOfBreakfast() > availCount) {
	            			bindingResult.rejectValue("messId", "error.foodFrequency", "Available capacity for the selected Mess and Session " +CommonEnum.DR.toString()+" is "+availCount+" only");
	            		}
		            }
	            }
	        }
    	}
        // Ensure at least one session is selected in the whole list (if not NonVeg - Online coupon)
        if ((category!=null && !category.equalsIgnoreCase(messageSource.getMessage("message.online.coupon", null, Locale.getDefault())))
				&& (vegOrNonVeg!=null && !vegOrNonVeg.equalsIgnoreCase(messageSource.getMessage("message.nonveg", null, Locale.getDefault())))
                &&!dto.isBulkCoupon()) {
            if (!atLeastOneMealSelected) {
                bindingResult.rejectValue("messId", "error.foodFrequency", "At least one session must be selected in the entire list.");
            }
        }
    }

}

