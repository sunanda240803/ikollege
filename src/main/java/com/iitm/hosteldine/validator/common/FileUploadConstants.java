package com.iitm.hosteldine.validator.common;

public class FileUploadConstants {
	// Common MIME Types
	public static final String PDF = "application/pdf";
    public static final String DOC = "application/msword";
    public static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String XLS = "application/vnd.ms-excel";
    public static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String JPG = "image/jpeg";
    public static final String PNG = "image/png";
    public static final String SVG_XML = "image/svg+xml";
    public static final String GIF = "image/gif";
    public static final String TXT = "text/plain";
    public static final String CSV = "text/csv";
    public static final String ZIP = "application/zip";
    public static final String JSON = "application/json";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String XLSX_EXTENSION = ".xlsx";
    public static final String PNG_FORMAT = "png";

    public static final String[] HOSTEL_ACCOMMODATION_ALLOWED_EXTENSIONS = { "pdf", "jpg", "bmp", "png", "jpeg", "doc", "docx", "xls", "xlsx" };

    // File Size Limit (in bytes) - 2 MB
    public static final long MAX_FILE_SIZE = 2 * 1024 * 1024;
}
