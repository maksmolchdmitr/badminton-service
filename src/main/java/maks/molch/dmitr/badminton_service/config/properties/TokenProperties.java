package maks.molch.dmitr.badminton_service.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "token")
@Configuration
@Data
public class TokenProperties {
    private String privateKey;
    private String publicKey;
    private Long expireTimeInSeconds;
    private Integer refreshTokenExpireTimeInDays;
}