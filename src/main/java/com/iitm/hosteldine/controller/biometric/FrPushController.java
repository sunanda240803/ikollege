package com.iitm.hosteldine.controller.biometric;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.biometric.URLConstant;
import com.iitm.hosteldine.controller.CommonController;
import com.iitm.hosteldine.form.biometric.FrForm;
import com.iitm.hosteldine.model.biometric.MessMaster;
import com.iitm.hosteldine.model.biometric.MessTerminal;
import com.iitm.hosteldine.service.BulkAsyncExecutor;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.service.biometric.MessService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping(URLConstant.FR_PUSH)
public class FrPushController {
    private final String tag = "frPushByIP";

    private MessService messService;
    private boolean markComplete = true;
    private InMemoryLogService logService;
    private CommonController commonController;
    private CommonResponseUtil commonResponseUtil;
    private BulkAsyncExecutor bulkAsyncExecutor;

    @GetMapping
    public String index(ModelMap model, HttpServletRequest request) {
        Object result = request.getSession().getAttribute("result");
        request.getSession().removeAttribute("result");
        model.addAttribute("result", result);

        List<MessMaster> messMasterList = messService.getMessMasterList();
        FrForm frForm = new FrForm();
        frForm.setMessList(messMasterList);
        model.addAttribute("frForm", frForm);
        commonController.updateCommonAttributes(model);
        commonResponseUtil.updateCommonModelAttributes(model, request);
        return "biometric/frPush";
    }

    @GetMapping(value = "/getMessDevices")
    public @ResponseBody List<MessTerminal> getMessDevices(@RequestParam("messId") Long messId) {
        return messService.getMessTerminalListByMess(messId);
    }

    @GetMapping(value = "/getCompleteStatus")
    public @ResponseBody Boolean getCompleteStatus() {
        return markComplete;
    }

    @GetMapping(value = "/{messIp}/{deviceIp}")
    public @ResponseBody String push(@PathVariable String deviceIp, @PathVariable String messIp) {
        if (markComplete) {
            logService.addLog(tag, "Push Requested");
            markComplete = false;
            bulkAsyncExecutor.execute(tag, ()->messService.pushDataByMessDevice(tag, messIp, deviceIp.replaceAll("-", ".")));
            markComplete = true;
        } else {
            logService.addLog(tag, "Push already running");
        }
        return commonResponseUtil.getMessage("message.push.process.started");
    }

    @Autowired
    public void setMessService(MessService messService) {
        this.messService = messService;
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
