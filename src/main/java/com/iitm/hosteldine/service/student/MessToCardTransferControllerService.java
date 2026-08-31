package com.iitm.hosteldine.service.student;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.student.MessCardAmountTransferControllerDto;
import com.iitm.hosteldine.model.student.MessCardAmountTransferControllerEntity;
import com.iitm.hosteldine.repository.student.MessCardAmountTransferControllerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessToCardTransferControllerService {
	private final MessCardAmountTransferControllerRepository messCardAmountTransferControllerRepository;
	
	
	public Double getMaxTransferAmount() {
        return messCardAmountTransferControllerRepository.getMaxAmount(ModelConstants.STATUS_ACTIVE);
    }


	public boolean isMessToCardTransferActive() {
	    return messCardAmountTransferControllerRepository.isMessToCardTransferActive(ModelConstants.STATUS_ACTIVE);
	}
}

	

	
	
	

