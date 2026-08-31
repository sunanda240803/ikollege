package com.iitm.hosteldine.service.mess;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.SimsConfigType;
import com.iitm.hosteldine.dto.mess.MessVendorAllocationDto;
import com.iitm.hosteldine.entity.mess.MessVendorAllocationEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.mess.MessVendorAllocationMapper;
import com.iitm.hosteldine.model.SimsConfigDataEntity;
import com.iitm.hosteldine.repository.SimsConfigDataRepository;
import com.iitm.hosteldine.repository.mess.MessAllocationEntityRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessVendorAllocationService {
    private final MessAllocationEntityRepository repository;
    private final SimsConfigDataRepository simsConfigRepository;

    public Page<MessVendorAllocationDto> getAllocations(PaginationForm form) {
        int page = form.getPage() - 1;
        Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("modifiedAt").descending());

        if (StringUtils.isEmpty(form.getSearch())) {
            return repository.getVendorAllocationsByActiveFlag(ModelConstants.STATUS_ACTIVE, pageable);
        } else {
            return repository.getVendorAllocationsByActiveAndSearch(ModelConstants.STATUS_ACTIVE, pageable, form.getSearch());
        }
    }

    public String manageVendorAllocation(MessVendorAllocationDto dto) {
        MessVendorAllocationEntity entity;
        String vendorCode = dto.getVendorCode();
        Long messId = dto.getMessId();

        if (StringUtils.isNotEmpty(vendorCode) && messId != null) {
            Optional<MessVendorAllocationEntity> optEntity = repository.getByMessIdAndVendorCode(messId, vendorCode, ModelConstants.YES);

            if (optEntity.isPresent()) {
                entity = optEntity.get();
                entity.setFromDate(dto.getFromDate());
                entity.setToDate(dto.getToDate());
                entity.setRate(dto.getRate());
                entity.setEffectiveDate(dto.getFromDate());
            } else {
                entity = MessVendorAllocationMapper.INSTANCE.toEntity(dto);
            }

            repository.save(entity);
        }

        return Constants.SAVED;
    }

    public Integer getGst() {
        Optional<SimsConfigDataEntity> optSimsConfig = simsConfigRepository.findByConfigKeyIgnoreCaseAndActiveFlag(
                SimsConfigType.MESS_GST_PERCENTAGE.name(), ModelConstants.STATUS_ACTIVE);
        return Integer.valueOf(optSimsConfig.map(SimsConfigDataEntity::getConfigValue).orElse("0"));
    }
}
