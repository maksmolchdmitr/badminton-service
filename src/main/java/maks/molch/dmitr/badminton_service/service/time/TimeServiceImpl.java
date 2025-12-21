package maks.molch.dmitr.badminton_service.service.time;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Service
@Profile("!test")
public class TimeServiceImpl implements TimeService {
    @Override
    public OffsetDateTime now(ZoneId zoneId) {
        return OffsetDateTime.now(zoneId);
    }

    @Override
    public Instant now() {
        return Instant.now();
    }
}
