package com.itheima.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

@Slf4j
public class LoggingCacheErrorHandler implements CacheErrorHandler {
    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        logFailure("读取", exception, cache, key);
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        logFailure("写入", exception, cache, key);
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        logFailure("删除", exception, cache, key);
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        logFailure("清空", exception, cache, null);
    }

    private void logFailure(String operation, RuntimeException exception, Cache cache, Object key) {
        log.warn("Redis缓存{}失败，已降级为数据库访问：cache={}, key={}, reason={}",
                operation, cache.getName(), key, exception.getMessage());
        log.debug("Redis缓存异常详情", exception);
    }
}
