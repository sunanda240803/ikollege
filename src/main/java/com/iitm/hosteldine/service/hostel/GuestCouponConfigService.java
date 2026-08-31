package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.GuestCouponConfigDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.hostel.GuestCouponConfigMapper;
import com.iitm.hosteldine.model.hostel.GuestCouponConfigEntity;
import com.iitm.hosteldine.repository.hostel.GuestCouponConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuestCouponConfigService {

    private final GuestCouponConfigRepository guestCouponConfigRepository;
    private final GuestCouponConfigMapper guestCouponConfigMapper;

    /**
     * Fetch all guest coupon configurations with pagination.
     */
    public Page<GuestCouponConfigDto> getGuestCouponConfigList(PaginationForm form) {
        var pageRequest = PageRequest.of(form.getPage() - 1, form.getSize(), Sort.by("effectiveDate").descending());
        if (form.getSearch() == null || form.getSearch().isEmpty()) {
            return guestCouponConfigRepository.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE, pageRequest).map(guestCouponConfigMapper::toDto);
        } else {
            return guestCouponConfigRepository.findByGuestCouponConfigSearchList(ModelConstants.STATUS_ACTIVE, pageRequest, form.getSearch()).map(guestCouponConfigMapper::toDto);
        }
    }

    /**
     * Fetch a specific guest coupon configuration by ID.
     */
    public GuestCouponConfigDto getGuestCouponConfigById(Long id) throws RecordNotExistsException {
        return guestCouponConfigRepository.findById(id)
                .map(guestCouponConfigMapper::toDto)
                .orElseThrow(() -> new RecordNotExistsException("message.guest.coupon.exception.id" + id));
    }

    /**
     * Save or update a guest coupon configuration.
     */
    public String saveOrUpdateGuestCouponConfig(GuestCouponConfigDto dto) {

        return Optional.ofNullable(dto.getId()).filter(id -> (id != null && id > 0))
                .flatMap(id -> guestCouponConfigRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE))
                .map(existingEntity -> {
                    guestCouponConfigMapper.onUpdateEntity(existingEntity, dto);
                    guestCouponConfigRepository.save(existingEntity);
                    return Constants.UPDATED;
                }).orElseGet(() -> {
                    GuestCouponConfigEntity newEntity = guestCouponConfigMapper.toEntity(dto);
                    guestCouponConfigRepository.save(newEntity);
                    return Constants.SAVED;
                });
    }

    public boolean deactivateGuestCouponConfig(Long id) throws RecordNotExistsException {
        Optional<GuestCouponConfigEntity> guestCouponConfigOpt = guestCouponConfigRepository.findById(id);

        if (guestCouponConfigOpt.isPresent()) {
            GuestCouponConfigEntity guestCouponConfig = guestCouponConfigOpt.get();
            guestCouponConfig.setActiveFlag("N");  // Mark as inactive
            guestCouponConfigRepository.save(guestCouponConfig);
            return true;
        } else {
            throw new RecordNotExistsException("message.guest.coupon.exception.id" + id);
        }
    }

    public GuestCouponConfigDto getGuestCouponRates(LocalDate startDate, LocalDate toDate, String category) {
        return guestCouponConfigRepository.getCouponRates(startDate, toDate, category);
    }
}
