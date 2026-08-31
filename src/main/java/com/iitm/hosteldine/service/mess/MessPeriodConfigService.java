package com.iitm.hosteldine.service.mess;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mailQueue.MailTemplateDto;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.dto.studentDashboard.StudentMessDetailsDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.mailQueue.MailTemplateMapper;
import com.iitm.hosteldine.mapper.mess.MessMasterControllerMapper;
import com.iitm.hosteldine.mapper.studentDashboard.StudentMessDetailsMapper;
import com.iitm.hosteldine.model.mess.MessMasterControllerEntity;
import com.iitm.hosteldine.model.mess.StudentMessDetailsEntity;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.repository.mess.MessMasterControllerRepository;
import com.iitm.hosteldine.repository.studentDashboard.StudentMessDetailsRepository;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.CustomValidators;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessPeriodConfigService {

    private final MessMasterControllerRepository messMasterControllerRepository;
    private final CustomValidators customValidators;
    private final CommonResponseUtil commonResponseUtil;
    private final Utility utility;
    private final MailQueueService mailQueueService;
    private final StudentMessDetailsRepository studentMessDetailsRepository;
    private final SimsConfigDataService simsConfigDataService;
    private final MailTemplateRepository mailTemplateRepository;
    private final InMemoryLogService logService;

    public Page<MessMasterControllerDto> getAllMessPeriodConfig(PaginationForm paginationForm) {
        var pageRequest = PageRequest.of(paginationForm.getPage() - 1, paginationForm.getSize());
        return messMasterControllerRepository.getAllByActiveFlagAndSearchFilter(ModelConstants.STATUS_ACTIVE,
                        paginationForm.getSearch(), pageRequest)
                .map(MessMasterControllerMapper.INSTANCE::fromMessMasterControllerEntity);
    }

    public MessMasterControllerDto getMessPeriodConfigById(Long id) {
        MessMasterControllerDto dto= messMasterControllerRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
                .map(MessMasterControllerMapper.INSTANCE::fromMessMasterControllerEntity)
                .orElse(new MessMasterControllerDto());
        if(dto.getBulkMailContent()==null || dto.getBulkMailContent().isEmpty()){
            MailTemplateDto template = mailTemplateRepository
                    .findByActiveFlagAndMailType(ModelConstants.STATUS_ACTIVE, ModelConstants.MESS_BULK_MAIL).map(MailTemplateMapper.INSTANCE::toDto)
                    .orElse(new MailTemplateDto());
            dto.setBulkMailSubject(template.getMailSubject());
            dto.setBulkMailContent(template.getMailTemplate());
        }

        return  dto;
    }

    public String saveOrUpdateMessPeriodConfig(MessMasterControllerDto messMasterControllerDto) {
        if (messMasterControllerDto.getId() == null || messMasterControllerDto.getId() <= 0) {
            messMasterControllerRepository.updateAllMessPeriodConfig(ModelConstants.STATUS_ACTIVE);
        }
        //Set Mail Template
        MailTemplateDto template = mailTemplateRepository
                .findByActiveFlagAndMailType(ModelConstants.STATUS_ACTIVE, ModelConstants.MESS_BULK_MAIL).map(MailTemplateMapper.INSTANCE::toDto)
                .orElse(new MailTemplateDto());
        messMasterControllerDto.setBulkMailSubject(template.getMailSubject());
        messMasterControllerDto.setBulkMailContent(template.getMailTemplate());

        return messMasterControllerRepository.findByIdAndActiveFlag(messMasterControllerDto.getId(), ModelConstants.STATUS_ACTIVE)
                .map(entity -> updateMessPeriodConfig(entity, messMasterControllerDto))
                .orElseGet(() -> saveMessPeriodConfig(messMasterControllerDto));
    }

    public String saveMessPeriodConfig(MessMasterControllerDto messMasterControllerDto) {
        MessMasterControllerEntity messMasterControllerEntity = MessMasterControllerMapper.INSTANCE.toMessMasterControllerEntity(messMasterControllerDto);
        messMasterControllerEntity.onCreate();
        messMasterControllerRepository.saveAndFlush(messMasterControllerEntity);
        return Constants.SAVED;
    }

    public String updateMessPeriodConfig(MessMasterControllerEntity entity,
                                         MessMasterControllerDto messMasterControllerDto) {
        MessMasterControllerMapper.INSTANCE.updateMessPeriodConfig(entity, messMasterControllerDto);
        entity.onUpdate();
        messMasterControllerRepository.saveAndFlush(entity);
        return Constants.UPDATED;
    }



    @Transactional
    public boolean sendStudentMessBulkMail(MessMasterControllerDto messMasterControllerDto) throws Exception{
        System.out.println(TransactionSynchronizationManager.isActualTransactionActive()
        );
        addLog(messMasterControllerDto.getLogTag(), 0, commonResponseUtil.getMessage("upload.validation.start"));
        return messMasterControllerRepository.findByIdAndActiveFlag(messMasterControllerDto.getId(),
                        ModelConstants.STATUS_ACTIVE)
                .map(entity -> saveOrUpdateMailContent(entity, messMasterControllerDto))
                .orElseThrow(() -> new RuntimeException(commonResponseUtil.getMessage("validation.error.id.not.found")));
    }

    public boolean saveOrUpdateMailContent(MessMasterControllerEntity entity,
                                           MessMasterControllerDto messMasterControllerDto){
        String logTag=messMasterControllerDto.getLogTag();
        addLog(logTag, 0, commonResponseUtil.getMessage("upload.save.started"));
        MessMasterControllerMapper.INSTANCE.updateBulkMailContent(entity, messMasterControllerDto);
        if (sendMailToStudents(entity,logTag)) {
            entity.setMailStatus(ModelConstants.SENT);
            entity.onUpdate();
            messMasterControllerRepository.save(entity);
            addLog(logTag, 0, commonResponseUtil.getMessage("upload.wait.response"));
            addLog(logTag, 0, commonResponseUtil.getMessage("mail.success"));
            return true;
        }
        addLog(logTag, 0, commonResponseUtil.getMessage("process.failed"));
        return false;
    }

    public boolean sendMailToStudents(MessMasterControllerEntity messMasterControllerEntity,String logTag) {
        addLog(logTag, 0, commonResponseUtil.getMessage("get.pending.list"));
        List<Object[]> resultList = messMasterControllerRepository
                .getAllMessStudents(ModelConstants.STATUS_ACTIVE)
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new RuntimeException(
                        commonResponseUtil.getMessage("message.exception.no.students.mail")));
        for (Object[] row : resultList) {
            StudentMessDetailsEntity entity = (StudentMessDetailsEntity) row[0];
            AllStudentsDetailsViewEntity view = (AllStudentsDetailsViewEntity) row[1];
            StudentMessDetailsDto dto =
                    StudentMessDetailsMapper.INSTANCE.fromEntityAndView(entity, view);

            try {
                sendMail(messMasterControllerEntity, dto,logTag);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        addLog(logTag, 0, commonResponseUtil.getMessage("upload.students.count")+" "+resultList.size());
        return true;
    }

    public void sendMail(MessMasterControllerEntity messMasterControllerEntity, StudentMessDetailsDto dto,String logTag) throws Exception{
        String subject = messMasterControllerEntity.getBulkMailSubject();
        String content = messMasterControllerEntity.getBulkMailContent();
        String studentName = Objects.nonNull(dto.getAllStudentsDetailsViewDto().getStudentName()) ? dto.getAllStudentsDetailsViewDto().getStudentName() : ModelConstants.HYPHEN;
        String studentId = dto.getAllStudentsDetailsViewDto().getStudentId();
        String hostelName = dto.getAllStudentsDetailsViewDto().getHostelName();

        String studentEmailSuffix = simsConfigDataService.getSimConfigValue(SimsConfigDataService.STUDENT_MAIL_SUFFIX);
        String email = studentId + studentEmailSuffix;

        content = content.replace("#%studentName%#", studentName)
                .replace("#%periodFromDate%#", utility.dateFormatter(dto.getFromDate()))
                .replace("#%periodToDate%#", utility.dateFormatter(dto.getToDate()))
                .replace("#%diningFromDate%#", utility.dateFormatter(dto.getChangeFromDate()))
                .replace("#%diningToDate%#", utility.dateFormatter(dto.getChangeToDate()))
                .replace("#%studentId%#", studentId)
                .replace("#%hostelName%#", Objects.nonNull(hostelName) ? hostelName : ModelConstants.NOT_APPLICABLE)
                .replace("#%messName%#", dto.getMessMasterDto().getMessName());
        addLog(logTag, 0, commonResponseUtil.getMessage("mail.queue.save")+"-"+studentId);
        boolean mailQueueStatus=true;
        mailQueueStatus = mailQueueService.saveMailQueue(subject, studentName, content,
            email, "Mess Period Config", SecurityCtxUtil.userId(), null,
            null, null, "Regards", null);

        if (mailQueueStatus) {
            Optional<StudentMessDetailsEntity> optionalEntity =
                    studentMessDetailsRepository
                            .findByStudentDetailsInfo_StudentIdAndFromDateAndToDateAndActiveFlagAndCurrentActiveFlag(
                                    studentId,dto.getFromDate(), dto.getToDate(), ModelConstants.STATUS_ACTIVE,ModelConstants.STATUS_ACTIVE);
            if (optionalEntity.isPresent()) {
                updateStudentMessDetails(optionalEntity.get());
            } else {
                throw new IllegalArgumentException(
                        commonResponseUtil.getMessage("message.exception.failed.student.mess.details")
                );
            }
        } else {
            throw new RuntimeException(
                    commonResponseUtil.getMessage("message.exception.fail.send.mail")
            );
        }
    }

    public void updateStudentMessDetails(StudentMessDetailsEntity entity) {
        entity.setMailStatus(ModelConstants.SENT);
        entity.onUpdate();
        studentMessDetailsRepository.save(entity);
    }

    public void validateMessPeriodConfig(MessMasterControllerDto messMasterControllerDto, BindingResult result) {
        customValidators.validateField(messMasterControllerDto.getMonth(), "month",
                "message.validation.month.required", result);
        customValidators.validateField(messMasterControllerDto.getDiningFromDate(), "diningFromDate",
                "message.validation.dining.from.date.required", result);
        customValidators.validateField(messMasterControllerDto.getDiningToDate(), "diningToDate",
                "message.validation.dining.to.date.required", result);
        customValidators.validateField(messMasterControllerDto.getRegBeginDate(),
                "regBeginDate", "message.validation.registration.start.date.required", result);
        customValidators.validateField(messMasterControllerDto.getRegEndDate(), "regEndDate",
                "message.validation.registration.end.date.required", result);
        customValidators.validateField(messMasterControllerDto.getRegBeginTime(), "regBeginTime",
                "message.validation.mess.start.time.required", result);
        customValidators.validateField(messMasterControllerDto.getRegEndTime(), "regEndTime",
                "message.validation.mess.end.time.required", result);
        customValidators.validateField(messMasterControllerDto.getPushingDate(), "pushingDate",
                "message.validation.push.date.required", result);
        customValidators.validateField(messMasterControllerDto.getPushingTime(), "pushingTime",
                "message.validation.push.time.required", result);

        customValidators.validateDateOrder(messMasterControllerDto.getDiningFromDate(), messMasterControllerDto.getDiningToDate(),
                "diningToDate", "message.validation.dining.to.date", result);
        customValidators.validateDateOrder(messMasterControllerDto.getRegBeginDate(), messMasterControllerDto.getRegEndDate(),
                "regEndDate", "message.validation.reg.end.date", result);
    }

    private void addLog(String tag, long sessionId, String msg) {
        logService.addLog(tag, " <-" + sessionId + "-> " + msg);
    }

    private void addError(String tag, long sessionId, String msg,Exception e) {
        logService.addError(tag, " <-" + sessionId + "-> " + msg,e);
    }

    private void addValidation(String tag, long sessionId, String msg) {
        logService.addValidation(tag, " <-" + sessionId + "-> " + msg);
    }

}