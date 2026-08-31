package com.iitm.hosteldine.service;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.StudentBioDataFamilyInfoDto;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.mapper.StudentBioDataFamilyInfoMapper;
import com.iitm.hosteldine.model.StudentBioDataFamilyInfoEntity;
import com.iitm.hosteldine.repository.StudentBioDataFamilyInfoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentBioDataFamilyInfoService {
    private final StudentBioDataFamilyInfoRepository studentBioDataFamilyInfoRepository;
    private final FileService fileService;


    void saveFamilyDetails(StudentBioDataFormDetailDto form, boolean isNew, boolean adminSave) {
        List<StudentBioDataFamilyInfoDto> familyList = form.getFamilyDetails() != null ? form.getFamilyDetails() : new ArrayList<>();
        List<StudentBioDataFamilyInfoDto> familyDtoList = new ArrayList<>();
        ArrayList<StudentBioDataFamilyInfoEntity> familyEntityList = new ArrayList<>();
        if (isNew && ModelConstants.YES.equals(form.getGuardianStatus())) {
            form.getGuardianDetails().setRelationType(ModelConstants.RELATION_GUARDIAN);
            familyList.add(form.getGuardianDetails());
        }
        familyList.forEach(familyMemberDetails -> {
            StudentBioDataFamilyInfoEntity entity = null;

            boolean hasValidId = familyMemberDetails.getId() != null && familyMemberDetails.getId() > 0;
            boolean hasRelationName = Strings.isNotEmpty(familyMemberDetails.getRelationName());
            boolean hasFileToUpload = familyMemberDetails.getChooseFile() != null && !familyMemberDetails.getChooseFile().isEmpty();

            if (isNew && hasRelationName) {
                System.out.println("==== BEFORE MAPPING ====");
                System.out.println("DTO RelationName: " + familyMemberDetails.getRelationName());
                System.out.println("DTO RelationType: " + familyMemberDetails.getRelationType());

                entity = StudentBioDataFamilyInfoMapper.INSTANCE.toStudentBioDataFamilyInfoEntity(familyMemberDetails);

                System.out.println("==== AFTER MAPPING ====");
                System.out.println("ENTITY RelationName: " + entity.getRelationName());
                System.out.println("ENTITY RelationType: " + entity.getRelationType());

                entity.setBioDataId(form.getId());

                System.out.println("==== BEFORE formProofName() ====");
                System.out.println("DTO RelationType used for filename: " + familyMemberDetails.getRelationType());

                formProofName(form.getStudentId(), familyMemberDetails);

                System.out.println("==== AFTER formProofName() ====");
                System.out.println("Generated FileName: " + familyMemberDetails.getProofFileName());
                familyEntityList.add(entity);

            } else if (!isNew) {
                if (adminSave) {
					if (hasRelationName || hasValidId) {
						entity = hasValidId
								? studentBioDataFamilyInfoRepository.findById(familyMemberDetails.getId()).orElse(null)
								: new StudentBioDataFamilyInfoEntity();

						if (entity == null) {
							entity = new StudentBioDataFamilyInfoEntity();
						} else {
							familyMemberDetails.setExistingProofFileName(entity.getProofFileName());
						}

						entity.setBioDataId(form.getId());
						formProofName(form.getStudentId(), familyMemberDetails);
						StudentBioDataFamilyInfoMapper.INSTANCE.updateDtoToEntity(familyMemberDetails, entity);
						familyEntityList.add(entity);
					}
                } else if (hasValidId && hasFileToUpload) {
                    entity = studentBioDataFamilyInfoRepository.findById(familyMemberDetails.getId()).orElse(null);
                    if (entity != null) {
                        formProofName(form.getStudentId(), familyMemberDetails);
                        StudentBioDataFamilyInfoMapper.INSTANCE.updateDtoToEntity(familyMemberDetails, entity);
                        familyEntityList.add(entity);
                    }
                }
            }

            if (entity != null) {
                familyMemberDetails.setRelationType(entity.getRelationType());
                familyMemberDetails.setId(entity.getId());
                familyDtoList.add(familyMemberDetails);
            }
        });

        studentBioDataFamilyInfoRepository.saveAll(familyEntityList);
        familyEntityList.forEach(memberEntity -> familyDtoList.stream()
                .filter(memberDto -> Objects.equals(memberDto.getRelationType(), memberEntity.getRelationType())
                        && memberDto.getRelationName().equals(memberEntity.getRelationName()))
                .findFirst().ifPresent(memberDto -> {
                    memberDto.setId(memberEntity.getId());
                    memberDto.setProofFileName(memberEntity.getProofFileName());
                    memberDto.setProofType(memberEntity.getProofType());
                }));
        if (!familyDtoList.isEmpty()) {
            saveBioDataFile(familyDtoList, form.getStudentId());
        }
    }
    public boolean saveBioDataFile(List<StudentBioDataFamilyInfoDto> familyInfoList, String studentId) {
        if (familyInfoList == null || familyInfoList.isEmpty())
            return false;

        List<StudentBioDataFamilyInfoDto> filteredFamilyInfoList = familyInfoList.stream()
                .filter(familyInfo -> familyInfo.getId() != null
                        && familyInfo.getChooseFile() != null && !familyInfo.getChooseFile().isEmpty())
                .peek(familyInfo -> {
                    if (familyInfo.getProofFileName() == null) {
                        StudentBioDataFamilyInfoEntity entity = studentBioDataFamilyInfoRepository.findById(familyInfo.getId()).orElse(null);
                        if (entity != null) {
                            formProofName(studentId, familyInfo);
                            StudentBioDataFamilyInfoMapper.INSTANCE.updateDtoToEntity(familyInfo, entity);
                            studentBioDataFamilyInfoRepository.save(entity);
                        }
                    }
                })
                .toList();

        if (!filteredFamilyInfoList.isEmpty()) {
            filteredFamilyInfoList.forEach(familyInfo -> {
                String proofFileName = familyInfo.getProofFileName();
                if (proofFileName != null && !proofFileName.isEmpty()) {
                    try {
                        if(fileService.deleteFile(ModelConstants.FILE_BIO_DATA_PARENT_PROOF, familyInfo.getExistingProofFileName())) {
                            fileService.encodeFile(ModelConstants.FILE_BIO_DATA_PARENT_PROOF, familyInfo.getChooseFile().getBytes(),
                                    familyInfo.getProofFileName());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return true;
        }
        return false;
    }


    public List<StudentBioDataFamilyInfoDto> getFileUploadDetails(List<Long> guestIds, String studentId) {
        List<Object[]> studentBioDataFamilyInfoList = studentBioDataFamilyInfoRepository.getFileUploadDetails(studentId, guestIds, ModelConstants.STATUS_ACTIVE);
        if (studentBioDataFamilyInfoList != null && !studentBioDataFamilyInfoList.isEmpty()) {
            return studentBioDataFamilyInfoList.stream().filter(Objects::nonNull).map(obj -> {
                StudentBioDataFamilyInfoDto familyDto = new StudentBioDataFamilyInfoDto();
                familyDto.setProofFileName(obj.length > 0 && obj[0] != null ? obj[0].toString() : null);
                familyDto.setRelationName(obj.length > 0 && obj[1] != null ? obj[1].toString() : Strings.EMPTY);
                familyDto.setRelationType(obj.length > 0 && obj[2] != null ? obj[2].toString() : Strings.EMPTY);
                return familyDto;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public boolean checkFileById(String bioId) {
        Optional<String> studentBioDetails = studentBioDataFamilyInfoRepository.checkFileName(
                Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase(), Long.parseLong(bioId), ModelConstants.STATUS_ACTIVE);
        return studentBioDetails.isPresent() && !studentBioDetails.get().isEmpty();
    }

    public void formProofName(String studentId, StudentBioDataFamilyInfoDto familyInfo) {
        familyInfo.setProofFileName(null);
        if (familyInfo.getChooseFile() != null) {
            String fileNameWithExtension = familyInfo.getChooseFile().getOriginalFilename();
            System.out.println("Original file chosen for relation [" + familyInfo.getRelationName() + "] : " + fileNameWithExtension);
            if (!Objects.requireNonNull(fileNameWithExtension).isEmpty()) {
                String fileExtension = fileNameWithExtension.substring(fileNameWithExtension.lastIndexOf(Constants.DOT));
                String unique = UUID.randomUUID().toString().substring(0, 8);
                String proofFileName = familyInfo.getRelationType() + ModelConstants.UNDERSCORE + unique;
                String formattedFileName = studentId + ModelConstants.UNDERSCORE + proofFileName + fileExtension;
                familyInfo.setProofFileName(formattedFileName);
                System.out.println("Formatted file name to be stored : " + formattedFileName);
            }
        }
    }
}
