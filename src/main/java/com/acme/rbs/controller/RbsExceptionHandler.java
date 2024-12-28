package com.acme.rbs.controller;

import com.acme.rbs.dto.response.ErrorDTO;
import com.acme.rbs.exception.BookingRequestException;
import com.acme.rbs.exception.CancellationException;
import com.acme.rbs.exception.RoomNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RbsExceptionHandler {

    @ExceptionHandler({BookingRequestException.class, CancellationException.class, RoomNotFoundException.class})
    public ResponseEntity<ErrorDTO> handleRbsBadRequests(RuntimeException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(new ErrorDTO(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), request.getRequestURI()));
    }

    @ExceptionHandler({ConstraintViolationException.class, PersistenceException.class})//could add more if needed
    public ResponseEntity<ErrorDTO> handleDbExceptions(Exception ex, HttpServletRequest request) {
        return ResponseEntity.internalServerError()
                .body(new ErrorDTO("Something went wrong during database transaction", HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(Exception ex, HttpServletRequest request) {
        return ResponseEntity.internalServerError()
                .body(new ErrorDTO(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), request.getRequestURI()));
    }
}
