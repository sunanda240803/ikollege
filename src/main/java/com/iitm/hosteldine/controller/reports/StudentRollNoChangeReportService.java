package com.iitm.hosteldine.controller.reports;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.entity.UserManagementEntity;
import com.iitm.hosteldine.entity.UserManagementId;
import com.iitm.hosteldine.entity.student.RfidMappingEntity;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.entity.student.UserFpCardEntity;
import com.iitm.hosteldine.exception.RecordAlreadyExistsException;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.reports.StudentRollNoChangeForm;
import com.iitm.hosteldine.model.StudentBioDataFormDetailEntity;
import com.iitm.hosteldine.model.hostel.HostelRoomAllotmentInfoEntity;
import com.iitm.hosteldine.model.student.StudentRollnoChangeEntity;
import com.iitm.hosteldine.repository.StudentBioDataFormDetailRepository;
import com.iitm.hosteldine.repository.UserManagementRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomAllotmentRepository;
import com.iitm.hosteldine.repository.student.RfidRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.repository.student.StudentRollnoChangeRepository;
import com.iitm.hosteldine.repository.student.UserFpCardRepository;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
class StudentRollNoChangeReportService {

    private final StudentRollnoChangeRepository studentRollnoChangeRepository;
    private final Utility utility;
    private final StudentDetailsInfoRepository studentDetailsInfoRepository;
    private final StudentBioDataFormDetailRepository studentBioDataFormDetailRepository;
    private final HostelRoomAllotmentRepository hostelRoomAllotmentRepository;
    private final UserManagementRepository userManagementRepository;
    private final RfidRepository rfidRepository;
    private final UserFpCardRepository userFpCardRepository;
    private final CommonResponseUtil commonResponseUtil;
	private final FileService fileService;

    public List<StudentRollChangeRecord> getStudentRollNoChange(StudentRollNoChangeForm form) {
        return studentRollnoChangeRepository.getStudentRollNoChange(form.getPreviousId(), form.getChangeStudentId(),
                        form.getStudentName(), String.valueOf(form.getRequestDate()))
                .stream()
                .map(this::mapToStudentRollChangeRecord)
                .toList();
    }

    private StudentRollChangeRecord mapToStudentRollChangeRecord(Object[] o) {
        return new StudentRollChangeRecord(
                utility.dateFormatter(utility.convertToLocalDate(o[0])),
                utility.parseString(o[1]),
                utility.parseString(o[2]),
                utility.parseString(o[3]),
                utility.parseString(o[4]),
                utility.parseLong(o[6]),
                utility.parseString(o[5])

        );
    }

    @Transactional(rollbackFor = Exception.class)
    public String approveStudentRollNoChange(String studentId, String changeRollNo, Long id) throws RecordNotExistsException, RecordAlreadyExistsException {
        StudentDetailsInfoEntity existingStudentEntity = studentDetailsInfoRepository.findByStudentIdAndActiveFlag(studentId, ModelConstants.STATUS_ACTIVE)
                .orElse(null);
        if (Objects.isNull(existingStudentEntity)) {
            throw new RecordNotExistsException(commonResponseUtil.getMessage("message.student.id.not.exists.exception")
                    .replace("#%studentId%#", studentId)
                    .replace("#%table%#", "STUDENT_DETAILS_INFO"));
        }

        boolean exists = studentDetailsInfoRepository.findByStudentIdAndActiveFlag(changeRollNo, ModelConstants.STATUS_ACTIVE)
                .isPresent();
        if (exists) {
            throw new RecordAlreadyExistsException(commonResponseUtil.getMessage("message.student.id.exists.exception")
                    .replace("#%studentId%#", changeRollNo)
                    .replace("#%table%#", "STUDENT_DETAILS_INFO"));
        }

        StudentBioDataFormDetailEntity studentBioDataFormDetailEntity = studentBioDataFormDetailRepository.findByStudentIdAndActiveFlag(studentId, ModelConstants.STATUS_ACTIVE)
                .orElse(null);
        if (Objects.isNull(studentBioDataFormDetailEntity)) {
            throw new RecordNotExistsException(commonResponseUtil.getMessage("message.student.id.not.exists.exception")
                    .replace("#%studentId%#", studentId)
                    .replace("#%table%#", "STUDENT_BIO_DATA_FORM_DETAILS"));
        }

        UserManagementEntity userManagementEntity = userManagementRepository.findByIdUserIdAndActiveFlag(studentId, ModelConstants.STATUS_ACTIVE)
                .orElse(null);
        if (Objects.nonNull(userManagementEntity)) {
            UserManagementEntity newUser = userManagementRepository.findByIdUserIdAndActiveFlag(changeRollNo, ModelConstants.STATUS_ACTIVE)
                    .orElse(null);
            if (Objects.nonNull(newUser)) {
                throw new RecordAlreadyExistsException(commonResponseUtil.getMessage("message.student.id.exists.exception")
                        .replace("#%studentId%#", changeRollNo)
                        .replace("#%table%#", "USER_MANAGEMENT"));
            }
        } else {
            throw new RecordNotExistsException(commonResponseUtil.getMessage("message.student.id.not.exists.exception")
                    .replace("#%studentId%#", studentId)
                    .replace("#%table%#", "USER_MANAGEMENT"));
        }

        boolean studentDetails = createStudentDetails(existingStudentEntity, changeRollNo);

        studentBioDataFormDetailEntity.setStudentId(changeRollNo);
        studentBioDataFormDetailRepository.save(studentBioDataFormDetailEntity);

        HostelRoomAllotmentInfoEntity hostelRoomAllotmentInfoEntity = hostelRoomAllotmentRepository.findByStudentIdEqualsIgnoreCaseAndActiveFlagAndVacateDateIsNullAndShiftedDateIsNull(studentId, ModelConstants.STATUS_ACTIVE)
                .orElse(null);
        if (Objects.nonNull(hostelRoomAllotmentInfoEntity)) {
            hostelRoomAllotmentInfoEntity.setStudentId(changeRollNo);
            hostelRoomAllotmentRepository.save(hostelRoomAllotmentInfoEntity);
        }

        UserManagementEntity newUser = new UserManagementEntity();
        UserManagementId userId = new UserManagementId();
        userId.setUserId(changeRollNo);
        userId.setUsername(changeRollNo.toLowerCase());
        newUser.setId(userId);

        newUser.setEmployeeId(userManagementEntity.getEmployeeId());
        newUser.setAccountType(userManagementEntity.getAccountType());
        newUser.setLastLoginTime(userManagementEntity.getLastLoginTime());
        newUser.setWrongPwdTime(userManagementEntity.getWrongPwdTime());
        newUser.setPassword(userManagementEntity.getPassword());
        newUser.setPasswordModifiedTime(userManagementEntity.getPasswordModifiedTime());
        newUser.setIsLockable(userManagementEntity.getIsLockable());
        newUser.setNoFailedAttempts(userManagementEntity.getNoFailedAttempts());
        newUser.setAuthenticationServer(userManagementEntity.getAuthenticationServer());
        newUser.setCardPin(userManagementEntity.getCardPin());
        newUser.setEmail(userManagementEntity.getEmail());
        newUser.setIpAddress(userManagementEntity.getIpAddress());
        newUser.setRole(userManagementEntity.getRole());
        newUser.setRoleSecondary(userManagementEntity.getRoleSecondary());
        userManagementRepository.save(newUser);

        userManagementEntity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
        userManagementRepository.save(userManagementEntity);

        RfidMappingEntity rfidMappingEntity = rfidRepository.findByStudentId(studentId).orElse(null);
        if (Objects.nonNull(rfidMappingEntity)) {
            rfidMappingEntity.setStudentId(changeRollNo);
            rfidRepository.save(rfidMappingEntity);
        }

        UserFpCardEntity userFpCardEntity = userFpCardRepository.findByUserId(studentId);
        if (Objects.nonNull(userFpCardEntity)) {
            boolean userFpCardDetails = createUserFpCardDetails(userFpCardEntity, changeRollNo);
        }

        StudentRollnoChangeEntity studentRollnoChangeEntity = studentRollnoChangeRepository.findByIdAndActiveFlag(id,
                ModelConstants.STATUS_ACTIVE).orElse(null);
        if (Objects.nonNull(studentRollnoChangeEntity)) {
            studentRollnoChangeEntity.setStatus(WorkflowStatus.APPROVED.getStatus());
            studentRollnoChangeRepository.save(studentRollnoChangeEntity);
        }

        existingStudentEntity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
        studentDetailsInfoRepository.save(existingStudentEntity);
        return Constants.SAVED;
    }

    private boolean createStudentDetails(StudentDetailsInfoEntity studentDetailsInfoEntity, String changeRollNo) {
        StudentDetailsInfoEntity newEntity = new StudentDetailsInfoEntity(studentDetailsInfoEntity);
        newEntity.setStudentId(changeRollNo);
        newEntity.setPreviousId(studentDetailsInfoEntity.getPreviousId() + "," + studentDetailsInfoEntity.getStudentId());
        newEntity.onCreate();
        studentDetailsInfoRepository.save(newEntity);
        return true;
    }

    private boolean createUserFpCardDetails(UserFpCardEntity userFpCardEntity, String changeRollNo) {
        UserFpCardEntity entity = new UserFpCardEntity(userFpCardEntity);
        entity.setUserId(changeRollNo);
        entity.setCardActiveStatus(ModelConstants.STATUS_ACTIVE);
        entity.onUpdate();
        userFpCardRepository.save(entity);
        return true;
    }

	public ByteArrayResource loadFile(String fileName) throws Exception {
		byte[] fileData = fileService.getDecodedFile(SimsConfigDataService.UPLOAD_FILE_LOCATION, fileName);
		return new ByteArrayResource(fileData);
	}

}
