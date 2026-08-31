package com.iitm.hosteldine.controller.biometric;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.biometric.URLConstant;
import com.iitm.hosteldine.controller.CommonController;
import com.iitm.hosteldine.service.BulkAsyncExecutor;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.service.biometric.BiometricService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping(value = URLConstant.FR_CLEAR)
public class FrClearController {
    private final String tag = "clearData";
    private BiometricService biometricService;
    private CommonController commonController;
    private InMemoryLogService logService;
    private boolean markComplete = true;
    private CommonResponseUtil commonResponseUtil;
    private BulkAsyncExecutor bulkAsyncExecutor;

    @GetMapping
    public String index(ModelMap model, HttpServletRequest request) {
        commonController.updateCommonAttributes(model);
        commonResponseUtil.updateCommonModelAttributes(model, request);
        return "biometric/frClear";
    }

    @GetMapping("/clearUsers")
    public @ResponseBody String clearUsers(@RequestParam("ip") String ip) {
        if (markComplete) {
            logService.addLog(tag, tag + " Requested");
            markComplete = false;
            bulkAsyncExecutor.execute(tag, ()->biometricService.clearDataByIp(tag, ip));
            markComplete = true;
        } else {
            logService.addLog(tag, tag + " already running");
        }
        return commonResponseUtil.getMessage("message.pull.process.started");
    }

    @Autowired
    public void setBiometricService(BiometricService biometricService) {
        this.biometricService = biometricService;
    }

    @Autowired public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
    }

    @Autowired
    public void setLogService(InMemoryLogService logService) {
        this.logService = logService;
    }

    @Autowired
    public void setCommonResponseUtil(CommonResponseUtil commonResponseUtil) {
        this.commonResponseUtil = commonResponseUtil;
    }

    @Autowired
    public void setBulkAsyncExecutor(BulkAsyncExecutor bulkAsyncExecutor) {
        this.bulkAsyncExecutor = bulkAsyncExecutor;
    }
}
