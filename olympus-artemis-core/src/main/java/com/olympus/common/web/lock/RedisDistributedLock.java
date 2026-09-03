package com.olympus.common.web.lock;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 2019-07-09
 * redis分布式锁实现
 * @author eddie
 */
@Slf4j
public class RedisDistributedLock {

    /**
     * REDIS操作管理器
     */
    private final RedisTemplate<String, String> redisTemplate;
    /**
     * REDIS锁
     */
    private static final String LOCK_KEY = "LX-TRADE-CENTER:REDIS-LOCK:LOCK_TRADE:";
    /**
     * 随机数
     */
    private static final String LOCK_VALUE = UUID.randomUUID().toString();
    /**
     * 成功的返回值
     */
    private static final Long RELEASE_SUCCESS = 1L;

    public RedisDistributedLock(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Boolean lock(String lockKey, Long timeout) {
        return lock(lockKey, LOCK_VALUE, timeout);
    }

    public Boolean lock(String lockKey, String lockValue, Long timeout) {
        if (StringUtils.isBlank(lockKey)) {
            return false;
        }
        lockKey = LOCK_KEY.concat(lockKey);
        if (Objects.isNull(timeout)) {
            timeout = 20L;
        }
        Boolean lockIfSuccess = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, timeout, TimeUnit.SECONDS);
        if (Objects.isNull(lockIfSuccess)){
            return false;
        }
        return lockIfSuccess;
    }

    public Boolean releaseLock(String lockKey) {
        return releaseLock(lockKey, LOCK_VALUE);
    }

    public Boolean releaseLock(String lockKey, String lockValue) {
        if (StringUtils.isBlank(lockKey)) {
            return false;
        }
        lockKey = LOCK_KEY.concat(lockKey);

        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setResultType(Long.class);
        defaultRedisScript.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
        Long result = redisTemplate.execute(defaultRedisScript, List.of(lockKey), lockValue);
        return Objects.nonNull(result) && RELEASE_SUCCESS.equals(result);
    }
}