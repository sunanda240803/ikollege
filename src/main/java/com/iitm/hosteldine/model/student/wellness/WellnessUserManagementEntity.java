package com.iitm.hosteldine.model.student.wellness;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "\"WELLNESS_USER_MANAGEMENT\"", schema = ModelConstants.SCHEMA)
public class WellnessUserManagementEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, length = 25)
    private String userId;

    @Column(name = "username", nullable = false, length = 60)
    private String username;

    @Column(name = "password", length = 130)
    private String password;

}
