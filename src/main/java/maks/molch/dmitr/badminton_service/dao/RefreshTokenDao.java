package maks.molch.dmitr.badminton_service.dao;

import lombok.RequiredArgsConstructor;
import maks.molch.dmitr.badminton_service.config.properties.TokenProperties;
import maks.molch.dmitr.badminton_service.generated.jooq.tables.records.RefreshTokenRecord;
import maks.molch.dmitr.badminton_service.service.time.TimeService;
import maks.molch.dmitr.badminton_service.service.uuid.UuidGenerator;
import maks.molch.dmitr.badminton_service.util.TimeUtils;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import static maks.molch.dmitr.badminton_service.generated.jooq.tables.RefreshToken.REFRESH_TOKEN;

@Service
@RequiredArgsConstructor
public class RefreshTokenDao {
    private final DSLContext dsl;

    public Optional<RefreshTokenRecord> findBy(UUID userId) {
        return dsl.selectFrom(REFRESH_TOKEN)
                .where(REFRESH_TOKEN.USER_ID.eq(userId))
                .fetchOptional();
    }

    public RefreshTokenRecord insert(RefreshTokenRecord record) {
        return dsl.insertInto(REFRESH_TOKEN)
                .set(record)
                .returning()
                .fetchOne();
    }
}
