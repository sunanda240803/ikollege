package com.iitm.hosteldine.entity;

import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "roles", schema = ModelConstants.SCHEMA)
public class RoleEntity extends CommonEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_name", nullable = false, length = 64)
    private String roleName;

    @Override
    public String toString() {
        return "Role (" + roleId + ", " + roleName + ')';
    }
}
