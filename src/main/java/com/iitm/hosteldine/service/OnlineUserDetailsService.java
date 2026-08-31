package com.iitm.hosteldine.service;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.MyUserDetails;
import com.iitm.hosteldine.entity.UserManagementOnlineEntity;
import com.iitm.hosteldine.repository.UserManagementOnlineRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("otherUserDetailsService")
@AllArgsConstructor
public class OnlineUserDetailsService implements UserDetailsService {
    private final UserManagementOnlineRepository userManagementOnlineRepository;

    @Transactional(readOnly = true)
    @Override
    public MyUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUserDetails userDetails = null;
        Optional<UserManagementOnlineEntity> optionalUser = userManagementOnlineRepository.findByUserEmailIdAndActiveFlag(username, ModelConstants.STATUS_ACTIVE);
        if (optionalUser.isPresent()) {
            UserManagementOnlineEntity userManagementEntity = optionalUser.get();
            userDetails = new MyUserDetails(userManagementEntity.getUserEmailId(), userManagementEntity.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + ModelConstants.OTHER_LOGIN_TYPE)));
            userDetails.setAuthServer(ModelConstants.APP_AUTH);
            userDetails.setUserId(String.valueOf(userManagementEntity.getUserId()));
            userDetails.setLoginType(ModelConstants.OTHER_LOGIN_TYPE);
            userDetails.setAccountType(ModelConstants.OTHER_LOGIN_TYPE);
            userDetails.setStatus(userManagementEntity.getActiveStatus());
            userDetails.setCandidateId(userManagementEntity.getApplicationId());
        }
        return userDetails;
    }

    public boolean saveOrUpdateUser(UserManagementOnlineEntity userManagementEntity) {
        return userManagementOnlineRepository.save(userManagementEntity).getUserId() > 0;
    }

    public UserManagementOnlineEntity getUserByUserName(String userName) {
        return userManagementOnlineRepository.findByUserEmailIdAndActiveFlag(userName, ModelConstants.STATUS_ACTIVE)
                .orElse(null);
    }

}
