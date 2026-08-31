package com.iitm.hosteldine.service.mess;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.MessAllottedDinedDto;
import com.iitm.hosteldine.form.common.MessDineSummaryForm;
import com.iitm.hosteldine.repository.mess.MessMasterControllerRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.service.PdfActionService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessAllottedDinedReportService {

    private final MessMasterControllerRepository messMasterControllerRepository;
    private final MessageSource messageSource;
    private final MessMasterRepository messMasterRepository;
    private final SimsConfigDataService simsConfigDataService;


    public MessAllottedDinedDto getMessAllottedDined(MessDineSummaryForm searchForm) throws Exception {
        Long messPeriodId = searchForm.getMessPeriodId() != null ? searchForm.getMessPeriodId().longValue() : null;
        Long messNameId = searchForm.getMessId() != null ? searchForm.getMessId().longValue() : 0;

        // Add validation if needed
        if (messPeriodId == null) {
            throw new IllegalArgumentException(messageSource.getMessage("message.validation.mess.period.required", null, Locale.getDefault()));
        }

        // Get mess period details
        List<Object[]> messAllottedDineDetails = messMasterRepository.getMessAllottedDineDetails(messPeriodId, messNameId);

        if (CollectionUtils.isEmpty(messAllottedDineDetails)) {
            throw new RuntimeException("Mess period details not found");
        }

        Object[] periodDetails = messAllottedDineDetails.get(0);
        if (periodDetails == null || periodDetails.length == 0) {
            throw new RuntimeException("Mess period details not found");
        }

        LocalDate fromDate = DateUtility.parseSqlDateToLocalDate((java.sql.Date) periodDetails[0]);// fromDate
        LocalDate toDate = DateUtility.parseSqlDateToLocalDate((java.sql.Date) periodDetails[1]);// toDate

        // Get attendance summary
        List<Object[]> messAllottedDinedCount = messMasterRepository.getMessAllottedDinedCount(
                fromDate,
                toDate,
                messNameId,
                messPeriodId);
        Object[] attendanceSummary = null;
        if (CollectionUtils.isNotEmpty(messAllottedDinedCount)) {
            attendanceSummary = messAllottedDinedCount.getFirst();
            if (attendanceSummary == null || attendanceSummary.length == 0) {
                attendanceSummary = new Object[]{0, 0};
            }
        }

        return new MessAllottedDinedDto(periodDetails, attendanceSummary);
    }

    private final PdfActionService pdfActiveService;

    public Resource generatePdf(MessDineSummaryForm searchForm) throws Exception {
        String tempFileLocation = pdfActiveService.getTempFileLocation();
        String fileName =  messageSource.getMessage("message.label.mess.allotted.dined.report", null, Locale.getDefault())
                + ModelConstants.UNDERSCORE + System.currentTimeMillis() + PdfActionService.PDF_EXTENSION;
        String outputFilePath = tempFileLocation + fileName;
        MessAllottedDinedDto messAllottedDined = getMessAllottedDined(searchForm);

        try (PdfWriter writer = new PdfWriter(outputFilePath);
             PdfDocument pdfDocument = new PdfDocument(writer);
             Document document = new Document(pdfDocument)) {
            setPdfDocumentHeader(document);//Pdf Header Section
            document.add(new LineSeparator(new SolidLine()).setMarginTop(5F).setMarginBottom(10F));

            setPdfPersonalDetails(document, messAllottedDined);//Pdf Personal Details Section

            pdfActiveService.addWatermarkImage(pdfDocument);
            document.close();
            return new FileSystemResource(outputFilePath);
        } catch (IOException e) {
            throw new Exception(messageSource.getMessage("message.label.error.generate.pdf", null, Locale.getDefault()), e);
        }
    }


    public void setPdfDocumentHeader(Document document) throws IOException {
        Table idTable = new Table(1)
                .setWidth(UnitValue.createPercentValue(100))
                .addCell(new Cell()
                        .add(new Paragraph(messageSource.getMessage("message.label.sl.no.pdf", null, Locale.getDefault()))
                                .setFontSize(10)
                                .setBold())
                        .setBorder(Border.NO_BORDER)
                        .setTextAlignment(TextAlignment.RIGHT));
        //document.add(idTable);
        //document.add(new Paragraph(ModelConstants.NEW_LINE).setMarginBottom(10));
        pdfActiveService.addDocumentHeader(document,
                messageSource.getMessage("message.label.heading", null, Locale.getDefault()));
        document.add(new Paragraph(messageSource.getMessage("message.label.mess.allotted.dined.pdf.heading", null, Locale.getDefault()))
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setMarginTop(-10)
                .setFontSize(14)
                .setMarginLeft(80));

    }

    public void setPdfPersonalDetails(Document document, MessAllottedDinedDto dto) throws IOException {
        String[] messageKeys = {
                "message.label.pdf.mess.period",
                "message.label.mess.name",
                "message.label.total.days",
                "message.label.no.of.students.allotted",
                "message.label.total.no.of.sessions.allotted.for.students",
                "message.label.actual.no.of.sessions.dined.by.students",
                "message.label.actual.no.of.days.dined.by.student",
                "message.label.actual.no.of.days.dined.by.students",
                "message.label.mess.rate",
                "message.label.mess.allotted.dined.total.amount"
        };
        String[] messages = Arrays.stream(messageKeys)
                .map(key -> messageSource.getMessage(key, null, Locale.getDefault()))
                .toArray(String[]::new);
        document.add(pdfActiveService.addFullWidthSubTitle("Report Date: "+ DateUtility.formatDate(LocalDate.now()), TextAlignment.LEFT));
        document.add(new Paragraph(PdfActionService.NEXT_LINE));
        document.add(pdfActiveService.addFullWidthTitle(messages[0] + "( "+DateUtility.formatDate(dto.getFromDate())+" to "+
                DateUtility.formatDate(dto.getToDate())+" )", TextAlignment.CENTER));
        document.add(new Paragraph(PdfActionService.NEXT_LINE));
        Table personalDetailsTable = new Table(new float[]{4,6});
        personalDetailsTable.setWidth(PdfActionService.VALUE_100_P);
        personalDetailsTable.setMarginBottom(5);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
        pdfActiveService.addTableTextValue(messages[1], dto.getMessName(), personalDetailsTable);
        pdfActiveService.addTableTextValue(messages[2], dto.getTotalDays(), personalDetailsTable);
        pdfActiveService.addTableTextValue(messages[3], dto.getStudentsAlloted(), personalDetailsTable);
        pdfActiveService.addTableTextValue(messages[4], dto.getTotalDays() * dto.getStudentsAlloted(), personalDetailsTable);

        pdfActiveService.addTableTextValue(messages[5], dto.getSessionTotalCount(), personalDetailsTable);
        pdfActiveService.addTableTextValue(messages[6], dto.getDayTotalCount() != null && dto.getStudentsAlloted() != 0 ?
                dto.getDayTotalCount() / dto.getStudentsAlloted() : 0, personalDetailsTable);
        pdfActiveService.addTableTextValue(messages[7], dto.getDayTotalCount(), personalDetailsTable);
        pdfActiveService.addTableTextValue(messages[8],
                dto.getRate() != null ? String.format("%.2f", dto.getRate().doubleValue()) : "0.00",
                personalDetailsTable);

        pdfActiveService.addTableTextValue(messages[9],
                (dto.getDayTotalCount() != null && dto.getRate() != null)
                        ? String.format("%.2f", (dto.getDayTotalCount() * dto.getRate()))
                        : "0.00",
                personalDetailsTable);

        document.add(personalDetailsTable);
    }


}


