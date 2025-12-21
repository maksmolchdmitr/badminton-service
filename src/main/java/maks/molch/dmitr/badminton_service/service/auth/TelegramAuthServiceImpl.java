package maks.molch.dmitr.badminton_service.service.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import maks.molch.dmitr.badminton_service.config.properties.TelegramProperties;
import maks.molch.dmitr.badminton_service.model.TelegramUserModel;
import maks.molch.dmitr.badminton_service.util.CryptoUtils;
import org.springframework.stereotype.Service;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Default {@link TelegramAuthService} implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class TelegramAuthServiceImpl implements TelegramAuthService {

    private static final long MAX_AUTH_AGE_SECONDS = 86_400;

    private final TelegramProperties telegramProperties;

    @Override
    public void checkTelegramUserData(TelegramUserModel telegramUserModel) {
        if (telegramUserModel.hash().isBlank()) {
            throw new IllegalArgumentException("Hash is blank");
        }

        if (telegramUserModel.authDate().equals(0L)) {
            throw new IllegalArgumentException("Auth date is 0");
        }

        long ageSeconds = Instant.now().getEpochSecond() - telegramUserModel.authDate();
        if (ageSeconds > MAX_AUTH_AGE_SECONDS) {
            throw new SecurityException("Data is outdated");
        }

        String dataCheckString = buildDataCheckString(toMap(telegramUserModel));
        String calculatedHash = calculateTelegramHash(dataCheckString);

        if (!calculatedHash.equalsIgnoreCase(telegramUserModel.hash())) {
            throw new SecurityException("Invalid hash");
        }
    }

    private Map<String, String> toMap(TelegramUserModel telegramUserModel) {
        return Map.of(
                "id", telegramUserModel.id().toString(),
                "first_name", telegramUserModel.firstName(),
                "last_name", telegramUserModel.lastName(),
                "username", telegramUserModel.username(),
                "photo_url", telegramUserModel.photoUrl(),
                "auth_date", telegramUserModel.authDate().toString()
        );
    }

    private String buildDataCheckString(Map<String, String> authData) {
        return authData.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

    private String calculateTelegramHash(String dataCheckString) {
        byte[] secretKey = CryptoUtils.sha256(telegramProperties.getToken().getBytes(StandardCharsets.UTF_8));
        byte[] signature = CryptoUtils.hmacSha256(secretKey, dataCheckString.getBytes(StandardCharsets.UTF_8));
        return CryptoUtils.bytesToHex(signature);
    }
}
