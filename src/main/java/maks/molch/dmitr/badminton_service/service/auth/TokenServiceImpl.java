package maks.molch.dmitr.badminton_service.service.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import maks.molch.dmitr.badminton_service.config.properties.TokenProperties;
import maks.molch.dmitr.badminton_service.model.TelegramUserModel;
import maks.molch.dmitr.badminton_service.model.TokenModel;
import maks.molch.dmitr.badminton_service.service.time.TimeService;
import maks.molch.dmitr.badminton_service.util.CryptoUtils;
import maks.molch.dmitr.badminton_service.util.TimeUtils;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TimeService timeService;
    private final TokenProperties tokenProperties;

    @Override
    public TokenModel generate(TelegramUserModel telegramUserModel) {
        OffsetDateTime now = timeService.now(ZoneId.systemDefault());
        String accessToken = generateAccessToken(telegramUserModel, now);

        return new TokenModel(accessToken, "");
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

    private Date expirationDate() {
        return Date.from(
                timeService.now()
                        .plusSeconds(tokenProperties.getExpireTimeInSeconds())
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );
    }
}
