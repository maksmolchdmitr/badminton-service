package maks.molch.dmitr.badminton_service.model;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record TelegramUserModel(
        Long id,
        String firstName,
        String lastName,
        String username,
        String photoUrl,
        Long authDate,
        String hash
) {
}
