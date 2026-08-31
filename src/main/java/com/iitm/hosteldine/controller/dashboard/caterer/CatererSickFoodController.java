package com.iitm.hosteldine.controller.dashboard.caterer;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.caterer.CatererSickFoodDeliveryDto;
import com.iitm.hosteldine.dto.dean.DeanApprovalDto;
import com.iitm.hosteldine.dto.dean.OtherCandidateRequestDto;
import com.iitm.hosteldine.dto.student.SickFoodRequestDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.caterer.CatererSickFoodDeliveryService;
import com.iitm.hosteldine.service.dean.DeanDashboardService;
import com.iitm.hosteldine.service.student.SickFoodService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "${url.caterer.sick.food.delivery}")
@RequiredArgsConstructor
public class CatererSickFoodController {

    private final DeanDashboardService deanDashboardService;
    private final CommonResponseUtil commonResponseUtil;
    private final SimsConfigDataService simsConfigDataService;
    private final CatererSickFoodDeliveryService catererSickFoodDeliveryService;
    private final SickFoodService sickFoodService;
    private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;

    @Value("${url.caterer.sick.food.delivery}")
    private String baseUrl;

    @GetMapping
    String getCatererSickFoodDelivery(@RequestParam Map<String, String> allParams, PaginationForm form,
                                      ModelMap map, HttpServletRequest request) {
        DeanApprovalDto columnList = deanDashboardService.getDeanMenuListByRoleAndUserIdAndValue(SecurityCtxUtil.userRole()
                , SecurityCtxUtil.userName(), baseUrl.replace("/", ""));
        map.addAttribute("columnDto", columnList);

        commonResponseUtil.getAdditionalParams(allParams, form);
        List<String> filterList = List.of("requestFromDate", "requestToDate", "catererStatus", "studentStatus",
                "studentId", "studentName", "messSession");
        if (!form.isSearchFilter()) {
            filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
        }
        map.addAttribute("messSessionMap", simsConfigDataService.getSimConfigValueAsMap(SimsConfigDataService.MESS_SESSION));
        map.addAttribute("catererStatusMap", simsConfigDataService.getSimConfigValueAsMap(SimsConfigDataService.CATERER_STATUS));
        List<CatererSickFoodDeliveryDto> sickFoodDeliveryList = catererSickFoodDeliveryService.getSickFoodDeliveryList(form,
                baseUrl.replace("/", ""), columnList.getActionUrlList());
        commonResponseUtil.updateCommonModelAttributes2(map, request, sickFoodDeliveryList, form);
        return HTMLPage.DEAN_DASHBOARD_MENU_LIST;
    }

    @GetMapping(value = "${url.get.view}" + "${id}")
    String getView(@PathVariable("id") Long requestId, ModelMap map) {
        SickFoodRequestDto requestDto = sickFoodService.getById(requestId);
        String studentName = allStudentsDetailsViewRepository.findBystudentId(requestDto.getStudentId())
                .map(AllStudentsDetailsViewEntity::getStudentName)
                .orElse(Strings.EMPTY);
        requestDto.setStudentName(studentName);
        map.addAttribute("sickFoodRequestDto", requestDto);
        return HTMLPage.FOOD_REQUEST_VIEW;
    }

    @GetMapping(value = "${type}" + "${RequestId}" + "${id}")
    @ResponseBody
    BaseResponse updateRequestStatus(@PathVariable("type") String type, @PathVariable("RequestId") Long requestId,
                                     @PathVariable("id") Long deliveryId) {
        try {
            String s = catererSickFoodDeliveryService.updateDeliveryStatus(type, requestId, deliveryId);
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setStatus(Constants.SUCCESS);
            baseResponse.setMessage(s);
            return baseResponse;
        } catch (Exception e) {
            return commonResponseUtil.errorExceptionHandling(e);
        }
    }
}
