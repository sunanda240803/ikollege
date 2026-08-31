package com.iitm.hosteldine.config;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.MyUserDetails;
import com.iitm.hosteldine.dto.StudentDetails;
import com.iitm.hosteldine.entity.UserManagementOnlineEntity;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.service.MyUserDetailsService;
import com.iitm.hosteldine.service.OnlineUserDetailsService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.MD5Encryption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;
import java.util.Objects;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MyAuthenticationProvider implements AuthenticationProvider {

    private final MyUserDetailsService myUserDetailsService;
    private final OnlineUserDetailsService onlineUserDetailsService;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final StudentDetailsInfoRepository studentDetailsInfoRepository;
    private final SimsConfigDataService simsConfigDataService;
    @Value("${ldap.uri}")
    private String ldapUri;
    @Value("${ldap.bindDN}")
    private String bindDN;
    @Value("${ldap.bind.password}")
    private String bindPassword;
    @Value("${ldap.base}")
    private String ldapBase;
    @Value("${ldap.context.factory}")
    private String ldapContextFactory;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String methodName = "authenticate";
        log.info("Entering {}", methodName);

        String[] userNameAndLoginType = authentication.getPrincipal().toString().split("~");
        String username = userNameAndLoginType[0].trim();
        String password = null;
        String loginType = null;
        Authentication auth = null;
        MyUserDetails user = null;

        try {
            loginType = MCrypt.getInstance().decryptToString(userNameAndLoginType[1]);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        if (authentication.getCredentials() != null) {
            password = authentication.getCredentials().toString().trim();
        }

        try {
            // Get the user if authentication is successful
            user = setUserDetails(loginType, username, password);
        } catch (Exception e) {
            log.error("At Authentication", e);
            throw new UsernameNotFoundException(e.getMessage());
        }

        if (user != null) {
            // Create the auth token using the user details
            auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        }

        log.info("Exiting {} - {}", methodName, auth != null);
        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private boolean validateLdapUser(String loginName, String loginPassword) {
        String loginUserName = loginName.trim();
        String loginUserPassword;
        boolean isUserValid = false;
        if (loginPassword != null) {
            loginUserPassword = loginPassword.trim();

            Hashtable<String, String> env = new Hashtable<>(11);
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, ldapUri);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, bindDN);
            env.put(Context.SECURITY_CREDENTIALS, bindPassword);
            NamingEnumeration<SearchResult> results;
            try {
                DirContext ctx = new InitialDirContext(env);
                SearchControls controls = new SearchControls();
                controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                results = ctx.search(ldapBase, "(&(objectclass=*)(uid=" + loginUserName + "))", controls);

                while (results.hasMore()) {
                    String value = "";
                    while (results.hasMoreElements()) {
                        SearchResult sr = results.next();
                        value = sr.getNameInNamespace();
                    }

                    Hashtable<String, String> envLoginUser = new Hashtable<>(11);
                    envLoginUser.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
                    envLoginUser.put(Context.PROVIDER_URL, ldapUri);
                    envLoginUser.put(Context.SECURITY_PRINCIPAL, value);
                    envLoginUser.put(Context.SECURITY_CREDENTIALS, loginUserPassword);
                    new InitialDirContext(envLoginUser);
                    isUserValid = true;
                }
            } catch (Exception e) {
                log.error("At LDAP Authentication", e);
            }
        }
        return isUserValid;

    }

    public MyUserDetails setUserDetails(String loginType, String userName, String password) {
        String encPassword;
        MyUserDetails user = null;
        boolean settlementStatus=false, dayScholarStatus=false;

        // Encrypt the password
        try {
            encPassword = MD5Encryption.md5Encrypt(password);
        } catch (Exception e) {
            throw new RuntimeException("Password encryption failed", e);
        }

        // Logic for each login type
        switch (loginType) {
            case ModelConstants.STUDENT_LOGIN_TYPE:
                user = myUserDetailsService.loadUserByUsername(userName.toLowerCase().trim());
                if (user != null && user.getAccountType().equals(ModelConstants.STUDENT_LOGIN_TYPE)) {
                    log.debug("User Exists.");
                    user.setLoginType(loginType);
                    
                    if (isPasswordValid(user, password, encPassword)) {
                    	//TODO Validate by getting Student Information and allow only if student info is available
                    	StudentDetails studentDetails = studentDetailsInfoService.getStudentDetails(userName.toUpperCase().trim());
                    	user.setStudentDetails(studentDetails);
                    	
                        settlementStatus = studentDetailsInfoRepository.existsByActiveFlagAndStudentIdAndSettlementFlag(
                                ModelConstants.STATUS_ACTIVE, userName.toLowerCase().trim(), ModelConstants.STATUS_ACTIVE);
                        log.debug("Settlement: {}", settlementStatus);
                        if (settlementStatus) {
                            dayScholarStatus = studentDetailsInfoRepository
                                    .existsByActiveFlagAndStudentIdAndSettlementFlagAndDayScholar(
                                            ModelConstants.STATUS_ACTIVE, userName.toLowerCase().trim(),
                                            ModelConstants.STATUS_ACTIVE, ModelConstants.STATUS_ACTIVE);
                            log.debug("Day Scholar: {}", dayScholarStatus);
                            if (dayScholarStatus) {
                                return user;
                            }
                        }else{
                            return user;
                        }
                    }
                }
                break;

            case ModelConstants.OTHER_LOGIN_TYPE:
                user = onlineUserDetailsService.loadUserByUsername(userName.trim().toLowerCase());
                String status = ModelConstants.FAILURE;
                if (user != null) {
                    if(user.getStatus().equals(ModelConstants.NOT_BLOCKED)){
                        if(encPassword.equals(user.getPassword())){
                            status = ModelConstants.SUCCESS;
                            checkLoginAttempts(userName,status);
                            return user;
                        }
                        else{
                            checkLoginAttempts(userName,status);
                            return null;
                        }
                    }
                    else{
                        return null;
                    }
                }
                break;

            case ModelConstants.FACULTY_LOGIN_TYPE:
                user = myUserDetailsService.loadUserByUsername(userName);
                if (user != null) {
                    boolean isFaculty = user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .map(role -> role.replace("ROLE_", ModelConstants.EMPTY_STRING))
                            .anyMatch(role -> role.equalsIgnoreCase(Constants.USER_ROLE_FACULTY));
                    if (isFaculty) {
                        user.setLoginType(loginType);
                        if (isPasswordValid(user, password, encPassword)) {
                            return user;
                        }
                    }
                }
                break;

            case ModelConstants.HM_OFFICE_LOGIN_TYPE:
                user = myUserDetailsService.loadUserByUsername(userName);
                if (user != null && !user.getAccountType().equals(ModelConstants.STUDENT_LOGIN_TYPE)) {
                    user.setLoginType(loginType);
                    if (isPasswordValid(user, password, encPassword)) {
                        return user;
                    }
                }
                break;

            default:
                throw new RuntimeException("Invalid login type: " + loginType);
        }
        return null;
    }


    public boolean isPasswordValid(MyUserDetails user, String password, String encPassword) {
        boolean auth = false;
        if (user.getAuthServer().equalsIgnoreCase("LDAP")) {
            auth =  validateLdapUser(user.getUsername().toLowerCase().trim(), password);
        } else if (user.getAuthServer().equalsIgnoreCase("APP")) {
            auth = encPassword.equals(user.getPassword());
        }
        log.debug("{} Authentication {}", user.getAuthServer().toUpperCase(), auth ? "successful" : "failed");
        return auth;
    }

    private void checkLoginAttempts(String userName, String status) {
        UserManagementOnlineEntity userByUserName = onlineUserDetailsService.getUserByUserName(userName);
        Long retryCount = Long.parseLong(simsConfigDataService.getSimConfigValue(SimsConfigDataService.OTHER_LOGIN_RETRY_COUNT));
        if (userByUserName != null && userByUserName.getNoOfFailedAttempts() < retryCount) {
            if (status.equals(ModelConstants.FAILURE)) {
                userByUserName.setNoOfFailedAttempts(userByUserName.getNoOfFailedAttempts() + 1);
                if (Objects.equals(userByUserName.getNoOfFailedAttempts(), retryCount)) {
                    userByUserName.setActiveStatus(ModelConstants.BLOCKED);
                }
            } else {
                userByUserName.setNoOfFailedAttempts(0L);
            }
            onlineUserDetailsService.saveOrUpdateUser(userByUserName);
        }
    }

}



