package com.iitm.hosteldine.service.mess;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.dto.student.StudentDetailsInfoDto;
import com.iitm.hosteldine.repository.mess.MessOpeningBalRepository;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MessOpeningBalService {

    private final StudentDetailsInfoService studentDetailsInfoService;
    private final MessOpeningBalRepository messOpeningBalRepository;

    public Double getOpeningBal() {
        String studentId = SecurityCtxUtil.userId().toUpperCase();
        StudentDetailsInfoDto studentInfo = studentDetailsInfoService.getStudentInfoDetails(studentId);

        List<String> accountHeads = new ArrayList<>();
        accountHeads.add(studentId);

        Optional.ofNullable(studentInfo)
                .map(StudentDetailsInfoDto::getPreviousId)
                .filter(id -> !id.isEmpty())
                .ifPresent(previousId -> accountHeads.addAll(Arrays.asList(previousId.split(","))));

        return messOpeningBalRepository.getOpeningBalance(accountHeads)
                .filter(list->!list.isEmpty())
                .map(list -> Optional.ofNullable(list.getFirst()[4])
                        .map(String::valueOf)
                        .map(Double::parseDouble)
                        .orElse(0.0))
                .orElse(0.0);
    }
}
