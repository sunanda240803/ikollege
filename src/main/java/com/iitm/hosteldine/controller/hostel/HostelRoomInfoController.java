package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.hostel.HostelFloorMasterDto;
import com.iitm.hosteldine.dto.hostel.HostelRoomInfoDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.HostelFloorMasterService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.hostel.HostelRoomInfoService;
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
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.hostel.room.info}")
public class HostelRoomInfoController {

	private final HostelRoomInfoService hostelRoomInfoService;
	private final MessageSource messageSource;
	private final HostelMasterService hostelMasterService;
	private final HostelFloorMasterService hostelFloorMasterService;
	private final SimsConfigDataService simsConfigDataService;
	private final CommonResponseUtil commonResponseUtil;

	@Value("${url.hostel.room.info}")
	private String getHostelRoomInfo;
	
	@RequestMapping(method = { RequestMethod.GET })
	public String getRoomInfoList(@RequestParam Map<String, String> allParams,PaginationForm form,ModelMap map,
			HttpServletRequest request) throws Exception {
		Page<HostelRoomInfoDto> roomInfoList = null;
		// List<HostelRoomInfoDto> roomInfoList = hostelRoomInfoService.getHostelRoomInfoList(roomInfoDto);
		commonResponseUtil.getAdditionalParams(allParams, form);
		if (form.getAdditionalParam().get("hostelId") != null) {
			roomInfoList = hostelRoomInfoService.getHostelRoomInfoList(form);
			map.addAttribute("hostelFloorList", hostelFloorMasterService
					.getFloorListByHostelId(Long.parseLong(form.getAdditionalParam().get("hostelId").toString())));
		} else {
			form.getAdditionalParam().put("hostelId", 0l);
			form.getAdditionalParam().put("floorId", 0l);
		}
		map.addAttribute("hostelList", hostelMasterService.getHostelList());
		commonResponseUtil.updateCommonModelAttributes(map, request, roomInfoList, form);
		return HTMLPage.HOSTEL_ROOM_INFO;
	}

	@GetMapping("${url.get}" + "${id}" + "${hostelId}")
	public String getHostelRoomInfoDetailsById(@PathVariable String id, @PathVariable long hostelId, ModelMap map,
			HttpServletRequest request) throws Exception {
		HostelRoomInfoDto roomInfoDto = new HostelRoomInfoDto();
		if (id != null && Integer.parseInt(id) > 0) {
			roomInfoDto = hostelRoomInfoService.getHostelRoomInfoDetailsById(Integer.parseInt(id));
			hostelId = roomInfoDto.getBuilding().getHostel().getId();
		}

		if (hostelId > 0) {
			map.addAttribute("hostelFloorList", hostelFloorMasterService.getFloorListByHostelId(hostelId));
		}
		map.addAttribute("roomInfoDto", roomInfoDto);
		map.addAttribute("hostelList", hostelMasterService.getHostelList());
		map.addAttribute("roomConfigType", simsConfigDataService.getSimConfigValueArrayList("OFFICIAL_GUEST_STATUS"));

		return HTMLPage.ADD_EDIT_HOSTEL_ROOM_INFO;
	}

	@PostMapping("${url.save}")
	public String saveAndUpdate(ModelMap map, @ModelAttribute HostelRoomInfoDto roomInfoDto, HttpServletRequest request,
			RedirectAttributes redirectAttrs) throws Exception {
		String saveStatus = hostelRoomInfoService.saveAndUpdate(roomInfoDto);
		String message=null;
		if(saveStatus !=null) {
			message=saveStatus.equalsIgnoreCase(Constants.SAVED)?"message.room.config.save":"message.room.config.update";
		}
		commonResponseUtil.updateSaveResponseByStatus(saveStatus, redirectAttrs,message);		
		redirectAttrs.addFlashAttribute("roomInfoDto", roomInfoDto);
		return "redirect:" + getHostelRoomInfo;
	}

	@DeleteMapping("${url.delete}" + "${id}")
	public @ResponseBody BaseResponse deleteHostelRoomInfoById(@PathVariable Long id, ModelMap map,
			HttpServletRequest request, RedirectAttributes redirectAttrs) throws Exception {
		 try {
		        boolean deleteStatus = hostelRoomInfoService.deleteHostelRoomInfoById(id);
		        return CommonResponseUtil.generateDeleteResponseByStatus(deleteStatus);
		    } catch (RecordNotExistsException e) {
		        // Create a custom error response in case of an exception
		        BaseResponse errorResponse = new BaseResponse();
		        errorResponse.setMessage(e.getMessage());  
		        errorResponse.setStatus("Failure");        
		        return errorResponse;
		    }
	}

	@PostMapping("${url.exist}")
	public @ResponseBody boolean checkHostelRoomInfoExist(@RequestBody HostelRoomInfoDto dto, ModelMap map,
			HttpServletRequest request) {
		System.out.println("test");
		return hostelRoomInfoService.checkHostelRoomInfoExist(dto.getFloorId(), dto.getHostelId(), dto.getRoomNo(),
				dto.getId());
	}

	@GetMapping("${url.floor.list}" + "${id}")
	public @ResponseBody List<HostelFloorMasterDto> checkHostelRoomInfoExist(@PathVariable long id,
			HttpServletRequest request) {
		return hostelFloorMasterService.getFloorListByHostelId(id);
	}

}
