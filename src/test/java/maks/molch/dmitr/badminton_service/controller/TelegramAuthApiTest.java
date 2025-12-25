package maks.molch.dmitr.badminton_service.controller;

import maks.molch.dmitr.badminton_service.AbstractContainerTest;
import maks.molch.dmitr.badminton_service.service.time.TimeService;
import maks.molch.dmitr.badminton_service.service.uuid.UuidGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.junit.jupiter.api.AfterEach;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class TelegramAuthApiTest extends AbstractContainerTest {
    @MockitoBean
    private TimeService timeService;
    @MockitoBean
    private UuidGenerator uuidGenerator;

    @AfterEach
    void cleanup() {
        dbCleanup("badminton");
    }

    @Test
    void testTelegramLogin() throws Exception {
        dbBeforeInit(
                "/maks/molch/dmitr/badminton_service/controller/TelegramAuthApiTest/testTelegramLogin/db/before",
                "badminton"
        );

        Instant mockedInstant = Instant.ofEpochSecond(1766345094);
        when(timeService.now()).thenReturn(mockedInstant);
        when(timeService.now(any(ZoneId.class)))
                .thenReturn(OffsetDateTime.ofInstant(mockedInstant, ZoneId.of("Europe/Moscow")));
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

    @Test
    void testRefreshToken() throws Exception {
        dbBeforeInit(
                "/maks/molch/dmitr/badminton_service/controller/TelegramAuthApiTest/testRefreshToken/db/before",
                "badminton"
        );

        Instant mockedInstant = Instant.ofEpochSecond(1766345094);
        when(timeService.now()).thenReturn(mockedInstant);
        when(timeService.now(any(ZoneId.class)))
                .thenReturn(OffsetDateTime.ofInstant(mockedInstant, ZoneId.of("Europe/Moscow")));
        when(uuidGenerator.random())
                .thenReturn(UUID.fromString("382a523e-d6d4-4f81-b615-586045948d8c")); // for new refresh token

        mockPostRequest(
                "/api/auth/refresh",
                "/maks/molch/dmitr/badminton_service/controller/TelegramAuthApiTest/testRefreshToken/json/request.json",
                "/maks/molch/dmitr/badminton_service/controller/TelegramAuthApiTest/testRefreshToken/json/response.json"
        );

        dbAfterAssert(
                "/maks/molch/dmitr/badminton_service/controller/TelegramAuthApiTest/testRefreshToken/db/after",
                "badminton"
        );
    }

}