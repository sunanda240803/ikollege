package com.iitm.hosteldine.util.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class BaseResponse {
    protected Integer code;
    @NonNull
    protected String message;
    @NonNull
    protected String status;
    Object errors;
    Object data;
}

