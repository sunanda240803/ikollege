package com.iitm.hosteldine.controller.hostel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.form.hostel.RoomInventoryForm;
import com.iitm.hosteldine.service.hostel.BulkRoomInventoryService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.bulk.room.inventory}")
public class BulkRoomInventoryController {
	private final BulkRoomInventoryService roomInventoryService;
	private final CommonResponseUtil commonResponseUtil;

	@Value("${url.bulk.room.inventory}")
	private String getBulkRoomInventory;
	
	@GetMapping
	public String getBulkRoomInventory(ModelMap map, HttpServletRequest request) throws Exception {
		commonResponseUtil.updateCommonModelAttributes(map, request);
		return HTMLPage.BULK_ROOM_INVENTORY;
	}

	@GetMapping(value = "${url.template.download}")
	public void downloadRoomInventoryBulkUploadTemplate(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Workbook workbook = roomInventoryService.downloadRoomInventoryBulkUploadTemplate();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		workbook.write(bos);
		workbook.close();
		// Convert the ByteArrayOutputStream to a byte array
		byte[] excelBytes = bos.toByteArray();
		// Set the content type and headers for the response
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=Room Inventory Bulk Upload Template.xlsx");
		response.setContentLength(excelBytes.length);
		// Write the byte array to the response output stream
		try (ServletOutputStream outputStream = response.getOutputStream()) {
			outputStream.write(excelBytes);
			outputStream.flush();
		}
	}

	@PostMapping(value = "${url.save}")
	public String saveRoomInventoryBulkUpload(@ModelAttribute RoomInventoryForm roomInventoryForm, HttpServletRequest request,
			HttpServletResponse response, RedirectAttributes redirectAttrs) throws Exception {
		roomInventoryForm = roomInventoryService.saveRoomInventoryBulkUpload(roomInventoryForm.getFile());
		if (roomInventoryForm.getErrorList().isEmpty()) {
			commonResponseUtil.updateSaveResponseByStatus("Success", redirectAttrs);
		} else {
			redirectAttrs.addFlashAttribute("roomInfoDto", roomInventoryForm);
		}
		return "redirect:" + getBulkRoomInventory;
	}

}

