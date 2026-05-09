package com.internship.tool;

import com.internship.tool.entity.Policy;
import com.internship.tool.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false",
                "spring.flyway.enabled=false",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "spring.mail.host=localhost",
                "spring.mail.port=1025",
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration"
        }
)
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("test")
class PolicyIntegrationTest {

    static {
        // Fix PostgreSQL timezone issue
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");
    }

    @SuppressWarnings("resource")
    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @SuppressWarnings("resource")
    @Container
    static final GenericContainer<?> redis =
            new GenericContainer<>("redis:7")
                    .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

        // PostgreSQL
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name",
                () -> "org.postgresql.Driver");

        // Hibernate / JPA
        registry.add("spring.jpa.database-platform",
                () -> "org.hibernate.dialect.PostgreSQLDialect");

        registry.add("spring.jpa.properties.hibernate.jdbc.time_zone",
                () -> "UTC");

        // Redis
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port",
                () -> redis.getMappedPort(6379));
    }

    @Autowired
    private PolicyRepository policyRepository;

    private Policy policy;

    @BeforeEach
    void setup() {
        policyRepository.deleteAll();

        policy = new Policy();
        policy.setTitle("Integration Test Policy");
        policy.setDescription("Testing full CRUD");
        policy.setCategory("Security");
        policy.setStatus("ACTIVE");
        policy.setCreatedBy("test@test.com");
        policy.setDeleted(false);
        // set dueDate to exercise deadline column
        policy.setDueDate(java.time.LocalDateTime.now().plusDays(7));
    }

    @Test
    void testCreatePolicy() {
        Policy saved = policyRepository.save(policy);

        assertNotNull(saved.getId());
        assertEquals("Integration Test Policy", saved.getTitle());
        assertEquals("Security", saved.getCategory());
        assertEquals("ACTIVE", saved.getStatus());
    }

    @Test
    void testReadPolicy() {
        Policy saved = policyRepository.save(policy);

        Optional<Policy> found = policyRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getTitle(), found.get().getTitle());
        assertEquals(saved.getDescription(), found.get().getDescription());
    }

    @Test
    void testUpdatePolicy() {
        Policy saved = policyRepository.save(policy);

        saved.setTitle("Updated Policy");
        saved.setStatus("INACTIVE");

        Policy updated = policyRepository.save(saved);

        assertEquals("Updated Policy", updated.getTitle());
        assertEquals("INACTIVE", updated.getStatus());
    }

    @Test
    void testDeletePolicy() {
        Policy saved = policyRepository.save(policy);

        policyRepository.deleteById(saved.getId());

        Optional<Policy> deleted = policyRepository.findById(saved.getId());

        assertTrue(deleted.isEmpty());
    }
}