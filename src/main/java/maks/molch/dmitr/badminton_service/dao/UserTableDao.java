package maks.molch.dmitr.badminton_service.dao;

import lombok.RequiredArgsConstructor;
import maks.molch.dmitr.badminton_service.generated.jooq.tables.UserTable;
import maks.molch.dmitr.badminton_service.generated.jooq.tables.records.UserTableRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static maks.molch.dmitr.badminton_service.generated.jooq.tables.UserTable.USER_TABLE;

@Service
@RequiredArgsConstructor
public class UserTableDao {
    private final DSLContext dsl;

    public UUID upsert(UserTableRecord record) {
        return dsl.insertInto(USER_TABLE)
                .set(record)
                .onConflict(USER_TABLE.TG_ID)
                .doUpdate()
                .set(USER_TABLE.FIRST_NAME, record.getFirstName())
                .set(USER_TABLE.LAST_NAME, record.getLastName())
                .set(USER_TABLE.PHOTO_URL, record.getPhotoUrl())
                .set(USER_TABLE.USERNAME, record.getUsername())
                .returning(USER_TABLE.ID)
                .fetchOne(USER_TABLE.ID);
    }
}
