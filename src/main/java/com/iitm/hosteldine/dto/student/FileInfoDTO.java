package com.iitm.hosteldine.dto.student;

import lombok.Data;

@Data
public class FileInfoDTO {
    private Integer fileId;
    private Integer requestId;
    private String uploadModifiedFileName;
    private String fileDescrption;
}
