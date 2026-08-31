package com.iitm.hosteldine.controller.dashboard;

import java.util.List;

import com.iitm.hosteldine.service.UserFpCardService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.form.hostel.RoomInventoryForm;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.service.hostel.HostelRoomAllotmentService;
import com.iitm.hosteldine.util.HTMLPage;

import com.iitm.hosteldine.service.hostel.ShowEventMasterService;
import com.iitm.hosteldine.service.student.StudentRollnoChangeService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.student.dashboard}")
public class StudentDashboardController {

	private final StudentBioDataService studentBioDataService;
	private final HostelRoomAllotmentService hostelRoomAllotmentService;
	private final CommonResponseUtil commonResponseUtil;
	private final ShowEventMasterService showEventMasterService;

	@Value("${url.student.dashboard}")
	private String getStudentDashboard;

	@GetMapping
	public String getStudentDashboard(ModelMap map, HttpServletRequest request) throws Exception {
		map.addAttribute("eventList",showEventMasterService.getActiveEventListByDate());
		commonResponseUtil.updateCommonModelAttributes(map, request);
		return HTMLPage.STUDENT_DASHBOARD;
	}
}
	 