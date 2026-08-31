package com.iitm.hosteldine.service.hostel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dean.DeanConstants;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.hostel.HostelRoomAllotmentLogsDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.repository.hostel.HostelRoomAllotmentRepository;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.validator.common.ValidationCommon;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HostelRoomAllotmentLogsService {
	
	private final HostelRoomAllotmentRepository hostelRoomAllotmentRepository;
	private final Utility utility;
	
	public Page<HostelRoomAllotmentLogsDto> getRoomAllotmentList(PaginationForm form) {
		Page<Object[]> result = fetchRoomAllotmentLogsList(form);
		return setRoomAllotmentListValues(result);
	}
	
	private Page<Object[]> fetchRoomAllotmentLogsList(PaginationForm form) {
		int hostelId = ValidationCommon.toIntegerOrZero(form.getAdditionalParam().get(DeanConstants.HOSTEL_NAME.getConstants()));
		LocalDate fromdate = ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("allotedFromDate"));
		LocalDate toDate = ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("allotedToDate"));
		String userName = ValidationCommon.toString(form.getAdditionalParam().get("userName"));
		String allocationType = ValidationCommon.toString(form.getAdditionalParam().get("allocationType"));
		String studentId = ValidationCommon.toString(form.getAdditionalParam().get("studentId"));
		String email = ValidationCommon.toString(form.getAdditionalParam().get("email"));
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());
		Page<Object[]> result = Page.empty();

		result = hostelRoomAllotmentRepository.getRoomAllotmentLogsList(hostelId, fromdate, toDate, userName, allocationType, studentId, email, pageable);
		
		return result;
	}
	
	private Page<HostelRoomAllotmentLogsDto> setRoomAllotmentListValues(Page<Object[]> result) {
		return result.map(objects -> {
			HostelRoomAllotmentLogsDto dto = new HostelRoomAllotmentLogsDto();			
			dto.setStudentType(ValidationCommon.toString(objects[0]));
			dto.setAllocationType(ValidationCommon.toString(objects[1]));
			dto.setStudentId(ValidationCommon.toString(objects[2]));
			dto.setStudentName(ValidationCommon.toString(objects[3]));
			dto.setEmail(ValidationCommon.toString(objects[4]));
			dto.setPreviousHostelName(ValidationCommon.toString(objects[5]));
			dto.setCurrentHostelName(ValidationCommon.toString(objects[6]));
			dto.setPreviousRoomNo(utility.parseInt(objects[7]));
			dto.setCurrentRoomNo(utility.parseInt(objects[8]));
			dto.setCreatedBy(ValidationCommon.toString(objects[9]));
			dto.setAllotmentDate(ValidationCommon.formatDate(objects[10], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
			dto.setShiftedDate(ValidationCommon.formatDate(objects[11], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
			dto.setVacateDate(ValidationCommon.formatDate(objects[12], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
			dto.setStayFrom(ValidationCommon.formatDate(objects[13], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
			dto.setStayTo(ValidationCommon.formatDate(objects[14], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
			
			if(ModelConstants.STUDENT_LOGIN_TYPE.equalsIgnoreCase(dto.getStudentType()) && ModelConstants.NON_VACATION.equals(dto.getAllocationType())) {
				dto.setType(dto.getStudentType() + " " + ModelConstants.REG);
			} else if(ModelConstants.STUDENT_LOGIN_TYPE.equalsIgnoreCase(dto.getStudentType()) && ModelConstants.VACATION.equals(dto.getAllocationType())) {
				dto.setType(dto.getStudentType() + " " + ModelConstants.VAC);
			} else {
				dto.setType(dto.getStudentType());
			}
			
//			if(ModelConstants.STUDENT_LOGIN_TYPE.equalsIgnoreCase(dto.getStudentType())){
//				if(objects[12] != null) {
//					dto.setStayTo(ValidationCommon.formatDate(objects[12], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
//				} else {
//					dto.setStayTo(ValidationCommon.formatDate(objects[11], DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)));
//				}
//			}
			return dto;
		});
	}
	
	public List<Map<String, Object>> getHostelName(List<HostelMasterDto> hostelList) {
    	List<Map<String, Object>> dropdownList = new ArrayList<>();        
        for (HostelMasterDto hostelMasterDto : hostelList) {
            if (hostelMasterDto != null) {
                Map<String, Object> dropdownItem = new HashMap<>();
                dropdownItem.put("id", hostelMasterDto.getId());
                dropdownItem.put("hostelName", hostelMasterDto.getHostelName());
                dropdownList.add(dropdownItem);
            }
        }        
        return dropdownList;
    }
	
	public String[] getUserList() {
		List<String> usersList = hostelRoomAllotmentRepository.getUsersList();
		return usersList.toArray(new String[0]);
	}
	
}
