package com.iitm.hosteldine.controller;

import com.iitm.hosteldine.config.SecurityCtxUtil;
import com.iitm.hosteldine.constant.Constants;
import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.dto.student.StudentDetailsDto;
import com.iitm.hosteldine.service.FileService;
import com.iitm.hosteldine.service.SimsConfigDataService;
import com.iitm.hosteldine.service.StudentBioDataService;
import com.iitm.hosteldine.util.CommonEnum;
import com.iitm.hosteldine.util.Utility;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Controller
@RequestMapping(value = "${url.file}")
@RequiredArgsConstructor
public class FileHandlingController {

    private final SimsConfigDataService simsConfigDataService;
    private final FileService fileService;
    private final StudentBioDataService studentBioDataService;

    @GetMapping(value = "${url.image}" + "${type}" + "${id}")
    public ResponseEntity<?> getImage(@PathVariable("type") String type, @PathVariable("id") String imageId)
            throws Exception {

        String imagePath;
        String imageName;

        if (CommonEnum.PROFILE.toString().equals(type.toUpperCase())) {
            switch (SecurityCtxUtil.userRole()) {
                case "Student":
                    type = CommonEnum.STUDENT_PROFILE.name();
                    break;
                case "Other Candidate":
                    type = CommonEnum.CANDIDATE_PROFILE.name();
                    imageId = SecurityCtxUtil.getProfile();
                    break;
            }
        }

        switch (CommonEnum.valueOf(type.toUpperCase())) {
            case SHOW -> {
                imagePath = simsConfigDataService.getSimConfigValue("seatLayout");
                imageName = imageId + simsConfigDataService.getSimConfigValue(Constants.COMMONFILEEXTENSION,
                        ModelConstants.DEFAULT_FILE_EXTENSION);
            }
            case STUDENT_PROFILE -> {
                StudentDetailsDto studentDetails = studentBioDataService.getFullStudentDetails(Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase());
                byte[] image = null;
                if(studentDetails !=null && studentDetails.getImageBytes() != null) {
                    image = studentDetails.getImageBytes();
                }
/*                byte[] image = fileService.getDecodedFile(ModelConstants.IMAGE_BIO_DATA_PROFILE,
                        Objects.requireNonNull(SecurityCtxUtil.userId()).toUpperCase() + ModelConstants.UNDERSCORE
                                + ModelConstants.FILE_STUDENT_PROFILE);*/
                return ResponseEntity.ok().body(image);
            }
            case STUDENT_IMAGE -> {
                StudentDetailsDto studentDetails = studentBioDataService.getFullStudentDetails(imageId);
                byte[] image = null;
                if(studentDetails !=null && studentDetails.getImageBytes() != null) {
                    image = studentDetails.getImageBytes();
                }
/*                byte[] image = fileService.getDecodedFile(ModelConstants.IMAGE_BIO_DATA_PROFILE,
                        Objects.requireNonNull(imageId).toUpperCase() + ModelConstants.UNDERSCORE
                                + ModelConstants.FILE_STUDENT_PROFILE);*/
                return ResponseEntity.ok().body(image);
            }
            case CANDIDATE_PROFILE, CANDIDATE_IMAGE -> {
                imagePath = simsConfigDataService.getSimConfigValue(ModelConstants.IMAGE_CANDIDATE_PROFILE);
                imageName = imageId + simsConfigDataService.getSimConfigValue(Constants.COMMONFILEEXTENSION,
                        ModelConstants.DEFAULT_FILE_EXTENSION);
            }
            case GUEST_ACCOMMODATION -> {
                imagePath = simsConfigDataService.getSimConfigValue(SimsConfigDataService.BIO_DATA_PARENT_PROOF);
                imageName = imageId + ModelConstants.DEFAULT_FILE_EXTENSION;
            }
            case WARDEN_IMAGE -> {
                imagePath = simsConfigDataService.getSimConfigValue(SimsConfigDataService.FILE_PATH_WARDEN_IMAGE);
                imageName = imageId + simsConfigDataService.getSimConfigValue(Constants.COMMONFILEEXTENSION,
                        ModelConstants.DEFAULT_FILE_EXTENSION);
            }
            default -> {
                return null;
            }
        }

        if (imageId.toLowerCase().endsWith(".pdf")) {
            try {
                byte[] imageBytes = convertPdfToImage(imagePath, imageName);
                return ResponseEntity.ok().body(imageBytes);

            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body("Error processing PDF file");
            }
        } else {
            try {
                Path imageFile = Paths.get(imagePath, imageName);
                byte[] imageBytes = Files.readAllBytes(imageFile);
                if (imageName.toLowerCase().contains(".svg"))
                    return ResponseEntity.ok().contentType(MediaType.parseMediaType("image/svg+xml")).body(imageBytes);
                else
                    return ResponseEntity.ok().body(imageBytes);
            } catch (IOException e) {
                return ResponseEntity.status(404).body("Image not found");
            }
        }
    }

    /**
     * Converts the first page of a PDF to an image (PNG format).
     *
     * @param imagePath The path where the image (PDF) is located.
     * @param imageName The name of the image (PDF file).
     * @return byte[] The byte array of the first page rendered as an image.
     * @throws IOException If there's an error reading the PDF or writing the image.
     */
    private byte[] convertPdfToImage(String imagePath, String imageName) throws IOException {
        Path imageFile = Paths.get(imagePath, imageName);
        PDDocument document = PDDocument.load(imageFile.toFile());
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        // Render the first page of the PDF as a BufferedImage
        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(0, 300);

        // Convert BufferedImage to byte[] (PNG format)
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "PNG", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Close the document
        document.close();

        return imageBytes;
    }

    @GetMapping(value = "${url.download}" + "${type}" + "${fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("type") String type,
                                                 @PathVariable("fileName") String fileName) {

        String filePath;

        switch (CommonEnum.valueOf(type.toUpperCase())) {
            case CANDIDATE_PROFILE -> filePath = SimsConfigDataService.CANDIDATE_FILE;
            case HOSTEL_ACCOMMODATION -> filePath = SimsConfigDataService.HOSTEL_ACCOMMODATION;
            case ROLL_NO_CHANGE -> filePath = SimsConfigDataService.UPLOAD_FILE_LOCATION;
            case STUDENT_COMPLAINT_CONFIG -> filePath = SimsConfigDataService.STUDENT_COMPLAINT_CONFIG;
            case BIO_DATA_PARENT_PROOF -> filePath = SimsConfigDataService.BIO_DATA_PARENT_PROOF;
            case HDC_COMPLAINT_FILE_PATH -> filePath = SimsConfigDataService.HDC_COMPLAINT_FILE_PATH;
            case MESS_INSPECTION_FILE_PATH -> filePath = SimsConfigDataService.MESS_INSPECTION_FILE_PATH;
            default -> {
                return null;
            }
        }

        byte[] fileData;
        try {
            fileData = fileService.getDecodedFile(filePath, fileName);
            if(Objects.isNull(fileData)) {
                Path path = Paths.get(filePath).resolve(fileName).normalize();
                fileData = Files.readAllBytes(path);
            }
            ByteArrayResource resource = new ByteArrayResource(fileData);
            return Utility.prepareDownloadFile(resource, fileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
