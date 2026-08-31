package com.iitm.hosteldine.controller.api;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.api.ApiConstants;
import com.iitm.hosteldine.dto.api.MessTokenAPIDto;
import com.iitm.hosteldine.dto.api.StudentInfoAPIDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.hostel.HostelRoomInfoDto;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.service.api.MessTokenAPIService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.hostel.HostelRoomInfoService;
import com.iitm.hosteldine.service.transactions.ReceiptEntryService;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "${url.mess.token.api}")
@Slf4j
public class MessTokenAPIController {

    private final MessTokenAPIService messTokenAPIService;
    private final HostelMasterService hostelMasterService;
    private final HostelRoomInfoService hostelRoomInfoService;
    private final StudentBioDataService studentBioDataService;
    private final ReceiptEntryService receiptEntryService;
    private final CommonResponseUtil commonResponseUtil;

    @GetMapping("/v1_authenticateForMessAPI")
    public ResponseEntity<?> v1_authenticateForMessAPI(HttpServletRequest request, HttpServletResponse response,
                                                       @RequestParam(required = true) String loginId,
                                                       @RequestParam(required = true) String password,
                                                       @RequestParam(required = true) String mac,
                                                       @RequestParam(required = true) String source) {
        try {
            MessTokenAPIDto returnDto = new MessTokenAPIDto();
            if (loginId != null && !loginId.isEmpty()) {
                MessTokenAPIDto apiDto = new MessTokenAPIDto();
                apiDto.setUserName(loginId);
                apiDto.setPassword(password);
                apiDto.setMacId(mac);
                apiDto.setSource(source);

                // Business logic
                returnDto = messTokenAPIService.v1_authenticateForMessAPI(apiDto);
            }
            response.setContentType("application/json");
            return ResponseEntity.ok(returnDto);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(ApiConstants.STATUS.getString(), Constants.ERROR);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/v1_logOutUserAPI")
    public ResponseEntity<?> v1_logOutUserAPI(HttpServletRequest request, HttpServletResponse response,
                                              @RequestParam(required = true) String token) {
        Map<String, Object> outResponse = new HashMap<>();
        try {
            MessTokenAPIDto returnDto = new MessTokenAPIDto();
            if (token != null && !token.isEmpty()) {
                returnDto = messTokenAPIService.v1_logOutUserAPI(token);
            } else {
                outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.TOKEN_MISSING.toString());
            }
            response.setContentType("application/json");

            outResponse.put(ApiConstants.STATUS.getString(), Constants.LOGOUT);
            return ResponseEntity.ok(outResponse);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(ApiConstants.STATUS.getString(), Constants.ERROR);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/v1_createMessTokenForStudent")
    public ResponseEntity<?> v1_createMessTokenForStudent(HttpServletRequest request, HttpServletResponse response,
                                                          @RequestParam(required = true) String token) {
        Map<String, Object> outResponse = new HashMap<>();
        try {
            MessTokenAPIDto returnDto = new MessTokenAPIDto();
            if (token != null && !token.isEmpty()) {
                returnDto = messTokenAPIService.v1_createMessTokenForStudent(token);

                outResponse.put(ApiConstants.STATUS.getString(), returnDto.getStatus());
                outResponse.put(ApiConstants.QR_NUMBER.getString(), returnDto.getQrNumber());
                outResponse.put(ApiConstants.QR_ID.getString(), returnDto.getQrId());
                outResponse.put(ApiConstants.MESS_NAME.getString(), returnDto.getMessName());
            } else {
                outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.TOKEN_MISSING.toString());
            }
            response.setContentType("application/json");
            return ResponseEntity.ok(outResponse);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(ApiConstants.STATUS.getString(), Constants.ERROR);
            return ResponseEntity.internalServerError().body(errorResponse);
        }

    }

    @GetMapping("/v1_validateMessAndGuestTokens")
    public ResponseEntity<Map<String, Object>> v1_validateMessAndGuestTokens(HttpServletRequest request, HttpServletResponse response,
                                                                             @RequestParam(required = true) String qrDetails,
                                                                             @RequestParam(required = true) long id,
                                                                             @RequestParam(required = true) String userType,
                                                                             @RequestParam(required = true) String token) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            if (qrDetails != null && !qrDetails.isEmpty()) {
                MessTokenAPIDto apiDto = new MessTokenAPIDto();
                apiDto.setQrId(id);
                apiDto.setQrNumber(qrDetails);
                apiDto.setUserType(userType);
                apiDto.setToken(token);

                // Business logic
                MessTokenAPIDto returnDto = messTokenAPIService.validateMessAndGuestTokens(apiDto);

                responseMap.put(ApiConstants.STATUS.getString(), returnDto.getStatus());
                if (returnDto.getStatus() != null && returnDto.getStatus().equals(Constants.SUCCESS)) {
                    responseMap.put(ApiConstants.MESS_NAME.getString(), returnDto.getMessName());
                    responseMap.put(ApiConstants.MESSAGE.getString(), ApiConstants.COUPON_SUCCESS.getString());
                }
            } else {
                responseMap.put(ApiConstants.STATUS.getString(), "Invalid Qr");
            }
            response.setContentType("application/json");
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            responseMap.put(ApiConstants.STATUS.getString(), Constants.ERROR);
            return ResponseEntity.internalServerError().body(responseMap);

        }
    }


    @GetMapping("/v1_getHostelList")
    public ResponseEntity<?> v1_getHostelList(HttpServletRequest request, HttpServletResponse response,
                                              @RequestParam(required = true) String token) {
        Map<String, Object> outResponse = new HashMap<>();

        try {
            MessTokenAPIDto returnDto = new MessTokenAPIDto();
            if (token != null && !token.isEmpty()) {
                returnDto = messTokenAPIService.validateToken(token);
                if (returnDto.getStatus() != null && returnDto.getStatus().equals(ApiConstants.VALID_TOKEN.getString())) {
                    List<HostelMasterDto> hostelList = hostelMasterService.getHostelList();
                    if (hostelList.size() > 0) {
                        List<Map<String, Object>> simplifiedList = hostelList.stream()
                                .map(hostel -> {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("hostelId", hostel.getId());
                                    map.put("hostelName", hostel.getHostelName());
                                    return map;
                                })
                                .collect(Collectors.toList());

                        outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.AVAILABLE.getString());
                        outResponse.put(ApiConstants.SIZE.getString(), simplifiedList.size());
                        outResponse.put("list", simplifiedList);
                    } else {
                        outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.UNAVAILABLE.getString());
                        outResponse.put(ApiConstants.MESSAGE.getString(), ApiConstants.NO_HOSTEL.getString());
                        outResponse.put(ApiConstants.SIZE.getString(), 0);
                    }
                } else {
                    outResponse.put(ApiConstants.STATUS.getString(), returnDto.getStatus() == null ? Constants.ERROR : returnDto.getStatus());
                }
            } else {
                outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.TOKEN_MISSING.toString());
            }
            response.setContentType("application/json");
            return ResponseEntity.ok(outResponse);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(ApiConstants.STATUS.getString(), Constants.ERROR);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/v1_roomList")
    public ResponseEntity<?> v1_roomList(HttpServletRequest request, HttpServletResponse response,
                                         @RequestParam(required = true) String token,
                                         @RequestParam(required = true) long hostelId) {
        Map<String, Object> outResponse = new HashMap<>();
        try {
            MessTokenAPIDto returnDto = new MessTokenAPIDto();
            if (hostelId != 0) {
                if (token != null && !token.isEmpty()) {
                    returnDto = messTokenAPIService.validateToken(token);
                    if (returnDto.getStatus() != null && returnDto.getStatus().equals(ApiConstants.VALID_TOKEN.getString())) {
                        List<HostelRoomInfoDto> roomsList = hostelRoomInfoService.getRoomListByHostelId(hostelId);
                        if (roomsList.size() > 0) {
                            List<Map<String, Object>> simplifiedList = roomsList.stream()
                                    .map(room -> {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("roomId", room.getId());
                                        map.put("roomNo", room.getRoomNo());
                                        return map;
                                    })
                                    .collect(Collectors.toList());

                            outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.AVAILABLE.getString());
                            outResponse.put(ApiConstants.SIZE.getString(), simplifiedList.size());
                            outResponse.put("list", simplifiedList);
                        } else {
                            outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.UNAVAILABLE.getString());
                            outResponse.put(ApiConstants.MESSAGE.getString(), ApiConstants.NO_ROOMS.getString());
                            outResponse.put(ApiConstants.SIZE.getString(), 0);
                        }
                    } else {
                        outResponse.put(ApiConstants.STATUS.getString(), returnDto.getStatus() == null ? Constants.ERROR : returnDto.getStatus());
                    }
                } else {
                    outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.TOKEN_MISSING.toString());
                }
            } else {
                outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.HOSTEL_ID_CHECK.getString());
            }

            response.setContentType("application/json");
            return ResponseEntity.ok(outResponse);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(ApiConstants.STATUS.getString(), Constants.ERROR);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }


    @GetMapping("/v1_getStudentDetailsBySearch")
    public ResponseEntity<?> v1_getStudentDetailsBySearch(HttpServletRequest request, HttpServletResponse response,
                                                          @RequestParam(required = true) String token,
                                                          @RequestParam(required = false) String hostelId,
                                                          @RequestParam(required = false) String roomId,
                                                          @RequestParam(required = false) String studentId) {
        Map<String, Object> outResponse = new HashMap<>();
        try {
            MessTokenAPIDto returnDto = new MessTokenAPIDto();

            if (token == null || token.isEmpty()) {
                outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.TOKEN_MISSING.toString());
                return ResponseEntity.badRequest().body(outResponse);
            }

            boolean isHostelIdPresent = (hostelId != null && !hostelId.isEmpty());
            boolean isRoomIdPresent = (roomId != null && !roomId.isEmpty());
            boolean isStudentIdPresent = (studentId != null && !studentId.isEmpty());

            if (isHostelIdPresent || isRoomIdPresent) {
                // ✅ If one is present, both must be present
                if (!(isHostelIdPresent && isRoomIdPresent)) {
                    outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.UNAVAILABLE.getString());
                    outResponse.put(ApiConstants.MESSAGE.getString(), "Both hostelId and roomId are required together.");
                    return ResponseEntity.badRequest().body(outResponse);
                }
            } else {
                if (!isStudentIdPresent) {
                    outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.UNAVAILABLE.getString());
                    outResponse.put(ApiConstants.MESSAGE.getString(), "StudentId is required.");
                    return ResponseEntity.badRequest().body(outResponse);
                } else {
                    String status = receiptEntryService.validateStudentId(studentId);
                    if (Objects.nonNull(status)) {
                        outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.UNAVAILABLE.getString());
                        outResponse.put(ApiConstants.MESSAGE.getString(), commonResponseUtil.getMessage(status));
                        outResponse.put(ApiConstants.SIZE.getString(), 0);
                        return ResponseEntity.badRequest().body(outResponse);
                    }
                }
            }

            returnDto = messTokenAPIService.validateToken(token);
            if (returnDto.getStatus() != null && returnDto.getStatus().equals(ApiConstants.VALID_TOKEN.getString())) {
                if ((isHostelIdPresent && isRoomIdPresent) || isStudentIdPresent) {
                    int hostelIdInt = isHostelIdPresent ? Integer.parseInt(hostelId) : 0;
                    int roomIdInt = isRoomIdPresent ? Integer.parseInt(roomId) : 0;
                    List<StudentInfoAPIDto> studDetailsList = messTokenAPIService.getStudentDetails(studentId, hostelIdInt, roomIdInt);
                    if (studDetailsList.size() > 0) {
                        List<Map<String, Object>> outputList = studDetailsList.stream()
                                .map(dto -> {
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("studentId", dto.getStudentId());
                                    map.put("name", dto.getStudentName());
                                    map.put("hostel", dto.getHostelName());
                                    map.put("roomNo", dto.getRoomNumber());
                                    map.put("seat", dto.getSeat());
                                    map.put("gender", dto.getGender());
                                    map.put("category", dto.getCategory());
                                    map.put("studentMobileNo", dto.getStudentMobile());
                                    map.put("parentDetails", dto.getFamilyMobileNo());
                                    map.put("finalBalance", dto.getFinalBalance());
                                    return map;
                                })
                                .collect(Collectors.toList());

                        outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.AVAILABLE.getString());
                        outResponse.put(ApiConstants.MESSAGE.getString(), ApiConstants.STUDENT_LIST_AVAILABLE.getString());
                        outResponse.put(ApiConstants.SIZE.getString(), outputList.size());
                        outResponse.put("list", outputList);
                    } else {
                        outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.UNAVAILABLE.getString());
                        outResponse.put(ApiConstants.MESSAGE.getString(), ApiConstants.STUDENT_LIST_UNAVAILABLE.getString());
                        outResponse.put(ApiConstants.SIZE.getString(), 0);
                    }
                }
            } else {
                outResponse.put(ApiConstants.STATUS.getString(), returnDto.getStatus() == null ? Constants.ERROR : returnDto.getStatus());
            }


            response.setContentType("application/json");
            return ResponseEntity.ok(outResponse);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(ApiConstants.STATUS.getString(), Constants.ERROR);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }


    @GetMapping("/v1_downloadBioDataPDF")
    public ResponseEntity<?> v1_downloadBioDataPDF(HttpServletRequest request, HttpServletResponse response,
                                                   @RequestParam(required = true) String token,
                                                   @RequestParam(required = true) String studentId) {
        Map<String, Object> outResponse = new HashMap<>();
        try {
            MessTokenAPIDto returnDto = new MessTokenAPIDto();
            if (studentId != null && !studentId.isEmpty()) {
                if (token != null && !token.isEmpty()) {
                    returnDto = messTokenAPIService.validateToken(token);
                    if (returnDto.getStatus() != null && returnDto.getStatus().equals(ApiConstants.VALID_TOKEN.getString())) {
                        Resource resource = studentBioDataService.getStudentBioDataPDF(studentId.toUpperCase(), true);
                        if (resource != null && resource.exists()) {
                            return Utility.prepareDownloadFile(resource);
                        } else {
                            outResponse.put(ApiConstants.STATUS.getString(), Constants.ERROR);
                            outResponse.put(ApiConstants.MESSAGE.getString(), "PDF not found for student.");
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(outResponse);
                        }
                    } else {
                        outResponse.put(ApiConstants.STATUS.getString(), returnDto.getStatus() == null ? Constants.ERROR : returnDto.getStatus());
                    }
                } else {
                    outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.TOKEN_MISSING.toString());
                }
            } else {
                outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.STUDENT_MISSING.getString());
            }

            response.setContentType("application/json");
            return ResponseEntity.ok(outResponse);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(ApiConstants.STATUS.getString(), Constants.ERROR);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

}
