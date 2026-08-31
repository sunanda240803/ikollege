package com.iitm.hosteldine.controller.dashboard;

import com.iitm.hosteldine.form.student.ShowActionForm;
import com.iitm.hosteldine.service.dashboard.ShowActionService;
import com.iitm.hosteldine.service.hostel.ShowEventMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "${url.show.action}")
@RequiredArgsConstructor
public class ShowActionController {

    private final CommonResponseUtil commonResponseUtil;
    private final ShowActionService showActionService;
    private final ShowEventMasterService showEventMasterService;

    @Value("${url.show.action}")
    private String baseUrl;

    @GetMapping("${url.widget}")
    public String getProfessionalShowWidget(HttpServletRequest request, ModelMap model){
        model.addAttribute("eventList",showEventMasterService.getActiveEventListByDate());
        return HTMLPage.SHOW_TICKET_WIDGET;
    }

    @GetMapping(value = "${id}")
    public String getShowsByEvent(@PathVariable("id") long eventId, ModelMap model, HttpServletRequest request) {
        ShowActionForm showActionForm = showActionService.getShowList(eventId);
        commonResponseUtil.updateCommonModelAttributes(model, request,null,null);
        model.addAttribute("showForm", showActionForm);
        return HTMLPage.SHOW_ACTION;
    }

    @PostMapping
    public String saveOrUpdateShowDetails(@ModelAttribute ShowActionForm showActionForm,ModelMap model, RedirectAttributes redirectAttributes) {
        try {
            StringBuilder errorMessage = new StringBuilder();
            boolean status = showActionService.saveOrUpdateShowDetails(showActionForm, errorMessage);
            if(status){
                String message="message.professional.show.purchase";
                commonResponseUtil.updateSaveResponseByStatus("Save", redirectAttributes,message);
            }
            if(!errorMessage.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", errorMessage.toString());
            }
        }catch (Exception ex) {
            commonResponseUtil.exceptionMessageHandling(ex, redirectAttributes);
        }
        finally {
            return "redirect:" + baseUrl + "/" + showActionForm.getEventId();
        }
    }


}
