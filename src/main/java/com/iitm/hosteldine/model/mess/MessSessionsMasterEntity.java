package com.iitm.hosteldine.model.mess;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"MESS_SESSIONS_MASTER\"", schema = ModelConstants.SCHEMA)
@EqualsAndHashCode
public class MessSessionsMasterEntity extends CommonEntity {
    @Id
    @Column(name = "session_code")
    private String sessionCode;

    @Column(name = "session_name", length = 32)
    private String sessionName;






}
