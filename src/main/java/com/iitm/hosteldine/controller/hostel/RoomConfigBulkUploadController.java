package com.iitm.hosteldine.controller.hostel;

import com.iitm.hosteldine.dto.hostel.HostelRoomInfoDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.hostel.HostelRoomInfoService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.hostel.bulk.room.config}")
public class RoomConfigBulkUploadController {
	private final HostelRoomInfoService hostelRoomInfoService;
	private final MessageSource messageSource;
	private final CommonResponseUtil commonResponseUtil;
	@Value("${url.hostel.bulk.room.config}")
	private String getRoomConfigBulkUpload;

	@GetMapping
	public String getBulkRoomConfigUpload(ModelMap map, HttpServletRequest request, HttpServletResponse response) {
		Map<String, ?> flashInputMap = RequestContextUtils.getInputFlashMap(request);
		map.addAttribute("roomInfoDto",
				flashInputMap != null && flashInputMap.get("roomInfoDto") != null
						? ((HostelRoomInfoDto) flashInputMap.get("roomInfoDto"))
						: new HostelRoomInfoDto());
		commonResponseUtil.updateCommonModelAttributes(map, request);
		return HTMLPage.HOSTEL_BULK_ROOM_CONFIG;
	}

	@PostMapping(value = "${url.save}")
	public String saveBulkRoomConfigUpload(@ModelAttribute HostelRoomInfoDto roomInfoDto, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttrs) throws IOException {
		roomInfoDto = hostelRoomInfoService.saveBulkRoomConfigUpload(roomInfoDto.getFile());
		if (roomInfoDto.getErrorList().isEmpty()) {
			commonResponseUtil.updateSaveResponseByStatus("Success", redirectAttrs,"message.bulk.room.config.save");	
		} else {
			redirectAttrs.addFlashAttribute("roomInfoDto", roomInfoDto);
		}

		return "redirect:" + getRoomConfigBulkUpload;
	}

	@GetMapping(value = "${url.template.download}")
	public void downloadBulkRoomConfigUploadTemplate(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Workbook workbook = hostelRoomInfoService.downloadBulkRoomConfigUploadTemplate();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		workbook.close();
		// Convert the ByteArrayOutputStream to a byte array
		byte[] excelBytes = bos.toByteArray();
		// Set the content type and headers for the response
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=BulkRoomConfigUploadTemplate.xlsx");
		response.setContentLength(excelBytes.length);
		// Write the byte array to the response output stream
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			outputStream.write(excelBytes);
			outputStream.flush();
		}

	}
}
