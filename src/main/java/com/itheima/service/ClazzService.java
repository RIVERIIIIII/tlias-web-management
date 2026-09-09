package com.itheima.service;

import com.itheima.pojo.Clazz;
import com.itheima.pojo.ClazzQueryParam;
import com.itheima.pojo.PageResult;

import java.util.List;

public interface ClazzService {

    PageResult<Clazz> list(ClazzQueryParam param);

    void save(Clazz clazz);

    Clazz getInfo(Integer id);

    void update(Clazz clazz);

    void deleteById(Integer id);

    List<Clazz> getList();
}
