package com.iitm.hosteldine.controller.hostel;

import java.util.List;
import java.util.Map;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.service.hostel.HostelUserMappingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.hostel.HostelRoomInfoDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.form.hostel.RoomInventoryForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.asset.AssetConfigurationService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.hostel.HostelRoomInfoService;
import com.iitm.hosteldine.service.hostel.RoomInventoryService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.room.inventory}")
public class RoomInventoryController {
	private final RoomInventoryService roomInventoryService;
	private final AssetConfigurationService assetConfigurationService;
	private final HostelMasterService hostelMasterService;
	private final HostelRoomInfoService hostelRoomInfoService;
	private final SimsConfigDataService simsConfigDataService;
	private final CommonResponseUtil commonResponseUtil;
	private final HostelUserMappingService hostelUserMappingService;

	@Value("${url.room.inventory}")
	private String getRoomInventory;

	@Value("${url.student.vacating}")
	private String studentVacatingURL;

	@Value("${url.list}")
	private String getList;

	@GetMapping
	public String getRoomInventoryDetails(@RequestParam Map<String, String> allParams,PaginationForm form,ModelMap map,
			HttpServletRequest request) throws Exception {
		Page<RoomInventoryForm> roomInventoryList = null;
		commonResponseUtil.getAdditionalParams(allParams, form);
		if (form.getAdditionalParam().get("hostelId") != null) {
			roomInventoryList = roomInventoryService.getRoomInventoryList(form);
		} else {
			form.getAdditionalParam().put("hostelId", 0l);
			form.getAdditionalParam().put("assetCondition", "");
		}
		map.addAttribute("hostelList", hostelMasterService.getHostelList());
		map.addAttribute("assetConditionList", simsConfigDataService.getSimConfigValueFromJsonArray("ASSET_CONDITION"));
		commonResponseUtil.updateCommonModelAttributes(map, request, roomInventoryList, form);
		return HTMLPage.ROOM_INVENTORY;
	}

	@PostMapping
	public String getRoomInventoryList(ModelMap map, @ModelAttribute RoomInventoryForm roomInventoryForm,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		redirectAttrs.addFlashAttribute("roomInventoryForm", roomInventoryForm);
		return Constants.REDIRECT + getRoomInventory;
	}

	@GetMapping("${url.details}" + "${id}")
	public String getRoomInventoryDetailsByCategoryId(@PathVariable Long id, ModelMap map,
			HttpServletRequest request) throws Exception {
		RoomInventoryForm roomInventoryForm = new RoomInventoryForm();
		if (id != null) {
			roomInventoryForm = roomInventoryService.getRoomInventoryDetailsByAssetId(id);
		}
		map.addAttribute("roomInventoryForm", roomInventoryForm);
		map.addAttribute("categoryList", assetConfigurationService.getCategoryList());
		map.addAttribute("hostelList", SecurityCtxUtil.userName().toLowerCase().contains(".hostel") ?
				hostelUserMappingService.getHostelDetailsList(SecurityCtxUtil.userName()) :
				hostelMasterService.getHostelList());
		map.addAttribute("roomList", hostelRoomInfoService.findRoomByHostelId(roomInventoryForm.getHostelId()));
		map.addAttribute("assetConditionList", simsConfigDataService.getSimConfigValueFromJsonArray("ASSET_CONDITION"));
		return HTMLPage.ROOM_INVENTORY_MODAL;
	}

	@PostMapping("${url.details}")
	public String saveAndUpdate(@ModelAttribute RoomInventoryForm roomInventoryForm, RedirectAttributes redirectAttrs) throws Exception {
		String saveStatus = roomInventoryService.saveAndUpdateRoomInventory(roomInventoryForm);
		redirectAttrs.addFlashAttribute("roomInventoryForm", roomInventoryForm);
		commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs);
		return Constants.REDIRECT + ( SecurityCtxUtil.userName().toLowerCase().contains(".hostel") ? studentVacatingURL + getRoomInventory : getRoomInventory );
	}

	@DeleteMapping("${url.details}" + "${id}")
	public @ResponseBody BaseResponse deleteRoomInventoryDetailsById(@PathVariable Long id, ModelMap map,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		return CommonResponseUtil
				.generateDeleteResponseByStatus(roomInventoryService.deleteRoomInventoryDetailsById(id));
	}
	
	@GetMapping("${url.room.list}" + "${id}")
	public @ResponseBody List<HostelRoomInfoDto> getHostelRoomListByHostelId(@PathVariable long id,
			HttpServletRequest request) {
		return hostelRoomInfoService.findRoomByHostelId(id);
	}

}
