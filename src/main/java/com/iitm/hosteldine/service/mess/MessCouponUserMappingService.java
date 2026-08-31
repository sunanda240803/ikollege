package com.iitm.hosteldine.service.mess;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.MessCouponUserMappingDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.mess.MessCouponUserMappingMapper;
import com.iitm.hosteldine.model.mess.MessCouponUserMappingEntity;
import com.iitm.hosteldine.model.mess.MessMasterEntity;
import com.iitm.hosteldine.repository.mess.MessCouponUserMappingRepository;
import com.iitm.hosteldine.service.UserManagementService;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.CustomValidators;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class MessCouponUserMappingService {

    private final MessCouponUserMappingRepository messCouponUserMappingRepository;
    private final CommonResponseUtil commonResponseUtil;
    private final CustomValidators customValidators;
    private final UserManagementService userManagementService;

    public Page<MessCouponUserMappingDto> getAllMessCouponUserMappings(PaginationForm form) {
        var pageRequest = PageRequest.of(form.getPage() - 1, form.getSize(), Sort.by("modifiedAt").descending());
        return messCouponUserMappingRepository.getAllMessCouponUserMapping(ModelConstants.STATUS_ACTIVE, form.getSearch(), pageRequest)
                .map(MessCouponUserMappingMapper.INSTANCE::toDto);
    }

    public Map<String, Boolean> saveMessCouponUserMapping(MessCouponUserMappingDto messCouponUserMappingDto) {
        AtomicBoolean saveOrUpdate = new AtomicBoolean(false);
        AtomicBoolean existsStatus = new AtomicBoolean(false);
        messCouponUserMappingDto.getMessId().forEach(id ->
                messCouponUserMappingRepository.findByUserNameAndMessMaster_Id(messCouponUserMappingDto.getUserName(), id)
                        .ifPresentOrElse(entity -> handleExistingMapping(entity, saveOrUpdate, existsStatus),
                                () -> saveNewMessCouponUserMapping(messCouponUserMappingDto.getUserName(), id, saveOrUpdate))
        );

        String saveStatus = saveOrUpdate.get() ? Constants.SAVED : ModelConstants.FAILURE;
        Map<String, Boolean> map = new HashMap();
        map.put(saveStatus, existsStatus.get());
        return map;
    }

    private void handleExistingMapping(MessCouponUserMappingEntity entity, AtomicBoolean status, AtomicBoolean existsStatus) {
        if (ModelConstants.STATUS_INACTIVE.equals(entity.getActiveFlag())) {
            entity.setActiveFlag(ModelConstants.STATUS_ACTIVE);
            entity.onUpdate();
            messCouponUserMappingRepository.save(entity);
            status.set(true);
        } else {
            existsStatus.set(true);
        }
    }

    private void saveNewMessCouponUserMapping(String userName, Long messId, AtomicBoolean status) {
        MessMasterEntity messMaster = new MessMasterEntity();
        messMaster.setId(messId);

        MessCouponUserMappingEntity entity = new MessCouponUserMappingEntity();
        entity.setMessMaster(messMaster);
        entity.setUserName(userName);
        entity.onCreate();
        messCouponUserMappingRepository.save(entity);
        status.set(true);
    }

    public boolean deleteMessCouponUserMapping(Long id) throws RecordNotExistsException {
        return messCouponUserMappingRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
                .map(this::deleteMessCouponMapping)
                .orElseThrow(() -> new RecordNotExistsException(commonResponseUtil.getMessage("validation.error.id.not.found")));
    }

    private boolean deleteMessCouponMapping(MessCouponUserMappingEntity messCouponUserMappingEntity) {
        messCouponUserMappingEntity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
        messCouponUserMappingEntity.onUpdate();
        messCouponUserMappingRepository.save(messCouponUserMappingEntity);
        return true;
    }

    public void validateMessCouponUserMapping(MessCouponUserMappingDto messCouponUserMappingDto, BindingResult result) {
        if (customValidators.isNullOrEmpty(messCouponUserMappingDto.getUserName().trim())) {
            customValidators.rejectField(result, "userName", "message.validation.enter.usernme");
        } else {
            if (!userManagementService.getUserByUserName(messCouponUserMappingDto.getUserName().trim())) {
                customValidators.rejectField(result, "userName", "message.validation.valid.user");
            }
        }

        if (messCouponUserMappingDto.getMessId() == null || messCouponUserMappingDto.getMessId().isEmpty()) {
            customValidators.rejectField(result, "messId", "message.validation.choose.account.head.name");
        }
    }

}