package maks.molch.dmitr.badminton_service.common;

import java.time.Instant;

public record ErrorResponse(Instant timestamp, String message, String path) {
}
