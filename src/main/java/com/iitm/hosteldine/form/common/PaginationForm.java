package com.iitm.hosteldine.form.common;

import com.iitm.hosteldine.constant.Constants;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class PaginationForm {
	private int page = 1; // default page is 1
    private int size = 25; // default size
    private String search;
    private String currentUrl;
    private Map<String, Object> additionalParam = new HashMap<>();
    private Page<?> paginationList;
    private List<?> list;
    private int pageVal = 1;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate fromDate;
    @DateTimeFormat(pattern = Constants.BACKEND_DATE_FORMAT)
    private LocalDate toDate;
    private boolean hasSearchParam = false;
    private boolean searchFilter = false;

    public boolean hasValue(String key) {
        return additionalParam != null && !additionalParam.isEmpty() && additionalParam.get(key) != null && !additionalParam.get(key).toString().isEmpty();
    }
}
