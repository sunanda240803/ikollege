package com.iitm.hosteldine.service.staff;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.entity.UserManagementEntity;
import com.iitm.hosteldine.model.staff.StaffDetailsEntity;
import com.iitm.hosteldine.util.MD5Encryption;
import com.iitm.hosteldine.util.PasswordGenerator;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.UserManagementDto;
import com.iitm.hosteldine.dto.staff.StaffDetailsDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.UserManagementMapper;
import com.iitm.hosteldine.repository.UserManagementRepository;
import com.iitm.hosteldine.repository.hostel.HostelUserMappingRepository;
import com.iitm.hosteldine.repository.staff.StaffDetailsRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffSearchService {
	private final StaffDetailsRepository staffDetailsRepository;
	private final UserManagementRepository userManagementRepo;
	private final HostelUserMappingRepository mappingRepo;
	private final MessageSource messageSource;
	private final CommonResponseUtil commonResponseUtil;

	public List<StaffDetailsDto> getStaffList() {
	    List<Object[]> results = staffDetailsRepository.findStaffDetailsByActiveFlag(ModelConstants.STATUS_ACTIVE);
	    return results.stream()
	        .map(row -> {
	            StaffDetailsDto staffDetails = new StaffDetailsDto();
	            staffDetails.setFacultyId((String) row[0]);
	            staffDetails.setFirstName((String) row[1]);
	            staffDetails.setDesignation((String) row[2]);
	            staffDetails.setContactNumber((Long) row[3]);
	            staffDetails.setEmailAddress((String) row[4]);
	            staffDetails.setUserName((String) row[5]);
	            return staffDetails;
	        })
	        .collect(Collectors.toList());
	}

	@Transactional
	public boolean deleteStaffById(String facultyId) throws RecordNotExistsException {
		UserManagementDto userManagementDto = userManagementRepo.findByIdUserIdIgnoreCase(facultyId).map(UserManagementMapper.INSTANCE::fromUserManagementEntity).orElse(null);
		if (Objects.nonNull(userManagementDto)) {
			if (mappingRepo.existsByActiveFlagAndIdUser(ModelConstants.STATUS_ACTIVE, userManagementDto.getUserName())) {
				throw new RecordNotExistsException(messageSource.getMessage(
						"message.validation.error.hostel.user.mapping.assigned", null, Locale.getDefault()));
			}
			userManagementRepo.findByIdUserIdIgnoreCase(facultyId).ifPresent(userEntity -> {
				userEntity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
				userManagementRepo.saveAndFlush(userEntity);
			});
			StaffDetailsEntity entity = staffDetailsRepository.findByFacultyId(facultyId)
					.orElseThrow(() -> new RecordNotExistsException(commonResponseUtil.getMessage("message.validation.error.id.not.found")));
			entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
			staffDetailsRepository.saveAndFlush(entity);
			return true;
		} else {
			return false;
		}
	}

	public Page<StaffDetailsDto> getStaffList(PaginationForm form) {
		Page<Object[]> result = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("fpd.faculty_id").ascending());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			result = staffDetailsRepository.findStaffDetailsByActiveFlag(ModelConstants.STATUS_ACTIVE, pageable);
		} else {
			result = staffDetailsRepository.findByStaffDetailsSearchContainingAndActiveFlag(form.getSearch(),
					ModelConstants.STATUS_ACTIVE, pageable);
		}
		return result.map(row -> {
			StaffDetailsDto staffDetails = new StaffDetailsDto();
			staffDetails.setFacultyId((String) row[0]);
			staffDetails.setFirstName((String) row[1]);
			staffDetails.setDesignation((String) row[2]);
			staffDetails.setContactNumber((Long) row[3]);
			staffDetails.setEmailAddress((String) row[4]);
			staffDetails.setUserName((String) row[5]);
			String roles = row[6]!=null ? String.valueOf(row[6]) : "";
			if (row[7]!=null) {
				roles += !roles.isEmpty() ? ", " : "";
				roles += String.valueOf(row[7]);
			}
            staffDetails.setRoleName(roles);
			return staffDetails;
		});

	}

    public String updateStaffCreds(String staffId) {
		UserManagementEntity userManagementEntity = userManagementRepo.findByIdUserIdIgnoreCase(staffId).orElse(null);
		if (Objects.nonNull(userManagementEntity)) {
			String newPassword = PasswordGenerator.generatePassword(8);
			userManagementEntity.setPassword(MD5Encryption.md5Encrypt(newPassword));
			userManagementEntity.setPasswordModifiedTime(LocalDateTime.now());
			userManagementEntity.setModifiedBy(SecurityCtxUtil.userId());
			userManagementEntity.setModifiedAt(LocalDateTime.now());
			userManagementRepo.save(userManagementEntity);
			return newPassword;
		}
        return null;
    }
}

