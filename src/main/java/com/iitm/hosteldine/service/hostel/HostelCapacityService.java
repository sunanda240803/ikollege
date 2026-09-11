package com.iitm.hosteldine.service.hostel;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
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
import com.iitm.hosteldine.model.warden.GuestAccommodationChargesEntity;
import com.iitm.hosteldine.repository.hostel.HostelMasterRepository;
import com.iitm.hosteldine.repository.warden.GuestAccommodationChargesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HostelCapacityService {

    private final HostelMasterRepository hostelMasterRepository;
    private final GuestAccommodationChargesRepository guestAccommodationChargesRepository;

    public List<HostelCapacityDto> getHostelCapacityList(String fromDate, String toDate) {
        List<Object[]> rawList = hostelMasterRepository.getHostelCapacityListByDateRange(fromDate, toDate);
        List<HostelCapacityDto> dtoList = new ArrayList<>();

        for (Object[] row : rawList) {
            Long hostelId = row[0] != null ? ((Number) row[0]).longValue() : 0L;
            String hostelName = row[1] != null ? row[1].toString() : "";
            String hostelCode = row[2] != null ? row[2].toString() : "";
            String genderType = row[3] != null ? row[3].toString() : "";
            
            Long singleRooms = row[6] != null ? ((Number) row[6]).longValue() : 0L;
            Long singleCapacity = row[7] != null ? ((Number) row[7]).longValue() : 0L;
            Long singleUtilized = row[8] != null ? ((Number) row[8]).longValue() : 0L;
            Long singleVacantRooms = row[9] != null ? ((Number) row[9]).longValue() : 0L;
            Long singleUtilizedRooms = row[10] != null ? ((Number) row[10]).longValue() : 0L;
            Long singlePartVacRooms = row[11] != null ? ((Number) row[11]).longValue() : 0L;
            Long singlePartVacBeds = row[12] != null ? ((Number) row[12]).longValue() : 0L;
            Long singleOverRooms = row[13] != null ? ((Number) row[13]).longValue() : 0L;
            Long singleOverSeats = row[14] != null ? ((Number) row[14]).longValue() : 0L;
            Long singleVacant = (singleVacantRooms * 1) + singlePartVacBeds;
            
            Long doubleRooms = row[15] != null ? ((Number) row[15]).longValue() : 0L;
            Long doubleCapacity = row[16] != null ? ((Number) row[16]).longValue() : 0L;
            Long doubleUtilized = row[17] != null ? ((Number) row[17]).longValue() : 0L;
            Long doubleVacantRooms = row[18] != null ? ((Number) row[18]).longValue() : 0L;
            Long doubleUtilizedRooms = row[19] != null ? ((Number) row[19]).longValue() : 0L;
            Long doublePartVacRooms = row[20] != null ? ((Number) row[20]).longValue() : 0L;
            Long doublePartVacBeds = row[21] != null ? ((Number) row[21]).longValue() : 0L;
            Long doubleOverRooms = row[22] != null ? ((Number) row[22]).longValue() : 0L;
            Long doubleOverSeats = row[23] != null ? ((Number) row[23]).longValue() : 0L;
            Long doubleVacant = (doubleVacantRooms * 2) + doublePartVacBeds;
            
            Long tripleRooms = row[24] != null ? ((Number) row[24]).longValue() : 0L;
            Long tripleCapacity = row[25] != null ? ((Number) row[25]).longValue() : 0L;
            Long tripleUtilized = row[26] != null ? ((Number) row[26]).longValue() : 0L;
            Long tripleVacantRooms = row[27] != null ? ((Number) row[27]).longValue() : 0L;
            Long tripleUtilizedRooms = row[28] != null ? ((Number) row[28]).longValue() : 0L;
            Long triplePartVacRooms = row[29] != null ? ((Number) row[29]).longValue() : 0L;
            Long triplePartVacBeds = row[30] != null ? ((Number) row[30]).longValue() : 0L;
            Long tripleOverRooms = row[31] != null ? ((Number) row[31]).longValue() : 0L;
            Long tripleOverSeats = row[32] != null ? ((Number) row[32]).longValue() : 0L;
            Long tripleVacant = (tripleVacantRooms * 3) + triplePartVacBeds;
            
            Long quadRooms = row[33] != null ? ((Number) row[33]).longValue() : 0L;
            Long quadCapacity = row[34] != null ? ((Number) row[34]).longValue() : 0L;
            Long quadUtilized = row[35] != null ? ((Number) row[35]).longValue() : 0L;
            Long quadVacantRooms = row[36] != null ? ((Number) row[36]).longValue() : 0L;
            Long quadUtilizedRooms = row[37] != null ? ((Number) row[37]).longValue() : 0L;
            Long quadPartVacRooms = row[38] != null ? ((Number) row[38]).longValue() : 0L;
            Long quadPartVacBeds = row[39] != null ? ((Number) row[39]).longValue() : 0L;
            Long quadOverRooms = row[40] != null ? ((Number) row[40]).longValue() : 0L;
            Long quadOverSeats = row[41] != null ? ((Number) row[41]).longValue() : 0L;
            Long quadVacant = (quadVacantRooms * 4) + quadPartVacBeds;
            
            Long dormRooms = row[42] != null ? ((Number) row[42]).longValue() : 0L;
            Long dormCapacity = row[43] != null ? ((Number) row[43]).longValue() : 0L;
            Long dormUtilized = row[44] != null ? ((Number) row[44]).longValue() : 0L;
            Long dormVacantRooms = row[45] != null ? ((Number) row[45]).longValue() : 0L;
            Long dormUtilizedRooms = row[46] != null ? ((Number) row[46]).longValue() : 0L;
            Long dormPartVacRooms = row[47] != null ? ((Number) row[47]).longValue() : 0L;
            Long dormPartVacBeds = row[48] != null ? ((Number) row[48]).longValue() : 0L;
            Long dormOverRooms = row[49] != null ? ((Number) row[49]).longValue() : 0L;
            Long dormOverSeats = row[50] != null ? ((Number) row[50]).longValue() : 0L;
            Long dormVacant = (dormVacantRooms * (dormRooms > 0 ? (dormCapacity / dormRooms) : 10)) + dormPartVacBeds;
            
            Long pdRooms = row[51] != null ? ((Number) row[51]).longValue() : 0L;
            Long pdCapacity = row[52] != null ? ((Number) row[52]).longValue() : 0L;
            Long pdUtilized = row[53] != null ? ((Number) row[53]).longValue() : 0L;
            Long pdVacantRooms = row[54] != null ? ((Number) row[54]).longValue() : 0L;
            Long pdUtilizedRooms = row[55] != null ? ((Number) row[55]).longValue() : 0L;
            Long pdPartVacRooms = row[56] != null ? ((Number) row[56]).longValue() : 0L;
            Long pdPartVacBeds = row[57] != null ? ((Number) row[57]).longValue() : 0L;
            Long pdOverRooms = row[58] != null ? ((Number) row[58]).longValue() : 0L;
            Long pdOverSeats = row[59] != null ? ((Number) row[59]).longValue() : 0L;
            Long pdVacant = (pdVacantRooms * 1) + pdPartVacBeds;
            
            Long guestRooms = row[60] != null ? ((Number) row[60]).longValue() : 0L;
            Long guestCapacity = row[61] != null ? ((Number) row[61]).longValue() : 0L;
            Long guestUtilized = row[62] != null ? ((Number) row[62]).longValue() : 0L;
            Long guestVacantRooms = row[63] != null ? ((Number) row[63]).longValue() : 0L;
            Long guestUtilizedRooms = row[64] != null ? ((Number) row[64]).longValue() : 0L;
            Long guestPartVacRooms = row[65] != null ? ((Number) row[65]).longValue() : 0L;
            Long guestPartVacBeds = row[66] != null ? ((Number) row[66]).longValue() : 0L;
            Long guestOverRooms = row[67] != null ? ((Number) row[67]).longValue() : 0L;
            Long guestOverSeats = row[68] != null ? ((Number) row[68]).longValue() : 0L;
            Long guestVacant = (guestVacantRooms * 1) + guestPartVacBeds;

            Long icsrRooms = row[69] != null ? ((Number) row[69]).longValue() : 0L;
            Long icsrCapacity = row[70] != null ? ((Number) row[70]).longValue() : 0L;
            Long icsrUtilized = row[71] != null ? ((Number) row[71]).longValue() : 0L;
            Long icsrVacantRooms = row[72] != null ? ((Number) row[72]).longValue() : 0L;
            Long icsrUtilizedRooms = row[73] != null ? ((Number) row[73]).longValue() : 0L;
            Long icsrPartVacRooms = row[74] != null ? ((Number) row[74]).longValue() : 0L;
            Long icsrPartVacBeds = row[75] != null ? ((Number) row[75]).longValue() : 0L;
            Long icsrOverRooms = row[76] != null ? ((Number) row[76]).longValue() : 0L;
            Long icsrOverSeats = row[77] != null ? ((Number) row[77]).longValue() : 0L;
            Long icsrVacant = (icsrVacantRooms * 1) + icsrPartVacBeds;

            Long officialRooms = row[78] != null ? ((Number) row[78]).longValue() : 0L;
            Long officialCapacity = row[79] != null ? ((Number) row[79]).longValue() : 0L;
            Long officialUtilized = row[80] != null ? ((Number) row[80]).longValue() : 0L;
            Long officialVacantRooms = row[81] != null ? ((Number) row[81]).longValue() : 0L;
            Long officialUtilizedRooms = row[82] != null ? ((Number) row[82]).longValue() : 0L;
            Long officialPartVacRooms = row[83] != null ? ((Number) row[83]).longValue() : 0L;
            Long officialPartVacBeds = row[84] != null ? ((Number) row[84]).longValue() : 0L;
            Long officialOverRooms = row[85] != null ? ((Number) row[85]).longValue() : 0L;
            Long officialOverSeats = row[86] != null ? ((Number) row[86]).longValue() : 0L;
            Long officialVacant = (officialVacantRooms * 1) + officialPartVacBeds;

            Long totalCapacity = row[4] != null ? ((Number) row[4]).longValue() : 0L;
            Long totalUtilized = row[5] != null ? ((Number) row[5]).longValue() : 0L;
            Long totalVacant = singleVacant + doubleVacant + tripleVacant + quadVacant + dormVacant + pdVacant + guestVacant + icsrVacant + officialVacant;

            Long partiallyVacantRooms = row.length > 87 && row[87] != null ? ((Number) row[87]).longValue() : 0L;
            Long partiallyVacantBeds = row.length > 88 && row[88] != null ? ((Number) row[88]).longValue() : 0L;
            Long partiallyUtilizedRooms = partiallyVacantRooms;

            Long overloadedRooms = row.length > 89 && row[89] != null ? ((Number) row[89]).longValue() : 0L;
            Long overloadedSeats = row.length > 90 && row[90] != null ? ((Number) row[90]).longValue() : 0L;

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
                    .singleVacantRooms(singleVacantRooms)
                    .singleUtilizedRooms(singleUtilizedRooms)
                    .singlePartVacRooms(singlePartVacRooms)
                    .singlePartVacBeds(singlePartVacBeds)
                    .singleOverRooms(singleOverRooms)
                    .singleOverSeats(singleOverSeats)
                    .doubleRooms(doubleRooms)
                    .doubleCapacity(doubleCapacity)
                    .doubleUtilized(doubleUtilized)
                    .doubleVacant(doubleVacant)
                    .doubleVacantRooms(doubleVacantRooms)
                    .doubleUtilizedRooms(doubleUtilizedRooms)
                    .doublePartVacRooms(doublePartVacRooms)
                    .doublePartVacBeds(doublePartVacBeds)
                    .doubleOverRooms(doubleOverRooms)
                    .doubleOverSeats(doubleOverSeats)
                    .tripleRooms(tripleRooms)
                    .tripleCapacity(tripleCapacity)
                    .tripleUtilized(tripleUtilized)
                    .tripleVacant(tripleVacant)
                    .tripleVacantRooms(tripleVacantRooms)
                    .tripleUtilizedRooms(tripleUtilizedRooms)
                    .triplePartVacRooms(triplePartVacRooms)
                    .triplePartVacBeds(triplePartVacBeds)
                    .tripleOverRooms(tripleOverRooms)
                    .tripleOverSeats(tripleOverSeats)
                    .quadRooms(quadRooms)
                    .quadCapacity(quadCapacity)
                    .quadUtilized(quadUtilized)
                    .quadVacant(quadVacant)
                    .quadVacantRooms(quadVacantRooms)
                    .quadUtilizedRooms(quadUtilizedRooms)
                    .quadPartVacRooms(quadPartVacRooms)
                    .quadPartVacBeds(quadPartVacBeds)
                    .quadOverRooms(quadOverRooms)
                    .quadOverSeats(quadOverSeats)
                    .dormRooms(dormRooms)
                    .dormCapacity(dormCapacity)
                    .dormUtilized(dormUtilized)
                    .dormVacant(dormVacant)
                    .dormVacantRooms(dormVacantRooms)
                    .dormUtilizedRooms(dormUtilizedRooms)
                    .dormPartVacRooms(dormPartVacRooms)
                    .dormPartVacBeds(dormPartVacBeds)
                    .dormOverRooms(dormOverRooms)
                    .dormOverSeats(dormOverSeats)
                    .pdRooms(pdRooms)
                    .pdCapacity(pdCapacity)
                    .pdUtilized(pdUtilized)
                    .pdVacant(pdVacant)
                    .pdVacantRooms(pdVacantRooms)
                    .pdUtilizedRooms(pdUtilizedRooms)
                    .pdPartVacRooms(pdPartVacRooms)
                    .pdPartVacBeds(pdPartVacBeds)
                    .pdOverRooms(pdOverRooms)
                    .pdOverSeats(pdOverSeats)
                    .guestRooms(guestRooms)
                    .guestCapacity(guestCapacity)
                    .guestUtilized(guestUtilized)
                    .guestVacant(guestVacant)
                    .guestVacantRooms(guestVacantRooms)
                    .guestUtilizedRooms(guestUtilizedRooms)
                    .guestPartVacRooms(guestPartVacRooms)
                    .guestPartVacBeds(guestPartVacBeds)
                    .guestOverRooms(guestOverRooms)
                    .guestOverSeats(guestOverSeats)
                    .icsrRooms(icsrRooms)
                    .icsrCapacity(icsrCapacity)
                    .icsrUtilized(icsrUtilized)
                    .icsrVacant(icsrVacant)
                    .icsrVacantRooms(icsrVacantRooms)
                    .icsrUtilizedRooms(icsrUtilizedRooms)
                    .icsrPartVacRooms(icsrPartVacRooms)
                    .icsrPartVacBeds(icsrPartVacBeds)
                    .icsrOverRooms(icsrOverRooms)
                    .icsrOverSeats(icsrOverSeats)
                    .officialRooms(officialRooms)
                    .officialCapacity(officialCapacity)
                    .officialUtilized(officialUtilized)
                    .officialVacant(officialVacant)
                    .officialVacantRooms(officialVacantRooms)
                    .officialUtilizedRooms(officialUtilizedRooms)
                    .officialPartVacRooms(officialPartVacRooms)
                    .officialPartVacBeds(officialPartVacBeds)
                    .officialOverRooms(officialOverRooms)
                    .officialOverSeats(officialOverSeats)
                    .partiallyVacantRooms(partiallyVacantRooms)
                    .partiallyVacantBeds(partiallyVacantBeds)
                    .partiallyUtilizedRooms(partiallyUtilizedRooms)
                    .overloadedRooms(overloadedRooms)
                    .overloadedSeats(overloadedSeats)
                    .build();

            dtoList.add(dto);
        }

        return dtoList;
    }

    public List<HostelStudentDistributionDto> getHostelStudentDistribution(Long hostelId, String fromDate, String toDate) {
        Long targetHostelId = hostelId != null ? hostelId : 0L;
        List<Object[]> rawList = hostelMasterRepository.getHostelStudentDistributionByDateRange(targetHostelId, fromDate, toDate);
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

    public HostelGuestTariffDto getLiveGuestRoomTariffDetails(Long selectedHostelId) {
        Long targetHostelId = selectedHostelId != null ? selectedHostelId : 0L;
        List<Object[]> rawList = hostelMasterRepository.getLiveGuestRoomDetailsList(targetHostelId);

        GuestAccommodationChargesEntity charges = guestAccommodationChargesRepository.getAccommodationCharges("Y").orElse(null);
        Integer singleTariff = charges != null && charges.getIndividualRoomAmount() != null ? charges.getIndividualRoomAmount() : 750;
        Integer sharedTariff = charges != null && charges.getIndividualRoomMultipleAmount() != null ? charges.getIndividualRoomMultipleAmount() : 500;
        Integer baseCharge = charges != null && charges.getAmount() != null ? charges.getAmount() : 200;
        String desc = charges != null && charges.getDescription() != null ? charges.getDescription() : "Standard Guest Room Tariff Rules";

        List<HostelGuestTariffDto.GuestRoomDetail> roomDetailList = new ArrayList<>();
        long totalGuestRooms = 0;
        long totalGuestCap = 0;
        long utilizedToday = 0;
        long vacantToday = 0;

        java.util.Map<String, HostelGuestTariffDto.GuestTypeSummary> summaryMap = new java.util.LinkedHashMap<>();
        summaryMap.put("guest", HostelGuestTariffDto.GuestTypeSummary.builder().guestType("guest").displayName("Guest Rooms").totalRooms(0L).totalCapacity(0L).occupiedRooms(0L).occupiedSeats(0L).vacantRooms(0L).vacantSeats(0L).build());
        summaryMap.put("icsr", HostelGuestTariffDto.GuestTypeSummary.builder().guestType("icsr").displayName("ICSR Rooms").totalRooms(0L).totalCapacity(0L).occupiedRooms(0L).occupiedSeats(0L).vacantRooms(0L).vacantSeats(0L).build());
        summaryMap.put("official", HostelGuestTariffDto.GuestTypeSummary.builder().guestType("official").displayName("Official Rooms").totalRooms(0L).totalCapacity(0L).occupiedRooms(0L).occupiedSeats(0L).vacantRooms(0L).vacantSeats(0L).build());

        for (Object[] row : rawList) {
            String hName = row[1] != null ? row[1].toString() : "";
            String floorName = row[2] != null ? row[2].toString() : "";
            String roomNo = row[3] != null ? row[3].toString() : "";
            Integer cap = row[4] != null ? ((Number) row[4]).intValue() : 1;
            String guestTypeRaw = row[5] != null ? row[5].toString().toLowerCase() : "guest";
            String status = row[6] != null ? row[6].toString() : "Vacant";
            String occupantInfo = row[7] != null ? row[7].toString() : "";

            totalGuestRooms++;
            totalGuestCap += cap;

            boolean isOccupied = "Occupied".equalsIgnoreCase(status);
            if (isOccupied) {
                utilizedToday++;
            } else {
                vacantToday++;
            }

            Integer ratePerDay = (cap == 1) ? singleTariff : sharedTariff;

            roomDetailList.add(HostelGuestTariffDto.GuestRoomDetail.builder()
                    .roomNo(roomNo + " (" + hName + ")")
                    .floorName(floorName)
                    .capacity(cap)
                    .currentStatus(status)
                    .occupantType(isOccupied ? occupantInfo : "Available")
                    .tariffPerDay(ratePerDay)
                    .build());

            HostelGuestTariffDto.GuestTypeSummary typeSummary = summaryMap.computeIfAbsent(guestTypeRaw, k -> {
                String dName = "guest".equals(k) ? "Guest Rooms" : "icsr".equals(k) ? "ICSR Rooms" : "official".equals(k) ? "Official Rooms" : (k.toUpperCase() + " Rooms");
                return HostelGuestTariffDto.GuestTypeSummary.builder()
                        .guestType(k)
                        .displayName(dName)
                        .totalRooms(0L).totalCapacity(0L).occupiedRooms(0L).occupiedSeats(0L).vacantRooms(0L).vacantSeats(0L)
                        .build();
            });

            typeSummary.setTotalRooms(typeSummary.getTotalRooms() + 1);
            typeSummary.setTotalCapacity(typeSummary.getTotalCapacity() + cap);
            if (isOccupied) {
                typeSummary.setOccupiedRooms(typeSummary.getOccupiedRooms() + 1);
                typeSummary.setOccupiedSeats(typeSummary.getOccupiedSeats() + cap);
            } else {
                typeSummary.setVacantRooms(typeSummary.getVacantRooms() + 1);
                typeSummary.setVacantSeats(typeSummary.getVacantSeats() + cap);
            }
        }

        List<HostelGuestTariffDto.GuestTypeSummary> typeSummaries = new ArrayList<>(summaryMap.values());

        return HostelGuestTariffDto.builder()
                .hostelId(targetHostelId)
                .hostelName(targetHostelId == 0L ? "All Hostels (Consolidated Live View)" : "Selected Hostel")
                .totalGuestRooms(totalGuestRooms)
                .totalGuestCapacity(totalGuestCap)
                .utilizedGuestRoomsToday(utilizedToday)
                .vacantGuestRoomsToday(vacantToday)
                .singleRoomTariff(singleTariff)
                .sharedRoomTariff(sharedTariff)
                .lodgingBaseCharge(baseCharge)
                .tariffDescription(desc)
                .vacantRoomsCost(vacantToday * singleTariff)
                .bookingDurationDays(1)
                .roomDetails(roomDetailList)
                .typeSummaries(typeSummaries)
                .build();
    }

    public byte[] downloadHostelCapacityExcelReport(Long selectedHostelId, String fromDate, String toDate) throws Exception {
        List<HostelCapacityDto> capacityList = getHostelCapacityList(fromDate, toDate);
        List<HostelStudentDistributionDto> studentDistList = getHostelStudentDistribution(selectedHostelId, fromDate, toDate);
        HostelGuestTariffDto guestTariffDto = getLiveGuestRoomTariffDetails(selectedHostelId);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            // Shared Styles
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle subHeaderStyle = workbook.createCellStyle();
            Font subHeaderFont = workbook.createFont();
            subHeaderFont.setBold(true);
            subHeaderFont.setColor(IndexedColors.WHITE.getIndex());
            subHeaderStyle.setFont(subHeaderFont);
            subHeaderStyle.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
            subHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);

            // Sheet 1: Capacity Summary (Category-Wise)
            Sheet sheet1 = workbook.createSheet("1. Capacity Summary");
            Row titleRow1 = sheet1.createRow(0);
            Cell titleCell1 = titleRow1.createCell(0);
            titleCell1.setCellValue("SECTION 1: HOSTEL CAPACITY SUMMARY BY CATEGORY (" + (fromDate != null ? fromDate : "Start") + " to " + (toDate != null ? toDate : "End") + ")");
            titleCell1.setCellStyle(headerStyle);

            String[] s1Headers = {
                "Hostel Name", "Code", "Gender", "Total Cap", "Total Utilized", "Partially Occupied Rooms", "Partially Vacant Seats", "Total Vacant",
                "Single Rooms", "Single Cap", "Double Rooms", "Double Cap", "Triple Rooms", "Triple Cap",
                "Quad Rooms", "Quad Cap", "Dorm Rooms", "Dorm Cap", "PD Rooms", "PD Cap",
                "Guest Rooms", "Guest Cap", "ICSR Rooms", "ICSR Cap", "Official Rooms", "Official Cap"
            };

            Row headerRow1 = sheet1.createRow(2);
            for (int i = 0; i < s1Headers.length; i++) {
                Cell cell = headerRow1.createCell(i);
                cell.setCellValue(s1Headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rIdx1 = 3;
            for (HostelCapacityDto h : capacityList) {
                if (selectedHostelId != null && selectedHostelId > 0 && !selectedHostelId.equals(h.getHostelId())) continue;
                Row row = sheet1.createRow(rIdx1++);
                row.createCell(0).setCellValue(h.getHostelName());
                row.createCell(1).setCellValue(h.getHostelCode());
                row.createCell(2).setCellValue("M".equals(h.getHostelGenderType()) ? "Male" : "F".equals(h.getHostelGenderType()) ? "Female" : "Co-Ed");
                row.createCell(3).setCellValue(h.getTotalCapacity());
                row.createCell(4).setCellValue(h.getTotalUtilized());
                row.createCell(5).setCellValue(h.getPartiallyVacantRooms());
                row.createCell(6).setCellValue(h.getPartiallyVacantBeds());
                row.createCell(7).setCellValue(h.getTotalVacant());

                row.createCell(8).setCellValue(h.getSingleRooms());
                row.createCell(9).setCellValue(h.getSingleCapacity());
                row.createCell(10).setCellValue(h.getDoubleRooms());
                row.createCell(11).setCellValue(h.getDoubleCapacity());
                row.createCell(12).setCellValue(h.getTripleRooms());
                row.createCell(13).setCellValue(h.getTripleCapacity());
                row.createCell(14).setCellValue(h.getQuadRooms());
                row.createCell(15).setCellValue(h.getQuadCapacity());
                row.createCell(16).setCellValue(h.getDormRooms());
                row.createCell(17).setCellValue(h.getDormCapacity());
                row.createCell(18).setCellValue(h.getPdRooms());
                row.createCell(19).setCellValue(h.getPdCapacity());
                row.createCell(20).setCellValue(h.getGuestRooms());
                row.createCell(21).setCellValue(h.getGuestCapacity());
                row.createCell(22).setCellValue(h.getIcsrRooms());
                row.createCell(23).setCellValue(h.getIcsrCapacity());
                row.createCell(24).setCellValue(h.getOfficialRooms());
                row.createCell(25).setCellValue(h.getOfficialCapacity());
            }
            for (int i = 0; i < s1Headers.length; i++) sheet1.autoSizeColumn(i);

            // Sheet 2: Vacancy Report (including Partial Vacancy)
            Sheet sheet2 = workbook.createSheet("2. Vacancy Report");
            Row titleRow2 = sheet2.createRow(0);
            Cell titleCell2 = titleRow2.createCell(0);
            titleCell2.setCellValue("SECTION 2: HOSTEL VACANCY & PARTIAL VACANCY REPORT");
            titleCell2.setCellStyle(headerStyle);

            String[] s2Headers = {
                "Hostel Name", "Total Vacant Seats", "Partially Vacant Rooms", "Partially Vacant Seats",
                "Vacant Single Seats", "Vacant Double Seats", "Vacant Triple Seats", "Vacant Quad Seats",
                "Vacant Dorm Seats", "Vacant PD Seats", "Vacant Guest Seats", "Vacant ICSR Seats", "Vacant Official Seats"
            };
            Row headerRow2 = sheet2.createRow(2);
            for (int i = 0; i < s2Headers.length; i++) {
                Cell cell = headerRow2.createCell(i);
                cell.setCellValue(s2Headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rIdx2 = 3;
            for (HostelCapacityDto h : capacityList) {
                if (selectedHostelId != null && selectedHostelId > 0 && !selectedHostelId.equals(h.getHostelId())) continue;
                Row row = sheet2.createRow(rIdx2++);
                row.createCell(0).setCellValue(h.getHostelName());
                row.createCell(1).setCellValue(h.getTotalVacant());
                row.createCell(2).setCellValue(h.getPartiallyVacantRooms());
                row.createCell(3).setCellValue(h.getPartiallyVacantBeds());
                row.createCell(4).setCellValue(h.getSingleVacant());
                row.createCell(5).setCellValue(h.getDoubleVacant());
                row.createCell(6).setCellValue(h.getTripleVacant());
                row.createCell(7).setCellValue(h.getQuadVacant());
                row.createCell(8).setCellValue(h.getDormVacant());
                row.createCell(9).setCellValue(h.getPdVacant());
                row.createCell(10).setCellValue(h.getGuestVacant());
                row.createCell(11).setCellValue(h.getIcsrVacant());
                row.createCell(12).setCellValue(h.getOfficialVacant());
            }
            for (int i = 0; i < s2Headers.length; i++) sheet2.autoSizeColumn(i);

            // Sheet 3: Utilization & Student Distribution Report
            Sheet sheet3 = workbook.createSheet("3. Utilization Report");
            addTitleRow(sheet3, 0, "SECTION 3: HOSTEL UTILIZATION & STUDENT DISTRIBUTION ANALYSIS", headerStyle);

            int rIdx3 = 2;
            addTitleRow(sheet3, rIdx3++, "SECTION 3A: ROOM CATEGORY UTILIZATION SUMMARY", subHeaderStyle);
            addHeaderRow(sheet3, rIdx3++, new String[]{
                "Hostel Name", "Room Category", "Utilized Rooms (Count)",
                "Partially Occupied (Rooms / Beds)", "Overloaded (Rooms / Seats)", "Total Allotted Seats (Inc. Overload)"
            }, headerStyle);

            for (HostelCapacityDto h : capacityList) {
                if (selectedHostelId != null && selectedHostelId > 0 && !selectedHostelId.equals(h.getHostelId())) continue;

                Object[][] cats = {
                    {"Single Occupancy", nvl(h.getSingleUtilizedRooms()), h.getSinglePartVacRooms(), h.getSinglePartVacBeds(), h.getSingleOverRooms(), h.getSingleOverSeats(), nvl(h.getSingleUtilized())},
                    {"Double Occupancy", nvl(h.getDoubleUtilizedRooms()), h.getDoublePartVacRooms(), h.getDoublePartVacBeds(), h.getDoubleOverRooms(), h.getDoubleOverSeats(), nvl(h.getDoubleUtilized())},
                    {"Triple Occupancy", nvl(h.getTripleUtilizedRooms()), h.getTriplePartVacRooms(), h.getTriplePartVacBeds(), h.getTripleOverRooms(), h.getTripleOverSeats(), nvl(h.getTripleUtilized())},
                    {"Quadruple Occupancy", nvl(h.getQuadUtilizedRooms()), h.getQuadPartVacRooms(), h.getQuadPartVacBeds(), h.getQuadOverRooms(), h.getQuadOverSeats(), nvl(h.getQuadUtilized())},
                    {"Dormitory Rooms", nvl(h.getDormUtilizedRooms()), h.getDormPartVacRooms(), h.getDormPartVacBeds(), h.getDormOverRooms(), h.getDormOverSeats(), nvl(h.getDormUtilized())},
                    {"PD Rooms", nvl(h.getPdUtilizedRooms()), h.getPdPartVacRooms(), h.getPdPartVacBeds(), h.getPdOverRooms(), h.getPdOverSeats(), nvl(h.getPdUtilized())},
                    {"Guest Rooms", nvl(h.getGuestUtilizedRooms()), h.getGuestPartVacRooms(), h.getGuestPartVacBeds(), h.getGuestOverRooms(), h.getGuestOverSeats(), nvl(h.getGuestUtilized())},
                    {"ICSR Rooms", nvl(h.getIcsrUtilizedRooms()), h.getIcsrPartVacRooms(), h.getIcsrPartVacBeds(), h.getIcsrOverRooms(), h.getIcsrOverSeats(), nvl(h.getIcsrUtilized())},
                    {"Official Rooms", nvl(h.getOfficialUtilizedRooms()), h.getOfficialPartVacRooms(), h.getOfficialPartVacBeds(), h.getOfficialOverRooms(), h.getOfficialOverSeats(), nvl(h.getOfficialUtilized())}
                };

                long totRooms = 0;
                for (Object[] c : cats) {
                    addCatRow(sheet3, rIdx3++, h.getHostelName(), (String) c[0], (Long) c[1], (Long) c[2], (Long) c[3], (Long) c[4], (Long) c[5], (Long) c[6]);
                    totRooms += (Long) c[1];
                }

                Row rTot = sheet3.createRow(rIdx3++);
                addStyledCell(rTot, 0, h.getHostelName(), boldStyle);
                addStyledCell(rTot, 1, "TOTAL ALLOTTED SEATS (INC. OVERLOAD)", boldStyle);
                addStyledCell(rTot, 2, totRooms, boldStyle);
                addStyledCell(rTot, 3, nvl(h.getPartiallyVacantRooms()) + " / " + nvl(h.getPartiallyVacantBeds()), boldStyle);
                addStyledCell(rTot, 4, nvl(h.getOverloadedRooms()) + " / " + nvl(h.getOverloadedSeats()), boldStyle);
                addStyledCell(rTot, 5, nvl(h.getTotalUtilized()), boldStyle);

                rIdx3++;
            }

            rIdx3++;
            addTitleRow(sheet3, rIdx3++, "SECTION 3B: RESIDENT STUDENT DISTRIBUTION (COURSE & BATCH YEAR)", subHeaderStyle);
            addHeaderRow(sheet3, rIdx3++, new String[]{"Hostel Name", "Course / Department", "Course Code", "Batch Year", "Resident Student Count"}, headerStyle);

            for (HostelStudentDistributionDto s : studentDistList) {
                Row row = sheet3.createRow(rIdx3++);
                row.createCell(0).setCellValue(s.getHostelName());
                row.createCell(1).setCellValue(s.getCourseName());
                row.createCell(2).setCellValue(s.getCourseCode());
                row.createCell(3).setCellValue(s.getBatchYear());
                row.createCell(4).setCellValue(s.getStudentCount());
            }

            for (int i = 0; i < 6; i++) sheet3.autoSizeColumn(i);

            // Sheet 4: Live Guest Room Type Summary & Tariff Report (Present Date Only)
            Sheet sheet4 = workbook.createSheet("4. Live Guest Rooms & Tariff");
            addTitleRow(sheet4, 0, "SECTION 4: LIVE GUEST ROOM DETAILS & TARIFF (PRESENT DATE: " + LocalDate.now() + ")", headerStyle);

            Row tariffInfoRow = sheet4.createRow(1);
            tariffInfoRow.createCell(0).setCellValue("Single Guest Room Rate: ₹" + guestTariffDto.getSingleRoomTariff() + " | Shared Guest Room Rate: ₹" + guestTariffDto.getSharedRoomTariff() + " | Stay with Student: ₹" + guestTariffDto.getLodgingBaseCharge());

            addHeaderRow(sheet4, 3, new String[]{"Room Category / Type", "Total Rooms", "Total Capacity (Seats)", "Occupied Rooms", "Occupied Seats", "Vacant Rooms", "Vacant Seats"}, headerStyle);

            int rIdx4 = 4;
            if (guestTariffDto.getTypeSummaries() != null) {
                for (HostelGuestTariffDto.GuestTypeSummary s : guestTariffDto.getTypeSummaries()) {
                    Row row = sheet4.createRow(rIdx4++);
                    row.createCell(0).setCellValue(s.getDisplayName());
                    row.createCell(1).setCellValue(s.getTotalRooms());
                    row.createCell(2).setCellValue(s.getTotalCapacity());
                    row.createCell(3).setCellValue(s.getOccupiedRooms());
                    row.createCell(4).setCellValue(s.getOccupiedSeats());
                    row.createCell(5).setCellValue(s.getVacantRooms());
                    row.createCell(6).setCellValue(s.getVacantSeats());
                }
            }
            for (int i = 0; i < 7; i++) sheet4.autoSizeColumn(i);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void addTitleRow(Sheet s, int rowIdx, String title, CellStyle style) {
        Row r = s.createRow(rowIdx);
        Cell c = r.createCell(0);
        c.setCellValue(title);
        if (style != null) c.setCellStyle(style);
    }

    private void addHeaderRow(Sheet s, int rowIdx, String[] headers, CellStyle style) {
        Row r = s.createRow(rowIdx);
        for (int i = 0; i < headers.length; i++) {
            Cell c = r.createCell(i);
            c.setCellValue(headers[i]);
            if (style != null) c.setCellStyle(style);
        }
    }

    private void addCatRow(Sheet s, int rowIdx, String hostel, String category, long utilRooms, Long partR, Long partB, Long overR, Long overS, long utilSeats) {
        Row r = s.createRow(rowIdx);
        r.createCell(0).setCellValue(hostel);
        r.createCell(1).setCellValue(category);
        r.createCell(2).setCellValue(utilRooms);
        r.createCell(3).setCellValue(nvl(partR) + " / " + nvl(partB));
        r.createCell(4).setCellValue(nvl(overR) + " / " + nvl(overS));
        r.createCell(5).setCellValue(utilSeats);
    }

    private void addStyledCell(Row r, int col, Object val, CellStyle style) {
        Cell c = r.createCell(col);
        if (val instanceof Number) c.setCellValue(((Number) val).doubleValue());
        else c.setCellValue(val != null ? val.toString() : "");
        if (style != null) c.setCellStyle(style);
    }

    private long nvl(Long val) {
        return val != null ? val : 0L;
    }
}
