package com.iitm.hosteldine.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import com.iitm.hosteldine.repository.hostel.HostelRoomAllotmentRepository;
import com.iitm.hosteldine.service.hostel.HostelRoomInfoService;
import org.apache.logging.log4j.util.Strings;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.StudentBioDataFamilyInfoDto;
import com.iitm.hosteldine.dto.StudentBioDataFormDetailDto;
import com.iitm.hosteldine.dto.StudentDetailsInfoMapper;
import com.iitm.hosteldine.dto.collegeInfo.CourseMasterDto;
import com.iitm.hosteldine.dto.hostel.HostelMasterDto;
import com.iitm.hosteldine.dto.hostel.HostelRoomAllotmentInfoDto;
import com.iitm.hosteldine.dto.hostel.HostelRoomInfoDto;
import com.iitm.hosteldine.dto.student.AllStudentsDetailsViewDto;
import com.iitm.hosteldine.dto.student.StudentDetailsDto;
import com.iitm.hosteldine.dto.student.StudentDetailsInfoDto;
import com.iitm.hosteldine.dto.student.StudentDto;
import com.iitm.hosteldine.entity.student.StudentDetailsInfoEntity;
import com.iitm.hosteldine.form.common.PaginationForm;
import com.iitm.hosteldine.mapper.StudentBioDataFamilyInfoMapper;
import com.iitm.hosteldine.mapper.StudentBioDataFormDetailMapper;
import com.iitm.hosteldine.mapper.collegeInfo.CourseMasterMapper;
import com.iitm.hosteldine.mapper.hostel.HostelMasterMapper;
import com.iitm.hosteldine.mapper.hostel.HostelRoomAllotmentInfoMapper;
import com.iitm.hosteldine.mapper.hostel.HostelRoomInfoMapper;
import com.iitm.hosteldine.model.StudentBioDataFamilyInfoEntity;
import com.iitm.hosteldine.model.StudentBioDataFormDetailEntity;
import com.iitm.hosteldine.model.collegeInfo.CourseMasterEntity;
import com.iitm.hosteldine.model.hostel.HostelMasterEntity;
import com.iitm.hosteldine.model.hostel.HostelRoomAllotmentInfoEntity;
import com.iitm.hosteldine.model.hostel.HostelRoomInfoEntity;
import com.iitm.hosteldine.repository.CourseAllocationInfoRepository;
import com.iitm.hosteldine.repository.StudentBioDataFamilyInfoRepository;
import com.iitm.hosteldine.repository.StudentBioDataFormDetailRepository;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentBioDataService {
    private final HostelRoomAllotmentRepository hostelRoomAllotmentRepository;
    private final HostelRoomInfoService hostelRoomInfoService;
    private final StudentBioDataFormDetailRepository studentBioDataFormDetailRepository;
    private final StudentBioDataFamilyInfoRepository studentBioDataFamilyInfoRepository;
    private final FileService fileService;
    private final SimsConfigDataService simsConfigDataService;
    private final MessageSource messageSource;
    private final CourseAllocationInfoRepository courseAllocationInfoRepository;
    private final PdfActionService pdfActiveService;
	private final AllStudentsDetailsViewService allStudentsDetailsViewService;

    private static final String SPACE = " ";
    private static final String COMMA = ", ";
    private static final Text COMMA_TEXT = new Text(COMMA);
    private static final String SLASH = "\\";
    private static final int DEFAULT_BULLET_INDENT = 10;
    private static final int DEFAULT_DECLARATION_INDENT = 10;
    private static final String DEFAULT_BULLET_SYMBOL_L1 = "•";
    private static final UnitValue VALUE_100_P = UnitValue.createPercentValue(100);
    private static final UnitValue VALUE_40_P = UnitValue.createPercentValue(40);
    private static final UnitValue VALUE_30_P = UnitValue.createPercentValue(30);
    private static final UnitValue VALUE_33_P = UnitValue.createPercentValue(100 / 3F);
    private final StudentBioDataFamilyInfoService studentBioDataFamilyInfoService;
    private final StudentDetailsInfoService studentDetailsInfoService;

    @Transactional
    public String saveStudentRegistration(StudentBioDataFormDetailDto form) throws Exception {
        System.out.println("Biodata StudentId Service---------------"+ form.getStudentId());
        StudentBioDataFormDetailEntity entity = null;
        boolean isNew = form.getId() == null;
        if (form.getPanNum() != null && form.getPanNum().isEmpty()) form.setPanNum(null);
        if (form.getStudentPersonalEmail() != null && form.getStudentPersonalEmail().isEmpty()) form.setStudentPersonalEmail(null);
        if (form.getFacultyEmail() != null && form.getFacultyEmail().isEmpty()) form.setFacultyEmail(null);
        if (form.getOtherInfo() != null && form.getOtherInfo().isEmpty()) form.setOtherInfo(null);
        if (form.getApplicationNumber() != null && form.getApplicationNumber().isEmpty()) form.setApplicationNumber(null);
        if (isNew) {
			Optional<StudentBioDataFormDetailEntity> optionalBioData = studentBioDataFormDetailRepository
					.findTopByStudentIdAndActiveFlag(SecurityCtxUtil.userId(), ModelConstants.STATUS_ACTIVE);
			if (!optionalBioData.isPresent()) {
		        if (!SecurityCtxUtil.ANONYMOUS_USER.equals(SecurityCtxUtil.userId())) {
		            StudentDetailsInfoDto studentDetailsInfoDto = studentDetailsInfoService.getStudentInfoDetails(
		                    Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase(Locale.ROOT));
		            if(studentDetailsInfoDto.getStudentId()!=null) {
                        String lastName = Objects.nonNull(studentDetailsInfoDto.getLastName()) ? (ModelConstants.SPACE + studentDetailsInfoDto.getLastName()) : ModelConstants.EMPTY_STRING;
                        form.setStudentName((studentDetailsInfoDto.getFirstName() + lastName).trim());
                        form.setGender(studentDetailsInfoDto.getGender());
                        form.setApplicationNumber(studentDetailsInfoDto.getStudentId());
                        form.setStudentId(studentDetailsInfoDto.getStudentId());

                    }
                }
                String fileName = form.getStudentId() + ModelConstants.UNDERSCORE;
                form.setRegistrationDate(LocalDate.now());
                form.setImageLocation(fileName + ModelConstants.FILE_STUDENT_PROFILE);
                form.setStudentSignLocation(fileName + ModelConstants.FILE_STUDENT_SIGNATURE);
                form.setParentSignLocation(fileName + ModelConstants.FILE_PARENT_SIGNATURE);
                form.setRegistrationDate(LocalDate.now());
		        entity = StudentBioDataFormDetailMapper.INSTANCE.toStudentBioDataFormDetailEntity(form);
    		}
        } else {
            entity = studentBioDataFormDetailRepository.findById(form.getId()).orElse(null);
            if (entity != null) {
                if (form.getAadhaarNumber() != null) entity.setAadhaarNumber(form.getAadhaarNumber());
                if (form.getPanNum() != null) entity.setPanNum(form.getPanNum());
                if (form.getOtherInfo() != null) entity.setOtherInfo(form.getOtherInfo());
                if (form.getApplicationNumber() != null) entity.setApplicationNumber(form.getApplicationNumber());
                if (form.getStudentPersonalEmail() != null) entity.setStudentPersonalEmail(form.getStudentPersonalEmail());
				if (form.getFacultyName() != null && !form.getFacultyName().isEmpty()
						&& !("0").equals(form.getFacultyName())) {
                    entity.setFacultyName(form.getFacultyName());
                    entity.setFacultyContactNo(form.getFacultyContactNo());
                    entity.setFacultyEmail(form.getFacultyEmail());
				} else {
					if (entity.getFacultyName() != null && !entity.getFacultyName().equals("0")) {
						if ((entity.getFacultyEmail() == null || entity.getFacultyEmail().isEmpty())) {
							entity.setFacultyEmail(form.getFacultyEmail());
						}
						if (entity.getFacultyContactNo() == null) {
							entity.setFacultyContactNo(form.getFacultyContactNo());
						}
					}
				}
                form.setStudentId(entity.getStudentId());
                if (form.getPwd() != null) entity.setPwd(form.getPwd());
                if (form.getPwdPercentage() != null) entity.setPwdPercentage(form.getPwdPercentage());
                String fileName = form.getStudentId() + ModelConstants.UNDERSCORE;
                if (entity.getImageLocation() == null) entity.setImageLocation(fileName + ModelConstants.FILE_STUDENT_PROFILE);
                if (entity.getStudentSignLocation() == null) entity.setStudentSignLocation(fileName + ModelConstants.FILE_STUDENT_SIGNATURE);
                if (entity.getParentSignLocation() == null) entity.setParentSignLocation(fileName + ModelConstants.FILE_PARENT_SIGNATURE);
            } else {
                throw new RuntimeException("Record not found");
            }
        }
		if (entity != null) {
            entity = studentBioDataFormDetailRepository.save(entity);
            if (entity.getId() > 0) {
                form.setId(entity.getId());
                studentBioDataFamilyInfoService.saveFamilyDetails(form, isNew, false);
                String fileName = form.getStudentId() + ModelConstants.UNDERSCORE;
                if (form.getStudentProfile() != null) {
                    fileService.encodeFile(ModelConstants.IMAGE_BIO_DATA_PROFILE, form.getStudentProfile().getBytes(),
                            fileName + ModelConstants.FILE_STUDENT_PROFILE);
                }
                if (form.getStudentProfile() != null) {
                    fileService.encodeFile(ModelConstants.IMAGE_BIO_DATA_STUDENT_SIGN, form.getStudentSignature().getBytes(),
                            fileName + ModelConstants.FILE_STUDENT_SIGNATURE);
                }
                if (form.getStudentProfile() != null) {
                    fileService.encodeFile(ModelConstants.IMAGE_BIO_DATA_PARENT_SIGN, form.getParentSignature().getBytes(),
                            fileName + ModelConstants.FILE_PARENT_SIGNATURE);
                }
            }
        }
        return entity != null && entity.getId() > 0 ? entity.getId().toString() : null;
    }

    public Resource getStudentBioDataPDF(String studentId,boolean needDeclaration) throws Exception {
        String tempFileLocation = simsConfigDataService.getSimConfigValue(SimsConfigDataService.TEMP_FILE_LOCATION);
        Optional<StudentBioDataFormDetailEntity> optionalStudentBioDataForm = studentBioDataFormDetailRepository
                .findTopByStudentIdAndActiveFlag(studentId, ModelConstants.STATUS_ACTIVE);
        if (optionalStudentBioDataForm.isPresent()) {
            StudentBioDataFormDetailDto studentBioDataFormDetailDto = StudentBioDataFormDetailMapper.INSTANCE.fromStudentBioDataFormDetailEntity(optionalStudentBioDataForm.get());
            List<StudentBioDataFamilyInfoEntity> familyInfoEntityList = studentBioDataFamilyInfoRepository.findAllByBioDataId(studentBioDataFormDetailDto.getId());
            List<StudentBioDataFamilyInfoDto> familyInfoDtoList = new ArrayList<>();
            familyInfoEntityList.forEach(familyEntity -> familyInfoDtoList.add(StudentBioDataFamilyInfoMapper.INSTANCE.fromStudentBioDataFamilyInfoEntity(familyEntity)));
//            String imageFileName = studentBioDataFormDetailDto.getStudentId() + ModelConstants.UNDERSCORE;
            String profileFileName = getFileName(studentBioDataFormDetailDto, studentBioDataFormDetailDto.getStudentId(), StudentBioDataFormDetailDto::getImageLocation, ModelConstants.FILE_STUDENT_PROFILE);
            String studentSign = getFileName(studentBioDataFormDetailDto, studentBioDataFormDetailDto.getStudentId(), StudentBioDataFormDetailDto::getStudentSignLocation, ModelConstants.FILE_STUDENT_SIGNATURE);
            String parentSign = getFileName(studentBioDataFormDetailDto, studentBioDataFormDetailDto.getStudentId(), StudentBioDataFormDetailDto::getParentSignLocation, ModelConstants.FILE_PARENT_SIGNATURE);
            byte[] studentProfileImage = fileService.getDecodedFile(ModelConstants.IMAGE_BIO_DATA_PROFILE, profileFileName);
            byte[] studentSignatureImage = fileService.getDecodedFile(ModelConstants.IMAGE_BIO_DATA_STUDENT_SIGN, studentSign);
            byte[] parentSignatureImage = fileService.getDecodedFile(ModelConstants.IMAGE_BIO_DATA_PARENT_SIGN, parentSign);
            String bioDataFileName = studentId.toUpperCase(Locale.ROOT) + "_Bio_Data_" + System.currentTimeMillis() + ".pdf";
            File file = new File(tempFileLocation + bioDataFileName);
            //CourseAllocationInfoEntity courseDetails = courseAllocationInfoRepository.findByActiveFlagAndStudentId( ModelConstants.STATUS_ACTIVE,studentBioDataFormDetailDto.getStudentId());
            String courseName = courseAllocationInfoRepository.findCourseNameByActiveFlagAndStudentId(ModelConstants.STATUS_ACTIVE, studentBioDataFormDetailDto.getStudentId());

            boolean created;
            if (!file.getParentFile().exists()) {
                created = file.getParentFile().mkdirs();
            } else {
                created = true;
            }
            if (created) {
                created = file.createNewFile();
            }
            if (created) {
                ClassLoader classLoader = getClass().getClassLoader();
                InputStream imageStream = classLoader.getResourceAsStream("static/assets/img/client/hm_iitm_logo.png");
                Image headerLogo = null;
                if (imageStream != null) {
                    headerLogo = new Image(ImageDataFactory.create(imageStream.readAllBytes()));
                    headerLogo.setHeight(50);
                    headerLogo.setWidth(50);
                }

                Image studentProfile = null;
                if (studentProfileImage != null) {
                    try {
                    studentProfile = new Image(ImageDataFactory.create(studentProfileImage));
                    studentProfile.setWidth(60);
                    studentProfile.setHeight(80);
                    } catch (Exception e) {
                        log.error("Error at forming student profile.", e);
                    }
                }

                Image studentSignature = null;
                if (studentSignatureImage != null) {
                    try {
                    studentSignature = new Image(ImageDataFactory.create(studentSignatureImage));
                    studentSignature.setWidth(80);
                    studentSignature.setHeight(20);
                    } catch (Exception e) {
                        log.error("Error at forming student sign.", e);
                    }
                }

                Image parentSignature = null;
                if (parentSignatureImage != null) {
                    try {
                        parentSignature = new Image(ImageDataFactory.create(parentSignatureImage));
                        parentSignature.setWidth(80);
                        parentSignature.setHeight(20);
                        parentSignature.setBorder(null);
                    } catch (Exception e) {
                        log.error("Error at forming parent sign.", e);
                    }
                }

                LineSeparator lineSeparator = new LineSeparator(new SolidLine());
                lineSeparator.setWidth(VALUE_100_P);

                Cell emptySpace = new Cell();
                emptySpace.setHeight(10F);

                PdfWriter writer = new PdfWriter(tempFileLocation + bioDataFileName);
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc);

                Table table = new Table(new float[]{1, 3, 1}); // 3 columns
                table.setWidth(VALUE_100_P);

                Cell logoCell = new Cell();
                logoCell.add(headerLogo);
                logoCell.setHorizontalAlignment(HorizontalAlignment.LEFT);
                logoCell.setBorder(null);

                Cell profileCell = new Cell();
                if (studentProfile != null) {
                    profileCell.add(studentProfile);
                } else {
                    profileCell.add(new Paragraph("No Image"));
                }
                profileCell.setHorizontalAlignment(HorizontalAlignment.RIGHT);
                profileCell.setBorder(null);


                Paragraph title = new Paragraph("Office of Hostel Management\nIIT - Madras")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                        .setFontSize(16); // Adjust as needed
                Cell titleCell = new Cell();
                titleCell.add(title);
                titleCell.setHorizontalAlignment(HorizontalAlignment.CENTER);

                Paragraph subtitle = new Paragraph("Student Bio-Data Form")
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                        .setFontSize(14); // Adjust as needed
                Cell subtitleCell = new Cell();
                subtitleCell.add(subtitle);
                subtitleCell.setHorizontalAlignment(HorizontalAlignment.CENTER);

                Cell headingTextCell = new Cell();
                headingTextCell.add(titleCell);
                headingTextCell.add(subtitleCell);
                headingTextCell.setBorder(null);

                table.addCell(logoCell);
                table.addCell(headingTextCell);
                table.addCell(profileCell);

                Table headerTable = new Table(new float[]{2, 3, 2, 3});
                headerTable.setWidth(VALUE_100_P);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.FRONTEND_DATE_FORMAT);
                addTableTextValue("Student Id: ", studentBioDataFormDetailDto.getStudentId(), headerTable);
                addTableTextValue("Application No: ", studentBioDataFormDetailDto.getApplicationNumber(), headerTable);
                addTableTextValue("Student Name: ", studentBioDataFormDetailDto.getStudentName(), headerTable, new int[]{1, 3});
                addTableTextValue("Gender: ", studentBioDataFormDetailDto.getGender(), headerTable);
                addTableTextValue("Date of Birth: ", studentBioDataFormDetailDto.getDob() != null
                        ? studentBioDataFormDetailDto.getDob().format(formatter) : null, headerTable);
                addTableTextValue("Personal Email: ", studentBioDataFormDetailDto.getStudentPersonalEmail(), headerTable);
                addTableTextValue("Blood Group: ", studentBioDataFormDetailDto.getBloodGroup(), headerTable);
                addTableTextValue("Preferred Mess: ", studentBioDataFormDetailDto.getMessName(), headerTable);
                addTableTextValue("Mess Name: ", "N/A", headerTable);
                addTableTextValue("Address: ", studentBioDataFormDetailDto.getStudentAddress(), headerTable, new int[]{1, 3});
                addTableTextValue("Special Instructions: ", studentBioDataFormDetailDto.getOtherInfo(), headerTable, new int[]{1, 3});
                addTableTextValue("Mobile Number: ", studentBioDataFormDetailDto.getStudentMobile(), headerTable);
                addTableTextValue("Category: ", studentBioDataFormDetailDto.getCategory() != null
                        ? studentBioDataFormDetailDto.getCategory().toUpperCase() : null, headerTable);
                addTableTextValue("Aadhaar Number: ", studentBioDataFormDetailDto.getAadhaarNumber(), headerTable);
                addTableTextValue("PAN: ", studentBioDataFormDetailDto.getPanNum(), headerTable);
                addTableTextValue("Faculty Name: ", studentBioDataFormDetailDto.getFacultyName(), headerTable);
                addTableTextValue("Faculty Contact: ", studentBioDataFormDetailDto.getFacultyContactNo(), headerTable);
                addTableTextValue("Faculty Email: ", studentBioDataFormDetailDto.getFacultyEmail(), headerTable);
                addTableTextValue("Course Name: ", courseName, headerTable);
                addTableTextValue("PWD: ", Objects.nonNull(studentBioDataFormDetailDto.getPwd()) ?
                        studentBioDataFormDetailDto.getPwd():Constants.NA, headerTable);

                Table hostelTable = new Table(new float[]{2, 3, 2, 3});
                hostelTable.setWidth(VALUE_100_P);
                AllStudentsDetailsViewDto allStudentsDetailsViewDto = allStudentsDetailsViewService.getCompleteStudentDetails(studentBioDataFormDetailDto.getStudentId());
                String hostelName = ModelConstants.NOT_APPLICABLE, roomNo = ModelConstants.NOT_APPLICABLE, seat = ModelConstants.NOT_APPLICABLE;
                if (Objects.nonNull(allStudentsDetailsViewDto)) {
                    hostelName = Objects.nonNull(allStudentsDetailsViewDto.getHostelName()) && !allStudentsDetailsViewDto.getHostelName().isEmpty() ? allStudentsDetailsViewDto.getHostelName() : ModelConstants.NOT_APPLICABLE;
                    roomNo = Objects.nonNull(allStudentsDetailsViewDto.getRoomNumber()) && !allStudentsDetailsViewDto.getRoomNumber().isEmpty() ? allStudentsDetailsViewDto.getRoomNumber() : ModelConstants.NOT_APPLICABLE;
                    seat = Objects.nonNull(allStudentsDetailsViewDto.getSeat()) && !allStudentsDetailsViewDto.getSeat().isEmpty() ? allStudentsDetailsViewDto.getSeat() : ModelConstants.NOT_APPLICABLE;
                }
                addTableTextValue("Hostel Name:", hostelName, hostelTable);
                addTableTextValue("Room No: ", roomNo, hostelTable);
                addTableTextValue("Seat: ",  seat, hostelTable);

                Boolean needIdProof =
                        Boolean.valueOf(simsConfigDataService.getSimConfigValue(SimsConfigDataService.NEED_ID_PROOF));

                Map<Table,Table> familyDetailsTable = new HashMap<>();
                if (!familyInfoDtoList.isEmpty()) {
                    familyInfoDtoList.forEach(familyMember -> {
                        Table familyProofTable;
                        if (needIdProof) {

                            byte[] familyIdProof = this.getFamilyIdProof(familyMember.getProofFileName());
                            Image familyProofImage = null;

                            // Check if the proof file is a PDF or an image
                            if (familyIdProof != null) {
                                String proofFileName = familyMember.getProofFileName().toLowerCase();

                                if (proofFileName.endsWith(".pdf")) {
                                    // Convert PDF to image (first page) and set as familyProofImage
                                    try {
                                        familyProofImage = convertPdfToImage(familyIdProof);
                                        familyProofImage.setWidth(525);
                                        familyProofImage.setHeight(560);
                                    } catch (IOException e) {
                                        e.printStackTrace(); // Handle the error appropriately
                                    }
                                } else {
                                    // Handle image files as usual
                                    familyProofImage = new Image(ImageDataFactory.create(familyIdProof));
                                    familyProofImage.setWidth(525);
                                    familyProofImage.setHeight(525);
                                }
                            }

                            Cell proofCell = new Cell();
                            if (Objects.nonNull(familyProofImage)) {
                                proofCell.add(familyProofImage);
                            } else {
                                // If no image is available, display "No ID proof"
                                proofCell.add(new Paragraph("No ID proof"));
                            }

                            proofCell.setHorizontalAlignment(HorizontalAlignment.RIGHT);
                            proofCell.setBorder(null);

                            familyProofTable = new Table(new float[]{1});
                            familyProofTable.setWidth(VALUE_100_P);
                            familyProofTable.addCell(proofCell);
                        } else {
                            familyProofTable = null;
                        }

                        // Family details table
                        Table familyDetailsList = new Table(new float[]{2, 3, 2, 3});
                        familyDetailsList.setWidth(VALUE_100_P);
                        addTableTextValue("Relation Type: ", familyMember.getRelationType(), familyDetailsList);
                        addTableTextValue("Age: ", familyMember.getAge(), familyDetailsList);
                        addTableTextValue("Relation Name: ", familyMember.getRelationName(), familyDetailsList, new int[]{1, 3});
                        addTableTextValue("Occupation: ", familyMember.getOccupation(), familyDetailsList);
                        addTableTextValue("Mobile No.: ", familyMember.getMobileNo(), familyDetailsList);
                        addTableTextValue("Email: ", familyMember.getEmail(), familyDetailsList);
                        addTableTextValue("Annual Income: ", familyMember.getIncome() != null ?
                                CommonService.formatAmountToDecimalPlaces(familyMember.getIncome()) : 0.00, familyDetailsList);
                        addTableTextValue("Address: ", Objects.nonNull(familyMember.getAddress()) ?
                                familyMember.getAddress() : Constants.NA, familyDetailsList, new int[]{1, 3});

                        // Combine family details with proof table
                        familyDetailsTable.put(familyDetailsList, familyProofTable);
                    });
                }

                Table studentDeclarationTable = null;
                Table studentDeclarationSignTable = null;
                Table parentDeclarationTable = null;
                Table parentDeclarationSignTable = null;
                Table vehicleDeclarationTable = null;
                Table vehicleDeclarationSignTable = null;
                if (needDeclaration) {
                    studentDeclarationTable = new Table(1);
                    String studentNameText = (studentBioDataFormDetailDto.getStudentName().toUpperCase());
                    String signedParentNameText = (studentBioDataFormDetailDto.getSignedParentName() != null
                            ? studentBioDataFormDetailDto.getSignedParentName().toUpperCase()
                            : "");
                    String rollNoText = (studentBioDataFormDetailDto.getStudentId() != null
                            ? studentBioDataFormDetailDto.getStudentId().toUpperCase()
                            : "");

                    Text studDeclarationPrefix = new Text(messageSource.getMessage("message.label.student.prefix", null, Locale.getDefault()));
                    Text studDeclarationStudentPrefix = new Text(messageSource.getMessage("message.label.student.parents.prefix", null, Locale.getDefault()));
                    Text studDeclarationAdmission = new Text(messageSource.getMessage("message.label.admission.declaration", null, Locale.getDefault()));

                    Paragraph studentDeclaration1 = getDeclarationParagraph();
                    studentDeclaration1.add(studDeclarationPrefix);
                    studentDeclaration1.add(new Text(studentNameText).setBold()).add(SPACE);
                    studentDeclaration1.add(studDeclarationStudentPrefix);
                    studentDeclaration1.add(new Text(signedParentNameText).setBold()).add(SPACE);
                    studentDeclaration1.add(studDeclarationAdmission);
                    studentDeclarationTable.addCell(getDefaultCell(studentDeclaration1, VALUE_100_P));

                    com.itextpdf.layout.element.List studentDeclaration1BPList = getDefaultBulletPointList();
                    studentDeclaration1BPList.add(new ListItem(messageSource.getMessage("message.label.student.supreme.court.judgement", null, Locale.getDefault())));
                    studentDeclaration1BPList.add(new ListItem(messageSource.getMessage("message.label.student.regulations.essential.extracts.acknowledgment", null, Locale.getDefault())));
                    studentDeclaration1BPList.add(new ListItem(messageSource.getMessage("message.label.student.provisions.acknowledgment", null, Locale.getDefault())));
                    Paragraph studentDeclaration1BP = new Paragraph();
                    studentDeclaration1BP.add(studentDeclaration1BPList);
                    studentDeclarationTable.addCell(getDefaultCell(studentDeclaration1BP, VALUE_100_P));

                    {
                        Paragraph studentDeclaration2 = getDeclarationParagraph();
                        studentDeclaration2.add(messageSource.getMessage("message.label.regulations.clause.three.understanding", null, Locale.getDefault()));
                        studentDeclarationTable.addCell(getDefaultCell(studentDeclaration2, VALUE_100_P));

                        Paragraph studentDeclaration3 = getDeclarationParagraph();
                        studentDeclaration3.add(messageSource.getMessage("message.label.student.regulations.clause.seven.nine.understanding", null, Locale.getDefault()));
                        studentDeclarationTable.addCell(getDefaultCell(studentDeclaration3, VALUE_100_P));

                        Paragraph studentDeclaration4 = getDeclarationParagraph();
                        studentDeclaration4.add(messageSource.getMessage("message.label.undertaking.statement", null, Locale.getDefault()));
                        studentDeclarationTable.addCell(getDefaultCell(studentDeclaration4, VALUE_100_P));
                    }

                    com.itextpdf.layout.element.List studentDeclarationBPRagging = getDefaultBulletPointList();
                    studentDeclarationBPRagging.add(new ListItem(messageSource.getMessage("message.label.student.commitment.no.ragging", null, Locale.getDefault())));
                    studentDeclarationBPRagging.add(new ListItem(messageSource.getMessage("message.label.student.commitment.ragging.participation", null, Locale.getDefault())));
                    Paragraph studentDeclaration4BP = new Paragraph();
                    studentDeclaration4BP.add(studentDeclarationBPRagging);
                    studentDeclarationTable.addCell(getDefaultCell(studentDeclaration4BP, VALUE_100_P));

                    Paragraph studentDeclaration5 = getDeclarationParagraph();
                    studentDeclaration5.add(messageSource.getMessage("message.label.student.affirmation.ragging.consequences", null, Locale.getDefault()));
                    studentDeclarationTable.addCell(getDefaultCell(studentDeclaration5, VALUE_100_P));

                    Paragraph studentDeclaration6 = getDeclarationParagraph();
                    studentDeclaration6.add(messageSource.getMessage("message.label.ward.not.expelled.debarred", null, Locale.getDefault()));
                    studentDeclarationTable.addCell(getDefaultCell(studentDeclaration6, VALUE_100_P));

                    studentDeclarationSignTable = new Table(new float[]{1, 1, 1});
                    studentDeclarationSignTable.setWidth(VALUE_100_P);
                    studentDeclarationSignTable.addCell(getDefaultCell(new Paragraph(messageSource.getMessage("message.label.student.name", null, Locale.getDefault())), VALUE_40_P));
                    studentDeclarationSignTable.addCell(getDefaultCell(new Paragraph(messageSource.getMessage("message.label.student.mob.no", null, Locale.getDefault())), VALUE_30_P));
                    studentDeclarationSignTable.addCell(getDefaultCell(new Paragraph(messageSource.getMessage("message.label.student.signature", null, Locale.getDefault())), VALUE_30_P));
                    studentDeclarationSignTable.addCell(getDefaultCell(new Paragraph(studentNameText), VALUE_40_P));
                    studentDeclarationSignTable.addCell(getDefaultCell(new Paragraph(String.valueOf(
                            studentBioDataFormDetailDto.getStudentMobile() != null ? studentBioDataFormDetailDto.getStudentMobile() : "")), VALUE_30_P));
                    studentDeclarationSignTable.addCell(getDefaultCell(studentSignature, VALUE_30_P));

                    /* -----------------------------*/
                    parentDeclarationTable = new Table(1);

                    Paragraph parentDeclaration1 = getDeclarationParagraph();
                    parentDeclaration1.add(new Text(messageSource.getMessage("message.label.parents.prefix", null, Locale.getDefault())));
                    parentDeclaration1.add(new Text(signedParentNameText).setBold()).add(SPACE);
                    parentDeclaration1.add(new Text(messageSource.getMessage("message.label.parents.student.prefix", null, Locale.getDefault())));
                    parentDeclaration1.add(new Text(studentNameText).setBold());
                    parentDeclaration1.add(COMMA_TEXT);
                    parentDeclaration1.add(new Text(rollNoText).setBold()).add(SPACE);
                    parentDeclaration1.add(new Text(messageSource.getMessage("message.label.student.context", null, Locale.getDefault())));
                    parentDeclarationTable.addCell(getDefaultCell(parentDeclaration1, VALUE_100_P));

                    com.itextpdf.layout.element.List parentBP1 = getDefaultBulletPointList();
                    parentBP1.add(new ListItem(messageSource.getMessage("message.label.parents.supreme.court.judgement", null, Locale.getDefault())));
                    parentBP1.add(new ListItem(messageSource.getMessage("message.label.parents.regulations.essential.extracts.acknowledgment", null, Locale.getDefault())));
                    parentBP1.add(new ListItem(messageSource.getMessage("message.label.parents.provisions.acknowledgment", null, Locale.getDefault())));
                    Paragraph parentDeclaration1BP = new Paragraph();
                    parentDeclaration1BP.add(parentBP1);
                    parentDeclarationTable.addCell(getDefaultCell(parentDeclaration1BP, VALUE_100_P));

                    Paragraph parentDeclaration2 = getDeclarationParagraph();
                    parentDeclaration2.add(messageSource.getMessage("message.label.regulations.clause.three.understanding", null, Locale.getDefault()));
                    parentDeclarationTable.addCell(getDefaultCell(parentDeclaration2, VALUE_100_P));

                    Paragraph parentDeclaration3 = getDeclarationParagraph();
                    parentDeclaration3.add(new Text(messageSource.getMessage("message.label.parents.regulations.clause.seven.nine.understanding", null, Locale.getDefault())));
                    parentDeclaration3.add(new Text(studentNameText).setBold());
                    parentDeclaration3.add(COMMA_TEXT);
                    parentDeclaration3.add(new Text(rollNoText).setBold()).add(SPACE);
                    parentDeclaration3.add(new Text(messageSource.getMessage("message.label.regulations.ragging.consequences", null, Locale.getDefault())));
                    parentDeclarationTable.addCell(getDefaultCell(parentDeclaration3, VALUE_100_P));

                    Paragraph parentDeclaration4 = getDeclarationParagraph();
                    parentDeclaration4.add(messageSource.getMessage("message.label.undertaking.statement", null, Locale.getDefault()));
                    parentDeclarationTable.addCell(getDefaultCell(parentDeclaration4, VALUE_100_P));

                    com.itextpdf.layout.element.List parentBPRagging = getDefaultBulletPointList();
                    parentBPRagging.add(formParentRaggingPointsPrefix(studentNameText, rollNoText, messageSource.getMessage("message.label.parents.commitment.no.ragging", null, Locale.getDefault())));
                    parentBPRagging.add(formParentRaggingPointsPrefix(studentNameText, rollNoText, messageSource.getMessage("message.label.parents.commitment.ragging.participation", null, Locale.getDefault())));
                    Paragraph parentDeclaration4BP = new Paragraph();
                    parentDeclaration4BP.add(parentBPRagging);
                    parentDeclarationTable.addCell(getDefaultCell(parentDeclaration4BP, VALUE_100_P));

                    Paragraph parentDeclaration5 = getDeclarationParagraph();
                    parentDeclaration5.add(new Text(messageSource.getMessage("message.label.affirmation.ragging.consequences", null, Locale.getDefault()))).add(SPACE);
                    parentDeclaration5.add(new Text(studentNameText).setBold());
                    parentDeclaration5.add(COMMA_TEXT);
                    parentDeclaration5.add(new Text(rollNoText).setBold()).add(SPACE);
                    parentDeclaration5.add(new Text(messageSource.getMessage("message.label.liability.punishment.clause", null, Locale.getDefault()) + SPACE));
                    parentDeclaration5.add(new Text(messageSource.getMessage("message.label.against.ward", null, Locale.getDefault()) + SPACE));
                    parentDeclaration5.add(new Text(studentNameText).setBold());
                    parentDeclaration5.add(COMMA_TEXT);
                    parentDeclaration5.add(new Text(rollNoText).setBold()).add(SPACE);
                    parentDeclaration5.add(new Text(messageSource.getMessage("message.label.liability.penal.law.applicable.law", null, Locale.getDefault())));
                    parentDeclarationTable.addCell(getDefaultCell(parentDeclaration5, VALUE_100_P));

                    Paragraph parentDeclaration6 = getDeclarationParagraph();
                    parentDeclaration6.add(messageSource.getMessage("message.label.ward.not.expelled.debarred", null, Locale.getDefault()));
                    parentDeclarationTable.addCell(getDefaultCell(parentDeclaration6, VALUE_100_P));

                    parentDeclarationSignTable = new Table(new float[]{1, 1, 1});
                    parentDeclarationSignTable.setPaddingTop(DEFAULT_DECLARATION_INDENT);
                    parentDeclarationSignTable.setWidth(VALUE_100_P);
                    parentDeclarationSignTable.addCell(getDefaultCell(new Paragraph(messageSource.getMessage("message.label.parent.name", null, Locale.getDefault())), VALUE_40_P));
                    parentDeclarationSignTable.addCell(getDefaultCell(new Paragraph(messageSource.getMessage("message.label.parents.mob.no", null, Locale.getDefault())), VALUE_30_P));
                    parentDeclarationSignTable.addCell(getDefaultCell(new Paragraph(messageSource.getMessage("message.label.parents.signature", null, Locale.getDefault())), VALUE_30_P));
                    parentDeclarationSignTable.addCell(getDefaultCell(new Paragraph(signedParentNameText), VALUE_40_P));
                    parentDeclarationSignTable.addCell(getDefaultCell(new Paragraph(String.valueOf(
                            studentBioDataFormDetailDto.getStudentMobile() != null ? studentBioDataFormDetailDto.getStudentMobile() : "")), VALUE_30_P));
                    parentDeclarationSignTable.addCell(getDefaultCell(parentSignature, VALUE_30_P));

                    /* -----------------------------*/
                    vehicleDeclarationTable = new Table(1);

                    Paragraph vehicleDeclaration1 = getDeclarationParagraph();
                    vehicleDeclaration1.add(messageSource.getMessage("message.label.student.prefix", null, Locale.getDefault()));
                    vehicleDeclaration1.add(new Text(studentNameText).setBold()).add(SPACE);
                    vehicleDeclaration1.add(new Text(messageSource.getMessage("message.label.vehicle.declaration.student.prefix", null, Locale.getDefault())));
                    vehicleDeclaration1.add(new Text(signedParentNameText).setBold()).add(SPACE);
                    vehicleDeclaration1.add(new Text(messageSource.getMessage("message.label.no.powered.vehicle", null, Locale.getDefault())));
                    vehicleDeclarationTable.addCell(getDefaultCell(vehicleDeclaration1, VALUE_100_P));

                    Paragraph vehicleDeclaration2 = getDeclarationParagraph();
                    vehicleDeclaration2.add(messageSource.getMessage("message.label.non.compliance.consequences", null, Locale.getDefault()));
                    vehicleDeclarationTable.addCell(getDefaultCell(vehicleDeclaration2, VALUE_100_P));

                    vehicleDeclarationSignTable = new Table(new float[]{1, 1, 1});
                    vehicleDeclarationSignTable.setPaddingTop(DEFAULT_DECLARATION_INDENT);
                    vehicleDeclarationSignTable.setWidth(VALUE_100_P);
                    vehicleDeclarationSignTable.addCell(getDefaultCell(new Paragraph(messageSource.getMessage("message.label.date", null, Locale.getDefault())), VALUE_33_P));
                    vehicleDeclarationSignTable.addCell(getDefaultCell(new Paragraph(messageSource.getMessage("message.label.student.signature", null, Locale.getDefault())), VALUE_33_P));
                    vehicleDeclarationSignTable.addCell(getDefaultCell(new Paragraph(messageSource.getMessage("message.label.parents.signature", null, Locale.getDefault())), VALUE_33_P));
                    vehicleDeclarationSignTable.addCell(getDefaultCell(new Paragraph(studentBioDataFormDetailDto.getRegistrationDate() != null
                            ? studentBioDataFormDetailDto.getRegistrationDate().format(formatter) : ""), VALUE_33_P));
                    vehicleDeclarationSignTable.addCell(getDefaultCell(studentSignature, VALUE_33_P));
                    vehicleDeclarationSignTable.addCell(getDefaultCell(parentSignature, VALUE_33_P));
                }

                document.add(table);
                document.add(lineSeparator);
                document.add(addFullWidthTitle("Student Details", TextAlignment.LEFT));
                document.add(headerTable);
                document.add(lineSeparator);
                document.add(addFullWidthTitle("Hostel Details", TextAlignment.LEFT));
                document.add(hostelTable);
                document.add(lineSeparator);
                document.add(new AreaBreak());
                if (!familyInfoDtoList.isEmpty()) {
                    if (!needIdProof) {
                        document.add(addFullWidthTitle("Family Details", TextAlignment.LEFT));
                    }
                    Iterator<Map.Entry<Table, Table>> iterator = familyDetailsTable.entrySet().iterator();

                    int index = 0;

                    while (iterator.hasNext()) {
                        Map.Entry<Table, Table> entry = iterator.next();

                        if (needIdProof) {
                            document.add(addFullWidthTitle("Family Details (" + (index + 1) +"/"+ familyDetailsTable.size()+")", TextAlignment.LEFT));
                            document.add(entry.getKey());
                            document.add(entry.getValue());

                           /* if (iterator.hasNext()) {
                                document.add(new AreaBreak());
                            }*/
                        } else {
                            document.add(entry.getKey());
                            if (iterator.hasNext()) {
                                document.add(lineSeparator);
                            }
                        }
                        index++;
                    }
                    if(needDeclaration){
                        document.add(new AreaBreak());
                    }
                }
                if (needDeclaration) {
                    document.add(addFullWidthTitle(messageSource.getMessage("message.label.student.self.dec", null, Locale.getDefault()), TextAlignment.CENTER));
                    document.add(studentDeclarationTable);
                    document.add(emptySpace);
                    document.add(studentDeclarationSignTable);
                    document.add(new AreaBreak());
                    document.add(addFullWidthTitle(messageSource.getMessage("message.label.parents.self.dec", null, Locale.getDefault()), TextAlignment.CENTER));
                    document.add(parentDeclarationTable);
                    document.add(emptySpace);
                    document.add(parentDeclarationSignTable);
                    document.add(new AreaBreak());
                    document.add(addFullWidthTitle(messageSource.getMessage("message.label.vehicle.declaration", null, Locale.getDefault()), TextAlignment.CENTER));
                    document.add(vehicleDeclarationTable);
                    document.add(emptySpace);
                    document.add(vehicleDeclarationSignTable);
                }
                document.close();
            }
            String fileInputName = tempFileLocation + bioDataFileName;

            String outputFileName = tempFileLocation + "watermarked_" + bioDataFileName; // Define the output file path

            try (PdfDocument finalPdfDoc = new PdfDocument(new PdfReader(fileInputName), new PdfWriter(outputFileName))) {
                System.out.println("Number of pages before watermarking: " + finalPdfDoc.getNumberOfPages());
                pdfActiveService.addWatermarkImage(finalPdfDoc); // Apply the watermark
                finalPdfDoc.close();
                // Return the resource pointing to the new watermarked file
                Path outputPath = Paths.get(outputFileName);
                return new UrlResource(outputPath.toUri());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else return null;
    }

    /**
     * Converts a PDF to an image (first page).
     *
     * @param pdfBytes The PDF file as byte array.
     * @return Image The converted first page as an image.
     * @throws IOException If there's an error reading the PDF or converting it to an image.
     */
    private Image convertPdfToImage(byte[] pdfBytes) throws IOException {
        PDDocument document = PDDocument.load(pdfBytes);
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        // Render the first page of the PDF as a BufferedImage
        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300);

        // Convert BufferedImage to byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Convert byte array to Image
        Image image = new Image(ImageDataFactory.create(imageBytes));
        image.setWidth(525);
        image.setHeight(525);

        // Close the document
        document.close();

        return image;
    }
    
    private ListItem formParentRaggingPointsPrefix(String studentNameText, String rollNoText, String suffix) {
        Paragraph parentDeclarationUndertake = new Paragraph();
        parentDeclarationUndertake.add(new Text(messageSource.getMessage("message.label.my.ward", null, Locale.getDefault()) + SPACE));
        parentDeclarationUndertake.add(new Text(studentNameText).setBold());
        parentDeclarationUndertake.add(COMMA_TEXT);
        parentDeclarationUndertake.add(new Text(rollNoText).setBold());
        parentDeclarationUndertake.add(SPACE);
        parentDeclarationUndertake.add(new Text(suffix));
        ListItem parentBPRaggingListItem = new ListItem();
        parentBPRaggingListItem.add(parentDeclarationUndertake);
        return parentBPRaggingListItem;
    }

    @SuppressWarnings("SameParameterValue")
    private Cell getDefaultCell(Image element, UnitValue cellWidth) {
        Cell cell = new Cell();
        if (element != null) {
            cell.add(element);
        }
        cell.setWidth(cellWidth);
        cell.setBorder(null);
        return cell;
    }

    private Cell getDefaultCell(IBlockElement element, UnitValue cellWidth) {
        Cell cell = new Cell();
        if (element != null) {
            cell.add(element);
        }
        cell.setWidth(cellWidth);
        cell.setBorder(null);
        return cell;
    }

    private Paragraph getDeclarationParagraph() {
        Paragraph paragraph = new Paragraph();
//        paragraph.setFirstLineIndent(DEFAULT_DECLARATION_INDENT);
        return paragraph;
    }

    private com.itextpdf.layout.element.List getDefaultBulletPointList() {
        com.itextpdf.layout.element.List bulletPointList = new com.itextpdf.layout.element.List();
        bulletPointList.setListSymbol(DEFAULT_BULLET_SYMBOL_L1);
        bulletPointList.setSymbolIndent(DEFAULT_BULLET_INDENT);
        bulletPointList.setPaddingLeft(DEFAULT_DECLARATION_INDENT);
        return bulletPointList;
    }

    private IBlockElement addFullWidthTitle(String title, TextAlignment alignment) {
        Paragraph titlePara = new Paragraph(title)
                .setBold()
                .setFontSize(14);
        Cell titleCell = new Cell();
        titleCell.add(titlePara);
        titleCell.setTextAlignment(alignment);
        titleCell.setVerticalAlignment(VerticalAlignment.BOTTOM);
        titleCell.setMinHeight(30);
        titleCell.setBorder(null);
        titleCell.setPaddingBottom(10F);
        Table titleTable = new Table(1);
        titleTable.setWidth(VALUE_100_P);
        titleTable.addCell(titleCell);
        return titleTable;
    }

    private void addTableTextValue(String text, Object value, Table table) {
        addTableTextValue(text, value, table, new int[]{1, 1});
    }

    private void addTableTextValue(String text, Object value, Table table, int[] colSpan) {
        Cell textCell = new Cell(1, colSpan[0]).add(new Paragraph(text));
        textCell.setWidth(UnitValue.createPercentValue(20));
        textCell.setBorder(null);
        textCell.setPaddingBottom(10F);
        table.addCell(textCell);
        Cell valueCell = new Cell(1, colSpan[1]).add(new Paragraph(value != null && !value.equals("") ? value.toString() : "N/A"));
        valueCell.setWidth(UnitValue.createPercentValue(30));
        valueCell.setBorder(null);
        valueCell.setPaddingBottom(10F);
        table.addCell(valueCell);
    }

    public String validateRollNumber(String studentId) {
        Optional<StudentBioDataFormDetailEntity> optionalBioData = studentBioDataFormDetailRepository.findTopByStudentIdAndActiveFlag(studentId.toUpperCase(), ModelConstants.STATUS_ACTIVE);
        if (optionalBioData.isPresent()) {
            return studentIdAlreadyExistsError;
        }
        return null;
    }

    @Value("${message.validation.roll.no.already.exists}")
    private String studentIdAlreadyExistsError;

    public StudentBioDataFormDetailDto getStudentRegistrationById(String studentId) {
        StudentBioDataFormDetailDto dto = studentBioDataFormDetailRepository
                .findTopByStudentIdAndActiveFlag(studentId, ModelConstants.STATUS_ACTIVE)
                .map(StudentBioDataFormDetailMapper.INSTANCE::fromStudentBioDataFormDetailEntity)
                .orElse(new StudentBioDataFormDetailDto());

        if (dto.getId() !=null && dto.getId() > 0) {
            List<StudentBioDataFamilyInfoEntity> familyInfoEntityList = studentBioDataFamilyInfoRepository.findAllByBioDataId(dto.getId());
            ArrayList<StudentBioDataFamilyInfoDto> familyInfoDtoList = new ArrayList<>();
            familyInfoEntityList.forEach(familyEntity -> familyInfoDtoList.add(StudentBioDataFamilyInfoMapper.INSTANCE.fromStudentBioDataFamilyInfoEntity(familyEntity)));
            dto.setFamilyDetails(familyInfoDtoList);

            try {
                StudentBioDataFormDetailDto studentDetails = getStudentDetails(studentId);

                String profileFileName = getFileName(studentDetails, studentId, StudentBioDataFormDetailDto::getImageLocation, ModelConstants.FILE_STUDENT_PROFILE);
                dto.setImageBytes(fileService.getDecodedFile(ModelConstants.IMAGE_BIO_DATA_PROFILE, profileFileName));

                String signatureFileName = getFileName(studentDetails, studentId, StudentBioDataFormDetailDto::getStudentSignLocation, ModelConstants.FILE_STUDENT_SIGNATURE);
                dto.setStudentSignBytes(fileService.getDecodedFile(ModelConstants.IMAGE_BIO_DATA_STUDENT_SIGN, signatureFileName));

                String parentSignatureFileName = getFileName(studentDetails, studentId, StudentBioDataFormDetailDto::getParentSignLocation, ModelConstants.FILE_PARENT_SIGNATURE);
                dto.setParentSignBytes(fileService.getDecodedFile(ModelConstants.IMAGE_BIO_DATA_PARENT_SIGN, parentSignatureFileName));
            } catch (Exception e) {
                log.error("Bio data files", e);
            }

            if (ModelConstants.YES.equals(dto.getGuardianStatus())) {
                StudentBioDataFamilyInfoDto guardianDetails = studentBioDataFamilyInfoRepository
                        .findByBioDataIdAndRelationTypeAndActiveFlag(dto.getId(), ModelConstants.RELATION_GUARDIAN,
                                ModelConstants.STATUS_ACTIVE)
                        .map(StudentBioDataFamilyInfoMapper.INSTANCE::fromStudentBioDataFamilyInfoEntity)
                        .orElse(new StudentBioDataFamilyInfoDto());
                dto.setGuardianDetails(guardianDetails);
            }
        } else {
            StudentDetailsInfoDto studentDetailsInfoDto = studentDetailsInfoService.getStudentInfoDetails(studentId);
            String fullName = studentDetailsInfoDto.getFirstName();
            if (studentDetailsInfoDto.getLastName() != null && !studentDetailsInfoDto.getLastName().trim().isEmpty()) {
                fullName += ModelConstants.SPACE + studentDetailsInfoDto.getLastName();
            }
            dto.setStudentName(Objects.nonNull(fullName) && !fullName.isEmpty() ? fullName.trim() : null);
            dto.setGender(studentDetailsInfoDto.getGender());
            dto.setStudentId(studentId);
            dto.setApplicationNumber(studentId);
            dto.setRegistrationDate(LocalDate.now());
        }
        if (dto.getFacultyContactNo() != null && dto.getFacultyContactNo()==0L) {
            dto.setFacultyContactNo(null);
        }
        if (Strings.isEmpty(dto.getFacultyEmail())) {
            dto.setFacultyEmail(null);
        }
        if (Strings.isEmpty(dto.getFacultyName()) || dto.getFacultyName().equals("0")) {
            dto.setFacultyName(null);
            dto.setFacultyContactNo(null);
            dto.setFacultyEmail(null);
        }

        return dto;
    }

    public String getFileName(StudentBioDataFormDetailDto dto, String studentId, Function<StudentBioDataFormDetailDto, String> getter, String defaultSuffix) {
        if (dto != null) {
            String value = getter.apply(dto);
            if (value != null) {
                return value;
            }
        }
        return studentId + ModelConstants.UNDERSCORE + defaultSuffix;
    }

    public boolean checkBiodataFormByStudentId(String studentId) {
        return !studentBioDataFormDetailRepository.findAllByActiveFlagAndStudentId(ModelConstants.STATUS_ACTIVE, studentId).isEmpty();
    }

    public boolean checkApplicationNumberExist(String applicationNo) {
        boolean status = false;
        Optional<StudentBioDataFormDetailEntity> entity = studentBioDataFormDetailRepository.getApplicationNumber(ModelConstants.STATUS_ACTIVE, applicationNo);
        if (entity.isPresent()) {
            StudentBioDataFormDetailEntity bioEntity = entity.get();
            bioEntity.setStudentId(SecurityCtxUtil.userId().toUpperCase());
            bioEntity.onUpdate();
            studentBioDataFormDetailRepository.save(bioEntity);
            return true;
        }
        return status;
    }

    public List<StudentBioDataFamilyInfoDto> getStudentFamilyDetailsById() {
        return studentBioDataFamilyInfoRepository
                .getStudentFamilyDetails(SecurityCtxUtil.userId().toUpperCase(), ModelConstants.STATUS_ACTIVE)
                .map(list -> list.stream()
                        .map(objects -> {
                            StudentBioDataFamilyInfoDto dto = new StudentBioDataFamilyInfoDto();
                            dto.setId((Long) objects[0]);
                            dto.setBioDataId((Long) objects[1]);
                            dto.setRelationType((String) objects[2]);
                            dto.setRelationName((String) objects[3]);
                            dto.setProofFileName((String) objects[4]);
                            dto.setAge((Integer) objects[5]);
                            return dto;
                        })
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    public StudentBioDataFormDetailDto getStudentDetails(String studentId) {
        return studentBioDataFormDetailRepository
                .getStudentDetails(studentId, ModelConstants.STATUS_ACTIVE)
                .map(StudentBioDataFormDetailMapper.INSTANCE::fromStudentBioDataFormDetailEntity)
                .orElse(new StudentBioDataFormDetailDto());
    }

    public String isStudentDataFilled(String studentId) {
        StudentBioDataFormDetailDto dto = studentBioDataFormDetailRepository
                .getStudentDetails(studentId, ModelConstants.STATUS_ACTIVE)
                .map(StudentBioDataFormDetailMapper.INSTANCE::fromStudentBioDataFormDetailEntity).orElse(null);
        String resultStr = null;

        if (dto != null) {
            if (dto.getFacultyName() != null && !dto.getFacultyName().isEmpty() && dto.getFacultyName().equals("0")) dto.setFacultyName(null);
            boolean result = !Stream
                    .of(dto.getApplicationNumber(), dto.getFacultyName(), dto.getFacultyEmail(), dto.getFacultyContactNo(),
                    		dto.getStudentPersonalEmail())
                    .allMatch(field -> Objects.nonNull(field) && !String.valueOf(field).isBlank());
            resultStr = result ? messageSource.getMessage("message.label.bio.data.incomplete", null, Locale.getDefault()) : null;
            if (!result) {
                if(studentBioDataFamilyInfoRepository.hasNoFamilyProofs(studentId, Constants.GUARDIAN) > 0) {
                    resultStr = messageSource.getMessage("message.label.bio.data.family.proof", null, Locale.getDefault());
                } else {
                    if(!Stream.of(dto.getAadhaarNumber(), dto.getPanNum(), dto.getPwd())
                            .allMatch(field -> Objects.nonNull(field) && !String.valueOf(field).isBlank())) {
                        resultStr = messageSource.getMessage("message.label.bio.data.incomplete", null, Locale.getDefault());
                    }
                }
            }
        } else resultStr = messageSource.getMessage("message.label.register.bio.data.form", null, Locale.getDefault());
        return resultStr;
    }
    
    public StudentDto getStudentDetailsWithEntities(String studentId) throws Exception {
        Object obj = studentBioDataFormDetailRepository.getStudentDetailsWithEntities(studentId,
                ModelConstants.STATUS_ACTIVE);

        if (obj != null) {
            StudentDto dto = new StudentDto();
            Object[] result = (Object[]) obj;

            StudentBioDataFormDetailEntity bioData = (StudentBioDataFormDetailEntity) result[0];
            StudentDetailsInfoEntity studentDetailsInfo = (StudentDetailsInfoEntity) result[1];
            HostelRoomInfoEntity roomInfo = (HostelRoomInfoEntity) result[2];
            HostelMasterEntity hostelMaster = (HostelMasterEntity) result[3];
            HostelRoomAllotmentInfoEntity roomAllotmentInfo = (HostelRoomAllotmentInfoEntity) result[4];

			StudentBioDataFormDetailDto studentBioDataFormDetailDto = bioData != null
					? StudentBioDataFormDetailMapper.INSTANCE.fromStudentBioDataFormDetailEntity(bioData)
					: new StudentBioDataFormDetailDto();

			if (studentBioDataFormDetailDto.getStudentId() != null) {
				String profileFileName = studentBioDataFormDetailDto.getStudentId() + ModelConstants.UNDERSCORE + ModelConstants.FILE_STUDENT_PROFILE;
				studentBioDataFormDetailDto.setImageBytes(fileService.getDecodedFile(ModelConstants.IMAGE_BIO_DATA_PROFILE, profileFileName));
				dto.setStudentBioDataFormDetailDto(studentBioDataFormDetailDto);
			}
			
            dto.setStudentDetailsInfoDto(studentDetailsInfo != null
                ? StudentDetailsInfoMapper.INSTANCE.fromStudentDetailsInfoEntity(studentDetailsInfo)
                : new StudentDetailsInfoDto());

            dto.setHostelRoomInfoDto(roomInfo != null 
                ? HostelRoomInfoMapper.INSTANCE.fromHostelRoomInfoEntity(roomInfo)
                : new HostelRoomInfoDto());

            dto.setHostelMasterDto(hostelMaster != null
                ? HostelMasterMapper.INSTANCE.fromHostelMasterEntity(hostelMaster)
                : new HostelMasterDto());

            dto.setHostelRoomAllotmentInfoDto(roomAllotmentInfo != null
                ? HostelRoomAllotmentInfoMapper.INSTANCE.fromHostelRoomAllotmentInfoEntity(roomAllotmentInfo)
                : new HostelRoomAllotmentInfoDto());
            
            CourseMasterEntity courseDetails = courseAllocationInfoRepository.getStudentCourseDetails(ModelConstants.STATUS_ACTIVE, studentId);

			dto.setCourseMasterDto(courseDetails != null ? CourseMasterMapper.INSTANCE.fromCourseMasterEntity(courseDetails)
				: new CourseMasterDto());

			List<StudentBioDataFamilyInfoEntity> familyInfoEntityList = studentBioDataFamilyInfoRepository
					.findAllByBioDataId(dto.getStudentBioDataFormDetailDto().getId());
			List<StudentBioDataFamilyInfoDto> familyInfoDtoList = new ArrayList<>();
            familyInfoEntityList.forEach(familyEntity -> familyInfoDtoList.add(StudentBioDataFamilyInfoMapper.INSTANCE.fromStudentBioDataFamilyInfoEntity(familyEntity)));
            dto.setFamilyInfoDtoList(familyInfoDtoList);
            return dto;
        }
        return null;
    }

	public Boolean isStudentGuideNameEmailFilled(String studentId) {
		Optional<StudentBioDataFormDetailEntity> studentDetails = studentBioDataFormDetailRepository
				.getStudentDetails(studentId, ModelConstants.STATUS_ACTIVE);
		if (studentDetails.isEmpty())
			return true;
		return studentDetails.map(StudentBioDataFormDetailMapper.INSTANCE::fromStudentBioDataFormDetailEntity)
				.map(dto -> dto == null || dto.getFacultyName() == null || dto.getFacultyName().equals("0")
						|| dto.getFacultyName().trim().isEmpty() || dto.getFacultyEmail() == null
						|| dto.getFacultyEmail().trim().isEmpty())
				.orElse(false);
	}

	public StudentDetailsDto getFullStudentDetails(String studentId) throws Exception {
		AllStudentsDetailsViewDto studentDetails = allStudentsDetailsViewService.getCompleteStudentDetails(studentId);
        Optional<StudentBioDataFormDetailEntity> bioDataFormDetailEntity = studentBioDataFormDetailRepository.getStudentDetails(studentId,Constants.ACTIVE_FLAG);
        if(Objects.requireNonNull(SecurityCtxUtil.userRole()).equals("SoftwareAdmin")){
			if(bioDataFormDetailEntity.isPresent()) {
				if(bioDataFormDetailEntity.get().getPwd()!=null && !bioDataFormDetailEntity.get().getPwd().isEmpty()) {
					StudentBioDataFormDetailEntity studentEntity= bioDataFormDetailEntity.get();
					studentDetails.setPwd(studentEntity.getPwd());
					studentDetails.setPwdPercentage(studentEntity.getPwdPercentage());
					studentDetails.setPwdDescription(studentEntity.getPwdDescription());
					studentDetails.setOtherInfo(studentEntity.getOtherInfo());
				}
			}
		}
		if (studentDetails != null) {
			StudentDetailsDto dto = new StudentDetailsDto();

			dto.setAllStudentsDetailsViewDto(studentDetails);

			// Retrieve Student profile picture and map family details if bio_data_id exists
			if (dto.getAllStudentsDetailsViewDto().getBioDataId() != null) {
                if(bioDataFormDetailEntity.isPresent()) {
                    StudentBioDataFormDetailEntity studentEntity = bioDataFormDetailEntity.get();
                    dto.setImageBytes(fileService.getDecodedFile(ModelConstants.IMAGE_BIO_DATA_PROFILE, studentEntity.getImageLocation()));
                }

				List<StudentBioDataFamilyInfoDto> familyInfoDtoList = new ArrayList<>();
				List<StudentBioDataFamilyInfoEntity> familyInfoEntityList = studentBioDataFamilyInfoRepository
						.findAllByBioDataId(dto.getAllStudentsDetailsViewDto().getBioDataId());
				familyInfoEntityList.forEach(familyEntity -> familyInfoDtoList
						.add(StudentBioDataFamilyInfoMapper.INSTANCE.fromStudentBioDataFamilyInfoEntity(familyEntity)));
				dto.setFamilyInfoDtoList(familyInfoDtoList);
			}
			return dto;
		}
		return null;
	}

    public byte[] getFamilyIdProof(String fileName) {
        String imagePath = simsConfigDataService.getSimConfigValue(SimsConfigDataService.BIO_DATA_PARENT_PROOF);
        if (Objects.nonNull(fileName) && !fileName.isEmpty()) {
            try {
                Path imageFile = Paths.get(imagePath, fileName+ModelConstants.DEFAULT_FILE_EXTENSION);
                return Files.readAllBytes(imageFile);
            } catch (IOException e) {
                return null;
            }
        }
        else{
            return null;
        }
    }

	public String updateStudentBioData(@Valid StudentDetailsDto studentDetailsDto) {
		StudentBioDataFormDetailEntity savedEntity = null;
		Optional<StudentBioDataFormDetailEntity> bioDataFormDetailEntity = studentBioDataFormDetailRepository.getStudentDetails(studentDetailsDto.getStudentId(),Constants.ACTIVE_FLAG);
		if(bioDataFormDetailEntity.isPresent()) {
			StudentBioDataFormDetailEntity detailEntity = bioDataFormDetailEntity.get();
			StudentBioDataFormDetailMapper.INSTANCE.updateStudentBioData(detailEntity,studentDetailsDto);
			detailEntity.onUpdate();
			savedEntity = studentBioDataFormDetailRepository.saveAndFlush(detailEntity);
            StudentBioDataFormDetailDto formDetailDto = StudentBioDataFormDetailMapper.INSTANCE.fromStudentBioDataFormDetailEntity(detailEntity);
            formDetailDto.setFamilyDetails(studentDetailsDto.getFamilyDetails());
            studentBioDataFamilyInfoService.saveFamilyDetails(formDetailDto, false, true);
		}
		if(savedEntity!=null) {
			return Constants.SAVED;
		}else {			
			return null;
		}
	}

	public Page<StudentBioDataFormDetailDto> getStudentBioDataFormDetailList(PaginationForm form) {
	    int page = form.getPage() - 1;
	    Pageable pageable = PageRequest.of(page, form.getSize());
	    
	    Page<StudentBioDataFormDetailEntity> entityPage = null;
	    
	    if (form.getSearch() == null || form.getSearch().isEmpty()) {
	    	entityPage = studentBioDataFormDetailRepository.findAllByActiveFlag(Constants.ACTIVE_FLAG, pageable);
	    } else {
	    	entityPage = studentBioDataFormDetailRepository
	                .searchByFields(Constants.ACTIVE_FLAG, form.getSearch(), pageable);
	    }
	    

	    Page<StudentBioDataFormDetailDto> result = entityPage
	        .map(StudentBioDataFormDetailMapper.INSTANCE::fromStudentBioDataFormDetailEntity);

	    return result;
	}
}
