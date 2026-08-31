package com.iitm.hosteldine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BulkAsyncExecutor {
    private final InMemoryLogService logService;

    @Async
    public void execute(String tag, Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            addError(tag, 0, "Async Execution Failed : " + e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    private void addError(String tag, long sessionId, String msg,Exception e) {
        logService.addError(tag, " <-" + sessionId + "-> " + msg,null);
    }
}
