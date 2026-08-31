package com.iitm.hosteldine.controller.dashboard.student;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.service.student.SickFoodService;
import com.iitm.hosteldine.service.student.StudentRollnoChangeService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.dashboard.RollNumberValidator;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SickFoodRequestWidgetController {
	
	private final SickFoodService sickFoodService;
	
	
	@GetMapping("${url.sickFoodRequest.widget}")
	public String getSickFoodRequestList(ModelMap map, HttpServletRequest request) throws Exception {
	    String studentId = SecurityCtxUtil.userId().toUpperCase(); 
	    
	    Map<String, String> sickFoodRequest = sickFoodService.getTodayFoodRequests(studentId);
	    
	    map.addAttribute("sickFoodRequest", sickFoodRequest);
	    
	    return HTMLPage.SICK_FOOD_REQUEST_WIDGET;
	}
	
}
