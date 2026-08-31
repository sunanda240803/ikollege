package com.iitm.hosteldine.service.api;

import com.iitm.hosteldine.dto.api.WorkFlowStudentAPIDto;
import com.iitm.hosteldine.dto.api.WorkFlowWardenAPIDto;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkFlowAPIService {

    private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;

    public List<WorkFlowStudentAPIDto> getStudentDetailsJSON(String fromDate, String toDate) {
        return allStudentsDetailsViewRepository.getWorkflowApiStudentDetails(fromDate, toDate).stream()
                .map(row -> WorkFlowStudentAPIDto.builder()
                        .studentId((String) row[0])
                        .studentName((String) row[1])
                        .hostelName((String) row[2])
                        .roomNo((int) row[3])
                        .seatName((String) row[4])
                        .lastResidenceDate((Date) row[5])
                        .hostelStudentStatus((String) row[6])
                        .previousId((String) row[7])
                        .vacateDate(row[8]!=null ? (String) row[8] : "-")
                        .workFlowFlag((String) row[9])
                        .workFlowDate((String) row[10])
                        .inputDateParameter(fromDate!=null && toDate!=null ? fromDate+" - "+toDate : null)
                        .build())
                .collect(Collectors.toList());
    }

    public List<WorkFlowWardenAPIDto> getWardenDetailsJSON() {
        return allStudentsDetailsViewRepository.getWorkflowApiWardenDetails().stream()
                .map(row -> WorkFlowWardenAPIDto.builder()
                        .wardenName((String) row[0])
                        .wardenEmail((String) row[1])
                        .wardenPhoneNo((String) row[2])
                        .wardenLDAPName((String) row[3])
                        .hostelCode((String) row[4])
                        .hostelName((String) row[5])
                        .build())
                .collect(Collectors.toList());
    }
}
