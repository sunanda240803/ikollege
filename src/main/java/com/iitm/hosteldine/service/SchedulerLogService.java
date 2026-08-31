package com.iitm.hosteldine.service;

import com.iitm.hosteldine.dto.SchedulerLogDto;
import com.iitm.hosteldine.mapper.SchedulerLogMapper;
import com.iitm.hosteldine.model.SchedulerLog;
import com.iitm.hosteldine.repository.SchedulerLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SchedulerLogService {
    private SchedulerLogRepository schedulerLogRepository;

    public void addLog(String tag, String logMsg) {
        SchedulerLog log = new SchedulerLog(tag, logMsg);
        schedulerLogRepository.saveAndFlush(log);
    }

    @Autowired
    public void setSchedulerLogRepository(SchedulerLogRepository schedulerLogRepository) {
        this.schedulerLogRepository = schedulerLogRepository;
    }

    public List<SchedulerLogDto> getLogByDate(LocalDateTime now) {
        return schedulerLogRepository.getAllByCreatedDate(now).stream().map(SchedulerLogMapper.INSTANCE::fromEntity).toList();
    }
}
