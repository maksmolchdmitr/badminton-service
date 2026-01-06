package maks.molch.dmitr.badminton_service.controller;

import lombok.RequiredArgsConstructor;
import maks.molch.dmitr.badminton_service.generated.api.UsersApiDelegate;
import maks.molch.dmitr.badminton_service.generated.model.User;
import maks.molch.dmitr.badminton_service.service.time.TimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersApiDelegateImpl implements UsersApiDelegate {
    private final TimeService timeService;

    @Override
    public ResponseEntity<User> getCurrentUser() {
        return ResponseEntity.ok(new User(
                UUID.randomUUID().toString(),
                134341314L,
                timeService.now(ZoneId.systemDefault())
        ));
    }
}
