package com.iitm.hosteldine.controller.otherCandidate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.OtherCandidate.TempAccomPaymentAdviceDto;
import com.iitm.hosteldine.dto.paymentGatewayCC.PaymentGatewayCcavenueDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.OtherCandidate.OtherCandidatePaymentService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "${url.other.candidate.payment}")
public class OtherCandidatePaymentDetailsController {
	
	private final OtherCandidatePaymentService paymentService;
	private final CommonResponseUtil commonResponseUtil;
	
	@Value("${url.other.candidate.payment}")
	private String otherCandidatePayment;
	
	@GetMapping
    public String getPaymentList(PaginationForm form,ModelMap map, HttpServletRequest request) {
        Page<TempAccomPaymentAdviceDto> paymentList = paymentService.getPaymentList(form);
        commonResponseUtil.updateCommonModelAttributes(map, request,paymentList,form);
        return HTMLPage.PAYMENT_LIST;
    }
	
	@GetMapping("${url.initiate}"+"${id}")
    public String initiateTransaction(@PathVariable String id,ModelMap model,HttpServletRequest request) throws Exception {

		PaymentGatewayCcavenueDto pgDto = paymentService.initiateTransaction(id,request);
        
        // Pass encrypted data and access code to the Thymeleaf template
        model.addAttribute("pgDto", pgDto);
        
        return HTMLPage.CCAVENUE_HTML;
    }
	
	@PostMapping("${url.payment.response}")
    public String savePaymentResponse(@RequestParam(value = "responseType", required = false) String responseType,ModelMap model,HttpServletRequest request) throws Exception {

		System.out.println("responseType------"+responseType);
		PaymentGatewayCcavenueDto pgDto = paymentService.savePaymentResponse(responseType,request);
        
		if(responseType!=null & responseType.equals(Constants.CANCEL)) {
        	return Constants.REDIRECT + otherCandidatePayment;
        }
		
		model.addAttribute("paymentGatewayDto", pgDto);
		model.addAttribute("backToList", otherCandidatePayment);
        return HTMLPage.CCAVENUE_RESPONSE_HTML;
    }
	
   @GetMapping("${url.receipt.pdf.download}" + "${id}" + "${orderNo}")
   public void downloadPaymentReceiptPdf(@PathVariable String id,@PathVariable String orderNo,HttpServletResponse response) throws Exception {
	  	Long decryptedId = MCrypt.getInstance().decryptToLong(id);
	  	paymentService.PaymentReceiptPdfGenerator(decryptedId,orderNo,response);
    }
}
