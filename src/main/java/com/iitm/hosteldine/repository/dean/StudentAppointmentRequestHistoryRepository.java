package com.iitm.hosteldine.repository.dean;

import com.iitm.hosteldine.model.dean.StudentAppointmentRequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentAppointmentRequestHistoryRepository extends JpaRepository<StudentAppointmentRequestHistory, Long> {
}