package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.*;
import com.iitm.hosteldine.form.common.ShowWiseReportForm;
import com.iitm.hosteldine.model.hostel.ShowEventMasterEntity;
import com.iitm.hosteldine.model.hostel.ShowMasterEntity;
import com.iitm.hosteldine.model.hostel.ShowSeatDetailsEntity;
import com.iitm.hosteldine.repository.hostel.ShowEventMasterRepository;
import com.iitm.hosteldine.repository.hostel.ShowMasterRepository;
import com.iitm.hosteldine.repository.hostel.ShowSeatDetailsRepository;
import com.iitm.hosteldine.repository.student.ShowStudentDetailRepository;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.Utility;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowWiseReportService {

    private final ShowStudentDetailRepository showStudentDetailRepository;
    private final MessageSource messageSource;
    private final ShowMasterRepository showMasterRepository;
    private final ShowEventMasterRepository showEventMasterRepository;
    private final ShowSeatDetailsRepository showSeatDetailsRepository;
    private final Utility utility;

    public List<EventDTO> getActiveEvents() {
        List<ShowEventMasterEntity> results = showEventMasterRepository.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE);
        return results.stream()
                .map(result -> new EventDTO(
                        result.getId(),
                        result.getEventName()))
                .collect(Collectors.toList());
    }

    public List<ShowDTO> getActiveShowsByEvent(Long eventId) {
        Optional<ShowEventMasterEntity> eventMaster = showEventMasterRepository.findById(eventId);
        List<ShowMasterEntity> results = showMasterRepository.findByShowEventMasterAndActiveFlag(eventMaster.get(), ModelConstants.STATUS_ACTIVE);
        return results.stream()
                .map(result -> new ShowDTO(
                        result.getId(),
                        result.getShowName()))
                .collect(Collectors.toList());
    }

    public List<SeatDTO> getActiveSeatsByShow(Long showId) {
        Optional<ShowMasterEntity> showMasterEntity = showMasterRepository.findById(showId);
        List<ShowSeatDetailsEntity> results = showSeatDetailsRepository.findByShowAndActiveFlag(showMasterEntity.get(), ModelConstants.STATUS_ACTIVE);
        return results.stream()
                .map(result -> new SeatDTO(
                        result.getId(),
                        result.getSeatName()))
                .collect(Collectors.toList());
    }

    public List<ClaimDTO> getClaimsByEventId(Long eventId) {
        List<Object[]> results = showStudentDetailRepository.findClaimsByEventId(eventId);

        return results.stream()
                .map(result -> new ClaimDTO(
                        ((Number) result[0]).longValue(),  // claimAmount
                        (String) result[1],                 // studentId
                        ((Number) result[2]).longValue()     // eventId
                ))
                .collect(Collectors.toList());
    }

    public List<ShowWiseReportDto> getPurchaseData( ShowWiseReportForm searchForm) {

        String eventId = searchForm.getEventId() != null ?
                searchForm.getEventId().toString() : null;
        LocalDate fromDate = searchForm.getFromDate() != null ?
                searchForm.getFromDate() : null;
        LocalDate toDate = searchForm.getToDate() != null ?
                searchForm.getToDate() : null;


        // Add validation if needed
        if (fromDate == null || toDate == null || eventId == null) {
            throw new IllegalArgumentException("Required field not available");
        }

        List<Object[]> results = showStudentDetailRepository.findShowPurchases(
                searchForm.getEventId(),
                searchForm.getShowId(),
                searchForm.getSeatId(),
                searchForm.getFromDate(),
                searchForm.getToDate(),
                ModelConstants.STATUS_ACTIVE
        );


        List<ClaimDTO> claims = getClaimsByEventId(searchForm.getEventId());

        Map<String, Long> claimAmounts  = claims.stream().collect(Collectors.toMap(ClaimDTO::getStudentId, ClaimDTO::getClaimAmount));

        return results.stream()
                .map(result -> {
                    ShowWiseReportDto dto = mapToDTO(result);
                    Long claimAmount = claimAmounts.isEmpty() ? 0L :
                            claimAmounts.getOrDefault((String) result[1], 0L);
                    dto.setClaimAmount(claimAmount);
                    dto.setBalanceAmount(dto.getTotalPurchase() - claimAmount);
                    return dto;
                })
                .collect(Collectors.toList());

    }

    private ShowWiseReportDto mapToDTO(Object[] result) {
        try {
            return new ShowWiseReportDto(
                    result[0] != null ? ((Number) result[0]).intValue() : 0,          // purchasedCount
                    result[1] != null ? (String) result[1] : null,                       // studentId
                    result[2] != null ? (String) result[2] : null,                        // studentName
                    result[3] != null ? (String) result[3] : null,                        // hostelName
                    result[4] != null ? result[4].toString() : null,                    // roomNo
                    result[5] != null ? (String) result[5] : null,                        // seatName
                    result[6] != null ? ((Number) result[6]).longValue() : 0L,          // totalPurchase
                    result[7] != null ? (String) result[7] : "", // createdAt
                    result[8] != null ? (String) result[8] : null,                        // showName
                    result[9] != null ? (String) result[9] : null                          // purchasedStudentName
            );
        } catch (Exception e) {
            throw new RuntimeException("Error mapping result to DTO", e);
        }
    }

    public Workbook getShowWiseReport(List<ShowWiseReportDto> content,ShowWiseReportForm searchForm) throws Exception {
        XSSFWorkbook workbook = null;
        int colCount = 0;
        ExcelUtility excelUtility = new ExcelUtility();

        String eventId = searchForm.getEventId() != null ? searchForm.getEventId().toString() : null;
        LocalDate fromDate = searchForm.getFromDate() != null ? searchForm.getFromDate() : null;
        LocalDate toDate = searchForm.getToDate() != null ? searchForm.getToDate() : null;

        Optional<ShowEventMasterEntity> eventMaster = eventId != null ? showEventMasterRepository.findById(Long.valueOf(eventId)) : Optional.empty();

        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(messageSource.getMessage("message.hostel.show.wise.report.filename", null, Locale.getDefault()));

            // Create styles
            XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
            headerStyle.setWrapText(true);
            XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
            dataStyle.setWrapText(true);

            // Create header rows
            XSSFRow rowheadZero = sheet.createRow(1);
            excelUtility.createCell(rowheadZero, 0, messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 16));

            XSSFRow rowheadFirst = sheet.createRow(2);
            excelUtility.createCell(rowheadFirst, 0, messageSource.getMessage("message.hostel.show.wise.report.filename", null, Locale.getDefault()), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 16));

            XSSFRow rowheadSecond = sheet.createRow(3);
            String headerString = messageSource.getMessage("message.label.event.name", null, Locale.getDefault()) + " :" + eventMaster.get().getEventName() + "   "
                    + messageSource.getMessage("message.label.from.date", null, Locale.getDefault()) + " : " + DateUtility.formatDate(fromDate) + "  -  "
                    + messageSource.getMessage("message.label.to.date", null, Locale.getDefault()) + " : " + DateUtility.formatDate(toDate);

            excelUtility.createCell(rowheadSecond, 0, headerString , headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 16));

            // Create column headers in specified order
            XSSFRow rowhead = sheet.createRow(5);
            
            String[] headers = {
                    messageSource.getMessage("message.label.sl.no", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.student.id", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.student.name", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.hostel.name", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.room.no", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.total.purchase", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.claim.amount", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.balance.amount", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.booked.date.time", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.show.name", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.seat.name", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.printing.name", null, Locale.getDefault()),
                    messageSource.getMessage("message.label.purchased.count", null, Locale.getDefault())
            };

            // Add headers to the sheet
            for (String header : headers) {
                excelUtility.createCell(rowhead, colCount, header, headerStyle);
                sheet.setColumnWidth(colCount, 4000); // Set initial column width
                colCount++;
            }

            // Populate data rows in specified order
            int rowcount = 5;
            int slno = 1;
            if (CollectionUtils.isNotEmpty(content)) {

                for (ShowWiseReportDto dto : content) {
                    rowcount++;
                    XSSFRow row = sheet.createRow(rowcount);

                    excelUtility.createCell(row, 0, slno, dataStyle);
                    excelUtility.createCell(row, 1, dto.getStudentId(), dataStyle);
                    excelUtility.createCell(row, 2, dto.getStudentName(), dataStyle);
                    excelUtility.createCell(row, 3, dto.getHostelName(), dataStyle);
                    excelUtility.createCell(row, 4, dto.getRoomNo(), dataStyle);
                    excelUtility.createCell(row, 5, dto.getTotalPurchase(), dataStyle);
                    excelUtility.createCell(row, 6, dto.getClaimAmount() != null ? dto.getClaimAmount() : 0, dataStyle);
                    excelUtility.createCell(row, 7, dto.getBalanceAmount() != null ? dto.getBalanceAmount() : dto.getTotalPurchase(), dataStyle);
                    excelUtility.createCell(row, 8, dto.getCreatedAt() != null ? utility.convertDateStr(dto.getCreatedAt(), Constants.FRONTEND_DATE_TIME_FORMAT_2, Constants.FRONTEND_DATE_TIME_FORMAT) : "", dataStyle);
                    excelUtility.createCell(row, 9, dto.getShowName(), dataStyle);
                    excelUtility.createCell(row, 10, dto.getSeatName(), dataStyle);
                    excelUtility.createCell(row, 11, dto.getPrintingName(), dataStyle);
                    excelUtility.createCell(row, 12, dto.getPurchasedCount(), dataStyle);

                    slno++;
                }
            }

            // Auto-size columns with maximum width limit
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                if (sheet.getColumnWidth(i) > 8000) {
                    sheet.setColumnWidth(i, 8000);
                } else if (sheet.getColumnWidth(i) < 3000) {
                    sheet.setColumnWidth(i, 3000); // Minimum width
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Error generating show wise report", exception);
        }
        return workbook;
    }
}
