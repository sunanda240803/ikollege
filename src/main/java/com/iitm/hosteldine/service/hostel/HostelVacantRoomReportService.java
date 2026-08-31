package com.iitm.hosteldine.service.hostel;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.util.Strings;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.dto.hostel.VacantRoomDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.repository.hostel.HostelRoomInfoRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.util.Utility;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HostelVacantRoomReportService {
	
	private final HostelRoomInfoRepository hostelRoomInfoRepository;
	private final SimsConfigDataService simsConfigDataService;
	private final MessageSource messageSource;
	private final Utility utility;
	
	
	public Page<VacantRoomDto> getVacantRoomList(Long hostelId, Long floorId, String stableRoom, PaginationForm form, boolean isDownload) {
		int page = form.getPage() - 1;
		Pageable pageable = Pageable.unpaged();
		Page<Object[]> result = Page.empty();
		if(!isDownload) {
			pageable = PageRequest.of(page, form.getSize());
		}
		if(Strings.isNotEmpty(stableRoom)) {
			result = hostelRoomInfoRepository.getHostelVacantRoomDetailsCommon(hostelId, floorId, pageable);
		} else {
			result = hostelRoomInfoRepository.getHostelVacantRoomDetails(hostelId, floorId, pageable);
		}
		
		return result.map(objects -> {
			return setVacanRoomListDto(stableRoom, objects);
		});
	}

	private VacantRoomDto setVacanRoomListDto(String stableRoom, Object[] objects) {
		VacantRoomDto dto = new VacantRoomDto();
		dto.setRoomNo(utility.parseInt(objects[0]));
		dto.setHostelFloorName(utility.parseString(objects[2]));
		if(Strings.isEmpty(stableRoom)) {
			dto.setVacancy(utility.parseInt(objects[5]));
		}		
		return dto;
	}

	public byte[] generatePdf(String hostelName, List<VacantRoomDto> vacantRoomList) throws Exception {
	    byte[] byteVal;

	    try (ByteArrayOutputStream outfile = new ByteArrayOutputStream()) {
	        int marginBottom = 10;
	        int normalFontSize = 11;
	        int boldFontSize = 10;

	        PdfWriter writer = new PdfWriter(outfile);
	        PdfDocument pdfDoc = new PdfDocument(writer);
	        Document document = new Document(pdfDoc, PageSize.A4);

	        float[] columnWidths = { 100 };
	        Table table = new Table(columnWidths);
	        table.setWidth(UnitValue.createPercentValue(100));

	        PdfFont labelFont = PdfFontFactory.createFont("Times-Roman");
	        PdfFont boldFont = PdfFontFactory.createFont("Times-Bold");
	        
	        Cell cell = new Cell();
	        cell.setPadding(5);

	        float[] nestedColumnWidths = { 1, 3 };
	        Table nestedTable = new Table(nestedColumnWidths);
	        nestedTable.setWidth(UnitValue.createPercentValue(100)).setBorder(Border.NO_BORDER);

	        Cell logoCell = new Cell().setBorder(Border.NO_BORDER);
	        ClassLoader classLoader = getClass().getClassLoader();
	        InputStream imageStream = classLoader.getResourceAsStream(simsConfigDataService.getSimConfigValue(SimsConfigDataService.LOGO));
	        if (imageStream != null) {
	            ImageData imageData = ImageDataFactory.create(imageStream.readAllBytes());
	            Image headerLogo = new Image(imageData);
	            headerLogo.setHeight(40);
	            headerLogo.setWidth(40);
	            logoCell.add(headerLogo);
	        }
	        nestedTable.addCell(logoCell);

	        Cell textCell = new Cell().setBorder(Border.NO_BORDER);
	        textCell.add(new Paragraph(messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null, Locale.getDefault()))
	                .setFont(boldFont).setFontSize(boldFontSize)
	                .setTextAlignment(TextAlignment.LEFT).setMarginBottom(marginBottom));
	        nestedTable.addCell(textCell);

	        cell.add(nestedTable);
	        table.addCell(cell);
	        table.setBorder(Border.NO_BORDER);

	        document.add(table);

	        document.add(new Paragraph(messageSource.getMessage("message.label.hostel.name.pdf", null, Locale.getDefault()) + Strings.EMPTY + hostelName)
	                .setFont(labelFont)
	                .setFontSize(normalFontSize)
	                .setTextAlignment(TextAlignment.LEFT)
	                .setMarginBottom(10));

	        // Create table for vacant rooms
	        float[] roomTableWidths = {4, 4, 2};
	        Table roomTable = new Table(roomTableWidths);
	        roomTable.setWidth(UnitValue.createPercentValue(100));

	        // Add table headers
	        roomTable.addHeaderCell(new Cell().add(new Paragraph(messageSource.getMessage("message.label.hostel.floor.name", null, Locale.getDefault())).setFont(boldFont)));
	        roomTable.addHeaderCell(new Cell().add(new Paragraph(messageSource.getMessage("message.label.room.number", null, Locale.getDefault())).setFont(boldFont)));
	        roomTable.addHeaderCell(new Cell().add(new Paragraph(messageSource.getMessage("message.label.vacancy", null, Locale.getDefault())).setFont(boldFont)));

	        for (VacantRoomDto dto : vacantRoomList) {
	            roomTable.addCell(new Cell().add(new Paragraph(dto.getHostelFloorName()).setFont(labelFont).setFontSize(normalFontSize)));
	            roomTable.addCell(new Cell().add(new Paragraph(String.valueOf(dto.getRoomNo())).setFont(labelFont).setFontSize(normalFontSize)));
	            roomTable.addCell(new Cell().add(new Paragraph(String.valueOf(dto.getVacancy())).setFont(labelFont).setFontSize(normalFontSize)));
	        }

	        document.add(roomTable);

	        document.close();
	        byteVal = outfile.toByteArray();
	    }

	    return byteVal;
	}
}
