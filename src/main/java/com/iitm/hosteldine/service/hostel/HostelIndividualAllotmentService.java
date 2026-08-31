package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.constant.hostel.BulkAllotmentStudentsConstants;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.dto.OtherCandidate.CandidateProfileDto;
import com.iitm.hosteldine.dto.hostel.*;
import com.iitm.hosteldine.dto.mailQueue.MailTemplateDto;
import com.iitm.hosteldine.dto.student.StudentDetailsInfoDto;
import com.iitm.hosteldine.entity.UserManagementOnlineEntity;
import com.iitm.hosteldine.mapper.hostel.HostelFloorMasterMapper;
import com.iitm.hosteldine.mapper.hostel.HostelRoomAllotmentInfoMapper;
import com.iitm.hosteldine.mapper.hostel.HostelRoomInfoMapper;
import com.iitm.hosteldine.mapper.hostel.VacationHostelRoomAllotmentInfoMapper;
import com.iitm.hosteldine.model.OtherCandidate.CandidateStayRequestEntity;
import com.iitm.hosteldine.model.dashboard.student.StudentAppointmentRequestEntity;
import com.iitm.hosteldine.model.hostel.HostelRoomAllotmentInfoEntity;
import com.iitm.hosteldine.model.hostel.StudentHostelRoomVacatingRequestViewEntity;
import com.iitm.hosteldine.model.hostel.VacationHostelRoomAllotmentInfoEntity;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateAppointmentRequestRepository;
import com.iitm.hosteldine.repository.OtherCandidate.CandidateStayRequestRepository;
import com.iitm.hosteldine.repository.dashboard.student.StudentAppointmentRequestRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomAllotmentRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomInfoRepository;
import com.iitm.hosteldine.repository.hostel.StudentHostelRoomVacatingRequestViewRepository;
import com.iitm.hosteldine.repository.hostel.VacationHostelRoomAllotmentInfoRepository;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.service.OnlineUserDetailsService;
import com.iitm.hosteldine.service.OtherCandidate.OtherCandidateService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.util.HostelIndividualAllotmentEnum;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class HostelIndividualAllotmentService {

    private final HostelRoomInfoRepository hostelRoomInfoRepository;
    private final HostelRoomInfoService hostelRoomInfoService;
    private final OnlineUserDetailsService onlineUserDetailsService;
    private final OtherCandidateService otherCandidateService;
    private final HostelMasterService hostelMasterService;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final SimsConfigDataService simsConfigDataService;
    private final CommonResponseUtil commonResponseUtil;
    private final StudentAppointmentRequestRepository studentAppointmentRequestRepository;
    private final CandidateStayRequestRepository candidateStayRequestRepository;
    private final HostelRoomAllotmentRepository hostelRoomAllotmentRepository;
    private final StudentDetailsInfoRepository studentDetailsInfoRepository;
    private final VacationHostelRoomAllotmentInfoRepository vacationHostelRoomAllotmentInfoRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final StudentHostelRoomVacatingRequestViewRepository studentHostelRoomVacatingRequestViewRepository;
    private final Utility utility;
    private final CandidateAppointmentRequestRepository candidateAppointmentRequestRepository;
    private final MailTemplateService mailTemplateService;
    private final MailQueueService mailQueueService;
    private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;
    private final HostelFloorMasterService hostelFloorMasterService;

    public List<HostelRoomInfoDto> getHostelRoomOccupiedList(Long hostelId, LocalDate startDate, LocalDate endDate, String occupancyStatus, String floorId) {
        return hostelRoomInfoRepository
                .getHostelRoomOccupancy(hostelId, startDate, endDate)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::convertHostelRoomDetailToDto)
                .filter(dto -> filterOccupancyStatus(dto, occupancyStatus))
                .filter(dto -> filterByFloor(dto, floorId))
                .toList();
    }

    public List<HostelRoomInfoDto> getGuestRoomOccupiedList(Long hostelId, LocalDate startDate, LocalDate endDate, String occupancyStatus, String floorId) {
        return hostelRoomInfoRepository
                .getGuestRoomOccupancy(hostelId, startDate, endDate)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::convertGuestRoomDetailToDto)
                .filter(dto -> filterOccupancyStatus(dto, occupancyStatus))
                .filter(dto -> filterByFloor(dto, floorId))
                .toList();
    }

    private HostelRoomInfoDto convertHostelRoomDetailToDto(Object[] row) {
        HostelRoomInfoDto dto = new HostelRoomInfoDto();
        dto.setId(((Number) row[0]).longValue());
        dto.setRoomNo((String) row[1]);
        dto.setCapacity((Integer) row[2]);
        String studentCountsStr = (String) row[3];
        System.out.println("Student Count Str for room number " + dto.getRoomNo() + ": " + studentCountsStr);
        List<Integer> studentCounts = Arrays.stream(studentCountsStr.split(ModelConstants.COMMA))
                .map(Integer::parseInt).toList();
        System.out.println("Student Counts " + dto.getRoomNo() + ": " +studentCounts);
        dto.setStudentCount(studentCounts);
        dto.setAverageStudentCount((Integer) row[4]);

        if (row.length > 5 && row[5] != null) {
            long floorId = ((Number) row[5]).longValue();
            HostelFloorMasterDto floorDto = hostelFloorMasterService.getFloorDetailsById(floorId);
            dto.setBuilding(floorDto);
        }
        return dto;
    }

    private HostelRoomInfoDto convertGuestRoomDetailToDto(Object[] row) {
        HostelRoomInfoDto dto = new HostelRoomInfoDto();
        dto.setId(((Number) row[0]).longValue());
        dto.setRoomNo((String) row[1]);
        dto.setCapacity((Integer) row[2]);
        String studentCountsStr = (String) row[3];
        List<Integer> studentCounts = Arrays.stream(studentCountsStr.split(ModelConstants.COMMA))
                .map(Integer::parseInt).toList();
        dto.setStudentCount(studentCounts);
        dto.setAverageStudentCount((Integer) row[4]);

        if (row.length > 5 && row[5] != null) {
            long floorId = ((Number) row[5]).longValue();
            HostelFloorMasterDto floorDto = hostelFloorMasterService.getFloorDetailsById(floorId);
            dto.setBuilding(floorDto);
        }
        return dto;
    }

    private boolean filterOccupancyStatus(HostelRoomInfoDto dto, String occupancyStatus) {
        if (occupancyStatus == null) return true;
        int avg = dto.getAverageStudentCount();
        int cap = dto.getCapacity();
        return switch (occupancyStatus) {
            case Constants.OCCUPIED -> avg >= cap;
            case Constants.VACANT -> avg == 0;
            case Constants.PARTIALLY_OCCUPIED -> avg > 0 && avg < cap;
            default -> true;
        };
    }

    private boolean filterByFloor(HostelRoomInfoDto dto, String floorId) {
        if (floorId == null || Long.parseLong(floorId) <= 0) {
            return true;
        }
        return dto.getBuilding() != null
                && dto.getBuilding().getId() != null
                && dto.getBuilding().getId().equals(Long.parseLong(floorId));
    }



    public List<HostelRoomInfoDto> getRoomOccupancyDetails(Long roomId, LocalDate date) {
        HostelRoomInfoDto hostelRoomInfoDto = hostelRoomInfoService.getHostelRoomInfoDetailsById(roomId.intValue());
        int capacity = Objects.requireNonNullElse(hostelRoomInfoDto.getCapacity(), 0);

        List<String> subRoomIds = IntStream.range(0, capacity)
                .mapToObj(i -> String.valueOf((char) ('A' + i)))
                .collect(Collectors.toList());

        List<HostelRoomInfoDto> occupiedRooms = hostelRoomInfoRepository.getRoomOccupancyDetails(roomId, date, ModelConstants.STATUS_ACTIVE)
                .orElse(new ArrayList<>())
                .stream()
                .map(row -> mapOccupantDetailsBySubRoom(row, date, capacity, subRoomIds))
                .collect(Collectors.toList());

        occupiedRooms.addAll(subRoomIds.stream().map(subRoomId -> {
            HostelRoomInfoDto emptyDto = new HostelRoomInfoDto();
            emptyDto.setSubRoomId(subRoomId);
            emptyDto.setScheduleDate(date.toString());
            emptyDto.setCapacity(capacity);
            return emptyDto;
        }).toList());

        return occupiedRooms;
    }

    public List<HostelRoomInfoDto> getGuestRoomOccupancyDetails(Long roomId, LocalDate date) {
        HostelRoomInfoDto hostelRoomInfoDto = hostelRoomInfoService.getHostelRoomInfoDetailsById(roomId.intValue());
        int capacity = Objects.requireNonNullElse(hostelRoomInfoDto.getCapacity(), 0);
        List<HostelRoomInfoDto> roomInfo = hostelRoomInfoRepository.getRoomOccupancyDetailsOfGuest(roomId, date)
                .orElse(new ArrayList<>())
                .stream()
                .map(row -> mapGuestOccupantDetails(row, date, capacity))
                .collect(Collectors.toList());
        return roomInfo;
    }

    private HostelRoomInfoDto mapOccupantDetailsBySubRoom(Object[] row, LocalDate date, int capacity, List<String> subRoomIds) {
        HostelRoomInfoDto dto = new HostelRoomInfoDto();
        dto.setSubRoomId(row[0] != null ? row[0].toString() : null);
        dto.setScheduleDate(date.toString());
        dto.setStayFromDate(row[1] != null ? ((Date) row[1]).toLocalDate() : null);
        dto.setStayToDate(row[2] != null ? ((Date) row[2]).toLocalDate() : null);
        dto.setVacateDate(row[3] != null ? ((Date) row[3]).toLocalDate() : null);
        dto.setShiftedDate(row[4] != null ? ((Date) row[4]).toLocalDate() : null);
        dto.setStudentName(row[5] != null ? row[5].toString() : null);
        dto.setEmail(row[6] != null ? row[6].toString() : null);
        dto.setStudentId(row[7] != null ? row[7].toString() : null);
        dto.setStudentType(row[8] != null ? row[8].toString() : null);
        dto.setRoomAllotmentId(row[9] != null ? Long.parseLong(row[9].toString()) : 0L);
        dto.setPwdStatus(row[10] != null ? row[10].toString() : null);
        dto.setCapacity(capacity);
        if (subRoomIds != null && dto.getSubRoomId() != null) {
            subRoomIds.remove(dto.getSubRoomId());
        }
        return dto;
    }

    private HostelRoomInfoDto mapGuestOccupantDetails(Object[] row, LocalDate date, int capacity) {
        HostelRoomInfoDto dto = new HostelRoomInfoDto();
        dto.setScheduleDate(date.toString());
        dto.setStayFromDate(row[0] != null ? ((Date) row[0]).toLocalDate() : null);
        dto.setStayToDate(row[1] != null ? ((Date) row[1]).toLocalDate() : null);
        dto.setStudentName(row[2] != null ? row[2].toString() : null);
        dto.setEmail(row[3] != null ? row[3].toString() : null);
        dto.setStudentId(row[4] != null ? row[4].toString() : null);
        dto.setRoomAllotmentId(row[5] != null ? Long.parseLong(row[5].toString()) : 0L);
        dto.setGuestName(row[6] != null ? row[6].toString() : null);
        dto.setGuestRelationship(row[7] != null ? row[7].toString() : null);
        dto.setNoOfDays(row[8] != null ? Long.parseLong(row[8].toString()) : 0L);
        dto.setNoOfPerson(row[9] != null ? Long.parseLong(row[9].toString()) : 0L);
        dto.setCapacity(capacity);
        return dto;
    }

    public Map<LocalDate, Integer> calculateVacantSeats(List<HostelRoomInfoDto> roomOccupancyList, List<LocalDate> dates) {
        return dates.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        date -> roomOccupancyList.stream()
                                .mapToInt(room -> calculateVacancyForRoom(room, dates.getFirst(), date))
                                .sum()
                ));
    }

    private int calculateVacancyForRoom(HostelRoomInfoDto room, LocalDate startDate, LocalDate date) {
        return Optional.of((int) ChronoUnit.DAYS.between(startDate, date))
                .filter(index -> index >= 0)
                .filter(index -> index < room.getStudentCount().size())
                .map(index -> Math.max(0, room.getCapacity() - room.getStudentCount().get(index)) > 0 ? 1 : 0)
                .orElse(0);
    }

    public HostelRoomInfoDto getCandidateDetails(String email, Long hostelId, String subRoomid, String screenType, String studentId, LocalDate selectedDate) throws Exception {
        HostelMasterDto hostelMasterDto = hostelMasterService.getHostelDetailsById(hostelId);
        UserManagementOnlineEntity userManagementOnlineEntity = onlineUserDetailsService.getUserByUserName(email);
        CandidateProfileDto candidateProfile;
        StudentDetailsInfoDto studentDetailsInfoDto;

        if (isUserAvailable(userManagementOnlineEntity) && isScreenCandidate(screenType)) {
            studentDetailsInfoDto = null;
            candidateProfile = otherCandidateService.getOtherCandidateDetails(userManagementOnlineEntity.getUserId());
        } else {
            candidateProfile = null;
            if (isScreenStudent(screenType) && studentId != null) {
                studentDetailsInfoDto = studentDetailsInfoService.getStudentInfoDetails(studentId);
            } else {
                studentDetailsInfoDto = null;
            }
        }

        return switch (screenType) {
            case String s when isScreenCandidate(s) -> {
                HostelRoomInfoDto candidateStayDetailsDto = getCandidateStayDetails(email, selectedDate, screenType, candidateProfile, subRoomid);

                if (candidateStayDetailsDto == null && candidateProfile == null) {
                    yield createNotApprovedOrAvailableResponse(email, screenType);
                } else if(!checkCandidateGender(hostelMasterDto.getHostelGenderType(), Objects.requireNonNull(candidateProfile).getGender())) {
                    yield createGenderCheckFalseResponse(candidateProfile.getGender(), screenType);
                }

                HostelRoomInfoDto processedCandidate = processStayDetails(candidateStayDetailsDto, email, screenType);
                yield (processedCandidate != null) ? processedCandidate : createNotApprovedResponse(screenType);
            }

            case String s when isScreenStudent(s) && studentId != null -> {
                HostelRoomInfoDto studentStayDetailsDto = getStudentStayDetails(studentId, selectedDate, screenType, studentDetailsInfoDto, subRoomid);
                boolean genderStatus = checkGender(hostelMasterDto.getHostelGenderType().trim(), studentId);
                if (studentStayDetailsDto == null) {
                    yield createNotApprovedOrAvailableResponse(studentId, screenType);
                } else if(!genderStatus) {
                    yield createGenderCheckFalseResponse(Objects.requireNonNull(studentDetailsInfoDto).getGender(), screenType);
                }

                HostelRoomInfoDto processedCandidate = processStayDetails(studentStayDetailsDto, studentId, screenType);
                yield (processedCandidate != null) ? processedCandidate : createNotApprovedResponse(screenType);
            }

            case String s when isScreenCandidate(s) && checkCandidateGender(hostelMasterDto.getHostelGenderType(), Objects.requireNonNull(candidateProfile).getGender()) ->
                    createGenderCheckFalseResponse(candidateProfile.getGender(), screenType);

            case String s when isScreenStudent(s) && checkGender(hostelMasterDto.getHostelGenderType(), Objects.requireNonNull(studentDetailsInfoDto).getGender()) ->
                    createGenderCheckFalseResponse(studentDetailsInfoDto.getGender(), screenType);

            default -> createNotApprovedOrAvailableResponse(studentId, screenType);
        };
    }


    private HostelRoomInfoDto processStayDetails(HostelRoomInfoDto detailsDto, String identifier, String screenType) {
        if (detailsDto == null || detailsDto.getStatus() == null)
            return createNotApprovedOrAvailableResponse(identifier, screenType);

        WorkflowStatus status = getWorkflowStatus(detailsDto.getStatus());

        return switch (status) {
            case APPROVED -> buildCandidateOrStudentDetailDto(detailsDto);
            case ALLOTTED -> createAlreadyAllottedResponse(identifier, screenType);
            default -> createNotApprovedResponse(screenType);
        };
    }

    private WorkflowStatus getWorkflowStatus(String status) {
        return Arrays.stream(WorkflowStatus.values())
                .filter(ws -> ws.getStatus().equalsIgnoreCase(status))
                .findFirst()
                .orElse(WorkflowStatus.DEFAULT);
    }


    private HostelRoomInfoDto getStudentStayDetails(String studentId, LocalDate selectedDate, String screenType, StudentDetailsInfoDto studentDetailsInfoDto, String subRoomId) {
        Optional<Object> studentStayDetails = hostelRoomInfoRepository.findStudentStayDetails(studentId, selectedDate);
        return studentStayDetails.map(data -> mapToHostelRoomInfoDto(data, screenType.trim(), studentDetailsInfoDto.getGender(), subRoomId)).orElse(null);
    }


    private HostelRoomInfoDto getCandidateStayDetails(String email, LocalDate selectedDate, String screenType, CandidateProfileDto candidateProfileDto, String subRoomId) {
        Optional<Object> candidateStayDetails = hostelRoomInfoRepository.findCandidateStayDetails(email.trim(), selectedDate);
        return candidateStayDetails.map(data -> mapToHostelRoomInfoDto(data, screenType.trim(), candidateProfileDto.getGender(), subRoomId)).orElse(null);
    }

    private HostelRoomInfoDto mapToHostelRoomInfoDto(Object data, String screenType, String gender, String subRoomId) {
        if (!(data instanceof Object[] row) || row.length == 0) {
            return new HostelRoomInfoDto();
        }
        HostelRoomInfoDto hostelRoomInfoDto = new HostelRoomInfoDto();
        hostelRoomInfoDto.setRequestId(row[0] instanceof Number ? ((Number) row[0]).longValue() : null);
        hostelRoomInfoDto.setStudentName(row[1] != null ? row[1].toString() : null);
        hostelRoomInfoDto.setDob(row[2] instanceof String ?
                LocalDate.parse((String) row[2], DateTimeFormatter.ofPattern(Constants.BACKEND_DATE_FORMAT)) :
                (row[2] instanceof Date ? ((Date) row[2]).toLocalDate() : null));
        hostelRoomInfoDto.setStatus(row[3] != null ? row[3].toString() : null);
        hostelRoomInfoDto.setStayFromDate(row[4] instanceof Date ? ((Date) row[4]).toLocalDate() : null);
        hostelRoomInfoDto.setStayToDate(row[5] instanceof Date ? ((Date) row[5]).toLocalDate() : null);
        hostelRoomInfoDto.setCategory(row[6] != null ? row[6].toString() : null);
        hostelRoomInfoDto.setDining(row[7] instanceof Boolean ? (Boolean) row[7] : null);
        hostelRoomInfoDto.setEmail(row[8] != null ? row[8].toString() : null);

        if (isScreenCandidate(screenType)) {
            hostelRoomInfoDto.setStayId(row.length > 9 && row[9] instanceof Number ? ((Number) row[9]).longValue() : null);
            hostelRoomInfoDto.setOtherCategory(row[10] != null ? row[10].toString() : null);
        } else if (isScreenStudent(screenType)) {
            hostelRoomInfoDto.setStudentId(row[9] != null ? row[9].toString() : null);
        }

        hostelRoomInfoDto.setGender(gender);
        hostelRoomInfoDto.setSubRoomId(subRoomId.trim());

        return hostelRoomInfoDto;
    }

    private HostelRoomAllotmentInfoDto mapRoomOccupancyToHostelRoomInfoDto(Object[] data) {
        HostelRoomAllotmentInfoDto hrai = new HostelRoomAllotmentInfoDto();
        int i = -1;
        hrai.setRoomAllotmentId(utility.parseLong(data[++i]));
        hrai.setHostelId(utility.parseLong(data[++i]));
        hrai.setHostelName(String.valueOf(data[++i]));
        hrai.setFloorId(utility.parseLong(data[++i]));
        hrai.setFloorName(String.valueOf(data[++i]));
        hrai.setRoomId(utility.parseLong(data[++i]));
        hrai.setRoomNo(String.valueOf(data[++i]));
        hrai.setSubRoomId(String.valueOf(data[++i]));
        hrai.setStudentType(String.valueOf(data[++i]));
        hrai.setRequestId(utility.parseLong(data[++i]));
        hrai.setStudentName(String.valueOf(data[++i]));
        hrai.setStudentId(String.valueOf(data[++i]));
        hrai.setStayFromDate(utility.convertToLocalDate(data[++i]));
        hrai.setStayToDate(utility.convertToLocalDate(data[++i]));
        hrai.setVacateDate(utility.convertToLocalDate(data[++i]));
        hrai.setShiftedDate(utility.convertToLocalDate(data[++i]));
        hrai.setDob(utility.convertToLocalDate(data[++i]));
        hrai.setEmail(String.valueOf(data[++i]));
        hrai.setNatureOfAppointment(String.valueOf(data[++i]));
        hrai.setDiningRequired(String.valueOf(data[++i]));
        hrai.setAllocationType(String.valueOf(data[++i]));
        ++i;
        ++i;
        ++i;
        ++i;
        ++i;//create by, createdAt, modified by, modified at and active flag. not required.
        hrai.setVacationCategory(String.valueOf(data[++i]));
        return hrai;
    }


    private HostelRoomInfoDto createNotApprovedOrAvailableResponse(String user, String screenType) {
        HostelRoomInfoDto dto = new HostelRoomInfoDto();
        dto.setError(HostelConstants.ACCOMMODATION_FOR_THE.getConstants() +
                ModelConstants.SPACE +
                (isScreenCandidate(screenType)
                        ? (HostelConstants.CANDIDATE_WITH_EMAIL_ID.getConstants() + ModelConstants.SPACE + ModelConstants.COLAN)
                        : (HostelConstants.STUDENT_WITH_EMAIL_ID.getConstants() + ModelConstants.SPACE + ModelConstants.COLAN) + ModelConstants.SPACE)
                + user + ModelConstants.SPACE + HostelConstants.NOT_AVAILABLE_OR_APPROVED.getConstants());
        return dto;
    }

    private HostelRoomInfoDto createAlreadyAllottedResponse(String user, String screenType) {
        HostelRoomInfoDto dto = new HostelRoomInfoDto();
        dto.setError(HostelConstants.ACCOMMODATION_FOR_THE.getConstants() +
                ModelConstants.SPACE +
                (HostelConstants.CANDIDATE.getConstants().equalsIgnoreCase(screenType.trim())
                        ? (HostelConstants.CANDIDATE_WITH_EMAIL_ID.getConstants() + ModelConstants.SPACE + ModelConstants.COLAN)
                        : (HostelConstants.STUDENT_WITH_EMAIL_ID.getConstants() + ModelConstants.SPACE + ModelConstants.COLAN) + ModelConstants.SPACE)
                + user + ModelConstants.SPACE + HostelConstants.ALLOTTED_FOR_SAME_DATE.getConstants());
        return dto;
    }

    private HostelRoomInfoDto createNotApprovedResponse(String screenType) {
        HostelRoomInfoDto dto = new HostelRoomInfoDto();
        dto.setError(HostelConstants.CANDIDATE.getConstants().equalsIgnoreCase(screenType) ?
                HostelConstants.CANDIDATE_CAPS.getConstants() :
                HostelConstants.STUDENT_CAPS.getConstants() + ModelConstants.SPACE + HostelConstants.REQUEST_NOT_APPROVED.getConstants());
        return dto;
    }

    private HostelRoomInfoDto createGenderCheckFalseResponse(String gender, String screenType) {
        HostelRoomInfoDto dto = new HostelRoomInfoDto();
        dto.setError(HostelConstants.IN_THIS_HOSTEL.getConstants() + ModelConstants.SPACE + ModelConstants.SINGLE_QUOTE +
                (gender.equalsIgnoreCase(HostelConstants.F.getConstants()) ?
                        HostelConstants.FEMALE.getConstants() : HostelConstants.MALE.getConstants()) +
                ModelConstants.SINGLE_QUOTE + ModelConstants.SPACE + screenType + ModelConstants.SPACE + HostelConstants.CANT_BE_ALLOWED.getConstants());
        return dto;
    }

    private boolean isUserAvailable(UserManagementOnlineEntity userManagementOnlineEntity) {
        return userManagementOnlineEntity != null && userManagementOnlineEntity.getUserEmailId() != null;
    }

    private boolean checkCandidateGender(String hostelGender, String candidateGender) {
        HostelRoomInfoDto dto = new HostelRoomInfoDto();
        dto.setGender(hostelGender);
        return hostelGender.equalsIgnoreCase(candidateGender);
    }

    private boolean isScreenCandidate(String screenType) {
        return HostelConstants.CANDIDATE.getConstants().equalsIgnoreCase(screenType.trim());
    }

    private boolean isScreenDirect(String screenType) {
        return HostelConstants.DIRECT.getConstants().equalsIgnoreCase(screenType.trim());
    }

    private boolean isScreenStudent(String screenType) {
        return HostelConstants.STUDENT.getConstants().equalsIgnoreCase(screenType.trim());
    }

    private boolean isScreenStudentApp(String screenType) {
        return HostelConstants.STUDENT_APP.getConstants().equalsIgnoreCase(screenType.trim());
    }

    private HostelRoomInfoDto buildCandidateOrStudentDetailDto(HostelRoomInfoDto dto) {
        HostelRoomInfoDto hostelRoomInfoDto = new HostelRoomInfoDto();
        hostelRoomInfoDto.setRequestId(dto.getRequestId());
        hostelRoomInfoDto.setStayFromDate(dto.getStayFromDate());
        hostelRoomInfoDto.setStayToDate(dto.getStayToDate());
        hostelRoomInfoDto.setCategory(dto.getCategory());
        hostelRoomInfoDto.setOtherCategory(dto.getOtherCategory());
        hostelRoomInfoDto.setDining(dto.getDining());
        hostelRoomInfoDto.setStudentName(dto.getStudentName());
        hostelRoomInfoDto.setDob(dto.getDob());
        hostelRoomInfoDto.setGender(dto.getGender());
        hostelRoomInfoDto.setEmail(dto.getEmail());
        if (dto.getStudentId() != null) {
            hostelRoomInfoDto.setStudentId(dto.getStudentId());
        }
        return hostelRoomInfoDto;
    }

    public String getDateValidationQuery(String fromDate, String toDate, String fromColumn, String toColumn) {
        return "(( :" + fromDate + " BETWEEN " + fromColumn + " AND " + toColumn + ") " +
                "OR ( :" + toDate + " BETWEEN " + fromColumn + " AND " + toColumn + ") " +
                "OR ((" + fromColumn + " >= :" + fromDate + ") AND (" + toColumn + " <= :" + toDate + ")))";
    }

    public List<HostelRoomInfoDto> getAllotmentInfo(LocalDate stayFrom, LocalDate stayTo, String filterValue, String filterColumn) {
        String dateValidationQuery = getDateValidationQuery(HostelConstants.STAY_FROM.getConstants(),
                HostelConstants.STAY_TO.getConstants(), HostelConstants.STAY_FROM_DATE.getConstants(), HostelConstants.STAY_TO_DATE.getConstants());

        String sql = "SELECT * FROM schooldev.\"VACATION_HOSTEL_ROOM_ALLOTMENT_INFO\" WHERE ("
                + dateValidationQuery +
                " OR (stay_from_date >= :stayFrom AND stay_to_date IS NULL)) " +
                "AND " + filterColumn + " = :filterValue AND active_flag = '" + ModelConstants.STATUS_ACTIVE + "'";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(HostelConstants.STAY_FROM.getConstants(), stayFrom)
                .addValue(HostelConstants.STAY_TO.getConstants(), stayTo)
                .addValue(HostelConstants.FILTER_VALUE.getConstants(), filterValue);

        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(HostelRoomInfoDto.class));
    }

    @Transactional
    public String saveOrInsertAllotment(VacationHostelRoomAllotmentInfoDto vacationHostelRoomAllotmentInfoDto) {
        boolean genderStatus = true;
        HostelRoomInfoDto hostelRoomInfoDto = hostelRoomInfoService.getHostelRoomInfoDetailsById(vacationHostelRoomAllotmentInfoDto.getRoomId().intValue());
        hostelRoomInfoDto.setSubRoomId(vacationHostelRoomAllotmentInfoDto.getSubRoomid());
        HostelMasterDto hostelMasterDto = null;
        String status;

        if (isScreenDirect(vacationHostelRoomAllotmentInfoDto.getScreenType())) {
            hostelMasterDto = hostelMasterService.getHostelDetailsById(vacationHostelRoomAllotmentInfoDto.getBuilding().getHostel().getId());
            genderStatus = vacationHostelRoomAllotmentInfoDto.getGender().equalsIgnoreCase(hostelMasterDto.getHostelGenderType());
            vacationHostelRoomAllotmentInfoDto.setRequestid(
                    studentAppointmentRequestRepository.getRecentStatus(
                            vacationHostelRoomAllotmentInfoDto.getStudentId(), ModelConstants.STATUS_ACTIVE
                    ).map(entity -> entity.getId().toString()).orElse(null)
            );
        }
        if (genderStatus){
            if (isCandidateHostelOccupant(vacationHostelRoomAllotmentInfoDto))
                status = HostelConstants.ROOM_ALREADY_OCCUPIED.getConstants();

            else if (!getCandidateOrStudentAllotmentList(vacationHostelRoomAllotmentInfoDto).isEmpty() ||
                    !getCandidateOrStudentAllotmentList(vacationHostelRoomAllotmentInfoDto).isEmpty())
                status = HostelConstants.STD_CANDIDATE_ALREADY_EXIST.getConstants();
            else {
                saveVacatingHostelRoomAllotmentInfo(vacationHostelRoomAllotmentInfoDto, hostelRoomInfoDto);
                if (isScreenCandidate(vacationHostelRoomAllotmentInfoDto.getScreenType())) {
                    updateCandidateStayRequest(vacationHostelRoomAllotmentInfoDto, hostelMasterDto, hostelRoomInfoDto);
                } else if (isScreenStudent(vacationHostelRoomAllotmentInfoDto.getScreenType())) {
                    updateStudentAppointmentRequest(vacationHostelRoomAllotmentInfoDto, hostelMasterDto, hostelRoomInfoDto);
                }
                status = vacationHostelRoomAllotmentInfoDto.getScreenType() + ModelConstants.HYPHEN + Constants.SAVED;
            }
        }else{
            status = HostelConstants.IN_THIS_HOSTEL.getConstants() + ModelConstants.COMMA + ModelConstants.SPACE
                    + (HostelConstants.M.getConstants().equalsIgnoreCase(vacationHostelRoomAllotmentInfoDto.getGender()) ?
                    HostelConstants.MALE.getConstants() : HostelConstants.FEMALE.getConstants() ) +
                    ModelConstants.SPACE + HostelConstants.STUDENT_CAPS.getConstants() + ModelConstants.SPACE + HostelConstants.CANT_BE_ALLOWED.getConstants();
        }
        return status;
    }

    private boolean isCandidateHostelOccupant(VacationHostelRoomAllotmentInfoDto vacationHostelRoomAllotmentInfoDto){
        return checkHostelOccupancy(vacationHostelRoomAllotmentInfoDto.getHostelId(),
                vacationHostelRoomAllotmentInfoDto.getRoomId(),
                vacationHostelRoomAllotmentInfoDto.getSubRoomid(),
                vacationHostelRoomAllotmentInfoDto.getStayFromDate(),
                vacationHostelRoomAllotmentInfoDto.getStayToDate());
    }

    private List<HostelRoomInfoDto> getCandidateOrStudentAllotmentList(VacationHostelRoomAllotmentInfoDto vacationHostelRoomAllotmentInfoDto){
        if (isScreenCandidate(vacationHostelRoomAllotmentInfoDto.getScreenType())) {
            return getAllotmentInfo(
                    vacationHostelRoomAllotmentInfoDto.getStayFromDate(),
                    vacationHostelRoomAllotmentInfoDto.getStayToDate(),
                    vacationHostelRoomAllotmentInfoDto.getEmail(),
                    HostelConstants.EMAIL.getConstants()
            );
        }else if (isScreenStudent(vacationHostelRoomAllotmentInfoDto.getScreenType())) {
            return getAllotmentInfo(
                    vacationHostelRoomAllotmentInfoDto.getStayFromDate(),
                    vacationHostelRoomAllotmentInfoDto.getStayToDate(),
                    vacationHostelRoomAllotmentInfoDto.getStudentId(),
                    HostelConstants.STUDENT_ID.getConstants()
            );
        }
        return new ArrayList<>();
    }

    public void saveVacatingHostelRoomAllotmentInfo(VacationHostelRoomAllotmentInfoDto vacationHostelRoomAllotmentInfoDto,
                                            HostelRoomInfoDto hostelRoomInfoDto) {
        VacationHostelRoomAllotmentInfoEntity entity = new VacationHostelRoomAllotmentInfoEntity();
        entity.setRequestid(isScreenDirect(vacationHostelRoomAllotmentInfoDto.getScreenType()) ?
                String.valueOf(0) : vacationHostelRoomAllotmentInfoDto.getRequestid());
        entity.setStudentName(vacationHostelRoomAllotmentInfoDto.getStudentName());
        entity.setDob(vacationHostelRoomAllotmentInfoDto.getDob());
        entity.setEmail(vacationHostelRoomAllotmentInfoDto.getEmail());
        entity.setStayFromDate(vacationHostelRoomAllotmentInfoDto.getStayFromDate());
        entity.setStayToDate(vacationHostelRoomAllotmentInfoDto.getStayToDate());
        entity.setNatureOfAppointment(vacationHostelRoomAllotmentInfoDto.getNatureOfAppointment());
        entity.setDiningRequired(vacationHostelRoomAllotmentInfoDto.getDiningRequired());
        entity.setStayId(vacationHostelRoomAllotmentInfoDto.getStayId() == null ? String.valueOf(0) : vacationHostelRoomAllotmentInfoDto.getStayId());
        entity.setRoomid(HostelRoomInfoMapper.INSTANCE.toHostelRoomInfoEntity(hostelRoomInfoDto));
        entity.setBuilding(HostelFloorMasterMapper.INSTANCE.toHostelFloorMasterEntity(hostelRoomInfoDto.getBuilding()));
        entity.setStudentId(vacationHostelRoomAllotmentInfoDto.getStudentId() != null ? vacationHostelRoomAllotmentInfoDto.getStudentId() : null);
        entity.setSubRoomid(vacationHostelRoomAllotmentInfoDto.getSubRoomid());
        entity.setStayType(vacationHostelRoomAllotmentInfoDto.getSubRoomid());
        entity.setGender(vacationHostelRoomAllotmentInfoDto.getGender().toUpperCase());
        vacationHostelRoomAllotmentInfoRepository.saveAndFlush(entity);
    }
    private void updateCandidateStayRequest(VacationHostelRoomAllotmentInfoDto vacationHostelRoomAllotmentInfoDto,
                                            HostelMasterDto hostelMasterDto,
                                            HostelRoomInfoDto hostelRoomInfoDto) {
        Optional.ofNullable(vacationHostelRoomAllotmentInfoDto.getStayId())
                .map(String::trim)
                .filter(stayId -> !stayId.isEmpty())
                .map(this::parseLongSafely)
                .flatMap(stayId -> candidateStayRequestRepository.findByStayIdAndActiveFlag(stayId, ModelConstants.STATUS_ACTIVE))
                .ifPresentOrElse(
                        existing -> updateCandidateStay(existing, vacationHostelRoomAllotmentInfoDto, hostelMasterDto, hostelRoomInfoDto),
                        () -> updateCandidateAppointment(vacationHostelRoomAllotmentInfoDto, hostelMasterDto, hostelRoomInfoDto)
                );
    }

    private Long parseLongSafely(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void updateCandidateStay(CandidateStayRequestEntity existing,
                                     VacationHostelRoomAllotmentInfoDto dto,
                                     HostelMasterDto hostelMasterDto,
                                     HostelRoomInfoDto hostelRoomInfoDto) {
        existing.setApprovalStatus(WorkflowStatus.ALLOTTED.getStatus());
        existing.setStatusNotes(setStudentOrCandidateStatusNote(hostelMasterDto, hostelRoomInfoDto));
        existing.setModifiedBy(SecurityCtxUtil.userName());
        existing.setModifiedAt(LocalDateTime.now());
        candidateStayRequestRepository.save(existing);
    }

    private void updateCandidateAppointment(VacationHostelRoomAllotmentInfoDto dto,
                                            HostelMasterDto hostelMasterDto,
                                            HostelRoomInfoDto hostelRoomInfoDto) {
        Optional.ofNullable(dto.getRequestid())
                .flatMap(requestId -> candidateAppointmentRequestRepository.findByIdAndActiveFlag(Long.valueOf(requestId), ModelConstants.STATUS_ACTIVE))
                .ifPresent(existing -> {
                    existing.setApprovalStatus(WorkflowStatus.ALLOTTED.getStatus());
                    existing.setStatusNotes(setStudentOrCandidateStatusNote(hostelMasterDto, hostelRoomInfoDto));
                    existing.setModifiedBy(SecurityCtxUtil.userName());
                    existing.setModifiedAt(LocalDateTime.now());
                    candidateAppointmentRequestRepository.save(existing);
                });
    }


    private void updateStudentAppointmentRequest(VacationHostelRoomAllotmentInfoDto vacationHostelRoomAllotmentInfoDto,
                                                 HostelMasterDto hostelMasterDto,
                                                 HostelRoomInfoDto hostelRoomInfoDto) {
        Optional<StudentAppointmentRequestEntity> appointmentRequestEntity =
                studentAppointmentRequestRepository.findById(
                        Long.valueOf(vacationHostelRoomAllotmentInfoDto.getRequestid()));

        if (appointmentRequestEntity.isPresent()) {
            StudentAppointmentRequestEntity existing = appointmentRequestEntity.get();
            existing.setStatus(WorkflowStatus.ALLOTTED.getStatus());
            existing.setStatusNotes(setStudentOrCandidateStatusNote(hostelMasterDto, hostelRoomInfoDto));
            studentAppointmentRequestRepository.save(existing);
        }
    }

    private String setStudentOrCandidateStatusNote(HostelMasterDto hostelMasterDto, HostelRoomInfoDto hostelRoomInfoDto){
        return HostelConstants.YOU_HAVE_BEEN_ALLOTTED.getConstants() + ModelConstants.SPACE +
                (hostelRoomInfoDto.getBuilding() != null ? hostelRoomInfoDto.getBuilding().getHostel().getHostelName() :  ModelConstants.NOT_APPLICABLE) +
                ModelConstants.SPACE + ModelConstants.HYPHEN + ModelConstants.SPACE + HostelConstants.ROOM_NO.getConstants() + ModelConstants.SPACE +
                hostelRoomInfoDto.getRoomNo() + ModelConstants.SPACE + ModelConstants.HYPHEN + ModelConstants.SPACE + hostelRoomInfoDto.getSubRoomId();
    }

    public StudentHostelInfoDetailsDto validateStudentAllocationDetails(StudentHostelInfoDetailsDto dto) {
        String studentId = dto.getNewStudentId();
        var studentDetails = hostelRoomInfoRepository.getStudentHostelRoomVacatingDetails(studentId, ModelConstants.STATUS_ACTIVE)
                .flatMap(list -> list.stream().findFirst())
                .orElse(null);

        if (Objects.isNull(studentDetails) || studentDetails.length == 0) {
            return StudentHostelInfoDetailsDto.builder()
                    .status(HostelIndividualAllotmentEnum.STUDENT_NOT_EXIST.getValue())
                    .errorMessage(commonResponseUtil.getMessage("message.error.student.not.exists"))
                    .build();
        }

        /*StudentHotelInfoDetailsDto studentHotelInfoDetailsDto = isStudentSettlementCompleted(studentDetails)
                ? StudentHotelInfoDetailsDto
                .builder()
                .status(HostelIndividualAllotmentEnum.SETTLEMENT_COMPLETED.getValue())
                .errorMessage(commonResponseUtil.getMessage("message.error.settlement.completed"))
                .build()
                : getStudentRoomDetails(studentId);*/

        if (isStudentSettlementCompleted(studentDetails)) {
            return StudentHostelInfoDetailsDto
                    .builder()
                    .status(HostelIndividualAllotmentEnum.SETTLEMENT_COMPLETED.getValue())
                    .errorMessage(commonResponseUtil.getMessage("message.error.settlement.completed"))
                    .build();
        }

        if (!HostelIndividualAllotmentEnum.SWAP.getValue().equalsIgnoreCase(dto.getScreenType())) {
            dto = checkVacatingForm(studentDetails, dto);
            if (HostelIndividualAllotmentEnum.VACATING_FORM_APPROVED.getValue().equalsIgnoreCase(dto.getStatus()) ||
                    HostelIndividualAllotmentEnum.ALREADY_ALLOTTED.getValue().equalsIgnoreCase(dto.getStatus())) {
                return dto;
            }
        }

        StudentHostelInfoDetailsDto studentHostelInfoDetailsDto = getStudentRoomDetails(studentId);
        HostelMasterDto hostelMasterDto = hostelMasterService.getHostelDetailsById(dto.getHostelId());
//        if (!HostelIndividualAllotmentEnum.NOT_EXIST.getValue().equalsIgnoreCase(studentHostelInfoDetailsDto.getStatus())) {
            boolean genderStatus = checkGender(hostelMasterDto.getHostelGenderType().trim(), studentId);
            if (!genderStatus) {
                studentHostelInfoDetailsDto.setStatus(HostelIndividualAllotmentEnum.GENDER_FAIL.getValue());
                String gender = hostelMasterDto.getHostelGenderType().equalsIgnoreCase("M") ? "Female" : "Male";
                String message = commonResponseUtil.getMessage("message.error.gender.fail").replace("%gender%", gender);
                studentHostelInfoDetailsDto.setErrorMessage(message);
                return studentHostelInfoDetailsDto;
            }
//        }

        if (checkUserHasPrivilegesToAllocateRoom()) {
            studentHostelInfoDetailsDto.setHasPrivilege(true);
        } else {
            studentHostelInfoDetailsDto.setErrorMessage(commonResponseUtil.getMessage("message.error.hostel.allocation.no.privileges"));
            studentHostelInfoDetailsDto.setHasPrivilege(false);
        }

        return studentHostelInfoDetailsDto;
    }

    private boolean isStudentSettlementCompleted(Object[] studentDetails) {
        return Optional.ofNullable(studentDetails[1])
                .map(Object::toString)
                .map(status -> !status.trim().equalsIgnoreCase(ModelConstants.NO))
                .orElse(false);
    }

    public StudentHostelInfoDetailsDto checkVacatingForm(Object[] o, StudentHostelInfoDetailsDto
            studentHotelInfoDetailsDto) {
        if (Objects.nonNull(o[2])) {
            studentHotelInfoDetailsDto.setStatus(HostelIndividualAllotmentEnum.VACATING_FORM_APPROVED.getValue());
            studentHotelInfoDetailsDto.setErrorMessage(commonResponseUtil.getMessage("message.error.vacating.form.approved"));
        } else {
            boolean occupancyStatus = checkHostelOccupancy(studentHotelInfoDetailsDto.getHostelId(),
                    studentHotelInfoDetailsDto.getRoomId(), studentHotelInfoDetailsDto.getSubRoomId(), LocalDate.now(),
                    LocalDate.now().plusYears(1));
            if (occupancyStatus) {
                studentHotelInfoDetailsDto.setStatus(HostelIndividualAllotmentEnum.ALREADY_ALLOTTED.getValue());
                studentHotelInfoDetailsDto.setErrorMessage(commonResponseUtil.getMessage("message.error.seat.already.allotted"));
            }
        }
        return studentHotelInfoDetailsDto;
    }

    /*Returns true if seat occupied else false*/
    public boolean checkHostelOccupancy(Long hostelId, Long roomId, String subRoomId, LocalDate stayFrom, LocalDate
            stayTo) {
        return getHostelOccupancy(hostelId, roomId, subRoomId, stayFrom, stayTo)
                .filter(list -> !list.isEmpty())
                .isPresent();
    }

    public Optional<List<Object[]>> getHostelOccupancy(Long hostelId, Long roomId, String subRoomId, LocalDate
            stayFrom, LocalDate stayTo) {
        return hostelRoomAllotmentRepository.getAllottedDetails(stayFrom, stayTo, hostelId, roomId, subRoomId);
    }

    public boolean checkGender(String hostelGender, String studentId) {
        return studentDetailsInfoRepository.findByStudentIdAndActiveFlag(studentId, ModelConstants.STATUS_ACTIVE)
                .map(info -> hostelGender.equalsIgnoreCase(info.getGender()))
                .orElse(false);
    }

    private StudentHostelInfoDetailsDto getStudentRoomDetails(String studentId) {
        return hostelRoomInfoRepository.getStudentRoomDetails(studentId, ModelConstants.STATUS_ACTIVE)
                .flatMap(list -> list.stream().findFirst())
                .map(this::mapToStudentHotelInfoDetailsDto)
                .orElseGet(() -> StudentHostelInfoDetailsDto.builder()
                        .status(HostelIndividualAllotmentEnum.NOT_EXIST.getValue())
                        .errorMessage(commonResponseUtil.getMessage("message.error.hostel.allocation.not.exists"))
                        .build());
    }

    private StudentHostelInfoDetailsDto mapToStudentHotelInfoDetailsDto(Object[] studentRoomDetails) {
        return StudentHostelInfoDetailsDto.builder()
                .roomNo(Objects.nonNull(studentRoomDetails[0]) ? Long.parseLong(studentRoomDetails[0].toString()) : 0)
                .hostelName(Objects.nonNull(studentRoomDetails[1]) ? studentRoomDetails[1].toString() : Strings.EMPTY)
                .floorName(Objects.nonNull(studentRoomDetails[2]) ? studentRoomDetails[2].toString() : Strings.EMPTY)
                .subRoomId(Objects.nonNull(studentRoomDetails[3]) ? String.valueOf(studentRoomDetails[3]) : Strings.EMPTY)
                .stayFromDate(Objects.nonNull(studentRoomDetails[4]) ? LocalDate.parse(studentRoomDetails[4].toString()) : null)
                .hostelGenderType(Objects.nonNull(studentRoomDetails[5]) ? String.valueOf(studentRoomDetails[5]) : null)
                .roomId(Objects.nonNull(studentRoomDetails[6]) ? Long.parseLong(studentRoomDetails[6].toString()) : 0L)
                .status(HostelIndividualAllotmentEnum.EXIST.getValue())
                .errorMessage(commonResponseUtil.getMessage("message.error.hostel.allocation.already.exists"))
                .build();
    }

    public boolean checkUserHasPrivilegesToAllocateRoom() {
        return simsConfigDataService.getSimConfigValueFromJsonArray(SimsConfigDataService.ALL_HOSTEL_VIEW_ROLES)
                .stream()
                .anyMatch(role -> role.getValue().equalsIgnoreCase(SecurityCtxUtil.userRole()));
    }

    @Transactional
    public boolean swapStudents(StudentHostelInfoDetailsDto dto) {
        return swapStudentAllotmentDetails(dto.getCurrentStudentId(), dto.getNewStudentId());
    }

    public boolean swapStudentAllotmentDetails(String currentStudentId, String newStudentId) {
        var currentStudentDetails = hostelRoomAllotmentRepository.findByStudentIdEqualsIgnoreCaseAndActiveFlagAndVacateDateIsNullAndShiftedDateIsNull(currentStudentId, ModelConstants.STATUS_ACTIVE)
                .orElse(null);
        var newStudentDetails = hostelRoomAllotmentRepository.findByStudentIdEqualsIgnoreCaseAndActiveFlagAndVacateDateIsNullAndShiftedDateIsNull(newStudentId, ModelConstants.STATUS_ACTIVE)
                .orElse(null);

        if (Objects.isNull(currentStudentDetails) || Objects.isNull(newStudentDetails)) {
            return false;
        }

        HostelRoomAllotmentInfoDto newStudentDto = HostelRoomAllotmentInfoMapper.INSTANCE.fromHostelRoomAllotmentInfoEntity(newStudentDetails);
        HostelRoomAllotmentInfoDto currentStudentDto = HostelRoomAllotmentInfoMapper.INSTANCE.fromHostelRoomAllotmentInfoEntity(currentStudentDetails);

        HostelRoomAllotmentInfoDto swapedStudent1 = processStudentAllotment(currentStudentDetails, newStudentDto);
        HostelRoomAllotmentInfoDto swapedStudent2 = processStudentAllotment(newStudentDetails, currentStudentDto);
        boolean status = Objects.nonNull(swapedStudent1) && Objects.nonNull(swapedStudent2);
        if (status) {
            sendSwapMail(swapedStudent1, swapedStudent2);
            return true;
        }
        return false;
    }

    private void sendSwapMail(HostelRoomAllotmentInfoDto swapedStudent1, HostelRoomAllotmentInfoDto swapedStudent2) {
        StudentDetailsInfoDto studentInfoDetails1 = studentDetailsInfoService.getStudentInfoDetails(swapedStudent1.getStudentId());
        StudentDetailsInfoDto studentInfoDetails2 = studentDetailsInfoService.getStudentInfoDetails(swapedStudent2.getStudentId());
        if (Objects.nonNull(studentInfoDetails1) && Objects.nonNull(studentInfoDetails2)) {
            MailTemplateDto mailTemplate = mailTemplateService.getMailTemplate(ModelConstants.HOSTEL_SWAP_MAIL);
            if (Objects.nonNull(mailTemplate.getMailType())) {
                String studentLastName1 = Objects.nonNull(studentInfoDetails1.getLastName()) ? ModelConstants.SPACE + studentInfoDetails1.getLastName() : ModelConstants.EMPTY_STRING;
                String studentLastName2 = Objects.nonNull(studentInfoDetails2.getLastName()) ? ModelConstants.SPACE + studentInfoDetails2.getLastName() : ModelConstants.EMPTY_STRING;
                String studentName1 = studentInfoDetails1.getFirstName() + studentLastName1;
                String studentName2 = studentInfoDetails2.getFirstName() + studentLastName2;

                String hostelName1 = Optional.ofNullable(hostelFloorMasterService.getFloorDetailsById(swapedStudent1.getBuildingId()))
                        .map(HostelFloorMasterDto::getHostel)
                        .map(HostelMasterDto::getHostelName)
                        .orElse(Strings.EMPTY);

                String hostelName2 = Optional.ofNullable(hostelFloorMasterService.getFloorDetailsById(swapedStudent2.getBuildingId()))
                        .map(HostelFloorMasterDto::getHostel)
                        .map(HostelMasterDto::getHostelName)
                        .orElse(Strings.EMPTY);

                String roomNo1 = Optional.ofNullable(hostelRoomInfoService.getHostelRoomInfoDetailsById(Math.toIntExact(swapedStudent1.getRoomId())))
                        .map(HostelRoomInfoDto::getRoomNo)
                        .orElse(Strings.EMPTY);

                String roomNo2 = Optional.ofNullable(hostelRoomInfoService.getHostelRoomInfoDetailsById(Math.toIntExact(swapedStudent2.getRoomId())))
                        .map(HostelRoomInfoDto::getRoomNo)
                        .orElse(Strings.EMPTY);

                String studentId1 = studentInfoDetails1.getStudentId();
                String studentId2 = studentInfoDetails2.getStudentId();
                String to = studentInfoDetails1.getEmailId() + ModelConstants.COMMA + studentInfoDetails2.getEmailId();
                String bcc = allStudentsDetailsViewRepository
                        .getWardenAndFacultyDetails(List.of(studentId1, studentId2), ModelConstants.STATUS_ACTIVE).stream()
                        .flatMap(r -> Stream.of(r.wardenEmail(), r.facultyEmail()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.collectingAndThen(
                                Collectors.toSet(),
                                set -> String.join(ModelConstants.COMMA, set)
                        ));


                String message = mailTemplate.getMailTemplate()
                        .replace("#%studentName1%#", studentName1)
                        .replace("#%studentId1%#", studentId1)
                        .replace("#%hostelName1%#", hostelName1)
                        .replace("#%roomNo1%#", roomNo1)
                        .replace("#%studentName2%#", studentName2)
                        .replace("#%studentId2%#", studentId2)
                        .replace("#%hostelName2%#", hostelName2)
                        .replace("#%roomNo2%#", roomNo2);
                try {
                    mailQueueService.saveMailQueue(mailTemplate.getMailSubject(), "Students", message, to, "Hostel GUI", null,
                            null, null, null, ModelConstants.REGARDS, ModelConstants.CCW_OFFICE,bcc);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private HostelRoomAllotmentInfoDto processStudentAllotment(HostelRoomAllotmentInfoEntity
                                                                       student, HostelRoomAllotmentInfoDto hostelRoomAllotmentInfoDto) {
        HostelRoomAllotmentInfoDto dto;
        if (LocalDate.now().isEqual(student.getStayFromDate())) {
            dto = updateStudentRoomAllotmentDetails(student, hostelRoomAllotmentInfoDto);
        } else {
            dto = updateAndInsertStudentRoomAllotmentDetails(student, hostelRoomAllotmentInfoDto);
        }

        return dto;
    }


    public HostelRoomAllotmentInfoDto updateStudentRoomAllotmentDetails(HostelRoomAllotmentInfoEntity
                                                                                student, HostelRoomAllotmentInfoDto swapWith) {
        if (Objects.nonNull(student)) {
            student.setBuildingId(swapWith.getBuildingId());
            student.setRoomId(swapWith.getRoomId());
            student.setSubRoomId(swapWith.getSubRoomId());
            student.setIsMissing(false);
            return Optional.of(hostelRoomAllotmentRepository.save(student))
                    .map(HostelRoomAllotmentInfoMapper.INSTANCE::fromHostelRoomAllotmentInfoEntity)
                    .orElse(null);
        } else {
            return null;
        }
    }

    public HostelRoomAllotmentInfoDto updateAndInsertStudentRoomAllotmentDetails(HostelRoomAllotmentInfoEntity
                                                                                         student, HostelRoomAllotmentInfoDto swapWith) {
        if (Objects.nonNull(student) && Objects.nonNull(swapWith)) {
            HostelRoomAllotmentInfoEntity newEntity = new HostelRoomAllotmentInfoEntity();
            newEntity.setStudentId(student.getStudentId());
            newEntity.setBuildingId(swapWith.getBuildingId());
            newEntity.setRoomId(swapWith.getRoomId());
            newEntity.setSubRoomId(swapWith.getSubRoomId());
            newEntity.setStayFromDate(LocalDate.now());
            newEntity.setIsMissing(false);
            newEntity.onCreate();
            hostelRoomAllotmentRepository.save(newEntity);

            student.setNewRoomAllotmentId(newEntity.getRoomAllotmentId());
            student.setIsMissing(false);
            student.setShiftedDate(LocalDate.now().minusDays(1));
            hostelRoomAllotmentRepository.save(student);


            return Optional.of(newEntity)
                    .map(HostelRoomAllotmentInfoMapper.INSTANCE::fromHostelRoomAllotmentInfoEntity)
                    .orElse(null);
        }

        return null;
    }

    @Transactional
    public boolean allocateStudentProcess(StudentHostelInfoDetailsDto studentHotelInfoDetailsDto) {
        HostelRoomAllotmentInfoDto statusDto = null;
        VacationHostelRoomAllotmentInfoDto vacationHostelRoomAllotmentInfoDto = null;
        switch (studentHotelInfoDetailsDto.getScreenType().toLowerCase(Locale.ROOT)) {
            case "allocate":
                studentHotelInfoDetailsDto.setIsMissing(false);
                statusDto = allocateStudent(studentHotelInfoDetailsDto);
                break;
            case "change":
                studentHotelInfoDetailsDto.setIsMissing(false);
                statusDto = allocateChangeHostel(studentHotelInfoDetailsDto);
                break;
            case "delete":
                if (isScreenStudent(studentHotelInfoDetailsDto.getStudentType())){
                    StudentHostelInfoDetailsDto existingStudDetails = getStudentRoomDetails(studentHotelInfoDetailsDto.getCurrentStudentId());
                    existingStudDetails.setCurrentStudentId(studentHotelInfoDetailsDto.getCurrentStudentId());
//                    existingStudDetails.setIsMissing(true);
                    existingStudDetails.setScreenType(studentHotelInfoDetailsDto.getScreenType().toLowerCase(Locale.ROOT));
                    var currentStudentDetails = hostelRoomAllotmentRepository.findByStudentIdEqualsIgnoreCaseAndActiveFlagAndVacateDateIsNullAndShiftedDateIsNull(
                                    existingStudDetails.getCurrentStudentId(), ModelConstants.STATUS_ACTIVE)
                            .orElse(null);
                    statusDto = reAllocateStudent(existingStudDetails, currentStudentDetails, false);
                } else {
                    vacationHostelRoomAllotmentInfoDto = reAllocateVacationStudent(studentHotelInfoDetailsDto);
                }
                break;
            default:
                studentHotelInfoDetailsDto.setIsMissing(false);
                var currentStudentDetails = hostelRoomAllotmentRepository.findByStudentIdEqualsIgnoreCaseAndActiveFlagAndVacateDateIsNullAndShiftedDateIsNull(
                                studentHotelInfoDetailsDto.getCurrentStudentId(), ModelConstants.STATUS_ACTIVE)
                        .orElse(null);
                statusDto = reAllocateStudent(studentHotelInfoDetailsDto, currentStudentDetails, true);
                break;
        }
        return Objects.nonNull(statusDto) || Objects.nonNull(vacationHostelRoomAllotmentInfoDto);
    }

    private VacationHostelRoomAllotmentInfoDto reAllocateVacationStudent(StudentHostelInfoDetailsDto studentHotelInfoDetailsDto) {
        var vacationHostelRoomAllotmentInfoEntity = vacationHostelRoomAllotmentInfoRepository
                .findByIdAndActiveFlag(studentHotelInfoDetailsDto.getRoomAllotmentId(), ModelConstants.STATUS_ACTIVE)
                .orElse(null);
        if (Objects.nonNull(vacationHostelRoomAllotmentInfoEntity)){
            vacationHostelRoomAllotmentInfoEntity.setStayToDate(LocalDate.now().minusDays(1));
            vacationHostelRoomAllotmentInfoRepository.save(vacationHostelRoomAllotmentInfoEntity);
            return Optional.of(vacationHostelRoomAllotmentInfoEntity)
                    .map(VacationHostelRoomAllotmentInfoMapper.INSTANCE::toDto)
                    .orElse(null);
        }
        return null;
    }

    private HostelRoomAllotmentInfoDto allocateChangeHostel(StudentHostelInfoDetailsDto studentHostelInfo) {
        Optional<StudentHostelRoomVacatingRequestViewEntity> oStudentVacatingForm = studentHostelRoomVacatingRequestViewRepository
                .checkStudentApprovalVacatingForm(studentHostelInfo.getCurrentStudentId(),
                        ModelConstants.STATUS_ACTIVE, WorkflowStatus.APPROVED.getStatus(), BulkAllotmentStudentsConstants.COURSE_COMPLETED.getString());
        if (oStudentVacatingForm.isPresent()) {
            HostelRoomAllotmentInfoDto hostelRoomAllotmentInfoDto = new HostelRoomAllotmentInfoDto();
            hostelRoomAllotmentInfoDto.setStatus(HostelIndividualAllotmentEnum.VACATING_FORM_APPROVED.getValue());
            hostelRoomAllotmentInfoDto.setErrorMessage(commonResponseUtil.getMessage("message.error.vacating.form.approved"));
            return hostelRoomAllotmentInfoDto;
        } else {
            StudentHostelInfoDetailsDto currentStudDetails = getStudentRoomDetails(studentHostelInfo.getCurrentStudentId());
            studentHostelInfo.setStayFromDate(currentStudDetails.getStayFromDate());
            studentHostelInfo.setStayToDate(LocalDate.now().plusYears(1));
            List<Object[]> hostelRoomInfoDto = getHostelOccupancy(studentHostelInfo.getHostelId(),
                    studentHostelInfo.getRoomId(), studentHostelInfo.getSubRoomId(), studentHostelInfo.getStayFromDate(),
                    studentHostelInfo.getStayToDate()).orElseGet(ArrayList::new);
            if (!hostelRoomInfoDto.isEmpty()) {
                HostelRoomAllotmentInfoDto hostelRoomAllotmentInfoDto = mapRoomOccupancyToHostelRoomInfoDto(hostelRoomInfoDto.getFirst());
                if (hostelRoomAllotmentInfoDto.getRoomAllotmentId() > 0) {
                    if ("Student".equals(hostelRoomAllotmentInfoDto.getStudentType())) {
                        StudentHostelInfoDetailsDto existingStudDetails = getStudentRoomDetails(hostelRoomAllotmentInfoDto.getStudentId());
                        existingStudDetails.setCurrentStudentId(hostelRoomAllotmentInfoDto.getStudentId());
                        existingStudDetails.setFloorId(hostelRoomAllotmentInfoDto.getFloorId());
                        var currentStudentDetails = hostelRoomAllotmentRepository.findByStudentIdEqualsIgnoreCaseAndActiveFlagAndVacateDateIsNullAndShiftedDateIsNull(
                                        existingStudDetails.getCurrentStudentId(), ModelConstants.STATUS_ACTIVE)
                                .orElse(null);
                        reAllocateStudent(existingStudDetails, currentStudentDetails, false);
                    }
                }
            }
            var currentStudentDetails = hostelRoomAllotmentRepository.findByStudentIdEqualsIgnoreCaseAndActiveFlagAndVacateDateIsNullAndShiftedDateIsNull(
                            studentHostelInfo.getCurrentStudentId(), ModelConstants.STATUS_ACTIVE)
                    .orElse(null);
            return reAllocateStudent(studentHostelInfo, currentStudentDetails, true);
        }
    }

    public HostelRoomAllotmentInfoDto allocateStudent(StudentHostelInfoDetailsDto studentHotelInfoDetailsDto) {
        HostelRoomAllotmentInfoEntity newEntity = new HostelRoomAllotmentInfoEntity();
        newEntity.setStudentId(studentHotelInfoDetailsDto.getCurrentStudentId());
        newEntity.setBuildingId(studentHotelInfoDetailsDto.getFloorId());
        newEntity.setRoomId(studentHotelInfoDetailsDto.getRoomId());
        newEntity.setSubRoomId(studentHotelInfoDetailsDto.getSubRoomId());
        newEntity.setIsMissing(studentHotelInfoDetailsDto.getIsMissing());
        newEntity.setStayFromDate(LocalDate.now());
        newEntity.onCreate();
        return Optional.of(hostelRoomAllotmentRepository.save(newEntity))
                .map(HostelRoomAllotmentInfoMapper.INSTANCE::fromHostelRoomAllotmentInfoEntity)
                .orElse(null);
    }

    public HostelRoomAllotmentInfoDto reAllocateStudent(StudentHostelInfoDetailsDto studentHotelInfoDetailsDto, HostelRoomAllotmentInfoEntity currentStudentDetails,
                                                        boolean reallocate) {
        if (Objects.nonNull(currentStudentDetails)) {
            if (Objects.nonNull(studentHotelInfoDetailsDto.getStayFromDate()) &&
                    LocalDate.now().isEqual(studentHotelInfoDetailsDto.getStayFromDate()) &&
                    !"delete".equalsIgnoreCase(studentHotelInfoDetailsDto.getScreenType())) {
                currentStudentDetails.setBuildingId(studentHotelInfoDetailsDto.getFloorId());
                currentStudentDetails.setRoomId(studentHotelInfoDetailsDto.getRoomId());
                currentStudentDetails.setSubRoomId(studentHotelInfoDetailsDto.getSubRoomId());
                return Optional.of(hostelRoomAllotmentRepository.save(currentStudentDetails))
                        .map(HostelRoomAllotmentInfoMapper.INSTANCE::fromHostelRoomAllotmentInfoEntity)
                        .orElse(null);
            } else {
                currentStudentDetails.setShiftedDate(LocalDate.now().minusDays(1));
                currentStudentDetails.setIsMissing(studentHotelInfoDetailsDto.getIsMissing());
                hostelRoomAllotmentRepository.save(currentStudentDetails);
                return Optional.of(!reallocate ? currentStudentDetails : insertHostelRoomAllotment(studentHotelInfoDetailsDto))
                        .map(HostelRoomAllotmentInfoMapper.INSTANCE::fromHostelRoomAllotmentInfoEntity)
                        .orElse(null);
            }
        }
        return null;
    }

    private HostelRoomAllotmentInfoEntity insertHostelRoomAllotment(StudentHostelInfoDetailsDto
                                                                            studentHotelInfoDetailsDto) {
        HostelRoomAllotmentInfoEntity newEntity = new HostelRoomAllotmentInfoEntity();
        newEntity.setStudentId(Strings.isBlank(studentHotelInfoDetailsDto.getChangedStudentId()) ? studentHotelInfoDetailsDto.getCurrentStudentId() : studentHotelInfoDetailsDto.getChangedStudentId());
        newEntity.setBuildingId(studentHotelInfoDetailsDto.getFloorId());
        newEntity.setRoomId(studentHotelInfoDetailsDto.getRoomId());
        newEntity.setSubRoomId(studentHotelInfoDetailsDto.getSubRoomId());
        newEntity.setIsMissing(studentHotelInfoDetailsDto.getIsMissing());
        newEntity.setStayFromDate(LocalDate.now());
        newEntity.onCreate();
        return hostelRoomAllotmentRepository.save(newEntity);
    }

    public HostelRoomInfoDto checkForHostelChange(StudentHostelInfoDetailsDto studentHostelInfoDetailsDto) {
        studentHostelInfoDetailsDto.setStayFromDate(LocalDate.now());
        studentHostelInfoDetailsDto.setStayToDate(LocalDate.now().plusYears(1));
        return hostelRoomInfoRepository.getRoomOccupancyDetails(
                        studentHostelInfoDetailsDto.getNewRoomId(), studentHostelInfoDetailsDto.getNewSubRoomId(),
                        studentHostelInfoDetailsDto.getStayFromDate(), ModelConstants.STATUS_ACTIVE)
                .orElseGet(ArrayList::new)
                .stream().findFirst()
                .map(data -> mapOccupantDetailsBySubRoom(data, studentHostelInfoDetailsDto.getStayFromDate(), 0, null))
                .orElseGet(HostelRoomInfoDto::new)
                ;
    }


}
