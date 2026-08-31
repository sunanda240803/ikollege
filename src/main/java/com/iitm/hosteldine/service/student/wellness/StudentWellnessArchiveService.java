package com.iitm.hosteldine.service.student.wellness;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.DateUtility;
import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.SimsConfigDataJsonArrayDto;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.dto.student.wellness.StudentWellnessCategoricalDataDto;
import com.iitm.hosteldine.dto.student.wellness.StudentWellnessDto;
import com.iitm.hosteldine.dto.student.wellness.StudentWellnessFollowupDataDto;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.student.AllStudentsDetailsViewMapper;
import com.iitm.hosteldine.mapper.student.wellness.StudentWellnessCategoricalDataMapper;
import com.iitm.hosteldine.mapper.student.wellness.StudentWellnessFollowupDataMapper;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.model.student.wellness.StudentWellnessCategoricalDataEntity;
import com.iitm.hosteldine.model.student.wellness.StudentWellnessFollowupDataEntity;
import com.iitm.hosteldine.repository.student.wellness.DostElectionDepartmentRepository;
import com.iitm.hosteldine.repository.student.wellness.StudentWellnessCategoricalDataRepository;
import com.iitm.hosteldine.repository.student.wellness.StudentWellnessFollowupDataRepository;
import com.iitm.hosteldine.repository.student.wellness.WellnessUserManagementRepository;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.PdfActionService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.MCrypt;
import com.iitm.hosteldine.util.WatermarkEventHandler;
import com.iitm.hosteldine.validator.common.ValidationCommon;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class StudentWellnessArchiveService {

	private final MessageSource messageSource;
	private final ExcelUtility excelUtility;
	private final FileService fileService;
	private final PdfActionService pdfActiveService;
	private final SimsConfigDataService simsConfigDataService;
	private final StudentWellnessCategoricalDataRepository wellnessCategoricalDataRepository;
	private final DostElectionDepartmentRepository dostElectionDepartmentRepository;
	private final WellnessUserManagementRepository wellnessUserManagementRepository;
	private final StudentWellnessFollowupDataRepository studentWellnessFollowupDataRepository;
	private final StudentBioDataService studentBioDataService;

	public StudentWellnessCategoricalDataDto getWellnessDetailsByStudentId(String studentId) throws Exception {
		StudentWellnessCategoricalDataEntity wellnessEntity = wellnessCategoricalDataRepository
				.getWellnessEntityByStudentIdAndActiveFlag(studentId, ModelConstants.STATUS_ACTIVE);
		if (wellnessEntity != null) {
			return StudentWellnessCategoricalDataMapper.INSTANCE
					.fromStudentWellnessCategoricalDataEntity(wellnessEntity);
		}
		return null;
	}

	public List<String> getDepartmentCodeList() throws Exception {
		return dostElectionDepartmentRepository.getDeptCodeList(ModelConstants.STATUS_ACTIVE);
	}

	public List<StudentWellnessCategoricalDataDto> getStudentWellnessList(PaginationForm form) throws Exception {
		List<Object[]> entityList = fetchStudentWellnessData(form);
		return entityList.stream().map(
				record-> {
					try {
						StudentWellnessCategoricalDataDto dto = new StudentWellnessCategoricalDataDto();
						dto.setStudentId(ValidationCommon.getStringValueOrHyphen(record[0]));
						dto.setWellnessId(MCrypt.getInstance().encryptToText(ValidationCommon.getStringValueOrHyphen(record[1])));
						dto.setStudentName(ValidationCommon.getStringValueOrHyphen(record[2]));
						dto.setReferralDate(record[3] != null ? LocalDate.parse(record[3].toString()) : null);
						dto.setReferralType(MCrypt.getInstance().decryptToString(ValidationCommon.getStringValueOrHyphen(record[4])));
						dto.setConcernType(MCrypt.getInstance().decryptToString(ValidationCommon.getStringValueOrHyphen(record[5])));
						dto.setCoordinatedName(MCrypt.getInstance().decryptToString(ValidationCommon.getStringValueOrHyphen(record[6])));
						dto.setSubmittedDate(record[7] != null ? LocalDate.parse(record[7].toString()) : null);
						dto.setNoOfVisit(record[8] != null ? ValidationCommon.getStringValueOrHyphen(record[8]) : "0");
						dto.setCategory(ValidationCommon.getStringValueOrHyphen(record[21]));
						return dto;
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
		).toList();
	}

	private List<Object[]> fetchStudentWellnessData(PaginationForm form) throws Exception {
		StudentWellnessDto filter = getFilterData(form);
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize());
		return wellnessCategoricalDataRepository.getAllStudentWellnessDetails(
				ValidationCommon.isValid(filter.getStudentName()) ? filter.getStudentName().trim() : null,
				ValidationCommon.isValid(filter.getStudentId()) ? filter.getStudentId().trim() : null,
				filter.getReferralDateFrom(), filter.getReferralDateTo(),
				ValidationCommon.isValid(filter.getReferralType()) ? filter.getReferralType().trim() : null,
				ValidationCommon.isValid(filter.getConcernType()) ? filter.getConcernType().trim() : null,
				ValidationCommon.isValid(filter.getCoordinatorName()) ? filter.getCoordinatorName().trim() : null,
				filter.getStatus(), filter.getVisitDateFrom(), filter.getVisitDateTo(), SecurityCtxUtil.userId(), filter.getRole(),
				ValidationCommon.isValid(filter.getDepartment()) ? filter.getDepartment().trim() : null);
	}

	private List<Object[]> fetchStudentWellnessDataList(PaginationForm form) throws Exception {
		StudentWellnessDto filter = getFilterData(form);
		return wellnessCategoricalDataRepository.getAllStudentWellnessDetails(
				ValidationCommon.isValid(filter.getStudentName()) ? filter.getStudentName().trim() : null,
				ValidationCommon.isValid(filter.getStudentId()) ? filter.getStudentId().trim() : null,
				filter.getReferralDateFrom(), filter.getReferralDateTo(),
				ValidationCommon.isValid(filter.getReferralType()) ? filter.getReferralType().trim() : null,
				ValidationCommon.isValid(filter.getConcernType()) ? filter.getConcernType().trim() : null,
				ValidationCommon.isValid(filter.getCoordinatorName()) ? filter.getCoordinatorName().trim() : null,
				filter.getStatus(), filter.getVisitDateFrom(), filter.getVisitDateTo(), SecurityCtxUtil.userId(), filter.getRole(),
				ValidationCommon.isValid(filter.getDepartment()) ? filter.getDepartment().trim() : null);
	}

	public Workbook downloadStudentWellnessReport(PaginationForm form) throws Exception {
		String wellnessReport = messageSource.getMessage("message.label.wellness.report", null, Locale.getDefault());
		String sheetName = wellnessReport;
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);

		int columnCount = ExcelConstants.WELLNESS_REPORT_HEADER_DATA.length;
		String[] headerData = ExcelConstants.WELLNESS_REPORT_HEADER_DATA;
		String[] headerDataWidth = ExcelConstants.WELLNESS_REPORT_HEADER_DATA_WIDTH;

		XSSFCellStyle style3 = excelUtility.setDataStyle(workbook);
		XSSFCellStyle centerAlignStyle = excelUtility.setCenterAlignStyle(workbook);

		// Add new row with title "Office of the Hostel Management - IITMADRAS CAMPUS"
		Row row0 = sheet.createRow(0);
		Cell cell0 = row0.createCell(0);
		cell0.setCellValue(messageSource.getMessage("message.label.office.hostel.management.iitm.campus", null,
				Locale.getDefault()));
		cell0.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

		// Row 1 with wellness report title
		Row row1 = sheet.createRow(1);
		Cell cell1 = row1.createCell(0);
		cell1.setCellValue(wellnessReport);
		cell1.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, columnCount - 1));

		// Row 2 with report date
		Row row2 = sheet.createRow(2);
		SimpleDateFormat sdf = new SimpleDateFormat(
				messageSource.getMessage("session.date.format", null, Locale.getDefault()));
		String reportDate = messageSource.getMessage("message.label.report.date.colon", null, Locale.getDefault())
				+ sdf.format(new Date());
		cell1 = row2.createCell(0);
		cell1.setCellValue(reportDate);
		cell1.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, columnCount - 1));

		// Row 3 for header data
		Row row3 = sheet.createRow(3);
		excelUtility.createHeader(row3, 0, headerData, workbook);

		// Set column widths
		IntStream.range(0, headerData.length).forEach(i -> {
			int width = Integer.parseInt(headerDataWidth[i]);
			sheet.setColumnWidth(i, width);
		});

		// Fetch and populate wellness data
		List<Object[]> entityList = fetchStudentWellnessDataList(form);
		AtomicInteger rowCount = new AtomicInteger(3); // Start from row 4 (index 3)

		entityList.forEach(welness -> {
			Row row = sheet.createRow(rowCount.incrementAndGet());
			try {
				ArrayList<SimsConfigDataJsonArrayDto> visitStatusList = simsConfigDataService
						.getSimConfigValueFromJsonArray(SimsConfigDataService.VISIT_STATUS);

				String decryptedVisitStatus = MCrypt.getInstance().decryptToString(getNonNullValue(welness[20]));

				// Find the matching value from the configuration list
				String visitStatus = visitStatusList.stream().filter(dto -> dto.getId().equals(decryptedVisitStatus))
						.map(SimsConfigDataJsonArrayDto::getValue).findFirst().orElse(null);
				
				

				String[] values = { getNonNullValue(welness[21]), getNonNullValue(welness[0]),
						getNonNullValue(welness[2]), getNonNullValue(welness[11]), getNonNullValue(welness[12]),
						getNonNullValue(welness[21]).equals(
								messageSource.getMessage("message.label.category.students", null, Locale.getDefault()))
										? getNonNullValue(welness[13]) : getNonNullValue(welness[22]),
						getNonNullValue(welness[21]).equals(
								messageSource.getMessage("message.label.category.students", null, Locale.getDefault()))
										? getNonNullValue(welness[14]) : getNonNullValue(welness[23]),
						DateUtility.formatDate(welness[9]), getNonNullValue(welness[8]),
						MCrypt.getInstance().decryptToString(getNonNullValue(welness[15])),
						MCrypt.getInstance().decryptToString(getNonNullValue(welness[4])),
						MCrypt.getInstance().decryptToString(getNonNullValue(welness[5])),
						MCrypt.getInstance().decryptToString(getNonNullValue(welness[6])),
						MCrypt.getInstance().decryptToString(getNonNullValue(welness[18])),
						DateUtility.formatDate(welness[10]),
						MCrypt.getInstance().decryptToString(getNonNullValue(welness[19])), visitStatus };
				for (int colIdx = 0; colIdx < values.length; colIdx++) {
					excelUtility.createAndSetColumn(sheet, row, colIdx, -1, values[colIdx], style3);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		// Auto-size columns
		for (int i = 0; i < columnCount; i++) {
			sheet.autoSizeColumn(i);
		}

		return workbook;
	}

	public String retrieveExcelPassword() throws Exception {
		String password = wellnessUserManagementRepository.getUserPassword(SecurityCtxUtil.userName());
		if (password != null && !password.isEmpty()) {
			return MCrypt.getInstance().decryptToString(String.valueOf(password));
		} else {
			return simsConfigDataService.getSimConfigValue(SimsConfigDataService.WELLNESS_EXCEL_CREDENTIALS);
		}
	}

	@Transactional
	public String saveOrUpdateWellnessDetails(StudentWellnessCategoricalDataDto dto) throws Exception {
		String result = Optional.ofNullable(dto.getId()).filter(id -> (id != null && id > 0))
				.flatMap(
						id -> wellnessCategoricalDataRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					StudentWellnessCategoricalDataMapper.INSTANCE.toStudentWellnessCategoricalDataEntity(existingEntity,
							dto);
					existingEntity.onUpdate();
					wellnessCategoricalDataRepository.save(existingEntity);
					return Constants.UPDATED;
				}).orElseGet(() -> {
					if (dto.getCategory().equals(messageSource.getMessage("message.label.category.others", null, Locale.getDefault()))) {
					    dto.setOtherStudPhone(dto.getOtherStudPhone().replace(ModelConstants.HYPHEN, Strings.EMPTY));
					}
					StudentWellnessCategoricalDataEntity newEntity = StudentWellnessCategoricalDataMapper.INSTANCE
							.toStudentWellnessCategoricalDataEntity(dto);
					newEntity.onCreate();
					wellnessCategoricalDataRepository.save(newEntity);
					return Constants.SAVED;
				});
		return result;
	}

	public boolean deleteWellnessDetails(String wellnessId) throws Exception {
		return wellnessCategoricalDataRepository
				.findByIdAndActiveFlag(MCrypt.getInstance().decryptToLong(wellnessId), ModelConstants.STATUS_ACTIVE)
				.map(entity -> {
					entity.setActiveFlag(ModelConstants.STATUS_INACTIVE);
					entity.onUpdate();
					wellnessCategoricalDataRepository.save(entity);
					return true;
				}).orElse(false);
	}

	public List<StudentWellnessFollowupDataDto> getStudentWellnessViewList(String wellnessId, PaginationForm form)
			throws Exception {
		List<StudentWellnessFollowupDataEntity> result = studentWellnessFollowupDataRepository.getWellnessViewList(
				MCrypt.getInstance().decryptToLong(wellnessId), ModelConstants.STATUS_ACTIVE);
		int page = form.getPage() - 1;
		Pageable pageable = PageRequest.of(page, form.getSize(), Sort.by("modifiedAt").ascending());
		/*if (form.getSearch() == null || form.getSearch().isEmpty()) {
			result = studentWellnessFollowupDataRepository.getWellnessViewList(
					MCrypt.getInstance().decryptToLong(wellnessId), ModelConstants.STATUS_ACTIVE, pageable);
		} else {
			result = studentWellnessFollowupDataRepository.getWellnessViewListBySearch(
					MCrypt.getInstance().decryptToLong(wellnessId), ModelConstants.STATUS_ACTIVE, form.getSearch(),
					MCrypt.getInstance().encryptToText(form.getSearch()),
					pageable);
		}
		return result.map(StudentWellnessFollowupDataMapper.INSTANCE::fromStudentWellnessFollowupDataEntity);*/
		return result.stream().map(StudentWellnessFollowupDataMapper.INSTANCE::fromStudentWellnessFollowupDataEntity).toList();
	}

	public StudentWellnessCategoricalDataDto getWellnessDetailsByWellnessId(String wellnessId) throws Exception {
		Object[] result = null;
		result = wellnessCategoricalDataRepository.getWellnessDetailsByIdAndActiveFlag(
				MCrypt.getInstance().decryptToLong(wellnessId), ModelConstants.STATUS_ACTIVE);
		if (result != null && result.length > 0) {
			Object[] obj = (Object[]) result[0];
			StudentWellnessCategoricalDataEntity wellnessEntity = (StudentWellnessCategoricalDataEntity) obj[0];
			AllStudentsDetailsViewEntity studentEntity = (AllStudentsDetailsViewEntity) obj[1];
			StudentWellnessCategoricalDataDto wellnessDto = StudentWellnessCategoricalDataMapper.INSTANCE
					.fromStudentWellnessCategoricalDataEntity(wellnessEntity);
			wellnessDto.setStudentDetails(
					AllStudentsDetailsViewMapper.INSTANCE.fromAllStudentsDetailsViewEntity(studentEntity));
			if (wellnessDto.getCategory()
					.equals(messageSource.getMessage("message.label.category.students", null, Locale.getDefault()))) {
				wellnessDto.setDayScholar(Objects.nonNull(studentEntity) && studentEntity.getDayScholar().equals(ModelConstants.YES)
					? ModelConstants.OPEN_PARENTHESIS + ModelConstants.SPACE
						+ messageSource.getMessage("message.label.iitm.student", null, Locale.getDefault())
						+ ModelConstants.SPACE + ModelConstants.HYPHEN + ModelConstants.SPACE
						+ messageSource.getMessage("message.label.day.scholar", null, Locale.getDefault())
						+ ModelConstants.SPACE + ModelConstants.CLOSE_PARENTHESIS : ModelConstants.OPEN_PARENTHESIS + ModelConstants.SPACE
						+ messageSource.getMessage("message.label.iitm.student", null, Locale.getDefault())
						+ ModelConstants.SPACE + ModelConstants.CLOSE_PARENTHESIS);
			} else {
				wellnessDto.setDayScholar(ModelConstants.OPEN_PARENTHESIS + ModelConstants.SPACE
						+ wellnessDto.getCategory() + ModelConstants.SPACE + ModelConstants.CLOSE_PARENTHESIS);
			}
			return wellnessDto;
		}
		return null;
	}

	public StudentWellnessFollowupDataDto getWellnessVisitDetailsById(Long id, Long wellnessId) throws Exception {
		return Optional
				.ofNullable(studentWellnessFollowupDataRepository.findByIdAndWellnessIdAndActiveFlag(id, wellnessId,
						ModelConstants.STATUS_ACTIVE))
				.map(StudentWellnessFollowupDataMapper.INSTANCE::fromStudentWellnessFollowupDataEntity)
				.orElse(new StudentWellnessFollowupDataDto());
	}

	public Integer getWellnessVisitCountByWellnessId(Long wellnessId) throws Exception {
		return studentWellnessFollowupDataRepository.visitCountByWellnessId(wellnessId, ModelConstants.STATUS_ACTIVE);
	}

	@Transactional
	public String saveOrUpdateWellnessVisitDetails(StudentWellnessFollowupDataDto dto) throws Exception {
		String result = Optional.ofNullable(dto.getId()).filter(id -> (id != null && id > 0)).flatMap(
				id -> studentWellnessFollowupDataRepository.findByIdAndActiveFlag(id, ModelConstants.STATUS_ACTIVE))
				.map(existingEntity -> {
					StudentWellnessFollowupDataMapper.INSTANCE.toStudentWellnessFollowupDataEntity(existingEntity, dto);
					existingEntity.onUpdate();
					studentWellnessFollowupDataRepository.save(existingEntity);
					return Constants.UPDATED;
				}).orElseGet(() -> {
					StudentWellnessFollowupDataEntity newEntity = StudentWellnessFollowupDataMapper.INSTANCE
							.toStudentWellnessFollowupDataEntity(dto);
					Optional<StudentWellnessCategoricalDataEntity> wellness = wellnessCategoricalDataRepository
							.findByIdAndActiveFlag(newEntity.getWellness().getId(), ModelConstants.STATUS_ACTIVE);
					newEntity.setWellness(wellness.get());
					newEntity.onCreate();
					studentWellnessFollowupDataRepository.save(newEntity);
					return Constants.SAVED;
				});
		return result;
	}

	public Resource downloadWellnessDetailsPDF(String wellnessId) throws Exception {
		String tempFileLocation = pdfActiveService.getTempFileLocation();
		
		StudentWellnessCategoricalDataDto dto = getWellnessDetailsByWellnessId(wellnessId);
		List<StudentWellnessFollowupDataDto> wellnessVisitDtoList = new ArrayList<>();
		List<StudentWellnessFollowupDataEntity> wellnessVisitList = studentWellnessFollowupDataRepository
				.getWellnessViewList(MCrypt.getInstance().decryptToLong(wellnessId), ModelConstants.STATUS_ACTIVE);
		wellnessVisitList.forEach(visitList -> wellnessVisitDtoList
				.add(StudentWellnessFollowupDataMapper.INSTANCE.fromStudentWellnessFollowupDataEntity(visitList)));
		StudentBioDataFormDetailDto bioDataFormDetailDto = studentBioDataService.getStudentDetails(dto.getStudentId());
		String profileFileName = studentBioDataService.getFileName(bioDataFormDetailDto, bioDataFormDetailDto.getStudentId(), StudentBioDataFormDetailDto::getImageLocation, ModelConstants.FILE_STUDENT_PROFILE);
		bioDataFormDetailDto.setImageBytes(fileService.getDecodedFile(ModelConstants.IMAGE_BIO_DATA_PROFILE, profileFileName));
//		String imageFileName = dto.getStudentId() + ModelConstants.UNDERSCORE;
		byte[] studentProfileImage = fileService.getDecodedFile(ModelConstants.IMAGE_BIO_DATA_PROFILE, profileFileName);
		
		String fileName = "Wellness_Report_" + System.currentTimeMillis() + PdfActionService.PDF_EXTENSION;
		String outputFilePath = tempFileLocation + fileName;

		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);

		String password = retrieveExcelPassword();
		String owner_password = password + "_owner";
		WriterProperties writerProperties = new WriterProperties()
				.setStandardEncryption(password.getBytes(),owner_password.getBytes(),
						EncryptionConstants.ALLOW_PRINTING,EncryptionConstants.ENCRYPTION_AES_256);

		try (PdfWriter writer = new PdfWriter(outputFilePath,writerProperties);
			 PdfDocument pdfDocument = new PdfDocument(writer);
			 Document document = new Document(pdfDocument)) {
			// ✅ Add watermark handler
			ImageData watermarkImageData = pdfActiveService.getWatermarkImageData();
			pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE,new WatermarkEventHandler(watermarkImageData));

			pdfActiveService.addDocumentHeaderWithProfile(document,
					messageSource.getMessage("message.label.heading", null, Locale.getDefault()),
					messageSource.getMessage("message.label.wellness.report", null, Locale.getDefault()),
					studentProfileImage);

			document.add(new Paragraph(PdfActionService.NEXT_LINE));
			
			String category, studentName, emailId, mobileNo = null;
			if (dto.getCategory().equals(messageSource.getMessage("message.label.category.students", null, Locale.getDefault()))) {
				category = (Objects.nonNull(dto.getStudentDetails()) && Objects.nonNull(dto.getStudentDetails().getDayScholar()) && dto.getStudentDetails().getDayScholar().equals(ModelConstants.YES)
						? ModelConstants.OPEN_PARENTHESIS + ModelConstants.SPACE
								+ messageSource.getMessage("message.label.iitm.student", null, Locale.getDefault())
								+ ModelConstants.SPACE + ModelConstants.HYPHEN + ModelConstants.SPACE
								+ messageSource.getMessage("message.label.day.scholar", null, Locale.getDefault())
								+ ModelConstants.SPACE + ModelConstants.CLOSE_PARENTHESIS
						: ModelConstants.OPEN_PARENTHESIS + ModelConstants.SPACE
								+ messageSource.getMessage("message.label.iitm.student", null, Locale.getDefault())
								+ ModelConstants.SPACE + ModelConstants.CLOSE_PARENTHESIS);
				studentName = Objects.nonNull(dto.getStudentDetails()) && Objects.nonNull(dto.getStudentDetails().getStudentName()) ? dto.getStudentDetails().getStudentName() : ModelConstants.NOT_APPLICABLE;
				emailId = Objects.nonNull(dto.getStudentDetails()) && Objects.nonNull(dto.getStudentDetails().getEmailId()) ? dto.getStudentDetails().getEmailId() : ModelConstants.NOT_APPLICABLE;
				mobileNo = Objects.nonNull(dto.getStudentDetails()) && Objects.nonNull(dto.getStudentDetails().getStudentMobile()) ? String.valueOf(dto.getStudentDetails().getStudentMobile()) : ModelConstants.NOT_APPLICABLE;
			} else {
				category = ModelConstants.OPEN_PARENTHESIS + ModelConstants.SPACE + dto.getCategory()
						+ ModelConstants.SPACE + ModelConstants.CLOSE_PARENTHESIS;
				studentName = dto.getOtherStudName();
				emailId = dto.getOtherStudEmail();
				mobileNo = dto.getOtherStudPhone();
			}
			
			// Wellness Data Section
			document.add(pdfActiveService.addFullWidthTitle(
					messageSource.getMessage("message.label.wellness.data", null, Locale.getDefault()) + ModelConstants.COLAN
					+ ModelConstants.SPACE + category, TextAlignment.LEFT));
			
            Table wellnessDetailsTable = new Table(new float[]{2, 3, 2, 3});
            wellnessDetailsTable.setWidth((PdfActionService.VALUE_100_P));
            
			String studentNameMsg = messageSource.getMessage("message.label.student.id", null, Locale.getDefault()) + ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(studentNameMsg, dto.getStudentId(), wellnessDetailsTable, new int[] { 1, 3 });
			String fullName = messageSource.getMessage("message.label.student.name", null, Locale.getDefault()) + ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(fullName, studentName, wellnessDetailsTable, new int[] { 1, 3 });
			String course = messageSource.getMessage("message.label.course", null, Locale.getDefault()) + ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(course, dto.getStudentDetails() != null && dto.getStudentDetails().getDeptName() != null
							? dto.getStudentDetails().getDeptName() : null, wellnessDetailsTable);
			String gender = messageSource.getMessage("message.label.gender", null, Locale.getDefault()) + ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(gender, dto.getStudentDetails() != null && dto.getStudentDetails().getGender() != null
							? dto.getStudentDetails().getGender() : null, wellnessDetailsTable);
			String hostelName = messageSource.getMessage("message.label.hostel.name", null, Locale.getDefault()) + ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(hostelName, dto.getStudentDetails() != null && dto.getStudentDetails().getHostelName() != null
							?  dto.getStudentDetails().getHostelName() : null, wellnessDetailsTable);
			String roomNumber = messageSource.getMessage("message.label.room.number", null, Locale.getDefault()) + ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(roomNumber, dto.getStudentDetails() != null && dto.getStudentDetails().getRoomNumber() != null
							?  dto.getStudentDetails().getRoomNumber() : null, wellnessDetailsTable);
			String studMail = messageSource.getMessage("message.label.student.mail", null, Locale.getDefault()) + ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(studMail, emailId, wellnessDetailsTable);
			String studPhone = messageSource.getMessage("message.label.student.mobile.number", null, Locale.getDefault()) + ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(studPhone, mobileNo, wellnessDetailsTable);
			String referralDate = messageSource.getMessage("message.label.referral.date", null, Locale.getDefault()) + ModelConstants.COLAN;
			String date = dto.getReferralDate().format(dateFormatter);
			pdfActiveService.addTableTextValue(referralDate, date, wellnessDetailsTable);
			String referralType = messageSource.getMessage("message.label.referral.type", null, Locale.getDefault()) + ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(referralType, dto.getReferralType(), wellnessDetailsTable);
			String referredBy = messageSource.getMessage("message.label.referred.by", null, Locale.getDefault()) + ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(referredBy, dto.getReferralBy(), wellnessDetailsTable);
			String referrerMail = messageSource.getMessage("message.label.referrer", null, Locale.getDefault())
					+ ModelConstants.SPACE
					+ messageSource.getMessage("message.label.email.id", null, Locale.getDefault())
					+ ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(referrerMail, dto.getReferralEmail(), wellnessDetailsTable);
			String referrerPhoneNo = messageSource.getMessage("message.label.referrer", null, Locale.getDefault())
					+ ModelConstants.SPACE
					+ messageSource.getMessage("message.label.phone.number", null, Locale.getDefault())
					+ ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(referrerPhoneNo, dto.getReferralPhone(), wellnessDetailsTable);
			String concernType = messageSource.getMessage("message.label.concern.type", null, Locale.getDefault())
					+ ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(concernType, dto.getConcernType(), wellnessDetailsTable);
			String coordinatedName = messageSource.getMessage("message.label.coordinator.name", null,
					Locale.getDefault()) + ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(coordinatedName, dto.getCoordinatedName(), wellnessDetailsTable);
			String coordinatedEmail = messageSource.getMessage("message.label.coordinator.email", null,
					Locale.getDefault()) + ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(coordinatedEmail, dto.getCoordinatedEmail(), wellnessDetailsTable);
			String selfHarm = messageSource.getMessage("message.label.self.harm", null, Locale.getDefault())
					+ ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(selfHarm,
					dto.getSelfHarm().equals(true) ? ModelConstants.Y : ModelConstants.N, wellnessDetailsTable);
			String selfHarmType = messageSource.getMessage("message.label.self.harm.type", null, Locale.getDefault())
					+ ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(selfHarmType, dto.getSelfHarmType(), wellnessDetailsTable);
			String psychiatric = messageSource.getMessage("message.label.psychiatric.consultation", null,
					Locale.getDefault()) + ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(psychiatric,
					dto.getPsychiatricConsultation().equals(true) ? ModelConstants.Y : ModelConstants.N,
					wellnessDetailsTable);
			String psychiatricName = messageSource.getMessage("message.label.psychiatric.name", null,
					Locale.getDefault()) + ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(psychiatricName, dto.getPsychiatricName(), wellnessDetailsTable);
			String referralDesc = messageSource.getMessage("message.label.referral.type", null, Locale.getDefault())
					+ ModelConstants.SPACE
					+ messageSource.getMessage("message.label.description", null, Locale.getDefault())
					+ ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(referralDesc, dto.getReferralOthersDescription(), wellnessDetailsTable,
					new int[] { 1, 3 });
			String concernDesc = messageSource.getMessage("message.label.concern.description", null,
					Locale.getDefault()) + ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(concernDesc, dto.getConcernOthersDescription(), wellnessDetailsTable,
					new int[] { 1, 3 });
			String additional = messageSource.getMessage("message.label.additional.details", null, Locale.getDefault())
					+ ModelConstants.COLAN;
			pdfActiveService.addTableTextValue(additional, dto.getAdditionalDetails(), wellnessDetailsTable,
					new int[] { 1, 3 });

			document.add(wellnessDetailsTable);
			document.add(new LineSeparator(new SolidLine()).setMarginTop(5F).setMarginBottom(5F));

			// Visit Details Section
			ArrayList<Table> visitDetailsTable = new ArrayList<>();
			if (!wellnessVisitDtoList.isEmpty()) {
				document.add(new AreaBreak());
				wellnessVisitDtoList.forEach(visitDetails -> {
					Table visitDetailsList = new Table(new float[] { 2, 3, 2, 3 });
					visitDetailsList.setWidth((PdfActionService.VALUE_100_P));
					String visitDate = messageSource.getMessage("message.label.visit.date", null, Locale.getDefault())
							+ ModelConstants.COLAN;
					String formattedVisitDate = visitDetails.getVisitDate().format(dateFormatter);
					pdfActiveService.addTableTextValue(visitDate, formattedVisitDate, visitDetailsList);
					String noOfVisit = messageSource.getMessage("message.label.no.of.visit", null, Locale.getDefault())
							+ ModelConstants.COLAN;
					pdfActiveService.addTableTextValue(noOfVisit, visitDetails.getNoOfVisit(), visitDetailsList);
					String interactionMode = messageSource.getMessage("message.label.interaction.mode", null,
							Locale.getDefault()) + ModelConstants.COLAN;
					pdfActiveService.addTableTextValue(interactionMode, visitDetails.getInteractionMode(),
							visitDetailsList);
					String duration = messageSource.getMessage("message.label.duration", null, Locale.getDefault())
							+ ModelConstants.COLAN;
					String durationMin = visitDetails.getDuration() + ModelConstants.SPACE
							+ messageSource.getMessage("message.label.mins", null, Locale.getDefault());
					pdfActiveService.addTableTextValue(duration, durationMin, visitDetailsList);
					String concernDiscussed = messageSource.getMessage("message.label.concerns.discussed", null,
							Locale.getDefault()) + ModelConstants.COLAN;
					pdfActiveService.addTableTextValue(concernDiscussed, visitDetails.getConcernsDiscussed(),
							visitDetailsList, new int[] { 1, 3 });
					String futureActionPlan = messageSource.getMessage("message.label.future.action.plan", null,
							Locale.getDefault()) + ModelConstants.COLAN;
					pdfActiveService.addTableTextValue(futureActionPlan, visitDetails.getFutureActionPlan(),
							visitDetailsList, new int[] { 1, 3 });
					String followUpDate = messageSource.getMessage("message.label.follow.up.date", null,
							Locale.getDefault()) + ModelConstants.COLAN;
					String formattedFollowUpDate = ModelConstants.NOT_APPLICABLE;
					if (visitDetails.getFollowUpDate() != null) {
						formattedFollowUpDate = visitDetails.getFollowUpDate().format(dateFormatter);
					}
					pdfActiveService.addTableTextValue(followUpDate, formattedFollowUpDate, visitDetailsList);
					visitDetailsTable.add(visitDetailsList);
				});
			}

			if (!wellnessVisitDtoList.isEmpty()) {
				document.add(pdfActiveService.addFullWidthTitle(
						messageSource.getMessage("message.label.visit.details", null, Locale.getDefault())
								+ ModelConstants.COLAN,
						TextAlignment.LEFT));
				visitDetailsTable.forEach(familyTable -> {
					document.add(familyTable);
					document.add(new LineSeparator(new SolidLine()).setMarginTop(5F).setMarginBottom(5F));
				});
			}
			//pdfActiveService.addWatermarkImage(pdfDocument);
			document.close();
//			String tempOutputPath = outputFilePath + "_temp.pdf";
//			PdfReader reader = new PdfReader(outputFilePath,new ReaderProperties().setPassword(owner_password.getBytes()));
//			PdfDocument pdfDoc = new PdfDocument(new PdfReader(outputFilePath), new PdfWriter(tempOutputPath));
//			PdfDocument pdfDoc = new PdfDocument(reader, new PdfWriter(tempOutputPath));
//			pdfActiveService.addWatermarkImage(pdfDoc);
//			pdfDoc.close();
//			Files.move(Paths.get(tempOutputPath), Paths.get(outputFilePath), StandardCopyOption.REPLACE_EXISTING);
			return new FileSystemResource(outputFilePath);
		} catch (IOException e) {
			throw new Exception(messageSource.getMessage("message.label.error.generate.pdf", null, Locale.getDefault()),
					e);
		}
	}

	private String getNonNullValue(Object obj) {
		return (obj != null) ? String.valueOf(obj) : Strings.EMPTY;
	}

	public StudentWellnessDto getFilterData(PaginationForm form) throws Exception  {
		StudentWellnessDto filter = new StudentWellnessDto();
		String[] roleParts = SecurityCtxUtil.userRole().split(ModelConstants.UNDERSCORE);
		String role = roleParts[roleParts.length - 1];
		filter.setStudentId(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("studentId")));
		filter.setStudentName(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("studentName")));
		filter.setReferralDateFrom(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("referralDateFrom")));
		filter.setReferralDateTo(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("referralDateTo")));
		filter.setVisitDateFrom(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("visitDateFrom")));
		filter.setVisitDateTo(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("visitDateTo")));
		
	    String refTypePlain = ValidationCommon.toStringOrNull(form.getAdditionalParam().get("referralType"));
	    filter.setReferralType(refTypePlain != null ? MCrypt.getInstance().encryptToText(refTypePlain.trim()) : null);

	    String concernPlain = ValidationCommon.toStringOrNull(form.getAdditionalParam().get("concernType"));
	    filter.setConcernType(concernPlain != null ? MCrypt.getInstance().encryptToText(concernPlain.trim()) : null);

	    String coordPlain = ValidationCommon.toStringOrNull(form.getAdditionalParam().get("coordinatorName"));
	    filter.setCoordinatorName(coordPlain != null ? MCrypt.getInstance().encryptToText(coordPlain.trim()) : null);
	    
		filter.setDepartment(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("department")));
		filter.setStatus(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("status")));
		filter.setRole(role);
		return filter;
	}
	
	public StudentWellnessDto getDecryptFilterData(PaginationForm form) throws Exception  {
		StudentWellnessDto filter = new StudentWellnessDto();
		String[] roleParts = SecurityCtxUtil.userRole().split(ModelConstants.UNDERSCORE);
		String role = roleParts[roleParts.length - 1];
		filter.setStudentId(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("studentId")));
		filter.setStudentName(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("studentName")));
		filter.setReferralDateFrom(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("referralDateFrom")));
		filter.setReferralDateTo(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("referralDateTo")));
		filter.setVisitDateFrom(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("visitDateFrom")));
		filter.setVisitDateTo(ValidationCommon.toLocalDateOrNull(form.getAdditionalParam().get("visitDateTo")));
		filter.setReferralType(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("referralType")));
		filter.setConcernType(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("concernType")));
		filter.setCoordinatorName(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("coordinatorName")));
		filter.setDepartment(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("department")));
		filter.setStatus(ValidationCommon.toStringOrNull(form.getAdditionalParam().get("status")));
		filter.setRole(role);
		return filter;
	}
	
}
