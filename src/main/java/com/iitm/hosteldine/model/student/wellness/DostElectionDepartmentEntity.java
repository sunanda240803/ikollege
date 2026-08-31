package com.iitm.hosteldine.model.student.wellness;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.entity.CommonEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "dost_election_department", schema = ModelConstants.SCHEMA)
public class DostElectionDepartmentEntity extends CommonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id", nullable = false)
    private Long id;

    @Column(name = "dept_code", nullable = false, length = 2)
    private String deptCode;

    @Column(name = "alt_dept_code", length = 2)
    private String altDeptCode;

    @Column(name = "dept_name", nullable = false, length = 64)
    private String deptName;

    @Column(name = "alt_dept_code2", length = 2)
    private String altDeptCode2;

}
