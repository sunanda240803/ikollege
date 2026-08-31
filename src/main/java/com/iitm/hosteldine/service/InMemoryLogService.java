package com.iitm.hosteldine.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Service
public class InMemoryLogService {

    private static final Map<String, ConcurrentLinkedQueue<String>> logMap = new ConcurrentHashMap<>();

    public void addLog(String tag, String logMessage) {
        addLog(tag, logMessage, true);
    }

    public void addLog(String tag, String logMessage, boolean withTimestamp) {
        log.info(logMessage);
        ConcurrentLinkedQueue<String> queue = logMap.computeIfAbsent(tag, k -> new ConcurrentLinkedQueue<>());
        queue.add("INFO: " + (withTimestamp ? (new Date() + "|->") : "") + tag + " :" + logMessage);
    }

    public void addError(String tag, String message, Exception e) {
        addError(tag, message, e, true);
    }

    public void addError(String tag, String message, Exception e, boolean withTimestamp) {
        log.error(message, e);
        ConcurrentLinkedQueue<String> queue = logMap.computeIfAbsent(tag, k -> new ConcurrentLinkedQueue<>());
        queue.add("ERROR: " + (withTimestamp ? (new Date() + "|->") : "") + tag + " :" + message + ">>" + e.getMessage());
    }

    public void addValidation(String tag, String message) {
        addValidation(tag, message, true);
    }

    public void addValidation(String tag, String message, boolean withTimestamp) {
        log.info(message);
        ConcurrentLinkedQueue<String> queue = logMap.computeIfAbsent(tag, k -> new ConcurrentLinkedQueue<>());
        queue.add("ERROR: " + (withTimestamp ? (new Date() + "|->") : "") + tag + " :" + message );
    }

    public synchronized List<String> getLatestLogs(String tag, int count) {
        ConcurrentLinkedQueue<String> queue = logMap.get(tag);
        if (queue == null) return new ArrayList<>();
        if (count == 0) count = queue.size();
        List<String> latestLogs = new ArrayList<>(count);
        for (int i = 0; i < count && !queue.isEmpty(); i++) {
            latestLogs.add(queue.poll());
        }
        return latestLogs;
    }
}
