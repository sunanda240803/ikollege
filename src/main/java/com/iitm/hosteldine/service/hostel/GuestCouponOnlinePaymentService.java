package com.iitm.hosteldine.service.hostel;

import com.ccavenue.security.AesCryptUtil;
import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.GuestCouponIssuedDTO;
import com.iitm.hosteldine.dto.paymentGatewayCC.PaymentGatewayCcavenueDto;
import com.iitm.hosteldine.mapper.paymentGatewayCC.PaymentGatewayCcavenueMapper;
import com.iitm.hosteldine.model.OtherCandidate.PaymentGatewayConfigCcavEntity;
import com.iitm.hosteldine.model.hostel.GuestCouponOnlinePaymentEntity;
import com.iitm.hosteldine.model.hostel.GuestCouponPaymentAdviceEntity;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.repository.hostel.GuestCouponOnlinePaymentRepository;
import com.iitm.hosteldine.repository.hostel.GuestCouponPaymentAdviceRepository;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.paymentGatewayCC.PaymentGatewayCcavenueService;
import com.iitm.hosteldine.util.MCrypt;
import io.netty.channel.ChannelOption;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GuestCouponOnlinePaymentService {

    @Value("${url.hostel.guest.coupon.request}")
    private String guestCouponRequestPath;

    @Value("${url.payment.response}")
    private String paymentResponse;

    private static final String ORDER_STATUS_PATH = "/apis/servlet/DoWebTrans";
    private static final String COMMAND = "orderStatusTracker";

    private final GuestCouponPaymentAdviceRepository paymentAdviceRepository;
    private final GuestCouponOnlinePaymentRepository couponOnlinePaymentRepository;
    private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;
    private final PaymentGatewayCcavenueService paymentGatewayCcavenueService;
    private final MessageSource messageSource;
    private final SimsConfigDataService simsConfigDataService;
    private final GuestCouponRequestService guestCouponRequestService;

    private final WebClient webClient = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(
                    HttpClient.create()
                            .responseTimeout(Duration.ofSeconds(20))
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
            ))
            .defaultHeaders(h -> h.setAccept(List.of(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON)))
            .build();

    public PaymentGatewayCcavenueDto initiateTransaction(String encryptedId, HttpServletRequest request) throws Exception {
        String decrypted = MCrypt.getInstance().decryptToString(encryptedId);
        String[] parts = decrypted.split("~");

        Long paymentAdviceId = Long.parseLong(parts[0]);
        PaymentGatewayCcavenueDto returnDto = new PaymentGatewayCcavenueDto();

        List<Object[]> result = paymentAdviceRepository.checkPaymentStatus(paymentAdviceId, ModelConstants.STATUS_ACTIVE, Constants.SUCCESS);
        if (result != null && !result.isEmpty()) {
            Object[] row = result.get(0);
            GuestCouponPaymentAdviceEntity adviceEntity = (GuestCouponPaymentAdviceEntity) row[0];
            GuestCouponOnlinePaymentEntity onlinePaymentEntity = (GuestCouponOnlinePaymentEntity) row[1];

            Long totalAmount = (adviceEntity.getOverallAmount() != null) ? adviceEntity.getOverallAmount() : null;

            String errorMessage = handlePaymentStatus(adviceEntity, onlinePaymentEntity);
            if (errorMessage != null) {
                returnDto.setErrorStatus(errorMessage);
                return returnDto;
            }

            // TODO: check the requested date and session Mess availability
            List<GuestCouponIssuedDTO> errorList = guestCouponRequestService.checkMessAvailability(adviceEntity.getMessId(), adviceEntity.getDiningFromDate(), adviceEntity.getDiningToDate());
            if (errorList != null && !errorList.isEmpty()) {
                returnDto.setErrorList(errorList);
//                returnDto.setStatus("messAvailCheck");
                return returnDto;
            }

            /** Get the New Order Number **/
            String orderId = "";
            PaymentGatewayConfigCcavEntity urlEntity = paymentGatewayCcavenueService.getCredentialsForUrl(request);
            if (urlEntity.getInstance() != null && urlEntity.getInstance().equals(ModelConstants.PRODUCTION)) {
                orderId = "COUPON" + couponOnlinePaymentRepository.getNextOrderId();
            } else {
                orderId = "COUPONTEST" + couponOnlinePaymentRepository.getNextOrderId();
            }

            /***
             * Save the Initiated Transaction Details into 'IITM_GUEST_COUPON_ONLINE_PAYMENT_ORDERID'
             */
            GuestCouponOnlinePaymentEntity transactionEntity = new GuestCouponOnlinePaymentEntity();
            GuestCouponPaymentAdviceEntity paymentAdvice = new GuestCouponPaymentAdviceEntity();
            paymentAdvice.setRequestId(paymentAdviceId);
            transactionEntity.setPaymentAdvice(paymentAdvice);
            transactionEntity.setOverallAmount(totalAmount);
            transactionEntity.setOrderNo(orderId);
            transactionEntity.setPaymentStatus(ModelConstants.INITIATED);

            couponOnlinePaymentRepository.save(transactionEntity);

            /**
             * Get Student personal details and amount details to show it in Payment gateway page
             */
            String studentId = SecurityCtxUtil.userId().toUpperCase();
            Optional<AllStudentsDetailsViewEntity> studentEntity = allStudentsDetailsViewRepository.findBystudentId(studentId);
            PaymentGatewayCcavenueDto pgDto = PaymentGatewayCcavenueMapper.INSTANCE.toPGDtoFromStudent(studentEntity.get());
            pgDto.setOrderId(orderId);
            pgDto.setTotalAmount(Double.valueOf(totalAmount));
            pgDto.setRedirectUrl(paymentGatewayCcavenueService.getBaseUrl(request) + guestCouponRequestPath + paymentResponse + Constants.AFTER_PAYMENTGATEWAY);
            pgDto.setCancelUrl(paymentGatewayCcavenueService.getBaseUrl(request) + guestCouponRequestPath + paymentResponse + Constants.PAYMENT_CANCEL);

            /**
             * Redirect to Payment gateway page
             */
            returnDto = paymentGatewayCcavenueService.redirectPaymentGateway(pgDto, request);


        }
        return returnDto;
    }

    private String handlePaymentStatus(GuestCouponPaymentAdviceEntity adviceEntity,
                                       GuestCouponOnlinePaymentEntity onlinePaymentEntity) {

        if (onlinePaymentEntity != null
                && Constants.SUCCESS.equalsIgnoreCase(onlinePaymentEntity.getPaymentStatus())) {
            return "Payment Already Completed";
        }

        return handlePendingPaymentExpiry(adviceEntity);
    }


    private String handlePendingPaymentExpiry(GuestCouponPaymentAdviceEntity adviceEntity) {
        LocalDateTime createdAt = null;
        if (adviceEntity.getCreatedAt() != null) {
            createdAt = adviceEntity.getCreatedAt();
        }

        int expireDuration = Integer.parseInt(
                simsConfigDataService.getSimConfigValue(
                        SimsConfigDataService.ONLINE_PENDING_EXPIRED_DURATION));

        LocalDateTime expiryTime = createdAt.plusMinutes(expireDuration);
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());

        if (expiryTime.isBefore(now)) {
            boolean deleteStatus = guestCouponRequestService
                    .deleteGuestCouponByRequestId(adviceEntity.getRequestId(), "Admin");

            return deleteStatus
                    ? "Payment Expired - Request Removed"
                    : "Payment expired but deletion failed";
        }

        return null; // not expired
    }


    public PaymentGatewayCcavenueDto savePaymentResponse(String responseType, HttpServletRequest request) throws Exception {
        PaymentGatewayCcavenueDto returnDto = new PaymentGatewayCcavenueDto();

        System.out.println("encResp---" + request.getParameter("encResp") + "\n");

        String decResp = paymentGatewayCcavenueService.decryptResponse(request);
        System.out.println("decryptedResp---" + decResp + "\n");

        // Parse the decrypted response into a map
        Map<String, Object> resMap = paymentGatewayCcavenueService.parseDecryptedResponse(decResp);
        System.out.println("MapResponse---" + resMap + "\n");

        //Update the response in "IIT_PS_TEMP_ACCOM_PAYMENT_TRANSACTION_DETAILS" table
        String orderNo = resMap.get("order_id").toString();
        System.out.println("order_id---" + orderNo + "\n");

        GuestCouponOnlinePaymentEntity transaction = couponOnlinePaymentRepository.findByOrderNoAndActiveFlag(orderNo, ModelConstants.STATUS_ACTIVE);
        if (transaction != null) {
            System.out.println("trans entity order_id---" + transaction.getOrderNo() + "\n");
            transaction = PaymentGatewayCcavenueMapper.mapToTransactionEntity(transaction, resMap);
            GuestCouponOnlinePaymentEntity updatedTrans = couponOnlinePaymentRepository.save(transaction);

            // update the status in payment advice details table
            try {
                int count = paymentAdviceRepository.updatePaymentGatewayResponse(updatedTrans, SecurityCtxUtil.userId(), ModelConstants.STATUS_ACTIVE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (responseType != null && responseType.equalsIgnoreCase(Constants.AFTER_PAYMENT)) {
                //set the response to dto
                returnDto = PaymentGatewayCcavenueMapper.changeToPGDto(resMap);
            }

        }

        return returnDto;
    }

    public int updateOnlineCouponAllPendingRequestsV1(HttpServletRequest request) throws Exception {
        int count = 0;
        try {
            System.out.println("----into---------updateOnlineCouponAllPendingRequestsV2---------");
            /**
             * Syncs missing 'Success' payment statuses from the Transaction table to the 'IITM_GUEST_COUPON_PAYMENT_ADVICE' table.
             */
            List<Object[]> result = paymentAdviceRepository.getPaymentAdviceNotSuccessRequests
                    (messageSource.getMessage("message.online.coupon", null, Locale.getDefault()),
                            ModelConstants.STATUS_ACTIVE, Constants.SUCCESS);
            if (result != null && !result.isEmpty()) {
                for (Object[] row : result) {
                    GuestCouponPaymentAdviceEntity adviceEntity = (GuestCouponPaymentAdviceEntity) row[0];
                    GuestCouponOnlinePaymentEntity onlinePaymentEntity = (GuestCouponOnlinePaymentEntity) row[1];

                    paymentAdviceRepository.updatePaymentGatewayResponse(onlinePaymentEntity, Constants.ONLINE_JOB, ModelConstants.STATUS_ACTIVE);
                }
            }

            /*
             * Fetch pending requests from 'IITM_GUEST_COUPON_ONLINE_PAYMENT_TRANSACTIONS'
             * where the corresponding request ID does not have a 'Success' status
             * in the 'IITM_GUEST_COUPON_PAYMENT_ADVICE' table.
             */

            int retry_count = Integer.valueOf(simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_MESSS_COUPON_RETRY_COUNT));
            String pending_status = simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_MESSS_COUPON_PENDING_STATUS);
            //List.of("Initiated", "Awaited", "Invalid", "Timeout");
            List<String> statuses = Arrays.asList(pending_status.split(ModelConstants.COMMA));

            List<GuestCouponOnlinePaymentEntity> onlineEntity = paymentAdviceRepository.getOnlinePaymentPendingRequests(ModelConstants.STATUS_ACTIVE, Constants.SUCCESS, retry_count, statuses);
            if (!onlineEntity.isEmpty()) {
                count = updateOnlinePaymentStatusFromAPIV1(request, onlineEntity);
            }


        } catch (Exception e) {
            throw new Exception(e);
        }
        return count;
    }

    public int updateOnlinePaymentStatusFromAPIV1(HttpServletRequest request,
                                                List<GuestCouponOnlinePaymentEntity> onlineEntities) throws Exception {
        int updatedCount = 0;

        PaymentGatewayConfigCcavEntity ccAvenEntity = paymentGatewayCcavenueService.getCredentialsForUrl(request);
        final String accessCode = Objects.requireNonNull(ccAvenEntity.getAccessCode(), "accessCode is null");
        final String workingKey = Objects.requireNonNull(ccAvenEntity.getWorkingKey(), "workingKey is null");

        AesCryptUtil aes = new AesCryptUtil(workingKey);
        System.out.println("accessCode---" + accessCode + "\n");
        System.out.println("workingKey---" + workingKey + "\n");

        for (GuestCouponOnlinePaymentEntity entity : onlineEntities) {
            System.out.println("entity---" + entity.getOrderNo() + "\n");
            String orderNo = entity.getOrderNo();
            if (orderNo == null || orderNo.isBlank()) continue;

            try {
                // 1) Build request JSON and encrypt
                JSONObject reqJson = new JSONObject().put("order_no", orderNo);
                String encRequest = aes.encrypt(reqJson.toString());

                String apiUrl = "https://api.ccavenue.com/apis/servlet/DoWebTrans?";
                String params = "&enc_request=" + encRequest + "&access_code=" + accessCode
                        + "&command=orderStatusTracker&request_type=JSON&response_type=JSON&version=1.1";
                System.out.println("apiUrl---" + apiUrl);
                System.out.println("params---" + params);
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(apiUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = params.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int responseCode = connection.getResponseCode();
                    System.out.println("Response Code: " + responseCode);

                    // Read the response
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer responseBuffer = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        responseBuffer.append(inputLine);
                    }
                    in.close();
                    String rawResponse = responseBuffer.toString();
                    System.out.println("CCAvenue API response:" + rawResponse);

                    if (rawResponse == null || rawResponse.isBlank()) continue;

                    // 4) Parse response
                    Map<String, String> kv = paymentGatewayCcavenueService.parseKvResponse(rawResponse);
                    String status = kv.get("status");
                    String encResp = kv.get("enc_response");
                    System.out.println("enc_response---" + encResp + "\n");
                    System.out.println("status---" + status + "\n");

                    if (!"0".equals(status) || encResp == null || encResp.isBlank()) continue;

                    // 5) Decrypt response
                    AesCryptUtil aes2 = new AesCryptUtil(workingKey);
                    String decrypted = aes2.decrypt(encResp);
                    System.out.println("CCAvenue API decrypt:" + decrypted);
                    JSONObject gatewayJson = new JSONObject(decrypted);
                    if ("1".equals(gatewayJson.optString("status"))) {
                        gatewayJson.put("order_no", orderNo);
                    }

                    // 6) Update DB
                    updatedCount += updateOnlinePaymentDetailsCron(gatewayJson);
                } finally {
                    if (connection != null) {
                        System.out.println("finally---");
                        connection.disconnect();
                    }
                }

            } catch (Exception ex) {
                System.err.println("CCAvenue status update failed for order " + entity.getOrderNo() + ": " + ex.getMessage());
                throw new Exception(ex);
            }
        }

        return updatedCount;
    }

    public int updateOnlineCouponAllPendingRequestsV2(HttpServletRequest request) throws Exception {
        int count = 0;
        try {
            System.out.println("----into---------updateOnlineCouponAllPendingRequestsV2---------");
            /**
             * Syncs missing 'Success' payment statuses from the Transaction table to the 'IITM_GUEST_COUPON_PAYMENT_ADVICE' table.
             */
            List<Object[]> result = paymentAdviceRepository.getPaymentAdviceNotSuccessRequests
                    (messageSource.getMessage("message.online.coupon", null, Locale.getDefault()),
                            ModelConstants.STATUS_ACTIVE, Constants.SUCCESS);
            if (result != null && !result.isEmpty()) {
                for (Object[] row : result) {
                    GuestCouponPaymentAdviceEntity adviceEntity = (GuestCouponPaymentAdviceEntity) row[0];
                    GuestCouponOnlinePaymentEntity onlinePaymentEntity = (GuestCouponOnlinePaymentEntity) row[1];

                    paymentAdviceRepository.updatePaymentGatewayResponse(onlinePaymentEntity, Constants.ONLINE_JOB, ModelConstants.STATUS_ACTIVE);
                }
            }

            /*
             * Fetch pending requests from 'IITM_GUEST_COUPON_ONLINE_PAYMENT_TRANSACTIONS'
             * where the corresponding request ID does not have a 'Success' status
             * in the 'IITM_GUEST_COUPON_PAYMENT_ADVICE' table.
             */

            int retry_count = Integer.valueOf(simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_MESSS_COUPON_RETRY_COUNT));
            String pending_status = simsConfigDataService.getSimConfigValue(SimsConfigDataService.ONLINE_MESSS_COUPON_PENDING_STATUS);
            //List.of("Initiated", "Awaited", "Invalid", "Timeout");
            List<String> statuses = Arrays.asList(pending_status.split(ModelConstants.COMMA));

            //List<GuestCouponOnlinePaymentEntity> onlineEntity = paymentAdviceRepository.getOnlinePaymentPendingRequests(ModelConstants.STATUS_ACTIVE, Constants.SUCCESS, retry_count, statuses);
            List<String> orderNoList = paymentAdviceRepository.getOnlinePaymentPendingOrderNo(ModelConstants.STATUS_ACTIVE, Constants.SUCCESS, retry_count, statuses);
            if (!orderNoList.isEmpty()) {
                PaymentGatewayConfigCcavEntity ccAvenEntity = paymentGatewayCcavenueService.getCredentialsForUrl(request);
                final String accessCode = Objects.requireNonNull(ccAvenEntity.getAccessCode(), "accessCode is null");
                final String workingKey = Objects.requireNonNull(ccAvenEntity.getWorkingKey(), "workingKey is null");

                AesCryptUtil aes = new AesCryptUtil(workingKey);
                System.out.println("accessCode---" + accessCode + "\n");
                System.out.println("workingKey---" + workingKey + "\n");

                for (String orderNo : orderNoList) {
                    System.out.println("orderNo---" + orderNo + "\n");
                    if (orderNo == null || orderNo.isBlank()) continue;

                    JSONObject gatewayJson =
                            paymentGatewayCcavenueService.getPaymentStatusFromAPI(orderNo,accessCode,workingKey,aes);

                    if (gatewayJson != null) {
                        count += updateOnlinePaymentDetailsCron(gatewayJson);
                    }
                }
                //count = updateOnlinePaymentStatusFromAPI(request, orderNoList);
            }

        } catch (Exception e) {
            throw new Exception(e);
        }
        return count;
    }



    /**
     * Parses "a=b&c=d" style responses into a Map.
     */
//    private Map<String, String> parseKvResponse(String s) {
//        Map<String, String> out = new HashMap<>();
//        for (String pair : s.split("&")) {
//            int idx = pair.indexOf('=');
//            if (idx > 0) {
//                String k = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
//                String v = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
//                out.put(k, v);
//            } else if (!pair.isEmpty()) {
//                // handle keys without a value, like "flag" -> ""
//                out.put(pair, "");
//            }
//        }
//        return out;
//    }


    @Transactional
    public int updateOnlinePaymentDetailsCron(JSONObject jsonResponse) throws Exception {
        int count = 0;
        String orderNo = jsonResponse.getString("order_no");
        try {
            GuestCouponOnlinePaymentEntity paymentEntity = couponOnlinePaymentRepository.findByOrderNoAndActiveFlag(orderNo, "Y");

            // 2. Handle retry count and payment status update
            int retryCount = paymentEntity.getRetryCount();
            String orderStatus = paymentGatewayCcavenueService.getOrderStatus(jsonResponse);
            System.out.println("retryCount---" + retryCount + "\n");
            if (jsonResponse.has("status") && jsonResponse.optString("status").equals("0")) {
                // 3. Update the payment entity with details from the response
                paymentEntity = updatePaymentEntity(paymentEntity, jsonResponse, orderStatus, retryCount);

                // 4. Update payment advice if needed (if order status is successful)
                count = updatePaymentAdvice(orderNo, paymentEntity);

            } else if (jsonResponse.has("status") && jsonResponse.optString("status").equals("1") &&
                    jsonResponse.has("error_code")) {
                paymentEntity.setRetryCount(retryCount + 1);
                paymentEntity.setStatusMessage(jsonResponse.optString("error_desc"));
                paymentEntity.setModifiedBy(Constants.ONLINE_JOB);
                couponOnlinePaymentRepository.save(paymentEntity);
            }
        } catch (Exception ex) {
             System.err.println("updateOnlinePaymentDetailsCron failed for order " + orderNo + ": " + ex.getMessage());
             throw new Exception(ex);
    }
        // 5. Return the number of records updated
        return count;
    }



    private GuestCouponOnlinePaymentEntity updatePaymentEntity(GuestCouponOnlinePaymentEntity paymentEntity, JSONObject jsonResponse,
                                                               String orderStatus, int retryCount) {
        // Update the fields of the payment entity from the JSON response
        paymentEntity.setNetPayable(jsonResponse.optDouble("order_gross_amt"));
        paymentEntity.setTransactionRefNumber(jsonResponse.optString("order_bank_ref_no"));
        paymentEntity.setCcavReferenceNo(jsonResponse.optString("reference_no"));
        paymentEntity.setPaymentMethod(jsonResponse.optString("order_option_type"));
        paymentEntity.setPaymentGateway(jsonResponse.optString("order_card_name"));
        paymentEntity.setReceivedAmount(jsonResponse.optString("order_capt_amt", "0.0"));
        paymentEntity.setPaymentStatus(orderStatus);
        paymentEntity.setTransFee(jsonResponse.optDouble("order_fee_perc_value", 0.0));
        paymentEntity.setServiceTax(jsonResponse.optDouble("order_tax"));
        paymentEntity.setTransactionDate(DateUtility.parseToLocalDateTime2(jsonResponse.getString("order_date_time")));
        paymentEntity.setStatusMessage(jsonResponse.optString("order_bank_response", ""));
        paymentEntity.setRetryCount(retryCount + 1);

        paymentEntity = couponOnlinePaymentRepository.save(paymentEntity);
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date parsedDate = sdf.parse(jsonResponse.getString("order_date_time"));
//            paymentEntity.setTransactionDate(parsedDate.toInstant());
//        } catch (ParseException e) {
//            throw new RuntimeException("Error parsing transaction date", e);
//        }
        return paymentEntity;
    }

    private int updatePaymentAdvice(String orderNo, GuestCouponOnlinePaymentEntity paymentEntity) {
        int count = 0;
        // Check if there’s a corresponding payment advice to update
        Optional<GuestCouponOnlinePaymentEntity> onlineEntity = paymentAdviceRepository
                .checkPaymentAdviceNotSuccess(ModelConstants.STATUS_ACTIVE, Constants.SUCCESS, orderNo);
        if (onlineEntity.isPresent()) {
            // If payment advice exists and isn't marked 'Success', update its status
            count = paymentAdviceRepository.updatePaymentGatewayResponse(paymentEntity, Constants.ONLINE_JOB, ModelConstants.STATUS_ACTIVE);
        }
        return count;
    }


}
