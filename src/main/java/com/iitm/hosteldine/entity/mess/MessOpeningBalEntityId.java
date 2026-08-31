package com.iitm.hosteldine.entity.mess;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class MessOpeningBalEntityId implements Serializable {
    private static final long serialVersionUID = 7219693062998039727L;
    @NotNull
    @Column(name = "companyid", nullable = false)
    private Integer companyid;

    @Size(max = 16)
    @NotNull
    @Column(name = "fin_year", nullable = false, length = 16)
    private String finYear;

    @Size(max = 45)
    @NotNull
    @Column(name = "acchead", nullable = false, length = 45)
    private String acchead;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MessOpeningBalEntityId entity = (MessOpeningBalEntityId) o;
        return Objects.equals(this.acchead, entity.acchead) &&
                Objects.equals(this.companyid, entity.companyid) &&
                Objects.equals(this.finYear, entity.finYear);
    }

    @Override
    public int hashCode() {
        return Objects.hash(acchead, companyid, finYear);
    }

}