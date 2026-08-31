package com.iitm.hosteldine.service.dashboard.student;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.dto.dashboard.student.HostelVacatingAllowedStudentDto;
import com.iitm.hosteldine.mapper.StudentBioDataFormDetailMapper;
import com.iitm.hosteldine.mapper.dashboard.student.HostelVacatingAllowedStudentMapper;
import com.iitm.hosteldine.repository.StudentBioDataFormDetailRepository;
import com.iitm.hosteldine.repository.dashboard.student.HostelVacatingAllowedStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HostelVacatingAllowedStudentService {
    private final HostelVacatingAllowedStudentRepository hostelVacatingAllowedStudentRepository;
    private final StudentBioDataFormDetailRepository studentBioDataFormDetailRepository;

    public List<Object[]> getStudentAllottedRoomStatus(String studentId) {
        return hostelVacatingAllowedStudentRepository.getStudentAndHostelDetails(studentId);
    }

    public HostelVacatingAllowedStudentDto getHostelVacatingAllowedStudentByStudentId() {
        return hostelVacatingAllowedStudentRepository.findByStudentIdAndActiveFlag(Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase(), ModelConstants.STATUS_ACTIVE)
                .map(HostelVacatingAllowedStudentMapper.INSTANCE::toDto)
                .orElse(HostelVacatingAllowedStudentDto.builder().build());
    }

    public StudentBioDataFormDetailDto getStudentDetails(){
        return studentBioDataFormDetailRepository.findTopByStudentIdAndActiveFlag(Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase(), ModelConstants.STATUS_ACTIVE)
                .map(StudentBioDataFormDetailMapper.INSTANCE::fromStudentBioDataFormDetailEntity)
                .orElse(new StudentBioDataFormDetailDto());
    }

    public HostelVacatingAllowedStudentDto getStudentBalanceDetails() {
        Optional<List<Object[]>> optionalList = hostelVacatingAllowedStudentRepository
                .getStudentBalanceDetails(Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase());
        if (optionalList.isPresent() && !optionalList.get().isEmpty()) {
            Object[] o = optionalList.get().get(0);
            return HostelVacatingAllowedStudentDto.builder()
                    .accHead(Objects.nonNull(o[0]) ? o[0].toString() : ModelConstants.NOT_APPLICABLE)
                    .studentBalance(Objects.nonNull(o[1]) ? Integer.parseInt(o[1].toString()) : 0)
                    .hostelDepositAmt(Objects.nonNull(o[2]) ? Double.parseDouble(o[2].toString()) : 0.0)
                    .netBal(Objects.nonNull(o[3]) ? Double.parseDouble(o[3].toString()) : 0.0)
                    .build();
        }
        return HostelVacatingAllowedStudentDto.builder()
                .accHead(ModelConstants.NOT_APPLICABLE)
                .studentBalance(0)
                .hostelDepositAmt(0.0)
                .netBal(0.0)
                .build();
    }

    public List<Object[]> getDueApprovalStatus(){
       return hostelVacatingAllowedStudentRepository.getDueApprovalStatus(Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase(), ModelConstants.STATUS_ACTIVE);
    }

	public List<Object[]> getStudentAllottedRoomStatusByStudentId(String studentId) {
		return hostelVacatingAllowedStudentRepository.getStudentAndHostelDetails(studentId);
	}
}
