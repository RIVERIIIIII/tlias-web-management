package com.itheima;

import com.itheima.config.CacheNames;
import com.itheima.config.LoggingCacheErrorHandler;
import com.itheima.mapper.DeptMapper;
import com.itheima.pojo.Dept;
import com.itheima.service.DeptService;
import com.itheima.service.impl.DeptServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(CacheBehaviorTests.TestConfig.class)
class CacheBehaviorTests {
    @Autowired
    private DeptService deptService;
    @Autowired
    private DeptMapper deptMapper;
    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void resetState() {
        reset(deptMapper);
        Cache cache = cacheManager.getCache(CacheNames.DEPT_LIST);
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    void repeatedDepartmentQueryUsesCache() {
        Dept dept = new Dept(1, "研发部", LocalDateTime.now(), LocalDateTime.now());
        when(deptMapper.findAll()).thenReturn(List.of(dept));

        assertEquals(List.of(dept), deptService.findAll());
        assertEquals(List.of(dept), deptService.findAll());

        verify(deptMapper, times(1)).findAll();
    }

    @Test
    void departmentUpdateEvictsListCache() {
        Dept dept = new Dept(1, "研发部", LocalDateTime.now(), LocalDateTime.now());
        when(deptMapper.findAll()).thenReturn(List.of(dept));

        deptService.findAll();
        deptService.findAll();
        deptService.update(dept);
        deptService.findAll();

        verify(deptMapper, times(2)).findAll();
        verify(deptMapper).update(dept);
    }

    @Test
    void cacheErrorsDoNotReplaceBusinessResultWithCacheFailure() {
        LoggingCacheErrorHandler handler = new LoggingCacheErrorHandler();
        Cache cache = mock(Cache.class);
        RuntimeException failure = new RuntimeException("redis unavailable");
        when(cache.getName()).thenReturn("testCache");

        assertDoesNotThrow(() -> handler.handleCacheGetError(failure, cache, "key"));
        assertDoesNotThrow(() -> handler.handleCachePutError(failure, cache, "key", "value"));
        assertDoesNotThrow(() -> handler.handleCacheEvictError(failure, cache, "key"));
        assertDoesNotThrow(() -> handler.handleCacheClearError(failure, cache));
    }

    @Configuration(proxyBeanMethods = false)
    @EnableCaching
    static class TestConfig {
        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager(CacheNames.DEPT_LIST);
        }

        @Bean
        DeptMapper deptMapper() {
            return mock(DeptMapper.class);
        }

        @Bean
        DeptService deptService() {
            return new DeptServiceImpl();
        }
    }
}
