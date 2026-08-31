package com.iitm.hosteldine.service.dashboard.student;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.dashboard.student.WorkflowMasterDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.dashboard.student.WorkflowMasterMapper;
import com.iitm.hosteldine.model.dashboard.student.WorkflowMasterEntity;
import com.iitm.hosteldine.repository.dashboard.student.WorkflowMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class WorkflowMasterService {

	public static final String GUIDE = "Guide";
	public static final String MESS_REBATE = "MESS-REBATE";
	public static final String VACATING_STUDENTS = "VACATING-STUDENTS";
	public static final String HOSTEL_ACCOMMODATION = "HOSTEL-ACCOMMODATION";

    private final WorkflowMasterRepository workflowMasterRepository;
	private final MessageSource messageSource;

	public Page<WorkflowMasterDto> getWorkflowMasterList(PaginationForm form) {
		var pageRequest = PageRequest.of(form.getPage() - 1, form.getSize(), Sort.by("modifiedAt").descending());
		Page<WorkflowMasterEntity> result = Optional.ofNullable(form.getSearch())
				.filter(search -> !search.isEmpty())
				.map(search -> workflowMasterRepository.getWorkflowMasterList(ModelConstants.STATUS_ACTIVE, pageRequest, search))
				.orElseGet(() -> workflowMasterRepository.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE, pageRequest));
		return result.map(WorkflowMasterMapper.INSTANCE::toDto);
	}

	public WorkflowMasterDto getWorkflowMasterById(Long id) {
		return workflowMasterRepository.findById(id).map(WorkflowMasterMapper.INSTANCE::toDto).orElse(new WorkflowMasterDto());
	}

	public String saveOrUpdateWorkflowMaster(WorkflowMasterDto workflowMasterDto) {
		return Optional.ofNullable(workflowMasterDto.getId())
				.filter(id -> id > 0L)
				.flatMap(workflowMasterRepository::findById)
				.map(existingEntity -> {
					workflowMasterRepository.save(existingEntity);
					return Constants.UPDATED;
				})
				.orElseGet(() -> {
					WorkflowMasterEntity entity = WorkflowMasterMapper.INSTANCE.toEntity(workflowMasterDto);
					entity.setId(null);
					entity.setAuthenticationType(HostelConstants.A.getConstants());
					entity.onCreate();
					workflowMasterRepository.save(entity);
					return Constants.SAVED;
				});
	}

	public boolean deleteWorkflowMaster(Long id) throws RecordNotExistsException {
		return workflowMasterRepository.findById(id)
				.map(entity->{
					entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
					workflowMasterRepository.save(entity);
					return true;
				})
				.orElseThrow(() -> new RecordNotExistsException(
						messageSource.getMessage("validation.error.id.not.found", null, Locale.getDefault()
						)));
	}

    public List<WorkflowMasterDto> getAllWorkflowMastersByCategory(String category) {
        return workflowMasterRepository.getWorkflowMasterList(category, ModelConstants.STATUS_ACTIVE)
                .stream()
                .map(WorkflowMasterMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }
    
	public List<WorkflowMasterEntity> getAllEntityListByCategory(String category) {
		return workflowMasterRepository.findAllByCategoryAndActiveFlagOrderByAuthenticationTypeAscApprovalLevelAsc(
				category, ModelConstants.STATUS_ACTIVE);
	}
}
