package com.iitm.hosteldine.controller.biometric;

import com.iitm.hosteldine.controller.CommonController;
import com.iitm.hosteldine.dao.biometric.StudentMessDetailsDao;
import com.iitm.hosteldine.service.biometric.MessService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/history")
public class HistoryController {

    private StudentMessDetailsDao studentMessDetailsDao;
    private MessService messService;
    private CommonController commonController;
    private CommonResponseUtil commonResponseUtil;

    @GetMapping
    public String showHistory(@RequestParam(required = false) String date, @RequestParam(required = false) String status,
                              @RequestParam(required = false) Long messId, ModelMap model, HttpServletRequest request) {
        if (date == null || date.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.format(new Date());
        }
        model.addAttribute("studentList", studentMessDetailsDao.getListByDate(date, messId, status));
        model.addAttribute("messList", messService.getMessMasterList());
        model.addAttribute("messId", messId);
        model.addAttribute("date", date);
        model.addAttribute("status", status);
        commonController.updateCommonAttributes(model);
        commonResponseUtil.updateCommonModelAttributes(model, request);
        return "biometric/history";
    }

    @Autowired
    public void setStudentMessDetailsDao(StudentMessDetailsDao studentMessDetailsDao) {
        this.studentMessDetailsDao = studentMessDetailsDao;
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
    public void setCommonResponseUtil(CommonResponseUtil commonResponseUtil) {
        this.commonResponseUtil = commonResponseUtil;
    }
}
