package com.iitm.hosteldine.dao.biometric;

import com.iitm.hosteldine.model.biometric.DeviceFRLogs;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
@Slf4j
public class DeviceFRLogsDao {
    String className = "DeviceFRLogsDao";

    private EntityManager entityManager;

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    public int saveLogs(List<DeviceFRLogs> logs) {
        log.info("Entering {}:{}", className, "saveLogs");
        int count = 0;
        Session session = getSession();
        for (DeviceFRLogs log : logs) {
            session.save(log);
            count++;
        }
        log.info("Exiting {}:{}", className, "saveLogs");
        return count;
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
