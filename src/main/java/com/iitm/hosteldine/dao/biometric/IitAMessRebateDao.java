package com.iitm.hosteldine.dao.biometric;

import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.model.biometric.IitAMessRebate;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Query;
import org.hibernate.Session;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
@Slf4j
public class IitAMessRebateDao {
    String className = "IitAMessRebateDao";
    private EntityManager entityManager;

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    public List<IitAMessRebate> getCurrentMessRebateList(MessMasterControllerDto currentMessSession) {
        log.info("Entering {}:{}", className, "getCurrentMessRebateList");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -4);
        Date fromDate = cal.getTime();
        List<IitAMessRebate> currentMessRebateList;
        Query query = getSession().createQuery("from IitAMessRebate where (rebateFrom >= :fromDate and rebateFrom <= CURRENT_DATE) " +
                " and rebateTo > CURRENT_DATE and activeFlag = 'Y' " +
                " and approvalStatus='Approved' ");
        query.setParameter("fromDate", fromDate);
        currentMessRebateList = query.list();
        log.info("Exiting {}:{}", className, "getCurrentMessRebateList");
        return currentMessRebateList;
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
