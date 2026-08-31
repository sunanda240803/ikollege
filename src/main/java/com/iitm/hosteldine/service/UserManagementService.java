package com.iitm.hosteldine.service;

import com.iitm.hosteldine.config.MyAuthenticationProvider;
import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.LoginDto;
import com.iitm.hosteldine.dto.MyUserDetails;
import com.iitm.hosteldine.dto.UserManagementDto;
import com.iitm.hosteldine.dto.staff.StaffDetailsDto;
import com.iitm.hosteldine.entity.RoleEntity;
import com.iitm.hosteldine.entity.UserManagementEntity;
import com.iitm.hosteldine.entity.UserManagementId;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.mapper.UserManagementMapper;
import com.iitm.hosteldine.repository.RoleRepository;
import com.iitm.hosteldine.repository.UserManagementRepository;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.MD5Encryption;
import com.iitm.hosteldine.util.PasswordGenerator;
import com.iitm.hosteldine.util.RoleEnum;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserManagementRepository userManagementRepository;
    private final MyAuthenticationProvider myAuthenticationProvider;
    private final MailTemplateRepository mailTemplateRepository;
    private final MailQueueService mailQueueService;
    private final CommonResponseUtil commonResponseUtil;
    private final RoleRepository roleRepository;

    public List<UserManagementDto> getUserList() {
        return userManagementRepository.findAllByActiveFlagOrderByModifiedAtDesc(ModelConstants.STATUS_ACTIVE).stream()
                .map(UserManagementMapper.INSTANCE::fromUserManagementEntity).collect(Collectors.toList());
    }

    public MyUserDetails validateCredentials(LoginDto loginDto) throws Exception {
        return myAuthenticationProvider.setUserDetails(MCrypt.getInstance().decryptToString
                (loginDto.getLoginType()), loginDto.getUserName(), loginDto.getPassword());
    }

    public UserManagementDto getUserByFacultyId(String facultyId) {
        return userManagementRepository.findByIdUserIdIgnoreCase(facultyId).map(UserManagementMapper.INSTANCE::fromUserManagementEntity).orElse(null);
    }

    @Transactional
    public boolean createUpdateStaff(StaffDetailsDto staffDetailsDto) {
        String newPassword;
        boolean createUpdateAccount = staffDetailsDto.getAccountCreationStatus() != null && staffDetailsDto.getAccountCreationStatus().equals("on");
        RoleEntity roleId = roleRepository.findByRoleIdAndActiveFlag(staffDetailsDto.getRoleId(), ModelConstants.STATUS_ACTIVE).orElse(new RoleEntity());
        RoleEntity secondaryRoleId = new RoleEntity();
        String roleName = roleId.getRoleName();
        boolean isUserFaculty = roleName != null && RoleEnum.FACULTY.getValue().equalsIgnoreCase(roleName.trim());
        if (!userManagementRepository.existsByIdUserIdIgnoreCase(staffDetailsDto.getFacultyId())) {
            if (createUpdateAccount) {
                UserManagementEntity userManagementEntity = new UserManagementEntity();
                UserManagementId userManagementId = new UserManagementId();
                roleId.setRoleId(staffDetailsDto.getRoleId());
                if (staffDetailsDto.getSecondaryRoleId() != null && staffDetailsDto.getSecondaryRoleId() > 0L) {
                    secondaryRoleId.setRoleId(staffDetailsDto.getSecondaryRoleId());
                    userManagementEntity.setRoleSecondary(secondaryRoleId);
                }
                userManagementEntity.setRole(roleId);
                userManagementId.setUserId(staffDetailsDto.getFacultyId().toUpperCase());
                userManagementId.setUsername(staffDetailsDto.getUserName().toLowerCase());
                userManagementEntity.setEmail(staffDetailsDto.getEmailAddress());
                userManagementEntity.setId(userManagementId);
                userManagementEntity.setLastLoginTime(LocalDateTime.now());
                userManagementEntity.setAccountType(ModelConstants.FACULTY_LOGIN_TYPE);
                userManagementEntity.setNoFailedAttempts(0);
                userManagementEntity.setEmployeeId(staffDetailsDto.getEmployeeId());
                newPassword = PasswordGenerator.generatePassword(8);
                userManagementEntity.setAuthenticationServer(isUserFaculty ? ModelConstants.LDAP_AUTH : ModelConstants.APP_AUTH);
                userManagementEntity.setPassword(MD5Encryption.md5Encrypt(newPassword));
                userManagementEntity.onCreate();
                userManagementRepository.save(userManagementEntity);
                if (!isUserFaculty){
                    saveMail(newPassword, staffDetailsDto);
                }
            }
        } else {
            userManagementRepository.findByIdUserIdIgnoreCase(staffDetailsDto.getFacultyId())
                    .map(userEntity -> {
                                if (createUpdateAccount) {
									if (userEntity.getRole() == null || !Objects.equals(userEntity.getRole().getRoleId(), staffDetailsDto.getRoleId())) {
	                                    roleId.setRoleId(staffDetailsDto.getRoleId());
	                                    userEntity.setRole(roleId);
									}
                                    if (!Objects.equals(userEntity.getRoleSecondary() != null ? userEntity.getRoleSecondary().getRoleId() : null, staffDetailsDto.getSecondaryRoleId())) {

                                        if (staffDetailsDto.getSecondaryRoleId() == null) {
                                            // User has removed the secondary role
                                            userEntity.setRoleSecondary(null);
                                        } else {
                                            RoleEntity secondaryRole = roleRepository.findByRoleIdAndActiveFlag(staffDetailsDto.getSecondaryRoleId(),
                                                            ModelConstants.STATUS_ACTIVE)
                                                    .orElseThrow(() -> new RuntimeException("Secondary role not found: " + staffDetailsDto.getSecondaryRoleId()));
                                            userEntity.setRoleSecondary(secondaryRole);
                                        }
                                    }
                                    userEntity.setActiveFlag(ModelConstants.STATUS_ACTIVE);
                                } else {
                                    userEntity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
                                }
                                userManagementRepository.save(userEntity);
                                return userEntity;
                            }
                    );
        }
        return true;
    }

    public UserManagementDto getUserByUserName(){
        return userManagementRepository.findByIdUsernameIgnoreCaseAndActiveFlag(SecurityCtxUtil.userName(),ModelConstants.STATUS_ACTIVE)
                .map(UserManagementMapper.INSTANCE::fromUserManagementEntity).orElse(null);
    }

    public boolean getUserByUserName(String userName){
        return userManagementRepository.existsByIdUsernameAndActiveFlag(userName,ModelConstants.STATUS_ACTIVE);
    }

    private void saveMail(String newPassword, StaffDetailsDto staffDetailsDto) {
        Optional<MailTemplateEntity> candidateResetPasswordMailTemplate = mailTemplateRepository
                .findByMailType(MailTemplateEntity.NEW_STAFF_CREDENTIALS);
        String mailTemplate = ModelConstants.EMPTY_STRING;
        if (candidateResetPasswordMailTemplate.isPresent()) {
            mailTemplate = candidateResetPasswordMailTemplate.get().getMailTemplate();
            mailTemplate = mailTemplate.replaceAll("#%userName%#", staffDetailsDto.getUserName().toLowerCase());
            mailTemplate = mailTemplate.replaceAll("#%password%#", newPassword);
        }
        try {
            mailQueueService.saveMailQueue(
                    commonResponseUtil.getMessage("message.validate.new.staff.account.credentials"),
                    commonResponseUtil.getMessage("message.mail.greetings.for.faculty"), mailTemplate, staffDetailsDto.getEmailAddress(),
                    commonResponseUtil.getMessage("message.validate.account.credentials"), SecurityCtxUtil.userName(), null,null, null,
                    ModelConstants.REGARDS, ModelConstants.CCW_OFFICE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
