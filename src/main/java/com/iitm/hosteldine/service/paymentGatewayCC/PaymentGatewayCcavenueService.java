package com.iitm.hosteldine.service.paymentGatewayCC;

import com.ccavenue.security.AesCryptUtil;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.paymentGatewayCC.PaymentGatewayCcavenueDto;
import com.iitm.hosteldine.model.OtherCandidate.PaymentGatewayConfigCcavEntity;
import com.iitm.hosteldine.repository.OtherCandidate.PaymentGatewayConfigCcavRepository;
import com.iitm.hosteldine.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentGatewayCcavenueService {
	
	@Value("${merchant.id}")
    private String merchantId;

	private final PaymentGatewayConfigCcavRepository paymentGatewayConfigCcavRepository;
	
    public PaymentGatewayCcavenueDto redirectPaymentGateway(PaymentGatewayCcavenueDto pgDto, HttpServletRequest request) throws Exception {

        // Get credentials dynamically based on the URL or other criteria
    	PaymentGatewayConfigCcavEntity entity = getCredentialsForUrl(request);
        String accessCode = entity.getAccessCode();
        String workingKey = entity.getWorkingKey();
        System.out.println("accessCode,workingKey,url---"+accessCode+","+workingKey+","+entity.getActionUrl());
        
    	String tid="",currency="",order_id="",language="",cancel_url="",redirect_url="",
				billing_name="",billing_address="",billing_city="",billing_state="",billing_tel="",
                billing_zip="",billing_email="",billing_country="",additional_Info="";
		long merchant_id=0;
		Double amount=0.0;
		 tid=pgDto.getTransactionId();
		 merchant_id=Long.valueOf(merchantId);
		 currency=ModelConstants.PG_CURRENCY;
		 amount=pgDto.getTotalAmount();
		 order_id=pgDto.getOrderId();
		 language=ModelConstants.PG_LANGUAGE;
		 redirect_url=URLEncoder.encode(pgDto.getRedirectUrl(),"UTF-8");
		 cancel_url=URLEncoder.encode(pgDto.getCancelUrl(),"UTF-8");
		 billing_name=pgDto.getBillingName();
		 billing_address=pgDto.getBillingAddress();
		 billing_city=pgDto.getBillingCity();
		 billing_state=pgDto.getBillingState();
		 billing_zip=pgDto.getBillingZip();
		 billing_tel=pgDto.getBillingTel();
		 billing_email=pgDto.getBillingEmail();
        billing_country=ModelConstants.BILL_COUNTRY;
		 additional_Info="additional Info.";

		String requestDatas = "tid="+tid+"&merchant_id="+merchant_id + 
					"&currency=" +currency +"&amount="+amount + "&order_id="+order_id +"&redirect_url="+redirect_url+
					"&cancel_url="+cancel_url+"&promo_code=&billing_state="+billing_state+"&delivery_name=&billing_email="+billing_email+
				 "&customer_identifier=&delivery_state=&billing_country="+billing_country+"&delivery_tel=&delivery_country=&billing_tel="+billing_tel+
				 "&delivery_city=&billing_name="+billing_name+"&delivery_address=&delivery_zip=&billing_zip="+billing_zip+
				 "&billing_city="+billing_city+"&merchant_param1="+additional_Info+"&language=EN"
				 + "&merchant_param5="+additional_Info+"&merchant_param4="+additional_Info+""
				 + "&merchant_param3="+additional_Info+"&merchant_param2="+additional_Info+"&billing_address="+billing_address+""; 

		System.out.println("requestDatas---"+requestDatas);
        // Encrypt the request data
		String encRequest ="";
		if(workingKey!=null && !workingKey.isEmpty()) {
	        AesCryptUtil aesUtil = new AesCryptUtil(workingKey);
	        encRequest = aesUtil.encrypt(requestDatas);
		}
		PaymentGatewayCcavenueDto returnDto= new  PaymentGatewayCcavenueDto();
        returnDto.setActionUrl(entity.getActionUrl());
        returnDto.setEncRequest(encRequest);
        returnDto.setAccessCode(accessCode);
		System.out.println("encr-requestDatas---"+requestDatas);
        return returnDto;
    }
    
    public String getBaseUrl(HttpServletRequest request) {
//        String scheme = request.getScheme();       // http or https
//        String serverName = request.getServerName(); // ccw.triesten.com
//        String contextPath = request.getContextPath(); // /hosteldine

        return Utility.getDomainUrl(request);
    }

    public PaymentGatewayConfigCcavEntity getCredentialsForUrl(HttpServletRequest request) {
    	// Construct base URL
        String url =getBaseUrl(request);
        System.out.println("url---"+url);
    	Optional<PaymentGatewayConfigCcavEntity> entity =  paymentGatewayConfigCcavRepository.findByUrl(url);
    	if(entity.isPresent()) {
    		return entity.get();
    	}else {
    		return new PaymentGatewayConfigCcavEntity();
    	}
    }
    
    public String decryptResponse(HttpServletRequest request) {
    	// Get credentials dynamically based on the URL or other criteria
    	PaymentGatewayConfigCcavEntity entity = getCredentialsForUrl(request);
        String workingKey = entity.getWorkingKey();
        System.out.println("workingKey---"+workingKey);
        
    	AesCryptUtil aesUtil=new AesCryptUtil(workingKey);
		String decResp = aesUtil.decrypt(request.getParameter("encResp"));

        return decResp;
    }
    
    public Map<String, Object> parseDecryptedResponse(String decryptedResponse) {
        Map<String, Object> responseMap = new HashMap<>();

        // Split the decrypted response by '&' to get key-value pairs
        String[] pairs = decryptedResponse.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                responseMap.put(keyValue[0], keyValue[1]);
            }
        }
        return responseMap;
    }

	public JSONObject getPaymentStatusFromAPI(String orderNo, String accessCode, String workingKey, AesCryptUtil aes) throws Exception {
		int updatedCount = 0;
		JSONObject gatewayJson=null;
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

				if (rawResponse == null || rawResponse.isBlank()) {
					return null;
				}

				// 4) Parse response
				Map<String, String> kv = parseKvResponse(rawResponse);
				String status = kv.get("status");
				String encResp = kv.get("enc_response");
				System.out.println("enc_response---" + encResp + "\n");
				System.out.println("status---" + status + "\n");

				if (!"0".equals(status) || encResp == null || encResp.isBlank()) {
					return null;
				}
				// 5) Decrypt response
				AesCryptUtil aes2 = new AesCryptUtil(workingKey);
				String decrypted = aes2.decrypt(encResp);
				System.out.println("CCAvenue API decrypt:" + decrypted);
				gatewayJson = new JSONObject(decrypted);
				if ("1".equals(gatewayJson.optString("status"))) {
					gatewayJson.put("order_no", orderNo);
				}

				// 6) Update DB
				//updatedCount += updateOnlinePaymentDetailsCron(gatewayJson);
			} finally {
				if (connection != null) {
					System.out.println("finally---");
					connection.disconnect();
				}
			}

		} catch (Exception ex) {
			System.err.println("CCAvenue status update failed for order " + orderNo + ": " + ex.getMessage());
			throw new Exception(ex);
		}

		return gatewayJson;
	}

	public String getOrderStatus(JSONObject jsonResponse) {
		String orderStatus = "";
		if (jsonResponse.has("order_status")) {
			String status = jsonResponse.getString("order_status");
			if ("Shipped".equalsIgnoreCase(status) || "Successful".equalsIgnoreCase(status)) {
				orderStatus = "Success";
			} else if ("Unsuccessful".equalsIgnoreCase(status)) {
				orderStatus = "Failure";
			} else {
				orderStatus = status;
			}
		}
		return orderStatus;
	}

	public static Map<String, String> parseKvResponse(String response) {
		Map<String, String> responseMap = new HashMap<>();
		// Split the response string by '&' to get each key-value pair
		String[] keyValuePairs = response.split("&");
		for (String pair : keyValuePairs) {
			// Split each pair by '=' to separate the key and value
			String[] keyValue = pair.split("=", 2);
			if (keyValue.length == 2) {
				responseMap.put(keyValue[0], keyValue[1]);
			} else {
				responseMap.put(keyValue[0], ""); // Handle keys without values
			}
		}
		return responseMap;
	}
}
