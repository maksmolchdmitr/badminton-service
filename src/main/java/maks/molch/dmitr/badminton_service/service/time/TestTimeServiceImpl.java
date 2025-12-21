package maks.molch.dmitr.badminton_service.service.time;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Service
@Profile("test")
public class TestTimeServiceImpl implements TimeService {
    @Override
    public OffsetDateTime now(ZoneId zoneId) {
        Instant instant = now();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return OffsetDateTime.of(localDateTime, zoneId.getRules().getOffset(instant));
    }

    @Override
    public Instant now() {
        return Instant.parse("2025-12-21T22:11:55.000Z");
    }
}
