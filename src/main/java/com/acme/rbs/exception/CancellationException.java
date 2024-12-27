package com.acme.rbs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class CancellationException extends RuntimeException {
    public CancellationException(String message) {
        super(message);
    }
}
