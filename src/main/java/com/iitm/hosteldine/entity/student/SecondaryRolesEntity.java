package com.iitm.hosteldine.entity.student;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "\"SECONDARY_ROLES\"", schema = ModelConstants.SCHEMA)
public class SecondaryRolesEntity extends CommonEntity {


    @Column(name = "user_id", nullable = false, length = 30)
    private String userId;

    @Column(name = "role_id", nullable = false)
    private Long roleId = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "secondary_reference_role_id", nullable = false)
    private Long secondaryReferenceRoleId = 0L;

}
