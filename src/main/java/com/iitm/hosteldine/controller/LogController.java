package com.iitm.hosteldine.controller;

import com.iitm.hosteldine.service.InMemoryLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LogController {

    private final InMemoryLogService logService;

    @Autowired
    public LogController(InMemoryLogService logService) {
        this.logService = logService;
    }

    @GetMapping("/logs/{tag}")
    public List<String> getLatestLogs(@PathVariable String tag) {
        return logService.getLatestLogs(tag, 0);
    }
}
