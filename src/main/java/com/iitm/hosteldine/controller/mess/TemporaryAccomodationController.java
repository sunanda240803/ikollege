package com.iitm.hosteldine.controller.mess;

import com.iitm.hosteldine.dto.OtherCandidate.TempAccomPaymentAdviceDto;
import com.iitm.hosteldine.dto.mess.TemporaryAccomodationDto;
import com.iitm.hosteldine.exception.RecordListEmptyException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.service.OtherCandidate.OtherCandidatePaymentService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.mess.TemporaryAccomodationService;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.BaseResponse;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping(value = "${url.temporary.accomodation.list}")
@RequiredArgsConstructor
public class TemporaryAccomodationController {

	@Value("${url.temporary.accomodation.list}")
	private String baseUrl;

	private final TemporaryAccomodationService temporaryAccomodationService;
	private final SimsConfigDataService simsConfigDataService;
	private final CommonResponseUtil commonResponseUtil;
	private final OtherCandidatePaymentService otherCandidatePaymentService;

	
	@RequestMapping(method = { RequestMethod.GET })
	public String getTemporaryAccomodationList(@RequestParam Map<String, String> allParams, PaginationForm form, ModelMap map, HttpServletRequest request) throws Exception {
		String splitBaseUrl = baseUrl.replace("/", "");
		if (!form.isSearchFilter()) {
			List<String> filterList = List.of("candidateName", "requestId", "approvalFromDate", "approvalToDate");
			filterList.forEach(filter -> form.getAdditionalParam().put(filter, ""));
		}
		commonResponseUtil.getAdditionalParams(allParams, form);
		Page<TemporaryAccomodationDto> temporaryAccomodationList = temporaryAccomodationService.getTemporaryAccomodationList(form, splitBaseUrl);

		/* This is to get the total approved amount from all the record */
		PaginationForm paginationForm = new PaginationForm();
		BeanUtils.copyProperties(form, paginationForm);
		paginationForm.setPage(1);
		paginationForm.setSize(Integer.MAX_VALUE);
		Page<TemporaryAccomodationDto> temporaryAccomodationAllList = temporaryAccomodationService.getTemporaryAccomodationList(paginationForm, splitBaseUrl);

		double totalApprovedAmount = temporaryAccomodationAllList.getContent().stream()
				.map(TemporaryAccomodationDto::getNetAmount)
				.filter(Objects::nonNull)
				.mapToDouble(Double::doubleValue)
				.sum();
		map.addAttribute("totalApprovedAmount", totalApprovedAmount);

		map.addAttribute("filters", form.getAdditionalParam());
		commonResponseUtil.updateCommonModelAttributes(map, request, temporaryAccomodationList, form);
		return HTMLPage.MESS_TEMPORARY_ACCOMODATION_LIST;
	}

	@GetMapping(value = "${url.view}")
	public String getPaymentViewDetails(@RequestParam String data, ModelMap map, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
		Boolean status = Utility.checkRequestType(data, 3, 2);
		if (status) {
			try {
				String splitBaseUrl = baseUrl.replace("/", "");
				TemporaryAccomodationDto temporaryAccomodationDto = new TemporaryAccomodationDto();
				String[] split = Utility.decryptData(data);
				Long candidateId = Utility.getLongValueOrDefault(split, 0, null);
				Long requestId = Utility.getLongValueOrDefault(split, 1, null);
				temporaryAccomodationDto = temporaryAccomodationService.getPaymentAdviceDetails(candidateId, requestId,splitBaseUrl);
				map.addAttribute("temporaryAccomodationDto", temporaryAccomodationDto);
				map.addAttribute("cardCharges", simsConfigDataService.getSimConfigValue(SimsConfigDataService.CARD_CHARGES));
				commonResponseUtil.updateCommonModelAttributes(map, request);
				return HTMLPage.MESS_TEMPORARY_ACCOMODATION_PAYMENT_VIEW;
			} catch (Exception e) {
				e.printStackTrace();
				commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
			}
		} else {
			commonResponseUtil.invalidAccess(redirectAttributes);
		}
		return null;
	}

	@PostMapping("${url.save}")
	public @ResponseBody BaseResponse savePaymentAdvice(@RequestBody TempAccomPaymentAdviceDto dto) throws Exception {
		boolean status = temporaryAccomodationService.savePaymentAdvice(dto);
		return CommonResponseUtil.updateResponseByStatus(status, "response.update.success", "response.update.error");
	}

	@DeleteMapping("${url.delete}")
	public @ResponseBody BaseResponse deletePaymentAdvice(@RequestParam Long id) throws Exception {
		boolean status = temporaryAccomodationService.deletePaymentAdvice(id);
		return CommonResponseUtil.updateResponseByStatus(status, "response.delete.success", "response.delete.error");
	}
	
	@PutMapping("${url.approve}")
	public @ResponseBody BaseResponse approvePaymentAdvice(@RequestParam Long id) throws Exception {
		boolean status = temporaryAccomodationService.approvePaymentAdvice(id);
		return CommonResponseUtil.updateResponseByStatus(status, "message.label.approved.successfully", "message.label.approved.failure");
	}	

	@GetMapping("${url.payment.advice.view}")
	public String getPaymentAdviceView(@PathVariable Long requestId, @PathVariable Long candidateId, @PathVariable Long id, ModelMap map, HttpServletRequest request) throws RecordListEmptyException {
		TempAccomPaymentAdviceDto tempAccomPaymentAdviceDto = new TempAccomPaymentAdviceDto();
		tempAccomPaymentAdviceDto = temporaryAccomodationService.getPaymentDetails(candidateId, requestId, id);
		map.addAttribute("tempAccomPaymentAdviceDto", tempAccomPaymentAdviceDto);
		commonResponseUtil.updateCommonModelAttributes(map, request);
		return HTMLPage.MESS_PAYMENT_ADVICE_VIEW;
	}

	@PutMapping("${url.save.payment}")
	public @ResponseBody BaseResponse savePaymentDetails(@RequestBody TempAccomPaymentAdviceDto dto)
			throws Exception {
		boolean status = temporaryAccomodationService.savePaymentDetails(dto);
		return CommonResponseUtil.updateResponseByStatus(status, "response.update.success", "response.update.error");
	}
	
	@PutMapping("${url.update.payment}")
	public @ResponseBody BaseResponse updatePaymentDetails(@RequestBody TempAccomPaymentAdviceDto dto)
			throws Exception {
		boolean status = temporaryAccomodationService.updatePaymentDetails(dto);
		return CommonResponseUtil.updateResponseByStatus(status, "response.update.success", "response.update.error");
	}

	@PostMapping("${url.check.reference.number}")
	public @ResponseBody BaseResponse checkReferenceNumber(@RequestBody Map<String, String> request) throws Exception {
		String referenceNumber = request.get("referenceNumber");
		int count = temporaryAccomodationService.checkReferenceNumber(referenceNumber);
		return CommonResponseUtil.updateResponseByValue(count);
	}
	
	@PostMapping("${url.check.dates}")
	public @ResponseBody BaseResponse checkDates(@RequestBody TempAccomPaymentAdviceDto dto) throws Exception {
		int count = temporaryAccomodationService.checkDates(dto);
		return CommonResponseUtil.updateResponseByValue(count);
	}
	
	@GetMapping("${url.payment.advice.hostel.view}")
	public String getHostelView(@RequestParam String data, @RequestParam String gender, ModelMap map, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
		Boolean status = Utility.checkRequestType(data, 4, 3);
		if (status) {
			try {
				String[] split = Utility.decryptData(data);
				Long candidateId = Utility.getLongValueOrDefault(split, 0, null);
				Long requestId = Utility.getLongValueOrDefault(split, 1, null);
				Long paymentAdviceId = Utility.getLongValueOrDefault(split, 2, null);
				TempAccomPaymentAdviceDto tempAccomPaymentAdviceDto = new TempAccomPaymentAdviceDto();
				tempAccomPaymentAdviceDto = temporaryAccomodationService.getPaymentDetails(candidateId, requestId, paymentAdviceId);
				map.addAttribute("hostelList", temporaryAccomodationService.getHostelList());
				map.addAttribute("tempAccomPaymentAdviceDto", tempAccomPaymentAdviceDto);
				map.addAttribute("gender", gender);
				map.addAttribute("messMap",temporaryAccomodationService.fetchMessListAsMap());
				commonResponseUtil.updateCommonModelAttributes(map, request);
				return HTMLPage.MESS_PAYMENT_ADVICE_HOSTEL_VIEW;
			} catch (Exception e) {
				e.printStackTrace();
				commonResponseUtil.exceptionMessageHandling(e, redirectAttributes);
			}
		} else {
			commonResponseUtil.invalidAccess(redirectAttributes);
		}
		return null;
	}
	
	@GetMapping("${url.get.room.list}")
	public @ResponseBody BaseResponse getRoomList(@RequestParam Long requestId, @RequestParam Long paymentAccomAdviceId, Long hostelId) throws Exception {
		Map<Long,String> roomMap = temporaryAccomodationService.getRoomList(requestId, paymentAccomAdviceId, hostelId);
		return CommonResponseUtil.updateResponseByValue(roomMap);
	}
	
	@GetMapping("${url.get.seat}")
	public @ResponseBody BaseResponse getSeatList(@RequestParam Long requestId, @RequestParam Long paymentAccomAdviceId, Long hostelId, String roomNo) throws Exception {
		List<String> seatList = temporaryAccomodationService.getSeatList(requestId, paymentAccomAdviceId, hostelId, roomNo);
		return CommonResponseUtil.updateResponseByValue(seatList);
	}
	
	@PutMapping("${url.save.hostel}")
	public @ResponseBody BaseResponse saveHostelDetails(@RequestBody TempAccomPaymentAdviceDto dto)
			throws Exception {
		boolean status = temporaryAccomodationService.saveHostelDetails(dto);
		return CommonResponseUtil.updateResponseByStatus(status, "response.update.success", "response.update.error");
	}
	
	@PutMapping("${url.change.mess}")
	public @ResponseBody BaseResponse changeMess(@RequestBody TempAccomPaymentAdviceDto dto)
			throws Exception {
		boolean status = temporaryAccomodationService.changeMess(dto);
		return CommonResponseUtil.updateResponseByStatus(status, "response.update.success", "response.update.error");
	}
	
	@GetMapping("/openPdf")
	public void openPdf(@RequestParam String data, HttpServletResponse response) throws Exception {
		Boolean status = Utility.checkRequestType(data, 4, 3);
		if (status) {
			try {
				String[] split = Utility.decryptData(data);
				Long candidateId = Utility.getLongValueOrDefault(split, 0, null);
				Long requestId = Utility.getLongValueOrDefault(split, 1, null);
				Long paymentAdviceId = Utility.getLongValueOrDefault(split, 2, null);
				byte[] pdfBytes = temporaryAccomodationService.generatePdf(candidateId, requestId, paymentAdviceId);
			    response.reset();
			    response.setContentType("application/pdf");
			    response.setHeader("Content-Disposition", "attachment; filename=CandidatePayment.pdf");
			    response.setContentLength(pdfBytes.length);
			    try (ServletOutputStream outputStream = response.getOutputStream()) {
			        outputStream.write(pdfBytes);
			        outputStream.flush();
			    }				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@GetMapping("${url.hostel.accomodation.payment.details}")
	public String getTemporaryOnlinePaymentDetails(@RequestParam Map<String, String> allParams, PaginationForm form,
			ModelMap map, HttpServletRequest request) throws Exception {
		commonResponseUtil.getAdditionalParams(allParams, form);
		Page<TemporaryAccomodationDto> onlinePaymentList = otherCandidatePaymentService.getTemporaryOnlinePaymentDetails(form);
		commonResponseUtil.updateCommonModelAttributes(map, request, onlinePaymentList, form);

		String paymentStatus = form.getAdditionalParam().get("paymentStatus") != null
				? form.getAdditionalParam().get("paymentStatus").toString()
				: null;
		LocalDate submittedFrom = (form.getAdditionalParam().get("submittedFrom") != null
				&& !form.getAdditionalParam().get("submittedFrom").equals(""))
				? LocalDate.parse(form.getAdditionalParam().get("submittedFrom").toString())
				: null;
		LocalDate submittedTo = (form.getAdditionalParam().get("submittedTo") != null
				&& !form.getAdditionalParam().get("submittedTo").equals(""))
				? LocalDate.parse(form.getAdditionalParam().get("submittedTo").toString())
				: null;
		TemporaryAccomodationDto paymentDetailsDto = new TemporaryAccomodationDto();
		paymentDetailsDto.setPaymentStatus(paymentStatus);
		paymentDetailsDto.setSubmittedFrom(submittedFrom);
		paymentDetailsDto.setSubmittedTo(submittedTo);
		map.addAttribute("paymentDetailsDto", paymentDetailsDto);
		return HTMLPage.TEMPORARY_ACCOMMODATION_PAYMENT_LIST;
	}
}
