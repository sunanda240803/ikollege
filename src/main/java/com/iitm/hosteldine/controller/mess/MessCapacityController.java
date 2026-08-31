package com.iitm.hosteldine.controller.mess;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.form.common.RequestForm;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.mess.capacity}")
public class MessCapacityController {
    @Value("${url.mess.capacity}")
    private String urlMessCapacity;
    private final MessMasterService messMasterService;
    private final CommonResponseUtil commonResponseUtil;

    @GetMapping
    public String getMessCapacityList(ModelMap map, HttpServletRequest request) throws Exception {
        map.addAttribute("messMasterDto", messMasterService.getMessCapacityList());
        commonResponseUtil.updateCommonModelAttributes(map, request);
        return HTMLPage.MESS_CAPACITY_CONFIG;
    }

    @PostMapping
    public String updateMessCapacity(@ModelAttribute MessMasterDto messMasterDto, HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
        String updateStatus = messMasterService.updateMessCapacity(messMasterDto);
        commonResponseUtil.updateSaveResponseByStatus(updateStatus, redirectAttrs,"message.mess.capacity.success");
        redirectAttrs.addFlashAttribute(Constants.FORM, messMasterDto);
        return Constants.REDIRECT + urlMessCapacity;
    }
}
