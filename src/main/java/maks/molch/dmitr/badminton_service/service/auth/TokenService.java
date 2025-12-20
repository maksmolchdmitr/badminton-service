package maks.molch.dmitr.badminton_service.service.auth;

import maks.molch.dmitr.badminton_service.model.TelegramUserModel;
import maks.molch.dmitr.badminton_service.model.TokenModel;

public interface TokenService {
    TokenModel generate(TelegramUserModel telegramUserModel);
}
