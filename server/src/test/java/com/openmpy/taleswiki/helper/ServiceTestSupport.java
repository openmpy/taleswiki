package com.openmpy.taleswiki.helper;

import com.openmpy.taleswiki.common.application.ImageS3Service;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;

@Transactional
@Import(EmbeddedRedisConfig.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public abstract class ServiceTestSupport {

    @Autowired
    protected RedisTemplate<String, Object> redisTemplate;

    @MockitoBean
    protected ImageS3Service imageS3Service;

    @MockitoBean
    protected S3Client s3Client;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }
}
