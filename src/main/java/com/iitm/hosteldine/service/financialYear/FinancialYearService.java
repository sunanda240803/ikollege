package com.iitm.hosteldine.service.financialYear;

import com.iitm.hosteldine.FinancialYearMapper;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.financialYear.FinancialYearDto;
import com.iitm.hosteldine.repository.financialYear.FinancialYearRepository;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class FinancialYearService {

    private final FinancialYearRepository financialYearRepository;
    private final CommonResponseUtil commonResponseUtil;

    public FinancialYearDto getActiveFinancialYear() {
        return financialYearRepository.findByActiveFlag(ModelConstants.STATUS_ACTIVE)
                .map(FinancialYearMapper.INSTANCE::toDto)
                .orElseThrow(() -> new NoSuchElementException(commonResponseUtil.getMessage("message.exception.fin.year.not.found")));
    }
}
