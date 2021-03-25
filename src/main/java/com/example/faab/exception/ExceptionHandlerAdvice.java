package com.example.faab.exception;


import com.example.faab.domain.ApiResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @version : 1.0
 * @Author : zh
 * @Date ：2021/3/22
 * @Description ：
 **/
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    public ExceptionHandlerAdvice() {
    }


    @ExceptionHandler(BaseException.class)
    public ApiResult businessExceptionHandler(BaseException e) {
        return ApiResult.builder()
                .code(e.getCode())
                .msg(e.getMessage())
                .build();
    }
}
