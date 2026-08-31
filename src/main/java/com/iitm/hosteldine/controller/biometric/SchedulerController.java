package com.iitm.hosteldine.controller.biometric;

import com.iitm.hosteldine.constant.biometric.URLConstant;
import com.iitm.hosteldine.controller.CommonController;
import com.iitm.hosteldine.dto.SchedulerLogDto;
import com.iitm.hosteldine.model.biometric.DeviceFRLogs;
import com.iitm.hosteldine.model.biometric.MessMaster;
import com.iitm.hosteldine.model.biometric.MessTerminal;
import com.iitm.hosteldine.service.BulkAsyncExecutor;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.service.SchedulerLogService;
import com.iitm.hosteldine.service.biometric.BiometricService;
import com.iitm.hosteldine.service.biometric.MessService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(URLConstant.FR_SCHEDULER)
public class SchedulerController {

    private final String tag = "frScheduler";

    private MessService messService;
    private BiometricService biometricService;
    private InMemoryLogService logService;
    private CommonController commonController;
    private BulkAsyncExecutor bulkAsyncExecutor;

    @Value("${daily.at.midnight}")
    String dailyPushRemove;
    @Value("${daily.at.end.of.day}")
    String dailyCollectLogs;
    @Value("${clear.iot.logs}")
    String clearIotLogs;
    private CommonResponseUtil commonResponseUtil;
    private SchedulerLogService schedulerLogService;
    private boolean record = true;

    @GetMapping
    public String scheduler(ModelMap model, HttpServletRequest request) {
        commonController.updateCommonAttributes(model);
        commonResponseUtil.updateCommonModelAttributes(model, request);
        return "biometric/frScheduler";
    }

    @GetMapping(value = "/pushRemoveCron")
    public @ResponseBody String checkMessUpdateAPI() {
        addLog(tag, "Entering checkMessUpdateAPI", record);
        checkMessUpdate();
        return commonResponseUtil.getMessage("message.check.mess.update.task.triggered");
    }

    @GetMapping(value = "/pushNextSessionCron")
    public @ResponseBody String pushNextSessionAPI() {
        addLog(tag, "Entering checkMessUpdateAPI", record);
        checkNextMessUpdate();
        return commonResponseUtil.getMessage("message.check.next.mess.update.task.triggered");
    }

    @GetMapping(value = "/collectDeviceLogsCron")
    public @ResponseBody String collectDeviceLogsAPI(@RequestParam(required = false) String fromDate,
                                                     @RequestParam(required = false) String toDate) {
        addLog(tag, "Entering collectDeviceLogsAPI", record);
        collectDeviceLogsAsyncTrigger(fromDate, toDate);
        return commonResponseUtil.getMessage("message.collect.device.log.triggered");
    }

    @GetMapping(value = "/clearDeviceLogsCron")
    public @ResponseBody String clearDeviceLogsAPI() {
        addLog(tag, "Entering clearDeviceLogsAPI", record);
        clearDeviceLogsAsyncTrigger();
        return commonResponseUtil.getMessage("message.clear.device.log.task.triggered");
    }

    @GetMapping(value="/getSchedulerLogs")
    public @ResponseBody List<SchedulerLogDto> getSchedulerLogs() {
        return messService.getSchedulerLogs();
    }

    @Scheduled(cron = "${daily.at.midnight}")
    public void checkMessUpdateCron() {
        System.out.println("Mess Update Runs every day at "+dailyPushRemove);
        addLog(tag, "Entering checkMessUpdateCron", record);
        checkMessUpdate();
    }

    @Scheduled(cron = "${daily.at.end.of.day}")
    public void collectDeviceLogsCron() {
        System.out.println("Collect Logs Runs every day at "+dailyCollectLogs);
        addLog(tag, "Entering collectDeviceLogsCron", record);
        collectDeviceLogsAsyncTrigger(null, null);
    }

    @Scheduled(cron = "${clear.iot.logs}")
    public void clearDeviceLogsCron() {
        System.out.println("Clear Logs Runs every month at "+clearIotLogs);
        addLog(tag, "Entering clearLogsCron " + clearIotLogs, record);
        clearDeviceLogsAsyncTrigger();
    }

    private void checkMessUpdate() {
        addLog(tag, "Entering checkMessUpdate", record);
        bulkAsyncExecutor.execute(tag, () -> messService.checkMessUpdate(tag, record));
//        messService.checkMessUpdate(tag, record);
    }

    private void checkNextMessUpdate() {
        addLog(tag, "Entering checkNextMessUpdate", record);
        bulkAsyncExecutor.execute(tag, () -> messService.checkNextMessUpdate(tag, record));
//        messService.checkNextMessUpdate(tag, record);
    }

    private void collectDeviceLogsAsyncTrigger(String fromDate, String toDate) {
        addLog(tag, "Entering collectLogs", record);
        bulkAsyncExecutor.execute(tag, () -> collectDeviceLogs(fromDate, toDate));
    }

    private void clearDeviceLogsAsyncTrigger() {
        addLog(tag, "Entering clearLogs", record);
        bulkAsyncExecutor.execute(tag, this::clearDeviceLogs);
    }

    private void collectDeviceLogs(String fromDate, String toDate) {
        List<MessMaster> messMasterList = messService.getMessMasterList();
        for (MessMaster messMaster : messMasterList) {
            addLog(tag, "Collecting IPs for " + messMaster.getMessName(), record);
            List<MessTerminal> messTerminalList = messService.getMessTerminalListByMess(messMaster.getMessMasterId());
            for (MessTerminal messTerminal : messTerminalList) {
                addLog(tag, "Collecting Logs for " + messTerminal.getTerminalIp(), record);
                List<DeviceFRLogs> logs = biometricService.getLogsByIp(tag, messTerminal, fromDate, toDate, record);
                addLog(tag, "Log count: " + logs.size(), record);
                messService.saveLogs(tag, logs);
            }
        }
        addLog(tag, "Exiting collectLogs", record);
        addLog(tag, "Exiting collectDeviceLogsAPI", record);
    }

    private void clearDeviceLogs() {
        addLog(tag, "Entering clearLogs", record);
        List<MessMaster> messMasterList = messService.getMessMasterList();
        for (MessMaster messMaster : messMasterList) {
            addLog(tag, "Collecting IPs for " + messMaster.getMessName(), record);
            List<MessTerminal> messTerminalList = messService.getMessTerminalListByMess(messMaster.getMessMasterId());
            for (MessTerminal messTerminal : messTerminalList) {
                addLog(tag, "Collecting Logs for " + messTerminal.getTerminalIp(), record);
                biometricService.deleteLogsByIp(tag, messTerminal, record);
            }
        }
        addLog(tag, "Exiting clearLogs", record);
        addLog(tag, "Exiting clearDeviceLogsAPI", record);
    }

    private void addLog(String tag, String msg, boolean record) {
        logService.addLog(tag, " <--> " + msg);
        if (record) {
            schedulerLogService.addLog(tag, msg);
        }
    }

    @Autowired
    public void setLogService(InMemoryLogService logService) {
        this.logService = logService;
    }

    @Autowired
    public void setMessService(MessService messService) {
        this.messService = messService;
    }

    @Autowired
    public void setBiometricService(BiometricService biometricService) {
        this.biometricService = biometricService;
    }

    @Autowired public void setCommonController(CommonController commonController) {
        this.commonController = commonController;
    }

    @Autowired
    public void setCommonResponseUtil(CommonResponseUtil commonResponseUtil) {
        this.commonResponseUtil = commonResponseUtil;
    }

    @Autowired
    public void setSchedulerLogService(SchedulerLogService schedulerLogService) {
        this.schedulerLogService = schedulerLogService;
    }

    @Autowired
    public void setBulkAsyncExecutor(BulkAsyncExecutor bulkAsyncExecutor) {
        this.bulkAsyncExecutor = bulkAsyncExecutor;
    }
}
