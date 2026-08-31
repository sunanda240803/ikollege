package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.hostel.ShowEventMasterDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.ShowEventMasterService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping(value = "${url.hostel.event.master}")
@RequiredArgsConstructor
public class ShowEventMasterController {
    private final CommonResponseUtil commonResponseUtil;
    private final ShowEventMasterService showEventMasterService;


    @Value("${url.hostel.event.master}")
    private String baseUrl;

    @GetMapping
    public String getShowEventMaster(PaginationForm form,ModelMap map, HttpServletRequest request) {
        Page<ShowEventMasterDto> floorList = showEventMasterService.getEventMasterList(form);
        commonResponseUtil.updateCommonModelAttributes(map, request,floorList,form);
        return HTMLPage.SHOW_EVENT_MASTER;
    }

    @GetMapping(value = "${url.get}" + "${id}")
    public String getEventMasterById(@PathVariable("id") Long id, ModelMap model,HttpServletRequest request) {
        String formKey = "showEventMasterDto";
        ShowEventMasterDto showEventMasterDto = commonResponseUtil.handleModalFormError(request,model,formKey, ShowEventMasterDto.class);

        if (Objects.nonNull(id) && id > 0) {
             showEventMasterDto = showEventMasterService.getEventMasterById(id);
        }

        model.addAttribute(formKey, showEventMasterDto);
        model.addAttribute("accountHeadList", showEventMasterService.getAccountHeadList());
        return HTMLPage.ADD_EDIT_EVENT_MASTER;
    }

    @PostMapping(value = "${url.save}")
    public String saveOrUpdateEventMaster(@Valid @ModelAttribute ShowEventMasterDto showEventMasterDto, BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()){
            commonResponseUtil.updateModalFormErrorAttributes(redirectAttributes,bindingResult,showEventMasterDto);
            return "redirect:" + baseUrl;
        }

        String status = showEventMasterService.saveOrUpdateEventMaster(showEventMasterDto);
        String message=status.equalsIgnoreCase(Constants.SAVED)?"message.event.master.save":"message.event.master.update";
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes,message);
        return "redirect:" + baseUrl;
    }

    @DeleteMapping("${url.delete}" + "${id}")
    public @ResponseBody BaseResponse deleteEventMasterById(@PathVariable("id") Long id) {
        try {
            return CommonResponseUtil.generateDeleteResponseByStatus(showEventMasterService.deleteEventMaster(id));
        } catch (RecordNotExistsException e) {
            // Create a custom error response in case of an exception
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatus("Failure");
            return errorResponse;
        }
    }
    
    @GetMapping("${id}")
    public ResponseEntity<ShowEventMasterDto> getEventMasterById(@PathVariable("id") Long id) {
        ShowEventMasterDto showEventMasterDto = showEventMasterService.getEventMasterById(id);
        if (showEventMasterDto != null) {
            return ResponseEntity.ok(showEventMasterDto); // Return 200 OK with the data
        } else {
            return ResponseEntity.notFound().build(); // Return 404 Not Found if no data
        }
    }
}
