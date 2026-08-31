package com.iitm.hosteldine.service.mess;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.iitm.hosteldine.dto.dean.BulkApprovalRejectDto;
import com.iitm.hosteldine.dto.dean.DeanMessRebateDto;
import com.iitm.hosteldine.service.dean.DeanMessRebateService;
import com.iitm.hosteldine.util.MCrypt;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.dto.mess.MessRebateDto;
import com.iitm.hosteldine.dto.mess.MessRebateWorkflowDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.mess.MessRebateMapper;
import com.iitm.hosteldine.mapper.mess.MessRebateWorkflowMapper;
import com.iitm.hosteldine.model.dashboard.student.WorkflowMasterEntity;
import com.iitm.hosteldine.model.mess.MessRebateEntity;
import com.iitm.hosteldine.model.mess.MessRebateWorkflowEntity;
import com.iitm.hosteldine.repository.StudentBioDataFormDetailRepository;
import com.iitm.hosteldine.repository.mess.MessRebateRepository;
import com.iitm.hosteldine.repository.mess.MessRebateWorkflowRepository;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.service.dashboard.student.WorkflowMasterService;
import com.iitm.hosteldine.service.helper.MessRebateServiceHelper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessRebateService {

	private final FileService fileService;
	private final MessRebateRepository messRebateRepository;
	private final WorkflowMasterService workflowMasterService;
	private final MessRebateWorkflowRepository messRebateWorkflowRepository;
	private final StudentBioDataService studentBioDataService;
	private final StudentBioDataFormDetailRepository studentBioDataFormDetailRepository;
	private final MessRebateServiceHelper messRebateServiceHelper;
	private final DeanMessRebateService deanMessRebateService;

	public String getLastestStudentMessRebateStatus(String studentId) throws Exception {
		MessRebateEntity entity = messRebateRepository.findTopByStudentIdAndActiveFlagOrderByModifiedAtDesc(studentId,
				ModelConstants.STATUS_ACTIVE);
		if (entity == null) {
			return Constants.HYPHEN;
		}
		return entity.getApprovalStatus() != null ? entity.getApprovalStatus() : "";
	}

	public Page<MessRebateDto> getStudentMessRebateList(PaginationForm form) throws Exception {
		Page<MessRebateEntity> messRebateDto = null;
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("modifiedAt").descending());
		if (form.getSearch() == null || form.getSearch().isEmpty()) {
			messRebateDto = messRebateRepository.findAllByStudentIdAndActiveFlag(SecurityCtxUtil.userId().toUpperCase(),
					ModelConstants.STATUS_ACTIVE, pageable);
		}
		return messRebateDto.map(MessRebateMapper.INSTANCE::fromMessRebateEntity);
	}

	public MessRebateDto getStudentMessRebateById(Long id) throws Exception {
		MessRebateEntity entity = messRebateRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE);
		MessRebateDto messRebateDto = new MessRebateDto();
		if (entity != null) {
			messRebateDto = MessRebateMapper.INSTANCE.fromMessRebateEntity(entity);
			return messRebateDto;
		}
		return messRebateDto;
	}

	public List<MessRebateWorkflowDto> getStudentMessRebateWorkflowDetails(Long id) throws Exception {
		List<MessRebateWorkflowEntity> entities = messRebateWorkflowRepository
				.findAllByRequestIdAndActiveFlagAndAuthenticationTypeOrderByModifiedAtDesc(id,
						ModelConstants.STATUS_ACTIVE, ModelConstants.AUTHENTICATION_TYPE_APPROVAL);

	    if (entities != null && !entities.isEmpty()) {
	        return entities.stream()
	                       .map(MessRebateWorkflowMapper.INSTANCE::fromMessRebateWorkflowEntity)
	                       .toList();
	    }
	    return new ArrayList<>();
	}

	@Transactional
	public String saveAndUpdate(MessRebateDto messRebateDto, String url, HttpServletRequest request) throws Exception {
		MessRebateEntity entity = MessRebateMapper.INSTANCE.messRebateEntity(messRebateDto);
		String fileUpload = null;
		String studentId = SecurityCtxUtil.userId().toUpperCase();

		if (messRebateDto.getFile() != null && !messRebateDto.getFile().isEmpty()) {
			String originalFileName = messRebateDto.getFile().getOriginalFilename();
			String fileExtension = "";

			if (originalFileName != null && originalFileName.contains(".")) {
				fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
			}
			fileUpload = studentId + ModelConstants.UNDERSCORE + FileService.MESS_REBATE_DOCUMENT
					+ ModelConstants.UNDERSCORE + System.currentTimeMillis() + fileExtension;

			entity.setFileUpload(fileUpload);
		}

		
		if (messRebateDto.getGuideName() != null) {
			studentBioDataFormDetailRepository.updateFacultyName(ModelConstants.STATUS_ACTIVE, studentId, messRebateDto.getGuideName());
		    entity.setGuideName(messRebateDto.getGuideName());
		}

		if (messRebateDto.getGuideEmail() != null) {
			studentBioDataFormDetailRepository.updateFacultyEmail(ModelConstants.STATUS_ACTIVE, studentId, messRebateDto.getGuideEmail());
		    entity.setGuideEmail(messRebateDto.getGuideEmail());
		}
		
		StudentBioDataFormDetailDto studentDetails = null;

		if (messRebateDto.getGuideName() == null || messRebateDto.getGuideEmail() == null) {
		    studentDetails = studentBioDataService.getStudentDetails(studentId);
		}

		if (messRebateDto.getGuideName() == null) {
		    entity.setGuideName(studentDetails.getFacultyName());
		}

		if (messRebateDto.getGuideEmail() == null) {
		    entity.setGuideEmail(studentDetails.getFacultyEmail());
		}

		entity.setStudentId(studentId);
		entity.setCancelStatus(ModelConstants.STATUS_INACTIVE);
		entity.setApprovalStatus(WorkflowStatus.PENDING.getStatus());
		entity.setRebateStatus(WorkflowStatus.DEFAULT.getStatus());
		entity.onCreate();
		MessRebateEntity savedEntity = messRebateRepository.saveAndFlush(entity);

		if (messRebateDto.getFile() != null && !messRebateDto.getFile().isEmpty()) {
			byte[] fileData = messRebateDto.getFile().getBytes();
			fileService.encodeFile(SimsConfigDataService.MESS_REBATE_DOCUMENT, fileData, fileUpload);
		}

		List<WorkflowMasterEntity> workflowMasterEntity = workflowMasterService
				.getAllEntityListByCategory(WorkflowMasterService.MESS_REBATE);

		List<MessRebateWorkflowEntity> workflowMasterEntityList = new ArrayList<>();
		if (workflowMasterEntity != null && !workflowMasterEntity.isEmpty()) {
			boolean pendingStatusAssigned = false;

			for (WorkflowMasterEntity masterEntity : workflowMasterEntity) {
				MessRebateWorkflowEntity workflowEntity = new MessRebateWorkflowEntity();

				if (masterEntity.getAuthorityType().equals(WorkflowMasterService.GUIDE)) {
					MessRebateWorkflowMapper.INSTANCE.guideWorkflowEntity(workflowEntity, masterEntity, savedEntity);
				} else {
					MessRebateWorkflowMapper.INSTANCE.workflowEntity(workflowEntity, masterEntity, savedEntity);
				}

				if (workflowEntity.getAuthenticationType().equals(ModelConstants.AUTHENTICATION_TYPE_INFORMATION)) {
					workflowEntity.setApprovalStatus(WorkflowStatus.APPROVED.getStatus());
				} else if (workflowEntity.getAuthenticationType().equals(ModelConstants.AUTHENTICATION_TYPE_APPROVAL)) {
					if (!pendingStatusAssigned) {
						workflowEntity.setApprovalStatus(WorkflowStatus.PENDING.getStatus());
						pendingStatusAssigned = true;
					} else {
						workflowEntity.setApprovalStatus(WorkflowStatus.DEFAULT.getStatus());
					}
				}
				workflowEntity.onCreate();
				workflowMasterEntityList.add(workflowEntity);
			}

			if (!workflowMasterEntityList.isEmpty()) {
				messRebateWorkflowRepository.saveAll(workflowMasterEntityList);
			}
		}
		
		messRebateServiceHelper.sendMailToValidators(studentId, entity.getId(), url, request);
		return Constants.SAVED;
	}

	public ByteArrayResource downloadFile(String fileName) throws Exception {
		String reportDoc = SimsConfigDataService.MESS_REBATE_DOCUMENT;
		byte[] fileData = fileService.getDecodedFile(reportDoc, fileName);
		return new ByteArrayResource(fileData);
	}

	@Value("${message.validation.rebate.from.date.exists}")
	private String rebateFromDateExists;

	public String checkFromDateAndToDateExists(LocalDate rebateFrom, LocalDate rebateTo) throws Exception {
		Integer count = messRebateRepository.checkFromDateAndToDateExists(SecurityCtxUtil.userId().toUpperCase(), rebateFrom, rebateTo, WorkflowStatus.REJECTED.getStatus());
		if (count > 0 ) {
			return rebateFromDateExists;
		}
		return null;
	}

	public String bulkApproveReject(BulkApprovalRejectDto dto, String url, HttpServletRequest request) {
		try {
			for (String encryptedId : dto.getEncryptedIds()) {
				boolean saved = processBulkApproval(encryptedId, dto, url, request);
				if (!saved) {
					return Constants.FAILURE;
				}
			}
			return Constants.SAVED;
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.ERROR;
		}
	}

	private boolean processBulkApproval(String s, BulkApprovalRejectDto dto, String url, HttpServletRequest request) {
		try {
			MCrypt crypt = new MCrypt();
			String decrypted = crypt.decryptToString(s);
			String[] split = decrypted.split(Constants.BACKTICK);
			String studentId = split[0];
			Long id = Long.valueOf(split[1]);
			String authorityType = split[2];

			DeanMessRebateDto deanMessRebateDto = deanMessRebateService.getMessRebateDetails(studentId, id, authorityType);
			deanMessRebateDto.setWorkflowApprovalNotes(Objects.nonNull(dto.getApprovalNote()) ? dto.getApprovalNote() : ModelConstants.EMPTY_STRING);
			deanMessRebateDto.setWorkflowRejectReason(Objects.nonNull(dto.getRejectionReason()) ? dto.getRejectionReason() : ModelConstants.EMPTY_STRING);
			deanMessRebateDto.setStudentId(studentId);
			if (Objects.nonNull(dto.getStayFromDate())) {
				deanMessRebateDto.setFromDateRebate(dto.getStayFromDate().toString());
			}
			if (Objects.nonNull(dto.getStayToDate())) {
				deanMessRebateDto.setToDateRebate(dto.getStayToDate().toString());
			}
			return deanMessRebateService.updateMessRebateStatusFromView(deanMessRebateDto, dto.getApprovalStatus(), url, request);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
