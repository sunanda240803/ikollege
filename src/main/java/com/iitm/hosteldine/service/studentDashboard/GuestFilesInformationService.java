package com.iitm.hosteldine.service.studentDashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.StudentBioDataFamilyInfoDto;
import com.iitm.hosteldine.dto.studentDashboard.GuestAccommodationRequestDto;
import com.iitm.hosteldine.dto.studentDashboard.GuestFilesInformationDto;
import com.iitm.hosteldine.mapper.studentDashboard.GuestFilesInformationMapper;
import com.iitm.hosteldine.model.studentDashboard.GuestFilesInformationEntity;
import com.iitm.hosteldine.repository.studentDashboard.GuestFilesInformationRepository;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuestFilesInformationService {
	private final GuestFilesInformationRepository guestFilesInformationRepository;
	private final FileService fileService;

	public boolean saveGuestFileUpload(GuestAccommodationRequestDto dto, Long requestId) throws Exception {
		List<GuestFilesInformationEntity> fileEntities = new ArrayList<>();
		String studentId = SecurityCtxUtil.userId().toUpperCase();

		if (dto.getFamilyDetails() != null && !dto.getFamilyDetails().isEmpty()) {
			for (int i = 0; i < dto.getFamilyDetails().size(); i++) {
				StudentBioDataFamilyInfoDto bioData = dto.getFamilyDetails().get(i);
				MultipartFile file = bioData.getChooseFile();
				if (file != null && !file.isEmpty()) {
					String originalFileName = file.getOriginalFilename();
					String[] nameParts = originalFileName.split("\\.");
					String extension = nameParts.length > 1 ? nameParts[nameParts.length - 1]
							: ModelConstants.EMPTY_STRING;
					String timestamp = String.valueOf(System.currentTimeMillis());
					String formattedFileName = studentId + ModelConstants.UNDERSCORE + i + ModelConstants.UNDERSCORE
							+ timestamp + Constants.DOT + extension;
					GuestFilesInformationEntity fileEntity = new GuestFilesInformationEntity();
					fileEntity.setRequestId(requestId);
					fileEntity.setFilename(formattedFileName);
					fileEntity.setDescription(bioData.getDescription());
					fileEntity.setActiveFlag(ModelConstants.STATUS_ACTIVE);
					fileEntities.add(fileEntity);
				}
			}
		}

		if (!fileEntities.isEmpty()) {
			List<GuestFilesInformationEntity> savedEntities = guestFilesInformationRepository.saveAll(fileEntities);
			if (!savedEntities.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public List<GuestFilesInformationDto> getFileUploadByRequestId(Long requestId) {
		return Optional.ofNullable(guestFilesInformationRepository.findAllByActiveFlagAndRequestId(ModelConstants.STATUS_ACTIVE,requestId))
				.orElse(Collections.emptyList()).stream().map(GuestFilesInformationMapper.INSTANCE::fromGuestFilesInformationEntity)
				.collect(Collectors.toList());

	}

	public ByteArrayResource downloadFile(String fileName) throws Exception {
		String uploadDoc = SimsConfigDataService.BIO_DATA_PARENT_PROOF;
		byte[] fileData = fileService.getDecodedFile(uploadDoc, fileName);
		return new ByteArrayResource(fileData);
	}
}



