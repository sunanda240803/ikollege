package com.iitm.hosteldine.dto.dean;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rows {
    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Stay from date is required")
    private LocalDate stayFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Stay to date is required")
    private LocalDate stayTo;

    @NotNull(message = "Male participants is required")
    @Min(value = 0, message = "Must be 0 or greater")
    private Integer maleParticipants;

    @NotNull(message = "Female participants is required")
    @Min(value = 0, message = "Must be 0 or greater")
    private Integer femaleParticipants;

    @NotBlank(message = "Dining option is required")
    private String dining;

    @NotBlank(message = "Session is required")
    private String session;

    private String fromDate;

    private String toDate;
    private Integer breakfastCount;
    private Integer lunchCount;
    private Integer dinnerCount;
}