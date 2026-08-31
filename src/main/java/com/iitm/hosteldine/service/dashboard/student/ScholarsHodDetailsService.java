package com.iitm.hosteldine.service.dashboard.student;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.StudentConstants;
import com.iitm.hosteldine.dto.dashboard.student.ScholarsHodDetailDto;
import com.iitm.hosteldine.mapper.dashboard.student.ScholarsHodDetailMapper;
import com.iitm.hosteldine.repository.dashboard.student.ScholarsHodDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScholarsHodDetailsService {
    private final ScholarsHodDetailsRepository scholarsHodDetailsRepository;

    public ScholarsHodDetailDto getScholarsHodDetailsList() {
        return scholarsHodDetailsRepository.getDepartmentCode(
                SecurityCtxUtil.userName().substring(0,2),
                ModelConstants.STATUS_ACTIVE).map(ScholarsHodDetailMapper.INSTANCE::toDto)
                .orElse(new ScholarsHodDetailDto());
    }
}
