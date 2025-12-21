package maks.molch.dmitr.badminton_service.service.time;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public interface TimeService {
    OffsetDateTime now(ZoneId zoneId);

    Instant now();
}
