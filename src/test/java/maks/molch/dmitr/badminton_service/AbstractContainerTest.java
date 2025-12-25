package maks.molch.dmitr.badminton_service;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.json.JsonCompareMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static org.dbunit.Assertion.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("resource")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractContainerTest {
    @Autowired
    private MockMvc mockMvc;

    private static final PostgreSQLContainer<?> DATABASE_CONTAINER;

    private IDatabaseTester databaseTester;
    private final Map<String, DatabaseConnection> schemaConnections = new HashMap<>();

    static {
        DATABASE_CONTAINER = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("random_walk_postgres")
                .withUsername("postgres")
                .withPassword("postgres");
        DATABASE_CONTAINER.start();
    }

    @BeforeAll
    public void setUp() throws Exception {
        databaseTester = new JdbcDatabaseTester(
                DATABASE_CONTAINER.getDriverClassName(),
                DATABASE_CONTAINER.getJdbcUrl(),
                DATABASE_CONTAINER.getUsername(),
                DATABASE_CONTAINER.getPassword()
        );
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DATABASE_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", DATABASE_CONTAINER::getUsername);
        registry.add("spring.datasource.password", DATABASE_CONTAINER::getPassword);
    }

    protected void mockPostRequest(String path, String requestPath, String responsePath) throws Exception {
        String requestJson = Files.readString(Paths.get("src/test/resources/" + requestPath));
        String expectedResponseJson = Files.readString(Paths.get("src/test/resources/" + responsePath));

        mockMvc.perform(MockMvcRequestBuilders.post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseJson, JsonCompareMode.STRICT));
    }

    /**
     * Подготовка БД перед тестом
     *
     * @param csvFolderPath путь к папке с CSV для каждой таблицы
     */
    protected void dbBeforeInit(String csvFolderPath, String schema) {
        try {
            DatabaseConnection dbConnection = getConnection(schema);

            File folder = new File("src/test/resources/" + csvFolderPath);
            if (!folder.exists() || !folder.isDirectory()) {
                throw new RuntimeException("CSV folder not found: " + folder.getAbsolutePath());
            }

            IDataSet dataSet = new CsvDataSet(folder);

            DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    /**
     * Очистка БД (удаление всех данных) без зависимости от конкретных таблиц
     * Использует временное отключение foreign key constraints для безопасного удаления
     *
     * @param schema схема базы данных
     */
    @SuppressWarnings({"unused", "SqlDialectInspection", "SqlNoDataSourceInspection"})
    protected void dbCleanup(String schema) {
        try {
            DatabaseConnection dbConnection = getConnection(schema);
            Connection connection = dbConnection.getConnection();

            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                try (ResultSet tables = connection.getMetaData().getTables(null, schema, null, new String[]{"TABLE"})) {
                    while (tables.next()) {
                        String tableName = tables.getString("TABLE_NAME");
                        String qualifiedTableName = schema + "." + tableName;
                        statement.executeUpdate("DELETE FROM " + qualifiedTableName);
                    }
                }
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to cleanup database", e);
        }
    }

    /**
     * Проверка БД после теста
     *
     * @param csvFolderPath путь к папке с ожидаемыми CSV для каждой таблицы
     */
    protected void dbAfterAssert(String csvFolderPath, String schema) {
        try {
            DatabaseConnection dbConnection = getConnection(schema);

            File folder = new File("src/test/resources/" + csvFolderPath);
            if (!folder.exists() || !folder.isDirectory()) {
                throw new RuntimeException("CSV folder not found: " + folder.getAbsolutePath());
            }

            IDataSet expectedDataSet = new CsvDataSet(folder);
            IDataSet actualDataSet = dbConnection.createDataSet();

            assertEquals(expectedDataSet, actualDataSet);

        } catch (Exception e) {
            throw new RuntimeException("Failed to assert database", e);
        }
    }

    private DatabaseConnection getConnection(String schema) throws Exception {
        if (schemaConnections.containsKey(schema)) {
            return schemaConnections.get(schema);
        }

        DatabaseConnection dbConnection = new DatabaseConnection(
                databaseTester.getConnection().getConnection(),
                schema
        );

        dbConnection.getConfig().setProperty(
                DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
                new org.dbunit.ext.postgresql.PostgresqlDataTypeFactory()
        );

        dbConnection.getConfig().setProperty(
                DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES,
                true
        );

        schemaConnections.put(schema, dbConnection);

        return dbConnection;
    }
}
