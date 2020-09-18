package com.example.personalbutler.exception;

import org.springframework.http.HttpStatus;

/**
 * @description: 自定义异常
 * @author: Mask
 * @time: 2020/9/17 4:48 下午
 */
public class ContainerException extends Exception {

    public ContainerException(String msg, HttpStatus statusCode) {
        super(msg);
        this.statusCode = statusCode;
    }

    public ContainerException(String msg, HttpStatus statusCode, Throwable throwable) {
        super(msg, throwable);
        this.statusCode = statusCode;
    }


    private HttpStatus statusCode;

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }
}
