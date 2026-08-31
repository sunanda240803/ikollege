package com.iitm.hosteldine.controller.biometric;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.biometric.URLConstant;
import com.iitm.hosteldine.form.biometric.FrForm;
import com.iitm.hosteldine.form.biometric.UserListForm;
import com.iitm.hosteldine.service.BulkAsyncExecutor;
import com.iitm.hosteldine.service.biometric.MessService;
import com.iitm.hosteldine.service.biometric.UserFpService;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(URLConstant.DASHBOARD)
public class BiometricDashboardController {

    private final UserFpService userFpService;
    private final MessService messService;
    private final CommonResponseUtil commonResponseUtil;
    private final BulkAsyncExecutor bulkAsyncExecutor;

    private final String tag = "frDashboard";

    @GetMapping
    public String dashboard(ModelMap model, HttpServletRequest request) {
        FrForm frForm = new FrForm();
        if (request.getSession().getAttribute("form") != null) {
            frForm = (FrForm) request.getSession().getAttribute("form");
            request.getSession().removeAttribute("form");
        }
        List<UserListForm> userList = userFpService.getUserList(frForm);
        frForm.setUserList(userList);
        model.addAttribute("frForm", frForm);
        commonResponseUtil.updateCommonModelAttributes(model, request);
        return "biometric/dashboard";
    }

    @PostMapping
    public String getStudents(@ModelAttribute FrForm form, HttpServletRequest request) {
        request.getSession().setAttribute("form", form);
        return "redirect:" + URLConstant.DASHBOARD;
    }

    @PostMapping("${url.update}" + "${studentId}" + "${status}")
    public @ResponseBody BaseResponse updateStatus(@PathVariable String studentId, @PathVariable String status) {
        BaseResponse baseResponse = new BaseResponse();
        String result = userFpService.updateCardStatus(studentId, status);
        baseResponse.setStatus(Constants.UPDATED.equals(result) ? Constants.SUCCESS : Constants.ERROR);
        baseResponse.setMessage(Constants.UPDATED.equals(result) ? commonResponseUtil.getMessage("message.student.status.update.success") : commonResponseUtil.getMessage("message.student.status.update.failure"));
        return baseResponse;
    }

    @PostMapping(value = "/pull")
    public @ResponseBody String pull(@ModelAttribute FrForm form) {
        bulkAsyncExecutor.execute(tag, ()->userFpService.pullData(tag, form));
        return commonResponseUtil.getMessage("message.pull.process.started");
    }

    @PostMapping(value = {"/push"})
    public @ResponseBody String push(@ModelAttribute FrForm form) {
        bulkAsyncExecutor.execute(tag, ()->userFpService.pushData(tag, form));
        return commonResponseUtil.getMessage("message.push.process.started");
    }

    @PostMapping(value = {"/pushByMess"})
    public @ResponseBody String pushByMess(@ModelAttribute FrForm form) {
        return messService.pushDataByMess(tag, form);
    }

}
