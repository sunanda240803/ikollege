package com.iitm.hosteldine.config;

import com.iitm.hosteldine.exception.GlobalExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class WebSecurityConfig {
    static String urlLogout;
    static String urlIndex;
    static String urlHome;
    static String urlHome2;
    static String urlLogin;
    static String urlArchivePhotos;
    static String urlAuthenticate;
    static String urlOtherLogin;
    static String urlRegister;
    static String urlError;
    static String urlStudentRegistration;
    static String urlEmailIdExists;
    static String urlCaptcha;
    static String urlForgotPassword;
    static String urlSaveOnlineUser;
    static String urlFacultyLogin;
    static String urlHmOfficeLogin;
    static String urlValidateCredentials;
    static String urlPublic;
    static String urlFile;
    static String urlConvocation;
    static String urlLoginIssueMessReg;

    private final CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, DynamicSecurityService dynamicSecurityService) throws Exception {
        // Configure CORS
//        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> {
//                    requests.requestMatchers(whiteListURIs).permitAll();
                    requests.anyRequest().access(new DynamicAuthorizationManager(dynamicSecurityService));
                })
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin) // Use new API for frame options
                )
                .exceptionHandling((exception) -> exception.accessDeniedPage(urlError + GlobalExceptionHandler.PAGE_NOT_ALLOWED_PARAM))
                .formLogin(login -> login.loginPage(urlLogin).loginProcessingUrl(urlLogin + urlAuthenticate)
                        .usernameParameter("userName").passwordParameter("password")
                        .defaultSuccessUrl(urlIndex, true)
                        .failureHandler(authenticationFailureHandler))
                .logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher(urlLogout))
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // Create a new session to prevent triggering the invalidSessionUrl
                            request.getSession(true);
                            response.sendRedirect(request.getContextPath());
                        })
                        .clearAuthentication(true)
                        .invalidateHttpSession(true))
                .sessionManagement(session -> session.invalidSessionUrl(urlError + GlobalExceptionHandler.SESSION_EXPIRED_PARAM)
                );
        return http.build();
    }

   /* @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // Allow specific origins (for example, frontend app)
        corsConfiguration.setAllowedOrigins(List.of("https://ikollege.iitm.ac.in/iitmhostel/","https://secure.ccavenue.com/transaction/")); // Replace with your frontend server URL
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        corsConfiguration.setAllowCredentials(true); // If you need to allow cookies or authorization headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply CORS to all paths (or specify specific ones like "/payment", etc.)
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }*/

    @Value("${url.validate.credentials}")
    public void setUrlValidateCredentials(String urlValidateCredentials) {
        WebSecurityConfig.urlValidateCredentials = urlValidateCredentials;
    }

    @Value("${url.hm.office.login}")
    public void setUrlHmOfficeLogin(String urlHmOfficeLogin) {
        WebSecurityConfig.urlHmOfficeLogin = urlHmOfficeLogin;
    }

    @Value("${url.faculty.login}")
    public void setUrlFacultyLogin(String urlFacultyLogin) {
        WebSecurityConfig.urlFacultyLogin = urlFacultyLogin;
    }

    @Value("${url.save.online.user}")
    public void setUrlSaveOnlineUser(String urlSaveOnlineUser) {
        WebSecurityConfig.urlSaveOnlineUser = urlSaveOnlineUser;
    }

    @Value("${url.forgot.password}")
    public void setUrlForgotPassword(String urlForgotPassword) {
        WebSecurityConfig.urlForgotPassword = urlForgotPassword;
    }

    @Value("${url.captcha}")
    public void setUrlCaptcha(String urlCaptcha) {
        WebSecurityConfig.urlCaptcha = urlCaptcha;
    }

    @Value("${url.email.id.exists}")
    public void setUrlEmailIdExists(String urlEmailIdExists) {
        WebSecurityConfig.urlEmailIdExists = urlEmailIdExists;
    }

    @Value("${url.student.registration}")
    public void setUrlStudentRegistration(String urlStudentRegistration) {
        WebSecurityConfig.urlStudentRegistration = urlStudentRegistration;
    }

    @Value("${url.error}")
    public void setUrlError(String urlError) {
        WebSecurityConfig.urlError = urlError;
    }

    @Value("${url.online.registration}")
    public void setUrlRegister(String urlRegister) {
        WebSecurityConfig.urlRegister = urlRegister;
    }

    @Value("${url.other.login}")
    public void setUrlOtherLogin(String urlOtherLogin) {
        WebSecurityConfig.urlOtherLogin = urlOtherLogin;
    }

    @Value("${url.authenticate}")
    public void setUrlAuthenticate(String urlAuthenticate) {
        WebSecurityConfig.urlAuthenticate = urlAuthenticate;
    }

    @Value("${url.login}")
    public void setUrlLogin(String urlLogin) {
        WebSecurityConfig.urlLogin = urlLogin;
    }

    @Value("${url.archive}")
    public void setUrlArchivePhotos(String archivePhotos) {
        WebSecurityConfig.urlArchivePhotos = archivePhotos;
    }

    @Value("${url.home}")
    public void setUrlHome(String urlHome) {
        WebSecurityConfig.urlHome = urlHome;
    }

    @Value("${url.home2}")
    public void setUrlHome2(String urlHome2) {
        WebSecurityConfig.urlHome2 = urlHome2;
    }

    @Value("${url.index}")
    public void setUrlIndex(String urlIndex) {
        WebSecurityConfig.urlIndex = urlIndex;
    }

    @Value("${url.logout}")
    public void setUrlLogout(String urlLogout) {
        WebSecurityConfig.urlLogout = urlLogout;
    }

    @Value("${url.public.api}")
    public void setUrlPublicApi(String urlPublic) {  WebSecurityConfig.urlPublic = urlPublic;}

    @Value("${url.file}")
    public void setUrlFile(String urlFile) {  WebSecurityConfig.urlFile = urlFile;}

    @Value("${url.accommodation.mess.convocation}")
    public void setUrlConvocation(String urlConvocation) {
        WebSecurityConfig.urlConvocation = urlConvocation;
    }

    @Value("${url.login.issue.mess.reg}")
    public void setUrlLoginIssueMessReg(String urlLoginIssueMessReg) {
        WebSecurityConfig.urlLoginIssueMessReg = urlLoginIssueMessReg;
    }


}
