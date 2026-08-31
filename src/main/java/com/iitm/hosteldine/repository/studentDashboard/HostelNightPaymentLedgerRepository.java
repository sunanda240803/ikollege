package com.iitm.hosteldine.repository.studentDashboard;

import com.iitm.hosteldine.model.studentDashboard.HostelNightPaymentLedgerViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostelNightPaymentLedgerRepository extends JpaRepository<HostelNightPaymentLedgerViewEntity, String> {
}