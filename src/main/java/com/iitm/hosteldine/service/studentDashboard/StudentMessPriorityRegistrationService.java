package com.iitm.hosteldine.service.studentDashboard;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.mess.MessMasterControllerDto;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.dto.mess.MessRegistrationMappingDto;
import com.iitm.hosteldine.dto.studentDashboard.StudentExcessMessDetailsDto;
import com.iitm.hosteldine.dto.studentDashboard.StudentMessPriorityRegistrationDto;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.mapper.mess.MessMasterMapper;
import com.iitm.hosteldine.model.mess.MessMasterEntity;
import com.iitm.hosteldine.model.mess.StudentExcessMessDetailsEntity;
import com.iitm.hosteldine.model.mess.StudentMessPriorityRegistrationEntity;
import com.iitm.hosteldine.model.mess.StudentMessPriorityRegistrationId;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import com.iitm.hosteldine.repository.studentDashboard.StudentExcessMessDetailsRepository;
import com.iitm.hosteldine.repository.studentDashboard.StudentMessPriorityRegistrationRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.dashboard.ShowActionService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.service.mess.MessMasterCommonService;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.service.mess.MessRegistrationMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentMessPriorityRegistrationService {
	private final MessMasterCommonService messMasterCommonService;
	private final MessRegistrationMappingService messRegistrationMappingService;
	private final StudentMessPriorityRegistrationRepository studentMessPriorityRegistrationRepository;
	private final MessMasterService messMasterService;
//	private final StudentDetailsInfoService studentDetailsInfoService;
	private final StudentMessLoginIssuePriorityService studentMessLoginIssuePriorityService;
	private final StudentExcessMessDetailsService studentExcessMessDetailsService;
	private final StudentExcessMessDetailsRepository studentExcessMessDetailsRepository;
	private final MessageSource messageSource;
	private final MailQueueService mailQueueService;
	private final MailTemplateRepository mailTemplateRepository;
	private final ShowActionService showActionService;
    private final SimsConfigDataService simsConfigDataService;
	private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;


    public List<StudentMessPriorityRegistrationDto> getMessRegistrationList(String studentIdInput) {
	    MessMasterControllerDto dto = null;
	    String girlsOption1 = "";
	    String girlsOption2 = "";
	    String boysOption1 = "";
	    Long currentId = null;
	    boolean list = false;
	    MessRegistrationMappingDto firstMapping = null;
		String studentId= (studentIdInput!=null && studentIdInput!="") ? studentIdInput : SecurityCtxUtil.userId().toUpperCase();

	    // List to hold the result
	    List<StudentMessPriorityRegistrationDto> messPriorityRegistrationList = new ArrayList<>();

		AllStudentsDetailsViewEntity studentDetail=new AllStudentsDetailsViewEntity();
		Optional<AllStudentsDetailsViewEntity> studentDetailOpt = allStudentsDetailsViewRepository.findBystudentId(studentId);
		if(studentDetailOpt.isPresent()) {
			 studentDetail = studentDetailOpt.get();
		}

	    // Registration mapping list
	    List<MessRegistrationMappingDto> messRegistrationMappingList = messRegistrationMappingService.getMappingList();
	    if (!messRegistrationMappingList.isEmpty()) {
	        firstMapping = messRegistrationMappingList.get(0);
	        girlsOption1 = firstMapping.getGirlsOptionOne().replaceAll(Constants.SYMBOL_PATTERN, ""); 
	        girlsOption2 = firstMapping.getGirlsOptionTwo().replaceAll(Constants.SYMBOL_PATTERN, ""); 
	        boysOption1 = firstMapping.getBoysOption().replaceAll(Constants.SYMBOL_PATTERN, ""); 
	    }
	    
	    // Previous mess period details
	    Optional<MessMasterControllerDto> messPeriodDetails = messMasterCommonService.getMessPeriodDetails();
	    if (messPeriodDetails.isPresent()) {
	        dto = messPeriodDetails.get();
	        currentId = dto.getId(); // Access the currentId
	    }
	    
	    // Get priority mess registration list
	    List<Object[]> resultList = studentMessPriorityRegistrationRepository
	            .getPriorityMessRegistrationList(SecurityCtxUtil.userId().toUpperCase(),
	                                             ModelConstants.STATUS_ACTIVE, currentId);
	    if (!resultList.isEmpty()) {
	        list = true;
	        for (Object[] row : resultList) {
	            StudentMessPriorityRegistrationEntity entity = (StudentMessPriorityRegistrationEntity) row[0];
	            StudentMessPriorityRegistrationDto messPriorityRegistrationDto = new StudentMessPriorityRegistrationDto();
	            messPriorityRegistrationDto.setMessId((Long) row[1]);
	            messPriorityRegistrationDto.setMessName((String) row[2]);
	            messPriorityRegistrationDto.setDescription((String) row[3]);
	            messPriorityRegistrationDto.setCapacity((Integer) row[4]);
	            messPriorityRegistrationDto.setMessPreference(entity.getMessPreference());
	            messPriorityRegistrationList.add(messPriorityRegistrationDto);
	        }
	    } else {
	        // Add dummy records as no registration has happened yet
	        messPriorityRegistrationList.add(new StudentMessPriorityRegistrationDto());
	        if (firstMapping != null && firstMapping.getGirlsOptionTwoEnable() && (Constants.FEMALE.equalsIgnoreCase(studentDetail.getGender()))) {
	            messPriorityRegistrationList.add(new StudentMessPriorityRegistrationDto());
	        }
	    }

	    if (firstMapping != null) {
	        List<Long> girlsOptionOneLong = Arrays.stream(girlsOption1.split(Constants.ARRAY_SEPARATOR))
	                                              .map(Long::valueOf)
	                                              .collect(Collectors.toList());
	        List<Long> girlsOptionTwoLong = Arrays.stream(girlsOption2.split(Constants.ARRAY_SEPARATOR))
	                                              .map(Long::valueOf)
	                                              .collect(Collectors.toList());
	        List<Long> boysOption1Long = Arrays.stream(boysOption1.split(Constants.ARRAY_SEPARATOR))
	                                           .map(Long::valueOf)
	                                           .collect(Collectors.toList());

	        // Get mess list based on gender
	        if (Constants.FEMALE.equalsIgnoreCase(studentDetail.getGender())) {
	           if (!firstMapping.getGirlsOptionTwoEnable()) {
                   List<MessMasterEntity> messNameListOne = messMasterService
                           .getPriorityMessMasterList(girlsOptionOneLong, ModelConstants.STATUS_ACTIVE);
                   addMessMasterEntitiesToList(messNameListOne, messPriorityRegistrationList.getFirst());
               } else {
                   List<MessMasterEntity> messNameListTwo = messMasterService
                           .getPriorityMessMasterList(girlsOptionTwoLong, ModelConstants.STATUS_ACTIVE);
//                   if (messPriorityRegistrationList.size() > 1) {
                       addMessMasterEntitiesToList(messNameListTwo, messPriorityRegistrationList.getFirst());
//                   }
               }
            } else if (Constants.MALE.equalsIgnoreCase(studentDetail.getGender())) {
	        	List<MessMasterEntity> messNameListBoys = messMasterService
	                    .getPriorityMessMasterList(boysOption1Long, ModelConstants.STATUS_ACTIVE);
	            addMessMasterEntitiesToList(messNameListBoys, messPriorityRegistrationList.get(0));
	        }
	    }

	    return messPriorityRegistrationList;
	}

	private void addMessMasterEntitiesToList(List<MessMasterEntity> messNameList,
	                                         StudentMessPriorityRegistrationDto priorityDto) {
	    if (messNameList != null && !messNameList.isEmpty()) {
	        if (priorityDto.getMessList() == null) {
	            priorityDto.setMessList(new ArrayList<>());
	        }
	        
	        for (MessMasterEntity messMaster : messNameList) {
	            if (messMaster != null) {
	                MessMasterDto dto = MessMasterMapper.INSTANCE.fromMessMasterEntity(messMaster);
	                if (dto.getId() != null && dto.getId().equals(priorityDto.getMessId())) {
	                    dto.setCheck(true);
	                }
	                priorityDto.getMessList().add(dto);
	            }
	        }
	    }
	}

	public String saveStudentMessPriorityRegistration(StudentMessPriorityRegistrationDto dto) throws Exception {
		String dinningFromDate = "", dinningToDate = "";
		String status = null ,month = "";
		Long previousId = null , currentId = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT); 
		
		//getting dining dates (for Mail purpose)
		List<MessMasterControllerDto> messControllerList = messMasterCommonService.getMessMasterControllerList();
		if(messControllerList != null && !messControllerList.isEmpty()) {
			MessMasterControllerDto messMasterControllerDto =messControllerList.get(0);
			dinningFromDate = messMasterControllerDto.getDiningFromDate().format(formatter);
			dinningToDate = messMasterControllerDto.getDiningToDate().format(formatter);;
		}
		
		// Getting current and previous Id from mess master controller
		Optional<MessMasterControllerDto> messPeriodDetails = messMasterCommonService.getMessPeriodDetails();
		if (messPeriodDetails.isPresent()) {
			MessMasterControllerDto messMasterControllerDto = messPeriodDetails.get();
			currentId =  messMasterControllerDto.getId();
		}
		StudentMessPriorityRegistrationEntity entity = new StudentMessPriorityRegistrationEntity();
		//to check already the student has registered for current period
		StudentMessPriorityRegistrationEntity messRegistrationEntity = studentMessPriorityRegistrationRepository.
				findByActiveFlagAndIdStudentIdAndIdMmcNId(ModelConstants.STATUS_ACTIVE,SecurityCtxUtil.userId().toUpperCase(),currentId.intValue());
		if(messRegistrationEntity != null) {
			// Reset active flag to 'N'
			messRegistrationEntity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
			studentMessPriorityRegistrationRepository.save(messRegistrationEntity);
			if(dto != null){
			// check updated registered student
				/*StudentMessPriorityRegistrationEntity entity = studentMessPriorityRegistrationRepository.
						findByActiveFlagAndId_StudentIdAndId_MmcNId(ModelConstants.STATUS_INACTIVE,
								SecurityCtxUtil.userId().toUpperCase(),currentId.intValue());*/

				if(messRegistrationEntity != null) {
					entity = new StudentMessPriorityRegistrationEntity();
					StudentMessPriorityRegistrationId messRegId = new StudentMessPriorityRegistrationId();
					messRegId.setPriorityMessId(dto.getId().getPriorityMessId());
					messRegId.setMmcNId(currentId.intValue());
					messRegId.setPriorityOrder(1);
					messRegId.setStudentId(SecurityCtxUtil.userId().toUpperCase());
					entity.setActiveFlag(ModelConstants.STATUS_ACTIVE);
					entity.setGroupId(0L);
					entity.setSemMon(Constants.MONTHLY);
					entity.setId(messRegId);
					entity.onCreate();
					entity = studentMessPriorityRegistrationRepository.save(entity);
					status = Constants.UPDATED;
				}
			}

		}else {
			entity = new StudentMessPriorityRegistrationEntity();
			entity.setActiveFlag(ModelConstants.STATUS_ACTIVE);
			StudentMessPriorityRegistrationId messRegId = new StudentMessPriorityRegistrationId();
			messRegId.setMmcNId(currentId.intValue());
			messRegId.setPriorityMessId(dto.getId().getPriorityMessId());
			messRegId.setPriorityOrder(1);
			messRegId.setStudentId(SecurityCtxUtil.userId().toUpperCase());
			entity.setId(messRegId);
			entity.setGroupId(0L);
			entity.setSemMon(Constants.MONTHLY);
			entity.onCreate();
			entity = studentMessPriorityRegistrationRepository.save(entity);
			status = Constants.SAVED;
		}
		if(entity != null) {
			List<Object[]> result = studentMessPriorityRegistrationRepository.getStudentMessPriorityList(currentId,SecurityCtxUtil.userId().toUpperCase(),ModelConstants.STATUS_ACTIVE);
			for (Object[] row : result) {
				StudentMessPriorityRegistrationDto messDto = new StudentMessPriorityRegistrationDto();
				messDto.setStudentName((String) row[0]);
				messDto.setParentEmailId((String) row[1]);
				messDto.setMessName((String) row[2]);

				Optional<MailTemplateEntity> templateOpt = mailTemplateRepository
						.findByMailType(MailTemplateEntity.PRIORITY_MESS_REGISTRATION);
				if (templateOpt.isPresent()) {
					MailTemplateEntity template = templateOpt.get();
					String subject = template.getMailSubject();
					String content = template.getMailTemplate();
					DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_TIME_FORMAT);
					String currentDateTime = LocalDateTime.now().format(dateFormatter);
					content = content.replaceAll("#%dinning_from%#", dinningFromDate);
					content = content.replaceAll("#%dinning_to%#", dinningToDate);
					content = content.replaceAll("#%student_id%#", SecurityCtxUtil.userId().toUpperCase());
					content = content.replaceAll("#%student_name%#", messDto.getStudentName()!= null ? messDto.getStudentName() :ModelConstants.HYPHEN);
					content = content.replaceAll("#%mess_name%#", messDto.getMessName() != null ? messDto.getMessName() : ModelConstants.HYPHEN);
					content = content.replaceAll("#%register_date%#", currentDateTime);
					String toEmail = messDto.getParentEmailId() != null ? messDto.getParentEmailId() : ModelConstants.EMPTY_STRING;

					boolean mailStatus = mailQueueService.saveMailQueue(subject,
							messDto.getStudentName() +" ("+SecurityCtxUtil.userName()+")", content,
							toEmail , Constants.STUDENT_MESS_PRIORITY,
							SecurityCtxUtil.userId(), 1, null, null, null, null);
				}

			}
		}
			

		return status;
}

	public String checkValidations(StudentMessPriorityRegistrationDto dto,String regType) {
		String status = null, gender = "";
		Long currentId = null;
		int capacity = 0;
		int thresholdCount = 0;
		String pushTime = null;
		LocalDate pushDate = null;
		boolean insertStatus = true;
		String studentId= (regType!=null && regType!= "")? dto.getId().getStudentId().toUpperCase(): SecurityCtxUtil.userId().toUpperCase();

		//Check student has negative balance
        boolean balCheck=Boolean.valueOf(simsConfigDataService.getSimConfigValue(SimsConfigDataService.PRIORITY_MESS_BAL_CHECK));
		boolean student_bal = showActionService.checkStudentBalanceForMessReg(studentId);
		if ((balCheck && student_bal) || (!balCheck)) {
			// checking the current date is for Registration or not.
			List<Object[]> dates = messMasterCommonService.getRegistrationDate();
			if (!dates.isEmpty()) {
				// Getting current Id from mess master controller
				Optional<MessMasterControllerDto> messPeriodDetails = messMasterCommonService.getMessPeriodDetails();
				if (messPeriodDetails.isPresent()) {
					MessMasterControllerDto messMasterControllerDto = messPeriodDetails.get();
					currentId =  messMasterControllerDto.getId();
				}
				//checkAlreadyRegisteredStudent (using login issue)
				String regStatus = "";
				regStatus = studentMessLoginIssuePriorityService.
						checkStudentRegistration(ModelConstants.STATUS_ACTIVE, studentId, currentId.intValue());
				if (regStatus != null
						&& !regStatus.equalsIgnoreCase(messageSource.getMessage("message.register.already", null, Locale.getDefault()))) {
					// Get gender details
					AllStudentsDetailsViewEntity studentDetail=new AllStudentsDetailsViewEntity();
					Optional<AllStudentsDetailsViewEntity> studentDetailOpt = allStudentsDetailsViewRepository.findBystudentId(studentId);
					if(studentDetailOpt.isPresent()) {
						studentDetail = studentDetailOpt.get();
						gender = studentDetail.getGender();
					}

					// Getting mess period config details
					List<MessMasterControllerDto> messControllerList = messMasterCommonService.getMessMasterControllerList();
					if (messControllerList != null && !messControllerList.isEmpty()) {
						MessMasterControllerDto messMasterControllerDto = messControllerList.get(0);
						currentId = messMasterControllerDto.getId();
						pushTime = messMasterControllerDto.getPushingTime();
						pushDate = messMasterControllerDto.getPushingDate();
						StudentMessPriorityRegistrationEntity messRegistrationEntity = new StudentMessPriorityRegistrationEntity();
						if (regType != null && !regType.isEmpty() && regType.equalsIgnoreCase(Constants.LOGIN_ISSUE)) {
							// Check the student already registered by login
							messRegistrationEntity = studentMessPriorityRegistrationRepository
									.findByActiveFlagAndIdStudentIdAndIdMmcNId(ModelConstants.STATUS_ACTIVE,
											studentId, currentId.intValue());
						} else{
							// Check the student already registered for current period and same mess
							messRegistrationEntity = studentMessPriorityRegistrationRepository
									.findByActiveFlagAndIdStudentIdAndIdMmcNIdAndIdPriorityMessId(ModelConstants.STATUS_ACTIVE,
											studentId, currentId.intValue(), dto.getId().getPriorityMessId());
						}
						if (messRegistrationEntity == null) {

							// Get mess capacity;
							MessMasterDto messMasterDto = messMasterService.getMessMasterDetailsById(dto.getId().getPriorityMessId());
							if (messMasterDto != null) {
								capacity = messMasterDto.getCapacity();
								thresholdCount = messMasterDto.getGirlsThresholdCount();
							}

							// Get mess count
							int appliedCount = studentMessPriorityRegistrationRepository.getMessCount(dto.getId().getPriorityMessId(),
									ModelConstants.STATUS_ACTIVE);
//							if (messCount != 0) {
								if (appliedCount >= capacity) {
									status = messageSource.getMessage("message.mess.capacity.exceeds", null, Locale.getDefault());
									if (insertStatus) {
										StudentExcessMessDetailsDto excessDto = studentExcessMessDetailsService.getExcessMessDetails(
												dto.getId().getPriorityMessId(), studentId,
												currentId.intValue());
										if (excessDto == null) {
											StudentExcessMessDetailsEntity studentExcessMessDetailsEntity = new StudentExcessMessDetailsEntity();
											MessMasterEntity messMasterEntity = new MessMasterEntity();
											StudentDetailsInfoEntity studentDetailsInfoEntity = new StudentDetailsInfoEntity();
											Long messId = (long) dto.getId().getPriorityMessId();
											studentExcessMessDetailsEntity.setActiveFlag(ModelConstants.STATUS_ACTIVE);
											studentExcessMessDetailsEntity.setMmcId(currentId.intValue());
											studentDetailsInfoEntity.setStudentId(studentId);
											messMasterEntity.setId(messId);
											studentExcessMessDetailsEntity.setMessMaster(messMasterEntity);
											studentExcessMessDetailsEntity.setStudentDetailsInfo(studentDetailsInfoEntity);
											studentExcessMessDetailsEntity.onCreate();
											studentExcessMessDetailsRepository.save(studentExcessMessDetailsEntity);
										}
									}
								} else {
									//Convert pushDate and pushTime to timestamp
									// Parse pushtime as LocalTime
									DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
									LocalTime localTime = LocalTime.parse(pushTime, timeFormatter);
									// Combine date and time
									LocalDateTime boysOpeningTime = LocalDateTime.of(pushDate, localTime);
									// Get current time
									LocalDateTime currentTime = LocalDateTime.now();

									if (gender.equalsIgnoreCase(Constants.MALE) && thresholdCount > 0 && currentTime.isBefore(boysOpeningTime)) {
										status = messageSource.getMessage("message.first.preference.girls", null, Locale.getDefault());
									}
								}
//							}
							/*else {
								int girlsRegisteredCount = studentMessPriorityRegistrationRepository
										.getGirlsRegCount(dto.getId().getPriorityMessId(), ModelConstants.STATUS_ACTIVE);
								if (girlsRegisteredCount != 0) {
									appliedCount = girlsRegisteredCount;
									if (thresholdCount != 0 && appliedCount < thresholdCount && gender.equals(Constants.MALE)) {
										status = messageSource.getMessage("message.first.preference.girls", null, Locale.getDefault());
									}
								}
							}*/

						} else {
							if (regType != null && !regType.isEmpty() && regType.equalsIgnoreCase(Constants.LOGIN_ISSUE)) {
								status = messageSource.getMessage("message.registered.student.dashboard", null, Locale.getDefault());
							}else{
								status = messageSource.getMessage("message.registered.same.mess", null, Locale.getDefault());
							}

						}
					}
				} else {
					return messageSource.getMessage("message.already.registered", null, Locale.getDefault());
				}
			} else {
				return messageSource.getMessage("message.registerd.date.invalid", null, Locale.getDefault());
			}
		} else {
			status = messageSource.getMessage("message.negative.balance", null, Locale.getDefault());
		}
		return status;

	}

public MessMasterControllerDto getPreviousMessDetails() {
	MessMasterControllerDto dto = null;
	Optional<MessMasterControllerDto> messPeriodDetails = messMasterCommonService.getMessPeriodDetails();
	dto = messPeriodDetails.get();
	MessMasterControllerDto studentMessDetails = messMasterCommonService.getStudentMessDetails(dto.getPreviousId(),
			SecurityCtxUtil.userId().toUpperCase());
	return studentMessDetails;
}
}

	


