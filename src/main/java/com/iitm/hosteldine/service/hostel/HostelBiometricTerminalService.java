package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.dto.hostel.HostelBiometricTerminalDto;
import com.iitm.hosteldine.mapper.hostel.HostelBiometricTerminalMapper;
import com.iitm.hosteldine.repository.hostel.HostelBiometricTerminalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HostelBiometricTerminalService {

    private final HostelBiometricTerminalRepository hostelBiometricTerminalRepository;

    public List<HostelBiometricTerminalDto> getHostelBiometricTerminals(String activeStatus) {
        return hostelBiometricTerminalRepository.findAllByActiveFlag(activeStatus)
                .stream()
                .map(HostelBiometricTerminalMapper.INSTANCE::toDto)
                .toList();
    }
}