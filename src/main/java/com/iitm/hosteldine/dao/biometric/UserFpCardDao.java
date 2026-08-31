package com.iitm.hosteldine.dao.biometric;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.form.biometric.UserListForm;
import com.iitm.hosteldine.model.biometric.UserFpCard;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Repository
@Transactional
@Slf4j
public class UserFpCardDao {

    private MessMasterControllerDao messMasterControllerDao;
    private EntityManager entityManager;

    public List<UserListForm> getUserFpList(String idPart, String seralNo, boolean withFacialImage) {
        String methodName = "getUserFpList";
        log.info("Entering {}", methodName);
        List<UserListForm> userFpCardList = null;
        StringBuilder queryStr = formQuery(idPart, seralNo, withFacialImage);
        Query query = getSession().createNativeQuery(queryStr.toString());
        if (idPart != null) {
            query.setParameter("userId", idPart);
        }
        if (seralNo != null) {
            query.setParameter("seralNo", seralNo);
        }
        query.setMaxResults(100);
        List<Object[]> entityList = query.list();
        if (entityList != null && !entityList.isEmpty()) {
            userFpCardList = new ArrayList<>();
            for (Object[] data : entityList) {
                int i = -1;
                UserListForm userListForm = new UserListForm();
                userListForm.setFirstName(String.valueOf(data[++i]));
                userListForm.setLastName(String.valueOf(data[++i]));
                userListForm.setAccessSerialNo(String.valueOf(data[++i]));
                userListForm.setUserId(String.valueOf(data[++i]));
                String hasFingerprint = String.valueOf(data[++i]);
                String cardStatus = String.valueOf(data[++i]);
                String hasFacial = String.valueOf(data[++i]);
                userListForm.setCardActiveStatus(cardStatus);
                userListForm.setHasFingerprint(hasFingerprint != null && hasFingerprint.equals("true"));
                userListForm.setHasCardSn(cardStatus != null && cardStatus.equals("Y"));
                userListForm.setHasFacial(hasFacial != null && hasFacial.equals("true"));
                Object messName = data[++i];
                userListForm.setMessName(messName != null ? String.valueOf(messName) : null);
                userListForm.setCardSn(String.valueOf(data[++i]));
                userListForm.setPinNo(String.valueOf(data[++i]));
                if (withFacialImage) {
                    byte[] image = (byte[]) data[++i];
                    if (userListForm.isHasFacial()) {
                        userListForm.setFacialPhotoSrc(Base64.getEncoder().encodeToString(image));
                    }
                }
                userFpCardList.add(userListForm);
            }
        }
        log.info("Exiting {} with user size {}", methodName, (userFpCardList != null ? userFpCardList.size() : 0));
        return userFpCardList;
    }

    private static StringBuilder formQuery(String idPart, String seralNo, boolean withFacialImage) {
        StringBuilder queryStr = new StringBuilder(" select sdi.student_name, '', ufc.access_card_serial_no,");
        queryStr.append("\nufc.user_id, ufc.active, ufc.card_active_status, ufc.facial, smd.mess_name, ufc.card_sn , ufc.pin_no");
        if (withFacialImage) {
            queryStr.append(", facial_photo");
        }
        queryStr.append("\nfrom schooldev.\"USER_FP_CARD\" ufc");
        queryStr.append("\nleft join schooldev.\"ALL_STUDENTS_DETAILS_VIEW\" sdi on");
        queryStr.append("\n   sdi.student_id = ufc.user_id");
        queryStr.append("\nleft join schooldev.\"CURRENT_MESS_DETAILS_VIEW\" smd on");
        queryStr.append("\n   smd.student_id = sdi.student_id");
        queryStr.append("\nwhere ufc.access_card_serial_no is not null");
        if (idPart != null) {
            queryStr.append("\n and lower(user_id) like lower('%' || :userId || '%')");
        }
        if (seralNo != null) {
            queryStr.append("\n and (access_card_serial_no) like ('%' || :seralNo || '%')");
        }
        queryStr.append("\norder by");
        queryStr.append("\n    case when ufc.facial is not null then 0 else 1 end,");
        queryStr.append("\n    case when smd.mess_name is not null then 0 else 1 end");
        return queryStr;
    }

    private Session getSession() {
        return entityManager.unwrap(Session.class);
    }

    public UserFpCard getUserByUserId(String userId) {
//        String methodName = "getUserFpList";
//        log.info("Entering {}", methodName);
        UserFpCard resultUser = null;
        Query query = getSession().createQuery("from UserFpCard where userId = :userId and facial = 'true'");
        query.setParameter("userId", userId);
        List<UserFpCard> resultList = query.list();
        if (resultList != null && !resultList.isEmpty()) {
            resultUser = resultList.get(0);
        } else {
            resultUser = new UserFpCard();
            resultUser.setUserId(userId);
        }
//        log.info("Exiting {}", methodName);
        return resultUser;
    }

    public UserFpCard getUserByAccessSerialNo(String serialNo) {
//        String methodName = "getUserFpList";
//        log.info("Entering {}", methodName);
        UserFpCard resultUser = null;
        Query query = getSession().createQuery("from UserFpCard where accessCardSerialNo = :serialNo");
        query.setParameter("serialNo", serialNo);
        List<UserFpCard> resultList = query.list();
        if (resultList != null && !resultList.isEmpty()) {
            resultUser = resultList.get(0);
        }
//        log.info("Exiting {}", methodName);
        return resultUser;
    }

    public String updateFacialData(UserFpCard userFpCard) {
        String result = "";
        try {
            getSession().update(userFpCard);
            result = "Updated Successfully";
        } catch (Exception e) {
            result = "Error: " + e.getMessage();
        }
        return result;
    }

    public String updateCardStatus(String studentId, String cardStatus) {
        UserFpCard userFpCard = findByUserId(studentId);
        if (userFpCard == null) {
            return Constants.ERROR;
        }
        userFpCard.setCardActiveStatus(ModelConstants.STATUS_ACTIVE.equals(cardStatus) ? ModelConstants.STATUS_INACTIVE : ModelConstants.STATUS_ACTIVE);
        try {
            getSession().update(userFpCard);
            return Constants.UPDATED;
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.ERROR;
        }
    }

    public UserFpCard findByUserId(String userId) {
        return getSession().get(UserFpCard.class, userId);
    }

    @Autowired
    public void setMessMasterControllerDao(MessMasterControllerDao messMasterControllerDao) {
        this.messMasterControllerDao = messMasterControllerDao;
    }

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
