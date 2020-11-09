package com.Infrastructure.Exception;


public class NotFoundException extends BaseException {
    public NotFoundException() {
        super("RESOURCE_NOT_FOUND", 404);
    }
}
