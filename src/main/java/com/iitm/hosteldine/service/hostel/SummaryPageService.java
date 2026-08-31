package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.SummaryPageDto;
import com.iitm.hosteldine.repository.hostel.HostelUserMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SummaryPageService {

    private final HostelUserMappingRepository hostelUserMappingRepository;

    public SummaryPageDto getRoomDetails(Long hostelId) {
        return Optional.ofNullable(hostelId)
                .flatMap(hostelUserMappingRepository::getRoomDetails)
                .map(result -> {
                    Object[] row = (Object[]) result;
                    return returnRoomDetailsDto(row);
                })
                .orElse(null);
    }

    public List<SummaryPageDto> getAccommodationCountList(String userName) {
        return hostelUserMappingRepository.getAccommodationCountList(userName, ModelConstants.HYPHEN)
                .map(resultList -> resultList.stream()
                        .map(obj -> returnAccommodationCountDetailsDto((Object[]) obj))
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    public Long getVacantCount(String userRole, String userName, String approvalStatus){
        return hostelUserMappingRepository.getVacatingCount(userRole, userName, approvalStatus).orElse(0L);
    }

    public Long getHostelEnrollmentCount(String approvalStatus, String userRole, String userName, Long hostelId){
        return hostelUserMappingRepository.getHostelEnrollmentCount(approvalStatus, userRole, userName, hostelId).orElse(0L);
    }

    private SummaryPageDto returnRoomDetailsDto(Object[] row) {
        return SummaryPageDto.builder()
                .hostelId(getStringValue(row, 0))
                .hostelName(getStringValue(row, 1))
                .totalRooms(getIntValue(row, 2))
                .general(getIntValue(row, 3))
                .official(getIntValue(row, 4))
                .guest(getIntValue(row, 5))
                .icsr(getIntValue(row, 6))
                .notFit(getIntValue(row, 7))
                .pd(getIntValue(row, 8))
                .roomsAllotted(getIntValue(row, 9))
                .roomsVacant(getIntValue(row, 10))
                .totalNoOfSeats(getIntValue(row, 11))
                .allottedSeats(getIntValue(row, 12))
                .seatsVacant(getIntValue(row, 13))
                .build();
    }

    private SummaryPageDto returnAccommodationCountDetailsDto(Object[] row) {
        return SummaryPageDto.builder()
                .accommodationType(getStringValue(row, 0))
                .studentType(getStringValue(row, 1))
                .allottedTotal(getIntValue(row, 2))
                .checkedIn(getIntValue(row, 3))
                .pendingCheckedIn(getIntValue(row, 4))
                .tobCheckedOutToday(getIntValue(row, 5))
                .checkedOutToday(getIntValue(row, 6))
                .pendingCheckedOut(getIntValue(row, 7))
                .build();
    }

    private String getStringValue(Object[] row, int index) {
        return row != null && index < row.length && row[index] != null
                ? row[index].toString()
                : ModelConstants.EMPTY_STRING;
    }

    private int getIntValue(Object[] row, int index) {
        return row != null && index < row.length && row[index] != null
                ? ((Number) row[index]).intValue()
                : 0;
    }
}
