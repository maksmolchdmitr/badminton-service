package maks.molch.dmitr.badminton_service.model;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public record TokenModel(
        String accessToken,
        String refreshToken
) {
}
