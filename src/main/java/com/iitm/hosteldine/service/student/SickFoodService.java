package com.iitm.hosteldine.service.student;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.mess.MessMasterDto;
import com.iitm.hosteldine.dto.mess.MessVendorMasterDto;
import com.iitm.hosteldine.dto.student.SickFoodDeliveryStatusDto;
import com.iitm.hosteldine.dto.student.SickFoodRequestDto;
import com.iitm.hosteldine.entity.mailQueue.MailTemplateEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.mess.MessVendorMasterMapper;
import com.iitm.hosteldine.mapper.student.SickFoodDeliveryStatusMapper;
import com.iitm.hosteldine.mapper.student.SickFoodRequestMapper;
import com.iitm.hosteldine.model.mess.MessSessionEntity;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.model.student.SickFoodDeliveryStatusEntity;
import com.iitm.hosteldine.model.student.SickFoodRequestEntity;
import com.iitm.hosteldine.repository.mailQueue.MailQueueDetailsRepository;
import com.iitm.hosteldine.repository.mailQueue.MailTemplateRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.repository.mess.MessSessionRepository;
import com.iitm.hosteldine.repository.mess.MessVendorMasterRepository;
import com.iitm.hosteldine.repository.student.AllStudentsDetailsViewRepository;
import com.iitm.hosteldine.repository.student.SickFoodDeliveryStatusRepository;
import com.iitm.hosteldine.repository.student.SickFoodRequestRepository;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.service.mess.MessMasterService;
import com.iitm.hosteldine.util.CommonEnum;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SickFoodService {
	private final SickFoodRequestRepository sickFoodRequestRepository;
	private final SickFoodDeliveryStatusRepository sickFoodDeliveryStatusRepository;
	private final MessMasterRepository messMasterRepository;
	private final FileService fileService;
	private final MessSessionRepository messSessionRepository;
	private final MailQueueDetailsRepository mailQueueRep;
	private final MailQueueService mailQueueService;
	private final MailTemplateRepository mailTemplateRepository;
	private final MessageSource messageSource;
	private final SimsConfigDataService simsConfigDataService;
	private final SickFoodDeliveryStatusService sickFoodDeliveryStatusService;
	private final MessMasterService messMasterService;
	private final MessVendorMasterRepository messVendorMasterRepository;
    private final CommonResponseUtil commonResponseUtil;
	private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;


    public Page<SickFoodRequestDto> getSickFoodList(PaginationForm form, String studentId) {
	    int page = form.getPage() - 1; 
	    Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("a.requestDate").descending());
	    
	    Page<SickFoodRequestEntity> result;
	  
	    if (form.getSearch() == null || form.getSearch().isEmpty()) {
	        result = sickFoodRequestRepository.getActiveFlagAndStudentId(
	            ModelConstants.STATUS_ACTIVE, studentId, pageable);
	    } else {
	        result = sickFoodRequestRepository.getMedicalReasonOrMessTypeAndActiveFlag(
	            ModelConstants.STATUS_ACTIVE, studentId, form.getSearch(), pageable);
	    }

		Page<SickFoodRequestDto> map = result.map(entity -> {
			SickFoodRequestDto dto = SickFoodRequestMapper.INSTANCE.fromSickFoodRequestEntity(entity);
			List<SickFoodDeliveryStatusDto> deliveryStatus = sickFoodDeliveryStatusService.getSickFoodDeliveryStatuses(dto.getId());
			List<String> order = List.of(CommonEnum.BF.toString(), CommonEnum.LC.toString(), CommonEnum.DR.toString());

			Map<String, SickFoodDeliveryStatusDto> sessionMap = deliveryStatus
					.stream()
					.filter(dto1 -> order.contains(dto1.getMessSession()))
					.collect(Collectors.toMap(SickFoodDeliveryStatusDto::getMessSession, dto1 -> dto1));

			// Create the sorted list, ensuring all three sessions are present
			List<SickFoodDeliveryStatusDto> messSessionStatus = order.stream()
					.map(session -> {
						SickFoodDeliveryStatusDto s = sessionMap.getOrDefault(session, createNewSickFoodDeliveryStatusDto(session));
						if (s.getCatererStatus() == null) {
							s.setCatererStatus("");
						}
						try {
							if (s.getSickFoodRequest() != null && s.getSickFoodRequest().getMessId() != null && s.getSickFoodRequest().getId() != null) {
								sickFoodDeliveryStatusService.getPendingDeliveries(s);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return s;
					})
					//.filter(status->status.getId()!=null && status.getId()>0)
					.toList();


			dto.setMessSessionStatus(messSessionStatus);

			return dto;
		});

		return map;
	}




	@Transactional
	public String saveOrUpdateSickFood(SickFoodRequestDto sickFoodRequestDto) throws Exception {
		 String studentId = SecurityCtxUtil.userId().toUpperCase();
		Long messId = getSickFoodAvailMessId();
		String fileUpload = null;
	    long currentMillis = System.currentTimeMillis();
	    String fileExtension = "";
	    AtomicReference<String> messSessionFullForm = new AtomicReference<>(ModelConstants.EMPTY_STRING);
        List<String> sessionNames = new ArrayList<>();

		if (checkSickFoodRequestExists(studentId, sickFoodRequestDto.getRequestDate())) {
			throw new IllegalArgumentException("You have already requested food for the same day.");
		}
        if (sickFoodRequestDto.getUploadedFileName() != null && !sickFoodRequestDto.getUploadedFileName().isEmpty()) {
		 	    fileExtension = sickFoodRequestDto.getUploadedFileName().getOriginalFilename().substring(sickFoodRequestDto.getUploadedFileName().getOriginalFilename().lastIndexOf("."));
		        fileUpload =   studentId + ModelConstants.UNDERSCORE + ModelConstants.SICK_FOOD_REQUEST_MEDICAL_FILES_PATH + ModelConstants.UNDERSCORE + currentMillis + fileExtension;
		        sickFoodRequestDto.setMedicalProofDoc(fileUpload);
		}
		String result = Optional.ofNullable(sickFoodRequestDto.getId()).filter(id -> (id != null && id > 0))
				.flatMap(id -> sickFoodRequestRepository.findByIdAndActiveFlag(sickFoodRequestDto.getId(),
						ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					if (sickFoodRequestDto.getUploadedFileName() == null
							|| sickFoodRequestDto.getUploadedFileName().isEmpty()) {
						sickFoodRequestDto.setMedicalProofDoc(existingEntity.getMedicalProofDoc());
					}
					SickFoodRequestMapper.INSTANCE.onUpdateEntity(existingEntity, sickFoodRequestDto);
					existingEntity.setMessId(messId.intValue());
					existingEntity.setStudentId(studentId);
					sickFoodRequestRepository.save(existingEntity);
					//update secondary
					List<SickFoodDeliveryStatusEntity> updatedDeliveryStatuses = sickFoodRequestDto.getMessSessionStatus().stream()
	                        .filter(messSession -> messSession.getId()!=null)
	                        .map(messSession -> {
	                            SickFoodDeliveryStatusEntity deliveryStatusEntity = sickFoodDeliveryStatusRepository
	        	                        .findByIdAndSickFoodRequestIdAndActiveFlag(messSession.getId(),existingEntity.getId(),ModelConstants.STATUS_ACTIVE)
	        	                        .orElse(new SickFoodDeliveryStatusEntity());
                                  if(deliveryStatusEntity.getId()!=null) {
      	                            if(messSession.getMessSession()==null) {
      	                            	deliveryStatusEntity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
      	                            }
      	                            else {
      	                            	deliveryStatusEntity.setMessSession(messSession.getMessSession());
           	                            deliveryStatusEntity.setCatererStatus(WorkflowStatus.PENDING.getStatus());
           	                            deliveryStatusEntity.setStudentDeliveryStatus(sickFoodRequestDto.getStudentDeliveryStatus());
      	                            }
                                  }
                                  if(messSession.getId()==0 && messSession.getMessSession()!=null) {;
                                	  deliveryStatusEntity.setSickFoodRequest(existingEntity);
                                	  deliveryStatusEntity.setMessSession(messSession.getMessSession());
                                	  deliveryStatusEntity.setCatererStatus(WorkflowStatus.PENDING.getStatus());
                                	  deliveryStatusEntity.setStudentDeliveryStatus(sickFoodRequestDto.getStudentDeliveryStatus());
									  deliveryStatusEntity.setId(null);
                                  }
	                          
	                            return deliveryStatusEntity;
	                        }).filter(deliveryStatusEntity->deliveryStatusEntity.getMessSession()!=null)
	                        .toList();
					// threshold check second table
					LocalDate today = LocalDate.now();
					SickFoodRequestDto dto = new SickFoodRequestDto();
					if (sickFoodRequestDto.getRequestDate().equals(today)) {
						try {
							dto = checkAvailableSessionWithThresholdTime(sickFoodRequestDto);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// Validation based on sessions and thresholds
					for (SickFoodDeliveryStatusDto session : sickFoodRequestDto.getMessSessionStatus()) {
						String messSession = session.getMessSession();
						LocalDate requestDate = sickFoodRequestDto.getRequestDate();
						if (CommonEnum.BF.toString().equals(messSession)) {
							sessionNames.add(commonResponseUtil.getMessage("message.label.breakfast"));
						} else if (CommonEnum.LC.toString().equals(messSession)) {
							sessionNames.add(commonResponseUtil.getMessage("message.label.lunch"));
						} else if (CommonEnum.DR.toString().equals(messSession)) {
							sessionNames.add(commonResponseUtil.getMessage("message.label.dinner"));
						}
						messSessionFullForm.set(String.join(ModelConstants.COMMA + ModelConstants.SPACE, sessionNames));
						// If the request date is today, enforce session time thresholds
						if (requestDate.equals(today)) {
							if (CommonEnum.BF.toString().equals(messSession) && !session.isBfThreshold()) {
								throw new IllegalArgumentException(messageSource.getMessage("message.request.mess.time.bf.closed", null, Locale.getDefault()));
							}
							if (CommonEnum.LC.toString().equals(messSession) && !session.isLcThreshold()) {
								throw new IllegalArgumentException(messageSource.getMessage("message.request.mess.time.lc.closed", null, Locale.getDefault()));
							}
							if (CommonEnum.DR.toString().equals(messSession) && !session.isDnThreshold()) {
								throw new IllegalArgumentException(messageSource.getMessage("message.request.mess.time.dr.closed", null, Locale.getDefault()));
							}
						}
					}
	                // Save all updated or newly created records
	                sickFoodDeliveryStatusRepository.saveAll(updatedDeliveryStatuses);
					return Constants.UPDATED;

				}).orElseGet(() -> {
					// Save the primary table entity
					SickFoodRequestEntity newEntity = SickFoodRequestMapper.INSTANCE.onSaveEntity(sickFoodRequestDto);
					newEntity.onCreate();
					newEntity.setStudentId(studentId);
					newEntity.setMessId(messId.intValue());
					sickFoodRequestRepository.save(newEntity);
					// threshhold check second table
					LocalDate today = LocalDate.now();
					SickFoodRequestDto dto = new SickFoodRequestDto();
					if (sickFoodRequestDto.getRequestDate().equals(today)) {
						try {
							dto = checkAvailableSessionWithThresholdTime(sickFoodRequestDto);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					// Validation based on sessions and thresholds
					for (SickFoodDeliveryStatusDto session : sickFoodRequestDto.getMessSessionStatus()) {
						String messSession = session.getMessSession();
						LocalDate requestDate = sickFoodRequestDto.getRequestDate();
                        if (CommonEnum.BF.toString().equals(messSession)) {
                            sessionNames.add(commonResponseUtil.getMessage("message.label.breakfast"));
                        } else if (CommonEnum.LC.toString().equals(messSession)) {
                            sessionNames.add(commonResponseUtil.getMessage("message.label.lunch"));
                        } else if (CommonEnum.DR.toString().equals(messSession)) {
                            sessionNames.add(commonResponseUtil.getMessage("message.label.dinner"));
                        }
                        messSessionFullForm.set(String.join(ModelConstants.COMMA + ModelConstants.SPACE, sessionNames));
						// If the request date is today, enforce session time thresholds
						if (requestDate.equals(today)) {
							if (CommonEnum.BF.toString().equals(messSession) && !session.isBfThreshold()) {
								throw new IllegalArgumentException(messageSource.getMessage("message.request.mess.time.bf.closed", null, Locale.getDefault()));
							}
							if (CommonEnum.LC.toString().equals(messSession) && !session.isLcThreshold()) {
								throw new IllegalArgumentException(messageSource.getMessage("message.request.mess.time.lc.closed", null, Locale.getDefault()));
							}
							if (CommonEnum.DR.toString().equals(messSession) && !session.isDnThreshold()) {
								throw new IllegalArgumentException(messageSource.getMessage("message.request.mess.time.dr.closed", null, Locale.getDefault()));
							}
						}

					}
					List<SickFoodDeliveryStatusEntity> deliveryStatus = sickFoodRequestDto.getMessSessionStatus()
							.stream().filter(messSession -> messSession.getMessSession() != null).map(messSession -> {
								SickFoodDeliveryStatusEntity newDeliveryStatusEntity = SickFoodDeliveryStatusMapper.INSTANCE
										.toSickFoodDeliveryStatusEntity(messSession);
								newDeliveryStatusEntity.setSickFoodRequest(newEntity);
								// newDeliveryStatusEntity.setMessSession(sickFoodRequestDto.getMessSession());
								newDeliveryStatusEntity.setCatererStatus(WorkflowStatus.PENDING.getStatus());
								newDeliveryStatusEntity
										.setStudentDeliveryStatus(sickFoodRequestDto.getStudentDeliveryStatus());
								newDeliveryStatusEntity.setId(null);
								return newDeliveryStatusEntity;
							}).toList();
					sickFoodDeliveryStatusRepository.saveAll(deliveryStatus);
				
					return Constants.SAVED;
				});

				AllStudentsDetailsViewEntity studentDetail=new AllStudentsDetailsViewEntity();
				Optional<AllStudentsDetailsViewEntity> studentDetailOpt = allStudentsDetailsViewRepository.findBystudentId(studentId);
				if(studentDetailOpt.isPresent()) {
					studentDetail = studentDetailOpt.get();
				}
		// Mail template
		
			Optional<MailTemplateEntity> templateOpt = mailTemplateRepository
					.findByMailType(MailTemplateEntity.SICK_FOOD_REQUEST);
			if (templateOpt.isPresent()) {
				MailTemplateEntity template = templateOpt.get();
				String subject = template.getMailSubject();
				String content = template.getMailTemplate();

				// Replace placeholders with actual values
				content = content.replaceAll("#%student_name%#", studentDetail.getStudentName());
				content = content.replaceAll("#%student_id%#", studentId);
				content = content.replaceAll("#%hostel%#", studentDetail.getHostelName());
				content = content.replaceAll("#%room_number%#", studentDetail.getRoomNumber());
				content = content.replaceAll("#%mobile_number%#", sickFoodRequestDto.getMobileNum());
				content = content.replaceAll("#%medical_reason%#", sickFoodRequestDto.getMedicalReason());
				// Format request date
			    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
				String formattedDate = sickFoodRequestDto.getRequestDate().format(formatter);
				content = content.replaceAll("#%request_date%#", formattedDate);
				content = content.replaceAll("#%delivery_address%#", sickFoodRequestDto.getDeliveryAddress());
				content = content.replaceAll("#%mess_session%#", String.valueOf(messSessionFullForm));

				// Save mail queue details
				boolean mailStatus = mailQueueService.saveMailQueue(subject,
						commonResponseUtil.getMessage("message.mail.greetings.for"), content,
						simsConfigDataService.getSimConfigValue(SimsConfigDataService.SICK_FOOD_REQUEST_ADMIN_EMAIL), Constants.SICK_FOOD,
						SecurityCtxUtil.userId(), 1, null, null, null, null);
			} else {
				throw new RuntimeException(messageSource.getMessage("message.mail.template.sickfood.request", null, Locale.getDefault()));
			}
		

		//Upload File Path
		if (result.equalsIgnoreCase("Saved") || result.equalsIgnoreCase("updated")) {
			if (sickFoodRequestDto.getUploadedFileName() != null
					&& !sickFoodRequestDto.getUploadedFileName().isEmpty()) {
				try {
					fileService.encodeFile(SimsConfigDataService.SICK_FOOD_REQUEST_MEDICAL_FILES_PATH,
							sickFoodRequestDto.getUploadedFileName().getBytes(), studentId + ModelConstants.UNDERSCORE
									+ ModelConstants.SICK_FOOD_REQUEST_MEDICAL_FILES_PATH+ ModelConstants.UNDERSCORE+currentMillis+ fileExtension);
				} 
				
				
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return result;
	}



	
	public SickFoodRequestDto getSickFoodById(long id) {
		return sickFoodRequestRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE).map(entity -> {

			SickFoodRequestDto request = SickFoodRequestMapper.INSTANCE.fromSickFoodRequestEntity(entity);
			List<String> order = List.of(CommonEnum.BF.toString(), CommonEnum.LC.toString(),CommonEnum.DR.toString()) ;

			Map<String, SickFoodDeliveryStatusDto> sessionMap = sickFoodDeliveryStatusService.getSickFoodDeliveryStatusByRequestId(id)
					.stream()
					.filter(dto -> order.contains(dto.getMessSession()))
					.collect(Collectors.toMap(SickFoodDeliveryStatusDto::getMessSession, dto -> dto));

			// Create the sorted list, ensuring all three sessions are present
			List<SickFoodDeliveryStatusDto> messSessionStatus = order.stream()
					.map(session -> sessionMap.getOrDefault(session, createNewSickFoodDeliveryStatusDto(session)))
					.toList();
			request.setMessSessionStatus(messSessionStatus);

			boolean anyAccepted = messSessionStatus.stream()
					.anyMatch(dto -> WorkflowStatus.ACCEPTED.getStatus().equalsIgnoreCase(dto.getCatererStatus()));
			request.setAnyAccepted(anyAccepted);

			return request;
		}).orElseGet(() -> {
			SickFoodRequestDto dto = new SickFoodRequestDto();
			List<SickFoodDeliveryStatusDto> messSessionStatus = List.of(new SickFoodDeliveryStatusDto(),
					new SickFoodDeliveryStatusDto(), new SickFoodDeliveryStatusDto());
			dto.setMessSessionStatus(messSessionStatus);
			dto.setAnyAccepted(false);
			return dto;
		});

	}
	
	private SickFoodDeliveryStatusDto createNewSickFoodDeliveryStatusDto(String session) {
	    SickFoodDeliveryStatusDto newDto = new SickFoodDeliveryStatusDto();
	    newDto.setMessSession(session);
	    return newDto;
	}
	
	
	public boolean checkSickFoodRequestExists(String studentId, LocalDate requestDate) {
	    Optional<SickFoodRequestEntity> existingRequest = sickFoodRequestRepository
	        .findTopByActiveFlagAndStudentIdIgnoreCaseAndRequestDate(ModelConstants.STATUS_ACTIVE, studentId, requestDate);
	    
	    return existingRequest.isPresent();
	}


	/*public Long getSickFoodAvailMessId(String messType) throws Exception {
		return messMasterRepository.getMessMasterIdByMessType(messType)
				.orElseThrow(() -> new Exception (messageSource.getMessage("message.active.mess.id.not.found", null, Locale.getDefault()) + " " + messType));

	}*/
	public Long getSickFoodAvailMessId() throws Exception {
		return messMasterRepository.getMessMasterIdByMessType()
				.orElseThrow(() -> new Exception (messageSource.getMessage("message.active.mess.id.not.found", null, Locale.getDefault())));

	}
	 
		public ByteArrayResource downloadFile(long id) throws Exception {
			SickFoodRequestEntity sickFood = sickFoodRequestRepository.findById(id)
					.orElseThrow(() -> new RuntimeException(messageSource.getMessage("message.sickfood.medical.report.not.found", null, Locale.getDefault())));
			String fileName = sickFood.getMedicalProofDoc();
			String sickFoodPath = SimsConfigDataService.SICK_FOOD_REQUEST_MEDICAL_FILES_PATH;

			byte[] fileData = fileService.getDecodedFile(sickFoodPath, fileName);

			return new ByteArrayResource(fileData);
		}

	    public String getFileName(Long id) {
	    	SickFoodRequestEntity sickFood = sickFoodRequestRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException(messageSource.getMessage("message.sickfood.medical.report.not.found", null, Locale.getDefault())));
	        return sickFood.getMedicalProofDoc();
	    }
	    
	 
	   
	    public SickFoodRequestDto checkAvailableSessionWithThresholdTime(SickFoodRequestDto requestDto) throws Exception {
	        
	        Long messId = getSickFoodAvailMessId();
	       
	        int messIds = messId.intValue();
	        int thresholdTime = Integer.parseInt(simsConfigDataService.getSimConfigValue("SICK_FOOD_THRESHOLD_TIME"));
	        String currentTimeString = LocalTime.now().format(DateTimeFormatter.ofPattern(Constants.TIME_FORMAT));
	        List<MessSessionEntity> sessions = messSessionRepository.getAvailableSessionsWithThreshold(
	                messIds, thresholdTime, currentTimeString);

	        // Update session thresholds in the request DTO
	        requestDto.getMessSessionStatus().forEach(session -> {
	            String sessionName = session.getMessSession();
	            if (sessions.stream().anyMatch(s -> s.getId().getSessionName().equals(sessionName))) {
	                switch (CommonEnum.valueOf(sessionName.toUpperCase())) {
	                    case BF -> session.setBfThreshold(true);
	                    case LC -> session.setLcThreshold(true);
	                    case DR -> session.setDnThreshold(true);
	                    
	                }
	            }
	        });

	        return requestDto;
	    }


           /*widget*/
	    public Map<String, String> getTodayFoodRequests(String studentId) {
	        // Fetch today's food requests for the student
	        List<Map<String, Object>> result = sickFoodRequestRepository.getTodayRequests(studentId);

	        // If there are no results, return null to indicate no requests
	        if (result.isEmpty()) {
	            return null;
	        }

	        // Map to store the final response (mess session as key and status as value)
	        Map<String, String> response = new LinkedHashMap<>();

	        // Initialize with default "NA" values for each session
	        response.put(CommonEnum.BF.toString(), Constants.NA);
	        response.put(CommonEnum.LC.toString(), Constants.NA);
	        response.put(CommonEnum.DR.toString(), Constants.NA);

	        // Map the data
            for (Map<String, Object> row : result) {
                String messSession = (String) row.get("mess_session");
                String catererStatus = (String) row.get("caterer_status");
                String studentStatus = (String) row.get("stud_delivery_status");

                // Update only if the session is valid (BF, LC, DR)
                if (response.containsKey(messSession)) {
                    // Prefer student status if available, otherwise use caterer status
                    String status;
                    if (WorkflowStatus.PENDING.getStatus().equalsIgnoreCase(catererStatus)) {
                        status = WorkflowStatus.ORDER_PENDING.getStatus();
                    } else if (WorkflowStatus.ACCEPTED.getStatus().equalsIgnoreCase(catererStatus)) {
                        status = WorkflowStatus.ORDER_ACCEPTED.getStatus();
                    } else if (WorkflowStatus.DELIVERED.getStatus().equalsIgnoreCase(catererStatus)) {
                        status = WorkflowStatus.DELIVERED.getStatus();
                    } else {
                        status = WorkflowStatus.OUT_FOR_DELIVERY.getStatus();
                    }
                    response.put(messSession, status);
                }
            }

	        return response;
	    }

        public Long callVendor(List<SickFoodRequestDto> list){
			SickFoodRequestDto recentRequest = list
					.stream()
					.filter(dto->dto.getRequestDate().equals(LocalDate.now()))
					.findFirst().orElse(null);

			if(Objects.nonNull(recentRequest) && !recentRequest.getMessSessionStatus().isEmpty()) {
				int thresholdTime = Integer.parseInt(simsConfigDataService.getSimConfigValue("SICK_FOOD_THRESHOLD_TIME"));
				String currentTimeString = LocalTime.now().format(DateTimeFormatter.ofPattern(Constants.TIME_FORMAT));

				MessVendorMasterDto messVendorMasterdto =
						Optional.ofNullable(messMasterService.getMessMasterDetailsById(recentRequest.getMessId()))
								.filter(MessMasterDto::getSickFoodAvail)
								.map(dto -> messVendorMasterRepository.getMessVendorMasterByMessId(dto.getId(),ModelConstants.STATUS_ACTIVE))
								.map(MessVendorMasterMapper.INSTANCE::fromMessVendorMasterEntity)
								.orElse(null);

				List<Object[]> objects = recentRequest.getMessSessionStatus()
						.stream()
						.filter(req -> Objects.nonNull(req.getStartTime()) && Objects.nonNull(req.getEndTime()))
						.findFirst()
						.map(req -> sickFoodDeliveryStatusRepository.getPendingDeliveriesToCallVendor(
								recentRequest.getMessId(), thresholdTime, currentTimeString, SecurityCtxUtil.userId(),
								recentRequest.getRequestDate(), ModelConstants.STATUS_ACTIVE)
						)
						.orElse(null);

				if(Objects.nonNull(objects) && objects.size() > 0){
					return messVendorMasterdto.getMobileNo();
				}
				else{
					return 0L;
				}
			}

			return 0L;
		}

        public SickFoodRequestDto getById(Long id){
            return sickFoodRequestRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE)
                    .map(SickFoodRequestMapper.INSTANCE::fromSickFoodRequestEntity).orElse(null);
        }
	   
}



