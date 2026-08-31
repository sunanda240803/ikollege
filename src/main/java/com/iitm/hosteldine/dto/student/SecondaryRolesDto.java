package com.iitm.hosteldine.dto.student;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SecondaryRolesDto {

    private String userId;
    private Long roleId;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private String activeFlag;
    private Long secondaryReferenceRoleId;
    private Integer schoolId;
}
