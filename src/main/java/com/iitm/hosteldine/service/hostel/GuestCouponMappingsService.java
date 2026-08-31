package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.hostel.CouponStatus;
import com.iitm.hosteldine.dto.hostel.DailyCouponRequestDTO;
import com.iitm.hosteldine.dto.hostel.GuestCouponIssuedDTO;
import com.iitm.hosteldine.dto.hostel.GuestCouponIssuedResultDTO;
import com.iitm.hosteldine.dto.hostel.GuestCouponRequestDTO;
import com.iitm.hosteldine.dto.student.StudentDetailsWithHostelDTO;
import com.iitm.hosteldine.exception.UserRoleNotMappedException;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.model.hostel.GuestCouponMappingsEntity;
import com.iitm.hosteldine.model.hostel.GuestCouponPaymentAdviceEntity;
import com.iitm.hosteldine.repository.hostel.GuestCouponMappingsRepository;
import com.iitm.hosteldine.repository.mess.MessMasterRepository;
import com.iitm.hosteldine.util.CommonEnum;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.StringUtility;
import com.iitm.hosteldine.util.TokenGeneration;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.iitm.hosteldine.util.StringUtility.getNullIfEmpty;

@Service
@RequiredArgsConstructor
public class GuestCouponMappingsService {
    private final GuestCouponMappingsRepository guestCouponRepository;
    private final MessMasterRepository messMasterRepository;
    private final MessageSource messageSource;
    private final ExcelUtility excelUtility;

    public List<GuestCouponIssuedResultDTO> getIssuedGuestCoupons(PaginationForm form, GuestCouponIssuedDTO req) throws UserRoleNotMappedException {
        int page = form.getPage() - 1;
        Pageable pageable = PageRequest.of(page, form.getSize());
        String userRole = Objects.requireNonNullElse(SecurityCtxUtil.userRole(), ModelConstants.EMPTY_STRING);
        if (userRole.equalsIgnoreCase(Constants.ANONYMOUS)) {
            throw new UserRoleNotMappedException(messageSource.getMessage(
                    "message.error.role.notFound",
                    new Object[]{SecurityCtxUtil.userName()},
                    Locale.getDefault()));
        }

        req.setUserRole(userRole);
        req.setLoggedInUser(SecurityCtxUtil.userName());

        return guestCouponRepository.getIssuedCoupons(req.getName(), req.getUsedStatus(), req.getRequestId(),
                req.getDiningFrom(), req.getDiningTo(), req.getSubmittedFrom(), req.getSubmittedTo(), req.getMessId(),
                userRole, SecurityCtxUtil.userName());
    }

    public GuestCouponIssuedDTO getCountsForSessions() throws UserRoleNotMappedException {
        GuestCouponIssuedDTO countDto = new GuestCouponIssuedDTO();
        String userName = SecurityCtxUtil.userName();
        List<Object[]> resultList = messMasterRepository.getMessDetailsForCaterer(userName, ModelConstants.STATUS_ACTIVE);
        if (CollectionUtils.isNotEmpty(resultList)) {
            Object[] result = resultList.get(0);
            int messId = result[1] != null ? Integer.valueOf(result[1].toString()) : 0;

            // Fetch the session counts using the messId
            Object[] sessionCounts = guestCouponRepository.getSessionCountsByMessId(messId);
            if (sessionCounts != null && sessionCounts.length > 0) {
                Object[] obj = (Object[]) sessionCounts[0];
                countDto.setBfCount(obj[0] != null ? Integer.valueOf(obj[0].toString()) : 0);
                countDto.setLcCount(obj[1] != null ? Integer.valueOf(obj[1].toString()) : 0);
                countDto.setDrCount(obj[2] != null ? Integer.valueOf(obj[2].toString()) : 0);
            }
        }
        return countDto;
    }

    private String getStringValue(Object obj) {
        return obj != null && !String.valueOf(obj).trim().isEmpty() ? String.valueOf(obj) : Constants.HYPHEN;
    }

    public String returnCoupons(List<Long> couponIds) {
        List<GuestCouponMappingsEntity> entities = guestCouponRepository.findByCouponIdIn(couponIds);
        entities.forEach(e -> {
            e.setUsedStatus(CouponStatus.RETURNED.getStatus());
            guestCouponRepository.save(e);
        });

        return messageSource.getMessage("response.status.success", null, Locale.getDefault());
    }

    public Workbook downloadIssuedCoupons(GuestCouponIssuedDTO request) throws UserRoleNotMappedException {
        String userRole = Objects.requireNonNullElse(SecurityCtxUtil.userRole(), ModelConstants.EMPTY_STRING);
        if (userRole.equalsIgnoreCase(Constants.ANONYMOUS)) {
            throw new UserRoleNotMappedException(messageSource.getMessage(
                    "message.error.role.notFound",
                    new Object[]{SecurityCtxUtil.userName()},
                    Locale.getDefault()));
        }
        String[] headerData = ModelConstants.GUEST_COUPON_ISSUED_HEADER;
        int columnCount = headerData.length;
        String sheetName = messageSource.getMessage("message.label.guest.coupon.issued.list", null,
                Locale.getDefault());

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        XSSFCellStyle style3 = excelUtility.setDataStyle(workbook);

        Row row0 = sheet.createRow(0);
        String excelHeader = messageSource.getMessage("message.label.heading", null, Locale.getDefault());
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));
        excelUtility.createHeaderWithCenterStyling(row0, 0, new Object[] {excelHeader}, workbook);

        Row row1 = sheet.createRow(1);
        SimpleDateFormat sdf = new SimpleDateFormat(
                messageSource.getMessage("session.date.format", null, Locale.getDefault()));
        String reportDate = messageSource.getMessage("message.label.report.date.colon", null, Locale.getDefault())
                + sdf.format(new Date());
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, columnCount - 1));
        excelUtility.createHeaderWithCenterStyling(row1, 0, new Object[] {reportDate}, workbook);

        Row row3 = sheet.createRow(2);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, columnCount - 1));
        excelUtility.createHeaderWithCenterStyling(row3, 0, new Object[] {sheetName}, workbook);

        excelUtility.createHeader(sheet.createRow(4), 0, headerData, workbook);

        List<GuestCouponIssuedResultDTO> issuedCoupons = guestCouponRepository.getIssuedCoupons(request.getName(),
                request.getUsedStatus(), request.getRequestId() != null ? request.getRequestId() : 0L,
                getNullIfEmpty(request.getDiningFrom()), getNullIfEmpty(request.getDiningTo()),
                getNullIfEmpty(request.getSubmittedFrom()), getNullIfEmpty(request.getSubmittedTo()),
                request.getMessId(), userRole, SecurityCtxUtil.userName());

        issuedCoupons.forEach(coupon -> {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);

            String[] cellValues = { coupon.getSubmittedDate(), StringUtility.getNonNullValue(coupon.getRequestId()),
                    coupon.getMessName(), coupon.getCouponNumber(), coupon.getName(), coupon.getValidityFromDate(),
                    coupon.getValidityToDate(), coupon.getCouponType(), coupon.getUsedStatus() };

            for (int colIdx = 0; colIdx < cellValues.length; colIdx++) {
                excelUtility.createAndSetColumn(sheet, row, colIdx, -1, cellValues[colIdx], style3);
            }
        });

        for (int colIdx = 0; colIdx < columnCount; colIdx++) {
            sheet.autoSizeColumn(colIdx);
        }

        return workbook;
    }

    public void saveCoupons(GuestCouponRequestDTO dto, GuestCouponPaymentAdviceEntity paymentEntity)  {
        // TODO: len and deprecated
        List<GuestCouponMappingsEntity> guestCoupons;
        boolean isBulkCoupon =dto.isBulkCoupon();

        // TODO: move to mapper
        if (!isBulkCoupon) {
            guestCoupons = getDailyCoupons(dto, paymentEntity);
        } else {
            guestCoupons = getBulkCoupons(dto, paymentEntity);
        }

        guestCouponRepository.saveAll(guestCoupons);
    }

    private List<GuestCouponMappingsEntity> getBulkCoupons(GuestCouponRequestDTO dto, GuestCouponPaymentAdviceEntity paymentEntity) {
        List<GuestCouponMappingsEntity> allSessionCoupons = new ArrayList<>();

        for (DailyCouponRequestDTO couponDto : dto.getFoodFrequency()) {
            allSessionCoupons.addAll(getSessionCoupons(couponDto, paymentEntity, CommonEnum.BF.toString(), dto.getMessId(),
                    couponDto.getNoOfBreakfast()));
            allSessionCoupons.addAll(getSessionCoupons(couponDto, paymentEntity,CommonEnum.LC.toString(), dto.getMessId(),
                    couponDto.getNoOfLunch()));
            allSessionCoupons.addAll(getSessionCoupons(couponDto, paymentEntity, CommonEnum.DR.toString(), dto.getMessId(),
                    couponDto.getNoOfDinner()));
            allSessionCoupons.addAll(getSessionCoupons(couponDto, paymentEntity, CommonEnum.ET.toString(), dto.getMessId(),
                    couponDto.getNoOfSnacks()));
        }

        return allSessionCoupons;
    }

    private List<GuestCouponMappingsEntity> getSessionCoupons(DailyCouponRequestDTO dto,
                                                              GuestCouponPaymentAdviceEntity paymentEntity,
                                                              String session, Long messId, Integer sessionCount) {
        List<GuestCouponMappingsEntity> sessionCoupons = new ArrayList<>();

        for (int couponInd = 0; couponInd < sessionCount; couponInd++) {
            sessionCoupons.add(getCouponMapping(dto, paymentEntity, session, messId));
        }

        return sessionCoupons;
    }

    private List<GuestCouponMappingsEntity> getDailyCoupons(GuestCouponRequestDTO dto,
                                                            GuestCouponPaymentAdviceEntity paymentEntity) {
        List<GuestCouponMappingsEntity> guestCoupons = new ArrayList<>();

        for (DailyCouponRequestDTO couponDto : dto.getFoodFrequency()) {
            if (couponDto.getHavingBreakfast()) {
                guestCoupons.add(getCouponMapping(couponDto, paymentEntity, CommonEnum.BF.toString(), dto.getMessId()));
            }

            if (couponDto.getHavingLunch()) {
                guestCoupons.add(getCouponMapping(couponDto, paymentEntity, CommonEnum.LC.toString(), dto.getMessId()));
            }

            if (couponDto.getHavingDinner()) {
                guestCoupons.add(getCouponMapping(couponDto, paymentEntity, CommonEnum.DR.toString(), dto.getMessId()));
            }
            if (couponDto.getHavingSnacks()) {
                guestCoupons.add(getCouponMapping(couponDto, paymentEntity, CommonEnum.ET.toString(), dto.getMessId()));
            }
        }

        return guestCoupons;
    }

    private GuestCouponMappingsEntity getCouponMapping(DailyCouponRequestDTO dto, GuestCouponPaymentAdviceEntity paymentEntity,
                                  String couponType, Long messId) {
        GuestCouponMappingsEntity mappingsEntity = new GuestCouponMappingsEntity();
        mappingsEntity.setCouponNumber(new TokenGeneration(6).nextNumericString());
        mappingsEntity.setRequestId(paymentEntity.getRequestId());
        mappingsEntity.setCouponType(couponType);
        mappingsEntity.setMessId(messId);
        // TODO
        mappingsEntity.setFromDate(dto.getDate());
        mappingsEntity.setToDate(dto.getDate());
        mappingsEntity.setUsedStatus("Distributed");
        mappingsEntity.setActiveFlag(ModelConstants.STATUS_ACTIVE);

        return mappingsEntity;
    }
}
