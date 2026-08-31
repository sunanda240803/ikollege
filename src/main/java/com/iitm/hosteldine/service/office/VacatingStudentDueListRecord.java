package com.iitm.hosteldine.service.office;

import com.iitm.hosteldine.dto.dean.PropertyDto;

import java.time.LocalDate;
import java.util.List;

public record VacatingStudentDueListRecord(
        Long id,
        String slNo,
        String studentId,
        String studentName,
        String hostelName,
        String roomNumber,
        LocalDate vacatingDate,
        String vacatingReason,
        List<PropertyDto> actionList,
        String vacatingDateStr,
        Double studentBalance
        ) {
}
