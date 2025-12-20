package maks.molch.dmitr.badminton_service.controller;

import lombok.RequiredArgsConstructor;
import maks.molch.dmitr.badminton_service.generated.api.TelegramAuthApiDelegate;
import maks.molch.dmitr.badminton_service.generated.model.TelegramUser;
import maks.molch.dmitr.badminton_service.mapper.TelegramUserMapper;
import maks.molch.dmitr.badminton_service.model.TelegramUserModel;
import maks.molch.dmitr.badminton_service.service.TelegramAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramAuthApiDelegateImpl implements TelegramAuthApiDelegate {

    private static final String TG_USER_COOKIE = "tg_user";

    private final TelegramAuthService telegramAuthService;
    private final TelegramUserMapper telegramUserMapper;

    @Override
    public ResponseEntity<TelegramUser> telegramLogin(TelegramUser telegramUser) {
        TelegramUserModel telegramUserModel = telegramUserMapper.toModel(telegramUser);
        boolean isValid = telegramAuthService.checkTelegramUserData(telegramUserModel);

        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ResponseCookie cookie = ResponseCookie.from(TG_USER_COOKIE, telegramUser.getId().toString())
                .path("/")
                .httpOnly(true)
                .secure(true)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(telegramUser);
    }
}
