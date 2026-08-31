package com.iitm.hosteldine.service.office;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.constant.dean.DeanConstants;
import com.iitm.hosteldine.dto.dean.DeanApprovalDto;
import com.iitm.hosteldine.dto.dean.PropertyDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.dashboard.student.StudentHostelRoomVacatingRequestEntity;
import com.iitm.hosteldine.repository.dashboard.student.StudentHostelRoomVacatingRequestRepository;
import com.iitm.hosteldine.repository.hostel.StudentHostelRoomVacatingRequestViewRepository;
import com.iitm.hosteldine.service.dashboard.student.StudentHostelRoomVacatingRequestService;
import com.iitm.hosteldine.service.hostel.HostelMasterService;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VacatingStudentDueListService {

    private final StudentHostelRoomVacatingRequestViewRepository studentHostelRoomVacatingRequestViewRepository;
    private final HostelMasterService hostelMasterService;
    private final CommonResponseUtil commonResponseUtil;
    private final StudentHostelRoomVacatingRequestRepository studentHostelRoomVacatingRequestRepository;
    private final StudentHostelRoomVacatingRequestService studentHostelRoomVacatingRequestService;

    private final AtomicInteger counter = new AtomicInteger(0);

    public List<VacatingStudentDueListRecord> getVacatingStudentDueList(PaginationForm form, DeanApprovalDto columnList, String baseUrl) {
        int page = form.getPage() - 1;
        Pageable pageable = PageRequest.of(page, form.getSize());
        Map<String, Object> params = form.getAdditionalParam();
        boolean hasFilter = params != null && params.values().stream()
                .anyMatch(v -> v != null && !v.toString().trim().isEmpty());
        Long hostelId = Optional.ofNullable(Objects.requireNonNull(params).get(DeanConstants.HOSTEL_NAME.getConstants()))
                .filter(v -> !v.toString().trim().isEmpty())
                .map(v -> Long.parseLong(v.toString()))
                .orElse(null);
        HostelMasterDto hostelMasterDto = hostelId != null ? hostelMasterService.getHostelDetailsById(hostelId) : null;
        List<VacatingStudentDueListRecord> result = getResult(hasFilter, pageable, params, hostelMasterDto, columnList, baseUrl);
//        List<VacatingStudentDueListRecord> updatedContent = getUpdatedContent(result, page, form, columnList, baseUrl);
//        return new PageImpl<>(updatedContent, pageable, result.getTotalElements());
        return result;
    }

    public VacatingStudentDueListRecord getVacatingStudentDueDetails(String studentId) {
        Optional<Object[]> result = studentHostelRoomVacatingRequestViewRepository.getVacatingStudentDueDetails(studentId, ModelConstants.STATUS_ACTIVE, ModelConstants.FLAG_LIST[2]);

        if (result.isEmpty()) {
            return new VacatingStudentDueListRecord(
                    null, null, studentId, null, null, null, null,
                    null, null, ModelConstants.EMPTY_STRING, null
            );
        }
        Object[] outer = result.get();
        Object[] o = (Object[]) outer[0];
        LocalDate vacatingDate = null;
        if (o[3] != null) {
            if (o[3] instanceof java.sql.Date sqlDate) {
                vacatingDate = sqlDate.toLocalDate();
            } else if (o[3] instanceof LocalDate localDate) {
                vacatingDate = localDate;
            }
        }
        return new VacatingStudentDueListRecord(null, null, (String) o[1], (String) o[2], null, null, vacatingDate, (String) o[4], null,
                vacatingDate != null
                        ? vacatingDate.format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT))
                        : ModelConstants.EMPTY_STRING, (Double) o[0]
        );
    }


    private VacatingStudentDueListRecord getUpdatedContent(VacatingStudentDueListRecord r, DeanApprovalDto columnList, String baseUrl) {
//        AtomicInteger counter = new AtomicInteger(0);
        return new VacatingStudentDueListRecord(
                r.id(),
                String.valueOf(counter.incrementAndGet()),
                r.studentId(),
                r.studentName(),
                r.hostelName(),
                r.roomNumber(),
                null,
                r.vacatingReason(),
                getButtonList(columnList, baseUrl, r),
                r.vacatingDate() != null
                        ? r.vacatingDate().format(DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT))
                        : ModelConstants.EMPTY_STRING, null
        );

    }

    private List<PropertyDto> getButtonList(DeanApprovalDto columnList, String baseUrl, VacatingStudentDueListRecord r) {
        return columnList.getActionUrlList().stream()
                .map(v -> {
                    try {
                        PropertyDto clone = v.clone();
                        clone.setUrl(baseUrl + commonResponseUtil.getMessage("url.view") + ModelConstants.SLASH +
                                MCrypt.getInstance().encryptToText(r.studentId()) + Constants.BACKTICK + MCrypt.getInstance().encryptToText(String.valueOf(r.id())));
                        return clone;
                    } catch (Exception e) {
                        throw new RuntimeException("Error while cloning PropertyDto or setting URL", e);
                    }
                })
                .collect(Collectors.toList());
    }

    private List<VacatingStudentDueListRecord> getResult(boolean hasFilter, Pageable pageable, Map<String, Object> params, HostelMasterDto hostelMasterDto, DeanApprovalDto columnList, String baseUrl) {
        List<VacatingStudentDueListRecord> page = hasFilter ? studentHostelRoomVacatingRequestViewRepository.getFilteredVacatingStudentDueList(
                ModelConstants.STATUS_ACTIVE,
                ModelConstants.STATUS_INACTIVE,
                ModelConstants.FLAG_LIST[2],
                WorkflowStatus.PENDING.getStatus(),
                nullIfEmpty(params.get(DeanConstants.SEARCH_KEY.getConstants()))):
                studentHostelRoomVacatingRequestViewRepository.getVacatingStudentDueList(ModelConstants.STATUS_ACTIVE,
                ModelConstants.STATUS_INACTIVE, ModelConstants.FLAG_LIST[2], WorkflowStatus.PENDING.getStatus());

        return page.stream()
                .map(r->getUpdatedContent(r, columnList, baseUrl)).toList();
    }

    private String nullIfEmpty(Object value) {
        if (value == null) return null;
        String str = value.toString().trim();
        return str.isEmpty() ? null : str;
    }

    public String approveVacatingStudentDueDetails(Long id) {
        try{
            Optional<StudentHostelRoomVacatingRequestEntity> studentHostelRoomVacatingRequestEntity = studentHostelRoomVacatingRequestRepository.findById(id);
            studentHostelRoomVacatingRequestEntity.ifPresent(studentHostelRoomVacatingRequest -> {
                studentHostelRoomVacatingRequest.setDuesPermissionRequired("Done");
                final String[] authType = {ModelConstants.EMPTY_STRING};
                studentHostelRoomVacatingRequestService.hostelRoomVacatingWorkflow(authType,studentHostelRoomVacatingRequest);
                studentHostelRoomVacatingRequest.setModifiedBy(SecurityCtxUtil.userId());
                studentHostelRoomVacatingRequest.setModifiedAt(LocalDateTime.now());
                studentHostelRoomVacatingRequestRepository.save(studentHostelRoomVacatingRequest);
            });
        } catch (Exception e){
            return Constants.ERROR;
        }
        return Constants.SAVED;
    }
}
