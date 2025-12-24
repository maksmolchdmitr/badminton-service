package maks.molch.dmitr.badminton_service.controller;

import maks.molch.dmitr.badminton_service.AbstractContainerTest;
import maks.molch.dmitr.badminton_service.service.time.TimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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