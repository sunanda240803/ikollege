package com.iitm.hosteldine.service.student;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.student.StudentRollnoChangeDto;
import com.iitm.hosteldine.mapper.student.StudentRollnoChangeMapper;
import com.iitm.hosteldine.model.student.StudentRollnoChangeEntity;
import com.iitm.hosteldine.repository.student.StudentRollnoChangeRepository;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.util.MCrypt;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentRollnoChangeService {

	private final StudentRollnoChangeRepository studentRollnoChangeRepository;
	private final StudentDetailsInfoService studentDetailsInfoService;
	private final FileService fileService;
	private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;

	@Value("${message.validation.roll.no.already.exists}")
	private String rollNoAlreadyExistsError;

	public StudentRollnoChangeDto getRollnoStatus(String studentId) throws Exception {
		StudentRollnoChangeEntity entity = studentRollnoChangeRepository.findByStudentidAndActiveFlag(studentId,
				ModelConstants.STATUS_ACTIVE);
		StudentRollnoChangeDto dto = new StudentRollnoChangeDto();
		if (entity != null && entity.getStatus() != null && entity.getNewRollNo() != null) {
			dto = StudentRollnoChangeMapper.INSTANCE.fromStudentRollnoChangeEntity(entity);
			return dto;
		}
		dto.setNewRollNo("-");
		return dto;
	}

	public String checkRollNoExist(String rollno) throws Exception {
		String studentDetailsInfo = studentDetailsInfoService.validateRollNumber(rollno);
		Optional<StudentRollnoChangeEntity> optionalBioData = studentRollnoChangeRepository
				.findTopByNewRollNoAndActiveFlag(rollno.toUpperCase(), ModelConstants.STATUS_ACTIVE);
		if (optionalBioData.isPresent() || studentDetailsInfo != null) {
			return rollNoAlreadyExistsError;
		}
		return null;
	}

	@Transactional
	public String saveAndUpdate(StudentRollnoChangeDto studentRollnoChangeDto) throws IOException, Exception {
		String isSaved = null;
		Optional<StudentRollnoChangeEntity> optionalEntity = studentRollnoChangeRepository
				.findTopByStudentidAndActiveFlagAndStatus(SecurityCtxUtil.userId().toUpperCase(),
						ModelConstants.STATUS_ACTIVE, ModelConstants.PENDING);
		if (optionalEntity.isPresent()) {
			StudentRollnoChangeEntity deactiveEntity = optionalEntity.get();
			deactiveEntity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
			studentRollnoChangeRepository.save(deactiveEntity);
		}
		StudentRollnoChangeEntity entity = new StudentRollnoChangeEntity();

		String modifiedFileName = null;
		MultipartFile file = studentRollnoChangeDto.getFile();
		// Check if the file exists
		String uploadFileName = (file != null && !file.getOriginalFilename().isEmpty()) ? file.getOriginalFilename() : "";
		if (!uploadFileName.isEmpty()) {
			// Extract the file extension
			String originalFileName = file.getOriginalFilename();
			String fileExtension = "";
			String fileUpload = null;

			if (originalFileName != null && originalFileName.contains(".")) {
				fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
			}
			fileUpload = SecurityCtxUtil.userId().toUpperCase() + ModelConstants.UNDERSCORE + FileService.ROLL_NUMBER_PROOF
					+ ModelConstants.UNDERSCORE + System.currentTimeMillis();
			
			String encryptedName = MCrypt.getInstance().encryptToText(fileUpload);
			modifiedFileName = encryptedName + fileExtension;
		}
		entity.setFileUpload(modifiedFileName);
		entity.setStudentid(SecurityCtxUtil.userId().toUpperCase());
		entity.setNewRollNo(studentRollnoChangeDto.getNewRollNo());
		entity.setStatus(ModelConstants.PENDING);
		entity.onCreate();

		StudentRollnoChangeEntity savedEntity = studentRollnoChangeRepository.save(entity);
		if (Objects.nonNull(savedEntity)) {
			isSaved = "saved";
			// Handle file upload after successful save
			if (studentRollnoChangeDto.getFile() != null && !studentRollnoChangeDto.getFile().isEmpty()
					&& modifiedFileName != null) {
				byte[] fileData = file.getBytes();
				fileService.encodeFile(SimsConfigDataService.UPLOAD_FILE_LOCATION, fileData, modifiedFileName);
			}
		}
		return isSaved;
	}

	public Page<AllStudentsDetailsViewEntity> getStudentDetailsByPreviousId(PaginationForm form) {
		var page = PageRequest.of(form.getPage() - 1, form.getSize());
		var studentId = Optional.ofNullable(form.getSearch())
				.map(Object::toString)
				.orElse(null);
		return allStudentsDetailsViewRepository.getAllStudentDetails(studentId, page);
	}
}
