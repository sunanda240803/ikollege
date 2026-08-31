package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.hostel.GuestCouponParam;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.hostel.GuestCouponIssuedDTO;
import com.iitm.hosteldine.dto.hostel.GuestCouponIssuedResultDTO;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.exception.UserRoleNotMappedException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.GuestCouponMappingsService;
import com.iitm.hosteldine.service.mess.MessRegistrationMappingService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.RoleEnum;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.*;

import static com.iitm.hosteldine.util.StringUtility.getNullIfEmpty;
import static com.iitm.hosteldine.util.Utility.numToNullIfZero;
import static org.apache.commons.lang3.math.NumberUtils.*;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.hostel.guest.coupon.issued}")
public class GuestCouponMappingsController {
    private final GuestCouponMappingsService couponMappingsService;
    private final CommonResponseUtil commonResponseUtil;
    private final MessRegistrationMappingService messRegistrationService;
    private final MessageSource messageSource;
    private final SimsConfigDataService simsConfigDataService;

    @GetMapping
    public String getIssuedCoupons(@RequestParam Map<String, String> requestParams, ModelMap map, PaginationForm form,
                                   HttpServletRequest request) throws UserRoleNotMappedException {
        commonResponseUtil.getAdditionalParams(requestParams, form);
        Map<String, Object> additionalParams = form.getAdditionalParam();

        GuestCouponIssuedDTO coupon = GuestCouponIssuedDTO.builder()
                .name((String) additionalParams.get(GuestCouponParam.NAME.getKey()))
                .requestId(toLong((String) additionalParams.get(GuestCouponParam.REQUEST_ID.getKey()), 0L))
                .diningFrom(getNullIfEmpty((String) additionalParams.get(GuestCouponParam.DINING_FROM.getKey())))
                .diningTo(getNullIfEmpty((String) additionalParams.get(GuestCouponParam.DINING_TO.getKey())))
                .submittedFrom(getNullIfEmpty((String) additionalParams.get(GuestCouponParam.SUBMITTED_FROM.getKey())))
                .submittedTo(getNullIfEmpty((String) additionalParams.get(GuestCouponParam.SUBMITTED_TO.getKey())))
                .messId(toInt((String) additionalParams.get(GuestCouponParam.MESS_ID.getKey()), 0))
                .usedStatus((String) additionalParams.get(GuestCouponParam.USED_STATUS.getKey()))
                .build();
        List<GuestCouponIssuedResultDTO> searchedCoupons = couponMappingsService.getIssuedGuestCoupons(form, coupon);
        List<MessMasterDto> messes=new ArrayList<>();
        String userRole = Objects.requireNonNullElse(SecurityCtxUtil.userRole(), ModelConstants.EMPTY_STRING);
        if(RoleEnum.CATERER.getValue().equalsIgnoreCase(userRole)) {
            messes = messRegistrationService.getMessMasterListByCaterer();
        }else{
            messes = messRegistrationService.getMessMasterList();
        }
        map.addAttribute(HostelConstants.MESSES.getConstants(), messes);
        map.addAttribute(HostelConstants.COUPON_STATUSES.getConstants(), simsConfigDataService
                .getSimConfigValueFromJsonArray(SimsConfigDataService.COUPON_STATUS));
        map.addAttribute(HostelConstants.SEARCHED_COUPONS.getConstants(), searchedCoupons);
        // reset numbers to stop showing 0 on UI
        coupon.setRequestId(numToNullIfZero(coupon.getRequestId()));
        map.addAttribute("coupon", coupon);
        //Get coupon count for Caterer
        GuestCouponIssuedDTO countDto = couponMappingsService.getCountsForSessions();
        map.addAttribute("bfCount", countDto.getBfCount());
        map.addAttribute("lcCount", countDto.getLcCount());
        map.addAttribute("drCount", countDto.getDrCount());

        commonResponseUtil.updateCommonModelAttributes2(map, request, searchedCoupons, form);
        map.addAttribute("USE_DATATABLES", true);
        return HTMLPage.GUEST_COUPON_ISSUED;
    }

    @PatchMapping
    public @ResponseBody String returnCoupons(@RequestBody List<Long> couponIds) {
        return couponMappingsService.returnCoupons(couponIds);
    }

    @GetMapping(value = "${url.download}")
    public void downloadAllStudentsDetailsViewReport(HttpServletResponse response,
                                                     @RequestParam(value = "name", required = false) String name,
                                                     @RequestParam(value = "requestId", required = false) Long requestId,
                                                     @RequestParam(value = "usedStatus", required = false) String usedStatus,
                                                     @RequestParam(value = "diningFrom", required = false) String validFrom,
                                                     @RequestParam(value = "diningTo", required = false) String validTo,
                                                     @RequestParam(value = "submittedFrom", required = false) String submittedFrom,
                                                     @RequestParam(value = "submittedTo", required = false) String submittedTo,
                                                     @RequestParam(value = "messId", required = false) Integer messId) throws Exception {
        GuestCouponIssuedDTO coupon = GuestCouponIssuedDTO.builder().name(name).requestId(requestId)
                .usedStatus(usedStatus).diningFrom(validFrom).diningTo(validTo).submittedFrom(submittedFrom)
                .submittedTo(submittedTo).messId(messId).build();
        Workbook workbook = couponMappingsService.downloadIssuedCoupons(coupon);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        byte[] excelBytes = bos.toByteArray();
        response.setContentType(FileUploadConstants.XLSX);
        String fileName = messageSource.getMessage("message.hostel.guest-coupon-issued.filename", null, Locale.getDefault());
        response.setHeader(FileUploadConstants.CONTENT_DISPOSITION, messageSource.getMessage(
                "message.attachment.filename", new Object[]{fileName}, Locale.getDefault()));
        response.setContentLength(excelBytes.length);

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(excelBytes);
            outputStream.flush();
        }
    }

}
