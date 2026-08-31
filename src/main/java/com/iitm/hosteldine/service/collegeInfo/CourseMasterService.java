package com.iitm.hosteldine.service.collegeInfo;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.collegeInfo.CourseMasterDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.collegeInfo.CourseMasterMapper;
import com.iitm.hosteldine.model.collegeInfo.CourseMasterEntity;
import com.iitm.hosteldine.repository.CourseAllocationInfoRepository;
import com.iitm.hosteldine.repository.collegeInfo.CourseMasterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseMasterService {
	private final CourseMasterRepository courseMasterRepository;
	private final MessageSource messageSource;
	private final CourseAllocationInfoRepository courseAllocationInfoRepository;

	public List<CourseMasterDto> getCourseMasterList(CourseMasterDto courseMasterDto) {
		List<Object[]> courseMasterList = courseMasterRepository.findAllByActiveFlagOrderByCourseMasterName(ModelConstants.STATUS_ACTIVE);
		return courseMasterList.stream()
		        .map(objects -> {
		            CourseMasterDto dto = new CourseMasterDto();
		            dto.setCourseMasterId((Long) objects[0]);
		            dto.setCourseMasterName((String) objects[1]);
		            dto.setDescription((String) objects[2]);
		            dto.setDegreeAwarded((String) objects[3]);
		            dto.setAffiliation((String) objects[4]);
		            dto.setDepartmentName((String) objects[5]);
		            dto.setCourseMasterHead((String) objects[6]);
		            return dto;
		        })
		        .collect(Collectors.toList());
	}

	public CourseMasterDto getCourseMasterDetailsById(long courseMasterId) {
		return courseMasterRepository
				.findByCourseMasterIdAndActiveFlag(courseMasterId, ModelConstants.STATUS_ACTIVE)
				.map(CourseMasterMapper.INSTANCE::fromCourseMasterEntity).orElse(null);
	}

	public String saveUpdateCourseMaster(CourseMasterDto dto) throws Exception {
		String result = Optional.ofNullable(dto.getCourseMasterId()).filter(id -> (id != null && id > 0))
				.flatMap(id -> courseMasterRepository.findByCourseMasterIdAndActiveFlag(dto.getCourseMasterId(),
						ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					CourseMasterMapper.INSTANCE.onUpdateCouseMasterEntity(existingEntity, dto);
					courseMasterRepository.save(existingEntity); 
					return Constants.UPDATED;
				}).orElseGet(() -> {
					CourseMasterEntity newEntity = CourseMasterMapper.INSTANCE.onSaveEntity(dto);
					newEntity.setCourseMasterId(null);
					courseMasterRepository.save(newEntity);
					return Constants.SAVED;
				});
		return result;
	}

	public Boolean deleteCourseMasterById(long id) throws Exception {
		if (courseAllocationInfoRepository.findAllByCourseIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
				.size() > 0) {
			throw new RecordNotExistsException(
					messageSource.getMessage("message.validation.error.course.master.assigned", null, Locale.getDefault()));
		} else {
			CourseMasterEntity entity = courseMasterRepository.findByCourseMasterId(id).orElse(null);
			if (entity != null) {
				entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
				entity.onUpdate();
				return courseMasterRepository.save(entity).getCourseMasterId() != null;
			} else {
				throw new RecordNotExistsException(
						messageSource.getMessage("message.validation.error.id.not.found", null, Locale.getDefault()));
			}
		}
	}

	public boolean checkCourseMasterNameExist(String courseMasterName, long id) {
		return courseMasterRepository
				.findByActiveFlagAndCourseMasterNameIgnoreCaseAndCourseMasterIdNot(
						ModelConstants.STATUS_ACTIVE, courseMasterName,id).isPresent();
	}

	public boolean checkCourseCodeExist(String courseCode, long id) {
		return courseMasterRepository
				.findByActiveFlagAndCourseMasterHeadIgnoreCaseAndCourseMasterIdNot(
						ModelConstants.STATUS_ACTIVE, courseCode,id).size() > 0;
	}

	public Page<CourseMasterDto> getCourseMasterList(PaginationForm form) {
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("cm.courseMasterName").ascending());
		Page<Object[]> result;
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			result = courseMasterRepository.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE, pageable);
		} else {
			result = courseMasterRepository.findCourseMasterDetailsAndActiveFlag(form.getSearch(),
					ModelConstants.STATUS_ACTIVE, pageable);
		}
		return result.map(record -> {
			CourseMasterDto dto = new CourseMasterDto();
			dto.setCourseMasterId((Long) record[0]);
			dto.setCourseMasterName((String) record[1]);
			dto.setDescription((String) record[2]);
			dto.setDegreeAwarded((String) record[3]);
			dto.setAffiliation((String) record[4]);
			dto.setDepartmentName((String) record[5]);
			dto.setCourseMasterHead((String) record[6]);
			return dto;
		});
	}
}
