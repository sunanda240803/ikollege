package com.iitm.hosteldine.service.collegeInfo;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.collegeInfo.DepartmentDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.mapper.collegeInfo.DepartmentMapper;
import com.iitm.hosteldine.model.collegeInfo.DepartmentEntity;
import com.iitm.hosteldine.repository.collegeInfo.CourseMasterRepository;
import com.iitm.hosteldine.repository.collegeInfo.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {
	private final DepartmentRepository departmentRepository;
	private final MessageSource messageSource;
	private final CourseMasterRepository courseMasterRepository;

	public List<DepartmentDto> getDepartmentList() {
		return departmentRepository.findAllByActiveFlagOrderByDepartmentName(ModelConstants.STATUS_ACTIVE)
				.stream()
				.map(DepartmentMapper.INSTANCE::fromDepartmentEntity)
				.collect(Collectors.toList());
		
	}

	public DepartmentDto getDepartmentDetailsById(long id) {
		return departmentRepository
				.findByDepartmentIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
				.map(DepartmentMapper.INSTANCE::fromDepartmentEntity).orElse(null);
	}

	public String saveUpdateDepartment(DepartmentDto dto) throws Exception {
		String result = Optional.ofNullable(dto.getDepartmentId()).filter(id -> (id != null && id > 0))
				.flatMap(id -> departmentRepository.findByDepartmentIdAndActiveFlag(dto.getDepartmentId(),
						ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					DepartmentMapper.INSTANCE.onUpdateDepartmentEntity(existingEntity, dto);
					departmentRepository.save(existingEntity); 
					return Constants.UPDATED;
				}).orElseGet(() -> {
					DepartmentEntity newEntity = DepartmentMapper.INSTANCE.onSaveEntity(dto);
					newEntity.setDepartmentId(null);
					departmentRepository.save(newEntity); 
					return Constants.SAVED;
				});
		return result;

	}

	public boolean checkDepartmentNameExist(String departmentName, long id) {
		return departmentRepository
				.findByActiveFlagAndDepartmentNameIgnoreCaseAndDepartmentIdNot(
						ModelConstants.STATUS_ACTIVE, departmentName,id).isPresent();
		
	}

	public List<DepartmentDto> getDepartmentNameList() {
		List<DepartmentDto> departmentList = departmentRepository.getDepartmentName().stream().map(o -> {
			DepartmentDto dto = new DepartmentDto();
			dto.setDepartmentName(o[0] != null ? o[0].toString() : null);
			dto.setDepartmentId(o[1] != null ? ((Number) o[1]).longValue() : null);
			return dto;
		}).collect(Collectors.toList());

		// Sort the list by departmentName in ascending order
		return departmentList.stream()
	            .sorted(Comparator.comparing(DepartmentDto::getDepartmentName, String.CASE_INSENSITIVE_ORDER))
	            .collect(Collectors.toList());
		
	}

	public Boolean deleteDepartmentById(long id) throws Exception {
		Long count = courseMasterRepository.countByDepartmentIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE);
		if (count > 0) {
			throw new RecordNotExistsException(
					messageSource.getMessage("message.validation.error.department.assigned", null, Locale.getDefault()));
		} else {
			DepartmentEntity entity = departmentRepository.findByDepartmentId(id).orElse(null);
			if (entity != null) {
				entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
				entity.onUpdate();
				return departmentRepository.save(entity).getDepartmentId() != null;
			} else {
				throw new RecordNotExistsException(
						messageSource.getMessage("message.validation.error.id.not.found", null, Locale.getDefault()));
			}
		}
	}
}
