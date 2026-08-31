package com.iitm.hosteldine.model.mess;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"STUDENT_MESS_PRIORITY_REGISTRATION\"", schema = ModelConstants.SCHEMA) 
public class StudentMessPriorityRegistrationEntity extends CommonEntity{
	
	@EmbeddedId
    private StudentMessPriorityRegistrationId id;
	
    @Column(name = "mess_preference", length = 32)
    private String messPreference;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "sem_mon", length = 10)
    private String semMon;

}
