package com.iitm.hosteldine.controller.biometric;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.biometric.URLConstant;
import com.iitm.hosteldine.controller.CommonController;
import com.iitm.hosteldine.form.biometric.FrForm;
import com.iitm.hosteldine.service.BulkAsyncExecutor;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.service.biometric.BiometricService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping(URLConstant.FR_PULL)
public class FrPullController {
    private final String tag = "frPull";

    private BiometricService biometricService;
    private boolean markComplete = true;
    private InMemoryLogService logService;
    private CommonController commonController;
    private CommonResponseUtil commonResponseUtil;
    private BulkAsyncExecutor bulkAsyncExecutor;

    @GetMapping
    public String index(ModelMap model, HttpServletRequest request) {
        Object result = request.getSession().getAttribute("result");
        request.getSession().removeAttribute("result");
        model.addAttribute("frForm", new FrForm());
        model.addAttribute("result", result);
        commonResponseUtil.updateCommonModelAttributes(model, request);
        commonController.updateCommonAttributes(model);
        return "biometric/frPull";
    }

    @GetMapping(value = "/getCompleteStatus")
    public @ResponseBody Boolean getCompleteStatus() {
        return markComplete;
    }

    @GetMapping(value="/{ip}/{deleteUser}")
    public @ResponseBody String pull(@PathVariable String ip, @PathVariable Boolean deleteUser) {
        if (markComplete) {
            logService.addLog(tag, tag + " Requested");
            markComplete = false;
            bulkAsyncExecutor.execute(tag, ()->biometricService.pullData(tag, ip, deleteUser));
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

    @Autowired
    public void setLogService(InMemoryLogService logService) {
        this.logService = logService;
    }

    @Autowired public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
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
