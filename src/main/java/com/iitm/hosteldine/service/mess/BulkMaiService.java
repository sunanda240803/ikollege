package com.iitm.hosteldine.service.mess;

import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.service.BulkAsyncExecutor;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BulkMaiService {

    private final MessPeriodConfigService messPeriodConfigService;
    private final BulkAsyncExecutor bulkAsyncExecutor;
    private final CommonResponseUtil commonResponseUtil;
    private final InMemoryLogService logService;

    public MessMasterControllerDto startBulkMail(MessMasterControllerDto dto) {
        addLog(dto.getLogTag(), 0, commonResponseUtil.getMessage("process.started"));
        bulkAsyncExecutor.execute(dto.getLogTag(),
                () -> {
                    try {
                        messPeriodConfigService.sendStudentMessBulkMail(dto);
                    } catch (Exception e) {
                        addError(dto.getLogTag(), 0, commonResponseUtil.getMessage("process.failed") + e.getMessage(),e);
                        throw new RuntimeException(e);
                    }
                });
        return dto;
    }

    private void addLog(String tag, long sessionId, String msg) {
        logService.addLog(tag, " <-" + sessionId + "-> " + msg);
    }

    private void addError(String tag, long sessionId, String msg,Exception e) {
        logService.addError(tag, " <-" + sessionId + "-> " + msg,e);
    }

}