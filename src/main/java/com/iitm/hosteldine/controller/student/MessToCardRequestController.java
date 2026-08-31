package com.iitm.hosteldine.controller.student;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.student.MessCardAmountTransferDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.student.MessToCardRequestService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.FileUploadConstants;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping(value = "${url.mess.card.request}")
@RequiredArgsConstructor
public class MessToCardRequestController {

	private final CommonResponseUtil commonResponseUtil;
	private final MessToCardRequestService messToCardRequestService;
	
	@Value("${url.mess.card.request}")
    private String baseUrl;

    @GetMapping
    public String getMessCardRequestList(@RequestParam Map<String, String> allParams,PaginationForm form,
    		ModelMap map, HttpServletRequest request, ModelMap model) throws Exception {
    	String studentId = SecurityCtxUtil.accountType().equals(ModelConstants.STUDENT) ? SecurityCtxUtil.userId().toString() : null; 
    	commonResponseUtil.getAdditionalParams(allParams, form);
		Page<MessCardAmountTransferDto> messToCardList = messToCardRequestService.getMessToCardRequestList(form,studentId);
		commonResponseUtil.updateCommonModelAttributes(map, request ,messToCardList , form);
		model.addAttribute("workflowStatus", WorkflowStatus.class);
		model.addAttribute("studentId", studentId);
		
		String requestStatus = form.getAdditionalParam().get("requestedStatus")!=null ? form.getAdditionalParam().get("requestedStatus").toString() : null;
	    LocalDate requestDate = (form.getAdditionalParam().get("requestDate")!=null && !form.getAdditionalParam().get("requestDate").equals("")) ? LocalDate.parse(form.getAdditionalParam().get("requestDate").toString()) : null;
	    LocalDate transferDate = (form.getAdditionalParam().get("transferredDate")!=null && !form.getAdditionalParam().get("transferredDate").equals("")) ? LocalDate.parse(form.getAdditionalParam().get("transferredDate").toString()) : null;
	    MessCardAmountTransferDto messCardAmountTransferDto = new MessCardAmountTransferDto();
	    messCardAmountTransferDto.setRequestedStatus(requestStatus);
	    messCardAmountTransferDto.setRequestDate(requestDate);
	    messCardAmountTransferDto.setTransferredDate(transferDate);
		model.addAttribute("messCardAmountTransferDto", messCardAmountTransferDto);
        return HTMLPage.MESS_TO_CARD_REQUEST_LIST;
    }
    
    @GetMapping("${url.approve.reject}" + "/{id}")
	public @ResponseBody BaseResponse messToCardRequestApproveOrReject(@PathVariable Long id, @RequestParam String status,
				@RequestParam String rejectReason) throws Exception {
	    try {
	        boolean approveStatus = messToCardRequestService.approveMessToCardRequest(id,status,rejectReason);
	        return CommonResponseUtil.generateDeleteResponseByStatus(approveStatus);
	    } catch (Exception e) {
	        BaseResponse errorResponse = new BaseResponse();
	        errorResponse.setMessage(e.getMessage());  
	        errorResponse.setStatus("Failure");        
	        return errorResponse;
	    }
	}
    
    @GetMapping(value = "${url.download.all.mess.card.request.view.report}")
    public void downloadAllMessToCardRequestReport(@ModelAttribute MessCardAmountTransferDto messCardAmountTransferDto, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Workbook workbook = messToCardRequestService.downloadAllMessToCardRequestReport(messCardAmountTransferDto);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        workbook.close();
        byte[] excelBytes = bos.toByteArray();
        response.setContentType(FileUploadConstants.XLSX);
        response.setHeader(ExcelConstants.CONTENT_DISPOSITION, ExcelConstants.MESS_TO_CARD_REQUEST_LIST);
        response.setContentLength(excelBytes.length);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            outputStream.write(excelBytes);
            outputStream.flush();
        }
    }

    @PostMapping(value = "${url.transfer.amount}")
    public String transferMessCardAmount(@ModelAttribute MessCardAmountTransferDto messCardAmountTransferDto,ModelMap model,
    		RedirectAttributes redirectAttrs) throws Exception {
    	String approveStatus = messToCardRequestService.transferMessCardAmount(messCardAmountTransferDto);
    	String message;
        if (approveStatus.equals(Constants.SAVED)) {
            message = "message.mess.to.card.transfer.success";
        }else {
        	message = "message.mess.to.card.transfer.failure";
        }
    	commonResponseUtil.updateSaveResponseByStatus(approveStatus,redirectAttrs,message);
    	model.addAttribute("messCardAmountTransferDto", new MessCardAmountTransferDto());
    	return Constants.REDIRECT + baseUrl;
    }
    
}
