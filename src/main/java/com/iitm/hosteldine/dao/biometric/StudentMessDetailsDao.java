package com.iitm.hosteldine.dao.biometric;

import com.iitm.hosteldine.constant.biometric.FrConstant;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.model.biometric.*;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.service.SchedulerLogService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Query;
import org.hibernate.Session;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.iitm.hosteldine.constant.biometric.FrConstant.TO_BE_PUSHED;

@SuppressWarnings("unchecked")
@Repository
@Transactional
@Slf4j
public class StudentMessDetailsDao {
    private EntityManager entityManager;
    private MessMasterControllerDao messMasterControllerDao;
    private InMemoryLogService logService;
    private SchedulerLogService schedulerLogService;

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    public List<UserFpCard> getStudentFPDetailsByMessId(Long messId) {
        log.info("Entering getStudentDetailsByMessId: {}", messId);
        List<UserFpCard> studentMessDetailsList = null;
        MessMasterControllerDto currentMessSession = messMasterControllerDao.getCurrentMessMasterController();
        Query query = getSession().createQuery("select ufc.accessCardSerialNo, ufc.userId, ufc.facial, " +
                "ufc.facialPhoto, ufc.facialTemplate, ufc.facialTemplateType " +
                "from UserFpCard ufc " +
                "join StudentMessDetails smd on smd.studentId = ufc.userId and smd.mmcId = :currentMessPeriodId " +
                "where smd.messId = :messId and smd.activeFlag = :activeFlag and ufc.facial = 'true' and smd.pushRemoveStatus = '" + TO_BE_PUSHED + "'");
        query.setParameter("messId", messId);
        query.setParameter("currentMessPeriodId", currentMessSession.getId());
        query.setParameter("activeFlag", "Y");
        List<Object[]> resultList = query.list();
        if (resultList != null && !resultList.isEmpty()) {
            studentMessDetailsList = new ArrayList<>();
            for (Object[] data : resultList) {
                UserFpCard userFpCard = new UserFpCard();
                int i = -1;
                Object accessCardSerialNo = data[0];
                if (accessCardSerialNo != null) {
                    userFpCard.setAccessCardSerialNo(data[++i].toString());
                    userFpCard.setUserId(data[++i].toString());
                    userFpCard.setFacial(data[++i].toString());
                    userFpCard.setFacialPhoto((byte[]) data[++i]);
                    userFpCard.setFacialTemplate((byte[]) data[++i]);
                    userFpCard.setFacialTemplateType((Integer) data[++i]);
                    studentMessDetailsList.add(userFpCard);
                }
            }
        }
        log.info("Leaving getStudentDetailsByMessId: {}", messId);
        return studentMessDetailsList;
    }

    public String updatePushStatus(String userId, String pushed, String fromStatus,MessMasterControllerDto messPeriod) {
        String status = null;
        Query query = getSession().createQuery("from StudentMessDetails where studentId = :studentId " +
                "and pushRemoveStatus = :fromStatus and activeFlag = :activeFlag order by id desc");
        query.setParameter("studentId", userId);
        query.setParameter("fromStatus", fromStatus);
        query.setParameter("activeFlag", "Y");
        List<StudentMessDetails> result = query.list();
        if (result != null && result.size() > 0) {
            StudentMessDetails studentMessDetails = result.get(0);
            studentMessDetails.setPushRemoveStatus(null);
            if (fromStatus.equals(FrConstant.TO_BE_REMOVED)) {
                if (studentMessDetails.getToPushDate() != null && studentMessDetails.getMmcId()==messPeriod.getId()) {
                    studentMessDetails.setPushRemoveStatus(TO_BE_PUSHED);
                }
                studentMessDetails.setToRemoveDate(null);
            } else {
                studentMessDetails.setToPushDate(null);
            }
            studentMessDetails.setPushStatus(pushed);
            studentMessDetails.setPushDate(new Date());
            getSession().update(studentMessDetails);
            status = "updated";
        }
        return status;
    }

    public String updatePushRemoveByMess(Long messId, String pushRemove) {
        MessMasterControllerDto currentMessSession = messMasterControllerDao.getCurrentMessMasterController();
        StringBuilder queryStr = new StringBuilder("update StudentMessDetails set pushRemoveStatus = '");
        if (pushRemove.equals("push")) queryStr.append(TO_BE_PUSHED).append("', toPushDate = cast(CURRENT_DATE as timestamp) ");
        if (pushRemove.equals("remove")) queryStr.append(FrConstant.TO_BE_REMOVED).append("', toRemoveDate = cast(CURRENT_DATE as timestamp) ");
        queryStr.append(" where mmcId = :currentMessPeriodId and activeFlag = 'Y'");
        if (messId != null && messId > 0) {
            queryStr.append(" and messId = :messId");
        }
        Query query = getSession().createQuery(queryStr.toString());
        query.setParameter("currentMessPeriodId", currentMessSession.getId());
        if (messId != null && messId > 0) {
            query.setParameter("messId", messId);
        }
        return query.executeUpdate() + "";
    }

    public String updatePrevMessPeriodStudStatus(MessMasterControllerDto prevMessPeriod,String tag, boolean record) {
        String status = null;
        Query query = getSession().createQuery("from StudentMessDetails where mmcId = :prevMessPeriod and activeFlag = :activeFlag " +
                "and (pushRemoveStatus!= :status or pushRemoveStatus is null) ");
        query.setParameter("prevMessPeriod", prevMessPeriod.getPreviousId());
        query.setParameter("activeFlag", "Y");
        query.setParameter("status", FrConstant.TO_BE_REMOVED);
        List<StudentMessDetails> result = query.list();
        if (result != null && result.size() > 0) {
            addLog(tag, "Prev Mess Status update count: "+result.size(), record);
            for (StudentMessDetails studentMessDetails : result) {
                addLog(tag, "Prev Mess Status update for "+studentMessDetails.getStudentId(), record);
                studentMessDetails.setPushRemoveStatus(FrConstant.TO_BE_REMOVED);
                getSession().update(studentMessDetails);
                status = "updated";
                addLog(tag, "Prev Mess Period Status: " + status, record);
            }
        }else {
            addLog(tag, "No Prev Mess Period Entries", record);
        }
        return status;
    }

    public String updateRebateStatus(IitAMessRebate iitAMessRebate,MessMasterControllerDto currentMessSession) {
        String status = null;
        Query query = getSession().createQuery("from StudentMessDetails where studentId = :studentId " +
                "and mmcId = :currentMessPeriodId and activeFlag = :activeFlag and currentActiveFlag = :activeFlag " +
                "and  (pushRemoveStatus is null or pushRemoveStatus!= :pushRemoveStatus) " +
                "and (toPushDate is null or toPushDate < CURRENT_DATE) ");
        query.setParameter("studentId", iitAMessRebate.getStudentId());
        query.setParameter("currentMessPeriodId", currentMessSession.getId());
        query.setParameter("activeFlag", "Y");
        query.setParameter("pushRemoveStatus", TO_BE_PUSHED);
        List<StudentMessDetails> result = query.list();
        if (result != null && result.size() > 0) {
            StudentMessDetails studentMessDetails = result.get(0);
            studentMessDetails.setPushRemoveStatus(FrConstant.TO_BE_REMOVED);
            studentMessDetails.setToRemoveDate(iitAMessRebate.getRebateFrom());
            studentMessDetails.setToPushDate(iitAMessRebate.getRebateTo());
            getSession().update(studentMessDetails);
            status = "updated";
        }
        return status;
    }

    public String updateVacateStatus(VacatingRequest vacatingRequest,MessMasterControllerDto currentMessSession) {
        String status = null;
        Query query = getSession().createQuery("from StudentMessDetails where studentId = :studentId " +
                "and mmcId = :currentMessPeriodId and activeFlag = :activeFlag and currentActiveFlag = :activeFlag ");
        query.setParameter("studentId", vacatingRequest.getStudentId());
        query.setParameter("currentMessPeriodId", currentMessSession.getId());
        query.setParameter("activeFlag", "Y");
        List<StudentMessDetails> result = query.list();
        if (result != null && result.size() > 0) {
            StudentMessDetails studentMessDetails = result.get(0);
            studentMessDetails.setPushRemoveStatus(FrConstant.TO_BE_REMOVED);
            studentMessDetails.setToRemoveDate(vacatingRequest.getVacatingDate());
            getSession().update(studentMessDetails);
            status = "updated";
        }
        return status;
    }
/*

from com.triesten.iitm.biometric.domain.StudentMessDetails where pushRemoveStatus != null and (toPushDate != null || toRemoveDate != null) and activeFlag = 'Y' and schoolId = 1
 */
    public List<StudentMessDetails> getPendingList() {
        List<StudentMessDetails> studentMessDetailsList = null;
        Query query = getSession().createQuery("from StudentMessDetails where pushRemoveStatus != null " +
                "and (toPushDate != null or toRemoveDate != null) and activeFlag = 'Y' ");
        studentMessDetailsList = query.list();
        return studentMessDetailsList;
    }

    public List<StudentMessDetails> getCurrentStudentMessDetailsByMessId(Long messId) {
        List<StudentMessDetails> studentMessDetailsList;
        MessMasterControllerDto currentMessSession = messMasterControllerDao.getCurrentMessMasterController();
        Query query = getSession().createQuery("from StudentMessDetails where messId = :messId and mmcId = :currentMessPeriodId");
        query.setParameter("messId", messId);
        query.setParameter("currentMessPeriodId", currentMessSession.getId());
        studentMessDetailsList = query.list();
        return studentMessDetailsList;
    }

    public List<Object[]> getCurrentStudentMessDetailsWithUserByMessId(Long messId) {
        MessMasterControllerDto currentMessSession = messMasterControllerDao.getCurrentMessMasterController();
        Query query = getSession().createQuery("select s, u from StudentMessDetails s left join UserFpCard u on s.studentId = u.userId and u.facial = 'true' " +
                        "where s.messId = :messId and s.mmcId = :currentMessPeriodId");
        query.setParameter("messId", messId);
        query.setParameter("currentMessPeriodId", currentMessSession.getId());
        return query.list();
    }

    public List<StudentMessDetails> getListByDate(String date, Long messId, String status) {
        if (date != null) {
            String queryString = formQuery(messId, status);
            Query query = getSession().createNativeQuery(queryString);
            query.setParameter("date", date);
            if (messId != null && messId > 0) {
                query.setParameter("messId", messId);
            }
            if (status != null && !status.isEmpty()) {
                query.setParameter("status", status);
            }
            List<Object[]> result =  query.list();
            if (result != null && !result.isEmpty()) {
                List<StudentMessDetails> resultList = new ArrayList<>();
                for (Object[] data: result) {
                    StudentMessDetails studentMessDetails = new StudentMessDetails();
                    int i = -1;
                    studentMessDetails.setMessName(String.valueOf(data[++i]));
                    studentMessDetails.setStudentId(String.valueOf(data[++i]));
                    studentMessDetails.setStudentName(data[++i] + " " + data[++i]);
                    studentMessDetails.setPushStatus(String.valueOf(data[++i]));
                    resultList.add(studentMessDetails);
                }
                return resultList;
            }

        }
        return new ArrayList<>();
    }

    private static String formQuery(Long messId, String status) {
        String queryString =
"                    select mm.mess_name, sdi.student_id, sdi.first_name, sdi.last_name, smd.push_status " +
"                    from schooldev.\"STUDENT_MESS_DETAILS\" smd" +
"                    join schooldev.\"STUDENT_DETAILS_INFO\" sdi on (smd.student_id = sdi.student_id)\n" +
"                    join schooldev.\"MESS_MASTER\" mm on (mm.mess_master_id = smd.mess_id)" +
"                    where smd.push_date = cast(:date as date)";
        if (messId != null && messId > 0) {
            queryString += " and smd.mess_id = :messId";
        }
        if (status != null && !status.isEmpty()) {
            queryString += " and smd.push_status = :status";
        }
        queryString += " order by smd.mess_id, sdi.student_id";
        return queryString;
    }

    public MessMaster getMessDetailsByStudentId(String userId) {
        MessMasterControllerDto currentMessSession = messMasterControllerDao.getCurrentMessMasterController();
        final MessMaster[] messMaster = {null};
        Query query = getSession().createQuery("select mm.messMasterId, mm.messName " +
                "from StudentMessDetails smd " +
                "join MessMaster mm on (mm.messMasterId = smd.messId) " +
                "where smd.mmcId = :currentMessPeriodId and smd.studentId = :studentId " +
                "and smd.activeFlag = :activeFlag and smd.currentActiveFlag = :activeFlag ");
        query.setParameter("currentMessPeriodId", currentMessSession.getId());
        query.setParameter("studentId", userId);
        query.setParameter("activeFlag", "Y");
        List<Object[]> messDetails = query.list();
        if (messDetails != null && !messDetails.isEmpty()) {
            messDetails.stream().findFirst().ifPresent(it -> {
                messMaster[0] = new MessMaster();
                messMaster[0].setMessMasterId(((Long) it[0]).longValue());
                messMaster[0].setMessName((String) it[1]);
            });
        }
        return messMaster[0];
    }

    private void addLog(String tag, String msg, boolean record) {
        logService.addLog(tag, " <--> " + msg);
        if (record) {
            schedulerLogService.addLog(tag, msg);
        }
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    public void setMessMasterControllerDao(MessMasterControllerDao messMasterControllerDao) {
        this.messMasterControllerDao = messMasterControllerDao;
    }

    @Autowired
    public void setLogService(InMemoryLogService logService) {
        this.logService = logService;
    }

    @Autowired
    public void setSchedulerLogService(SchedulerLogService schedulerLogService) {
        this.schedulerLogService = schedulerLogService;
    }

}
