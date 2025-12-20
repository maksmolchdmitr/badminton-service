package maks.molch.dmitr.badminton_service.service.auth;

import maks.molch.dmitr.badminton_service.model.TelegramUserModel;

public interface TelegramAuthService {

    /**
     * Checks if the Telegram user data is valid by comparing the hash with the calculated hash.
     *
     * @param telegramUserModel Telegram user data
     * @throws IllegalArgumentException if some data is wrong
     * @throws SecurityException        if the hash is invalid
     */
    void checkTelegramUserData(TelegramUserModel telegramUserModel);
}
