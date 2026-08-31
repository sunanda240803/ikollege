package com.iitm.hosteldine.model.student;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students_final_balance", schema = "schooldev")
public class StudentsFinalBalanceEntity {
    @Id
    @Column(name = "student_id")
    private String studentId;
    
    @Column(name = "net_bal")
    private BigDecimal netBal;
    
    // Getters and setters
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public BigDecimal getNetBal() {
        return netBal;
    }
    
    public void setNetBal(BigDecimal netBal) {
        this.netBal = netBal;
    }
}