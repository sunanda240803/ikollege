package com.iitm.hosteldine.model.dashboard.student;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class SeasonMasterEntityId implements Serializable {
    @Serial
    private static final long serialVersionUID = 136776427729726753L;
    @Size(max = 16)
    @NotNull
    @Column(name = "academic_year", nullable = false, length = 16)
    private String academicYear;

    @Size(max = 32)
    @NotNull
    @Column(name = "season", nullable = false, length = 32)
    private String season;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SeasonMasterEntityId entity = (SeasonMasterEntityId) o;
        return Objects.equals(this.academicYear, entity.academicYear) &&
                Objects.equals(this.season, entity.season);
    }

    @Override
    public int hashCode() {
        return Objects.hash(academicYear, season);
    }

}