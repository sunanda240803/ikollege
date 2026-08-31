package com.iitm.hosteldine.service.reports;

import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.repository.hostel.HostelBiometricTerminalRepository;
import com.iitm.hosteldine.util.Utility;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GeneralLogReportService {

    private final HostelBiometricTerminalRepository hostelBiometricTerminalRepository;
    private final Utility utility;

    public Page<LateNightEntryRecord> getGeneralLogReport(PaginationForm form) {
        String hostelName = (form.getAdditionalParam().get("hostelName") != null && !form.getAdditionalParam().get("hostelName").equals("")) ?
                form.getAdditionalParam().get("hostelName").toString() : null;
        String fromDate = (form.getAdditionalParam().get("fromDate") != null && !form.getAdditionalParam().get("fromDate").equals(""))
                ? form.getAdditionalParam().get("fromDate").toString()
                : null;
        String toDate = (form.getAdditionalParam().get("toDate") != null && !form.getAdditionalParam().get("toDate").equals("")) ?
                form.getAdditionalParam().get("toDate").toString() : null;
        String genderType = (form.getAdditionalParam().get("genderType") != null && !form.getAdditionalParam().get("genderType").equals(""))
                ? form.getAdditionalParam().get("genderType").toString()
                : null;
        if (Objects.isNull(hostelName) && Objects.isNull(fromDate) && Objects.isNull(toDate)
                && Objects.isNull(genderType)) {
            return null;
        } else {
            var pageRequest = PageRequest.of(form.getPage() - 1, form.getSize());
            return hostelBiometricTerminalRepository.getGeneralLogDetails(Integer.parseInt(hostelName), fromDate, toDate, genderType, pageRequest)
                    .map(this::mapToGeneralLog);
        }
    }

    private LateNightEntryRecord mapToGeneralLog(Object[] o) {
        return new LateNightEntryRecord(
                Objects.nonNull(o[1]) ? String.valueOf(o[1]) : Strings.EMPTY,
                Objects.nonNull(o[2]) ? String.valueOf(o[2]) : Strings.EMPTY,
                Objects.nonNull(o[3]) ? String.valueOf(o[3]) : Strings.EMPTY,
                Objects.nonNull(o[7]) ? String.valueOf(o[7]) : Strings.EMPTY,
                Objects.nonNull(o[5]) ? utility.dateFormatter(utility.convertToLocalDate(o[5])) : null,
                Objects.nonNull(o[6]) ? String.valueOf(o[6]) : Strings.EMPTY
        );
    }
}