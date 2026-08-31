package com.iitm.hosteldine.service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.form.common.ArchiveProgress;
import com.iitm.hosteldine.model.ArchiveLogEntity;
import com.iitm.hosteldine.model.IITWCandidatePhotoEntity;
import com.iitm.hosteldine.model.StudentBioDataFormDetailEntity;
import com.iitm.hosteldine.repository.ArchiveLogRepository;
import com.iitm.hosteldine.repository.ArchiveTableMasterRepository;
import com.iitm.hosteldine.repository.IITWCandidatePhotoRepository;
import com.iitm.hosteldine.repository.StudentBioDataFormDetailRepository;
import com.iitm.hosteldine.util.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchiveService {
    private final StudentBioDataFormDetailRepository studentBioDataFormDetailRepository;
    private final FileService fileService;
    private final IITWCandidatePhotoRepository iitwCandidatePhotoRepository;
    private final ArchiveLogRepository archiveLogRepository;
    private final ArchiveTableMasterRepository archiveTableMasterRepository;
    private final VacuumService vacuumService;
    public static final ArchiveProgress[] archiveProgress = new ArchiveProgress[3];
    private final InMemoryLogService logService;
    private final Utility utility;

    public void archiveCandidatePhotos(String tag) {
        int index = 0;
        if (archiveProgress[index] == null || !archiveProgress[index].isRunning()) {
            archiveProgress[index] = new ArchiveProgress();
            archiveProgress[index].setRunning(true);
            archiveProgress[index].setStatus("Archive Started");
            //check path
            int pageCount = 0;
            int pageSize;
            int pageLimit = 100;
            int totalCount = 0;
            archiveProgress[index].setMaxRecords(iitwCandidatePhotoRepository.countAllByActiveFlagAndImageIsNotNull(ModelConstants.STATUS_ACTIVE));
            archiveProgress[index].setProgress(0);
            Long[] totalTimeTaken = new Long[]{0L};
            do {
                long queryTime = System.currentTimeMillis();
                List<IITWCandidatePhotoEntity> candidateList = iitwCandidatePhotoRepository
                        .findAllByActiveFlagAndImageIsNotNull(ModelConstants.STATUS_ACTIVE, PageRequest.of(pageCount, pageLimit));
                long queryExecutionDuration = System.currentTimeMillis() - queryTime;
                archiveProgress[index].setStatus("Processing");
                pageSize = candidateList.size();
                totalCount += pageSize;
                long totalQueryEt = ((archiveProgress[index].getMaxRecords() - totalCount) / pageLimit) * queryExecutionDuration;
                log.debug("Processing next {} records with the total of {}", pageSize, totalCount);
                candidateList.forEach(candidate -> {
                    long currentTime = System.currentTimeMillis();
                    try {
                        String fileName = candidate.getId() + "_";
                        if (candidate.getImage() != null) {
                            fileService.encodeFile(ModelConstants.IMAGE_CANDIDATE_PROFILE, candidate.getImage(), fileName + "profile");
                            candidate.setImage(null);
                        }
                        iitwCandidatePhotoRepository.saveAndFlush(candidate);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    calculateETC(index, totalTimeTaken, totalQueryEt, currentTime);
                });
            } while (pageSize == pageLimit);
            archiveProgress[index].setStatus("Archive Complete");
            log.debug("Total processed candidate count: {} records", totalCount);
            vacuumService.vacuumDatabase();
            archiveProgress[index].setRunning(false);
        }
    }

    public void archiveStudentPhotos(String tag) {
        int index = 1;
        if (archiveProgress[index] == null || !archiveProgress[index].isRunning()) {
            archiveProgress[index] = new ArchiveProgress();
            archiveProgress[index].setRunning(true);
            archiveProgress[index].setStatus("Archive Started");
            int pageCount = 0;
            int pageSize;
            int pageLimit = 100;
            int totalCount = 0;
            archiveProgress[index].setMaxRecords(studentBioDataFormDetailRepository.countAllByActiveFlagAndImageBytesIsNotNull(ModelConstants.STATUS_ACTIVE));
            archiveProgress[index].setProgress(0);
            Long[] totalTimeTaken = new Long[]{0L};
            do {
                long queryTime = System.currentTimeMillis();
                List<StudentBioDataFormDetailEntity> studentBioDataFormDetailEntityList = studentBioDataFormDetailRepository
                        .findAllByActiveFlagAndImageBytesIsNotNull(ModelConstants.STATUS_ACTIVE, PageRequest.of(pageCount, pageLimit));
                long queryExecutionDuration = System.currentTimeMillis() - queryTime;
                archiveProgress[index].setStatus("Processing");
                pageSize = studentBioDataFormDetailEntityList.size();
                totalCount += pageSize;
                long totalQueryEt = ((archiveProgress[index].getMaxRecords() - totalCount) / pageLimit) * queryExecutionDuration;
                log.debug("Processing next {} records with total of {}", pageSize, totalCount);
                studentBioDataFormDetailEntityList.forEach(studentBioDataFormDetailEntity -> {
                    long currentTime = System.currentTimeMillis();
                    try {
                        String fileName = studentBioDataFormDetailEntity.getStudentId() + "_";
                        if (studentBioDataFormDetailEntity.getImageBytes() != null) {
                            fileService.encodeFile(ModelConstants.IMAGE_BIO_DATA_PROFILE, studentBioDataFormDetailEntity.getImageBytes(), fileName + "profile");
                            studentBioDataFormDetailEntity.setImageBytes(null);
                        }
                        if (studentBioDataFormDetailEntity.getStudentSignBytes() != null) {
                            fileService.encodeFile(ModelConstants.IMAGE_BIO_DATA_STUDENT_SIGN, studentBioDataFormDetailEntity.getStudentSignBytes(), fileName + "stud_sign");
                            studentBioDataFormDetailEntity.setStudentSignBytes(null);
                        }
                        if (studentBioDataFormDetailEntity.getParentSignBytes() != null) {
                            fileService.encodeFile(ModelConstants.IMAGE_BIO_DATA_PARENT_SIGN, studentBioDataFormDetailEntity.getParentSignBytes(), fileName + "parent_sign");
                            studentBioDataFormDetailEntity.setParentSignBytes(null);
                        }
                        studentBioDataFormDetailRepository.saveAndFlush(studentBioDataFormDetailEntity);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    calculateETC(index, totalTimeTaken, totalQueryEt, currentTime);
                });
            } while (pageSize == pageLimit);
            archiveProgress[index].setStatus("Archive Complete");
            log.debug("Total processed student count: {} records", totalCount);
            vacuumService.vacuumDatabase();
            archiveProgress[index].setRunning(false);
        }
    }

    private void calculateETC(int index, Long[] totalTimeTaken, long totalQueryEt, long currentTime) {
        long timeTaken = System.currentTimeMillis() - currentTime;
        totalTimeTaken[0] += timeTaken;
        archiveProgress[index].setProgress(archiveProgress[index].getProgress() + 1);
        long etc = totalQueryEt + ((totalTimeTaken[0] / archiveProgress[index].getProgress()) * (archiveProgress[index].getMaxRecords() - archiveProgress[index].getProgress()));
        archiveProgress[index].setEtc(Utility.durationInString(etc));
    }

    public void archiveSettledStudents(String tag) {
        int index = 2;
        if (archiveProgress[index] == null || !archiveProgress[index].isRunning()) {
            archiveProgress[index] = new ArchiveProgress();
            archiveProgress[index].setRunning(true);
            archiveProgress[index].setStatus("Archive Started");
            LocalDateTime archiveId = LocalDateTime.now();
            archiveProgress[index].setArchiveId(archiveId);
            try {
                archiveLogRepository.saveAndFlush(new ArchiveLogEntity(archiveId, "Initial Log", "Archive Settled Students"));
                logService.addLog(tag, "Initial Log > Archive Settled Students", false);
                archiveLogRepository.saveAndFlush(new ArchiveLogEntity(archiveId, "Archive Tables", "Creating Archive Tables."));
                logService.addLog(tag, "Archive Tables > Creating Archive Tables.", false);
                archiveTableMasterRepository.createArchiveTables();
                archiveLogRepository.saveAndFlush(new ArchiveLogEntity(archiveId, "Archive Tables", "Archive Tables created successfully."));
                logService.addLog(tag, "Archive Tables > Archive Tables created successfully.", false);
                archiveProgress[index].setStatus("Archive Tables created.");

                archiveLogRepository.saveAndFlush(new ArchiveLogEntity(archiveId, "Archive Data", "Archive settled students process started."));
                logService.addLog(tag, "Archive Data > Archive settled students process started.", false);
                int totalCount = archiveTableMasterRepository.getSettledStudentCount();
                archiveLogRepository.saveAndFlush(new ArchiveLogEntity(archiveId, "Archive Data", totalCount, 0, "Total Students to Process"));
                logService.addLog(tag, "Archive Data > Total Students to Process " + totalCount, false);
                if (totalCount < 1000) {
                    archiveProgress[index].setStatus("Archiving by limit of " + totalCount);
                    archiveLogRepository.saveAndFlush(new ArchiveLogEntity(archiveId, "Archive Data", totalCount, 0, "Archiving by Limit"));
                    logService.addLog(tag, "Archive Data > Archiving by Limit: " + totalCount, false);
                    archiveTableMasterRepository.archiveByLimitAndBatch(archiveId, totalCount, null);
                    archiveLogRepository.saveAndFlush(new ArchiveLogEntity(archiveId, "Archive Data", totalCount, 0, "Archiving by Limit complete"));
                    logService.addLog(tag, "Archive Data > Archiving by Limit complete", false);
                    archiveProgress[index].setStatus("Archiving by limit complete");
                } else {
                    archiveProgress[index].setStatus("Archiving by Batch");
                    int noOfBatches = archiveTableMasterRepository.getNoOfBatches();
                    archiveLogRepository.saveAndFlush(new ArchiveLogEntity(archiveId, "Archive Data", noOfBatches, 0, "Archiving by Batch"));
                    logService.addLog(tag, "Archive Data > Archiving by Batch. Batches found: " + noOfBatches, false);
                    Integer[] runningBatchCount = new Integer[]{0};
                    archiveTableMasterRepository.getBatchList().forEach(batchRec -> {
                        if (!archiveProgress[index].isStop()) {
                            runningBatchCount[0]++;
                            String batch = batchRec[0];
                            int noOfStudents = Integer.parseInt(batchRec[1]);
                            archiveLogRepository.saveAndFlush(new ArchiveLogEntity(archiveId, "Archive Data", noOfStudents, runningBatchCount[0], "Archiving for Batch: " + batch));
                            logService.addLog(tag, "Archive Data > Archiving for Batch: '" + batch + "' with " + noOfStudents + " students.", false);
                            archiveProgress[index].setStatus("Batch '" + batch + "' in Progress");
                            archiveTableMasterRepository.archiveByLimitAndBatch(archiveId, noOfStudents, batch);
                            archiveLogRepository.saveAndFlush(new ArchiveLogEntity(archiveId, "Archive Data", noOfStudents, runningBatchCount[0], "Archiving for Batch: " + batch + " complete"));
                            logService.addLog(tag, "Archive Data > Archiving for Batch: '" + batch + "' complete.", false);
                        } else {
                            archiveLogRepository.saveAndFlush(new ArchiveLogEntity(archiveId, "Archive Data", "Archiving stopped by user."));
                            logService.addLog(tag, "Archive Data > Archiving stopped by user.", false);
                        }
                    });
                    archiveLogRepository.saveAndFlush(new ArchiveLogEntity(archiveId, "Archive Data", noOfBatches, runningBatchCount[0], "Archiving by Batch complete"));
                    archiveProgress[index].setStatus("Archiving by Batch complete");
                    logService.addLog(tag, "Archive Data >, Archiving by Batch complete", false);
                }
                archiveLogRepository.saveAndFlush(new ArchiveLogEntity(archiveId, "Final Log", "Archive Settled Students completed. View the logs for more details."));
                logService.addLog(tag, "Final Log > Archive Settled Students completed. View the logs for more details.", false);
                archiveProgress[index].setStatus("Archive Complete");
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                archiveLogRepository.saveAndFlush(new ArchiveLogEntity(archiveId, "Error", sw.toString()));
                archiveLogRepository.saveAndFlush(new ArchiveLogEntity(archiveId, "Final Log", "Archive Settled Students completed with errors."));
                logService.addLog(tag, "Final Log > Archive Settled Students completed. View the logs for more details.", false);
                archiveProgress[index].setStatus("Archive Complete");
            } finally {
                archiveProgress[index].setRunning(false);
            }
        }
    }

    public ArchiveProgress getProgress(String type) {
        return switch (type) {
            case "candidate" -> archiveProgress[0];
            case "student" -> archiveProgress[1];
            default -> {
                final ArchiveProgress mArchiveProgress = archiveProgress[2];
                archiveLogRepository.findFirstByArchiveIdOrderByIdDesc(mArchiveProgress.archiveId).ifPresent(it -> {
                    if (it.getTotalRecords() != null) {
                        mArchiveProgress.setMaxRecords(it.getTotalRecords());
                    } else mArchiveProgress.setMaxRecords(0);
                    if (it.getRemainingRecords() != null) {
                        mArchiveProgress.setProgress(it.getRemainingRecords());
                    } else mArchiveProgress.setProgress(0);
                    if (it.getTimeRemaining() != null) {
                        mArchiveProgress.setEtl(Utility.durationInString(it.getTimeRemaining()));
                    } else mArchiveProgress.setEtl(Utility.durationInString(0));
                    if (it.getEstimatedTimeOfCompletion() != null) {
                        mArchiveProgress.setEtc(utility.dateFormatterLocalDateTime(it.getEstimatedTimeOfCompletion(), Constants.FRONTEND_DATE_TIME_FORMAT));
                    } else mArchiveProgress.setEtc(null);
                });
                archiveLogRepository.findFirstByArchiveIdAndSegmentOrderByIdDesc(mArchiveProgress.archiveId, "Archive Data")
                        .ifPresent(it -> {
                            String log =  it.getSegment() + " > " + it.getAdditionalLog();
                            if (!log.equals(mArchiveProgress.getLastLog())) {
                                logService.addLog(type, log, false);
                            }
                            mArchiveProgress.setLastLog(log);

                        });
                yield mArchiveProgress;
            }
        };
    }
}
