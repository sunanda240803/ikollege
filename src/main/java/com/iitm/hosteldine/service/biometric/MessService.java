package com.iitm.hosteldine.service.biometric;

import com.hectrix.www.ACTAtek_xsd.ActatekUser;
import com.iitm.hosteldine.constant.biometric.FrConstant;
import com.iitm.hosteldine.dao.biometric.*;
import com.iitm.hosteldine.dto.SchedulerLogDto;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.form.biometric.FrForm;
import com.iitm.hosteldine.form.biometric.MessDetailsCountForm;
import com.iitm.hosteldine.form.biometric.UserListForm;
import com.iitm.hosteldine.model.biometric.*;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.service.SchedulerLogService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessService {
    private MessMasterDao messMasterDao;
    private MessTerminalDao messTerminalDao;
    private UserFpService userFpService;
    private StudentMessDetailsDao studentMessDetailsDao;
    private InMemoryLogService logService;
    private BiometricService biometricService;
    private UserFpCardDao userFpCardDao;
    private DeviceFRLogsDao deviceFRLogsDao;
    private IitAMessRebateDao iitAMessRebateDao;
    private VacatingRequestDao vacatingRequestDao;
    private MessMasterControllerDao messMasterControllerDao;
    private SchedulerLogService schedulerLogService;
    private MessMasterCommonService messMasterCommonService;

    public List<MessMaster> getMessMasterList() {
        return messMasterDao.getMessMasterList().stream().sorted(Comparator.comparing(MessMaster::getMessName)).collect(Collectors.toList());
    }

    public List<MessTerminal> getMessTerminalListByMess(Long messId) {
        return messTerminalDao.getMessTerminalList(messId).stream().sorted(Comparator.comparing(MessTerminal::getTerminalIp)).collect(Collectors.toList());
    }

    public List<UserListForm> getStudentListByMess(String tag, Long messId) {
        List<ActatekUser> users = new ArrayList<>();
        getMessTerminalListByMess(messId).forEach(messTerminal -> {
            addLog(tag, "Sync Initiated for " + messTerminal.getTerminalIp());
            ActatekUser[] userList = biometricService.getUserListByIP(tag, messId, messTerminal);
            users.addAll(Arrays.asList(userList != null ? userList : new ActatekUser[0]));
        });
        List<UserListForm> resultList = new ArrayList<>();
        if (!users.isEmpty()) {
            users.forEach(actatekUser -> {
                UserListForm userListForm = userFpService.getUserBySerial(actatekUser.getUserID(), true);
                resultList.add(userListForm);
            });
        }
        return resultList;
    }

    public void performSync(String tag, Long messId) throws Exception {
        if (messId == 0) {
            getMessMasterList().forEach(messMaster -> {
                try {
                    performSyncByMess(tag, messMaster.getMessMasterId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            performSyncByMess(tag, messId);
        }
    }

    public void stopSync(String tag, Long messId) {
        addLog(tag, "Force Stop triggered for " + messId);
        biometricService.forceStopSync(messId);

    }

    public void performSyncByMess(String tag, Long messMasterId) throws Exception {
        List<StudentMessDetails> studentMessDetails = studentMessDetailsDao.getCurrentStudentMessDetailsByMessId(messMasterId);
        List<String> studentIds = studentMessDetails.stream().map(StudentMessDetails::getStudentId).toList();
        addLog(tag, "Fetched " + studentIds.size() + " students");
        getMessTerminalListByMess(messMasterId).forEach(messTerminal -> {
            addLog(tag, "Sync Initiated for " + messTerminal.getTerminalIp());
            List<String> studentList = new ArrayList<>(studentIds);
            new Thread(() -> biometricService.syncData(tag, messMasterId, messTerminal, studentList)).start();
        });
    }

    public String updatePushRemoveStatus(Long messId, String pushRemove) {
        return studentMessDetailsDao.updatePushRemoveByMess(messId, pushRemove);
    }

    public void pushDataByMessDevice(String tag, String messId, String pushIp) {
        addLog(tag, "MessId: " + messId + "|DeviceIP: " + pushIp);
        Map<Long, List<MessTerminal>> messTerminalList = new HashMap<>();
        if (pushIp != null && pushIp.equals("0")) {
            List<Long> messIds = new ArrayList<>();
            if (messId != null) {
                if (!messId.equals("0")) {
                    messIds.add(Long.valueOf(messId));
                } else {
                    messIds.addAll(messMasterDao.getMessIdList());
                }
            }
            addLog(tag, "MessIds: " + Arrays.toString(messIds.toArray()));
            if (!messIds.isEmpty()) {
                for (Long id : messIds) {
                    messTerminalList.put(id, messTerminalDao.getMessTerminalList(id));
                }
            }
        } else {
            List<MessTerminal> terminals = new ArrayList<>();
            MessTerminal terminal = messTerminalDao.getMessTerminalByIP(pushIp);
            if (terminal != null) {
                terminals.add(terminal);
                messTerminalList.put(terminal.getMessMasterId(), terminals);
            }
        }
        addLog(tag, "DeviceIPs: " + messTerminalList);
        for (Long id : messTerminalList.keySet()) {
            userFpService.pushDataByMessAndIp(tag, id, messTerminalList.get(id));
        }
        logService.addLog(tag, "Push Request Complete");
    }

    public void checkMessUpdate(String tag, boolean record) {
        addLog(tag, "Mess Update Cron Started", record);
        //Update Prev Mess Period Status
        updatePrevMessPeriodStatus(tag, record);
        //Update Mess Rebate Status
        updateMessRebateStatus(tag, record);
        //Update Mess Vacate Status
        updateMessVacateStatus(tag, record);
        //Remove users from devices
        checkMessPushRemove(tag, true, record);
        //Add Users to devices
        checkMessPushRemove(tag, false, record);
        addLog(tag, "Mess Update Cron Ended", record);
        addLog(tag, "Exiting checkMessUpdate", record);
        addLog(tag, "Exiting checkMessUpdateAPI", record);
    }

    public void checkNextMessUpdate(String tag, boolean record) {
        checkNextMessPushRemove(tag, record);
        addLog(tag, "Exiting checkNextMessUpdate", record);
        addLog(tag, "Exiting checkMessUpdateAPI", record);
    }

    private void checkNextMessPushRemove(String tag, boolean record) {
        MessMasterControllerDto nextMessPeriod = messMasterControllerDao.getNextMessMasterController();
        if (nextMessPeriod != null) {
            checkMessPushRemoveByPeriod(tag, false, nextMessPeriod, record);
        } else {
            addLog(tag, "Next Mess Period not available", record);
        }
    }

    private void updatePrevMessPeriodStatus(String tag, boolean record) {
        addLog(tag, "Checking Current date is equal to current period start.", record);
        MessMasterControllerDto currentMessSession = messMasterControllerDao.getCurrentMessMasterController();
        LocalDate today = LocalDate.now();
        if (currentMessSession.getDiningFromDate().equals(today)) {
            addLog(tag, "Getting Prev Mess Period details.", record);
            Optional<MessMasterControllerDto> prevMessPeriodDetails = messMasterCommonService.getMessPeriodDetails();
            MessMasterControllerDto prevMessdto = prevMessPeriodDetails.get();
            String result = studentMessDetailsDao.updatePrevMessPeriodStudStatus(prevMessdto,tag,record);
        }else{
            addLog(tag, "Not equal to current period start.", record);
        }
    }

    private void updateMessRebateStatus(String tag, boolean record) {
        addLog(tag, "Checking Mess Rebate status of students.", record);
        MessMasterControllerDto currentMessSession = messMasterControllerDao.getCurrentMessMasterController();
        List<IitAMessRebate> rebateStudentList = iitAMessRebateDao.getCurrentMessRebateList(currentMessSession);
        if (rebateStudentList != null && !rebateStudentList.isEmpty()) {
            addLog(tag, "Rebate List Size: " + rebateStudentList.size(), record);
            int count = 0;
            for (IitAMessRebate iitAMessRebate : rebateStudentList) {
                addLog(tag, "Rebate for " + iitAMessRebate.getStudentId() + " from " + iitAMessRebate.getRebateFrom() + " to " + iitAMessRebate.getRebateTo(), record);
                String result = studentMessDetailsDao.updateRebateStatus(iitAMessRebate,currentMessSession);
                addLog(tag, "Update Status: " + result, record);
                if (result != null && result.equals("updated")) {
                    count++;
                }
            }
            addLog(tag, "Count of To be Removed status updated for Rebate: " + count, record);
        } else {
            addLog(tag, "No Rebate data for the day", record);
        }
    }

    private void updateMessVacateStatus(String tag, boolean record) {
        addLog(tag, "Checking Room Vacate status of students.", record);
        List<VacatingRequest> vacatingStudentList = vacatingRequestDao.getVacatingStudentList();
        if (vacatingStudentList != null && !vacatingStudentList.isEmpty()) {
            addLog(tag, "Vacate List: " + vacatingStudentList.size(), record);
            MessMasterControllerDto currentMessSession = messMasterControllerDao.getCurrentMessMasterController();
            for (VacatingRequest vacatingRequest : vacatingStudentList) {
                addLog(tag, vacatingRequest.getStudentId() + " vacating on " + vacatingRequest.getVacatingDate(), record);
                String result = studentMessDetailsDao.updateVacateStatus(vacatingRequest,currentMessSession);
                addLog(tag, "Update Status: " + result, record);
            }
        } else {
            addLog(tag, "No Vacating data for the day", record);
        }
    }

    public void checkMessPushRemove(String tag, boolean toRemove, boolean record) {
        checkMessPushRemoveByPeriod(tag, toRemove, messMasterControllerDao.getCurrentMessMasterController(), record);
    }

    public void checkMessPushRemoveByPeriod(String tag, boolean toRemove, MessMasterControllerDto messPeriod,  boolean record) {
        Map<Long, ArrayList<StudentMessDetails>> studentList = messMasterDao.getStudentsToPushOrRemove(toRemove, messPeriod);

        addLog(tag, "Push or remove:" + (toRemove ? "Remove" : "Push"), record);
        addLog(tag, "Mess count: " + studentList.size(), record);
        if (!studentList.isEmpty()) {
            for (Map.Entry<Long, ArrayList<StudentMessDetails>> studentMap : studentList.entrySet()) {
                Long messId = studentMap.getKey();
                addLog(tag, "Mess: " + messId, record);
                addLog(tag, "Student Count: " + studentMap.getValue().size(), record);
                List<MessTerminal> terminalList = messTerminalDao.getMessTerminalList(messId);
                if (terminalList != null && !terminalList.isEmpty()) {
                    ArrayList<UserFpCard> userFpCardsList = new ArrayList<>();
                    for (StudentMessDetails studentDetails : studentMap.getValue()) {
                        Calendar expiryDate = Calendar.getInstance();
                        LocalDate diningToDate = studentDetails.getChangeToDate();
                        Date date = Date.from(diningToDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        expiryDate.setTime(date);
                        UserFpCard userFpCard = userFpCardDao.getUserByUserId(studentDetails.getStudentId());
                        userFpCard.setExpiryDate(expiryDate);
                        if (userFpCard != null) {
                            userFpCardsList.add(userFpCard);
                        }
                    }
                    addLog(tag, "Student Facial Count: " + userFpCardsList.size(), record);

                    Map<String, Integer> studentUpdateCount = new HashMap<>();
                    if (userFpCardsList.size()>0 && !userFpCardsList.isEmpty()) {
                        for (MessTerminal messTerminal : terminalList) {
                            List<String> updatedList = biometricService.pushOrRemoveUserListByIp(tag, toRemove, userFpCardsList, messTerminal, record);
                            for (String updatedStudentId : updatedList) {
                                Integer count = studentUpdateCount.get(updatedStudentId);
                                if (count == null) count = 0;
                                studentUpdateCount.put(updatedStudentId, ++count);
                            }
                        }
                        int count = 0;
                        for (Map.Entry<String, Integer> studentMapEntry : studentUpdateCount.entrySet()) {
                            if (studentMapEntry.getValue() == terminalList.size()) {
                                String result = studentMessDetailsDao.updatePushStatus(studentMapEntry.getKey(),
                                        toRemove ? "Removed" : "Pushed", toRemove ? FrConstant.TO_BE_REMOVED : FrConstant.TO_BE_PUSHED,messPeriod);
                                addLog(tag, " Status " + result + " for " + studentMapEntry.getKey(), record);
                                if (result != null && result.equals("updated")) {
                                    count++;
                                }
                            }
                        }
                        addLog(tag, "Count of students updated: " + count, record);
                    } else {
                        addLog(tag, "No Users with Facial data", record);
                    }
                } else {
                    addLog(tag, "No FR Terminal available for this Mess", record);
                }
            }
        }
    }

    public void saveLogs(String tag, List<DeviceFRLogs> logsList) {
        Map<String, MessMaster> messMap = new HashMap<>();
        Map<String, String> studentMap = new HashMap<>();
        logsList.forEach(log -> {
            MessMaster messMaster = messMap.get(log.getDeviceIp());
            if (messMaster == null) {
                MessTerminal messTerminal = messTerminalDao.getMessTerminalByIP(log.getDeviceIp());
                messMaster = messMasterDao.getMessMasterById(messTerminal.getMessMasterId());
                if (messMaster != null) {
                    messMap.put(log.getDeviceIp(), messMaster);
                }
            }
            String studentId = studentMap.get(log.getUserId());
            if (studentId == null) {
                UserListForm user = userFpService.getUserBySerial(log.getUserId(), false);
                if (user != null) {
                    studentId = user.getUserId();
                    studentMap.put(log.getUserId(), studentId);
                }
            }
            if (messMaster != null) {
                log.setMessId(messMaster.getMessMasterId());
                log.setMessName(messMaster.getMessName());
            }
            log.setStudentId(studentId);
        });
        int count = deviceFRLogsDao.saveLogs(logsList);
        addLog(tag, "Logs saved: " + count);
    }

    private void addLog(String tag, String msg) {
        addLog(tag, msg, false);
    }

    private void addLog(String tag, String msg, boolean record) {
        logService.addLog(tag, " <--> " + msg);
        if (record) {
            schedulerLogService.addLog(tag, msg);
        }
    }

    public MessDetailsCountForm getMessDetailCounts() {
        MessDetailsCountForm messDetailsCountForm = new MessDetailsCountForm();
        ArrayList<MessDetailsCountForm> messDetailList = new ArrayList<>();
        MessMasterControllerDto currentMessSession = messMasterControllerDao.getCurrentMessMasterController();
        List<MessMaster> masterList = messMasterDao.getMessMasterList();
        for (MessMaster messMaster : masterList) {
            MessDetailsCountForm messCountForm = getMessDetailCountByMess(messMaster.getMessMasterId());
            if (messCountForm != null) {
                messCountForm.setMessId(messMaster.getMessMasterId());
                messCountForm.setMessName(messMaster.getMessName());
                messDetailList.add(messCountForm);
            }
        }
        messDetailList.sort(Comparator.comparing(MessDetailsCountForm::getMessName));
        messDetailsCountForm.setMessList(messDetailList);
        return messDetailsCountForm;
    }

    public MessDetailsCountForm getMessDetailCountByMess(Long messId) {
        //Pushed count, to be pushed, to be removed
        MessDetailsCountForm messDetailsCountForm = new MessDetailsCountForm();
        MessMasterControllerDto currentMessSession = messMasterControllerDao.getCurrentMessMasterController();
        if (currentMessSession != null) {
            List<Object[]> resultList = studentMessDetailsDao.getCurrentStudentMessDetailsWithUserByMessId(messId);
            int pushedCount = 0;
            int pushedTodayCount = 0;
            int toBePushedTodayCount = 0;
            int totalToBePushedCount = 0;
            int toBeRemovedTodayCount = 0;
            int totalToBeRemovedCount = 0;
            int noFacialData = 0;
            if (resultList != null && !resultList.isEmpty()) {
                messDetailsCountForm.setTotalStudentCount(resultList.size());
                for (Object[] row : resultList) {
                    StudentMessDetails studentMessDetails = (StudentMessDetails) row[0];
                    UserFpCard user = (UserFpCard) row[1];
                    if (studentMessDetails.getPushDate() != null) pushedCount++;
                    if (checkDateIsToday(studentMessDetails.getPushDate())) pushedTodayCount++;
                    if (checkDateIsToday(studentMessDetails.getToPushDate())) toBePushedTodayCount++;
                    if (studentMessDetails.getToPushDate() != null) totalToBePushedCount++;
                    if (checkDateIsToday(studentMessDetails.getToRemoveDate())) toBeRemovedTodayCount++;
                    if (studentMessDetails.getToRemoveDate() != null) totalToBeRemovedCount++;
                    if (user != null && user.getFacial()!=null && user.getFacial().equals("true")) noFacialData++;
                }
            }
            messDetailsCountForm.setTotalPushedCount(pushedCount);
            messDetailsCountForm.setPushedTodayCount(pushedTodayCount);
            messDetailsCountForm.setToBePushedTodayCount(toBePushedTodayCount);
            messDetailsCountForm.setTotalToBePushedCount(totalToBePushedCount);
            messDetailsCountForm.setToBeRemovedTodayCount(toBeRemovedTodayCount);
            messDetailsCountForm.setTotalToBeRemovedCount(totalToBeRemovedCount);
            messDetailsCountForm.setNoFacialData(noFacialData);
        }
        return messDetailsCountForm;
    }

    private boolean checkDateIsToday(Date dateToCheck) {
        if (dateToCheck == null) return false;
        Date today = new Date();
        int diff = today.compareTo(dateToCheck);
        return diff == 0;
    }

    public String pushDataByMess(String tag, FrForm form) {
        if (form.getSerialNumber() != null && !form.getSerialNumber().isEmpty()) {
            form.setSerialNumberHidden(form.getSerialNumber());
        }
        if (form.getSerialNumberHidden() == null || form.getSerialNumberHidden().isEmpty()) {
            return "Serial No is empty.";
        } else {
            UserFpCard userFpCard = userFpCardDao.getUserByAccessSerialNo(form.getSerialNumberHidden());
            if (userFpCard != null) {
                MessMaster messMaster = studentMessDetailsDao.getMessDetailsByStudentId(userFpCard.getUserId());
                if (messMaster != null) {
                    List<MessTerminal> messTerminalList = messTerminalDao.getMessTerminalList(messMaster.getMessMasterId());
                    if (!messTerminalList.isEmpty()) {
                        Calendar expiryDate = Calendar.getInstance();
                        if (form.isMessPeriodAsExpiry()) {
                            MessMasterControllerDto currentMessSession = messMasterControllerDao.getCurrentMessMasterController();
                            Date date = Date.from(currentMessSession.getDiningToDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                            expiryDate.setTime(date);
                        } else if (form.getExpiryDate() != null) {
                            Date date = Date.from(form.getExpiryDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                            expiryDate.setTime(date);
                        }
                        for (MessTerminal messTerminal: messTerminalList) {
                            biometricService.pushDataBySerialAndIp(tag, userFpCard, messTerminal.getTerminalIp(), expiryDate);
                        }
                    } else {
                        return "No Terminals configured for the User's Mess " + messMaster.getMessName() + '.';
                    }
                } else {
                    return "Mess not found for User " + userFpCard.getUserId() + " not found.";
                }
            } else {
                return"Serial No not found.";
            }
        }
        return "Request is complete. Check the logs at the bottom for further details.";
    }

    public List<SchedulerLogDto> getSchedulerLogs() {
        List<SchedulerLogDto> schedulerLogs = schedulerLogService.getLogByDate(LocalDateTime.now());
        return schedulerLogs.stream().sorted(Comparator.comparing(SchedulerLogDto::getCreatedAt).reversed()).collect(Collectors.toList());
    }

    @Autowired
    public void setMessMasterDao(MessMasterDao messMasterDao) {
        this.messMasterDao = messMasterDao;
    }

    @Autowired
    public void setMessTerminalDao(MessTerminalDao messTerminalDao) {
        this.messTerminalDao = messTerminalDao;
    }

    @Autowired
    public void setUserFpService(UserFpService userFpService) {
        this.userFpService = userFpService;
    }

    @Autowired
    public void setLogService(InMemoryLogService logService) {
        this.logService = logService;
    }

    @Autowired
    public void setBiometricService(BiometricService biometricService) {
        this.biometricService = biometricService;
    }

    @Autowired
    public void setStudentMessDetailsDao(StudentMessDetailsDao studentMessDetailsDao) {
        this.studentMessDetailsDao = studentMessDetailsDao;
    }

    @Autowired
    public void setUserFpCardDao(UserFpCardDao userFpCardDao) {
        this.userFpCardDao = userFpCardDao;
    }

    @Autowired
    public void setDeviceFRLogsDao(DeviceFRLogsDao deviceFRLogsDao) {
        this.deviceFRLogsDao = deviceFRLogsDao;
    }

    @Autowired
    public void setVacatingRequestDao(VacatingRequestDao vacatingRequestDao) {
        this.vacatingRequestDao = vacatingRequestDao;
    }

    @Autowired
    public void setIitAMessRebateDao(IitAMessRebateDao iitAMessRebateDao) {
        this.iitAMessRebateDao = iitAMessRebateDao;
    }

    @Autowired
    public void setMessMasterControllerDao(MessMasterControllerDao messMasterControllerDao) {
        this.messMasterControllerDao = messMasterControllerDao;
    }

    @Autowired
    public void setSchedulerLogService(SchedulerLogService schedulerLogService) {
        this.schedulerLogService = schedulerLogService;
    }

    @Autowired
    public void setMessMasterCommonService(MessMasterCommonService messMasterCommonService) {
        this.messMasterCommonService = messMasterCommonService;
    }
}
