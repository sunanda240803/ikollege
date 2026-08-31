package com.iitm.hosteldine.service;

import com.iitm.hosteldine.constant.ModelConstants;
import com.iitm.hosteldine.util.SecureFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class FileService {
	public static final String ROLL_NUMBER_PROOF = "roll_number_proof";
	public static final String MESS_REBATE_DOCUMENT = "mess_rebate_doc";
	public static final String HOSTEL_ACCOMMODATION = "hostel_accommodation";

    private SimsConfigDataService simsConfigDataService;
    private SecureFileService secureFileService;

    private final Map<String, String> simsConfigLocation = new HashMap<>();

    public void resetSimsConfigLocation() {
        simsConfigLocation.clear();   // This resets the map
    }
    public boolean encodeFile(String imageType, byte[] imageBytes, String fileName) throws Exception {
        boolean result = false;
        String imageLocation = getImageFolder(imageType);
        log.info("image location: {}", imageLocation);
        if (imageBytes != null && imageBytes.length > 0) {
            imageLocation += fileName + getFileExtension();
            log.info("image location with file: {}", imageLocation);
            if (getEncryptFiles().equals("true")) {
                result = secureFileService.saveEncryptedFile(imageBytes, imageLocation);
            } else {
                result = secureFileService.saveFile(imageBytes, imageLocation);
            }
        }
        return result;
    }

    public byte[] getDecodedFile(String filePath, String fileName) throws Exception {
        String imageLocation = getImageFolder(filePath);
        imageLocation += fileName + getFileExtension();
        return secureFileService.readEncryptedFile(imageLocation);
    }

    private String getImageFolder(String imageType) {
        String imageLocation = simsConfigLocation.get(imageType);
        if (imageLocation == null) {
            imageLocation = simsConfigDataService.getSimConfigValue(imageType);
            simsConfigLocation.put(imageType, imageLocation);
        }
        log.info("FileService: image path: {}", imageLocation);
        return imageLocation;
    }

    private String getEncryptFiles() {
        return simsConfigDataService.getSimConfigValue("encryptFiles", "false");
    }

    private String getFileExtension() {
        return simsConfigDataService.getSimConfigValue("commonFileExtension", ModelConstants.DEFAULT_FILE_EXTENSION);
    }

    public boolean deleteFile(String imageType, String fileName) {
        boolean result;
        if (fileName != null && !fileName.isEmpty()) {
            String imageLocation = getImageFolder(imageType);
            imageLocation += fileName + getFileExtension();
            if (getEncryptFiles().equals("true")) {
                result = secureFileService.deleteEncryptedFile(imageLocation);
            } else {
                result = secureFileService.deleteFile(imageLocation);
            }
        } else result = true;
        return result;
    }

    @Autowired
    public void setSimsConfigDataService(SimsConfigDataService simsConfigDataService) {
        this.simsConfigDataService = simsConfigDataService;
    }

    @Autowired
    public void setSecureFileService(SecureFileService secureFileService) {
        this.secureFileService = secureFileService;
    }
}
