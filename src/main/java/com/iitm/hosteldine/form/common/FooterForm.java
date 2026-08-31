package com.iitm.hosteldine.form.common;

import com.iitm.hosteldine.constant.Constants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.iitm.hosteldine.util.response.BaseResponse;

import java.util.Map;

@Data
public class FooterForm {
    private BaseResponse response;

    public FooterForm() {
    }

    public FooterForm(BaseResponse saveResponse) {
        setResponse(saveResponse);
    }

    public static FooterForm defaultForm(BaseResponse baseResponse) {
        return baseResponse != null ? new FooterForm(baseResponse) : null;
    }

    public static FooterForm defaultForm(HttpServletRequest request) {
        Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
        if (flashInputMap != null  && flashInputMap.get("response") != null &&
                flashInputMap.get("response") instanceof BaseResponse baseResponse) {
            return new FooterForm(baseResponse);
        }
        return new FooterForm();
    }
    
    public void updateCommonModelAttributes(ModelMap model, HttpServletRequest request, Map<String, ?> flashInputMap) {
		BaseResponse saveResponse = flashInputMap != null && flashInputMap.get("response") != null
				? ((BaseResponse) flashInputMap.get("response"))
				: null;
		model.addAttribute("footerForm", FooterForm.defaultForm(saveResponse));
		Boolean modalError = flashInputMap != null && flashInputMap.get("modalError") != null
				? ((Boolean) flashInputMap.get("modalError"))
				: null;
		if (modalError != null) {
			model.addAttribute("modalError", true);
			request.getSession().setAttribute("form", flashInputMap.get("form"));
            request.getSession().setAttribute(Constants.FORM_ERROR, flashInputMap.get(Constants.FORM_ERROR));
        }
	}
}
