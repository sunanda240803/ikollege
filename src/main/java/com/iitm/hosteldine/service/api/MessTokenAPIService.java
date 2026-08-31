package com.iitm.hosteldine.service.api;

import com.iitm.hosteldine.config.MyAuthenticationProvider;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.api.ApiConstants;
import com.iitm.hosteldine.controller.TransferAmountUtils;
import com.iitm.hosteldine.dto.MyUserDetails;
import com.iitm.hosteldine.dto.StudentDetailsInfoMapper;
import com.iitm.hosteldine.dto.api.MessTokenAPIDto;
import com.iitm.hosteldine.dto.api.StudentInfoAPIDto;
import com.iitm.hosteldine.dto.financialYear.FinancialYearDto;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.entity.RoleEntity;
import com.iitm.hosteldine.entity.UserManagementEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntity;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.entity.student.UserFpCardEntity;
import com.iitm.hosteldine.form.common.TransactionDto;
import com.iitm.hosteldine.generated.model.GuestCouponAccessTokenEntity;
import com.iitm.hosteldine.generated.model.MessQrApplicationEntity;
import com.iitm.hosteldine.mapper.api.GuestCouponAccessTokenMapper;
import com.iitm.hosteldine.mapper.mess.MessMasterMapper;
import com.iitm.hosteldine.model.StudentNetBalanceEntity;
import com.iitm.hosteldine.model.hostel.GuestCouponMappingsEntity;
import com.iitm.hosteldine.model.hostel.GuestCouponPaymentAdviceEntity;
import com.iitm.hosteldine.model.mess.*;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.repository.StudentNetBalanceEntityRepository;
import com.iitm.hosteldine.repository.UserManagementRepository;
import com.iitm.hosteldine.repository.api.GuestCouponAccessTokenRepository;
import com.iitm.hosteldine.repository.api.MessQrApplicationRepository;
import com.iitm.hosteldine.repository.financialYear.FinancialYearRepository;
import com.iitm.hosteldine.repository.hostel.GuestCouponMappingsRepository;
import com.iitm.hosteldine.repository.hostel.GuestCouponRequestRepository;
import com.iitm.hosteldine.repository.mess.*;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import com.iitm.hosteldine.repository.student.ShowStudentDetailRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.repository.student.UserFpCardRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.hostel.AccountHeadService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.util.MD5Encryption;
import com.iitm.hosteldine.util.TokenGeneration;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MessTokenAPIService {

    private final GuestCouponAccessTokenRepository accessTokenRepository;
    private final MessMasterCommonService messMasterCommonService;
    private final GuestCouponRequestRepository guestCouponRepository;
    private final SimsConfigDataService simsConfigDataService;
    private final MyAuthenticationProvider myAuthenticationProvider;
    private final UserManagementRepository userManagementRepository;
    private final MessCouponUserMappingRepository messCouponUserMappingRepository;
    private final StudentDetailsInfoRepository studentDetailsInfoRepository;
    private final MessQrApplicationRepository messQrApplicationRepository;
    private final GuestCouponMappingsRepository guestCouponMappingsRepository;
    private final MessSessionRepository messSessionRepository;
    private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;
    private final MessTerminalRepository messTerminalRepository;
    private final UserFpCardRepository userFpCardRepository;
    private final ShowStudentDetailRepository showStudentDetailRepository;
    private final StudentNetBalanceEntityRepository studentNetBalanceEntityRepository;
    private final FoodCourtLedgerRepository foodCourtLedgerRepository;
    private final MessMasterService messMasterService;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final MessageSource messageSource;
    private final MessAllocationEntityRepository messAllocationEntityRepository;
    private final FinancialYearRepository financialYearRepository;
    private final AccountHeadService accountHeadService;
    private final MessLedgerARepository messLedgerARepository;
    private final MessLedgerBRepository messLedgerBRepository;

    public MessTokenAPIDto v1_authenticateForMessAPI(MessTokenAPIDto inputDto) throws Exception {
        MessTokenAPIDto returnDto = new MessTokenAPIDto();
        String encPassword = MD5Encryption.md5Encrypt(inputDto.getPassword());
        //Get user details
        Optional<UserManagementEntity> userEntityOpt = userManagementRepository.findByIdUsernameIgnoreCaseAndActiveFlag(inputDto.getUserName(), ModelConstants.STATUS_ACTIVE);
        if (userEntityOpt.isPresent()) {
            UserManagementEntity userEntity = userEntityOpt.get();
            String roleName = Optional.ofNullable(userEntity.getRole())
                    .map(RoleEntity::getRoleName)
                    .orElse("");
            String secondaryRoleName = Optional.ofNullable(userEntity.getRoleSecondary())
                    .map(RoleEntity::getRoleName)
                    .orElse("");

            String userType = Stream.of(roleName, secondaryRoleName)
                    .filter(Objects::nonNull)
                    .map(String::toLowerCase)
                    .map(role -> {
                        if (role.equals(Constants.STUDENT.toLowerCase())) return Constants.STUDENT;
                        if (role.equals(Constants.CATERER.toLowerCase())) return Constants.CATERER;
                        if (role.equals(Constants.APP_ADMIN.toLowerCase())) return Constants.APP_ADMIN;
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            inputDto.setUserName(userEntity.getId().getUsername());
            inputDto.setUserType(userType);

            if (userType != null && (userType.equals(Constants.CATERER) || userType.equals(Constants.APP_ADMIN))) {
                if (userEntity.getPassword().equals(encPassword)) {
                    //Generating the token and inserting it.
                    returnDto = saveAccessToken(inputDto, returnDto);
                    if (returnDto.getStatus().equals(Constants.SUCCESS)) {
                        if (userType.equals(Constants.CATERER)) {
                            //Get mess details
                            Optional<List<MessCouponUserMappingEntity>> messCoupon = messCouponUserMappingRepository.
                                    findByUserNameIgnoreCaseAndActiveFlag(inputDto.getUserName(), ModelConstants.STATUS_ACTIVE);
                            if (messCoupon.isPresent()) {
                                List<MessCouponUserMappingEntity> userList = messCoupon.get();
                                if (userList.isEmpty()) {
                                    returnDto.setStatus("User hasn't been assigned to any mess.");
                                } else {
                                    MessCouponUserMappingEntity mappingEntity = messCoupon.get().get(0);
                                    returnDto.setMessName(mappingEntity.getMessMaster().getMessName());
                                    returnDto.setMessId(mappingEntity.getMessMaster().getId());
                                    //This is for POS login check
                                    if (inputDto.getTerminalIp() != null && !inputDto.getTerminalIp().isEmpty()) {
                                        Optional<MessTerminalEntity> terminalEntityOpt = messTerminalRepository.getTerminalDetails(ModelConstants.STATUS_ACTIVE, inputDto.getTerminalIp());
                                        if (terminalEntityOpt.isPresent()) {
                                            if (mappingEntity.getMessMaster().getId() != terminalEntityOpt.get().getMessMaster().getId()) {
                                                returnDto.setStatus("Wrong Mess");
                                            }
                                        } else {
                                            returnDto.setStatus("Not found any mess for this Terminal Ip");
                                        }
                                    }
                                }
                            } else {
                                returnDto.setStatus("Not found any mess for this user");
                            }
                        }
                    }
                } else {
                    returnDto.setStatus("invalid password");
                }
            } else if (userType != null && userType.equals(Constants.STUDENT)) {
                returnDto.setStatus("invalid user");
                return  returnDto;
                //Check whether the student login
//                boolean studentStatus = studentDetailsInfoRepository.existsByActiveFlagAndStudentIdIgnoreCaseAndSettlementFlag(
//                        ModelConstants.STATUS_ACTIVE, inputDto.getUserName(), ModelConstants.STATUS_INACTIVE);
//                if (studentStatus) {
//                    MyUserDetails user = new MyUserDetails(userEntity.getId().getUsername(), userEntity.getPassword(),
//                            List.of(new SimpleGrantedAuthority("ROLE_" + userEntity.getRole().getRoleName())));
//                    user.setAuthServer(userEntity.getAuthenticationServer());
//                    Boolean authStatus = myAuthenticationProvider.isPasswordValid(user, inputDto.getPassword(), encPassword);
//                    if (authStatus) {
//                        //Generating the token and inserting it.
//                        returnDto = saveAccessToken(inputDto, returnDto);
//                    } else {
//                        returnDto.setStatus("invalid password");
//                    }
//                } else {
//                    returnDto.setStatus("invalid user");
//                }
            } else {
                returnDto.setStatus("Invalid User Type");
            }
            if (returnDto.getStatus().equals(Constants.SUCCESS)) {
                returnDto.setUserName(userEntity.getId().getUsername());
                returnDto.setUserType(userType);
            }
        } else {
            returnDto.setStatus("User Not Found");
        }

        return returnDto;
    }

    public MessTokenAPIDto saveAccessToken(MessTokenAPIDto inputDto, MessTokenAPIDto returnDto) throws Exception {

        // Token generation for each insert
        var token = new TokenGeneration(16).nextString();
        inputDto.setToken(System.currentTimeMillis() + token);

        //Check any active token for this user already, if its update to false
        List<GuestCouponAccessTokenEntity> tokenList =
                accessTokenRepository
                        .findAllByUsernameIgnoreCaseAndActiveFlagTrue(inputDto.getUserName());

        if (!tokenList.isEmpty()) {
            tokenList.forEach(tokenEntity -> tokenEntity.setActiveFlag(false));
            accessTokenRepository.saveAll(tokenList);
        }

        //get and set time limit
        String timeLimit = simsConfigDataService.getSimConfigValue(SimsConfigDataService.MESS_TOKEN_TIME_LIMIT);    // Example: "caterer~1800000,student~86400000,AppAdmin~86400000"
        Map<String, Long> userTypeToLimit = Arrays.stream(timeLimit.split(","))
                .map(entry -> entry.split("~"))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(parts -> parts[0].trim(), parts -> Long.parseLong(parts[1].trim())));
        inputDto.setTimeLimit(userTypeToLimit.getOrDefault(inputDto.getUserType(), 0L));  // Default to 0 if userType not matched or missing
        returnDto.setTimeLimit(inputDto.getTimeLimit());
        // Save new token
        GuestCouponAccessTokenEntity newEntity = GuestCouponAccessTokenMapper.INSTANCE.toEntity(inputDto);
        GuestCouponAccessTokenEntity savedEntity = accessTokenRepository.save(newEntity);

        if (savedEntity != null && savedEntity.getTokenId() != null) {
            returnDto.setStatus(Constants.SUCCESS);
            returnDto.setTimeLimit(inputDto.getTimeLimit());
            returnDto.setToken(inputDto.getToken());
        } else {
            returnDto.setStatus(Constants.FAILURE);
        }
        return returnDto;
    }

    public MessTokenAPIDto v1_logOutUserAPI(String token) throws Exception {
        MessTokenAPIDto returnDto = new MessTokenAPIDto();
        //Check active token for this token, if its update to false
        accessTokenRepository.
                findByTokenAndActiveFlagTrue(token)
                .ifPresent(tokenEntity -> {
                    tokenEntity.setActiveFlag(false);
                    accessTokenRepository.save(tokenEntity);
                });

        return returnDto;
    }

    public MessTokenAPIDto v1_createMessTokenForStudent(String token) throws Exception {
        MessTokenAPIDto apiDto = new MessTokenAPIDto();
        try {
            //Validate the token
            apiDto = validateToken(token);
            if (apiDto.getStatus() != null && apiDto.getStatus().equals(ApiConstants.VALID_TOKEN.getString())) {  // ✅ Valid token
                String formattedTime = LocalTime.now().format(DateTimeFormatter.ofPattern(Constants.TIME_FORMAT));
                //Get student and Mess Details
                List<Object[]> resultList = accessTokenRepository.findStudentDetailsWithMessInfoForApi(apiDto.getUserName(), formattedTime, ModelConstants.STATUS_ACTIVE);
                if (!resultList.isEmpty()) {
                    Object[] obj = resultList.get(0);
                    StudentDetailsInfoEntity studentEntity = (StudentDetailsInfoEntity) obj[0];
                    StudentMessDetailsEntity messEntity = obj[1] != null ? (StudentMessDetailsEntity) obj[1] : null;
                    MessSessionEntity sessionEntity = obj[2] != null ? (MessSessionEntity) obj[2] : null;
                    if (studentEntity.getSettlementFlag() != null && studentEntity.getSettlementFlag().trim().equals("N")) {
                        if (messEntity != null && messEntity.getMessMaster().getId() != 0) {
                            apiDto.setMessName(messEntity.getMessMaster().getMessName());
                            if (sessionEntity != null && sessionEntity.getId().getSessionName() != null) {
                                apiDto.setMessName(apiDto.getMessName() + "(" + sessionEntity.getId().getSessionName() + ")");

                                LocalDateTime startOfDay = DateUtility.getStartOfTodayInstant();
                                LocalDateTime endOfDay = DateUtility.getEndOfTodayInstant();

                                Optional<MessQrApplicationEntity> messQrEntityOpt = messQrApplicationRepository.
                                        findTodayQrApplicationsByStudentIdAndSession(apiDto.getUserName(), sessionEntity.getId().getSessionName(),
                                                ModelConstants.STATUS_ACTIVE, startOfDay, endOfDay);
                                if (messQrEntityOpt.isPresent()) {
                                    MessQrApplicationEntity messQrEntity = messQrEntityOpt.get();
                                    //If that QR used for that session then throw return status like below.
                                    if (messQrEntity.getQrUsageStatus() != null && messQrEntity.getQrUsageStatus().equals("Used")) {
                                        apiDto.setStatus("You have already requested and used for the same mess session");
                                    } else {
                                        apiDto.setStatus(Constants.SUCCESS);
                                        apiDto.setQrNumber(messQrEntity.getQrNumber());
                                        apiDto.setQrId(messQrEntity.getQrId());
                                    }
                                } else {
                                    //Save
                                    MessQrApplicationEntity messQrNewEntity = new MessQrApplicationEntity();
                                    messQrNewEntity.setStudentId(apiDto.getUserName().toUpperCase());
                                    messQrNewEntity.setMessId(messEntity.getMessMaster().getId());
                                    messQrNewEntity.setMessSession(sessionEntity.getId().getSessionName());
                                    messQrNewEntity.setQrNumber(new TokenGeneration(6).nextNumericString());
                                    messQrNewEntity.setQrUsageStatus("Generated");
                                    messQrNewEntity.setCreatedBy(apiDto.getUserName().toUpperCase());
                                    messQrNewEntity.setModifiedBy(apiDto.getUserName().toUpperCase());
                                    MessQrApplicationEntity messQrSaved = messQrApplicationRepository.save(messQrNewEntity);
                                    if (messQrSaved != null && messQrSaved.getQrId() != 0) {
                                        apiDto.setStatus(Constants.SUCCESS);
                                        apiDto.setQrNumber(messQrSaved.getQrNumber());
                                        apiDto.setQrId(messQrSaved.getQrId());
                                    }
                                }
                            } else {
                                apiDto.setStatus("This is not a valid mess session");
                            }

                        } else {
                            apiDto.setStatus("You have not allocated to any mess");
                        }
                    } else {
                        apiDto.setStatus("Settlement completed");
                    }


                } else {
                    apiDto.setStatus("Student does not exist");
                }
            } else {
                return apiDto;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return apiDto;
    }

    public MessTokenAPIDto validateMessAndGuestTokens(MessTokenAPIDto inputDto) throws Exception {
        MessTokenAPIDto returnDto = new MessTokenAPIDto();
        try {
            long loginMessId = 0;
            //Validate the token
            returnDto = validateToken(inputDto.getToken());
            if (returnDto.getStatus() != null && returnDto.getStatus().equals(ApiConstants.VALID_TOKEN.getString())) {  // ✅ Valid token
                //Check Current Mess period
                MessMasterControllerDto currentMessPeriod = messMasterCommonService.getCurrentMessPeriod();
                if (currentMessPeriod != null) {
                    //Get logged in user assigned mess details from token
                    Optional<MessCouponUserMappingEntity> messEntity = accessTokenRepository.getMessFromUserToken(inputDto.getToken(), ModelConstants.STATUS_ACTIVE);
                    if (messEntity.isPresent()) {
                        MessCouponUserMappingEntity messEntityEntity = messEntity.get();
                        loginMessId = messEntityEntity.getMessMaster().getId();
                        returnDto.setMessName(messEntityEntity.getMessMaster().getMessName());

                        if (inputDto.getUserType() != null && inputDto.getUserType().equals("S")) { //Mess QR
                            // Further validation of Mess Token QR
                            returnDto = validateMessQr(inputDto, returnDto, loginMessId);
                        } else { //Guest coupon
                            // Further validation of Guest coupon QR
                            returnDto = validateGuestCoupon(inputDto, returnDto, loginMessId);
                        }
                    } else {
                        returnDto.setStatus("Mess not found for this user");
                    }

                } else {
                    returnDto.setStatus("Mess Period is not available");
                }
            } else {
                return returnDto;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return returnDto;
    }

    public MessTokenAPIDto validateToken(String token) throws Exception {
        MessTokenAPIDto returnDto = new MessTokenAPIDto();
        Optional<GuestCouponAccessTokenEntity> optionalToken = accessTokenRepository.findByTokenAndActiveFlagTrue(token);
        if (optionalToken.isPresent()) {
            GuestCouponAccessTokenEntity tokenEntity = optionalToken.get();
            boolean isValid = checkTokenExpiry(tokenEntity);
            if (isValid) {
                // Update last usage time in token table
                tokenEntity.setLastUsed(DateUtility.getNowTimeInstant());
                accessTokenRepository.save(tokenEntity);
                returnDto.setStatus(ApiConstants.VALID_TOKEN.getString());
                returnDto.setUserName(tokenEntity.getUsername());
            } else {
                returnDto.setStatus("Token Expired");
            }
        } else {
            returnDto.setStatus("Invalid Token");
        }
        return returnDto;
    }

    public boolean checkTokenExpiry(GuestCouponAccessTokenEntity tokenEntity) throws Exception {
        Long timeLimit = tokenEntity.getTimeLimit(); // in milliseconds
        LocalDateTime lastUsed = tokenEntity.getLastUsed();
        // If never used, consider valid
        boolean isValid = (lastUsed == null);
        if (!isValid && timeLimit != null) {
            long elapsedMillis = Duration.between(lastUsed, DateUtility.getNowTimeInstant()).toMillis();
            isValid = elapsedMillis < timeLimit;
        }
        return isValid;
    }

    public MessTokenAPIDto validateMessQr(MessTokenAPIDto inputDto, MessTokenAPIDto returnDto, long loginMessId) throws Exception {
        List<Object[]> resultList = messQrApplicationRepository.getMessTokenQrDetails(inputDto.getQrId(), inputDto.getQrNumber(), ModelConstants.STATUS_ACTIVE);
        if (!resultList.isEmpty()) {
            Object[] obj = resultList.get(0);
            MessQrApplicationEntity messQrEntity = obj[0] != null ? (MessQrApplicationEntity) obj[0] : null;
            MessMasterEntity mmEntity = obj[1] != null ? (MessMasterEntity) obj[1] : null;

            Long assignedMessId = messQrEntity != null ? messQrEntity.getMessId() : 0L;
            String assignedMessName = mmEntity != null ? mmEntity.getMessName() : "";
            //check the login mess id and assigned mess id same or not
            if (assignedMessId == loginMessId) {
                //Get Mess Sessions
                String formattedTime = LocalTime.now().format(DateTimeFormatter.ofPattern(Constants.TIME_FORMAT));
                Optional<MessSessionEntity> sessionEntity = messSessionRepository.checkMessSession(loginMessId, messQrEntity.getMessSession(),
                        ModelConstants.STATUS_ACTIVE, formattedTime);
                if (sessionEntity.isPresent()) {
                    String qrUsedStatus = messQrEntity.getQrUsageStatus();
                    switch (qrUsedStatus) {
                        case "Generated" -> returnDto.setStatus("Success");
                        case "Used" -> returnDto.setStatus("Already Used");
                        default -> returnDto.setStatus("Could not get Qr usage status");
                    }
                    if (returnDto.getStatus() != null && returnDto.getStatus().equals(Constants.SUCCESS)) {
                        //Update QR usage details
                        messQrEntity.setMessId(loginMessId);
                        messQrEntity.setQrUsageStatus("Used");
                        messQrEntity.setQrUsageDate(LocalDate.now());
                        messQrEntity.setQrUsageTime(DateUtility.getNowTimeInstant());
                        messQrEntity.setModifiedBy(returnDto.getUserName());
                        messQrApplicationRepository.save(messQrEntity);
                    }
                } else {
                    returnDto.setStatus("Invalid Mess Session");
                }
            } else {
                returnDto.setStatus("This coupon was assigned to " + assignedMessName + ". Not for this Mess");
            }

        } else {
            returnDto.setStatus("Invalid Mess Qr");
        }
        return returnDto;
    }

    public MessTokenAPIDto validateGuestCoupon(MessTokenAPIDto inputDto, MessTokenAPIDto returnDto, long loginMessId) throws Exception {
        List<Object[]> resultList = guestCouponRepository.getCouponDetailsByQrNumberAndQrId(inputDto.getQrId(), inputDto.getQrNumber(), ModelConstants.STATUS_ACTIVE);
        if (!resultList.isEmpty()) {
            Object[] obj = resultList.get(0);
            GuestCouponMappingsEntity mappingEntity = obj[0] != null ? (GuestCouponMappingsEntity) obj[0] : null;
            GuestCouponPaymentAdviceEntity adviceEntity = obj[1] != null ? (GuestCouponPaymentAdviceEntity) obj[1] : null;
            MessMasterEntity messEntity = obj[2] != null ? (MessMasterEntity) obj[2] : null;

            if(adviceEntity!=null && adviceEntity.getPaymentStatus().equals(Constants.SUCCESS)) {
                LocalDate validityDate = mappingEntity.getToDate();
                Long assignedMessId = adviceEntity != null ? adviceEntity.getMessId() : 0L;
                String assignedMessName = messEntity != null ? messEntity.getMessName() : "";

                // Compare current date with validity date
                LocalDate nowDate = LocalDate.now();
                int comparison = nowDate.compareTo(validityDate);
                if (comparison > 0) {
                    returnDto.setStatus("Coupon Expired");
                } else if (comparison < 0) {
                    returnDto.setStatus("Coupon Not Valid for Current date");
                } else {
                    //check the login mess id and assigned mess id same or not)
                    if (assignedMessId == loginMessId) {
                        //Get Mess Sessions
                        String formattedTime = LocalTime.now().format(DateTimeFormatter.ofPattern(Constants.TIME_FORMAT));
                        Optional<MessSessionEntity> sessionEntity = messSessionRepository.checkMessSession(loginMessId, mappingEntity.getCouponType(),
                                ModelConstants.STATUS_ACTIVE, formattedTime);
                        if (sessionEntity.isPresent()) {
                            String couponUsedStatus = mappingEntity.getUsedStatus();
                            switch (couponUsedStatus) {
                                case "Distributed" -> returnDto.setStatus("Success");
                                case "Used" -> returnDto.setStatus("Already Used");
                                case "Returned" -> returnDto.setStatus("Returned");
                                default -> returnDto.setStatus("Could not get coupon usage status");
                            }
                            if (returnDto.getStatus() != null && returnDto.getStatus().equals(Constants.SUCCESS)) {
                                //Update Coupon usage details
                                mappingEntity.setMessId(loginMessId);
                                mappingEntity.setUsedStatus("Used");
                                mappingEntity.setUsedDate(LocalDate.now());
                                mappingEntity.setUsedTime(DateUtility.getNowTimeInstant());
                                mappingEntity.setModifiedBy(returnDto.getUserName());
                                guestCouponMappingsRepository.save(mappingEntity);
                            }
                        } else {
                            returnDto.setStatus("Invalid Mess Session");
                        }
                    } else {
                        returnDto.setStatus("This coupon was assigned to " + assignedMessName + ". Not for this Mess");
                    }
                }
            }else {
                returnDto.setStatus("Payment Not Done");
            }
        } else {
            returnDto.setStatus("Invalid Coupon");
        }
        return returnDto;
    }

    public List<StudentInfoAPIDto>  getStudentDetails(String studentId, long hostelId, long roomId) throws Exception {

        return  allStudentsDetailsViewRepository.getStudentDetailsForApp(studentId, hostelId, roomId);
    }

    public MessTokenAPIDto v1_getStudentRFIDInfo(MessTokenAPIDto inputDto) throws Exception {
        MessTokenAPIDto returnDto = new MessTokenAPIDto();
        List<Object[]> resultList = userFpCardRepository.checkUserFpAndStudentMessDetails(inputDto.getCardSerialNo(), ModelConstants.STATUS_ACTIVE);
        if (!resultList.isEmpty()) {
            Object[] obj = resultList.get(0);
            UserFpCardEntity userFpEntity = obj[0] != null ? (UserFpCardEntity) obj[0] : null;
            AllStudentsDetailsViewEntity studEntity = obj[1] != null ? (AllStudentsDetailsViewEntity) obj[1] : null;
            if (studEntity != null && studEntity.getStudentId() != null) {
                if (userFpEntity != null && userFpEntity.getPinNo().equals(inputDto.getPinNumber())) {
                    if (studEntity.getMessId() !=null && studEntity.getMessId() != 0) {
                        if (inputDto.getMessId() == studEntity.getMessId()) {
                            returnDto.setCardStatus(0); //All checks pass
                            returnDto.setStudentName(studEntity.getStudentName());
                            returnDto.setStudID(studEntity.getStudentId());
                            returnDto.setMessId(studEntity.getMessId());

                            // Configuration for food court mess id
                            String fcmessIds = simsConfigDataService.getSimConfigValue(SimsConfigDataService.FOOD_COURT_MESS_IDS);
                            Set<String> fcMessIds = Arrays.stream(fcmessIds.split(","))
                                    .map(String::trim)
                                    .collect(Collectors.toSet());
                            if (fcMessIds.contains(String.valueOf(inputDto.getMessId()))) {
                                // Foodcourt purchase
                                returnDto.setAmount(accessTokenRepository.getFoodCourtBalance(returnDto.getStudID(),returnDto.getMessId()));
                                System.out.println("returnDto after food court balance set in service ===> " + returnDto.getAmount());
                            } else {
                                // Extras purchase
                               showStudentDetailRepository.checkStudentBalance(returnDto.getStudID());
                                Optional<StudentNetBalanceEntity> studentNetBalanceEntityOptional = studentNetBalanceEntityRepository.findByStudentId(returnDto.getStudID());
                                System.out.println("studentNetBalanceEntityOptional present ===> " + studentNetBalanceEntityOptional.isPresent());
                                if (studentNetBalanceEntityOptional.isPresent()) {
                                    StudentNetBalanceEntity balanceEntity = studentNetBalanceEntityOptional.get();
                                    System.out.println("returnDto student net balance get in studentNetBalanceEntityOptional in service ===> " + balanceEntity.getNetBalanceCard());
                                    returnDto.setAmount(balanceEntity.getNetBalanceCard() != null ? balanceEntity.getNetBalanceCard() : 0L);
                                }
                            }

                        } else {
                            returnDto.setCardStatus(6); // Student Not assigned to this mess
                            return returnDto;
                        }
                    } else {
                        returnDto.setCardStatus(5); // Student not assigned to any mess
                        return returnDto;
                    }
                } else {
                    returnDto.setCardStatus(2); // Wrong PIN
                    return returnDto;
                }
            } else {
                returnDto.setCardStatus(1); // Student is Not Available
                return returnDto;
            }
        } else {
            returnDto.setCardStatus(3); //Card ID not available
            return returnDto;
        }
        return returnDto;
    }

    @Transactional
    public MessTokenAPIDto v1_saveMessBill(MessTokenAPIDto inputDto) throws Exception {
        MessTokenAPIDto returnDto = new MessTokenAPIDto();
        // Configuration for food court mess id
        String fcmessIds = simsConfigDataService.getSimConfigValue(SimsConfigDataService.FOOD_COURT_MESS_IDS);
        Set<String> fcMessIds = Arrays.stream(fcmessIds.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
        /**  Check whether this mess was food court or not,
        *  If it is food court mess, then Save in Food court purchase
        *  else Save in Extras purchase **/
        if (fcMessIds.contains(String.valueOf(inputDto.getMessId()))) {
            // Foodcourt purchase save
            returnDto= saveFoodCourtBilling(inputDto,returnDto);
        } else {
            // Extras purchase save
            returnDto= saveExtrasMessBilling(inputDto,returnDto);
        }
        return returnDto;
    }


    public MessTokenAPIDto saveExtrasMessBilling(MessTokenAPIDto inputDto, MessTokenAPIDto returnDto) {

        String vendorCode = messAllocationEntityRepository.findRateAndVendorCodeByMessMasterId(inputDto.getMessId(),ModelConstants.STATUS_ACTIVE);
        MessMasterDto mmDto= messMasterService.getMessMasterDetailsById(inputDto.getMessId());
        FinancialYearDto finYearDto = accountHeadService.getFinYearDto();
        Integer nextValMessLedger = messLedgerARepository.getNextValMessBillingSeq();
        String voucherNumber="EX"+nextValMessLedger;
        //save mess ledger A
        TransactionDto transDtoA = new TransactionDto();
        transDtoA.setBookType(Constants.CREDIT_CARD);
        transDtoA.setVoucherNo(voucherNumber);
        transDtoA.setDate(DateUtility.dateTimeStrToLocalDate(inputDto.getBillingDate()));
        transDtoA.setAccHead(vendorCode);
        transDtoA.setSubAccHead(Constants.NIL);
        transDtoA.setDescription(inputDto.getBillNo()+" "+inputDto.getBillingDate()+" "+inputDto.getStudID());
        transDtoA.setAmount(inputDto.getAmount());
        transDtoA.setCancelStatus(ModelConstants.STATUS_INACTIVE);
        transDtoA.setDocRefNo(inputDto.getBillNo());
        transDtoA.setDebitOrCredit(Constants.CREDIT);
        MessLedgerAEntity messLedgerAEntity = TransferAmountUtils
                .createMessLedgerAEntity(transDtoA, finYearDto.getFinYear());
        messLedgerAEntity.setCreatedBy(inputDto.getStudID());
        messLedgerAEntity.setModifiedBy(inputDto.getStudID());
        messLedgerARepository.save(messLedgerAEntity);

        //save mess ledger B
        TransactionDto transDtoB = new TransactionDto();
        transDtoB.setBookType(Constants.CREDIT_CARD);
        transDtoB.setVoucherNo(voucherNumber);
        transDtoB.setDate(DateUtility.dateTimeStrToLocalDate(inputDto.getBillingDate()));
        transDtoB.setAccHead(inputDto.getStudID());
        transDtoB.setSubAccHead(Constants.NIL);
        transDtoB.setDescription("Purchased Extras at "+ mmDto.getMessName());
        transDtoB.setDocRefNo(inputDto.getBillNo());
        transDtoB.setAmount(inputDto.getAmount());
        transDtoB.setCancelStatus(ModelConstants.STATUS_INACTIVE);
        transDtoB.setDebitOrCredit(Constants.DEBIT);
        MessLedgerBEntity messLedgerBEntity = TransferAmountUtils
                .createMessLedgerBEntity(transDtoB, finYearDto.getFinYear(), 0, 1);
        messLedgerBEntity.setCreatedBy(inputDto.getStudID());
        messLedgerBEntity.setModifiedBy(inputDto.getStudID());
        messLedgerBRepository.save(messLedgerBEntity);

        returnDto.setSaveStatus(1);
        returnDto.setVoucherNo(voucherNumber);

        return returnDto;
    }

    public MessTokenAPIDto saveFoodCourtBilling(MessTokenAPIDto inputDto, MessTokenAPIDto returnDto) {
        returnDto.setSaveStatus(0);
        MessMasterControllerDto currentMessPeriodDto = messMasterCommonService.getCurrentMessPeriod();
        if(currentMessPeriodDto!=null){
            //Save food court purchases
            FoodCourtLedgerEntity fcLedgerEntity = new FoodCourtLedgerEntity();
            fcLedgerEntity.setMessMaster(MessMasterMapper.INSTANCE
                    .toMessMasterEntity(messMasterService.getMessMasterDetailsById(inputDto.getMessId())));
            fcLedgerEntity.setStudent(StudentDetailsInfoMapper.INSTANCE
                    .toEntity(studentDetailsInfoService.getStudentInfoDetails(inputDto.getStudID().toUpperCase())));
            fcLedgerEntity.setMessPeriodId(currentMessPeriodDto.getId());
            fcLedgerEntity.setAmount(inputDto.getAmount());
            fcLedgerEntity.setPurchaseTimestamp(DateUtility.getNowTimeInstant());
            fcLedgerEntity.setDebitOrCredit(Constants.DEBIT);
            fcLedgerEntity.setDescription(messageSource.getMessage("message.label.food.court.purchase.api.description", null, Locale.getDefault()));
            fcLedgerEntity.setAmount(inputDto.getAmount());
            fcLedgerEntity.setMessBillingNo(inputDto.getBillNo());
            fcLedgerEntity.setTerminalIp(inputDto.getTerminalIp());
            fcLedgerEntity.setPurchaseTimestamp(DateUtility.getNowTimeInstant());
            fcLedgerEntity.setCreatedBy(inputDto.getStudID());
            fcLedgerEntity.setModifiedBy(inputDto.getStudID());
            FoodCourtLedgerEntity afterSave = foodCourtLedgerRepository.save(fcLedgerEntity);
            if(afterSave.getId()!=null && afterSave.getId()!=0){
                returnDto.setSaveStatus(1);
                returnDto.setVoucherNo("EX"+afterSave.getId());
            }
        }
    return returnDto;
    }

}