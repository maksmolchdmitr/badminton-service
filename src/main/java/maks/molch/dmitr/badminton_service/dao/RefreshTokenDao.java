package maks.molch.dmitr.badminton_service.dao;

import lombok.RequiredArgsConstructor;
import maks.molch.dmitr.badminton_service.generated.jooq.tables.records.RefreshTokenRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

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

    public Optional<RefreshTokenRecord> findById(UUID refreshToken) {
        return dsl.selectFrom(REFRESH_TOKEN)
                .where(REFRESH_TOKEN.TOKEN.eq(refreshToken))
                .fetchOptional();
    }

    public void deleteById(UUID refreshToken) {
        dsl.deleteFrom(REFRESH_TOKEN)
                .where(REFRESH_TOKEN.TOKEN.eq(refreshToken))
                .execute();
    }
}
