package com.iitm.hosteldine.dao.biometric;

import com.iitm.hosteldine.model.biometric.MessTerminal;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Query;
import org.hibernate.Session;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
@Slf4j
public class MessTerminalDao {
    String className = "MessTerminalDao";

    private EntityManager entityManager;

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    public List<MessTerminal> getMessTerminalList(Long messId) {
        log.info("Entering {}:{}", className, "getMessTerminalList");
        ArrayList<MessTerminal> resultList = new ArrayList<>();
        String queryStr = "from MessTerminal where activeStatus = 'Y' and terminalIp is not null and terminalDescription like 'FR-%' ";
        if (messId != null && messId > 0) {
            queryStr += " and messMasterId = " + messId;
        }
        Query query = getSession().createQuery(queryStr);
        List<MessTerminal> result = query.list();
        if (result != null && !result.isEmpty()) {
            resultList.addAll(result);
        }
        log.info("Exiting {}:{}", className, "getMessMasterList");
        return resultList;
    }

    public MessTerminal getMessTerminalByIP(String pushIp) {
        log.info("Entering {}:{}", className, "getMessTerminalByIP");
        String queryStr = "from MessTerminal where activeStatus = 'Y' and terminalIp = :terminalIp and terminalDescription like 'FR-%' ";
        Query query = getSession().createQuery(queryStr);
        query.setParameter("terminalIp", pushIp);
        log.info("Exiting {}:{}", className, "getMessTerminalByIP");
        return getMessTerminal(query);
    }

    public MessTerminal getMessTerminalById(Long terminalId) {
        log.info("Entering {}:{}", className, "getMessTerminalById");
        String queryStr = "from MessTerminal where activeStatus = 'Y' and terminalId = :terminalId and terminalDescription like 'FR-%' ";
        Query query = getSession().createQuery(queryStr);
        query.setParameter("terminalId", terminalId);
        MessTerminal terminal = getMessTerminal(query);
        log.info("Exiting {}:{}", className, "getMessTerminalById");
        return terminal;
    }

    private MessTerminal getMessTerminal(Query query) {
        log.info("Exiting {}:{}", className, "getMessTerminal");
        List<MessTerminal> result = query.list();
        MessTerminal messTerminal = null;
        if (result != null && !result.isEmpty()) {
            messTerminal = result.get(0);
        }
        log.info("Exiting {}:{}", className, "getMessTerminal");
        return messTerminal;
    }

    public String updateTerminalDetails(MessTerminal terminal) {
        log.info("Exiting {}:{}", className, "updateTerminalDetails");
        if (getMessTerminalById(terminal.getTerminalId()) != null) {
            MessTerminal messTerminal = getMessTerminalById(terminal.getTerminalId());
            messTerminal.setTerminalIp(terminal.getTerminalIp());
            messTerminal.setUserName(terminal.getUserName());
            messTerminal.setPassword(terminal.getPassword());
            getSession().saveOrUpdate(messTerminal);
            return "success";
        }
        log.info("Exiting {}:{}", className, "updateTerminalDetails");
        return "ipExists";
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
