package com.itheima.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.config.CacheNames;
import com.itheima.mapper.ClazzMapper;
import com.itheima.pojo.Clazz;
import com.itheima.pojo.ClazzQueryParam;
import com.itheima.pojo.PageResult;
import com.itheima.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

//班级管理
@Service
public class ClazzServiceImpl implements ClazzService {
    @Autowired
    private ClazzMapper clazzMapper;

    @Override
    public PageResult<Clazz> list(ClazzQueryParam param) {
        //设置分页参数
        PageHelper.startPage(param.getPage(), param.getPageSize());
        //查询基础信息
        List<Clazz> clazzList = clazzMapper.list(param);
        //封装
        clazzList.forEach(clazz -> {
            if (clazz.getBeginDate().isAfter(LocalDate.now()))
                clazz.setStatus("未开班");
                else if (clazz.getEndDate().isBefore(LocalDate.now()))
                    clazz.setStatus("已结课");
                    else
                        clazz.setStatus("在读中");
        });
        Page<Clazz> p = (Page<Clazz>) clazzList;
        return new PageResult<Clazz>(p.getTotal(), p.getResult());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.CLAZZ_LIST, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.REPORT, allEntries = true)
    })
    public void save(Clazz clazz) {
        //补全基础属性
        clazz.setCreateTime(LocalDateTime.now());
        clazz.setUpdateTime(LocalDateTime.now());
        //保存
        clazzMapper.insert(clazz);
    }

    @Override
    public Clazz getInfo(Integer id) {
        Clazz clazz = clazzMapper.getById(id);
        return clazz;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.CLAZZ_LIST, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.REPORT, allEntries = true)
    })
    public void update(Clazz clazz) {
        //更新基本属性
        clazz.setUpdateTime(LocalDateTime.now());
        clazzMapper.updateById(clazz);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.CLAZZ_LIST, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.REPORT, allEntries = true)
    })
    public void deleteById(Integer id) {
        clazzMapper.deleteById(id);
    }

    @Override
    @Cacheable(cacheNames = CacheNames.CLAZZ_LIST, key = "'all'", sync = true)
    public List<Clazz> getList() {
        List<Clazz> list = clazzMapper.getList();
        return list;
    }
}
