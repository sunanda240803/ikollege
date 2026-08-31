package com.iitm.hosteldine.controller.biometric;

import com.iitm.hosteldine.constant.biometric.URLConstant;
import com.iitm.hosteldine.controller.CommonController;
import com.iitm.hosteldine.form.biometric.UserListForm;
import com.iitm.hosteldine.model.biometric.MessMaster;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.service.biometric.MessService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(URLConstant.SYNC_DATA)
public class SyncDataController {
    private final String tag = "syncData";

    private MessService messService;
    private InMemoryLogService logService;
    private CommonController commonController;
    private CommonResponseUtil commonResponseUtil;

    @GetMapping
    public String indexWithoutMessId(ModelMap model, HttpServletRequest request) {
        List<MessMaster> messMasterList = messService.getMessMasterList();
        model.addAttribute("messList", messMasterList);
        commonController.updateCommonAttributes(model);
        commonResponseUtil.updateCommonModelAttributes(model, request);
        return "biometric/syncData";
    }

    @GetMapping("/{messId}")
    public String indexWithMessId(ModelMap model, @PathVariable Long messId, HttpServletRequest request) {
        if (messId != null && messId > 0) {
            List<UserListForm> userDetails = messService.getStudentListByMess(tag, messId);
            model.addAttribute("userList", userDetails);
        }
        List<MessMaster> messMasterList = messService.getMessMasterList();
        model.addAttribute("messList", messMasterList);
        model.addAttribute("messId", messId);
        commonController.updateCommonAttributes(model);
        commonResponseUtil.updateCommonModelAttributes(model, request);
        return "biometric/syncData";
    }

    @GetMapping(value = "/perform")
    public @ResponseBody String perform(@RequestParam("messId") Long messId) {
        logService.addLog(tag, "SyncData Requested for " + messId);
        try {
            messService.performSync(tag, messId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @GetMapping(value = "/stop")
    public @ResponseBody String stop(@RequestParam("messId") Long messId) {
        logService.addLog(tag, "SyncDataStop Requested for " + messId);
        messService.stopSync(tag, messId);
        return "";
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
}
