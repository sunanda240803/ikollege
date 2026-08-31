package com.iitm.hosteldine.service.hostel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.hostel.BulkAllotmentStudentsConstants;
import com.iitm.hosteldine.constant.mess.MessAllotmentConstants;
import com.iitm.hosteldine.controller.TransferAmountUtils;
import com.iitm.hosteldine.dto.financialYear.FinancialYearDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.hostel.StudentDebitAccheadConfigDto;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntity;
import com.iitm.hosteldine.form.common.TransactionDto;
import com.iitm.hosteldine.model.hostel.HostelMasterEntity;
import com.iitm.hosteldine.repository.hostel.HostelMasterRepository;
import com.iitm.hosteldine.repository.hostel.StudentDebitAccheadConfigRepository;
import com.iitm.hosteldine.repository.mess.MessLedgerARepository;
import com.iitm.hosteldine.repository.mess.MessLedgerBRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.service.AuditTrailService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudentDemandUploadService {

	private final ExcelUtility excelUtility;
    private final AuditTrailService auditTrailService;
    private final CommonResponseUtil commonResponseUtil;
	private final AccountHeadService accountHeadService;
	private final HostelMasterService hostelMasterService;
	private final HostelMasterRepository hostelMasterRepository;
	private final MessLedgerARepository messLedgerARepository;
	private final MessLedgerBRepository messLedgerBRepository;
	private final StudentDetailsInfoRepository studentDetailsInfoRepository;
	private final StudentDebitAccheadConfigRepository studentDebitAccheadConfigRepository;

	public Workbook downloadStudentDemandTemplate() {
		XSSFWorkbook workbook = new XSSFWorkbook();
		String sheetName = ExcelConstants.STUDENT_DEMANDS;
		String[] headerData = ExcelConstants.STUDENT_DEMAND_HEADER_DATA;
		String[] headerDataWidth = ExcelConstants.STUDENT_DEMAND_HEADER_DATA_WIDTH;
		XSSFSheet sheet = workbook.createSheet(sheetName);
		int columnCount = 0;
		/**
		 * Data Style
		 */
		excelUtility.setDataStyle(workbook);
		Row row0 = sheet.createRow(0);
		/**
		 * Set Header Lines
		 */
		excelUtility.createHeader(row0, columnCount, headerData, workbook);
		/**
		 * Set cell width
		 */
		IntStream.range(0, headerDataWidth.length).forEach(i -> {
			sheet.setColumnWidth(i, Integer.parseInt(headerDataWidth[i]));
		});

		// Create a hidden sheet for dropdown data
		XSSFSheet hiddenSheet = workbook.createSheet("DropdownData");
		workbook.setSheetHidden(workbook.getSheetIndex("DropdownData"), true);

		// Dropdown data
		List<String> hostelNames = hostelMasterService.getHostelList().stream().map(HostelMasterDto::getHostelName)
				.collect(Collectors.toList());

		// Add dropdown data to the hidden sheet
		excelUtility.addDropdownDataToHiddenSheet(hiddenSheet, 1, hostelNames);

		// Add dropdowns to the main sheet columns
		ExcelUtility.addDropdownToColumn(sheet, 1, 100, 1, "DropdownData!$B$1:$B$" + hostelNames.size()); // Hostel Name

		// Auto-size the columns
		IntStream.range(0, headerData.length).forEach(sheet::autoSizeColumn);
		return workbook;
	}

	public List<StudentDebitAccheadConfigDto> getAccheadInputs() {
		return Optional.ofNullable(studentDebitAccheadConfigRepository.findAllByActiveFlag(ModelConstants.STATUS_ACTIVE))
				.orElse(Collections.emptyList()).stream().map(config -> StudentDebitAccheadConfigDto.builder().acchead(config.getAcchead())
				.accheadName(config.getAccheadName()).creditOrDebit(config.getCreditOrDebit()).build()).collect(Collectors.toList());
	}

	@Transactional(rollbackFor = Exception.class)
	public TransactionDto saveStudentDemands(TransactionDto transactionDto) throws Exception {
		List<TransactionDto> messLedgerB = new ArrayList<>();
		TransactionDto messBillingDto = new TransactionDto();
		DataFormatter formatter = new DataFormatter();
		messBillingDto.setErrorList(new ArrayList<>());
		ArrayList<String> prevIdList = new ArrayList<>();

		try (InputStream is = transactionDto.getFile().getInputStream();
				Workbook workbook = new XSSFWorkbook(is)) {
			Sheet sheet = workbook.getSheetAt(0);
			int rowNumber = 0;

			Row firstRow = sheet.getRow(0);
			ArrayList<String> cellValues = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				Cell cell = firstRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				cellValues.add(formatter.formatCellValue(cell));
			}
			boolean isStudentId = cellValues.contains(MessAllotmentConstants.STUDENT_ID.getString() + ModelConstants.SPACE + ModelConstants.ASTERISK);
			boolean isHostelName = cellValues.contains(BulkAllotmentStudentsConstants.HOSTEL_NAME.getString());
			if (isStudentId && isHostelName) {
				int rowCount = ExcelUtility.countNonEmptyRows(sheet);
				boolean hasValidData = false;
				for (Row row : sheet) {
					if (rowCount > rowNumber) {
						if (rowNumber == 0) {
							rowNumber++;
							continue;
						}
						Integer column = 0;
						boolean error = false;
						TransactionDto dto = new TransactionDto();
						StringBuilder errorDetails = new StringBuilder();

						// Student ID Validation
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							Cell studentIdCell = row.getCell(column);
							String studentId = formatter.formatCellValue(studentIdCell).replace('\u00A0', ' ').trim();

							if (!studentId.matches(ModelConstants.ALPHA_NUMERIC_REGEX)) {
								handleCellError(BulkAllotmentStudentsConstants.ALPHANUMERIC_CHARACTERS_LONG.getString(),
										column, rowNumber, dto, errorDetails);
								error = true;
							} else {
								String formattedStudentId = studentId.toUpperCase();

								if (prevIdList.contains(formattedStudentId)) {
									handleCellError(BulkAllotmentStudentsConstants.STUDENT_ID.getString()
											+ ModelConstants.SPACE + formattedStudentId + ModelConstants.SPACE
											+ BulkAllotmentStudentsConstants.SHOULD_NOT_REPEAT_WITHIN_SAME_EXCEL.getString(),
											column, rowNumber, dto, errorDetails);
									error = true;
								} else {
									prevIdList.add(formattedStudentId);
								}

								if (!studentDetailsInfoRepository.checkStudentIdExistInPreviousId(studentId, ModelConstants.STATUS_ACTIVE).isEmpty()) {
									handleCellError(BulkAllotmentStudentsConstants.STUDENT_ID_CHANGED.getString() + studentId,
											column, rowNumber, dto, errorDetails);
									error = true;
								}

								if (studentDetailsInfoRepository.checkStudentIdExist(studentId, ModelConstants.STATUS_ACTIVE).isEmpty()) {
									handleCellError(BulkAllotmentStudentsConstants.STUDENT_ID.getString() + ModelConstants.SPACE
											+ studentId + BulkAllotmentStudentsConstants.DOES_NOT_EXIT.getString(),
											column, rowNumber, dto, errorDetails);
									error = true;
								}

								if (studentDetailsInfoRepository.existsByActiveFlagAndStudentIdAndSettlementFlag(
										ModelConstants.STATUS_ACTIVE, studentId, ModelConstants.YES)) {
									handleCellError(BulkAllotmentStudentsConstants.SETTLEMENT_COMPLETED_FOR_THE_STUDENT.getString()
											+ studentId, column, rowNumber, dto, errorDetails);
									error = true;
								}
								dto.setStudentId(formattedStudentId);
							}
						} else {
							handleCellError(MessAllotmentConstants.STUDENT_ID.getString() + ModelConstants.SPACE
											+ BulkAllotmentStudentsConstants.SHOULD_NOT_BE_EMPTY.getString(),
											column, rowNumber, dto, errorDetails);
							error = true;
						}
						column++;
						
						// Hostel Name Validation
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							Cell hostelNameCell = row.getCell(column);
							String cellValue = hostelNameCell.getStringCellValue().trim();

							HostelMasterEntity hostelInfo = hostelMasterRepository.checkHostelExist(cellValue,
									ModelConstants.STATUS_ACTIVE);

							if (hostelInfo != null) {
								dto.setFcno(String.valueOf(hostelInfo.getId()));
							} else {
								handleCellError(BulkAllotmentStudentsConstants.HOSTEL_NOT_FOUND.getString()
										+ ModelConstants.SPACE + cellValue, column, rowNumber, dto, errorDetails);
								error = true;
							}
						} else {
							handleCellError(BulkAllotmentStudentsConstants.HOSTEL_NAME.getString() + ModelConstants.SPACE
									+ BulkAllotmentStudentsConstants.SHOULD_NOT_BE_EMPTY.getString(), column, rowNumber, dto, errorDetails);
							error = true;
						}
						column++;
						
						if (error) {
							messBillingDto.getErrorList().add(ModelConstants.ROW + ModelConstants.SPACE + (rowNumber + 1)
								+ ModelConstants.COLAN + ModelConstants.SPACE + errorDetails.toString());
							hasValidData = true;
						} else {
							messLedgerB.add(dto);
							hasValidData = true;
						}
						rowNumber++;
					}
				}
				// If no valid data was processed, show an error message
				if (!hasValidData) {
					messBillingDto.getErrorList().add(BulkAllotmentStudentsConstants.EMPTY_DATA_IN_EXCEL_FILE.getString());
				}
			} else {
				messBillingDto.getErrorList().add(MessAllotmentConstants.INVALID_EXCEL_TEMPLATE.getString());
			}

			if (!messBillingDto.getErrorList().isEmpty()) {
				messBillingDto.setDate(transactionDto.getDate());
				messBillingDto.setDescription(transactionDto.getDescription());
				return messBillingDto;
			}

			FinancialYearDto finYearDto = accountHeadService.getFinYearDto();
			Map<String, Integer> estBHostelList = new HashMap<>();

			for (TransactionDto billing : messLedgerB) {
				String hostelName = billing.getFcno();
				boolean hasEstBCharges = false;

				for (StudentDebitAccheadConfigDto input : transactionDto.getAccheadInputs()) {
					if ("Establishment B Charges".equalsIgnoreCase(input.getAccheadName())
							&& input.getValue() != null) {
						hasEstBCharges = true;
						break;
					}
				}
				if (hasEstBCharges && hostelName != null && !hostelName.equalsIgnoreCase("null")) {
					estBHostelList.put(hostelName, estBHostelList.getOrDefault(hostelName, 0) + 1);
				}
			}

			int index = 0, siNo = 0;
			int studentListSize = prevIdList.size();
			double totalAmount = 0.0;
			String voucherNo = String.valueOf(messLedgerARepository.getNextValMessLedger());
			
			List<StudentDebitAccheadConfigDto> accheadList = transactionDto.getAccheadInputs();
			for (int i = 0; i < accheadList.size(); i++) {
				StudentDebitAccheadConfigDto input = accheadList.get(i);

				if (input.getValue() != null) {
					totalAmount += (input.getValue() * studentListSize);
					
					if ("Establishment B Charges".equalsIgnoreCase(input.getAccheadName())) {
						for (Map.Entry<String, Integer> entry : estBHostelList.entrySet()) {
							String hostelName = entry.getKey();
							int hostelCount = entry.getValue();
							double accHeadVsHostelTotalAmount = input.getValue() * hostelCount;
	
							String hostelId = hostelName;
							if (index == 0) {
								TransactionDto messBillDto = new TransactionDto();
								messBillDto.setBookType(Constants.MESS_MS);
								messBillDto.setVoucherNo(voucherNo);
								messBillDto.setDate(transactionDto.getDate());
								messBillDto.setAccHead(input.getAcchead().toUpperCase());
								messBillDto.setSubAccHead(Constants.NIL);
								messBillDto.setDescription(transactionDto.getDescription());
								messBillDto.setAmount(accHeadVsHostelTotalAmount);
								messBillDto.setRecon(ModelConstants.STATUS_INACTIVE);
								messBillDto.setCancelStatus(ModelConstants.STATUS_INACTIVE);
								messBillDto.setFcno(hostelId);
								messBillDto.setDebitOrCredit(Constants.CREDIT);
								messBillDto.setActiveFlag(ModelConstants.STATUS_ACTIVE);
								messBillDto.setScreenType(Constants.SCREEN_TYPE_STUDENT_DEMAND);
								messBillDto.setStudentCount(studentListSize);
								MessLedgerAEntity messLedgerAEntity = TransferAmountUtils
										.createMessLedgerAEntity(messBillDto, finYearDto.getFinYear());
								messLedgerARepository.save(messLedgerAEntity);
								index++;
							} else {
								TransactionDto messBillDto = new TransactionDto();
								messBillDto.setBookType(Constants.MESS_MS);
								messBillDto.setVoucherNo(voucherNo);
								messBillDto.setDate(transactionDto.getDate());
								messBillDto.setAccHead(input.getAcchead().toUpperCase());
								messBillDto.setSubAccHead(Constants.NIL);
								messBillDto.setDescription(transactionDto.getDescription());
								messBillDto.setAmount(accHeadVsHostelTotalAmount);
								messBillDto.setRecon(ModelConstants.STATUS_INACTIVE);
								messBillDto.setCancelStatus(ModelConstants.STATUS_INACTIVE);
								messBillDto.setFcno(hostelId);
								messBillDto.setDebitOrCredit(Constants.CREDIT);
								MessLedgerBEntity messLedgerBEntity = TransferAmountUtils
										.createMessLedgerBEntity(messBillDto, finYearDto.getFinYear(), 0, siNo);
								messLedgerBRepository.save(messLedgerBEntity);
								siNo++;
							}
						}
					} else {
						double accHeadtotalAmount = input.getValue() * studentListSize;
						if (index == 0) {
							TransactionDto messBillDto = new TransactionDto();
							messBillDto.setBookType(Constants.MESS_MS);
							messBillDto.setVoucherNo(voucherNo);
							messBillDto.setDate(transactionDto.getDate());
							messBillDto.setAccHead(input.getAcchead().toUpperCase());
							messBillDto.setSubAccHead(Constants.NIL);
							messBillDto.setDescription(transactionDto.getDescription());
							messBillDto.setAmount(accHeadtotalAmount);
							messBillDto.setRecon(ModelConstants.STATUS_INACTIVE);
							messBillDto.setCancelStatus(ModelConstants.STATUS_INACTIVE);
							messBillDto.setDebitOrCredit(Constants.CREDIT);
							messBillDto.setActiveFlag(ModelConstants.STATUS_ACTIVE);
							messBillDto.setScreenType(Constants.SCREEN_TYPE_STUDENT_DEMAND);
							messBillDto.setStudentCount(studentListSize);
							MessLedgerAEntity messLedgerAEntity = TransferAmountUtils
									.createMessLedgerAEntity(messBillDto, finYearDto.getFinYear());
							messLedgerARepository.save(messLedgerAEntity);
							index++;
						} else {
							TransactionDto messBillDto = new TransactionDto();
							messBillDto.setBookType(Constants.MESS_MS);
							messBillDto.setVoucherNo(voucherNo);
							messBillDto.setDate(transactionDto.getDate());
							messBillDto.setAccHead(input.getAcchead().toUpperCase());
							messBillDto.setSubAccHead(Constants.NIL);
							messBillDto.setDescription(transactionDto.getDescription());
							messBillDto.setAmount(accHeadtotalAmount);
							messBillDto.setRecon(ModelConstants.STATUS_INACTIVE);
							messBillDto.setCancelStatus(ModelConstants.STATUS_INACTIVE);
							messBillDto.setFcno("0");
							messBillDto.setDebitOrCredit(Constants.CREDIT);
							MessLedgerBEntity messLedgerBEntity = TransferAmountUtils
									.createMessLedgerBEntity(messBillDto, finYearDto.getFinYear(), 0, siNo);
							messLedgerBRepository.save(messLedgerBEntity);
							siNo++;
						}
					}
					if (!messLedgerB.isEmpty()) {
						List<MessLedgerBEntity> messLedgerBEntityList = new ArrayList<>();
						for (TransactionDto dto : messLedgerB) {
							TransactionDto messBillDto = new TransactionDto();
							messBillDto.setBookType(Constants.MESS_MS);
							messBillDto.setVoucherNo(voucherNo);
							messBillDto.setDate(transactionDto.getDate());
							messBillDto.setAccHead(dto.getStudentId().toUpperCase());
							messBillDto.setSubAccHead(Constants.NIL);
							messBillDto.setDescription(transactionDto.getDescription());
							messBillDto.setAmount(input.getValue());
							messBillDto.setRecon(ModelConstants.STATUS_INACTIVE);
							messBillDto.setCancelStatus(ModelConstants.STATUS_INACTIVE);
							messBillDto.setFcno(dto.getFcno());
							messBillDto.setDebitOrCredit(Constants.DEBIT);
							MessLedgerBEntity messLedgerBEntity = TransferAmountUtils.createMessLedgerBEntity(messBillDto,
									finYearDto.getFinYear(), 0, siNo);
							siNo++;
							messLedgerBEntityList.add(messLedgerBEntity);
						}
						messLedgerBRepository.saveAll(messLedgerBEntityList);
					}
				}
			}
			auditTrailService.saveAuditTrail(HTMLPage.STUDENT_DEMAND_UPLOAD,
					commonResponseUtil.getMessage("url.student.demand.upload"), this.getClass().getName()
							+ Constants.HYPHEN + Thread.currentThread().getStackTrace()[1].getMethodName());
			messBillingDto.setAmount(totalAmount);
			messBillingDto.setStudentCount(studentListSize);
		}
		return messBillingDto;
	}

	private void handleCellError(String errorMessage, int column, int rowNumber, TransactionDto dto,
			StringBuilder errorDetails) {
		cellError(errorMessage, column, rowNumber, dto);
		dto.setError("error");
		errorDetails.append(ModelConstants.HYPHEN).append(dto.getExcelErrorMsg()).append(ModelConstants.NEW_LINE);
	}

	private static void cellError(String msg, int column, int rowCount, TransactionDto object) {
		String[] columns = ModelConstants.EXCEL_COLUMNS;
		if (column == 0 && rowCount == 0)
			object.setExcelErrorMsg(msg);
		else
			object.setExcelErrorMsg(" Cell " + columns[column] + (rowCount + 1) + " : " + msg);
		object.setError("error");
	}
}
