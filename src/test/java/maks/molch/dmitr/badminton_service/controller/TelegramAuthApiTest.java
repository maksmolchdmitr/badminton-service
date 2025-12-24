package maks.molch.dmitr.badminton_service.controller;

import maks.molch.dmitr.badminton_service.AbstractContainerTest;
import maks.molch.dmitr.badminton_service.service.time.TimeService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.time.Instant;

import static org.mockito.Mockito.when;

@SpringBootTest
class TelegramAuthApiTest extends AbstractContainerTest {
    @MockitoSpyBean
    private TimeService timeService;

    @Test
    void testTelegramLogin() throws Exception {
        dbBeforeInit(
                "/maks/molch/dmitr/badminton_service/controller/TelegramAuthApiTest/testTelegramLogin/db/before",
                "badminton"
        );

        when(timeService.now()).thenReturn(Instant.ofEpochSecond(1766345094));

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