package com.iitm.hosteldine.model.collegeInfo;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import com.iitm.hosteldine.entity.RoleEntity;
import com.iitm.hosteldine.model.MenuListEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "role_menu_privilege", schema = ModelConstants.SCHEMA)
public class RoleMenuPrivilegeEntity extends CommonEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private MenuListEntity menu;

    @Override
    public String toString() {
        return "RMP (" + id +
                "| " + activeFlag +
                "| " + role +
                "| " + menu +
                ')';
    }
}
