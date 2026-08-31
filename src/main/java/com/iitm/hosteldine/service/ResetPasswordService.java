package com.iitm.hosteldine.service;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.MyUserDetails;
import com.iitm.hosteldine.dto.ResetPasswordDto;
import com.iitm.hosteldine.entity.UserManagementEntity;
import com.iitm.hosteldine.entity.UserManagementOnlineEntity;
import com.iitm.hosteldine.repository.UserManagementOnlineRepository;
import com.iitm.hosteldine.repository.UserManagementRepository;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.util.MD5Encryption;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResetPasswordService {
    private final UserManagementOnlineRepository userManagementOnlineRepository;
    private final MailQueueService mailQueueService;
    private final UserManagementRepository userManagementRepository;

    @Transactional
    public String resetPassword(ResetPasswordDto resetPasswordDto) {
        String newEncryptedPassword = MD5Encryption.md5Encrypt(resetPasswordDto.getNewPassword());
        String oldEncryptedPassword = MD5Encryption.md5Encrypt(resetPasswordDto.getApplicantPassword());
        Optional<?> optionalUser;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        String loginType = userDetails.getLoginType();
        resetPasswordDto.setLoginType(loginType);
        if (resetPasswordDto.getLoginType().equals(ModelConstants.OTHER_LOGIN_TYPE)) {
            optionalUser = userManagementOnlineRepository.findByUserEmailIdAndPasswordAndActiveFlag(SecurityCtxUtil.userName(), oldEncryptedPassword, ModelConstants.STATUS_ACTIVE);
        } else if (resetPasswordDto.getLoginType().equals(ModelConstants.HM_OFFICE_LOGIN_TYPE)) {
            optionalUser = userManagementRepository.findByIdUserIdAndPasswordAndActiveFlagAndAuthenticationServerIgnoreCase(SecurityCtxUtil.userId(), oldEncryptedPassword, ModelConstants.STATUS_ACTIVE, ModelConstants.APP_AUTH);
        } else {
            return ModelConstants.PASSWORD_MISMATCH;
        }

        return optionalUser.map(user -> {
            if (user instanceof UserManagementOnlineEntity userManagementOnlineEntity) {
                userManagementOnlineEntity.setPassword(newEncryptedPassword);
                userManagementOnlineEntity.setPasswordModifiedTime(LocalDateTime.now());
                userManagementOnlineEntity.setModifiedAt(LocalDateTime.now());
                userManagementOnlineEntity.setModifiedBy(SecurityCtxUtil.userName());
                userManagementOnlineRepository.save(userManagementOnlineEntity);
            } else if (user instanceof UserManagementEntity userManagementEntity) {
                userManagementEntity.setPassword(newEncryptedPassword);
                userManagementEntity.setPasswordModifiedTime(LocalDateTime.now());
                userManagementEntity.setModifiedAt(LocalDateTime.now());
                userManagementEntity.setModifiedBy(SecurityCtxUtil.userName());
                userManagementRepository.save(userManagementEntity);
            }
            //mailQueueService.saveMailContent(SecurityCtxUtil.userName(), resetPasswordDto.getNewPassword());
            return ModelConstants.SUCCESS;
        }).orElse(ModelConstants.PASSWORD_MISMATCH);


    }
}
