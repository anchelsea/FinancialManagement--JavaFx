package com.Infrastructure.Exception;

public class BadRequestException extends BaseException {
    public BadRequestException() {
        super("BAD_REQUEST", 400);
    }
}

