package com.example.faab.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResult<T> {
    private String code;
    private String msg;
    private T data;

    public ApiResult(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiResult(String code, String msg,T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}





