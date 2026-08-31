package com.iitm.hosteldine.dao.biometric;

import com.iitm.hosteldine.model.biometric.User;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
@Slf4j
public class UserDao {

    String className = "UserDaoImpl";

    private EntityManager entityManager;

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    public User findByUserName(String username) {
        String methodName = "findByUserName : " + username;
        log.info("Entering Class:{} & Method:{}", className, methodName);
        Query query = getSession().createQuery("from User where upper(userName) = ?")
                .setParameter(0, username.toUpperCase());
        if (query.list() != null && query.list().size() == 1) {
            log.debug("App user exists {}", username);
            return (User) query.list().get(0);
        }
        log.info("Exiting Class:{} & Method:{}", className, methodName);
        return null;
    }

    public String getFPStatusById(String id) {
        String methodName = "getFPStatusById : " + id;
        log.info("Entering Class:{} & Method:{}", className, methodName);
        String status;
        List fpStatus = getSession().createNamedQuery("select * from schooldev.\"USER_FP_CARD\" where user_id ='" + id + "' and fingerprint_image is not null and octet_length(fingerprint_image) > 500").list();
        if (!fpStatus.isEmpty()) {
            status = "Completed";
        } else {
            status = "Pending";
        }
        log.info("Exiting Class:{} & Method:{}", className, methodName);
        return status;
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}