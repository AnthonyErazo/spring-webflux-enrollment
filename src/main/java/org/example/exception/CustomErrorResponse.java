package org.example.exception;

import java.time.LocalDateTime;

public record CustomErrorResponse(
        String message,
        LocalDateTime dateTime
) {
}
