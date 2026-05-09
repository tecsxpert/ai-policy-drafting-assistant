package com.internship.tool;

import com.internship.tool.entity.Policy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@Disabled("Integration test requires Docker/Testcontainers — enable manually")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.task.scheduling.enabled=false",
                "spring.mail.host=localhost",
                "spring.mail.port=1025",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SuppressWarnings("resource")
public class Day11PolicyCrudIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("testdb")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @Container
    static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:7").withExposedPorts(6379);
        static {
                // Ensure containers are started before DynamicPropertySource reads mapped ports
                POSTGRES.start();
                REDIS.start();
        }

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");

        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    }

    private String baseUrl() {
        return "http://localhost:" + port + "/api/policies";
    }

    @Test
    public void fullCrudFlow_restEndpoints() {

        TestRestTemplate auth = restTemplate.withBasicAuth("admin", "admin");

        Policy p = Policy.builder()
                .title("Day11 Policy")
                .description("Testcontainers CRUD")
                .category("Test")
                .status("DRAFT")
                .createdBy("tester")
                .createdAt(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(5))
                .build();

        // CREATE
        ResponseEntity<Policy> createResp =
                auth.postForEntity(baseUrl() + "/create", p, Policy.class);

        assertEquals(HttpStatus.OK, createResp.getStatusCode());
        Long id = createResp.getBody().getId();
        assertNotNull(id);

        // READ
        ResponseEntity<Policy> getResp =
                auth.getForEntity(baseUrl() + "/" + id, Policy.class);

        assertEquals(HttpStatus.OK, getResp.getStatusCode());

        // UPDATE
        Policy update = getResp.getBody();
        update.setTitle("Updated Policy");

        ResponseEntity<Policy> putResp =
                auth.exchange(baseUrl() + "/" + id, HttpMethod.PUT,
                        new HttpEntity<>(update),
                        Policy.class);

        assertEquals(HttpStatus.OK, putResp.getStatusCode());
        assertEquals("Updated Policy", putResp.getBody().getTitle());

        // DELETE
        ResponseEntity<String> delResp =
                auth.exchange(baseUrl() + "/" + id, HttpMethod.DELETE,
                        null, String.class);

        assertEquals(HttpStatus.OK, delResp.getStatusCode());
    }
}