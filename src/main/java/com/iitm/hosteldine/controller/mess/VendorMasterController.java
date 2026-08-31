package com.iitm.hosteldine.controller.mess;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.controller.VendorMasterService;
import com.iitm.hosteldine.dto.mess.MessVendorMasterDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.mess.vendor.master}")
public class VendorMasterController {
    private final VendorMasterService vendorMasterService;
    private final MessageSource messageSource;
    private final CommonResponseUtil commonResponseUtil;

    @Value("${url.mess.vendor.master}")
    private String getVendorMaster;

    @GetMapping
    public String getVendorMasterList(PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
        //map.addAttribute("vendorMasterList", vendorMasterService.getVendorMasterList());
        Page<MessVendorMasterDto> vendorMasterList = vendorMasterService.getVendorMasterList(form);
        commonResponseUtil.updateCommonModelAttributes(map, request, vendorMasterList, form);
        return HTMLPage.MESS_VENDOR_MASTER;
    }

    @GetMapping("${url.get}" + "${id}")
    public String getVendorMasterDetailsById(@PathVariable String id, ModelMap map, HttpServletRequest request)
            throws Exception {
        MessVendorMasterDto vendorMasterDto = new MessVendorMasterDto();
        if (id != null && id != "") {
            vendorMasterDto = vendorMasterService.getVendorMasterDetailsById(id);
            vendorMasterDto.setMobileNoMask(String.valueOf(vendorMasterDto.getMobileNo()));
        }
        map.addAttribute("vendorMasterDto", vendorMasterDto);

        return HTMLPage.ADD_EDIT_MESS_VENDOR_MASTER;
    }

    @PostMapping("${url.save}")
    public String saveAndUpdate(ModelMap map, @ModelAttribute MessVendorMasterDto dto, HttpServletRequest request,
                                RedirectAttributes redirectAttrs) throws Exception {
        dto.setMobileNo(dto.getMobileNoMask() != null ? Long.parseLong(dto.getMobileNoMask().replaceAll("-", "")) : 0);

        String saveStatus = vendorMasterService.saveAndUpdate(dto);
        String message = null;
        if (saveStatus != null) {
            message = saveStatus.equalsIgnoreCase(Constants.SAVED) ? "message.vendor.master.save" : "message.vendor.master.update";
        }
        commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs, message);
        return Constants.REDIRECT + getVendorMaster;
    }

    @DeleteMapping("${url.delete}" + "${id}")
    public @ResponseBody BaseResponse deleteVendorMasterById(@PathVariable String id, ModelMap map,
                                                             HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
        try {
            return CommonResponseUtil.generateDeleteResponseByStatus(vendorMasterService.deleteVendorMasterById(id));
        } catch (RecordNotExistsException e) {
            // Create a custom error response in case of an exception
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatus("Failure");
            return errorResponse;
        }
    }

    @GetMapping("${url.exist}" + "${id}")
    public @ResponseBody boolean checkVendorCodeExist(@PathVariable String id, ModelMap map,
                                                      HttpServletRequest request) {
        return vendorMasterService.checkVendorCodeExist(id);
    }

}
