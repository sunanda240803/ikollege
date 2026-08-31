package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mailQueue.MailTemplateDto;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.mailQueue.MailTemplateMapper;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.CustomValidators;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MailTemplateService {

    private final MailTemplateRepository mailTemplateRepository;
    private final CustomValidators customValidators;

    public Page<MailTemplateDto> getMailTemplates(PaginationForm form){
        var pageRequest = PageRequest.of(form.getPage() - 1, form.getSize(), Sort.by("mailType").ascending());
        return mailTemplateRepository.findAllByActiveFlagOrderByMailType(ModelConstants.STATUS_ACTIVE,pageRequest)
                .map(MailTemplateMapper.INSTANCE::toDto);
    }

    public MailTemplateDto getMailTemplateByType(String type){
        return Optional.ofNullable(type)
                .filter(t -> !type.isEmpty() && !type.equals("0"))
                .map(this::getMailTemplate)
                .orElse(new MailTemplateDto());
    }

    public MailTemplateDto getMailTemplate(String type){
        return mailTemplateRepository.findByActiveFlagAndMailType(ModelConstants.STATUS_ACTIVE, type)
                .map(MailTemplateMapper.INSTANCE::toDto)
                .orElse(new MailTemplateDto());
    }

    public String saveOrUpdateMailTemplate(MailTemplateDto mailTemplateDto){
        return mailTemplateRepository.findByActiveFlagAndMailType(ModelConstants.STATUS_ACTIVE, mailTemplateDto.getMailType())
                .map(entity->updateMailTemplate(entity,mailTemplateDto))
                .orElse(saveMailTemplate(mailTemplateDto));
    }

    public String updateMailTemplate(MailTemplateEntity mailTemplateEntity, MailTemplateDto mailTemplateDto){
        MailTemplateMapper.INSTANCE.updateEntity(mailTemplateEntity,mailTemplateDto);
        mailTemplateEntity.onUpdate();
        mailTemplateRepository.saveAndFlush(mailTemplateEntity);
        return Constants.UPDATED;
    }

    public String saveMailTemplate(MailTemplateDto mailTemplateDto){
        MailTemplateEntity entity = MailTemplateMapper.INSTANCE.toEntity(mailTemplateDto);
        entity.onCreate();
        mailTemplateRepository.saveAndFlush(entity);
        return Constants.SAVED;
    }

    public boolean existsByMailType(String type){
        return mailTemplateRepository.findByMailType(type).isPresent();
    }

    public void validateMailTemplate(MailTemplateDto dto, BindingResult bindingResult){
        if (customValidators.isNullOrEmpty(dto.getMailType())) {
            customValidators.rejectField(bindingResult, "mailType", "message.validation.mail.type.required");
        }
        else{
            if(existsByMailType(dto.getMailType()) && customValidators.isNullOrEmpty(dto.getActiveFlag())){
                customValidators.rejectField(bindingResult,"mailType","message.validation.mail.type.exists");
            }
        }

        if(customValidators.isNullOrEmpty(dto.getMailSubject())){
            customValidators.rejectField(bindingResult, "mailSubject", "message.validation.mail.subject.required");
        }

        if(customValidators.isNullOrEmpty(dto.getDescription())){
            customValidators.rejectField(bindingResult, "description", "message.validation.description.required");
        }

        if(customValidators.isNullOrEmpty(dto.getMailTemplate())){
            customValidators.rejectField(bindingResult,"mailTemplate", "message.validation.mail.content.required");
        }
    }
}