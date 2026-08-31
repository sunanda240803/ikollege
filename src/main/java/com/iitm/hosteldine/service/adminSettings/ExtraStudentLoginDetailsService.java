package com.iitm.hosteldine.service.adminSettings;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.UserManagementOnlineDto;
import com.iitm.hosteldine.entity.UserManagementOnlineEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.UserManagementOnlineMapper;
import com.iitm.hosteldine.repository.UserManagementOnlineRepository;
import com.iitm.hosteldine.service.OnlineUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExtraStudentLoginDetailsService {

    private final UserManagementOnlineRepository userManagementOnlineRepository;
    private final OnlineUserDetailsService onlineUserDetailsService;

    public Page<UserManagementOnlineDto> getExtraStudentLoginDetails(PaginationForm paginationForm) {
        var pageRequest = PageRequest.of(paginationForm.getPage() - 1, paginationForm.getSize(), Sort.by("noOfFailedAttempts").descending());
        return  userManagementOnlineRepository.getAllOrByUserIdOrFirstNameOrLastName(ModelConstants.BLOCKED,
                ModelConstants.STATUS_ACTIVE, paginationForm.getSearch(), pageRequest)
        .map(UserManagementOnlineMapper.INSTANCE::fromUserManagementOnlineEntity);
    }

    public boolean activeBlockedUser(String userId) {
        return userManagementOnlineRepository.findByUserEmailIdAndActiveFlag(userId, ModelConstants.STATUS_ACTIVE)
                .map(this::updateBlockedUser)
                .orElse(false);
    }

    public boolean updateBlockedUser(UserManagementOnlineEntity userManagementOnlineEntity) {
        userManagementOnlineEntity.setActiveStatus(ModelConstants.NOT_BLOCKED);
        userManagementOnlineEntity.setNoOfFailedAttempts(0L);
        userManagementOnlineEntity.onUpdate();
        return onlineUserDetailsService.saveOrUpdateUser(userManagementOnlineEntity);
    }
}
