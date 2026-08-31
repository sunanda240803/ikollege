package com.iitm.hosteldine.service;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.model.UserAuditTrailEntity;
import com.iitm.hosteldine.repository.UserAuditTrailRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditTrailService {

    private final UserAuditTrailRepository userAuditTrailRepository;

    public boolean saveAuditTrail(String formName, String actionPath, String classMethodName){
        UserAuditTrailEntity userAuditTrailEntity = new UserAuditTrailEntity();
        userAuditTrailEntity.setDate(LocalDateTime.now());
        userAuditTrailEntity.setUserName(SecurityCtxUtil.userName());
        userAuditTrailEntity.setUserId(SecurityCtxUtil.userId());
        userAuditTrailEntity.setIpAddress(Strings.EMPTY);
        userAuditTrailEntity.setFormName(formName);
        userAuditTrailEntity.setActionCommitType(Constants.SAVED);
        userAuditTrailEntity.setActionPath(actionPath);
        userAuditTrailEntity.setClassMethodName(classMethodName);
        userAuditTrailRepository.save(userAuditTrailEntity);
        return true;
    }

}