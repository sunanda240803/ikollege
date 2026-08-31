package com.iitm.hosteldine.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class MyUserDetails extends User {

    private String password;
    private String userName;
    private String authServer;
    private final Set<GrantedAuthority> authorities;
    private String loginType;
    private String userId;
    private String accountType;
    private StudentDetails studentDetails;
    private String status;
    private String candidateId;
    private Long dashboardId;
    private String profileImageName;
    private String profileName;

    public MyUserDetails(String userName, String password, Collection<? extends GrantedAuthority> authorities) {
        super(userName, password, authorities);
        this.userName = userName;
        this.password = password;
        this.authorities = new HashSet<>(authorities);
    }

}
