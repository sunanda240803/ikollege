package com.iitm.hosteldine.form.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class FileForm {
    private String description;
    private MultipartFile file;
}
