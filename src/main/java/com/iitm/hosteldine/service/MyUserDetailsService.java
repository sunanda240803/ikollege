package com.iitm.hosteldine.service;


import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.UserManagementEntity;
import com.iitm.hosteldine.dto.MyUserDetails;
import com.iitm.hosteldine.repository.UserManagementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("myUserDetailsService")
public class MyUserDetailsService implements UserDetailsService {
    private UserManagementRepository userManagementRepository;

    @Transactional(readOnly = true)
    @Override
    public MyUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUserDetails userDetails = null;
        Optional<UserManagementEntity> optionalUser = userManagementRepository.findByIdUsernameIgnoreCaseAndActiveFlag(username, ModelConstants.STATUS_ACTIVE);
        if (optionalUser.isPresent()) {
            UserManagementEntity userManagementEntity = optionalUser.get();
            List<GrantedAuthority> roles = new ArrayList<>();
            roles.add(new SimpleGrantedAuthority("ROLE_" + userManagementEntity.getRole().getRoleName()));
            if (userManagementEntity.getRoleSecondary() != null) {
                roles.add(new SimpleGrantedAuthority("ROLE_" + userManagementEntity.getRoleSecondary().getRoleName()));
            }
            userDetails = new MyUserDetails(userManagementEntity.getId().getUsername(), userManagementEntity.getPassword(), roles);
            userDetails.setAuthServer(userManagementEntity.getAuthenticationServer());
           userDetails.setUserId(userManagementEntity.getId().getUserId());
           userDetails.setAccountType(userManagementEntity.getAccountType());
           userDetails.setStatus(ModelConstants.NOT_BLOCKED);
        }
        return userDetails;
    }

    @Autowired
    public void setUserManagementRepository(UserManagementRepository userManagementRepository) {
        this.userManagementRepository = userManagementRepository;
    }
}
