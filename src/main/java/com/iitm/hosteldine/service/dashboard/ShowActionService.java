package com.iitm.hosteldine.service.dashboard;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.StudentDetails;
import com.iitm.hosteldine.dto.UserManagementDto;
import com.iitm.hosteldine.dto.financialYear.FinancialYearDto;
import com.iitm.hosteldine.dto.hostel.ShowEventMasterDto;
import com.iitm.hosteldine.dto.hostel.ShowMasterDto;
import com.iitm.hosteldine.dto.hostel.ShowSeatDetailsDto;
import com.iitm.hosteldine.dto.student.ShowStudentDetailDto;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntityId;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntityId;
import com.iitm.hosteldine.form.student.ShowActionForm;
import com.iitm.hosteldine.model.student.ShowStudentDetailEntity;
import com.iitm.hosteldine.repository.mess.MessLedgerARepository;
import com.iitm.hosteldine.repository.mess.MessLedgerBRepository;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentDetailsInfoService;
import com.iitm.hosteldine.service.UserManagementService;
import com.iitm.hosteldine.service.financialYear.FinancialYearService;
import com.iitm.hosteldine.service.hostel.ShowEventMasterService;
import com.iitm.hosteldine.service.hostel.ShowMasterService;
import com.iitm.hosteldine.service.hostel.ShowSeatDetailsService;
import com.iitm.hosteldine.service.mailQueue.MailQueueService;
import com.iitm.hosteldine.service.student.ShowStudentDetailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class ShowActionService {
    private final ShowMasterService showMasterService;
    private final ShowSeatDetailsService showSeatDetailsService;
    private final SimsConfigDataService simsConfigDataService;
    private final ShowEventMasterService showEventMasterService;
    private final MailQueueService mailQueueService;
    private final UserManagementService userManagementService;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final ShowStudentDetailService showStudentDetailService;
    private final MessageSource messageSource;
    private final FinancialYearService financialYearService;
    private final MessLedgerARepository messLedgerARepository;
    private final MessLedgerBRepository messLedgerBRepository;

    @Value("${date.time.format}")
    private String dateTimeFormat;

    public ShowActionForm getShowList(long eventId) {
        ShowActionForm showActionForm = new ShowActionForm();
        showActionForm.setEventId(eventId);
        showActionForm.setShowMasterDtoList(
                showMasterService.getShowNameList(eventId)
                        .stream()
                        .filter(show->show.getCurrentlyActive().equalsIgnoreCase(ModelConstants.STATUS_ACTIVE))
                .peek(show -> {
                    // Fetch seat details for the show
                    var seatDetails = showSeatDetailsService.getSeatDetailsByShowId(show.getId())
                                    .stream()
                                    .filter(s->s.getCurrentlyActive().equalsIgnoreCase(ModelConstants.STATUS_ACTIVE))
                                    .toList();
                    show.setShowSeatDetails(seatDetails);

                    // Determine if the show is booked
                    boolean isShowBooked = seatDetails.stream()
                            .anyMatch(seat -> Boolean.TRUE.equals(seat.getIsSeatBooked()));
                    show.setIsShowBooked(isShowBooked);

                    // Calculate total seats booked for the show
                    long totalSeatsBooked = seatDetails.stream()
                            .mapToLong(seat -> showStudentDetailService.getTotalSeatsBooked(seat.getId(),SecurityCtxUtil.userId().toUpperCase()))
                            .sum();
                    show.setTotalSeatsBooked(totalSeatsBooked);
                })
                .toList()
        );
        ShowEventMasterDto eventMasterDto = showEventMasterService.getEventMasterById(eventId);
        showActionForm.setCreditLimit(eventMasterDto.getCreditLimit());
        showActionForm.setShowEventMasterDto(eventMasterDto);

        //calculating student total purchase amount
        Double totPurchaseAmount = getTotalPurchaseAmount(eventId);
        showActionForm.setTotalPurchasedAmount(totPurchaseAmount);

//        List<ShowStudentDetailDto> studentDetails = showStudentDetailService.getAllStudentPurchaseDetails();
//        double totalPurchaseAmount = studentDetails.isEmpty()
//                ? 0.0
//                : studentDetails.stream()
//                .mapToDouble(item -> item.getDiscountAmount() * item.getPurchasedCount())
//                .sum();
//        showActionForm.setTotalPurchasedAmount(totalPurchaseAmount);

        return showActionForm;
    }

    @Transactional
    public boolean saveOrUpdateShowDetails(ShowActionForm showActionForm,StringBuilder errorMessage) throws Exception {
        List<ShowStudentDetailEntity> showStudentDetailEntities = new ArrayList<>();
        ShowEventMasterDto eventMasterDto;

        // Filter and map seat details in a single pass
        showActionForm.setShowMasterDtoList(
                showActionForm.getShowMasterDtoList().stream()
                        .filter(show -> (Objects.nonNull(show.getShowSeatDetails()) && !show.getShowSeatDetails().isEmpty()) ||
                                (Objects.nonNull(show.getShowSeatDetailsDto()) && Objects.nonNull(show.getShowSeatDetailsDto().getId())))
                        .peek(show -> {
                                    if (Objects.nonNull(show.getShowSeatDetails())) {
                                        show.setShowSeatDetails(
                                                show.getShowSeatDetails().stream()
                                                        .filter(seat -> Objects.nonNull(seat.getId()))
                                                        .toList()
                                        );
                                    }
                                }
                        )
                        .toList()
        );

        if (checkStudentBalance()) {
            List<Object[]> objects = showStudentDetailService.checkStudentHostler(SecurityCtxUtil.userId().toUpperCase());
            if (!objects.isEmpty()) {
                if (checkStudentCreditLimit(showActionForm)) {
                    eventMasterDto = showEventMasterService.getEventMasterById(showActionForm.getEventId());
                    if (checkIsStudentHasPermission(eventMasterDto)) {
                        showActionForm.setTotalCartAmount(0.0);
                        showActionForm.getShowMasterDtoList().forEach(show -> {
                            ShowMasterDto showDetails = showMasterService.getShowWiseDetailsById(show.getId());
                            showDetails.setShowSeatDetails(show.getShowSeatDetails());
                            showDetails.setShowSeatDetailsDto(show.getShowSeatDetailsDto());
                            showDetails.setPrintName(show.getPrintName());
                            handlePurhaseSave(showDetails, showActionForm, showStudentDetailEntities,errorMessage);
                        });
                    } else {
                        throw new IllegalArgumentException(messageSource.getMessage("message.validation.not.b.tech.student", null, Locale.getDefault()));
                    }
                } else {
                    throw new IllegalArgumentException(messageSource.getMessage("message.validation.student.credit.limit",null,Locale.getDefault()));
                }
            } else {
                throw new IllegalArgumentException(messageSource.getMessage("message.validation.hotel.students.register",null,Locale.getDefault()));
            }
        } else {
            throw new IllegalArgumentException(messageSource.getMessage("message.validation.negative.balance",null,Locale.getDefault()));
        }

        if (!showStudentDetailEntities.isEmpty()) {
            //handleLedgerTransactions(showActionForm);
            List<ShowStudentDetailDto> showStudentDetailDtos = showStudentDetailService.saveAllStudentShowDetail(showStudentDetailEntities);
            sendEmailForPurchase(showActionForm, showStudentDetailDtos);
            return true;
        }
        else return false;
    }

    private boolean handlePurhaseSave(ShowMasterDto showDetails, ShowActionForm showActionForm,
                                      List<ShowStudentDetailEntity> showStudentDetailEntities,StringBuilder errorMessage) {
        List<ShowStudentDetailDto> studentSeatList = showStudentDetailService.getStudentDetailsByShow(SecurityCtxUtil.userId(), showDetails.getId());
        AtomicReference<Boolean> status = new AtomicReference<>(true);
        if (!studentSeatList.isEmpty()) {
            return false;
        }
        if (showDetails.getMaxSeatCount() == 1) {
            showDetails.setShowSeatDetails(new ArrayList<>());
            showDetails.getShowSeatDetails().add(showDetails.getShowSeatDetailsDto());
        }
        if (Objects.nonNull(showDetails.getShowSeatDetails()) && !showDetails.getShowSeatDetails().isEmpty()) {
            showDetails.getShowSeatDetails()
                    .forEach(seat -> {
                        if(Objects.nonNull(seat.getId())) {
                            if (isSeatAvailable(seat.getId(), showDetails)) {
                                saveOrUpdateShowStudentDetails(showDetails, seat, showActionForm, showStudentDetailEntities);
                            } else {
                                ShowSeatDetailsDto seatDetailsDto = showSeatDetailsService.getShowSeatDetailsById(seat.getId());
                                errorMessage.append("Show: ").append(showDetails.getShowName()).append(", Seat: ")
                                        .append(seatDetailsDto.getSeatName()).append("<br>");
                            }
                        }
                    });
        }

        return status.get();
    }

    private boolean isSeatAvailable(Long seatId,ShowMasterDto showMasterDto) {
        boolean isSeatAvailable = showSeatDetailsService.getSeatIsAvailable(seatId);

        if (!isSeatAvailable) {
            return false;
        }
        else{
            List<ShowStudentDetailDto> totalSeatsBooked = showStudentDetailService.getAllStudentShowDetailBySeatId(seatId,
                    SecurityCtxUtil.userId().toUpperCase());
            return totalSeatsBooked.isEmpty() || (showMasterDto.getMaxSeatCount() != totalSeatsBooked.size());
        }
    }

    public boolean checkStudentBalance() {
        return Optional.ofNullable(showStudentDetailService.getStudentBalance(SecurityCtxUtil.userId().toUpperCase()))
                .filter(balance -> balance >= 0)
                .isPresent();
    }
    public boolean checkStudentBalanceForMessReg(String studentId) {
        return Optional.ofNullable(showStudentDetailService.getStudentBalance(studentId))
                .filter(balance -> balance >= -1000)
                .isPresent();
    }

    public boolean checkIsStudentHasPermission(ShowEventMasterDto eventMasterDto) {
        if(eventMasterDto.getEventDesc() == null){
            return false;
        }
        return switch (eventMasterDto.getEventDesc().toUpperCase()) {
            case String s when s.contains("SAARANG") -> checkPermission("SAARANG_REGISTRATION_STUDENT_ID");
            case String s when s.contains("SHAASTRA") -> checkPermission("SHAASTRA_REGISTRATION_STUDENT_ID");
            default -> false;
        };
    }

    private boolean checkPermission(String configKey) {
        return Optional.ofNullable(simsConfigDataService.getSimConfigValue(configKey))
                .map(studentRegIdPattern -> studentRegIdPattern.contains(SecurityCtxUtil.userId().substring(4, 5).toUpperCase()))
                .orElse(false);
    }

    private boolean checkStudentCreditLimit(ShowActionForm showActionForm) {
        ShowEventMasterDto eventMasterDto = showEventMasterService.getEventMasterById(showActionForm.getEventId());
        Double savedPurchaseAmount = getTotalPurchaseAmount(showActionForm.getEventId());

        double totalPurchase = savedPurchaseAmount + showActionForm.getTotalCartAmount();
        double creditLimit = eventMasterDto.getCreditLimit();
        return  totalPurchase <= creditLimit;
    }

    private Double getTotalPurchaseAmount (Long eventId){
        Double purchaseAmount = 0.0;
        List<Object[]> totalPurAmt = showStudentDetailService.getStudentLimit(SecurityCtxUtil.userId().toUpperCase(), eventId);
        if(!totalPurAmt.isEmpty()){
            for (Object[] o : totalPurAmt) {
                purchaseAmount = o[0] != null ? Double.parseDouble(o[0].toString()) : 0.0; // totalPurchase
            }
        }
        return purchaseAmount;
    }

    public boolean saveOrUpdateShowStudentDetails(ShowMasterDto showMasterDto, ShowSeatDetailsDto showSeatDetailsDto,
                                                               ShowActionForm showActionForm,
                                                  List<ShowStudentDetailEntity> showStudentDetailEntities) {
        ShowSeatDetailsDto seatDetails = showSeatDetailsService.getSeatDetailsByIdAndShowIdAndIsAvailable(showSeatDetailsDto.getId(),
                showMasterDto.getId());

        if (seatDetails != null) {
            Double amount = Objects.nonNull(seatDetails.getDiscountedAmount()) && seatDetails.getDiscountedAmount() > 0 ?
                    seatDetails.getDiscountedAmount() : seatDetails.getAmount();
            showActionForm.setTotalCartAmount(showActionForm.getTotalCartAmount() + amount);
            ShowStudentDetailEntity studentDetailEntity = new ShowStudentDetailEntity();
            studentDetailEntity.setStudentId(SecurityCtxUtil.userId().toUpperCase());
            studentDetailEntity.setStudentName(Objects.nonNull(showMasterDto.getPrintName()) && !showMasterDto.getPrintName().isEmpty()?
                            showMasterDto.getPrintName():"-");
            studentDetailEntity.setSeatId(seatDetails.getId());
            studentDetailEntity.setAmount(seatDetails.getAmount());
            studentDetailEntity.setDiscountAmount(seatDetails.getDiscountedAmount());
            studentDetailEntity.setPurchasedCount(showMasterDto.getMaxSeatCount() == 1 ? 1 : showSeatDetailsDto.getPurchaseCount());
            studentDetailEntity.setDeliveryType(showActionForm.getDeliveryType());
            if(showActionForm.getDeliveryType().equals("Delivery")){
                studentDetailEntity.setDeliveryAddress(showActionForm.getName() + ", " + showActionForm.getDoorNumber() + ", "
                        + showActionForm.getStreet() + ", " + showActionForm.getCity() + ", " + showActionForm.getDistrict() + ", "
                        + showActionForm.getState() + ", " + showActionForm.getPinCode());
                studentDetailEntity.setContactNo(Long.parseLong(showActionForm.getContactNumber().replace("-","")));
            }
            studentDetailEntity.onCreate();
            showStudentDetailEntities.add(studentDetailEntity);
            return true;
        }
        return false;
    }

    public void sendEmailForPurchase(ShowActionForm showActionForm,List<ShowStudentDetailDto> showStudentDetailDtoList) throws Exception {
        if(!showStudentDetailDtoList.isEmpty()){
            ShowEventMasterDto eventMasterById = showEventMasterService.getEventMasterById(showActionForm.getEventId());
            String eventName = eventMasterById.getEventName();
            // Get the current date and time
            LocalDateTime now = LocalDateTime.now();

            // Define the desired format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimeFormat);

            // Format the current date and time
            String formattedDateTime = now.format(formatter);
            StringBuilder message = new StringBuilder();

            String messageTemplete = null;
            if (eventName != null && eventName.toUpperCase().contains("SHAASTRA")) {
                messageTemplete = simsConfigDataService.getSimConfigValue("SHAASTRA_MAIL_TEMPLATE");
            }
            else{
                messageTemplete = simsConfigDataService.getSimConfigValue("SAARANG_MAIL_TEMPLATE");
            }

            showStudentDetailDtoList.forEach(showStudentDetailDto -> {
                ShowMasterDto showBySeatId = showMasterService.getShowBySeatId(showStudentDetailDto.getSeatId());
                ShowSeatDetailsDto showSeatDetailsById = showSeatDetailsService.getShowSeatDetailsById(showStudentDetailDto.getSeatId());
                if(showBySeatId != null){
                    Double purchasedAmount = showStudentDetailDto.getDiscountAmount() * showStudentDetailDto.getPurchasedCount();
                    DecimalFormat indianFormatter = new DecimalFormat("#,##,##,##0.00");
                    indianFormatter.setCurrency(Currency.getInstance(new Locale("en", "IN")));
                    String formattedAmount = indianFormatter.format(purchasedAmount != null ? purchasedAmount : 0).replaceAll(",", "");

                    message.append("<tr>")
                            .append("<td>").append(showBySeatId.getShowName()).append("</td>")
                            .append("<td>").append(showSeatDetailsById.getSeatName()).append("</td>")
                            .append("<td>").append("Rs.").append(formattedAmount).append("</td>")
                            .append("<td>").append(formattedDateTime).append("</td>");

                    if (eventName != null && eventName.toUpperCase().contains("SHAASTRA")) {
                        message.append("<td>").append(showStudentDetailDto.getPurchasedCount()).append("</td>");
                    }
                    message.append("</tr>");
                }
            });

            messageTemplete = messageTemplete.replace("#%eventName%#",eventName);
            messageTemplete = messageTemplete.replace("#%tr%#",message.toString());


            UserManagementDto userManagementDto = userManagementService.getUserByUserName();
            StudentDetails studentDetails = studentDetailsInfoService.getStudentDetails(SecurityCtxUtil.userId().toUpperCase());
            boolean mailQueueStatus = mailQueueService.saveMailQueue(eventName + " PURCHASE CONFIRMATION",
                    studentDetails.getStudentFullName(),
                    messageTemplete, userManagementDto.getEmail(), "ShowAction - sendEmailForPurchase", SecurityCtxUtil.userId(), null, null, null,
                    "Regards", eventName);
        }
    }

    public void handleLedgerTransactions(ShowActionForm showActionForm){
        String voucherNo = "3564" + String.valueOf(System.currentTimeMillis()).substring(8);
        ShowEventMasterDto event = showEventMasterService.getEventMasterById(showActionForm.getEventId());
        FinancialYearDto activeFinancialYear = financialYearService.getActiveFinancialYear();

        MessLedgerAEntity messLedgerAEntity = createMessLedgerAEntity(showActionForm,event,activeFinancialYear,voucherNo);
        MessLedgerBEntity messLedgerBEntity = createMessLedgerBEntity(showActionForm,event,activeFinancialYear,voucherNo);

        MessLedgerAEntity saved = messLedgerARepository.save(messLedgerAEntity);
        messLedgerARepository.flush();
        messLedgerBEntity.getId().setVoucherNo(saved.getId().getVoucherNo());
        messLedgerBRepository.saveAndFlush(messLedgerBEntity);
    }

    public MessLedgerAEntity createMessLedgerAEntity(ShowActionForm showActionForm,ShowEventMasterDto event,
                                                     FinancialYearDto activeFinancialYear, String voucherNo){

        MessLedgerAEntity messLedgerAEntity = new MessLedgerAEntity();
        MessLedgerAEntityId messLedgerAEntityId = new MessLedgerAEntityId();
        messLedgerAEntityId.setBookType(Constants.MESS_MS);
        messLedgerAEntityId.setFinYear(activeFinancialYear.getFinYear());
        messLedgerAEntityId.setVoucherNo(voucherNo);

        messLedgerAEntity.setId(messLedgerAEntityId);
        messLedgerAEntity.setVoucherDate(LocalDate.now());
        messLedgerAEntity.setAcchead(event.getAcchead());
        messLedgerAEntity.setDescription(event.getEventDesc());
        messLedgerAEntity.setAmount(showActionForm.getTotalCartAmount());
        messLedgerAEntity.setRecon(ModelConstants.STATUS_INACTIVE);
        messLedgerAEntity.setCancelStatus(ModelConstants.STATUS_INACTIVE);
        messLedgerAEntity.setDebitOrCredit(Constants.CREDIT);
        messLedgerAEntity.setItAmount(0D);
        messLedgerAEntity.setItPer(0D);
        messLedgerAEntity.setLink(0);
        messLedgerAEntity.setSurcharge(0D);
        messLedgerAEntity.setUnmatchAmnt(0D);
        messLedgerAEntity.onCreate();
        return messLedgerAEntity;
    }

    public MessLedgerBEntity createMessLedgerBEntity(ShowActionForm showActionForm,ShowEventMasterDto event,
                                                     FinancialYearDto activeFinancialYear, String voucherNo){
        int slno = 334 + (int) (Math.random() * 10);
        MessLedgerBEntity messLedgerBEntity = new MessLedgerBEntity();
        MessLedgerBEntityId messLedgerBEntityId = new MessLedgerBEntityId();
        messLedgerBEntityId.setBookType(Constants.MESS_MS);
        messLedgerBEntityId.setVoucherNo(voucherNo);
        messLedgerBEntityId.setFinYear(activeFinancialYear.getFinYear());
        messLedgerBEntityId.setSlno(slno);
        messLedgerBEntity.setId(messLedgerBEntityId);

        messLedgerBEntity.setVoucherDate(LocalDate.now());
        messLedgerBEntity.setAcchead(SecurityCtxUtil.userId().toUpperCase());
        messLedgerBEntity.setDescription(event.getEventDesc());
        messLedgerBEntity.setAmount(showActionForm.getTotalCartAmount());
        messLedgerBEntity.setRecon(ModelConstants.STATUS_INACTIVE);
        messLedgerBEntity.setCancelStatus(ModelConstants.STATUS_INACTIVE);
        messLedgerBEntity.setDebitOrCredit(Constants.DEBIT);
        messLedgerBEntity.setTdsValue(0D);
        messLedgerBEntity.setAdvAmt(0D);
        messLedgerBEntity.setBillAmt(0D);
        messLedgerBEntity.setLink(0);
        messLedgerBEntity.setSurcharge(0D);
        messLedgerBEntity.setUnmatchAmnt(0D);
        messLedgerBEntity.onCreate();
        return messLedgerBEntity;
    }

}
