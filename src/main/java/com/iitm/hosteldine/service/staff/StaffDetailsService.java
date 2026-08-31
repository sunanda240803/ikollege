package com.iitm.hosteldine.service.staff;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.UserManagementDto;
import com.iitm.hosteldine.dto.staff.StaffDetailsDto;
import com.iitm.hosteldine.mapper.staff.StaffDetailsMapper;
import com.iitm.hosteldine.model.staff.StaffDetailsEntity;
import com.iitm.hosteldine.repository.staff.StaffDetailsRepository;
import com.iitm.hosteldine.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class StaffDetailsService {
    private final StaffDetailsRepository staffDetailsRepository;
    private final UserManagementService userManagementService;

    public long getNewEmployeeId() {
        return staffDetailsRepository.getNextEmployeeId();
    }

    @Transactional
    public StaffDetailsEntity saveUpdateStaffDetails(StaffDetailsDto staffDetailsDto) {
        Optional<StaffDetailsEntity> optionalStaffDetails = staffDetailsRepository.findByFacultyIdAndActiveFlag(staffDetailsDto.getFacultyId(),
                ModelConstants.STATUS_ACTIVE);
        StaffDetailsEntity staffEntity;
        if (optionalStaffDetails.isPresent()) {
            staffEntity = optionalStaffDetails.get();
            StaffDetailsMapper.INSTANCE.onUpdateStaffDetailsEntity(staffEntity, staffDetailsDto);
        } else {
            long employeeId = getNewEmployeeId();
            String formattedEmployeeId = "ST" + String.format("%05d", employeeId);
            if (staffDetailsDto.getNewFacultyId().isEmpty()) {
                staffDetailsDto.setFacultyId(formattedEmployeeId);
            } else {
                staffDetailsDto.setFacultyId(staffDetailsDto.getNewFacultyId());
            }
            staffDetailsDto.setEmployeeId(employeeId);
            staffEntity = StaffDetailsMapper.INSTANCE.onSaveEntity(staffDetailsDto);
        }
        staffDetailsRepository.save(staffEntity);
        /*if (!staffEntity.getFacultyId().equals(staffDetailsDto.getNewFacultyId())) {
            staffDetailsRepository.updateFacultyId(staffEntity.getFacultyId(), staffDetailsDto.getUserName());
        }*/
        boolean createUpdateSuccess = userManagementService.createUpdateStaff(staffDetailsDto);
        if (createUpdateSuccess) {
            log.debug("Account created / updated successfully for {}", staffDetailsDto.getFacultyId());
        }
        return staffEntity;
    }

    public StaffDetailsDto getStaffDetails(String facultyId) {
        StaffDetailsDto staffDetailsDto = staffDetailsRepository.findByFacultyIdAndActiveFlag(facultyId, ModelConstants.STATUS_ACTIVE)
                .map(StaffDetailsMapper.INSTANCE::fromStaffDetailsEntity).orElse(null);
        if (staffDetailsDto != null) {
            UserManagementDto userManagementDto = userManagementService.getUserByFacultyId(staffDetailsDto.getFacultyId());
            if (userManagementDto != null) {
                if (ModelConstants.STATUS_ACTIVE.equals(userManagementDto.getActiveFlag())) {
                    staffDetailsDto.setAccountCreationStatus("on");
                }
				staffDetailsDto.setUserName(userManagementDto.getUserName());
				staffDetailsDto.setRoleId(userManagementDto.getRoleId() != null ? Long.valueOf(userManagementDto.getRoleId()) : 0);
				staffDetailsDto.setSecondaryRoleId(userManagementDto.getSecondaryRoleId() != null ? Long.valueOf(userManagementDto.getSecondaryRoleId()) : 0);
            }
        }
        return staffDetailsDto;
    }
}
