package com.iitm.hosteldine.controller.biometric;

import com.iitm.hosteldine.constant.biometric.URLConstant;
import com.iitm.hosteldine.controller.CommonController;
import com.iitm.hosteldine.form.biometric.MessDetailsCountForm;
import com.iitm.hosteldine.service.biometric.MessService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = URLConstant.LIVE_STATUS)
public class LiveStatusController {
    private MessService messService;
    private CommonController commonController;
    private CommonResponseUtil commonResponseUtil;

    @GetMapping
    public String index(ModelMap model, HttpServletRequest request) {
        MessDetailsCountForm messCountForm = messService.getMessDetailCounts();
        model.addAttribute("messCountForm", messCountForm);
        commonController.updateCommonAttributes(model);
        commonResponseUtil.updateCommonModelAttributes(model, request);
        return "biometric/liveStatus";
    }

    @Autowired
    public void setMessService(MessService messService) {
        this.messService = messService;
    }


    @Autowired public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
    }

    @Autowired
    public void setCommonResponseUtil(CommonResponseUtil commonResponseUtil) {
        this.commonResponseUtil = commonResponseUtil;
    }
}
