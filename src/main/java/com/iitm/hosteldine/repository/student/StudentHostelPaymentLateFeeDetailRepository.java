package com.iitm.hosteldine.repository.student;

import com.iitm.hosteldine.model.student.StudentHostelPaymentLateFeeDetailEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentHostelPaymentLateFeeDetailRepository extends JpaRepository<StudentHostelPaymentLateFeeDetailEntity, Long> {

  @Query(value = """
    select shp,hme.hostelName from StudentHostelPaymentLateFeeDetailEntity shp
        left join HostelMasterEntity hme on (hme.id = shp.hostelId)
            where shp.activeFlag = :activeFlag and hme.activeFlag = :activeFlag
    """)
  Page<Object[]> getStudentLateFeeDetails(String activeFlag, Pageable pageable);
}