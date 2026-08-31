package com.iitm.hosteldine.service;

import java.time.LocalDateTime;
import java.util.*;

import com.iitm.hosteldine.dto.student.StudentRoomInfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.StudentDetails;
import com.iitm.hosteldine.dto.StudentDetailsInfoMapper;
import com.iitm.hosteldine.dto.student.AllStudentsDetailsViewDto;
import com.iitm.hosteldine.dto.student.StudentDetailsInfoDto;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentDetailsInfoService {
	private final StudentDetailsInfoRepository studentDetailsInfoRepository;
	private final AllStudentsDetailsViewService allStudentsDetailsViewService;

	public StudentDetails getStudentDetails(String studentId) {
		AllStudentsDetailsViewDto dto = allStudentsDetailsViewService.getCompleteStudentDetails(studentId);
		StudentDetails studentDetails = null;
		if (dto != null) {
			studentDetails = new StudentDetails();
			String studentName = dto.getStudentName() != null && !dto.getStudentName().isEmpty()
					? dto.getStudentName() : ModelConstants.HYPHEN;
			String hostelName = dto.getHostelName() != null && !dto.getHostelName().isEmpty() ? dto.getHostelName()
					: ModelConstants.HYPHEN;
			String roomNo = dto.getRoomNumber() != null && !dto.getRoomNumber().isEmpty() ? dto.getRoomNumber()
					: ModelConstants.HYPHEN;
			String messName = dto.getMessName() != null && !dto.getMessName().isEmpty() ? dto.getMessName()
					: ModelConstants.HYPHEN;
			
			studentDetails.setStudentFullName(studentName);
			studentDetails.setHostelName(hostelName);
			studentDetails.setRoomNumber(roomNo);
			studentDetails.setMess(messName);
			studentDetails.setRoomId(dto.getRoomId());
			studentDetails.setHostelId(dto.getHostelId());
		}
		studentDetails.setSessionStartTime(LocalDateTime.now());
		return studentDetails;
	}

    public int checkPreviousIdExist(String studentId, String prevId) {
        List<String> studentIds = studentDetailsInfoRepository.findByPreviousId(prevId);
        return studentIds.stream().anyMatch(resultStudentId -> !studentId.equalsIgnoreCase(resultStudentId)) ? 1 : 0;
    }

    public String checkDuplicateNoWithSeparate(String aStudentId) {


        List<Object[]> resultset = studentDetailsInfoRepository.checkDuplicateNoWithSeparate(
                aStudentId);

        List<String> splitId = new ArrayList<>(Arrays.asList(aStudentId.split(",")));

        resultset.stream()
                .map(result -> (String) result[2])
                .filter(splitId::contains)
                .forEach(splitId::remove);

        return String.join(",", splitId);
    }



public String checkAndUpdatePreviousStudId(String studentId, String prevId) {
    if (prevId != null && !prevId.isEmpty()) {

        List<Object[]> results = studentDetailsInfoRepository.findMatchingStudents(prevId, studentId);

        List<String> appendPrevId = new ArrayList<>();
        boolean appendStatus = false;

        // Check if the results list is not empty and process the results
        if (!results.isEmpty()) {
            // Iterate through the results
            for (Object[] row : results) {
                String studId = (String) row[2];
                appendPrevId.add(studId);
            }
            appendStatus = true;
        }

        // Update previousStudId if any results were found
        if (appendStatus) {
            return String.join(",", appendPrevId);
        }

    }
    return null;
}

public String getPrevIdsOfPrevIds(String prevId) {
	if (prevId == null || prevId.trim().isEmpty()) {
		return null;
	}
	// Fetch DB values
	List<String> ids =
			studentDetailsInfoRepository.getPrevIdsOfPrevIds(prevId);
	return String.join(",", ids);
}


public int deactivateStudents(String previousStudId) {
    if (StringUtils.isNotEmpty(previousStudId)) {
        List<String> previousIds = Arrays.asList(previousStudId.toUpperCase().split(","));
        return studentDetailsInfoRepository.deactivateStudentsByIds(previousIds,SecurityCtxUtil.userId(),DateUtility.getNowTimeInstant());
    }
    return 0;
}

	@Value("${message.validation.roll.no.already.exists}")
	private String studentIdAlreadyExistsError;

	public String validateRollNumber(String rollNo) {
		Optional<StudentDetailsInfoEntity> optionalEntity = studentDetailsInfoRepository
				.findByStudentIdAndActiveFlag(rollNo.toUpperCase(), ModelConstants.STATUS_ACTIVE);
		if (optionalEntity.isPresent()) {
			return studentIdAlreadyExistsError;
		}
		return null;
	}

	public StudentDetailsInfoDto getStudentInfoDetails(String studentId) {
		return studentDetailsInfoRepository.findByStudentIdAndActiveFlag(studentId, ModelConstants.STATUS_ACTIVE)
				.map(StudentDetailsInfoMapper.INSTANCE::fromStudentDetailsEntity).orElse(new StudentDetailsInfoDto());
	}

	public StudentDetailsInfoDto getStudentInfoWithoutActive(String studentId) {
		return studentDetailsInfoRepository.findByStudentId(studentId)
				.map(StudentDetailsInfoMapper.INSTANCE::fromStudentDetailsEntity).orElse(new StudentDetailsInfoDto());
	}
	
	@Transactional
	public String saveVacatingDayScholarDetails(AllStudentsDetailsViewDto allStudentsDetailsViewDto) {

		List<StudentDetailsInfoEntity> entitiesToUpdate = new ArrayList<>();

		for (AllStudentsDetailsViewDto studentDto : allStudentsDetailsViewDto.getAllStudentsDetailsViewList()) {
			StudentDetailsInfoEntity entity = new StudentDetailsInfoEntity();
			entity.setStudentId(studentDto.getStudentId());
			entity.setDayScholar(studentDto.getDayScholarStatus() != null ? ModelConstants.YES : ModelConstants.NO);
			entity.setVacationCategory(studentDto.getVacationCategoryStatus() != null ? ModelConstants.YES : ModelConstants.NO);
			entitiesToUpdate.add(entity);
		}

		for (StudentDetailsInfoEntity entity : entitiesToUpdate) {
			studentDetailsInfoRepository.updateStudentInfoDetails(entity.getStudentId(), entity.getDayScholar(),
					entity.getVacationCategory(), SecurityCtxUtil.userId(), DateUtility.getNowTimeInstant());
		}
		return Constants.SAVED;
	}

    public List<String> getStudentPreviousInfoDetails(String studentId) {
		return studentDetailsInfoRepository.findByPreviousId(studentId);
	}

	public List<String> getStudentIdsByPreviousId(String studentId) {
		return studentDetailsInfoRepository.getStudentIdByPrevId(studentId, ModelConstants.STATUS_ACTIVE);
	}

	public StudentRoomInfoDTO getStudentRoomDetails(String studentId) {
		return studentDetailsInfoRepository.getStudentRoomDetails(studentId);
	}

}

