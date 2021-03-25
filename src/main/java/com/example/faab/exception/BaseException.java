package com.example.faab.exception;

import lombok.Data;

/**
 * @version : 1.0
 * @Author : zh
 * @Date ：2021/3/22
 * @Description ：
 **/
@Data
public class BaseException extends  RuntimeException{

    String code;

    String message;

    public BaseException() {
        super();
    }

    public BaseException(String code, String message) {
        this.message = message;
        this.code = code;
    }

}
