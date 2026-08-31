package com.iitm.hosteldine.service.student;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.student.SickFoodDeliveryStatusDto;
import com.iitm.hosteldine.dto.student.SickFoodRequestDto;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.student.SickFoodDeliveryStatusMapper;
import com.iitm.hosteldine.model.student.SickFoodDeliveryStatusEntity;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.repository.student.SickFoodDeliveryStatusRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.Utility;

import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SickFoodDeliveryStatusService {
	private final SickFoodDeliveryStatusRepository sickFoodDeliveryStatusRepository;
	private final MessageSource messageSource;
	private final SimsConfigDataService simsConfigDataService;
	private final MessMasterRepository messMasterRepository;
	private final MailQueueService mailQueueService;
	private final MailTemplateRepository mailTemplateRepository;
	private final Utility utility;
	private final CommonResponseUtil commonResponseUtil;

    public boolean updateDeliveryStatus(SickFoodDeliveryStatusDto requestDto) throws Exception {
   	 SickFoodDeliveryStatusEntity deliveryStatus = sickFoodDeliveryStatusRepository.findByIdAndActiveFlag(requestDto.getId(),ModelConstants.STATUS_ACTIVE).orElse(null);
   	
       if (deliveryStatus!=null && Constants.OUT_FOR_DELIVERY.equalsIgnoreCase(deliveryStatus.getCatererStatus())) {
       	deliveryStatus.setStudentDeliveryStatus(Constants.RECEIVED);
       	deliveryStatus.setCatererStatus(Constants.DELIVERED);
           // Update feedback and rating only if provided
           if (requestDto.getStudentFeedback() != null &&  requestDto.getFeedbackRating() != null) {
           	deliveryStatus.setStudentFeedback( requestDto.getStudentFeedback());
           	deliveryStatus.setFeedbackRating(requestDto.getFeedbackRating());
               
              
           }
           sickFoodDeliveryStatusRepository.save(deliveryStatus);
           return true;
       } else {
           throw new Exception(messageSource.getMessage("message.caterer.status.not.delivered", null, Locale.getDefault()));
       }
   } 
    
   public List<SickFoodDeliveryStatusDto> getSickFoodDeliveryStatuses(Long id){
	    return sickFoodDeliveryStatusRepository.getSickFoodDeliveryStatuses(id, ModelConstants.STATUS_ACTIVE)
       		.stream()
       		.map(SickFoodDeliveryStatusMapper.INSTANCE::fromSickFoodDeliveryStatusEntity).toList();
   }
   
   public List<SickFoodDeliveryStatusDto> getSickFoodDeliveryStatusByRequestId(long id){
	return sickFoodDeliveryStatusRepository
	   .findAllBySickFoodRequestIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE).stream()
		.map(SickFoodDeliveryStatusMapper.INSTANCE::fromSickFoodDeliveryStatusEntity).toList();
   }
   
   public void getPendingDeliveries(SickFoodDeliveryStatusDto requestDto) throws Exception {
	   Long messId = getSickFoodAvailMessId();
	   Long  requestId=requestDto.getSickFoodRequest().getId();
       int messIds = messId.intValue();
       int thresholdTime = Integer.parseInt(simsConfigDataService.getSimConfigValue("SICK_FOOD_THRESHOLD_TIME"));
       String currentTimeString = LocalTime.now().format(DateTimeFormatter.ofPattern(Constants.TIME_FORMAT));
       List<Object[]> result= sickFoodDeliveryStatusRepository.findPendingDeliveries(messIds, thresholdTime,currentTimeString, requestId,ModelConstants.STATUS_ACTIVE);
       List<SickFoodDeliveryStatusDto> pendingDeliveries = new ArrayList<>();
       for (Object[] row : result) {
           SickFoodDeliveryStatusDto dto = new SickFoodDeliveryStatusDto();

           // Map columns to fields
           dto.setId(row[0] != null ? ((Number) row[0]).longValue() : null); 
           dto.setMessSession(row[2] != null ? row[2].toString() : null); 
           dto.setCatererStatus(row[3] != null ? row[3].toString() : null); 
           dto.setFoodDeliveryStatus(row[13] != null ? (Boolean) row[13] : false); 
           dto.setSessionName(row[14] != null ? row[14].toString() : null); 
           dto.setStartTime(row[15] != null ? row[15].toString() : null);
           dto.setEndTime(row[16] != null ? row[16].toString() : null);
           SickFoodRequestDto sickFoodRequestDto = new SickFoodRequestDto();
           sickFoodRequestDto.setId(requestId);  

           dto.setSickFoodRequest(sickFoodRequestDto);  
           pendingDeliveries.add(dto);
           }
       
       pendingDeliveries
    		   .stream()
    		   .filter(f-> f.getMessSession().equals(requestDto.getMessSession()))
    		   .findFirst()
    		   .ifPresent(f->{
    			   requestDto.setSessionName(f.getSessionName());
    			   requestDto.setFoodDeliveryStatus(f.getFoodDeliveryStatus());
				   requestDto.setStartTime(f.getStartTime());
				   requestDto.setEndTime(f.getEndTime());

    		   });
       
    		   
   }
   
   public Long getSickFoodAvailMessId() throws Exception {
		return messMasterRepository.getMessMasterIdByMessType()
				.orElseThrow(() -> new Exception (messageSource.getMessage("message.active.mess.id.not.found", null, Locale.getDefault())));
						
	}
   
   
   public boolean foodNotDeliverStatus(SickFoodDeliveryStatusDto requestDto) throws Exception {
	   	 SickFoodDeliveryStatusEntity deliveryStatus = sickFoodDeliveryStatusRepository.findByIdAndActiveFlag(requestDto.getId(),ModelConstants.STATUS_ACTIVE).orElse(null);

	   	   Optional<MailTemplateEntity> templateOpt = mailTemplateRepository
					.findByMailType(MailTemplateEntity.SICK_FOOD_NOT_DELIVER);
			if (templateOpt.isPresent()) {
				MailTemplateEntity template = templateOpt.get();
				String subject = template.getMailSubject();
				String content = template.getMailTemplate();

				content = content.replaceAll("#%student_id%#", deliveryStatus.getSickFoodRequest().getStudentId());
				content = content.replaceAll("#%medical_reason%#", deliveryStatus.getSickFoodRequest().getMedicalReason());
				content = content.replaceAll("#%mobile_number%#", deliveryStatus.getSickFoodRequest().getMobileNum());
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
				String formattedDate = deliveryStatus.getSickFoodRequest().getRequestDate().format(formatter);
				content = content.replaceAll("#%request_date%#", formattedDate);

				// Save mail queue details
				boolean mailStatus = mailQueueService.saveMailQueue(subject,
						commonResponseUtil.getMessage("message.mail.greetings.for"), content,
						simsConfigDataService.getSimConfigValue(SimsConfigDataService.SICK_FOOD_REQUEST_ADMIN_EMAIL), Constants.SICK_FOOD_NOT_DELIVER,
						SecurityCtxUtil.userId(), 1, null, null, null, null);
				if(mailStatus==true) {
				 deliveryStatus.setFoodDeliveryStatus(true);
		           sickFoodDeliveryStatusRepository.save(deliveryStatus);
		           return true;
			}
				else {
					return false;
				}
			}

			else {
				throw new RuntimeException(messageSource.getMessage("message.mail.template.sickfood.request", null, Locale.getDefault()));
			}
			
			
	       } 
   
   public Page<SickFoodDeliveryStatusDto> getSickFoodRequestList(PaginationForm form, boolean isExcelReport) {
		String studentId = (form.getAdditionalParam().get("studentId") != null && !form.getAdditionalParam().get("studentId").equals("")) ? form.getAdditionalParam().get("studentId").toString() : null;
		String studentName = (form.getAdditionalParam().get("studentName") != null && !form.getAdditionalParam().get("studentName").equals("")) ? form.getAdditionalParam().get("studentName").toString() : null;
		LocalDate requestFromDate = (form.getAdditionalParam().get("requestFromDate") != null && !form.getAdditionalParam().get("requestFromDate").equals("")) ? LocalDate.parse(form.getAdditionalParam().get("requestFromDate").toString()) : null;
		LocalDate requestToDate = (form.getAdditionalParam().get("requestToDate") != null && !form.getAdditionalParam().get("requestToDate").equals("")) ? LocalDate.parse(form.getAdditionalParam().get("requestToDate").toString()) : null;
		String catererStatus = (form.getAdditionalParam().get("catererStatusId") != null && !form.getAdditionalParam().get("catererStatusId").equals("")) ? form.getAdditionalParam().get("catererStatusId").toString() : null;
		String studentStatus = (form.getAdditionalParam().get("studentStatusId") != null && !form.getAdditionalParam().get("studentStatusId").equals("")) ? form.getAdditionalParam().get("studentStatusId").toString() : null;
		String messSession = (form.getAdditionalParam().get("messSession") != null && !form.getAdditionalParam().get("messSession").equals("")) ? form.getAdditionalParam().get("messSession").toString() : null;
		
		Pageable pageable = Pageable.unpaged();
		Page<Object[]> result = Page.empty();
		
		if (isExcelReport) {
			pageable = Pageable.unpaged();
		} else {
			int page = form.getPage() - 1;
			pageable = PageRequest.of(page, form.getSize());			
		}

		String userRole = SecurityCtxUtil.userRole();
		String userName = SecurityCtxUtil.userName();
		
		result = sickFoodDeliveryStatusRepository.getSickFoodRequestList(userRole.toUpperCase(), userName, 
				requestFromDate != null ? requestFromDate.toString() : null,
				requestToDate != null ? requestToDate.toString() : null,
				studentId, studentName, 
				catererStatus, 
				studentStatus, 
				messSession, 
				pageable);
		
		return setSickFoodRequestList(result);		
	}
   
	private Page<SickFoodDeliveryStatusDto> setSickFoodRequestList(Page<Object[]> result) {
		return result.map(record -> {
			SickFoodDeliveryStatusDto dto = new SickFoodDeliveryStatusDto();
			SickFoodRequestDto sickFoodRequestDto = new SickFoodRequestDto();
			sickFoodRequestDto.setRequestDate(utility.convertToLocalDate(record[2]));
			sickFoodRequestDto.setStudentId(utility.parseString(record[3]));
			sickFoodRequestDto.setStudentName(utility.parseString(record[4]));
			sickFoodRequestDto.setMedicalReason(utility.parseString(record[7]));
			sickFoodRequestDto.setMessName(utility.parseString(record[14]));
			sickFoodRequestDto.setMessType(utility.parseString(record[9]));
			sickFoodRequestDto.setMessSession(utility.parseString(record[8]));
			sickFoodRequestDto.setCatererStatus(utility.parseString(record[10]));
			dto.setStudentDeliveryStatus(utility.parseString(record[11]));
			dto.setStudentFeedback(utility.parseString(record[12]));
			dto.setFeedbackRating(utility.parseString(record[18]));
			dto.setSickFoodRequest(sickFoodRequestDto);
			return dto;
		});
	}
	
	public Workbook getSickFoodRequestReport(List<SickFoodDeliveryStatusDto> sickFoodRequestList) throws Exception {
		XSSFWorkbook workbook = null;
		int colCount = 0;
		ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

		try {
			workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(messageSource.getMessage("message.label.sick.food.request.list", null, Locale.getDefault()));

			// Create styles using ExcelUtility
			XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
			headerStyle.setWrapText(true);
			XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
			dataStyle.setWrapText(true);
			
			// Create First header row
			XSSFRow rowheadZero = sheet.createRow(1);
            excelUtility.createCell(rowheadZero, 0, messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 12));
            
			// Create second header row
			XSSFRow rowheadFirst = sheet.createRow(2);
			excelUtility.createCell(rowheadFirst, 0, messageSource.getMessage("message.label.sick.food.request.list", null, Locale.getDefault()), headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 12));

			// Create third header row for report date
			XSSFRow rowheadSecond = sheet.createRow(3);
			SimpleDateFormat sdf = new SimpleDateFormat(messageSource.getMessage("session.date.format", null, Locale.getDefault()));
    		String reportDate = messageSource.getMessage("message.label.report.date.colon", null, Locale.getDefault()) + sdf.format(new Date());
            excelUtility.createCell(rowheadSecond, 0, reportDate, headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 12));

			// Create column headers
			XSSFRow rowhead = sheet.createRow(4);
			String[] headers = { messageSource.getMessage("message.label.report.student.id", null, Locale.getDefault()),
					messageSource.getMessage("message.label.student.name", null, Locale.getDefault()),
					messageSource.getMessage("message.label.request.date", null, Locale.getDefault()),
					messageSource.getMessage("message.label.medical.reason", null, Locale.getDefault()),
					messageSource.getMessage("message.label.mess.name", null, Locale.getDefault()),
					messageSource.getMessage("message.label.mess.type", null, Locale.getDefault()),
					messageSource.getMessage("message.label.mess.session", null, Locale.getDefault()),
					messageSource.getMessage("message.label.caterer.status", null, Locale.getDefault()),
					messageSource.getMessage("message.label.student.status", null, Locale.getDefault()),
					messageSource.getMessage("message.label.mess.inspection.student.feedBack", null, Locale.getDefault()),
					messageSource.getMessage("message.label.feedback.rating.start", null, Locale.getDefault())

					 };

			// Add headers to the sheet
			for (String header : headers) {
				excelUtility.createCell(rowhead, colCount, header, headerStyle);
				sheet.setColumnWidth(colCount, 4000); // Set column width
				colCount++;
			}

			// Populate data rows
			int rowcount = 4;
			if (CollectionUtils.isNotEmpty(sickFoodRequestList)) {
				for (SickFoodDeliveryStatusDto dto : sickFoodRequestList) {
					rowcount++;
					XSSFRow row = sheet.createRow(rowcount);
					excelUtility.createCell(row, 0, dto.getSickFoodRequest().getStudentId(), dataStyle);
					excelUtility.createCell(row, 1, dto.getSickFoodRequest().getStudentName(), dataStyle);
					excelUtility.createCell(row, 2, utility.convertToLocalDate(dto.getSickFoodRequest().getRequestDate()).format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT)), dataStyle);
					excelUtility.createCell(row, 3, dto.getSickFoodRequest().getMedicalReason(), dataStyle);
					excelUtility.createCell(row, 4, dto.getSickFoodRequest().getMessName(), dataStyle);
					excelUtility.createCell(row, 5, dto.getSickFoodRequest().getMessType(), dataStyle);
					excelUtility.createCell(row, 6, dto.getSickFoodRequest().getMessSession(), dataStyle);
					excelUtility.createCell(row, 7, dto.getSickFoodRequest().getCatererStatus(), dataStyle);
					excelUtility.createCell(row, 8, dto.getStudentDeliveryStatus(), dataStyle);
					excelUtility.createCell(row, 9, dto.getStudentFeedback(), dataStyle);
					excelUtility.createCell(row, 10, dto.getFeedbackRating(), dataStyle);
				}
			}

			// Auto-size columns with a maximum width limit
			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
				// Cap the column width to 10000 (about 100 characters) to prevent extremely wide columns
				if (sheet.getColumnWidth(i) > 10000) {
					sheet.setColumnWidth(i, 10000);
				}
			}


		} catch (Exception exception) {
			exception.printStackTrace();
			throw new Exception("Error generating Sick Food Request List report", exception);
		}
		return workbook;
	}
}
   


