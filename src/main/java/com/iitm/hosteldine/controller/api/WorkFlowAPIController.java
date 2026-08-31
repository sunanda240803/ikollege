package com.iitm.hosteldine.controller.api;

import com.iitm.hosteldine.dto.api.WorkFlowStudentAPIDto;
import com.iitm.hosteldine.dto.api.WorkFlowWardenAPIDto;
import com.iitm.hosteldine.service.api.WorkFlowAPIService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "${url.workflow.api}")
@Slf4j
public class WorkFlowAPIController {
    @Value("${student_data_transfer_api_url}")
    private String studentUrl;

    @Value("${warden_data_transfer_api_url}")
    private String wardenUrl;

    @Value("${api_data_transfer_ip_list}")
    private String ipList;

    @Value("${api_data_transfer_token}")
    private String tokenConfig;

    private final WorkFlowAPIService workFlowAPIService;

    @GetMapping("/student")
    public ResponseEntity<Map<String, Object>> getStudentDetailsJSON(HttpServletRequest request, HttpServletResponse response,
                                                   @RequestParam(required = true) String token,
                                                   @RequestParam(required = false) String fromDate,
                                                   @RequestParam(required = false) String toDate) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Optional<ResponseEntity<Map<String, Object>>> validationResult = checkConditions(request,token,studentUrl);
            if (validationResult.isPresent()) {
                return validationResult.get(); // Return early with error response
            }
            // Date validations
            if ((fromDate == null || fromDate.isEmpty() || "null".equals(fromDate))
                    && (toDate == null || toDate.isEmpty() || "null".equals(toDate))) {
                // Allowed - both empty
            } else if (fromDate == null || fromDate.isEmpty() || "null".equals(fromDate)
                    || toDate == null || toDate.isEmpty() || "null".equals(toDate)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("status", "Both fromDate and toDate should be provided or both should be empty."));
            } else if (!isValidDateFormat(fromDate) || !isValidDateFormat(toDate)) {
                return ResponseEntity.badRequest()
                        .body(Map.of("status", "Date format must be yyyy-MM-dd (e.g., 2025-01-01)."));
            }
            // Business logic
            List<WorkFlowStudentAPIDto> studentList = workFlowAPIService.getStudentDetailsJSON(fromDate, toDate);
            responseMap.put("student_list", (studentList != null && !studentList.isEmpty()) ? studentList : "no records found");
            responseMap.put("status", "success");
            response.setContentType("application/json");
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            responseMap.put("status", "error");
            return ResponseEntity.internalServerError().body(responseMap);
        }
    }

    @GetMapping("/warden")
    public ResponseEntity<Map<String, Object>> getWardenDetailsJSON(HttpServletRequest request,HttpServletResponse response,
                                                  @RequestParam(required = true) String token) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Optional<ResponseEntity<Map<String, Object>>> validationResult = checkConditions(request,token,wardenUrl);
            if (validationResult.isPresent()) {
                return validationResult.get(); // Return early with error response
            }
            List<WorkFlowWardenAPIDto> wardenList = workFlowAPIService.getWardenDetailsJSON();
            responseMap.put("warden_list", (wardenList != null && !wardenList.isEmpty()) ? wardenList : "no records found");
            responseMap.put("status", "success");
            response.setContentType("application/json");
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            responseMap.put("status", "error");
            return ResponseEntity.internalServerError().body(responseMap);
        }
    }

    public Optional<ResponseEntity<Map<String, Object>>> checkConditions(HttpServletRequest request, String token,String url) {
        String output = request.getRequestURL().toString();
        String[] output1 = output.split("\\?");
        System.out.println("Request url: " + output +"-----"+output1[0]);
        System.out.println("Config url: " + url);


        String clientIp = Optional.ofNullable(request.getHeader("X-FORWARDED-FOR"))
                .orElse(request.getRemoteAddr());
        System.out.println("Request from IP: " + clientIp);

        List<String> allowedIps = Arrays.asList(ipList.split(","));
        if (!allowedIps.contains(clientIp)) {
            return Optional.of(ResponseEntity.badRequest().body(Map.of("status", "Invalid Access")));
        }
        if (!(url != null && url.equals(output1[0]))) {
            return Optional.of(ResponseEntity.badRequest().body(Map.of("status", "Invalid URL")));
        }
        if (!(token != null && tokenConfig.equals(token))) {
            return Optional.of(ResponseEntity.badRequest().body(Map.of("status", "Invalid Token")));
        }
        return Optional.empty(); // All validations passed
    }

    private boolean isValidDateFormat(String dateStr) {
        try {
            new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
