package com.iitm.hosteldine.controller.biometric;

import com.iitm.hosteldine.constant.biometric.URLConstant;
import com.iitm.hosteldine.controller.CommonController;
import com.iitm.hosteldine.dao.biometric.MessTerminalDao;
import com.iitm.hosteldine.model.biometric.MessMaster;
import com.iitm.hosteldine.model.biometric.MessTerminal;
import com.iitm.hosteldine.service.biometric.MessService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = URLConstant.UPDATE_STATUS)
public class UpdateStatusController {
    private MessService messService;
    private CommonController commonController;
    private MessTerminalDao messTerminalDao;
    private CommonResponseUtil commonResponseUtil;

    @GetMapping
    public String index(@RequestParam(required = false) String status, ModelMap model, HttpServletRequest request) {
        List<MessMaster> messMasterList = messService.getMessMasterList();
        model.addAttribute("messMasterList", messMasterList);
        model.addAttribute("status", status);
        commonController.updateCommonAttributes(model);
        commonResponseUtil.updateCommonModelAttributes(model, request);
        return "biometric/updateStatus";
    }

    @GetMapping(value = "/updateStatus/{pushRemove}/{messId}")
    public @ResponseBody String updatePushStatus(@PathVariable Long messId, @PathVariable String pushRemove) {
        return messService.updatePushRemoveStatus(messId, pushRemove);
    }

    @GetMapping("/updateTerminal/{id}")
    public String terminalDetails(@PathVariable Long id, ModelMap modelMap) {
        modelMap.addAttribute("terminal", messTerminalDao.getMessTerminalById(id));
        return "biometric/terminal_modal";
    }

    @PostMapping("/updateTerminal")
    public String updateTerminalDetails(@ModelAttribute MessTerminal terminal) {
        String status = messTerminalDao.updateTerminalDetails(terminal);
        return "redirect:/messDetails?status=" + status;
    }

    @Autowired
    public void setMessService(MessService messService) {
        this.messService = messService;
    }

    @Autowired
    public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
    }

    @Autowired
    public void setMessTerminalDao(MessTerminalDao messTerminalDao) {
        this.messTerminalDao = messTerminalDao;
    }

    @Autowired
    public void setCommonResponseUtil(CommonResponseUtil commonResponseUtil) {
        this.commonResponseUtil = commonResponseUtil;
    }
}
