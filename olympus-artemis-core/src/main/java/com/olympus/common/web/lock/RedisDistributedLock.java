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
    private final RedisTemplate<String, Object> redisTemplate;
    /**
     * REDIS锁
     */
    private static final String LOCK_KEY = "LX-TRADE-CENTER:REDIS-LOCK:LOCK_TRADE:";
    /**
     * 随机数
     */
    private static final String LOCK_VALUE = "RedisDistributedLock.class";
    /**
     * 成功的返回值
     */
    private static final Long RELEASE_SUCCESS = 1L;
    /**
     * 非成功的返回值
     */
    private static final Long RELEASE_UNSUCCESSFUL = 0L;

    public RedisDistributedLock(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Boolean simpleLock(String lockKey, Long timeout) {
        return commonLock(lockKey, LOCK_VALUE, timeout);
    }

    /**
     * 可重入锁
     */
    public boolean reentrantLock(String lockKey, String lockValue, long timeoutInSeconds) {
        lockKey = getLockKey(lockKey);
        Object oldLockValue = redisTemplate.opsForValue().get(lockKey);
        if (Objects.nonNull(oldLockValue) && !"null".equals(oldLockValue.toString())) {
            if (oldLockValue.toString().equals(lockValue)) {
                return true;
            }
        }
        return commonLock(lockKey, lockValue, timeoutInSeconds);
    }

    /**
     * 通用锁
     */
    public Boolean commonLock(String lockKey, String lockValue, Long timeout) {
        if (StringUtils.isBlank(lockKey)) {
            return false;
        }
        lockKey = getLockKey(lockKey);
        if (Objects.isNull(timeout) || timeout == -1) {
            timeout = 20L;
        }
        Boolean lockIfSuccess = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, timeout, TimeUnit.SECONDS);
        if (Objects.isNull(lockIfSuccess)){
            return false;
        }
        return lockIfSuccess;
    }

    /**
     * 重试锁
     */
    public boolean retryLock(String lockKey, String lockValue, long timeoutInSeconds) {
        String lockKeyFull = getLockKey(lockKey);

        long startTimestamp = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTimestamp) < timeoutInSeconds * 1000) {
            Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKeyFull, lockValue, timeoutInSeconds, TimeUnit.SECONDS);
            if (lockAcquired != null && lockAcquired) {
                return true;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(100); // Wait before retrying
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return false;
    }

    public Boolean simpleReleaseLock(String lockKey) {
        return releaseLock(lockKey, LOCK_VALUE);
    }

    public Boolean releaseLock(String lockKey, String lockValue) {
        if (StringUtils.isBlank(lockKey)) {
            return false;
        }
        lockKey = getLockKey(lockKey);
        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setResultType(Long.class);
        defaultRedisScript.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
        Long result = redisTemplate.execute(defaultRedisScript, List.of(lockKey), lockValue);
        if (Objects.nonNull(result) && RELEASE_SUCCESS.equals(result)) {
            return true;
        }
        // 如果解锁未成功 且 key依然存在 返回解锁失败
        if (Objects.nonNull(result) && RELEASE_UNSUCCESSFUL.equals(result)) {
            Boolean isKeyExist = redisTemplate.hasKey(lockKey);
            return !Objects.nonNull(isKeyExist) || !isKeyExist;
        }
        return true;
    }

    private String getLockKey(String lockKey) {
        return LOCK_KEY + lockKey;
    }

}