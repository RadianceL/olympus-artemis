package com.el.common.support;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 2019-07-09
 * redis分布式锁实现
 * @author eddie
 */
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedisDistributedLock implements AutoCloseable {

    /**
     * REDIS操作管理器
     */
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * REDIS锁
     */
    private static final String LOCK_KEY = "LX-TRADE-CENTER:REDIS-LOCK:LOCK_TRADE";

    /**
     * 随机数
     */
    private static final String LOCK_VALUE = UUID.randomUUID().toString();

    /**
     * 成功的返回值
     */
    private static final Long RELEASE_SUCCESS = 1L;

    public Boolean lock() {
        long timeout = 20;
        Boolean lockIfSuccess = redisTemplate.opsForValue().setIfAbsent(LOCK_KEY, LOCK_VALUE, timeout, TimeUnit.SECONDS);
        if (Objects.isNull(lockIfSuccess)){
            return false;
        }
        return lockIfSuccess;
    }

    @Override
    public void close() {
        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setResultType(Long.class);
        defaultRedisScript.setScriptText("if redis.call('get', KEYS[1]) == KEYS[2] then return redis.call('del', KEYS[1]) else return 0 end");
        redisTemplate.execute(defaultRedisScript, Arrays.asList(LOCK_KEY, LOCK_VALUE));
    }
}