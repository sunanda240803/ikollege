package com.iitm.hosteldine.controller.dashboard.student;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.service.hostel.GuestCouponRequestService;
import com.iitm.hosteldine.util.HTMLPage;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OnlineMessCouponWidgetController {
		
    private final GuestCouponRequestService service;
    private final MessageSource messageSource;
    
	@GetMapping("${url.hostel.guest.coupon.request}" + "${url.widget}")
	public String getWidgetInfoForOnlineMessCoupon(ModelMap map, HttpServletRequest request) throws Exception {
		String studentId = SecurityCtxUtil.userId().toUpperCase();
		map.addAttribute("onlineMessCoupon", service.getCouponPaymentEntityForStudent(studentId,
				messageSource.getMessage("message.online.coupon", null, Locale.getDefault())));
		return HTMLPage.ONLINE_MESS_COUPON_WIDGET;
	}
	
}
