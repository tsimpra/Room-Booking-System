package com.acme.rbs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BookingRequestException extends RuntimeException {
    public BookingRequestException(String message) {
        super(message);
    }
}
