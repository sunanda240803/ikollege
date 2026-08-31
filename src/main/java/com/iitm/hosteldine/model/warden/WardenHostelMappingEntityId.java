package com.iitm.hosteldine.model.warden;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class WardenHostelMappingEntityId implements Serializable {

    @NotNull
    @Column(name = "hostel_id", nullable = false)
    private Long hostelId;

    @NotNull
    @Column(name = "warden_id", nullable = false)
    private Long wardenId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WardenHostelMappingEntityId that = (WardenHostelMappingEntityId) o;
        return Objects.equals(hostelId, that.hostelId) && Objects.equals(wardenId, that.wardenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hostelId, wardenId);
    }
}