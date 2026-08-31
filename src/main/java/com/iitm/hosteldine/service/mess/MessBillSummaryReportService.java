package com.iitm.hosteldine.service.mess;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.MessBillSummaryDto;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.model.mess.StudentMessDetailsEntity;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.repository.mess.MessMasterControllerRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.repository.studentDashboard.StudentMessDetailsRepository;
import com.iitm.hosteldine.service.PdfActionService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.validator.common.ValidationCommon;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessBillSummaryReportService {

	private final MessageSource messageSource;
	private final Utility utility;
	private final ExcelUtility excelUtility;
	private final MessMasterControllerRepository messMasterControllerRepo;
	private final MessMasterRepository messMasterRepo;
	private final PdfActionService pdfActiveService;
	private final MailTemplateRepository mailTemplateRepo;
	private final MailQueueService mailQueueService;
	private final StudentMessDetailsRepository studentMessDetailsRepo;
	private final MessAllottedListService messAllottedListService;
	public static final String MESS_BILL_SUMMARY_REPORT = "MESS_BILL_SUMMARY_REPORT";

	private List<Object[]> fetchMessBillSummaryDetails(Integer messPeriod, Integer messName) {
		return messMasterControllerRepo.getMessBillSummaryReport(messName, messPeriod);
	}

	public Workbook downloadMessBillSummaryReport(Integer messPeriod, Integer messName) throws Exception {
		String messbillReport = messageSource.getMessage("message.label.mess.bill.summary.report", null,
				Locale.getDefault());
		String sheetName = messbillReport;
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);

		int columnCount = ExcelConstants.MESS_BILL_SUMMARY_HEADER_DATA.length;
		String[] headerData = ExcelConstants.MESS_BILL_SUMMARY_HEADER_DATA;
		String[] headerDataWidth = ExcelConstants.MESS_BILL_SUMMARY_HEADER_DATA_WIDTH;

		XSSFCellStyle style3 = excelUtility.setDataStyle(workbook);
		XSSFCellStyle centerAlignStyle = excelUtility.setCenterAlignStyle(workbook);

		// Add new row with title "Office of the Hostel Management - IITMADRAS CAMPUS"
		Row row0 = sheet.createRow(0);
		Cell cell0 = row0.createCell(0);
		cell0.setCellValue(messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null,
				Locale.getDefault()));
		cell0.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

		// Row 1 with wellness report title
		Row row1 = sheet.createRow(1);
		Cell cell1 = row1.createCell(0);
		cell1.setCellValue(messbillReport);
		cell1.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, columnCount - 1));

		// Row 2 with report date
		Row row2 = sheet.createRow(2);
		SimpleDateFormat sdf = new SimpleDateFormat(
				messageSource.getMessage("session.date.format", null, Locale.getDefault()));
		String reportDate = messageSource.getMessage("message.label.report.date.colon", null, Locale.getDefault())
				+ sdf.format(new Date());
		cell1 = row2.createCell(0);
		cell1.setCellValue(reportDate);
		cell1.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, columnCount - 1));

		String messPeriodName = messMasterControllerRepo.findById(Long.valueOf(messPeriod))
				.map(entity -> DateUtility.formatDate(entity.getDiningFromDate()) + " to "
						+ DateUtility.formatDate(entity.getDiningToDate()))
				.orElse("");

		// Row 4 Mess Period
		Row row3 = sheet.createRow(3);
		Cell cell3 = row3.createCell(0);
		cell3.setCellValue("Mess Period: " + messPeriodName);
		cell3.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, columnCount - 1));

		String mess = messMasterRepo.findByIdAndActiveFlag(Long.valueOf(messName), ModelConstants.STATUS_ACTIVE)
				.map(entity -> entity.getMessName()).orElse("");

		// Row 4 Mess Period
		Row row4 = sheet.createRow(4);
		Cell cell4 = row4.createCell(0);
		cell4.setCellValue("Mess Name: " + mess);
		cell4.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, columnCount - 1));

		// Row 3 for header data
		Row row5 = sheet.createRow(5);
		excelUtility.createHeader(row5, 0, headerData, workbook);

		// Set column widths
		IntStream.range(0, headerData.length).forEach(i -> {
			int width = Integer.parseInt(headerDataWidth[i]);
			sheet.setColumnWidth(i, width);
		});

		// Fetch and populate wellness data
		List<Object[]> entityList = fetchMessBillSummaryDetails(messPeriod, messName);
		AtomicInteger rowCount = new AtomicInteger(5);

		AtomicLong slNo = new AtomicLong(1);

		entityList.forEach(messBill -> {
			Row row = sheet.createRow(rowCount.incrementAndGet());
			try {
				String[] values = { String.valueOf(slNo.getAndIncrement()), ValidationCommon.toString(messBill[4]),
						ValidationCommon.getReportNonNullValue(messBill[5]),
						ValidationCommon.getReportNonNullValue(DateUtility.formatDate(messBill[0])),
						ValidationCommon.getReportNonNullValue(DateUtility.formatDate(messBill[1])),
						ValidationCommon.getReportNonNullValue(messBill[9]),
						ValidationCommon.getReportNonNullValue(messBill[12]),
						ValidationCommon.getReportNonNullValue(DateUtility.formatDate(messBill[13])),
						ValidationCommon.getReportNonNullValue(DateUtility.formatDate(messBill[15])),
						ValidationCommon.getReportNonNullValue(messBill[17]),
						utility.formatCommaSeperatedCurrency(
								Double.valueOf(ValidationCommon.getSafeDouble((messBill[18])))),
						utility.formatCommaSeperatedCurrency(
								Double.valueOf(ValidationCommon.getSafeDouble(messBill[19]))),
						utility.formatCommaSeperatedCurrency(
								Double.valueOf(ValidationCommon.getSafeDouble(messBill[21]))),
						utility.formatCommaSeperatedCurrency(
								Double.valueOf(ValidationCommon.getSafeDouble(messBill[22]))) };
				for (int colIdx = 0; colIdx < values.length; colIdx++) {
					excelUtility.createAndSetColumn(sheet, row, colIdx, -1, values[colIdx], style3);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		// Auto-size columns
		for (int i = 0; i < columnCount; i++) {
			sheet.autoSizeColumn(i);
		}
		return workbook;
	}

	public String downloadMessBillSummaryPDF(Integer messPeriod, Integer messName, HttpServletResponse response)
			throws NoSuchMessageException, Exception {
		String fileName = "Mess_Bill_Summary_" + System.currentTimeMillis() + PdfActionService.PDF_EXTENSION;
		try {
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
			PdfWriter writer = new PdfWriter(response.getOutputStream());

			try (PdfDocument pdfDocument = new PdfDocument(writer); Document document = new Document(pdfDocument)) {
				pdfDocument.setDefaultPageSize(PageSize.A4.rotate());

				// Add header
				pdfActiveService.addDocumentHeader(document,
						messageSource.getMessage("message.label.heading", null, Locale.getDefault()),
						messageSource.getMessage("message.label.mess.bill.summary.report", null, Locale.getDefault()));

				document.add(new Paragraph(PdfActionService.NEXT_LINE));

				// Column widths as per your design
				float[] columnWidths = { 1f, 2f, 3f, 2.5f, 2.5f, 2f, 3f, 2.5f, 2.5f, 2.5f, 2f, 2f, 1.5f, 2.5f };
				Table table = new Table(columnWidths);
				table.setWidth(UnitValue.createPercentValue(100));

				// Add header cells
				String[] headers = ExcelConstants.MESS_BILL_SUMMARY_HEADER_DATA;
				for (String header : headers) {
					table.addHeaderCell(header);
				}

				// Fetch data
				List<Object[]> entityList = fetchMessBillSummaryDetails(messPeriod, messName);
				AtomicInteger slNo = new AtomicInteger(1);

				for (Object[] messBill : entityList) {
					String[] values = { String.valueOf(slNo.getAndIncrement()), ValidationCommon.toString(messBill[4]),
							ValidationCommon.getReportNonNullValue(messBill[5]).toUpperCase(),
							DateUtility.formatDate(messBill[0]), DateUtility.formatDate(messBill[1]),
							ValidationCommon.getReportNonNullValue(messBill[9]),
							ValidationCommon.getReportNonNullValue(messBill[12]), DateUtility.formatDate(messBill[13]),
							DateUtility.formatDate(messBill[15]), ValidationCommon.getReportNonNullValue(messBill[17]),
							utility.formatCommaSeperatedCurrency(ValidationCommon.getSafeDouble(messBill[18])),
							utility.formatCommaSeperatedCurrency(ValidationCommon.getSafeDouble(messBill[19])),
							utility.formatCommaSeperatedCurrency(ValidationCommon.getSafeDouble(messBill[21])),
							utility.formatCommaSeperatedCurrency(ValidationCommon.getSafeDouble(messBill[22])) };

					for (String val : values) {
						table.addCell(val != null ? val : "");
					}
				}
				document.add(table);
				return fileName;
			}
		} catch (IOException e) {
			throw new Exception(messageSource.getMessage("message.label.error.generate.pdf", null, Locale.getDefault()),
					e);
		}
	}

	@Transactional
	public String sendMailToStudents(MessBillSummaryDto dto) throws NoSuchMessageException, Exception {
		List<Object[]> entityList = fetchMessBillSummaryDetails(dto.getMessPeriod(), dto.getMessName());
		if (entityList != null && !entityList.isEmpty()) {
			Optional<MailTemplateEntity> mailTemplate = mailTemplateRepo
					.findByMailType(MailTemplateEntity.MESS_BILL_SUMMARY_MAIL);
			if (mailTemplate.isPresent()) {
				int count = 0;
				MailTemplateEntity template = mailTemplate.get();
				for (Object[] messBill : entityList) {
					String subject = template.getMailSubject();
					String content = template.getMailTemplate();
					String studentName = ValidationCommon.getReportNonNullValue(messBill[5]).toUpperCase();
					String studentEmail = ValidationCommon.getReportNonNullValue(messBill[6]);
					content = content.replaceAll("#%student_name%#", studentName);
					content = content.replaceAll("#%dining_from%#", DateUtility.formatDate(messBill[0]));
					content = content.replaceAll("#%dining_to%#", DateUtility.formatDate(messBill[1]));
					content = content.replaceAll("#%dined_days%#",
							ValidationCommon.getReportNonNullValue(messBill[17]));
					content = content.replaceAll("#%per_day_rate%#",
							utility.formatCommaSeperatedCurrency(ValidationCommon.getSafeDouble(messBill[18])));
					content = content.replaceAll("#%dined_amount%#",
							utility.formatCommaSeperatedCurrency(ValidationCommon.getSafeDouble(messBill[19])));
					content = content.replaceAll("#%gst_amount%#",
							utility.formatCommaSeperatedCurrency(ValidationCommon.getSafeDouble(messBill[21])));
					content = content.replaceAll("#%total_amount%#",
							utility.formatCommaSeperatedCurrency(ValidationCommon.getSafeDouble(messBill[22])));

					mailQueueService.saveMailQueue(subject, studentName, content, studentEmail,
							MESS_BILL_SUMMARY_REPORT, SecurityCtxUtil.userId(), 1, null, null,
							MailQueueService.BEST_REGARDS, null);
					count++;
				}
				if (count > 0) {
					return Constants.SAVED;
				} else {
					return null;
				}
			}
		}
		return null;
	}

	@Transactional
	public String saveStudentMessChangeFromDate(MessBillSummaryDto dto) {
		Optional<StudentMessDetailsEntity> existingEntity = studentMessDetailsRepo
				.findByStudentDetailsInfoStudentIdAndCurrentActiveFlagAndMmcId(dto.getStudentId(),
						ModelConstants.STATUS_ACTIVE, messAllottedListService.fetchCurrentMessPeriodId());
		int count = 0;
		if (!existingEntity.isEmpty()) {
			count = studentMessDetailsRepo.updateFromDateAndCommentsToEntity(dto.getEffectiveFromDate(),
					dto.getComments(), SecurityCtxUtil.userId(), DateUtility.getNowTimeInstant(), dto.getStudentId(),
					messAllottedListService.fetchCurrentMessPeriodId(), ModelConstants.STATUS_ACTIVE);
		}
		return count > 0 ? Constants.SAVED : null;
	}
	
}