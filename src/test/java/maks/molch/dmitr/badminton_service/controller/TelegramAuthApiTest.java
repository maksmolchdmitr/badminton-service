package maks.molch.dmitr.badminton_service.controller;

import maks.molch.dmitr.badminton_service.AbstractContainerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TelegramAuthApiTest extends AbstractContainerTest {

    @Test
    void testTelegramLogin() {
        dbBeforeInit(
                "/maks/molch/dmitr/badminton_service/controller/TelegramAuthApiTest/testTelegramLogin/before",
                "badminton"
        );
        dbAfterAssert(
                "/maks/molch/dmitr/badminton_service/controller/TelegramAuthApiTest/testTelegramLogin/after",
                "badminton"
        );
    }

}