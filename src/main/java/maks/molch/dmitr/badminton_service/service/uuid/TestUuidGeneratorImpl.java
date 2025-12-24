package maks.molch.dmitr.badminton_service.service.uuid;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Profile("test")
public class TestUuidGeneratorImpl implements UuidGenerator {
    @Override
    public UUID random() {
        return UUID.fromString("1bdc818f-7851-4aef-b31a-a6999a4cab52");
    }
}
