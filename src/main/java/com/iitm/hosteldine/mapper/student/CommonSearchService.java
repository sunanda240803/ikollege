package com.iitm.hosteldine.mapper.student;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.student.StudentSearchViewDto;
import com.iitm.hosteldine.entity.UserManagementEntity;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.model.student.StudentSearchViewEntity;
import com.iitm.hosteldine.repository.UserManagementRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.student.AllStudentsDetailsViewDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;

import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommonSearchService {

	private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;
    private final StudentDetailsInfoRepository studentDetailsInfoRepository;
    private final UserManagementRepository userManagementRepository;

    public Page<StudentSearchViewDto> getStudentDetailsList(PaginationForm form) {
		Page<StudentSearchViewEntity> entityList;

		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());

		String searchCriteria = form.getAdditionalParam().get("searchCriteria").toString();
		String field = form.getAdditionalParam().get("field").toString();
		String searchString = form.getAdditionalParam().get("searchString").toString();
        String searchString1 = (searchString != null && !searchString.isEmpty()) ? searchString.trim().toLowerCase() : "";

        if (searchCriteria.equals(ModelConstants.ACTIVE)) {
            entityList = allStudentsDetailsViewRepository.getActiveStudentDetails(ModelConstants.ACTIVE,ModelConstants.STATUS_ACTIVE, field,
                    searchString1,
                    pageable);
        } else {
            entityList = allStudentsDetailsViewRepository.getStudentDetails(field,
                    searchString1,
                    pageable);
        }
		form.getAdditionalParam().put("tableLength", (entityList != null && entityList.hasContent()) ? 1 : 0);
		return Objects.requireNonNull(entityList).map(StudentSearchViewMapper.INSTANCE::toDto);
	}

    @Transactional
    public String updateStudentStatus(String studentId, String activeFlag) {
        Optional<StudentDetailsInfoEntity> student = studentDetailsInfoRepository.findByStudentIdAndActiveFlag(studentId, activeFlag);
        Optional<UserManagementEntity> user = userManagementRepository.findByIdUsernameIgnoreCaseAndActiveFlag(studentId, activeFlag);
        if (student.isEmpty() || user.isEmpty()) {
            return Constants.ERROR;
        }

        String newFlag = ModelConstants.STATUS_ACTIVE.equals(activeFlag) ? ModelConstants.STATUS_INACTIVE : ModelConstants.STATUS_ACTIVE;

        StudentDetailsInfoEntity studentDetailsInfoEntity = student.get();
        studentDetailsInfoEntity.setActiveFlag(newFlag);
        studentDetailsInfoRepository.save(studentDetailsInfoEntity);

        UserManagementEntity userManagementEntity = user.get();
        userManagementEntity.setActiveFlag(newFlag);
        userManagementRepository.save(userManagementEntity);
        return Constants.UPDATED;
    }
}
