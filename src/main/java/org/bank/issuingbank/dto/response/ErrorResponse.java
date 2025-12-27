package org.bank.issuingbank.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(
        String errorCode,
        String message,
        int status,
        String path,
        LocalDateTime timestamp
) {
    public ErrorResponse(String errorCode, String message, int status, String path) {
        this(errorCode, message, status, path, LocalDateTime.now());
    }

    public ErrorResponse(String errorCode, String message, int status) {
        this(errorCode, message, status, null, LocalDateTime.now());
    }
}
