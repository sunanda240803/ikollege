package com.iitm.hosteldine.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.form.common.FooterForm;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import com.iitm.hosteldine.util.Utility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.iitm.hosteldine.config.DynamicSecurityService;
import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.LoginDto;
import com.iitm.hosteldine.dto.MenuListDto;
import com.iitm.hosteldine.dto.MyUserDetails;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.dto.UserManagementOnlineDto;
import com.iitm.hosteldine.exception.GlobalExceptionHandler;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.OtherUserService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.service.UserManagementService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final DynamicSecurityService dynamicSecurityService;
    @Value("${url.student.registration.complete}")
    private String studentRegistrationComplete;
    @Value("${url.expiry.duration}")
    private Long urlExpiryDuration;
    @Value("${url.dashboard}" + "${url.student}")
    private String getStudentDashboard;

    private final OtherUserService otherUserService;
    private final StudentBioDataService studentBioDataService;
    private final CommonResponseUtil commonResponseUtil;
    private final UserManagementService userManagementService;
    private final SimsConfigDataService simsConfigDataService;
    private final MessMasterCommonService messMasterCommonService;
    
    @Value("${url.other.login}")
    private String otherLogin;
    @Value("${url.home}")
    private String homeURL;

    @Value("${url.admin.student.bio.data}")
    private String studentBioDataUrl;

    @Value("${url.error}")
    private String errorPage;


    @GetMapping({"${url.home}", "${url.home2}"})
    public String home(ModelMap map, HttpServletRequest request) {
        String username = SecurityCtxUtil.userName();
        if (username != null && !SecurityCtxUtil.ANONYMOUS_USER.equalsIgnoreCase(username)) {
            if (simsConfigDataService.isUserAllowedForHostelCapacity(username)) {
                return Constants.REDIRECT + "/hostelCapacity/slideshow";
            }
            return Constants.REDIRECT + "/index";
        }
        boolean isIFPPButtonNeeded = Boolean.parseBoolean(simsConfigDataService.getSimConfigValue(SimsConfigDataService.IS_IFPP_BUTTON_NEEDED));
        ArrayList<String> convocationDates = simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.CONVOCATION_DATES);
        String convocationOpenLink = simsConfigDataService.getSimConfigValue(SimsConfigDataService.CONVOCATION_OPEN_LINK);
        String convocationOpenLinkDate = simsConfigDataService.getSimConfigValue(SimsConfigDataService.CONVOCATION_OPEN_LINK_DATE);
        String convocationYear="";
        Boolean isConvocationOpen = Boolean.valueOf(convocationOpenLink);
        if (convocationDates != null && !convocationDates.isEmpty()) {
            String dateRange = convocationDates.getFirst().trim();
            String[] parts = dateRange.split(ModelConstants.TILDE);
            if (parts.length == 2) {
                LocalDate startDate = LocalDate.parse(parts[0].trim().replace(ModelConstants.SLASH, ModelConstants.HYPHEN));
                convocationYear = String.valueOf(startDate.getYear());
            }
        }
        map.addAttribute(Constants.FOOTER_FORM, FooterForm.defaultForm(request));
        map.addAttribute("isIFPPButtonNeeded", isIFPPButtonNeeded);
        map.addAttribute("isConvocationOpen", isConvocationOpen);
        map.addAttribute("convocationYear", convocationYear);
        map.addAttribute("convocationOpenLinkDate", convocationOpenLinkDate);
        return HTMLPage.LANDING;
    }

    @GetMapping("${url.login}")
    public String login(ModelMap map) throws Exception {
        map.addAttribute("loginType", MCrypt.getInstance().encryptToText(ModelConstants.STUDENT_LOGIN_TYPE));
        map.addAttribute("loginRole", ModelConstants.STUDENT_LOGIN_TYPE);

        List<MessMasterControllerDto> messControllerList = messMasterCommonService.getMessMasterControllerList();
        String regBeginDate = messControllerList != null ? messControllerList.get(0).getRegBeginDate().toString(): ModelConstants.EMPTY_STRING ;
        String regEndDate = messControllerList != null ? messControllerList.get(0).getRegEndDate().toString(): ModelConstants.EMPTY_STRING ;
        String beginTime = messControllerList != null ? messControllerList.get(0).getRegBeginTime(): ModelConstants.EMPTY_STRING ;
        String endTime = messControllerList != null ? messControllerList.get(0).getRegEndTime(): ModelConstants.EMPTY_STRING ;

        String beginDateTimeString = regBeginDate + Constants.TIME + beginTime + ":00";
        String endDateTimeString = regEndDate + Constants.TIME + endTime + ":00";
        LocalDateTime beginDateTime = LocalDateTime.parse(beginDateTimeString);
        LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeString);
        ArrayList<String> convocationDates = simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.CONVOCATION_DATES);
        boolean isConvocationOpen = false;
        if (convocationDates != null && !convocationDates.isEmpty()) {
            String dateRange = convocationDates.getFirst().trim();
            String[] parts = dateRange.split(ModelConstants.TILDE);
            if (parts.length == 2) {
                LocalDate startDate = LocalDate.parse(parts[0].trim().replace(ModelConstants.SLASH, ModelConstants.HYPHEN));
                LocalDate endDate   = LocalDate.parse(parts[1].trim().replace(ModelConstants.SLASH, ModelConstants.HYPHEN));
                LocalDate today = LocalDate.now();
                isConvocationOpen = !today.isBefore(startDate) && !today.isAfter(endDate);
            }
        }
        map.addAttribute("beginDateTime", beginDateTime);
        map.addAttribute("endDateTime", endDateTime);
        map.addAttribute("currentDateTime", LocalDateTime.now());
        map.addAttribute("isConvocationOpen", isConvocationOpen);

        return HTMLPage.LOGIN;
    }

    @GetMapping("${url.student.registration}")
    public String studentRegistration(ModelMap map, HttpServletRequest request) {
        Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
        map.addAttribute("saveStatus", flashInputMap != null ? flashInputMap.get("saveStatus") : null);
        map.addAttribute("form", new StudentBioDataFormDetailDto());
        LocalDate dobMax = LocalDate.of(LocalDate.now().getYear() - 16, 12, 31);
        map.addAttribute("dobMax", java.sql.Date.valueOf(dobMax));
        map.addAttribute("declarationMin", LocalDate.now());
        map.addAttribute("action", "new");
        map.addAttribute("loginType", "register");
        String guideEmail = simsConfigDataService.getSimConfigValue(SimsConfigDataService.GUIDE_EMAIL);
        map.addAttribute("guideEmail",guideEmail);
        map.addAttribute("proofTypeList", simsConfigDataService.getSimConfigValueArrayList(SimsConfigDataService.PROOF_TYPES));
        map.addAttribute("edit",true);
        return HTMLPage.STUDENT_REGISTRATION;
    }

    @PostMapping("${url.student.registration}")
    public String saveStudentRegistration(@ModelAttribute StudentBioDataFormDetailDto form, RedirectAttributes redirectAttributes) throws Exception {
        boolean isNew = SecurityCtxUtil.ANONYMOUS_USER.equals(SecurityCtxUtil.userId()) || form.getId() == null;
        System.out.println("Biodata StudentId Controller---------------"+ form.getStudentId());
        String saveStatus = studentBioDataService.saveStudentRegistration(form);
        redirectAttributes.addFlashAttribute("saveStatus", saveStatus);
        String referenceCode = MCrypt.getInstance().encryptToText(System.currentTimeMillis() + "`" + form.getStudentId());
        commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttributes);
        if(!isNew && SecurityCtxUtil.accountType().equals(Constants.SOFTWARE_ADMIN)) {
            redirectAttributes.addFlashAttribute("studentId", form.getStudentId());
            return Constants.REDIRECT + studentBioDataUrl;
        }
        return Constants.REDIRECT + (isNew ? (studentRegistrationComplete + "?reference=" + referenceCode) : getStudentDashboard);
    }

    @GetMapping("${url.student.registration}" + "${url.validate.student.id}")
    public @ResponseBody String validateRollNumberAtRegistration(@RequestParam(value = "studentId") String studentId) {
        return studentBioDataService.validateRollNumber(studentId);
    }

    private void validateReferenceString(String reference, ModelMap map) {
        map.addAttribute("invalidURL", true);
        String[] referenceSplit = null;
        try {
            String decodedReference = MCrypt.getInstance().decryptToString(reference);
            referenceSplit = decodedReference.split("`");
        } catch (Exception ignore) {
        }
        if (referenceSplit != null && referenceSplit.length == 2) {
            String dateStr = referenceSplit[0];
            try {
                long referenceTimestamp = Long.parseLong(dateStr);
                if ((System.currentTimeMillis() - referenceTimestamp) < urlExpiryDuration) {
                    String studentId = referenceSplit[1];
                    if (studentId != null) {
                        String studentIdExists = studentBioDataService.validateRollNumber(studentId);
                        if (studentIdExists != null) {
                            map.addAttribute("reference", reference);
                            map.addAttribute("studentId", studentId);
                            map.addAttribute("urlExpired", false);
                            map.addAttribute("invalidURL", false);
                        }
                    }
                } else {
                    map.addAttribute("invalidURL", false);
                    map.addAttribute("urlExpired", true);
                }
            } catch (NumberFormatException ignore) {
            }
        }
    }

    @GetMapping("${url.student.registration.complete}")
    public String studentRegistrationComplete(@RequestParam(required = false) String reference, ModelMap map) {
        validateReferenceString(reference, map);
        Boolean isBiodataPdfEnable = Boolean.valueOf(simsConfigDataService.getSimConfigValue(SimsConfigDataService.BIO_DATA_PDF_ENABLE));
        map.addAttribute("isBiodataPdfEnable", isBiodataPdfEnable);
        return HTMLPage.STUDENT_REGISTRATION_COMPLETE;
    }

    @SuppressWarnings("MVCPathVariableInspection")
    @GetMapping("${url.student.registration.pdf.download}" + "${url.student.id.variable}")
    public ResponseEntity<Resource> getStudentBioDataPDF(@PathVariable(required = false) String studentId, ModelMap map) throws Exception {
        validateReferenceString(studentId, map);
        boolean invalidURL = Boolean.parseBoolean(Objects.requireNonNull(map.getAttribute("invalidURL")).toString());
        if (!invalidURL) {
            boolean urlExpired = Boolean.parseBoolean(Objects.requireNonNull(map.getAttribute("urlExpired")).toString());
            if (!urlExpired) {
                Resource resource =
                        studentBioDataService.getStudentBioDataPDF(Objects.requireNonNull(map.getAttribute("studentId")).toString(),true);
                return Utility.prepareDownloadFile(resource);
            }
        }
        return null;
    }

    @GetMapping("${url.other.login}")
    public String othersLogin(ModelMap map, HttpServletRequest request) throws Exception {
        map.addAttribute("userManagementForm", new UserManagementOnlineDto());
        map.addAttribute("loginType", MCrypt.getInstance().encryptToText(ModelConstants.OTHER_LOGIN_TYPE));
        commonResponseUtil.updateCommonModelAttributes(map, request);
        return HTMLPage.OTHER_LOGIN;
    }

    @GetMapping("${url.faculty.login}")
    public String facultyLogin(ModelMap map) throws Exception {
        map.addAttribute("loginType", MCrypt.getInstance().encryptToText(ModelConstants.FACULTY_LOGIN_TYPE));
        map.addAttribute("loginRole", ModelConstants.FACULTY_LOGIN_TYPE);
        return HTMLPage.LOGIN;
    }

    @GetMapping("${url.hm.office.login}")
    public String hmOfficeLogin(ModelMap map) throws Exception {
        map.addAttribute("loginType", MCrypt.getInstance().encryptToText(ModelConstants.HM_OFFICE_LOGIN_TYPE));
        map.addAttribute("loginRole", ModelConstants.HM_OFFICE_LOGIN_TYPE);
        return HTMLPage.LOGIN;
    }

    @GetMapping("${url.index}")
    public String index(ModelMap map, HttpServletRequest request) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (SecurityCtxUtil.ANONYMOUS_USER.equals(String.valueOf(authentication.getPrincipal()))) {
            return Constants.REDIRECT + homeURL;
        } else {
            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

            String loginType = userDetails.getLoginType();
            map.addAttribute("loginType", loginType);

            List<MenuListDto> dashboards = dynamicSecurityService.getMenusForRole(authentication, "dashboard");
            String username = SecurityCtxUtil.userName();
            if (simsConfigDataService.isUserAllowedForHostelCapacity(username)) {
                return Constants.REDIRECT + "/hostelCapacity/slideshow";
            }

            if (!dashboards.isEmpty()) {
                return Constants.REDIRECT + dashboards.getFirst().getUrlPath();
            } else {
                return Constants.REDIRECT + errorPage + GlobalExceptionHandler.NO_DASHBOARD_PARAM;
            }
        }
    }

    @GetMapping(value = "sample/{screen}")
    public String sample(@PathVariable String screen) {
        return "sample/" + screen;
    }

    @GetMapping(value = "dev/{screen}")
    public String dev(@PathVariable String screen,PaginationForm form,ModelMap map,HttpServletRequest request) {
        commonResponseUtil.updateCommonModelAttributes(map, request,null,form);
        return "dev/" + screen;
    }

    @GetMapping("${url.online.registration}")
    public String register(ModelMap map, @ModelAttribute UserManagementOnlineDto userManagementOnlineDto)
            throws Exception {
        map.addAttribute("loginType", MCrypt.getInstance().encryptToText(ModelConstants.OTHER_LOGIN_TYPE));
        map.addAttribute("userManagementOnlineDto", userManagementOnlineDto);
        return HTMLPage.ONLINE_USER_REGISTRATION;
    }

    @PostMapping("${url.save.online.user}")
    public String saveOnlineUsers(ModelMap map, @ModelAttribute UserManagementOnlineDto userManagementOnlineDto
            , RedirectAttributes redirectAttrs)
            throws Exception {
        if (!otherUserService.checkEmailIdExist(userManagementOnlineDto.getApplicantEmail())) {
            String saveStatus = otherUserService.saveOtherLogin(userManagementOnlineDto);
            commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs, "response.registration.success");
        } else {
            System.out.println("Already exist");
        }
        map.addAttribute("loginType", MCrypt.getInstance().encryptToText(ModelConstants.OTHER_LOGIN_TYPE));
        return "redirect:" + otherLogin;
    }

    @PostMapping("${url.email.id.exists}")
    public @ResponseBody boolean checkEmailIdExist(@RequestParam String emailId) {
        return otherUserService.checkEmailIdExist(emailId);
    }

    @PostMapping("${url.forgot.password}")
    public @ResponseBody String forgotPassword(@RequestParam String applicantEmail, HttpServletRequest request)
            throws Exception {
        String returnStatus = otherUserService.forgotPassword(applicantEmail);
        return switch (returnStatus) {
            case ModelConstants.SUCCESS -> ModelConstants.SUCCESS;
            case ModelConstants.FAILURE -> ModelConstants.FAILURE;
            case ModelConstants.PASSWORD_MISMATCH -> ModelConstants.PASSWORD_MISMATCH;
            default -> throw new Exception("Unexpected value: " + returnStatus);
        };
    }

    @PostMapping(value = "${url.validate.credentials}")
    public @ResponseBody String validateCredentials(LoginDto loginDto ) throws Exception {
        return Optional.ofNullable(userManagementService.validateCredentials(loginDto))
                .map(MyUserDetails::getStatus)
                .orElse(ModelConstants.FAILURE);
    }

}
