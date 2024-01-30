package com.redis;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class SpringBootTestApplicationTests {

    @Resource
    private StringRedisTemplate redis;

    @Test
    public void contextLoads() {

        ValueOperations<String, String> operations = redis.opsForValue();

        operations.set("c", "xxxxx");
        System.out.println(operations.get("c"));

        redis.delete("c");
        System.out.println(operations.get("c"));

    }

}
