package com.iitm.hosteldine.entity;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
@MappedSuperclass
public abstract class CommonEntity {
    @Column(name = "created_by", nullable = false, length = 20)
    public String createdBy;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt;

    @Column(name = "modified_by", nullable = false, length = 20)
    protected String modifiedBy;

    @Column(name = "modified_at", nullable = false)
    protected LocalDateTime modifiedAt;

    @Column(name = "active_flag", nullable = false, length = 1)
    protected String activeFlag;

    @PrePersist
    public void onCreate() {
        setCreatedAt(DateUtility.getNowTimeInstant());
        setCreatedBy(userId());
        onUpdate();
        setActiveFlag(ModelConstants.STATUS_ACTIVE);
    }

    @PreUpdate
    public void onUpdate() {
        setModifiedAt(DateUtility.getNowTimeInstant());
        setModifiedBy(userId());
    }

    public String userId(){
        String userId;
        if(Objects.nonNull(SecurityCtxUtil.accountType()) && SecurityCtxUtil.accountType().equalsIgnoreCase(ModelConstants.OTHER_LOGIN_TYPE)
                && Objects.nonNull(SecurityCtxUtil.candidateId()) && SecurityCtxUtil.candidateId() > 0L){
            userId = String.valueOf(SecurityCtxUtil.candidateId());
        }
        else{
            userId = SecurityCtxUtil.userId();
        }
        return userId;
    }
}
