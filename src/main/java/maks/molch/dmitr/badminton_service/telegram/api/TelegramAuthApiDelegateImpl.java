package maks.molch.dmitr.badminton_service.telegram.api;

import lombok.RequiredArgsConstructor;
import maks.molch.dmitr.badminton_service.generated.api.TelegramAuthApiDelegate;
import maks.molch.dmitr.badminton_service.generated.model.TelegramAuthData;
import maks.molch.dmitr.badminton_service.generated.model.TelegramUser;
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
        maks.molch.dmitr.badminton_service.telegram.model.TelegramUser user = telegramAuthService.checkTelegramAuthorization(authData);

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
        if (data == null) {
            throw new IllegalArgumentException("Auth data is empty");
        }

        Map<String, String> map = new HashMap<>();
        if (data.getId() != null) {
            map.put("id", data.getId().toString());
        }
        if (data.getFirstName() != null) {
            map.put("first_name", data.getFirstName());
        }
        if (data.getLastName() != null) {
            map.put("last_name", data.getLastName());
        }
        if (data.getUsername() != null) {
            map.put("username", data.getUsername());
        }
        if (data.getPhotoUrl() != null) {
            map.put("photo_url", data.getPhotoUrl());
        }
        if (data.getAuthDate() != null) {
            map.put("auth_date", data.getAuthDate().toString());
        }
        if (data.getHash() != null) {
            map.put("hash", data.getHash());
        }
        return map;
    }

    private static TelegramUser toGeneratedUser(maks.molch.dmitr.badminton_service.telegram.model.TelegramUser user) {
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
