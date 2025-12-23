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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.dbunit.Assertion.assertEquals;

@SuppressWarnings("resource")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractContainerTest {

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
