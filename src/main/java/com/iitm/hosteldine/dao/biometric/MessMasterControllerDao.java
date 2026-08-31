package com.iitm.hosteldine.dao.biometric;

import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessMasterControllerDao {

    private final MessMasterCommonService messMasterCommonService;

    public MessMasterControllerDto getCurrentMessMasterController() {
        return messMasterCommonService.getCurrentMessPeriod();
    }

    public MessMasterControllerDto getNextMessMasterController() {
        return messMasterCommonService.getNextMessPeriod();
    }

}
