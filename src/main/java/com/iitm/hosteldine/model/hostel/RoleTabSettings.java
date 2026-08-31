package com.iitm.hosteldine.model.hostel;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name ="\"IIT_WD_ROLE_TAB_SETTINGS\"" , schema = ModelConstants.SCHEMA)
public class RoleTabSettings extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role")
    private String role;

    @Column(name = "tab_id")
    private Long tabId;

    @Column(name = "show_hide")
    private Boolean showHide;
}
