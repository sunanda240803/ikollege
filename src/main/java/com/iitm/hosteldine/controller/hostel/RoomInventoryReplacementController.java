package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.form.hostel.RoomInventoryForm;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.hostel.RoomInventoryService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping(value = "${url.room.inventory.replacement}")
@RequiredArgsConstructor
public class RoomInventoryReplacementController {

    private final CommonResponseUtil commonResponseUtil;
    private final RoomInventoryService roomInventoryService;
    private final HostelMasterService hostelMasterService;

    @GetMapping
    public String getRoomInventoryReplacementList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
        commonResponseUtil.getAdditionalParams(allParams, form);
        Page<RoomInventoryForm> roomInventoryList = Optional.ofNullable(form.getAdditionalParam().get(HostelConstants.HOSTEL_ID.getConstants()))
                .map(id -> roomInventoryService.getRoomInventoryReplacedList(form))
                .orElseGet(() -> {
                    form.getAdditionalParam().put(HostelConstants.HOSTEL_ID.getConstants(), 0L);
                    return Page.empty();
                });
        map.addAttribute(HostelConstants.HOSTEL_LIST.getConstants(), hostelMasterService.getHostelList());
        commonResponseUtil.updateCommonModelAttributes(map, request, roomInventoryList, form);
        return HTMLPage.ROOM_INVENTORY_REPLACEMENT_LIST;
    }

    @PutMapping("${url.room.inventory.replaced.or.repaired}" + "${id}")
    public @ResponseBody BaseResponse updateReplacedOrRepairedStatus(@PathVariable("id") Long id) {
        try {
            return CommonResponseUtil.generateDeleteResponseByStatus(roomInventoryService.updateReplacedOrRepairedStatus(id));
        } catch (RecordNotExistsException e) {
            BaseResponse errorResponse = new BaseResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setStatus("Failure");
            return errorResponse;
        }
    }
}

