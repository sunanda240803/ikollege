package com.iitm.hosteldine.service.studentDashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.dto.studentDashboard.GuestAccommodationGuestDetailsDto;
import com.iitm.hosteldine.mapper.studentDashboard.GuestAccommodationGuestDetailsMapper;
import com.iitm.hosteldine.model.studentDashboard.GuestAccommodationGuestDetailsEntity;
import com.iitm.hosteldine.repository.studentDashboard.GuestAccommodationGuestDetailsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuestAccommodationGuestDetailsService {
	private final GuestAccommodationGuestDetailsRepository guestAccommodationGuestDetailsRepository;

	public boolean saveGuestDetails(List<GuestAccommodationGuestDetailsDto> selectedGuests, int requestId) {
		if (!selectedGuests.isEmpty()) {
			List<GuestAccommodationGuestDetailsEntity> guestDetailsEntities = new ArrayList<>();
			selectedGuests.forEach(guest -> {
				GuestAccommodationGuestDetailsEntity guestEntity = new GuestAccommodationGuestDetailsEntity();
				guestEntity.setGuestName(guest.getGuestName());
				guestEntity.setRelationOfGuest(guest.getRelationOfGuest());
				guestEntity.setRequestId(requestId);
				guestEntity.setGuestId(guest.getGuestId());
				guestEntity.setProofDescription(guest.getGuestName());
				guestEntity.setGuestGender(
					    (guest.getRelationOfGuest().equalsIgnoreCase(Constants.FATHER) || guest.getRelationOfGuest().equalsIgnoreCase(Constants.BROTHER) ||
					     guest.getRelationOfGuest().equalsIgnoreCase(Constants.HUSBAND)) ? Constants.MALE_FULL_FORM : Constants.FEMALE_FULL_FORM);
				guestEntity.onCreate();
				guestDetailsEntities.add(guestEntity);
			});

			List<GuestAccommodationGuestDetailsEntity> entity = guestAccommodationGuestDetailsRepository.saveAll(guestDetailsEntities);
			if(!entity.isEmpty()) {
				return true;
			}
		}
		return false;

	}

	public List<GuestAccommodationGuestDetailsDto> getGuestList(Long requestId) {
		return Optional.ofNullable(guestAccommodationGuestDetailsRepository.findAllByRequestId(requestId))
				.orElse(Collections.emptyList()).stream().map(GuestAccommodationGuestDetailsMapper.INSTANCE::fromGuestAccommodationGuestDetailsEntity)
				.collect(Collectors.toList());
	}

	public List<GuestAccommodationGuestDetailsDto> getGuestId(Long parentReqId) {
		return Optional.ofNullable(guestAccommodationGuestDetailsRepository.findAllByRequestId(parentReqId))
				.orElse(Collections.emptyList()).stream().map(GuestAccommodationGuestDetailsMapper.INSTANCE::fromGuestAccommodationGuestDetailsEntity)
				.collect(Collectors.toList());
	}

	

}
