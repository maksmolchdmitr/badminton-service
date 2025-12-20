package maks.molch.dmitr.badminton_service.telegram.api;

import lombok.RequiredArgsConstructor;
import maks.molch.dmitr.badminton_service.generated.api.TelegramAuthApiDelegate;
import maks.molch.dmitr.badminton_service.generated.model.TelegramAuthData;
import maks.molch.dmitr.badminton_service.generated.model.TelegramUser;
import maks.molch.dmitr.badminton_service.telegram.model.TelegramUserModel;
import maks.molch.dmitr.badminton_service.telegram.service.TelegramAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TelegramAuthApiDelegateImpl implements TelegramAuthApiDelegate {

    private static final String TG_USER_COOKIE = "tg_user";

    private final TelegramAuthService telegramAuthService;

    @Override
    public ResponseEntity<TelegramUser> telegramLogin(TelegramAuthData telegramAuthData) {
        Map<String, String> authData = toAuthDataMap(telegramAuthData);
        TelegramUserModel user = telegramAuthService.checkTelegramAuthorization(authData);

        ResponseCookie cookie = ResponseCookie.from(TG_USER_COOKIE, user.getId().toString())
                .path("/")
                .httpOnly(true)
                .secure(true)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(toGeneratedUser(user));
    }

    @Override
    public ResponseEntity<Void> telegramLogout() {
        ResponseCookie cookie = ResponseCookie.from(TG_USER_COOKIE, "")
                .path("/")
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ZERO)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @Override
    public ResponseEntity<TelegramUser> telegramCurrentUser(String tgUser) {
        if (tgUser == null || tgUser.isBlank()) {
            throw new SecurityException("Not authenticated");
        }

        long parsedId;
        try {
            parsedId = Long.parseLong(tgUser);
        } catch (NumberFormatException e) {
            throw new SecurityException("Not authenticated", e);
        }

        TelegramUser user = new TelegramUser().id(parsedId);
        return ResponseEntity.ok(user);
    }

    private static Map<String, String> toAuthDataMap(TelegramAuthData data) {
        Map<String, String> map = new HashMap<>();
        putIfNotNull(map, "id", data.getId());
        putIfNotNull(map, "first_name", data.getFirstName());
        putIfNotNull(map, "last_name", data.getLastName());
        putIfNotNull(map, "username", data.getUsername());
        putIfNotNull(map, "photo_url", data.getPhotoUrl());
        putIfNotNull(map, "auth_date", data.getAuthDate());
        putIfNotNull(map, "hash", data.getHash());
        return map;
    }

    private static <T> void putIfNotNull(Map<String, String> map, String key, T value) {
        if (value != null) {
            map.put(key, value.toString());
        }
    }

    private static TelegramUser toGeneratedUser(TelegramUserModel user) {
        return new TelegramUser()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .photoUrl(user.getPhotoUrl())
                .authDate(user.getAuthDate())
                .hash(user.getHash());
    }
}
