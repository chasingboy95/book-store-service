package com.chasing.bookstoreservice.exception;

import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final Response.Status httpStatus;

    public BusinessException(Response.Status httpStatus, String message) {
        this(httpStatus, message, null);
    }

    public BusinessException(Response.Status httpStatus, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}

