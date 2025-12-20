package maks.molch.dmitr.badminton_service.service.auth;

import maks.molch.dmitr.badminton_service.model.TelegramUserModel;
import maks.molch.dmitr.badminton_service.model.TokenModel;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    @Override
    public TokenModel generate(TelegramUserModel telegramUserModel) {
        // TODO: implement token generation
        return new TokenModel("", "");
    }
}
