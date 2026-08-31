package com.iitm.hosteldine.repository.bulkappointment;

import com.iitm.hosteldine.model.bulkappointment.StudentBulkAppointmentDetailsEntity;
import com.iitm.hosteldine.model.bulkappointment.StudentMasterBulkAppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentBulkAppointmentDetailsRepository extends JpaRepository<StudentBulkAppointmentDetailsEntity, Long> {

	List<StudentBulkAppointmentDetailsEntity> findByStudentMasterBulkAppointment(StudentMasterBulkAppointmentEntity studentMasterBulkAppointment);

}