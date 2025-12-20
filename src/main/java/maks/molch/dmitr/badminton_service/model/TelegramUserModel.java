package maks.molch.dmitr.badminton_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ParametersAreNonnullByDefault
public class TelegramUserModel {
    private Long id;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String username;

    @JsonProperty("photo_url")
    private String photoUrl;

    @JsonProperty("auth_date")
    private Long authDate;

    private String hash;
}
