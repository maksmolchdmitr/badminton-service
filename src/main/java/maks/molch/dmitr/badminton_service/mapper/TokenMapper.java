package maks.molch.dmitr.badminton_service.mapper;

import maks.molch.dmitr.badminton_service.generated.model.TokenResponse;
import maks.molch.dmitr.badminton_service.model.TokenModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TokenMapper {
    TokenResponse toResponse(TokenModel tokenModel);
}
