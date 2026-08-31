package com.iitm.hosteldine.controller.api;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.api.ApiConstants;
import com.iitm.hosteldine.dto.api.MessTokenAPIDto;
import com.iitm.hosteldine.service.api.MessTokenAPIService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "${url.mess.pos.api}")
@Slf4j
public class MessPosAPIController {

    private final MessTokenAPIService messTokenAPIService;

    @GetMapping("/v1_authenticateMessPOSUser")
    public ResponseEntity<?> v1_authenticateMessPOSUser(HttpServletRequest request, HttpServletResponse response,
                                                        @RequestParam(required = true) String loginId,
                                                        @RequestParam(required = true) String password,
                                                        @RequestParam(required = true) String deviceIp,
                                                        @RequestParam(required = false) String dayName,
                                                        @RequestParam(required = false) String currentTime) {
        Map<String, Object> outResponse = new HashMap<>();
        try {
            MessTokenAPIDto returnDto = new MessTokenAPIDto();
            if (loginId != null && !loginId.isEmpty()) {
                MessTokenAPIDto apiDto = new MessTokenAPIDto();
                apiDto.setUserName(loginId);
                apiDto.setPassword(password);
                apiDto.setTerminalIp(deviceIp);

                // Business logic
                returnDto = messTokenAPIService.v1_authenticateForMessAPI(apiDto);
                response.setContentType("application/json");
                return ResponseEntity.ok(returnDto);
            } else {
                outResponse.put(ApiConstants.STATUS.getString(), ApiConstants.LOGIN_ID_MISSING.toString());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(outResponse);
            }


        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(ApiConstants.STATUS.getString(), Constants.ERROR);
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/v1_getStudentRFIDInfo")
    public ResponseEntity<?> v1_getStudentRFIDInfo(HttpServletRequest request, HttpServletResponse response,
                                                   @RequestParam(required = true) String cardsn,
                                                   @RequestParam(required = true) String messId,
                                                   @RequestParam(required = true) String pinNo) {
        Map<String, Object> outResponse = new HashMap<>();
        Map<String, Object> cardDetails = new HashMap<>();

        try {
            MessTokenAPIDto apiDto = new MessTokenAPIDto();
            apiDto.setCardSerialNo(cardsn);
            apiDto.setMessId(messId != null ? Long.valueOf(messId) : 0);
            apiDto.setPinNumber(pinNo);
            if (apiDto.getMessId() != null && apiDto.getMessId() != 0) {
                MessTokenAPIDto returnDto = messTokenAPIService.v1_getStudentRFIDInfo(apiDto);
                cardDetails.put("cardStatus", returnDto.getCardStatus());
                System.out.println("cardDetails before card status check in controller ===> " + cardDetails);
                if (returnDto.getCardStatus() == 0) { // Card is valid
                    cardDetails.put("amount", returnDto.getAmount());
                    cardDetails.put("studID", returnDto.getStudID());
                    cardDetails.put("StudentName", returnDto.getStudentName());
                }
                System.out.println("cardDetails after card status check in controller ===> " + cardDetails);
            } else {
                System.out.println("cardDetails if mess id is invalid or 0 in controller ===> " + cardDetails);
                // If messId is invalid or 0, set cardStatus to indicate error (e.g., 1)
                cardDetails.put("cardStatus", 7);
            }
            outResponse.put(ApiConstants.CARD_DETAILS.getString(), cardDetails);
            response.setContentType("application/json");
            System.out.println("outResponse in controller ===> " + outResponse);
            return ResponseEntity.ok(outResponse);

        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(ApiConstants.STATUS.getString(), Constants.ERROR);
            return ResponseEntity.internalServerError().body(errorResponse);
        }

    }


    @GetMapping("/v1_saveMessBill")
    public ResponseEntity<?> v1_saveMessBill(HttpServletRequest request, HttpServletResponse response,
                                             @RequestParam(required = true) String userId,
                                             @RequestParam(required = true) String billNo,
                                             @RequestParam(required = true) String billingAmount,
                                             @RequestParam(required = true) String billingDate,
                                             @RequestParam(required = true) String messId,
                                             @RequestParam(required = true) String ipAddress) {
        Map<String, Object> outResponse = new HashMap<>();
        Map<String, Object> studentDetails = new HashMap<>();

        try {
            MessTokenAPIDto apiDto = new MessTokenAPIDto();
            apiDto.setStudID(userId);
            apiDto.setBillNo(billNo);
            apiDto.setAmount(billingAmount != null ? Double.valueOf(billingAmount) : 0.0);
            apiDto.setBillingDate(billingDate);
            apiDto.setTerminalIp(ipAddress);
            apiDto.setMessId(messId != null ? Long.valueOf(messId) : 0);

            if (apiDto.getMessId() != null && apiDto.getMessId() != 0) {
                MessTokenAPIDto returnDto = messTokenAPIService.v1_saveMessBill(apiDto);
                if (returnDto.getSaveStatus() == 1) {
                    studentDetails.put("saveStatus", returnDto.getSaveStatus());
                    studentDetails.put("studID", apiDto.getStudID());
                    studentDetails.put("voucherNo", returnDto.getVoucherNo());
                    studentDetails.put("billDate", apiDto.getBillingDate());
                } else {
                    studentDetails.put("saveStatus", returnDto.getSaveStatus());
                }
            } else {
                // If messId is invalid or 0
                studentDetails.put("saveStatus", ApiConstants.MESS_ID_MISSING.toString());
            }
            outResponse.put("studentDetails", studentDetails);
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
