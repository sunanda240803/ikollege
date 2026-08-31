package com.iitm.hosteldine.controller.mess;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.controller.VendorMasterService;
import com.iitm.hosteldine.dto.mess.MessVendorAllocationDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.service.mess.MessVendorAllocationService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.mess.vendor.allocation}")
public class MessVendorAllocationController {
    @Value("${url.mess.vendor.allocation}")
    private String vendorAllocationPath;

    private final MessVendorAllocationService allocationService;
    private final CommonResponseUtil commonResponseUtil;
    private final MessMasterService messMasterService;
    private final VendorMasterService vendorMasterService;

    @GetMapping
    public String getMessVendorAllocations(PaginationForm form, ModelMap map, HttpServletRequest request) {
        Page<MessVendorAllocationDto> allocations = allocationService.getAllocations(form);
        commonResponseUtil.updateCommonModelAttributes(map, request, allocations, form);

        return HTMLPage.MESS_VENDOR_ALLOCATION;
    }

    @GetMapping("/add")
    public String addNewAllocation(ModelMap map) {
//        List<MessVendorAllocationDto> floors = messMasterService.getMessFloors(ModelConstants.STATUS_ACTIVE);
        List<MessVendorAllocationDto> vendors = vendorMasterService.getVendorNames(ModelConstants.STATUS_ACTIVE);
        Integer gst = allocationService.getGst();
//        map.addAttribute("floors", floors);
        map.addAttribute("messes", messMasterService.getMessMasterList());
        map.addAttribute("vendors", vendors);
        map.addAttribute("allocation", MessVendorAllocationDto.builder().gst(gst).build());

        return HTMLPage.MESS_VENDOR_ALLOCATION_MODAL;
    }

    @PostMapping
    public String createUpdateVendorAllocation(@ModelAttribute MessVendorAllocationDto dto, RedirectAttributes redirectAttributes) {
        String status = allocationService.manageVendorAllocation(dto);
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes);

        return Constants.REDIRECT + vendorAllocationPath;
    }
}
