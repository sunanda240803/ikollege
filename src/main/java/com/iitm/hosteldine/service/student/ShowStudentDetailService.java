package com.iitm.hosteldine.service.student;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.student.ShowStudentDetailDto;
import com.iitm.hosteldine.mapper.student.ShowStudentDetailMapper;
import com.iitm.hosteldine.model.student.ShowStudentDetailEntity;
import com.iitm.hosteldine.repository.student.ShowStudentDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.Show;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShowStudentDetailService {

    private final ShowStudentDetailRepository showStudentDetailRepository;

    public List<ShowStudentDetailDto> saveAllStudentShowDetail(List<ShowStudentDetailEntity> showStudentDetailEntityList){
        return showStudentDetailRepository.saveAll(showStudentDetailEntityList)
                .stream()
                .map(ShowStudentDetailMapper.INSTANCE::toDto)
                .toList();
    }

    public List<ShowStudentDetailDto> getAllStudentShowDetailBySeatId(Long seatId,String studentId){
        return showStudentDetailRepository.findAllByActiveFlagAndSeatIdAndStudentIdIgnoreCase(
                ModelConstants.STATUS_ACTIVE, seatId, studentId)
                .stream()
                .map(ShowStudentDetailMapper.INSTANCE::toDto)
                .toList();

    }

    public Double getStudentBalance(String studentId){
        return showStudentDetailRepository.checkStudentBalance(studentId);
    }

    public List<Object[]> getStudentLimit(String studentId,Long eventId){
        return showStudentDetailRepository.getStudentLimit(studentId, eventId, ModelConstants.STATUS_ACTIVE);
    }

    public List<ShowStudentDetailDto> getStudentDetailsByShow(String studentId, Long showId){
        return showStudentDetailRepository.getByStudentDetailsByShow(studentId, showId, ModelConstants.STATUS_ACTIVE)
                .stream()
                .map(ShowStudentDetailMapper.INSTANCE::toDto)
                .toList();
    }

    public Long getTotalSeatsBooked(Long seatId,String studentId){
        return showStudentDetailRepository.countAllByActiveFlagAndSeatIdAndStudentId(
                ModelConstants.STATUS_ACTIVE, seatId,studentId);
    }

    public List<ShowStudentDetailDto> getAllStudentPurchaseDetails() {
        return showStudentDetailRepository
                .findAllByStudentIdAndActiveFlag(SecurityCtxUtil.userId().toUpperCase(), ModelConstants.STATUS_ACTIVE)
                .map(list -> list.stream()
                        .map(ShowStudentDetailMapper.INSTANCE::toDto)
                        .toList())
                .orElseGet(Collections::emptyList);
    }

    public List<Object[]> checkStudentHostler(String studentId){
        return showStudentDetailRepository.checkIsStudentDayShcolarOrHostler(studentId);
    }

}
