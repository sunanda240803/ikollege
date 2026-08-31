package com.iitm.hosteldine.service.hostel;

import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.hostel.BulkAllotmentStudentsConstants;
import com.iitm.hosteldine.constant.hostel.HostelConstants;
import com.iitm.hosteldine.constant.mess.MessAllotmentConstants;
import com.iitm.hosteldine.controller.TransferAmountUtils;
import com.iitm.hosteldine.dto.financialYear.FinancialYearDto;
import com.iitm.hosteldine.dto.mess.MessAllottedListDTO;
import com.iitm.hosteldine.entity.mess.MessLedgerAEntity;
import com.iitm.hosteldine.entity.mess.MessLedgerBEntity;
import com.iitm.hosteldine.form.common.TransactionDto;
import com.iitm.hosteldine.repository.mess.MessLedgerARepository;
import com.iitm.hosteldine.repository.mess.MessLedgerBRepository;
import com.iitm.hosteldine.repository.student.StudentDetailsInfoRepository;
import com.iitm.hosteldine.service.AuditTrailService;
import com.iitm.hosteldine.service.BulkAsyncExecutor;
import com.iitm.hosteldine.service.InMemoryLogService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.HTMLPage;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import com.iitm.hosteldine.validator.common.ValidationConstants;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class StudentCreditDebitUploadService {

	private final ExcelUtility excelUtility;
	private final AccountHeadService accountHeadService;
	private final MessLedgerARepository messLedgerARepository;
	private final MessLedgerBRepository messLedgerBRepository;
	private final StudentDetailsInfoRepository studentDetailsInfoRepository;
    private final AuditTrailService auditTrailService;
    private final CommonResponseUtil commonResponseUtil;
	private final InMemoryLogService logService;
	private final BulkAsyncExecutor bulkAsyncExecutor;

	public Workbook downloadStudentCreditDebitTemplate() {
		XSSFWorkbook workbook = new XSSFWorkbook();
		String sheetName = ExcelConstants.STUDENT_CREDIT_DEBIT;
		String[] headerData = ExcelConstants.STUDENT_CREDIT_DEBIT_HEADER_DATA;
		String[] headerDataWidth = ExcelConstants.STUDENT_CREDIT_DEBIT_HEADER_DATA_WIDTH;

		XSSFSheet sheet = workbook.createSheet(sheetName);
		excelUtility.setDataStyle(workbook);
		Row headerRow = sheet.createRow(0);
		excelUtility.createHeader(headerRow, 0, headerData, workbook);

		IntStream.range(0, headerDataWidth.length).forEach(i -> {
			try {
				sheet.setColumnWidth(i, Integer.parseInt(headerDataWidth[i]));
			} catch (NumberFormatException e) {
				sheet.setColumnWidth(i, 4000);
			}
		});

		IntStream.range(0, headerData.length).forEach(sheet::autoSizeColumn);
		return workbook;
	}

	public TransactionDto startBulkUpload(TransactionDto messBilling) {
		addLog(messBilling.getLogTag(), 0, commonResponseUtil.getMessage("upload.started"));

		bulkAsyncExecutor.execute(messBilling.getLogTag(), () -> {
			try {
				saveStudentCreditDebitDetails(messBilling);
			} catch (Exception e) {
				addError(messBilling.getLogTag(), 0, commonResponseUtil.getMessage("upload.failed") + e.getMessage(),e);
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		});

		return messBilling;
	}

	@Transactional(rollbackFor = Exception.class)
	public TransactionDto saveStudentCreditDebitDetails(TransactionDto messBilling) throws Exception {
		TransactionDto messLedgerA = new TransactionDto();
		List<TransactionDto> messLedgerB = new ArrayList<>();
		TransactionDto messBillingDto = new TransactionDto();
		DataFormatter formatter = new DataFormatter();
		messBillingDto.setErrorList(new ArrayList<>());
		ArrayList<String> prevIdList = new ArrayList<>();
		Double totalAmount = 0.0;
		FinancialYearDto finYearDto = accountHeadService.getFinYearDto();
		int studentCount = 0;
		String tag = messBilling.getLogTag();
		addLog(tag, 0, commonResponseUtil.getMessage("upload.validation.start"));

		try (InputStream is = new ByteArrayInputStream(messBilling.getFileBytes());
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
			boolean isAmount = cellValues.contains(HostelConstants.AMOUNT.getConstants() + ModelConstants.SPACE + ModelConstants.ASTERISK);
			boolean isDescription = cellValues.contains(MessAllotmentConstants.DESCRIPTION.getString());
			if (isStudentId && isAmount && isDescription) {
				int rowCount = ExcelUtility.countNonEmptyRows(sheet);
				boolean hasValidData = false;
				for (Row row : sheet) {
					if (rowCount > rowNumber) {
						if (rowNumber == 0) { // Skip header row
							rowNumber++;
							continue;
						}

						addLog(tag, 0, commonResponseUtil.getMessage("upload.validating.row")+" " +(rowNumber + 1));

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
//
//								if (prevIdList.contains(formattedStudentId)) {
//									handleCellError(BulkAllotmentStudentsConstants.STUDENT_ID.getString()
//											+ ModelConstants.SPACE + formattedStudentId + ModelConstants.SPACE
//											+ BulkAllotmentStudentsConstants.SHOULD_NOT_REPEAT_WITHIN_SAME_EXCEL.getString(),
//											column, rowNumber, dto, errorDetails);
//									error = true;
//								} else {
//									prevIdList.add(formattedStudentId);
//								}

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
						
						// Amount Validation
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							Cell amountCell = row.getCell(column);
							String amountStr = "";

							if (amountCell != null) {
							    if (amountCell.getCellType() == CellType.STRING) {
							        amountStr = amountCell.getStringCellValue().trim();
							    } else if (amountCell.getCellType() == CellType.NUMERIC) {
							        amountStr = String.valueOf(amountCell.getNumericCellValue());
							    } else if (amountCell.getCellType() == CellType.FORMULA) {
							        // Evaluate the formula and convert to string
							        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
							        CellValue cellValue = evaluator.evaluate(amountCell);
							        if (cellValue.getCellType() == CellType.NUMERIC) {
							            amountStr = String.valueOf(cellValue.getNumberValue());
							        } else if (cellValue.getCellType() == CellType.STRING) {
							            amountStr = cellValue.getStringValue().trim();
							        }
							    }
							}

						    try {
					            Double amountValue = Double.parseDouble(amountStr);
						        // Regex format validation
						        if (!amountStr.matches(ValidationConstants.AMOUNT_PATTERN)) {
						            handleCellError(HostelConstants.AMOUNT_VALUE_LENGTH_VALIDATION.getConstants()
						                + ModelConstants.SPACE + amountStr, column, rowNumber, dto, errorDetails
						            );
						            error = true;
						        } else {
						            // Check if value is 0
						            if (amountValue <= 0.0) {
						                handleCellError(HostelConstants.AMOUNT_SHOULD_BE_GREATER_THAN_ZERO.getConstants()
						                    + ModelConstants.SPACE + amountStr, column, rowNumber, dto, errorDetails
						                );
						                error = true;
						            } else {
						            	totalAmount += amountValue;
						            	dto.setAmount(amountValue);
						            	studentCount++;
						            }
						        }
						    } catch (NumberFormatException e) {
						        handleCellError(HostelConstants.AMOUNT_INCORRECT_FORMAT.getConstants()
						            + ModelConstants.SPACE + amountStr, column, rowNumber, dto, errorDetails
						        );
						        error = true;
						    }
						} else {
						    handleCellError(HostelConstants.AMOUNT.getConstants() + ModelConstants.SPACE
						        + BulkAllotmentStudentsConstants.SHOULD_NOT_BE_EMPTY.getString(),
						        column, rowNumber, dto, errorDetails
						    );
						    error = true;
						}
						column++;

						// Description
						if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
							Cell descriptionCell = row.getCell(column);
							String cellValue = descriptionCell.getStringCellValue().trim();
							dto.setDescription(cellValue);
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
				messBillingDto.setVoucherDate(messBilling.getVoucherDate());
				messBillingDto.setDebitOrCredit(messBilling.getDebitOrCredit());
				messBillingDto.setAccHead(messBilling.getAccHead());
				messBillingDto.setFcno(messBilling.getFcno());
				messBillingDto.setBookType(messBilling.getBookType());
				messBillingDto.setDescription(messBilling.getDescription());

				addValidation(tag, 0, commonResponseUtil.getMessage("upload.validation.errors"));
				messBillingDto.getErrorList().forEach(err -> addValidation(tag, 0, err));

				return messBillingDto;
			}
			String voucherNo = String.valueOf(messLedgerARepository.getNextValMessLedger());
			messLedgerA.setBookType(messBilling.getBookType());
			messLedgerA.setVoucherNo(voucherNo);
			messLedgerA.setDate(messBilling.getDate());
			messLedgerA.setAccHead(messBilling.getAccHead().toUpperCase());
			messLedgerA.setSubAccHead(Constants.NIL); 
			messLedgerA.setDescription(messBilling.getDescription());
			messLedgerA.setAmount(totalAmount);
			messLedgerA.setRecon(ModelConstants.STATUS_ACTIVE);
			messLedgerA.setCancelStatus(ModelConstants.STATUS_INACTIVE);
			messLedgerA.setFcno(messBilling.getFcno());
			messLedgerA.setDebitOrCredit(messBilling.getDebitOrCredit().equals(Constants.CREDIT)? Constants.DEBIT : Constants.CREDIT);
			messLedgerA.setScreenType(Constants.SCREEN_TYPE_STUDENT_DEBIT);
			messLedgerA.setStudentCount(studentCount);
			MessLedgerAEntity messLedgerAEntity = TransferAmountUtils.createMessLedgerAEntity(messLedgerA, finYearDto.getFinYear());
			messLedgerARepository.save(messLedgerAEntity);
			
			if (!messLedgerB.isEmpty()) {
				List<MessLedgerBEntity> messLedgerBEntityList = new ArrayList<>();
				int siNo = 1;
				for (TransactionDto dto : messLedgerB) {
					TransactionDto messBillDto = new TransactionDto();
					messBillDto.setBookType(messBilling.getBookType());
					messBillDto.setVoucherNo(voucherNo);
					messBillDto.setDate(messBilling.getDate());
					messBillDto.setAccHead(dto.getStudentId().toUpperCase());
					messBillDto.setSubAccHead(Constants.NIL);
					messBillDto.setDescription(messBilling.getDescription() + ModelConstants.SPACE + Constants.HYPHEN + ModelConstants.SPACE + dto.getDescription());
					messBillDto.setAmount(dto.getAmount());
					messBillDto.setRecon(ModelConstants.STATUS_ACTIVE);
					messBillDto.setCancelStatus(ModelConstants.STATUS_INACTIVE);
					messBillDto.setFcno(messBilling.getFcno());
					messBillDto.setDebitOrCredit(messBilling.getDebitOrCredit());
					messBillDto.setTdsValue(0);
					messBillDto.setAdvAmount(0);
					messBillDto.setBillAmount(0);
					MessLedgerBEntity messLedgerBEntity = TransferAmountUtils.createMessLedgerBEntity(messBillDto, finYearDto.getFinYear(), 0, siNo);
					siNo++;
					addLog(tag, 0, commonResponseUtil.getMessage("upload.inserting.student")+dto.getStudentId().toUpperCase());
					messLedgerBEntityList.add(messLedgerBEntity);
				}
				messLedgerBRepository.saveAll(messLedgerBEntityList);

				messBillingDto.setAmount(totalAmount);
				messBillingDto.setStudentCount(studentCount);

				addLog(tag, 0, commonResponseUtil.getMessage("upload.students.count")+" "+studentCount);
				addLog(tag, 0, commonResponseUtil.getMessage("upload.total.amount")+" "+totalAmount);
				addLog(tag, 0, commonResponseUtil.getMessage("upload.wait.response"));
				addLog(tag, 0, commonResponseUtil.getMessage("upload.success"));
			}
			auditTrailService.saveAuditTrail(HTMLPage.STUDENT_CREDIT_DEBIT_UPLOAD,
					commonResponseUtil.getMessage("url.student.credit.debit.upload"), this.getClass().getName()
							+ Constants.HYPHEN + Thread.currentThread().getStackTrace()[1].getMethodName());

		} catch (Exception e) {
			addError(tag, 0, commonResponseUtil.getMessage("upload.failed") + e.getMessage(),e);
			e.printStackTrace();
			throw new Exception(e);
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

	private void addLog(String tag, long sessionId, String msg) {
		logService.addLog(tag, " <-" + sessionId + "-> " + msg);
	}

	private void addError(String tag, long sessionId, String msg,Exception e) {
		logService.addError(tag, " <-" + sessionId + "-> " + msg,e);
	}

	private void addValidation(String tag, long sessionId, String msg) {
		logService.addValidation(tag, " <-" + sessionId + "-> " + msg);
	}
}
