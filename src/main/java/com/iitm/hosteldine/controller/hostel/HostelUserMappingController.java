package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.dto.hostel.HostelUserMappingDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.UserManagementService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.hostel.HostelUserMappingService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.hostel.user.mapping}")
public class HostelUserMappingController {
	private final HostelUserMappingService mappingService;
	private final HostelMasterService hostelMasterService;
	private final UserManagementService userManagementService;
	private final CommonResponseUtil commonResponseUtil;

	@Value("${url.hostel.user.mapping}")
	private String getUserMapping;

	@GetMapping
	public String getUserMappingList(@ModelAttribute HostelUserMappingDto dto,PaginationForm form, ModelMap map, HttpServletRequest request)
			throws Exception {

		Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
		dto = flashInputMap != null && flashInputMap.get("userMappingDto") != null
				? ((HostelUserMappingDto) flashInputMap.get("userMappingDto"))
				: new HostelUserMappingDto();
		//List<HostelUserMappingDto> userMappingList = mappingService.getMappingList();
		//map.addAttribute("userMappingList", userMappingList);
		Page<HostelUserMappingDto> userMappingList = mappingService.getMappingList(form);
		HostelUserMappingDto userMappingInfo = new HostelUserMappingDto();
		map.addAttribute("userList", userManagementService.getUserList());
		userMappingInfo.setHostelMasterList(hostelMasterService.getHostelList());
		map.addAttribute("userMappingInfo", userMappingInfo);
		commonResponseUtil.updateCommonModelAttributes(map, request,userMappingList,form);
		return HTMLPage.HOSTEL_USER_MAPPING;
	}

	@PostMapping("${url.save}")
	public String saveAndUpdate(ModelMap map, @ModelAttribute HostelUserMappingDto dto, HttpServletRequest request,
			RedirectAttributes redirectAttrs) throws Exception {
		Boolean saveStatus = mappingService.saveAndUpdate(dto);
		redirectAttrs.addFlashAttribute("roomInfoDto", dto);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus ? "success" : null, redirectAttrs,"message.user.mapping.save");
		return "redirect:" + getUserMapping;
	}

	@DeleteMapping("${url.delete}")
	public @ResponseBody BaseResponse deleteHostelRoomInfoById(@RequestBody HostelUserMappingDto dto, ModelMap map,
			HttpServletRequest request) throws Exception {
		return CommonResponseUtil.generateDeleteResponseByStatus(mappingService.deleteMappingById(dto));
	}

}
