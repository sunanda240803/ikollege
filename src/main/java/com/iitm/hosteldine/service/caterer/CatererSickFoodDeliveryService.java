package com.iitm.hosteldine.service.caterer;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.dashboard.student.WorkflowStatus;
import com.iitm.hosteldine.dto.caterer.CatererSickFoodDeliveryDto;
import com.iitm.hosteldine.dto.dean.OtherCandidateRequestDto;
import com.iitm.hosteldine.dto.dean.PropertyDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.mailQueue.MailTemplateDto;
import com.iitm.hosteldine.dto.student.SickFoodDeliveryStatusDto;
import com.iitm.hosteldine.exception.RecordNotExistsException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.student.SickFoodDeliveryStatusEntity;
import com.iitm.hosteldine.repository.student.SickFoodDeliveryStatusRepository;
import com.iitm.hosteldine.repository.student.SickFoodRequestRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.hostel.MailTemplateService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.util.FilterEnum;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatererSickFoodDeliveryService {

    private final Utility utility;
    private final SickFoodRequestRepository sickFoodRequestRepository;
    private final CommonResponseUtil commonResponseUtil;
    private final SickFoodDeliveryStatusRepository sickFoodDeliveryStatusRepository;
    private final SimsConfigDataService simsConfigDataService;
    private final MailTemplateService mailTemplateService;
    private final MailQueueService mailQueueService;

    public List<CatererSickFoodDeliveryDto> getSickFoodDeliveryList(PaginationForm form, String url, List<PropertyDto> buttonList) {
        String requestFromDate = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.REQUEST_FROM_DATE.getValue()))
                .map(utility::convertToLocalDate)
                .map(String::valueOf).orElse("null");
        String requestToDate = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.REQUEST_TO_DATE.getValue()))
                .map(utility::convertToLocalDate)
                .map(String::valueOf).orElse("null");
        String catererStatus = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.CATERER_STATUS.getValue()))
                .map(String::valueOf)
                .orElse(null);
        String studentStatus = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.STUDENT_STATUS.getValue()))
                .map(String::valueOf)
                .orElse(null);
        String messSession = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.MESS_SESSION.getValue()))
                .map(String::valueOf)
                .orElse(null);
        String studentName = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.STUDENT_NAME.getValue()))
                .map(String::valueOf)
                .orElse(null);
        String studentId = Optional.ofNullable(utility.getFormAdditionalParam(form, FilterEnum.STUDENT_ID.getValue()))
                .map(String::valueOf)
                .orElse(null);

        Pageable pageable = PageRequest.of(form.getPage()-1, form.getSize());
        return Arrays.stream(sickFoodRequestRepository.getSickFoodRequestList(SecurityCtxUtil.userRole(), SecurityCtxUtil.userName(), requestFromDate, requestToDate,
                        studentId, studentName, catererStatus, studentStatus, messSession))
                .map(o -> mapToDto((Object[]) o, buttonList, url)).toList();
    }

    public CatererSickFoodDeliveryDto mapToDto(Object[] o, List<PropertyDto> buttonList, String url) {
        long requestId = utility.parseLong(o[0]);
        long deliveryId = utility.parseLong(o[1]);
        Boolean messTimeThreshold = utility.parseBoolean(o[16]);
        String catererStatus = utility.parseString(o[10]);
        String studentDeliveryStatus = utility.parseString(o[11]);
        List<String> requiredButtonList = getButtonList(catererStatus, messTimeThreshold);
        CatererSickFoodDeliveryDto dto = CatererSickFoodDeliveryDto.builder()
                .requestedDate(utility.convertToLocalDate(o[2]))
                .studentId(utility.parseString(o[3]))
                .studentName(utility.parseString(o[4]))
                .deliveryAddress(utility.parseString(o[6]))
                .messSession(utility.parseString(o[8]))
                .messType(utility.parseString(o[9]))
                .catererStatus(utility.parseString(o[10]))
                .catererDeliveryStatus(Strings.EMPTY)
                .studentDeliveryStatus(utility.parseString(o[11]))
                .actionList(generateButtons(buttonList, requestId, deliveryId, url, requiredButtonList))
                .build();
        return updateStatus(dto, catererStatus, studentDeliveryStatus, messTimeThreshold);
    }

    private List<String> getButtonList(String catererStatus, boolean messTimeThreshold) {
        List<String> buttonList = new ArrayList<>();
        buttonList.add("View Food Request");
        if (WorkflowStatus.PENDING.getStatus().equalsIgnoreCase(catererStatus)) {
            if (messTimeThreshold) {
                buttonList.add("Accept Request");
            }
        } else if (WorkflowStatus.ACCEPTED.getStatus().equalsIgnoreCase(catererStatus)) {
            if (messTimeThreshold) {
                buttonList.add("Out For Delivery");
            }
        }
        return buttonList;
    }

    private CatererSickFoodDeliveryDto updateStatus(CatererSickFoodDeliveryDto dto, String catererStatus, String studentDeliveryStatus, boolean messTimeThreshold) {
        //Caterer Status
        if (WorkflowStatus.PENDING.getStatus().equalsIgnoreCase(catererStatus)) {
            if (!messTimeThreshold) {
                dto.setCatererStatus(WorkflowStatus.PENDING.getStatus());
            } else {
                dto.setCatererStatus(WorkflowStatus.YET_TO_ACCEPT.getStatus());
            }
        } else {
            dto.setCatererStatus(WorkflowStatus.ORDER_ACCEPTED.getStatus());
        }

        //Caterer Delivery Status
        if (WorkflowStatus.ACCEPTED.getStatus().equalsIgnoreCase(catererStatus)) {
            if (!messTimeThreshold) {
                dto.setCatererDeliveryStatus(WorkflowStatus.PENDING.getStatus());
            } else {
                dto.setCatererDeliveryStatus(WorkflowStatus.YET_TO_DELIVER.getStatus());
            }
        } else if (WorkflowStatus.DELIVERED.getStatus().equalsIgnoreCase(catererStatus)) {
            dto.setCatererDeliveryStatus(WorkflowStatus.DELIVERED.getStatus());
        } else if (Constants.OUT_FOR_DELIVERY.equalsIgnoreCase(catererStatus)) {
            dto.setCatererDeliveryStatus(WorkflowStatus.OUT_FOR_DELIVERY.getStatus());
        } else if (WorkflowStatus.PENDING.getStatus().equalsIgnoreCase(catererStatus)) {
            dto.setCatererDeliveryStatus(Constants.HYPHEN);
        } else {
            dto.setCatererDeliveryStatus(WorkflowStatus.APPROVE.getStatus());
        }

        //Student Delivery Status
        if(Constants.RECEIVED.equalsIgnoreCase(studentDeliveryStatus)) {
            dto.setStudentDeliveryStatus(Constants.RECEIVED);
        }
        else dto.setStudentDeliveryStatus(Constants.HYPHEN);
        return dto;
    }

    private List<PropertyDto> generateButtons(List<PropertyDto> buttonList, Long requestId, Long deliveryId,
                                              String baseUrl, List<String> requiredButtons) {
        String url = Strings.EMPTY;
        List<PropertyDto> propertyDtoList = new ArrayList<>();
        for (PropertyDto o : buttonList) {
            if (requiredButtons.contains(o.getDisplayName())) {
                if (o.getDisplayName().equalsIgnoreCase("View Food Request")) {
                    url = baseUrl + commonResponseUtil.getMessage("url.get.view") +
                            ModelConstants.SLASH + requestId;
                } else if (o.getDisplayName().equalsIgnoreCase("Accept Request")) {
                    url = baseUrl + ModelConstants.SLASH + "catererAccept/" + requestId + ModelConstants.SLASH + deliveryId;
                } else if (o.getDisplayName().equalsIgnoreCase("Out For Delivery")) {
                    url = baseUrl + ModelConstants.SLASH + "catererDelivery/" + requestId + ModelConstants.SLASH + deliveryId;
                }
                o.setUrl(url);
                try {
                    propertyDtoList.add(o.clone());
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        return propertyDtoList;
    }

    public String updateDeliveryStatus(String type, Long requestId, Long deliveryId) throws Exception {
        String catererStatus;
        if ("catererAccept".equalsIgnoreCase(type)) {
            catererStatus = WorkflowStatus.ACCEPTED.getStatus();
        } else {
            catererStatus = Constants.OUT_FOR_DELIVERY;
        }

        SickFoodDeliveryStatusEntity deliveryStatus = sickFoodDeliveryStatusRepository.findByIdAndActiveFlag(deliveryId, ModelConstants.STATUS_ACTIVE)
                .orElse(null);
        if (Objects.nonNull(deliveryStatus)) {
            deliveryStatus.setCatererStatus(catererStatus);
            sickFoodDeliveryStatusRepository.save(deliveryStatus);
            triggerMail(type, requestId, deliveryId);
            return commonResponseUtil.getMessage("message.label.request.updated.successfully");
        } else {
            throw new RecordNotExistsException(commonResponseUtil.getMessage("Record Not Exists"));
        }
    }

    private void triggerMail(String type, Long requestId, Long deliveryId) {
        List<Object[]> sickFoodDeliveryStudentDetails = sickFoodDeliveryStatusRepository.getSickFoodDeliveryStudentDetails(requestId, deliveryId, ModelConstants.STATUS_ACTIVE);
        if (!sickFoodDeliveryStudentDetails.isEmpty()) {
            Object[] first = sickFoodDeliveryStudentDetails.getFirst();
            String status = type.equalsIgnoreCase("catererAccept") ? "Accepted by caterer" : "Out for Delivery";
            String submittedDate = utility.dateFormatter(utility.convertToLocalDate(first[2]));
            String studentName = utility.parseString(first[3]);
            String email = utility.parseString(first[4]);
            String messSession = utility.parseString(first[5]);
            Map<String, String> messMap = simsConfigDataService.getSimConfigValueAsMap(SimsConfigDataService.MESS_SESSION);
            String session = messMap.get(messSession.toUpperCase());
            String message = commonResponseUtil.getMessage("message.label.sick.food.mail.message")
                    .replace("#%submitDate%#", submittedDate)
                    .replace("#%messSession%#", session)
                    .replace("#%status%#", status);

            MailTemplateDto mailTemplate = mailTemplateService.getMailTemplate(ModelConstants.SICK_FOOD_REQUEST_UPDATE_MAIL);
            if (Objects.nonNull(mailTemplate)) {
                String messageTemplate = mailTemplate.getMailTemplate()
                        .replace("#%message%#", message);
                try {
                    mailQueueService.saveMailQueue(mailTemplate.getMailSubject(), studentName, messageTemplate, email, "Sick Food Delivery", null,
                            null, null, null, ModelConstants.REGARDS, ModelConstants.CCW_OFFICE);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

}