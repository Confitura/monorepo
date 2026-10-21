package pl.confitura.jelatyna;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.postgresql.PostgreSQLContainer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// The migration creating News Entries seeds the public feed they replace. It migrates its own
// database in the shared container, so the News Entries other tests delete stay seeded here.
class NewsEntrySeedMigrationTest {

    private static final String DATABASE = "news_entry_seed";
    private static final PostgreSQLContainer postgres = BaseIntegrationTest.postgres;

    @BeforeAll
    static void migrate() throws SQLException {
        postgres.start();
        try (Connection connection = connect(postgres.getDatabaseName());
             Statement statement = connection.createStatement()) {
            statement.execute("DROP DATABASE IF EXISTS " + DATABASE);
            statement.execute("CREATE DATABASE " + DATABASE);
        }
        Flyway.configure().dataSource(url(DATABASE), postgres.getUsername(), postgres.getPassword()).load().migrate();
    }

    @Test
    void seedsEveryEntryOfThePublicFeedVerbatimPublishedAndSlugged() throws SQLException {
        // The feed as fetched from api.confitura.pl/api/resources/edition-2026/news.json, newest first.
        JsonNode feed = JsonMapper.builder().build()
                .readTree(getClass().getResourceAsStream("/news/feed-2026-10-21.json")).get("all");
        List<String> slugs = List.of("thank-you", "were-in-the-home-stretch", "tickets-2", "agenda-tickets",
                "v4p", "confitura-2025-wrap-up", "tickets", "tickets-are-here");
        List<List<Object>> expected = new ArrayList<>();
        for (int i = 0; i < feed.size(); i++) {
            JsonNode entry = feed.get(i);
            expected.add(List.of(entry.get("title").asString(), slugs.get(i), entry.get("body").asString(),
                    Instant.parse(entry.get("publishedAt").asString()), true));
        }

        List<List<Object>> seeded = new ArrayList<>();
        try (Connection connection = connect(DATABASE);
             Statement statement = connection.createStatement();
             ResultSet rows = statement.executeQuery(
                     "SELECT title, slug, body, date, published FROM news_entry ORDER BY date DESC")) {
            while (rows.next()) {
                seeded.add(List.of(rows.getString("title"), rows.getString("slug"), rows.getString("body"),
                        rows.getObject("date", OffsetDateTime.class).toInstant(), rows.getBoolean("published")));
            }
        }

        assertThat(seeded).containsExactlyElementsOf(expected);
    }

    @Test
    void aSlugIsRequiredAndNamesOnlyOneEntry() throws SQLException {
        try (Connection connection = connect(DATABASE);
             Statement statement = connection.createStatement()) {
            assertThatThrownBy(() -> statement.execute("""
                    INSERT INTO news_entry (id, title, slug, body, date, published)
                    VALUES ('clash', 'Tickets', 'tickets', '', now(), false)
                    """))
                    .hasMessageContaining("news_entry_slug_key");
            assertThatThrownBy(() -> statement.execute("""
                    INSERT INTO news_entry (id, title, body, date, published)
                    VALUES ('no-slug', 'Tickets', '', now(), false)
                    """))
                    .hasMessageContaining("null value in column \"slug\"");
        }
    }

    private static Connection connect(String database) throws SQLException {
        return DriverManager.getConnection(url(database), postgres.getUsername(), postgres.getPassword());
    }

    private static String url(String database) {
        return "jdbc:postgresql://%s:%d/%s".formatted(
                postgres.getHost(), postgres.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT), database);
    }
}
