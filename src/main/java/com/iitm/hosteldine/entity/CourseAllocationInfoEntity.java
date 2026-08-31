package com.iitm.hosteldine.entity;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.model.collegeInfo.CourseMasterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "\"COURSE_ALLOCATION_INFO\"", schema = ModelConstants.SCHEMA)
public class CourseAllocationInfoEntity extends  CommonEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_allocation_id")
    private Integer courseAllocationId;

    @Column(name = "student_id")
    private String studentId;

    //@ManyToOne(fetch = FetchType.LAZY)
   /* @JoinColumn(name = "course_id",
            referencedColumnName = "course_master_id", insertable = false, updatable = false)
    private CourseMasterEntity courseMaster;*/
    @Column(name="course_id")
    private Long courseId;

    @Column(name = "course_period_id")
    private Integer coursePeriodId;

    @Column(name = "section_id")
    private Integer sectionId;

    @Column(name = "batch_id", nullable = false)
    private Integer batchId;

   @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(name = "joining_period_id")
    private Integer joiningPeriodId;

    @Column(name = "student_registration_number")
    private String studentRegistrationNumber;

   /* @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "student_id", referencedColumnName = "student_id", insertable = false, updatable = false),
        @JoinColumn(name = "school_id", referencedColumnName = "school_id", insertable = false, updatable = false)
    })
    private StudentDetailsInfo studentDetailsInfo;*/



}
