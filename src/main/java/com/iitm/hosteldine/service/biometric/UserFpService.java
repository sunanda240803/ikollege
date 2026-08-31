package com.iitm.hosteldine.service.biometric;

import com.hectrix.www.ACTAtek_xsd.Facial;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.biometric.FrConstant;
import com.iitm.hosteldine.controller.mess.MessMasterController;
import com.iitm.hosteldine.dao.biometric.MessMasterControllerDao;
import com.iitm.hosteldine.dao.biometric.StudentMessDetailsDao;
import com.iitm.hosteldine.dao.biometric.UserFpCardDao;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.form.biometric.FrForm;
import com.iitm.hosteldine.form.biometric.UserListForm;
import com.iitm.hosteldine.model.biometric.MessTerminal;
import com.iitm.hosteldine.model.biometric.UserFpCard;
import com.iitm.hosteldine.model.mess.MessMasterControllerEntity;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UserFpService {

    private UserFpCardDao userFpCardDao;
    private StudentMessDetailsDao studentMessDetailsDao;
    private BiometricService biometricService;
    private InMemoryLogService logService;
    private MessMasterControllerDao messMasterControllerDao;

    public List<UserListForm> getUserList(FrForm form) {
        String idPart = null, serialPart = null;
        if (form.getRollNo() != null && !form.getRollNo().isEmpty()) {
            idPart = form.getRollNo();
        }
        if (form.getSerialNumber() != null && !form.getSerialNumber().isEmpty()) {
            form.setSerialNumberHidden(form.getSerialNumber());
        }
        if (form.getSerialNumberHidden() != null && !form.getSerialNumberHidden().isEmpty()) {
            serialPart = form.getSerialNumberHidden();
        }
        return userFpCardDao.getUserFpList(idPart, serialPart, true);
    }

    public UserListForm getUserBySerial(String serialNo, boolean withFacial) {
        List<UserListForm> userList = userFpCardDao.getUserFpList(null, serialNo, withFacial);
        if (userList != null && !userList.isEmpty()) {
            return userList.get(0);
        }
        return null;
    }

    @Autowired
    public void setUserFpCardDao(UserFpCardDao userFpCardDao) {
        this.userFpCardDao = userFpCardDao;
    }

    public void pushData(String tag, FrForm form) {
        String pushIp = form.getPushIp();
        boolean pushData = true;
        if (pushIp == null || pushIp.isEmpty()) {
            pushData = false;
            logService.addLog(tag, "Push IP is empty.");
        } else if (pushIp.split("\\.").length != 4) {
            pushData = false;
            logService.addLog(tag, "Push IP is incorrect.");
        }
        if (form.getSerialNumber() != null && !form.getSerialNumber().isEmpty()) {
            form.setSerialNumberHidden(form.getSerialNumber());
        }
        if (form.getSerialNumberHidden() == null || form.getSerialNumberHidden().isEmpty()) {
            pushData = false;
            logService.addLog(tag, "Serial No is empty.");
        }
        if (pushData) {
            UserFpCard userFpCard = userFpCardDao.getUserByAccessSerialNo(form.getSerialNumberHidden());
            if (userFpCard != null) {
                Calendar expiryDate = Calendar.getInstance();
                if (form.isMessPeriodAsExpiry()) {
                    MessMasterControllerDto currentMessSession = messMasterControllerDao.getCurrentMessMasterController();
                    Date date = Date.from(currentMessSession.getDiningToDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    expiryDate.setTime(date);
                } else if (form.getExpiryDate() != null) {
                    Date date = Date.from(form.getExpiryDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    expiryDate.setTime(date);
                }
                biometricService.pushDataBySerialAndIp("pushData", userFpCard, pushIp, expiryDate);
            } else {
                logService.addLog(tag, "Serial No not found.");
            }
        }
    }

    public void pullData(String tag, FrForm form) {
        String pullIp = form.getPullIp();
        boolean pushData = true;

        if (pullIp == null || pullIp.isEmpty()) {
            pushData = false;
            logService.addLog(tag, "Pull IP is empty.");
        } else if (pullIp.split("\\.").length != 4) {
            pushData = false;
            logService.addLog(tag, "Pull IP is incorrect.");
        }
        if (form.getSerialNumber() != null && !form.getSerialNumber().isEmpty()) {
            form.setSerialNumberHidden(form.getSerialNumber());
        }
        if (form.getSerialNumberHidden() == null || form.getSerialNumberHidden().isEmpty()) {
            pushData = false;
            logService.addLog(tag, "Serial No is empty.");
        }
        if (pushData) {
            UserFpCard userFpCard = userFpCardDao.getUserByAccessSerialNo(form.getSerialNumberHidden());
            if (userFpCard != null) {
                biometricService.pullFromDeviceById(tag, userFpCard, pullIp);
            } else {
                logService.addLog(tag, "Serial No not found.");
            }
        }
    }

    public void pushDataByMessAndIp(String tag, Long messId, List<MessTerminal> messTerminals) {
        List<UserFpCard> userFpCardList = studentMessDetailsDao.getStudentFPDetailsByMessId(messId);
        MessMasterControllerDto currentMessSession = messMasterControllerDao.getCurrentMessMasterController();
        Calendar expiryDate = DateUtility.localDateToCalendar(currentMessSession.getDiningToDate());
        if (userFpCardList != null && !userFpCardList.isEmpty()) {
            for (MessTerminal messTerminal : messTerminals) {
                Long biometricSession = biometricService.getNewLogin(tag, messTerminal, false);
                int count = 0;
                List<String> noFacialData = new ArrayList<>();
                for (UserFpCard userFpCard : userFpCardList) {
                    String result = biometricService.pushBulkDataByUser(tag, userFpCard, biometricSession, expiryDate);
                    if (result != null && result.equals(userFpCard.getAccessCardSerialNo())) {
                        result = studentMessDetailsDao.updatePushStatus(userFpCard.getUserId(), "Pushed", FrConstant.TO_BE_PUSHED,currentMessSession);
                        logService.addLog(tag, " Status " + result + " for " + userFpCard.getUserId());
                        if (result != null && result.equals("updated")) {
                            count++;
                        }
                    } else if (result != null && result.equals("Facial Data not available")) {
                        noFacialData.add(userFpCard.getUserId());
                    }
                }
                logService.addLog(tag + "Count", messId + " : " + messTerminal + ":" + count);
                if (!noFacialData.isEmpty()) {
                    logService.addLog(tag + "Missing Facial Records", messId + " : " + noFacialData);
                }
                biometricService.logoutBySession(tag, biometricSession, false);
            }
        } else {
            logService.addLog(tag, " <--> " + "No User with Facial data found for mess Id " + messId);
        }
    }

    @Transactional
    public String updateCardStatus(String studentId, String status) {
        return userFpCardDao.updateCardStatus(studentId, status);
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
    public void setLogService(InMemoryLogService logService) {
        this.logService = logService;
    }

    @Autowired public void setMessMasterControllerDao(MessMasterControllerDao messMasterControllerDao) {
        this.messMasterControllerDao = messMasterControllerDao;
    }
}
