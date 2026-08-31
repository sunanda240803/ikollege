package com.iitm.hosteldine.dao.biometric;

import com.iitm.hosteldine.model.biometric.VacatingRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Query;
import org.hibernate.Session;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
@Slf4j
public class VacatingRequestDao {
    private EntityManager entityManager;

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    public List<VacatingRequest> getVacatingStudentList() {
        Query query = getSession().createQuery("from VacatingRequest where vacatingDate = CURRENT_DATE and activeFlag= 'Y' " +
                "and hostelOrWardenApprovalStatus ='Approved' ");
        return (List<VacatingRequest>) query.list();
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
