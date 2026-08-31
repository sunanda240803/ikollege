package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.HostelRoomAllotmentInfoDto;
import com.iitm.hosteldine.dto.studentDashboard.GuestAccommodationRequestDto;
import com.iitm.hosteldine.form.hostel.RoomInventoryForm;
import com.iitm.hosteldine.mapper.hostel.HostelRoomAllotmentInfoMapper;
import com.iitm.hosteldine.repository.hostel.HostelRoomAllotmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HostelRoomAllotmentService {
	private final HostelRoomAllotmentRepository hostelRoomAllotmentRepository;
    
	
  	public List<RoomInventoryForm> getInventoryDetailsStuid(String studentId) {
  	    List<Object[]> results = hostelRoomAllotmentRepository.viewInventory(ModelConstants.STATUS_ACTIVE, studentId);
  	    
  	    return results.stream()
  	        .map(record -> {
  	            RoomInventoryForm form = new RoomInventoryForm();
  	            form.setAssetCode((String) record[0]); // assetCode
  	            form.setAssetName((String) record[1]); // assetName
  	            form.setAssetConditionStatus((String) record[2]); // assetConditionStatus
  	            form.setAssetCategory((String) record[3]);
  	            return form;
  	        })
  	        .collect(Collectors.toList());
  	}


	public GuestAccommodationRequestDto getHostelDetailsByStudentId(String studentId) {
		List<Object[]> results = hostelRoomAllotmentRepository.getHostelDetailsByStudentId(ModelConstants.STATUS_ACTIVE,
				studentId);
		return results.stream().findFirst().
				map(record -> {
			GuestAccommodationRequestDto form = new GuestAccommodationRequestDto();
			form.setStudentId((String) record[0]);
			form.setRoomNo((String) record[1]);
			form.setStudentName((String) record[2]);
			form.setFloorName((String) record[3]);
			form.setGender((String) record[4]);
			form.setHostelName((String) record[5]);
			form.setHostelId((Long) record[6]);
			return form;
		}).orElse(new GuestAccommodationRequestDto());
	}

	public HostelRoomAllotmentInfoDto getAllotmentDetailsByStudentId(String studentId) {
		  return hostelRoomAllotmentRepository.findByStudentIdEqualsIgnoreCase(studentId)
				  .map(HostelRoomAllotmentInfoMapper.INSTANCE::fromHostelRoomAllotmentInfoEntity)
				  .orElse(null);
	}

}
