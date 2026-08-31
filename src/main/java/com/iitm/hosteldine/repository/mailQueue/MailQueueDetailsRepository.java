package com.iitm.hosteldine.repository.mailQueue;

import com.iitm.hosteldine.entity.mailQueue.MailQueueDetailsEntity;
import com.iitm.hosteldine.model.collegeInfo.FaqEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface MailQueueDetailsRepository extends JpaRepository<MailQueueDetailsEntity, Long> {
    String STATUS_QUERY = "";
//            "        and (case when :status is not null and :status = 'Sent' then mailStatus = 3" +
//            "                when :status is not null and :status = 'Pending' then mailStatus = 1" +
//            "                when :status is not null and :status = 'Error' then errorDef is not null" +
//            "                else mailStatus = mailStatus end) ";
    String SEARCH_QUERY = " and (" +
            "                        (lower(submittedModule) like '%' || :search || '%') or " +
            "                        (lower(mailSubject) like '%' || :search || '%') or " +
            "                        (lower(mailFrom) like '%' || :search || '%') or " +
            "                        (lower(mailTo) like '%' || :search || '%') or " +
            "                        (lower(mailCc) like '%' || :search || '%') or " +
            "                        (lower(mailBcc) like '%' || :search || '%') or " +
            "                        (lower(errorDef) like '%' || :search || '%')" +
            "                  ) ";

    @Query(value =
            "from MailQueueDetailsEntity m " +
                    "where activeFlag = 'Y' "
                    + "AND (COALESCE(CAST(:fromDate AS date), m.createdAt) = m.createdAt OR cast(m.createdAt as date) >= :fromDate) "
                    + "AND (COALESCE(CAST(:toDate AS date), m.createdAt) = m.createdAt OR cast(m.createdAt as date) <= :toDate) "
                    + STATUS_QUERY)
    Page<MailQueueDetailsEntity> getByActiveFlag(String activeFlag, Pageable pageable, LocalDate fromDate, LocalDate toDate, String status);

    @Query(value =
            "from MailQueueDetailsEntity m " +
            "where activeFlag = 'Y' " + SEARCH_QUERY
            + " AND (COALESCE(CAST(:fromDate AS date), m.createdAt) = m.createdAt OR cast(m.createdAt as date) >= :fromDate) "
            + " AND (COALESCE(CAST(:toDate AS date), m.createdAt) = m.createdAt OR cast(m.createdAt as date) <= :toDate) "
            + STATUS_QUERY)
    Page<MailQueueDetailsEntity> getAllBySearch(String activeFlag, Pageable pageable, String search, LocalDate fromDate, LocalDate toDate, String status);

}
