package com.iitm.hosteldine.controller.staff;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.RoleDto;
import com.iitm.hosteldine.dto.staff.StaffDetailsDto;
import com.iitm.hosteldine.model.staff.StaffDetailsEntity;
import com.iitm.hosteldine.service.CommonService;
import com.iitm.hosteldine.service.collegeInfo.DepartmentService;
import com.iitm.hosteldine.service.staff.DesignationMasterService;
import com.iitm.hosteldine.service.staff.StaffDetailsService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.staff.details}")
public class StaffDetailsController {
	private final CommonResponseUtil commonResponseUtil;
	private final StaffDetailsService staffDetailsService;
	private final DepartmentService departmentService;
	private final DesignationMasterService designationMasterService;
	private final CommonService commonService;

	@Value("${url.staff.details}")
	private String getStaffDetails;
	@Value("${url.staff.search}")
	private String staffSearch;

	@GetMapping("${id}")
	public String getStaffDetails(@PathVariable String id, ModelMap map, HttpServletRequest request)
			throws Exception {
		StaffDetailsDto staffDetailsDto;
		if (!id.equals("new")) {
			staffDetailsDto = staffDetailsService.getStaffDetails(id);
		} else {
			staffDetailsDto = new StaffDetailsDto();
			long employeeId = staffDetailsService.getNewEmployeeId();
			String formattedEmployeeId = "ST" + String.format("%05d", employeeId);
			staffDetailsDto.setNewFacultyId(formattedEmployeeId);
		}
		List<RoleDto> roleList = commonService.getRoleslist();
		map.addAttribute("staffDetailsDto", staffDetailsDto);
		map.addAttribute("rolesList", roleList);
		map.addAttribute("departmentNameList", departmentService.getDepartmentNameList());
		map.addAttribute("designationList", designationMasterService.getDesignationList());
		
		commonResponseUtil.updateCommonModelAttributes(map, request);
		return HTMLPage.STAFF_DETAILS;
	}
	
	@PostMapping
	public String saveUpdateStaffDetails(@ModelAttribute StaffDetailsDto staffDetailsDto, HttpServletRequest request,
			RedirectAttributes redirectAttrs) throws Exception {
		String facultyId = staffDetailsDto.getFacultyId();
		StaffDetailsEntity saveStatus = staffDetailsService.saveUpdateStaffDetails(staffDetailsDto);
		if (saveStatus != null) {
			String status = facultyId.equals(saveStatus.getFacultyId()) ? Constants.UPDATED : Constants.SAVED;
			commonResponseUtil.updateSaveResponseByStatus(status, redirectAttrs);
			return Constants.REDIRECT + staffSearch;
		}
		return Constants.REDIRECT + getStaffDetails + "/" + facultyId;
	}

//    @GetMapping("/getUsername/{employeeId}/{firstName}/{lastName}")
//    public @ResponseBody String getUsername(@PathVariable Long employeeId, @PathVariable String firstName, @PathVariable String lastName) {
//        return staffDetailsService.getUsername(employeeId, firstName, lastName);
//    }
    
    @GetMapping("${url.username}" + "${firstName}" + "${lastName}" + "${accountType}" + "${userId}")
    public @ResponseBody String getUserName(@PathVariable String firstName, @PathVariable String lastName,
			@PathVariable String accountType, @PathVariable String userId, HttpServletRequest request) {
        return commonService.getUserName(firstName, lastName, accountType, userId);
    }
}
