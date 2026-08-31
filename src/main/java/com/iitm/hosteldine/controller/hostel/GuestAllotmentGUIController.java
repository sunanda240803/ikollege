package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.hostel.*;
import com.iitm.hosteldine.service.CommonService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.*;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.guest.allotment.gui}")
public class GuestAllotmentGUIController {


    private final HostelMasterService hostelMasterService;
    private final HostelIndividualAllotmentService hostelIndividualAllotmentService;
    private final HostelRoomInfoService hostelRoomInfoService;
    private final HostelFloorMasterService hostelFloorMasterService;
    private final CommonResponseUtil commonResponseUtil;
    private final SimsConfigDataService simsConfigDataService;
    private final Utility utility;
    private final MessageSource messageSource;
    private final CommonService commonService;

    @Value("${url.guest.allotment.gui}")
    private String baseUrl;
    @GetMapping
    public String getGuestAllotmentList(@RequestParam(value = "hostelId", defaultValue = ModelConstants.EMPTY_STRING) Long hostelId,
                                        @RequestParam(value = "occupancyStatus", defaultValue = ModelConstants.EMPTY_STRING) String occupancyStatus,
                                        @RequestParam(value = "floorId", defaultValue = ModelConstants.EMPTY_STRING) String floorId,
                                        @RequestParam(value = "monthOffset", defaultValue = "0") int monthOffset,
                                        ModelMap map,
                                        HttpServletRequest request) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.plusMonths(monthOffset);
        LocalDate endDate   = startDate.plusMonths(1);
        List<HostelMasterDto> hostelList = commonService.getHostelListForUser();

        boolean hasAccess = !hostelList.isEmpty();
        if (!hasAccess) {
            map.addAttribute(HostelConstants.ERROR_MESSAGE_KEY.getConstants(), HostelConstants.ERROR_MESSAGE_VALUE.getConstants());
            commonResponseUtil.updateCommonModelAttributes(map, request);
            return HTMLPage.HOSTEL_INDIVIDUAL_ALLOTMENT_LIST;
        }

        List<HostelRoomInfoDto> roomOccupancyList = hostelIndividualAllotmentService
                .getGuestRoomOccupiedList(hostelId, startDate, endDate, occupancyStatus.isEmpty() ? null : occupancyStatus, floorId.isEmpty() ? null : floorId);

        int totalVacantSeats = roomOccupancyList.stream()
                .mapToInt(HostelRoomInfoDto::getTotalVacantSeats)
                .sum();

        List<LocalDate> dateSequence = startDate.datesUntil(endDate.plusDays(1))
                .collect(Collectors.toList());

        Map<LocalDate, Integer> vacancyMap = hostelIndividualAllotmentService.calculateVacantSeats(roomOccupancyList, dateSequence);

        map.addAttribute(HostelConstants.MONTH_OFFSET.getConstants(), monthOffset);
        map.addAttribute(HostelConstants.CURRENT_DATE.getConstants(), now);
        map.addAttribute(HostelConstants.START_DATE.getConstants(), startDate);
        map.addAttribute(HostelConstants.END_DATE.getConstants(), endDate);
        map.addAttribute(HostelConstants.HOSTEL_ID.getConstants(), hostelId);
        map.addAttribute(HostelConstants.HOSTEL_OR_WARDEN_LIST.getConstants(), hostelList);
        map.addAttribute(HostelConstants.HOSTEL_ALLOTMENT_GUI_STATUS.getConstants(),
                simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.HOSTEL_ALLOTMENT_GUI_STATUS));
        map.addAttribute(HostelConstants.OCCUPANCY_STATUS.getConstants(), occupancyStatus);
        map.addAttribute(HostelConstants.ROOM_LIST.getConstants(), roomOccupancyList);
        map.addAttribute(HostelConstants.TOTAL_VACANT_SEATS.getConstants(), totalVacantSeats);
        map.addAttribute(HostelConstants.DATE_SEQUENCE.getConstants(), dateSequence);
        map.addAttribute(HostelConstants.VACANCY_MAP.getConstants(), vacancyMap);
        map.addAttribute("baseUrl", baseUrl);
        map.addAttribute("floorId", floorId);
        map.addAttribute("isGuest", true);
        map.addAttribute("hostelFloorList", hostelFloorMasterService.getFloorListByHostelId(Objects.nonNull(hostelId) ? hostelId : 0));
        commonResponseUtil.updateCommonModelAttributes(map, request);
        return HTMLPage.HOSTEL_INDIVIDUAL_ALLOTMENT_LIST;
    }

    @GetMapping("${date}" + "${roomId}" + "${hostelId}")
    public String getOccupiedDetails(@PathVariable String date,
                                     @PathVariable Long roomId,
                                     @PathVariable String hostelId,
                                     Model model) {
        HostelRoomInfoDto hostelRoomInfoDto = hostelRoomInfoService.getHostelRoomInfoDetailsById(roomId.intValue());
        LocalDate formattedDate = parseDateSafely(date);
        model.addAttribute(HostelConstants.HOSTEL_ROOM_INFO_DTO.getConstants(), Objects.requireNonNull(hostelRoomInfoDto));
        model.addAttribute(HostelConstants.HOSTEL_FLOOR_NAME.getConstants(), hostelRoomInfoDto.getBuilding().getFloorName());
        model.addAttribute(HostelConstants.HOSTEL_MASTER_DTO.getConstants(), hostelRoomInfoDto.getBuilding().getHostel());
        model.addAttribute(HostelConstants.FLOOR_ID.getConstants(), hostelRoomInfoDto.getBuilding().getId());
        model.addAttribute(HostelConstants.HOSTEL_ID.getConstants(), hostelId);
        model.addAttribute(HostelConstants.ROOM_OCCUPANCY_DETAILS.getConstants(), hostelIndividualAllotmentService.getGuestRoomOccupancyDetails(roomId, formattedDate));
        model.addAttribute(HostelConstants.DATE.getConstants(), date);
        model.addAttribute(HostelConstants.ROOM_ID.getConstants(), roomId);
        model.addAttribute("isGuest", true);
        return HTMLPage.HOSTEL_INDIVIDUAL_ALLOTMENT_OCCUPIED_MODAL;
    }

    private LocalDate parseDateSafely(String date) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT, Locale.ENGLISH);
            return LocalDate.parse(date, inputFormatter);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    @GetMapping("${roomId}")
    public String getRoomDetails(@PathVariable Long roomId, Model model) {
        HostelRoomInfoDto hostelRoomInfoDto = hostelRoomInfoService.getHostelRoomInfoDetailsById(roomId.intValue());
        HostelFloorMasterDto hostelFloorMasterDto = hostelFloorMasterService.getFloorDetailsById(hostelRoomInfoDto.getBuilding().getId());
        HostelMasterDto hostelMasterDto = hostelMasterService.getHostelDetailsById(hostelRoomInfoDto.getBuilding().getHostel().getId());
        model.addAttribute(HostelConstants.HOSTEL_ROOM_INFO_DTO.getConstants(), hostelRoomInfoDto);
        model.addAttribute(HostelConstants.HOSTEL_FLOOR_MASTER_MASTER_DTO.getConstants(), hostelFloorMasterDto);
        model.addAttribute(HostelConstants.HOSTEL_MASTER_DTO.getConstants(), hostelMasterDto);
        return HTMLPage.HOSTEL_INDIVIDUAL_ROOM_DETAILS_MODAL;
    }

    @GetMapping(value = "${url.hostel.master}")
    @ResponseBody
    public ResponseEntity<?> getHostelList() {
        return ResponseEntity.ok(commonService.getHostelListForUser()
                .stream()
                .sorted(Comparator.comparing(HostelMasterDto::getHostelName))
                .toList());
    }

    @GetMapping(value = "${url.floor.list}" + "${hostelId}")
    @ResponseBody
    public ResponseEntity<?> getFloorList(@PathVariable Long hostelId) {
        return ResponseEntity.ok(hostelFloorMasterService.getFloorListByHostelId(hostelId)
                .stream()
                .sorted(Comparator.comparing(HostelFloorMasterDto::getFloorName))
                .toList());
    }
}
