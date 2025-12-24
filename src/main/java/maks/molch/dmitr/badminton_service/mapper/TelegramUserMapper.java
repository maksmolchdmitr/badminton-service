package maks.molch.dmitr.badminton_service.mapper;

import maks.molch.dmitr.badminton_service.generated.jooq.tables.records.UserTableRecord;
import maks.molch.dmitr.badminton_service.generated.model.TelegramUser;
import maks.molch.dmitr.badminton_service.model.TelegramUserModel;
import maks.molch.dmitr.badminton_service.service.time.TimeService;
import maks.molch.dmitr.badminton_service.service.uuid.UuidGenerator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Mapper(componentModel = "spring")
public abstract class TelegramUserMapper {
    @Autowired
    protected TimeService timeService;
    @Autowired
    protected UuidGenerator uuidGenerator;

    public abstract TelegramUserModel toModel(TelegramUser telegramUser);

    @Mapping(target = "id", expression = "java(generateUuid())")
    @Mapping(target = "tgId", source = "id")
    @Mapping(target = "createdAt", expression = "java(generateLocalDateTime())")
    public abstract UserTableRecord toRecord(TelegramUserModel telegramUserModel);

    protected UUID generateUuid() {
        return uuidGenerator.random();
    }

    protected LocalDateTime generateLocalDateTime() {
        return timeService.now(ZoneId.systemDefault()).toLocalDateTime();
    }
}
