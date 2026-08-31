package com.iitm.hosteldine.service.collegeInfo;

import java.util.Locale;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.collegeInfo.FaqDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.collegeInfo.FaqMapper;
import com.iitm.hosteldine.model.collegeInfo.FaqEntity;
import com.iitm.hosteldine.repository.collegeInfo.FaqRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FaqService {
    private final FaqRepository faqRepository;
    private final MessageSource messageSource;

    public Page<FaqDto> getFaqList(PaginationForm form) {
        var pageRequest = PageRequest.of(form.getPage() - 1, form.getSize(), Sort.by("id").ascending());

        Page<FaqEntity> result = Optional.ofNullable(form.getSearch())
                .filter(search -> !search.isEmpty())
                .map(search -> faqRepository.findByQuestionAndAnswerAndActive(ModelConstants.STATUS_ACTIVE, pageRequest, search))
                .orElseGet(() -> faqRepository.findAllByActiveFlagOrderByIdAsc(ModelConstants.STATUS_ACTIVE, pageRequest));

        return result.map(FaqMapper.INSTANCE::fromFaqEntity);
    }

    public FaqDto getFaqById(Long id) {
        return faqRepository.findById(id).map(FaqMapper.INSTANCE::fromFaqEntity).orElse(new FaqDto());
    }

    public String saveOrUpdateFaq(FaqDto faqDto) {
        return Optional.ofNullable(faqDto.getId())
                .filter(id -> id > 0L)
                .flatMap(faqRepository::findById)
                .map(existingEntity -> {
                	FaqMapper.INSTANCE.onUpdateEntity(existingEntity, faqDto);
                	faqRepository.save(existingEntity);
                    return Constants.UPDATED;
                })
                .orElseGet(() -> {
                	FaqEntity entity = FaqMapper.INSTANCE.toFaqEntity(faqDto);
                    entity.setId(null);
                    faqRepository.save(entity);
                    return Constants.SAVED;
                });
    }


    public boolean deleteFaq(Long id) throws RecordNotExistsException {
        return faqRepository.findById(id)
                .map(entity->{
                    entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
                    faqRepository.save(entity);
                    return true;
                })
                .orElseThrow(() -> new RecordNotExistsException(
                        messageSource.getMessage("validation.error.id.not.found", null, Locale.getDefault()
                        )));
    }

}
