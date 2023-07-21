package ru.all_easy.push.common;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;
import ru.all_easy.push.telegram.api.controller.TestConfig;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
@Import(TestConfig.class)
public abstract class IntegrationTest {
    public static Jedis jedis;

    @Container
    private static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer()
            .withDatabaseName("test")
            .withUsername("username")
            .withPassword("password");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @Container
    private static GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis"))
            .withExposedPorts(6379)
            .waitingFor(Wait.defaultWaitStrategy());

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", redisContainer::getFirstMappedPort);

        jedis = new Jedis(redisContainer.getHost(), redisContainer.getFirstMappedPort());
    }

    @Test
    void postgresqlContainerTest() {
        assertThat(postgresqlContainer.isRunning()).isTrue();
    }

    @Test
    void redisContainerTest() {
        assertThat(redisContainer.isRunning()).isTrue();
    }
}
