package maks.molch.dmitr.badminton_service.service;

import maks.molch.dmitr.badminton_service.model.TelegramUserModel;

public interface TelegramAuthService {
    boolean checkTelegramUserData(TelegramUserModel telegramUserModel);
}
