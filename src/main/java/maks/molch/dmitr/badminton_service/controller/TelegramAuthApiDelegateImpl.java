package maks.molch.dmitr.badminton_service.controller;

import lombok.RequiredArgsConstructor;
import maks.molch.dmitr.badminton_service.generated.api.TelegramAuthApiDelegate;
import maks.molch.dmitr.badminton_service.generated.model.TelegramUser;
import maks.molch.dmitr.badminton_service.generated.model.TokenResponse;
import maks.molch.dmitr.badminton_service.mapper.TelegramUserMapper;
import maks.molch.dmitr.badminton_service.mapper.TokenMapper;
import maks.molch.dmitr.badminton_service.model.TelegramUserModel;
import maks.molch.dmitr.badminton_service.model.TokenModel;
import maks.molch.dmitr.badminton_service.service.auth.TelegramAuthService;
import maks.molch.dmitr.badminton_service.service.auth.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramAuthApiDelegateImpl implements TelegramAuthApiDelegate {
    private final TelegramAuthService telegramAuthService;
    private final TokenService tokenService;
    private final TelegramUserMapper telegramUserMapper;
    private final TokenMapper tokenMapper;

    @Override
    public ResponseEntity<TokenResponse> telegramLogin(TelegramUser telegramUser) {
        TelegramUserModel telegramUserModel = telegramUserMapper.toModel(telegramUser);
        telegramAuthService.checkTelegramUserData(telegramUserModel);

        TokenModel tokenModel = tokenService.generate(telegramUserModel);
        TokenResponse response = tokenMapper.toResponse(tokenModel);

        return ResponseEntity.ok(response);
    }
}
