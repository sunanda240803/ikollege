package com.iitm.hosteldine.repository.dashboard.student;

import com.iitm.hosteldine.model.dashboard.student.SeasonMasterEntity;
import com.iitm.hosteldine.model.dashboard.student.SeasonMasterEntityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SeasonMasterRepository extends JpaRepository<SeasonMasterEntity, SeasonMasterEntityId> {
    @Query(value = "SELECT s FROM SeasonMasterEntity s "
            + "WHERE s.activeFlag = :activeStatus "
            + "AND s.isCurrent = :isCurrent")
    List<SeasonMasterEntity> getSeasonMasterList(String activeStatus, Boolean isCurrent);
}
