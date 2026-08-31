package com.iitm.hosteldine.service.mailQueue;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.SimsConfigDataDto;
import com.iitm.hosteldine.dto.collegeInfo.SchoolGeographyInfoDto;
import com.iitm.hosteldine.dto.mailQueue.MailQueueDetailsDto;
import com.iitm.hosteldine.entity.mailQueue.MailQueueDetailsEntity;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.SimsConfigDataMapper;
import com.iitm.hosteldine.mapper.mailQueue.MailQueueDetailsMapper;
import com.iitm.hosteldine.repository.SimsConfigDataRepository;
import com.iitm.hosteldine.repository.mailQueue.MailQueueDetailsRepository;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.collegeInfo.SchoolGeographyInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MailQueueService {
    MailTemplateRepository mailTemplateRepository;

    public static final String BEST_REGARDS = "Best Regards";
    public static final String CCW_OFFICE = "CCW Office";

    private final SimsConfigDataRepository simsConfigDataRepository;
    private final MailQueueDetailsRepository mailQueueDetailsRepository;

    private final SimsConfigDataService simsConfigDataService;
    private final SchoolGeographyInfoService sgiService;

    public String getmailFrom() {
        return simsConfigDataService.getSimConfigValue("MAIL_FROM");
    }

    public String getmailCommonTemplate() {
        return simsConfigDataService.getSimConfigValue("MAIL_COMMON_TEMPLATE");
    }

    public String prepareMailContent(MailQueueDetailsDto mailQueueDetailsDto) throws Exception {

        Optional<MailTemplateEntity> mailQueueDetailsEntity = mailTemplateRepository.findByMailType(mailQueueDetailsDto.getMailType());
//        message = message.replaceAll("#%EmailTitle%#", heading);
//        message = message.replace("#%Current Time%#", DateUtility.formatDateForMail(new Date()));
//        message = message.replace("#%UserName%#", userName);
//        message = message.replace("#%Main Message%#", mainMessage);

        return null;

    }


    public MailQueueDetailsEntity setMailQueueDetails(String userEmail, String subject, String module, String content, int priority, String attachment, String pdfAttachment) {
        MailQueueDetailsEntity mailQueueDetails = new MailQueueDetailsEntity();
        mailQueueDetails.setMailTo(userEmail);
        mailQueueDetails.setMailSubject(subject);
        mailQueueDetails.setMailContent(content);
        mailQueueDetails.setSubmittedModule(module);
        mailQueueDetails.setMailStatus(1);
        mailQueueDetails.setMailPriority(1);
        mailQueueDetails.setMailFrom("");
        mailQueueDetails.setActiveFlag(ModelConstants.STATUS_ACTIVE);
        mailQueueDetails.setAttachFile(attachment);
        return mailQueueDetails;
    }

    public boolean saveMailQueue(String subject, String userName,
                                 String mainMessage, String userEmail, String module, String userId, Integer priority, String urlLink,
                                 String attachments, String greetings, String greetingsName) throws Exception {
        return saveMailQueue(subject, userName, mainMessage, userEmail, module, userId, priority, urlLink, attachments, greetings, greetingsName, null);
    }

    public boolean saveMailQueue(String subject, String userName,
                                 String mainMessage, String userEmail, String module, String userId, Integer priority, String urlLink,
                                 String attachments, String greetings, String greetingsName, String bcc) throws Exception {
        return saveMailQueue(subject, userName, mainMessage, userEmail, module, userId, priority, urlLink, attachments, greetings, greetingsName, null,bcc);
    }

    public boolean saveMailQueue(String subject, String userName,
                                 String mainMessage, String userEmail, String module, String userId, Integer priority, String urlLink,
                                 String attachments, String greetings, String greetingsName, String cc, String bcc) throws Exception {
        MailQueueDetailsEntity mailQueueDetails = new MailQueueDetailsEntity();
        mailQueueDetails.setMailTo(userEmail);
        mailQueueDetails.setMailSubject(subject);
        String content = prepareMailContent(getmailCommonTemplate(), subject, userName, mainMessage, userEmail, greetings,
                greetingsName);
        mailQueueDetails.setMailContent(content);
        mailQueueDetails.setSubmittedModule(module);
        mailQueueDetails.setMailStatus(1);
        mailQueueDetails.setRetryCount(0);
        mailQueueDetails.setMailPriority(1);

        String mailFrom = getmailFrom();
        if (mailFrom == null) {
            mailFrom = simsConfigDataRepository
                    .findByConfigKeyIgnoreCaseAndActiveFlag("MAIL_FROM",
                            ModelConstants.STATUS_ACTIVE).map(SimsConfigDataMapper.INSTANCE::fromSimsConfigDataEntity)
                    .map(SimsConfigDataDto::getConfigValue).orElse(new SimsConfigDataDto().getConfigValue());
        }

        mailQueueDetails.setMailFrom(mailFrom);
        mailQueueDetails.setActiveFlag(ModelConstants.STATUS_ACTIVE);
        mailQueueDetails.setCreatedBy(userId);
        mailQueueDetails.setModifiedBy(userId);
        mailQueueDetails.setAttachFile(attachments);
        mailQueueDetails.onCreate();
        mailQueueDetails.setMailCc(cc);
        mailQueueDetails.setMailBcc(bcc);
        return mailQueueDetailsRepository.save(mailQueueDetails) != null;
    }

    public String prepareMailContent(String template, String subject, String userName, String mainMessage,
                                     String userEmail, String greetings, String greetingsName) {
        SchoolGeographyInfoDto schoolDetails = sgiService.getSchoolDetails();
        if (template == null) {
            template = simsConfigDataRepository
                    .findByConfigKeyIgnoreCaseAndActiveFlag("MAIL_COMMON_TEMPLATE",
                            ModelConstants.STATUS_ACTIVE).map(SimsConfigDataMapper.INSTANCE::fromSimsConfigDataEntity)
                    .map(SimsConfigDataDto::getConfigValue).orElse(new SimsConfigDataDto().getConfigValue());
        }

        template = template.replace("#%heading%#", schoolDetails.getSchoolName());
        template = template.replace("#%location%#", schoolDetails.getLocation() != null ? schoolDetails.getLocation() : "");
        template = template.replace("#%address%#", schoolDetails.getAddress1() != null ? schoolDetails.getAddress1() : "");
        template = template.replace("#%phone%#", Objects.toString(schoolDetails.getLandlineContactNo1(), ""));
        template = template.replace("#%email%#", schoolDetails.getEmail() != null ? schoolDetails.getEmail() : "");
        template = template.replace("#%date%#", DateUtility.formatDateInd(new java.util.Date()));
        template = template.replace("#%userName%#", userName);
        template = template.replace("#%subject%#", subject);
        template = template.replace("#%mainMessage%#", mainMessage);
        template = template.contains("#%greetings%#")
                ? template.replace("#%greetings%#", greetings != null ? greetings : "Regards,")
                : template;
        template = template.contains("#%greetingsName%#")
                ? template.replace("#%greetingsName%#", greetingsName != null ? greetingsName : "Ikollege Team")
                : template;

        return template;

    }

    public Page<MailQueueDetailsDto> getList(PaginationForm form) {
        var pageRequest = PageRequest.of(form.getPage() - 1, form.getSize(),
                Sort.by("id").descending());
        LocalDate fromDate = form.hasValue("fromDate") ? LocalDate.parse(form.getAdditionalParam().get("fromDate").toString(), DateTimeFormatter.ofPattern(Constants.BACKEND_DATE_FORMAT)) : null;
        LocalDate toDate = form.hasValue("toDate") ? LocalDate.parse(form.getAdditionalParam().get("toDate").toString(), DateTimeFormatter.ofPattern(Constants.BACKEND_DATE_FORMAT)) : null;

        String status = form.hasValue("status") ? String.valueOf(form.getAdditionalParam().get("status")) : null;

        Page<MailQueueDetailsEntity> result = Optional.ofNullable(form.getSearch())
                .filter(search -> !search.isEmpty())
                .map(search -> mailQueueDetailsRepository.getAllBySearch(ModelConstants.STATUS_ACTIVE, pageRequest, search.toLowerCase(), fromDate, toDate, status))
                .orElseGet(() -> mailQueueDetailsRepository.getByActiveFlag(ModelConstants.STATUS_ACTIVE, pageRequest, fromDate, toDate, status));

        return result.map(MailQueueDetailsMapper.INSTANCE::toDto);
    }

    public MailQueueDetailsDto getById(Long id) {
        return mailQueueDetailsRepository.findById(id).map(MailQueueDetailsMapper.INSTANCE::toDto)
                .orElse(new MailQueueDetailsDto());
    }
}
