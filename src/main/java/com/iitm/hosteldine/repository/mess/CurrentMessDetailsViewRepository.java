package com.iitm.hosteldine.repository.mess;

import com.iitm.hosteldine.model.mess.CurrentMessDetailsView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrentMessDetailsViewRepository extends JpaRepository<CurrentMessDetailsView, String> {
    Optional<CurrentMessDetailsView> findByStudentId(String studentId);
}
