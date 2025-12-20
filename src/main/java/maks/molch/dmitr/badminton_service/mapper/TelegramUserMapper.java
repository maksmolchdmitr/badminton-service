package maks.molch.dmitr.badminton_service.mapper;

import maks.molch.dmitr.badminton_service.generated.model.TelegramUser;
import maks.molch.dmitr.badminton_service.model.TelegramUserModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TelegramUserMapper {

    TelegramUserModel toModel(TelegramUser telegramUser);
}
