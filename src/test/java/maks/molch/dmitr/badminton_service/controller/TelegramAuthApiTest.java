package maks.molch.dmitr.badminton_service.controller;

import maks.molch.dmitr.badminton_service.AbstractContainerTest;
import maks.molch.dmitr.badminton_service.service.time.TimeService;
import maks.molch.dmitr.badminton_service.service.uuid.UuidGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.when;

@SpringBootTest
class TelegramAuthApiTest extends AbstractContainerTest {
    @MockitoSpyBean
    private TimeService timeService;
    @MockitoSpyBean
    private UuidGenerator uuidGenerator;

    @Test
    void testTelegramLogin() throws Exception {
        dbBeforeInit(
                "/maks/molch/dmitr/badminton_service/controller/TelegramAuthApiTest/testTelegramLogin/db/before",
                "badminton"
        );

        when(timeService.now()).thenReturn(Instant.ofEpochSecond(1766345094));
        when(uuidGenerator.random())
                .thenReturn(UUID.fromString("1bdc818f-7851-4aef-b31a-a6999a4cab52")) // for user id
                .thenReturn(UUID.fromString("282a523e-d6d4-4f81-b615-586045948d8c")); // for refresh token

        mockPostRequest(
                "/api/auth/telegram/login",
                "/maks/molch/dmitr/badminton_service/controller/TelegramAuthApiTest/testTelegramLogin/json/request.json",
                "/maks/molch/dmitr/badminton_service/controller/TelegramAuthApiTest/testTelegramLogin/json/response.json"
        );

        dbAfterAssert(
                "/maks/molch/dmitr/badminton_service/controller/TelegramAuthApiTest/testTelegramLogin/db/after",
                "badminton"
        );
    }

}