package com.iitm.hosteldine.form.common;

import java.util.Map;

import org.springframework.web.servlet.support.RequestContextUtils;

import com.iitm.hosteldine.util.response.BaseResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

@Data
public class RequestForm {
	private BaseResponse response;

	public RequestForm() {
	}

	public RequestForm(BaseResponse saveResponse) {
		setResponse(saveResponse);
	}


	public static RequestForm footerForm(HttpServletRequest request) {
		Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
        BaseResponse baseResponse = flashInputMap != null && flashInputMap.get("response") != null
                ? ((BaseResponse) flashInputMap.get("response")) : null;
		return baseResponse != null ? new RequestForm(baseResponse) : null;
	}
	
	public static Object getFormObject(HttpServletRequest request, String objName) {
		Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
        return flashInputMap != null && flashInputMap.get(objName) != null
                ? flashInputMap.get(objName) : null;
	}
	
	public static RequestForm setException(BaseResponse baseResponse) {
		return baseResponse != null ? new RequestForm(baseResponse) : null;
	}
}
