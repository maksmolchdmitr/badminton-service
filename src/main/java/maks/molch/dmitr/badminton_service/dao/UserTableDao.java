package maks.molch.dmitr.badminton_service.dao;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserTableDao {
    private final DSLContext dsl;
}
