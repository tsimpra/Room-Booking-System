package com.acme.rbs.dto.response;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public record ErrorDTO(String message, LocalDateTime timestamp, int status, String path) {
    public ErrorDTO(String message, int status, String path) {
        this(message, LocalDateTime.now(ZoneOffset.UTC), status, path);
    }
}
