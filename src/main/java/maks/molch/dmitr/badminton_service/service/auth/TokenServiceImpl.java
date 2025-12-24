package maks.molch.dmitr.badminton_service.service.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import maks.molch.dmitr.badminton_service.config.properties.TokenProperties;
import maks.molch.dmitr.badminton_service.dao.RefreshTokenDao;
import maks.molch.dmitr.badminton_service.generated.jooq.tables.records.RefreshTokenRecord;
import maks.molch.dmitr.badminton_service.model.TelegramUserModel;
import maks.molch.dmitr.badminton_service.model.TokenModel;
import maks.molch.dmitr.badminton_service.service.time.TimeService;
import maks.molch.dmitr.badminton_service.service.uuid.UuidGenerator;
import maks.molch.dmitr.badminton_service.util.CryptoUtils;
import maks.molch.dmitr.badminton_service.util.TimeUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TimeService timeService;
    private final UuidGenerator uuidGenerator;
    private final RefreshTokenDao refreshTokenDao;
    private final TokenProperties tokenProperties;

    @Override
    public TokenModel generate(TelegramUserModel telegramUserModel, UUID userId) {
        OffsetDateTime now = timeService.now(ZoneId.systemDefault());
        String accessToken = generateAccessToken(telegramUserModel, now);
        String refreshToken = generateRefreshToken(userId);
        return new TokenModel(accessToken, refreshToken);
    }

    private String generateAccessToken(TelegramUserModel telegramUserModel, OffsetDateTime now) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setIssuer(tokenProperties.getIssuerUrl())
                .setSubject(telegramUserModel.id().toString())
                .setIssuedAt(TimeUtils.toDate(now))
                .setExpiration(expirationDate())
                .signWith(
                        CryptoUtils.loadPrivateKey(tokenProperties.getPrivateKey()),
                        SignatureAlgorithm.RS256
                )
                .compact();
    }

    private String generateRefreshToken(UUID userId) {
        RefreshTokenRecord refreshTokenRecord = refreshTokenDao.findBy(userId).orElseGet(() -> {
            UUID token = uuidGenerator.random();
            OffsetDateTime now = timeService.now(ZoneId.systemDefault());
            LocalDateTime localDateTimeNow = TimeUtils.toLocalDateTime(now);
            LocalDateTime expiresAt = localDateTimeNow.plusDays(tokenProperties.getRefreshTokenExpireTimeInDays());
            RefreshTokenRecord record = new RefreshTokenRecord(token, userId, localDateTimeNow, expiresAt);
            return refreshTokenDao.insert(record);
        });
        return refreshTokenRecord.getToken().toString();
    }

    private Date expirationDate() {
        return Date.from(
                timeService.now()
                        .plusSeconds(tokenProperties.getExpireTimeInSeconds())
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
    }
}
