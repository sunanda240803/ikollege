package com.iitm.hosteldine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class UserManagementId implements Serializable {
    private static final long serialVersionUID = 3164393359456688453L;
    @Column(name = "user_id", nullable = false, length = 25)
    private String userId;


    @Column(name = "user_name", nullable = false, length = 60)
    private String username;

//    @Column(name = "schoolid", nullable = false)
//    private Integer schoolId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserManagementId entity = (UserManagementId) o;
        return /*Objects.equals(this.schoolId, entity.schoolId) &&*/
                Objects.equals(this.userId, entity.userId) &&
                Objects.equals(this.username, entity.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(/*schoolId, */userId, username);
    }

}