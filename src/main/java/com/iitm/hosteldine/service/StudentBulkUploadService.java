package com.iitm.hosteldine.service;

import com.iitm.hosteldine.constant.ExcelConstants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.constant.SimsConfigType;
import com.iitm.hosteldine.dto.StudentDetailsInfoMapper;
import com.iitm.hosteldine.dto.hostel.StudentHostelInfoDetailsDto;
import com.iitm.hosteldine.dto.hostel.StudentsHostelAllotmentDto;
import com.iitm.hosteldine.dto.student.StudentBulkInfoDto;
import com.iitm.hosteldine.entity.CourseAllocationInfoEntity;
import com.iitm.hosteldine.entity.RoleEntity;
import com.iitm.hosteldine.entity.UserManagementEntity;
import com.iitm.hosteldine.entity.UserManagementId;
import com.iitm.hosteldine.entity.student.RfidMappingEntity;
import com.iitm.hosteldine.entity.student.SecondaryRolesEntity;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.entity.student.UserFpCardEntity;
import com.iitm.hosteldine.mapper.StudentBioDataFormDetailMapper;
import com.iitm.hosteldine.model.StudentBioDataFormDetailEntity;
import com.iitm.hosteldine.model.collegeInfo.CourseMasterEntity;
import com.iitm.hosteldine.model.student.AllStudentsDetailsViewEntity;
import com.iitm.hosteldine.repository.CourseAllocationInfoRepository;
import com.iitm.hosteldine.repository.RoleRepository;
import com.iitm.hosteldine.repository.StudentBioDataFormDetailRepository;
import com.iitm.hosteldine.repository.UserManagementRepository;
import com.iitm.hosteldine.repository.collegeInfo.CourseMasterRepository;
import com.iitm.hosteldine.repository.hostel.HostelRoomAllotmentRepository;
import com.iitm.hosteldine.repository.student.*;
import com.iitm.hosteldine.service.hostel.HostelIndividualAllotmentService;
import com.iitm.hosteldine.util.ExcelUtility;
import com.iitm.hosteldine.util.MD5Encryption;
import com.iitm.hosteldine.util.response.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;


@Service
@RequiredArgsConstructor
public class StudentBulkUploadService {
    private final ExcelUtility excelUtility;
    private final StudentDetailsInfoRepository studentDetailsInfoRepository;
    private final UserManagementRepository userManagementRepository;
    private final RfidRepository rfidRepository;
    private final UserFpCardRepository userFpCardRepository;
    private final SecondaryRolesRepository secondaryRolesRepository;
    private final CourseAllocationInfoRepository courseAllocationInfoRepository;
    private final RoleRepository roleRepository;
    private final SimsConfigDataService simsConfigDataService;
    private final StudentDetailsInfoService studentDetailsInfoService;
    private final CourseMasterRepository courseMasterRepository;
    private final StudentBioDataFormDetailRepository studentBioDataFormDetailRepository;
    private final AllStudentsDetailsViewRepository allStudentsDetailsViewRepository;
	private final MessageSource messageSource;
    private final HostelIndividualAllotmentService hostelIndividualAllotmentService;
    private final HostelRoomAllotmentRepository hostelRoomAllotmentRepository;
    private final InMemoryLogService logService;
    private final BulkAsyncExecutor bulkAsyncExecutor;
    private final CommonResponseUtil commonResponseUtil;

    public Workbook downloadStudentBulkUploadTemplate() {
        String sheetName = "StudentUpload";
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);
        int columnCount = 0;
        String[] headerData = ExcelConstants.STUDENT_HEADER_DATA;
        String[] headerDataWidth = ExcelConstants.STUDENT_HEADER_DATA_WIDTH;
        /**
         * Data Style
         */
        XSSFCellStyle style3 = excelUtility.setDataStyle(workbook);

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

        // Dropdown data for the two columns
        List<String> genderList = Arrays.asList(ModelConstants.GENDER_LIST);
        List<String> courseHeadList = courseMasterRepository.findAllByActiveFlagOrderByCourseMasterHeadAsc(ModelConstants.STATUS_ACTIVE);

        // Add dropdown data to the hidden sheet

        ExcelUtility.addDropdownDataToHiddenSheet(hiddenSheet, "Gender", genderList);
        ExcelUtility.addDropdownDataToHiddenSheet(hiddenSheet, "Course Head", courseHeadList);

        // Add dropdowns to the main sheet columns
        ExcelUtility.addDropdownToColumn(sheet, 1, 2500, 3, "DropdownData!$D$1:$D$" + genderList.size()); // Gender List
        ExcelUtility.addDropdownToColumn(sheet, 1, 2500, 4, "DropdownData!$E$1:$E$" + courseHeadList.size()); // Course Head List

        return workbook;
    }
    public List<String> getCourseHeaders() {
        return courseMasterRepository.findAllByActiveFlagOrderByCourseMasterHeadAsc(ModelConstants.STATUS_ACTIVE);
    }

    public StudentBulkInfoDto startBulkUpload(StudentBulkInfoDto studentBulkUploadDto) {
        addLog(studentBulkUploadDto.getLogTag(), 0, commonResponseUtil.getMessage("upload.started"));

        bulkAsyncExecutor.execute(studentBulkUploadDto.getLogTag(), () -> {
            try {
                saveStudentBulkUpload(studentBulkUploadDto);
            } catch (Exception e) {
                addError(studentBulkUploadDto.getLogTag(), 0, commonResponseUtil.getMessage("upload.failed") + e.getMessage(),e);
                throw new RuntimeException(e);
            }
        });

        return studentBulkUploadDto;
    }

    @Transactional(rollbackFor = Exception.class)
    public StudentBulkInfoDto saveStudentBulkUpload(StudentBulkInfoDto studentBulkUploadDto) throws IOException {
        List<StudentBulkInfoDto> students = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();
        StudentBulkInfoDto studentBulkInfoDto = new StudentBulkInfoDto();
        studentBulkInfoDto.setErrorList(new ArrayList<>());
        String previousCheck = "";
        String idRepeatCheck = "";
        ArrayList prevIdList = new ArrayList();
        int rowNumber = 0;
        String tag=studentBulkUploadDto.getLogTag();
        addLog(tag, 0, commonResponseUtil.getMessage("upload.validation.start"));
        try (InputStream is = new ByteArrayInputStream(studentBulkUploadDto.getFileBytes()) ;
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(rowNumber);
            List<String> headerList = Arrays.asList(ExcelConstants.STUDENT_HEADER_DATA);
            List<String> actualHeaders = new ArrayList<>();
            for (Cell cell : headerRow) {
                actualHeaders.add(formatter.formatCellValue(cell));
            }

            if (!headerList.containsAll(actualHeaders)) {
                studentBulkInfoDto.getErrorList().add("Invalid Excel Template");
                addValidation(tag, 0, commonResponseUtil.getMessage("upload.validation.errors"));
                studentBulkInfoDto.getErrorList().forEach(err -> addValidation(tag, 0, err));
                return studentBulkInfoDto;
            }
            rowNumber++;

            // Check for empty file
            if (sheet.getPhysicalNumberOfRows() <= 1) {
                studentBulkInfoDto.getErrorList().add("Empty file. Please upload with data");
                addValidation(tag, 0, commonResponseUtil.getMessage("upload.validation.errors"));
                studentBulkInfoDto.getErrorList().forEach(err -> addValidation(tag, 0, err));
                return studentBulkInfoDto;
            }
            int rowCount = ExcelUtility.countNonEmptyRows(sheet);

            for (Row row : sheet) {
                if (rowCount > rowNumber) {
                    if (row.getRowNum() == 0) { // Skip header row
                        continue;
                    }
                    addLog(tag, 0, commonResponseUtil.getMessage("upload.validating.row")+" " +(rowNumber + 1));
                    StudentBulkInfoDto student = new StudentBulkInfoDto();
                    StringBuilder errorDetails = new StringBuilder();
                    boolean error = false;
                    int column = 0;
                    // Student Id
                    if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
                        boolean isValidStudentId = formatter.formatCellValue(row.getCell(column)).matches(ModelConstants.ALPHA_NUMERIC_REGEX);
                        if (isValidStudentId) {
                            student.setStudentId(formatter.formatCellValue(row.getCell(column)).trim().toUpperCase());
                            if (prevIdList.toString().contains(student.getStudentId())) {
                                cellError("Previous ID " + student.getStudentId() + " should not be Student ID ", column, rowNumber, student);
                                error = true;
                                errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                            }
                        } else {
                            cellError("Accept alphanumeric characters with a minimum of 6 and a maximum of 10 characters.", column, rowNumber, student);
                            error = true;
                            errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                        }

                        if (studentDetailsInfoRepository.existsByActiveFlagAndStudentIdAndSettlementFlag(ModelConstants.STATUS_ACTIVE, student.getStudentId(), ModelConstants.YES)) {
                            cellError("Student ID " + student.getStudentId() + ":Settlement Completed", column, rowNumber, student);
                            error = true;
                            errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                        }
                        if (!studentDetailsInfoRepository.checkStudentIdExistInPreviousId(student.getStudentId()).isEmpty()) {
                            cellError("Student ID " + student.getStudentId() + " already assigned to another roll no", column, rowNumber, student);
                            error = true;
                            errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                        }

                    } else {
                        cellError("Student ID should not be empty ", column, rowNumber, student);
                        error = true;
                        errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                    }
                    column++;

                    // First Name
                    if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
                        boolean isValidName = formatter.formatCellValue(row.getCell(column)).matches(ModelConstants.ALPHA_WITH_SPACE);
                        if (isValidName) {
                            student.setFirstName(CommonService.capitalizeEachWord(formatter.formatCellValue(row.getCell(column)).trim()));
                        } else {
                            cellError("Enter only Alphabets", column, rowNumber, student);
                            error = true;
                            errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                        }
                    } else {
                        cellError("Student First Name should not be empty", column, rowNumber, student);
                        error = true;
                        errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                    }
                    column++;

                    // Last Name
                    if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
                        boolean isValidLastName = formatter.formatCellValue(row.getCell(column)).matches(ModelConstants.ALPHA_WITH_SPACE);
                        if (isValidLastName) {
                            student.setLastName(formatter.formatCellValue(row.getCell(column)).trim());
                        } else {
                            cellError("Enter only Alphabets", column, rowNumber, student);
                            error = true;
                            errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                        }
                    }
                    column++;

                    // Gender
                    if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
                        student.setGender(row.getCell(column).getStringCellValue());
                        boolean containsGender = Arrays.asList(ModelConstants.GENDER_LIST).contains(student.getGender());
                        if (!containsGender) {
                            cellError("Gender should be either 'M' or 'F'", column, rowNumber, student);
                            error = true;
                            errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                        }
                    } else {
                        cellError("Gender should not be empty", column, rowNumber, student);
                        error = true;
                        errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                    }
                    column++;

                    // Course Name
                    if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
                        student.setCourse(formatter.formatCellValue(row.getCell(column)).trim());
                        if (!courseMasterRepository.existsByCourseMasterHeadIgnoreCaseAndActiveFlag(student.getCourse(), ModelConstants.STATUS_ACTIVE)) {
                            cellError("Course Head does not exist", column, rowNumber, student);
                            error = true;
                            errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                        }
                    } else {
                        cellError("Course Head should not be empty", column, rowNumber, student);
                        error = true;
                        errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                    }
                    column++;

                    //Previous Id
                    if (row.getCell(column) != null && !Objects.equals(row.getCell(column).toString(), "")) {
                        String previousStudId = formatter.formatCellValue(row.getCell(column)).trim().toUpperCase();
                        String[] previousArray = previousStudId.split(",");
                        // Initialize and populate the prevId array
                        String[] prevId = new String[previousArray.length];
                        for (int i = 0; i < previousArray.length; i++) {
                            prevId[i] = previousArray[i].trim();
                        }
                        // Set the previous IDs without brackets
                        student.setPreviousId(String.join(",", prevId));


                        // Check if the previous ID is assigned to another roll number
                        int checkStudentExist = studentDetailsInfoService.checkPreviousIdExist(student.getStudentId(), previousStudId);
                        if (checkStudentExist > 0) {
                            cellError("Previous ID " + previousStudId + " Already Assigned to Another Student ID", column, rowNumber, student);
                            error = true;
                            errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                        }

                        // Check if the previous student ID exists
                        String prevIdCheck = studentDetailsInfoService.checkDuplicateNoWithSeparate(student.getPreviousId());
                        if (!Objects.equals(prevIdCheck, "")) {
                            cellError("Previous Student ID " + prevIdCheck + " does not Exist: ", column, rowNumber, student);
                            error = true;
                            errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                        }

                        // Check if any IDs are repeated
                        String[] tempIds = previousStudId.split(",");
                        for (String id : tempIds) {
                            if (idRepeatCheck.contains(id)) {
                                cellError("Previous Student ID " + id + " Should not be Repeated ", column, rowNumber, student);
                                error = true;
                                errorDetails.append("-").append(student.getExcelErrorMsg()).append("\n");
                            }
                        }

                        // Update idRepeatCheck with the current ID for future checks
                        prevIdList.add(previousStudId);
                        idRepeatCheck = String.join(",", prevIdList);
                    }


                    // Add errors for the row to the studentBulkInfoDto if any
                    if (error) {
                        studentBulkInfoDto.getErrorList().add(ModelConstants.ROW + rowNumber + ": " + errorDetails);
                    } else {
                        student.setEmailId(student.getStudentId().toUpperCase() + simsConfigDataService.getSimConfigValue("STUDENT_MAIL_ID"));
                        students.add(student);
                    }
                    rowNumber++;
                }


            }
            if(!studentBulkInfoDto.getErrorList().isEmpty()){
                addValidation(tag, 0, commonResponseUtil.getMessage("upload.validation.errors"));
                studentBulkInfoDto.getErrorList().forEach(err -> addValidation(tag, 0, err));
                return studentBulkInfoDto;
            }
            if (!students.isEmpty()) saveStudentDetailsInfo(students,tag);

        } catch (Exception e) {
            addError(tag, 0, commonResponseUtil.getMessage("upload.failed") + e.getMessage(),e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return studentBulkInfoDto;
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveStudentDetailsInfo(List<StudentBulkInfoDto> students,String tag) {
        students.forEach(student -> {
            addLog(tag, 0, commonResponseUtil.getMessage("upload.inserting.student")+student.getStudentId().toUpperCase());
            boolean existStatus = studentDetailsInfoRepository.existsByStudentIdAndActiveFlag(student.getStudentId(), ModelConstants.STATUS_ACTIVE);
            if (existStatus) {
                if (student.getPreviousId() != null) {
                    String previousId = studentDetailsInfoService.checkAndUpdatePreviousStudId
                            (student.getStudentId(), student.getPreviousId());
                    if (previousId != null) {
                        student.setPreviousId(previousId);
                    }

                    studentDetailsInfoRepository.updatePreviousStudId(student.getPreviousId(), student.getStudentId());
                }
            }else{
                if (student.getPreviousId() != null) {
                    String previousId = studentDetailsInfoService.getPrevIdsOfPrevIds
                            (student.getPreviousId());
                    if (previousId != null) {
                        student.setPreviousId(previousId);
                    }
                }
            }

            int returnCount = studentDetailsInfoService.deactivateStudents(student.getPreviousId());

            // Fetch the existing entity if it exists
            Optional<StudentDetailsInfoEntity> studentOptional = studentDetailsInfoRepository.findByStudentIdAndActiveFlag(student.getStudentId(), ModelConstants.STATUS_ACTIVE);
            StudentDetailsInfoEntity studentEntity = null;
            // Map the provided student to an entity (for a new student or if no existing entity is found)
            // If an existing entity is found, update the studentEntity with the existing one
            if (studentOptional.isPresent()) {
                studentEntity = studentOptional.get();
                StudentDetailsInfoMapper.INSTANCE.onUpdateEntity(studentEntity, student);
                studentEntity.onUpdate();
            } else {
                studentEntity = StudentDetailsInfoMapper.INSTANCE.toStudentDetailsEntity(student);
                studentEntity.onCreate();
            }

            // Add the entity to the list
            studentDetailsInfoRepository.save(studentEntity);


            // Map StudentDto to UserManagementEntity.
            // De-active previous id students
            if (student.getPreviousId() != null) {
                List<String> userIds = Arrays.asList(student.getPreviousId().toUpperCase().split(","));
                userManagementRepository.updateUserActiveFlagByIds(userIds);
            }

            if (!userManagementRepository.existsByIdUsernameAndActiveFlag(student.getStudentId().toLowerCase(), ModelConstants.STATUS_ACTIVE)) {
                UserManagementEntity userManagementEntity = new UserManagementEntity();
                UserManagementId userManagementId = new UserManagementId();
                userManagementId.setUserId(student.getStudentId().toUpperCase());
                userManagementId.setUsername(student.getStudentId().toLowerCase());
                userManagementEntity.setEmail(student.getEmailId());
                userManagementEntity.setId(userManagementId);
                userManagementEntity.setAuthenticationServer(ModelConstants.LDAP_AUTH);
                userManagementEntity.setLastLoginTime(LocalDateTime.now());
                userManagementEntity.setAccountType(ModelConstants.STUDENT_LOGIN_TYPE);
                userManagementEntity.setNoFailedAttempts(0);
                userManagementEntity.setEmail(student.getEmailId());
                userManagementEntity.setEmployeeId(0L);
				userManagementEntity.setRole(roleRepository
						.findByRoleName(ModelConstants.STUDENT_LOGIN_TYPE).orElseGet(RoleEntity::new));
                userManagementEntity.onCreate();
                //userManagementEntity.setPassword(MD5Encryption.md5Encrypt(autoGenerateString.nextString(ModelConstants.PASSWORD_LENGTH)));
                userManagementEntity.setPassword(MD5Encryption.md5Encrypt("welcome123"));
                userManagementRepository.save(userManagementEntity);
            }


            // Map StudentDto to SecondaryRolesEntity
            if (!secondaryRolesRepository.existsByUserIdAndActiveFlag(student.getStudentId(), ModelConstants.STATUS_ACTIVE)) {
                SecondaryRolesEntity secondaryRolesEntity = new SecondaryRolesEntity();
                secondaryRolesEntity.setUserId(student.getStudentId());
                secondaryRolesEntity.setRoleId(roleRepository.getRoleIdByName(ModelConstants.STUDENT_LOGIN_TYPE, ModelConstants.STATUS_ACTIVE));
                secondaryRolesRepository.save(secondaryRolesEntity);
            }

            // Map StudentDto to RfidMappingEntity
            if (!rfidRepository.existsByStudentId(student.getStudentId())) {
                RfidMappingEntity rfidMappingEntity = new RfidMappingEntity();
                rfidMappingEntity.setStudentId(student.getStudentId());
                rfidRepository.save(rfidMappingEntity);
            }

            // Map StudentDto to UserFpCardEntity
            UserFpCardEntity userFpCardEntity = new UserFpCardEntity();
            userFpCardEntity.setUserId(student.getStudentId());
            userFpCardEntity.setCardActiveStatus(ModelConstants.STATUS_ACTIVE);
            userFpCardEntity.setAccessCardSerialNo(userFpCardRepository.findMaxAccessCardSerialNo());
            userFpCardRepository.save(userFpCardEntity);

            // Map StudentDto to CourseAllocationInfoEntity
            if (!(student.getCourse() == null)) {
                Optional<CourseMasterEntity> courseMasterEntityOptional = courseMasterRepository.findByCourseMasterHeadIgnoreCaseAndActiveFlag(student.getCourse(), ModelConstants.STATUS_ACTIVE);
                courseMasterEntityOptional.ifPresent(course -> {
                    Optional<CourseAllocationInfoEntity> existingAllocationOptional = courseAllocationInfoRepository.findByStudentIdAndActiveFlag(student.getStudentId(), ModelConstants.STATUS_ACTIVE);
                    if (existingAllocationOptional.isPresent()) {
                        // Update existing allocation
                        CourseAllocationInfoEntity existingAllocation = existingAllocationOptional.get();
                        existingAllocation.setCourseId(course.getCourseMasterId());
                        courseAllocationInfoRepository.save(existingAllocation);
                    } else {
                        // Create new allocation
                        CourseAllocationInfoEntity courseAllocationInfoEntity = new CourseAllocationInfoEntity();
                        courseAllocationInfoEntity.setStudentId(student.getStudentId());
                        courseAllocationInfoEntity.setCourseId(course.getCourseMasterId());
                        courseAllocationInfoEntity.setCoursePeriodId(null);
                        courseAllocationInfoEntity.setSectionId(null);
                        courseAllocationInfoEntity.setBatchId(0);
                        courseAllocationInfoRepository.save(courseAllocationInfoEntity);
                    }
                });
            }

            updateRoomAllotmentInfo(student);
            
//            // Fetch the existing entity if it exists
            if (Objects.nonNull(student.getPreviousId())){
                Optional<StudentBioDataFormDetailEntity> optionalStudentBioDataForm = studentBioDataFormDetailRepository
                        .findTopByStudentIdAndActiveFlag(student.getPreviousId(), ModelConstants.STATUS_ACTIVE);
                StudentBioDataFormDetailEntity studentBioDataForm = null;
                if (optionalStudentBioDataForm.isPresent()) {
                    studentBioDataForm = optionalStudentBioDataForm.get();
                    StudentBioDataFormDetailMapper.INSTANCE.onUpdateEntity(studentBioDataForm, student);
                    studentBioDataForm.onUpdate();
                    studentBioDataFormDetailRepository.save(studentBioDataForm);
                }
            }
//            // If an existing entity is found, update the studentEntity with the existing one
//            if (optionalStudentBioDataForm.isPresent()) {
//            } else {
//            	studentBioDataForm = StudentBioDataFormDetailMapper.INSTANCE.onCreateEntity(student);
//            	studentBioDataForm.onCreate();
//            }
//            studentBioDataFormDetailRepository.save(studentBioDataForm);
        });
        addLog(tag, 0, commonResponseUtil.getMessage("upload.wait.response"));
        addLog(tag, 0, commonResponseUtil.getMessage("upload.success"));

    }

    private void updateRoomAllotmentInfo(StudentBulkInfoDto student) {
        StudentHostelInfoDetailsDto studentHostelInfoDetailsDto = StudentHostelInfoDetailsDto.builder().build();
        var currentStudentDetails = hostelRoomAllotmentRepository.findByStudentIdEqualsIgnoreCaseAndActiveFlagAndVacateDateIsNullAndShiftedDateIsNull(
                        student.getPreviousId(), ModelConstants.STATUS_ACTIVE)
                .orElse(null);
        if (Objects.nonNull(currentStudentDetails)) {
            studentHostelInfoDetailsDto.setChangedStudentId(student.getStudentId());
            studentHostelInfoDetailsDto.setFloorId(currentStudentDetails.getBuildingId());
            studentHostelInfoDetailsDto.setRoomAllotmentId(currentStudentDetails.getRoomAllotmentId());
            studentHostelInfoDetailsDto.setRoomId(currentStudentDetails.getRoomId());
            studentHostelInfoDetailsDto.setSubRoomId(currentStudentDetails.getSubRoomId());
            studentHostelInfoDetailsDto.setIsMissing(currentStudentDetails.getIsMissing());
            hostelIndividualAllotmentService.reAllocateStudent(studentHostelInfoDetailsDto, currentStudentDetails,true);
        }
    }

    private void cellError(String msg, int column, int rowCount, StudentBulkInfoDto studentBulkInfoDto) {
        String[] columns = ModelConstants.EXCEL_COLUMNS;
        if (column == 0 && rowCount == 0) studentBulkInfoDto.setExcelErrorMsg(msg);
        else studentBulkInfoDto.setExcelErrorMsg(" Cell " + columns[column] + (rowCount + 1) + " : " + msg);
        System.out.println(studentBulkInfoDto.getExcelErrorMsg());
        studentBulkInfoDto.setError("error");
    }

	public Workbook downloadAllStudentDetailsReport() throws Exception {
		String sheetName = messageSource.getMessage("message.label.all.student.details", null, Locale.getDefault());
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(sheetName);

		int columnCount = ExcelConstants.ALL_STUDENT_DETAILS_HEADER_DATA.length;
		String[] headerData = ExcelConstants.ALL_STUDENT_DETAILS_HEADER_DATA;
		String[] headerDataWidth = ExcelConstants.ALL_STUDENT_DETAILS_HEADER_DATA_WIDTH;

		XSSFCellStyle style3 = excelUtility.setDataStyle(workbook);
		XSSFCellStyle centerAlignStyle = excelUtility.setCenterAlignStyle(workbook);

		Row row0 = sheet.createRow(0);
		Cell cell0 = row0.createCell(0);
		cell0.setCellValue(messageSource.getMessage("message.label.student.details.report", null, Locale.getDefault()));
		cell0.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));

		Row row1 = sheet.createRow(1);
		Cell cell1 = row1.createCell(0);
		SimpleDateFormat sdf = new SimpleDateFormat(
				messageSource.getMessage("session.date.format", null, Locale.getDefault()));
		String reportDate = messageSource.getMessage("message.label.report.date.colon", null, Locale.getDefault())
				+ sdf.format(new Date());
		cell1.setCellValue(reportDate);
		cell1.setCellStyle(centerAlignStyle);
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, columnCount - 1));

		Row row2 = sheet.createRow(2);
		excelUtility.createHeader(row2, 0, headerData, workbook);

		IntStream.range(0, headerData.length).forEach(i -> {
			int width = Integer.parseInt(headerDataWidth[i]);
			sheet.setColumnWidth(i, width);
		});

		List<AllStudentsDetailsViewEntity> entityList = allStudentsDetailsViewRepository
				.findAllByStudentStatus(ModelConstants.ACTIVE);

		AtomicInteger rowCount = new AtomicInteger(2);

		entityList.forEach(student -> {
			Row row = sheet.createRow(rowCount.incrementAndGet());

			String[] values = { String.valueOf(rowCount.get() - 2), getNonNullValue(student.getStudentId()),
					getNonNullValue(student.getStudentName()), getNonNullValue(student.getHostelName()),
					getNonNullValue(student.getRoomNumber()), getNonNullValue(student.getSeat()),
					getNonNullValue(student.getGender()), getNonNullValue(student.getDeptName()),
					student.getFromDate() != null ? student.getFromDate().toString() : "",
					getNonNullValue(student.getPreviousId()),
					student.getDob() != null ? student.getDob().toString() : "", getNonNullValue(student.getCategory()),
					getNonNullValue(student.getStudentMobile()),
					"", "", "", "", "", getNonNullValue(student.getStudentAddress()),
					getNonNullValue(student.getStudentPersonalEmail()), getNonNullValue(student.getAuth()) };

			for (int colIdx = 0; colIdx < values.length; colIdx++) {
				excelUtility.createAndSetColumn(sheet, row, colIdx, -1, values[colIdx], style3);
			}
		});

		for (int i = 0; i < columnCount; i++) {
			sheet.autoSizeColumn(i);
		}

		return workbook;
	}
	
	private String getNonNullValue(Long value) {
	    return value != null ? String.valueOf(value) : "";
	}
	
	private String getNonNullValue(String value) {
		return value == null ? "" : value;
	}

    public StudentBulkInfoDto validateStudentAndSave(StudentBulkInfoDto student){
        Integer column = 0;
        Integer rowNumber = 0;

        StudentBulkInfoDto studentBulkInfoDto = new StudentBulkInfoDto();

        StringBuilder errorDetails = new StringBuilder();
        ArrayList<String> errorList = new ArrayList<>();
        boolean error = false;

        String fieldValue = student.getStudentId();
        Boolean cellNotEmptyCheck = StringUtils.isNotEmpty(fieldValue);

        if (cellNotEmptyCheck) {
            boolean isValidStudentId = fieldValue.matches(ModelConstants.ALPHA_NUMERIC_REGEX);
            if (isValidStudentId) {
                student.setStudentId(fieldValue.trim().toUpperCase());
            } else {
                error = true;
                errorList.add("studentID- "+ messageSource.getMessage("message.student.error.student.id.alphanumeric", null, Locale.getDefault()));
            }

            if (studentDetailsInfoRepository.existsByActiveFlagAndStudentIdAndSettlementFlag(ModelConstants.STATUS_ACTIVE, student.getStudentId(), ModelConstants.YES)) {
                error = true;
                errorList.add("studentID- Student ID " + student.getStudentId() + messageSource.getMessage("message.student.error.student.settlement.completed", null, Locale.getDefault()));
            }
            if (!studentDetailsInfoRepository.checkStudentIdExistInPreviousId(student.getStudentId()).isEmpty()) {
                error = true;
                errorList.add("studentID - Student ID " + student.getStudentId() + messageSource.getMessage("message.student.error.student.assigned", null, Locale.getDefault()));
            }

        } else {
            error = true;
            errorList.add("studentID- "+messageSource.getMessage("message.student.error.student.id.empty", null, Locale.getDefault()));
        }
        fieldValue = student.getFirstName();
        cellNotEmptyCheck = StringUtils.isNotEmpty(fieldValue);

        // First Name
        if (cellNotEmptyCheck) {
            boolean isValidName = fieldValue.matches(ModelConstants.ALPHA_WITH_SPACE);
            if (isValidName) {
                student.setFirstName(CommonService.capitalizeEachWord(fieldValue.trim()));
            } else {
                error = true;
                errorList.add("firstName- "+messageSource.getMessage("message.student.error.field.alphabet", null, Locale.getDefault()));
            }
        } else {
            error = true;
            errorList.add("firstName- "+messageSource.getMessage("message.student.error.firstname.empty", null, Locale.getDefault()));
        }

        fieldValue = student.getLastName();
        cellNotEmptyCheck = StringUtils.isNotEmpty(fieldValue);
        // Last Name
        if (cellNotEmptyCheck) {
            boolean isValidLastName = fieldValue.matches(ModelConstants.ALPHA_WITH_SPACE);
            if (isValidLastName) {
                student.setLastName(fieldValue.trim());
            } else {
                error = true;
                errorList.add("lastName- "+messageSource.getMessage("message.student.error.field.alphabet", null, Locale.getDefault()));
            }
        }
        fieldValue = student.getGender();
        cellNotEmptyCheck = StringUtils.isNotEmpty(fieldValue);
        // Gender
        if (cellNotEmptyCheck) {
            student.setGender(fieldValue);
            boolean containsGender = Arrays.asList(ModelConstants.GENDER_LIST).contains(student.getGender());
            if (!containsGender) {
                error = true;
                errorList.add("genderType2- "+messageSource.getMessage("message.student.error.gender.valid", null, Locale.getDefault()));
            }
        } else {
            errorList.add("genderType2- "+messageSource.getMessage("message.student.error.gender.valid", null, Locale.getDefault()));
            error = true;
        }

        fieldValue = student.getCourse();
        cellNotEmptyCheck = StringUtils.isNotEmpty(fieldValue);
        // Course Name
        if (cellNotEmptyCheck) {
            student.setCourse(fieldValue.trim());
            if (!courseMasterRepository.existsByCourseMasterHeadIgnoreCaseAndActiveFlag(student.getCourse(), ModelConstants.STATUS_ACTIVE)) {
                error = true;
                errorList.add("courseCode- "+messageSource.getMessage("message.student.error.course.valid ", null, Locale.getDefault()));
            }
        } else {
            errorList.add("courseCode- "+messageSource.getMessage("message.student.error.course.empty", null, Locale.getDefault()));
            error = true;
        }

        fieldValue = student.getPreviousId();
        cellNotEmptyCheck = StringUtils.isNotEmpty(fieldValue);
        //Previous Id
        if (cellNotEmptyCheck) {
            String previousStudId = fieldValue.trim().toUpperCase();
            String[] previousArray = previousStudId.split(",");
            // Initialize and populate the prevId array
            String[] prevId = new String[previousArray.length];
            for (int i = 0; i < previousArray.length; i++) {
                prevId[i] = previousArray[i].trim();
            }
            // Set the previous IDs without brackets
            student.setPreviousId(String.join(",", prevId));


            // Check if the previous ID is assigned to another roll number
            int checkStudentExist = studentDetailsInfoService.checkPreviousIdExist(student.getStudentId(), previousStudId);
            if (checkStudentExist > 0) {
                error = true;
                errorList.add("previousID- "+"Previous ID " + previousStudId + messageSource.getMessage("message.student.error.previousID.valid", null, Locale.getDefault()));
            }

            // Check if the previous student ID exists
            String prevIdCheck = studentDetailsInfoService.checkDuplicateNoWithSeparate(student.getPreviousId());
            if (!Objects.equals(prevIdCheck, "")) {
                error = true;
                errorList.add("previousID- "+"Previous Student ID " + prevIdCheck + messageSource.getMessage("message.student.error.previousID.not.exist", null, Locale.getDefault()));
            }
        }
        List<StudentBulkInfoDto> students = new ArrayList<>();

        // Add errors for the row to the studentBulkInfoDto if any
        if (error) {
            studentBulkInfoDto.setErrorList(errorList);
        } else {
            student.setEmailId(student.getStudentId().toUpperCase() + simsConfigDataService.getSimConfigValue(SimsConfigType.STUDENT_MAIL_ID.name()));
            students.add(student);
        }

        if (!students.isEmpty()) saveStudentDetailsInfo(students,"addStudent");
        return studentBulkInfoDto;
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
