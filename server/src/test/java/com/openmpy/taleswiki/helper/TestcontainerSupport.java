package com.openmpy.taleswiki.helper;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class TestcontainerSupport {

    @Container
    static final GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.4.2"))
            .withExposedPorts(6379);

    @Container
    static final ElasticsearchContainer ELASTICSEARCH_CONTAINER = new ElasticsearchContainer(
            DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.12.2"))
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("bootstrap.memory_lock", "true")
            .withEnv("ES_JAVA_OPTS", "-Xms1g -Xmx1g")
            .withReuse(true)
            .withExposedPorts(9200);

    @DynamicPropertySource
    private static void registerProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.timeout", () -> 10000L);

        registry.add("spring.elasticsearch.uris", ELASTICSEARCH_CONTAINER::getHttpHostAddress);
        registry.add("spring.elasticsearch.port", () -> ELASTICSEARCH_CONTAINER.getMappedPort(9200));
    }
}
