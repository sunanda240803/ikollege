package com.iitm.hosteldine.dao.biometric;

import com.iitm.hosteldine.constant.biometric.FrConstant;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.model.biometric.MessMaster;
import com.iitm.hosteldine.model.biometric.StudentMessDetails;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import jakarta.persistence.EntityManager;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
@Slf4j
public class MessMasterDao {
    String className = "MessMasterDao";

    private EntityManager entityManager;

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    public List<MessMaster> getMessMasterList() {
        log.info("Entering {}:{}", className, "getMessMasterList");
        ArrayList<MessMaster> resultList = new ArrayList<>();
        Query query = getSession().createNativeQuery("select mm.mess_master_id, mm.mess_name, mm.capacity, " +
                "mm.gender_option, mm.active_flag, string_agg(mt.terminal_id || '~' || mt.terminal_ip, ', ') \n" +
                "from schooldev.\"MESS_MASTER\" mm \n" +
                "    left join schooldev.\"MESS_TERMINAL\" mt on (mt.mess_master_id = mm.mess_master_id and mt.active_flag = 'Y'\n" +
                "                                                   and terminal_ip is not null) \n" +
                "where mm.active_flag = 'Y' \n" +
                "group by mm.mess_master_id, mm.mess_name, mm.capacity, mm.gender_option, mm.active_flag;");
        List<Object[]> result = query.list();
        if (result != null && !result.isEmpty()) {
            for (Object[] resultData : result) {
                MessMaster messMaster = new MessMaster();
                int i = -1;
                messMaster.setMessMasterId(Long.valueOf(String.valueOf(resultData[++i])));
                messMaster.setMessName(String.valueOf(resultData[++i]));
                messMaster.setCapacity(String.valueOf(resultData[++i]));
                messMaster.setGenderOption(String.valueOf(resultData[++i]));
                messMaster.setActiveStatus(String.valueOf(resultData[++i]));
                messMaster.setTerminalIps(String.valueOf(resultData[++i]).split(", "));
                resultList.add(messMaster);
            }
        }
        log.info("Exiting {}:{}", className, "getMessMasterList");
        return resultList;
    }

    public MessMaster getMessMasterById(Long messMasterId) {
        Query query = getSession().createQuery("from MessMaster where messMasterId = :messId");
        query.setParameter("messId", messMasterId);
        List<MessMaster> messMasterList = query.list();
        if (!messMasterList.isEmpty()) {
            return messMasterList.get(0);
        }
        return null;
    }

    public List<Long> getMessIdList() {
        log.info("Entering {}:{}", className, "getMessIdList");
        ArrayList<Long> resultList = new ArrayList<>();
        Query query = getSession().createQuery("select messMasterId from MessMaster where activeStatus = 'Y'");
        List<Long> result = query.list();
        if (result != null && !result.isEmpty()) {
            resultList.addAll(result);
        }
        log.info("Exiting {}:{}", className, "getMessIdList");
        return resultList;
    }

    public Map<Long, ArrayList<StudentMessDetails>> getStudentsToPushOrRemove(boolean toRemove, MessMasterControllerDto messPeriod) {
        log.info("Entering {}:{}", className, "getStudentsToPushOrRemove");
        String pullRemoveDate = toRemove ? "toRemoveDate" : "toPushDate";
        String pullRemoveStatus = toRemove ? FrConstant.TO_BE_REMOVED : FrConstant.TO_BE_PUSHED;
        Query query = getSession().createQuery("from StudentMessDetails smd " +
                "where smd.pushRemoveStatus = :status and " +
                "(" + pullRemoveDate + " is null or " + pullRemoveDate + " <= CURRENT_DATE) and " +
                "activeFlag = :activeFlag " +
                "order by messId");
        query.setParameter("activeFlag", "Y");
        query.setParameter("status", pullRemoveStatus);
//        query.setParameter("messPeriodStart", messPeriod.getDiningFromDate());
        List<StudentMessDetails> resultList  = query.list();
        Map<Long, ArrayList<StudentMessDetails>> studentMap = new HashMap<>();
        if (resultList != null && !resultList.isEmpty()) {
            for(StudentMessDetails smd : resultList) {
                Long messId = smd.getMessId();
                ArrayList<StudentMessDetails> studentList = studentMap.get(messId);
                if (studentList == null) {
                    studentList = new ArrayList<>();
                }
                studentList.add(smd);
                studentMap.put(messId, studentList);
            }
        }
        log.info("Exiting {}:{}", className, "getStudentsToPushOrRemove");
        return studentMap;
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
