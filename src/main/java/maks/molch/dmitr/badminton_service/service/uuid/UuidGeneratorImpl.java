package maks.molch.dmitr.badminton_service.service.uuid;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Profile("!test")
public class UuidGeneratorImpl implements UuidGenerator {
    @Override
    public UUID random() {
        return UUID.randomUUID();
    }
}
