package com.iitm.hosteldine.controller.mess;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.dto.mess.MessRegistrationMappingDto;
import com.iitm.hosteldine.service.mess.MessRegistrationMappingService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.mess.reg.mapping}")
public class MessRegistrationMappingController {
    private final CommonResponseUtil commonResponseUtil;
    private final MessRegistrationMappingService messRegistrationMappingService;

    @Value("${url.mess.reg.mapping}")
    private String baseUrl;

    @GetMapping
    public String getMessMasterList(ModelMap map, HttpServletRequest request, Model model) {
        commonResponseUtil.updateCommonModelAttributes(map, request);
        MessRegistrationMappingDto messRegistrationMapping = messRegistrationMappingService.getMessRegistrationMapping();
        List<MessMasterDto> girlsOneUpdatedList = updateMessListWithCheck(
                messRegistrationMappingService.getMessMasterList(),
                messRegistrationMappingService.getMessPreferences().get(0));
        List<MessMasterDto> girlsTwoUpdatedList = updateMessListWithCheck(
                messRegistrationMappingService.getMessMasterList(),
                messRegistrationMappingService.getMessPreferences().get(1));
        List<MessMasterDto> boysOneUpdatedList = updateMessListWithCheck(
                messRegistrationMappingService.getMessMasterList(),
                messRegistrationMappingService.getMessPreferences().get(2));
        model.addAttribute(HostelConstants.GIRLS_ONE_MESS_LIST.getConstants(), girlsOneUpdatedList);
        model.addAttribute(HostelConstants.GIRLS_TWO_MESS_LIST.getConstants(), girlsTwoUpdatedList);
        model.addAttribute(HostelConstants.BOYS_ONE_MESS_LIST.getConstants(), boysOneUpdatedList);
        model.addAttribute(HostelConstants.MESS_REGISTRATION_MAPPING.getConstants(), messRegistrationMapping);
        return HTMLPage.MESS_REGISTRATION_MAPPING;
    }

    @PostMapping(value = "${url.save}")
    public String saveMessRegistrationMapping(@Valid @ModelAttribute MessRegistrationMappingDto messRegistrationMappingDto, RedirectAttributes redirectAttributes) {
        String status = messRegistrationMappingService.saveMessRegistrationMapping(messRegistrationMappingDto);
        commonResponseUtil.updateSaveResponseByStatus(status, redirectAttributes, "message.mess.registration.mapping.save");
        return Constants.REDIRECT + baseUrl;
    }

    private List<MessMasterDto> updateMessListWithCheck(List<MessMasterDto> messList, List<MessMasterDto> preferenceList) {
        return messList.stream().peek(mess -> mess.setCheck(
                        preferenceList.stream().anyMatch(preference -> preference.getId().equals(mess.getId()))
                )).toList();
    }
}
