package maks.molch.dmitr.badminton_service.exception;

import java.time.Instant;

public record ErrorResponse(Instant timestamp, String message, String path) {
}
