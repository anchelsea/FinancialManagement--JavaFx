package com.Infrastructure.Exception;

public class BaseException extends Exception {
    private int code;

    public BaseException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
