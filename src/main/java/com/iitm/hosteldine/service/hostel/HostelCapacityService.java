package com.iitm.hosteldine.service.hostel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.dto.hostel.HostelCapacityDto;
import com.iitm.hosteldine.dto.hostel.HostelGuestTariffDto;
import com.iitm.hosteldine.dto.hostel.HostelStudentDistributionDto;
import com.iitm.hosteldine.repository.hostel.HostelMasterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HostelCapacityService {

    private final HostelMasterRepository hostelMasterRepository;

    public List<String> getAcademicYearList() {
        return Arrays.asList("2026-2027", "2025-2026", "2024-2025", "2023-2024", "2022-2023", "2021-2022", "ALL");
    }

    public List<HostelCapacityDto> getHostelCapacityList(String academicYear) {
        int startYear = parseStartYear(academicYear);
        List<Object[]> rawList = hostelMasterRepository.getHostelCapacityListByYear(startYear);
        List<HostelCapacityDto> dtoList = new ArrayList<>();

        for (Object[] row : rawList) {
            Long totalCapacity = getLong(row, 4);
            Long totalUtilized = getLong(row, 5);
            
            Long singleCapacity = getLong(row, 13);
            Long singleUtilized = getLong(row, 14);
            Long singleVacant = Math.max(0L, singleCapacity - singleUtilized);

            Long doubleCapacity = getLong(row, 16);
            Long doubleUtilized = getLong(row, 17);

            Long tripleCapacity = getLong(row, 19);
            Long tripleUtilized = getLong(row, 20);

            Long quadCapacity = getLong(row, 22);
            Long quadUtilized = getLong(row, 23);

            Long dormCapacity = getLong(row, 25);
            Long dormUtilized = getLong(row, 26);

            Long pdCapacity = getLong(row, 28);
            Long pdUtilized = getLong(row, 29);

            Long guestCapacity = getLong(row, 31);
            Long guestUtilized = getLong(row, 32);

            HostelCapacityDto dto = HostelCapacityDto.builder()
                    .hostelId(getLong(row, 0))
                    .hostelName(getString(row, 1))
                    .hostelCode(getString(row, 2))
                    .hostelGenderType(getString(row, 3))
                    .academicYear(academicYear)
                    .totalCapacity(totalCapacity)
                    .totalUtilized(totalUtilized)
                    .totalVacant(Math.max(0L, totalCapacity - totalUtilized))
                    .totalRooms(getLong(row, 6))
                    .fullyOccupiedRooms(getLong(row, 7))
                    .partiallyOccupiedRooms(getLong(row, 8))
                    .fullyVacantRooms(getLong(row, 9))
                    .partiallyOccupiedBeds(getLong(row, 10))
                    .partiallyVacantBeds(getLong(row, 11))
                    .singleRooms(getLong(row, 12))
                    .singleCapacity(singleCapacity)
                    .singleUtilized(singleUtilized)
                    .singleVacant(singleVacant)
                    .doubleRooms(getLong(row, 15))
                    .doubleCapacity(doubleCapacity)
                    .doubleUtilized(doubleUtilized)
                    .doubleVacant(Math.max(0L, doubleCapacity - doubleUtilized))
                    .tripleRooms(getLong(row, 18))
                    .tripleCapacity(tripleCapacity)
                    .tripleUtilized(tripleUtilized)
                    .tripleVacant(Math.max(0L, tripleCapacity - tripleUtilized))
                    .quadRooms(getLong(row, 21))
                    .quadCapacity(quadCapacity)
                    .quadUtilized(quadUtilized)
                    .quadVacant(Math.max(0L, quadCapacity - quadUtilized))
                    .dormRooms(getLong(row, 24))
                    .dormCapacity(dormCapacity)
                    .dormUtilized(dormUtilized)
                    .dormVacant(Math.max(0L, dormCapacity - dormUtilized))
                    .pdRooms(getLong(row, 27))
                    .pdCapacity(pdCapacity)
                    .pdUtilized(pdUtilized)
                    .pdVacant(Math.max(0L, pdCapacity - pdUtilized))
                    .guestRooms(getLong(row, 30))
                    .guestCapacity(guestCapacity)
                    .guestUtilized(guestUtilized)
                    .guestVacant(Math.max(0L, guestCapacity - guestUtilized))
                    .singleFullVac(row.length > 33 && row[33] != null ? getLong(row, 33) : singleVacant)
                    .doubleFullVac(getLong(row, 34))
                    .doublePartOccBeds(getLong(row, 35))
                    .doublePartVacBeds(getLong(row, 36))
                    .tripleFullVac(getLong(row, 37))
                    .triplePartOccBeds(getLong(row, 38))
                    .triplePartVacBeds(getLong(row, 39))
                    .quadFullVac(getLong(row, 40))
                    .quadPartOccBeds(getLong(row, 41))
                    .quadPartVacBeds(getLong(row, 42))
                    .dormFullVac(getLong(row, 43))
                    .dormPartOccBeds(getLong(row, 44))
                    .dormPartVacBeds(getLong(row, 45))
                    .pdFullVac(getLong(row, 46))
                    .pdPartOccBeds(getLong(row, 47))
                    .pdPartVacBeds(getLong(row, 48))
                    .guestFullVac(getLong(row, 49))
                    .guestPartOccBeds(getLong(row, 50))
                    .guestPartVacBeds(getLong(row, 51))
                    .build();

            dtoList.add(dto);
        }

        return dtoList;
    }

    private Long getLong(Object[] row, int index) {
        if (row != null && index >= 0 && index < row.length && row[index] != null) {
            try {
                return ((Number) row[index]).longValue();
            } catch (Exception e) {
                return 0L;
            }
        }
        return 0L;
    }

    private String getString(Object[] row, int index) {
        if (row != null && index >= 0 && index < row.length && row[index] != null) {
            return row[index].toString();
        }
        return "";
    }

    public List<HostelStudentDistributionDto> getHostelStudentDistribution(Long hostelId, String academicYear) {
        int startYear = parseStartYear(academicYear);
        Long targetHostelId = hostelId != null ? hostelId : 0L;
        List<Object[]> rawList = hostelMasterRepository.getHostelStudentDistributionByYear(targetHostelId, startYear);
        List<HostelStudentDistributionDto> dtoList = new ArrayList<>();

        for (Object[] row : rawList) {
            Long hId = row[0] != null ? ((Number) row[0]).longValue() : 0L;
            String hName = row[1] != null ? row[1].toString() : "";
            String cCode = row[2] != null ? row[2].toString() : "";
            String cName = row[3] != null ? row[3].toString() : "";
            String bYear = row[4] != null ? row[4].toString() : "";
            Long count = row[5] != null ? ((Number) row[5]).longValue() : 0L;

            HostelStudentDistributionDto dto = HostelStudentDistributionDto.builder()
                    .hostelId(hId)
                    .hostelName(hName)
                    .courseCode(cCode)
                    .courseName(cName)
                    .batchYear(bYear)
                    .studentCount(count)
                    .build();

            dtoList.add(dto);
        }

        return dtoList;
    }

    public List<HostelCapacityDto> getYearWiseHostelCapacityList(Long hostelId) {
        List<String> academicYears = Arrays.asList("2026-2027", "2025-2026", "2024-2025", "2023-2024", "2022-2023");
        List<HostelCapacityDto> allYearsList = new ArrayList<>();

        for (String yr : academicYears) {
            List<HostelCapacityDto> list = getHostelCapacityList(yr);
            for (HostelCapacityDto dto : list) {
                if (hostelId == null || hostelId == 0L || hostelId.equals(dto.getHostelId())) {
                    dto.setAcademicYear(yr);
                    allYearsList.add(dto);
                }
            }
        }
        return allYearsList;
    }

    public List<HostelGuestTariffDto> getGuestRoomTariffReport(Long hostelId) {
        Long targetHostelId = hostelId != null ? hostelId : 0L;
        List<Object[]> rawList = hostelMasterRepository.getGuestRoomTariffReport(targetHostelId);
        List<HostelGuestTariffDto> dtoList = new ArrayList<>();

        for (Object[] row : rawList) {
            Long hId = row[0] != null ? ((Number) row[0]).longValue() : 0L;
            String hName = row[1] != null ? row[1].toString() : "";
            String hCode = row[2] != null ? row[2].toString() : "";
            Long totalRooms = row[3] != null ? ((Number) row[3]).longValue() : 0L;
            Long totalCap = row[4] != null ? ((Number) row[4]).longValue() : 0L;
            Long todayUtil = row[5] != null ? ((Number) row[5]).longValue() : 0L;
            Long todayVac = row[6] != null ? ((Number) row[6]).longValue() : 0L;
            Integer singleTariff = row[7] != null ? ((Number) row[7]).intValue() : 800;
            Integer sharedTariff = row[8] != null ? ((Number) row[8]).intValue() : 1200;
            Integer baseCharge = row[9] != null ? ((Number) row[9]).intValue() : 250;
            String desc = row[10] != null ? row[10].toString() : "Standard Tariff";

            Long singleRooms = row.length > 11 && row[11] != null ? ((Number) row[11]).longValue() : 0L;
            Long singleVacant = row.length > 12 && row[12] != null ? ((Number) row[12]).longValue() : 0L;
            Long sharedRooms = row.length > 13 && row[13] != null ? ((Number) row[13]).longValue() : 0L;
            Long sharedVacant = row.length > 14 && row[14] != null ? ((Number) row[14]).longValue() : 0L;

            Long vacantCost = (singleVacant * singleTariff) + (sharedVacant * sharedTariff);
            Integer bookingDays = todayUtil > 0 ? 1 : 0;

            HostelGuestTariffDto dto = HostelGuestTariffDto.builder()
                    .hostelId(hId)
                    .hostelName(hName)
                    .hostelCode(hCode)
                    .totalGuestRooms(totalRooms)
                    .totalGuestCapacity(totalCap)
                    .singleGuestRooms(singleRooms)
                    .singleGuestVacant(singleVacant)
                    .sharedGuestRooms(sharedRooms)
                    .sharedGuestVacant(sharedVacant)
                    .utilizedGuestRoomsToday(todayUtil)
                    .vacantGuestRoomsToday(todayVac)
                    .vacantRoomsCost(vacantCost)
                    .bookingDurationDays(bookingDays)
                    .singleRoomTariff(singleTariff)
                    .sharedRoomTariff(sharedTariff)
                    .lodgingBaseCharge(baseCharge)
                    .tariffDescription(desc)
                    .build();

            dtoList.add(dto);
        }

        return dtoList;
    }

    private int parseStartYear(String academicYear) {
        if (academicYear != null && academicYear.contains("-")) {
            try {
                return Integer.parseInt(academicYear.split("-")[0].trim());
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    public byte[] downloadHostelCapacityExcelReport(Long selectedHostelId, String fromDate, String toDate) throws Exception {
        String academicYear = (fromDate != null && fromDate.length() >= 4) ? fromDate.substring(0, 4) + "-" + (Integer.parseInt(fromDate.substring(0, 4)) + 1) : "2026-2027";
        List<HostelCapacityDto> capacityList = getHostelCapacityList(academicYear);
        List<HostelStudentDistributionDto> studentDistList = getHostelStudentDistribution(selectedHostelId, academicYear);
        List<HostelGuestTariffDto> guestTariffList = getGuestRoomTariffReport(selectedHostelId);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            // Shared Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Sheet 1: Capacity Summary
            Sheet sheet1 = workbook.createSheet("1. Capacity Summary");
            Row s1TitleRow = sheet1.createRow(0);
            Cell s1TitleCell = s1TitleRow.createCell(0);
            s1TitleCell.setCellValue("HOSTEL CAPACITY SUMMARY REPORT");
            s1TitleCell.setCellStyle(headerStyle);

            String[] s1Headers = {
                "Hostel Name", "Code", "Single Bed Capacity", "Double Bed Capacity",
                "Triple Bed Capacity", "Quadruple Bed Capacity", "Dormitory Capacity",
                "PD Room Capacity", "Guest Room Capacity", "Total Capacity (Beds)"
            };

            Row s1HeaderRow = sheet1.createRow(2);
            for (int i = 0; i < s1Headers.length; i++) {
                Cell cell = s1HeaderRow.createCell(i);
                cell.setCellValue(s1Headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int s1RowIdx = 3;
            for (HostelCapacityDto h : capacityList) {
                if (selectedHostelId != null && selectedHostelId > 0 && !selectedHostelId.equals(h.getHostelId())) {
                    continue;
                }
                Row row = sheet1.createRow(s1RowIdx++);
                row.createCell(0).setCellValue(h.getHostelName());
                row.createCell(1).setCellValue(h.getHostelCode());
                row.createCell(2).setCellValue(h.getSingleCapacity() != null ? h.getSingleCapacity() : 0);
                row.createCell(3).setCellValue(h.getDoubleCapacity() != null ? h.getDoubleCapacity() : 0);
                row.createCell(4).setCellValue(h.getTripleCapacity() != null ? h.getTripleCapacity() : 0);
                row.createCell(5).setCellValue(h.getQuadCapacity() != null ? h.getQuadCapacity() : 0);
                row.createCell(6).setCellValue(h.getDormCapacity() != null ? h.getDormCapacity() : 0);
                row.createCell(7).setCellValue(h.getPdCapacity() != null ? h.getPdCapacity() : 0);
                row.createCell(8).setCellValue(h.getGuestCapacity() != null ? h.getGuestCapacity() : 0);
                row.createCell(9).setCellValue(h.getTotalCapacity() != null ? h.getTotalCapacity() : 0);
            }
            for (int i = 0; i < s1Headers.length; i++) sheet1.autoSizeColumn(i);

            // Sheet 2: Vacancy Report
            Sheet sheet2 = workbook.createSheet("2. Vacancy Report");
            Row s2TitleRow = sheet2.createRow(0);
            Cell s2TitleCell = s2TitleRow.createCell(0);
            s2TitleCell.setCellValue("HOSTEL VACANCY REPORT");
            s2TitleCell.setCellStyle(headerStyle);

            String[] s2Headers = {
                "Hostel Name", "Code", "Total Vacant Capacity (Beds)", "Fully Vacant Rooms",
                "Partial Occupied Beds", "Partial Vacant Beds", "Vacant Single Beds",
                "Vacant Double Beds", "Vacant Triple Beds", "Vacant Quad Beds",
                "Vacant Dormitory Capacity", "Vacant PD Rooms", "Vacant Guest Rooms"
            };

            Row s2HeaderRow = sheet2.createRow(2);
            for (int i = 0; i < s2Headers.length; i++) {
                Cell cell = s2HeaderRow.createCell(i);
                cell.setCellValue(s2Headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int s2RowIdx = 3;
            for (HostelCapacityDto h : capacityList) {
                if (selectedHostelId != null && selectedHostelId > 0 && !selectedHostelId.equals(h.getHostelId())) {
                    continue;
                }
                Row row = sheet2.createRow(s2RowIdx++);
                row.createCell(0).setCellValue(h.getHostelName());
                row.createCell(1).setCellValue(h.getHostelCode());
                row.createCell(2).setCellValue(h.getTotalVacant() != null ? h.getTotalVacant() : 0);
                row.createCell(3).setCellValue(h.getFullyVacantRooms() != null ? h.getFullyVacantRooms() : 0);
                row.createCell(4).setCellValue(h.getPartiallyOccupiedBeds() != null ? h.getPartiallyOccupiedBeds() : 0);
                row.createCell(5).setCellValue(h.getPartiallyVacantBeds() != null ? h.getPartiallyVacantBeds() : 0);
                row.createCell(6).setCellValue(h.getSingleVacant() != null ? h.getSingleVacant() : 0);
                row.createCell(7).setCellValue(h.getDoubleVacant() != null ? h.getDoubleVacant() : 0);
                row.createCell(8).setCellValue(h.getTripleVacant() != null ? h.getTripleVacant() : 0);
                row.createCell(9).setCellValue(h.getQuadVacant() != null ? h.getQuadVacant() : 0);
                row.createCell(10).setCellValue(h.getDormVacant() != null ? h.getDormVacant() : 0);
                row.createCell(11).setCellValue(h.getPdVacant() != null ? h.getPdVacant() : 0);
                row.createCell(12).setCellValue(h.getGuestVacant() != null ? h.getGuestVacant() : 0);
            }
            for (int i = 0; i < s2Headers.length; i++) sheet2.autoSizeColumn(i);

            // Sheet 3: Utilization & Student Distribution
            Sheet sheet3 = workbook.createSheet("3. Utilization Report");
            Row s3TitleRow = sheet3.createRow(0);
            Cell s3TitleCell = s3TitleRow.createCell(0);
            s3TitleCell.setCellValue("HOSTEL UTILIZATION REPORT");
            s3TitleCell.setCellStyle(headerStyle);

            String[] s3Headers = {
                "Hostel Name", "Code", "Total Utilized Capacity (Beds)", "Fully Occupied Rooms",
                "Partial Occupied Beds", "Partial Vacant Beds", "Utilized Single Beds",
                "Utilized Double Beds", "Utilized Triple Beds", "Utilized Quad Beds",
                "Utilized Dorm Capacity", "Utilized PD Rooms", "Utilized Guest Rooms"
            };

            Row s3HeaderRow = sheet3.createRow(2);
            for (int i = 0; i < s3Headers.length; i++) {
                Cell cell = s3HeaderRow.createCell(i);
                cell.setCellValue(s3Headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int s3RowIdx = 3;
            for (HostelCapacityDto h : capacityList) {
                if (selectedHostelId != null && selectedHostelId > 0 && !selectedHostelId.equals(h.getHostelId())) {
                    continue;
                }
                Row row = sheet3.createRow(s3RowIdx++);
                row.createCell(0).setCellValue(h.getHostelName());
                row.createCell(1).setCellValue(h.getHostelCode());
                row.createCell(2).setCellValue(h.getTotalUtilized() != null ? h.getTotalUtilized() : 0);
                row.createCell(3).setCellValue(h.getFullyOccupiedRooms() != null ? h.getFullyOccupiedRooms() : 0);
                row.createCell(4).setCellValue(h.getPartiallyOccupiedBeds() != null ? h.getPartiallyOccupiedBeds() : 0);
                row.createCell(5).setCellValue(h.getPartiallyVacantBeds() != null ? h.getPartiallyVacantBeds() : 0);
                row.createCell(6).setCellValue(h.getSingleUtilized() != null ? h.getSingleUtilized() : 0);
                row.createCell(7).setCellValue(h.getDoubleUtilized() != null ? h.getDoubleUtilized() : 0);
                row.createCell(8).setCellValue(h.getTripleUtilized() != null ? h.getTripleUtilized() : 0);
                row.createCell(9).setCellValue(h.getQuadUtilized() != null ? h.getQuadUtilized() : 0);
                row.createCell(10).setCellValue(h.getDormUtilized() != null ? h.getDormUtilized() : 0);
                row.createCell(11).setCellValue(h.getPdUtilized() != null ? h.getPdUtilized() : 0);
                row.createCell(12).setCellValue(h.getGuestUtilized() != null ? h.getGuestUtilized() : 0);
            }
            for (int i = 0; i < s3Headers.length; i++) sheet3.autoSizeColumn(i);

            // Sheet 3 Sub-Table: Student Distribution
            s3RowIdx += 2;
            Row distHeaderTitle = sheet3.createRow(s3RowIdx++);
            Cell distTitleCell = distHeaderTitle.createCell(0);
            distTitleCell.setCellValue("HOSTEL-WISE STUDENT DISTRIBUTION ANALYSIS");
            distTitleCell.setCellStyle(headerStyle);

            String[] distHeaders = { "Hostel Name", "Course / Department Name", "Course Code", "Batch Year", "Resident Student Count" };
            Row distHeaderRow = sheet3.createRow(s3RowIdx++);
            for (int i = 0; i < distHeaders.length; i++) {
                Cell cell = distHeaderRow.createCell(i);
                cell.setCellValue(distHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            for (HostelStudentDistributionDto s : studentDistList) {
                Row row = sheet3.createRow(s3RowIdx++);
                row.createCell(0).setCellValue(s.getHostelName());
                row.createCell(1).setCellValue(s.getCourseName());
                row.createCell(2).setCellValue(s.getCourseCode());
                row.createCell(3).setCellValue(s.getBatchYear());
                row.createCell(4).setCellValue(s.getStudentCount() != null ? s.getStudentCount() : 0);
            }

            // Sheet 4: Year-wise Hostel Capacity and Vacancy Report
            Sheet sheet4 = workbook.createSheet("4. Year-wise Capacity & Vacancy");
            Row s4TitleRow = sheet4.createRow(0);
            Cell s4TitleCell = s4TitleRow.createCell(0);
            s4TitleCell.setCellValue("YEAR-WISE HOSTEL CAPACITY AND VACANCY REPORT (" + academicYear + ")");
            s4TitleCell.setCellStyle(headerStyle);

            String[] s4Headers = {
                "Active Academic Year", "Hostel Name", "Code", "Total Capacity", "Utilized Capacity", "Vacant Capacity",
                "Single (Cap/Util/Vac)", "Double (Cap/Util/Vac)", "Triple (Cap/Util/Vac)", "Quad (Cap/Util/Vac)",
                "Dorm (Cap/Util/Vac)", "PD Rooms (Cap/Util/Vac)", "Guest Rooms (Cap/Util/Vac)"
            };

            Row s4HeaderRow = sheet4.createRow(2);
            for (int i = 0; i < s4Headers.length; i++) {
                Cell cell = s4HeaderRow.createCell(i);
                cell.setCellValue(s4Headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int s4RowIdx = 3;
            for (HostelCapacityDto h : capacityList) {
                if (selectedHostelId != null && selectedHostelId > 0 && !selectedHostelId.equals(h.getHostelId())) {
                    continue;
                }
                Row row = sheet4.createRow(s4RowIdx++);
                row.createCell(0).setCellValue(academicYear != null ? academicYear : "2026-2027");
                row.createCell(1).setCellValue(h.getHostelName());
                row.createCell(2).setCellValue(h.getHostelCode());
                row.createCell(3).setCellValue(h.getTotalCapacity() != null ? h.getTotalCapacity() : 0);
                row.createCell(4).setCellValue(h.getTotalUtilized() != null ? h.getTotalUtilized() : 0);
                row.createCell(5).setCellValue(h.getTotalVacant() != null ? h.getTotalVacant() : 0);
                row.createCell(6).setCellValue((h.getSingleCapacity() != null ? h.getSingleCapacity() : 0) + "/" + (h.getSingleUtilized() != null ? h.getSingleUtilized() : 0) + "/" + (h.getSingleVacant() != null ? h.getSingleVacant() : 0));
                row.createCell(7).setCellValue((h.getDoubleCapacity() != null ? h.getDoubleCapacity() : 0) + "/" + (h.getDoubleUtilized() != null ? h.getDoubleUtilized() : 0) + "/" + (h.getDoubleVacant() != null ? h.getDoubleVacant() : 0));
                row.createCell(8).setCellValue((h.getTripleCapacity() != null ? h.getTripleCapacity() : 0) + "/" + (h.getTripleUtilized() != null ? h.getTripleUtilized() : 0) + "/" + (h.getTripleVacant() != null ? h.getTripleVacant() : 0));
                row.createCell(9).setCellValue((h.getQuadCapacity() != null ? h.getQuadCapacity() : 0) + "/" + (h.getQuadUtilized() != null ? h.getQuadUtilized() : 0) + "/" + (h.getQuadVacant() != null ? h.getQuadVacant() : 0));
                row.createCell(10).setCellValue((h.getDormCapacity() != null ? h.getDormCapacity() : 0) + "/" + (h.getDormUtilized() != null ? h.getDormUtilized() : 0) + "/" + (h.getDormVacant() != null ? h.getDormVacant() : 0));
                row.createCell(11).setCellValue((h.getPdCapacity() != null ? h.getPdCapacity() : 0) + "/" + (h.getPdUtilized() != null ? h.getPdUtilized() : 0) + "/" + (h.getPdVacant() != null ? h.getPdVacant() : 0));
                row.createCell(12).setCellValue((h.getGuestCapacity() != null ? h.getGuestCapacity() : 0) + "/" + (h.getGuestUtilized() != null ? h.getGuestUtilized() : 0) + "/" + (h.getGuestVacant() != null ? h.getGuestVacant() : 0));
            }
            for (int i = 0; i < s4Headers.length; i++) sheet4.autoSizeColumn(i);

            // Sheet 5: Guest Room Availability & Tariff Report
            Sheet sheet5 = workbook.createSheet("5. Guest Room Tariff Report");
            Row s5TitleRow = sheet5.createRow(0);
            Cell s5TitleCell = s5TitleRow.createCell(0);
            s5TitleCell.setCellValue("GUEST ROOM AVAILABILITY & TARIFF REPORT (PRESENT DATE)");
            s5TitleCell.setCellStyle(headerStyle);

            String[] s5Headers = {
                "Hostel Name", "Code", "Total Guest Rooms",
                "Utilized Guest Rooms (Today)", "Vacant Guest Rooms (Today)",
                "Cost of Vacant Rooms (₹)", "Booking Duration (Days)",
                "Single Room Tariff (₹/Day)", "Shared Room Tariff (₹/Day)", "Lodging Base Charge (₹)", "Tariff Description"
            };

            Row s5HeaderRow = sheet5.createRow(2);
            for (int i = 0; i < s5Headers.length; i++) {
                Cell cell = s5HeaderRow.createCell(i);
                cell.setCellValue(s5Headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int s5RowIdx = 3;
            for (HostelGuestTariffDto g : guestTariffList) {
                Row row = sheet5.createRow(s5RowIdx++);
                row.createCell(0).setCellValue(g.getHostelName());
                row.createCell(1).setCellValue(g.getHostelCode());
                row.createCell(2).setCellValue(g.getTotalGuestRooms() != null ? g.getTotalGuestRooms() : 0);
                row.createCell(3).setCellValue(g.getUtilizedGuestRoomsToday() != null ? g.getUtilizedGuestRoomsToday() : 0);
                row.createCell(4).setCellValue(g.getVacantGuestRoomsToday() != null ? g.getVacantGuestRoomsToday() : 0);
                row.createCell(5).setCellValue(g.getVacantRoomsCost() != null ? g.getVacantRoomsCost() : 0);
                row.createCell(6).setCellValue(g.getBookingDurationDays() != null ? g.getBookingDurationDays() : 0);
                row.createCell(7).setCellValue(g.getSingleRoomTariff() != null ? g.getSingleRoomTariff() : 0);
                row.createCell(8).setCellValue(g.getSharedRoomTariff() != null ? g.getSharedRoomTariff() : 0);
                row.createCell(9).setCellValue(g.getLodgingBaseCharge() != null ? g.getLodgingBaseCharge() : 0);
                row.createCell(10).setCellValue(g.getTariffDescription() != null ? g.getTariffDescription() : "");
            }
            for (int i = 0; i < s5Headers.length; i++) sheet5.autoSizeColumn(i);

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
