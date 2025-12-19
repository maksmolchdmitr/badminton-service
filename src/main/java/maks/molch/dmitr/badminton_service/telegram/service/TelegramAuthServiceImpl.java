package maks.molch.dmitr.badminton_service.telegram.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import maks.molch.dmitr.badminton_service.telegram.config.TelegramConfig;
import maks.molch.dmitr.badminton_service.telegram.model.TelegramUser;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Default {@link TelegramAuthService} implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramAuthServiceImpl implements TelegramAuthService {

    private static final long MAX_AUTH_AGE_SECONDS = 86_400;

    private final TelegramConfig telegramConfig;

    /**
     * {@inheritDoc}
     */
    @Override
    public TelegramUser checkTelegramAuthorization(Map<String, String> authData) {
        if (authData == null || authData.isEmpty()) {
            throw new IllegalArgumentException("Auth data is empty");
        }

        String receivedHash = authData.get("hash");
        if (receivedHash == null || receivedHash.isBlank()) {
            throw new IllegalArgumentException("Hash is missing");
        }

        String authDateString = authData.get("auth_date");
        if (authDateString == null || authDateString.isBlank()) {
            throw new IllegalArgumentException("Auth date is missing");
        }

        long authDate;
        try {
            authDate = Long.parseLong(authDateString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Auth date is invalid", e);
        }

        if ((Instant.now().getEpochSecond() - authDate) > MAX_AUTH_AGE_SECONDS) {
            throw new SecurityException("Data is outdated");
        }

        String dataCheckString = buildDataCheckString(authData);
        String calculatedHash = calculateTelegramHash(dataCheckString);

        if (!calculatedHash.equalsIgnoreCase(receivedHash)) {
            throw new SecurityException("Invalid hash");
        }

        log.debug("Successfully validated Telegram auth data for user: {}", authData.get("id"));
        return createTelegramUser(authData, receivedHash, authDate);
    }

    private String buildDataCheckString(Map<String, String> authData) {
        return authData.entrySet().stream()
                .filter(entry -> !"hash".equals(entry.getKey()))
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

    private String calculateTelegramHash(String dataCheckString) {
        byte[] secretKey = sha256(telegramConfig.getToken().getBytes(StandardCharsets.UTF_8));
        byte[] signature = hmacSha256(secretKey, dataCheckString.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(signature);
    }

    private TelegramUser createTelegramUser(Map<String, String> authData, String hash, long authDate) {
        String id = authData.get("id");
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("User id is missing");
        }

        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("User id is invalid", e);
        }

        return TelegramUser.builder()
                .id(parsedId)
                .firstName(authData.get("first_name"))
                .lastName(authData.get("last_name"))
                .username(authData.get("username"))
                .photoUrl(authData.get("photo_url"))
                .authDate(authDate)
                .hash(hash)
                .build();
    }

    private static byte[] sha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("Failed to calculate SHA-256", e);
        }
    }

    private static byte[] hmacSha256(byte[] key, byte[] data) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key, "HmacSHA256");
            sha256Hmac.init(secretKey);
            return sha256Hmac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new SecurityException("Failed to calculate HMAC-SHA256", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
