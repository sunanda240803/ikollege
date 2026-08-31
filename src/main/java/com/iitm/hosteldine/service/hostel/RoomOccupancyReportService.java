package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.dto.hostel.RoomOccupancyDto;
import com.iitm.hosteldine.form.common.RoomOccupancyForm;
import com.iitm.hosteldine.model.hostel.HostelMasterEntity;
import com.iitm.hosteldine.repository.hostel.HostelMasterRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomInfoRepository;
import com.iitm.hosteldine.repository.student.ShowStudentDetailRepository;
import com.iitm.hosteldine.util.ExcelUtility;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomOccupancyReportService {

    private final ShowStudentDetailRepository showStudentDetailRepository;
    private final MessageSource messageSource;
    private final ExcelUtility excelUtility;
    private final HostelMasterRepository hostelMasterRepository;
    private final HostelRoomInfoRepository hostelRoomInfoRepository;

    public List<RoomOccupancyDto> getCurrentAllocationsByHostelId(RoomOccupancyForm searchForm) {
        Long hostelId = searchForm.getHostelId() != null ?
                searchForm.getHostelId() : null;
        // Add validation if needed
        if (hostelId == null) {
            throw new IllegalArgumentException("Required field not available");
        }
        List<Object[]> results = new ArrayList<>();
        if(StringUtils.equalsIgnoreCase(searchForm.getReportType(),Constants.REPORT_TYPE_ROOM_OCCUPANCY)){
            results = hostelRoomInfoRepository.findCurrentAllocationsByHostelId(hostelId);
            return results.stream()
                    .map(RoomOccupancyDto::new)
                    .collect(Collectors.toList());
        } else {
            results = hostelRoomInfoRepository.findVacantRoomByHostelId(hostelId);
            return results.stream()
                    .map(row-> {
                        RoomOccupancyDto dto = new RoomOccupancyDto();
                        dto.setHostelName( row[1] != null ? String.valueOf(row[1]) : null);
                        dto.setRoomNo( row[3] != null ? String.valueOf(row[3]) : null);
                        return  dto;
                    })
                    .collect(Collectors.toList());
        }
    }


    public Workbook getRoomOccupancyReport(List<RoomOccupancyDto> content, RoomOccupancyForm searchForm) throws Exception {
        Long hostelId = searchForm.getHostelId() != null ?
                searchForm.getHostelId() : null;
        // Add validation if needed
        if (hostelId == null) {
            throw new IllegalArgumentException("Required field not available");
        }

        Optional<HostelMasterEntity> hostelMaster = hostelMasterRepository.findById(hostelId);
        XSSFWorkbook workbook = null;
        int colCount = 0;
        ExcelUtility excelUtility = new ExcelUtility();

        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(messageSource.getMessage("message.hostel.room.occupancy.report.filename", null, Locale.getDefault()));

            // Create styles
            XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
            headerStyle.setWrapText(true);
            XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
            dataStyle.setWrapText(true);

            // Create header rows
            XSSFRow rowheadZero = sheet.createRow(1);
            excelUtility.createCell(rowheadZero, 0, messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));

            XSSFRow rowheadFirst = sheet.createRow(2);
            excelUtility.createCell(rowheadFirst, 0, messageSource.getMessage("message.hostel.room.occupancy.report.filename", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 9));


            XSSFRow rowheadSecond = sheet.createRow(3);
            DateFormat dateFormat = new SimpleDateFormat(Constants.BACKEND_DATETIME_FORMAT_2);
            Date date = new Date();

            String headerString;
            if (hostelMaster.isPresent()){
                headerString = messageSource.getMessage("message.label.hostel.name", null, Locale.getDefault()) + " :" + hostelMaster.get().getHostelName() + "       ";
            }
            headerString = messageSource.getMessage("message.label.report.date", null, Locale.getDefault())+": " + DateUtility.formatDate(date);
            excelUtility.createCell(rowheadSecond, 0, headerString , headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 9));

            // Create column headers in specified order
            XSSFRow rowhead = sheet.createRow(4);

            String[] headers = {
                    messageSource.getMessage("message.label.hostel.name", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.room.number", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.seat", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.allotment.type", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.student.id", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.student.name", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.email.id", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.nature.of.appointment", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.vacating.date", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.phone.number", null, Locale.getDefault())
            };

            // Add headers to the sheet
            for (String header : headers) {
                excelUtility.createCell(rowhead, colCount, header, headerStyle);
                sheet.setColumnWidth(colCount, 8000); // Set initial column width
                colCount++;
            }

            // Populate data rows in specified order
            int rowcount = 4;
            if (CollectionUtils.isNotEmpty(content)) {
                for (RoomOccupancyDto dto : content) {
                    rowcount++;
                    XSSFRow row = sheet.createRow(rowcount);

                    excelUtility.createCell(row, 0, dto.getHostelName(), dataStyle);
                    excelUtility.createCell(row, 1, dto.getRoomNo(), dataStyle);
                    excelUtility.createCell(row, 2, dto.getSubRoomId(), dataStyle); // Assuming sub_room_id represents seat
                    excelUtility.createCell(row, 3, dto.getAllocationType(), dataStyle);
                    excelUtility.createCell(row, 4, dto.getStudentId(), dataStyle);
                    excelUtility.createCell(row, 5, dto.getStudentName(), dataStyle);
                    excelUtility.createCell(row, 6, dto.getEmail(), dataStyle);
                    excelUtility.createCell(row, 7, dto.getNatureOfAppointment(), dataStyle);
                    excelUtility.createCell(row, 8, dto.getVacateDate() != null ? dto.getVacateDate().toString() : "", dataStyle);
                    excelUtility.createCell(row, 9, "", dataStyle); // Phone number not in DTO, left empty
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
            throw new Exception("Error generating room occupancy report", exception);
        }
        return workbook;
    }

    public Workbook getRoomVacancyReport(List<RoomOccupancyDto> content, RoomOccupancyForm searchForm) throws Exception {
        Long hostelId = searchForm.getHostelId() != null ?
                searchForm.getHostelId() : null;
        // Add validation if needed
        if (hostelId == null) {
            throw new IllegalArgumentException("Required field not available");
        }

        Optional<HostelMasterEntity> hostelMaster = hostelMasterRepository.findById(hostelId);
        XSSFWorkbook workbook = null;
        int colCount = 0;
        ExcelUtility excelUtility = new ExcelUtility();

        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(messageSource.getMessage("message.hostel.room.vacancy.report.filename", null, Locale.getDefault()));

            // Create styles
            XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
            headerStyle.setWrapText(true);
            XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
            dataStyle.setWrapText(true);

            // Create header rows
            XSSFRow rowheadZero = sheet.createRow(1);
            excelUtility.createCell(rowheadZero, 0, messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));

            XSSFRow rowheadFirst = sheet.createRow(2);
            excelUtility.createCell(rowheadFirst, 0, messageSource.getMessage("message.hostel.room.vacancy.report.filename", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 9));


            XSSFRow rowheadSecond = sheet.createRow(3);
            DateFormat dateFormat = new SimpleDateFormat(Constants.BACKEND_DATETIME_FORMAT_2);
            Date date = new Date();

            String headerString;
            if (hostelMaster.isPresent()){
                headerString = messageSource.getMessage("message.label.hostel.name", null, Locale.getDefault()) + " :" + hostelMaster.get().getHostelName() + "       ";
            }
            headerString = messageSource.getMessage("message.label.report.date", null, Locale.getDefault())+": " + DateUtility.formatDate(date);
            excelUtility.createCell(rowheadSecond, 0, headerString , headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 9));

            // Create column headers in specified order
            XSSFRow rowhead = sheet.createRow(4);

            String[] headers = {
                    messageSource.getMessage("message.label.hostel.name", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.room.number", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.seat", null, Locale.getDefault()),
            };

            // Add headers to the sheet
            for (String header : headers) {
                excelUtility.createCell(rowhead, colCount, header, headerStyle);
                sheet.setColumnWidth(colCount, 8000); // Set initial column width
                colCount++;
            }

            // Populate data rows in specified order
            int rowcount = 4;
            if (CollectionUtils.isNotEmpty(content)) {
                for (RoomOccupancyDto dto : content) {
                    rowcount++;
                    XSSFRow row = sheet.createRow(rowcount);

                    excelUtility.createCell(row, 0, dto.getHostelName(), dataStyle);
                    excelUtility.createCell(row, 1, dto.getRoomNo(), dataStyle);
                    excelUtility.createCell(row, 2, dto.getSubRoomId(), dataStyle); // Assuming sub_room_id represents seat
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
            throw new Exception("Error generating room occupancy report", exception);
        }
        return workbook;
    }
}
