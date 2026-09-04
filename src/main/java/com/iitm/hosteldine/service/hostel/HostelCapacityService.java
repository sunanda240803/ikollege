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
            Long hostelId = row[0] != null ? ((Number) row[0]).longValue() : 0L;
            String hostelName = row[1] != null ? row[1].toString() : "";
            String hostelCode = row[2] != null ? row[2].toString() : "";
            String genderType = row[3] != null ? row[3].toString() : "";
            
            Long totalCapacity = row[4] != null ? ((Number) row[4]).longValue() : 0L;
            Long totalUtilized = row[5] != null ? ((Number) row[5]).longValue() : 0L;
            Long totalVacant = Math.max(0L, totalCapacity - totalUtilized);
            
            Long singleRooms = row[6] != null ? ((Number) row[6]).longValue() : 0L;
            Long singleCapacity = row[7] != null ? ((Number) row[7]).longValue() : 0L;
            Long singleUtilized = row[8] != null ? ((Number) row[8]).longValue() : 0L;
            Long singleVacant = Math.max(0L, singleCapacity - singleUtilized);
            
            Long doubleRooms = row[9] != null ? ((Number) row[9]).longValue() : 0L;
            Long doubleCapacity = row[10] != null ? ((Number) row[10]).longValue() : 0L;
            Long doubleUtilized = row[11] != null ? ((Number) row[11]).longValue() : 0L;
            Long doubleVacant = Math.max(0L, doubleCapacity - doubleUtilized);
            
            Long tripleRooms = row[12] != null ? ((Number) row[12]).longValue() : 0L;
            Long tripleCapacity = row[13] != null ? ((Number) row[13]).longValue() : 0L;
            Long tripleUtilized = row[14] != null ? ((Number) row[14]).longValue() : 0L;
            Long tripleVacant = Math.max(0L, tripleCapacity - tripleUtilized);
            
            Long quadRooms = row[15] != null ? ((Number) row[15]).longValue() : 0L;
            Long quadCapacity = row[16] != null ? ((Number) row[16]).longValue() : 0L;
            Long quadUtilized = row[17] != null ? ((Number) row[17]).longValue() : 0L;
            Long quadVacant = Math.max(0L, quadCapacity - quadUtilized);
            
            Long dormRooms = row[18] != null ? ((Number) row[18]).longValue() : 0L;
            Long dormCapacity = row[19] != null ? ((Number) row[19]).longValue() : 0L;
            Long dormUtilized = row[20] != null ? ((Number) row[20]).longValue() : 0L;
            Long dormVacant = Math.max(0L, dormCapacity - dormUtilized);
            
            Long pdRooms = row[21] != null ? ((Number) row[21]).longValue() : 0L;
            Long pdCapacity = row[22] != null ? ((Number) row[22]).longValue() : 0L;
            Long pdUtilized = row[23] != null ? ((Number) row[23]).longValue() : 0L;
            Long pdVacant = Math.max(0L, pdCapacity - pdUtilized);
            
            Long guestRooms = row[24] != null ? ((Number) row[24]).longValue() : 0L;
            Long guestCapacity = row[25] != null ? ((Number) row[25]).longValue() : 0L;
            Long guestUtilized = row[26] != null ? ((Number) row[26]).longValue() : 0L;
            Long guestVacant = Math.max(0L, guestCapacity - guestUtilized);

            HostelCapacityDto dto = HostelCapacityDto.builder()
                    .hostelId(hostelId)
                    .hostelName(hostelName)
                    .hostelCode(hostelCode)
                    .hostelGenderType(genderType)
                    .totalCapacity(totalCapacity)
                    .totalUtilized(totalUtilized)
                    .totalVacant(totalVacant)
                    .singleRooms(singleRooms)
                    .singleCapacity(singleCapacity)
                    .singleUtilized(singleUtilized)
                    .singleVacant(singleVacant)
                    .doubleRooms(doubleRooms)
                    .doubleCapacity(doubleCapacity)
                    .doubleUtilized(doubleUtilized)
                    .doubleVacant(doubleVacant)
                    .tripleRooms(tripleRooms)
                    .tripleCapacity(tripleCapacity)
                    .tripleUtilized(tripleUtilized)
                    .tripleVacant(tripleVacant)
                    .quadRooms(quadRooms)
                    .quadCapacity(quadCapacity)
                    .quadUtilized(quadUtilized)
                    .quadVacant(quadVacant)
                    .dormRooms(dormRooms)
                    .dormCapacity(dormCapacity)
                    .dormUtilized(dormUtilized)
                    .dormVacant(dormVacant)
                    .pdRooms(pdRooms)
                    .pdCapacity(pdCapacity)
                    .pdUtilized(pdUtilized)
                    .pdVacant(pdVacant)
                    .guestRooms(guestRooms)
                    .guestCapacity(guestCapacity)
                    .guestUtilized(guestUtilized)
                    .guestVacant(guestVacant)
                    .build();

            dtoList.add(dto);
        }

        return dtoList;
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

    public byte[] downloadHostelCapacityExcelReport(Long selectedHostelId, String academicYear) throws Exception {
        List<HostelCapacityDto> capacityList = getHostelCapacityList(academicYear);
        List<HostelStudentDistributionDto> studentDistList = getHostelStudentDistribution(selectedHostelId, academicYear);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Capacity Report");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Title Row
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("HOSTEL CAPACITY & VACANCY REPORT (" + (academicYear != null ? academicYear : "ALL") + ")");
            titleCell.setCellStyle(headerStyle);

            // Column Headers
            String[] headers = {
                "Hostel Name", "Code", "Gender", "Total Cap", "Total Utilized", "Total Vacant",
                "Single Rooms", "Single Cap", "Single Util", "Single Vacant",
                "Double Rooms", "Double Cap", "Double Util", "Double Vacant",
                "Triple Rooms", "Triple Cap", "Triple Util", "Triple Vacant",
                "Quad Rooms", "Quad Cap", "Quad Util", "Quad Vacant",
                "Dorm Rooms", "Dorm Cap", "Dorm Util", "Dorm Vacant",
                "PD Rooms", "PD Cap", "PD Util", "PD Vacant",
                "Guest Rooms", "Guest Cap", "Guest Util", "Guest Vacant"
            };

            Row headerRow = sheet.createRow(2);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 3;
            for (HostelCapacityDto h : capacityList) {
                if (selectedHostelId != null && selectedHostelId > 0 && !selectedHostelId.equals(h.getHostelId())) {
                    continue;
                }

                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(h.getHostelName());
                row.createCell(1).setCellValue(h.getHostelCode());
                row.createCell(2).setCellValue("M".equals(h.getHostelGenderType()) ? "Male" : "F".equals(h.getHostelGenderType()) ? "Female" : "Co-Ed");
                row.createCell(3).setCellValue(h.getTotalCapacity());
                row.createCell(4).setCellValue(h.getTotalUtilized());
                row.createCell(5).setCellValue(h.getTotalVacant());

                row.createCell(6).setCellValue(h.getSingleRooms());
                row.createCell(7).setCellValue(h.getSingleCapacity());
                row.createCell(8).setCellValue(h.getSingleUtilized());
                row.createCell(9).setCellValue(h.getSingleVacant());

                row.createCell(10).setCellValue(h.getDoubleRooms());
                row.createCell(11).setCellValue(h.getDoubleCapacity());
                row.createCell(12).setCellValue(h.getDoubleUtilized());
                row.createCell(13).setCellValue(h.getDoubleVacant());

                row.createCell(14).setCellValue(h.getTripleRooms());
                row.createCell(15).setCellValue(h.getTripleCapacity());
                row.createCell(16).setCellValue(h.getTripleUtilized());
                row.createCell(17).setCellValue(h.getTripleVacant());

                row.createCell(18).setCellValue(h.getQuadRooms());
                row.createCell(19).setCellValue(h.getQuadCapacity());
                row.createCell(20).setCellValue(h.getQuadUtilized());
                row.createCell(21).setCellValue(h.getQuadVacant());

                row.createCell(22).setCellValue(h.getDormRooms());
                row.createCell(23).setCellValue(h.getDormCapacity());
                row.createCell(24).setCellValue(h.getDormUtilized());
                row.createCell(25).setCellValue(h.getDormVacant());

                row.createCell(26).setCellValue(h.getPdRooms());
                row.createCell(27).setCellValue(h.getPdCapacity());
                row.createCell(28).setCellValue(h.getPdUtilized());
                row.createCell(29).setCellValue(h.getPdVacant());

                row.createCell(30).setCellValue(h.getGuestRooms());
                row.createCell(31).setCellValue(h.getGuestCapacity());
                row.createCell(32).setCellValue(h.getGuestUtilized());
                row.createCell(33).setCellValue(h.getGuestVacant());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Sheet 2: Student Distribution by Course & Batch Year
            Sheet sheet2 = workbook.createSheet("Student Distribution");
            Row s2TitleRow = sheet2.createRow(0);
            Cell s2TitleCell = s2TitleRow.createCell(0);
            s2TitleCell.setCellValue("STUDENT DISTRIBUTION BY COURSE & BATCH YEAR (" + (academicYear != null ? academicYear : "ALL") + ")");
            s2TitleCell.setCellStyle(headerStyle);

            String[] s2Headers = {"Hostel Name", "Course / Department", "Course Code", "Batch Year", "Resident Student Count"};
            Row s2HeaderRow = sheet2.createRow(2);
            for (int i = 0; i < s2Headers.length; i++) {
                Cell cell = s2HeaderRow.createCell(i);
                cell.setCellValue(s2Headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int s2RowIdx = 3;
            for (HostelStudentDistributionDto s : studentDistList) {
                Row row = sheet2.createRow(s2RowIdx++);
                row.createCell(0).setCellValue(s.getHostelName());
                row.createCell(1).setCellValue(s.getCourseName());
                row.createCell(2).setCellValue(s.getCourseCode());
                row.createCell(3).setCellValue(s.getBatchYear());
                row.createCell(4).setCellValue(s.getStudentCount());
            }

            for (int i = 0; i < s2Headers.length; i++) {
                sheet2.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
