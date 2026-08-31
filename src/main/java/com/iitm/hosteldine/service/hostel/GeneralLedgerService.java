package com.iitm.hosteldine.service.hostel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.hostel.AccountHeadDto;
import com.iitm.hosteldine.dto.hostel.GeneralLedgerDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.hostel.AccountHeadMapper;
import com.iitm.hosteldine.model.hostel.AccountHeadEntity;
import com.iitm.hosteldine.repository.hostel.AccountHeadRepository;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.Utility;
import com.iitm.hosteldine.validator.common.ValidationCommon;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeneralLedgerService {

	private final MessageSource messageSource;
	private final Utility utility;
	private final ExcelUtility excelUtility;
	private final AccountHeadService accHeadService;
	private final AccountHeadRepository accHeadRepository;

	public Page<GeneralLedgerDto> getGeneralLedgerList(PaginationForm form, GeneralLedgerDto filter, Double opBal)
			throws Exception {
		Page<Object[]> entityList = fetchGeneralLedgerData(form, filter);

		return switch (filter.getBookType()) {
		case Constants.MESS_MS, Constants.CREDIT_CARD -> getMessCardList(entityList, opBal != null ? opBal : 0.0);
		case Constants.TEMPORARY_ACCOMMODATION -> getTempAccomList(entityList);
		case Constants.GUEST_COUPON -> getGuestCouponList(entityList);
		default -> Page.empty();
		};
	}

	public Page<GeneralLedgerDto> getGuestCouponList(Page<Object[]> entityList) {
		List<GeneralLedgerDto> result = getGuestCouponWithSummary(entityList.getContent());
		return new PageImpl<>(result, entityList.getPageable(), Integer.MAX_VALUE);
	}

	public List<GeneralLedgerDto> getGuestCouponReport(List<Object[]> entityList) {
		return getGuestCouponWithSummary(entityList);
	}

	private List<GeneralLedgerDto> getGuestCouponWithSummary(List<Object[]> entityList) {
		List<GeneralLedgerDto> result = new ArrayList<>();
		Map<LocalDate, List<GeneralLedgerDto>> groupedByDate = new LinkedHashMap<>();

		int totalBf = 0;
		int totalLn = 0;
		int totalDn = 0;
		int totalSn = 0;

		for (Object[] record : entityList) {
			GeneralLedgerDto dto = new GeneralLedgerDto();
			LocalDate date = record[5] != null ? LocalDate.parse(record[5].toString()) : null;
			int bf = record[0] != null ? Integer.parseInt(record[0].toString()) : 0;
			int ln = record[1] != null ? Integer.parseInt(record[1].toString()) : 0;
			int dn = record[2] != null ? Integer.parseInt(record[2].toString()) : 0;
			int sn = record[3] != null ? Integer.parseInt(record[3].toString()) : 0;

			dto.setVoucherDate(date);
			dto.setStudentName(ValidationCommon.getStringValueOrHyphen(record[6]));
			dto.setMessName(ValidationCommon.getStringValueOrHyphen(record[7]));
			dto.setRefAccount(ValidationCommon.getStringValueOrHyphen(record[8]));
			dto.setBf(bf);
			dto.setLn(ln);
			dto.setDn(dn);
			dto.setSn(sn);

			totalBf += bf;
			totalLn += ln;
			totalDn += dn;
			totalSn += sn;

			if (date != null) {
				groupedByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(dto);
			}
		}

		for (Map.Entry<LocalDate, List<GeneralLedgerDto>> entry : groupedByDate.entrySet()) {
			List<GeneralLedgerDto> dayList = entry.getValue();
			result.addAll(dayList);

			int dayTotalBf = dayList.stream().mapToInt(d -> d.getBf() != null ? d.getBf() : 0).sum();
			int dayTotalLn = dayList.stream().mapToInt(d -> d.getLn() != null ? d.getLn() : 0).sum();
			int dayTotalDn = dayList.stream().mapToInt(d -> d.getDn() != null ? d.getDn() : 0).sum();
			int dayTotalSn = dayList.stream().mapToInt(d -> d.getSn() != null ? d.getSn() : 0).sum();

			GeneralLedgerDto daySummary = new GeneralLedgerDto();
			daySummary.setSummaryRow(true);
			daySummary.setDescription(
					messageSource.getMessage("message.label.total", null, Locale.getDefault()) + ModelConstants.COLAN);
			daySummary.setBf(dayTotalBf);
			daySummary.setLn(dayTotalLn);
			daySummary.setDn(dayTotalDn);
			daySummary.setSn(dayTotalSn);
			result.add(daySummary);
		}

		GeneralLedgerDto grandTotal = new GeneralLedgerDto();
		grandTotal.setSummaryRow(true);
		grandTotal.setSummaryType(
				messageSource.getMessage("message.label.grand", null, Locale.getDefault()).toLowerCase());
		grandTotal.setDescription(
				messageSource.getMessage("message.label.grand.total", null, Locale.getDefault()) + ModelConstants.COLAN);
		grandTotal.setBf(totalBf);
		grandTotal.setLn(totalLn);
		grandTotal.setDn(totalDn);
		grandTotal.setSn(totalSn);
		result.add(grandTotal);

		return result;
	}

	public Page<GeneralLedgerDto> getTempAccomList(Page<Object[]> entityList) {
		List<GeneralLedgerDto> result = getTempAccomWithSummary(entityList.getContent());
		return new PageImpl<>(result, entityList.getPageable(), Integer.MAX_VALUE);
	}

	public List<GeneralLedgerDto> getTempAccomReport(List<Object[]> entityList) {
		return getTempAccomWithSummary(entityList);
	}

	public List<GeneralLedgerDto> getTempAccomWithSummary(List<Object[]> entityList) {
		List<GeneralLedgerDto> result = new ArrayList<>();
		Map<LocalDate, List<GeneralLedgerDto>> groupedByDate = new LinkedHashMap<>();

		int totalBf = 0;
		int totalLn = 0;
		int totalDn = 0;
		int totalPayableCount = 0;

		for (Object[] record : entityList) {
			GeneralLedgerDto dto = new GeneralLedgerDto();
			LocalDate date = record[1] != null ? LocalDate.parse(record[1].toString()) : null;

			dto.setVoucherDate(date);
			dto.setRefAccount(ValidationCommon.getStringValueOrHyphen(record[3]));
			dto.setStudentName(ValidationCommon.getStringValueOrHyphen(record[2]));
			dto.setMessName(ValidationCommon.getStringValueOrHyphen(record[8]));

			int bf = record[4] != null ? Integer.parseInt(record[4].toString()) : 0;
			int ln = record[5] != null ? Integer.parseInt(record[5].toString()) : 0;
			int dn = record[6] != null ? Integer.parseInt(record[6].toString()) : 0;
			int payable = record[7] != null && Integer.parseInt(record[7].toString()) > 0 ? 1 : 0;

			dto.setBf(bf);
			dto.setLn(ln);
			dto.setDn(dn);
			dto.setPayableCount(payable);

			totalBf += bf;
			totalLn += ln;
			totalDn += dn;
			totalPayableCount += payable;

			if (date != null) {
				groupedByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(dto);
			}
		}

		for (Map.Entry<LocalDate, List<GeneralLedgerDto>> entry : groupedByDate.entrySet()) {
			List<GeneralLedgerDto> dayList = entry.getValue();
			result.addAll(dayList);

			int dayTotalBf = dayList.stream().mapToInt(d -> d.getBf() != null ? d.getBf() : 0).sum();
			int dayTotalLn = dayList.stream().mapToInt(d -> d.getLn() != null ? d.getLn() : 0).sum();
			int dayTotalDn = dayList.stream().mapToInt(d -> d.getDn() != null ? d.getDn() : 0).sum();
			int dayPayableCount = dayList.stream().mapToInt(d -> d.getPayableCount() != null ? d.getPayableCount() : 0)
					.sum();

			GeneralLedgerDto daySummary = new GeneralLedgerDto();
			daySummary.setSummaryRow(true);
			daySummary.setDescription(
					messageSource.getMessage("message.label.total", null, Locale.getDefault()) + ModelConstants.COLAN);
			daySummary.setBf(dayTotalBf);
			daySummary.setLn(dayTotalLn);
			daySummary.setDn(dayTotalDn);
			daySummary.setPayableCount(dayPayableCount);
			result.add(daySummary);
		}

		GeneralLedgerDto grandTotal = new GeneralLedgerDto();
		grandTotal.setSummaryRow(true);
		grandTotal.setSummaryType(
				messageSource.getMessage("message.label.grand", null, Locale.getDefault()).toLowerCase());
		grandTotal.setDescription(
				messageSource.getMessage("message.label.grand.total", null, Locale.getDefault()) + ModelConstants.COLAN);
		grandTotal.setBf(totalBf);
		grandTotal.setLn(totalLn);
		grandTotal.setDn(totalDn);
		grandTotal.setPayableCount(totalPayableCount);
		result.add(grandTotal);
		return result;
	}

	public Page<GeneralLedgerDto> getMessCardList(Page<Object[]> entityList, double openingBalance) {
		List<GeneralLedgerDto> result = processMessCard(entityList.getContent(), openingBalance);
		return new PageImpl<>(result, entityList.getPageable(), Integer.MAX_VALUE);
	}

	public List<GeneralLedgerDto> getMessCardReport(List<Object[]> entityList, double openingBalance) {
		return processMessCard(entityList, openingBalance);
	}

	private List<GeneralLedgerDto> processMessCard(List<Object[]> entityList, double openingBalance) {
		List<GeneralLedgerDto> result = new ArrayList<>();
		Map<LocalDate, List<GeneralLedgerDto>> groupedByDate = new LinkedHashMap<>();
		double runningClosingBalance = openingBalance;

		for (Object[] record : entityList) {
			String crOrDr = String.valueOf(record[1]);
			Double amount = record[2] != null ? Double.valueOf(record[2].toString()) : 0.00;

			GeneralLedgerDto dto = new GeneralLedgerDto();
			LocalDate date = record[4] != null ? LocalDate.parse(record[4].toString()) : null;

			dto.setVoucherDate(date);
			dto.setVoucherNo(ValidationCommon.getStringValueOrHyphen(record[5]));
			dto.setDescription(ValidationCommon.getStringValueOrHyphen(record[3]));
			dto.setHostelName(ValidationCommon.getStringValueOrHyphen(record[10]));
			dto.setRoomNo(ValidationCommon.getStringValueOrHyphen(record[9]));
			dto.setRefAccount(ValidationCommon.getStringValueOrHyphen(record[6]));
			dto.setStudentName(ValidationCommon.getStringValueOrHyphen(record[11]));
			dto.setRefSubAccount(ValidationCommon.getStringValueOrHyphen(record[7]));

			if (Constants.DEBIT.equals(crOrDr)) {
				dto.setDebit(utility.formatCommaSeperatedCurrency(amount));
				dto.setCredit(null);
				runningClosingBalance -= amount;
			} else if (Constants.CREDIT.equals(crOrDr)) {
				dto.setCredit(utility.formatCommaSeperatedCurrency(amount));
				dto.setDebit(null);
				runningClosingBalance += amount;
			}

			dto.setClosingBal(runningClosingBalance < 0
					? utility.formatCommaSeperatedCurrency(runningClosingBalance) + ModelConstants.SPACE
							+ messageSource.getMessage("message.label.dr", null, Locale.getDefault())
					: utility.formatCommaSeperatedCurrency(runningClosingBalance) + ModelConstants.SPACE
							+ messageSource.getMessage("message.label.cr", null, Locale.getDefault()));

			if (date != null) {
				groupedByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(dto);
			}
		}

		double grandTotalDebit = 0.00;
		double grandTotalCredit = 0.00;

		for (Map.Entry<LocalDate, List<GeneralLedgerDto>> entry : groupedByDate.entrySet()) {
			List<GeneralLedgerDto> dayList = entry.getValue();
			result.addAll(dayList);

			double totalDebit = dayList.stream().mapToDouble(d -> {
				String debitStr = d.getDebit();
				if (debitStr != null) {
					try {
						return Double.valueOf(debitStr.replace(",", ""));
					} catch (NumberFormatException e) {
						return 0.00;
					}
				}
				return 0.00;
			}).sum();

			double totalCredit = dayList.stream().mapToDouble(d -> {
				String creditStr = d.getCredit();
				if (creditStr != null) {
					try {
						return Double.valueOf(creditStr.replace(",", ""));
					} catch (NumberFormatException e) {
						return 0.00;
					}
				}
				return 0.00;
			}).sum();
			double dayClosingBal = (dayList.get(dayList.size() - 1).getClosingBal().contains(messageSource.getMessage("message.label.dr", null, Locale.getDefault())) ? -1 : 1) * Math
					.abs(Double.valueOf(dayList.get(dayList.size() - 1).getClosingBal().replaceAll("[^\\d.\\-]", "")));

			GeneralLedgerDto daySummary = new GeneralLedgerDto();
			daySummary.setSummaryRow(true);
			daySummary.setDescription(
					messageSource.getMessage("message.label.total", null, Locale.getDefault()) + ModelConstants.COLAN);
			daySummary.setTotalDebitForTheDay(utility.formatCommaSeperatedCurrency(totalDebit));
			daySummary.setTotalCreditForTheDay(utility.formatCommaSeperatedCurrency(totalCredit));
			daySummary.setClosingBalanceForTheDay(dayClosingBal < 0
					? utility.formatCommaSeperatedCurrency(dayClosingBal) + ModelConstants.SPACE
							+ messageSource.getMessage("message.label.dr", null, Locale.getDefault())
					: utility.formatCommaSeperatedCurrency(dayClosingBal) + ModelConstants.SPACE
							+ messageSource.getMessage("message.label.cr", null, Locale.getDefault()));
			result.add(daySummary);

			grandTotalDebit += totalDebit;
			grandTotalCredit += totalCredit;
		}

		GeneralLedgerDto grandSummary = new GeneralLedgerDto();
		grandSummary.setSummaryRow(true);
		grandSummary.setSummaryType(
				messageSource.getMessage("message.label.grand", null, Locale.getDefault()).toLowerCase());
		grandSummary.setDescription(
				messageSource.getMessage("message.label.grand.total", null, Locale.getDefault()) + ModelConstants.COLAN);
		grandSummary.setTotalDebitForTheDay(utility.formatCommaSeperatedCurrency(grandTotalDebit));
		grandSummary.setTotalCreditForTheDay(utility.formatCommaSeperatedCurrency(grandTotalCredit));
		grandSummary.setClosingBalanceForTheDay(runningClosingBalance < 0
				? utility.formatCommaSeperatedCurrency(runningClosingBalance) + ModelConstants.SPACE
						+ messageSource.getMessage("message.label.dr", null, Locale.getDefault())
				: utility.formatCommaSeperatedCurrency(runningClosingBalance) + ModelConstants.SPACE
						+ messageSource.getMessage("message.label.cr", null, Locale.getDefault()));
		result.add(grandSummary);

		return result;
	}

	public Double getOpeningBalance(PaginationForm form, GeneralLedgerDto filter) throws Exception {
		AccountHeadEntity accHeadEntity = accHeadRepository.findByIdAccheadAndIdFinYearAndActiveFlag(
				ValidationCommon.isValid(filter.getAccHead()) ? filter.getAccHead().trim() : null,
				accHeadService.getFinYear(), ModelConstants.STATUS_ACTIVE);
		if (accHeadEntity != null) {
			AccountHeadDto dto = AccountHeadMapper.INSTANCE.toDto(accHeadEntity);
			return dto.getOpbal();
		}
		return null;
	}

	public Page<Object[]> fetchGeneralLedgerData(PaginationForm form, GeneralLedgerDto filter) {
		return switch (filter.getBookType()) {
		case Constants.MESS_MS, Constants.CREDIT_CARD -> getMessCardData(form, filter);
		case Constants.TEMPORARY_ACCOMMODATION -> getTempAccomData(form, filter);
		case Constants.GUEST_COUPON -> getGuestCouponData(form, filter);
		default -> Page.empty();
		};
	}

	public List<Object[]> fetchGeneralLedgerDataReport(GeneralLedgerDto filter) {
		return switch (filter.getBookType()) {
		case Constants.MESS_MS, Constants.CREDIT_CARD -> getMessCardDataReport(filter);
		case Constants.TEMPORARY_ACCOMMODATION -> getTempAccomDataReport(filter);
		case Constants.GUEST_COUPON -> getGuestCouponDataReport(filter);
		default -> null;
		};
	}

	private Page<Object[]> getMessCardData(PaginationForm form, GeneralLedgerDto filter) {
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, Integer.MAX_VALUE);
		if (filter.getHostelId() != null && filter.getHostelId() != 0) {
			if (filter.getHostelId() == -2) {
				return accHeadRepository.getMessCardDataByDS(filter.getFromDate(), filter.getToDate(),
						ValidationCommon.isValid(filter.getAccHead()) ? filter.getAccHead().trim() : null,
						ValidationCommon.isValid(filter.getBookType()) ? filter.getBookType().trim() : null,
						accHeadService.getFinYear(), pageable);
			} else {
				return accHeadRepository.getMessCardDataByFC(filter.getFromDate(), filter.getToDate(),
						ValidationCommon.isValid(filter.getAccHead()) ? filter.getAccHead().trim() : null,
						ValidationCommon.isValid(filter.getBookType()) ? filter.getBookType().trim() : null,
						String.valueOf(filter.getHostelId()), accHeadService.getFinYear(), pageable);
			}
		} else {
			return accHeadRepository.getMessCardData(filter.getFromDate(), filter.getToDate(),
					ValidationCommon.isValid(filter.getAccHead()) ? filter.getAccHead().trim() : null,
					ValidationCommon.isValid(filter.getBookType()) ? filter.getBookType().trim() : null,
					accHeadService.getFinYear(), pageable);
		}
	}

	private List<Object[]> getMessCardDataReport(GeneralLedgerDto filter) {
		if (filter.getHostelId() != null && filter.getHostelId() != 0) {
			if (filter.getHostelId() == -2) {
				return accHeadRepository.getMessCardDataByDS(filter.getFromDate(), filter.getToDate(),
						ValidationCommon.isValid(filter.getAccHead()) ? filter.getAccHead().trim() : null,
						ValidationCommon.isValid(filter.getBookType()) ? filter.getBookType().trim() : null,
						accHeadService.getFinYear());
			} else {
				return accHeadRepository.getMessCardDataByFC(filter.getFromDate(), filter.getToDate(),
						ValidationCommon.isValid(filter.getAccHead()) ? filter.getAccHead().trim() : null,
						ValidationCommon.isValid(filter.getBookType()) ? filter.getBookType().trim() : null,
						String.valueOf(filter.getHostelId()), accHeadService.getFinYear());
			}
		} else {
			return accHeadRepository.getMessCardData(filter.getFromDate(), filter.getToDate(),
					ValidationCommon.isValid(filter.getAccHead()) ? filter.getAccHead().trim() : null,
					ValidationCommon.isValid(filter.getBookType()) ? filter.getBookType().trim() : null,
					accHeadService.getFinYear());
		}
	}

	private Page<Object[]> getTempAccomData(PaginationForm form, GeneralLedgerDto filter) {
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, Integer.MAX_VALUE);
		return accHeadRepository.getTempAccomData(filter.getFromDate(), filter.getToDate(),
				ValidationCommon.isValid(filter.getAccHead()) ? filter.getAccHead().trim() : null, pageable);
	}

	private List<Object[]> getTempAccomDataReport(GeneralLedgerDto filter) {
		return accHeadRepository.getTempAccomData(filter.getFromDate(), filter.getToDate(),
				ValidationCommon.isValid(filter.getAccHead()) ? filter.getAccHead().trim() : null);
	}

	private Page<Object[]> getGuestCouponData(PaginationForm form, GeneralLedgerDto filter) {
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, Integer.MAX_VALUE);
		return accHeadRepository.getGuestCouponData(filter.getFromDate(), filter.getToDate(),
				ValidationCommon.isValid(filter.getAccHead()) ? filter.getAccHead().trim() : null, pageable);
	}

	private List<Object[]> getGuestCouponDataReport(GeneralLedgerDto filter) {
		return accHeadRepository.getGuestCouponData(filter.getFromDate(), filter.getToDate(),
				ValidationCommon.isValid(filter.getAccHead()) ? filter.getAccHead().trim() : null);
	}

	public GeneralLedgerDto getFilterData(PaginationForm form) {
		GeneralLedgerDto filter = new GeneralLedgerDto();
		filter.setHostelId(ValidationCommon.toLongOrZero(form.getAdditionalParam().get("hostelId")));
		filter.setAccHead(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("accHead")));
		filter.setBookType(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("bookType")));
		filter.setFromDate(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("fromDate")));
		filter.setToDate(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("toDate")));
		return filter;
	}

	public Workbook downloadGeneralLedgerReport(GeneralLedgerDto filter, Double opBal,
			String openingBal) throws Exception {
		String glReport = messageSource.getMessage("message.label.general.ledger.report", null, Locale.getDefault());
		String sheetName = glReport;
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);
		int columnCount;
		String[] headerData;
		String[] headerDataWidth;

		if (Constants.MESS_MS.equals(filter.getBookType()) || Constants.CREDIT_CARD.equals(filter.getBookType())) {
			columnCount = ExcelConstants.MESS_CARD_HEADER_DATA.length;
			headerData = ExcelConstants.MESS_CARD_HEADER_DATA;
			headerDataWidth = ExcelConstants.MESS_CARD_HEADER_DATA_WIDTH;
		} else if (Constants.TEMPORARY_ACCOMMODATION.equals(filter.getBookType())) {
			columnCount = ExcelConstants.TEMP_ACCOMMODATION_HEADER_DATA.length;
			headerData = ExcelConstants.TEMP_ACCOMMODATION_HEADER_DATA;
			headerDataWidth = ExcelConstants.TEMP_ACCOMMODATION_HEADER_DATA_WIDTH;
		} else {
			columnCount = ExcelConstants.GUEST_COUPON_HEADER_DATA.length;
			headerData = ExcelConstants.GUEST_COUPON_HEADER_DATA;
			headerDataWidth = ExcelConstants.GUEST_COUPON_HEADER_DATA_WIDTH;
		}

		XSSFCellStyle style3 = excelUtility.setDataStyle(workbook);
		XSSFCellStyle centerAlignStyle = excelUtility.setCenterAlignStyle(workbook);

		// Row 0: Title
		Row row0 = sheet.createRow(0);
		Cell cell0 = row0.createCell(0);
		cell0.setCellValue(messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null,
				Locale.getDefault()));
		cell0.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

		// Row 1: Report Title
		Row row1 = sheet.createRow(1);
		Cell cell1 = row1.createCell(0);
		cell1.setCellValue(glReport);
		cell1.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, columnCount - 1));

		// Row 2: Account Head, From Date, To Date
		Row row2 = sheet.createRow(2);
		row2.setHeightInPoints(20);
		CellStyle dataStyle = excelUtility.setCenterAlignStyle(workbook);

		// Account Head
		Cell accHeadCell = row2.createCell(0);
		String accName = accHeadService.getAccountHeadByAccHead(filter.getAccHead()).getAccname();
		accHeadCell.setCellValue("Account Head: " + accName);
		accHeadCell.setCellStyle(dataStyle);
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 2));

		// From Date
		Cell fromDateCell = row2.createCell(3);
		String fromDateStr = filter.getFromDate() != null ? DateUtility.formatDate(filter.getFromDate()) : "";
		fromDateCell.setCellValue("From Date: " + fromDateStr);
		fromDateCell.setCellStyle(dataStyle);
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 3, 4));

		// To Date
		Cell toDateCell = row2.createCell(5);
		String toDateStr = filter.getToDate() != null ? DateUtility.formatDate(filter.getToDate()) : "";
		toDateCell.setCellValue("To Date: " + toDateStr);
		toDateCell.setCellStyle(dataStyle);
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 5, 6));

		// Row 3: Type and Opening Balance (shifted up)
		Row row3 = sheet.createRow(3);

		Cell typeCell = row3.createCell(0);
		typeCell.setCellValue("Type: " + filter.getBookType());
		typeCell.setCellStyle(dataStyle);
		if (Constants.MESS_MS.equals(filter.getBookType()) || Constants.CREDIT_CARD.equals(filter.getBookType())) {
			Cell opBalCell = row3.createCell(8);
			opBalCell.setCellValue("Opening Balance: " + openingBal);
			opBalCell.setCellStyle(dataStyle);
		}
		sheet.addMergedRegion(new CellRangeAddress(3, 3, 8, 10));

		// Row 4: Header Row (shifted up)
		Row row4 = sheet.createRow(4);
		excelUtility.createHeader(row4, 0, headerData, workbook);

		// Apply bold style to columns 3 and 4 (index 2 and 3)
		CellStyle boldStyle = workbook.createCellStyle();
		Font boldFont = workbook.createFont();
		boldFont.setBold(true);
		boldStyle.setFont(boldFont);

		if (headerData.length > 2) {
			Cell cell = row4.getCell(2);
			if (cell != null)
				cell.setCellStyle(boldStyle);
		}
		if (headerData.length > 3) {
			Cell cell = row4.getCell(3);
			if (cell != null)
				cell.setCellStyle(boldStyle);
		}

		IntStream.range(0, headerData.length).forEach(i -> {
			int width = Integer.parseInt(headerDataWidth[i]);
			sheet.setColumnWidth(i, width);
		});

		List<Object[]> entityList = fetchGeneralLedgerDataReport(filter);
		if (Constants.MESS_MS.equals(filter.getBookType()) || Constants.CREDIT_CARD.equals(filter.getBookType())) {
			List<GeneralLedgerDto> list = getMessCardReport(entityList, opBal);
			AtomicInteger rowCount = new AtomicInteger(4);

			list.forEach(entry -> {
				Row row = sheet.createRow(rowCount.incrementAndGet());
				try {
					String[] values;

					if (!entry.isSummaryRow()) {
						values = new String[] { DateUtility.formatDate(entry.getVoucherDate()),
								getNonNullValue(entry.getVoucherNo()), getNonNullValue(entry.getDescription()),
								getNonNullValue(entry.getHostelName()), getNonNullValue(entry.getRoomNo()),
								getNonNullValue(entry.getRefAccount()), getNonNullValue(entry.getStudentName()),
								getNonNullValue(entry.getRefSubAccount()),
								entry.getDebit() != null ? utility.formatCommaSeperatedCurrency(
										Double.valueOf(entry.getDebit().replaceAll(",", ""))) : Strings.EMPTY,
								entry.getCredit() != null ? utility.formatCommaSeperatedCurrency(
										Double.valueOf(entry.getCredit().replaceAll(",", ""))) : Strings.EMPTY,
								entry.getClosingBal() != null
										? utility.formatCommaSeperatedCurrency(
												(entry.getClosingBal().contains(messageSource.getMessage("message.label.dr", null, Locale.getDefault())) ? -1 : 1)
														* Math.abs(Double.valueOf(
																entry.getClosingBal().replaceAll("[^\\d.\\-]", ""))))
												+ ((entry.getClosingBal().contains(messageSource.getMessage("message.label.dr", null, Locale.getDefault())) ? -1 : 1)
														* Math.abs(Double.valueOf(
																entry.getClosingBal().replaceAll("[^\\d.\\-]", ""))) < 0
																		? ModelConstants.SPACE
																				+ messageSource.getMessage(
																						"message.label.dr", null,
																						Locale.getDefault())
																		: ModelConstants.SPACE + messageSource
																				.getMessage("message.label.cr", null,
																						Locale.getDefault()))
										: Strings.EMPTY };
					} else {
						values = new String[columnCount];
						Arrays.fill(values, "");
						values[0] = Strings.EMPTY;
						values[7] = getNonNullValue(entry.getDescription());
						values[8] = entry.getTotalDebitForTheDay() != null
								? utility.formatCommaSeperatedCurrency(
										Double.valueOf(entry.getTotalDebitForTheDay().replaceAll(",", "")))
								: Strings.EMPTY;

						values[9] = entry.getTotalCreditForTheDay() != null
								? utility.formatCommaSeperatedCurrency(
										Double.valueOf(entry.getTotalCreditForTheDay().replaceAll(",", "")))
								: Strings.EMPTY;

						String closingStr = Strings.EMPTY;
						if (entry.getClosingBalanceForTheDay() != null) {
							String balStr = entry.getClosingBalanceForTheDay();
							double val = (balStr.contains(messageSource.getMessage("message.label.dr", null, Locale.getDefault())) ? -1 : 1)
									* Math.abs(Double.valueOf(balStr.replaceAll("[^\\d.\\-]", "")));
							closingStr = utility.formatCommaSeperatedCurrency(val) + (val < 0
									? ModelConstants.SPACE
											+ messageSource.getMessage("message.label.dr", null, Locale.getDefault())
									: ModelConstants.SPACE
											+ messageSource.getMessage("message.label.cr", null, Locale.getDefault()));
						}
						values[10] = closingStr;
					}

					fillRow(row, values, sheet, style3);

				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} else if (Constants.TEMPORARY_ACCOMMODATION.equals(filter.getBookType())) {
			List<GeneralLedgerDto> list = getTempAccomWithSummary(entityList);
			AtomicInteger rowCount = new AtomicInteger(4);

			list.forEach(entry -> {
				Row row = sheet.createRow(rowCount.incrementAndGet());
				try {
					String[] values;

					if (!entry.isSummaryRow()) {
						values = new String[] { DateUtility.formatDate(entry.getVoucherDate()),
								getNonNullValue(entry.getStudentName()), getNonNullValue(entry.getMessName()),
								getNonNullValue(entry.getRefAccount()),
								entry.getBf() != null ? entry.getBf().toString() : "0",
								entry.getLn() != null ? entry.getLn().toString() : "0",
								entry.getDn() != null ? entry.getDn().toString() : "0",
								entry.getPayableCount() != null ? entry.getPayableCount().toString() : "0" };
					} else {
						values = new String[] { "", "", "", getNonNullValue(entry.getDescription()),
								entry.getBf() != null ? entry.getBf().toString() : "0",
								entry.getLn() != null ? entry.getLn().toString() : "0",
								entry.getDn() != null ? entry.getDn().toString() : "0",
								entry.getPayableCount() != null ? entry.getPayableCount().toString() : "0" };
					}

					fillRow(row, values, sheet, style3);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} else if (Constants.GUEST_COUPON.equals(filter.getBookType())) {
			List<GeneralLedgerDto> list = getGuestCouponWithSummary(entityList);
			AtomicInteger rowCount = new AtomicInteger(4);

			list.forEach(entry -> {
				Row row = sheet.createRow(rowCount.incrementAndGet());
				try {
					String[] values;

					if (entry.isSummaryRow()) {
						values = new String[] { Strings.EMPTY, Strings.EMPTY, Strings.EMPTY,
								entry.getDescription() != null ? entry.getDescription() : Strings.EMPTY,
								String.valueOf(entry.getBf() != null ? entry.getBf() : 0),
								String.valueOf(entry.getLn() != null ? entry.getLn() : 0),
								String.valueOf(entry.getDn() != null ? entry.getDn() : 0),
								String.valueOf(entry.getSn() != null ? entry.getSn() : 0) };
					} else {
						values = new String[] {
								entry.getVoucherDate() != null ? entry.getVoucherDate().toString() : Strings.EMPTY,
								entry.getStudentName() != null ? entry.getStudentName() : Strings.EMPTY,
								entry.getMessName() != null ? entry.getMessName() : Strings.EMPTY,
								entry.getRefAccount() != null ? entry.getRefAccount() : Strings.EMPTY,
								String.valueOf(entry.getBf() != null ? entry.getBf() : 0),
								String.valueOf(entry.getLn() != null ? entry.getLn() : 0),
								String.valueOf(entry.getDn() != null ? entry.getDn() : 0),
								String.valueOf(entry.getSn() != null ? entry.getSn() : 0) };
					}

					for (int i = 0; i < values.length; i++) {
						Cell cell = row.createCell(i);
						cell.setCellValue(values[i]);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
		for (int i = 0; i < columnCount; i++) {
			sheet.autoSizeColumn(i);
		}
		return workbook;
	}

	private void fillRow(Row row, String[] values, XSSFSheet sheet, XSSFCellStyle style) {
		for (int colIdx = 0; colIdx < values.length; colIdx++) {
			excelUtility.createAndSetColumn(sheet, row, colIdx, -1, values[colIdx], style);
		}
	}

	private String getNonNullValue(Object obj) {
		return (obj != null) ? String.valueOf(obj) : Strings.EMPTY;
	}

}
