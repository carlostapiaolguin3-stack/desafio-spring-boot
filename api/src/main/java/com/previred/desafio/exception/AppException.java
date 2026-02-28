package com.previred.desafio.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public abstract class AppException extends RuntimeException {
    private final HttpStatus status;

    public AppException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
