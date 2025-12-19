package maks.molch.dmitr.badminton_service.telegram.service;

import maks.molch.dmitr.badminton_service.telegram.model.TelegramUser;
import java.util.Map;

/**
 * Service for validating Telegram login widget authorization payload and constructing a {@link TelegramUser}.
 */
public interface TelegramAuthService {

    /**
     * Validates Telegram authorization data and returns the corresponding user.
     *
     * @param authData query parameters received from Telegram login widget
     * @return validated Telegram user
     * @throws SecurityException if validation fails
     */
    TelegramUser checkTelegramAuthorization(Map<String, String> authData);
}
