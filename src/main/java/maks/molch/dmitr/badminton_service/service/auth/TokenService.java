package maks.molch.dmitr.badminton_service.service.auth;

import maks.molch.dmitr.badminton_service.model.TelegramUserModel;
import maks.molch.dmitr.badminton_service.model.TokenModel;

import java.util.UUID;

public interface TokenService {
    TokenModel generate(TelegramUserModel telegramUserModel, UUID userId);
    TokenModel generate(UUID refreshToken);
}
