package com.iitm.hosteldine.repository;

import com.iitm.hosteldine.model.IITWCandidatePhotoEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IITWCandidatePhotoRepository extends JpaRepository<IITWCandidatePhotoEntity, Integer> {
    List<IITWCandidatePhotoEntity> findAllByActiveFlagAndImageIsNotNull(String statusActive, PageRequest of);
    int countAllByActiveFlagAndImageIsNotNull(String statusActive);
}