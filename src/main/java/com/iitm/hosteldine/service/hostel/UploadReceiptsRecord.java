package com.iitm.hosteldine.service.hostel;

import java.time.LocalDate;

public record UploadReceiptsRecord(String docRefNo,
                                   LocalDate voucherDate,
                                   String description,
                                   String accHead,
                                   Double amount,
                                   String studentName) {
}
