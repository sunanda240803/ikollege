package com.iitm.hosteldine.service.student;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.hostel.StudentComplaintConfigurationDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.hostel.StudentComplaintConfigurationMapper;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.model.student.StudentComplaintConfigurationEntity;
import com.iitm.hosteldine.repository.student.StudentComplaintConfigurationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentComplaintConfigurationService {
	private final StudentComplaintConfigurationRepository configurationRepository;
	private final MessageSource messageSource;

	public Page<StudentComplaintConfigurationDto> getStudentComplaintConfigList(PaginationForm form) {
		var pageRequest = PageRequest.of(form.getPage() - 1, form.getSize(), Sort.by("modifiedAt").descending());
		Page<StudentComplaintConfigurationEntity> result = Optional.ofNullable(form.getSearch())
				.filter(search -> !search.isEmpty())
				.map(search -> configurationRepository.getStudentComplaintConfigList(ModelConstants.STATUS_ACTIVE, pageRequest, search))
				.orElseGet(() -> configurationRepository.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE, pageRequest));
		return result.map(StudentComplaintConfigurationMapper.INSTANCE::toDto);
	}
	
	 public List<StudentComplaintConfigurationEntity> getActiveHostelComplaints() {
	        return configurationRepository.findByActiveFlagAndComplaintType( ModelConstants.STATUS_ACTIVE, Constants.HOSTEL);
	}

	public StudentComplaintConfigurationDto getStudentComplaintConfigById(Long id) {
		return configurationRepository.findById(id).map(StudentComplaintConfigurationMapper.INSTANCE::toDto).orElse(new StudentComplaintConfigurationDto());
	}
	 
	 public String getConfiguredEmail(String complaintType, String complaintName) {
		    if (Constants.MESS.equalsIgnoreCase(complaintType)) {
		        Optional<String> configuredEmail = configurationRepository.getEmail(
		                ModelConstants.STATUS_ACTIVE, complaintType, Constants.MESS);
		        return configuredEmail.orElseThrow(() -> new RuntimeException(messageSource.getMessage("message.label.email.complaint.type", null, Locale.getDefault())+complaintType));
		    } else {
		        Optional<String> configuredEmail = configurationRepository.getEmail(
		                ModelConstants.STATUS_ACTIVE, complaintType, complaintName);
		        
		        return configuredEmail.orElseThrow(() -> new RuntimeException(messageSource.getMessage("message.label.email.complaint.type", null, Locale.getDefault())+complaintType));
		    }
		}


	public String saveOrUpdateStudentComplaintConfig(StudentComplaintConfigurationDto studentComplaintConfigurationDto) {
		return Optional.ofNullable(studentComplaintConfigurationDto.getId())
				.filter(id -> id > 0)
				.flatMap(configurationRepository::findById)
				.map(existingEntity -> {
					StudentComplaintConfigurationMapper.INSTANCE.onUpdateEntity(existingEntity, studentComplaintConfigurationDto);
					existingEntity.setComplaintName(Objects.nonNull(existingEntity.getComplaintName())
							&& !existingEntity.getComplaintName().isEmpty()
							&& !existingEntity.getComplaintType().equals(HostelConstants.MESS.getConstants())
							? existingEntity.getComplaintName() : ModelConstants.HYPHEN);
					configurationRepository.save(existingEntity);
					return Constants.UPDATED;
				})
				.orElseGet(() -> {
					StudentComplaintConfigurationEntity entity = StudentComplaintConfigurationMapper.INSTANCE.toEntity(studentComplaintConfigurationDto);
					entity.onCreate();
					entity.setComplaintName(Objects.nonNull(entity.getComplaintName())
							&& !entity.getComplaintName().isEmpty()
							&& !entity.getComplaintType().equals(HostelConstants.MESS.getConstants())
							? entity.getComplaintName() : ModelConstants.HYPHEN);
					configurationRepository.save(entity);
					return Constants.SAVED;
				});
	}

	public boolean deleteStudentComplaintConfig(Long id) throws RecordNotExistsException {
		return configurationRepository.findById(id)
				.map(entity->{
					entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
					configurationRepository.save(entity);
					return true;
				})
				.orElseThrow(() -> new RecordNotExistsException(
						messageSource.getMessage("validation.error.id.not.found", null, Locale.getDefault()
						)));
	}
}
