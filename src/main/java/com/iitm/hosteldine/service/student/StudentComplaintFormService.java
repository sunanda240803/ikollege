package com.iitm.hosteldine.service.student;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.student.StudentComplaintDetailsDto;
import com.iitm.hosteldine.entity.mailQueue.MailQueueDetailsEntity;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.student.StudentComplaintDetailsMapper;
import com.iitm.hosteldine.model.student.StudentComplaintDetailsEntity;
import com.iitm.hosteldine.repository.mailQueue.MailQueueDetailsRepository;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.repository.student.StudentComplaintDetailsRepository;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentComplaintFormService {
	private final StudentComplaintDetailsRepository studentComplaintDetailsRepository;
	private final FileService fileService;
	private final MailQueueDetailsRepository mailQueueRep;
	private final MailQueueService mailQueueService;
	private final MailTemplateRepository mailTemplateRepository;
	private final MessageSource messageSource;
	private final StudentComplaintConfigurationService studentComplaintConfigurationService;
	private final HostelMasterService hostelMasterService;
    private final CommonResponseUtil commonResponseUtil;
    private final Utility utility;


	public Page<StudentComplaintDetailsDto> getStudentComplaintList(PaginationForm form,String studentId) {
		Page<StudentComplaintDetailsEntity> result = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("createdAt").descending());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			result = studentComplaintDetailsRepository.findAllByStudentIdAndActiveFlag(studentId,ModelConstants.STATUS_ACTIVE, pageable);
		}
		else {
	        result = studentComplaintDetailsRepository.findByComplaintSearchList(
	                studentId, ModelConstants.STATUS_ACTIVE, form.getSearch(), pageable);
	    }
		
		return result.map(StudentComplaintDetailsMapper.INSTANCE::fromStudentComplaintDetailsEntity);
	}

	
	
	public String saveStudentComplaintForm(StudentComplaintDetailsDto dto, HttpServletRequest request) throws Exception {
	    String fileUpload = null;
	    String student=dto.getStudentId();
	    long currentMillis = System.currentTimeMillis();
	    String fileExtension = "";
	    if (dto.getUploadedFileName() != null && !dto.getUploadedFileName().isEmpty()) {
	    	
	 	    fileExtension = dto.getUploadedFileName().getOriginalFilename().substring(dto.getUploadedFileName().getOriginalFilename().lastIndexOf("."));
	        fileUpload =   student + ModelConstants.UNDERSCORE+ ModelConstants.FILE_STUDENT_COMPLAINT_PROFILE+ ModelConstants.UNDERSCORE+currentMillis+fileExtension;
	        dto.setFileUpload(fileUpload);
	    }
	    else {
	        dto.setFileUpload(Constants.NA);
	    }

	    StudentComplaintDetailsEntity newEntity = StudentComplaintDetailsMapper.INSTANCE.onSaveEntity(dto);
	    studentComplaintDetailsRepository.save(newEntity);
	   
	    if (dto.getUploadedFileName() != null && !dto.getUploadedFileName().isEmpty()) {
	        fileService.encodeFile(
	        		SimsConfigDataService.STUDENT_COMPLAINT_PATH, 
	                dto.getUploadedFileName().getBytes(),
	                student + ModelConstants.UNDERSCORE+ ModelConstants.FILE_STUDENT_COMPLAINT_PROFILE+ ModelConstants.UNDERSCORE+currentMillis+fileExtension
	        );
	    }
	    String configuredEmail = studentComplaintConfigurationService.getConfiguredEmail(
	    		 dto.getStuComplaintType(), dto.getComplaints()
        );
	    Long hostelId = SecurityCtxUtil.hostelId();
	    HostelMasterDto cc = (hostelId != null) ? hostelMasterService.getHostelDetailsById(hostelId) : null;
	    String hostelOfficeEmail = (cc != null && cc.getHostelOfficeEmail() != null) ? cc.getHostelOfficeEmail() : "";


		 mailTemplateRepository.findByMailType(MailTemplateEntity.STUDENT_COMPLAINT_FORM)
				.ifPresent(templateOpt->{
				
					String subject = templateOpt.getMailSubject();
					 //String messageContent = dto.getComplaintDesc();
					 String content = templateOpt.getMailTemplate();
                     String domainUrl = Utility.getDomainUrl(request);
                     boolean hasFile = !Constants.NA.equalsIgnoreCase(dto.getFileUpload().trim());

                     // Replace placeholders with actual values
						content = content.replaceAll("#%student_id%#", student);
						content = content.replaceAll("#%student_name%#",SecurityCtxUtil.studentFullName().toUpperCase());
						content = content.replaceAll("#%hostel_name%#", SecurityCtxUtil.hostelName().toUpperCase());
						content = content.replaceAll("#%room_number%#", SecurityCtxUtil.roomNumber().toUpperCase());
						content = content.replaceAll("#%complaint_type%#",dto.getStuComplaintType());
						content = content.replaceAll("#%complaint_about%#",dto.getComplaints());
						content = content.replaceAll("#%complaint_desc%#",dto.getComplaintDesc());
						content = content.replaceAll("#%domain_url%#", hasFile ? domainUrl : ModelConstants.EMPTY_STRING);
						content = content.replaceAll("#%display_style%#",hasFile ? "display:inline-block;" : "display:none;");
						content = content.replaceAll("#%not_applicable%#", hasFile ? ModelConstants.EMPTY_STRING : ModelConstants.NOT_APPLICABLE);
						content = content.replaceAll("#%uploaded_file%#", hasFile ? dto.getFileUpload() : ModelConstants.EMPTY_STRING);
					MailQueueDetailsEntity mailQueueDetail;
					
					try {
						boolean mailQueueStatus = mailQueueService.saveMailQueue(subject,
                                commonResponseUtil.getMessage("message.mail.greetings.for"), content,
								configuredEmail, Constants.STUDENT_COMPLAINT,
								SecurityCtxUtil.userId(), 1, null, null, null, null, hostelOfficeEmail);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
		
		
	    return Constants.SAVED;
	}

	

	
	
	public ByteArrayResource downloadComplaintFile(Long complaintId) throws Exception {
        StudentComplaintDetailsEntity complaint = studentComplaintDetailsRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("message.complaint.not.found", null, Locale.getDefault())));
        String fileName = complaint.getFileUpload();
        if (Constants.NA.equals(fileName)) {
            throw new RuntimeException(messageSource.getMessage("message.label.no.upload.file", null, Locale.getDefault()));
        }
        String studentComplaintPath = SimsConfigDataService.STUDENT_COMPLAINT_PATH;

        byte[] fileData = fileService.getDecodedFile(studentComplaintPath, fileName);

        return new ByteArrayResource(fileData);
    }

    public String getFileName(Long complaintId) {
        StudentComplaintDetailsEntity complaint = studentComplaintDetailsRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("message.complaint.not.found", null, Locale.getDefault())));
        return complaint.getFileUpload();
    }
   
    
    public StudentComplaintDetailsDto getComplaintCounts(String studentId) {
        // Prepare constants as a list
        List<String> complaintTypes = Arrays.asList(Constants.HOSTEL, Constants.MESS);

        // Query the database
        List<Object[]> results = studentComplaintDetailsRepository.getComplaintCountsByType(
                ModelConstants.STATUS_ACTIVE,
                studentId,
                complaintTypes
        );

        // Initialize DTO with default values
        StudentComplaintDetailsDto dto = new StudentComplaintDetailsDto();
        dto.setHostelCount(0L);
        dto.setMessCount(0L);

        // Process query results
        for (Object[] record : results) {
            String complaintType = (String) record[0];
            Long complaintCount = (Long) record[1];

            if (Constants.HOSTEL.equalsIgnoreCase(complaintType)) {
                dto.setHostelCount(complaintCount);
            } else if (Constants.MESS.equalsIgnoreCase(complaintType)) {
                dto.setMessCount(complaintCount);
            }
        }

        return dto;
    }

	public List<StudentComplaintDetailsDto> getStudentComplaintList(String complaintType, LocalDate fromDate, LocalDate toDate) {
		if (complaintType != null && complaintType.isBlank()) {
			complaintType = null;
		}
		List<StudentComplaintDetailsDto> studentComplaintDetailsDtoList = studentComplaintDetailsRepository
				.findComplaints(complaintType, fromDate.atStartOfDay(), toDate.atTime(LocalTime.MAX), ModelConstants.STATUS_ACTIVE)
				.stream()
				.map(StudentComplaintDetailsMapper.INSTANCE::fromStudentComplaintDetailsEntity)
				.toList();
		return studentComplaintDetailsDtoList;
	}

	public Workbook generateExcelReport(List<StudentComplaintDetailsDto> studentComplaintDetailsDtoList, String report) throws Exception {
		XSSFWorkbook workbook;
		int colCount = 0;
		ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

		String[] headerList = ModelConstants.STUDENT_COMPLAINT_HEADER;
		final int TOTAL_COLUMNS = headerList.length;
		try {
			workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet(commonResponseUtil.getMessage(report));

			// Create styles using ExcelUtility
			XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
			headerStyle.setWrapText(true);
			XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
			dataStyle.setWrapText(true);

			XSSFCellStyle headerStyle2 = excelUtility.setHeaderStyle(workbook);
			headerStyle2.setWrapText(true);
			headerStyle2.setAlignment(HorizontalAlignment.LEFT);
			headerStyle2.setVerticalAlignment(VerticalAlignment.BOTTOM);

			// Create second header row
			XSSFRow rowheadFirst = sheet.createRow(1);
			excelUtility.createCell(rowheadFirst, 0, commonResponseUtil.getMessage("message.iitm.report.header"), headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, TOTAL_COLUMNS - 1));

			// Create second header row
			XSSFRow rowheadSecond = sheet.createRow(2);
			excelUtility.createCell(rowheadSecond, 0, commonResponseUtil.getMessage(report), headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, TOTAL_COLUMNS - 1));

			// Create third header row for report date
			XSSFRow rowheadthird = sheet.createRow(3);
			SimpleDateFormat sdf = new SimpleDateFormat(commonResponseUtil.getMessage("session.date.format"));
			String reportDate = commonResponseUtil.getMessage("message.label.report.date.colon") + sdf.format(new Date());
			excelUtility.createCell(rowheadthird, 0, reportDate, headerStyle);
			sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, TOTAL_COLUMNS - 1));

			// Create column headers
			XSSFRow rowhead = sheet.createRow(4);

			// Add headers to the sheet
			for (String p : headerList) {
				excelUtility.createCell(rowhead, colCount, p, headerStyle2);
				sheet.setColumnWidth(colCount, 4000); // Set column width
				colCount++;
			}

			// Populate data rows
			int rowCount = 4;
			if (CollectionUtils.isNotEmpty(studentComplaintDetailsDtoList)) {
				int sNo = 0;
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
				for (StudentComplaintDetailsDto studentComplaintDetailsDto : studentComplaintDetailsDtoList) {
					XSSFRow row = sheet.createRow(++rowCount);
					excelUtility.createCell(row, 0, ++sNo, dataStyle);
					excelUtility.createCell(row, 1, studentComplaintDetailsDto.getCreatedAt().toLocalDate().format(formatter), dataStyle);
					excelUtility.createCell(row, 2, studentComplaintDetailsDto.getStudentId(), dataStyle);
					excelUtility.createCell(row, 3, studentComplaintDetailsDto.getStuComplaintType(), dataStyle);
					excelUtility.createCell(row, 4, studentComplaintDetailsDto.getComplaints(), dataStyle);
					excelUtility.createCell(row, 5, studentComplaintDetailsDto.getComplaintDesc(), dataStyle);
				}
			}

			for (int i = 0; i < TOTAL_COLUMNS; i++) {
				if (i == 4 || i == 5) {
					continue;
				}
				sheet.autoSizeColumn(i);
				if (sheet.getColumnWidth(i) > 10000) {
					sheet.setColumnWidth(i, 10000);
				}
			}
			sheet.setColumnWidth(4, 13000);
			sheet.setColumnWidth(5, 20000);
		} catch (Exception exception) {
			exception.printStackTrace();
			throw new Exception("Error in generating report", exception);
		}
		return workbook;
	}
}
