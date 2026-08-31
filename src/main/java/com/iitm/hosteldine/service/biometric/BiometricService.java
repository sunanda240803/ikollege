package com.iitm.hosteldine.service.biometric;

import com.hectrix.www.ACTAtek_service.ACTAtekLocator;
import com.hectrix.www.ACTAtek_service.ACTAtekPortType;
import com.hectrix.www.ACTAtek_xsd.*;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dao.biometric.MessMasterControllerDao;
import com.iitm.hosteldine.dao.biometric.MessTerminalDao;
import com.iitm.hosteldine.dao.biometric.UserFpCardDao;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.model.biometric.DeviceFRLogs;
import com.iitm.hosteldine.model.biometric.MessTerminal;
import com.iitm.hosteldine.model.biometric.UserFpCard;
import com.iitm.hosteldine.repository.biometric.DeviceFRLogsRepository;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.service.SchedulerLogService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.AxisFault;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.iitm.hosteldine.service.SimsConfigDataService.BIOMETRIC_ADMIN_PASSWORD;
import static com.iitm.hosteldine.service.SimsConfigDataService.BIOMETRIC_ADMIN_USERNAME;

@Service
@Slf4j
public class BiometricService {

    private MessTerminalDao messTerminalDao;
    private InMemoryLogService logService;
    private final Map<Long, ACTAtekPortType> locatorMap = new HashMap<>();
    private final Map<Long, String> adminUser = new HashMap<>();
    @Value("${delete.iot.logs}")
    String deleteLogsDuration;
    private final Map<Long, Boolean> syncForceStop = new HashMap<>();
    private MessMasterControllerDao messMasterControllerDao;
    private UserFpCardDao userFpCardDao;
    private SimsConfigDataService simsConfigDataService;
    private CommonResponseUtil commonResponseUtil;
    private DeviceFRLogsRepository deviceFRLogsRepository;
    private SchedulerLogService schedulerLogService;

    public void forceStopSync(Long messId) {
        syncForceStop.put(messId, true);
    }

    public void pullData(String tag, String ip, boolean deleteUser) {
        log.info(ip);
        long sessionID = 0;
        ACTAtekPortType api = null;
        try {
            api = getApi(ip);
            sessionID = performLogin(api, tag, ip, false);
            addLog(tag, sessionID, "Attempting User List fetch");
            GetUsersCriteria usersCriteria = new GetUsersCriteria();
            usersCriteria.setDepartmentID(0);
            ActatekUser[] actatekUsers = api.getUsers(sessionID, usersCriteria);
            if (actatekUsers != null) {
                addLog(tag, sessionID, "User List fetched with size: " + actatekUsers.length);
                for (int i = 0; i < actatekUsers.length; i++) {
                    ActatekUser actatekUser = actatekUsers[i];
                    addLog(tag, sessionID, "User: " + i + ": " + actatekUser.getUserID());
                    if (actatekUser.getFacialrecords() != null) {
                        addLog(tag, sessionID, "Facial Record: " + actatekUser.getFacialrecords().length);
                    }
                    String updateResult = updateUser(api, actatekUser, tag, sessionID, deleteUser);
                    addLog(tag, sessionID, "Update status for " + actatekUser.getUserID() + ": " + updateResult);

                }
            } else {
                addLog(tag, sessionID, "No Users Found");
            }
        } catch (MalformedURLException | RemoteException | ServiceException e) {
            addError(tag, "At pull", e);
        } catch (Exception e) {
            addError(tag, "Unknown At pull", e);
        } finally {
            logout(api, tag, sessionID, false);
        }
        logService.addLog(tag, tag + " Request complete.");
    }

    private ActatekUser getEmptyUser() {
        Status status = new Status();
        status.setActive(true);
        status.setFacial(true);
        status.setFacialAutoMatch(true);

        ActatekUser user = new ActatekUser();
        user.setAdminLevel(AdminLevel.PERSONALUSER);
        user.setFingerprintSecurityLevel(FingerprintSecurityLevel.NORMAL);
        user.setLastName("");
        user.setFirstName("");
        user.setOtherName("");
        user.setUsePassword(false);
        user.setPassword("");
        user.setGroupID(new int[]{0});
        user.setDepartmentID(new int[]{0});
        user.setStatus(status);
        user.setCardsn("");
        user.setFpgroupid(0);
        user.setScoreThreshold(0);
        user.setMessage("");
        user.setExpirydate(Calendar.getInstance());
        return user;
    }

    public List<String> pushOrRemoveUserListByIp(String tag, boolean toRemove, List<UserFpCard> list, MessTerminal deviceIp, boolean record) {
        List<String> updatedList = new ArrayList<>();
        long sessionID = 0;
        ACTAtekPortType api = null;
        try {
            api = getApi(deviceIp.getTerminalIp());
            sessionID = performLogin(api, tag, deviceIp.getTerminalIp(), deviceIp.getUserName(), deviceIp.getPassword(), record);
            for (UserFpCard userFpCard : list) {
                if (toRemove) {
                    GetUsersCriteria usersCriteria = new GetUsersCriteria();
                    usersCriteria.setUserID(userFpCard.getAccessCardSerialNo());
                    ActatekUser[] users = api.getUsers(sessionID, usersCriteria);
                    if (users != null) {
                        for (ActatekUser actatekUser : users) {
                            if (actatekUser.getUserID().equals(userFpCard.getAccessCardSerialNo())) {
                                String result = deleteUser(tag, sessionID, api, actatekUser);
                                if (result == null) {
                                    updatedList.add(userFpCard.getUserId());
                                }
                            }
                        }
                    } else {
                        addLog(tag, sessionID, userFpCard.getUserId() + " already removed", record);
                        updatedList.add(userFpCard.getUserId());
                    }
                } else {
                    String result = pushUserAfterLogin(userFpCard, api, tag, sessionID, userFpCard.getExpiryDate());
                    if (result.equals(userFpCard.getAccessCardSerialNo())) {
                        updatedList.add(userFpCard.getUserId());
                    }
                }
            }
        } catch (MalformedURLException | RemoteException | ServiceException e) {
            addError(tag, "At push by Id", e, record);
        } catch (Exception e) {
            addLog(tag, sessionID, "Unknown Error: " + e.getMessage(), record);
            addError(tag, "Unknown At push by Id", e, record);
        } finally {
            logout(api, tag, sessionID, record);
        }
        return updatedList;
    }

    public void pullFromDeviceById(String tag, UserFpCard userFpCard, String pullIp) {
        long sessionID = 0;
        ACTAtekPortType api = null;
        try {
            api = getApi(pullIp);
            sessionID = performLogin(api, tag, pullIp, false);
            GetUsersCriteria usersCriteria = new GetUsersCriteria();
            usersCriteria.setDepartmentID(0);
            ActatekUser[] users = api.getUsers(sessionID, usersCriteria);
            if (users != null) {
                for (ActatekUser actatekUser : users) {
                    if (actatekUser.getUserID().equals(userFpCard.getAccessCardSerialNo())) {
                        updateUser(api, actatekUser, tag, sessionID, false);
                    }
                }
            }
        } catch (MalformedURLException | RemoteException | ServiceException e) {
            addError(tag, "At push by Id", e);
        } catch (Exception e) {
            addLog(tag, sessionID, "Unknown Error: " + e.getMessage());
            addError(tag, "Unknown At push by Id", e);
        } finally {
            logout(api, tag, sessionID, false);
        }
    }

    private String updateUser(ACTAtekPortType api, ActatekUser actatekUser, String tag, Long sessionID, boolean deleteUser) {
        String updateResult = updateFacialData(actatekUser.getUserID(), actatekUser);
        addLog(tag, sessionID, "Update status for " + actatekUser.getUserID() + ": " + updateResult);
        if ((adminUser.get(sessionID) == null || adminUser.get(sessionID).equals(actatekUser.getUserID())) && deleteUser) {
            deleteUser(tag, sessionID, api, actatekUser);
        }
        return updateResult;
    }

    private String deleteUser(String tag, Long sessionID, ACTAtekPortType api, ActatekUser actatekUser) {
        addLog(tag, sessionID, "Attempting User Delete");
        String result = null;
        try {
            result = api.deleteUser(sessionID, actatekUser.getUserID());
        } catch (RemoteException e) {
            addError(tag, "Error Deleting user.", e);
            addLog(tag, sessionID, "Error Deleting User : " + e.getMessage());
        }
        addLog(tag, sessionID, "User Delete result: " + (result == null ? "Success" : result) + " for " + actatekUser.getFirstName() );
        return result;
    }

    public void pushDataBySerialAndIp(String tag, UserFpCard userFpCard, String pushIp, Calendar expiryDate) {
        long sessionID = 0;
        ACTAtekPortType api = null;
        try {
            api = getApi(pushIp);
            sessionID = performLogin(api, tag, pushIp, false);
            pushUserAfterLogin(userFpCard, api, tag, sessionID, expiryDate);
        } catch (AxisFault e) {
            logService.addLog(tag, "Unable to connect to the IP " + pushIp);
        }catch (MalformedURLException | RemoteException | ServiceException e) {
            addError(tag, "At push by IP " + pushIp, e);
        } catch (Exception e) {
            addError(tag, "Unknown At push by IP " + pushIp, e);
        } finally {
            logout(api, tag, sessionID, false);
        }
    }

    private String pushUserAfterLogin(UserFpCard userFpCard, ACTAtekPortType api, String tag, Long sessionID, Calendar expiryDate) {
        String result = null;
        if (userFpCard != null && userFpCard.getFacialTemplate() != null && userFpCard.getFacialPhoto() != null && userFpCard.getFacialTemplateType() != null) {
            Facial facial = new Facial();
            facial.setFacialPhoto(userFpCard.getFacialPhoto());
            facial.setFacialTemplate(userFpCard.getFacialTemplate());
            facial.setFacialTemplateType(userFpCard.getFacialTemplateType());

            ActatekUser actatekUser = getEmptyUser();
            actatekUser.setUserID(userFpCard.getAccessCardSerialNo());
            actatekUser.setFacialrecords(new Facial[]{facial});
            actatekUser.setFirstName(userFpCard.getUserId());
            actatekUser.setExpirydate(Calendar.getInstance());
            actatekUser.getExpirydate().setTimeInMillis(expiryDate.getTimeInMillis() +
                    expiryDate.getTimeZone().getOffset(expiryDate.getTimeInMillis()));
            addLog(tag, sessionID, "Attempting User " + actatekUser.getFirstName() + " Push with expiry as " + actatekUser.getExpirydate().getTime());
            try {
                result = api.addUser(sessionID, actatekUser);
                if (result == null) {
                    result = userFpCard.getAccessCardSerialNo();
                }
            } catch (RemoteException e) {
                addError(tag, "At push", e);
            }
            addLog(tag, sessionID, "User Push Status: " + result + " for " + userFpCard.getUserId());
        } else {
            result = "Facial Data not available";
            addLog(tag, sessionID, "User Push Status: " + result + " for " + (userFpCard != null ? userFpCard.getUserId() : ""));
        }
        return result;
    }

    public String pushBulkDataByUser(String tag, UserFpCard userFpCard, Long sessionId, Calendar expiryDate) {
        ACTAtekPortType api = locatorMap.get(sessionId);
        String result = null;
        if (api != null) {
            addLog(tag, sessionId, "Session Retrieved");
            result = pushUserAfterLogin(userFpCard, api, tag, sessionId, expiryDate);
        }
        return result;
    }

    public List<DeviceFRLogs> getLogsByIp(String tag, MessTerminal terminalIp, String fromDate, String toDate, boolean record) {
        List<DeviceFRLogs> logsList = new ArrayList<>();
        try {
            getLogCriteria(fromDate, toDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Long sessionID = getNewLogin(tag, terminalIp, record);
        if (sessionID != null && sessionID > 0) {
            try {
                ACTAtekPortType api = locatorMap.get(sessionID);
                GetLogsCriteria logsCriteria = getLogCriteria(fromDate, toDate);
                addLog(tag, sessionID, "Retrieving logs of range " + new Timestamp(logsCriteria.getFrom().getTimeInMillis())
                        + " - " + new Timestamp(logsCriteria.getTo().getTimeInMillis()), record);
                Log[] logs = api.getLogs(sessionID, logsCriteria);
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                if (logs != null) {
                    for (Log log : logs) {
                        DeviceFRLogs deviceFRLogs = new DeviceFRLogs();
                        deviceFRLogs.setFetchDate(timestamp);
                        deviceFRLogs.setDeviceIp(terminalIp.getTerminalIp());
                        deviceFRLogs.setLogId(log.getLogID());
                        deviceFRLogs.setUserId(log.getUserID());
                        deviceFRLogs.setLogTimestamp(new Timestamp(log.getTimestamp().getTimeInMillis()));
                        deviceFRLogs.setEvent(log.getTrigger().toString());
                        logsList.add(deviceFRLogs);
                    }
                } else {
                    addLog(tag, sessionID, "No Logs available", record);
                }
            } catch (RemoteException | ParseException e) {
                addError(tag, "Exception At getLogsByIp", e, record);
            } finally {
                logoutBySession(tag, sessionID, record);
            }
        }
        return logsList;
    }

    public void deleteLogsByIp(String tag, MessTerminal terminalIp, boolean record) {
        Long sessionID = getNewLogin(tag, terminalIp, record);
        if (sessionID != null && sessionID > 0) {
            try {
                ACTAtekPortType api = locatorMap.get(sessionID);
                int mDeleteLogsDuration = deleteLogsDuration != null ? Integer.parseInt(deleteLogsDuration) : 30;
                String result = api.deleteLogs(sessionID, mDeleteLogsDuration);
                addLog(tag, sessionID, "Delete Logs status: " + result, record);
            } catch (RemoteException e) {
                addError(tag, "RemoteException At deleteLogsByIp", e, record);
            } finally {
                logoutBySession(tag, sessionID, record);
            }
        }
    }

    public void clearDataByIp(String tag, String terminalIp) {
        MessTerminal messTerminal = messTerminalDao.getMessTerminalByIP(terminalIp);
        if (messTerminal != null && messTerminal.getTerminalIp().length() > 7) {
            Long sessionID = getNewLogin(tag, messTerminal, false);
            if (sessionID != null && sessionID > 0) {
                try {
                    ACTAtekPortType api = locatorMap.get(sessionID);
                    GetUsersCriteria usersCriteria = new GetUsersCriteria();
                    usersCriteria.setDepartmentID(0);
                    ActatekUser[] users = api.getUsers(sessionID, usersCriteria);
                    if (users != null) {
                        addLog(tag, sessionID, "Deleting  " + users.length + " user(s)");
                        for (ActatekUser actatekUser : users) {
                            deleteUser(tag, sessionID, api, actatekUser);
                        }
                    }
                } catch (RemoteException e) {
                    addError(tag, "RemoteException At deleteLogsByIp", e);
                } finally {
                    logoutBySession(tag, sessionID, false);
                }
            }
        } else {
            addLog(tag, 0, "Invalid IP>" + terminalIp + "<");
        }
        logService.addLog(tag, tag + " Request Complete");
    }

    public void syncData(String tag, Long messMasterId, MessTerminal terminalIp, List<String> studentIds) {
        if (terminalIp.getTerminalIp() != null && terminalIp.getTerminalIp().length() > 7) {
            addLog(tag, 0, "Attempting new Login for " + terminalIp);
            Long sessionID = getNewLogin(tag, terminalIp, false);
            if (syncForceStop.get(messMasterId) == null || !syncForceStop.get(messMasterId)) {
                if (sessionID != null && sessionID > 0) {
                    try {
                        ACTAtekPortType api = locatorMap.get(sessionID);
                        GetUsersCriteria usersCriteria = new GetUsersCriteria();
                        usersCriteria.setDepartmentID(0);
                        addLog(tag, sessionID, "Fetching User list from device...");
                        ActatekUser[] users = api.getUsers(sessionID, usersCriteria);
                        if (users != null) {
                            addLog(tag, sessionID, "Deleting  " + users.length + " user(s)" + (studentIds != null ? " with condition." : ""));
                            for (ActatekUser actatekUser : users) {
                                if (syncForceStop.get(messMasterId) != null && syncForceStop.get(messMasterId)) break;
                                boolean containsStud = studentIds == null || studentIds.contains(actatekUser.getFirstName());
                                if (!containsStud) {
                                    deleteUser(tag, sessionID, api, actatekUser);
                                } else if (studentIds != null) {
                                    studentIds.remove(actatekUser.getFirstName());
                                }
                            }
                            if (studentIds != null && !studentIds.isEmpty()) {
                                addLog(tag, sessionID, "Adding missing students: " + studentIds.size() + " student(s)");
                                List<UserFpCard> userList = new ArrayList<>();
                                studentIds.forEach(studentId -> userList.add(getUserFpCardByStudentId(studentId)));
                                pushListByStudentId(tag, userList, sessionID, api);
                            }
                        }
                    } catch (RemoteException e) {
                        addError(tag, "RemoteException At deleteLogsByIp", e);
                    } finally {
                        logoutBySession(tag, sessionID, false);
                    }
                }
            }
        } else {
            addLog(tag, 0, "Invalid IP>" + terminalIp + "<");
        }
        if (syncForceStop.get(messMasterId) != null && syncForceStop.get(messMasterId)) {
            addLog(tag, 0, "Syncing force stopped at user request");
            syncForceStop.remove(messMasterId);
        }
    }

    private void pushListByStudentId(String tag, List<UserFpCard> userList, Long sessionID, ACTAtekPortType api) {
        MessMasterControllerDto currentMessSession = messMasterControllerDao.getCurrentMessMasterController();
        Calendar expiryDate = Calendar.getInstance();
        LocalDate diningToDate = currentMessSession.getDiningToDate();
        Date date = Date.from(diningToDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        expiryDate.setTime(date);
        userList.forEach(user -> pushBulkDataByUser(tag, user, sessionID, expiryDate));
    }

    public String updateFacialData(String serialNo, ActatekUser actatekUser) {
        log.info("Entering updateFacialData");
        String result = null;
        UserFpCard userFpCard = userFpCardDao.getUserByAccessSerialNo(serialNo);
        if (userFpCard != null) {
            boolean updateUser = false;
            if (actatekUser.getFacialrecords() != null && actatekUser.getFacialrecords().length == 1) {
                Facial facialRecord = actatekUser.getFacialrecords()[0];
                userFpCard.setFacialPhoto(facialRecord.getFacialPhoto());
                userFpCard.setFacialTemplate(facialRecord.getFacialTemplate());
                userFpCard.setFacialTemplateType(facialRecord.getFacialTemplateType());
                userFpCard.setFacial("true");
                userFpCard.setRegAccessModifiedDate(new Timestamp(System.currentTimeMillis()));
                updateUser = true;
            }
            if (actatekUser.getCardsn() != null && !Strings.isBlank(actatekUser.getCardsn())) {
                userFpCard.setCardSn(actatekUser.getCardsn());
                if (Strings.isEmpty(userFpCard.getPinNo())) {
                    userFpCard.setPinNo("1111");
                }
                updateUser = true;
            }
            if (updateUser) {
                result = userFpCardDao.updateFacialData(userFpCard);
            }
        } else {
            result = "Serial No not found.";
        }
        log.info("Exiting updateFacialData");
        return result;
    }

    public UserFpCard getUserFpCardByStudentId(String studentId) {
        return userFpCardDao.getUserByUserId(studentId);
    }

    public ActatekUser[] getUserListByIP(String tag, Long messId, MessTerminal terminalIp) {
        if (terminalIp.getTerminalIp() != null && terminalIp.getTerminalIp().length() > 7) {
            addLog(tag, 0, "Attempting new Login for " + terminalIp);
            Long sessionID = getNewLogin(tag, terminalIp, false);
            if (sessionID != null && sessionID > 0) {
                try {
                    ACTAtekPortType api = locatorMap.get(sessionID);
                    GetUsersCriteria usersCriteria = new GetUsersCriteria();
                    usersCriteria.setDepartmentID(0);
                    return api.getUsers(sessionID, usersCriteria);
                } catch (RemoteException e) {
                    addError(tag, "RemoteException At deleteLogsByIp", e);
                } finally {
                    logoutBySession(tag, sessionID, false);
                }
            }
        } else {
            addLog(tag, 0, "Invalid IP>" + terminalIp + "<");
        }
        return null;
    }

    private GetLogsCriteria getLogCriteria(String fromDateStr, String toDateStr) throws ParseException {
        GetLogsCriteria logsCriteria = new GetLogsCriteria();
        Calendar fromDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (fromDateStr != null) {
            fromDate.setTime(sdf.parse(fromDateStr));
        }
        Calendar toDate = Calendar.getInstance();
        if (toDateStr != null) {
            toDate.setTime(sdf.parse(toDateStr));
        } else if (fromDateStr != null) {
            toDate.setTime(fromDate.getTime());
        }
        if (fromDateStr == null && toDateStr != null) {
            fromDate.setTime(toDate.getTime());
        }
        if (fromDateStr == null && toDateStr == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            fromDate.setTimeInMillis(calendar.getTimeInMillis());
            fromDate.add(Calendar.DATE, -1);
            toDate.setTimeInMillis(fromDate.getTimeInMillis());
        }
        toDate.add(Calendar.DATE, +1);
        toDate.add(Calendar.MILLISECOND, -1);
        logsCriteria.setFrom(fromDate);
        logsCriteria.setTo(toDate);
        logsCriteria.setDepartmentID(0);
        return logsCriteria;
    }

    public Long getNewLogin(String tag, MessTerminal terminalIp, boolean record) {
        ACTAtekPortType api;
        long sessionID = 0;
        try {
            api = getApi(terminalIp.getTerminalIp());
            sessionID = performLogin(api, tag, terminalIp.getTerminalIp(), terminalIp.getUserName(), terminalIp.getPassword(), record);
            locatorMap.put(sessionID, api);
        } catch (MalformedURLException e) {
            addError(tag, "At MalformedURLException getNewLogin", e, record);
        } catch (ServiceException e) {
            addError(tag, "At ServerException getNewLogin", e, record);
        } catch (RemoteException e) {
            addError(tag, "At RemoteException getNewLogin", e, record);
        }
        return sessionID;
    }

    public void logoutBySession(String tag, Long sessionID, boolean record) {
        ACTAtekPortType api = locatorMap.get(sessionID);
        if (api != null) {
            logout(api, tag, sessionID, record);
            locatorMap.remove(sessionID);
        }
    }

    public Workbook generateDeviceFRLogsReport(String studentId, Long messId, LocalDate fromDate, LocalDate toDate, String report) throws Exception {
        XSSFWorkbook workbook;
        int colCount = 0;
        ExcelUtility excelUtility = new ExcelUtility(); // Initialize ExcelUtility

        String[] headerList = ModelConstants.DEVICE_FR_LOG_REPORT_HEADER;
        final int TOTAL_COLUMNS = headerList.length;
        try {
            workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet(commonResponseUtil.getMessage(report));
            Timestamp startTimestamp = Timestamp.valueOf(fromDate.atStartOfDay());
            Timestamp endTimestamp   = Timestamp.valueOf(toDate.atTime(LocalTime.MAX));
            List<DeviceFRLogs> deviceFRLogs = deviceFRLogsRepository.findDeviceFRLogs(startTimestamp, endTimestamp, studentId, messId);

            // Create styles using ExcelUtility
            XSSFCellStyle headerStyle = excelUtility.setHeaderStyle(workbook);
            headerStyle.setWrapText(true);
            XSSFCellStyle dataStyle = excelUtility.setDataStyle(workbook);
            dataStyle.setWrapText(true);

            XSSFCellStyle headerStyle2 = excelUtility.setHeaderStyle(workbook);
            headerStyle2.setWrapText(true);
            headerStyle2.setAlignment(HorizontalAlignment.LEFT);
            headerStyle2.setVerticalAlignment(VerticalAlignment.BOTTOM);

            // Create second header row
            XSSFRow rowheadFirst = sheet.createRow(1);
            excelUtility.createCell(rowheadFirst, 0, commonResponseUtil.getMessage("message.iitm.report.header"), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, TOTAL_COLUMNS - 1));

            // Create second header row
            XSSFRow rowheadSecond = sheet.createRow(2);
            excelUtility.createCell(rowheadSecond, 0, commonResponseUtil.getMessage(report), headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, TOTAL_COLUMNS - 1));

            // Create third header row for report date
            XSSFRow rowheadthird = sheet.createRow(3);
            SimpleDateFormat sdf = new SimpleDateFormat(commonResponseUtil.getMessage("session.date.format"));
            String reportDate = commonResponseUtil.getMessage("message.label.report.date.colon") + sdf.format(new Date());
            excelUtility.createCell(rowheadthird, 0, reportDate, headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, TOTAL_COLUMNS - 1));

            // Create column headers
            XSSFRow rowhead = sheet.createRow(4);

            // Add headers to the sheet
            for (String p : headerList) {
                excelUtility.createCell(rowhead, colCount, p, headerStyle2);
                sheet.setColumnWidth(colCount, 4000); // Set column width
                colCount++;
            }

            // Populate data rows
            int rowCount = 4;
            if (CollectionUtils.isNotEmpty(deviceFRLogs)) {
                int sNo = 0;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_TIME_FORMAT);
                for (DeviceFRLogs deviceFRLogs1 : deviceFRLogs) {
                    XSSFRow row = sheet.createRow(++rowCount);
                    String formattedLogTime = deviceFRLogs1.getLogTimestamp() != null ? deviceFRLogs1.getLogTimestamp().toLocalDateTime().format(formatter) : ModelConstants.EMPTY_STRING;
                    excelUtility.createCell(row, 0, ++sNo, dataStyle);
                    excelUtility.createCell(row, 1, deviceFRLogs1.getStudentId(), dataStyle);
                    excelUtility.createCell(row, 2, formattedLogTime, dataStyle);
                    excelUtility.createCell(row, 3, deviceFRLogs1.getMessName(), dataStyle);
                    excelUtility.createCell(row, 4, deviceFRLogs1.getUserId(), dataStyle);
                    excelUtility.createCell(row, 5, deviceFRLogs1.getDeviceIp(), dataStyle);
                }
            }

            sheet.setColumnWidth(0, 3000);
            sheet.setColumnWidth(1, 4000);
            sheet.setColumnWidth(2, 5000);
            sheet.setColumnWidth(3, 10000);
            sheet.setColumnWidth(4, 4000);
            sheet.setColumnWidth(5, 5000);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("Error in generating report", exception);
        }
        return workbook;
    }

    private long performLogin(ACTAtekPortType api, String tag, String ip, boolean record) throws RemoteException {
        return performLogin(api, tag, ip, null, null, record);
    }

    private long performLogin(ACTAtekPortType api, String tag, String ip, String username, String password, boolean record) throws RemoteException {
        addLog(tag, 0, "Attempting Login to " + ip, record);
        MessTerminal terminal = getAdminCredById(ip);
        if (terminal == null) {
            terminal = new MessTerminal();
            terminal.setUserName(username == null ? simsConfigDataService.getSimConfigValue(BIOMETRIC_ADMIN_USERNAME, Constants.BIOMETRIC_ADMIN_DEFAULT_USERNAME) : username);
            terminal.setPassword(password == null ? simsConfigDataService.getSimConfigValue(BIOMETRIC_ADMIN_PASSWORD, Constants.BIOMETRIC_ADMIN_DEFAULT_PASSWORD) : password);
        }
        long sessionId = api.login(terminal.getUserName(), terminal.getPassword());
        addLog(tag, sessionId, "Login success", record);
        adminUser.put(sessionId, terminal.getUserName());
        return sessionId;
    }

    private MessTerminal getAdminCredById(String ip) {
        return messTerminalDao.getMessTerminalByIP(ip);
    }

    private ACTAtekPortType getApi(String ip) throws MalformedURLException, ServiceException {
        ACTAtekLocator locator = new ACTAtekLocator();
        //noinspection HttpUrlsUsage
        return locator.getACTAtek(new URL("http://" + ip + "/cgi-bin/rpcrouter"));
    }

    private void logout(ACTAtekPortType api, String tag, long sessionID, boolean record) {
        if (api != null && sessionID > 0) {
            try {
                String logoutResult = api.logout(sessionID);
                addLog(tag, sessionID, "Logout Result: " + logoutResult, record);
            } catch (RemoteException e) {
                addError(tag, "RemoteException At session logout", e, record);
            }
        }
    }

    private void addLog(String tag, long sessionId, String msg) {
        addLog(tag, sessionId, msg, false);
    }
    private void addLog(String tag, long sessionId, String msg, boolean record) {
        logService.addLog(tag, " <-" + sessionId + "-> " + msg);
        if (record) {
            schedulerLogService.addLog(tag, msg);
        }
    }

    private void addError(String tag, String message, Exception e) {
        addError(tag, message, e, false);
    }
    private void addError(String tag, String message, Exception e, boolean record) {
        logService.addError(tag, message, e);
        if (record) {
            schedulerLogService.addLog(tag, "Error:" + message);
        }
    }

    @Autowired
    public void setLogService(InMemoryLogService logService) {
        this.logService = logService;
    }

    @Autowired
    public void setMessTerminalDao(MessTerminalDao messTerminalDao) {
        this.messTerminalDao = messTerminalDao;
    }

    @Autowired
    public void setMessMasterControllerDao(MessMasterControllerDao messMasterControllerDao) {
        this.messMasterControllerDao = messMasterControllerDao;
    }

    @Autowired
    public void setUserFpCardDao(UserFpCardDao userFpCardDao) {
        this.userFpCardDao = userFpCardDao;
    }

    @Autowired
    public void setSimsConfigDataService(SimsConfigDataService simsConfigDataService) {
        this.simsConfigDataService = simsConfigDataService;
    }

    @Autowired
    public void setCommonResponseUtil(CommonResponseUtil commonResponseUtil) {
        this.commonResponseUtil = commonResponseUtil;
    }

    @Autowired
    public void setDeviceFRLogsRepository(DeviceFRLogsRepository deviceFRLogsRepository) {
        this.deviceFRLogsRepository = deviceFRLogsRepository;
    }

    @Autowired
    public void setSchedulerLogService(SchedulerLogService schedulerLogService) {
        this.schedulerLogService = schedulerLogService;
    }
}
